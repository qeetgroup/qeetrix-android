package com.qeetgroup.qeetrix.quality

import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/localization/`: each localized specimen — Devanagari, Telugu and Arabic text
 * falling back from Qeet Sans to the platform's fonts, and pseudo-expanded English — in every configuration, one image
 * each (`<specimen>_<config>.png`), so the 2× ones fit the screenshot window. Reviewed for missing glyphs, clipping,
 * line breaks and mirroring.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixLocalizationScreenshotTest {
    @Test
    fun states() {
        for (specimen in LocalizedCatalog) {
            for (config in ScreenshotConfig.entries) {
                captureQeetrix("localization", specimen.name, config) { ScreenshotStack { specimen.content() } }
            }
        }
    }
}
