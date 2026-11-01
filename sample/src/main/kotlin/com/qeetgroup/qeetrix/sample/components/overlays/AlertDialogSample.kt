package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.dialog.QeetrixAlertDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogAction
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogActionRole
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Alert dialogs: a destructive decision (Cancel added by the dialog) and a warning with a secondary choice. */
@Composable
fun AlertDialogSample() {
    SamplePage {
        var outcome by rememberSaveable { mutableStateOf("No answer yet.") }
        SampleSection("A destructive decision") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Delete workspace…", onClick = { open = true }, tone = QeetrixButtonTone.Danger)
            QeetrixAlertDialog(
                open = open,
                onOpenChange = {
                    open = it
                    if (!it && outcome == "No answer yet.") outcome = "Dismissed: nothing happened."
                },
                title = "Delete this workspace?",
                description = "Its projects and files are removed for everyone. This can't be undone.",
                actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { outcome = DELETED }),
            )
        }
        SampleSection("A warning with a second choice") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Leave the editor", onClick = { open = true }, variant = QeetrixButtonVariant.Soft)
            QeetrixAlertDialog(
                open = open,
                onOpenChange = { open = it },
                title = "Leave with unsaved changes?",
                description = "You edited the description of this invoice.",
                intent = QeetrixDialogIntent.Warning,
                actions =
                    listOf(
                        QeetrixDialogAction("Keep editing", QeetrixDialogActionRole.Cancel) { outcome = "Kept editing." },
                        QeetrixDialogAction("Save draft", QeetrixDialogActionRole.Secondary) { outcome = "Draft saved." },
                        QeetrixDialogAction("Save and leave") { outcome = "Saved." },
                    ),
            )
        }
        SampleNote(
            "$outcome A tap outside never answers an alert dialog; Back closes it without an action. Opened from a keyboard, " +
                "focus starts on the cancelling action, which comes first in the footer.",
        )
    }
}

private const val DELETED = "Deleted (in this sample, nothing is)."

@PreviewLightDark
@Composable
private fun AlertDialogSamplePreview() {
    SamplePreview { AlertDialogSample() }
}
