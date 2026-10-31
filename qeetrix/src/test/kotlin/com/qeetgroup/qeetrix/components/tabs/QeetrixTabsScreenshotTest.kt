package com.qeetgroup.qeetrix.components.tabs

import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/tabs/`: both variants, both orientations, icons, counts, a disabled tab and an overflowing list. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixTabsScreenshotTest {
    private val tabs =
        listOf(
            QeetrixTab("profile", "Profile", QeetrixIconName.UserRound),
            QeetrixTab("alerts", "Alerts", QeetrixIconName.Bell, badge = 1284),
            QeetrixTab("failures", "Failures", badge = 3, badgeTone = QeetrixBadgeTone.Danger),
            QeetrixTab("billing", "Billing", enabled = false),
        )
    private val many = (1..7).map { QeetrixTab(it, "Section $it") }

    @Test
    fun states() =
        captureStates("tabs") {
            ScreenshotStack {
                QeetrixTabs(tabs, value = "alerts", onValueChange = {})
                QeetrixTabs(tabs.take(3), value = "profile", onValueChange = {}, variant = QeetrixTabsVariant.Contained)
                QeetrixTabs(many, value = 2, onValueChange = {})
                ScreenshotFlow {
                    QeetrixTabs(tabs, value = "profile", onValueChange = {}, orientation = QeetrixTabsOrientation.Vertical)
                    QeetrixTabs(
                        tabs.take(3),
                        value = "alerts",
                        onValueChange = {},
                        variant = QeetrixTabsVariant.Contained,
                        orientation = QeetrixTabsOrientation.Vertical,
                    )
                }
            }
        }
}
