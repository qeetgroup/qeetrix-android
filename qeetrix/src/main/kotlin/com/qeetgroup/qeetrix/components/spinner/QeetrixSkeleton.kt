package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.internal.atAlpha
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSkeletonComponentTokens

/** The outline of a [QeetrixSkeleton] (`value` is qeetrix-ios's name; the web sets it with a class). */
public enum class QeetrixSkeletonShape(
    public val value: String,
) {
    /** A block with the chip corner (`rounded-md`): a thumbnail, a button, a line of text. The default. */
    Rectangle("rectangle"),

    /** A circle: an avatar. */
    Circle("circle"),

    /** A pill (`rounded-full`): a badge or a chip. */
    Capsule("capsule"),
}

/**
 * A placeholder for content that is loading — the web's `Skeleton`. Size it with [modifier], and compose
 * several in the shape of what will arrive.
 *
 * ```
 * Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
 *     QeetrixSkeleton(Modifier.size(40.dp), QeetrixSkeletonShape.Circle)
 *     QeetrixSkeletonText(lines = 2, modifier = Modifier.weight(1f))
 * }
 * ```
 *
 * Its colour is a translucent tint of the foreground (`component.skeleton.background`), so it keeps the
 * same weight on the canvas, a card or a sheet, in both themes. A highlight sweeps across it every 1.6 s,
 * as on the web; with animations off it stands still. Only drawing changes as it sweeps: nothing is
 * recomposed.
 *
 * A skeleton has nothing to say to accessibility services, and they don't see it. Say that the region is
 * loading where it is — [com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState] reads its loading
 * branch as one "Loading" element — rather than letting each placeholder speak.
 */
@Composable
public fun QeetrixSkeleton(
    modifier: Modifier = Modifier,
    shape: QeetrixSkeletonShape = QeetrixSkeletonShape.Rectangle,
) {
    val outline =
        when (shape) {
            QeetrixSkeletonShape.Rectangle -> RoundedCornerShape(QeetrixCorner.Chip)

            // rounded-md
            QeetrixSkeletonShape.Circle -> CircleShape

            QeetrixSkeletonShape.Capsule -> RoundedCornerShape(percent = 50) // rounded-full
        }
    val fill = QeetrixSkeletonComponentTokens.Background // bg-(--qx-component-skeleton-background)
    // A highlight of the foreground at 8 %, swept from -100 % to 100 % of the width.
    val highlight = QeetrixTheme.colors.textPrimary.atAlpha(SHIMMER_ALPHA)
    val sweep = rememberShimmer()
    Box(
        modifier
            .clearAndSetSemantics {}
            .clip(outline)
            .drawWithCache {
                // One gradient per size and colour, moved each frame: the sweep allocates nothing per frame.
                val band = Brush.horizontalGradient(listOf(Color.Transparent, highlight, Color.Transparent), 0f, size.width)
                onDrawBehind {
                    drawRect(fill)
                    val progress = sweep?.value ?: return@onDrawBehind
                    translate(left = size.width * progress) { drawRect(band) }
                }
            },
    )
}

/**
 * Placeholder lines for a paragraph, each the height of a line of text in [role] with that role's spacing,
 * the last one shorter — so the skeleton takes the room the text will. The lines follow the font scale, as
 * the text will.
 *
 * ```
 * QeetrixSkeletonText(lines = 3, role = QeetrixTextRole.Body)
 * ```
 */
@Composable
public fun QeetrixSkeletonText(
    modifier: Modifier = Modifier,
    lines: Int = 1,
    role: QeetrixTextRole = QeetrixTextRole.Body,
) {
    val count = lines.coerceAtLeast(1)
    val spec = role.spec
    val bar = with(LocalDensity.current) { spec.size.sp.toDp() }
    // A bar the height of the font's size; the rest of the line height is the gap to the next.
    val gap = bar * (spec.lineHeight - 1f).coerceAtLeast(0f)
    Column(modifier.fillMaxWidth().clearAndSetSemantics {}, verticalArrangement = Arrangement.spacedBy(gap)) {
        repeat(count) { line ->
            // The last line of a paragraph rarely runs the full width.
            val fraction = if (line == count - 1 && count > 1) LAST_LINE_FRACTION else 1f
            QeetrixSkeleton(Modifier.fillMaxWidth(fraction).height(bar))
        }
    }
}

/**
 * The shimmer's position, from -1 to 1 of the width — `qx-shimmer 1.6s var(--ease-standard) infinite` — or
 * `null` with animations off, when the web's reduced-motion rule stops it.
 */
@Composable
private fun rememberShimmer(): State<Float>? {
    if (QeetrixTheme.reduceMotion) return null
    val transition = rememberInfiniteTransition(label = "qeetrix-shimmer")
    return transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(SHIMMER_MILLIS, easing = QeetrixEasing.Standard), RepeatMode.Restart),
        label = "qeetrix-shimmer-position",
    )
}

private const val SHIMMER_MILLIS = 1600
private const val SHIMMER_ALPHA = 0.08f
private const val LAST_LINE_FRACTION = 0.6f
