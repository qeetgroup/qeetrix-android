package com.qeetgroup.qeetrix.components.otpinput

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
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
                val focus = remember { FocusRequester() }
                LaunchedEffect(Unit) { focus.requestFocus() }
                // Focused: the ring and caret on the typing position.
                QeetrixOTPInput(
                    TextFieldState("12"),
                    Modifier.focusRequester(focus),
                    label = "Verification code",
                    description = "Sent to •••• 4821",
                )
                QeetrixOTPInput(
                    TextFieldState("048213"),
                    label = "Grouped",
                    groupSize = 3,
                    message = QeetrixFieldMessage("Code accepted", QeetrixFieldMessageTone.Success),
                )
                QeetrixOTPInput(
                    TextFieldState("9917"),
                    label = "Expired",
                    length = 4,
                    message = QeetrixFieldMessage("That code has expired."),
                )
                QeetrixOTPInput(
                    TextFieldState("AB12"),
                    label = "Recovery code",
                    length = 8,
                    characters = QeetrixOTPCharacters.Alphanumeric,
                    groupSize = 4,
                )
                QeetrixOTPInput(TextFieldState(), label = "Disabled", enabled = false)
            }
        }
}
