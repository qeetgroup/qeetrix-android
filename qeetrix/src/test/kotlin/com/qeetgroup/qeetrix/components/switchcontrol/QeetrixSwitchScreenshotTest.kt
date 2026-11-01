package com.qeetgroup.qeetrix.components.switchcontrol

import com.qeetgroup.qeetrix.components.input.rememberFocusedSource
import com.qeetgroup.qeetrix.components.input.rememberHoveredSource
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixSwitch] in `src/test/screenshots/switch/`. */
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
                    QeetrixSwitch(false, {}, invalid = true)
                    QeetrixSwitch(true, {}, invalid = true)
                    QeetrixSwitch(false, {}, interactionSource = rememberHoveredSource())
                    QeetrixSwitch(true, {}, interactionSource = rememberHoveredSource())
                    QeetrixSwitch(true, {}, interactionSource = rememberFocusedSource())
                    QeetrixSwitch(false, {}, enabled = false)
                    QeetrixSwitch(true, {}, enabled = false)
                }
                QeetrixSwitch(
                    true,
                    {},
                    label = "Auto-renew",
                    description = "Renews on the 1st of each month. A longer line that wraps under the label.",
                )
                QeetrixSwitch(false, {}, label = "Email alerts", interactionSource = rememberFocusedSource())
                QeetrixSwitch(true, {}, label = "Disabled", description = "Your admin sets this.", enabled = false)
            }
        }
}
