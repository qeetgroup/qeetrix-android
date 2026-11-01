package com.qeetgroup.qeetrix.internal

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * spinner.css: one turn every `2 × motion.duration.slower` (800 ms), `motion.easing.linear`, forever.
 */
internal val SpinnerTurnMillis: Int = 2 * QeetrixMotion.DurationSlower

/** spinner.css, under `prefers-reduced-motion`: a full ring that fades to 35% and back every 1.6 s, ease-in-out. */
private const val SPINNER_FADE_MILLIS = 1600
private const val SPINNER_FADE_ALPHA = 0.35f

/** CSS `ease-in-out`, the fade's timing function. */
private val EaseInOut = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)

/** spinner.css: `border: 2px solid`. */
private val SpinnerStroke = 2.dp

/**
 * The Qeetrix spinner's drawing (`.qx-spinner`): a 2 dp ring in [color] with its inline-end quarter clear
 * (`border-inline-end-color: transparent`), turning once every 800 ms. Under reduced motion it is a full
 * ring that fades instead, as the web's does. Decorative; callers give the semantics.
 */
@Composable
internal fun QeetrixSpinnerRing(
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val reduce = QeetrixTheme.reduceMotion
    val transition = rememberInfiniteTransition(label = "qeetrix-spinner")
    val angle =
        if (reduce) {
            null
        } else {
            transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(tween(SpinnerTurnMillis, easing = QeetrixMotion.EasingLinear), RepeatMode.Restart),
                label = "qeetrix-spinner-turn",
            )
        }
    val fade =
        if (reduce) {
            transition.animateFloat(
                initialValue = 1f,
                targetValue = SPINNER_FADE_ALPHA,
                animationSpec = infiniteRepeatable(tween(SPINNER_FADE_MILLIS / 2, easing = EaseInOut), RepeatMode.Reverse),
                label = "qeetrix-spinner-fade",
            )
        } else {
            null
        }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Canvas(modifier.size(size).graphicsLayer { alpha = fade?.value ?: 1f }) {
        val stroke = SpinnerStroke.toPx()
        val inset = stroke / 2
        val arcSize =
            androidx.compose.ui.geometry
                .Size(this.size.width - stroke, this.size.height - stroke)
        val topLeft =
            androidx.compose.ui.geometry
                .Offset(inset, inset)
        rotate(angle?.value ?: 0f) {
            if (reduce) {
                drawArc(color, 0f, 360f, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(stroke))
            } else {
                // A CSS border's four sides meet on the diagonals, so the clear inline-end side is the quarter
                // centred on 3 o'clock (9 o'clock in right to left).
                val clearCentre = if (rtl) 180f else 0f
                drawArc(color, clearCentre + 45f, 270f, useCenter = false, topLeft = topLeft, size = arcSize, style = Stroke(stroke))
            }
        }
    }
}
