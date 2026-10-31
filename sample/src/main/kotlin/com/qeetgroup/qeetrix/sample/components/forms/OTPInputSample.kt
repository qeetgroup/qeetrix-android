package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInputSize
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPValidationType
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** One-time codes: a sign-in code with a failed attempt, a console code, and an alphanumeric recovery code. */
@Composable
fun OTPInputSample() {
    SamplePage {
        SampleSection("Sign-in code") {
            val code = rememberTextFieldState()
            var failed by rememberSaveable { mutableStateOf(false) }
            QeetrixOTPInput(
                code,
                label = "Verification code",
                description = "Sent to •••• 4821. Try 123456.",
                size = QeetrixOTPInputSize.Lg,
                groups = listOf(3, 3),
                error = if (failed) "That code didn't work. Enter the new one we sent." else null,
                onValueComplete = { entered ->
                    failed = entered != "123456"
                    if (failed) code.clearText() // a failed attempt clears the code
                },
            )
            SampleNote(
                "One field drawn as slots: paste and SMS AutoFill fill it at once, and TalkBack reads one field. " +
                    "The check here is this sample's.",
            )
        }
        SampleSection("Recovery code") {
            QeetrixOTPInput(
                rememberTextFieldState(),
                label = "Recovery code",
                length = 10,
                groups = listOf(5, 5),
                validationType = QeetrixOTPValidationType.Alphanumeric,
                smsCodeAutofill = false,
            )
        }
        SampleSection("Disabled") {
            QeetrixOTPInput(rememberTextFieldState("48"), label = "Code", length = 4, enabled = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun OTPInputSamplePreview() {
    SamplePreview { OTPInputSample() }
}
