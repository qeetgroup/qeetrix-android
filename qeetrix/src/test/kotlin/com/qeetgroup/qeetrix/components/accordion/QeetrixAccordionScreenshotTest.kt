package com.qeetgroup.qeetrix.components.accordion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.QeetrixTextWeight
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/accordion/`: open and closed sections, a wrapping title, a disabled one, and a collapsible. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixAccordionScreenshotTest {
    @Test
    fun states() =
        captureStates("accordion") {
            ScreenshotStack {
                QeetrixAccordion(value = listOf("billing"), onValueChange = {}) {
                    QeetrixAccordionItem("billing", "How am I billed?") {
                        QeetrixText("Monthly, on the date you subscribed. Invoices include GST.")
                    }
                    QeetrixAccordionItem("numbering", "Invoice numbering") { QeetrixText("INV-YYYY-NNNN") }
                    QeetrixAccordionItem("advanced", "Advanced", description = "6 digits · every 30 seconds · SHA-1") {}
                    QeetrixAccordionItem("long", "A question long enough to wrap onto a second line of the trigger") {}
                    QeetrixAccordionItem("legacy", "Legacy export", enabled = false) {}
                }
                Column {
                    QeetrixCollapsibleTrigger("Advanced options", open = true, onOpenChange = {})
                    QeetrixCollapsiblePanel(open = true) { QeetrixText("Webhook signing secret: rotate every 90 days.") }
                    QeetrixCollapsibleTrigger("Show more", open = false, onOpenChange = {})
                }
            }
        }

    /**
     * `src/test/screenshots/accordion/compact_<config>.png`: a compact list of five in a card — each item drawing its own
     * body.md semibold title with its purpose in body.sm under it, the chevron centred on the trigger.
     */
    @Test
    fun compact() {
        val items =
            listOf(
                "phone" to "Your phone number" to "To sign you in and keep your account yours",
                "contacts" to "Your contacts" to "To find people you know who use Qeet Circle",
                "photo" to "Your profile photo" to "So the people you talk to can see it's you",
                "device" to "This phone's details" to "To keep your messages safe on it",
                "usage" to "How you use the app" to "Only if you turn it on, to fix problems",
            )
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("accordion", "compact", config) {
                QeetrixCard(Modifier.width(328.dp)) {
                    QeetrixAccordion(value = listOf("contacts"), onValueChange = {}) {
                        for ((head, purpose) in items) {
                            val (id, title) = head
                            QeetrixAccordionItem(
                                id,
                                "$title, $purpose",
                                titleContent = {
                                    Column {
                                        QeetrixText(title, weight = QeetrixTextWeight.Semibold)
                                        QeetrixText(purpose, size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
                                    }
                                },
                            ) { QeetrixText("Only the numbers, never the names. You can turn it off in Settings.") }
                        }
                    }
                }
            }
        }
    }
}
