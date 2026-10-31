package com.qeetgroup.qeetrix.components.stat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonVariant
import com.qeetgroup.qeetrix.foundation.format.QeetrixFormat
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** What a stat's note says about the number (`value` is `@qeetrix/react`'s StatTone). */
public enum class QeetrixStatTone(
    public val value: String,
) {
    /** `color.text.subtlest`: context, not a judgement. The default. */
    Neutral("neutral"),

    /** `color.text.success`: better. */
    Positive("positive"),

    /** `color.text.danger`: worse. */
    Negative("negative"),

    /** `color.text.warning`: worth a look. */
    Warning("warning"),
}

/**
 * One key number with its label and a note — `@qeetrix/react`'s Stat, inside a [QeetrixStatGroup]: "Active users,
 * 12,40,512, +3.2% this week" (stat.css). The [label] in `label.sm` `color.text.subtle`; the value in `heading.sm` with
 * tabular figures, `space.gap.xs` under it; the [note] in `body.sm`, 2 dp under the value, coloured by [tone].
 *
 * - **The value** is [value] formatted for the device's locale by [QeetrixFormat] — as money with [currency] (a whole
 *   amount shows no fraction: ₹18,42,600), otherwise as a number — or [text] for units and percentages ("99.9%").
 * - **A change is said in words**, not by colour or an arrow alone: "+3.2% this week", "Down 4 from yesterday". The
 *   tone colours it.
 * - **[loading]**: placeholders keep the cell's height and "Loading" is read in the value's place.
 *
 * ```
 * QeetrixStatGroup {
 *     QeetrixStat("Active users", value = 1240512, note = "+3.2% this week", tone = QeetrixStatTone.Positive)
 *     QeetrixStat("Revenue", value = 1842600, currency = "INR")
 * }
 * ```
 */
@Composable
public fun QeetrixStat(
    label: String,
    modifier: Modifier = Modifier,
    value: Number? = null,
    text: String? = null,
    currency: String? = null,
    note: String? = null,
    tone: QeetrixStatTone = QeetrixStatTone.Neutral,
    loading: Boolean = false,
) {
    require(loading || value != null || text != null) { "A stat needs a value or a text, unless it is loading." }
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val locale = LocalConfiguration.current.locales[0]
    val written =
        remember(value, text, currency, locale) {
            text ?: when {
                value == null -> {
                    ""
                }

                currency != null -> {
                    val whole = value.toDouble() == Math.rint(value.toDouble())
                    QeetrixFormat.currency(value, currency, locale, maximumFractionDigits = if (whole) 0 else null)
                }

                else -> {
                    QeetrixFormat.number(value, locale)
                }
            }
        }
    val loadingWords = stringResource(R.string.qx_loading)
    Column(modifier.semantics(mergeDescendants = true) {}) {
        // .qx-stat-label: label.sm, color.text.subtle
        BasicText(label, style = typography.labelSm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
        if (loading) {
            QeetrixSkeleton(
                Modifier.padding(top = QeetrixSpace.GapXs).semantics { contentDescription = loadingWords },
                variant = QeetrixSkeletonVariant.Text,
            )
        } else {
            // .qx-stat-value: heading.sm, tabular-nums, margin-top space.gap.xs
            BasicText(
                written,
                Modifier.padding(top = QeetrixSpace.GapXs),
                style =
                    typography.headingSm
                        .copy(
                            color = colors.textDefault,
                            fontFeatureSettings = "tnum",
                        ).labelDirection(alignToLayout = true),
            )
        }
        if (note != null && !loading) {
            // .qx-stat-note: body.sm, color.text.subtlest; [data-tone] success / danger / warning; margin-top 2px
            val noteColor =
                when (tone) {
                    QeetrixStatTone.Neutral -> colors.textSubtlest
                    QeetrixStatTone.Positive -> colors.textSuccess
                    QeetrixStatTone.Negative -> colors.textDanger
                    QeetrixStatTone.Warning -> colors.textWarning
                }
            BasicText(
                note,
                Modifier.padding(top = 2.dp),
                style = typography.bodySm.copy(color = noteColor).labelDirection(alignToLayout = true),
            )
        }
    }
}

/**
 * Key numbers in a ruled strip — `@qeetrix/react`'s StatGroup: [QeetrixStat]s side by side in equal cells, each at
 * least 200 dp wide, `space.inset.md` × `space.inset.lg` padding, a 1 dp `color.border.default` rule between cells.
 * Where the cells wrap, the rule runs between the rows instead, and every row keeps the gutter.
 */
@Composable
public fun QeetrixStatGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val rule = QeetrixTheme.colors.borderDefault
    val lines = remember { StatRules() }
    Layout(
        content,
        modifier.drawBehind {
            val width = QeetrixBorder.WidthDefault.toPx()
            for (x in lines.columns) drawRect(rule, Offset(x.toFloat(), 0f), Size(width, lines.rowHeight.toFloat()))
            for (y in lines.rows) drawRect(rule, Offset(0f, y.toFloat()), Size(size.width, width))
        },
    ) { measurables, constraints ->
        val total = if (constraints.hasBoundedWidth) constraints.maxWidth else measurables.size * MIN_CELL.roundToPx()
        val perRow = (total / MIN_CELL.roundToPx()).coerceIn(1, measurables.size.coerceAtLeast(1))
        val cell = total / perRow
        val padX = QeetrixSpace.InsetLg.roundToPx()
        val padY = QeetrixSpace.InsetMd.roundToPx()
        val placeables = measurables.map { it.measure(Constraints.fixedWidth((cell - 2 * padX).coerceAtLeast(0))) }
        val rows = placeables.chunked(perRow)
        val heights = rows.map { row -> (row.maxOfOrNull { it.height } ?: 0) + 2 * padY }
        lines.columns = if (rows.size == 1) (1 until rows[0].size).map { it * cell } else (1 until perRow).map { it * cell }
        lines.rowHeight = if (rows.size == 1) heights[0] else 0
        lines.rows = heights.runningFold(0, Int::plus).drop(1).dropLast(1)
        layout(total, heights.sum()) {
            var y = 0
            rows.forEachIndexed { r, row ->
                row.forEachIndexed { c, placeable -> placeable.placeRelative(c * cell + padX, y + padY) }
                y += heights[r]
            }
        }
    }
}

/** Where a stat group's rules fall, from its last layout. */
private class StatRules {
    var columns: List<Int> = emptyList()
    var rowHeight: Int = 0
    var rows: List<Int> = emptyList()
}

/** `minmax(min(100%, 200px), 1fr)`: a cell is at least 200 dp. */
private val MIN_CELL = 200.dp
