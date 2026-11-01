package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.segmented.QeetrixSegmentedControl
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The segmented control: a filter, options with icons, the three sizes, and disabled. */
@Composable
fun SegmentedControlSample() {
    SamplePage {
        SampleSection("A filter") {
            var filter by rememberSaveable { mutableStateOf("all") }
            QeetrixSegmentedControl(
                filter,
                { filter = it },
                listOf(QeetrixOption("all", "All"), QeetrixOption("pending", "Pending"), QeetrixOption("approved", "Approved")),
            )
            SampleNote("Showing: $filter. TalkBack reads a group of radio buttons; a choice plays the Selection haptic.")
        }
        SampleSection("With icons") {
            var box by rememberSaveable { mutableStateOf("inbox") }
            QeetrixSegmentedControl(
                box,
                { box = it },
                listOf(
                    QeetrixOption("inbox", "Inbox", icon = QeetrixIconName.Inbox),
                    QeetrixOption("waiting", "Waiting", icon = QeetrixIconName.Clock),
                    QeetrixOption("done", "Done", icon = QeetrixIconName.CircleCheck),
                ),
            )
        }
        SampleSection("Sizes") {
            for (size in QeetrixButtonSize.entries) {
                var range by rememberSaveable(size) { mutableStateOf("7d") }
                QeetrixSegmentedControl(
                    range,
                    { range = it },
                    listOf(QeetrixOption("24h", "24 h"), QeetrixOption("7d", "7 days"), QeetrixOption("30d", "30 days")),
                    size = size,
                )
            }
            SampleNote("Each segment is a 48 dp touch target, whatever the size drawn.")
        }
        SampleSection("Disabled") {
            QeetrixSegmentedControl("light", {}, listOf(QeetrixOption("light", "Light"), QeetrixOption("dark", "Dark")), enabled = false)
            var mode by rememberSaveable { mutableStateOf("light") }
            QeetrixSegmentedControl(
                mode,
                { mode = it },
                listOf(QeetrixOption("light", "Light"), QeetrixOption("dark", "Dark"), QeetrixOption("system", "System", enabled = false)),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SegmentedControlSamplePreview() {
    SamplePreview { SegmentedControlSample() }
}
