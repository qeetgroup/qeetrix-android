package com.qeetgroup.qeetrix

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qeetgroup.qeetrix.quality.LocalizedCatalog
import com.qeetgroup.qeetrix.quality.QualityCatalog
import com.qeetgroup.qeetrix.quality.clipping
import com.qeetgroup.qeetrix.quality.isClipped
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * The JVM's text-clipping scan on a device's real text stack and density, left to right and right to left, over
 * every catalog and localized specimen at the device's own font scale. Run with
 * `./gradlew :qeetrix:connectedDebugAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
class QeetrixLayoutDeviceTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun noTextIsClippedLeftToRight() = assertNoClipping(LayoutDirection.Ltr)

    /** Composed right to left from the start, as a right-to-left app renders: not switched from left to right. */
    @Test
    fun noTextIsClippedRightToLeft() = assertNoClipping(LayoutDirection.Rtl)

    private fun assertNoClipping(direction: LayoutDirection) {
        var specimen by mutableStateOf(QualityCatalog.first())
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                QeetrixTheme {
                    Box(Modifier.verticalScroll(rememberScrollState()).width(360.dp).padding(16.dp)) { specimen.content() }
                }
            }
        }
        val slack = with(compose.density) { 1.5.dp.toPx() }
        val clipped = mutableListOf<String>()
        for (each in QualityCatalog + LocalizedCatalog) {
            compose.runOnIdle { specimen = each }
            for (node in compose
                .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.GetTextLayoutResult), useUnmergedTree = true)
                .fetchSemanticsNodes()) {
                val layouts = mutableListOf<TextLayoutResult>()
                node.config[SemanticsActions.GetTextLayoutResult].action?.invoke(layouts)
                val text = layouts.firstOrNull() ?: continue
                val byDesign = each.name == "navigation-bar" && (0 until text.lineCount).any { text.isLineEllipsized(it) }
                if (text.isClipped(slack) && !byDesign) {
                    clipped +=
                        "${each.name} [$direction]: \"${text.layoutInput.text}\" ${text.clipping()} paragraph ${text.getParagraphDirection(
                            0,
                        )}"
                }
            }
        }
        assertTrue(clipped.joinToString("\n", prefix = "${clipped.size} clipped:\n"), clipped.isEmpty())
    }

    /**
     * The localized specimens drawn right to left on the device, saved for review (`adb pull` from the app's
     * external files directory): the reference for how right-to-left text renders on Android's own text stack.
     */
    @Test
    fun rightToLeftRenderingIsCapturedForReview() {
        var specimen by mutableStateOf(LocalizedCatalog.first())
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QeetrixTheme {
                    Box(Modifier.background(QeetrixTheme.colors.surfaceCanvas).width(411.dp).padding(16.dp)) { specimen.content() }
                }
            }
        }
        val directory = InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null)
        for (each in LocalizedCatalog) {
            compose.runOnIdle { specimen = each }
            val image = compose.onRoot().captureToImage().asAndroidBitmap()
            File(directory, "rtl_${each.name}.png").outputStream().use { image.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }
}
