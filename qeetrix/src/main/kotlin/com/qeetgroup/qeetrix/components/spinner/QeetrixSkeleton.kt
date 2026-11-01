package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The outline of a [QeetrixSkeleton] (`value` is `@qeetrix/react`'s Skeleton `variant`). */
public enum class QeetrixSkeletonVariant(
    public val value: String,
) {
    /** One line of text: a line of `body.md` tall (1lh), full width, `radius.sm` corners. The default. */
    Text("text"),

    /** A card, an image or a chart: 64 dp tall by default, full width, `radius.surface` corners. */
    Block("block"),

    /** An avatar: a circle of Avatar's sizes ([QeetrixSkeletonSize]). */
    Circle("circle"),
}

/** A circle skeleton's diameter, as Avatar's sizes (`value` is `@qeetrix/react`'s Skeleton `size`). */
public enum class QeetrixSkeletonSize(
    public val value: String,
    internal val diameter: Dp,
) {
    /** 24 dp. */
    Sm("sm", 24.dp),

    /** 32 dp. The default. */
    Md("md", 32.dp),

    /** 40 dp. */
    Lg("lg", 40.dp),

    /** 48 dp. */
    Xl("xl", 48.dp),
}

/**
 * A placeholder shaped like the content that is loading — `@qeetrix/react`'s Skeleton: a
 * `color.bg.neutral.subtle-pressed` shape that pulses its opacity to 45 % and back every 1.6 s
 * (`4 × motion.duration.slower` on `motion.easing.standard`), and stands still under reduced motion. Only drawing
 * changes as it pulses: nothing is recomposed.
 *
 * The [variant] sets its outline and default size: a line of text, a block (64 dp tall) or a circle of [size]. Size
 * it with [modifier] (`Modifier.width(…)`, `size(…)`) to match what will arrive; for a paragraph, use
 * [QeetrixSkeletonText].
 *
 * ```
 * Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
 *     QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle, size = QeetrixSkeletonSize.Lg)
 *     QeetrixSkeletonText(lines = 2, modifier = Modifier.weight(1f))
 * }
 * ```
 *
 * A skeleton has nothing to say to accessibility services, and they don't see it (`aria-hidden`). Say that the region
 * is loading where it is — [com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState] shows a labelled spinner —
 * rather than letting each placeholder speak.
 *
 * @param size The diameter of a [QeetrixSkeletonVariant.Circle]; the other variants ignore it.
 */
@Composable
public fun QeetrixSkeleton(
    modifier: Modifier = Modifier,
    variant: QeetrixSkeletonVariant = QeetrixSkeletonVariant.Text,
    size: QeetrixSkeletonSize = QeetrixSkeletonSize.Md,
) {
    val defaults =
        when (variant) {
            // block-size: 1lh; inline-size: 100%
            QeetrixSkeletonVariant.Text -> Modifier.fillMaxWidth().height(firstLineHeight(QeetrixTheme.typography.bodyMd))

            // [data-variant="block"] block-size: 64px
            QeetrixSkeletonVariant.Block -> Modifier.fillMaxWidth().height(BlockHeight)

            // [data-variant="circle"][data-size]: 24 / 32 / 40 / 48px
            QeetrixSkeletonVariant.Circle -> Modifier.size(size.diameter)
        }
    val shape =
        when (variant) {
            QeetrixSkeletonVariant.Text -> QeetrixShapes.Sm

            // radius.sm
            QeetrixSkeletonVariant.Block -> QeetrixShapes.Surface

            // radius.surface
            QeetrixSkeletonVariant.Circle -> QeetrixShapes.Full // radius.full
        }
    SkeletonShape(modifier.then(defaults), shape)
}

/**
 * Placeholder lines for a paragraph in [role] — `@qeetrix/react`'s Skeleton `variant="text" lines={n}`: [lines]
 * bars, each as tall as the role's font size and centred in its line, the last one 60 % wide. It takes the lines'
 * height exactly, so the text that replaces it moves nothing below; the bars follow the font scale, as the text will.
 *
 * ```
 * QeetrixSkeletonText(lines = 3)
 * ```
 *
 * @param lines How many lines; at least one is drawn.
 */
@Composable
public fun QeetrixSkeletonText(
    modifier: Modifier = Modifier,
    lines: Int = 1,
    role: QeetrixTextRole = QeetrixTextRole.BodyMd,
) {
    val count = lines.coerceAtLeast(1)
    val style = QeetrixTheme.typography[role]
    val line = firstLineHeight(style) // 1lh
    val bar = with(LocalDensity.current) { style.fontSize.toDp() } // 1em
    val half = ((line - bar) / 2).coerceAtLeast(0.dp)
    // .qx-skeleton-lines: gap 1lh − 1em; padding-block .5lh − .5em
    Column(
        modifier.fillMaxWidth().clearAndSetSemantics {}.padding(vertical = half),
        verticalArrangement = Arrangement.spacedBy(half * 2),
    ) {
        repeat(count) { index ->
            // :last-child:not(:first-child) inline-size: 60%
            val fraction = if (index == count - 1 && count > 1) LAST_LINE_FRACTION else 1f
            SkeletonShape(Modifier.fillMaxWidth(fraction).height(bar), QeetrixShapes.Sm)
        }
    }
}

/** One pulsing shape (`.qx-skeleton`): the fill, its corners, and the opacity pulse. */
@Composable
private fun SkeletonShape(
    modifier: Modifier,
    shape: Shape,
) {
    val pulse = rememberPulse()
    Box(
        modifier
            .clearAndSetSemantics {}
            .graphicsLayer { alpha = pulse?.value ?: 1f }
            .background(QeetrixTheme.colors.bgNeutralSubtlePressed, shape), // background: bg.neutral.subtle-pressed
    )
}

/**
 * The pulse's opacity — `qx-skeleton-pulse` to `opacity: .45`, `animation-duration: calc(4 × motion.duration.slower)`,
 * `motion.easing.standard`, infinite, alternate — or `null` under reduced motion, where the durations are 0 and it
 * stands still.
 */
@Composable
private fun rememberPulse(): State<Float>? {
    if (QeetrixTheme.reduceMotion) return null
    val transition = rememberInfiniteTransition(label = "qeetrix-skeleton")
    return transition.animateFloat(
        initialValue = 1f,
        targetValue = PULSE_OPACITY,
        animationSpec =
            infiniteRepeatable(
                tween(PULSE_STEPS * QeetrixMotion.DurationSlower, easing = QeetrixMotion.EasingStandard),
                RepeatMode.Reverse,
            ),
        label = "qeetrix-skeleton-pulse",
    )
}

/** `calc(4 * var(--qx-motion-duration-slower))`. */
private const val PULSE_STEPS = 4

/** `@keyframes qx-skeleton-pulse { to { opacity: .45 } }`. */
private const val PULSE_OPACITY = 0.45f

/** `.qx-skeleton-lines > .qx-skeleton:last-child:not(:first-child) { inline-size: 60% }`. */
private const val LAST_LINE_FRACTION = 0.6f

/** `.qx-skeleton[data-variant="block"] { block-size: 64px }`. */
private val BlockHeight = 64.dp
