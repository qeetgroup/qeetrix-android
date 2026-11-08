package com.qeetgroup.qeetrix.sample.components.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordion
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem
import com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsiblePanel
import com.qeetgroup.qeetrix.components.accordion.QeetrixCollapsibleTrigger
import com.qeetgroup.qeetrix.components.typography.QeetrixText
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

/** Sections that show and hide: one open at a time, several at once, a disabled section, and a one-section fold. */
@Composable
fun AccordionSample() {
    SamplePage {
        SampleSection("One at a time") {
            var open by rememberSaveable { mutableStateOf(listOf(FaqTopic.Billing.name)) }
            QeetrixAccordion(value = open, onValueChange = { open = it }) {
                for (topic in FaqTopic.entries) {
                    QeetrixAccordionItem(topic.name, topic.title) { QeetrixText(topic.answer) }
                }
            }
            SampleNote(
                "The app owns the open sections: a list, even when only one can be open. TalkBack reads each trigger as a " +
                    "heading button with Expanded or Collapsed.",
            )
        }
        SampleSection("Several at once") {
            var open by rememberSaveable { mutableStateOf(listOf("security")) }
            QeetrixAccordion(value = open, onValueChange = { open = it }, multiple = true) {
                QeetrixAccordionItem("security", "Security") { QeetrixText("Two passkeys and one active session.") }
                QeetrixAccordionItem("notifications", "Notifications") { QeetrixText("Email for invoices; push for sign-ins.") }
                QeetrixAccordionItem("advanced", "Advanced", description = "6 digits · every 30 seconds · SHA-1") {
                    QeetrixText("Change these only if the service asks you to.")
                }
                QeetrixAccordionItem("export", "Data export", enabled = false) {}
            }
            SampleNote("Open: ${open.joinToString()}. Data export is disabled, for workspace owners only.")
        }
        SampleSection("Collapsible") {
            var advanced by rememberSaveable { mutableStateOf(false) }
            QeetrixCollapsibleTrigger("Advanced options", open = advanced, onOpenChange = { advanced = it })
            QeetrixCollapsiblePanel(advanced) { QeetrixText("Webhook signing secret: rotate it every 90 days.") }
            SampleNote("One section without the rules: a ghost button whose chevron turns as its panel opens.")
        }
    }
}

@PreviewLightDark
@Composable
private fun AccordionSamplePreview() {
    SamplePreview { AccordionSample() }
}
