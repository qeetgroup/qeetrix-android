package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.dialog.QeetrixAlertDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogAction
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogActionRole
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Alert dialogs: a destructive decision (Cancel added by the dialog) and a three-way choice. */
@Composable
fun AlertDialogSample() {
    SamplePage {
        var outcome by rememberSaveable { mutableStateOf("No answer yet.") }
        SampleSection("A destructive decision") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Delete workspace…", onClick = { open = true }, variant = QeetrixButtonVariant.Destructive)
            if (open) {
                QeetrixAlertDialog(
                    title = "Delete this workspace?",
                    message = "Its projects and files are removed for everyone. This can't be undone.",
                    icon = QeetrixIconName.Trash,
                    actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { outcome = DELETED }),
                    onDismissRequest = {
                        open = false
                        if (outcome == "No answer yet.") outcome = "Dismissed: nothing happened."
                    },
                )
            }
        }
        SampleSection("Several answers") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Leave the editor", onClick = { open = true }, variant = QeetrixButtonVariant.Outline)
            if (open) {
                QeetrixAlertDialog(
                    title = "Save your changes?",
                    message = "You edited the description of this invoice.",
                    actions =
                        listOf(
                            QeetrixDialogAction("Keep editing", QeetrixDialogActionRole.Cancel) { outcome = "Kept editing." },
                            QeetrixDialogAction("Discard", QeetrixDialogActionRole.Destructive) { outcome = "Changes discarded." },
                            QeetrixDialogAction("Save") { outcome = "Changes saved." },
                        ),
                    onDismissRequest = { open = false },
                )
            }
        }
        SampleNote(
            "$outcome A tap outside never answers an alert; Back closes it without an action. Opened from a keyboard, " +
                "focus starts on the cancelling action.",
        )
    }
}

private const val DELETED = "Deleted (in this sample, nothing is)."

@PreviewLightDark
@Composable
private fun AlertDialogSamplePreview() {
    SamplePreview { AlertDialogSample() }
}
