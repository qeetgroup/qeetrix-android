package com.qeetgroup.qeetrix.components.dropdownmenu

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/menu/`: the open menu's surface, drawn in place — a popup cannot be captured. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixMenuScreenshotTest {
    @Test
    fun states() =
        captureStates("menu") {
            ScreenshotStack {
                MenuSurface(Modifier.width(240.dp), focusFirst = false, onDismiss = {}) {
                    QeetrixMenuLabel("Workspace")
                    QeetrixMenuItem("Rename", onClick = {}, icon = QeetrixIconName.Pencil)
                    QeetrixMenuItem("Duplicate", onClick = {}, icon = QeetrixIconName.Copy)
                    QeetrixMenuItem("Share", onClick = {}, icon = QeetrixIconName.Share2, enabled = false)
                    QeetrixMenuSeparator()
                    QeetrixMenuItem(
                        "Delete workspace",
                        onClick = {},
                        icon = QeetrixIconName.Trash,
                        variant = QeetrixMenuItemVariant.Destructive,
                    )
                }
            }
        }
}
