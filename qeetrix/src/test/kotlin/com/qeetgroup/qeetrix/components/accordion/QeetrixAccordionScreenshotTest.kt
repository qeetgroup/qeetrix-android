package com.qeetgroup.qeetrix.components.accordion

import androidx.compose.foundation.layout.Column
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
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
}
