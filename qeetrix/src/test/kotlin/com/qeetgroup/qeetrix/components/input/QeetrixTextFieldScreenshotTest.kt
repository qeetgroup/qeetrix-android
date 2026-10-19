package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixTextField] in `src/test/screenshots/textfield/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixTextFieldScreenshotTest {
    @Test
    fun states() =
        captureStates("textfield") {
            ScreenshotStack {
                QeetrixTextField(TextFieldState(), label = "Work email", placeholder = "you@company.com", required = true)
                QeetrixTextField(TextFieldState("Ada Lovelace"), label = "Name", description = "As it appears on your ID.")
                QeetrixTextField(TextFieldState("ada@"), label = "Email", message = QeetrixFieldMessage("Enter a valid email address."))
                QeetrixTextField(
                    TextFieldState("ada"),
                    label = "Username",
                    message = QeetrixFieldMessage("Available", QeetrixFieldMessageTone.Success),
                )
                QeetrixTextField(
                    TextFieldState("12"),
                    label = "Seats",
                    message = QeetrixFieldMessage("Above your plan", QeetrixFieldMessageTone.Warning),
                )
                QeetrixTextField(TextFieldState("QX-2041"), label = "Order", readOnly = true)
                QeetrixTextField(TextFieldState("Locked"), label = "Plan", enabled = false, optional = true)
                QeetrixTextField(
                    TextFieldState(),
                    Modifier.fillMaxWidth(),
                    placeholder = "Search",
                    leading = { QeetrixIcon(QeetrixIconName.Search, contentDescription = null, tint = QeetrixTheme.colors.textTertiary) },
                )
            }
        }
}
