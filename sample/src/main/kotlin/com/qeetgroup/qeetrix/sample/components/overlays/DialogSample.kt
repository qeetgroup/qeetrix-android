package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogSize
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** A dialog with a form that validates before it closes, and the dialog sizes. */
@Composable
fun DialogSample() {
    SamplePage {
        SampleSection("A form in a dialog") {
            var workspace by rememberSaveable { mutableStateOf("Northwind") }
            var renaming by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Rename workspace", onClick = { renaming = true }, variant = QeetrixButtonVariant.Outline)
            if (renaming) {
                val name = rememberTextFieldState(workspace)
                var error by rememberSaveable { mutableStateOf<String?>(null) }
                QeetrixDialog(
                    title = "Rename workspace",
                    description = "Everyone in the workspace sees the new name.",
                    onDismissRequest = { renaming = false },
                    footer = {
                        QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Outline)
                        QeetrixButton("Save", onClick = {
                            val value = name.text.trim().toString()
                            if (value.isEmpty()) {
                                error = "Enter a name."
                            } else {
                                workspace = value
                                renaming = false
                            }
                        })
                    },
                ) {
                    QeetrixTextField(name, label = "Name", required = true, message = error?.let { QeetrixFieldMessage(it) })
                }
            }
            SampleNote("Workspace: $workspace. The dialog closes only when the app says so, so an empty name keeps it open with its error.")
        }
        SampleSection("Sizes") {
            var size by rememberSaveable { mutableStateOf<String?>(null) }
            QeetrixRadioGroup(
                size,
                { size = it },
                QeetrixDialogSize.entries.map { QeetrixOption(it.value, it.value) },
                label = "Open a dialog of size",
            )
            val open = QeetrixDialogSize.entries.firstOrNull { it.value == size }
            if (open != null) {
                QeetrixDialog(title = "A ${open.value} dialog", onDismissRequest = { size = null }, size = open) {
                    QeetrixText("Its width is capped by the size and always leaves 16 dp to each side of the window; long content scrolls.")
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DialogSamplePreview() {
    SamplePreview { DialogSample() }
}
