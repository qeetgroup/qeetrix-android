package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.dialog.QeetrixFullScreenForm
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The full-screen form: a new account, with Save enabled once it has a name and a discard question once edited. */
@Composable
fun FullScreenFormSample() {
    var open by rememberSaveable { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf<String?>(null) }
    val name = rememberTextFieldState()
    val email = rememberTextFieldState()
    val notes = rememberTextFieldState()
    SamplePage {
        SampleSection("A new account") {
            QeetrixButton("New account", onClick = { open = true })
            SampleNote(
                saved?.let { "Saved \"$it\"." }
                    ?: "The whole screen on a phone, a sheet on a tablet. Type something, then Close: it asks before discarding.",
            )
        }
    }
    val edited = name.text.isNotEmpty() || email.text.isNotEmpty() || notes.text.isNotEmpty()
    QeetrixFullScreenForm(
        open,
        onOpenChange = {
            open = it
            if (!it) {
                name.clearText()
                email.clearText()
                notes.clearText()
            }
        },
        title = "New account",
        confirmLabel = "Save",
        onConfirm = {
            saved = name.text.toString()
            open = false
            name.clearText()
            email.clearText()
            notes.clearText()
        },
        dirty = edited,
        confirmEnabled = name.text.isNotBlank(),
    ) {
        QeetrixTextField(name, label = "Account name")
        QeetrixTextField(email, label = "Billing email", optional = true)
        QeetrixTextarea(notes, label = "Notes", optional = true)
    }
}

@PreviewLightDark
@Composable
private fun FullScreenFormSamplePreview() {
    SamplePreview { FullScreenFormSample() }
}
