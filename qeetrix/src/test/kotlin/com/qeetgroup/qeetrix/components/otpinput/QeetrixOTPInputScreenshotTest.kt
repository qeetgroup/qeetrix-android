package com.qeetgroup.qeetrix.components.otpinput

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.rememberFocusedSource
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixOTPInput] in `src/test/screenshots/otpinput/`. Synthetic codes only. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixOTPInputScreenshotTest {
    @Test
    fun states() =
        captureStates("otpinput") {
            ScreenshotStack {
                QeetrixOTPInput(TextFieldState(), label = "Verification code", description = "Sent to •••• 4821")
                QeetrixOTPInput(TextFieldState("012"), label = "Typing (focused)", interactionSource = rememberFocusedSource())
                QeetrixOTPInput(TextFieldState("482105"), label = "Sign-in code", size = QeetrixOTPInputSize.Lg, groups = listOf(3, 3))
                QeetrixOTPInput(
                    TextFieldState("4821"),
                    label = "Failed attempt",
                    error = "That code has expired. Enter the new one we sent.",
                )
                QeetrixOTPInput(
                    TextFieldState("AB12CD"),
                    label = "Recovery code",
                    length = 10,
                    groups = listOf(5, 5),
                    validationType = QeetrixOTPValidationType.Alphanumeric,
                    modifier = Modifier.width(300.dp),
                )
                QeetrixOTPInput(
                    TextFieldState("7"),
                    label = "Number match (square slots)",
                    length = 2,
                    slotWidth = QeetrixOTPSlotWidth.Square,
                )
                QeetrixOTPInput(TextFieldState("482105"), label = "Read-only", readOnly = true)
                QeetrixOTPInput(TextFieldState("12"), label = "Disabled", length = 4, enabled = false)
            }
        }
}
