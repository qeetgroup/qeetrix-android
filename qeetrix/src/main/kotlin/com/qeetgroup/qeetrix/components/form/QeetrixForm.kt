package com.qeetgroup.qeetrix.components.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Constraints
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.FontWeightSemibold
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.internal.windowAtLeast
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The buttons that end a form — the web's `FormActions`. On a narrow window (under 640 dp) they stack, full
 * width, with the last one on top, so the primary action sits nearest the content and under the thumb;
 * from 640 dp they sit in a row at the end, wrapping when they do not fit. Put the secondary action first
 * and the primary last, as on the web:
 *
 * ```
 * QeetrixFormActions {
 *     QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Outline)
 *     QeetrixButton("Save changes", onClick = ::save, loading = saving, loadingLabel = "Saving…")
 * }
 * ```
 *
 * A form itself needs no container in Compose: a `Column` spaced by `QeetrixTheme.density.fieldGap`, with
 * [com.qeetgroup.qeetrix.components.input.QeetrixFieldSet] for groups, is the web's `Form` and `FieldGroup`.
 */
@Composable
public fun QeetrixFormActions(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ActionRow(modifier.padding(top = QeetrixWebScale.spacing(2f)), content) // pt-2
}

/**
 * The web's action-row recipe — `flex flex-col-reverse gap-2 sm:flex-row sm:justify-end` — shared by
 * [QeetrixFormActions] and the dialogs' footers: under 640 dp the buttons stack full width with the last on
 * top; from 640 dp they sit in a row at the end, wrapping when they do not fit.
 */
@Composable
internal fun ActionRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val wide = windowAtLeast(QeetrixWebScale.BreakpointSm)
    Layout(content, modifier) { measurables, constraints ->
        val gap = QeetrixWebScale.spacing(2f).roundToPx() // gap-2
        if (!wide) {
            // flex-col-reverse: full width, the last child on top
            val full = if (constraints.hasBoundedWidth) Constraints.fixedWidth(constraints.maxWidth) else constraints.copy(minHeight = 0)
            val placeables = measurables.map { it.measure(full) }.asReversed()
            val height = placeables.sumOf { it.height } + gap * (placeables.size - 1).coerceAtLeast(0)
            layout(constraints.maxWidth.takeIf { constraints.hasBoundedWidth } ?: placeables.maxOf { it.width }, height) {
                var y = 0
                for (placeable in placeables) {
                    placeable.placeRelative(0, y)
                    y += placeable.height + gap
                }
            }
        } else {
            // sm:flex-row sm:flex-wrap sm:justify-end
            val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
            val rows = mutableListOf(mutableListOf<Placeable>())
            var rowWidth = 0
            for (placeable in placeables) {
                val needed = if (rows.last().isEmpty()) placeable.width else rowWidth + gap + placeable.width
                if (rows.last().isNotEmpty() && needed > constraints.maxWidth) {
                    rows += mutableListOf(placeable)
                    rowWidth = placeable.width
                } else {
                    rows.last() += placeable
                    rowWidth = needed
                }
            }
            val rowHeights = rows.map { row -> row.maxOfOrNull { it.height } ?: 0 }
            val width =
                if (constraints.hasBoundedWidth) {
                    constraints.maxWidth
                } else {
                    rows.maxOf { row ->
                        row.sumOf { it.width } +
                            gap * (row.size - 1)
                    }
                }
            layout(width, rowHeights.sum() + gap * (rows.size - 1)) {
                var y = 0
                rows.forEachIndexed { index, row ->
                    var x = width - (row.sumOf { it.width } + gap * (row.size - 1))
                    for (placeable in row) {
                        placeable.placeRelative(x, y + (rowHeights[index] - placeable.height) / 2)
                        x += placeable.width + gap
                    }
                    y += rowHeights[index] + gap
                }
            }
        }
    }
}

/**
 * One problem in a [QeetrixFormErrorSummary]: the [message] to show and what choosing it does — normally move
 * focus to the field, which scrolls the field into view: `QeetrixFormError("Enter your email", onClick = { emailFocus.requestFocus() })`.
 */
@Stable
public class QeetrixFormError(
    public val message: String,
    public val onClick: () -> Unit,
) {
    override fun toString(): String = "QeetrixFormError($message)"
}

/**
 * The problems with a form after a failed submit, in one place — the web's `FormErrorSummary`: the error
 * surface with its title ("There is a problem", `qx_error_summary_title`, or [title]) and a list of the
 * [errors], each a link that takes the user to the field. Nothing is shown when there are no errors.
 *
 * It is announced as soon as it appears. To take people straight to it after a failed submit — as the web
 * focuses it — give it a `FocusRequester` through [modifier] and request focus; it is focusable for that.
 * Each field should still show its own message. Which errors there are is your app's validation.
 *
 * ```
 * QeetrixFormErrorSummary(listOf(QeetrixFormError("Enter a name for the workspace.") { nameFocus.requestFocus() }),
 *     Modifier.focusRequester(summaryFocus))
 * ```
 */
@Composable
public fun QeetrixFormErrorSummary(
    errors: List<QeetrixFormError>,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    if (errors.isEmpty()) return
    val shape = RoundedCornerShape(QeetrixCorner.Control) // rounded-lg
    val source = remember { MutableInteractionSource() }
    val colors = QeetrixTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Assertive } // role="alert"; the links stay their own controls
            .qeetrixFocusRing(source, shape)
            .focusable(interactionSource = source)
            .background(colors.feedbackErrorSubtle, shape) // bg-destructive-subtle
            .border(QeetrixStroke.Hairline, colors.borderDanger, shape)
            .padding(QeetrixWebScale.spacing(4f)), // p-4
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2.5f)), // gap-2.5
        verticalAlignment = Alignment.Top,
    ) {
        val firstLine = with(LocalDensity.current) { TitleStyle.fontSize.toDp() } * QeetrixWebScale.TextSm.lineHeight
        Box(Modifier.height(firstLine), contentAlignment = Alignment.Center) {
            QeetrixIcon(
                QeetrixIconName.CircleAlert,
                contentDescription = null,
                size = QeetrixWebScale.spacing(4f),
                tint = colors.textDanger,
            )
        }
        Column(Modifier.weight(1f)) {
            BasicText(
                title ?: stringResource(R.string.qx_error_summary_title),
                Modifier.semantics { heading() },
                style = TitleStyle.copy(color = colors.textDanger),
            )
            // mt-2 space-y-1: each problem a full 48 dp row, so neighbouring links are never mistaken for each other
            Column(Modifier.padding(top = QeetrixWebScale.spacing(1f))) {
                for (error in errors) ErrorLink(error)
            }
        }
    }
}

@Composable
private fun ErrorLink(error: QeetrixFormError) {
    val source = remember { MutableInteractionSource() }
    val colors = QeetrixTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = QeetrixAccessibility.MinimumTouchTarget)
            .qeetrixFocusRing(source, RoundedCornerShape(QeetrixCorner.Sm))
            .clickable(source, indication = null, role = Role.Button, onClick = error.onClick),
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // list-disc with the marker in the error colour
        BasicText("•", style = LinkStyle.copy(color = colors.textDanger, textDecoration = null))
        BasicText(error.message, Modifier.weight(1f), style = LinkStyle.copy(color = colors.textDanger))
    }
}

/** font-semibold text-sm. */
private val TitleStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm, FontWeightSemibold)

/** font-medium text-sm underline. */
private val LinkStyle =
    qeetrixTextStyle(
        QeetrixFont.Sans,
        QeetrixWebScale.TextSm,
        FontWeightMedium,
    ).copy(textDecoration = TextDecoration.Underline)
