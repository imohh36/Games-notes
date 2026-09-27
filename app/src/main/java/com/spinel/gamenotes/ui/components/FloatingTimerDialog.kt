package com.spinel.gamenotes.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinel.gamenotes.service.FloatingOverlayService
import com.spinel.gamenotes.service.FloatingTimerManager
import com.spinel.gamenotes.util.LanguagePreferences.LocalAppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FloatingTimerDialog(
    onDismiss: () -> Unit,
    onLaunchOverlay: (() -> Unit)? = null
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val isRunning by FloatingTimerManager.isRunning.collectAsState()
    val isFinished by FloatingTimerManager.isFinished.collectAsState()
    val remainingSeconds by FloatingTimerManager.remainingSeconds.collectAsState()
    val totalSeconds by FloatingTimerManager.totalSeconds.collectAsState()
    val currentLabel by FloatingTimerManager.timerLabel.collectAsState()

    var customLabel by remember { mutableStateOf(if (isRunning) currentLabel else "") }
    var inputMinutes by remember { mutableStateOf("5") }

    val presetTimes = remember(strings) {
        listOf(1, 3, 5, 10, 15, 30).map { mins ->
            Pair(strings.timerPresetMinutes(mins), mins)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = strings.timerTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.timerSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active Countdown Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isFinished) Color(0xFFEF4444) else if (isRunning) primaryColor else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isFinished) strings.timerTimeUp else if (isRunning) strings.timerCounting(currentLabel) else strings.timerStopped,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isFinished) Color(0xFFEF4444) else if (isRunning) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Large Digital Display
                        Text(
                            text = FloatingTimerManager.formatRemainingTime(),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isFinished) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 2.sp
                        )

                        if (totalSeconds > 0) {
                            val progress = if (totalSeconds > 0) (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f) else 0f
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isFinished) Color(0xFFEF4444) else primaryColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Timer Control Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isRunning) {
                                Button(
                                    onClick = { FloatingTimerManager.pauseTimer() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Pause, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.timerPause, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            } else if (remainingSeconds > 0) {
                                Button(
                                    onClick = { FloatingTimerManager.resumeTimer(context) },
                                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.timerResume, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            if (remainingSeconds > 0 || isFinished) {
                                OutlinedButton(
                                    onClick = { FloatingTimerManager.resetTimer() },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(strings.timerReset, fontSize = 12.sp)
                                }
                            }
                        }

                        // Notification Note
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.timerNotificationNotice,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Timer Setup Fields
                OutlinedTextField(
                    value = customLabel,
                    onValueChange = { customLabel = it },
                    label = { Text(strings.timerTaskNameLabel, fontSize = 12.sp) },
                    placeholder = { Text(strings.timerTaskNamePlaceholder, fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Preset Chips
                Text(
                    text = strings.timerQuickPresets,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    presetTimes.forEach { (title, minutes) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = primaryColor.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor.copy(alpha = 0.35f)),
                            modifier = Modifier.clickable {
                                inputMinutes = minutes.toString()
                                val label = customLabel.ifBlank { title }
                                FloatingTimerManager.startTimer(context, label, minutes * 60)
                            }
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                color = primaryColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Custom Minutes Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputMinutes,
                        onValueChange = { inputMinutes = it.filter { ch -> ch.isDigit() } },
                        label = { Text(strings.timerDurationMinutesLabel, fontSize = 11.sp) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            val mins = inputMinutes.toIntOrNull() ?: 5
                            val label = customLabel.ifBlank { strings.timerDefaultLabelFormat(mins) }
                            FloatingTimerManager.startTimer(context, label, mins * 60)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        modifier = Modifier
                            .height(52.dp)
                            .padding(top = 6.dp)
                            .testTag("start_custom_timer_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(strings.timerStartFullButton, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onLaunchOverlay?.invoke()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text(strings.doneButton, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close)
            }
        }
    )
}
