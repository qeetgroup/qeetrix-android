package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The bar at the top of a screen: its [title] (a heading, in the `heading` type role), an optional
 * [subtitle], an optional [navigationIcon] at the start — usually [QeetrixBackButton] — and [actions] at
 * the end, usually [com.qeetgroup.qeetrix.components.button.QeetrixIconButton]s and a
 * [com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu] for overflow.
 *
 * - **Presentation only.** The bar draws what it is given; it keeps no navigation state, and it does not
 *   add a back button by itself. System Back keeps working whether or not the bar shows one — the app's
 *   navigation handles it.
 * - **[scrolled]**: pass `true` while content is scrolled under the bar (for a list,
 *   `listState.canScrollBackward`), and a rule appears along its bottom edge to separate the two.
 * - **Drawing.** The canvas surface, so the bar and the page read as one until content scrolls under it;
 *   at least 56 dp tall, growing with the text size. The title and subtitle keep to one line each and end in
 *   an ellipsis; accessibility services read them in full.
 * - **Edge to edge.** [windowInsets] (by default the top and horizontal safe-drawing insets) keeps the
 *   content clear of the status bar and display cutouts while the surface runs under them.
 *
 * ```
 * QeetrixTopAppBar(
 *     title = "Invoice INV-2041",
 *     subtitle = "Draft",
 *     navigationIcon = { QeetrixBackButton(onClick = navigateUp) },
 *     actions = { QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = ::share) },
 *     scrolled = listState.canScrollBackward,
 * )
 * ```
 */
@Composable
public fun QeetrixTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    scrolled: Boolean = false,
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
) {
    val colors = QeetrixTheme.colors
    val rule = colors.borderDefault
    val ruleAlpha by animateFloatAsState(
        if (scrolled) 1f else 0f,
        qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast),
        label = "top-app-bar-rule",
    )
    Row(
        modifier
            .fillMaxWidth()
            .background(colors.surfaceCanvas)
            .drawBehind {
                if (ruleAlpha == 0f) return@drawBehind
                val y = size.height - QeetrixStroke.Hairline.toPx() / 2
                drawLine(rule.copy(alpha = rule.alpha * ruleAlpha), Offset(0f, y), Offset(size.width, y), QeetrixStroke.Hairline.toPx())
            }.windowInsetsPadding(windowInsets)
            .heightIn(min = BarHeight)
            .padding(
                start = if (navigationIcon != null) QeetrixWebScale.spacing(1f) else QeetrixWebScale.spacing(4f),
                end = if (actions != null) QeetrixWebScale.spacing(1f) else QeetrixWebScale.spacing(4f),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        navigationIcon?.invoke()
        Column(
            Modifier
                .weight(1f)
                .padding(
                    start = if (navigationIcon != null) QeetrixWebScale.spacing(1f) else QeetrixWebScale.spacing(0f),
                    end = QeetrixWebScale.spacing(1f),
                ).padding(vertical = QeetrixWebScale.spacing(2f))
                .semantics(mergeDescendants = true) { heading() },
        ) {
            BasicText(
                title,
                style = QeetrixTheme.typography.heading.copy(color = colors.textPrimary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                BasicText(
                    subtitle,
                    style = SubtitleStyle.copy(color = colors.textTertiary), // text-sm text-muted-foreground
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (actions != null) Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically, content = actions)
    }
}

/**
 * The back button for [QeetrixTopAppBar]'s navigation icon: the `arrow-left` icon, pointing towards the
 * reading start — so it turns round in right-to-left layouts — named "Back" (`qx_back`) for accessibility
 * services. [onClick] is the app's: usually its navigation's "up" or back action.
 */
@Composable
public fun QeetrixBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.qx_back),
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    QeetrixIconButton(
        QeetrixIconName.ArrowLeft,
        contentDescription,
        onClick = onClick,
        // arrow-left is a physical left arrow upstream; "back" points to where the reading started.
        modifier = modifier.graphicsLayer { if (rtl) scaleX = -1f },
    )
}

/** h-14: the bar's height at the default text size. */
private val BarHeight = QeetrixWebScale.spacing(14f)

/** text-sm, the interface face. */
private val SubtitleStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm)
