package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.drawer.QeetrixSheetSize
import com.qeetgroup.qeetrix.components.drawer.QeetrixSideSheet
import com.qeetgroup.qeetrix.components.drawer.QeetrixSideSheetSide
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Side sheets: an edit form at the end edge, navigation at the start, and the bottom sheet they become on a phone. */
@Composable
fun SideSheetSample() {
    SamplePage {
        SampleSection("Edit a member") {
            var open by rememberSaveable { mutableStateOf(false) }
            var admin by rememberSaveable { mutableStateOf(false) }
            var draft by rememberSaveable { mutableStateOf(admin) }
            QeetrixButton("Edit Asha Rao", onClick = {
                draft = admin
                open = true
            }, variant = QeetrixButtonVariant.Soft)
            QeetrixSideSheet(
                open = open,
                onOpenChange = { open = it },
                title = "Edit member",
                description = "Changes apply at the next sign-in.",
                footer = {
                    QeetrixButton("Cancel", onClick = { open = false }, variant = QeetrixButtonVariant.Ghost)
                    QeetrixButton("Save", onClick = {
                        admin = draft
                        open = false
                    })
                },
            ) {
                QeetrixCheckbox(draft, { draft = it }, label = "Workspace admin", description = "Can invite and remove members.")
            }
            SampleNote(
                "Asha is ${if (admin) "an admin" else "a member"}. From 600 dp of window width the sheet opens at the end edge, " +
                    "full height and 560 dp wide; on a phone it is a bottom sheet. Swipe it towards its edge, tap outside, " +
                    "press Back or Close to leave without saving.",
            )
        }
        SampleSection("At the start, small") {
            var open by rememberSaveable { mutableStateOf(false) }
            QeetrixButton("Workspaces", onClick = { open = true }, variant = QeetrixButtonVariant.Soft)
            QeetrixSideSheet(
                open = open,
                onOpenChange = { open = it },
                title = "Workspaces",
                side = QeetrixSideSheetSide.Start,
                size = QeetrixSheetSize.Sm,
            ) {
                for (each in listOf("Acme", "Qeet Pay", "Northwind")) {
                    QeetrixButton(each, onClick = { open = false }, variant = QeetrixButtonVariant.Ghost)
                }
            }
            QeetrixText(
                "side = Start puts it at the reading start — the right edge in right to left. Size Sm is 400 dp.",
                tone = QeetrixTextTone.Subtle,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SideSheetSamplePreview() {
    SamplePreview { SideSheetSample() }
}
