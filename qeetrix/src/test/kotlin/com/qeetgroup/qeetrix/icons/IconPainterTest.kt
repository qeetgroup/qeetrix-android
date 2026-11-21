package com.qeetgroup.qeetrix.icons

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconStroke
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/**
 * The public drawing of an icon — [rememberQeetrixIconPainter] and [imageVector]: drawn in a Canvas, it paints exactly
 * the pixels [QeetrixIcon] paints, for each shape and variant, a variant upstream doesn't draw, and right to left.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class IconPainterTest {
    @get:Rule
    val compose = createComposeRule()

    private class Case(
        val name: QeetrixIconName,
        val shape: QeetrixIconShape,
        val variant: QeetrixIconVariant,
    )

    private var case by mutableStateOf(Case(QeetrixIconName.Send, QeetrixIconShape.Sharp, QeetrixIconVariant.Outline))
    private var direction by mutableStateOf(LayoutDirection.Ltr)

    private fun show() {
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                QeetrixTheme {
                    val tint = QeetrixTheme.colors.iconBrand
                    Row {
                        QeetrixIcon(
                            case.name,
                            contentDescription = null,
                            modifier = Modifier.testTag("icon"),
                            size = 24.dp,
                            shape = case.shape,
                            variant = case.variant,
                            tint = tint,
                        )
                        val painter = rememberQeetrixIconPainter(case.name, case.shape, case.variant)
                        Canvas(Modifier.testTag("canvas").size(24.dp)) {
                            with(painter) { draw(size, colorFilter = ColorFilter.tint(tint)) }
                        }
                    }
                }
            }
        }
    }

    private fun ImageBitmap.pixels(): List<Int> {
        val map = toPixelMap()
        return (0 until height).flatMap { y -> (0 until width).map { x -> map[x, y].hashCode() } }
    }

    private fun drawn(tag: String) = compose.onNodeWithTag(tag).captureToImage().pixels()

    @Test
    fun aCanvasDrawsWhatTheIconDraws() {
        show()
        val cases =
            listOf(
                Case(QeetrixIconName.Send, QeetrixIconShape.Sharp, QeetrixIconVariant.Outline),
                Case(QeetrixIconName.Send, QeetrixIconShape.Round, QeetrixIconVariant.Filled),
                Case(QeetrixIconName.Bell, QeetrixIconShape.Sharp, QeetrixIconVariant.Filled),
                // No filled check upstream: both fall back to the outline.
                Case(QeetrixIconName.Check, QeetrixIconShape.Round, QeetrixIconVariant.Filled),
            )
        for (each in cases) {
            for (way in LayoutDirection.entries) {
                compose.runOnIdle {
                    case = each
                    direction = way
                }
                val icon = drawn("icon")
                assertTrue("${each.name} drew nothing", icon.toSet().size > 1)
                assertEquals("${each.name} ${each.shape} ${each.variant} $way", icon, drawn("canvas"))
            }
        }
    }

    /** Send points the reading way: drawn right to left, the painter turns round as the icon does. */
    @Test
    fun aMirroringIconTurnsRoundRightToLeft() {
        show()
        val forward = drawn("canvas")
        compose.runOnIdle { direction = LayoutDirection.Rtl }
        assertNotEquals(forward, drawn("canvas"))
    }

    @Test
    fun theVectorIsTheIconsGridWithItsFallbackAndMirroring() {
        val send = QeetrixIconName.Send.imageVector()
        assertEquals(24f, send.viewportWidth)
        assertEquals(24.dp, send.defaultWidth)
        assertTrue(send.autoMirror)
        assertFalse(QeetrixIconName.Send.imageVector(mirrorInRightToLeft = false).autoMirror)
        assertFalse(QeetrixIconName.ChevronRight.imageVector().autoMirror)
        assertFalse(QeetrixIconName.Check.hasDrawing(QeetrixIconShape.Round, QeetrixIconVariant.Filled))

        fun paths(vector: androidx.compose.ui.graphics.vector.ImageVector) =
            vector.root.filterIsInstance<androidx.compose.ui.graphics.vector.VectorPath>().map { it.pathData to it.strokeLineWidth }
        val outline = paths(QeetrixIconName.Check.imageVector(QeetrixIconShape.Round, QeetrixIconVariant.Outline, QeetrixIconStroke.Thin))
        assertTrue(outline.isNotEmpty())
        assertEquals(
            outline,
            paths(QeetrixIconName.Check.imageVector(QeetrixIconShape.Round, QeetrixIconVariant.Filled, QeetrixIconStroke.Thin)),
        )
        assertEquals(QeetrixIconStroke.Thin, outline.first().second)
    }
}
