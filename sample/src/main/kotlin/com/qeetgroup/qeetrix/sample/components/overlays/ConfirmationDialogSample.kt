package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** "Are you sure?" with exactly two answers, destructive or not; the action runs once, after the dialog closes. */
@Composable
fun ConfirmationDialogSample() {
    SamplePage {
        var members by rememberSaveable { mutableIntStateOf(5) }
        var sent by rememberSaveable { mutableIntStateOf(0) }
        SampleSection("Destructive") {
            var asking by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Remove a member", onClick = { asking = true }, variant = QeetrixButtonVariant.Outline, enabled = members > 0)
            if (asking) {
                QeetrixConfirmationDialog(
                    title = "Remove Ada from the team?",
                    message = "They lose access to the team's projects straight away.",
                    confirmLabel = "Remove",
                    destructive = true,
                    onConfirm = { members-- },
                    onDismissRequest = { asking = false },
                )
            }
            SampleNote("$members members. A confirmation records the choice; whoever removes the member still checks it is allowed.")
        }
        SampleSection("Not destructive") {
            var asking by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Send invoice", onClick = { asking = true })
            if (asking) {
                QeetrixConfirmationDialog(
                    title = "Send the invoice now?",
                    message = "It goes to accounts@example.com.",
                    confirmLabel = "Send",
                    cancelLabel = "Not now",
                    onConfirm = { sent++ },
                    onDismissRequest = { asking = false },
                )
            }
            SampleNote("Sent $sent times.")
        }
    }
}

@PreviewLightDark
@Composable
private fun ConfirmationDialogSamplePreview() {
    SamplePreview { ConfirmationDialogSample() }
}
