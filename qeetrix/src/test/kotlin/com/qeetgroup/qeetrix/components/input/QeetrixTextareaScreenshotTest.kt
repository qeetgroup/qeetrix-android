package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.input.TextFieldState
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixTextarea] in `src/test/screenshots/textarea/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixTextareaScreenshotTest {
    @Test
    fun states() =
        captureStates("textarea") {
            ScreenshotStack {
                QeetrixTextarea(TextFieldState(), label = "Delivery notes", placeholder = "e.g. Gate code 4471", optional = true)
                QeetrixTextarea(
                    TextFieldState("The invoice total does not match the order.\nPlease check line 3."),
                    label = "Feedback",
                    description = "Up to 500 characters.",
                    rows = 2,
                    interactionSource = rememberFocusedSource(),
                )
                QeetrixTextarea(TextFieldState("Flat 4B"), label = "Address", error = "Add a PIN code to the address.", rows = 1)
                QeetrixTextarea(TextFieldState("Approved by finance on 12 Oct."), label = "Audit note", readOnly = true, rows = 1)
                QeetrixTextarea(TextFieldState("Locked"), label = "Disabled", enabled = false, rows = 1)
            }
        }
}
