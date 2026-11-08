package com.qeetgroup.qeetrix.internal

import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.PathNode

// Decodes the generator's one-string form of an icon drawing (Tools/QeetrixGen, IconEmitter.encode): the miter limit,
// then each element after a `|` — four flags (fill `-`/`n`/`e`, stroke `-`/`s`, cap `b`/`r`/`s`, join `m`/`r`/`b`) and
// its absolute commands `M x y`, `L x y`, `Q x1 y1 x y`, `C x1 y1 x2 y2 x y` and `Z`, numbers separated by spaces.

/** The drawing [encoded] holds; [icon] names it in an error. */
internal fun decodeIconDrawing(
    encoded: String,
    icon: String,
): QeetrixIconDrawing {
    val parts = encoded.split('|')
    val miterLimit = parts.first().toFloatOrNull() ?: malformed(icon, "miter limit \"${parts.first()}\"")
    val elements =
        parts.drop(1).map { element ->
            if (element.length < 4) malformed(icon, "element \"$element\"")
            QeetrixIconElement(
                fill = element[0] != '-',
                evenOdd = element[0] == 'e',
                stroke = element[1] == 's',
                cap =
                    when (element[2]) {
                        'b' -> StrokeCap.Butt
                        'r' -> StrokeCap.Round
                        's' -> StrokeCap.Square
                        else -> malformed(icon, "line cap '${element[2]}'")
                    },
                join =
                    when (element[3]) {
                        'm' -> StrokeJoin.Miter
                        'r' -> StrokeJoin.Round
                        'b' -> StrokeJoin.Bevel
                        else -> malformed(icon, "line join '${element[3]}'")
                    },
                nodes = decodePath(element, icon),
            )
        }
    return QeetrixIconDrawing(miterLimit, elements)
}

private fun decodePath(
    element: String,
    icon: String,
): List<PathNode> {
    val nodes = ArrayList<PathNode>()
    val numbers = FloatArray(6)
    var index = 4
    while (index < element.length) {
        val command = element[index++]
        val count =
            when (command) {
                'M', 'L' -> 2
                'Q' -> 4
                'C' -> 6
                'Z' -> 0
                else -> malformed(icon, "path command '$command'")
            }
        for (n in 0 until count) {
            if (n > 0) {
                if (element.getOrNull(index) != ' ') malformed(icon, "path after '$command'")
                index++
            }
            var end = index
            while (end < element.length && element[end] != ' ' && !element[end].isUpperCase()) end++
            numbers[n] = element.substring(index, end).toFloatOrNull() ?: malformed(icon, "number in '$command'")
            index = end
        }
        nodes +=
            when (command) {
                'M' -> PathNode.MoveTo(numbers[0], numbers[1])
                'L' -> PathNode.LineTo(numbers[0], numbers[1])
                'Q' -> PathNode.QuadTo(numbers[0], numbers[1], numbers[2], numbers[3])
                'C' -> PathNode.CurveTo(numbers[0], numbers[1], numbers[2], numbers[3], numbers[4], numbers[5])
                else -> PathNode.Close
            }
    }
    return nodes
}

private fun malformed(
    icon: String,
    what: String,
): Nothing = error("The $icon icon's drawing has a malformed $what; regenerate it with Scripts/generate-tokens.")
