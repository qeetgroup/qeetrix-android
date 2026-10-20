package com.qeetgroup.qeetrix.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke

/**
 * Stacks its children top to bottom, each as wide as the stack, [spacing] apart. With a [rule], a hairline
 * in that colour runs between each pair, [spacing] from both — none above the first or below the last —
 * and takes its own height, as a CSS border does: the web's `border-b … last:border-b-0` and `divide-y`.
 * The rules come from the children's positions, so a child that appears, disappears or changes height
 * moves them.
 */
@Composable
internal fun RuledColumn(
    rule: Color?,
    modifier: Modifier = Modifier,
    spacing: Dp = 0.dp,
    content: @Composable () -> Unit,
) {
    val rules = remember { RuleOffsets() }
    Layout(
        content,
        modifier.drawBehind {
            if (rule == null) return@drawBehind
            val height = QeetrixStroke.Hairline.toPx()
            for (y in rules.offsets) drawRect(rule, Offset(0f, y.toFloat()), Size(size.width, height))
        },
    ) { measurables, constraints ->
        val gap = spacing.roundToPx()
        val hairline = if (rule != null) QeetrixStroke.Hairline.roundToPx().coerceAtLeast(1) else 0
        val stretch = constraints.hasBoundedWidth
        val childConstraints = constraints.copy(minWidth = if (stretch) constraints.maxWidth else 0, minHeight = 0)
        val placeables = measurables.map { it.measure(childConstraints) }
        val tops = IntArray(placeables.size)
        val offsets = IntArray((placeables.size - 1).coerceAtLeast(0))
        var y = 0
        placeables.forEachIndexed { index, placeable ->
            if (index > 0) {
                y += gap
                offsets[index - 1] = y
                y += hairline + if (rule != null) gap else 0
            }
            tops[index] = y
            y += placeable.height
        }
        val width = if (stretch) constraints.maxWidth else (placeables.maxOfOrNull { it.width } ?: 0).coerceAtLeast(constraints.minWidth)
        rules.update(if (rule != null) offsets else IntArray(0))
        layout(width, y.coerceIn(constraints.minHeight, constraints.maxHeight)) {
            placeables.forEachIndexed { index, placeable -> placeable.placeRelative(0, tops[index]) }
        }
    }
}

/** Where a [RuledColumn]'s rules are, written by its layout and read when it draws. */
private class RuleOffsets {
    var offsets by mutableStateOf(IntArray(0))
        private set

    fun update(next: IntArray) {
        if (!next.contentEquals(offsets)) offsets = next
    }
}

/**
 * The height of [style]'s first line at the current font scale, so an icon beside wrapping text can be
 * centred on that line, as the web centres it with a top margin.
 */
@Composable
internal fun firstLineHeight(style: TextStyle): Dp = with(LocalDensity.current) { style.fontSize.toDp() } * style.lineHeight.value
