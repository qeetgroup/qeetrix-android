package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** How [QeetrixNavigationScaffold] presents its destinations (`value` is the same string on every platform). */
public enum class QeetrixNavigationLayout(
    public val value: String,
) {
    /** A [QeetrixNavigationBar] along the bottom: a compact window. */
    Bar("bar"),

    /** A [QeetrixNavigationRail] along the inline start: a medium or expanded window. */
    Rail("rail"),
    ;

    public companion object {
        /**
         * The layout for a window [width]: [Bar] below 600 dp, [Rail] from 600 dp — the compact and medium
         * width classes of Android's `WindowSizeClass` (`WIDTH_DP_MEDIUM_LOWER_BOUND`). It is the window's
         * width that counts, not the kind of device: a phone in landscape, a tablet in split screen and a
         * resized desktop window all follow the same rule.
         */
        public fun forWindowWidth(width: Dp): QeetrixNavigationLayout = if (width >= MediumWidth) Rail else Bar
    }
}

/**
 * A screen with its top-level destinations — a [QeetrixNavigationBar] on a compact window and a
 * [QeetrixNavigationRail] from the medium width up, switching as the window is resized, folded or rotated.
 *
 * - **Only presentation.** The app's [content] is the current destination; [selected] and [onSelect] are
 *   the app's, exactly as for the bar and the rail. Back, deep links and saved navigation state stay with
 *   the app's navigation library.
 * - **[layout]** fixes the presentation; by default it follows the window width
 *   ([QeetrixNavigationLayout.forWindowWidth]).
 * - **Content keeps its state** when the layout changes: it stays in one place in the composition, so its
 *   scroll positions and `remember`ed values survive a fold or a resize.
 * - **Insets.** The bar or the rail pads itself for the system bars; the content is told those insets are
 *   consumed, so a `windowInsetsPadding(WindowInsets.safeDrawing)` inside it does not pad twice.
 * - [railHeader] is the rail's [QeetrixNavigationRail] header; a bar has none.
 *
 * This is a small adapter over the bar and the rail rather than Material's `NavigationSuiteScaffold`, which
 * draws Material's navigation components (Documentation/Decisions/0015-navigation-overlays-and-feedback.md).
 *
 * ```
 * QeetrixNavigationScaffold(items, selected = destination, onSelect = { destination = it }) {
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
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    layout: QeetrixNavigationLayout = QeetrixNavigationLayout.forWindowWidth(windowWidth()),
    railHeader: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val rail = layout == QeetrixNavigationLayout.Rail
    val insets = if (rail) QeetrixNavigationRailInsets else QeetrixNavigationBarInsets
    Layout(
        contents =
            listOf(
                // The content first, always, so it keeps its place — and its state — when the layout changes.
                { Box(Modifier.consumeWindowInsets(insets)) { content() } },
                {
                    if (rail) {
                        QeetrixNavigationRail(items, selected, onSelect, header = railHeader)
                    } else {
                        QeetrixNavigationBar(items, selected, onSelect)
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

/** The current window's width. */
@Composable
@ReadOnlyComposable
private fun windowWidth(): Dp = LocalWindowInfo.current.containerDpSize.width

/** `WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND`: where a compact window ends. */
private val MediumWidth = 600.dp
