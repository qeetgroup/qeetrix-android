package com.qeetgroup.qeetrix.components.passwordinput

import androidx.compose.foundation.text.input.TextFieldState
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixPasswordInput] in `src/test/screenshots/passwordinput/`. Synthetic values only. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixPasswordInputScreenshotTest {
    @Test
    fun states() =
        captureStates("passwordinput") {
            ScreenshotStack {
                QeetrixPasswordInput(TextFieldState(), label = "Password", placeholder = "Enter your password")
                QeetrixPasswordInput(
                    TextFieldState("synthetic"),
                    label = "New password",
                    purpose = QeetrixPasswordPurpose.New,
                    description = "At least 12 characters.",
                )
                QeetrixPasswordInput(
                    TextFieldState("short"),
                    label = "Confirm password",
                    message = QeetrixFieldMessage("The passwords do not match."),
                )
                QeetrixPasswordInput(TextFieldState("synthetic"), label = "Without a reveal button", showToggle = false)
                QeetrixPasswordInput(TextFieldState("synthetic"), label = "Disabled", enabled = false)
            }
        }
}
