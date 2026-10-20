package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The bottom navigation bar: an app's three to five top-level destinations, side by side along the bottom
 * of a compact window. For a wider window use [QeetrixNavigationRail], or [QeetrixNavigationScaffold] to
 * switch between them as the window changes.
 *
 * - **The app owns navigation.** [selected] is the app's current destination and [onSelect] is called with
 *   the value of the item chosen — also when it is the current one, so the app can return to the top of
 *   that destination. Qeetrix keeps no back stack and knows no routes; it works with Navigation Compose,
 *   Navigation 3 or plain state alike.
 * - **Not tabs.** Destinations here are the app's top level, each with its own history; switching
 *   between views of one screen's content is [com.qeetgroup.qeetrix.components.tabs.QeetrixTabs].
 * - **Drawing.** The web sidebar's navigation signature, turned horizontal: the app-chrome surface
 *   (`surface.rail`) with a rule along its top; the selected item tinted, its icon in the brand colour, its
 *   label medium, and a 3 dp brand indicator on its top edge. Items share the width equally; at large text
 *   sizes a label that does not fit ends in an ellipsis (accessibility services read it in full) and the bar
 *   grows taller with the text.
 * - **Edge to edge.** The surface runs under the system navigation bar; [windowInsets] (by default the
 *   bottom and horizontal safe-drawing insets) keeps the items clear of it.
 * - **Accessibility.** One selectable group; each item is a tab, read with its label, its badge and
 *   whether it is selected, and is at least 48 dp tall.
 *
 * ```
 * QeetrixNavigationBar(
 *     items = listOf(
 *         QeetrixNavigationItem(Destination.Home, "Home", QeetrixIconName.House),
 *         QeetrixNavigationItem(Destination.Inbox, "Inbox", QeetrixIconName.Inbox, badge = "3"),
 *         QeetrixNavigationItem(Destination.Settings, "Settings", QeetrixIconName.Settings),
 *     ),
 *     selected = destination,
 *     onSelect = { destination = it },
 * )
 * ```
 */
@Composable
public fun <T> QeetrixNavigationBar(
    items: List<QeetrixNavigationItem<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = QeetrixNavigationBarInsets,
) {
    requireDistinctValues(items)
    val rule = QeetrixTheme.colors.borderDefault // border-sidebar-border
    Row(
        modifier
            .fillMaxWidth()
            .background(QeetrixTheme.colors.surfaceRail) // bg-sidebar
            .drawBehind { drawLine(rule, Offset(0f, 0f), Offset(size.width, 0f), QeetrixStroke.Hairline.toPx()) }
            .windowInsetsPadding(windowInsets)
            .padding(horizontal = QeetrixWebScale.spacing(1f))
            .height(IntrinsicSize.Min)
            .selectableGroup(),
    ) {
        for (item in items) {
            key(item.value) {
                NavigationItem(
                    item = item,
                    selected = item.value == selected,
                    onClick = { onSelect(item.value) },
                    edge = IndicatorEdge.Top,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

/** The bottom and horizontal safe-drawing insets: what a bottom bar keeps its items clear of. */
internal val QeetrixNavigationBarInsets: WindowInsets
    @Composable get() = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)

/** Values must be distinct, as each is one destination. */
internal fun requireDistinctValues(items: List<QeetrixNavigationItem<*>>) {
    require(items.map { it.value }.toSet().size == items.size) { "Each navigation item needs a distinct value." }
}
