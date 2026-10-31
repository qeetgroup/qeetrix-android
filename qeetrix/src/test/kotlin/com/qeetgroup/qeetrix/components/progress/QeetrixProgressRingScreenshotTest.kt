package com.qeetgroup.qeetrix.components.progress

import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixProgressRing] in `src/test/screenshots/progressring/`: sizes, shares and tones. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixProgressRingScreenshotTest {
    @Test
    fun states() =
        captureStates("progressring") {
            ScreenshotStack {
                ScreenshotFlow {
                    for (size in QeetrixProgressRingSize.entries) {
                        for (share in listOf(0f, 0.25f, 0.6f, 1f)) QeetrixProgressRing(share, size = size)
                    }
                }
                ScreenshotFlow {
                    for (tone in QeetrixMeterTone.entries) QeetrixProgressRing(0.7f, size = QeetrixProgressRingSize.Lg, tone = tone)
                }
            }
        }
}
