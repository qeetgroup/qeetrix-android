package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
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

/** Chips: a filter that toggles, single and multiple choice groups, applied filters you remove, and sizes. */
@Composable
fun ChipSample() {
    SamplePage {
        SampleSection("A filter") {
            var unread by rememberSaveable { mutableStateOf(false) }
            var starred by rememberSaveable { mutableStateOf(true) }
            SampleFlow {
                QeetrixChip("Unread", selected = unread, onSelectedChange = { unread = it })
                QeetrixChip("Starred", selected = starred, onSelectedChange = { starred = it }, icon = QeetrixIconName.Bell)
            }
            SampleNote("Selected takes the selected tint, a 2 dp Ember edge and a check, so it never rests on colour alone.")
        }
        SampleSection("One status") {
            var status by rememberSaveable { mutableStateOf<TaskStatus?>(null) }
            Row(Modifier.horizontalScroll(rememberScrollState())) {
                QeetrixChipGroup(label = "Status", value = status, onValueChange = { status = it }, options = StatusOptions)
            }
            SampleNote("Radio buttons for TalkBack, in one scrolling line.")
        }
        SampleSection("Several departments") {
            var departments by rememberSaveable { mutableStateOf(setOf("Design")) }
            QeetrixChipGroup(
                label = "Departments",
                values = departments,
                onValuesChange = { departments = it },
                options = listOf("Design", "Engineering", "Finance", "Legal", "People operations").map { QeetrixOption(it, it) },
                size = QeetrixChipSize.Sm,
            )
        }
        SampleSection("Applied filters") {
            var applied by rememberSaveable { mutableStateOf(listOf("Status: Active", "Team: Payments", "Region: India")) }
            SampleFlow {
                for (filter in applied) {
                    key(filter) { QeetrixChip(filter, onRemove = { applied = applied - filter }, size = QeetrixChipSize.Sm) }
                }
            }
            SampleNote(
                if (applied.isEmpty()) "No filters left." else "Each remove button is named for its chip: \"Remove Status: Active\".",
            )
        }
        SampleSection("Sizes") {
            SampleFlow {
                for (size in QeetrixChipSize.entries) {
                    QeetrixChip("Size ${size.value}", selected = false, onSelectedChange = {}, size = size)
                }
            }
            SampleNote("Heights from the density's control sizes; every chip keeps a 48 dp touch area.")
        }
    }
}

@PreviewLightDark
@Composable
private fun ChipSamplePreview() {
    SamplePreview { ChipSample() }
}
