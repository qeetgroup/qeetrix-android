package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.text.input.rememberTextFieldState
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/dialog/`: an alert, a confirmation and a dialog with a form — the
 * surfaces drawn in place, since a dialog window cannot be captured on the JVM. On the 411 dp screenshot
 * phone the buttons stack, as they do under 640 dp.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixDialogScreenshotTest {
    @Test
    fun states() =
        captureStates("dialog") {
            ScreenshotStack {
                AlertDialogSurface(
                    title = "Delete this workspace?",
                    message = "Its projects and files are removed for everyone.",
                    icon = QeetrixIconName.Trash,
                    actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) {}),
                    onDismissRequest = {},
                )
                AlertDialogSurface(
                    title = "Send the invoice now?",
                    actions = confirmationActions("Send", {}, destructive = false, cancelLabel = "Not now"),
                    onDismissRequest = {},
                )
                DialogLayout(
                    title = "Rename workspace",
                    description = "Everyone in the workspace sees the new name.",
                    onDismissRequest = {},
                    footer = {
                        QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Outline)
                        QeetrixButton("Save", onClick = {})
                    },
                ) {
                    QeetrixTextField(rememberTextFieldState("Northwind"), label = "Name", required = true)
                }
            }
        }
}
