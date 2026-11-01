package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/sidesheet/`: the side sheet's surface and parts drawn in place at a fixed height —
 * the sheet itself opens in a window, which the JVM cannot capture: no handle, the footer pinned to the bottom.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSideSheetScreenshotTest {
    @Test
    fun states() =
        captureStates("sidesheet") {
            ScreenshotStack {
                Sheet {
                    SheetLayout(
                        title = "Edit member",
                        description = "Changes apply at the next sign-in.",
                        onClose = {},
                        footerStart = null,
                        footer = {
                            QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                            QeetrixButton("Save", onClick = {})
                        },
                        modifier = Modifier.fillMaxSize(),
                        handle = false,
                    ) { QeetrixCheckbox(true, {}, label = "Workspace admin", description = "Can invite and remove members.") }
                }
            }
        }

    /** The side sheet's surface: `color.surface.overlay`, square, as tall as a short window. */
    @Composable
    private fun Sheet(content: @Composable () -> Unit) {
        Box(Modifier.fillMaxWidth().height(SHEET_HEIGHT).background(QeetrixTheme.colors.surfaceOverlay)) { content() }
    }
}

private val SHEET_HEIGHT = 360.dp
