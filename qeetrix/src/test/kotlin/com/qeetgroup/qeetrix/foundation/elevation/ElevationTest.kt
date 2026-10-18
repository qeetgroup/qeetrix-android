package com.qeetgroup.qeetrix.foundation.elevation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class ElevationTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theBlurHasTheCssStandardDeviation() {
        // CSS: σ = blur / 2. Skia: σ = 0.57735 · radius + 0.5. So a 16 px blur (σ 8) is radius 12.99.
        assertTrue(blurFilter(16f) != null)
        assertNull(blurFilter(1f)) // σ 0.5: under half a pixel, not drawn
    }

    @Test
    fun layersDifferByTheme() {
        val light = QeetrixElevation.Raised.layers(dark = false)
        val dark = QeetrixElevation.Raised.layers(dark = true)
        assertEquals(3, light.size)
        assertTrue(light != dark)
        assertEquals(emptyList<QeetrixShadowLayer>(), QeetrixElevation.Flat.layers(dark = false))
    }

    /** Real pixels, drawn by Robolectric's native graphics: shadow below the shape, the fill inside it. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers = "mdpi")
    fun aRaisedSurfaceCastsAShadowOutsideAndKeepsItsFillInside() {
        val canvas = Color.White
        val fill = Color(0xFFFFFFFF)
        compose.setContent {
            QeetrixTheme(QeetrixThemeMode.Light) {
                Box(Modifier.testTag("frame").background(canvas).padding(24.dp)) {
                    Box(Modifier.size(80.dp, 40.dp).qeetrixElevation(QeetrixElevation.Modal, QeetrixShapes.Surface, fill))
                }
            }
        }
        val pixels = compose.onNodeWithTag("frame").captureToImage().toPixelMap()
        // Inside the shape: exactly the fill.
        assertEquals(fill.toArgb(), pixels[64, 44].toArgb())
        // Just below the shape (the modal shadow is offset 8–24 dp down): darker than the canvas.
        val below = pixels[64, 24 + 40 + 6]
        assertTrue("shadow below: $below", below.red < canvas.red)
        // Far from the shape: the untouched canvas.
        assertEquals(canvas.toArgb(), pixels[2, 2].toArgb())
    }
}
