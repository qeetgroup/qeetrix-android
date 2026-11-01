package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import kotlinx.coroutines.delay

/** Dialogs: one the app closes once its action succeeds, the three intents, and the sizes. */
@Composable
fun DialogSample() {
    SamplePage {
        SampleSection("Closed by the app") {
            var open by rememberSaveable { mutableStateOf(false) }
            var publishing by rememberSaveable { mutableStateOf(false) }
            var published by rememberSaveable { mutableIntStateOf(0) }
            LaunchedEffect(publishing) {
                if (publishing) {
                    delay(SIMULATED_WORK_MILLIS)
                    publishing = false
                    published++
                    open = false
                }
            }
            QeetrixButton("Publish report", onClick = { open = true })
            QeetrixDialog(
                open = open,
                onOpenChange = { if (!publishing) open = it },
                title = "Publish the March report?",
                description = "Everyone in the workspace can read it once it is published.",
                footerStart = { QeetrixText("Preview first", size = QeetrixTextSize.Sm) },
                footer = {
                    QeetrixButton("Cancel", onClick = { open = false }, variant = QeetrixButtonVariant.Ghost, enabled = !publishing)
                    QeetrixButton("Publish", onClick = { publishing = true }, loading = publishing)
                },
            ) {
                QeetrixText("Its charts and tables are frozen at today's figures.")
            }
            SampleNote(
                "Published $published times. The dialog closes when the app says so: here, once publishing has finished. " +
                    "Back, the close button and a tap on the scrim ask to close; this sample ignores them while it publishes.",
            )
        }
        SampleSection("Intents") {
            var intent by rememberSaveable { mutableStateOf<QeetrixDialogIntent?>(null) }
            SampleFlow {
                for (each in QeetrixDialogIntent.entries) {
                    QeetrixButton(each.value, onClick = { intent = each }, variant = QeetrixButtonVariant.Soft)
                }
            }
            val shown = intent
            QeetrixDialog(
                open = shown != null,
                onOpenChange = { if (!it) intent = null },
                title = "A ${shown?.value ?: "standard"} dialog",
                description = "The line on top says what kind of decision it is.",
                intent = shown ?: QeetrixDialogIntent.Standard,
                size = QeetrixDialogSize.Sm,
                footer = { QeetrixButton("Done", onClick = { intent = null }) },
            )
            SampleNote(
                "Ember for a task, red for something destructive, amber for a warning. A danger dialog has no close button " +
                    "and ignores taps outside; Back still closes it, as Android never traps Back.",
            )
        }
        SampleSection("Sizes") {
            var size by rememberSaveable { mutableStateOf<QeetrixDialogSize?>(null) }
            SampleFlow {
                for (each in QeetrixDialogSize.entries) {
                    QeetrixButton(each.value, onClick = { size = each }, variant = QeetrixButtonVariant.Ghost)
                }
            }
            val shown = size
            QeetrixDialog(
                open = shown != null,
                onOpenChange = { if (!it) size = null },
                title = "A ${shown?.value ?: "md"} dialog",
                size = shown ?: QeetrixDialogSize.Md,
            ) {
                QeetrixText(
                    "Its width is capped by the size and always leaves 24 dp to each side of the window; long content scrolls " +
                        "between the header and the footer.",
                )
            }
        }
    }
}

private const val SIMULATED_WORK_MILLIS = 1500L

@PreviewLightDark
@Composable
private fun DialogSamplePreview() {
    SamplePreview { DialogSample() }
}
