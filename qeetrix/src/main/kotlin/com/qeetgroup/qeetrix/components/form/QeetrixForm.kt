package com.qeetgroup.qeetrix.components.form

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.QeetrixActionRow
import com.qeetgroup.qeetrix.internal.QeetrixTextWeight
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.focusFrame
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A form's column — `@qeetrix/react`'s Form: the fields `space.form.gap` apart (12 / 16 / 20 dp with the density),
 * with a [QeetrixFormErrorSummary] at the top while [errors] lists problems.
 *
 * Compose has no form element, so validation is your app's, as it is on the web's server side: after a submit
 * that fails, give each field its `error` and pass the same problems here, worded exactly as next to each field and
 * in the order of the fields. The summary takes focus when it appears (unless [summary] is off), stays until the
 * next submit, and goes once one succeeds — clear [errors] then. Prefix the screen's title with "Error: " while it
 * shows.
 *
 * ```
 * QeetrixForm(errors = problems) {
 *     QeetrixTextField(gstin, label = "GSTIN", error = gstinError)
 *     QeetrixTextField(tradeName, label = "Trade name", optional = true)
 *     QeetrixFormActions { QeetrixButton("Save", onClick = ::submit, size = QeetrixButtonSize.Lg) }
 * }
 * ```
 *
 * @param summaryTitle The summary's heading; by default "Fix these to continue" (`qx_error_summary_title`).
 */
@Composable
public fun QeetrixForm(
    modifier: Modifier = Modifier,
    errors: List<QeetrixFormError> = emptyList(),
    summary: Boolean = true,
    summaryTitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    // .qx-form { gap: space.form-gap; flex-direction: column }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.spaceFormGap)) {
        if (summary && errors.isNotEmpty()) QeetrixFormErrorSummary(errors, title = summaryTitle)
        content()
    }
}

/**
 * The buttons that end a form, at its end: tertiary first and the primary last, so it ends the row, `space.gap.sm`
 * apart, wrapping when they do not fit (the dialog footer's row). `@qeetrix/react` has no part for this — a form's
 * actions are its own row there — so it is kept from the earlier library. A form's main button is
 * `QeetrixButtonSize.Lg`, the field height (design rules §8); on a phone it can be `fullWidth`.
 *
 * ```
 * QeetrixFormActions {
 *     QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Lg)
 *     QeetrixButton("Save changes", onClick = ::save, size = QeetrixButtonSize.Lg, loading = saving)
 * }
 * ```
 */
@Composable
public fun QeetrixFormActions(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    QeetrixActionRow(modifier, content)
}

/**
 * One problem in a [QeetrixFormErrorSummary] — react's `ErrorSummaryItem`: the [message], worded exactly as the
 * field's own error ("Enter a work email"), and what choosing it does — normally move focus to the field, which
 * scrolls it into view: `QeetrixFormError("Enter a work email") { emailFocus.requestFocus() }`. Without [onClick]
 * the item is plain text.
 */
@Stable
public class QeetrixFormError(
    public val message: String,
    public val onClick: (() -> Unit)? = null,
) {
    override fun toString(): String = "QeetrixFormError($message)"
}

/**
 * The problems that stop a form going through, listed at its top — `@qeetrix/react`'s ErrorSummary, GOV.UK's error
 * summary. Nothing is shown without [items].
 *
 * - **Look.** The danger alert (alert.css): `color.bg.danger.subtle` with a 3 dp `color.icon.danger` edge on the
 *   start side, square, the 16 dp `circle-alert` icon, the [title] — "Fix these to continue"
 *   (`qx_error_summary_title`) by default, worded as what to do — in `label.md` at `title.sm`'s weight, and each
 *   item as a link (`color.text.link`, underlined) that takes the person to the field.
 * - **Announced and focused.** It is announced as an alert, its title a heading read as "Error: …". It takes focus
 *   when it appears ([autoFocus]), so its heading is read first and the keyboard goes on to its links, and shows
 *   the Ember frame inside its edge; it is focusable only until focus leaves it.
 * - Each field still shows its own error. Each link row is a 48 dp target.
 *
 * ```
 * QeetrixFormErrorSummary(listOf(QeetrixFormError("Enter a GSTIN") { gstinFocus.requestFocus() }))
 * ```
 */
@Composable
public fun QeetrixFormErrorSummary(
    items: List<QeetrixFormError>,
    modifier: Modifier = Modifier,
    title: String? = null,
    autoFocus: Boolean = true,
) {
    if (items.isEmpty()) return
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val requester = remember { FocusRequester() }
    // GOV.UK: focusable (tabindex -1) only until focus leaves it, so a later tap on its text does not frame it again.
    var focusTarget by remember { mutableStateOf(autoFocus) }
    var focused by remember { mutableStateOf(false) }
    // A new or changed list (after the next failed submit) takes focus again.
    LaunchedEffect(items) {
        if (autoFocus) {
            focusTarget = true
            requester.requestFocus()
        }
    }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val edge = colors.iconDanger
    val heading = title ?: stringResource(R.string.qx_error_summary_title)
    val spoken = "${stringResource(R.string.qx_alert_danger)}: $heading"
    val titleStyle = typography.labelMd.copy(color = colors.textDefault, fontWeight = QeetrixTextWeight.Semibold)
    // The focus target and the alert are separate elements, so moving focus does not race the announcement.
    Box(
        modifier
            .fillMaxWidth()
            .focusRequester(requester)
            .onFocusChanged {
                if (focused && !it.isFocused) focusTarget = false
                focused = it.isFocused
            }.focusProperties { canFocus = focusTarget }
            .focusable()
            // .qx-error-summary:focus { outline: focus.ring.width solid border.focus; outline-offset: -focus.ring.width }
            .focusFrame(focused, QeetrixShapes.None, FocusFrame.Inside, colors.borderFocus, Color.Transparent),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .semantics { liveRegion = LiveRegionMode.Assertive } // <Alert announce="urgent">: role="alert"
                .background(colors.bgDangerSubtle)
                .drawBehind {
                    // border-inline-start: border.width.accent solid color.icon.danger
                    val width = QeetrixBorder.WidthAccent.toPx()
                    drawRect(edge, Offset(if (rtl) size.width - width else 0f, 0f), Size(width, size.height))
                }
                // padding: space.inset.md space.inset.sm space.inset.md space.inset.md, inside the 3 dp edge
                .padding(
                    start = QeetrixBorder.WidthAccent + QeetrixSpace.InsetMd,
                    top = QeetrixSpace.InsetMd,
                    end = QeetrixSpace.InsetSm,
                    bottom = QeetrixSpace.InsetMd,
                ),
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
            verticalAlignment = Alignment.Top,
        ) {
            // .qx-alert-icon { margin-block-start: 2px }: on the title's first line.
            Box(Modifier.height(firstLineHeight(titleStyle)), contentAlignment = Alignment.Center) {
                QeetrixIcon(
                    QeetrixIconName.CircleAlert,
                    contentDescription = null,
                    size = QeetrixSize.IconSm,
                    shape = QeetrixIconShape.Sharp,
                    tint = colors.iconDanger,
                )
            }
            // .qx-alert-body { gap: 2px; padding-inline-end: space.gap.xs }
            Column(Modifier.weight(1f).padding(end = QeetrixSpace.GapXs), verticalArrangement = Arrangement.spacedBy(ALERT_BODY_GAP)) {
                BasicText(
                    heading,
                    Modifier.semantics {
                        heading()
                        contentDescription = spoken
                    },
                    style = titleStyle.labelDirection(alignToLayout = true),
                )
                // .qx-error-summary-list { gap: space.gap.xs; margin-block-start: space.gap.xs }; each row a 48 dp target
                Column(Modifier.padding(top = QeetrixSpace.GapXs)) {
                    for (item in items) ErrorItem(item)
                }
            }
        }
    }
}

/**
 * A summary item: a link (link.css: `color.text.link`, underlined; `color.text.link-hover` under the pointer or a
 * press; keyboard focus the Ember frame 2 dp outside) in `body.md`, or plain `color.text.default` text without an
 * action.
 */
@Composable
private fun ErrorItem(item: QeetrixFormError) {
    val colors = QeetrixTheme.colors
    val style = QeetrixTheme.typography.bodyMd
    val onClick = item.onClick
    if (onClick == null) {
        Box(Modifier.heightIn(min = QeetrixAccessibility.MinimumTouchTarget), contentAlignment = Alignment.CenterStart) {
            BasicText(item.message, style = style.copy(color = colors.textDefault).labelDirection(alignToLayout = true))
        }
        return
    }
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val color by animateColorAsState(
        if (hovered || pressed) colors.textLinkHover else colors.textLink,
        qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast),
        label = "error-link",
    )
    Box(
        Modifier
            .heightIn(min = QeetrixAccessibility.MinimumTouchTarget)
            .clickable(source, indication = null, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.CenterStart,
    ) {
        BasicText(
            item.message,
            Modifier.qeetrixFocusFrame(source, QeetrixShapes.Sm, FocusFrame.Outside), // border-radius: radius.sm
            style = style.copy(color = color, textDecoration = TextDecoration.Underline).labelDirection(alignToLayout = true),
        )
    }
}

/** `.qx-alert-body { gap: 2px }`. */
private val ALERT_BODY_GAP = 2.dp
