package com.qeetgroup.qeetrix.components.form

import androidx.compose.foundation.text.input.TextFieldState
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for the form patterns in `src/test/screenshots/form/` (a phone width: the actions stack). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixFormScreenshotTest {
    @Test
    fun states() =
        captureStates("form") {
            ScreenshotStack {
                QeetrixFormErrorSummary(
                    listOf(QeetrixFormError("Enter a name for the workspace.") {}, QeetrixFormError("Choose a plan.") {}),
                )
                QeetrixTextField(
                    TextFieldState(),
                    label = "Workspace name",
                    required = true,
                    message = QeetrixFieldMessage("Enter a name for the workspace."),
                )
                QeetrixFormActions {
                    QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Outline)
                    QeetrixButton("Create workspace", onClick = {})
                }
            }
        }
}
