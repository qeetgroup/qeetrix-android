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
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
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

/** List rows: text, an icon tile, an avatar, metadata, trailing content, selection, section headers, disabled. */
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
                QeetrixSeparator()
                QeetrixListItem(
                    title = "Ada Lovelace",
                    description = "ada@example.com",
                    onClick = { opened = "Ada Lovelace" },
                    // The title already names her: the avatar stays silent.
                    leading = { QeetrixAvatar("Ada Lovelace", decorative = true) },
                ) {
                    QeetrixBadge("Owner", tone = QeetrixBadgeTone.Brand)
                }
                QeetrixSeparator()
                QeetrixListItem(
                    title = "Weekly digest",
                    description = "Every Monday at 09:00",
                    leading = { QeetrixListItemIcon(QeetrixIconName.Mail) },
                ) {
                    QeetrixIconButton(QeetrixIconName.Ellipsis, "More for Weekly digest", onClick = {}, tone = QeetrixButtonTone.Neutral)
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
            SampleNote(
                "${selected.size} selected. A selected row takes the Ember tint and is read as selected; the checkbox " +
                    "makes the choice more than a colour. The Ember bar is for the current item, not a selection.",
            )
        }
        SampleSection("Section headers") {
            SampleSurface {
                QeetrixSectionHeader("Members", ruled = false, description = "People with access to this workspace.") {
                    QeetrixButton("Invite", onClick = {}, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm)
                }
                QeetrixListItem("Grace Hopper", description = "Editor", leading = { QeetrixAvatar("Grace Hopper", decorative = true) })
                QeetrixListItem("Alan Turing", description = "Viewer", leading = { QeetrixAvatar("Alan Turing", decorative = true) })
                QeetrixSectionHeader("Pending invitations")
                QeetrixListItem(
                    "Ada Lovelace",
                    description = "Invited yesterday",
                    leading = { QeetrixAvatar("Ada Lovelace", decorative = true) },
                )
            }
            SampleNote(
                "A group label in small tracked capitals under a rule, as the web Sidebar labels its groups. TalkBack reads " +
                    "the words as written, as a heading, so heading navigation moves between groups.",
            )
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
