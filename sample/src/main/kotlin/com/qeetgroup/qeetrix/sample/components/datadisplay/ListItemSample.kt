package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparatorVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface

private class SampleFile(
    val id: String,
    val name: String,
    val owner: String,
    val size: String,
)

private val Files =
    listOf(
        SampleFile("f1", "Quarterly report.pdf", "Shared by Ada Lovelace", "2.4 MB"),
        SampleFile("f2", "Brand guidelines.pdf", "Shared by Grace Hopper", "8.1 MB"),
        SampleFile("f3", "Onboarding checklist.docx", "Only you", "120 KB"),
    )

/** List rows: text, an icon tile, an avatar, metadata, trailing content, selection, a section header, disabled. */
@Composable
fun ListItemSample() {
    SamplePage {
        SampleSection("Rows") {
            var opened by rememberSaveable { mutableStateOf<String?>(null) }
            SampleSurface {
                QeetrixListItem(
                    title = "Quarterly report.pdf",
                    description = "Shared by Ada Lovelace",
                    metadata = "2.4 MB",
                    onClick = { opened = "Quarterly report.pdf" },
                    leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
                )
                QeetrixSeparator(variant = QeetrixSeparatorVariant.Muted)
                QeetrixListItem(
                    title = "Ada Lovelace",
                    description = "ada@example.com",
                    onClick = { opened = "Ada Lovelace" },
                    // The title already names her: the avatar stays silent.
                    leading = { QeetrixAvatar("Ada Lovelace", contentDescription = null) },
                ) {
                    QeetrixBadge("Owner", variant = QeetrixBadgeVariant.Brand)
                }
                QeetrixSeparator(variant = QeetrixSeparatorVariant.Muted)
                QeetrixListItem(
                    title = "Weekly digest",
                    description = "Every Monday at 09:00",
                    leading = { QeetrixListItemIcon(QeetrixIconName.Mail) },
                ) {
                    QeetrixIconButton(QeetrixIconName.Ellipsis, contentDescription = "More for Weekly digest", onClick = {})
                }
            }
            SampleNote(opened?.let { "Opened $it." } ?: "Tap a row: it reports the click, and the app decides what opens.")
        }
        SampleSection("Selection") {
            var selected by rememberSaveable { mutableStateOf(setOf("f2")) }
            SampleSurface {
                for (file in Files) {
                    key(file.id) {
                        val isSelected = file.id in selected
                        val toggle = { selected = if (isSelected) selected - file.id else selected + file.id }
                        QeetrixListItem(
                            title = file.name,
                            description = file.owner,
                            metadata = file.size,
                            selected = isSelected,
                            onClick = toggle,
                            // The row toggles; the checkbox only shows it, and TalkBack reads the row as one checkbox.
                            leading = { QeetrixCheckbox(checked = isSelected, onCheckedChange = null) },
                        )
                    }
                }
            }
            SampleNote("${selected.size} selected. A selected row is tinted and edged with the brand, and read as selected.")
        }
        SampleSection("Section header") {
            SampleSurface {
                QeetrixSectionHeader("Members", description = "People with access to this workspace.") {
                    QeetrixButton("Invite", onClick = {}, variant = QeetrixButtonVariant.Link, size = QeetrixButtonSize.Sm)
                }
                QeetrixListItem(
                    "Grace Hopper",
                    description = "Editor",
                    leading = { QeetrixAvatar("Grace Hopper", contentDescription = null) },
                )
                QeetrixListItem(
                    "Alan Turing",
                    description = "Viewer",
                    leading = { QeetrixAvatar("Alan Turing", contentDescription = null) },
                )
            }
            SampleNote("The title is a heading: TalkBack's heading navigation moves between groups.")
        }
        SampleSection("Disabled") {
            SampleSurface {
                QeetrixListItem(
                    "Archived project",
                    description = "Read-only until it is restored",
                    enabled = false,
                    onClick = {},
                    leading = { QeetrixListItemIcon(QeetrixIconName.Folder) },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ListItemSamplePreview() {
    SamplePreview { ListItemSample() }
}
