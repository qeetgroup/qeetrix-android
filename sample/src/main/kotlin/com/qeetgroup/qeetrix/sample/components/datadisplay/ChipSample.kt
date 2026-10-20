package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.badge.QeetrixChipSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private enum class TaskStatus { Open, InReview, Closed }

private val StatusOptions =
    listOf(
        QeetrixOption<TaskStatus?>(null, "All"),
        QeetrixOption(TaskStatus.Open, "Open"),
        QeetrixOption(TaskStatus.InReview, "In review"),
        QeetrixOption(TaskStatus.Closed, "Closed"),
    )

private val LabelOptions = listOf("Design", "Engineering", "Finance", "Legal", "Marketing", "Support").map { QeetrixOption(it, it) }

/** Chips: toggles, a single choice, several choices, removable chips, sizes and a scrolling row. */
@Composable
fun ChipSample() {
    SamplePage {
        SampleSection("Toggle") {
            var unread by rememberSaveable { mutableStateOf(true) }
            var mine by rememberSaveable { mutableStateOf(false) }
            SampleFlow {
                QeetrixChip("Unread", selected = unread, onSelectedChange = { unread = it })
                QeetrixChip("Assigned to me", selected = mine, onSelectedChange = { mine = it }, icon = QeetrixIconName.UserRound)
                QeetrixChip("Archived", selected = false, onSelectedChange = {}, enabled = false)
            }
            SampleNote("A selected chip shows a check mark (or its icon) as well as the brand tint. TalkBack reads a checkbox.")
        }
        SampleSection("One choice") {
            var status by rememberSaveable { mutableStateOf<TaskStatus?>(null) }
            QeetrixChipGroup(label = "Status", selected = status, onSelect = { status = it }, options = StatusOptions)
            SampleNote("Radio buttons in a group named Status. Showing: ${StatusOptions.first { it.value == status }.label}.")
        }
        SampleSection("Several choices") {
            var labels by rememberSaveable { mutableStateOf(setOf("Design")) }
            QeetrixChipGroup(label = "Labels", selected = labels, onSelectedChange = { labels = it }, options = LabelOptions)
            SampleNote("The chips wrap onto more lines when they don't fit.")
        }
        SampleSection("Removable") {
            var applied by rememberSaveable { mutableStateOf(listOf("Owner: Ada", "Region: Mumbai", "Updated this week")) }
            SampleFlow {
                for (filter in applied) {
                    key(filter) { QeetrixChip(filter, onRemove = { applied = applied - filter }) }
                }
            }
            SampleNote(if (applied.isEmpty()) "Every filter removed." else "Each remove button names its chip: \"Remove Owner: Ada\".")
        }
        SampleSection("Sizes and a scrolling row") {
            var size by rememberSaveable { mutableStateOf(QeetrixChipSize.Md) }
            QeetrixChipGroup(
                label = "Chip size",
                selected = size,
                onSelect = { size = it },
                options = QeetrixChipSize.entries.map { QeetrixOption(it, it.value) },
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                size = size,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ChipSamplePreview() {
    SamplePreview { ChipSample() }
}
