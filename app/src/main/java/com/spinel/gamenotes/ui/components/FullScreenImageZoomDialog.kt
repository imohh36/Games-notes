package com.spinel.gamenotes.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.spinel.gamenotes.util.StorageUtils
import com.spinel.gamenotes.util.LanguagePreferences.LocalAppStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DrawnPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

val DRAW_PALETTE = listOf(
    Color(0xFFFF3B30), // Red
    Color(0xFF10B981), // Gamer Teal
    Color(0xFFFFCC00), // Yellow
    Color(0xFF007AFF), // Blue
    Color(0xFFAF52DE), // Purple
    Color(0xFFFFFFFF), // White
    Color(0xFF000000)  // Black
)

@Composable
fun FullScreenImageZoomDialog(
    imageUri: String,
    onDismiss: () -> Unit,
    onImageSaved: ((newUri: String) -> Unit)? = null
) {
    if (imageUri.isBlank()) return

    val context = LocalContext.current
    val strings = LocalAppStrings.current
    val coroutineScope = rememberCoroutineScope()

    var isDrawMode by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(DRAW_PALETTE[0]) }
    var strokeWidth by remember { mutableFloatStateOf(8f) }
    var isSaving by remember { mutableStateOf(false) }

    // Drawing paths list
    val paths = remember { mutableStateListOf<DrawnPath>() }
    var currentPoints = remember { mutableStateListOf<Offset>() }

    // Zoom & Pan for View Mode
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val dialogContent = @Composable {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
                .onSizeChanged { containerSize = it }
                .testTag("fullscreen_image_dialog")
        ) {
            // Main image display + canvas layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isDrawMode, selectedColor, strokeWidth) {
                        if (!isDrawMode) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(1f, 5f)
                                scale = newScale
                                if (newScale > 1f) {
                                    val maxOffsetX = (size.width * (newScale - 1f)) / 2f
                                    val maxOffsetY = (size.height * (newScale - 1f)) / 2f
                                    val newOffset = offset + pan
                                    offset = Offset(
                                        x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                                        y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                                    )
                                } else {
                                    offset = Offset.Zero
                                }
                            }
                        } else {
                            // Draw mode: 1 finger strictly for drawing, 2 fingers for pinch-to-zoom & pan
                            awaitEachGesture {
                                awaitFirstDown(requireUnconsumed = false)
                                var isMultiTouchZooming = false
                                var isDrawingStroke = false

                                do {
                                    val event = awaitPointerEvent()
                                    val activePointers = event.changes.filter { it.pressed }

                                    if (activePointers.size >= 2) {
                                        // 2 or more fingers: Pinch-to-zoom & pan
                                        if (isDrawingStroke) {
                                            currentPoints.clear()
                                            isDrawingStroke = false
                                        }
                                        isMultiTouchZooming = true

                                        val zoomChange = event.calculateZoom()
                                        val panChange = event.calculatePan()

                                        val newScale = (scale * zoomChange).coerceIn(1f, 5f)
                                        scale = newScale
                                        if (newScale > 1f) {
                                            val maxOffsetX = (size.width * (newScale - 1f)) / 2f
                                            val maxOffsetY = (size.height * (newScale - 1f)) / 2f
                                            val newOffset = offset + panChange
                                            offset = Offset(
                                                x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                                                y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                                            )
                                        } else {
                                            offset = Offset.Zero
                                        }
                                        event.changes.forEach { it.consume() }
                                    } else if (activePointers.size == 1 && !isMultiTouchZooming) {
                                        // Exactly 1 finger: Drawing
                                        val singlePointer = activePointers.first()
                                        val unzoomedPoint = Offset(
                                            x = (singlePointer.position.x - offset.x) / scale,
                                            y = (singlePointer.position.y - offset.y) / scale
                                        )
                                        if (!isDrawingStroke) {
                                            isDrawingStroke = true
                                            currentPoints.clear()
                                            currentPoints.add(unzoomedPoint)
                                        } else {
                                            currentPoints.add(unzoomedPoint)
                                        }
                                        singlePointer.consume()
                                    }
                                } while (event.changes.any { it.pressed })

                                if (isDrawingStroke && currentPoints.isNotEmpty()) {
                                    paths.add(
                                        DrawnPath(
                                            points = currentPoints.toList(),
                                            color = selectedColor,
                                            strokeWidth = strokeWidth
                                        )
                                    )
                                    currentPoints.clear()
                                }
                            }
                        }
                    }
                    .pointerInput(isDrawMode) {
                        if (!isDrawMode) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (scale > 1.2f) {
                                        scale = 1f
                                        offset = Offset.Zero
                                    } else {
                                        scale = 2.5f
                                        offset = Offset.Zero
                                    }
                                }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Background Base Image + Canvas layer in synchronized graphicsLayer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Background Base Image
                    AsyncImage(
                        model = imageUri,
                        contentDescription = strings.fullScreenImageCd,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Canvas Layer for Markup Drawing
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                    // Draw established strokes
                    paths.forEach { drawnPath ->
                        if (drawnPath.points.size > 1) {
                            val path = Path().apply {
                                moveTo(drawnPath.points.first().x, drawnPath.points.first().y)
                                for (i in 1 until drawnPath.points.size) {
                                    lineTo(drawnPath.points[i].x, drawnPath.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = drawnPath.color,
                                style = Stroke(
                                    width = drawnPath.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        } else if (drawnPath.points.size == 1) {
                            drawCircle(
                                color = drawnPath.color,
                                radius = drawnPath.strokeWidth / 2f,
                                center = drawnPath.points.first()
                            )
                        }
                    }

                    // Draw in-progress active stroke
                    if (currentPoints.size > 1) {
                        val path = Path().apply {
                            moveTo(currentPoints.first().x, currentPoints.first().y)
                            for (i in 1 until currentPoints.size) {
                                lineTo(currentPoints[i].x, currentPoints[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = selectedColor,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    } else if (currentPoints.size == 1) {
                        drawCircle(
                            color = selectedColor,
                            radius = strokeWidth / 2f,
                            center = currentPoints.first()
                        )
                    }
                }
            }
        }

        // Top Control Bar
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode indicator & toggle button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDrawMode) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.6f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDrawMode) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.clickable {
                        isDrawMode = !isDrawMode
                        if (isDrawMode) {
                            scale = 1f
                            offset = Offset.Zero
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isDrawMode) Icons.Default.PanTool else Icons.Default.Brush,
                            contentDescription = if (isDrawMode) strings.panZoomMode else strings.drawingMode,
                            tint = if (isDrawMode) MaterialTheme.colorScheme.onPrimary else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isDrawMode) strings.drawingModeActive else strings.drawingAnnotation,
                            color = if (isDrawMode) MaterialTheme.colorScheme.onPrimary else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Right controls (Zoom level, Reset, Close)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isDrawMode && scale > 1.05f) {
                        Surface(
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomIn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${(scale * 100).toInt()}%",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                scale = 1f
                                offset = Offset.Zero
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = strings.resetZoom,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .testTag("close_fullscreen_image_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.close,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Draw Mode Bottom Toolbar (Color picker, Stroke size, Undo, Clear, Save)
            AnimatedVisibility(
                visible = isDrawMode,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    shadowElevation = 10.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Colors Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DRAW_PALETTE.forEach { color ->
                                val isSelected = selectedColor == color
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 30.dp else 24.dp)
                                        .background(color, CircleShape)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor = color }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions Row: Undo, Clear, Stroke Width, and Save modified image
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // Undo button
                                IconButton(
                                    onClick = {
                                        if (paths.isNotEmpty()) {
                                            paths.removeAt(paths.size - 1)
                                        }
                                    },
                                    enabled = paths.isNotEmpty(),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Undo,
                                        contentDescription = strings.undo,
                                        tint = if (paths.isNotEmpty()) Color.White else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Clear all button
                                IconButton(
                                    onClick = { paths.clear() },
                                    enabled = paths.isNotEmpty(),
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                                 ) {
                                     Icon(
                                         imageVector = Icons.Default.DeleteSweep,
                                         contentDescription = strings.clearDrawing,
                                         tint = if (paths.isNotEmpty()) Color(0xFFFF5252) else Color.Gray,
                                         modifier = Modifier.size(18.dp)
                                     )
                                }
                            }

                            // Stroke thickness slider
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size((strokeWidth * 1.5f).coerceIn(4f, 20f).dp)
                                        .background(selectedColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Slider(
                                    value = strokeWidth,
                                    onValueChange = { strokeWidth = it },
                                    valueRange = 3f..24f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = MaterialTheme.colorScheme.primary,
                                        activeTrackColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }

                            // Save Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .clickable(enabled = !isSaving) {
                                        if (paths.isEmpty()) {
                                            Toast.makeText(context, strings.noDrawingsToSave, Toast.LENGTH_SHORT).show()
                                            return@clickable
                                        }
                                        isSaving = true
                                        coroutineScope.launch {
                                            try {
                                                val savedUri = withContext(Dispatchers.IO) {
                                                    // Render base image + drawing paths into a new Bitmap
                                                    val options = BitmapFactory.Options().apply {
                                                        inPreferredConfig = Bitmap.Config.ARGB_8888
                                                    }
                                                    val inputStream = when {
                                                        imageUri.startsWith("file://") -> {
                                                            val path = Uri.parse(imageUri).path
                                                            if (path != null) java.io.FileInputStream(java.io.File(path)) else null
                                                        }
                                                        else -> context.contentResolver.openInputStream(Uri.parse(imageUri))
                                                    }
                                                    val baseBitmap = BitmapFactory.decodeStream(inputStream, null, options)
                                                    inputStream?.close()

                                                    if (baseBitmap != null) {
                                                        val mutableBitmap = baseBitmap.copy(Bitmap.Config.ARGB_8888, true)
                                                        val canvas = android.graphics.Canvas(mutableBitmap)

                                                        // Accurately map drawing coordinates onto the native bitmap using ContentScale.Fit aspect frame
                                                        val cW = containerSize.width.coerceAtLeast(1).toFloat()
                                                        val cH = containerSize.height.coerceAtLeast(1).toFloat()
                                                        val bmW = mutableBitmap.width.toFloat()
                                                        val bmH = mutableBitmap.height.toFloat()

                                                        val fitScale = minOf(cW / bmW, cH / bmH)
                                                        val displayedW = bmW * fitScale
                                                        val displayedH = bmH * fitScale
                                                        val offsetX = (cW - displayedW) / 2f
                                                        val offsetY = (cH - displayedH) / 2f

                                                        val paint = Paint().apply {
                                                            isAntiAlias = true
                                                            style = Paint.Style.STROKE
                                                            strokeCap = Paint.Cap.ROUND
                                                            strokeJoin = Paint.Join.ROUND
                                                        }

                                                        paths.forEach { dp ->
                                                            paint.color = android.graphics.Color.argb(
                                                                (dp.color.alpha * 255).toInt(),
                                                                (dp.color.red * 255).toInt(),
                                                                (dp.color.green * 255).toInt(),
                                                                (dp.color.blue * 255).toInt()
                                                            )
                                                            paint.strokeWidth = (dp.strokeWidth / fitScale).coerceAtLeast(1f)

                                                            if (dp.points.size > 1) {
                                                                val androidPath = android.graphics.Path()
                                                                val firstPt = dp.points.first()
                                                                val startX = ((firstPt.x - offsetX) / fitScale).coerceIn(0f, bmW)
                                                                val startY = ((firstPt.y - offsetY) / fitScale).coerceIn(0f, bmH)
                                                                androidPath.moveTo(startX, startY)
                                                                for (i in 1 until dp.points.size) {
                                                                    val pt = dp.points[i]
                                                                    val px = ((pt.x - offsetX) / fitScale).coerceIn(0f, bmW)
                                                                    val py = ((pt.y - offsetY) / fitScale).coerceIn(0f, bmH)
                                                                    androidPath.lineTo(px, py)
                                                                }
                                                                canvas.drawPath(androidPath, paint)
                                                            } else if (dp.points.size == 1) {
                                                                val pt = dp.points.first()
                                                                val px = ((pt.x - offsetX) / fitScale).coerceIn(0f, bmW)
                                                                val py = ((pt.y - offsetY) / fitScale).coerceIn(0f, bmH)
                                                                canvas.drawCircle(px, py, paint.strokeWidth / 2f, paint)
                                                            }
                                                        }

                                                        StorageUtils.saveBitmapToInternalStorage(context, mutableBitmap)
                                                    } else {
                                                        ""
                                                    }
                                                }

                                                if (savedUri.isNotBlank()) {
                                                    Toast.makeText(context, strings.drawingSavedSuccess, Toast.LENGTH_SHORT).show()
                                                    onImageSaved?.invoke(savedUri)
                                                    onDismiss()
                                                } else {
                                                    Toast.makeText(context, strings.failedToProcessSaveImage, Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "${strings.errorPrefix}: ${e.message}", Toast.LENGTH_SHORT).show()
                                            } finally {
                                                isSaving = false
                                            }
                                        }
                                    }
                                    .testTag("save_drawn_image_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isSaving) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Save,
                                            contentDescription = strings.save,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = strings.save,
                                            color = MaterialTheme.colorScheme.onPrimary,
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

            // Bottom hint for View Mode
            if (!isDrawMode) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    Text(
                        text = strings.zoomPinchHint,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    val registryOwner = androidx.activity.compose.LocalActivityResultRegistryOwner.current
    if (registryOwner != null) {
        Dialog(
            onDismissRequest = {
                if (!isSaving) onDismiss()
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            dialogContent()
        }
    } else {
        dialogContent()
    }
}
