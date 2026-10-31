package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
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
 * Baselines in `src/test/screenshots/bottomsheet/`: the sheet's surface, handle and parts drawn in place — the sheet
 * itself opens in a window, which the JVM cannot capture: a dismissible sheet with a footer and its start, and one that
 * is not dismissible (no close button).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixBottomSheetScreenshotTest {
    @Test
    fun states() =
        captureStates("bottomsheet") {
            ScreenshotStack {
                Sheet {
                    SheetLayout(
                        title = "Filter invoices",
                        description = "Show only the invoices you need.",
                        onClose = {},
                        footerStart = { QeetrixText("12 match", size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle) },
                        footer = {
                            QeetrixButton("Clear", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                            QeetrixButton("Show results", onClick = {})
                        },
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm)) {
                            QeetrixText("Status: overdue")
                            QeetrixText("Customer: any", tone = QeetrixTextTone.Subtle)
                        }
                    }
                }
                Sheet {
                    SheetLayout(
                        title = "Send feedback",
                        description = null,
                        onClose = null,
                        footerStart = null,
                        footer = { QeetrixButton("Send", onClick = {}) },
                    ) { QeetrixText("What could be better?") }
                }
            }
        }

    /** The sheet as the modal sheet draws it: Material's surface in `color.surface.overlay`, square, with the handle on top. */
    @Composable
    private fun Sheet(content: @Composable () -> Unit) {
        Column(Modifier.fillMaxWidth().background(QeetrixTheme.colors.surfaceOverlay)) {
            DragHandle()
            content()
        }
    }
}
