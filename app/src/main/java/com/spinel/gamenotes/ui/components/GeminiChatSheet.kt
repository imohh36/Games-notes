package com.spinel.gamenotes.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.outlined.DeleteOutline
import com.spinel.gamenotes.data.BlockType
import com.spinel.gamenotes.data.DocumentBlock
import com.spinel.gamenotes.data.GameNote
import com.spinel.gamenotes.data.NoteType
import com.spinel.gamenotes.data.TodoItem
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinel.gamenotes.GameNotesApp
import com.spinel.gamenotes.ai.ChatMessage
import com.spinel.gamenotes.ai.GeminiRepository
import com.spinel.gamenotes.data.AiModelOption
import com.spinel.gamenotes.data.AppSettingsPreferences
import com.spinel.gamenotes.data.ChatSession
import com.spinel.gamenotes.util.LanguagePreferences.LocalAppStrings
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeminiChatSheet(
    initialPrompt: String = "",
    noteTitle: String = "",
    gameTag: String = "Game",
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { GameNotesApp.instance.repository }
    val geminiRepository = remember { GeminiRepository() }
    val currentAiModel by AppSettingsPreferences.aiModel.collectAsState()
    val userGeminiApiKey by AppSettingsPreferences.userGeminiApiKey.collectAsState()
    val isAiEnabled = userGeminiApiKey.isNotBlank()

    // Multi-Chat Sessions State
    val chatSessions by repository.allChatSessions.collectAsState(initial = emptyList())
    var currentSessionId by remember { mutableStateOf<Long?>(null) }
    var showSessionsHistoryDialog by remember { mutableStateOf(false) }

    var promptInput by remember { mutableStateOf(initialPrompt) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val chatImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri.toString()
        }
    }

    val messages = remember { mutableStateListOf<ChatMessage>() }

    // Helper: load messages for a specific session
    suspend fun loadMessagesForSession(sessionId: Long) {
        val savedEntities = repository.getMessagesForChatDirect(sessionId)
        messages.clear()
        if (savedEntities.isNotEmpty()) {
            savedEntities.forEach { entity ->
                val senderEnum = if (entity.sender.equals("USER", ignoreCase = true)) {
                    ChatMessage.Sender.USER
                } else {
                    ChatMessage.Sender.GEMINI
                }
                messages.add(
                    ChatMessage(
                        id = entity.id.toString(),
                        sender = senderEnum,
                        text = entity.text,
                        timestamp = entity.timestamp
                    )
                )
            }
        } else {
            messages.add(
                ChatMessage(
                    sender = ChatMessage.Sender.GEMINI,
                    text = strings.geminiWelcomeGreeting(gameTag)
                )
            )
        }
    }

    // Initialize or bind active session on entry
    LaunchedEffect(Unit) {
        val active = repository.getOrCreateActiveChatSession()
        currentSessionId = active.id
        loadMessagesForSession(active.id)
    }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun createNewChat() {
        coroutineScope.launch {
            val titleName = if (noteTitle.isNotBlank()) strings.geminiChatWithNoteTitle(noteTitle) else strings.geminiNewChatSession
            val newId = repository.createChatSession(titleName)
            currentSessionId = newId
            messages.clear()
            messages.add(
                ChatMessage(
                    sender = ChatMessage.Sender.GEMINI,
                    text = strings.geminiNewChatStarted
                )
            )
            Toast.makeText(context, strings.geminiChatSessionCreatedNotice, Toast.LENGTH_SHORT).show()
        }
    }

    fun selectSession(sessionId: Long) {
        currentSessionId = sessionId
        coroutineScope.launch {
            loadMessagesForSession(sessionId)
        }
    }

    fun sendMessage() {
        val trimmed = promptInput.trim()
        val imageToSend = selectedImageUri
        if ((trimmed.isBlank() && imageToSend.isNullOrBlank()) || isLoading) return
        val activeChatId = currentSessionId ?: return

        val displayText = if (trimmed.isNotBlank()) trimmed else strings.geminiAttachedImageAnalysis
        val userMessage = ChatMessage(
            sender = ChatMessage.Sender.USER,
            text = displayText,
            imageUriString = imageToSend
        )
        messages.add(userMessage)
        val promptToSend = trimmed
        promptInput = ""
        selectedImageUri = null
        isLoading = true
        errorMessage = null

        // Immediately persist user message to Room Database linked to activeChatId
        coroutineScope.launch {
            repository.insertChatMessage(chatId = activeChatId, sender = "USER", text = displayText)
        }

        coroutineScope.launch {
            val result = geminiRepository.askGemini(
                prompt = promptToSend,
                previousChat = messages,
                modelOption = currentAiModel,
                context = context,
                imageUriString = imageToSend
            )
            isLoading = false
            result.onSuccess { reply ->
                messages.add(ChatMessage(sender = ChatMessage.Sender.GEMINI, text = reply))
                // Immediately persist Gemini response linked to activeChatId
                coroutineScope.launch {
                    repository.insertChatMessage(chatId = activeChatId, sender = "MODEL", text = reply)
                }
            }.onFailure { err ->
                errorMessage = err.localizedMessage ?: strings.geminiErrorCommunicating
                val replyErr = strings.geminiReplyErrorPrefix(err.localizedMessage ?: "")
                messages.add(
                    ChatMessage(
                        sender = ChatMessage.Sender.GEMINI,
                        text = replyErr
                    )
                )
                coroutineScope.launch {
                    repository.insertChatMessage(chatId = activeChatId, sender = "MODEL", text = replyErr)
                }
            }
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gemini_chat_sheet"),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = strings.geminiAssistantTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Interactive Model Switcher Chip
                            Surface(
                                onClick = {
                                    val nextModel = when (currentAiModel) {
                                        AiModelOption.GEMINI_FLASH_3_5 -> AiModelOption.GEMINI_FLASH_LITE_3_8
                                        AiModelOption.GEMINI_FLASH_LITE_3_8 -> AiModelOption.GEMINI_PRO_3_1
                                        AiModelOption.GEMINI_PRO_3_1 -> AiModelOption.GEMINI_FLASH_3_5
                                    }
                                    AppSettingsPreferences.setAiModel(context, nextModel)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = when (currentAiModel) {
                                    AiModelOption.GEMINI_PRO_3_1 -> MaterialTheme.colorScheme.tertiaryContainer
                                    AiModelOption.GEMINI_FLASH_LITE_3_8 -> MaterialTheme.colorScheme.secondaryContainer
                                    AiModelOption.GEMINI_FLASH_3_5 -> MaterialTheme.colorScheme.primaryContainer
                                },
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when (currentAiModel) {
                                        AiModelOption.GEMINI_PRO_3_1 -> MaterialTheme.colorScheme.tertiary
                                        AiModelOption.GEMINI_FLASH_LITE_3_8 -> MaterialTheme.colorScheme.secondary
                                        AiModelOption.GEMINI_FLASH_3_5 -> MaterialTheme.colorScheme.primary
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (currentAiModel) {
                                            AiModelOption.GEMINI_PRO_3_1 -> Icons.Default.Psychology
                                            AiModelOption.GEMINI_FLASH_LITE_3_8 -> Icons.Default.Speed
                                            AiModelOption.GEMINI_FLASH_3_5 -> Icons.Default.Bolt
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = when (currentAiModel) {
                                            AiModelOption.GEMINI_PRO_3_1 -> MaterialTheme.colorScheme.onTertiaryContainer
                                            AiModelOption.GEMINI_FLASH_LITE_3_8 -> MaterialTheme.colorScheme.onSecondaryContainer
                                            AiModelOption.GEMINI_FLASH_3_5 -> MaterialTheme.colorScheme.onPrimaryContainer
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = currentAiModel.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = when (currentAiModel) {
                                            AiModelOption.GEMINI_PRO_3_1 -> MaterialTheme.colorScheme.onTertiaryContainer
                                            AiModelOption.GEMINI_FLASH_LITE_3_8 -> MaterialTheme.colorScheme.onSecondaryContainer
                                            AiModelOption.GEMINI_FLASH_3_5 -> MaterialTheme.colorScheme.onPrimaryContainer
                                        }
                                    )
                                }
                            }
                        }
                        if (noteTitle.isNotBlank()) {
                            Text(
                                text = strings.geminiLinkedToNote(noteTitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Button to open Sessions History
                    IconButton(
                        onClick = { showSessionsHistoryDialog = true },
                        modifier = Modifier.testTag("gemini_chat_sessions_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = strings.geminiChatHistoryCd,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Button to start a brand new conversation
                    IconButton(
                        onClick = { createNewChat() },
                        modifier = Modifier.testTag("gemini_new_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = strings.geminiNewChatCd,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Clear current chat messages button
                    if (messages.size > 1 || (messages.size == 1 && messages[0].sender == ChatMessage.Sender.USER)) {
                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.testTag("clear_gemini_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = strings.geminiClearCurrentChatCd,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_gemini_sheet")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Multi-Chat Sessions Horizontal Bar
            if (chatSessions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(chatSessions, key = { it.id }) { session ->
                        val isSelected = session.id == currentSessionId
                        Surface(
                            onClick = { selectSession(session.id) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = session.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // In-layout clear chat confirmation and sessions history overlays are moved to the root Box below

            Spacer(modifier = Modifier.height(10.dp))

            // Quick suggestion chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val suggestions = listOf(
                    strings.geminiQuickQuestion1,
                    strings.geminiQuickQuestion2,
                    strings.geminiQuickQuestion3
                )
                suggestions.forEach { chipText ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.clickable {
                            promptInput = if (promptInput.isBlank()) chipText else "$promptInput ($chipText)"
                        }
                    ) {
                        Text(
                            text = chipText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(min = 120.dp, max = 280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isUser = msg.sender == ChatMessage.Sender.USER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (isUser) 0.85f else 0.92f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isUser) 14.dp else 2.dp,
                                        bottomEnd = if (isUser) 2.dp else 14.dp
                                    )
                                )
                                .background(
                                    if (isUser) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isUser) strings.geminiYouLabel else strings.geminiAiLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    val timeStr = remember(msg.timestamp) {
                                        try {
                                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))
                                        } catch (e: Exception) {
                                            ""
                                        }
                                    }
                                    if (timeStr.isNotBlank()) {
                                        Text(
                                            text = timeStr,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = (if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.6f),
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                if (!msg.imageUriString.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Image(
                                        painter = rememberAsyncImagePainter(model = msg.imageUriString),
                                        contentDescription = strings.geminiAttachedImageLabel,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 180.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                                if (!isUser && msg.text.isNotBlank() && !msg.text.startsWith("عذراً،") && !msg.text.startsWith("Sorry,")) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                                        thickness = 0.5.dp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Button 1: Convert to Note
                                        Surface(
                                            onClick = {
                                                val noteTitleFromPrompt = if (noteTitle.isNotBlank()) "$noteTitle - Gemini" else strings.geminiNoteFallbackTitle
                                                val newBlocks = listOf(
                                                    DocumentBlock(type = BlockType.TEXT, text = msg.text)
                                                )
                                                val newNote = GameNote(
                                                    title = noteTitleFromPrompt,
                                                    content = msg.text,
                                                    gameTag = gameTag,
                                                    blocksJson = DocumentBlock.listToJson(newBlocks),
                                                    noteType = NoteType.REGULAR.name
                                                )
                                                coroutineScope.launch {
                                                    repository.insertNote(newNote)
                                                    Toast.makeText(context, strings.geminiConvertedToNoteSuccess, Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                            modifier = Modifier.testTag("gemini_convert_to_note_button")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = strings.geminiConvertToNote,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = strings.geminiConvertToNote,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }

                                        // Button 2: Convert to Checklist
                                        Surface(
                                            onClick = {
                                                val lines = msg.text.lines().map { it.trim() }.filter { it.isNotBlank() }
                                                val items = lines.mapNotNull { line ->
                                                    val cleaned = line.replaceFirst(Regex("^([\\-*•]|\\d+[\\.\\)])\\s*"), "").trim()
                                                    if (cleaned.isNotBlank()) TodoItem(text = cleaned, isDone = false) else null
                                                }
                                                val validItems = if (items.isNotEmpty()) items else listOf(TodoItem(text = msg.text, isDone = false))
                                                val newNote = GameNote(
                                                    title = if (noteTitle.isNotBlank()) "$noteTitle - ${strings.todoTasks}" else strings.geminiTasksFallbackTitle,
                                                    content = "",
                                                    gameTag = gameTag,
                                                    noteType = NoteType.TODO_LIST.name,
                                                    todoItems = validItems
                                                )
                                                coroutineScope.launch {
                                                    repository.insertNote(newNote)
                                                    Toast.makeText(context, strings.geminiConvertedToChecklistSuccess, Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)),
                                            modifier = Modifier.testTag("gemini_convert_to_checklist_button")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Checklist,
                                                    contentDescription = strings.geminiConvertToChecklist,
                                                    tint = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = strings.geminiConvertToChecklist,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.weight(1f))

                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Gemini Gaming Tip", msg.text)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, strings.geminiCopiedNotice, Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = strings.geminiCopyResponse,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strings.geminiThinking,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (!isAiEnabled) {
                // Lock notice banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.geminiConfigureApiKeyPrompt,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                // Notice badge reminding user: No Auto-send
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = strings.geminiTextCopiedToInputHint,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Selected image attachment preview above input
            if (selectedImageUri != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = selectedImageUri),
                            contentDescription = strings.geminiAttachedImageLabel,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.geminiRemoveImageCd,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.geminiAttachedImageLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Input Row: Attachment button + Editable input box + Send button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attach Image Button
                IconButton(
                    onClick = {
                        chatImagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    enabled = isAiEnabled && !isLoading,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("gemini_attach_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = strings.geminiAttachImageCd,
                        tint = if (selectedImageUri != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = { 
                        Text(
                            if (isAiEnabled) strings.geminiInputHint else strings.geminiInputDisabledHint,
                            fontSize = 13.sp
                        ) 
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("gemini_prompt_input"),
                    maxLines = 3,
                    enabled = isAiEnabled,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { sendMessage() },
                    enabled = isAiEnabled && (promptInput.isNotBlank() || selectedImageUri != null) && !isLoading,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (isAiEnabled && (promptInput.isNotBlank() || selectedImageUri != null) && !isLoading) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .testTag("gemini_send_button")
                ) {
                    Icon(
                        imageVector = if (isAiEnabled) Icons.AutoMirrored.Filled.Send else Icons.Default.Lock,
                        contentDescription = strings.geminiSendMessageCd,
                        tint = if (isAiEnabled && (promptInput.isNotBlank() || selectedImageUri != null) && !isLoading) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // In-layout Clear Chat Confirmation Overlay (Crash-free, no AlertDialog)
        if (showClearConfirmDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = true, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = strings.geminiClearChatConfirmTitle,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = strings.geminiClearChatConfirmMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showClearConfirmDialog = false }) {
                                Text(strings.cancel)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    showClearConfirmDialog = false
                                    val activeId = currentSessionId
                                    if (activeId != null) {
                                        coroutineScope.launch {
                                            repository.deleteChatSession(activeId)
                                            val remaining = repository.getAllChatSessionsDirect()
                                            if (remaining.isNotEmpty()) {
                                                val nextSession = remaining.first()
                                                currentSessionId = nextSession.id
                                                loadMessagesForSession(nextSession.id)
                                            } else {
                                                val newId = repository.createChatSession(strings.geminiNewChatSession)
                                                currentSessionId = newId
                                                messages.clear()
                                                messages.add(
                                                    ChatMessage(
                                                        sender = ChatMessage.Sender.GEMINI,
                                                        text = strings.geminiChatClearedGreeting
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text(strings.clear, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // In-layout Sessions History Overlay (Crash-free, no AlertDialog)
        if (showSessionsHistoryDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable(enabled = true, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.geminiPastSessionsTitle,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick = { showSessionsHistoryDialog = false },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = strings.close)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        if (chatSessions.isEmpty()) {
                            Text(
                                text = strings.geminiNoPastChats,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 280.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(chatSessions, key = { it.id }) { session ->
                                    val isCurrent = session.id == currentSessionId
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectSession(session.id)
                                                showSessionsHistoryDialog = false
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = session.title,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                )
                                                val dateStr = remember(session.updatedAt) {
                                                    try {
                                                        SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault()).format(Date(session.updatedAt))
                                                    } catch (e: Exception) {
                                                        ""
                                                    }
                                                }
                                                if (dateStr.isNotBlank()) {
                                                    Text(
                                                        text = dateStr,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        repository.deleteChatSession(session.id)
                                                        if (session.id == currentSessionId) {
                                                            val remaining = repository.getAllChatSessionsDirect()
                                                            if (remaining.isNotEmpty()) {
                                                                selectSession(remaining.first().id)
                                                            } else {
                                                                createNewChat()
                                                            }
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = strings.geminiDeleteSessionCd,
                                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { showSessionsHistoryDialog = false }) {
                                Text(strings.close)
                            }
                            Button(
                                onClick = {
                                    showSessionsHistoryDialog = false
                                    createNewChat()
                                }
                            ) {
                                Text("+ ${strings.geminiNewChatSession}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
}
