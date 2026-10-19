package com.qeetgroup.qeetrix.internal

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The angle of `animate-spin` — one turn per second, linear, forever — or a still 0° when the user has
 * turned animations off: the web's global reduced-motion rule stops every animation after one
 * zero-length run.
 */
@Composable
internal fun rememberSpin(): State<Float> {
    if (QeetrixTheme.reduceMotion) return remember { mutableFloatStateOf(0f) }
    val transition = rememberInfiniteTransition(label = "qeetrix-spin")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(QeetrixWebScale.SPIN_MILLIS, easing = LinearEasing), RepeatMode.Restart),
        label = "qeetrix-spin-angle",
    )
}

/**
 * The Button family's busy indicator (`ButtonSpinner` in qeetrix-ui): a faint track and a turning
 * quarter arc in the label's colour, drawn on the web's 16-unit grid — a circle of radius 6.25 stroked
 * 1.5, the track at 25% opacity. Decorative: the button reports its busy state itself.
 */
@Composable
internal fun QeetrixBusyIndicator(
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val angle = rememberSpin()
    Canvas(modifier.size(size)) {
        val unit = this.size.minDimension / 16f
        val radius = 6.25f * unit
        val stroke = 1.5f * unit
        val topLeft = Offset(center.x - radius, center.y - radius)
        drawCircle(color.copy(alpha = color.alpha * 0.25f), radius = radius, style = Stroke(stroke))
        rotate(angle.value) {
            // M8 1.75 a6.25 6.25 0 0 1 6.25 6.25: from 12 o'clock a quarter turn clockwise.
            drawArc(
                color,
                startAngle = -90f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(2 * radius, 2 * radius),
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
    }
}
