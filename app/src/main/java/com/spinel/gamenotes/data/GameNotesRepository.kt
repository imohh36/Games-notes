package com.spinel.gamenotes.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

class GameNotesRepository(
    private val dao: GameNoteDao,
    private val tabDao: GameTabDao,
    private val chatDao: ChatMessageDao? = null,
    private val sessionDao: ChatSessionDao? = null
) {
    val allNotes: Flow<List<GameNote>> = dao.getAllNotes()
    val allTabs: Flow<List<GameTab>> = tabDao.getAllTabs()
    val deletedNotes: Flow<List<GameNote>> = dao.getDeletedNotes()
    val allChatMessages: Flow<List<ChatMessageEntity>> = chatDao?.getAllMessagesFlow() ?: flowOf(emptyList())
    val allChatSessions: Flow<List<ChatSession>> = sessionDao?.getAllSessionsFlow() ?: flowOf(emptyList())

    // Chat Sessions Management
    suspend fun createChatSession(title: String = "محادثة جديدة"): Long {
        val now = System.currentTimeMillis()
        val session = ChatSession(
            title = title.ifBlank { "محادثة جديدة" },
            createdAt = now,
            updatedAt = now
        )
        return sessionDao?.insertSession(session) ?: -1L
    }

    suspend fun getOrCreateActiveChatSession(): ChatSession {
        val latest = sessionDao?.getLatestSession()
        if (latest != null) return latest
        val id = createChatSession("محادثة جديدة")
        return sessionDao?.getSessionById(id) ?: ChatSession(id = id, title = "محادثة جديدة")
    }

    suspend fun getSessionById(sessionId: Long): ChatSession? {
        return sessionDao?.getSessionById(sessionId)
    }

    suspend fun updateSessionTitle(sessionId: Long, newTitle: String) {
        val session = sessionDao?.getSessionById(sessionId) ?: return
        sessionDao.updateSession(session.copy(title = newTitle, updatedAt = System.currentTimeMillis()))
    }

    suspend fun touchSession(sessionId: Long) {
        val session = sessionDao?.getSessionById(sessionId) ?: return
        sessionDao.updateSession(session.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun deleteChatSession(sessionId: Long) {
        sessionDao?.deleteSessionById(sessionId)
        chatDao?.deleteMessagesByChatId(sessionId)
    }

    suspend fun clearAllChatSessions() {
        sessionDao?.clearAllSessions()
        chatDao?.clearAllMessages()
    }

    suspend fun getAllChatSessionsDirect(): List<ChatSession> {
        return sessionDao?.getAllSessionsDirect() ?: emptyList()
    }

    // Chat Messages Management (tied to chatId)
    fun getMessagesForChatFlow(chatId: Long): Flow<List<ChatMessageEntity>> {
        return chatDao?.getMessagesByChatIdFlow(chatId) ?: flowOf(emptyList())
    }

    suspend fun getMessagesForChatDirect(chatId: Long): List<ChatMessageEntity> {
        return chatDao?.getMessagesByChatIdDirect(chatId) ?: emptyList()
    }

    suspend fun insertChatMessage(
        chatId: Long = 0L,
        sender: String,
        text: String,
        timestamp: Long = System.currentTimeMillis()
    ): Long {
        val resolvedChatId = if (chatId > 0) {
            chatId
        } else {
            getOrCreateActiveChatSession().id
        }

        // Update session's title if it's the default title and user just sent a message
        val currentSession = sessionDao?.getSessionById(resolvedChatId)
        if (currentSession != null) {
            val updatedTitle = if (sender.equals("USER", ignoreCase = true) &&
                (currentSession.title == "محادثة جديدة" || currentSession.title.isBlank())) {
                val preview = text.trim().take(28).replace("\n", " ")
                if (preview.isNotBlank()) preview else currentSession.title
            } else {
                currentSession.title
            }
            sessionDao.updateSession(currentSession.copy(title = updatedTitle, updatedAt = timestamp))
        }

        return chatDao?.insertMessage(
            ChatMessageEntity(
                chatId = resolvedChatId,
                sender = sender,
                text = text,
                timestamp = timestamp
            )
        ) ?: -1L
    }

    suspend fun getAllChatMessagesDirect(): List<ChatMessageEntity> {
        return chatDao?.getAllMessagesDirect() ?: emptyList()
    }

    suspend fun clearChatHistory() {
        chatDao?.clearAllMessages()
        sessionDao?.clearAllSessions()
    }

    fun getNotesByGame(gameTag: String): Flow<List<GameNote>> {
        return if (gameTag.isBlank() || gameTag == "الكل" || gameTag.equals("All", ignoreCase = true)) {
            dao.getAllNotes()
        } else {
            dao.getNotesByGame(gameTag)
        }
    }

    fun getNoteById(id: Long): Flow<GameNote?> = dao.getNoteById(id)

    suspend fun getNoteByIdDirect(id: Long): GameNote? = dao.getNoteById(id).firstOrNull()

    suspend fun insertNote(note: GameNote): Long = dao.insertNote(note)

    suspend fun updateNote(note: GameNote) {
        dao.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
    }

    // Soft delete sets isDeleted = true and moves to Trash
    suspend fun softDeleteNote(id: Long) = dao.softDeleteNoteById(id)

    // Restore note from Trash
    suspend fun restoreNote(id: Long) = dao.restoreNoteById(id)

    // Permanent deletion
    suspend fun hardDeleteNote(note: GameNote) = dao.deleteNote(note)

    suspend fun hardDeleteNoteById(id: Long) = dao.deleteNoteById(id)

    // Empty entire trash
    suspend fun emptyTrash() = dao.emptyTrash()

    // Backward compatibility alias: deleteNote performs soft delete
    suspend fun deleteNote(note: GameNote) = dao.softDeleteNoteById(note.id)

    suspend fun deleteNoteById(id: Long) = dao.softDeleteNoteById(id)

    suspend fun insertTab(name: String): Long {
        if (name.isBlank()) return -1
        return tabDao.insertTab(GameTab(name = name.trim()))
    }

    suspend fun deleteTab(tab: GameTab) = tabDao.deleteTab(tab)

    suspend fun deleteTabById(id: Long) = tabDao.deleteTabById(id)

    suspend fun toggleTodoItem(noteId: Long, todoId: String) {
        val note = dao.getNoteById(noteId).firstOrNull() ?: return
        val updatedTodos = note.todoItems.map {
            if (it.id == todoId) it.copy(isDone = !it.isDone) else it
        }
        val updatedBlocksJson = if (note.blocksJson.isNotBlank() && note.blocksJson != "[]") {
            try {
                val blocks = DocumentBlock.jsonToList(note.blocksJson)
                val newBlocks = blocks.map { b ->
                    if (b.type == BlockType.CHECKLIST && b.id == todoId) {
                        b.copy(isChecked = !b.isChecked)
                    } else b
                }
                DocumentBlock.listToJson(newBlocks)
            } catch (e: Exception) {
                note.blocksJson
            }
        } else {
            note.blocksJson
        }
        dao.updateNote(note.copy(
            todoItems = updatedTodos,
            blocksJson = updatedBlocksJson,
            updatedAt = System.currentTimeMillis()
        ))
    }

    suspend fun getAllNotesDirect(): List<GameNote> = dao.getAllNotesDirect()

    suspend fun getAllTabsDirect(): List<GameTab> = tabDao.getAllTabsDirect()

    suspend fun insertNotesList(notes: List<GameNote>) = dao.insertNotes(notes)

    suspend fun insertTabsList(tabs: List<GameTab>) = tabDao.insertTabs(tabs)
}
