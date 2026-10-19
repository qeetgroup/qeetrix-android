package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.input.TextFieldState
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixField] and [QeetrixFieldSet] in `src/test/screenshots/field/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixFieldScreenshotTest {
    @Test
    fun states() =
        captureStates("field") {
            ScreenshotStack {
                QeetrixFieldSet("Workspace", description = "Shown to everyone you invite.") {
                    QeetrixTextField(TextFieldState("Qeet Group"), label = "Name", required = true)
                    QeetrixTextField(TextFieldState(), label = "Website", optional = true, placeholder = "https://")
                    QeetrixCheckbox(true, {}, label = "Listed in the directory")
                    QeetrixCheckbox(false, {}, label = "Allow guests")
                }
                QeetrixFieldSet("Notifications", message = QeetrixFieldMessage("Choose at least one.")) {
                    QeetrixCheckbox(false, {}, label = "Email")
                    QeetrixCheckbox(false, {}, label = "Push")
                }
            }
        }
}
