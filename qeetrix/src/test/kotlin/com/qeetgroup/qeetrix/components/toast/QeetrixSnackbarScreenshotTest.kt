package com.qeetgroup.qeetrix.components.toast

import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/snackbar/`: the four tones, a description, an action, and one without a close button. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSnackbarScreenshotTest {
    @Test
    fun states() =
        captureStates("snackbar") {
            ScreenshotStack {
                QeetrixSnackbar("Conversation archived", actionLabel = "Undo", onAction = {}, onDismiss = {})
                QeetrixSnackbar(
                    "Invoice sent",
                    tone = QeetrixSnackbarTone.Success,
                    description = "INV-2041 to accounts@example.com",
                    onDismiss = {},
                )
                QeetrixSnackbar(
                    "Sync is paused",
                    tone = QeetrixSnackbarTone.Warning,
                    description = "Reconnect to keep working offline.",
                    onDismiss = {},
                )
                QeetrixSnackbar(
                    "Couldn't send the invoice",
                    tone = QeetrixSnackbarTone.Danger,
                    actionLabel = "Retry",
                    onAction = {},
                    onDismiss = {},
                )
                QeetrixSnackbar("Copied")
            }
        }
}
