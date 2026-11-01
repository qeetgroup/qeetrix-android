package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.foundation.layout.QeetrixBreakpoint
import com.qeetgroup.qeetrix.internal.windowAtLeast

/** How [QeetrixNavigationScaffold] presents its destinations (`value` is the same string on every platform). */
public enum class QeetrixNavigationLayout(
    public val value: String,
) {
    /** A [QeetrixNavigationBar] along the bottom: a compact window. */
    Bar("bar"),

    /** A [QeetrixNavigationRail] along the inline start: a medium or wider window. */
    Rail("rail"),
    ;

    public companion object {
        /**
         * The layout for a window [width]: [Bar] below `breakpoint.medium` (600 dp), [Rail] from it — the compact and
         * medium width classes of Android's `WindowSizeClass`, and the web's `--qx-viewport-medium`. It is the
         * window's width that counts, not the kind of device: a phone in landscape, a tablet in split screen and a
         * resized desktop window all follow the same rule.
         */
        public fun forWindowWidth(width: Dp): QeetrixNavigationLayout = if (width >= QeetrixBreakpoint.Medium) Rail else Bar
    }
}

/**
 * A screen with its top-level destinations — `@qeetrix/react`'s AppShell frame (its navigation column and main
 * region) as Android adapts it: a [QeetrixNavigationBar] on a compact window and a [QeetrixNavigationRail] from
 * `breakpoint.medium` up, switching as the window is resized, folded or rotated. The web's top bar, panels and drawer
 * belong to consoles; a screen's own bar is [QeetrixTopAppBar], inside [content].
 *
 * - **Only presentation.** The app's [content] is the current destination; [value] and [onValueChange] are the
 *   app's, exactly as for the bar and the rail. Back, deep links and saved navigation state stay with the app's
 *   navigation library.
 * - **[layout]** fixes the presentation; by default it follows the window width
 *   ([QeetrixNavigationLayout.forWindowWidth]).
 * - **Content keeps its state** when the layout changes: it stays in one place in the composition, so its scroll
 *   positions and `remember`ed values survive a fold or a resize.
 * - **Insets.** The bar or the rail pads itself for the system bars. The content is told the inset of the edge the
 *   navigation covers is consumed — the bottom above a bar, the start beside a rail — so a
 *   `windowInsetsPadding(WindowInsets.safeDrawing)` inside it does not pad that edge twice; its other edges still meet
 *   the window's, and the content keeps clear of them itself, as [QeetrixTopAppBar] does by default.
 * - [railHeader] is the rail's [QeetrixNavigationRail] header; a bar has none.
 *
 * ```
 * QeetrixNavigationScaffold(items, value = destination, onValueChange = { destination = it }) {
 *     when (destination) {
 *         Destination.Home -> HomeScreen()
 *         Destination.Inbox -> InboxScreen()
 *         Destination.Settings -> SettingsScreen()
 *     }
 * }
 * ```
 */
@Composable
public fun <T> QeetrixNavigationScaffold(
    items: List<QeetrixNavigationItem<T>>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    layout: QeetrixNavigationLayout =
        if (windowAtLeast(
                QeetrixBreakpoint.Medium,
            )
        ) {
            QeetrixNavigationLayout.Rail
        } else {
            QeetrixNavigationLayout.Bar
        },
    railHeader: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val rail = layout == QeetrixNavigationLayout.Rail
    // Only the edge the navigation covers. The rail pads for the top and bottom too, but the content beside it still
    // runs under the status and navigation bars, as the content above a bar runs to the window's sides: consuming the
    // navigation's whole insets put a top app bar beside the rail under the status bar (found on an API 36 tablet).
    val insets = WindowInsets.safeDrawing.only(if (rail) WindowInsetsSides.Start else WindowInsetsSides.Bottom)
    Layout(
        contents =
            listOf(
                // The content first, always, so it keeps its place — and its state — when the layout changes.
                { Box(Modifier.consumeWindowInsets(insets)) { content() } },
                {
                    if (rail) {
                        QeetrixNavigationRail(items, value, onValueChange, header = railHeader)
                    } else {
                        QeetrixNavigationBar(items, value, onValueChange)
                    }
                },
            ),
        modifier = modifier,
    ) { (contentMeasurables, navigationMeasurables), constraints ->
        check(constraints.hasBoundedWidth && constraints.hasBoundedHeight) {
            "QeetrixNavigationScaffold is a whole screen: give it a bounded size, not a scrolling container."
        }
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        if (rail) {
            val navigation = navigationMeasurables.map { it.measure(loose.copy(minHeight = height)) }
            val railWidth = navigation.maxOfOrNull { it.width } ?: 0
            val body = contentMeasurables.map { it.measure(Constraints.fixed((width - railWidth).coerceAtLeast(0), height)) }
            layout(width, height) {
                navigation.forEach { it.placeRelative(0, 0) }
                body.forEach { it.placeRelative(railWidth, 0) }
            }
        } else {
            val navigation = navigationMeasurables.map { it.measure(loose.copy(minWidth = width)) }
            val barHeight = navigation.maxOfOrNull { it.height } ?: 0
            val body = contentMeasurables.map { it.measure(Constraints.fixed(width, (height - barHeight).coerceAtLeast(0))) }
            layout(width, height) {
                body.forEach { it.placeRelative(0, 0) }
                navigation.forEach { it.placeRelative(0, height - barHeight) }
            }
        }
    }
}
