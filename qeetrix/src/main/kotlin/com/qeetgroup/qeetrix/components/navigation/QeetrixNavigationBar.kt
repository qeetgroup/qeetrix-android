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
import androidx.compose.ui.geometry.Size
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The bottom navigation bar: an app's three to five top-level destinations, side by side along the bottom of a
 * compact window. For a wider window use [QeetrixNavigationRail], or [QeetrixNavigationScaffold] to switch between
 * them as the window changes.
 *
 * `@qeetrix/react` has no bottom bar: below 840 px its AppShell opens the Sidebar as a drawer. This is Android's
 * bottom bar drawn as the AppShell's navigation column — `color.surface.canvas`, a 1 dp `color.border.default` seam
 * on the edge that meets the screen — with the Sidebar's items stacked into cells ([QeetrixNavigationItem]): the
 * current one tinted `color.bg.neutral.subtle-hover`, its round icon Ember and filled, its label at weight 600, and
 * the 3 dp Ember bar along its top edge, the one facing the screen.
 *
 * - **The app owns navigation.** [value] is the app's current destination and [onValueChange] is called with the
 *   value of the item chosen — also when it is the current one, so the app can return to the top of that
 *   destination. Qeetrix keeps no back stack and knows no routes; it works with Navigation Compose, Navigation 3 or
 *   plain state alike, and system Back stays the app's.
 * - **Not tabs.** Destinations here are the app's top level, each with its own history; views of one screen's
 *   content are [com.qeetgroup.qeetrix.components.tabs.QeetrixTabs].
 * - Items share the width equally; at large text sizes a label that does not fit ends in an ellipsis
 *   (accessibility services read it in full) and the bar grows taller with the text.
 * - **Edge to edge.** The surface runs under the system navigation bar; [windowInsets] (by default the bottom and
 *   horizontal safe-drawing insets) keeps the items clear of it.
 * - **Accessibility.** One selectable group; each item is a tab, read with its label, its badge and whether it is
 *   selected, and is at least 48 dp tall.
 *
 * ```
 * QeetrixNavigationBar(
 *     items = listOf(
 *         QeetrixNavigationItem(Destination.Home, "Home", QeetrixIconName.House),
 *         QeetrixNavigationItem(Destination.Inbox, "Inbox", QeetrixIconName.Inbox, badge = "3"),
 *         QeetrixNavigationItem(Destination.Settings, "Settings", QeetrixIconName.Settings),
 *     ),
 *     value = destination,
 *     onValueChange = { destination = it },
 * )
 * ```
 */
@Composable
public fun <T> QeetrixNavigationBar(
    items: List<QeetrixNavigationItem<T>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = QeetrixNavigationBarInsets,
) {
    requireDistinctValues(items)
    val seam = QeetrixTheme.colors.borderDefault
    Row(
        modifier
            .fillMaxWidth()
            .background(QeetrixTheme.colors.surfaceCanvas) // .qx-app-shell-nav-panel background: color.surface.canvas
            .drawBehind {
                // The seam: border.width.default solid color.border.default, on the edge that meets the screen.
                drawRect(seam, Offset.Zero, Size(size.width, QeetrixBorder.WidthDefault.toPx()))
            }.windowInsetsPadding(windowInsets)
            .padding(top = QeetrixBorder.WidthDefault)
            .height(IntrinsicSize.Min)
            .selectableGroup(),
    ) {
        for (item in items) {
            key(item.value) {
                NavigationItem(
                    item = item,
                    current = item.value == value,
                    onClick = { onValueChange(item.value) },
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
