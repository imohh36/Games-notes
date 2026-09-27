package com.spinel.gamenotes.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinel.gamenotes.data.NoteInternalTab
import com.spinel.gamenotes.util.LanguagePreferences.LocalAppStrings

@Composable
fun NoteInternalTabsBar(
    tabs: List<NoteInternalTab>,
    activeTabIndex: Int,
    onSelectTab: (Int) -> Unit,
    onAddTab: (String) -> Unit,
    onRenameTab: (Int, String) -> Unit,
    onDeleteTab: (Int) -> Unit,
    isPinned: Boolean = false,
    onTogglePin: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current

    // In-layout states replacing System Dialogs to avoid BadTokenException in Floating Overlay Service
    var showNewTabUI by remember { mutableStateOf(false) }
    var tabToEditIndex by remember { mutableStateOf<Int?>(null) }
    var tabToDeleteIndex by remember { mutableStateOf<Int?>(null) }

    var newTabTitleInput by remember { mutableStateOf("") }
    var renameTabTitleInput by remember { mutableStateOf("") }

    // Collapse / Expand Tabs state
    var isTabsCollapsed by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isTabsCollapsed) {
                // Collapsed mode: Show only the active tab chip
                val currentTab = tabs.getOrNull(activeTabIndex)
                if (currentTab != null) {
                    val activeBg = MaterialTheme.colorScheme.primaryContainer
                    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isTabsCollapsed = false }
                            .testTag("collapsed_active_tab_item"),
                        shape = RoundedCornerShape(12.dp),
                        color = activeBg,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = currentTab.title.ifBlank { strings.tabNumber(activeTabIndex + 1) },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = contentColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            } else {
                // Expanded mode: Show full LazyRow of tabs
                LazyRow(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("internal_tabs_row"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(tabs, key = { index, tab -> "${tab.id}_$index" }) { index, tab ->
                        val isActive = index == activeTabIndex
                        val activeBg = MaterialTheme.colorScheme.primaryContainer
                        val inactiveBg = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        val bgColor by animateColorAsState(if (isActive) activeBg else inactiveBg, label = "tabBg")
                        val contentColor = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelectTab(index) }
                                .testTag("internal_tab_item_$index"),
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            border = if (isActive) {
                                BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                            } else {
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isActive) Icons.Default.Folder else Icons.Default.Tab,
                                    contentDescription = null,
                                    tint = if (isActive) MaterialTheme.colorScheme.primary else contentColor.copy(alpha = 0.7f),
                                    modifier = Modifier.size(15.dp)
                                )

                                Text(
                                    text = tab.title.ifBlank { strings.tabNumber(index + 1) },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                    color = contentColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.5.sp
                                )

                                if (isActive) {
                                    // Rename button
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                tabToEditIndex = index
                                                renameTabTitleInput = tab.title
                                                showNewTabUI = false
                                                tabToDeleteIndex = null
                                            }
                                            .testTag("rename_tab_$index"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = strings.editTabName,
                                            tint = contentColor.copy(alpha = 0.75f),
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }

                                    // Delete button if more than 1 tab
                                    if (tabs.size > 1) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    tabToDeleteIndex = index
                                                    showNewTabUI = false
                                                    tabToEditIndex = null
                                                }
                                                .testTag("delete_tab_$index"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = strings.deleteTabCd,
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add new tab button item
                    item {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    newTabTitleInput = strings.tabNumber(tabs.size + 1)
                                    showNewTabUI = !showNewTabUI
                                    tabToEditIndex = null
                                    tabToDeleteIndex = null
                                }
                                .testTag("action_add_internal_tab"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (showNewTabUI) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (showNewTabUI) Icons.Default.Close else Icons.Default.Add,
                                    contentDescription = strings.addTabCd,
                                    tint = if (showNewTabUI) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = strings.newInternalTab,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showNewTabUI) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Pin / Unpin button in Tabs bar
            if (onTogglePin != null) {
                Surface(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .clickable { onTogglePin() }
                        .testTag("action_toggle_pin_internal_tab"),
                    shape = CircleShape,
                    color = if (isPinned) Color(0xFFF59E0B).copy(alpha = 0.18f) else Color.Transparent,
                    border = if (isPinned) BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f)) else null
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (isPinned) strings.unpinFromFloating else strings.pinToFloatingWindow,
                            tint = if (isPinned) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(2.dp))
            }

            // Collapse / Expand toggle button
            IconButton(
                onClick = { isTabsCollapsed = !isTabsCollapsed },
                modifier = Modifier
                    .size(34.dp)
                    .testTag("toggle_tabs_collapse_button")
            ) {
                Icon(
                    imageVector = if (isTabsCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = if (isTabsCollapsed) strings.expandTabsCd else strings.collapseTabsCd,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ========================================================================
        // IN-LAYOUT OVERLAYS (NO WindowToken, completely safe for Service Context)
        // ========================================================================

        // 1. In-Layout UI: Add New Tab
        AnimatedVisibility(
            visible = showNewTabUI,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("in_layout_add_tab_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.addInternalTabTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(
                            onClick = { showNewTabUI = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.close,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newTabTitleInput,
                        onValueChange = { newTabTitleInput = it },
                        label = { Text(strings.tabNameLabel) },
                        placeholder = { Text(strings.tabNamePlaceholder) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_tab_title"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showNewTabUI = false },
                            modifier = Modifier.testTag("cancel_add_tab_button")
                        ) {
                            Text(strings.cancel)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                val title = newTabTitleInput.trim().ifBlank { strings.tabNumber(tabs.size + 1) }
                                onAddTab(title)
                                showNewTabUI = false
                            },
                            modifier = Modifier.testTag("confirm_add_tab_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.addTabButton)
                        }
                    }
                }
            }
        }

        // 2. In-Layout UI: Rename Tab
        AnimatedVisibility(
            visible = tabToEditIndex != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            tabToEditIndex?.let { idx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("in_layout_rename_tab_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.editTabName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(
                                onClick = { tabToEditIndex = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = strings.close,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = renameTabTitleInput,
                            onValueChange = { renameTabTitleInput = it },
                            label = { Text(strings.tabNameLabel) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_rename_tab_title"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { tabToEditIndex = null },
                                modifier = Modifier.testTag("cancel_rename_tab_button")
                            ) {
                                Text(strings.cancel)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    val title = renameTabTitleInput.trim().ifBlank { strings.tabNumber(idx + 1) }
                                    onRenameTab(idx, title)
                                    tabToEditIndex = null
                                },
                                modifier = Modifier.testTag("confirm_rename_tab_button")
                            ) {
                                Text(strings.save)
                            }
                        }
                    }
                }
            }
        }

        // 3. In-Layout UI: Delete Tab
        AnimatedVisibility(
            visible = tabToDeleteIndex != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            tabToDeleteIndex?.let { idx ->
                val tabTitle = tabs.getOrNull(idx)?.title ?: strings.thisTab
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("in_layout_delete_tab_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.deleteTabTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            IconButton(
                                onClick = { tabToDeleteIndex = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = strings.close,
                                    tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.deleteInternalTabConfirm(tabTitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { tabToDeleteIndex = null },
                                modifier = Modifier.testTag("cancel_delete_tab_button")
                            ) {
                                Text(strings.cancel, color = MaterialTheme.colorScheme.onErrorContainer)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    onDeleteTab(idx)
                                    tabToDeleteIndex = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.testTag("confirm_delete_tab_button")
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(strings.delete)
                            }
                        }
                    }
                }
            }
        }
    }
}
