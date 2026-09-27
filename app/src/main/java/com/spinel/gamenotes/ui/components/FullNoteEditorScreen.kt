package com.spinel.gamenotes.ui.components

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.collectAsState
import com.spinel.gamenotes.data.AppSettingsPreferences
import com.spinel.gamenotes.data.NoteInternalTab
import com.spinel.gamenotes.ui.components.NoteInternalTabsBar
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.TextButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.runtime.mutableIntStateOf
import com.spinel.gamenotes.ui.components.FullScreenImageZoomDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.spinel.gamenotes.data.BlockType
import com.spinel.gamenotes.data.DocumentBlock
import com.spinel.gamenotes.data.GameNote
import com.spinel.gamenotes.data.NoteTag
import com.spinel.gamenotes.data.NoteType
import com.spinel.gamenotes.data.TodoItem
import com.spinel.gamenotes.ui.theme.ThemePreferences
import com.spinel.gamenotes.util.LanguagePreferences.LocalAppStrings
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullNoteEditorScreen(
    note: GameNote?,
    initialGameTag: String,
    availableGameTabs: List<String>,
    initialTabIndex: Int = 0,
    initialScrollItemIndex: Int = 0,
    initialScrollOffset: Int = 0,
    onBack: () -> Unit,
    onSave: (GameNote) -> Unit,
    onAskGemini: (GameNote) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var gameTag by remember { mutableStateOf(note?.gameTag?.takeIf { it.isNotBlank() } ?: initialGameTag) }
    var isEditingGameTagDialog by remember { mutableStateOf(false) }
    var zoomedImageUri by remember { mutableStateOf<String?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val strings = LocalAppStrings.current

    // Internal Tabs State
    val tabs = remember {
        mutableStateListOf<NoteInternalTab>().apply {
            val existing = note?.getEffectiveTabs() ?: emptyList()
            if (existing.isNotEmpty()) {
                addAll(existing)
            } else {
                add(NoteInternalTab(title = "الرئيسية"))
            }
        }
    }
    var activeTabIndex by remember {
        mutableIntStateOf(initialTabIndex.coerceIn(0, (tabs.size - 1).coerceAtLeast(0)))
    }

    fun loadBlocksForTab(tab: NoteInternalTab): List<DocumentBlock> {
        return if (tab.blocksJson.isNotBlank() && tab.blocksJson != "[]") {
            try {
                val loaded = DocumentBlock.jsonToList(tab.blocksJson)
                loaded.map { blk ->
                    if (blk.type == BlockType.IMAGE && blk.imageUri.isNotBlank()) {
                        blk.copy(imageUri = com.spinel.gamenotes.util.StorageUtils.saveImageToInternalStorage(context, blk.imageUri))
                    } else blk
                }
            } catch (e: Exception) {
                listOf(DocumentBlock(type = BlockType.TEXT, text = tab.content))
            }
        } else if (tab.content.isNotBlank()) {
            listOf(DocumentBlock(type = BlockType.TEXT, text = tab.content))
        } else {
            listOf(DocumentBlock(type = BlockType.TEXT, text = ""))
        }
    }

    // Rich blocks list for the active tab
    val blocks = remember {
        mutableStateListOf<DocumentBlock>().apply {
            val initialTab = tabs.getOrNull(activeTabIndex) ?: tabs.firstOrNull()
            if (initialTab != null) {
                addAll(loadBlocksForTab(initialTab))
            } else {
                add(DocumentBlock(type = BlockType.TEXT, text = ""))
            }
        }
    }

    var activeFocusedBlockIndex by remember { mutableIntStateOf(0) }
    var activeCursorPosition by remember { mutableIntStateOf(0) }

    val focusRequesters = remember { mutableMapOf<String, FocusRequester>() }
    var pendingFocusBlockId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialScrollItemIndex,
        initialFirstVisibleItemScrollOffset = initialScrollOffset
    )

    val initialScrollIndex = remember(note?.id) { initialScrollItemIndex }
    val initialScrollOff = remember(note?.id) { initialScrollOffset }

    // Accurate scroll restoration on launch for pinned reading state (run once per note/tab, not on every offset change)
    LaunchedEffect(note?.id, initialTabIndex) {
        if (initialScrollIndex > 0 || initialScrollOff > 0) {
            kotlinx.coroutines.delay(80)
            try {
                listState.scrollToItem(initialScrollIndex, initialScrollOff)
            } catch (_: Exception) {}
        }
    }

    val pinnedId by AppSettingsPreferences.pinnedFloatingNoteId.collectAsState()
    val pinnedTabIdx by AppSettingsPreferences.pinnedFloatingTabIndex.collectAsState()
    val isPinnedToFloating = note != null && note.id != 0L && pinnedId == note.id && pinnedTabIdx == activeTabIndex

    // Debounced update of saved scroll position to avoid recomposition feedback loops and excessive disk I/O
    LaunchedEffect(isPinnedToFloating) {
        if (isPinnedToFloating) {
            snapshotFlow {
                listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
            }
                .distinctUntilChanged()
                .debounce(400)
                .collect { pair ->
                    AppSettingsPreferences.updatePinnedScrollPosition(
                        context = context,
                        scrollItemIndex = pair.first,
                        scrollOffset = pair.second
                    )
                }
        }
    }

    LaunchedEffect(pendingFocusBlockId, blocks.size) {
        pendingFocusBlockId?.let { targetId ->
            kotlinx.coroutines.delay(60)
            focusRequesters[targetId]?.requestFocus()
            val targetIdx = blocks.indexOfFirst { it.id == targetId }
            if (targetIdx >= 0) {
                listState.animateScrollToItem(targetIdx)
            }
            pendingFocusBlockId = null
        }
    }

    val insertImageFromUri: (String) -> Unit = { internalUriStr ->
        val newImageBlock = DocumentBlock(
            type = BlockType.IMAGE,
            imageUri = internalUriStr,
            imageWidthPercent = 0.80f
        )

        if (activeFocusedBlockIndex in 0 until blocks.size && blocks[activeFocusedBlockIndex].type == BlockType.TEXT) {
            val currentBlock = blocks[activeFocusedBlockIndex]
            val currentText = currentBlock.text
            val cursor = activeCursorPosition.coerceIn(0, currentText.length)
            val textBefore = currentText.take(cursor)
            val textAfter = currentText.drop(cursor)

            if (textBefore.isEmpty() && textAfter.isEmpty()) {
                blocks[activeFocusedBlockIndex] = newImageBlock
                blocks.add(activeFocusedBlockIndex + 1, DocumentBlock(type = BlockType.TEXT, text = ""))
                activeFocusedBlockIndex = activeFocusedBlockIndex + 1
                activeCursorPosition = 0
            } else if (textBefore.isEmpty()) {
                blocks.add(activeFocusedBlockIndex, newImageBlock)
                activeFocusedBlockIndex = activeFocusedBlockIndex + 1
                activeCursorPosition = 0
            } else if (textAfter.isEmpty()) {
                val insertIdx = activeFocusedBlockIndex + 1
                blocks.add(insertIdx, newImageBlock)
                blocks.add(insertIdx + 1, DocumentBlock(type = BlockType.TEXT, text = ""))
                activeFocusedBlockIndex = insertIdx + 1
                activeCursorPosition = 0
            } else {
                blocks[activeFocusedBlockIndex] = currentBlock.copy(text = textBefore)
                val insertIdx = activeFocusedBlockIndex + 1
                blocks.add(insertIdx, newImageBlock)
                blocks.add(insertIdx + 1, DocumentBlock(type = BlockType.TEXT, text = textAfter))
                activeFocusedBlockIndex = insertIdx + 1
                activeCursorPosition = 0
            }
        } else {
            val targetIdx = (activeFocusedBlockIndex + 1).coerceIn(0, blocks.size)
            blocks.add(targetIdx, newImageBlock)
            blocks.add(targetIdx + 1, DocumentBlock(type = BlockType.TEXT, text = ""))
            activeFocusedBlockIndex = targetIdx + 1
            activeCursorPosition = 0
        }
    }

    val activityResultRegistryOwner = androidx.activity.compose.LocalActivityResultRegistryOwner.current
    var activeBlockIndexForSecondImage by remember { mutableIntStateOf(-1) }

    val photoPickerLauncher = if (activityResultRegistryOwner != null) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            uri?.let {
                val internalUriStr = com.spinel.gamenotes.util.StorageUtils.saveImageToInternalStorage(context, it)
                insertImageFromUri(internalUriStr)
            }
        }
    } else null

    val secondPhotoPickerLauncher = if (activityResultRegistryOwner != null) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri: Uri? ->
            uri?.let {
                val internalUriStr = com.spinel.gamenotes.util.StorageUtils.saveImageToInternalStorage(context, it)
                val idx = activeBlockIndexForSecondImage
                if (idx in 0 until blocks.size) {
                    val current = blocks[idx]
                    blocks[idx] = current.copy(secondImageUri = internalUriStr)
                }
                activeBlockIndexForSecondImage = -1
            }
        }
    } else null

    // Listen to OverlayImagePickerBridge for floating overlay mode
    LaunchedEffect(Unit) {
        com.spinel.gamenotes.service.OverlayImagePickerBridge.pickedImageUri.collect { internalUriStr ->
            if (internalUriStr.isNotBlank()) {
                val idx = activeBlockIndexForSecondImage
                if (idx in 0 until blocks.size) {
                    val current = blocks[idx]
                    blocks[idx] = current.copy(secondImageUri = internalUriStr)
                    activeBlockIndexForSecondImage = -1
                } else {
                    insertImageFromUri(internalUriStr)
                }
            }
        }
    }

    val launchPhotoPickerForMain: () -> Unit = {
        activeBlockIndexForSecondImage = -1
        if (photoPickerLauncher != null) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            com.spinel.gamenotes.service.OverlayImagePickerActivity.launch(context)
        }
    }

    val launchPhotoPickerForSecondImage: (Int) -> Unit = { blockIndex ->
        activeBlockIndexForSecondImage = blockIndex
        if (secondPhotoPickerLauncher != null) {
            secondPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            com.spinel.gamenotes.service.OverlayImagePickerActivity.launch(context)
        }
    }

    val customTextColor = MaterialTheme.colorScheme.primary
    val topBarBg = customTextColor
    val topBarContent = ThemePreferences.getContrastingContentColor(topBarBg)

    val insertChecklistAtCurrentCursor: () -> Unit = {
        if (blocks.isEmpty()) {
            val newChecklistBlock = DocumentBlock(type = BlockType.CHECKLIST, text = "", isChecked = false)
            blocks.add(newChecklistBlock)
            activeFocusedBlockIndex = 0
            activeCursorPosition = 0
            pendingFocusBlockId = newChecklistBlock.id
        } else {
            val targetIndex = activeFocusedBlockIndex.coerceIn(0, blocks.size - 1)
            val currBlock = blocks[targetIndex]

            if (currBlock.type == BlockType.TEXT) {
                val text = currBlock.text
                val cursor = activeCursorPosition.coerceIn(0, text.length)
                val textBefore = text.substring(0, cursor)
                val textAfter = text.substring(cursor)

                val newChecklistBlock = DocumentBlock(
                    type = BlockType.CHECKLIST,
                    text = "",
                    isChecked = false
                )

                if (textBefore.isEmpty() && textAfter.isEmpty()) {
                    blocks[targetIndex] = newChecklistBlock
                    activeFocusedBlockIndex = targetIndex
                    activeCursorPosition = 0
                } else if (textBefore.isEmpty()) {
                    blocks.add(targetIndex, newChecklistBlock)
                    activeFocusedBlockIndex = targetIndex
                    activeCursorPosition = 0
                } else if (textAfter.isEmpty()) {
                    blocks.add(targetIndex + 1, newChecklistBlock)
                    activeFocusedBlockIndex = targetIndex + 1
                    activeCursorPosition = 0
                } else {
                    // Split text at exact cursor position!
                    blocks[targetIndex] = currBlock.copy(text = textBefore)
                    blocks.add(targetIndex + 1, newChecklistBlock)
                    blocks.add(targetIndex + 2, DocumentBlock(id = java.util.UUID.randomUUID().toString(), type = BlockType.TEXT, text = textAfter))
                    activeFocusedBlockIndex = targetIndex + 1
                    activeCursorPosition = 0
                }
                pendingFocusBlockId = newChecklistBlock.id
            } else {
                val newChecklistBlock = DocumentBlock(
                    type = BlockType.CHECKLIST,
                    text = "",
                    isChecked = false
                )
                blocks.add(targetIndex + 1, newChecklistBlock)
                activeFocusedBlockIndex = targetIndex + 1
                activeCursorPosition = 0
                pendingFocusBlockId = newChecklistBlock.id
            }
        }
    }

    fun syncCurrentTabContent() {
        if (activeTabIndex in 0 until tabs.size) {
            val extracted = blocks.mapNotNull { b ->
                when (b.type) {
                    BlockType.TEXT -> b.text.takeIf { it.isNotBlank() }
                    BlockType.IMAGE -> b.text.takeIf { it.isNotBlank() }
                    BlockType.CHECKLIST -> null
                }
            }.joinToString("\n\n")
            val currentTab = tabs[activeTabIndex]
            tabs[activeTabIndex] = currentTab.copy(
                content = extracted,
                blocksJson = DocumentBlock.listToJson(blocks)
            )
        }
    }

    fun switchTab(newIndex: Int) {
        if (newIndex == activeTabIndex || newIndex !in 0 until tabs.size) return
        syncCurrentTabContent()
        activeTabIndex = newIndex
        val newBlocks = loadBlocksForTab(tabs[newIndex])
        blocks.clear()
        blocks.addAll(newBlocks)
        activeFocusedBlockIndex = 0
        activeCursorPosition = 0
    }

    fun addTab(tabTitle: String) {
        syncCurrentTabContent()
        val newTab = NoteInternalTab(title = tabTitle)
        tabs.add(newTab)
        activeTabIndex = tabs.lastIndex
        blocks.clear()
        blocks.add(DocumentBlock(type = BlockType.TEXT, text = ""))
        activeFocusedBlockIndex = 0
        activeCursorPosition = 0
    }

    fun renameTab(index: Int, newTitle: String) {
        if (index in 0 until tabs.size) {
            tabs[index] = tabs[index].copy(title = newTitle)
        }
    }

    fun deleteTab(index: Int) {
        if (tabs.size <= 1 || index !in 0 until tabs.size) return
        if (activeTabIndex == index) {
            val nextIndex = (index - 1).coerceAtLeast(0)
            tabs.removeAt(index)
            activeTabIndex = nextIndex
            val newBlocks = loadBlocksForTab(tabs[nextIndex])
            blocks.clear()
            blocks.addAll(newBlocks)
            activeFocusedBlockIndex = 0
            activeCursorPosition = 0
        } else {
            if (activeTabIndex > index) {
                activeTabIndex--
            }
            tabs.removeAt(index)
        }
    }

    fun saveCurrent(): GameNote {
        syncCurrentTabContent()
        val finalTitle = title.ifBlank { strings.newNoteTitleHint }
        val activeContent = tabs.getOrNull(activeTabIndex)?.content ?: ""

        val allImages = mutableListOf<String>()
        val allTodos = mutableListOf<TodoItem>()
        tabs.forEachIndexed { idx, tab ->
            val tabBlocks = if (idx == activeTabIndex) blocks.toList() else {
                try { DocumentBlock.jsonToList(tab.blocksJson) } catch (e: Exception) { emptyList() }
            }
            tabBlocks.forEach { b ->
                if (b.type == BlockType.IMAGE && b.imageUri.isNotBlank()) {
                    allImages.add(b.imageUri)
                }
                if (b.type == BlockType.CHECKLIST && b.text.isNotBlank()) {
                    allTodos.add(TodoItem(id = b.id, text = b.text, isDone = b.isChecked))
                }
            }
        }

        val saved = (note ?: GameNote(title = finalTitle)).copy(
            title = finalTitle,
            content = activeContent,
            gameTag = gameTag,
            noteType = NoteType.REGULAR.name,
            blocksJson = DocumentBlock.listToJson(blocks),
            internalTabs = tabs.toList(),
            imageUris = allImages.distinct(),
            todoItems = allTodos,
            updatedAt = System.currentTimeMillis()
        )
        onSave(saved)
        return saved
    }

    val togglePinAction: () -> Unit = {
        val saved = saveCurrent()
        val targetId = if (saved.id != 0L) saved.id else (note?.id ?: 0L)
        if (targetId != 0L) {
            val isPinned = AppSettingsPreferences.isNotePinnedToFloating(targetId, activeTabIndex)
            if (isPinned) {
                AppSettingsPreferences.clearPinnedFloatingNote(context)
                Toast.makeText(context, strings.unpinnedFromFloatingSuccess, Toast.LENGTH_SHORT).show()
            } else {
                val curScrollIdx = listState.firstVisibleItemIndex
                val curScrollOffset = listState.firstVisibleItemScrollOffset
                AppSettingsPreferences.setPinnedFloatingNote(
                    context = context,
                    noteId = targetId,
                    tabIndex = activeTabIndex,
                    scrollItemIndex = curScrollIdx,
                    scrollOffset = curScrollOffset
                )
                val currentTabName = tabs.getOrNull(activeTabIndex)?.title ?: "التبويب"
                Toast.makeText(context, strings.pinnedToFloatingSuccess(currentTabName), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, strings.saveFirstToPin, Toast.LENGTH_SHORT).show()
        }
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isKeyboardOpen = WindowInsets.ime.getBottom(density) > 0
    val isLandscapeKeyboard = isLandscape && isKeyboardOpen

    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
        topBar = {
            if (!isLandscapeKeyboard) {
                // Requirement 3: Top Bar containing Note Name, Game Name, and Action Buttons
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                saveCurrent()
                                onBack()
                            },
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .testTag("full_note_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.backAction,
                                tint = topBarContent
                            )
                        }
                    },
                title = {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .clickable { isEditingGameTagDialog = true }
                    ) {
                        Text(
                            text = if (title.isBlank()) strings.newNoteTitleHint else title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = topBarContent,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gamepad,
                                contentDescription = null,
                                tint = topBarContent.copy(alpha = 0.85f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (gameTag.isNotBlank()) gameTag else "${strings.defaultGeneralGameTag} (${strings.tapToEdit})",
                                style = MaterialTheme.typography.labelSmall,
                                color = topBarContent.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = strings.editGameTagPrompt,
                                tint = topBarContent.copy(alpha = 0.7f),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Requirement 3: زر مخصص لإدراج/إضافة 'قائمة مهام' في أي مكان داخل الملاحظة (Inline at cursor)
                        IconButton(
                            onClick = {
                                insertChecklistAtCurrentCursor()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("action_insert_checklist")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = strings.checklistNoteTitle,
                                tint = topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // زر إدراج صورة Inline
                        IconButton(
                            onClick = {
                                launchPhotoPickerForMain()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("action_insert_image")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = strings.insertImageAction,
                                tint = topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // زر اسأل جيمني
                        IconButton(
                            onClick = {
                                val plainContent = blocks.filter { it.type == BlockType.TEXT }.joinToString("\n") { it.text }
                                val temp = (note ?: GameNote(title = title.ifBlank { strings.newNoteTitleHint })).copy(
                                    title = title,
                                    content = plainContent,
                                    gameTag = gameTag
                                )
                                onAskGemini(temp)
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("action_ask_gemini")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = strings.geminiAssistantTitle,
                                tint = topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // زر تثبيت الملاحظة والتبويب في النافذة العائمة
                        IconButton(
                            onClick = togglePinAction,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("action_pin_to_floating")
                        ) {
                            val pinnedId by AppSettingsPreferences.pinnedFloatingNoteId.collectAsState()
                            val pinnedTabIdx by AppSettingsPreferences.pinnedFloatingTabIndex.collectAsState()
                            val isPinned = note != null && note.id != 0L && pinnedId == note.id && pinnedTabIdx == activeTabIndex

                            Icon(
                                imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (isPinned) strings.unpinNote else strings.pinToFloatingWindow,
                                tint = if (isPinned) Color(0xFFF59E0B) else topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // زر مشاركة / تصدير الملاحظة كنص منسق
                        IconButton(
                            onClick = {
                                syncCurrentTabContent()
                                val shareTitle = title.ifBlank { strings.untitledNote }
                                val shareText = buildString {
                                    append("🎮 [GameNotes - $gameTag]\n")
                                    append("📌 $shareTitle\n")

                                    tabs.forEachIndexed { tabIndex, tab ->
                                        if (tabs.size > 1) {
                                            append("\n━━━━━━━━━━━━━━━━━━━━\n")
                                            append("📑 ${tab.title}\n")
                                            append("━━━━━━━━━━━━━━━━━━━━\n\n")
                                        } else {
                                            append("\n")
                                        }

                                        val tabBlocks = if (tabIndex == activeTabIndex) {
                                            blocks.toList()
                                        } else {
                                            try {
                                                DocumentBlock.jsonToList(tab.blocksJson)
                                            } catch (e: Exception) {
                                                emptyList()
                                            }
                                        }

                                        if (tabBlocks.isEmpty() && tab.content.isNotBlank()) {
                                            append(tab.content.trim())
                                            append("\n\n")
                                        } else {
                                            tabBlocks.forEach { block ->
                                                when (block.type) {
                                                    BlockType.TEXT -> {
                                                        if (block.text.isNotBlank()) {
                                                            append(block.text.trim())
                                                            append("\n\n")
                                                        }
                                                    }
                                                    BlockType.CHECKLIST -> {
                                                        val checkSymbol = if (block.isChecked) "☑" else "☐"
                                                        append("$checkSymbol ${block.text}\n")
                                                    }
                                                    BlockType.IMAGE -> {
                                                        if (block.text.isNotBlank()) {
                                                            append("🖼️ ${block.text}\n\n")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }.trim()

                                try {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_SUBJECT, shareTitle)
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, strings.shareNoteChooserTitle)
                                    context.startActivity(shareIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, strings.shareNoteErrorNotice, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("action_share_note")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = strings.shareNoteAction,
                                tint = topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // زر حفظ
                        IconButton(
                            onClick = {
                                saveCurrent()
                                onBack()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("action_save_note")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = strings.saveAction,
                                tint = topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarBg)
                )
            }
        },
        bottomBar = {
            if (!isLandscapeKeyboard) {
                // Quick rich insert toolbar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val idx = (activeFocusedBlockIndex + 1).coerceIn(0, blocks.size)
                                blocks.add(idx, DocumentBlock(type = BlockType.TEXT, text = ""))
                                activeFocusedBlockIndex = idx
                                activeCursorPosition = 0
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TextFields,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.addFreeText,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                launchPhotoPickerForMain()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.addImage,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                insertChecklistAtCurrentCursor()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.addTask,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // Requirement 2: Full-Page Free Canvas Rich Text Editor (Like Google Keep / Notion)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .padding(horizontal = if (isLandscapeKeyboard) 8.dp else 20.dp)
                .testTag("full_note_canvas"),
            verticalArrangement = Arrangement.spacedBy(if (isLandscapeKeyboard) 4.dp else 14.dp)
        ) {
            item(key = "note_title_and_tabs_header") {
                if (!isLandscapeKeyboard) {
                    Spacer(modifier = Modifier.height(10.dp))
                    // Large Document Title (Borderless, modern Notion style - strictly single line)
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it.replace("\n", "").replace("\r", "") },
                        singleLine = true,
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { innerTextField ->
                            if (title.isEmpty()) {
                                Text(
                                    text = strings.noteTitlePlaceholder,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            innerTextField()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("full_note_title_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Internal Tabs Bar
                    NoteInternalTabsBar(
                        tabs = tabs,
                        activeTabIndex = activeTabIndex,
                        onSelectTab = { switchTab(it) },
                        onAddTab = { addTab(it) },
                        onRenameTab = { idx, newTitle -> renameTab(idx, newTitle) },
                        onDeleteTab = { deleteTab(it) },
                        isPinned = isPinnedToFloating,
                        onTogglePin = togglePinAction,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))
                }
            }

            // Rich document blocks
            itemsIndexed(blocks, key = { _, block -> block.id }) { index, block ->
                when (block.type) {
                    BlockType.TEXT -> {
                        val textRequester = focusRequesters.getOrPut(block.id) { FocusRequester() }
                        // Unconstrained text block: write freely anywhere with exact cursor tracking
                        var textVal by remember(block.id) {
                            mutableStateOf(TextFieldValue(text = block.text, selection = TextRange(block.text.length)))
                        }
                        if (textVal.text != block.text) {
                            textVal = textVal.copy(text = block.text)
                        }

                        BasicTextField(
                            value = textVal,
                            onValueChange = { newTfv ->
                                textVal = newTfv
                                if (block.text != newTfv.text) {
                                    blocks[index] = block.copy(text = newTfv.text)
                                }
                                activeFocusedBlockIndex = index
                                activeCursorPosition = newTfv.selection.start
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                lineHeight = 26.sp
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (isLandscapeKeyboard) Modifier.fillParentMaxHeight() else Modifier)
                                .focusRequester(textRequester)
                                .onFocusChanged { state ->
                                    if (state.isFocused) {
                                        activeFocusedBlockIndex = index
                                        activeCursorPosition = textVal.selection.start
                                    }
                                }
                                .testTag("rich_text_block_$index")
                        )
                    }

                    BlockType.IMAGE -> {
                        var isDragging by remember { mutableStateOf(false) }
                        var dragOffsetY by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
                        val isTwoImages = block.secondImageUri.isNotBlank()
                        val isAlignRight = block.imageAlignment != "LEFT"

                        if (isTwoImages) {
                            // Requirement 6: صورتين جنب بعض
                            // إذا بدك تحط صورة جنب صورة بروح خيار كتابة كلام جنب الصورة والصورتين بوخذوا عرض المكان بالتساوي
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset { IntOffset(0, dragOffsetY.roundToInt()) }
                                    .graphicsLayer {
                                        if (isDragging) {
                                            scaleX = 1.02f
                                            scaleY = 1.02f
                                            alpha = 0.92f
                                        }
                                    }
                                    .pointerInput(block.id, blocks.size) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                isDragging = true
                                                dragOffsetY = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffsetY += dragAmount.y
                                                val swapThreshold = 55.dp.toPx()
                                                if (dragOffsetY > swapThreshold && index < blocks.size - 1) {
                                                    val item = blocks.removeAt(index)
                                                    blocks.add(index + 1, item)
                                                    dragOffsetY = 0f
                                                } else if (dragOffsetY < -swapThreshold && index > 0) {
                                                    val item = blocks.removeAt(index)
                                                    blocks.add(index - 1, item)
                                                    dragOffsetY = 0f
                                                }
                                            },
                                            onDragEnd = {
                                                isDragging = false
                                                dragOffsetY = 0f
                                            },
                                            onDragCancel = {
                                                isDragging = false
                                                dragOffsetY = 0f
                                            }
                                        )
                                    }
                                    .testTag("dual_images_block_$index")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Image 1 (50% equal width)
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            AsyncImage(
                                                model = block.imageUri,
                                                contentDescription = strings.firstImageZoomCd,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable { zoomedImageUri = block.imageUri }
                                            )
                                            // Zoom icon
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.55f),
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .align(Alignment.TopStart)
                                                    .clickable { zoomedImageUri = block.imageUri }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ZoomIn,
                                                    contentDescription = strings.firstImageZoomCd,
                                                    tint = Color.White,
                                                    modifier = Modifier.padding(4.dp).size(14.dp)
                                                )
                                            }
                                            // Delete image 1 (promotes image 2 to primary)
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.6f),
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .align(Alignment.TopEnd)
                                                    .clickable {
                                                        blocks[index] = block.copy(imageUri = block.secondImageUri, secondImageUri = "")
                                                    }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = strings.deleteFirstImageCd,
                                                    tint = Color.White,
                                                    modifier = Modifier.padding(4.dp).size(14.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Image 2 (50% equal width)
                                    Card(
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            AsyncImage(
                                                model = block.secondImageUri,
                                                contentDescription = strings.secondImageZoomCd,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(150.dp)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .clickable { zoomedImageUri = block.secondImageUri }
                                            )
                                            // Zoom icon
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.55f),
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .align(Alignment.TopStart)
                                                    .clickable { zoomedImageUri = block.secondImageUri }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ZoomIn,
                                                    contentDescription = strings.secondImageZoomCd,
                                                    tint = Color.White,
                                                    modifier = Modifier.padding(4.dp).size(14.dp)
                                                )
                                            }
                                            // Delete image 2 (returns to single image)
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.6f),
                                                modifier = Modifier
                                                    .padding(6.dp)
                                                    .align(Alignment.TopEnd)
                                                    .clickable {
                                                        blocks[index] = block.copy(secondImageUri = "")
                                                    }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = strings.deleteSecondImageCd,
                                                    tint = Color.White,
                                                    modifier = Modifier.padding(4.dp).size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Dual Image Footer (Drag Handle, Info label & remove block)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.DragHandle,
                                            contentDescription = strings.longPressToReorder,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = strings.twoSideBySideImagesNotice,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                            fontSize = 11.sp
                                        )
                                    }

                                    IconButton(
                                        onClick = { blocks.removeAt(index) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = strings.deleteBothImages,
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Requirement 3: Single image with alignment (يمين أو شمال) + smart sizing + option to add second image
                            val isSideTextHidden = block.imageWidthPercent > 0.75f
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset { IntOffset(0, dragOffsetY.roundToInt()) }
                                    .graphicsLayer {
                                        if (isDragging) {
                                            scaleX = 1.02f
                                            scaleY = 1.02f
                                            alpha = 0.92f
                                        }
                                    }
                                    .pointerInput(block.id, blocks.size) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = {
                                                isDragging = true
                                                dragOffsetY = 0f
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffsetY += dragAmount.y
                                                val swapThreshold = 55.dp.toPx()
                                                if (dragOffsetY > swapThreshold && index < blocks.size - 1) {
                                                    val item = blocks.removeAt(index)
                                                    blocks.add(index + 1, item)
                                                    dragOffsetY = 0f
                                                } else if (dragOffsetY < -swapThreshold && index > 0) {
                                                    val item = blocks.removeAt(index)
                                                    blocks.add(index - 1, item)
                                                    dragOffsetY = 0f
                                                }
                                            },
                                            onDragEnd = {
                                                isDragging = false
                                                dragOffsetY = 0f
                                            },
                                            onDragCancel = {
                                                isDragging = false
                                                dragOffsetY = 0f
                                            }
                                        )
                                    }
                                    .testTag("inline_image_block_$index")
                            ) {
                                // Explicit LTR container to strictly position Right vs Left
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        val imageCardComposable = @Composable {
                                            Card(
                                                shape = RoundedCornerShape(16.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                                border = if (isDragging) {
                                                    androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                                } else {
                                                    androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                                },
                                                elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 8.dp else 2.dp),
                                                modifier = if (isSideTextHidden) {
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .testTag("inline_image_card_$index")
                                                } else {
                                                    Modifier
                                                        .weight(block.imageWidthPercent.coerceIn(0.25f, 0.75f), fill = true)
                                                        .testTag("inline_image_card_$index")
                                                }
                                            ) {
                                                Column(modifier = Modifier.padding(6.dp)) {
                                                    Box(modifier = Modifier.fillMaxWidth()) {
                                                        AsyncImage(
                                                            model = block.imageUri,
                                                            contentDescription = strings.noteImageClickToZoom,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .height(200.dp)
                                                                .clip(RoundedCornerShape(12.dp))
                                                                .clickable { zoomedImageUri = block.imageUri }
                                                        )

                                                        Surface(
                                                            shape = CircleShape,
                                                            color = Color.Black.copy(alpha = 0.5f),
                                                            modifier = Modifier
                                                                .padding(6.dp)
                                                                .align(Alignment.TopStart)
                                                                .clickable { zoomedImageUri = block.imageUri }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.ZoomIn,
                                                                contentDescription = strings.zoomImage,
                                                                tint = Color.White,
                                                                modifier = Modifier.padding(4.dp).size(16.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        val textCardComposable = @Composable {
                                            Card(
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                                                modifier = Modifier
                                                    .weight((1f - block.imageWidthPercent).coerceIn(0.05f, 0.75f), fill = true)
                                                    .padding(top = 2.dp)
                                            ) {
                                                // Text box for typing beside image
                                                BasicTextField(
                                                    value = block.text,
                                                    onValueChange = { newText ->
                                                        blocks[index] = block.copy(text = newText)
                                                    },
                                                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                                                        color = MaterialTheme.colorScheme.onBackground,
                                                        lineHeight = 22.sp
                                                    ),
                                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                                    decorationBox = { innerTextField ->
                                                        Box(modifier = Modifier.padding(10.dp)) {
                                                            if (block.text.isEmpty()) {
                                                                Text(
                                                                    text = strings.writeTextBesideImagePlaceholder,
                                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                                    )
                                                                )
                                                            }
                                                            innerTextField()
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .onFocusChanged { state ->
                                                            if (state.isFocused) {
                                                                activeFocusedBlockIndex = index
                                                                activeCursorPosition = block.text.length
                                                            }
                                                        }
                                                )
                                            }
                                        }

                                        if (isSideTextHidden) {
                                            imageCardComposable()
                                        } else if (isAlignRight) {
                                            // LTR order: Text on Left, Image on Right
                                            textCardComposable()
                                            Spacer(modifier = Modifier.width(8.dp))
                                            imageCardComposable()
                                        } else {
                                            // LTR order: Image on Left, Text on Right
                                            imageCardComposable()
                                            Spacer(modifier = Modifier.width(8.dp))
                                            textCardComposable()
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(3.dp))

                                // Controls Panel: Alignment Toggle + Preset Sizing + Smooth Slider + Add 2nd Image + Delete
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(6.dp)) {
                                        // Row 1: Alignment toggle & Add second image button & Delete button
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (!isSideTextHidden) {
                                                    // Alignment Switch: يمين / شمال (only relevant when side text exists)
                                                    Surface(
                                                        onClick = {
                                                            val newAlign = if (isAlignRight) "LEFT" else "RIGHT"
                                                            blocks[index] = block.copy(imageAlignment = newAlign)
                                                        },
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.SwapHoriz,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(14.dp),
                                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                            )
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(
                                                                text = if (isAlignRight) strings.alignmentRight else strings.alignmentLeft,
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                                            )
                                                        }
                                                    }

                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }

                                                // Add Second Image Side-by-Side button
                                                Surface(
                                                    onClick = {
                                                        launchPhotoPickerForSecondImage(index)
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer,
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Collections,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(14.dp),
                                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = strings.addSideBySideImage,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                                        )
                                                    }
                                                }
                                            }

                                            IconButton(
                                                onClick = { blocks.removeAt(index) },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = strings.deleteImage,
                                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        // Row 2: Sizing Presets (30%, 50%, 75%, 100%) + Slider
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = strings.sizeLabel,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))

                                            val presets = listOf(0.30f to "30%", 0.50f to "50%", 0.75f to "75%", 1.00f to "100%")
                                            presets.forEach { (pct, label) ->
                                                val isCur = kotlin.math.abs(block.imageWidthPercent - pct) < 0.05f
                                                Surface(
                                                    onClick = { blocks[index] = block.copy(imageWidthPercent = pct) },
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (isCur) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        0.5.dp,
                                                        if (isCur) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 2.dp)
                                                ) {
                                                    Text(
                                                        text = label,
                                                        fontSize = 10.sp,
                                                        fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isCur) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            // Smooth Slider for fine tuning
                                            androidx.compose.material3.Slider(
                                                value = block.imageWidthPercent,
                                                onValueChange = { blocks[index] = block.copy(imageWidthPercent = it) },
                                                valueRange = 0.25f..1.0f,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(26.dp)
                                                    .testTag("image_size_slider_$index")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    BlockType.CHECKLIST -> {
                        val requester = focusRequesters.getOrPut(block.id) { FocusRequester() }
                        // Requirement: Embedded checklist item inside the document
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = block.isChecked,
                                onCheckedChange = { checked ->
                                    blocks[index] = block.copy(isChecked = checked)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            BasicTextField(
                                value = block.text,
                                onValueChange = { newText ->
                                    if (newText.contains("\n")) {
                                        val parts = newText.split("\n", limit = 2)
                                        // If current item is empty when Enter was pressed, switch seamlessly to regular TEXT!
                                        if (block.text.isBlank()) {
                                            val newTextBlock = DocumentBlock(
                                                type = BlockType.TEXT,
                                                text = if (parts.size > 1) parts[1] else ""
                                            )
                                            blocks[index] = newTextBlock
                                            pendingFocusBlockId = newTextBlock.id
                                        } else {
                                            blocks[index] = block.copy(text = parts[0])
                                            val newChecklistBlock = DocumentBlock(
                                                type = BlockType.CHECKLIST,
                                                text = if (parts.size > 1) parts[1] else "",
                                                isChecked = false
                                            )
                                            blocks.add(index + 1, newChecklistBlock)
                                            pendingFocusBlockId = newChecklistBlock.id
                                        }
                                    } else {
                                        blocks[index] = block.copy(text = newText)
                                    }
                                },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (block.isChecked) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onBackground,
                                    textDecoration = if (block.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        if (block.text.isBlank()) {
                                            val newTextBlock = DocumentBlock(type = BlockType.TEXT, text = "")
                                            blocks[index] = newTextBlock
                                            pendingFocusBlockId = newTextBlock.id
                                        } else {
                                            val newChecklistBlock = DocumentBlock(type = BlockType.CHECKLIST, text = "", isChecked = false)
                                            blocks.add(index + 1, newChecklistBlock)
                                            pendingFocusBlockId = newChecklistBlock.id
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(requester)
                                    .onFocusChanged { state ->
                                        if (state.isFocused) {
                                            activeFocusedBlockIndex = index
                                            activeCursorPosition = block.text.length
                                        }
                                    }
                            )
                            IconButton(
                                onClick = { blocks.removeAt(index) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = strings.deleteAction,
                                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Requirement: Tap to resume writing standard text below checklists / canvas
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 220.dp)
                        .clickable {
                            val last = blocks.lastOrNull()
                            if (last == null || last.type != BlockType.TEXT || last.text.isNotBlank()) {
                                blocks.add(DocumentBlock(type = BlockType.TEXT, text = ""))
                            }
                        }
                        .padding(top = 16.dp, bottom = 64.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (blocks.lastOrNull()?.type == BlockType.CHECKLIST) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                            modifier = Modifier.clickable {
                                blocks.add(DocumentBlock(type = BlockType.TEXT, text = ""))
                                activeFocusedBlockIndex = blocks.size - 1
                                activeCursorPosition = 0
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TextFields,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.resumeWritingNormalTextHint,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

        }
    }

    // Dialog for changing Game Tag
    if (isEditingGameTagDialog) {
        var customTagInput by remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { isEditingGameTagDialog = false }
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clickable(enabled = false) { }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.selectOrChangeGameTab,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (availableGameTabs.isNotEmpty()) {
                        Text(
                            text = strings.availableTabsTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        availableGameTabs.forEach { tab ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        gameTag = tab
                                        isEditingGameTagDialog = false
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gamepad,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (tab == gameTag) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    OutlinedTextField(
                        value = customTagInput,
                        onValueChange = { customTagInput = it },
                        label = { Text(strings.orWriteNewGameName) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (customTagInput.isNotBlank()) {
                                    gameTag = customTagInput.trim()
                                }
                                isEditingGameTagDialog = false
                            }
                        ) {
                            Text(strings.confirmAction)
                        }
                    }
                }
            }
        }
    }

    // Fullscreen zoomable image dialog with Markup / Draw Mode
    if (zoomedImageUri != null) {
        val currentZoomed = zoomedImageUri
        FullScreenImageZoomDialog(
            imageUri = currentZoomed!!,
            onDismiss = { zoomedImageUri = null },
            onImageSaved = { newUri ->
                // Update the block that held zoomedImageUri to the newly saved drawn image
                val idx = blocks.indexOfFirst { it.type == BlockType.IMAGE && (it.imageUri == currentZoomed || it.secondImageUri == currentZoomed) }
                if (idx != -1) {
                    val b = blocks[idx]
                    blocks[idx] = if (b.imageUri == currentZoomed) {
                        b.copy(imageUri = newUri)
                    } else {
                        b.copy(secondImageUri = newUri)
                    }
                }
                zoomedImageUri = null
            }
        )
    }
}
