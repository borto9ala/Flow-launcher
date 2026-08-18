package com.flowlauncher.app.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun WaveAlphabet(
    letters: List<Char>,
    onLetterSelected: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    if (letters.isEmpty()) return

    val displayLetters = letters.map { it.uppercaseChar() }.distinct().sorted()
    if (displayLetters.isEmpty()) return
    val density = LocalDensity.current
    var highlightedIndex by remember { mutableIntStateOf(-1) }

    BoxWithConstraints(
        modifier = modifier
            .pointerInput(displayLetters) {
                detectDragGestures(
                    onDragStart = { offset ->
                        highlightedIndex = letterIndexFromY(offset.y, size.height.toFloat(), displayLetters.size)
                        displayLetters.getOrNull(highlightedIndex)?.let { onLetterSelected(it) }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        highlightedIndex = letterIndexFromY(change.position.y, size.height.toFloat(), displayLetters.size)
                        displayLetters.getOrNull(highlightedIndex)?.let { onLetterSelected(it) }
                    },
                    onDragEnd = { highlightedIndex = -1 },
                    onDragCancel = { highlightedIndex = -1 }
                )
            }
    ) {
        val boxHeight = maxHeight
        Canvas(modifier = Modifier.fillMaxHeight()) {
            val wavePath = Path().apply {
                val w = size.width
                val h = size.height
                moveTo(w * 0.8f, 0f)
                cubicTo(w * 0.2f, h * 0.25f, w * 0.2f, h * 0.75f, w * 0.8f, h)
            }
            drawPath(
                wavePath,
                color = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.15f),
                style = Stroke(width = 2f)
            )
        }

        displayLetters.forEachIndexed { index, letter ->
            val yFraction = index.toFloat() / (displayLetters.size - 1).coerceAtLeast(1)
            val waveOffset = kotlin.math.sin(yFraction * Math.PI).toFloat() * 8f
            val isHighlighted = index == highlightedIndex

            Text(
                text = letter.toString(),
                fontSize = if (isHighlighted) 14.sp else 10.sp,
                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset {
                        IntOffset(
                            x = with(density) { waveOffset.dp.roundToPx() },
                            y = (yFraction * (boxHeight.toPx() - 20.dp.toPx())).roundToInt()
                        )
                    }
            )
        }
    }
}

private fun letterIndexFromY(y: Float, height: Float, letterCount: Int): Int {
    val fraction = (y / height).coerceIn(0f, 1f)
    return (fraction * (letterCount - 1)).roundToInt()
}

@Composable
fun AppIcon(drawable: Drawable, modifier: Modifier = Modifier) {
    val bitmap = remember(drawable) { drawableToBitmap(drawable) }
    Image(
        bitmap = bitmap,
        contentDescription = null,
        modifier = modifier
    )
}

private fun drawableToBitmap(drawable: Drawable): androidx.compose.ui.graphics.ImageBitmap {
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
}
