package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordAutoComplete
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Password fields: signing in, choosing a new one with a rule, a secret that is not a password, and the states. */
@Composable
fun PasswordInputSample() {
    SamplePage {
        SampleSection("Signing in") {
            QeetrixPasswordInput(
                rememberTextFieldState(),
                label = "Password",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
            SampleNote(
                "Show password reveals it in the same field — the text, cursor and keyboard stay — and TalkBack hears " +
                    "\"Password shown\". With a hardware keyboard, \"Caps Lock is on\" appears while it is.",
            )
        }
        SampleSection("Choosing a new password") {
            val password = rememberTextFieldState()
            var visible by rememberSaveable { mutableStateOf(false) }
            QeetrixPasswordInput(
                password,
                label = "New password",
                description = "At least 12 characters.",
                error = if (password.text.isNotEmpty() && password.text.length < 12) "Use at least 12 characters." else null,
                autoComplete = QeetrixPasswordAutoComplete.NewPassword,
                visible = visible,
                onVisibleChange = { visible = it },
            )
            SampleNote("AutoFill can suggest a strong password here and offer to save it. The rule is this sample's.")
        }
        SampleSection("A secret, read-only, and disabled") {
            QeetrixPasswordInput(
                rememberTextFieldState("sk_live_synthetic_0042"),
                label = "Client secret",
                readOnly = true,
                autoComplete = QeetrixPasswordAutoComplete.Off,
                description = "Shown once. Copy it now.",
            )
            QeetrixPasswordInput(rememberTextFieldState("synthetic"), label = "Password", enabled = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun PasswordInputSamplePreview() {
    SamplePreview { PasswordInputSample() }
}
