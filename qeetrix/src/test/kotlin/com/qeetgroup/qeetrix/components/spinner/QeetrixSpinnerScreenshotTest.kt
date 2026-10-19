package com.qeetgroup.qeetrix.components.spinner

import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixSpinner] in `src/test/screenshots/spinner/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSpinnerScreenshotTest {
    @Test
    fun states() =
        captureStates("spinner") {
            ScreenshotFlow { for (size in QeetrixSpinnerSize.entries) QeetrixSpinner(size = size) }
        }
}
