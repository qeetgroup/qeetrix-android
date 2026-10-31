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

/**
 * A screen's main actions pinned to its bottom edge — Save, Continue, Approve and Deny — above the keyboard when it is
 * open and above the navigation bar or gesture area otherwise. `@qeetrix/react` has no counterpart: it is a dialog
 * footer's rules (design rules §13) on the screen's edge.
 *
 * - **At most one solid and one soft action** (design rules §7), the primary last. On a phone the bar shares its width
 *   equally between them — pass `fullWidth = true` so each fills its share — and stacks them, full width and in the
 *   same order, when a label wouldn't fit beside the other (at large text sizes). From `breakpoint.medium` the actions
 *   sit at the end, at their own width.
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
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        if (measurables.isEmpty()) return@Layout layout(width, 0) {}
        if (wide) {
            // End-aligned, each action at its own width.
            val placeables = measurables.map { it.measure(loose) }
            val height = placeables.maxOf { it.height }
            layout(width, height) {
                var x = width - (placeables.sumOf { it.width } + gap * (placeables.size - 1))
                for (placeable in placeables) {
                    placeable.placeRelative(x, (height - placeable.height) / 2)
                    x += placeable.width + gap
                }
            }
        } else {
            val share = (width - gap * (measurables.size - 1)) / measurables.size
            val fits = measurables.all { it.maxIntrinsicWidth(constraints.maxHeight) <= share }
            if (fits) {
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
            } else {
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
