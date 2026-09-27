package com.spinel.gamenotes.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinel.gamenotes.data.NoteTag
import com.spinel.gamenotes.util.LanguagePreferences.LocalAppStrings

// Predefined vibrant palette suitable for dark/teal gamer theme
val TAG_COLOR_PALETTE = listOf(
    "#10B981", // Emerald / Teal
    "#0EA5E9", // Sky Blue
    "#EF4444", // Coral Red
    "#F59E0B", // Amber Gold
    "#8B5CF6", // Purple
    "#EC4899", // Pink
    "#14B8A6", // Mint Teal
    "#F97316"  // Orange
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteTagsEditorSection(
    tags: List<NoteTag>,
    onAddTag: (name: String, colorHex: String) -> Unit,
    onRemoveTag: (NoteTag) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var isAddingTag by remember { mutableStateOf(false) }
    var newTagName by remember { mutableStateOf("") }
    var selectedColorHex by remember { mutableStateOf(TAG_COLOR_PALETTE[0]) }

    val handleAdd = {
        val trimmed = newTagName.trim()
        if (trimmed.isNotBlank()) {
            val formatted = if (trimmed.startsWith("#")) trimmed else "#$trimmed"
            onAddTag(formatted, selectedColorHex)
            newTagName = ""
            isAddingTag = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("note_tags_editor_section")
    ) {
        // Tag chips row + Add button
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                val tagColor = remember(tag.colorHex) {
                    try {
                        Color(android.graphics.Color.parseColor(tag.colorHex))
                    } catch (e: Exception) {
                        Color(0xFF10B981)
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = tagColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, tagColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 3.dp, bottom = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(tagColor, CircleShape)
                        )
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = tagColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        IconButton(
                            onClick = { onRemoveTag(tag) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.delete,
                                tint = tagColor.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            // Button to open new tag input
            if (!isAddingTag) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clickable { isAddingTag = true }
                        .testTag("add_tag_chip_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = strings.addTagButton,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = strings.addTagButton,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Expanded inline creator panel
        AnimatedVisibility(visible = isAddingTag) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Label,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${strings.tagLabel}:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { isAddingTag = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.cancel,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Text input for tag name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "#",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        BasicTextField(
                            value = newTagName,
                            onValueChange = { newTagName = it.replace("\n", "").replace(" ", "_") },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { handleAdd() }),
                            decorationBox = { innerTextField ->
                                if (newTagName.isEmpty()) {
                                    Text(
                                        text = strings.tagHint,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        fontSize = 12.sp
                                    )
                                }
                                innerTextField()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tag_name_input")
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { handleAdd() }
                                .testTag("confirm_add_tag_btn")
                        ) {
                            Text(
                                text = strings.add,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Color picker row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = strings.tagChooseColor,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        TAG_COLOR_PALETTE.forEach { hex ->
                            val color = try {
                                Color(android.graphics.Color.parseColor(hex))
                            } catch (e: Exception) {
                                Color(0xFF10B981)
                            }
                            val isSelected = selectedColorHex == hex

                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 24.dp else 20.dp)
                                    .background(color, CircleShape)
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorHex = hex }
                            )
                        }
                    }
                }
            }
        }
    }
}
