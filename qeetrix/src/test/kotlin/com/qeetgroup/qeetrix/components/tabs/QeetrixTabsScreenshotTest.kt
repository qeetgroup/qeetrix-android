package com.qeetgroup.qeetrix.components.tabs

import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/tabs/`: both variants, icons, a disabled tab, and a row that overflows. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixTabsScreenshotTest {
    private val tabs =
        listOf(
            QeetrixTab("overview", "Overview"),
            QeetrixTab("activity", "Activity", QeetrixIconName.Bell),
            QeetrixTab("billing", "Billing", enabled = false),
        )
    private val many = (1..7).map { QeetrixTab(it, "Section $it") }

    @Test
    fun states() =
        captureStates("tabs") {
            ScreenshotStack {
                QeetrixTabs(tabs, selected = "overview", onSelect = {})
                QeetrixTabs(tabs, selected = "activity", onSelect = {}, variant = QeetrixTabsVariant.Line)
                QeetrixTabs(many, selected = 2, onSelect = {}, variant = QeetrixTabsVariant.Line)
            }
        }
}
