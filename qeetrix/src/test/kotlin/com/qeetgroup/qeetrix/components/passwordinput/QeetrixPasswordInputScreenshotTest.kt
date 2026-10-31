package com.qeetgroup.qeetrix.components.passwordinput

import androidx.compose.foundation.text.input.TextFieldState
import com.qeetgroup.qeetrix.components.input.rememberFocusedSource
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
                QeetrixPasswordInput(TextFieldState(), label = "Password")
                QeetrixPasswordInput(TextFieldState("Synthetic-0042"), label = "Password", description = "At least 12 characters.")
                QeetrixPasswordInput(
                    TextFieldState("Synthetic-0042"),
                    label = "New password",
                    autoComplete = QeetrixPasswordAutoComplete.NewPassword,
                    visible = true,
                    onVisibleChange = {},
                    interactionSource = rememberFocusedSource(),
                )
                QeetrixPasswordInput(TextFieldState("short"), label = "Password", error = "Use at least 12 characters.")
                QeetrixPasswordInput(
                    TextFieldState("sk_live_synthetic"),
                    label = "Client secret",
                    readOnly = true,
                    autoComplete = QeetrixPasswordAutoComplete.Off,
                )
                QeetrixPasswordInput(TextFieldState("Synthetic-0042"), label = "Disabled", enabled = false)
            }
        }
}
