package com.qeetgroup.qeetrix.components.descriptionlist

import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/descriptionlist/`: stacked (a phone is below `sm`), divided, and a grid. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixDescriptionListScreenshotTest {
    @Test
    fun states() =
        captureStates("descriptionlist") {
            ScreenshotStack {
                QeetrixDescriptionList {
                    QeetrixDescriptionItem("Plan", "Team")
                    QeetrixDescriptionItem("Status") { QeetrixBadge("Active", variant = QeetrixBadgeVariant.Success) }
                }
                QeetrixDescriptionList(divided = true) {
                    QeetrixDescriptionItem("Region", "Mumbai (ap-south-1)")
                    QeetrixDescriptionItem("Billing contact", "Finance team, 4th floor, Indiranagar, Bengaluru 560038")
                    QeetrixDescriptionItem("Created", "12 March 2026")
                }
                QeetrixDescriptionList(layout = QeetrixDescriptionListLayout.Grid, divided = true) {
                    QeetrixDescriptionItem("Plan", "Team")
                    QeetrixDescriptionItem("Seats", "12")
                    QeetrixDescriptionItem("Owner", "Ada Lovelace")
                }
            }
        }
}
