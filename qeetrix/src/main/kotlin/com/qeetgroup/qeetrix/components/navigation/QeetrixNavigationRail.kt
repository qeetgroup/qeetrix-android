package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The navigation rail: an app's top-level destinations in a column along the inline start of a medium or expanded
 * window. On a compact window use [QeetrixNavigationBar]; [QeetrixNavigationScaffold] switches between the two as the
 * window changes.
 *
 * Its `@qeetrix/react` counterpart is the AppShell's navigation column with its Sidebar: `color.surface.canvas`, a
 * 1 dp `color.border.default` seam on its inline end, and rows that run edge to edge. The current row is tinted
 * `color.bg.neutral.subtle-hover`, its round icon Ember and filled, its label at weight 600, with the 3 dp Ember bar
 * on its start edge (design rules §11). The web's collapsed rail shows icons alone and puts the labels in tooltips;
 * Android's rail keeps each label under its icon, as touch has no hover to show a tooltip, and is 80 dp wide —
 * wider when a label needs the room at a large text size. The destinations scroll when they do not fit the height.
 *
 * - **The app owns navigation**, as for the bar: [value] and [onValueChange] carry the app's own values.
 * - **[header]** sits above the destinations — a primary action such as "Compose", or the product's mark.
 * - **Edge to edge.** [windowInsets] (by default the vertical and inline-start safe-drawing insets) keeps the items
 *   clear of the status bar, a display cutout and a navigation bar on the start side.
 * - **Accessibility.** One selectable group of tabs, each at least 48 dp tall, in reading order; keyboard and D-pad
 *   focus move through them with the arrow keys and Tab.
 *
 * ```
 * QeetrixNavigationRail(items, value = destination, onValueChange = { destination = it }) {
 *     QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = ::composeMessage, variant = QeetrixButtonVariant.Solid)
 * }
 * ```
 */
@Composable
public fun <T> QeetrixNavigationRail(
    items: List<QeetrixNavigationItem<T>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = QeetrixNavigationRailInsets,
    header: (@Composable ColumnScope.() -> Unit)? = null,
) {
    requireDistinctValues(items)
    val seam = QeetrixTheme.colors.borderDefault
    Column(
        modifier
            .fillMaxHeight()
            .background(QeetrixTheme.colors.surfaceCanvas) // .qx-app-shell-nav-panel background: color.surface.canvas
            .drawBehind {
                // border-inline-end: border.width.default solid color.border.default
                val width = QeetrixBorder.WidthDefault.toPx()
                val x = if (layoutDirection == LayoutDirection.Rtl) 0f else size.width - width
                drawRect(seam, Offset(x, 0f), Size(width, size.height))
            }.windowInsetsPadding(windowInsets)
            .padding(end = QeetrixBorder.WidthDefault)
            // 80 dp, or wider when a label needs it at a large text size: rail labels never truncate.
            .width(IntrinsicSize.Max)
            .widthIn(min = RailWidth)
            .verticalScroll(rememberScrollState())
            // .qx-sidebar-nav padding-block: space.gap.sm space.inset.md
            .padding(top = QeetrixSpace.GapSm, bottom = QeetrixSpace.InsetMd),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (header != null) {
            Column(
                Modifier.padding(start = QeetrixSpace.InsetXs, end = QeetrixSpace.InsetXs, bottom = QeetrixSpace.InsetMd),
                verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = header,
            )
        }
        Column(Modifier.fillMaxWidth().selectableGroup()) {
            for (item in items) {
                key(item.value) {
                    NavigationItem(
                        item = item,
                        current = item.value == value,
                        onClick = { onValueChange(item.value) },
                        edge = IndicatorEdge.Start,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

/** The vertical and inline-start safe-drawing insets: what a rail keeps its items clear of. */
internal val QeetrixNavigationRailInsets: WindowInsets
    @Composable get() = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)

/** The rail's width at the default text size: Android's navigation rail (a 20 dp icon over a `label.sm` label). */
internal val RailWidth = 80.dp
