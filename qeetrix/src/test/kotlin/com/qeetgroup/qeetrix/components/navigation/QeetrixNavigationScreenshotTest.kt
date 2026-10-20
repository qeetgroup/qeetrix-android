package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/navigation/`: the top app bar, the bottom bar and the rail. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixNavigationScreenshotTest {
    private val items =
        listOf(
            QeetrixNavigationItem("home", "Home", QeetrixIconName.House),
            QeetrixNavigationItem("inbox", "Inbox", QeetrixIconName.Inbox, badge = "3"),
            QeetrixNavigationItem("activity", "Activity", QeetrixIconName.Bell),
            QeetrixNavigationItem("settings", "Settings", QeetrixIconName.Settings, enabled = false),
        )

    @Test
    fun states() =
        captureStates("navigation") {
            ScreenshotStack {
                QeetrixTopAppBar(
                    title = "Invoice INV-2041",
                    subtitle = "Draft",
                    navigationIcon = { QeetrixBackButton(onClick = {}) },
                    actions = {
                        QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = {})
                        QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = {})
                    },
                    scrolled = true,
                )
                QeetrixTopAppBar(title = "Settings")
                QeetrixNavigationBar(items, selected = "inbox", onSelect = {})
                Box(Modifier.height(300.dp)) {
                    QeetrixNavigationRail(items, selected = "home", onSelect = {}) {
                        QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = {}, variant = QeetrixButtonVariant.Default)
                    }
                }
            }
        }
}
