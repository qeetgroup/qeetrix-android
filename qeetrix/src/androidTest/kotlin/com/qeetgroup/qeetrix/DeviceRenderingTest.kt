package com.qeetgroup.qeetrix

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qeetgroup.qeetrix.foundation.typography.QeetrixFonts
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What only a real device proves: that the platform's font stack applies the variable-font axes, and
 * that themed colours reach the screen. Run with `./gradlew :qeetrix:connectedDebugAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
class DeviceRenderingTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theGeomAxisChangesTheLetterforms() {
        val text = "Qeetrix geometry ag"
        var neutral = 0
        var geometric = 0
        compose.setContent {
            val measurer: TextMeasurer = rememberTextMeasurer()
            val body = QeetrixTextRole.Body.spec

            fun width(family: FontFamily) = measurer.measure(text, QeetrixTypography.style(body).copy(fontFamily = family)).size.width
            neutral = width(QeetrixFonts.family(body.copy(geometry = 0f)))
            geometric = width(QeetrixFonts.family(body.copy(geometry = 100f)))
        }
        compose.waitForIdle()
        // The same text set in two GEOM positions of one file has different advances: the axis is applied.
        assertNotEquals(neutral, geometric)
    }

    @Test
    fun theDarkThemeCanvasReachesTheScreen() {
        compose.setContent {
            QeetrixTheme(QeetrixThemeMode.Dark) {
                Box(Modifier.testTag("canvas").size(32.dp).background(QeetrixTheme.colors.surfaceCanvas))
            }
        }
        val pixel = compose.onNodeWithTag("canvas").captureToImage().toPixelMap()[16, 16]
        assertEquals(QeetrixDarkColors.surfaceCanvas.toArgb(), pixel.toArgb())
    }
}
