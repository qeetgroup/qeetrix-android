package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCharacters
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/**
 * Verification-code entry, as a UI demonstration: the sample checks codes against nothing. It only shows what
 * the component reports — that a code is complete — never the code itself.
 */
@Composable
fun OTPInputSample() {
    SamplePage {
        SampleSection("A six-digit code") {
            val code = rememberTextFieldState()
            var completions by rememberSaveable { mutableIntStateOf(0) }
            val complete = completions > 0 && code.text.length == 6
            QeetrixOTPInput(
                code,
                label = "Verification code",
                groupSize = 3,
                description = "Sent to •••• 4821.",
                message = if (complete) QeetrixFieldMessage("Code complete", QeetrixFieldMessageTone.Success) else null,
            ) { completions++ }
            SampleFlow {
                QeetrixButton("Clear", onClick = { code.clearText() }, variant = QeetrixButtonVariant.Outline, size = QeetrixButtonSize.Sm)
            }
            SampleNote(
                "Type, paste a whole code (\"123-456\" works) or let SMS AutoFill offer one. Completed $completions times; " +
                    "nothing is submitted for you.",
            )
        }
        SampleSection("Codes with letters") {
            QeetrixOTPInput(
                rememberTextFieldState(),
                label = "Recovery code",
                length = 8,
                characters = QeetrixOTPCharacters.Alphanumeric,
                groupSize = 4,
                smsCodeAutofill = false,
            )
            SampleNote("Not offered SMS AutoFill: a recovery code never comes by message.")
        }
        SampleSection("States") {
            QeetrixOTPInput(
                rememberTextFieldState("4821"),
                label = "Expired",
                length = 4,
                message = QeetrixFieldMessage("That code has expired."),
            )
            QeetrixOTPInput(rememberTextFieldState(), label = "Disabled", enabled = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun OTPInputSamplePreview() {
    SamplePreview { OTPInputSample() }
}

@Preview(name = "Right to left", locale = "ar")
@Composable
private fun OTPInputRightToLeftPreview() {
    SamplePreview { QeetrixOTPInput(rememberTextFieldState("123"), label = "Verification code") }
}
