package com.qeetgroup.qeetrix.gen.icons

import com.qeetgroup.qeetrix.gen.GeneratorError
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

/** A Qeetrix icon drawing, reduced to absolute path commands in the 24-unit grid. */
data class IconDrawing(
    val miterLimit: Double,
    val elements: List<Element>,
) {
    data class Element(
        val commands: List<PathCommand>,
        /** Filled with the icon colour; [evenOdd] selects the fill rule. */
        val fill: Boolean,
        val evenOdd: Boolean,
        /** Stroked with the icon colour at the icon's stroke width. */
        val stroke: Boolean,
        val lineCap: String,
        val lineJoin: String,
    )
}

/**
 * Absolute path commands. Unlike qeetrix-ios, which keeps circles and rounded rectangles as native
 * shapes, Compose's vector paths have no ellipse primitive, so ellipses and rounded corners are
 * emitted as cubic Béziers (four per full ellipse, k = 4/3·tan(π/8)); the largest radial error is
 * 0.027% of the radius, under 0.003 grid units for any Qeetrix icon.
 */
sealed interface PathCommand {
    data class Move(
        val x: Double,
        val y: Double,
    ) : PathCommand

    data class Line(
        val x: Double,
        val y: Double,
    ) : PathCommand

    data class Quad(
        val x1: Double,
        val y1: Double,
        val x: Double,
        val y: Double,
    ) : PathCommand

    data class Cubic(
        val x1: Double,
        val y1: Double,
        val x2: Double,
        val y2: Double,
        val x: Double,
        val y: Double,
    ) : PathCommand

    data object Close : PathCommand
}

/**
 * Parses the SVG subset qeetrix-icons uses — the same subset qeetrix-ios accepts. Anything outside it
 * (transforms, groups, gradients, masks, hard-coded colours, per-element stroke widths) fails with the
 * file and element named, so a drawing never renders differently from the web without someone
 * deciding it should.
 */
object SvgParser {
    private val geometry =
        mapOf(
            "path" to setOf("d"),
            "line" to setOf("x1", "y1", "x2", "y2"),
            "circle" to setOf("cx", "cy", "r"),
            "ellipse" to setOf("cx", "cy", "rx", "ry"),
            "rect" to setOf("x", "y", "width", "height", "rx", "ry"),
            "polyline" to setOf("points"),
            "polygon" to setOf("points"),
        )
    private val paint = setOf("fill", "fill-rule", "stroke", "stroke-linecap", "stroke-linejoin")
    private val root = setOf("xmlns", "viewBox", "fill", "stroke", "stroke-width", "stroke-linecap", "stroke-linejoin", "stroke-miterlimit")

    fun parse(
        text: String,
        context: String,
    ): IconDrawing {
        val factory =
            DocumentBuilderFactory.newInstance().apply {
                isNamespaceAware = false
                setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
                setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
            }
        val document =
            try {
                factory.newDocumentBuilder().parse(InputSource(StringReader(text)))
            } catch (error: Exception) {
                throw GeneratorError("$context: invalid SVG — ${error.message}.")
            }
        val svg = document.documentElement
        if (svg.tagName != "svg") throw GeneratorError("$context: the root element is <${svg.tagName}>, not <svg>.")
        val svgAttributes = attributes(svg)
        for (key in svgAttributes.keys) if (key !in root) throw GeneratorError("$context: unsupported <svg> attribute $key.")
        if (svgAttributes["viewBox"] != "0 0 24 24") throw GeneratorError("$context: viewBox must be \"0 0 24 24\".")
        svgAttributes["stroke-width"]?.let {
            if (it != "2") throw GeneratorError("$context: stroke-width $it; Qeetrix icons are drawn at 2 and scaled by the stroke token.")
        }

        val elements = mutableListOf<IconDrawing.Element>()
        val children = svg.childNodes
        for (index in 0 until children.length) {
            val node = children.item(index) as? Element ?: continue
            val name = node.tagName
            val label = "$context <$name>"
            if (node.childNodes.let { list -> (0 until list.length).any { list.item(it) is Element } }) {
                throw GeneratorError("$label: nested elements — groups are not supported.")
            }
            val allowed = geometry[name] ?: throw GeneratorError("$label: unsupported element.")
            val attributes = attributes(node)
            for (key in attributes.keys) if (key !in allowed && key !in paint) throw GeneratorError("$label: unsupported attribute $key.")

            fun inherited(key: String) = attributes[key] ?: svgAttributes[key]
            val fill = inherited("fill") ?: "black"
            val stroke = inherited("stroke") ?: "none"
            for ((key, value) in listOf("fill" to fill, "stroke" to stroke)) {
                if (value != "none" &&
                    value != "currentColor"
                ) {
                    throw GeneratorError("$label: $key=\"$value\" — icons are drawn in currentColor only.")
                }
            }
            val fillRule = attributes["fill-rule"] ?: "nonzero"
            if (fillRule != "nonzero" && fillRule != "evenodd") throw GeneratorError("$label: unsupported fill-rule $fillRule.")
            val cap = inherited("stroke-linecap") ?: "butt"
            val join = inherited("stroke-linejoin") ?: "miter"
            if (cap !in setOf("butt", "round", "square") || join !in setOf("miter", "round", "bevel")) {
                throw GeneratorError("$label: unsupported stroke-linecap/linejoin.")
            }
            elements +=
                IconDrawing.Element(
                    commands = commands(name, attributes, label),
                    fill = fill != "none",
                    evenOdd = fillRule == "evenodd",
                    stroke = stroke != "none",
                    lineCap = cap,
                    lineJoin = join,
                )
        }
        if (elements.isEmpty()) throw GeneratorError("$context: the drawing is empty.")
        val miterLimit = svgAttributes["stroke-miterlimit"]?.let { number(it, context) } ?: 4.0
        return IconDrawing(miterLimit, elements)
    }

    private fun attributes(element: Element): Map<String, String> {
        val map = LinkedHashMap<String, String>()
        val list = element.attributes
        for (index in 0 until list.length) map[list.item(index).nodeName] = list.item(index).nodeValue
        return map
    }

    private fun number(
        text: String,
        context: String,
    ): Double = text.trim().toDoubleOrNull() ?: throw GeneratorError("$context: \"$text\" is not a number.")

    private fun commands(
        name: String,
        attributes: Map<String, String>,
        context: String,
    ): List<PathCommand> {
        fun value(
            key: String,
            fallback: Double? = null,
        ): Double = attributes[key]?.let { number(it, context) } ?: fallback ?: throw GeneratorError("$context: missing $key.")
        return when (name) {
            "path" -> {
                PathData.parse(attributes["d"] ?: "", context)
            }

            "line" -> {
                listOf(PathCommand.Move(value("x1", 0.0), value("y1", 0.0)), PathCommand.Line(value("x2", 0.0), value("y2", 0.0)))
            }

            "circle" -> {
                val r = value("r")
                Shapes.ellipse(value("cx", 0.0), value("cy", 0.0), r, r)
            }

            "ellipse" -> {
                Shapes.ellipse(value("cx", 0.0), value("cy", 0.0), value("rx"), value("ry"))
            }

            "rect" -> {
                val width = value("width")
                val height = value("height")
                // SVG: a missing rx/ry takes the other's value; both clamp to half the side.
                var rx = attributes["rx"]?.toDoubleOrNull()
                var ry = attributes["ry"]?.toDoubleOrNull()
                rx = rx ?: ry
                ry = ry ?: rx
                Shapes.roundedRect(value("x", 0.0), value("y", 0.0), width, height, min(rx ?: 0.0, width / 2), min(ry ?: 0.0, height / 2))
            }

            "polyline", "polygon" -> {
                val numbers = PathData.numbers(attributes["points"] ?: "", context)
                if (numbers.size < 4 || numbers.size % 2 != 0) throw GeneratorError("$context: points needs an even number of coordinates.")
                val result = mutableListOf<PathCommand>(PathCommand.Move(numbers[0], numbers[1]))
                for (index in 2 until numbers.size step 2) result += PathCommand.Line(numbers[index], numbers[index + 1])
                if (name == "polygon") result += PathCommand.Close
                result
            }

            else -> {
                throw GeneratorError("$context: unsupported element.")
            }
        }
    }
}

/** Ellipses and rounded rectangles as path commands, drawn clockwise from the top-centre / top-left. */
object Shapes {
    /** The cubic control-point factor for a quarter ellipse: 4/3·tan(π/8). */
    val KAPPA: Double = 4.0 / 3.0 * tan(PI / 8)

    fun ellipse(
        cx: Double,
        cy: Double,
        rx: Double,
        ry: Double,
    ): List<PathCommand> {
        val kx = rx * KAPPA
        val ky = ry * KAPPA
        return listOf(
            PathCommand.Move(cx, cy - ry),
            PathCommand.Cubic(cx + kx, cy - ry, cx + rx, cy - ky, cx + rx, cy),
            PathCommand.Cubic(cx + rx, cy + ky, cx + kx, cy + ry, cx, cy + ry),
            PathCommand.Cubic(cx - kx, cy + ry, cx - rx, cy + ky, cx - rx, cy),
            PathCommand.Cubic(cx - rx, cy - ky, cx - kx, cy - ry, cx, cy - ry),
            PathCommand.Close,
        )
    }

    fun roundedRect(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        rx: Double,
        ry: Double,
    ): List<PathCommand> {
        if (rx == 0.0 || ry == 0.0) {
            return listOf(
                PathCommand.Move(x, y),
                PathCommand.Line(x + width, y),
                PathCommand.Line(x + width, y + height),
                PathCommand.Line(x, y + height),
                PathCommand.Close,
            )
        }
        val kx = rx * KAPPA
        val ky = ry * KAPPA
        val right = x + width
        val bottom = y + height
        return listOf(
            PathCommand.Move(x + rx, y),
            PathCommand.Line(right - rx, y),
            PathCommand.Cubic(right - rx + kx, y, right, y + ry - ky, right, y + ry),
            PathCommand.Line(right, bottom - ry),
            PathCommand.Cubic(right, bottom - ry + ky, right - rx + kx, bottom, right - rx, bottom),
            PathCommand.Line(x + rx, bottom),
            PathCommand.Cubic(x + rx - kx, bottom, x, bottom - ry + ky, x, bottom - ry),
            PathCommand.Line(x, y + ry),
            PathCommand.Cubic(x, y + ry - ky, x + rx - kx, y, x + rx, y),
            PathCommand.Close,
        )
    }
}

/**
 * SVG path data → absolute commands. Smooth curves are expanded and arcs are converted to cubic
 * Béziers (SVG 1.1 implementation notes F.6), so the library only needs move/line/quad/cubic/close.
 */
object PathData {
    private data class Point(
        val x: Double,
        val y: Double,
    )

    fun parse(
        text: String,
        context: String,
    ): List<PathCommand> {
        val scanner = Scanner(text, context)
        val result = mutableListOf<PathCommand>()
        var current = Point(0.0, 0.0)
        var start = Point(0.0, 0.0)
        var lastCubicControl: Point? = null
        var lastQuadControl: Point? = null
        var command: Char? = null
        while (true) {
            scanner.skipSeparators()
            val next = scanner.current ?: break
            if (next.isLetter()) {
                command = next
                scanner.position++
            } else if (command == null) {
                throw GeneratorError("$context: path data must start with a command.")
            }
            val op = command ?: break
            val relative = op.isLowerCase()

            fun point(
                x: Double,
                y: Double,
            ) = if (relative) Point(current.x + x, current.y + y) else Point(x, y)
            var cubicControl: Point? = null
            var quadControl: Point? = null
            when (op.uppercaseChar()) {
                'M' -> {
                    val p = point(scanner.number(), scanner.number())
                    result += PathCommand.Move(p.x, p.y)
                    current = p
                    start = p
                    command = if (relative) 'l' else 'L' // further pairs are implicit line-tos
                }

                'L' -> {
                    val p = point(scanner.number(), scanner.number())
                    result += PathCommand.Line(p.x, p.y)
                    current = p
                }

                'H' -> {
                    val x = scanner.number()
                    current = Point(if (relative) current.x + x else x, current.y)
                    result += PathCommand.Line(current.x, current.y)
                }

                'V' -> {
                    val y = scanner.number()
                    current = Point(current.x, if (relative) current.y + y else y)
                    result += PathCommand.Line(current.x, current.y)
                }

                'C' -> {
                    val c1 = point(scanner.number(), scanner.number())
                    val c2 = point(scanner.number(), scanner.number())
                    val p = point(scanner.number(), scanner.number())
                    result += PathCommand.Cubic(c1.x, c1.y, c2.x, c2.y, p.x, p.y)
                    cubicControl = c2
                    current = p
                }

                'S' -> {
                    val c1 = lastCubicControl?.let { Point(2 * current.x - it.x, 2 * current.y - it.y) } ?: current
                    val c2 = point(scanner.number(), scanner.number())
                    val p = point(scanner.number(), scanner.number())
                    result += PathCommand.Cubic(c1.x, c1.y, c2.x, c2.y, p.x, p.y)
                    cubicControl = c2
                    current = p
                }

                'Q' -> {
                    val c = point(scanner.number(), scanner.number())
                    val p = point(scanner.number(), scanner.number())
                    result += PathCommand.Quad(c.x, c.y, p.x, p.y)
                    quadControl = c
                    current = p
                }

                'T' -> {
                    val c = lastQuadControl?.let { Point(2 * current.x - it.x, 2 * current.y - it.y) } ?: current
                    val p = point(scanner.number(), scanner.number())
                    result += PathCommand.Quad(c.x, c.y, p.x, p.y)
                    quadControl = c
                    current = p
                }

                'A' -> {
                    val rx = scanner.number()
                    val ry = scanner.number()
                    val rotation = scanner.number()
                    val largeArc = scanner.flag()
                    val sweep = scanner.flag()
                    val p = point(scanner.number(), scanner.number())
                    result += arc(current, rx, ry, rotation, largeArc, sweep, p)
                    current = p
                }

                'Z' -> {
                    result += PathCommand.Close
                    current = start
                }

                else -> {
                    throw GeneratorError("$context: unsupported path command $op.")
                }
            }
            lastCubicControl = cubicControl
            lastQuadControl = quadControl
        }
        return result
    }

    /** The numbers in a `points` list. */
    fun numbers(
        text: String,
        context: String,
    ): List<Double> {
        val scanner = Scanner(text, context)
        val values = mutableListOf<Double>()
        while (true) {
            scanner.skipSeparators()
            if (scanner.current == null) return values
            values += scanner.number()
        }
    }

    /** SVG F.6.5 (endpoint → centre parameterisation) and F.6.6 (radius correction), then one cubic per quarter turn or less. */
    private fun arc(
        p0: Point,
        rxIn: Double,
        ryIn: Double,
        rotation: Double,
        largeArc: Boolean,
        sweep: Boolean,
        p1: Point,
    ): List<PathCommand> {
        if (p0.x == p1.x && p0.y == p1.y) return emptyList()
        var rx = abs(rxIn)
        var ry = abs(ryIn)
        if (rx == 0.0 || ry == 0.0) return listOf(PathCommand.Line(p1.x, p1.y))
        val phi = rotation * PI / 180
        val cosPhi = cos(phi)
        val sinPhi = sin(phi)
        val dx = (p0.x - p1.x) / 2
        val dy = (p0.y - p1.y) / 2
        val x1 = cosPhi * dx + sinPhi * dy
        val y1 = -sinPhi * dx + cosPhi * dy
        val lambda = (x1 * x1) / (rx * rx) + (y1 * y1) / (ry * ry)
        if (lambda > 1) {
            rx *= sqrt(lambda)
            ry *= sqrt(lambda)
        }
        val numerator = rx * rx * ry * ry - rx * rx * y1 * y1 - ry * ry * x1 * x1
        val denominator = rx * rx * y1 * y1 + ry * ry * x1 * x1
        val coefficient = (if (largeArc == sweep) -1.0 else 1.0) * sqrt(max(0.0, numerator / denominator))
        val cxPrime = coefficient * rx * y1 / ry
        val cyPrime = coefficient * -ry * x1 / rx
        val cx = cosPhi * cxPrime - sinPhi * cyPrime + (p0.x + p1.x) / 2
        val cy = sinPhi * cxPrime + cosPhi * cyPrime + (p0.y + p1.y) / 2

        fun angle(
            ux: Double,
            uy: Double,
            vx: Double,
            vy: Double,
        ) = atan2(ux * vy - uy * vx, ux * vx + uy * vy)
        val theta1 = angle(1.0, 0.0, (x1 - cxPrime) / rx, (y1 - cyPrime) / ry)
        var delta = angle((x1 - cxPrime) / rx, (y1 - cyPrime) / ry, (-x1 - cxPrime) / rx, (-y1 - cyPrime) / ry)
        if (!sweep && delta > 0) delta -= 2 * PI
        if (sweep && delta < 0) delta += 2 * PI
        val segments = max(1, ceil(abs(delta) / (PI / 2)).toInt())
        val step = delta / segments
        val k = 4.0 / 3.0 * tan(step / 4)

        fun at(t: Double) = Point(cx + rx * cos(t) * cosPhi - ry * sin(t) * sinPhi, cy + rx * cos(t) * sinPhi + ry * sin(t) * cosPhi)

        fun derivative(t: Double) = Point(-rx * sin(t) * cosPhi - ry * cos(t) * sinPhi, -rx * sin(t) * sinPhi + ry * cos(t) * cosPhi)
        val result = mutableListOf<PathCommand>()
        for (index in 0 until segments) {
            val a1 = theta1 + index * step
            val a2 = a1 + step
            val begin = at(a1)
            val end = if (index == segments - 1) p1 else at(a2) // land exactly on the endpoint
            val d1 = derivative(a1)
            val d2 = derivative(a2)
            result += PathCommand.Cubic(begin.x + k * d1.x, begin.y + k * d1.y, end.x - k * d2.x, end.y - k * d2.y, end.x, end.y)
        }
        return result
    }

    private class Scanner(
        val characters: String,
        val context: String,
    ) {
        var position = 0
        val current: Char? get() = characters.getOrNull(position)

        fun skipSeparators() {
            while (current?.let { it.isWhitespace() || it == ',' } == true) position++
        }

        /** An SVG number: `-1.5`, `.5`, `1e-3`. "1.5.5" is 1.5 then .5; "1-2" is 1 then -2. */
        fun number(): Double {
            skipSeparators()
            val text = StringBuilder()
            current?.let {
                if (it == '-' || it == '+') {
                    text.append(it)
                    position++
                }
            }
            var seenDot = false
            while (current?.let { it.isDigit() || (it == '.' && !seenDot) } == true) {
                if (current == '.') seenDot = true
                text.append(current)
                position++
            }
            if (current == 'e' || current == 'E') {
                text.append(current)
                position++
                current?.let {
                    if (it == '-' || it == '+') {
                        text.append(it)
                        position++
                    }
                }
                while (current?.isDigit() == true) {
                    text.append(current)
                    position++
                }
            }
            return text.toString().toDoubleOrNull() ?: throw GeneratorError("$context: expected a number at offset $position in path data.")
        }

        /** An arc flag: a single 0 or 1, which may be written without a separator ("a1 1 0 011 1"). */
        fun flag(): Boolean {
            skipSeparators()
            val c = current
            if (c != '0' && c != '1') throw GeneratorError("$context: expected an arc flag at offset $position.")
            position++
            return c == '1'
        }
    }
}
