package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
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
                    description = "Draft",
                    navigationIcon = { QeetrixBackButton(onClick = {}) },
                    actions = {
                        QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = {}, tone = QeetrixButtonTone.Neutral)
                        QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = {}, tone = QeetrixButtonTone.Neutral)
                    },
                )
                QeetrixTopAppBar(title = "Settings")
                QeetrixNavigationBar(items, value = "inbox", onValueChange = {})
                Box(Modifier.height(320.dp)) {
                    QeetrixNavigationRail(items, value = "activity", onValueChange = {}) {
                        QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = {}, variant = QeetrixButtonVariant.Solid)
                    }
                }
            }
        }
}
