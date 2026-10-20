package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbar
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarHost
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarResult
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbarType
import com.qeetgroup.qeetrix.components.toast.rememberQeetrixSnackbarHostState
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import kotlinx.coroutines.launch

/** Snackbars: the four types, an action with its result, repeats and replacement, and the system Toast's place. */
@Composable
fun SnackbarSample() {
    val snackbars = rememberQeetrixSnackbarHostState()
    val scope = rememberCoroutineScope()
    var outcome by rememberSaveable { mutableStateOf("Nothing shown yet.") }
    SamplePage {
        SampleSection("Show one") {
            SampleFlow {
                QeetrixButton(
                    "Info",
                    onClick = { scope.launch { snackbars.show("Conversation archived") } },
                    variant = QeetrixButtonVariant.Outline,
                )
                QeetrixButton("Success", onClick = {
                    scope.launch {
                        snackbars.show(
                            "Invoice sent",
                            QeetrixSnackbarType.Success,
                            description = "INV-2041 to accounts@example.com",
                        )
                    }
                }, variant = QeetrixButtonVariant.Outline)
                QeetrixButton("Warning", onClick = {
                    scope.launch {
                        snackbars.show(
                            "Sync is paused",
                            QeetrixSnackbarType.Warning,
                            description = "Reconnect to keep working offline.",
                        )
                    }
                }, variant = QeetrixButtonVariant.Outline)
                QeetrixButton("Error with retry", onClick = {
                    scope.launch {
                        val result = snackbars.show("Couldn't send the invoice", QeetrixSnackbarType.Error, actionLabel = "Retry")
                        outcome = if (result == QeetrixSnackbarResult.ActionPerformed) "Retry chosen." else "Error dismissed."
                    }
                }, variant = QeetrixButtonVariant.Outline)
            }
            SampleNote(
                "$outcome One snackbar at a time: a new one replaces the last, and showing the same one again restarts its " +
                    "time without a second announcement. A snackbar with an action stays while TalkBack is on.",
            )
        }
        SampleSection("Undo") {
            QeetrixButton("Archive a conversation", onClick = {
                scope.launch {
                    val result = snackbars.show("Conversation archived", actionLabel = "Undo")
                    outcome = if (result == QeetrixSnackbarResult.ActionPerformed) "Undone: the conversation is back." else "Archived."
                }
            })
        }
        SampleSection("Drawn on its own") {
            QeetrixSnackbar("Copied", type = QeetrixSnackbarType.Success)
            SampleNote(
                "For messages that must outlive the app's window — after the last screen closes — Android's own " +
                    "android.widget.Toast is the platform tool; Qeetrix does not imitate it.",
            )
        }
        // The page's own host; a real screen puts it at the bottom of its layout.
        Box(Modifier.fillMaxWidth().height(160.dp)) {
            QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
        }
    }
}

@PreviewLightDark
@Composable
private fun SnackbarSamplePreview() {
    SamplePreview { SnackbarSample() }
}
