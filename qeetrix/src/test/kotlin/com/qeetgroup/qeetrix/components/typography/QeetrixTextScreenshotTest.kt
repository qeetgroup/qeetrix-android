package com.qeetgroup.qeetrix.components.typography

import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixText] in `src/test/screenshots/text/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixTextScreenshotTest {
    @Test
    fun states() =
        captureStates("text") {
            ScreenshotStack {
                for (variant in QeetrixTextVariant.entries) QeetrixText("${variant.value}: Qeet Sans, नमस्ते", variant = variant)
            }
        }
}
