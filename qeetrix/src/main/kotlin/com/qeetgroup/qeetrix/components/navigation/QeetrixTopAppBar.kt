package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.foundation.format.QeetrixFormat
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlin.math.roundToInt

/**
 * The bar at the top of a screen — `@qeetrix/react`'s PageHeader, the head of a page, as a phone's app bar: the
 * screen's [title] (`title.lg`, a heading), an optional [description] (one line, `body.md` in `color.text.subtle`),
 * an optional [navigationIcon] at the start — usually [QeetrixBackButton], Android's place for PageHeaderBack — and
 * [actions] at the end, usually neutral [com.qeetgroup.qeetrix.components.button.QeetrixIconButton]s and a
 * [com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu] for overflow: at most one solid and one soft button,
 * the rest in the menu (design rules §7).
 *
 * - **Drawing.** PageHeader's band: `color.surface.default`, ending on a 1 dp `color.border.default` rule, the
 *   phone's 16 dp gutter (`space.inset.lg`). At least `size.control.lg` + 16 dp tall (56 dp at the default density),
 *   growing with the text size; the title and description keep to one line each and end in an ellipsis, and
 *   accessibility services read them in full.
 * - **Presentation only.** The bar draws what it is given; it keeps no navigation state and adds no back button by
 *   itself. System Back keeps working whether or not it shows one — the app's navigation handles it.
 * - **Edge to edge.** [windowInsets] (by default the top and horizontal safe-drawing insets) keeps the content clear
 *   of the status bar and display cutouts while the surface runs under them.
 *
 * - **Count, status and tabs** (PageHeader's, QTRX-196): [count] beside the title in its size at `body.md`'s weight,
 *   `color.text.subtlest`, formatted for the locale ("Members 412"); [status] after it, usually a
 *   [com.qeetgroup.qeetrix.components.badge.QeetrixBadge]; [tabs] under the bar, a
 *   [com.qeetgroup.qeetrix.components.tabs.QeetrixTabs] edge to edge.
 * - **A large title** ([size] `Medium` or `Large`, QTRX-137): the title in `heading.sm` or `heading.md` under the bar,
 *   with the description, collapsing into the bar as the content scrolls up when [scrollState] is given
 *   ([rememberQeetrixTopAppBarScrollState]); the bar's own title fades in as it goes.
 *
 * The console TopBar (product, scope switchers, search, account, on black) has no Android counterpart here, nor
 * PageHeader's breadcrumb (it stays off phones).
 *
 * ```
 * QeetrixTopAppBar(
 *     title = "Invoice INV-2041",
 *     description = "Draft",
 *     navigationIcon = { QeetrixBackButton(onClick = navigateUp) },
 *     actions = { QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = ::share, tone = QeetrixButtonTone.Neutral) },
 * )
 * ```
 */
@Composable
public fun QeetrixTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
    count: Int? = null,
    status: (@Composable () -> Unit)? = null,
    tabs: (@Composable () -> Unit)? = null,
    size: QeetrixTopAppBarSize = QeetrixTopAppBarSize.Small,
    scrollState: QeetrixTopAppBarScrollState? = null,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val rule = colors.borderDefault
    val locale = LocalConfiguration.current.locales[0]
    val counted = count?.let { remember(it, locale) { QeetrixFormat.number(it, locale) } }
    val large = size != QeetrixTopAppBarSize.Small
    // How far the large title has collapsed into the bar: 0 open, 1 gone (only with a scroll state).
    val collapsed = if (large) scrollState?.collapsedFraction ?: 0f else 1f
    Column(
        modifier
            .fillMaxWidth()
            .background(colors.surfaceDefault) // .qx-page-header background: color.surface.default
            .drawBehind {
                // border-block-end: border.width.default solid color.border.default
                val height = QeetrixBorder.WidthDefault.toPx()
                drawRect(rule, Offset(0f, this.size.height - height), Size(this.size.width, height))
            }.windowInsetsPadding(windowInsets)
            .padding(bottom = QeetrixBorder.WidthDefault),
    ) {
        Row(
            Modifier
                // .qx-top-bar block-size: calc(size.control.lg + 16px), the app chrome's height
                .heightIn(min = QeetrixTheme.density.sizeControlLg + BAR_EXTRA)
                .padding(
                    // --qx-page-gutter below 840 px: space.inset.lg; beside a 48 dp button, the 8 dp of its touch area
                    // round a 32 dp square already stand in for most of it.
                    start = if (navigationIcon != null) QeetrixSpace.InsetXs else QeetrixSpace.InsetLg,
                    end = if (actions != null) QeetrixSpace.InsetXs else QeetrixSpace.InsetLg,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navigationIcon?.invoke()
            // .qx-page-header-titles: a column, space.gap.xs apart; one heading with its description. With a large
            // title, the bar's own title fades in as the large one collapses.
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = if (navigationIcon != null) QeetrixSpace.InsetXs else 0.dp, end = QeetrixSpace.InsetXs)
                    .padding(vertical = QeetrixSpace.GapSm)
                    .graphicsLayer { alpha = if (large) ((collapsed - 0.5f) * 2f).coerceIn(0f, 1f) else 1f }
                    .then(
                        if (large &&
                            collapsed < 0.5f
                        ) {
                            Modifier.clearAndSetSemantics {}
                        } else {
                            Modifier.semantics(mergeDescendants = true) { heading() }
                        },
                    ),
                verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
            ) {
                TitleRow(title, counted, status, typography.titleLg)
                if (description != null && !large) {
                    BasicText(
                        description,
                        // .qx-page-header-description
                        style = typography.bodyMd.copy(color = colors.textSubtle).labelDirection(alignToLayout = true),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            // .qx-page-header-actions: centred; their 48 dp touch areas space the buttons.
            if (actions !=
                null
            ) {
                Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically, content = actions)
            }
        }
        if (large) {
            // The large title: heading.sm (Medium) or heading.md (Large) under the bar, collapsing into it on scroll.
            val role = if (size == QeetrixTopAppBarSize.Large) typography.headingMd else typography.headingSm
            Layout(
                content = {
                    Column(
                        Modifier
                            .padding(start = QeetrixSpace.InsetLg, end = QeetrixSpace.InsetLg, bottom = QeetrixSpace.InsetMd)
                            .then(
                                if (collapsed <
                                    0.5f
                                ) {
                                    Modifier.semantics(mergeDescendants = true) { heading() }
                                } else {
                                    Modifier.clearAndSetSemantics {}
                                },
                            ),
                        verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
                    ) {
                        TitleRow(title, counted, status, role, wrap = true)
                        if (description !=
                            null
                        ) {
                            BasicText(
                                description,
                                style = typography.bodyMd.copy(color = colors.textSubtle).labelDirection(alignToLayout = true),
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().clipToBounds(),
            ) { measurables, constraints ->
                val placeable = measurables.single().measure(constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity))
                scrollState?.heightOffsetLimit = -placeable.height.toFloat()
                val shown = (placeable.height + (scrollState?.heightOffset ?: 0f)).roundToInt().coerceIn(0, placeable.height)
                layout(placeable.width, shown) { placeable.placeRelative(0, shown - placeable.height) }
            }
        }
        if (tabs != null) {
            // .qx-page-header-tabs: margin-block-start space.gap.md, edge to edge.
            Box(Modifier.fillMaxWidth().padding(top = if (large) 0.dp else QeetrixSpace.GapXs)) { tabs() }
        }
    }
}

/** The title with its count (`.qx-page-header-count`) and status, wrapping as `.qx-page-header-title` does. */
@Composable
private fun TitleRow(
    title: String,
    count: String?,
    status: (@Composable () -> Unit)?,
    style: androidx.compose.ui.text.TextStyle,
    wrap: Boolean = false,
) {
    val colors = QeetrixTheme.colors
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
        verticalArrangement = Arrangement.Center,
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            title,
            style = style.copy(color = colors.textDefault).labelDirection(alignToLayout = true), // <Heading size="title-lg">
            maxLines = if (wrap) Int.MAX_VALUE else 1,
            overflow = if (wrap) TextOverflow.Clip else TextOverflow.Ellipsis,
        )
        if (count != null) {
            // .qx-page-header-count: the title's size at body.md's weight, color.text.subtlest, tabular-nums
            BasicText(
                count,
                style =
                    style.copy(
                        color = colors.textSubtlest,
                        fontWeight = FontWeight(QeetrixTextRole.BodyMd.spec.weight),
                        fontFeatureSettings = "tnum",
                    ),
            )
        }
        status?.invoke()
    }
}

/** How tall a [QeetrixTopAppBar] is (`value` is the name shared with qeetrix-ios). */
public enum class QeetrixTopAppBarSize(
    public val value: String,
) {
    /** One bar, the title in `title.lg`. The default. */
    Small("small"),

    /** A large title in `heading.sm` under the bar, collapsing into it on scroll. */
    Medium("medium"),

    /** A large title in `heading.md` under the bar, collapsing into it on scroll — a top-level screen's. */
    Large("large"),
}

/**
 * How far a [QeetrixTopAppBar]'s large title has collapsed, driven by the screen's scrolling: give
 * [nestedScrollConnection] to the scrolling content (`Modifier.nestedScroll(state.nestedScrollConnection)`). Scrolling
 * the content up collapses the title first; pulling down at the top opens it again.
 */
@androidx.compose.runtime.Stable
public class QeetrixTopAppBarScrollState internal constructor() {
    internal var heightOffsetLimit by mutableFloatStateOf(-Float.MAX_VALUE)
    internal var heightOffset by mutableFloatStateOf(0f)

    /** 0 while the large title is fully shown, 1 once it has collapsed into the bar. */
    public val collapsedFraction: Float
        get() =
            if (heightOffsetLimit == 0f ||
                heightOffsetLimit == -Float.MAX_VALUE
            ) {
                0f
            } else {
                (heightOffset / heightOffsetLimit).coerceIn(0f, 1f)
            }

    /** Feeds the content's scrolling to the large title. */
    public val nestedScrollConnection: NestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                // Collapse before the content scrolls up.
                if (available.y >= 0f) return Offset.Zero
                val before = heightOffset
                heightOffset = (heightOffset + available.y).coerceIn(heightOffsetLimit, 0f)
                return Offset(0f, heightOffset - before)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                // Open again once the content has scrolled back to its top.
                if (available.y <= 0f) return Offset.Zero
                val before = heightOffset
                heightOffset = (heightOffset + available.y).coerceIn(heightOffsetLimit, 0f)
                return Offset(0f, heightOffset - before)
            }
        }
}

/** A [QeetrixTopAppBarScrollState] for a screen with a large title. */
@Composable
public fun rememberQeetrixTopAppBarScrollState(): QeetrixTopAppBarScrollState = remember { QeetrixTopAppBarScrollState() }

/** `calc(size.control.lg + 16px)`: the chrome bar is the field height plus 16 dp. */
private val BAR_EXTRA = 16.dp

/**
 * The back button for [QeetrixTopAppBar]'s navigation icon — Android's PageHeaderBack: a neutral ghost
 * [QeetrixIconButton] with the sharp `arrow-left`, pointing the reading way — so it turns round in right-to-left
 * layouts, as the web's does (`scale: -1 1`) — named "Back" (`qx_back`) for accessibility services; name the parent
 * ("Back to Users") with [contentDescription] where it helps. [onClick] is the app's: usually its navigation's up or
 * back action.
 */
@Composable
public fun QeetrixBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.qx_back),
) {
    QeetrixIconButton(
        QeetrixIconName.ArrowLeft,
        contentDescription,
        onClick = onClick,
        modifier = modifier,
        tone = QeetrixButtonTone.Neutral,
        // "Back" is a reading direction: the arrow points to the start, mirrored in right to left (page-header.css).
        iconMirrorsInRightToLeft = true,
    )
}
