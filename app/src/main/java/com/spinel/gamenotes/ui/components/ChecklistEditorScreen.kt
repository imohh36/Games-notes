package com.spinel.gamenotes.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinel.gamenotes.data.GameNote
import com.spinel.gamenotes.data.NoteTag
import com.spinel.gamenotes.data.NoteType
import com.spinel.gamenotes.data.TodoItem
import com.spinel.gamenotes.ui.theme.ThemePreferences
import com.spinel.gamenotes.util.LanguagePreferences.LocalAppStrings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistEditorScreen(
    note: GameNote?,
    initialGameTag: String,
    availableGameTabs: List<String>,
    initialScrollItemIndex: Int = 0,
    initialScrollOffset: Int = 0,
    onBack: () -> Unit,
    onSave: (GameNote) -> Unit,
    onAskGemini: (GameNote) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var gameTag by remember { mutableStateOf(note?.gameTag?.takeIf { it.isNotBlank() } ?: initialGameTag) }
    var isEditingGameTagDialog by remember { mutableStateOf(false) }
    val tags = remember { mutableStateListOf<NoteTag>().apply { note?.tags?.let { addAll(it) } } }

    // List of checkable tasks: starts with 1 item by default if empty
    val items = remember {
        mutableStateListOf<TodoItem>().apply {
            if (note != null && note.todoItems.isNotEmpty()) {
                addAll(note.todoItems)
            } else {
                add(TodoItem(text = "", isDone = false))
            }
        }
    }

    // Focus requesters for auto-focus on Enter key
    val focusRequesters = remember { mutableMapOf<String, FocusRequester>() }
    var pendingFocusItemId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialScrollItemIndex,
        initialFirstVisibleItemScrollOffset = initialScrollOffset
    )
    val coroutineScope = rememberCoroutineScope()

    val initialScrollIndex = remember(note?.id) { initialScrollItemIndex }
    val initialScrollOff = remember(note?.id) { initialScrollOffset }

    // Accurate scroll restoration on launch for pinned reading state (run once per note, not on every offset change)
    LaunchedEffect(note?.id) {
        if (initialScrollIndex > 0 || initialScrollOff > 0) {
            delay(80)
            try {
                listState.scrollToItem(initialScrollIndex, initialScrollOff)
            } catch (_: Exception) {}
        }
    }

    val pinnedId by AppSettingsPreferences.pinnedFloatingNoteId.collectAsState()
    val isPinnedToFloating = note != null && note.id != 0L && pinnedId == note.id

    val localContext = LocalContext.current

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
                        context = localContext,
                        scrollItemIndex = pair.first,
                        scrollOffset = pair.second
                    )
                }
        }
    }

    // Handle auto-focus and auto-scroll when a new item is created via Enter or button
    LaunchedEffect(pendingFocusItemId, items.size) {
        pendingFocusItemId?.let { id ->
            delay(60)
            focusRequesters[id]?.requestFocus()
            val targetIdx = items.indexOfFirst { it.id == id }
            if (targetIdx >= 0) {
                listState.animateScrollToItem(targetIdx)
            }
            pendingFocusItemId = null
        }
    }

    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val customTextColor = MaterialTheme.colorScheme.primary
    val topBarBg = customTextColor
    val topBarContent = ThemePreferences.getContrastingContentColor(topBarBg)

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val imeInsets = androidx.compose.foundation.layout.WindowInsets.ime
    val isKeyboardOpen by androidx.compose.runtime.remember(density, imeInsets) {
        androidx.compose.runtime.derivedStateOf { imeInsets.getBottom(density) > 0 }
    }
    val isLandscapeKeyboard = isLandscape && isKeyboardOpen

    // Progress metrics
    val totalCount = items.size
    val completedCount = items.count { it.isDone }
    val progressFraction = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    fun saveCurrent(): GameNote {
        val filteredTodos = items.filter { it.text.isNotBlank() }
        val finalTitle = title.ifBlank { strings.newChecklistDefaultTitle }
        val saved = (note ?: GameNote(title = finalTitle)).copy(
            title = finalTitle,
            gameTag = gameTag,
            noteType = NoteType.TODO_LIST.name,
            tags = tags.toList(),
            todoItems = filteredTodos,
            updatedAt = System.currentTimeMillis()
        )
        onSave(saved)
        return saved
    }

    Scaffold(
        topBar = {
            if (!isLandscapeKeyboard) {
                // Requirement 3: Top Bar containing Note Name, Game Name, and Opposite Action Buttons
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                saveCurrent()
                                onBack()
                            },
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .testTag("checklist_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = strings.backAndSave,
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
                            text = if (title.isBlank()) strings.newChecklistDefaultTitle else title,
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
                                text = if (gameTag.isNotBlank()) gameTag else "${strings.generalTag} (${strings.tapToEdit})",
                                style = MaterialTheme.typography.labelSmall,
                                color = topBarContent.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = strings.editGameTag,
                                tint = topBarContent.copy(alpha = 0.7f),
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Gemini Action
                        IconButton(
                            onClick = {
                                val tempNote = (note ?: GameNote(title = title.ifBlank { strings.newChecklistDefaultTitle })).copy(
                                    title = title,
                                    gameTag = gameTag,
                                    todoItems = items.toList()
                                )
                                onAskGemini(tempNote)
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("checklist_ask_gemini_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = strings.askGemini,
                                tint = topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // زر تثبيت قائمة المهام في النافذة العائمة
                        IconButton(
                            onClick = {
                                val saved = saveCurrent()
                                val targetId = if (saved.id != 0L) saved.id else (note?.id ?: 0L)
                                if (targetId != 0L) {
                                    val isPinned = AppSettingsPreferences.isNotePinnedToFloating(targetId, 0)
                                    if (isPinned) {
                                        AppSettingsPreferences.clearPinnedFloatingNote(context)
                                        Toast.makeText(context, strings.unpinnedFromFloatingSuccess, Toast.LENGTH_SHORT).show()
                                    } else {
                                        val curScrollIdx = listState.firstVisibleItemIndex
                                        val curScrollOffset = listState.firstVisibleItemScrollOffset
                                        AppSettingsPreferences.setPinnedFloatingNote(
                                            context = context,
                                            noteId = targetId,
                                            tabIndex = 0,
                                            scrollItemIndex = curScrollIdx,
                                            scrollOffset = curScrollOffset
                                        )
                                        Toast.makeText(context, strings.pinnedToFloatingSuccess(saved.title.ifBlank { strings.newTodoListTitle }), Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, strings.saveFirstToPin, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("checklist_pin_button")
                        ) {
                            val pinnedId by AppSettingsPreferences.pinnedFloatingNoteId.collectAsState()
                            val isPinned = note != null && note.id != 0L && pinnedId == note.id

                            Icon(
                                imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                                contentDescription = strings.pinToFloatingWindow,
                                tint = if (isPinned) Color(0xFFF59E0B) else topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // زر مشاركة / تصدير قائمة المهام كنص منسق
                        IconButton(
                            onClick = {
                                val shareTitle = title.ifBlank { strings.newTodoListTitle }
                                val shareText = buildString {
                                    append("🎮 [GameNotes - $gameTag]\n")
                                    append("📋 $shareTitle\n")
                                    val doneCount = items.count { it.isDone }
                                    val total = items.size
                                    append("${strings.progressLabel}: $doneCount/$total (${if (total > 0) (doneCount * 100 / total) else 0}%)\n\n")

                                    items.forEach { item ->
                                        val checkSymbol = if (item.isDone) "☑" else "☐"
                                        append("$checkSymbol ${item.text}\n")
                                    }
                                }.trim()

                                try {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_SUBJECT, shareTitle)
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, strings.shareChecklistChooserTitle)
                                    context.startActivity(shareIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, strings.shareNoteErrorNotice, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("checklist_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = strings.shareChecklist,
                                tint = topBarContent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Done / Save button
                        IconButton(
                            onClick = {
                                saveCurrent()
                                onBack()
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("checklist_done_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = strings.saveAndClose,
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
    contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
    containerColor = MaterialTheme.colorScheme.background
) { paddingValues ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .imePadding()
            .padding(if (isLandscapeKeyboard) 6.dp else 16.dp)
    ) {
        if (!isLandscapeKeyboard) {
            // Requirement 4: المربع الأول العلوي: مخصص لكتابة عنوان قائمة المهام
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checklist_title_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = strings.checklistTitleLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it.replace("\n", "").replace("\r", "") },
                        placeholder = { Text(strings.checklistTitlePlaceholder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checklist_title_input"),
                        singleLine = true,
                        maxLines = 1,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    // Progress indicator if items exist
                    if (items.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.progressTasks(completedCount, totalCount),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }


                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

            // Requirement 4: المربع الكبير السفلي (الذي يغطي الشاشة)
            // يبدأ تلقائياً برقم 1. وبجانبه مربع اختيار فاضي (Checkbox).
            // وعند الضغط على Enter في الكيبورد، ينشئ تلقائياً السطر التالي (2.) ومربع اختيار فاضي!
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("checklist_items_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.checklistItemsInstruction,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = {
                                val newItem = TodoItem(text = "", isDone = false)
                                items.add(newItem)
                                pendingFocusItemId = newItem.id
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = strings.addChecklistItem,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(items, key = { _, item -> item.id }) { index, item ->
                            val requester = focusRequesters.getOrPut(item.id) { FocusRequester() }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Auto-numbering: 1., 2., 3., etc.
                                Surface(
                                    shape = CircleShape,
                                    color = if (item.isDone) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}.",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Checkbox: checks / unchecks the task
                                Checkbox(
                                    checked = item.isDone,
                                    onCheckedChange = { isChecked ->
                                        items[index] = item.copy(isDone = isChecked)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("checklist_checkbox_$index")
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // Task Text Field: auto-handles Enter key to insert next numbered item!
                                TextField(
                                    value = item.text,
                                    onValueChange = { newText ->
                                        if (newText.contains("\n")) {
                                            // Intercept Enter key!
                                            val parts = newText.split("\n", limit = 2)
                                            val currentText = parts[0]
                                            val nextText = if (parts.size > 1) parts[1] else ""
                                            items[index] = item.copy(text = currentText)
                                            val insertIndex = index + 1
                                            val newItem = TodoItem(text = nextText, isDone = false)
                                            items.add(insertIndex, newItem)
                                            pendingFocusItemId = newItem.id
                                        } else {
                                            items[index] = item.copy(text = newText)
                                        }
                                    },
                                    placeholder = {
                                        Text(
                                            text = "${strings.checklistWriteTaskPlaceholder} ${index + 1}...",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                        )
                                    },
                                    textStyle = TextStyle(
                                        fontSize = 14.sp,
                                        color = if (item.isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        else MaterialTheme.colorScheme.onSurface,
                                        textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = {
                                            // Pressing IME Next creates next numbered task
                                            val insertIndex = index + 1
                                            val newItem = TodoItem(text = "", isDone = false)
                                            items.add(insertIndex, newItem)
                                            pendingFocusItemId = newItem.id
                                        }
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .focusRequester(requester)
                                        .onFocusChanged { focusState ->
                                            if (focusState.isFocused) {
                                                coroutineScope.launch {
                                                    delay(100)
                                                    listState.animateScrollToItem(index)
                                                }
                                            }
                                        }
                                        .testTag("checklist_item_input_$index")
                                 )

                                // Delete task button
                                IconButton(
                                    onClick = {
                                        if (items.size > 1) {
                                            items.removeAt(index)
                                        } else {
                                            items[0] = TodoItem(text = "", isDone = false)
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = strings.delete,
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for editing Game Tag
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
                    .fillMaxWidth(0.95f)
                    .clickable(enabled = false) {}
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.gameTabDialogTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (availableGameTabs.isNotEmpty()) {
                        Text(
                            text = strings.availableTabs,
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
                            Text(strings.confirm)
                        }
                    }
                }
            }
        }
    }
}
