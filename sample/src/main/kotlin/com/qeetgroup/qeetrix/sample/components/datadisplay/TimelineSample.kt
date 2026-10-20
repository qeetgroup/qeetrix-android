package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineEmphasis
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineTone
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** A history: toned dots, icon markers for what matters most, minor events, and a marker label for TalkBack. */
@Composable
fun TimelineSample() {
    SamplePage {
        SampleSection("Invoice history") {
            QeetrixTimeline {
                QeetrixTimelineItem(
                    "Payment received",
                    time = "Today, 10:24",
                    description = "₹48,000 by bank transfer.",
                    tone = QeetrixTimelineTone.Success,
                    icon = QeetrixIconName.CircleCheck,
                )
                QeetrixTimelineItem("Reminder sent", time = "Yesterday", tone = QeetrixTimelineTone.Info)
                QeetrixTimelineItem("Viewed by the customer", time = "2 days ago", emphasis = QeetrixTimelineEmphasis.Minor)
                QeetrixTimelineItem("Due date changed", time = "2 days ago", emphasis = QeetrixTimelineEmphasis.Minor)
                QeetrixTimelineItem(
                    "Card payment",
                    time = "5 days ago",
                    description = "The bank declined the payment.",
                    tone = QeetrixTimelineTone.Destructive,
                    markerLabel = "Failed",
                )
                QeetrixTimelineItem("Invoice sent", time = "1 week ago", tone = QeetrixTimelineTone.Brand, icon = QeetrixIconName.Send)
            }
            SampleNote(
                "Each event is read as one element: title, time, description, and the marker's label when the words don't say it (\"Failed\").",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TimelineSamplePreview() {
    SamplePreview { TimelineSample() }
}
