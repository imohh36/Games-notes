package com.spinel.gamenotes.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.spinel.gamenotes.GameNotesApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

data class BackupResult(
    val success: Boolean,
    val notesCount: Int = 0,
    val tabsCount: Int = 0,
    val imagesCount: Int = 0,
    val errorMessage: String? = null
)

object BackupManager {

    private const val TAG = "BackupManager"
    private const val DATABASE_NAME = "game_notes_database"
    private const val IMAGES_DIR_NAME = "note_images"

    /**
     * Creates a ZIP archive containing:
     * 1. The Room database file (and WAL/SHM if present) after a full SQLite WAL checkpoint.
     * 2. All note images from the app's internal storage (note_images directory).
     * 3. metadata.json containing backup information.
     * 4. backup_data.json containing a human-readable/fallback JSON export.
     */
    suspend fun exportBackup(
        context: Context,
        uri: Uri,
        repository: GameNotesRepository
    ): BackupResult = withContext(Dispatchers.IO) {
        val tempDir = File(
            context.cacheDir,
            "backup_temp_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
        )
        val tempZip = File(
            context.cacheDir,
            "export_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.zip"
        )

        try {
            if (!tempDir.mkdirs()) {
                return@withContext BackupResult(false, errorMessage = "فشل في إنشاء المجلد المؤقت للتصدير")
            }

            val dbDir = File(tempDir, "database").apply { mkdirs() }
            val imagesDir = File(tempDir, "images").apply { mkdirs() }

            // 1. Force SQLite WAL Checkpoint to flush all recent writes to disk
            try {
                val db = AppDatabase.getDatabase(context)
                db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").use { cursor ->
                    cursor.moveToFirst()
                }
            } catch (e: Exception) {
                Log.w(TAG, "WAL checkpoint warning: ${e.message}")
            }

            // 2. Copy Database files
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            if (dbFile.exists()) {
                dbFile.copyTo(File(dbDir, DATABASE_NAME), overwrite = true)
                val walFile = File(dbFile.path + "-wal")
                if (walFile.exists()) {
                    walFile.copyTo(File(dbDir, "$DATABASE_NAME-wal"), overwrite = true)
                }
                val shmFile = File(dbFile.path + "-shm")
                if (shmFile.exists()) {
                    shmFile.copyTo(File(dbDir, "$DATABASE_NAME-shm"), overwrite = true)
                }
            } else {
                Log.w(TAG, "Database file does not exist at ${dbFile.path}")
            }

            // 3. Copy Note Images from internal storage
            var copiedImagesCount = 0
            val localImagesDir = File(context.filesDir, IMAGES_DIR_NAME)
            if (localImagesDir.exists() && localImagesDir.isDirectory) {
                localImagesDir.listFiles()?.forEach { file ->
                    if (file.isFile && file.length() > 0) {
                        file.copyTo(File(imagesDir, file.name), overwrite = true)
                        copiedImagesCount++
                    }
                }
            }

            val allNotes = repository.getAllNotesDirect()
            val allTabs = repository.getAllTabsDirect()

            // Also check for any note image references that might be located in filesDir
            allNotes.forEach { note ->
                note.imageUris.forEach { uStr ->
                    try {
                        val u = Uri.parse(uStr)
                        if (u.scheme == "file" && u.path != null) {
                            val f = File(u.path!!)
                            if (f.exists() && f.isFile && f.length() > 0) {
                                val dest = File(imagesDir, f.name)
                                if (!dest.exists()) {
                                    f.copyTo(dest, overwrite = true)
                                    copiedImagesCount++
                                }
                            }
                        }
                    } catch (_: Exception) {}
                }
                if (note.blocksJson.isNotBlank() && note.blocksJson != "[]") {
                    try {
                        val blocks = DocumentBlock.jsonToList(note.blocksJson)
                        blocks.forEach { b ->
                            listOf(b.imageUri, b.secondImageUri).forEach { uStr ->
                                if (uStr.isNotBlank()) {
                                    try {
                                        val u = Uri.parse(uStr)
                                        if (u.scheme == "file" && u.path != null) {
                                            val f = File(u.path!!)
                                            if (f.exists() && f.isFile && f.length() > 0) {
                                                val dest = File(imagesDir, f.name)
                                                if (!dest.exists()) {
                                                    f.copyTo(dest, overwrite = true)
                                                    copiedImagesCount++
                                                }
                                            }
                                        }
                                    } catch (_: Exception) {}
                                }
                            }
                        }
                    } catch (_: Exception) {}
                }
            }

            // 4. Create metadata.json
            val metaJson = JSONObject().apply {
                put("app", "GameNotes")
                put("version", 2)
                put("format", "zip")
                put("exportedAt", System.currentTimeMillis())
                put("notesCount", allNotes.size)
                put("tabsCount", allTabs.size)
                put("imagesCount", copiedImagesCount)
            }
            File(tempDir, "metadata.json").writeText(metaJson.toString(2), Charsets.UTF_8)

            // 5. Create backup_data.json for fallback / inspection
            val dataJson = serializeNotesAndTabsToJson(allNotes, allTabs)
            File(tempDir, "backup_data.json").writeText(dataJson.toString(2), Charsets.UTF_8)

            // 6. Compress temporary directory into ZIP
            zipDirectory(tempDir, tempZip)

            // 7. Write ZIP stream to the destination SAF Uri
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                tempZip.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
                outputStream.flush()
            } ?: return@withContext BackupResult(false, errorMessage = "تعذر فتح ملف الوجهة للكتابة")

            BackupResult(
                success = true,
                notesCount = allNotes.size,
                tabsCount = allTabs.size,
                imagesCount = copiedImagesCount
            )
        } catch (e: Exception) {
            Log.e(TAG, "Export backup failed", e)
            BackupResult(false, errorMessage = e.localizedMessage ?: "حدث خطأ غير متوقع أثناء التصدير")
        } finally {
            tempDir.deleteRecursively()
            if (tempZip.exists()) tempZip.delete()
        }
    }

    /**
     * Restores a backup from a ZIP archive or legacy JSON file.
     * Extracts images to internal note_images and replaces the Room database safely.
     */
    suspend fun importBackup(
        context: Context,
        uri: Uri,
        repository: GameNotesRepository
    ): BackupResult = withContext(Dispatchers.IO) {
        val tempImportFile = File(
            context.cacheDir,
            "import_temp_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.bin"
        )
        val unzipDir = File(
            context.cacheDir,
            "unzip_temp_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
        )

        try {
            // 1. Read input stream safely from SAF Uri into local temp file
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                tempImportFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return@withContext BackupResult(false, errorMessage = "تعذر قراءة ملف النسخة الاحتياطية")

            if (isZipArchive(tempImportFile)) {
                // 2. Unpack ZIP with Zip Slip prevention
                if (!unzipDir.mkdirs()) {
                    return@withContext BackupResult(false, errorMessage = "فشل في إنشاء مجلد استخراج النسخة الاحتياطية")
                }
                unzipArchive(tempImportFile, unzipDir)

                // 3. Extract and copy note images to internal filesDir/note_images
                val localImagesDir = File(context.filesDir, IMAGES_DIR_NAME).apply { mkdirs() }
                var restoredImagesCount = 0

                val extractedImagesDir = File(unzipDir, "images").takeIf { it.exists() && it.isDirectory }
                    ?: File(unzipDir, IMAGES_DIR_NAME).takeIf { it.exists() && it.isDirectory }

                if (extractedImagesDir != null) {
                    extractedImagesDir.listFiles()?.forEach { imgFile ->
                        if (imgFile.isFile) {
                            val destFile = File(localImagesDir, imgFile.name)
                            imgFile.copyTo(destFile, overwrite = true)
                            restoredImagesCount++
                        }
                    }
                }

                // 4. Merge Backup Data (Append notes as new items without erasing current ones)
                val backupDataJson = File(unzipDir, "backup_data.json").takeIf { it.exists() }
                val extractedDb = File(unzipDir, "database/$DATABASE_NAME").takeIf { it.exists() }
                    ?: File(unzipDir, DATABASE_NAME).takeIf { it.exists() }

                var notesCount = 0
                var tabsCount = 0

                if (backupDataJson != null) {
                    val jsonStr = backupDataJson.readText(Charsets.UTF_8)
                    val counts = parseAndImportJson(jsonStr, repository, localImagesDir)
                    notesCount = counts.first
                    tabsCount = counts.second
                } else if (extractedDb != null) {
                    // Open extracted DB as a secondary database to read notes and merge without wiping existing notes
                    val tempDb = androidx.room.Room.databaseBuilder(context, AppDatabase::class.java, extractedDb.absolutePath)
                        .build()
                    try {
                        val backupNotes = tempDb.gameNoteDao().getAllNotesDirect()
                        val backupTabs = tempDb.gameTabDao().getAllTabsDirect()

                        // Merge tabs without duplicate names
                        val existingTabs = repository.getAllTabsDirect()
                        val existingTabNames = existingTabs.map { it.name.trim().lowercase() }.toSet()
                        val tabsToInsert = backupTabs
                            .filter { it.name.trim().lowercase() !in existingTabNames }
                            .map { it.copy(id = 0) }

                        if (tabsToInsert.isNotEmpty()) {
                            repository.insertTabsList(tabsToInsert)
                        }

                        // Merge notes as new items (id = 0) to avoid overwriting or erasing current notes
                        val notesToInsert = backupNotes.map { note ->
                            val resolvedImageUris = note.imageUris.map { rawUri ->
                                val fName = rawUri.substringAfterLast("/")
                                val localFile = File(localImagesDir, fName)
                                if (localFile.exists()) Uri.fromFile(localFile).toString() else rawUri
                            }
                            note.copy(
                                id = 0,
                                imageUris = resolvedImageUris
                            )
                        }

                        if (notesToInsert.isNotEmpty()) {
                            repository.insertNotesList(notesToInsert)
                        }

                        notesCount = notesToInsert.size
                        tabsCount = tabsToInsert.size
                    } finally {
                        tempDb.close()
                    }
                } else {
                    return@withContext BackupResult(
                        false,
                        errorMessage = "ملف الـ ZIP لا يحتوي على قاعدة البيانات أو بيانات صالحة"
                    )
                }

                BackupResult(
                    success = true,
                    notesCount = notesCount,
                    tabsCount = tabsCount,
                    imagesCount = restoredImagesCount
                )
            } else {
                // Legacy JSON backup support
                val jsonString = tempImportFile.readText(Charsets.UTF_8)
                val counts = parseAndImportJson(jsonString, repository, null)
                BackupResult(
                    success = true,
                    notesCount = counts.first,
                    tabsCount = counts.second,
                    imagesCount = 0
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Import backup failed", e)
            BackupResult(false, errorMessage = e.localizedMessage ?: "تنسيق النسخة الاحتياطية غير صالح أو تالف")
        } finally {
            tempImportFile.delete()
            unzipDir.deleteRecursively()
        }
    }

    /**
     * Normalizes image URIs in restored notes so they match the local device's filesDir.
     */
    private suspend fun normalizeRestoredNoteImagePaths(db: AppDatabase, localImagesDir: File) {
        try {
            val notes = db.gameNoteDao().getAllNotesDirect()
            val updatedNotes = mutableListOf<GameNote>()

            for (note in notes) {
                var changed = false

                // 1. Check note.imageUris
                val newImageUris = note.imageUris.map { uriStr ->
                    val fileName = uriStr.substringAfterLast("/")
                    val localFile = File(localImagesDir, fileName)
                    if (localFile.exists()) {
                        val newUri = Uri.fromFile(localFile).toString()
                        if (newUri != uriStr) {
                            changed = true
                            newUri
                        } else uriStr
                    } else uriStr
                }

                // 2. Check blocksJson
                var newBlocksJson = note.blocksJson
                if (newBlocksJson.isNotBlank() && newBlocksJson != "[]") {
                    try {
                        val blocks = DocumentBlock.jsonToList(newBlocksJson)
                        var blocksChanged = false
                        val updatedBlocks = blocks.map { block ->
                            var bChanged = false
                            var img1 = block.imageUri
                            var img2 = block.secondImageUri

                            if (img1.isNotBlank()) {
                                val fn = img1.substringAfterLast("/")
                                val localF = File(localImagesDir, fn)
                                if (localF.exists()) {
                                    val nUri = Uri.fromFile(localF).toString()
                                    if (nUri != img1) {
                                        img1 = nUri
                                        bChanged = true
                                    }
                                }
                            }

                            if (img2.isNotBlank()) {
                                val fn = img2.substringAfterLast("/")
                                val localF = File(localImagesDir, fn)
                                if (localF.exists()) {
                                    val nUri = Uri.fromFile(localF).toString()
                                    if (nUri != img2) {
                                        img2 = nUri
                                        bChanged = true
                                    }
                                }
                            }

                            if (bChanged) {
                                blocksChanged = true
                                block.copy(imageUri = img1, secondImageUri = img2)
                            } else block
                        }

                        if (blocksChanged) {
                            newBlocksJson = DocumentBlock.listToJson(updatedBlocks)
                            changed = true
                        }
                    } catch (_: Exception) {}
                }

                // 3. Check internalTabs blocksJson
                var internalTabsChanged = false
                val newInternalTabs = note.internalTabs.map { tab ->
                    if (tab.blocksJson.isNotBlank() && tab.blocksJson != "[]") {
                        try {
                            val blocks = DocumentBlock.jsonToList(tab.blocksJson)
                            var blocksChanged = false
                            val updatedBlocks = blocks.map { block ->
                                var bChanged = false
                                var img1 = block.imageUri
                                var img2 = block.secondImageUri

                                if (img1.isNotBlank()) {
                                    val fn = img1.substringAfterLast("/")
                                    val localF = File(localImagesDir, fn)
                                    if (localF.exists()) {
                                        val nUri = Uri.fromFile(localF).toString()
                                        if (nUri != img1) {
                                            img1 = nUri
                                            bChanged = true
                                        }
                                    }
                                }

                                if (img2.isNotBlank()) {
                                    val fn = img2.substringAfterLast("/")
                                    val localF = File(localImagesDir, fn)
                                    if (localF.exists()) {
                                        val nUri = Uri.fromFile(localF).toString()
                                        if (nUri != img2) {
                                            img2 = nUri
                                            bChanged = true
                                        }
                                    }
                                }

                                if (bChanged) {
                                    blocksChanged = true
                                    block.copy(imageUri = img1, secondImageUri = img2)
                                } else block
                            }

                            if (blocksChanged) {
                                internalTabsChanged = true
                                tab.copy(blocksJson = DocumentBlock.listToJson(updatedBlocks))
                            } else tab
                        } catch (_: Exception) {
                            tab
                        }
                    } else tab
                }

                if (changed || internalTabsChanged) {
                    updatedNotes.add(
                        note.copy(
                            imageUris = newImageUris,
                            blocksJson = newBlocksJson,
                            internalTabs = newInternalTabs
                        )
                    )
                }
            }

            if (updatedNotes.isNotEmpty()) {
                db.gameNoteDao().insertNotes(updatedNotes)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error normalizing note image paths", e)
        }
    }

    private fun zipDirectory(sourceDir: File, outZipFile: File) {
        val rootPath = sourceDir.canonicalPath
        ZipOutputStream(BufferedOutputStream(FileOutputStream(outZipFile))).use { zos ->
            fun addFileToZip(file: File) {
                val relativePath = file.canonicalPath.substring(rootPath.length)
                    .trimStart(File.separatorChar)
                    .replace('\\', '/')
                if (relativePath.isEmpty()) return

                if (file.isDirectory) {
                    val entryName = if (relativePath.endsWith("/")) relativePath else "$relativePath/"
                    zos.putNextEntry(ZipEntry(entryName))
                    zos.closeEntry()
                    file.listFiles()?.forEach { child -> addFileToZip(child) }
                } else {
                    zos.putNextEntry(ZipEntry(relativePath))
                    FileInputStream(file).use { fis ->
                        fis.copyTo(zos)
                    }
                    zos.closeEntry()
                }
            }

            sourceDir.listFiles()?.forEach { child -> addFileToZip(child) }
        }
    }

    private fun unzipArchive(zipFile: File, destinationDir: File) {
        val canonicalDest = destinationDir.canonicalPath
        ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val entryFile = File(destinationDir, entry.name)
                val canonicalEntry = entryFile.canonicalPath
                // Zip Slip protection: ensure entry does not escape destinationDir
                if (!canonicalEntry.startsWith(canonicalDest + File.separator) && canonicalEntry != canonicalDest) {
                    throw SecurityException("Zip Slip path traversal attempt: ${entry.name}")
                }

                if (entry.isDirectory) {
                    entryFile.mkdirs()
                } else {
                    entryFile.parentFile?.mkdirs()
                    FileOutputStream(entryFile).use { fos ->
                        zis.copyTo(fos)
                    }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
    }

    private fun isZipArchive(file: File): Boolean {
        if (!file.exists() || file.length() < 4) return false
        return try {
            FileInputStream(file).use { fis ->
                val b1 = fis.read()
                val b2 = fis.read()
                val b3 = fis.read()
                val b4 = fis.read()
                b1 == 0x50 && b2 == 0x4B && (
                    (b3 == 0x03 && b4 == 0x04) ||
                    (b3 == 0x05 && b4 == 0x06) ||
                    (b3 == 0x07 && b4 == 0x08)
                )
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun serializeNotesAndTabsToJson(
        allNotes: List<GameNote>,
        allTabs: List<GameTab>
    ): JSONObject {
        val rootJson = JSONObject().apply {
            put("version", 2)
            put("app", "GameNotes")
            put("exportedAt", System.currentTimeMillis())

            // Tabs
            val tabsArray = JSONArray()
            allTabs.forEach { tab ->
                val tabObj = JSONObject().apply {
                    put("id", tab.id)
                    put("name", tab.name)
                    put("createdAt", tab.createdAt)
                }
                tabsArray.put(tabObj)
            }
            put("tabs", tabsArray)

            // Notes
            val notesArray = JSONArray()
            allNotes.forEach { note ->
                val noteObj = JSONObject().apply {
                    put("id", note.id)
                    put("title", note.title)
                    put("content", note.content)
                    put("gameTag", note.gameTag)
                    put("noteType", note.noteType)
                    put("blocksJson", note.blocksJson)
                    put("isPinned", note.isPinned)
                    put("isDeleted", note.isDeleted)
                    put("colorHex", note.colorHex)
                    put("createdAt", note.createdAt)
                    put("updatedAt", note.updatedAt)

                    // Todo items
                    val todosArray = JSONArray()
                    note.todoItems.forEach { todo ->
                        val tObj = JSONObject().apply {
                            put("id", todo.id)
                            put("text", todo.text)
                            put("isDone", todo.isDone)
                        }
                        todosArray.put(tObj)
                    }
                    put("todoItems", todosArray)

                    // Image URIs
                    val imgArray = JSONArray()
                    note.imageUris.forEach { imgArray.put(it) }
                    put("imageUris", imgArray)

                    // Tags
                    val tagsArray = JSONArray()
                    note.tags.forEach { tag ->
                        val tagObj = JSONObject().apply {
                            put("id", tag.id)
                            put("name", tag.name)
                            put("colorHex", tag.colorHex)
                        }
                        tagsArray.put(tagObj)
                    }
                    put("tags", tagsArray)

                    // Internal Tabs
                    val internalTabsArray = JSONArray()
                    note.internalTabs.forEach { tab ->
                        internalTabsArray.put(tab.toJson())
                    }
                    put("internalTabs", internalTabsArray)
                }
                notesArray.put(noteObj)
            }
            put("notes", notesArray)
        }
        return rootJson
    }

    private suspend fun parseAndImportJson(
        jsonString: String,
        repository: GameNotesRepository,
        localImagesDir: File?
    ): Pair<Int, Int> {
        val rootJson = JSONObject(jsonString)

        // Parse tabs
        val tabsList = mutableListOf<GameTab>()
        val tabsArray = rootJson.optJSONArray("tabs")
        if (tabsArray != null) {
            for (i in 0 until tabsArray.length()) {
                val obj = tabsArray.getJSONObject(i)
                tabsList.add(
                    GameTab(
                        id = 0,
                        name = obj.getString("name"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
        }

        // Parse notes
        val notesList = mutableListOf<GameNote>()
        val notesArray = rootJson.optJSONArray("notes")
        if (notesArray != null) {
            for (i in 0 until notesArray.length()) {
                val obj = notesArray.getJSONObject(i)

                val todoItemsList = mutableListOf<TodoItem>()
                val todosArray = obj.optJSONArray("todoItems")
                if (todosArray != null) {
                    for (j in 0 until todosArray.length()) {
                        val tObj = todosArray.getJSONObject(j)
                        todoItemsList.add(
                            TodoItem(
                                id = tObj.optString("id", j.toString()),
                                text = tObj.optString("text", ""),
                                isDone = tObj.optBoolean("isDone", false)
                            )
                        )
                    }
                }

                val imageUrisList = mutableListOf<String>()
                val imgArray = obj.optJSONArray("imageUris")
                if (imgArray != null) {
                    for (k in 0 until imgArray.length()) {
                        val rawUri = imgArray.getString(k)
                        val resolvedUri = if (localImagesDir != null) {
                            val fName = rawUri.substringAfterLast("/")
                            val localFile = File(localImagesDir, fName)
                            if (localFile.exists()) Uri.fromFile(localFile).toString() else rawUri
                        } else rawUri
                        imageUrisList.add(resolvedUri)
                    }
                }

                val tagsList = mutableListOf<NoteTag>()
                val tagsArr = obj.optJSONArray("tags")
                if (tagsArr != null) {
                    for (k in 0 until tagsArr.length()) {
                        val tagObj = tagsArr.getJSONObject(k)
                        tagsList.add(
                            NoteTag(
                                id = tagObj.optString("id", k.toString()),
                                name = tagObj.optString("name", ""),
                                colorHex = tagObj.optString("colorHex", "#10B981")
                            )
                        )
                    }
                }

                val internalTabsList = mutableListOf<NoteInternalTab>()
                val internalTabsArr = obj.optJSONArray("internalTabs")
                if (internalTabsArr != null) {
                    for (k in 0 until internalTabsArr.length()) {
                        val tabObj = internalTabsArr.getJSONObject(k)
                        internalTabsList.add(NoteInternalTab.fromJson(tabObj))
                    }
                }

                notesList.add(
                    GameNote(
                        id = 0,
                        title = obj.optString("title", "بدون عنوان"),
                        content = obj.optString("content", ""),
                        gameTag = obj.optString("gameTag", ""),
                        noteType = obj.optString("noteType", NoteType.REGULAR.name),
                        todoItems = todoItemsList,
                        imageUris = imageUrisList,
                        blocksJson = obj.optString("blocksJson", "[]"),
                        internalTabs = internalTabsList,
                        tags = tagsList,
                        isPinned = obj.optBoolean("isPinned", false),
                        isDeleted = obj.optBoolean("isDeleted", false),
                        colorHex = obj.optString("colorHex", "#10B981"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        updatedAt = obj.optLong("updatedAt", System.currentTimeMillis())
                    )
                )
            }
        }

        // Insert unique tabs
        val existingTabs = repository.getAllTabsDirect()
        val existingTabNames = existingTabs.map { it.name.trim().lowercase() }.toSet()
        val tabsToInsert = tabsList.filter { it.name.trim().lowercase() !in existingTabNames }

        if (tabsToInsert.isNotEmpty()) {
            repository.insertTabsList(tabsToInsert)
        }

        if (notesList.isNotEmpty()) {
            repository.insertNotesList(notesList)
        }

        return Pair(notesList.size, tabsToInsert.size)
    }
}
