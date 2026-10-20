package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private val StatusOptions =
    listOf(
        QeetrixOption("all", "All invoices"),
        QeetrixOption("overdue", "Overdue", "Past their due date"),
        QeetrixOption("paid", "Paid"),
        QeetrixOption("draft", "Drafts"),
    )

/** Bottom sheets: filters with a footer, a long sheet that opens half-way, and one gestures cannot close. */
@Composable
fun BottomSheetSample() {
    SamplePage {
        SampleSection("Filters") {
            var filtering by rememberSaveable { mutableStateOf(false) }
            var status by rememberSaveable { mutableStateOf("all") }
            var draft by rememberSaveable { mutableStateOf("all") }
            QeetrixButton("Filter invoices", onClick = {
                draft = status
                filtering = true
            }, variant = QeetrixButtonVariant.Outline)
            QeetrixBottomSheet(
                visible = filtering,
                onDismissRequest = { filtering = false },
                title = "Filter invoices",
                description = "Show only the invoices you need.",
                footer = {
                    QeetrixButton("Show results", onClick = {
                        status = draft
                        filtering = false
                    }, modifier = Modifier.fillMaxWidth())
                },
            ) {
                QeetrixRadioGroup(draft, { draft = it }, StatusOptions, label = "Status")
            }
            val shown = StatusOptions.first { it.value == status }.label
            SampleNote("Showing: $shown. Swipe down, tap outside, press Back or Close to leave without applying.")
        }
        SampleSection("Long content, opening half-way") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Release notes", onClick = { open = true }, variant = QeetrixButtonVariant.Outline)
            QeetrixBottomSheet(visible = open, onDismissRequest = { open = false }, title = "What's new", partiallyExpandable = true) {
                for (version in 12 downTo 1) {
                    QeetrixText("Version 2.$version", variant = QeetrixTextVariant.Large)
                    QeetrixText("Faster sync, clearer invoices and fixes reported by teams like yours.", variant = QeetrixTextVariant.Muted)
                }
            }
            SampleNote("Drag the sheet up to expand it; it scrolls its content before it moves itself.")
        }
        SampleSection("A step to finish deliberately") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Give feedback", onClick = { open = true }, variant = QeetrixButtonVariant.Outline)
            QeetrixBottomSheet(
                visible = open,
                onDismissRequest = { open = false },
                title = "Send feedback",
                dismissOnGesture = false,
                footer = { QeetrixButton("Send", onClick = { open = false }, modifier = Modifier.fillMaxWidth()) },
            ) {
                QeetrixTextarea(rememberTextFieldState(), label = "What could be better?")
            }
            SampleNote("Swiping down and tapping outside do nothing here; Back and Close still leave, so the sheet is never a trap.")
        }
    }
}

@PreviewLightDark
@Composable
private fun BottomSheetSamplePreview() {
    SamplePreview { BottomSheetSample() }
}
