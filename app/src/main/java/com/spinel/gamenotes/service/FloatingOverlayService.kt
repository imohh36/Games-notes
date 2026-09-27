package com.spinel.gamenotes.service

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ViewSidebar
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.core.app.NotificationCompat
import androidx.activity.compose.BackHandler
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import coil.compose.AsyncImage
import com.spinel.gamenotes.GameNotesApp
import com.spinel.gamenotes.MainActivity
import com.spinel.gamenotes.R
import com.spinel.gamenotes.data.AppSettingsPreferences
import com.spinel.gamenotes.data.BlockType
import com.spinel.gamenotes.data.DocumentBlock
import com.spinel.gamenotes.data.GameNote
import com.spinel.gamenotes.data.GameTab
import com.spinel.gamenotes.data.NoteType
import com.spinel.gamenotes.data.TodoItem
import com.spinel.gamenotes.ui.components.ChecklistEditorScreen
import com.spinel.gamenotes.ui.components.FullNoteEditorScreen
import com.spinel.gamenotes.ui.components.GeminiChatSheet
import com.spinel.gamenotes.ui.components.NoteCard
import com.spinel.gamenotes.ui.theme.MyApplicationTheme
import com.spinel.gamenotes.util.AppLanguage
import com.spinel.gamenotes.util.LanguagePreferences
import com.spinel.gamenotes.util.ProvideAppLanguageAndDirection
import com.spinel.gamenotes.util.LocalAppStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.ViewCompositionStrategy
import kotlin.math.abs
import kotlin.math.hypot

enum class OverlayMode {
    BUBBLE,
    PANEL,
    MINI_TODO_WIDGET
}

enum class OverlaySubScreen {
    LIST,
    CREATE_NOTE,
    VIEW_NOTE
}

class FloatingOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private val bubbleLifecycleOwner = OverlayLifecycleOwner()
    private var panelLifecycleOwner: OverlayLifecycleOwner? = null
    private val miniWidgetLifecycleOwner = OverlayLifecycleOwner()
    private val dismissZoneLifecycleOwner = OverlayLifecycleOwner()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private fun bindLifecycleToView(view: ComposeView, owner: OverlayLifecycleOwner) {
        view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        view.setViewTreeLifecycleOwner(owner)
        view.setViewTreeSavedStateRegistryOwner(owner)
        view.setViewTreeViewModelStoreOwner(owner)
        view.setViewTreeOnBackPressedDispatcherOwner(owner)
    }

    private var bubbleView: ComposeView? = null
    private var panelView: ComposeView? = null
    private var miniWidgetView: ComposeView? = null
    private var dismissZoneView: ComposeView? = null

    private var bubbleParams: WindowManager.LayoutParams? = null
    private var panelParams: WindowManager.LayoutParams? = null
    private var miniWidgetParams: WindowManager.LayoutParams? = null
    private var dismissZoneParams: WindowManager.LayoutParams? = null

    private var currentMode = OverlayMode.BUBBLE
    private var activeTodoNoteId by mutableStateOf<Long?>(null)
    private var targetPanelNoteId by mutableStateOf<Long?>(null)
    private var isDockedRight = true
    private var snapAnimator: ValueAnimator? = null
    private var miniWidgetWidthPx: Int = 0
    private var miniWidgetHeightPx: Int = 0

    // State for drag-to-dismiss visual indicator
    private val isDismissTargetHighlighted = MutableStateFlow(false)
    private var panelFocusManager: FocusManager? = null
    private val overlayScreenState = MutableStateFlow(OverlaySubScreen.LIST)
    private val activeViewingNote = MutableStateFlow<GameNote?>(null)
    private val activeCreatingNoteType = MutableStateFlow(NoteType.REGULAR)
    private var savedModeBeforeImagePicker: OverlayMode? = null
    private var isMinimizedForPicker by mutableStateOf(false)

    private fun resetOverlayNavigation() {
        panelFocusManager?.clearFocus(force = true)
        overlayScreenState.value = OverlaySubScreen.LIST
        activeViewingNote.value = null
        targetPanelNoteId = null
    }

    companion object {
        const val ACTION_STOP_SERVICE = "com.spinel.gamenotes.ACTION_STOP_SERVICE"
        const val ACTION_PIN_TODO = "com.spinel.gamenotes.ACTION_PIN_TODO"
        const val EXTRA_NOTE_ID = "extra_note_id"
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "gamenotes_overlay_channel"

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning = _isServiceRunning.asStateFlow()

        fun start(context: Context) {
            try {
                val intent = Intent(context, FloatingOverlayService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e("FloatingOverlayService", "Failed to start service", e)
            }
        }

        fun pinTodoWidget(context: Context, noteId: Long) {
            try {
                val intent = Intent(context, FloatingOverlayService::class.java).apply {
                    action = ACTION_PIN_TODO
                    putExtra(EXTRA_NOTE_ID, noteId)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e("FloatingOverlayService", "Failed to pin todo widget", e)
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, FloatingOverlayService::class.java).apply {
                    action = ACTION_STOP_SERVICE
                }
                context.startService(intent)
            } catch (e: Exception) {
                Log.e("FloatingOverlayService", "Failed to stop service", e)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            dismissZoneLifecycleOwner.onCreate()
            bubbleLifecycleOwner.onCreate()
            miniWidgetLifecycleOwner.onCreate()

            createNotificationChannel()
            startAsForeground()

            setupDismissZoneView()
            setupBubbleView()
            setupMiniWidgetView()

            serviceScope.launch {
                val pinnedId = AppSettingsPreferences.pinnedFloatingNoteId.first()
                if (pinnedId > 0L) {
                    targetPanelNoteId = pinnedId
                }
            }

            serviceScope.launch {
                AppSettingsPreferences.isMiniTaskListEnabled.collect { isMiniEnabled ->
                    if (!isMiniEnabled && currentMode == OverlayMode.MINI_TODO_WIDGET) {
                        activeTodoNoteId?.let { noteId ->
                            targetPanelNoteId = noteId
                            switchToPanel()
                        }
                    }
                }
            }

            serviceScope.launch {
                OverlayImagePickerBridge.isPickerActive.collect { isActive ->
                    if (isActive) {
                        // Force-minimize overlay to small floating pill during image selection without destroying state
                        minimizeOverlayForPicker()
                    } else {
                        // Restore previous overlay mode and full size upon completion or cancellation
                        restoreOverlayFromPicker()
                    }
                }
            }

            _isServiceRunning.value = true
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "Error during onCreate", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (intent?.action == ACTION_STOP_SERVICE) {
                stopSelf()
                return START_NOT_STICKY
            }
            val pinnedId = AppSettingsPreferences.pinnedFloatingNoteId.value
            if (pinnedId > 0L && targetPanelNoteId == null) {
                targetPanelNoteId = pinnedId
            }
            if (intent?.action == ACTION_PIN_TODO) {
                val noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
                if (noteId != -1L) {
                    if (AppSettingsPreferences.isMiniTaskListEnabled(applicationContext)) {
                        switchToMiniWidget(noteId)
                    } else {
                        targetPanelNoteId = noteId
                        switchToPanel()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "Error in onStartCommand", e)
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val strings = LanguagePreferences.getCurrentStrings()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                strings.overlayChannelName,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = strings.overlayNotificationChannelDesc
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun startAsForeground() {
        try {
            val openAppIntent = Intent(this, MainActivity::class.java)
            val pendingOpenApp = PendingIntent.getActivity(
                this, 0, openAppIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val stopIntent = Intent(this, FloatingOverlayService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            val pendingStop = PendingIntent.getService(
                this, 1, stopIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val strings = LanguagePreferences.getCurrentStrings()
            val notifTitle = strings.overlayNotificationTitle
            val notifContent = strings.overlayNotificationText
            val notifStopAction = strings.overlayNotificationStopAction

            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(notifTitle)
                .setContentText(notifContent)
                .setSmallIcon(R.drawable.ic_game_notes_logo)
                .setContentIntent(pendingOpenApp)
                .addAction(R.drawable.ic_game_notes_logo, notifStopAction, pendingStop)
                .setOngoing(true)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "Error in startAsForeground", e)
        }
    }

    // WindowManager Safe Helpers
    private fun safeAddView(view: View?, params: WindowManager.LayoutParams?, owner: OverlayLifecycleOwner? = null) {
        if (view == null || params == null) return
        try {
            owner?.onResume()
            if (view.parent == null && !view.isAttachedToWindow) {
                windowManager.addView(view, params)
            } else {
                windowManager.updateViewLayout(view, params)
            }
        } catch (e: Exception) {
            Log.w("FloatingOverlayService", "safeAddView failed: ${e.message}")
        }
    }

    private fun safeRemoveView(view: View?, owner: OverlayLifecycleOwner? = null) {
        if (view == null) return
        try {
            owner?.onStop()
            if (view.parent != null || view.isAttachedToWindow) {
                windowManager.removeView(view)
            }
        } catch (e: Exception) {
            try {
                windowManager.removeViewImmediate(view)
            } catch (_: Exception) {}
        }
    }

    private fun safeUpdateView(view: View?, params: WindowManager.LayoutParams?) {
        if (view == null || params == null) return
        try {
            if (view.parent != null || view.isAttachedToWindow) {
                windowManager.updateViewLayout(view, params)
            }
        } catch (e: Exception) {
            Log.w("FloatingOverlayService", "safeUpdateView failed: ${e.message}")
        }
    }

    private fun hideKeyboard(view: View?) {
        if (view == null) return
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? android.view.inputmethod.InputMethodManager
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.windowToken, 0)
                imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
            }
        } catch (e: Exception) {
            Log.w("FloatingOverlayService", "Failed to hide keyboard: ${e.message}")
        }
    }

    // 0. Drag to Dismiss Zone Setup (Visible during dragging, hidden by default)
    private fun setupDismissZoneView() {
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        dismissZoneParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = (48 * density).toInt()
        }

        dismissZoneView = ComposeView(this).apply {
            bindLifecycleToView(this, dismissZoneLifecycleOwner)
            visibility = View.GONE

            setContent {
                ProvideAppLanguageAndDirection {
                    val strings = LocalAppStrings.current
                    val isHighlighted by isDismissTargetHighlighted.collectAsStateWithLifecycle()
                    val scale by animateFloatAsState(
                        targetValue = if (isHighlighted) 1.25f else 1.0f,
                        label = "dismiss_scale"
                    )

                    Box(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .scale(scale),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(28.dp),
                            color = if (isHighlighted) Color(0xFFDC2626) else Color(0xDD1E293B),
                            border = androidx.compose.foundation.BorderStroke(
                                2.dp,
                                if (isHighlighted) Color.White else Color(0xFFEF4444)
                            ),
                            shadowElevation = 10.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = strings.cancel,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHighlighted) strings.releaseToDismiss else strings.cancel,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        safeAddView(dismissZoneView, dismissZoneParams, dismissZoneLifecycleOwner)
    }

    // 1. Standard Floating Bubble with Unified App Icon
    private fun setupBubbleView() {
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val sizePx = (62 * density).toInt()

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val hideOffset = sizePx / 2
        bubbleParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (displayMetrics.widthPixels - sizePx + hideOffset)
            y = (displayMetrics.heightPixels * 0.35f).toInt()
        }

        bubbleView = ComposeView(this).apply {
            bindLifecycleToView(this, bubbleLifecycleOwner)
            alpha = 0.45f

            setContent {
                ProvideAppLanguageAndDirection {
                    MyApplicationTheme {
                        FloatingBubbleContent(
                            hasActiveTodo = activeTodoNoteId != null
                        )
                    }
                }
            }

            setOnTouchListener(createBubbleTouchListener())
        }

        safeAddView(bubbleView, bubbleParams, bubbleLifecycleOwner)
        currentMode = OverlayMode.BUBBLE
    }

    // Smooth free dragging with Smart Snap to Edge, Partial Hide, and Dynamic Alpha
    private fun createBubbleTouchListener(): View.OnTouchListener {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var touchDownTime = 0L
        var isMoving = false

        return View.OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    snapAnimator?.cancel()
                    bubbleView?.alpha = 1.0f
                    initialX = bubbleParams?.x ?: 0
                    initialY = bubbleParams?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    touchDownTime = System.currentTimeMillis()
                    isMoving = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.rawX - initialTouchX
                    val dy = event.rawY - initialTouchY
                    val distance = hypot(dx.toDouble(), dy.toDouble())

                    // Differentiate between tap and drag
                    if (!isMoving && distance > 10) {
                        isMoving = true
                        bubbleView?.alpha = 1.0f
                        dismissZoneView?.visibility = View.VISIBLE
                    }

                    if (isMoving) {
                        val displayMetrics = resources.displayMetrics
                        val density = displayMetrics.density
                        val screenWidth = displayMetrics.widthPixels
                        val screenHeight = displayMetrics.heightPixels

                        bubbleParams?.let { params ->
                            val bubbleW = bubbleView?.width?.takeIf { it > 0 } ?: (62 * density).toInt()
                            val bubbleH = bubbleView?.height?.takeIf { it > 0 } ?: (62 * density).toInt()

                            params.x = (initialX + dx).toInt().coerceIn(- (bubbleW / 2), screenWidth - bubbleW + (bubbleW / 2))
                            params.y = (initialY + dy).toInt().coerceIn(0, screenHeight - bubbleH)
                            safeUpdateView(bubbleView, params)
                        }

                        // Calculate distance / intersection between bubble and dismiss zone (bottom-center)
                        val dismissCenterX = screenWidth / 2f
                        val dismissCenterY = screenHeight - (75 * density)
                        val distToDismiss = hypot((event.rawX - dismissCenterX).toDouble(), (event.rawY - dismissCenterY).toDouble())

                        val isOverDismiss = distToDismiss < (110 * density) ||
                                (event.rawY > (screenHeight - (160 * density)) && abs(event.rawX - dismissCenterX) < (100 * density))

                        isDismissTargetHighlighted.value = isOverDismiss
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    dismissZoneView?.visibility = View.GONE
                    val wasOverDismiss = isDismissTargetHighlighted.value
                    isDismissTargetHighlighted.value = false

                    if (isMoving) {
                        if (wasOverDismiss) {
                            // Dropped over Dismiss area: remove all views and stop service!
                            Toast.makeText(applicationContext, LanguagePreferences.getCurrentStrings().overlayClosedToast, Toast.LENGTH_SHORT).show()
                            stopSelf()
                            return@OnTouchListener true
                        }

                        // Smart Snap to Edge with Partial Hide & Dynamic Alpha
                        val displayMetrics = resources.displayMetrics
                        val density = displayMetrics.density
                        val screenWidth = displayMetrics.widthPixels
                        val bubbleW = bubbleView?.width?.takeIf { it > 0 } ?: (62 * density).toInt()

                        val currentX = bubbleParams?.x ?: 0
                        val bubbleCenterX = currentX + bubbleW / 2
                        val screenMidX = screenWidth / 2

                        // Push bubble outside screen by exactly half its width (bubbleW / 2)
                        val hideOffset = bubbleW / 2
                        val targetX = if (bubbleCenterX < screenMidX) {
                            -hideOffset
                        } else {
                            screenWidth - bubbleW + hideOffset
                        }

                        snapAnimator?.cancel()
                        snapAnimator = ValueAnimator.ofInt(currentX, targetX).apply {
                            duration = 260
                            interpolator = DecelerateInterpolator()
                            addUpdateListener { animator ->
                                bubbleParams?.let { params ->
                                    params.x = animator.animatedValue as Int
                                    safeUpdateView(bubbleView, params)
                                }
                            }
                            addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    // Dynamic Alpha: 0.45f when settled on edge
                                    bubbleView?.animate()?.alpha(0.45f)?.setDuration(200)?.start()
                                }
                            })
                            start()
                        }
                    } else {
                        // Regular tap
                        val clickDuration = System.currentTimeMillis() - touchDownTime
                        if (clickDuration < 400) {
                            bubbleView?.alpha = 1.0f
                            val pinnedId = AppSettingsPreferences.pinnedFloatingNoteId.value
                            if (activeTodoNoteId != null && AppSettingsPreferences.isMiniTaskListEnabled(applicationContext)) {
                                switchToMiniWidget(activeTodoNoteId!!)
                            } else {
                                if (pinnedId > 0L) {
                                    targetPanelNoteId = pinnedId
                                } else if (activeTodoNoteId != null) {
                                    targetPanelNoteId = activeTodoNoteId
                                }
                                switchToPanel()
                            }
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    // 2. Half-Screen Panel Removal & Dynamic Recreation (Prevents Compose Freezes)
    private fun removePanelView() {
        panelView?.let { view ->
            try {
                if (view.parent != null || view.isAttachedToWindow) {
                    windowManager.removeView(view)
                }
            } catch (e: Exception) {
                try {
                    windowManager.removeViewImmediate(view)
                } catch (_: Exception) {}
            }
        }
        panelView = null
        panelParams = null
        panelLifecycleOwner?.onDestroy()
        panelLifecycleOwner = null
    }

    private fun createPanelLayoutParams(): WindowManager.LayoutParams {
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val halfScreenWidth = if (isLandscape) {
            (displayMetrics.widthPixels * 0.45f).toInt().coerceIn((300 * density).toInt(), (450 * density).toInt())
        } else {
            (displayMetrics.widthPixels * 0.58f).toInt().coerceIn((320 * density).toInt(), (480 * density).toInt())
        }

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        return WindowManager.LayoutParams(
            halfScreenWidth,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or (if (isDockedRight) Gravity.END else Gravity.START)
            x = 0
            y = 0
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        }
    }

    private fun createPanelView(): ComposeView {
        val owner = OverlayLifecycleOwner()
        owner.onCreate()
        owner.onResume()
        panelLifecycleOwner = owner

        return ComposeView(this).apply {
            bindLifecycleToView(this, owner)

            isFocusableInTouchMode = true
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    if (owner.onBackPressedDispatcher.hasEnabledCallbacks()) {
                        owner.onBackPressedDispatcher.onBackPressed()
                    } else {
                        switchToBubble()
                    }
                    true
                } else {
                    false
                }
            }

            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    switchToBubble()
                    true
                } else {
                    false
                }
            }

            setContent {
                ProvideAppLanguageAndDirection {
                    val strings = LocalAppStrings.current
                    val currentSubScreen by overlayScreenState.collectAsStateWithLifecycle()
                    val viewingNote by activeViewingNote.collectAsStateWithLifecycle()
                    val creatingNoteType by activeCreatingNoteType.collectAsStateWithLifecycle()
                    MyApplicationTheme {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Preserved Full Overlay Content: Remains in composition so all input state is never lost
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(if (isMinimizedForPicker) 0f else 1f)
                            ) {
                                FloatingOverlayContent(
                                    isDockedRight = isDockedRight,
                                    currentSubScreen = currentSubScreen,
                                    currentViewingNote = viewingNote,
                                    creatingNoteType = creatingNoteType,
                                    onRegisterFocusManager = { fm -> panelFocusManager = fm },
                                    onNavigateToList = {
                                        panelFocusManager?.clearFocus(force = true)
                                        activeViewingNote.value = null
                                        overlayScreenState.value = OverlaySubScreen.LIST
                                    },
                                    onNavigateToCreateNote = { type ->
                                        activeCreatingNoteType.value = type
                                        overlayScreenState.value = OverlaySubScreen.CREATE_NOTE
                                    },
                                    onNavigateToViewNote = { note ->
                                        activeViewingNote.value = note
                                        overlayScreenState.value = OverlaySubScreen.VIEW_NOTE
                                    },
                                    onToggleDockSide = {
                                        isDockedRight = !isDockedRight
                                        panelParams?.let { params ->
                                            params.gravity = Gravity.TOP or (if (isDockedRight) Gravity.END else Gravity.START)
                                            safeUpdateView(panelView, params)
                                        }
                                    },
                                    onCollapse = { switchToBubble() },
                                    onSelectTodoList = { todoNoteId ->
                                        if (AppSettingsPreferences.isMiniTaskListEnabled(this@FloatingOverlayService)) {
                                            switchToMiniWidget(todoNoteId)
                                        } else {
                                            serviceScope.launch {
                                                val n = GameNotesApp.instance.repository.getNoteByIdDirect(todoNoteId)
                                                if (n != null) {
                                                    activeViewingNote.value = n
                                                    overlayScreenState.value = OverlaySubScreen.VIEW_NOTE
                                                }
                                            }
                                        }
                                    },
                                    onSetFocusable = { _ -> },
                                    onOpenInFullApp = { noteId ->
                                        try {
                                            val intent = Intent(this@FloatingOverlayService, MainActivity::class.java).apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                putExtra("target_note_id", noteId)
                                            }
                                            startActivity(intent)
                                            switchToBubble()
                                        } catch (e: Exception) {
                                            Log.e("FloatingOverlayService", "Failed to open full app", e)
                                        }
                                    }
                                )
                            }

                            // Compact Force-Minimized Floating Pill Indicator during image gallery picking
                            if (isMinimizedForPicker) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { restoreOverlayFromPicker() },
                                    shape = RoundedCornerShape(26.dp),
                                    color = Color(0xEE0F172A),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF10B981)),
                                    shadowElevation = 10.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddPhotoAlternate,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = strings.pickingImageInProgress,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setOverlayFocusable(focusable: Boolean) {
        // Dynamic recreate-on-open avoids focus manipulation freezes
    }

    private fun setMiniWidgetFocusable(focusable: Boolean) {
        miniWidgetParams?.let { params ->
            val hasNotFocusable = (params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE) != 0
            val shouldHaveNotFocusable = !focusable
            if (hasNotFocusable == shouldHaveNotFocusable) {
                return
            }
            if (focusable) {
                params.flags = params.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
                params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            } else {
                params.flags = params.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                hideKeyboard(miniWidgetView)
                miniWidgetView?.clearFocus()
            }
            safeUpdateView(miniWidgetView, params)
        }
    }

    // 3. Mini Task-List Widget (Fully draggable anywhere on screen, transparent HUD)
    private fun setupMiniWidgetView() {
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        miniWidgetWidthPx = (280 * density).toInt()
        miniWidgetHeightPx = (320 * density).toInt()

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        miniWidgetParams = WindowManager.LayoutParams(
            miniWidgetWidthPx,
            miniWidgetHeightPx,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = (displayMetrics.widthPixels - miniWidgetWidthPx - (14 * density)).toInt().coerceAtLeast(0)
            y = (displayMetrics.heightPixels * 0.18f).toInt()
        }

        miniWidgetView = ComposeView(this).apply {
            bindLifecycleToView(this, miniWidgetLifecycleOwner)
            isFocusableInTouchMode = true
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                    setMiniWidgetFocusable(false)
                    true
                } else {
                    false
                }
            }
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    setMiniWidgetFocusable(false)
                    false
                } else {
                    false
                }
            }

            setContent {
                ProvideAppLanguageAndDirection {
                    MyApplicationTheme {
                        val currentId = activeTodoNoteId
                        if (currentId != null && currentId > 0L) {
                            FloatingMiniTodoWidgetContent(
                                noteId = currentId,
                                onDragDelta = { dx, dy -> handleMiniWidgetDrag(dx, dy) },
                                onDragStart = { handleMiniWidgetDragStart() },
                                onDragEnd = { handleMiniWidgetDragEnd() },
                                onResizeCorner = { isLeft, dx, dy -> handleMiniWidgetResize(isLeft, dx, dy) },
                                onResizeHeight = { dy -> handleMiniWidgetResizeHeight(dy) },
                                onSetFocusable = { focusable -> setMiniWidgetFocusable(focusable) },
                                onMinimizeToBubble = { switchToBubble() },
                                onExpandToPanel = {
                                    targetPanelNoteId = currentId
                                    switchToPanel()
                                },
                                onCloseWidget = {
                                    activeTodoNoteId = null
                                    switchToBubble()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Smooth dragging for Mini Task-List Widget anywhere across the screen
    private fun handleMiniWidgetDrag(dx: Float, dy: Float) {
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        miniWidgetParams?.let { params ->
            val widgetWidth = params.width.takeIf { it > 0 } ?: miniWidgetWidthPx
            val widgetHeight = params.height.takeIf { it > 0 } ?: miniWidgetHeightPx
            params.x = (params.x + dx).toInt().coerceIn(0, (screenWidth - widgetWidth).coerceAtLeast(0))
            params.y = (params.y + dy).toInt().coerceIn(0, (screenHeight - widgetHeight).coerceAtLeast(0))
            safeUpdateView(miniWidgetView, params)

            val dismissCenterX = screenWidth / 2f
            val dismissCenterY = screenHeight - (75 * density)
            val currentCenterX = params.x + widgetWidth / 2f
            val currentCenterY = params.y + widgetHeight / 2f
            val distToDismiss = hypot((currentCenterX - dismissCenterX).toDouble(), (currentCenterY - dismissCenterY).toDouble())
            val isOverDismiss = distToDismiss < (110 * density) ||
                    (params.y > (screenHeight - 160 * density) && abs(currentCenterX - dismissCenterX) < (100 * density))
            isDismissTargetHighlighted.value = isOverDismiss
        }
    }

    // Dynamic corner and edge resizing for Mini Task-List Widget (Expand on pull, shrink on push)
    private fun handleMiniWidgetResize(isLeft: Boolean, dx: Float, dy: Float) {
        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val minWidth = (200 * density).toInt()
        val maxWidth = (screenWidth - (16 * density)).toInt().coerceAtLeast(minWidth)
        val minHeight = (180 * density).toInt()
        val maxHeight = (screenHeight * 0.85f).toInt().coerceAtLeast(minHeight)

        miniWidgetParams?.let { params ->
            var currentW = params.width.takeIf { it > 0 } ?: miniWidgetWidthPx
            var currentH = params.height.takeIf { it > 0 } ?: miniWidgetHeightPx
            var currentX = params.x
            var currentY = params.y

            if (!isLeft) {
                // Resizing from the Right corner (drag right to expand, push left to shrink)
                val maxAllowedW = (screenWidth - currentX).coerceAtLeast(minWidth)
                val newW = (currentW + dx).toInt().coerceIn(minWidth, minOf(maxWidth, maxAllowedW))
                currentW = newW
            } else {
                // Resizing from the Left corner (pull left to expand, push right to shrink)
                val maxLeftExtent = currentX + currentW
                val targetW = (currentW - dx).toInt().coerceIn(minWidth, maxLeftExtent.coerceAtLeast(minWidth))
                val actualDx = currentW - targetW
                currentX = (currentX + actualDx).coerceIn(0, (screenWidth - minWidth).coerceAtLeast(0))
                currentW = targetW
            }

            // Height resizing (drag down to expand, push up to shrink)
            val maxAllowedH = (screenHeight - currentY).coerceAtLeast(minHeight)
            val newH = (currentH + dy).toInt().coerceIn(minHeight, minOf(maxHeight, maxAllowedH))
            currentH = newH

            params.x = currentX
            params.y = currentY
            params.width = currentW
            params.height = currentH

            miniWidgetWidthPx = currentW
            miniWidgetHeightPx = currentH

            safeUpdateView(miniWidgetView, params)
        }
    }

    private fun handleMiniWidgetResizeHeight(dy: Float) {
        handleMiniWidgetResize(isLeft = false, dx = 0f, dy = dy)
    }

    private fun handleMiniWidgetDragStart() {
        setMiniWidgetFocusable(false)
        dismissZoneView?.visibility = View.VISIBLE
    }

    private fun handleMiniWidgetDragEnd() {
        dismissZoneView?.visibility = View.GONE
        val wasOverDismiss = isDismissTargetHighlighted.value
        isDismissTargetHighlighted.value = false
        if (wasOverDismiss) {
            Toast.makeText(applicationContext, LanguagePreferences.getCurrentStrings().miniWidgetClosedToast, Toast.LENGTH_SHORT).show()
            switchToBubble()
        }
    }

    // Safe State Transitions
    private fun switchToPanel() {
        try {
            setMiniWidgetFocusable(false)
            safeRemoveView(bubbleView, bubbleLifecycleOwner)
            safeRemoveView(miniWidgetView, miniWidgetLifecycleOwner)

            // 1. Remove existing panel view if any to prevent stale ComposeView state
            removePanelView()

            // 2. Always ensure navigation opens on the main notes list unless a target note was explicitly set
            val targetId = targetPanelNoteId
            if (targetId != null && targetId > 0L) {
                serviceScope.launch {
                    val note = GameNotesApp.instance.repository.getNoteByIdDirect(targetId)
                    if (note != null) {
                        activeViewingNote.value = note
                        overlayScreenState.value = OverlaySubScreen.VIEW_NOTE
                    } else {
                        overlayScreenState.value = OverlaySubScreen.LIST
                        activeViewingNote.value = null
                    }
                    targetPanelNoteId = null
                }
            } else {
                overlayScreenState.value = OverlaySubScreen.LIST
                activeViewingNote.value = null
            }

            // 3. Fresh LayoutParams and fresh ComposeView (Remove and Re-add prevents freezing)
            val params = createPanelLayoutParams()
            panelParams = params
            val view = createPanelView()
            panelView = view

            windowManager.addView(view, params)
            currentMode = OverlayMode.PANEL
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "switchToPanel failed", e)
        }
    }

    private fun switchToBubble() {
        try {
            // 1. Reset navigation BEFORE removing view
            panelFocusManager?.clearFocus(force = true)
            hideKeyboard(panelView)
            hideKeyboard(miniWidgetView)
            overlayScreenState.value = OverlaySubScreen.LIST
            activeViewingNote.value = null
            targetPanelNoteId = null

            // 2. Completely remove panelView from WindowManager
            removePanelView()
            safeRemoveView(miniWidgetView, miniWidgetLifecycleOwner)

            // 3. Add bubbleView
            bubbleView?.alpha = 0.45f
            safeAddView(bubbleView, bubbleParams, bubbleLifecycleOwner)
            currentMode = OverlayMode.BUBBLE
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "switchToBubble failed", e)
        }
    }

    private fun switchToMiniWidget(noteId: Long) {
        if (!AppSettingsPreferences.isMiniTaskListEnabled(applicationContext)) {
            activeTodoNoteId = noteId
            targetPanelNoteId = noteId
            switchToPanel()
            return
        }
        activeTodoNoteId = noteId
        try {
            panelFocusManager?.clearFocus(force = true)
            hideKeyboard(panelView)
            overlayScreenState.value = OverlaySubScreen.LIST
            activeViewingNote.value = null
            targetPanelNoteId = null

            removePanelView()
            safeRemoveView(bubbleView, bubbleLifecycleOwner)
            safeAddView(miniWidgetView, miniWidgetParams, miniWidgetLifecycleOwner)
            currentMode = OverlayMode.MINI_TODO_WIDGET
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "switchToMiniWidget failed", e)
        }
    }

    // State Preservation during Image Picking: Force-minimize to small pill instead of destroying/hiding
    private fun minimizeOverlayForPicker() {
        try {
            if (isMinimizedForPicker) return
            isMinimizedForPicker = true
            savedModeBeforeImagePicker = currentMode
            dismissZoneView?.visibility = View.GONE

            panelFocusManager?.clearFocus(force = true)
            hideKeyboard(panelView)
            hideKeyboard(miniWidgetView)
            panelView?.clearFocus()
            miniWidgetView?.clearFocus()

            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density

            if (currentMode == OverlayMode.PANEL) {
                // Force-minimize panel to a small floating badge at the top
                panelParams?.let { params ->
                    params.width = (190 * density).toInt()
                    params.height = (52 * density).toInt()
                    params.gravity = Gravity.TOP or (if (isDockedRight) Gravity.END else Gravity.START)
                    params.x = (16 * density).toInt()
                    params.y = (48 * density).toInt()
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    safeUpdateView(panelView, params)
                }
            } else if (currentMode == OverlayMode.MINI_TODO_WIDGET) {
                miniWidgetParams?.let { params ->
                    params.width = (64 * density).toInt()
                    params.height = (64 * density).toInt()
                    safeUpdateView(miniWidgetView, params)
                }
            } else if (currentMode == OverlayMode.BUBBLE) {
                bubbleView?.alpha = 0.9f
            }
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "minimizeOverlayForPicker failed", e)
        }
    }

    private fun restoreOverlayFromPicker() {
        try {
            if (!isMinimizedForPicker) return
            isMinimizedForPicker = false

            val displayMetrics = resources.displayMetrics
            val density = displayMetrics.density
            val halfScreenWidth = (displayMetrics.widthPixels * 0.58f).toInt()
                .coerceIn((320 * density).toInt(), (480 * density).toInt())

            if (savedModeBeforeImagePicker == OverlayMode.PANEL || currentMode == OverlayMode.PANEL) {
                panelParams?.let { params ->
                    params.width = halfScreenWidth
                    params.height = WindowManager.LayoutParams.MATCH_PARENT
                    params.gravity = Gravity.TOP or (if (isDockedRight) Gravity.END else Gravity.START)
                    params.x = 0
                    params.y = 0
                    params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    safeUpdateView(panelView, params)
                }
                currentMode = OverlayMode.PANEL
            } else if (savedModeBeforeImagePicker == OverlayMode.MINI_TODO_WIDGET || currentMode == OverlayMode.MINI_TODO_WIDGET) {
                miniWidgetParams?.let { params ->
                    params.width = miniWidgetWidthPx
                    params.height = miniWidgetHeightPx
                    safeUpdateView(miniWidgetView, params)
                }
                currentMode = OverlayMode.MINI_TODO_WIDGET
            } else if (savedModeBeforeImagePicker == OverlayMode.BUBBLE) {
                bubbleView?.alpha = 0.45f
                currentMode = OverlayMode.BUBBLE
            }
            savedModeBeforeImagePicker = null
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "restoreOverlayFromPicker failed", e)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            val displayMetrics = resources.displayMetrics
            val screenW = displayMetrics.widthPixels
            val screenH = displayMetrics.heightPixels
            val density = displayMetrics.density

            // 1. Adjust Bubble position so it never gets stuck or lost outside screen during rotation
            bubbleParams?.let { params ->
                val bubbleSize = (62 * density).toInt()
                val hideOffset = bubbleSize / 2
                val isNearRight = params.x > (screenW / 3)
                params.x = if (isNearRight) (screenW - bubbleSize + hideOffset) else -hideOffset
                params.y = params.y.coerceIn(0, (screenH - bubbleSize).coerceAtLeast(0))
                safeUpdateView(bubbleView, params)
            }

            // 2. Adjust Panel width for landscape / portrait without clipping or blackouts
            panelParams?.let { params ->
                val isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
                val halfScreenWidth = if (isLandscape) {
                    (screenW * 0.45f).toInt().coerceIn((300 * density).toInt(), (450 * density).toInt())
                } else {
                    (screenW * 0.58f).toInt().coerceIn((320 * density).toInt(), (480 * density).toInt())
                }
                params.width = halfScreenWidth
                params.height = WindowManager.LayoutParams.MATCH_PARENT
                params.gravity = Gravity.TOP or (if (isDockedRight) Gravity.END else Gravity.START)
                safeUpdateView(panelView, params)
            }

            // 3. Adjust Mini Todo Widget bounds so it stays within screen
            miniWidgetParams?.let { params ->
                params.x = params.x.coerceIn(0, (screenW - miniWidgetWidthPx).coerceAtLeast(0))
                params.y = params.y.coerceIn(0, (screenH - miniWidgetHeightPx).coerceAtLeast(0))
                safeUpdateView(miniWidgetView, params)
            }
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "Error during onConfigurationChanged", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        _isServiceRunning.value = false
        snapAnimator?.cancel()
        snapAnimator = null
        try {
            panelFocusManager?.clearFocus(force = true)
            setMiniWidgetFocusable(false)
            hideKeyboard(panelView)
            hideKeyboard(miniWidgetView)

            bubbleView?.setOnTouchListener(null)
            miniWidgetView?.setOnTouchListener(null)
            miniWidgetView?.setOnKeyListener(null)
            dismissZoneView?.setOnTouchListener(null)

            safeRemoveView(bubbleView, bubbleLifecycleOwner)
            removePanelView()
            safeRemoveView(miniWidgetView, miniWidgetLifecycleOwner)
            safeRemoveView(dismissZoneView, dismissZoneLifecycleOwner)

            bubbleLifecycleOwner.onDestroy()
            panelLifecycleOwner?.onDestroy()
            miniWidgetLifecycleOwner.onDestroy()
            dismissZoneLifecycleOwner.onDestroy()

            bubbleView = null
            miniWidgetView = null
            dismissZoneView = null
        } catch (e: Exception) {
            Log.e("FloatingOverlayService", "Error during onDestroy views cleanup", e)
        }
    }
}

// =========================================================================
// COMPOSABLES FOR OVERLAY
// =========================================================================

// 1. Unified Floating Bubble with Official App Vector Icon
@Composable
fun FloatingBubbleContent(
    hasActiveTodo: Boolean = false
) {
    val strings = LocalAppStrings.current
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .size(62.dp)
            .padding(3.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF0369A1),
                        primaryColor
                    )
                )
            )
            .border(2.dp, Color(0xFF38BDF8), CircleShape)
            .testTag("floating_bubble_icon"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_game_notes_logo),
                contentDescription = "GameNotes Overlay",
                modifier = Modifier.size(34.dp)
            )
            if (hasActiveTodo) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.padding(top = 1.dp)
                ) {
                    Text(
                        text = strings.todoTasks,
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

// 2. Mini Task-List Widget (HUD style, transparent glass on game)
@Composable
fun FloatingMiniTodoWidgetContent(
    noteId: Long,
    onDragDelta: (Float, Float) -> Unit = { _, _ -> },
    onDragStart: () -> Unit = {},
    onDragEnd: () -> Unit = {},
    onResizeCorner: (Boolean, Float, Float) -> Unit = { _, _, _ -> },
    onResizeHeight: (Float) -> Unit = {},
    onSetFocusable: (Boolean) -> Unit = {},
    onMinimizeToBubble: () -> Unit,
    onExpandToPanel: () -> Unit,
    onCloseWidget: () -> Unit
) {
    val repository = remember { GameNotesApp.instance.repository }
    val coroutineScope = rememberCoroutineScope()
    val noteFlow = remember(repository, noteId) { repository.getNoteById(noteId) }
    val note by noteFlow.collectAsStateWithLifecycle(initialValue = null)

    val strings = LocalAppStrings.current
    var newQuickTaskText by remember { mutableStateOf("") }
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mini_todo_widget")
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .shadow(14.dp, RoundedCornerShape(16.dp))
                .border(1.5.dp, primaryColor.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xF20B0F17),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                // Drag Handle & Header - Supports Free Dragging Anywhere On Screen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { onDragStart() },
                                onDragEnd = { onDragEnd() },
                                onDragCancel = { onDragEnd() },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    onDragDelta(dragAmount.x, dragAmount.y)
                                }
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Far Opposite Side: Expand to Full Overlay Panel
                    IconButton(
                        onClick = onExpandToPanel,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = strings.overlayExpandToPanel,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = note?.title ?: strings.overlayTasksTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Far Other Side: Minimize, Close, and Drag Handle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Minimize to Bubble
                        IconButton(
                            onClick = onMinimizeToBubble,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = strings.overlayMinimize,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Close Widget (Returns to bubble mode)
                        IconButton(
                            onClick = onCloseWidget,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = strings.overlayCloseMiniWidget,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(2.dp))

                        // Drag Handle
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = strings.overlayDragToMove,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Progress bar
                val totalTodos = note?.totalTodosCount ?: 0
                val completedTodos = note?.completedTodosCount ?: 0
                val progress = if (totalTodos > 0) completedTodos.toFloat() / totalTodos.toFloat() else 0f

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = primaryColor,
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$completedTodos/$totalTodos",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Checklist Items - Unified with in-app task design and wrap content to prevent word clipping
                val currentTodos = remember(note) {
                    if (!note?.todoItems.isNullOrEmpty()) {
                        note!!.todoItems
                    } else if (!note?.blocksJson.isNullOrBlank() && note?.blocksJson != "[]") {
                        try {
                            DocumentBlock.jsonToList(note!!.blocksJson)
                                .filter { it.type == BlockType.CHECKLIST && it.text.isNotBlank() }
                                .map { TodoItem(id = it.id, text = it.text, isDone = it.isChecked) }
                        } catch (e: Exception) {
                            emptyList()
                        }
                    } else {
                        emptyList()
                    }
                }

                if (currentTodos.isEmpty()) {
                    Text(
                        text = strings.overlayNoTasksCurrently,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemsIndexed(currentTodos, key = { _, it -> it.id }) { index, item ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.05f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.12f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        note?.let { n ->
                                            coroutineScope.launch {
                                                try {
                                                    repository.toggleTodoItem(n.id, item.id)
                                                } catch (e: Exception) {
                                                    Log.e("MiniTodoWidget", "Error toggling todo", e)
                                                }
                                            }
                                        }
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    // Number badge matching in-app style
                                    Surface(
                                        shape = CircleShape,
                                        color = if (item.isDone) primaryColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${index + 1}.",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.isDone) primaryColor else Color.White.copy(alpha = 0.8f),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Checkbox(
                                        checked = item.isDone,
                                        onCheckedChange = {
                                            note?.let { n ->
                                                coroutineScope.launch {
                                                    try {
                                                        repository.toggleTodoItem(n.id, item.id)
                                                    } catch (e: Exception) {
                                                        Log.e("MiniTodoWidget", "Error toggling todo", e)
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(22.dp),
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = primaryColor,
                                            uncheckedColor = Color.White.copy(alpha = 0.5f),
                                            checkmarkColor = Color.Black
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (item.isDone) Color.White.copy(alpha = 0.45f) else Color.White,
                                        textDecoration = if (item.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .weight(1f)
                                            .wrapContentHeight(),
                                        softWrap = true,
                                        maxLines = Int.MAX_VALUE
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Quick task addition
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newQuickTaskText,
                        onValueChange = { newQuickTaskText = it },
                        placeholder = { Text(strings.overlayQuickAddTaskHint, fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .onFocusChanged { focusState ->
                                onSetFocusable(focusState.isFocused)
                            },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (newQuickTaskText.isNotBlank() && note != null) {
                                val updatedTodos = note!!.todoItems + TodoItem(text = newQuickTaskText.trim(), isDone = false)
                                coroutineScope.launch {
                                    try {
                                        repository.updateNote(note!!.copy(todoItems = updatedTodos))
                                        newQuickTaskText = ""
                                        onSetFocusable(false)
                                    } catch (e: Exception) {
                                        Log.e("MiniTodoWidget", "Error updating note", e)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = strings.overlayAddQuickTask,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Resize Handle Bar with Corner Grips (سحب لتمديد أو تصغير النافذة)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Corner Resize Grip
                    Box(
                        modifier = Modifier
                            .size(32.dp, 18.dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        onResizeCorner(true, dragAmount.x, dragAmount.y)
                                    }
                                )
                            },
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Canvas(modifier = Modifier.size(13.dp)) {
                            val strokeWidth = 2.dp.toPx()
                            val c = primaryColor.copy(alpha = 0.85f)
                            drawLine(c, Offset(size.width * 0.7f, size.height), Offset(0f, size.height * 0.3f), strokeWidth)
                            drawLine(c, Offset(size.width * 0.95f, size.height), Offset(0f, size.height * 0.05f), strokeWidth)
                        }
                    }

                    // Center Bottom Resize Pill (Pull down to expand, push up to shrink)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        onResizeHeight(dragAmount.y)
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                    }

                    // Right Corner Resize Grip
                    Box(
                        modifier = Modifier
                            .size(32.dp, 18.dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        onResizeCorner(false, dragAmount.x, dragAmount.y)
                                    }
                                )
                            },
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Canvas(modifier = Modifier.size(13.dp)) {
                            val strokeWidth = 2.dp.toPx()
                            val c = primaryColor.copy(alpha = 0.85f)
                            drawLine(c, Offset(size.width * 0.3f, size.height), Offset(size.width, size.height * 0.3f), strokeWidth)
                            drawLine(c, Offset(size.width * 0.05f, size.height), Offset(size.width, size.height * 0.95f), strokeWidth)
                        }
                    }
                }
            }
        }

        // Generous corner touch zones for effortless corner dragging
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(38.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onResizeCorner(true, dragAmount.x, dragAmount.y)
                        }
                    )
                }
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(38.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            onResizeCorner(false, dragAmount.x, dragAmount.y)
                        }
                    )
                }
        )
    }
}

// 3. Half-Screen Overlay Panel with In-Place Note Viewer & Creator (Crash-Free, No Dialogs)
@Composable
fun FloatingOverlayContent(
    isDockedRight: Boolean,
    currentSubScreen: OverlaySubScreen = OverlaySubScreen.LIST,
    currentViewingNote: GameNote? = null,
    creatingNoteType: NoteType = NoteType.REGULAR,
    onRegisterFocusManager: (FocusManager?) -> Unit = {},
    onNavigateToList: () -> Unit = {},
    onNavigateToCreateNote: (NoteType) -> Unit = {},
    onNavigateToViewNote: (GameNote) -> Unit = {},
    onToggleDockSide: () -> Unit,
    onCollapse: () -> Unit,
    onSelectTodoList: (Long) -> Unit,
    onSetFocusable: (Boolean) -> Unit = {},
    onOpenInFullApp: (Long) -> Unit
) {
    val repository = remember { GameNotesApp.instance.repository }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    DisposableEffect(focusManager) {
        onRegisterFocusManager(focusManager)
        onDispose {
            onRegisterFocusManager(null)
        }
    }

    val strings = LocalAppStrings.current
    val allTabs by repository.allTabs.collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedGameTag by remember { mutableStateOf(strings.tabAll) }
    val notesFlow = remember(repository, selectedGameTag) { repository.getNotesByGame(selectedGameTag) }
    val notes by notesFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    var isAddingTabInPlace by remember { mutableStateOf(false) }

    // Gemini Assistant state inside overlay
    var isGeminiActive by remember { mutableStateOf(false) }
    var geminiInitialPrompt by remember { mutableStateOf("") }
    var geminiNoteTitle by remember { mutableStateOf("") }
    var geminiGameTag by remember { mutableStateOf("") }
    var notePendingDelete by remember { mutableStateOf<GameNote?>(null) }

    val pinnedNoteId by AppSettingsPreferences.pinnedFloatingNoteId.collectAsStateWithLifecycle()
    val pinnedTabIndex by AppSettingsPreferences.pinnedFloatingTabIndex.collectAsStateWithLifecycle()
    val pinnedScrollItemIdx by AppSettingsPreferences.pinnedFloatingScrollItemIndex.collectAsStateWithLifecycle()
    val pinnedScrollOffset by AppSettingsPreferences.pinnedFloatingScrollOffset.collectAsStateWithLifecycle()

    var noteActiveTabs by remember { mutableStateOf(mapOf<Long, Int>()) }

    LaunchedEffect(currentSubScreen, isGeminiActive, isAddingTabInPlace) {
        val needsFocus = currentSubScreen != OverlaySubScreen.LIST || isGeminiActive || isAddingTabInPlace
        onSetFocusable(needsFocus)
        if (!needsFocus) {
            focusManager.clearFocus(force = true)
        }
    }

    BackHandler(enabled = notePendingDelete != null || isGeminiActive || isAddingTabInPlace || currentSubScreen != OverlaySubScreen.LIST) {
        focusManager.clearFocus(force = true)
        when {
            notePendingDelete != null -> notePendingDelete = null
            isGeminiActive -> isGeminiActive = false
            isAddingTabInPlace -> isAddingTabInPlace = false
            currentSubScreen != OverlaySubScreen.LIST -> {
                onNavigateToList()
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val imeInsets = WindowInsets.ime
    val isKeyboardOpen by remember(density, imeInsets) {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }
    val isLandscapeKeyboard = isLandscape && isKeyboardOpen

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("half_screen_overlay_panel")
            .border(
                width = 1.dp,
                color = primaryColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(
                    topStart = if (isDockedRight) 20.dp else 0.dp,
                    bottomStart = if (isDockedRight) 20.dp else 0.dp,
                    topEnd = if (!isDockedRight) 20.dp else 0.dp,
                    bottomEnd = if (!isDockedRight) 20.dp else 0.dp
                )
            ),
        color = Color(0xF60B0F17),
        shape = RoundedCornerShape(
            topStart = if (isDockedRight) 20.dp else 0.dp,
            bottomStart = if (isDockedRight) 20.dp else 0.dp,
            topEnd = if (!isDockedRight) 20.dp else 0.dp,
            bottomEnd = if (!isDockedRight) 20.dp else 0.dp
        ),
        tonalElevation = 12.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isLandscapeKeyboard) 2.dp else 10.dp)
            ) {
                // Control Bar (Hidden when keyboard is visible in landscape to give full space to TextField)
                if (!isLandscapeKeyboard) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_game_notes_logo),
                                contentDescription = "GameNotes",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GameNotes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(6.dp))

                            // Switch Docking Side (Arrow button) - placed directly next to the app title
                            IconButton(
                                onClick = onToggleDockSide,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDockedRight) Icons.AutoMirrored.Filled.KeyboardArrowLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = strings.overlayToggleDockSide,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Flexible spacer pushing the minimize button to the far opposite edge
                            Spacer(modifier = Modifier.weight(1f))

                            // Minimize Button (Minus/Remove button) - placed at the opposite end
                            IconButton(
                                onClick = {
                                    focusManager.clearFocus(force = true)
                                    onCollapse()
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = strings.overlayMinimizeToBubble,
                                    tint = Color(0xFFFBBF24),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

            // Sub-Screen 1: Viewing / Editing a Note in Half-Screen Overlay
            if (currentSubScreen == OverlaySubScreen.VIEW_NOTE && currentViewingNote != null) {
                if (currentViewingNote.isTodoList) {
                    val isPinned = pinnedNoteId == currentViewingNote.id
                    val scrollIdx = if (isPinned) pinnedScrollItemIdx else 0
                    val scrollOff = if (isPinned) pinnedScrollOffset else 0
                    ChecklistEditorScreen(
                        note = currentViewingNote,
                        initialGameTag = currentViewingNote.gameTag,
                        availableGameTabs = allTabs.map { it.name },
                        initialScrollItemIndex = scrollIdx,
                        initialScrollOffset = scrollOff,
                        onBack = {
                            focusManager.clearFocus(force = true)
                            onNavigateToList()
                        },
                        onSave = { updatedNote ->
                            focusManager.clearFocus(force = true)
                            coroutineScope.launch {
                                try {
                                    repository.updateNote(updatedNote)
                                } catch (e: Exception) {
                                    Log.e("FloatingOverlayService", "Error updating note", e)
                                }
                            }
                            onNavigateToList()
                        },
                        onAskGemini = { n ->
                            val promptContext = buildString {
                                append(n.title)
                                if (n.content.isNotBlank()) append("\n").append(n.content)
                                val pending = n.todoItems.filter { !it.isDone }
                                if (pending.isNotEmpty()) {
                                    append("\n${strings.shareTasksLabel}: ")
                                    append(pending.joinToString(", ") { it.text })
                                }
                            }
                            geminiInitialPrompt = promptContext
                            geminiNoteTitle = n.title
                            geminiGameTag = n.gameTag
                            isGeminiActive = true
                        }
                    )
                } else {
                    val isPinned = pinnedNoteId == currentViewingNote.id
                    val tabIdx = if (isPinned) pinnedTabIndex else (noteActiveTabs[currentViewingNote.id] ?: 0)
                    val scrollIdx = if (isPinned) pinnedScrollItemIdx else 0
                    val scrollOff = if (isPinned) pinnedScrollOffset else 0
                    FullNoteEditorScreen(
                        note = currentViewingNote,
                        initialGameTag = currentViewingNote.gameTag,
                        availableGameTabs = allTabs.map { it.name },
                        initialTabIndex = tabIdx,
                        initialScrollItemIndex = scrollIdx,
                        initialScrollOffset = scrollOff,
                        onBack = {
                            focusManager.clearFocus(force = true)
                            onNavigateToList()
                        },
                        onSave = { updatedNote ->
                            focusManager.clearFocus(force = true)
                            coroutineScope.launch {
                                try {
                                    repository.updateNote(updatedNote)
                                } catch (e: Exception) {
                                    Log.e("FloatingOverlayService", "Error updating note", e)
                                }
                            }
                            onNavigateToList()
                        },
                        onAskGemini = { n ->
                            geminiInitialPrompt = buildString {
                                append(n.title)
                                if (n.content.isNotBlank()) append("\n").append(n.content)
                            }
                            geminiNoteTitle = n.title
                            geminiGameTag = n.gameTag
                            isGeminiActive = true
                        }
                    )
                }
                return@Column
            }

            // Sub-Screen 2: Creating a Note / Checklist in Half-Screen Overlay
            if (currentSubScreen == OverlaySubScreen.CREATE_NOTE) {
                val isAllSelected = selectedGameTag == strings.tabAll || selectedGameTag == "الكل" || selectedGameTag.equals("All", ignoreCase = true)
                val targetTag = if (!isAllSelected) selectedGameTag else (allTabs.firstOrNull()?.name ?: "")
                if (creatingNoteType == NoteType.TODO_LIST) {
                    ChecklistEditorScreen(
                        note = null,
                        initialGameTag = targetTag,
                        availableGameTabs = allTabs.map { it.name },
                        onBack = {
                            focusManager.clearFocus(force = true)
                            onNavigateToList()
                        },
                        onSave = { newNote ->
                            focusManager.clearFocus(force = true)
                            coroutineScope.launch {
                                try {
                                    val id = repository.insertNote(newNote)
                                    if (id > 0) {
                                        onSelectTodoList(id)
                                    }
                                } catch (e: Exception) {
                                    Log.e("FloatingOverlayService", "Error inserting checklist", e)
                                }
                            }
                            onNavigateToList()
                        },
                        onAskGemini = { n ->
                            geminiInitialPrompt = n.title
                            geminiNoteTitle = n.title
                            geminiGameTag = n.gameTag
                            isGeminiActive = true
                        }
                    )
                } else {
                    FullNoteEditorScreen(
                        note = null,
                        initialGameTag = targetTag,
                        availableGameTabs = allTabs.map { it.name },
                        onBack = {
                            focusManager.clearFocus(force = true)
                            onNavigateToList()
                        },
                        onSave = { newNote ->
                            focusManager.clearFocus(force = true)
                            coroutineScope.launch {
                                try {
                                    repository.insertNote(newNote)
                                } catch (e: Exception) {
                                    Log.e("FloatingOverlayService", "Error inserting note", e)
                                }
                            }
                            onNavigateToList()
                        },
                        onAskGemini = { n ->
                            geminiInitialPrompt = buildString {
                                append(n.title)
                                if (n.content.isNotBlank()) append("\n").append(n.content)
                            }
                            geminiNoteTitle = n.title
                            geminiGameTag = n.gameTag
                            isGeminiActive = true
                        }
                    )
                }
                return@Column
            }

            // Dynamic Game Tabs in Overlay
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = strings.overlayGamesLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick = { isAddingTabInPlace = !isAddingTabInPlace },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isAddingTabInPlace) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = strings.addNewTab,
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (isAddingTabInPlace) {
                var newTabInput by remember { mutableStateOf("") }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newTabInput,
                        onValueChange = { newTabInput = it },
                        placeholder = { Text(strings.newTabNameHint, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f)) },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (newTabInput.isNotBlank()) {
                                coroutineScope.launch {
                                    try {
                                        repository.insertTab(newTabInput.trim())
                                        selectedGameTag = newTabInput.trim()
                                        isAddingTabInPlace = false
                                    } catch (e: Exception) {
                                        Log.e("OverlayContent", "Error inserting tab", e)
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(strings.addTabButton, fontSize = 12.sp)
                    }
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isSelected = selectedGameTag == strings.tabAll || selectedGameTag == "الكل" || selectedGameTag.equals("All", ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) primaryColor.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.1f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, primaryColor) else null,
                        modifier = Modifier.clickable { selectedGameTag = strings.tabAll }
                    ) {
                        Text(
                            text = strings.tabAll,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) primaryColor else Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                items(allTabs, key = { it.id }) { tab ->
                    val isSelected = tab.name == selectedGameTag
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) primaryColor.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.1f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, primaryColor) else null,
                        modifier = Modifier.clickable { selectedGameTag = tab.name }
                    ) {
                        Text(
                            text = tab.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) primaryColor else Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Add Action Buttons (Direct In-Place, no crash)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        onNavigateToCreateNote(NoteType.REGULAR)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("overlay_quick_add_note"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = strings.overlayCreateNote, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        onNavigateToCreateNote(NoteType.TODO_LIST)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("overlay_quick_add_todo"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Checklist, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = strings.overlayCreateChecklist, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notes List: Clicking any note opens the full Half-Screen Note Viewer!
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = strings.overlayNoNotesInGame,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = strings.addNoteOrChecklist,
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notes, key = { it.id }, contentType = { "note_card" }) { note ->
                        // NoteCard with click-to-view in half-screen overlay!
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    onNavigateToViewNote(note)
                                },
                            color = Color.Transparent
                        ) {
                            NoteCard(
                                note = note,
                                isCompact = true,
                                activeInternalTabIndex = noteActiveTabs[note.id] ?: (if (pinnedNoteId == note.id) pinnedTabIndex else 0),
                                onTabSelected = { tabIdx ->
                                    noteActiveTabs = noteActiveTabs + (note.id to tabIdx)
                                },
                                onToggleTodo = { todoId ->
                                    coroutineScope.launch {
                                        try {
                                            repository.toggleTodoItem(note.id, todoId)
                                        } catch (e: Exception) {
                                            Log.e("OverlayContent", "Error toggling todo", e)
                                        }
                                    }
                                },
                                onPinAsMiniWidget = { n ->
                                    onSelectTodoList(n.id)
                                },
                                onAskGemini = { n ->
                                    val noteContextText = buildString {
                                        append(n.title)
                                        if (n.content.isNotBlank()) append("\n").append(n.content)
                                    }
                                    geminiInitialPrompt = noteContextText
                                    geminiNoteTitle = n.title
                                    geminiGameTag = n.gameTag
                                    isGeminiActive = true
                                },
                                onEdit = { n ->
                                    // Open in-overlay editor directly without leaving the game
                                    onNavigateToViewNote(n)
                                },
                                onDelete = { n ->
                                    // In-layout delete confirmation (no AlertDialog freeze!)
                                    notePendingDelete = n
                                },
                                onImageEdited = { n, oldUri, newUri ->
                                    coroutineScope.launch {
                                        try {
                                            val updatedImageUris = n.imageUris.map { if (it == oldUri) newUri else it }
                                            val updatedBlocksJson = if (n.blocksJson.isNotBlank() && n.blocksJson != "[]") {
                                                try {
                                                    val blocks = DocumentBlock.jsonToList(n.blocksJson)
                                                    val newBlocks = blocks.map { b ->
                                                        if (b.type == BlockType.IMAGE) {
                                                            var u = b
                                                            if (b.imageUri == oldUri) u = u.copy(imageUri = newUri)
                                                            if (b.secondImageUri == oldUri) u = u.copy(secondImageUri = newUri)
                                                            u
                                                        } else b
                                                    }
                                                    DocumentBlock.listToJson(newBlocks)
                                                } catch (_: Exception) {
                                                    n.blocksJson
                                                }
                                            } else {
                                                n.blocksJson
                                            }
                                            repository.updateNote(n.copy(
                                                imageUris = updatedImageUris,
                                                blocksJson = updatedBlocksJson,
                                                updatedAt = System.currentTimeMillis()
                                            ))
                                        } catch (e: Exception) {
                                            Log.e("OverlayContent", "Error updating note image", e)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // In-Layout Delete Confirmation Overlay (Zero Dialogs, Crash-Free)
        if (notePendingDelete != null) {
            val toDelete = notePendingDelete!!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f))
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
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = strings.deleteNoteConfirmTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.deleteNoteConfirmMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { notePendingDelete = null },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(strings.cancel)
                            }
                            Button(
                                onClick = {
                                    val target = notePendingDelete
                                    notePendingDelete = null
                                    if (target != null) {
                                        coroutineScope.launch {
                                            try {
                                                repository.deleteNote(target)
                                            } catch (e: Exception) {
                                                Log.e("FloatingOverlay", "Error deleting note", e)
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(strings.deleteAction)
                            }
                        }
                    }
                }
            }
        }

        // In-Layout Gemini Assistant Overlay (Covers the overlay without system dialogs)
        if (isGeminiActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B0F17))
                    .clickable(enabled = true, onClick = {})
            ) {
                GeminiChatSheet(
                    initialPrompt = geminiInitialPrompt,
                    noteTitle = geminiNoteTitle,
                    gameTag = geminiGameTag,
                    onClose = { isGeminiActive = false },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
}

