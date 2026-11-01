package com.qeetgroup.qeetrix.internal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace

/**
 * A row of actions, end-aligned and wrapping, `space.gap.sm` apart — `.qx-dialog-footer`'s
 * `justify-content: flex-end; flex-wrap: wrap; gap: space.gap.sm`, shared by the dialog footer and the
 * form's actions. Put the tertiary action first and the primary or destructive one last, so it ends the
 * row (design rules §13). Children keep their own width; a row that runs out of room wraps, each line
 * end-aligned.
 */
@Composable
internal fun QeetrixActionRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content, modifier) { measurables, constraints ->
        val gap = QeetrixSpace.GapSm.roundToPx()
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
                rows.maxOf { row -> row.sumOf { it.width } + gap * (row.size - 1).coerceAtLeast(0) }
            }
        val height = rowHeights.sum() + gap * (rows.size - 1).coerceAtLeast(0)
        layout(width, height.coerceAtLeast(constraints.minHeight)) {
            var y = 0
            rows.forEachIndexed { index, row ->
                var x = width - (row.sumOf { it.width } + gap * (row.size - 1).coerceAtLeast(0))
                for (placeable in row) {
                    placeable.placeRelative(x, y + (rowHeights[index] - placeable.height) / 2)
                    x += placeable.width + gap
                }
                y += rowHeights[index] + gap
            }
        }
    }
}
