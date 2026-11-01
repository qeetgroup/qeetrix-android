package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.feed.QeetrixTimeline
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineGroup
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineItem
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineTone
import com.qeetgroup.qeetrix.components.feed.QeetrixTimelineVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** A user's recent activity grouped by day, a failure in the danger tone, and the compact look for a side panel. */
@Composable
fun TimelineSample() {
    SamplePage {
        SampleSection("Recent activity") {
            QeetrixTimeline {
                QeetrixTimelineGroup("Today") {
                    QeetrixTimelineItem(
                        "Signed in with a passkey",
                        description = "Bengaluru, India · Chrome on macOS",
                        time = "2 min ago",
                        icon = QeetrixIconName.Lock,
                    )
                    QeetrixTimelineItem(
                        "Sign-in refused",
                        description = "Wrong one-time code, three times",
                        time = "1 h ago",
                        icon = QeetrixIconName.CircleX,
                        tone = QeetrixTimelineTone.Danger,
                    )
                }
                QeetrixTimelineGroup("Yesterday") {
                    QeetrixTimelineItem(
                        "Payment received",
                        description = "₹48,000 by bank transfer",
                        time = "10:24",
                        icon = QeetrixIconName.CircleCheck,
                        tone = QeetrixTimelineTone.Success,
                    )
                    QeetrixTimelineItem("Invoice sent", time = "09:02", icon = QeetrixIconName.Send, tone = QeetrixTimelineTone.Brand)
                }
            }
            SampleNote(
                "Each event is read as one element: title, description, time. A failure colours its icon and title; its " +
                    "words still say what happened. The line breaks between days.",
            )
        }
        SampleSection("Compact") {
            QeetrixTimeline(variant = QeetrixTimelineVariant.Compact) {
                QeetrixTimelineItem("Invoice sent", time = "1 week ago", tone = QeetrixTimelineTone.Brand)
                QeetrixTimelineItem("Viewed by the customer", time = "6 days ago")
                QeetrixTimelineItem(
                    "Card payment declined",
                    description = "Insufficient funds",
                    time = "5 days ago",
                    tone = QeetrixTimelineTone.Danger,
                )
            }
            SampleNote("A dot per event, centred on its title's line, for a side panel or a short summary.")
        }
    }
}

@PreviewLightDark
@Composable
private fun TimelineSamplePreview() {
    SamplePreview { TimelineSample() }
}
