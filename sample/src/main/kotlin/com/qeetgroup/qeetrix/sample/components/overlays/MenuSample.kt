package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemVariant
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuLabel
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuSeparator
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Menus from an overflow button and from a text button, with groups, a disabled item and a destructive one. */
@Composable
fun MenuSample() {
    SamplePage {
        var last by rememberSaveable { mutableStateOf("Nothing chosen yet.") }
        var confirmDelete by rememberSaveable { mutableStateOf(false) }
        SampleSection("Overflow and text triggers") {
            SampleFlow {
                var overflow by remember { mutableStateOf(false) }
                QeetrixMenu(
                    expanded = overflow,
                    onExpandedChange = { overflow = it },
                    trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { overflow = true }) },
                ) {
                    QeetrixMenuLabel("Project")
                    QeetrixMenuItem("Rename", onClick = { last = "Rename" }, icon = QeetrixIconName.Pencil)
                    QeetrixMenuItem("Duplicate", onClick = { last = "Duplicate" }, icon = QeetrixIconName.Copy)
                    QeetrixMenuItem("Share", onClick = { last = "Share" }, icon = QeetrixIconName.Share2, enabled = false)
                    QeetrixMenuSeparator()
                    QeetrixMenuItem(
                        "Delete project",
                        onClick = { confirmDelete = true },
                        icon = QeetrixIconName.Trash,
                        variant = QeetrixMenuItemVariant.Destructive,
                    )
                }
                var sort by remember { mutableStateOf(false) }
                QeetrixMenu(
                    expanded = sort,
                    onExpandedChange = { sort = it },
                    trigger = {
                        QeetrixButton(
                            "Sort by",
                            onClick = { sort = true },
                            variant = QeetrixButtonVariant.Outline,
                            trailingIcon = QeetrixIconName.ChevronDown,
                        )
                    },
                ) {
                    QeetrixMenuItem("Newest first", onClick = { last = "Newest first" })
                    QeetrixMenuItem("Oldest first", onClick = { last = "Oldest first" })
                    QeetrixMenuItem("Name", onClick = { last = "Name" })
                }
            }
            if (confirmDelete) {
                QeetrixConfirmationDialog(
                    title = "Delete this project?",
                    confirmLabel = "Delete project",
                    destructive = true,
                    onConfirm = { last = "Deleted (in this sample, nothing is)." },
                    onDismissRequest = { confirmDelete = false },
                )
            }
            SampleNote(
                "Last: $last Choosing an item closes the menu, then runs it once — the delete item asks for confirmation first. " +
                    "From a keyboard, the arrow keys move between items and Escape closes the menu.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MenuSamplePreview() {
    SamplePreview { MenuSample() }
}
