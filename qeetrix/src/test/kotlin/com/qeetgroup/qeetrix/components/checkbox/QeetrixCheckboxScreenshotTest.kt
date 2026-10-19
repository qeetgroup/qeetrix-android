package com.qeetgroup.qeetrix.components.checkbox

import androidx.compose.ui.state.ToggleableState
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixCheckbox] and [QeetrixTriStateCheckbox] in `src/test/screenshots/checkbox/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixCheckboxScreenshotTest {
    @Test
    fun states() =
        captureStates("checkbox") {
            ScreenshotStack {
                ScreenshotFlow {
                    QeetrixCheckbox(false, {})
                    QeetrixCheckbox(true, {})
                    QeetrixTriStateCheckbox(ToggleableState.Indeterminate, {})
                    QeetrixCheckbox(false, {}, invalid = true)
                    QeetrixCheckbox(true, {}, enabled = false)
                    QeetrixCheckbox(false, {}, enabled = false)
                }
                QeetrixCheckbox(true, {}, label = "Product updates", description = "About once a month. Unsubscribe whenever you like.")
                QeetrixTriStateCheckbox(ToggleableState.Indeterminate, {}, label = "All members")
                QeetrixCheckbox(false, {}, label = "Accept the terms", invalid = true)
                QeetrixCheckbox(true, {}, label = "Managed by your organisation", enabled = false)
            }
        }
}
