package com.qeetgroup.qeetrix.quality

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/localization/`: the localized specimens — Devanagari, Telugu and Arabic
 * text falling back from Qeet Sans to the platform's fonts, and pseudo-expanded English — in every configuration.
 * Reviewed for missing glyphs, clipping and mirroring.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixLocalizationScreenshotTest {
    @Test
    fun states() =
        captureStates("localization") {
            ScreenshotStack {
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) { for (specimen in LocalizedCatalog) specimen.content() }
            }
        }
}
