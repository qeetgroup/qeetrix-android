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
                QeetrixTextarea(
                    TextFieldState(),
                    label = "Feedback",
                    placeholder = "What could be better?",
                    description = "Up to 500 characters.",
                )
                QeetrixTextarea(
                    TextFieldState("The export finished, but the totals row was missing.\nIt happened twice this week."),
                    label = "Details",
                    message = QeetrixFieldMessage("Remove the account number before sending."),
                )
                QeetrixTextarea(TextFieldState("12 Residency Road, Bengaluru"), label = "Address", readOnly = true)
                QeetrixTextarea(TextFieldState("Locked"), label = "Notes", enabled = false)
            }
        }
}
