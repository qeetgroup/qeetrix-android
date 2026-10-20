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
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The navigation rail: an app's top-level destinations in a column along the inline start of a medium or
 * expanded window — the web sidebar's icon rail, with labels. On a compact window use
 * [QeetrixNavigationBar]; [QeetrixNavigationScaffold] switches between the two as the window changes.
 *
 * - **The app owns navigation**, as for the bar: [selected] and [onSelect] carry the app's own values.
 * - **[header]** sits above the destinations — a primary action such as "Compose", or the product's mark.
 * - **Drawing.** The web sidebar's signature: `surface.rail` with a rule along its inline end; the selected
 *   item tinted, its icon in the brand colour, its label medium, and the 3 dp brand indicator on its inline
 *   start (`component.sidebar.indicator-width`). The rail is 80 dp wide, wider when a label needs the room
 *   at a large text size, and the destinations scroll when they do not fit the height.
 * - **Edge to edge.** [windowInsets] (by default the vertical and inline-start safe-drawing insets) keeps the
 *   items clear of the status bar, a display cutout and a navigation bar on the start side.
 * - **Accessibility.** One selectable group of tabs, each at least 48 dp tall, in reading order; keyboard
 *   and D-pad focus move through them with the arrow keys and Tab.
 *
 * ```
 * QeetrixNavigationRail(items, selected = destination, onSelect = { destination = it }) {
 *     QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = ::composeMessage, variant = QeetrixButtonVariant.Default)
 * }
 * ```
 */
@Composable
public fun <T> QeetrixNavigationRail(
    items: List<QeetrixNavigationItem<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = QeetrixNavigationRailInsets,
    header: (@Composable ColumnScope.() -> Unit)? = null,
) {
    requireDistinctValues(items)
    val rule = QeetrixTheme.colors.borderDefault // border-e border-sidebar-border
    Column(
        modifier
            .fillMaxHeight()
            .background(QeetrixTheme.colors.surfaceRail) // bg-sidebar
            .drawBehind {
                val x = if (layoutDirection == LayoutDirection.Rtl) 0f else size.width
                drawLine(rule, Offset(x, 0f), Offset(x, size.height), QeetrixStroke.Hairline.toPx())
            }.windowInsetsPadding(windowInsets)
            // 80 dp, or wider when a label needs it at a large text size: rail labels never truncate.
            .width(IntrinsicSize.Max)
            .widthIn(min = RailWidth)
            .verticalScroll(rememberScrollState())
            .padding(vertical = QeetrixWebScale.spacing(3f), horizontal = QeetrixWebScale.spacing(1f)),
        verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1f)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (header != null) {
            Column(
                Modifier.padding(bottom = QeetrixWebScale.spacing(3f)),
                verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = header,
            )
        }
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1f))) {
            for (item in items) {
                key(item.value) {
                    NavigationItem(
                        item = item,
                        selected = item.value == selected,
                        onClick = { onSelect(item.value) },
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

/** w-20: the rail's width at the default text size — a 24 dp icon and a 12 sp label. */
internal val RailWidth = QeetrixWebScale.spacing(20f)
