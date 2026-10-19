package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordPurpose
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/**
 * Secure entry, as a UI demonstration only: nothing typed here is checked, stored or sent, and the page
 * never shows what was typed — only its length.
 */
@Composable
fun PasswordInputSample() {
    SamplePage {
        SampleSection("Signing in") {
            QeetrixPasswordInput(
                rememberTextFieldState(),
                label = "Password",
                purpose = QeetrixPasswordPurpose.Current,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
            SampleNote("Marked as a current password, so AutoFill can offer saved ones — if the device's provider does.")
        }
        SampleSection("Choosing a password") {
            val chosen = rememberTextFieldState()
            val confirm = rememberTextFieldState()
            val long = chosen.text.length >= 12
            QeetrixPasswordInput(
                chosen,
                label = "New password",
                purpose = QeetrixPasswordPurpose.New,
                description = "At least 12 characters.",
                message = if (long) QeetrixFieldMessage("Long enough", QeetrixFieldMessageTone.Success) else null,
            )
            QeetrixPasswordInput(
                confirm,
                label = "Confirm new password",
                purpose = QeetrixPasswordPurpose.New,
                message =
                    if (confirm.text.isNotEmpty() && confirm.text.toString() != chosen.text.toString()) {
                        QeetrixFieldMessage("The passwords do not match.")
                    } else {
                        null
                    },
            )
            SampleNote(
                "These rules are the sample's, not Qeetrix's. Show the password: the text, cursor, focus and keyboard " +
                    "stay where they were. Copy and cut are off.",
            )
        }
        SampleSection("States") {
            QeetrixPasswordInput(rememberTextFieldState("not-a-real-pw"), label = "Without the reveal button", showToggle = false)
            QeetrixPasswordInput(rememberTextFieldState("not-a-real-pw"), label = "Disabled", enabled = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun PasswordInputSamplePreview() {
    SamplePreview { PasswordInputSample() }
}
