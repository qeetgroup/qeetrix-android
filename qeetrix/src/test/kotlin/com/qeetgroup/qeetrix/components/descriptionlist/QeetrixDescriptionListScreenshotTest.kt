package com.qeetgroup.qeetrix.components.descriptionlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotGap
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/descriptionlist/`: `states_*` on the phone (rows stack below 24 rem), and
 * `wide_*` on a 720 dp window, where rows sit beside their terms, in a card edge to edge, and in columns.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixDescriptionListScreenshotTest {
    @Test
    fun states() = captureStates("descriptionlist") { ScreenshotStack { Specimens() } }

    @Test
    @Config(qualifiers = "en-rUS-w720dp-h2400dp-xhdpi")
    fun wide() {
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("descriptionlist", "wide", config) {
                Column(Modifier.width(560.dp)) { Specimens() }
            }
        }
    }
}

@Composable
private fun Specimens() {
    Column(verticalArrangement = Arrangement.spacedBy(ScreenshotGap)) {
        QeetrixDescriptionList {
            QeetrixDescriptionItem("Email", "priya.sharma@acme.in")
            QeetrixDescriptionItem("Status") { QeetrixBadge("Active", tone = QeetrixBadgeTone.Success) }
            QeetrixDescriptionItem("Billing contact", "Finance team, 4th floor, Indiranagar, Bengaluru 560038")
            QeetrixDescriptionItem("Phone", null)
            QeetrixDescriptionItem(
                "Display name",
                "Acme",
                description = "Shown in the top bar and in invitations.",
                action = { QeetrixButton("Change", onClick = {}, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm) },
            )
        }
        QeetrixCard(title = "Payment") {
            QeetrixDescriptionList {
                QeetrixDescriptionItem("Amount", "₹12,400.00")
                QeetrixDescriptionItem("Method", "UPI")
            }
        }
        QeetrixDescriptionList(layout = QeetrixDescriptionListLayout.Columns, columns = 3) {
            QeetrixDescriptionItem("Plan", "Team")
            QeetrixDescriptionItem("Seats", "12")
            QeetrixDescriptionItem("Owner", "Ada Lovelace")
            QeetrixDescriptionItem("Region", "Mumbai (ap-south-1)")
        }
    }
}
