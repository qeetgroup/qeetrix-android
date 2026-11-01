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
                for (size in QeetrixHeadingSize.entries) QeetrixHeading("${size.value}: Qeet Sans", level = 2, size = size)
                for (size in QeetrixTextSize.entries) {
                    for (weight in QeetrixTextWeight.entries) {
                        QeetrixText(
                            "body ${size.value} ${weight.value}: Qeet Sans, नमस्ते",
                            size = size,
                            weight = weight,
                        )
                    }
                }
                QeetrixText("subtle tone", tone = QeetrixTextTone.Subtle)
                QeetrixText("inv_2K9 · mono", mono = true)
                QeetrixText(
                    "A long line that truncates after one line with an ellipsis at its end, as the web's truncate does",
                    maxLines = 1,
                )
            }
        }
}
