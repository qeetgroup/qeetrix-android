package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.drawer.QeetrixActionSheet
import com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet
import com.qeetgroup.qeetrix.components.drawer.QeetrixSheetAction
import com.qeetgroup.qeetrix.components.drawer.QeetrixSheetActionSeparator
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemTone
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private val Statuses = listOf("All invoices", "Overdue", "Paid", "Drafts")

/** Bottom sheets: filters with a ruled footer, a long sheet that opens half-way, one only its actions close, and an action sheet. */
@Composable
fun BottomSheetSample() {
    SamplePage {
        SampleSection("Filters") {
            var filtering by rememberSaveable { mutableStateOf(false) }
            var status by rememberSaveable { mutableStateOf(Statuses.first()) }
            var draft by rememberSaveable { mutableStateOf(status) }
            QeetrixButton("Filter invoices", onClick = {
                draft = status
                filtering = true
            }, variant = QeetrixButtonVariant.Soft)
            QeetrixBottomSheet(
                open = filtering,
                onOpenChange = { filtering = it },
                title = "Filter invoices",
                description = "Show only the invoices you need.",
                footerStart = { QeetrixText("Showing: $draft", size = QeetrixTextSize.Sm) },
                footer = {
                    QeetrixButton("Clear", onClick = { draft = Statuses.first() }, variant = QeetrixButtonVariant.Ghost)
                    QeetrixButton("Show results", onClick = {
                        status = draft
                        filtering = false
                    })
                },
            ) {
                // A sample stand-in for a radio group: the chosen status carries the check.
                SampleFlow {
                    for (each in Statuses) {
                        QeetrixButton(
                            each,
                            onClick = { draft = each },
                            variant = QeetrixButtonVariant.Ghost,
                            icon = if (each == draft) QeetrixIconName.Check else null,
                        )
                    }
                }
            }
            SampleNote("Showing: $status. Swipe down, tap outside, press Back or Close to leave without applying.")
        }
        SampleSection("Long content, opening half-way") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Release notes", onClick = { open = true }, variant = QeetrixButtonVariant.Soft)
            QeetrixBottomSheet(open = open, onOpenChange = { open = it }, title = "What's new", partiallyExpandable = true) {
                Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapLg)) {
                    for (version in 12 downTo 1) {
                        Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                            QeetrixText("Version 2.$version", size = QeetrixTextSize.Lg)
                            QeetrixText(
                                "Faster sync, clearer invoices and fixes reported by teams like yours.",
                                tone = QeetrixTextTone.Subtle,
                            )
                        }
                    }
                }
            }
            SampleNote("Drag the sheet up to expand it; it scrolls its content before it moves itself.")
        }
        SampleSection("Closed by its own actions") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Give feedback", onClick = { open = true }, variant = QeetrixButtonVariant.Soft)
            QeetrixBottomSheet(
                open = open,
                onOpenChange = { open = it },
                title = "Send feedback",
                dismissible = false,
                footer = {
                    QeetrixButton("Discard", onClick = { open = false }, variant = QeetrixButtonVariant.Ghost)
                    QeetrixButton("Send", onClick = { open = false })
                },
            ) {
                QeetrixText("Tell us what could be better. Your workspace name is sent with it.")
            }
            SampleNote(
                "Not dismissible: no close button, and swiping down or tapping outside does nothing. Back still leaves, so the " +
                    "sheet is never a trap.",
            )
        }
        SampleSection("Action sheet") {
            var open by rememberSaveable { mutableStateOf(false) }
            var done by rememberSaveable { mutableStateOf<String?>(null) }
            QeetrixButton("Account actions", onClick = { open = true }, variant = QeetrixButtonVariant.Soft)
            QeetrixActionSheet(open = open, onOpenChange = { open = it }, title = "GitHub · priya") {
                QeetrixSheetAction("Copy code", onClick = { done = "Copy code" }, icon = QeetrixIconName.Copy)
                QeetrixSheetAction("Rename", onClick = { done = "Rename" }, icon = QeetrixIconName.Pencil)
                QeetrixSheetActionSeparator()
                QeetrixSheetAction(
                    "Delete account",
                    onClick = { done = "Delete account" },
                    icon = QeetrixIconName.Trash,
                    tone = QeetrixMenuItemTone.Danger,
                )
            }
            SampleNote(
                done?.let { "\"$it\" was chosen: the sheet closed first, then the action ran." }
                    ?: "An account's actions, built as menu items: the destructive one last, after a rule, in the danger tone.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun BottomSheetSamplePreview() {
    SamplePreview { BottomSheetSample() }
}
