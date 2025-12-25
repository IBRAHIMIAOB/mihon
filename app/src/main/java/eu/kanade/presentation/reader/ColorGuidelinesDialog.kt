package eu.kanade.presentation.reader

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Paint as AndroidPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource

import tachiyomi.core.common.util.system.logcat

data class DrawPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
)

@Composable
fun ColorGuidelinesDialog(
    pageImageBitmap: ImageBitmap?,
    onDismiss: () -> Unit,
    onConfirm: (Bitmap) -> Unit,
) {
    val colorPalette = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color(0xFFFFA500), // Orange
        Color(0xFF800080), // Purple
        Color(0xFFFF69B4), // Pink
        Color(0xFF8B4513), // Brown
        Color.White,
        Color.Black,
    )
    
    var selectedColor by remember { mutableStateOf(colorPalette[0]) }
    val paths = remember { mutableStateListOf<DrawPath>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    
    val strokeWidth = 15f
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                logcat { "TEMP_LOG: ColorGuidelinesDialog Composed" }
                // Top bar with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(MR.strings.ai_guidelines_title),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Canvas area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                ) {
                    // Background image
                    if (pageImageBitmap != null) {
                        AsyncImage(
                            model = pageImageBitmap.asAndroidBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .onSizeChanged { canvasSize = it },
                            contentScale = ContentScale.Fit,
                        )
                    }
                    
                    // Drawing canvas overlay
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPath = Path().apply {
                                            moveTo(offset.x, offset.y)
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        currentPath?.lineTo(change.position.x, change.position.y)
                                        // Force recomposition
                                        currentPath = currentPath?.let { Path().apply { addPath(it) } }
                                    },
                                    onDragEnd = {
                                        currentPath?.let { path ->
                                            paths.add(DrawPath(path, selectedColor, strokeWidth))
                                        }
                                        currentPath = null
                                    },
                                )
                            },
                    ) {
                        // Draw all completed paths
                        paths.forEach { drawPath ->
                            drawPath(
                                path = drawPath.path,
                                color = drawPath.color,
                                style = Stroke(
                                    width = drawPath.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round,
                                ),
                            )
                        }
                        
                        // Draw current path being drawn
                        currentPath?.let { path ->
                            drawPath(
                                path = path,
                                color = selectedColor,
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round,
                                ),
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Color palette
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    colorPalette.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (color == selectedColor) 3.dp else 1.dp,
                                    color = if (color == selectedColor) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.outline,
                                    shape = CircleShape,
                                )
                                .clickable { selectedColor = color },
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bottom action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Clear button
                    Button(
                        onClick = { paths.clear() },
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(MR.strings.ai_guidelines_clear))
                    }
                    
                    // Enhance button
                    Button(
                        onClick = {
                            if (pageImageBitmap != null && canvasSize.width > 0 && canvasSize.height > 0) {
                                val resultBitmap = createCompositeBitmap(
                                    pageImageBitmap,
                                    paths,
                                    canvasSize,
                                )
                                onConfirm(resultBitmap)
                            }
                        },
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(MR.strings.ai_guidelines_confirm))
                    }
                }
            }
        }
    }
}

private fun createCompositeBitmap(
    originalBitmap: ImageBitmap,
    paths: List<DrawPath>,
    canvasSize: IntSize,
): Bitmap {
    // Create a bitmap the same size as the original
    val original = originalBitmap.asAndroidBitmap()
    val result = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(result)
    
    // Draw the original image
    canvas.drawBitmap(original, 0f, 0f, null)
    
    // Calculate scale factor from canvas display size to actual image size
    val scaleX = original.width.toFloat() / canvasSize.width
    val scaleY = original.height.toFloat() / canvasSize.height
    
    // Draw each path scaled to image coordinates
    paths.forEach { drawPath ->
        val paint = AndroidPaint().apply {
            color = drawPath.color.toArgb()
            strokeWidth = drawPath.strokeWidth * scaleX
            style = AndroidPaint.Style.STROKE
            strokeCap = AndroidPaint.Cap.ROUND
            strokeJoin = AndroidPaint.Join.ROUND
            isAntiAlias = true
        }
        
        // Convert compose Path to Android Path with scaling
        val androidPath = android.graphics.Path()
        val pathIterator = drawPath.path.iterator()
        while (pathIterator.hasNext()) {
            val segment = pathIterator.next()
            when (segment.type) {
                androidx.compose.ui.graphics.PathSegment.Type.Move -> {
                    androidPath.moveTo(
                        segment.points[0] * scaleX,
                        segment.points[1] * scaleY,
                    )
                }
                androidx.compose.ui.graphics.PathSegment.Type.Line -> {
                    androidPath.lineTo(
                        segment.points[2] * scaleX,
                        segment.points[3] * scaleY,
                    )
                }
                else -> {}
            }
        }
        
        canvas.drawPath(androidPath, paint)
    }
    
    return result
}
