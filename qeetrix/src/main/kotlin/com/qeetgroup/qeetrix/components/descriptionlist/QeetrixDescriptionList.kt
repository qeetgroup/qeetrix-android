package com.qeetgroup.qeetrix.components.descriptionlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.card.LocalInsideQeetrixCardBody
import com.qeetgroup.qeetrix.components.card.QeetrixCardBleed
import com.qeetgroup.qeetrix.components.card.qeetrixCardBleed
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlin.math.max

/** How a [QeetrixDescriptionList] lays out its pairs (`value` is `@qeetrix/react`'s `layout`). */
public enum class QeetrixDescriptionListLayout(
    public val value: String,
) {
    /**
     * Each term beside its value, a `color.border.subtle` rule between rows, each row at least the density's row
     * height. Where the list is narrower than 24 rem (384 dp at the default text size — a phone, a side panel) the
     * term moves above its value. The default.
     */
    Rows("rows"),

    /** Each term above its value, in up to `columns` columns of at least 8 rem (128 dp): fewer where there isn't room. */
    Columns("columns"),
}

/**
 * Facts about one record, as term and value pairs — `@qeetrix/react`'s DescriptionList: a user's email and ID, a
 * payment's amount and status. Put [QeetrixDescriptionItem]s in it.
 *
 * ```
 * QeetrixDescriptionList {
 *     QeetrixDescriptionItem("Email", "priya@acme.in")
 *     QeetrixDescriptionItem("Status") { QeetrixBadge("Active", tone = QeetrixBadgeTone.Success) }
 *     QeetrixDescriptionItem("Phone", value = null) // "—", read as "Not set"
 * }
 * ```
 *
 * - **Reading.** Terms are `label.sm` in `color.text.subtle`; values are `body.md` in `color.text.default` and wrap
 *   rather than truncate. Each pair is read as one element ("Email, priya@acme.in"); a control in a value or an item's
 *   `action` stays its own focusable element.
 * - **Rows** (the default): the term column is 30% of the row, at least 8 rem, beside the value, aligned on the
 *   value's first line; rows are at least `size.row` tall with a rule between. In a
 *   [com.qeetgroup.qeetrix.components.card.QeetrixCard]'s content, a rows list placed directly there runs edge to
 *   edge, its rows keeping the card's padding.
 * - **Columns**: terms above values, [columns] at most (1 to 4).
 * - **Values are what you pass.** Format dates, amounts and identifiers first, and never pass a secret; the list
 *   logs, copies and announces nothing.
 *
 * @param columns In [QeetrixDescriptionListLayout.Columns], the most columns, 1 to 4.
 */
@Composable
public fun QeetrixDescriptionList(
    modifier: Modifier = Modifier,
    layout: QeetrixDescriptionListLayout = QeetrixDescriptionListLayout.Rows,
    columns: Int = 2,
    content: @Composable () -> Unit,
) {
    require(columns in 1..4) { "A description list has 1 to 4 columns, not $columns." }
    val inCard = LocalInsideQeetrixCardBody.current
    val bleed = remember(inCard) { QeetrixCardBleed() }
    val style = remember(layout, bleed) { DescriptionListStyle(layout, bleed) }
    CompositionLocalProvider(
        LocalDescriptionListStyle provides style,
        LocalInsideQeetrixCardBody provides false, // a card's edge-to-edge rule is for the list, not for lists inside it
        LocalQeetrixContentColor provides QeetrixTheme.colors.textDefault, // .qx-description-list color
    ) {
        when (layout) {
            QeetrixDescriptionListLayout.Rows -> {
                Rows(if (inCard) modifier.qeetrixCardBleed(bleed) else modifier, bleed, content)
            }

            QeetrixDescriptionListLayout.Columns -> {
                ColumnsGrid(columns, modifier, content)
            }
        }
    }
}

/**
 * `[data-layout="rows"]`: the items one under another, a 1 dp `color.border.subtle` rule above each after the first
 * (`.qx-description-item + .qx-description-item`), across the whole list. In a card body the list is as wide as the
 * card and its items keep the card's padding ([QeetrixCardBleed.inset]).
 */
@Composable
private fun Rows(
    modifier: Modifier,
    bleed: QeetrixCardBleed,
    content: @Composable () -> Unit,
) {
    val rule = QeetrixTheme.colors.borderSubtle
    var rules by remember { mutableStateOf(IntArray(0)) }
    Layout(
        content,
        modifier.fillMaxWidth().drawBehind {
            val height = QeetrixBorder.WidthDefault.toPx()
            for (y in rules) drawRect(rule, Offset(0f, y.toFloat()), Size(size.width, height))
        },
    ) { measurables, constraints ->
        val inset = bleed.inset
        val hairline = QeetrixBorder.WidthDefault.roundToPx().coerceAtLeast(1)
        val width = constraints.maxWidth
        val itemConstraints = Constraints.fixedWidth((width - 2 * inset).coerceAtLeast(0))
        val placeables = measurables.map { it.measure(itemConstraints) }
        val tops = IntArray(placeables.size)
        val lines = IntArray((placeables.size - 1).coerceAtLeast(0))
        var y = 0
        placeables.forEachIndexed { index, placeable ->
            if (index > 0) {
                lines[index - 1] = y
                y += hairline
            }
            tops[index] = y
            y += placeable.height
        }
        if (!lines.contentEquals(rules)) rules = lines
        layout(width, y.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            placeables.forEachIndexed { index, placeable -> placeable.placeRelative(inset, tops[index]) }
        }
    }
}

/**
 * `[data-layout="columns"]`: `repeat(auto-fill, minmax(max(8rem, (100% − (columns − 1) × gap.xl) / columns), 1fr))`
 * — as many columns as fit, at most [columns], sharing the width; `space.gap.lg` between rows, `space.gap.xl`
 * between columns, filled row by row.
 */
@Composable
private fun ColumnsGrid(
    columns: Int,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content, modifier.fillMaxWidth()) { measurables, constraints ->
        val width = constraints.maxWidth
        val gapX = QeetrixSpace.GapXl.roundToPx()
        val gapY = QeetrixSpace.GapLg.roundToPx()
        val minimum = max(rem(8f), (width - (columns - 1) * gapX) / columns)
        val count = ((width + gapX) / (minimum + gapX)).coerceIn(1, columns)
        val cellWidth = ((width - gapX * (count - 1)) / count).coerceAtLeast(0)
        val placeables = measurables.map { it.measure(Constraints.fixedWidth(cellWidth)) }
        val positions = ArrayList<Pair<Int, Int>>(placeables.size)
        var y = 0
        placeables.chunked(count).forEachIndexed { row, cells ->
            if (row > 0) y += gapY
            cells.forEachIndexed { column, _ -> positions += column * (cellWidth + gapX) to y }
            y += cells.maxOf { it.height }
        }
        layout(width, y.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            placeables.forEachIndexed { index, placeable -> placeable.placeRelative(positions[index].first, positions[index].second) }
        }
    }
}

/**
 * One term and its text value — `@qeetrix/react`'s DescriptionItem. A null or empty [value] shows "—" in
 * `color.text.subtlest`, read as [emptyLabel] ("Not set").
 *
 * ```
 * QeetrixDescriptionItem("Created", formattedDate)
 * QeetrixDescriptionItem(
 *     "Display name",
 *     "Acme",
 *     description = "Shown in the top bar and in invitations.",
 *     action = { QeetrixButton("Change", onClick = ::edit, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm) },
 * )
 * ```
 *
 * @param description A sentence under the value on what it means, in `body.sm`, `color.text.subtle`.
 * @param action An action on this fact at the row's end, such as a ghost "Change" — a settings row. Below the value
 *   where the list is narrow.
 */
@Composable
public fun QeetrixDescriptionItem(
    term: String,
    value: String?,
    modifier: Modifier = Modifier,
    description: String? = null,
    action: (@Composable () -> Unit)? = null,
    emptyLabel: String = stringResource(R.string.qx_not_set),
) {
    DescriptionItem(term, modifier, description, action) {
        if (value.isNullOrEmpty()) {
            EmptyValue(emptyLabel)
        } else {
            // overflow-wrap: anywhere — the value wraps in its track. A value takes its own text's direction (an email
            // or an amount stays left to right in a right-to-left layout) and the layout's start.
            BasicText(
                value,
                style =
                    QeetrixTheme.typography.bodyMd
                        .copy(color = QeetrixTheme.colors.textDefault)
                        .labelDirection(alignToLayout = true),
            )
        }
    }
}

/**
 * One term and a value of any content — a badge, a link, a copy button — read together with the term. Pieces of the
 * value sit side by side, `space.gap.xs` apart, and wrap; text in it is `body.md`, the default size of
 * [com.qeetgroup.qeetrix.components.typography.QeetrixText]. For an empty value use the [String] overload with null.
 */
@Composable
public fun QeetrixDescriptionItem(
    term: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    action: (@Composable () -> Unit)? = null,
    value: @Composable () -> Unit,
) {
    DescriptionItem(term, modifier, description, action, value)
}

/** `.qx-description-empty`: "—" in `color.text.subtlest`, hidden, with the visually hidden "Not set" read instead. */
@Composable
private fun EmptyValue(label: String) {
    BasicText(
        "—",
        Modifier.clearAndSetSemantics { text = AnnotatedString(label) },
        style = QeetrixTheme.typography.bodyMd.copy(color = QeetrixTheme.colors.textSubtlest),
    )
}

@Composable
private fun DescriptionItem(
    term: String,
    modifier: Modifier,
    description: String?,
    action: (@Composable () -> Unit)?,
    value: @Composable () -> Unit,
) {
    val style = LocalDescriptionListStyle.current
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val rows = style.layout == QeetrixDescriptionListLayout.Rows
    val rowHeight = QeetrixTheme.density.sizeRow
    Layout(
        content = {
            // .qx-description-term: label.sm, color.text.subtle
            BasicText(term, style = typography.labelSm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
            // .qx-description-value: flex, wrap, centred, space.gap.xs apart; the note takes a line of its own.
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
                    verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) { value() }
                if (description != null) {
                    // .qx-description-note: body.sm, color.text.subtle, flex-basis 100%
                    BasicText(
                        description,
                        Modifier.fillMaxWidth(),
                        style = typography.bodySm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true),
                    )
                }
            }
            if (action != null) action()
        },
        modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val rowGap = QeetrixSpace.GapXs.roundToPx() // row-gap: space.gap.xs
        val termMeasurable = measurables[0]
        val valueMeasurable = measurables[1]
        val actionMeasurable = measurables.getOrNull(2)
        // The container query: the list is narrower than 24rem (its rows' width plus a card's padding either side).
        val stacked = !rows || width + 2 * style.bleed.inset < rem(24f)
        if (stacked) {
            // A column: term, value, then the action at the start (`justify-content: flex-start`).
            val loose = Constraints(maxWidth = width)
            val termPlaceable = termMeasurable.measure(loose)
            val valuePlaceable = valueMeasurable.measure(Constraints.fixedWidth(width))
            val actionPlaceable = actionMeasurable?.measure(loose)
            val padding = if (rows) QeetrixSpace.GapSm.roundToPx() else 0 // padding-block: space.gap.sm
            var y = padding
            val termY = y
            y += termPlaceable.height + rowGap
            val valueY = y
            y += valuePlaceable.height
            val actionY = y + rowGap
            if (actionPlaceable != null) y = actionY + actionPlaceable.height
            val height = if (rows) max(y + padding, rowHeight.roundToPx()) else y // min-block-size: size.row
            layout(width, height) {
                termPlaceable.placeRelative(0, termY)
                valuePlaceable.placeRelative(0, valueY)
                actionPlaceable?.placeRelative(0, actionY)
            }
        } else {
            // grid-template-columns: minmax(8rem, 30%) minmax(0, 1fr) [auto]; column-gap: space.gap.md
            val gap = QeetrixSpace.GapMd.roundToPx()
            val padding = QeetrixSpace.GapSm.roundToPx()
            val actionPlaceable = actionMeasurable?.measure(Constraints(maxWidth = width / 2))
            val termWidth = max(rem(8f), (width * 0.3f).toInt()).coerceAtMost(width)
            val actionWidth = actionPlaceable?.let { it.width + gap } ?: 0
            val valueWidth = (width - termWidth - gap - actionWidth).coerceAtLeast(0)
            val termPlaceable = termMeasurable.measure(Constraints(maxWidth = termWidth))
            val valuePlaceable = valueMeasurable.measure(Constraints.fixedWidth(valueWidth))
            val content: Int
            val termY: Int
            val valueY: Int
            val actionY: Int
            if (actionPlaceable == null) {
                // align-items: baseline — the term on the value's first line.
                val termBaseline = termPlaceable.baselineOr(0)
                val valueBaseline = valuePlaceable.baselineOr(0)
                val shift = valueBaseline - termBaseline
                termY = max(shift, 0)
                valueY = max(-shift, 0)
                actionY = 0
                content = max(termY + termPlaceable.height, valueY + valuePlaceable.height)
            } else {
                // [data-action]: align-items: center.
                content = maxOf(termPlaceable.height, valuePlaceable.height, actionPlaceable.height)
                termY = (content - termPlaceable.height) / 2
                valueY = (content - valuePlaceable.height) / 2
                actionY = (content - actionPlaceable.height) / 2
            }
            val height = max(content + 2 * padding, rowHeight.roundToPx())
            val top = if (actionPlaceable == null) padding else (height - content) / 2
            layout(width, height) {
                termPlaceable.placeRelative(0, top + termY)
                valuePlaceable.placeRelative(termWidth + gap, top + valueY)
                actionPlaceable?.placeRelative(width - actionPlaceable.width, top + actionY)
            }
        }
    }
}

private fun Placeable.baselineOr(fallback: Int): Int = this[FirstBaseline].takeIf { it != AlignmentLine.Unspecified } ?: fallback

/** [count] rem: CSS's root font size, 16 sp, so it grows with the user's text size. */
private fun Density.rem(count: Float): Int = (16.sp.toPx() * count).toInt()

/** descriptionList.empty, in English; the built-in string is pending (`qx_not_set`). */

@Immutable
private class DescriptionListStyle(
    val layout: QeetrixDescriptionListLayout,
    val bleed: QeetrixCardBleed,
)

private val LocalDescriptionListStyle =
    staticCompositionLocalOf { DescriptionListStyle(QeetrixDescriptionListLayout.Rows, QeetrixCardBleed()) }
