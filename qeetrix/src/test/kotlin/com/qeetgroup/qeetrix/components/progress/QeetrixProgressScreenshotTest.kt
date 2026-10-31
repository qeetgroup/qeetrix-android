package com.qeetgroup.qeetrix.components.progress

import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/progress/`: progress at 0, part way, done and failed, unlabelled, and unknown (the
 * reduced-motion full bar, since animations are off); meters in every tone, one with a target.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixProgressScreenshotTest {
    @Test
    fun states() =
        captureStates("progress") {
            ScreenshotStack {
                QeetrixProgress(0f, label = "Not started", showValue = true)
                QeetrixProgress(42f, label = "Uploading report.pdf", showValue = true)
                QeetrixProgress(100f, label = "Uploaded", showValue = true, tone = QeetrixProgressTone.Success)
                QeetrixProgress(64f, label = "Import failed", tone = QeetrixProgressTone.Danger)
                QeetrixProgress(60f, contentDescription = "Unlabelled")
                QeetrixProgress(null, label = "Preparing export")
                for (tone in QeetrixMeterTone.entries) {
                    QeetrixMeter(
                        30f + 10f * tone.ordinal,
                        label = "Meter ${tone.value}",
                        showValue = true,
                        tone = tone,
                    )
                }
                QeetrixMeter(40f, label = "People with two-step", showValue = true, target = 80f)
            }
        }
}
