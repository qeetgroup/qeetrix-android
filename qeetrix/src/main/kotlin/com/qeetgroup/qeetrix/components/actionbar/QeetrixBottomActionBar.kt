package com.qeetgroup.qeetrix.components.actionbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import com.qeetgroup.qeetrix.foundation.layout.QeetrixBreakpoint
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.windowAtLeast
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** How a phone's [QeetrixBottomActionBar] lays out its actions (`value` is the name shared with qeetrix-ios). */
public enum class QeetrixBottomActionBarArrangement(
    public val value: String,
) {
    /** The actions share the width equally — pass `fullWidth = true` so each fills its share. The default. */
    Equal("equal"),

    /**
     * The actions before the last — a ghost "Not now" — at their own width at the start, the last, the primary,
     * filling the rest.
     */
    PrimaryFills("primary-fills"),

    /** The last action, the primary, full width on top, the others centred under it at their own width. */
    Stacked("stacked"),
}

/**
 * A screen's main actions pinned to its bottom edge — Save, Continue, Approve and Deny — above the keyboard when it is
 * open and above the navigation bar or gesture area otherwise. `@qeetrix/react` has no counterpart: it is a dialog
 * footer's rules (design rules §13) on the screen's edge.
 *
 * - **At most one solid and one soft action** (design rules §7), the primary last. On a phone the bar shares its width
 *   equally between them — pass `fullWidth = true` so each fills its share — and stacks them, full width and in the
 *   same order, when a label wouldn't fit beside the other (at large text sizes). From `breakpoint.medium` the actions
 *   sit at the end at their own width, `fullWidth` or not, as long as they fit in a row; when they don't, they share
 *   the width or stack as on a phone.
 * - **[arrangement]** changes the phone's row: [QeetrixBottomActionBarArrangement.PrimaryFills] keeps a ghost action
 *   at its own width at the start and lets the primary fill the rest; [QeetrixBottomActionBarArrangement.Stacked] puts
 *   the primary full width over a centred ghost action. Where a label wouldn't fit, both stack full width as above.
 * - **Look**: `color.surface.default`, `space.inset.lg` round the actions, `space.gap.sm` between them. A 1 dp
 *   `color.border.subtle` rule along the top appears only while content scrolls under the bar — pass the screen's
 *   [scroll] state — or always with [ruled].
 * - **Insets**: it pads itself for the keyboard (`WindowInsets.ime`) and the bottom and horizontal safe-drawing insets,
 *   so place it at the bottom of an edge-to-edge screen, after the scrolling content.
 *
 * ```
 * Column(Modifier.fillMaxSize()) {
 *     Column(Modifier.weight(1f).verticalScroll(scroll)) { /* the request */ }
 *     QeetrixBottomActionBar(scroll = scroll) {
 *         QeetrixButton("Deny", onClick = ::deny, variant = QeetrixButtonVariant.Soft, fullWidth = true)
 *         QeetrixButton("Approve", onClick = ::approve, fullWidth = true)
 *     }
 * }
 * ```
 */
@Composable
public fun QeetrixBottomActionBar(
    modifier: Modifier = Modifier,
    scroll: ScrollState? = null,
    ruled: Boolean = false,
    arrangement: QeetrixBottomActionBarArrangement = QeetrixBottomActionBarArrangement.Equal,
    content: @Composable () -> Unit,
) {
    val colors = QeetrixTheme.colors
    val rule = colors.borderSubtle
    val wide = windowAtLeast(QeetrixBreakpoint.Medium)
    Layout(
        content,
        modifier
            .fillMaxWidth()
            .background(colors.surfaceDefault)
            .drawBehind {
                // The hairline shows while there is content below the visible part of the screen, under the bar; the
                // scroll position is read while drawing, so scrolling never recomposes the bar.
                val under = ruled || (scroll != null && scroll.value < scroll.maxValue)
                if (!under) return@drawBehind
                drawRect(rule, Offset.Zero, Size(size.width, QeetrixBorder.WidthDefault.toPx()))
            }.windowInsetsPadding(
                WindowInsets.ime.union(WindowInsets.safeDrawing).only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
            ).padding(QeetrixSpace.InsetLg),
    ) { measurables, constraints ->
        val gap = QeetrixSpace.GapSm.roundToPx()
        val width = constraints.maxWidth
        if (measurables.isEmpty()) return@Layout layout(width, 0) {}
        // Each action's own width — a fullWidth button's content, not the bar — capped at the bar's.
        val own = if (wide) measurables.map { it.maxIntrinsicWidth(constraints.maxHeight).coerceAtMost(width) } else null
        if (own != null && own.sum() + gap * (own.size - 1) <= width) {
            // End-aligned, each action at its own width.
            val placeables = measurables.mapIndexed { index, measurable -> measurable.measure(Constraints.fixedWidth(own[index])) }
            val height = placeables.maxOf { it.height }
            layout(width, height) {
                var x = width - (placeables.sumOf { it.width } + gap * (placeables.size - 1))
                for (placeable in placeables) {
                    placeable.placeRelative(x, (height - placeable.height) / 2)
                    x += placeable.width + gap
                }
            }
        } else {
            val tallest = constraints.maxHeight
            val ownWidths = measurables.map { it.maxIntrinsicWidth(tallest).coerceAtMost(width) }
            val others = ownWidths.dropLast(1)
            val rest = width - others.sum() - gap * others.size
            when {
                arrangement == QeetrixBottomActionBarArrangement.Stacked -> {
                    // The primary (last) full width on top, the others centred under it at their own width.
                    val primary = measurables.last().measure(Constraints.fixedWidth(width))
                    val below =
                        measurables.dropLast(1).mapIndexed {
                            index,
                            measurable,
                            ->
                            measurable.measure(Constraints.fixedWidth(others[index]))
                        }
                    val height = primary.height + below.sumOf { it.height + gap }
                    layout(width, height) {
                        primary.placeRelative(0, 0)
                        var y = primary.height + gap
                        for (placeable in below) {
                            placeable.placeRelative((width - placeable.width) / 2, y)
                            y += placeable.height + gap
                        }
                    }
                }

                arrangement == QeetrixBottomActionBarArrangement.PrimaryFills &&
                    rest >= measurables.last().maxIntrinsicWidth(tallest) -> {
                    // The others at their own width from the start, the primary filling what they leave.
                    val placeables =
                        measurables.mapIndexed { index, measurable ->
                            measurable.measure(Constraints.fixedWidth(if (index < others.size) others[index] else rest))
                        }
                    val height = placeables.maxOf { it.height }
                    layout(width, height) {
                        var x = 0
                        for (placeable in placeables) {
                            placeable.placeRelative(x, (height - placeable.height) / 2)
                            x += placeable.width + gap
                        }
                    }
                }

                arrangement == QeetrixBottomActionBarArrangement.Equal &&
                    measurables.all { it.maxIntrinsicWidth(tallest) <= (width - gap * (measurables.size - 1)) / measurables.size } -> {
                    val share = (width - gap * (measurables.size - 1)) / measurables.size
                    val placeables = measurables.map { it.measure(Constraints.fixedWidth(share)) }
                    val height = placeables.maxOf { it.height }
                    layout(width, height) {
                        placeables.forEachIndexed { index, placeable ->
                            placeable.placeRelative(
                                index * (share + gap),
                                (height - placeable.height) / 2,
                            )
                        }
                    }
                }

                else -> {
                    // Stacked, full width, in the order given.
                    val placeables = measurables.map { it.measure(Constraints.fixedWidth(width)) }
                    val height = placeables.sumOf { it.height } + gap * (placeables.size - 1)
                    layout(width, height) {
                        var y = 0
                        for (placeable in placeables) {
                            placeable.placeRelative(0, y)
                            y += placeable.height + gap
                        }
                    }
                }
            }
        }
    }
}

/** 0.1.0's signature without `arrangement`, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun QeetrixBottomActionBar(
    modifier: Modifier = Modifier,
    scroll: ScrollState? = null,
    ruled: Boolean = false,
    content: @Composable () -> Unit,
): Unit = QeetrixBottomActionBar(modifier, scroll, ruled, QeetrixBottomActionBarArrangement.Equal, content)
