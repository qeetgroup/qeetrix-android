package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDialogComponentTokens
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/bottomsheet/`: the sheet's surface, handle and parts drawn in place — the
 * sheet itself opens in a window, which the JVM cannot capture.
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
                        footer = {
                            QeetrixButton("Show results", onClick = {}, modifier = Modifier.fillMaxWidth())
                            QeetrixButton(
                                "Clear filters",
                                onClick = {},
                                modifier = Modifier.fillMaxWidth(),
                                variant = QeetrixButtonVariant.Outline,
                            )
                        },
                    ) {
                        QeetrixRadioGroup(
                            "overdue",
                            {},
                            listOf(
                                QeetrixOption("all", "All invoices"),
                                QeetrixOption("overdue", "Overdue"),
                                QeetrixOption("paid", "Paid"),
                            ),
                            label = "Status",
                        )
                    }
                }
                Sheet {
                    SheetLayout(
                        title = null,
                        description = null,
                        onClose = null,
                        footer = null,
                    ) { QeetrixButton("Share link", onClick = {}) }
                }
            }
        }

    /**
     * The sheet's surface as the modal sheet draws it: Material's surface in the dialog colour, rounded on top, and the
     * handle's row, which draws the rule along the top edge. No shadow: none is drawn on a device (Phase 7).
     */
    @Composable
    private fun Sheet(content: @Composable () -> Unit) {
        val shape = RoundedCornerShape(topStart = QeetrixDialogComponentTokens.Corner, topEnd = QeetrixDialogComponentTokens.Corner)
        Column(
            Modifier
                .fillMaxWidth()
                .background(QeetrixDialogComponentTokens.Background, shape)
                .clip(shape),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DragHandle()
            content()
        }
    }
}
