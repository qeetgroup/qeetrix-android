package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/dialog/`: the three intents — a standard dialog with content, a footer start and
 * the close button; a danger confirmation, not dismissible, with no close button; a dismissible warning — drawn in
 * place, since a dialog window cannot be captured on the JVM. Each is as wide as the 411 dp phone leaves it (less
 * the viewport's 24 dp on each side).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixDialogScreenshotTest {
    @Test
    fun states() =
        captureStates("dialog") {
            ScreenshotStack {
                DialogSurface(
                    title = "Rename workspace",
                    description = "Everyone in the workspace sees the new name.",
                    intent = QeetrixDialogIntent.Standard,
                    size = QeetrixDialogSize.Md,
                    onClose = {},
                    modifier = Modifier.width(PhoneDialogWidth),
                    footerStart = { QeetrixText("Import a file", size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle) },
                    footer = {
                        QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                        QeetrixButton("Save", onClick = {})
                    },
                ) {
                    QeetrixText("The workspace is called Northwind. Its address stays the same.")
                }
                DialogSurface(
                    title = "Revoke this API key?",
                    description = "The qeet-pay reconciler stops working at once. This can't be undone.",
                    intent = QeetrixDialogIntent.Danger,
                    size = QeetrixDialogSize.Sm,
                    onClose = null,
                    modifier = Modifier.width(PhoneDialogWidth),
                    footer = {
                        DialogActions(confirmationActions("Revoke key", {}, QeetrixDialogIntent.Danger, "Cancel")) {}
                    },
                )
                DialogSurface(
                    title = "Send the invoice now?",
                    description = "It goes to accounts@example.com straight away.",
                    intent = QeetrixDialogIntent.Warning,
                    size = QeetrixDialogSize.Sm,
                    onClose = {},
                    modifier = Modifier.width(PhoneDialogWidth),
                    footer = {
                        DialogActions(confirmationActions("Send", {}, QeetrixDialogIntent.Warning, "Not now")) {}
                    },
                )
            }
        }
}

/** The 411 dp screenshot phone less `.qx-dialog-viewport`'s `space.inset.xl` on each side. */
private val PhoneDialogWidth = 363.dp
