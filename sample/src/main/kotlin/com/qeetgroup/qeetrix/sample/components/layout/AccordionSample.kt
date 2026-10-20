package com.qeetgroup.qeetrix.sample.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private enum class FaqTopic(
    val title: String,
    val answer: String,
) {
    Billing("How am I billed?", "Monthly, on the date you subscribed. Invoices include GST and are emailed to the billing contact."),
    Seats("Can I add people later?", "Yes. New members are billed from the day they join, for the rest of the month."),
    Cancel("How do I cancel?", "From Settings › Plan. Your workspace stays readable for 30 days after cancelling."),
}

/** Sections that show and hide content: one open at a time, several at once, with descriptions and icons, and a disabled item. */
@Composable
fun AccordionSample() {
    SamplePage {
        SampleSection("One at a time") {
            var open by rememberSaveable { mutableStateOf<FaqTopic?>(FaqTopic.Billing) }
            QeetrixAccordion {
                for (topic in FaqTopic.entries) {
                    key(topic) {
                        QeetrixAccordionItem(
                            title = topic.title,
                            expanded = open == topic,
                            onExpandedChange = { open = if (it) topic else null },
                        ) { QeetrixText(topic.answer, variant = QeetrixTextVariant.Small) }
                    }
                }
            }
            SampleNote(
                "The app owns one value; opening a section closes the other. TalkBack reads each header as a heading with Expanded or Collapsed.",
            )
        }
        SampleSection("Several at once") {
            var open by rememberSaveable { mutableStateOf(setOf("notifications")) }
            var email by rememberSaveable { mutableStateOf(true) }
            var push by rememberSaveable { mutableStateOf(false) }

            fun toggle(id: String) = { on: Boolean -> open = if (on) open + id else open - id }
            QeetrixAccordion {
                QeetrixAccordionItem(
                    title = "Notifications",
                    expanded = "notifications" in open,
                    onExpandedChange = toggle("notifications"),
                    description = "Email and push",
                    icon = QeetrixIconName.Bell,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xs)) {
                        QeetrixSwitch(checked = email, onCheckedChange = { email = it }, label = "Email")
                        QeetrixSwitch(checked = push, onCheckedChange = { push = it }, label = "Push notifications")
                    }
                }
                QeetrixAccordionItem(
                    title = "Security",
                    expanded = "security" in open,
                    onExpandedChange = toggle("security"),
                    description = "Passkeys and sessions",
                    icon = QeetrixIconName.Lock,
                ) { QeetrixText("Two passkeys and one active session.", variant = QeetrixTextVariant.Small) }
                QeetrixAccordionItem(
                    title = "Data export",
                    expanded = false,
                    onExpandedChange = {},
                    description = "Available to workspace owners",
                    icon = QeetrixIconName.Database,
                    enabled = false,
                ) {}
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AccordionSamplePreview() {
    SamplePreview { AccordionSample() }
}
