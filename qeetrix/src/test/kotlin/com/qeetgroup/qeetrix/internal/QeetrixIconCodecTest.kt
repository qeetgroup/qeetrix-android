package com.qeetgroup.qeetrix.internal

import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.PathNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class QeetrixIconCodecTest {
    @Test
    fun decodesEveryFlagAndCommand() {
        val drawing = decodeIconDrawing("4|-srrM8 2L8 5Z|essmM-1.5 0.25Q1 2 3 4C1 2 3 4 5 6.125|n-bbM0 0L24 24", "test")
        assertEquals(4f, drawing.miterLimit)
        val (stroked, evenOdd, nonZero) = drawing.elements
        assertFalse(stroked.fill)
        assertTrue(stroked.stroke)
        assertEquals(StrokeCap.Round, stroked.cap)
        assertEquals(StrokeJoin.Round, stroked.join)
        assertEquals(listOf(PathNode.MoveTo(8f, 2f), PathNode.LineTo(8f, 5f), PathNode.Close), stroked.nodes)

        assertTrue(evenOdd.fill && evenOdd.evenOdd && evenOdd.stroke)
        assertEquals(StrokeCap.Square, evenOdd.cap)
        assertEquals(StrokeJoin.Miter, evenOdd.join)
        assertEquals(
            listOf(PathNode.MoveTo(-1.5f, 0.25f), PathNode.QuadTo(1f, 2f, 3f, 4f), PathNode.CurveTo(1f, 2f, 3f, 4f, 5f, 6.125f)),
            evenOdd.nodes,
        )

        assertTrue(nonZero.fill && !nonZero.evenOdd && !nonZero.stroke)
        assertEquals(StrokeCap.Butt, nonZero.cap)
        assertEquals(StrokeJoin.Bevel, nonZero.join)
    }

    @Test
    fun aMalformedDrawingNamesItsIcon() {
        for (bad in listOf("x|-srrM0 0", "4|-sr", "4|-sxrM0 0", "4|-srxM0 0", "4|-srrA0 0", "4|-srrM0", "4|-srrM0 y")) {
            val error = assertThrows(IllegalStateException::class.java) { decodeIconDrawing(bad, "broken") }
            assertTrue(bad, error.message!!.startsWith("The broken icon's drawing has a malformed"))
        }
    }
}
