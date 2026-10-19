package com.qeetgroup.qeetrix.components.card

import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixCard] in `src/test/screenshots/card/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixCardScreenshotTest {
    @Test
    fun states() =
        captureStates("card") {
            ScreenshotStack {
                QeetrixCard(
                    title = "Workspace",
                    description = "3 members",
                    action = { QeetrixBadge("Pro", variant = QeetrixBadgeVariant.Brand) },
                    footer = { QeetrixButton("Manage", onClick = {}, variant = QeetrixButtonVariant.Outline, size = QeetrixButtonSize.Sm) },
                ) { QeetrixText("Created 12 March") }
                QeetrixCard(variant = QeetrixCardVariant.Outline, title = "Outline", description = "No shadow")
                QeetrixCard(variant = QeetrixCardVariant.Elevated, title = "Elevated", description = "One step forward")
                QeetrixCard(selected = true, size = QeetrixCardSize.Sm, title = "Selected, small", description = "2 dp brand boundary")
                QeetrixCard(onClick = {}, enabled = false, title = "Disabled, clickable") { QeetrixText("Faded") }
                QeetrixCard(title = "Nested") { QeetrixCard(title = "Inner card", description = "No second shadow") }
            }
        }
}
