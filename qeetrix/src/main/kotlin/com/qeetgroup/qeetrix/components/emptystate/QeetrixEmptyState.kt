package com.qeetgroup.qeetrix.components.emptystate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** How much room a [QeetrixEmptyState] takes (`value` is `@qeetrix/react`'s EmptyState `size`). */
public enum class QeetrixEmptyStateSize(
    public val value: String,
    internal val title: QeetrixTextRole,
    internal val paddingY: Dp,
) {
    /** A whole view: the title in `title.md`, `space.layout.lg` (48 dp) above and below. The default. */
    Page("page", QeetrixTextRole.TitleMd, QeetrixSpace.LayoutLg),

    /** Inside a card or a table: the title in `title.sm`, `space.inset.xl` (24 dp) above and below. */
    Section("section", QeetrixTextRole.TitleSm, QeetrixSpace.InsetXl),
}

/** The colour of a [QeetrixEmptyState]'s icon (`value` is `@qeetrix/react`'s EmptyState `tone`). */
public enum class QeetrixEmptyStateTone(
    public val value: String,
) {
    /** `color.icon.subtle`: nothing yet, no results. The default. */
    Neutral("neutral"),

    /** `color.icon.danger`: an error, such as a load that failed. */
    Danger("danger"),
}

/**
 * The message where content would be: nothing yet, no results, or an error — `@qeetrix/react`'s EmptyState. An
 * optional 24 dp sharp [icon], a [title] that says what is missing or what went wrong in a few words, a
 * [description] of one or two sentences, the [actions] (one primary at most), and under them an optional [footer]:
 * a link to the docs, or a second way in ("Or import users from a CSV file").
 *
 * ```
 * QeetrixEmptyState(
 *     title = "No invoices yet",
 *     description = "Invoices appear here once you bill a customer.",
 *     icon = QeetrixIconName.FileText,
 * ) {
 *     QeetrixButton("Create invoice", onClick = ::createInvoice, icon = QeetrixIconName.Plus)
 * }
 * ```
 *
 * - **It renders what you give it.** Whether a surface is empty because there is no data, because a filter
 *   excluded it, because the person lacks permission or because loading failed is the app's to decide — and so is
 *   what an action does. Never put a raw error, a status code or a stack trace in the description: say what
 *   happened and what the person can do.
 * - **Layout.** Centred, at most 480 dp wide, `space.gap.sm` between its parts and `space.inset.lg` at the sides;
 *   the actions wrap onto more lines, centred, when they don't fit. Text follows the font scale.
 * - **Accessibility.** With a [level] the title is a heading — set one when this is the screen's main message;
 *   without it the title is styled text, as on the web. The icon is decorative; actions are their own controls.
 *
 * For the loading, failure and empty states of one surface together, use [QeetrixDataState].
 *
 * @param level A heading level (1–6) for the title. Android has no heading levels, so any level makes the title a
 *   heading for accessibility services; it does not change the look, which [size] sets.
 * @param footer Under the actions, in `color.text.subtle`: draw its text at `QeetrixTextSize.Sm` (`body.sm`).
 * @param actions Buttons under the description, in a centred row that wraps: one primary at most (design rules §7).
 */
@Composable
public fun QeetrixEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    icon: QeetrixIconName? = null,
    description: String? = null,
    size: QeetrixEmptyStateSize = QeetrixEmptyStateSize.Page,
    tone: QeetrixEmptyStateTone = QeetrixEmptyStateTone.Neutral,
    level: Int? = null,
    footer: (@Composable () -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    EmptyStateContent(title, description, icon, size, tone, level, announce = false, modifier, footer, actions)
}

/**
 * The empty state's layout (`.qx-empty-state`). With [announce] its title and description are one assertive live
 * region, read out as they appear — a data state's failure, the web's `role="alert"`.
 */
@Composable
internal fun EmptyStateContent(
    title: String,
    description: String?,
    icon: QeetrixIconName?,
    size: QeetrixEmptyStateSize,
    tone: QeetrixEmptyStateTone,
    level: Int?,
    announce: Boolean,
    modifier: Modifier,
    footer: (@Composable () -> Unit)?,
    actions: (@Composable () -> Unit)?,
) {
    require(level == null || level in 1..6) { "A heading level is 1 to 6, not $level." }
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    // margin-inline: auto; max-inline-size: 480px
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier
                .widthIn(max = MaxWidth)
                .fillMaxWidth()
                .padding(horizontal = QeetrixSpace.InsetLg, vertical = size.paddingY), // padding: <size> space.inset.lg
            verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), // gap: space.gap.sm
            horizontalAlignment = Alignment.CenterHorizontally, // align-items: center; text-align: center
        ) {
            if (icon != null) {
                QeetrixIcon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = QeetrixSpace.GapXs), // margin-block-end: space.gap.xs
                    size = EmptyStateIconSize,
                    shape = QeetrixIconShape.Sharp,
                    tint = if (tone == QeetrixEmptyStateTone.Danger) colors.iconDanger else colors.iconSubtle,
                )
            }
            // Text fills its line and is centred in it, rather than measured to its content: content-width text in a
            // right-to-left paragraph can wrap its last word.
            Column(
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (announce) Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Assertive } else Modifier,
                    ),
                verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
            ) {
                if (level != null) {
                    QeetrixHeading(
                        title,
                        level,
                        Modifier.fillMaxWidth(),
                        size = if (size == QeetrixEmptyStateSize.Page) QeetrixHeadingSize.TitleMd else QeetrixHeadingSize.TitleSm,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    BasicText(
                        title,
                        Modifier.fillMaxWidth(),
                        style =
                            typography[size.title]
                                .copy(
                                    color = colors.textDefault,
                                    textAlign = TextAlign.Center,
                                ).labelDirection(alignToLayout = true),
                    )
                }
                if (description != null) {
                    BasicText(
                        description,
                        Modifier.fillMaxWidth(),
                        style =
                            typography.bodyMd
                                .copy(
                                    color = colors.textSubtle,
                                    textAlign = TextAlign.Center,
                                ).labelDirection(alignToLayout = true),
                    )
                }
            }
            if (actions != null) {
                // .qx-empty-state-actions: flex-wrap, justify-content center, gap space.gap.sm, margin-block-start space.gap.sm
                FlowRow(
                    Modifier.padding(top = QeetrixSpace.GapSm).wrapContentWidth(),
                    horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
                ) { actions() }
            }
            if (footer != null) {
                // .qx-empty-state-footer: body.sm, color.text.subtle
                CompositionLocalProvider(LocalQeetrixContentColor provides colors.textSubtle) { footer() }
            }
        }
    }
}

/** drawIcon(…, { size: 24 }). */
private val EmptyStateIconSize = 24.dp

/** `.qx-empty-state max-inline-size: 480px`. */
private val MaxWidth = 480.dp
