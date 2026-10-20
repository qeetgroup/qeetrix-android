package com.qeetgroup.qeetrix.components.accordion

import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/accordion/`: open and closed items, a description and icon, a wrapping title, disabled. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixAccordionScreenshotTest {
    @Test
    fun states() =
        captureStates("accordion") {
            ScreenshotStack {
                QeetrixAccordion {
                    QeetrixAccordionItem("How am I billed?", expanded = true, onExpandedChange = {}) {
                        QeetrixText("Monthly, on the date you subscribed. Invoices include GST.", variant = QeetrixTextVariant.Small)
                    }
                    QeetrixAccordionItem(
                        "Notifications",
                        expanded = false,
                        onExpandedChange = {},
                        description = "Email and push",
                        icon = QeetrixIconName.Bell,
                    ) {}
                    QeetrixAccordionItem(
                        "A question long enough to wrap onto a second line of the header",
                        expanded = false,
                        onExpandedChange = {},
                    ) {}
                    QeetrixAccordionItem("Data export", expanded = false, onExpandedChange = {}, enabled = false) {}
                }
            }
        }
}
