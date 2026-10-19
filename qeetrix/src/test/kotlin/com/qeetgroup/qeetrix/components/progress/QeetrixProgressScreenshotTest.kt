package com.qeetgroup.qeetrix.components.progress

import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixProgress] in `src/test/screenshots/progress/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixProgressScreenshotTest {
    @Test
    fun states() =
        captureStates("progress") {
            ScreenshotStack {
                QeetrixProgress(0f, label = "Not started")
                QeetrixProgress(0.42f, label = "Uploading report.pdf")
                QeetrixProgress(1f, label = "Done", size = QeetrixProgressSize.Lg)
                QeetrixProgress(0.6f, size = QeetrixProgressSize.Sm, contentDescription = "Small")
                QeetrixProgress(null, label = "Preparing export (still: animations off)")
            }
        }
}
