package com.spinel.gamenotes.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.spinel.gamenotes.MainActivity
import com.spinel.gamenotes.R
import com.spinel.gamenotes.util.AppLanguage
import com.spinel.gamenotes.util.LanguagePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object FloatingTimerManager {
    const val TIMER_CHANNEL_ID = "gamenotes_timer_channel"
    const val TIMER_NOTIFICATION_ID = 3001

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null

    // State flows
    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds = _remainingSeconds.asStateFlow()

    private val _totalSeconds = MutableStateFlow(0)
    val totalSeconds = _totalSeconds.asStateFlow()

    private fun getDefaultLabel(): String {
        return if (LanguagePreferences.currentLanguage.value == AppLanguage.ARABIC) "مؤقت اللعبة" else "Game Timer"
    }

    private val _timerLabel = MutableStateFlow(getDefaultLabel())
    val timerLabel = _timerLabel.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val isAr = LanguagePreferences.currentLanguage.value == AppLanguage.ARABIC
            val chName = if (isAr) "مؤقتات المهام العائمة" else "Floating Game Timers"
            val chDesc = if (isAr) "تنبيهات انتهاء وقت المهام والزعماء في الألعاب" else "Alerts for finished game and boss timers"

            val channel = NotificationChannel(
                TIMER_CHANNEL_ID,
                chName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = chDesc
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, audioAttributes)
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    fun startTimer(context: Context, label: String, durationSeconds: Int) {
        if (durationSeconds <= 0) return
        createNotificationChannel(context)

        timerJob?.cancel()
        _timerLabel.value = label.ifBlank { getDefaultLabel() }
        _totalSeconds.value = durationSeconds
        _remainingSeconds.value = durationSeconds
        _isFinished.value = false
        _isRunning.value = true

        val appContext = context.applicationContext

        timerJob = scope.launch {
            while (isActive && _remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }

            if (_remainingSeconds.value <= 0) {
                _isRunning.value = false
                _isFinished.value = true
                sendTimerFinishedNotification(appContext, _timerLabel.value)
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }

    fun resumeTimer(context: Context) {
        if (_remainingSeconds.value > 0 && !_isRunning.value) {
            val appContext = context.applicationContext
            _isRunning.value = true
            timerJob?.cancel()
            timerJob = scope.launch {
                while (isActive && _remainingSeconds.value > 0) {
                    delay(1000)
                    _remainingSeconds.value -= 1
                }
                if (_remainingSeconds.value <= 0) {
                    _isRunning.value = false
                    _isFinished.value = true
                    sendTimerFinishedNotification(appContext, _timerLabel.value)
                }
            }
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _remainingSeconds.value = 0
        _totalSeconds.value = 0
        _isRunning.value = false
        _isFinished.value = false
    }

    fun addMinutes(minutes: Int) {
        _remainingSeconds.value += (minutes * 60)
        _totalSeconds.value += (minutes * 60)
        _isFinished.value = false
    }

    private fun sendTimerFinishedNotification(context: Context, label: String) {
        try {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                100,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val isAr = LanguagePreferences.currentLanguage.value == AppLanguage.ARABIC
            val notifTitle = if (isAr) "⏰ انتهى الوقت!" else "⏰ Time's up!"
            val notifContent = if (isAr) "اكتمل مؤقت: $label" else "Timer finished: $label"

            val notification = NotificationCompat.Builder(context, TIMER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_game_notes_logo)
                .setContentTitle(notifTitle)
                .setContentText(notifContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(soundUri)
                .setVibrate(longArrayOf(0, 500, 200, 500))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(TIMER_NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            android.util.Log.e("FloatingTimerManager", "Error sending notification", e)
        }
    }

    fun formatRemainingTime(): String {
        val s = _remainingSeconds.value
        val m = s / 60
        val remS = s % 60
        return String.format(java.util.Locale.US, "%02d:%02d", m, remS)
    }
}
