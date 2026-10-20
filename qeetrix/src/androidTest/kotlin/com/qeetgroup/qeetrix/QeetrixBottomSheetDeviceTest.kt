package com.qeetgroup.qeetrix

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The bottom sheet as the screen shows it — its own window over the app's — which the JVM screenshots, drawn in
 * place, cannot show. Phase 7 regression: the sheet's elevation and top rule were passed to Material's sheet as a
 * modifier, which Material applies before it moves the sheet into place, so a sheet-sized white surface covered the
 * top of the screen while the real sheet sat at the bottom (seen in an independent app on API 29 and 36).
 */
@RunWith(AndroidJUnit4::class)
class QeetrixBottomSheetDeviceTest {
    @get:Rule
    val compose = createComposeRule()

    /** Mean brightness, 0–765, of a band across the screen at [fraction] of its height. */
    private fun Bitmap.brightnessAt(fraction: Float): Int {
        val y = (height * fraction).toInt()
        val samples = (1..9).map { getPixel(width * it / 10, y) }
        return samples.sumOf { Color.red(it) + Color.green(it) + Color.blue(it) } / samples.size
    }

    @Test
    fun theSheetDrawsOnlyWhereItIs() {
        compose.setContent {
            QeetrixTheme(QeetrixThemeMode.Light) {
                Box(Modifier.fillMaxSize().background(QeetrixTheme.colors.surfaceCanvas))
                QeetrixBottomSheet(visible = true, onDismissRequest = {}, title = "Share invoice") {
                    QeetrixButton("Copy link", onClick = {})
                }
            }
        }
        compose.waitUntil(5_000) { compose.onAllNodesWithText("Share invoice").fetchSemanticsNodes().isNotEmpty() }
        compose.waitForIdle()
        // Let the sheet's entrance and the scrim's fade finish.
        Thread.sleep(1_000)
        val title = compose.onNodeWithText("Share invoice").fetchSemanticsNode()
        val screen = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        // Above the sheet the light canvas shows through the scrim, darkened.
        val aboveTheSheet = screen.brightnessAt(0.12f)
        assertTrue("the screen above the sheet is not dimmed by the scrim (brightness $aboveTheSheet of 765)", aboveTheSheet < 600)
        // The sheet itself is light where it is: its background in the padding just before its title. (Not a band
        // across the screen: a tablet centres the sheet at 640 dp, and API 29 draws its navigation bar below it.)
        val x = (title.positionOnScreen.x - 8).toInt()
        val y = (title.positionOnScreen.y + title.size.height / 2).toInt()
        val sheet = screen.getPixel(x, y).let { Color.red(it) + Color.green(it) + Color.blue(it) }
        assertTrue("the sheet beside its title is not light (brightness $sheet of 765 at $x, $y)", sheet > 650)
    }
}
