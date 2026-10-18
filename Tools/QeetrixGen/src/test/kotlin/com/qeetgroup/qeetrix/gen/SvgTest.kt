package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.icons.PathCommand
import com.qeetgroup.qeetrix.gen.icons.PathData
import com.qeetgroup.qeetrix.gen.icons.Shapes
import com.qeetgroup.qeetrix.gen.icons.SvgParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

class SvgTest {
    private fun svg(
        body: String,
        root: String = """fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"""",
    ) = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" $root>$body</svg>"""

    @Test
    fun outlineElementsInheritTheRootPaint() {
        val drawing = SvgParser.parse(svg("""<circle cx="11" cy="11" r="8"/><path d="m21 21-4.34-4.34"/>"""), "search")
        assertEquals(2, drawing.elements.size)
        val circle = drawing.elements[0]
        assertTrue(circle.stroke && !circle.fill)
        assertEquals("round", circle.lineCap)
        assertEquals(4.0, drawing.miterLimit, 0.0)
        // A circle is a move, four quarter-ellipse cubics and a close.
        assertEquals(6, circle.commands.size)
        assertEquals(PathCommand.Move(11.0, 3.0), circle.commands.first())
    }

    @Test
    fun quarterEllipseCubicsStayOnTheCircle() {
        val commands = Shapes.ellipse(0.0, 0.0, 10.0, 10.0)
        val first = commands[1] as PathCommand.Cubic
        // The curve's midpoint (t = 0.5) of a kappa cubic lies within 0.027% of the radius.
        val x = 0.125 * 0 + 0.375 * first.x1 + 0.375 * first.x2 + 0.125 * first.x
        val y = 0.125 * -10 + 0.375 * first.y1 + 0.375 * first.y2 + 0.125 * first.y
        assertEquals(10.0, hypot(x, y), 10.0 * 0.00028)
    }

    @Test
    fun arcsBecomeCubicsThatLandOnTheirEndpoint() {
        val commands = PathData.parse("M 2 12 A 10 10 0 0 1 22 12", "arc")
        assertEquals(PathCommand.Move(2.0, 12.0), commands[0])
        assertEquals(3, commands.size) // a half turn is two quarter-turn cubics
        val last = commands.last() as PathCommand.Cubic
        assertEquals(22.0, last.x, 0.0)
        assertEquals(12.0, last.y, 0.0)
    }

    @Test
    fun relativeCommandsAndCompactFlagsParse() {
        val commands = PathData.parse("m9 12 2 2 4-4a1 1 0 011 1z", "check")
        assertEquals(PathCommand.Line(11.0, 14.0), commands[1])
        assertEquals(PathCommand.Line(15.0, 10.0), commands[2])
        assertEquals(PathCommand.Close, commands.last())
    }

    @Test
    fun filledDrawingsKeepTheirFillRule() {
        val drawing = SvgParser.parse(svg("""<path d="M0 0H24V24Z" fill-rule="evenodd"/>""", root = """fill="currentColor""""), "filled")
        assertTrue(drawing.elements.single().fill && drawing.elements.single().evenOdd)
    }

    @Test
    fun anythingOutsideTheSubsetFails() {
        assertThrows(GeneratorError::class.java) { SvgParser.parse(svg("""<g><path d="M0 0"/></g>"""), "group") }
        assertThrows(GeneratorError::class.java) { SvgParser.parse(svg("""<path d="M0 0" transform="scale(2)"/>"""), "transform") }
        assertThrows(GeneratorError::class.java) { SvgParser.parse(svg("""<path d="M0 0" fill="#F26D0E"/>"""), "colour") }
        assertThrows(GeneratorError::class.java) { SvgParser.parse(svg("<path d=\"M0 0\"/>", root = """stroke-width="1.5""""), "stroke") }
        assertThrows(GeneratorError::class.java) {
            SvgParser.parse("""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48"><path d="M0 0"/></svg>""", "viewBox")
        }
    }
}
