package com.qeetgroup.qeetrix.components.progress

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixProgressComponentTokens
import java.text.NumberFormat

/** Track thickness of [QeetrixProgress] (`value` is the web name): fixed steps, not density-resolved. */
public enum class QeetrixProgressSize(
    public val value: String,
    internal val thickness: Dp,
) {
    /** 4 dp (`h-1`). */
    Sm("sm", QeetrixWebScale.spacing(1f)),

    /** 8 dp (`h-2`), the default. */
    Md("md", QeetrixWebScale.spacing(2f)),

    /** 12 dp (`h-3`). */
    Lg("lg", QeetrixWebScale.spacing(3f)),
}

/**
 * Task progress: a filled track for a known fraction, or — with [progress] `null` — a moving segment
 * while the total is unknown.
 *
 * - [progress] is a fraction from 0 to 1; values outside are clamped, and NaN is rejected. The fill eases
 *   to a new value with the slow motion role.
 * - Indeterminate is a 40% segment sweeping along the reading direction (mirrored right to left). With
 *   animations turned off it is a still, hatched fill: busy, not done.
 * - [label] is shown above the track and names the progress for accessibility services; the value (a
 *   locale-formatted percentage) is shown opposite it unless [showValue] is false. Without a label, pass
 *   a [contentDescription].
 *
 * Accessibility services read it as a progress bar with its current value, from Compose's progress
 * semantics.
 */
@Composable
public fun QeetrixProgress(
    progress: Float?,
    modifier: Modifier = Modifier,
    size: QeetrixProgressSize = QeetrixProgressSize.Md,
    label: String? = null,
    showValue: Boolean = true,
    contentDescription: String? = null,
) {
    require(progress == null || !progress.isNaN()) { "progress must be a number from 0 to 1, or null when unknown." }
    val fraction = progress?.coerceIn(0f, 1f)
    val name = label ?: contentDescription
    Column(
        modifier
            .fillMaxWidth()
            .then(if (fraction != null) Modifier.progressSemantics(fraction) else Modifier.progressSemantics())
            .semantics(mergeDescendants = true) { if (name != null) this.contentDescription = name },
        verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1.5f)), // gap-1.5
    ) {
        if (label != null) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // progress-label: font-medium text-foreground
                BasicText(label, Modifier.weight(1f, fill = false), style = LabelStyle.copy(color = QeetrixTheme.colors.textPrimary))
                if (showValue && fraction != null) {
                    val locale = LocalConfiguration.current.locales[0]
                    val formatted = remember(fraction, locale) { NumberFormat.getPercentInstance(locale).format(fraction.toDouble()) }
                    // progress-value: tabular-nums text-muted-foreground
                    BasicText(formatted, style = ValueStyle.copy(color = QeetrixTheme.colors.textTertiary))
                }
            }
        }
        Track(fraction, size)
    }
}

@Composable
private fun Track(
    fraction: Float?,
    size: QeetrixProgressSize,
) {
    val trackColor = QeetrixProgressComponentTokens.Track
    val fill = QeetrixProgressComponentTokens.Indicator
    val reduce = QeetrixTheme.reduceMotion
    val shape = RoundedCornerShape(percent = 50) // rounded-full
    if (fraction != null) {
        // transition-[width] duration-slow ease-standard
        val animated by animateFloatAsState(
            fraction,
            qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Slow),
            label = "progress",
        )
        Canvas(Modifier.fillMaxWidth().height(size.thickness).clip(shape)) {
            drawRect(trackColor)
            val width = this.size.width * animated
            val left = if (layoutDirection == LayoutDirection.Ltr) 0f else this.size.width - width
            drawPill(fill, left, width)
        }
    } else {
        val position by rememberSweep(reduce)
        Canvas(Modifier.fillMaxWidth().height(size.thickness).clip(shape)) {
            drawRect(trackColor)
            if (reduce) {
                drawHatch(fill)
            } else {
                // A 40% segment translating from -140% to +100% of the track (the web's slide-in-from-start-[350%]
                // to translate-x-[250%] of a 2/5-wide element), along the reading direction.
                val segment = this.size.width * 0.4f
                val travel = this.size.width * (-1.4f + 2.4f * position)
                val left = if (layoutDirection == LayoutDirection.Ltr) travel else this.size.width - travel - segment
                drawPill(fill, left, segment)
            }
        }
    }
}

/** 0 → 1 over 1.4 s, linear, forever (`animation-duration-[1400ms]`, `ease-(--qx-motion-easing-linear)`). */
@Composable
private fun rememberSweep(reduce: Boolean): State<Float> {
    if (reduce) return remember { mutableFloatStateOf(0f) }
    return rememberInfiniteTransition(label = "progress-sweep").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(INDETERMINATE_MILLIS, easing = LinearEasing)),
        label = "progress-sweep-position",
    )
}

private fun DrawScope.drawPill(
    color: Color,
    left: Float,
    width: Float,
) {
    val radius = size.height / 2
    drawRoundRect(color, topLeft = Offset(left, 0f), size = Size(width, size.height), cornerRadius = CornerRadius(radius, radius))
}

/**
 * `repeating-linear-gradient(-45deg, fill 0 4px, transparent 4px 8px)`: still, hatched, busy. A -45° CSS
 * gradient line points up and to the left, so the bands run bottom-left to top-right ("/"), each 4 px
 * wide across the line (4√2 along the track).
 */
private fun DrawScope.drawHatch(color: Color) {
    val across = QeetrixWebScale.spacing(1f).toPx() * SQRT2 // a 4px band, measured along the track
    val height = size.height
    clipRect {
        var x = -height
        while (x < size.width) {
            val band =
                Path().apply {
                    moveTo(x + height, 0f)
                    lineTo(x + height + across, 0f)
                    lineTo(x + across, height)
                    lineTo(x, height)
                    close()
                }
            drawPath(band, color)
            x += 2 * across // the period is 8px across the line
        }
    }
}

private const val SQRT2 = 1.41421356f

private const val INDETERMINATE_MILLIS = 1400

// progress-header: text-sm; label font-medium; value tabular-nums.
private val LabelStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm, FontWeightMedium)
private val ValueStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm).copy(fontFeatureSettings = "tnum")
