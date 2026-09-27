package com.spinel.gamenotes.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.ViewSidebar
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.spinel.gamenotes.data.BlockType
import com.spinel.gamenotes.data.DocumentBlock
import com.spinel.gamenotes.data.GameNote
import com.spinel.gamenotes.util.LanguagePreferences.LocalAppStrings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val noteDateFormatter = ThreadLocal.withInitial { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: GameNote,
    onToggleTodo: (String) -> Unit,
    onAskGemini: (GameNote) -> Unit,
    onEdit: (GameNote) -> Unit,
    onDelete: (GameNote) -> Unit,
    onTogglePin: ((GameNote) -> Unit)? = null,
    onPinAsMiniWidget: ((GameNote) -> Unit)? = null,
    onImageEdited: ((note: GameNote, oldUri: String, newUri: String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    activeInternalTabIndex: Int? = null,
    onTabSelected: ((Int) -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    var isExpanded by remember { mutableStateOf(false) }
    var zoomedImageUri by remember { mutableStateOf<String?>(null) }

    val gameTagColor = remember(note.gameTag) {
        when {
            note.gameTag.contains("Stardew", ignoreCase = true) -> Color(0xFF10B981)
            note.gameTag.contains("Minecraft", ignoreCase = true) -> Color(0xFF0EA5E9)
            note.gameTag.contains("RPG", ignoreCase = true) -> Color(0xFFA855F7)
            note.gameTag.contains("Elden", ignoreCase = true) -> Color(0xFFF59E0B)
            else -> Color(0xFF06B6D4)
        }
    }

    val formattedDate = remember(note.updatedAt) { noteDateFormatter.get()?.format(Date(note.updatedAt)) ?: "" }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("note_card_${note.id}")
            .combinedClickable(
                onClick = { onEdit(note) },
                onLongClick = { onTogglePin?.invoke(note) }
            )
            .border(
                width = if (note.isPinned) 1.5.dp else 1.dp,
                color = if (note.isPinned) gameTagColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 10.dp else 14.dp)
        ) {
            // Header: Type badge, Game badge, Date, and Pin action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Note Type Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (note.isTodoList) Color(0xFF0284C7).copy(alpha = 0.18f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (note.isTodoList) Icons.Default.Checklist else Icons.AutoMirrored.Filled.Article,
                                contentDescription = null,
                                tint = if (note.isTodoList) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (note.isTodoList) strings.checklistBadge else strings.regularNoteBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (note.isTodoList) Color(0xFF38BDF8) else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Game Tag Badge if available
                    if (note.gameTag.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = gameTagColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, gameTagColor.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Gamepad,
                                    contentDescription = null,
                                    tint = gameTagColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = note.gameTag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = gameTagColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )

                    if (onTogglePin != null) {
                        IconButton(
                            onClick = { onTogglePin(note) },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("pin_button_${note.id}")
                        ) {
                            Icon(
                                imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = if (note.isPinned) strings.unpinNote else strings.pinNote,
                                tint = if (note.isPinned) gameTagColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = note.title,
                style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Internal Tabs Chips under note title
            val effectiveTabs = remember(note.internalTabs, note.content, note.blocksJson) {
                note.getEffectiveTabs()
            }
            var localActiveTabIndex by remember(note.id, note.updatedAt) {
                mutableIntStateOf(activeInternalTabIndex ?: 0)
            }
            LaunchedEffect(activeInternalTabIndex) {
                if (activeInternalTabIndex != null) {
                    localActiveTabIndex = activeInternalTabIndex
                }
            }
            val currentTabIndex = if (effectiveTabs.isNotEmpty()) {
                localActiveTabIndex.coerceIn(0, effectiveTabs.size - 1)
            } else 0
            val currentTab = effectiveTabs.getOrNull(currentTabIndex) ?: effectiveTabs.firstOrNull()

            val activeBlocksJson = currentTab?.blocksJson ?: note.blocksJson
            val activeContent = currentTab?.content?.takeIf { it.isNotBlank() } ?: note.content

            if (effectiveTabs.size > 1) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    effectiveTabs.forEachIndexed { idx, tab ->
                        val isSelected = idx == currentTabIndex
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    localActiveTabIndex = idx
                                    onTabSelected?.invoke(idx)
                                }
                                .testTag("note_tab_chip_${note.id}_$idx")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = tab.title.ifBlank { strings.tabNumber(idx + 1) },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.5.sp
                                )
                            }
                        }
                    }
                }
            }

            // Requirement 8: Unified Preview: Display text, images, and tasks integrated together exactly as they look inside
            val parsedBlocks = remember(activeBlocksJson) {
                if (activeBlocksJson.isNotBlank() && activeBlocksJson != "[]") {
                    DocumentBlock.jsonToList(activeBlocksJson).filter { b ->
                        b.text.isNotBlank() || b.imageUri.isNotBlank()
                    }
                } else {
                    emptyList()
                }
            }

            // Task Progress Bar on external note cards (Main app and Floating Overlay list)
            val (totalTasks, completedTasks) = remember(note.todoItems, parsedBlocks) {
                val checklistBlocks = parsedBlocks.filter { it.type == BlockType.CHECKLIST }
                if (checklistBlocks.isNotEmpty()) {
                    checklistBlocks.size to checklistBlocks.count { it.isChecked }
                } else {
                    note.todoItems.size to note.todoItems.count { it.isDone }
                }
            }
            val hasTasks = totalTasks > 0
            val progressFraction = if (hasTasks) completedTasks.toFloat() / totalTasks else 0f

            if (hasTasks) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.achievementTasksFormat(completedTasks, totalTasks),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (completedTasks == totalTasks && totalTasks > 0) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (completedTasks == totalTasks && totalTasks > 0) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            if (parsedBlocks.isNotEmpty()) {
                val displayBlocks = if (isExpanded) parsedBlocks else parsedBlocks.take(if (isCompact) 3 else 5)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    displayBlocks.forEach { block ->
                        when (block.type) {
                            BlockType.TEXT -> {
                                if (block.text.isNotBlank()) {
                                    Text(
                                        text = block.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                                        overflow = TextOverflow.Ellipsis,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }
                            }
                            BlockType.IMAGE -> {
                                if (block.secondImageUri.isNotBlank()) {
                                    // Dual images side by side (equal width)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                                .clickable { zoomedImageUri = block.imageUri },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = block.imageUri,
                                                contentDescription = strings.firstNoteImage,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(if (isCompact) 85.dp else 115.dp)
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                                .clickable { zoomedImageUri = block.secondImageUri },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = block.secondImageUri,
                                                contentDescription = strings.secondNoteImage,
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(if (isCompact) 85.dp else 115.dp)
                                            )
                                        }
                                    }
                                } else if (block.imageUri.isNotBlank()) {
                                    val isAlignRight = block.imageAlignment != "LEFT"
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        val imgBox = @Composable {
                                            Box(
                                                modifier = Modifier
                                                    .weight(block.imageWidthPercent.coerceIn(0.25f, 1.0f), fill = false)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                                    .clickable { zoomedImageUri = block.imageUri },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                AsyncImage(
                                                    model = block.imageUri,
                                                    contentDescription = strings.attachedImageZoom,
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .heightIn(max = if (isCompact) (120 * block.imageWidthPercent).dp.coerceAtLeast(80.dp) else (180 * block.imageWidthPercent).dp.coerceAtLeast(110.dp))
                                                )
                                            }
                                        }

                                        val textBox = @Composable {
                                            if (block.imageWidthPercent < 1.0f && block.text.isNotBlank()) {
                                                Text(
                                                    text = block.text,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                                                    overflow = TextOverflow.Ellipsis,
                                                    modifier = Modifier.weight(1f - block.imageWidthPercent.coerceIn(0.25f, 1.0f))
                                                )
                                            }
                                        }

                                        if (isAlignRight) {
                                            imgBox()
                                            if (block.imageWidthPercent < 1.0f && block.text.isNotBlank()) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                textBox()
                                            }
                                        } else {
                                            if (block.imageWidthPercent < 1.0f && block.text.isNotBlank()) {
                                                textBox()
                                                Spacer(modifier = Modifier.width(8.dp))
                                            }
                                            imgBox()
                                        }
                                    }
                                }
                            }
                            BlockType.CHECKLIST -> {
                                if (block.text.isNotBlank()) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = ripple(bounded = true),
                                                onClick = { onToggleTodo(block.id) }
                                            )
                                    ) {
                                        Checkbox(
                                            checked = block.isChecked,
                                            onCheckedChange = { onToggleTodo(block.id) },
                                            modifier = Modifier
                                                .padding(end = 6.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = ripple(bounded = false, radius = 20.dp),
                                                    onClick = { onToggleTodo(block.id) }
                                                ),
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = MaterialTheme.colorScheme.primary,
                                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        )
                                        Text(
                                            text = block.text,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (block.isChecked) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                            textDecoration = if (block.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                                            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (!isExpanded && parsedBlocks.size > displayBlocks.size) {
                        Text(
                            text = strings.moreItemsCount(parsedBlocks.size - displayBlocks.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            } else {
                // Fallback for legacy notes or notes with plain text content
                if (activeContent.isNotBlank()) {
                    Text(
                        text = activeContent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
                if (note.imageUris.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        note.imageUris.take(if (isExpanded) 6 else 2).forEach { uriString ->
                            AsyncImage(
                                model = uriString,
                                contentDescription = strings.attachedImageZoom,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(if (isCompact) 56.dp else 72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .clickable { zoomedImageUri = uriString }
                            )
                        }
                    }
                }
                if (note.todoItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    note.todoItems.take(if (isExpanded) note.todoItems.size else 3).forEach { todo ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true),
                                    onClick = { onToggleTodo(todo.id) }
                                )
                        ) {
                            Checkbox(
                                checked = todo.isDone,
                                onCheckedChange = { onToggleTodo(todo.id) },
                                modifier = Modifier
                                    .padding(end = 6.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = false, radius = 20.dp),
                                        onClick = { onToggleTodo(todo.id) }
                                    ),
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary,
                                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                            Text(
                                text = todo.text,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (todo.isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (todo.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }


            // Expand / Collapse button for long notes or compact view
            val hasMoreContent = (parsedBlocks.size > 3) || 
                                 (note.content.length > 120) || 
                                 (note.todoItems.size > 3) || 
                                 (note.imageUris.size > 2)
            if (hasMoreContent || isCompact) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = { isExpanded = !isExpanded },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) strings.showLess else strings.showMore,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isExpanded) strings.showLess else strings.showMore,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left action button based on type
                if (!note.isTodoList) {
                    Button(
                        onClick = { onAskGemini(note) },
                        modifier = Modifier
                            .testTag("ask_gemini_button_${note.id}")
                            .height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.askGemini,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (onPinAsMiniWidget != null) {
                    // Pin as floating mini widget button for To-Do lists
                    Button(
                        onClick = { onPinAsMiniWidget(note) },
                        modifier = Modifier
                            .testTag("pin_as_mini_widget_${note.id}")
                            .height(36.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0284C7).copy(alpha = 0.2f),
                            contentColor = Color(0xFF38BDF8)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ViewSidebar,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = Color(0xFF38BDF8)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.pinAsMiniWidget,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                // Edit, Delete & Expand/Collapse icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("expand_collapse_note_button_${note.id}")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) strings.showLess else strings.showMore,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { onEdit(note) },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("edit_note_button_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = strings.editAction,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = { onDelete(note) },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("delete_note_button_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = strings.deleteAction,
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }

    if (zoomedImageUri != null) {
        val currentUri = zoomedImageUri!!
        FullScreenImageZoomDialog(
            imageUri = currentUri,
            onDismiss = { zoomedImageUri = null },
            onImageSaved = { newUri ->
                onImageEdited?.invoke(note, currentUri, newUri)
                zoomedImageUri = null
            }
        )
    }
}
