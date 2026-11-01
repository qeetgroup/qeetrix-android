package com.qeetgroup.qeetrix.components.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** A [QeetrixProgressRing]'s size (`value` is the name shared with qeetrix-ios): the icon sizes it sits beside. */
public enum class QeetrixProgressRingSize(
    public val value: String,
    internal val diameter: Dp,
    internal val stroke: Dp,
) {
    /** `size.icon.sm`, 16 dp, a 2 dp ring: beside `body` text. */
    Sm("sm", QeetrixSize.IconSm, 2.dp),

    /** `size.icon.md`, 20 dp, a 2 dp ring. The default. */
    Md("md", QeetrixSize.IconMd, 2.dp),

    /** `size.icon.lg`, 24 dp, a 3 dp ring: beside a large code. */
    Lg("lg", QeetrixSize.IconLg, 3.dp),
}

/**
 * How far something has gone, as a ring — Progress's look drawn round, for a countdown or a small task beside text:
 * the seconds left on a one-time code. `@qeetrix/react` has no ring; it takes Progress's parts (progress.css): the
 * track (`color.border.default` in light, `color.surface.sunken` in dark), the [tone]'s indicator, square ends.
 *
 * - [value] is the share done, 0 to 1 (clamped; NaN is rejected), drawn clockwise from 12 o'clock — a clock's way,
 *   in right to left too. A new value is reached over `motion.duration.normal`, and at once under reduced motion; a
 *   countdown that sets it every frame moves smoothly, and steps once a second where it sets it once a second.
 * - **Accessibility.** Read as a progress bar named [contentDescription], with [stateDescription] as its value when
 *   given ("12 seconds left"); otherwise the percentage. Without a [contentDescription] the ring is decorative and
 *   hidden, for one whose meaning is already in text beside it.
 *
 * ```
 * QeetrixProgressRing(secondsLeft / 30f, contentDescription = "Time left", stateDescription = "$secondsLeft seconds left")
 * ```
 */
@Composable
public fun QeetrixProgressRing(
    value: Float,
    modifier: Modifier = Modifier,
    size: QeetrixProgressRingSize = QeetrixProgressRingSize.Md,
    tone: QeetrixMeterTone = QeetrixMeterTone.Brand,
    contentDescription: String? = null,
    stateDescription: String? = null,
) {
    require(!value.isNaN()) { "A ring's value is a share from 0 to 1." }
    val target = value.coerceIn(0f, 1f)
    val shown by animateFloatAsState(
        target,
        qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal),
        label = "progress-ring",
    )
    val colors = QeetrixTheme.colors
    val dark = QeetrixTheme.isDark
    val track = progressTrack(colors, dark)
    val indicator = tone.indicator(colors, dark)
    val described =
        if (contentDescription != null) {
            Modifier.progressSemantics(target, 0f..1f).semantics {
                this.contentDescription = contentDescription
                if (stateDescription != null) this.stateDescription = stateDescription
            }
        } else {
            Modifier // decorative: a Canvas has nothing to read
        }
    Canvas(modifier.size(size.diameter).then(described)) {
        val width = size.stroke.toPx()
        val inset = width / 2
        val arc = Size(this.size.width - width, this.size.height - width)
        drawArc(track, 0f, 360f, useCenter = false, topLeft = Offset(inset, inset), size = arc, style = Stroke(width))
        drawArc(
            indicator,
            -90f,
            360f * shown,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arc,
            style = Stroke(width, cap = StrokeCap.Butt),
        )
    }
}
