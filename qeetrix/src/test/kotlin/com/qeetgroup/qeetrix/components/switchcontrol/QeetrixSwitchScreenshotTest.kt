package com.qeetgroup.qeetrix.components.switchcontrol

import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixSwitch] in `src/test/screenshots/switch/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSwitchScreenshotTest {
    @Test
    fun states() =
        captureStates("switch") {
            ScreenshotStack {
                ScreenshotFlow {
                    QeetrixSwitch(false, {})
                    QeetrixSwitch(true, {})
                    QeetrixSwitch(false, {}, size = QeetrixSwitchSize.Sm)
                    QeetrixSwitch(true, {}, size = QeetrixSwitchSize.Sm)
                    QeetrixSwitch(true, {}, enabled = false)
                    QeetrixSwitch(false, {}, invalid = true)
                }
                QeetrixSwitch(true, {}, label = "Email alerts", description = "Sent within a minute of the event.")
                QeetrixSwitch(false, {}, label = "Weekly digest", enabled = false)
            }
        }
}
