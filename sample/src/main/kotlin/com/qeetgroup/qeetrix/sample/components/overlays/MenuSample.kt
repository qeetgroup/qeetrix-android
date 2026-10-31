package com.qeetgroup.qeetrix.sample.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.dialog.QeetrixConfirmationDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogIntent
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuCheckboxItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuGroupLabel
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemTone
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuRadioGroup
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuRadioItem
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuSeparator
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Menus: a row menu with a destructive item after a separator, and a view menu with checkbox and radio items. */
@Composable
fun MenuSample() {
    SamplePage {
        var last by rememberSaveable { mutableStateOf("Nothing chosen yet.") }
        var confirmDelete by rememberSaveable { mutableStateOf(false) }
        SampleSection("Actions") {
            SampleFlow {
                var overflow by remember { mutableStateOf(false) }
                QeetrixMenu(
                    open = overflow,
                    onOpenChange = { overflow = it },
                    trigger = {
                        QeetrixIconButton(
                            QeetrixIconName.EllipsisVertical,
                            "More options",
                            onClick = { overflow = true },
                            tone = QeetrixButtonTone.Neutral,
                        )
                    },
                ) {
                    QeetrixMenuGroupLabel("Project")
                    QeetrixMenuItem("Rename", onClick = { last = "Rename" }, icon = QeetrixIconName.Pencil, shortcut = "F2")
                    QeetrixMenuItem("Duplicate", onClick = { last = "Duplicate" }, icon = QeetrixIconName.Copy, shortcut = "Ctrl+D")
                    QeetrixMenuItem("Share", onClick = { last = "Share" }, icon = QeetrixIconName.Share2, enabled = false)
                    QeetrixMenuSeparator()
                    QeetrixMenuItem(
                        "Delete project",
                        onClick = { confirmDelete = true },
                        icon = QeetrixIconName.Trash,
                        tone = QeetrixMenuItemTone.Danger,
                    )
                }
            }
            QeetrixConfirmationDialog(
                open = confirmDelete,
                onOpenChange = { confirmDelete = it },
                title = "Delete this project?",
                confirmLabel = "Delete project",
                onConfirm = { last = "Deleted (in this sample, nothing is)." },
                intent = QeetrixDialogIntent.Danger,
            )
            SampleNote(
                "Last: $last Choosing an item closes the menu, then runs it once — the delete item asks for confirmation " +
                    "first. From a keyboard, the arrow keys move between items and Escape closes the menu.",
            )
        }
        SampleSection("Options") {
            var open by remember { mutableStateOf(false) }
            var archived by rememberSaveable { mutableStateOf(false) }
            var compact by rememberSaveable { mutableStateOf(true) }
            var sort by rememberSaveable { mutableStateOf("newest") }
            QeetrixMenu(
                open = open,
                onOpenChange = { open = it },
                trigger = {
                    QeetrixButton(
                        "View",
                        onClick = { open = true },
                        variant = QeetrixButtonVariant.Soft,
                        icon = QeetrixIconName.ChevronDown,
                    )
                },
            ) {
                QeetrixMenuCheckboxItem("Show archived", checked = archived, onCheckedChange = { archived = it })
                QeetrixMenuCheckboxItem("Compact rows", checked = compact, onCheckedChange = { compact = it }, shortcut = "Ctrl+J")
                QeetrixMenuSeparator()
                QeetrixMenuRadioGroup(sort, { sort = it }) {
                    QeetrixMenuGroupLabel("Sort by")
                    QeetrixMenuRadioItem("Newest first", "newest")
                    QeetrixMenuRadioItem("Oldest first", "oldest", description = "The first invoice you sent, at the top")
                    QeetrixMenuRadioItem("Amount", "amount")
                }
            }
            SampleNote(
                "Archived ${if (archived) "shown" else "hidden"}, rows ${if (compact) "compact" else "regular"}, sorted by $sort. " +
                    "Checkbox items keep the menu open, so several can be set in a row; choosing a sort closes it.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MenuSamplePreview() {
    SamplePreview { MenuSample() }
}
