package com.qeetgroup.qeetrix.components.descriptionlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.RuledColumn
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.internal.windowAtLeast
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** How a [QeetrixDescriptionList] arranges its terms and values (`value` is the web name). */
public enum class QeetrixDescriptionListLayout(
    public val value: String,
) {
    /**
     * A term column beside a value column from the web's `sm` breakpoint (640 dp of window width); each term
     * above its value below it. The default.
     */
    Horizontal("horizontal"),

    /** Each term above its value at every width — for narrow panels and sheets. */
    Vertical("vertical"),

    /** A grid of term-over-value cells, at least 192 dp wide, filling the width — for summaries. */
    Grid("grid"),
}

/**
 * Labelled details — the properties of one thing: a plan, a device, a file. The web's `DescriptionList`; a
 * [QeetrixDescriptionItem] on its own is the key-value row.
 *
 * ```
 * QeetrixDescriptionList(divided = true) {
 *     QeetrixDescriptionItem("Plan", "Team")
 *     QeetrixDescriptionItem("Region", "Mumbai (ap-south-1)")
 *     QeetrixDescriptionItem("Status") { QeetrixBadge("Active", variant = QeetrixBadgeVariant.Success) }
 * }
 * ```
 *
 * - **Reading.** Terms are quiet labels and values are the data, both 14 sp. Each pair is read as one
 *   element — "Plan, Team" — and a control in a value stays its own focusable element. Long values wrap
 *   rather than truncate.
 * - **Spacing** follows the density: pairs are the density's cell padding apart, so a compact density
 *   tightens the list as it tightens a table. [divided] draws a subtle rule between pairs.
 * - **Values are what you pass.** Format dates, amounts and identifiers first, and decide whether a value is
 *   safe to show; never pass a secret. The list never logs, copies or announces anything.
 *
 * In a lazy list, where each row is its own item, use one [QeetrixDescriptionItem] per item with your own
 * padding, and [com.qeetgroup.qeetrix.components.separator.QeetrixSeparator]s for rules.
 */
@Composable
public fun QeetrixDescriptionList(
    modifier: Modifier = Modifier,
    layout: QeetrixDescriptionListLayout = QeetrixDescriptionListLayout.Horizontal,
    divided: Boolean = false,
    content: @Composable () -> Unit,
) {
    val gap = QeetrixTheme.density.cellPaddingY
    val rule = if (divided) QeetrixTheme.colors.borderSubtle else null // border-border-subtle
    CompositionLocalProvider(LocalDescriptionListStyle provides DescriptionListStyle(layout, divided)) {
        if (layout == QeetrixDescriptionListLayout.Grid) {
            // grid-cols-[repeat(auto-fill,minmax(min(100%,12rem),1fr))] gap-x-6 gap-y-[cell-padding × 1.5]
            DescriptionGrid(
                columnGap = QeetrixWebScale.spacing(6f),
                rowGap = gap * 1.5f,
                rule = rule,
                modifier = modifier.fillMaxWidth(),
                content = content,
            )
        } else {
            // gap-y-(--qx-control-cell-padding-y); divided pairs carry it as padding either side of the rule.
            RuledColumn(rule, modifier.fillMaxWidth(), spacing = gap, content = content)
        }
    }
}

/**
 * One term and its value — the web's `DescriptionItem` with its `DescriptionTerm` and `DescriptionDetails`.
 * Inside a [QeetrixDescriptionList] it follows the list's layout; on its own it is a key-value row in the
 * default horizontal layout.
 *
 * ```
 * QeetrixDescriptionItem("Created", formattedDate)
 * ```
 */
@Composable
public fun QeetrixDescriptionItem(
    term: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    QeetrixDescriptionItem(term, modifier) {
        // wrap-break-word: the value fills its track and wraps in it.
        val color = QeetrixTheme.colors.textPrimary // text-foreground
        BasicText(value, Modifier.fillMaxWidth(), style = DescriptionTextStyle.copy(color = color))
    }
}

/**
 * One term and a value of any content — a badge, an avatar, a link — read together with the term. Give text
 * values `QeetrixText` with the `Small` variant, or the [String] overload, to match the terms.
 */
@Composable
public fun QeetrixDescriptionItem(
    term: String,
    modifier: Modifier = Modifier,
    value: @Composable () -> Unit,
) {
    val style = LocalDescriptionListStyle.current
    val termView: @Composable () -> Unit = {
        val color = QeetrixTheme.colors.textTertiary // text-muted-foreground
        BasicText(term, Modifier.fillMaxWidth(), style = DescriptionTextStyle.copy(color = color))
    }
    val padded =
        if (style.divided && style.layout == QeetrixDescriptionListLayout.Grid) {
            Modifier.padding(vertical = QeetrixTheme.density.cellPaddingY) // py-(--qx-control-cell-padding-y)
        } else {
            Modifier
        }
    val beside = style.layout == QeetrixDescriptionListLayout.Horizontal && windowAtLeast(QeetrixWebScale.BreakpointSm)
    val item = modifier.fillMaxWidth().semantics(mergeDescendants = true) {}.then(padded)
    if (beside) {
        // sm:grid-cols-[minmax(8rem,12rem)_minmax(0,1fr)] gap-x-6: the term track reaches its 12rem.
        Row(item, horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(6f))) {
            Box(Modifier.width(QeetrixWebScale.spacing(48f))) { termView() }
            Box(Modifier.weight(1f)) { value() }
        }
    } else {
        Column(item, verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f))) {
            // gap-y-0.5
            termView()
            value()
        }
    }
}

/**
 * The grid layout: as many columns of at least 192 dp as fit, sharing the width, filled row by row, [rowGap]
 * apart. With a [rule], a hairline above each cell after the first row replaces the gap, as wide as the cell.
 */
@Composable
private fun DescriptionGrid(
    columnGap: Dp,
    rowGap: Dp,
    rule: Color?,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    var rules by remember { mutableStateOf(emptyList<Pair<IntOffset, Int>>()) }
    Layout(
        content,
        modifier.drawBehind {
            if (rule == null) return@drawBehind
            val height = QeetrixStroke.Hairline.toPx()
            for ((at, width) in rules) drawRect(rule, Offset(at.x.toFloat(), at.y.toFloat()), Size(width.toFloat(), height))
        },
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val gapX = columnGap.roundToPx()
        val gapY = if (rule != null) QeetrixStroke.Hairline.roundToPx().coerceAtLeast(1) else rowGap.roundToPx()
        val minimum = QeetrixWebScale.spacing(48f).roundToPx() // min(100%, 12rem)
        val columns = ((width + gapX) / (minimum + gapX)).coerceAtLeast(1)
        val cellWidth = (width - gapX * (columns - 1)) / columns
        val placeables = measurables.map { it.measure(Constraints.fixedWidth(cellWidth)) }
        val positions = ArrayList<IntOffset>(placeables.size)
        val lines = ArrayList<Pair<IntOffset, Int>>()
        var y = 0
        placeables.chunked(columns).forEachIndexed { row, cells ->
            if (row > 0) y += gapY
            cells.forEachIndexed { column, _ ->
                val x = column * (cellWidth + gapX)
                if (row > 0 && rule != null) lines += IntOffset(x, y - gapY) to cellWidth
                positions += IntOffset(x, y)
            }
            y += cells.maxOf { it.height }
        }
        if (lines != rules) rules = lines
        layout(width, y.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            placeables.forEachIndexed { index, placeable -> placeable.placeRelative(positions[index]) }
        }
    }
}

@Immutable
private class DescriptionListStyle(
    val layout: QeetrixDescriptionListLayout,
    val divided: Boolean,
)

private val LocalDescriptionListStyle = staticCompositionLocalOf { DescriptionListStyle(QeetrixDescriptionListLayout.Horizontal, false) }

// text-sm, for terms and values.
private val DescriptionTextStyle: TextStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)
