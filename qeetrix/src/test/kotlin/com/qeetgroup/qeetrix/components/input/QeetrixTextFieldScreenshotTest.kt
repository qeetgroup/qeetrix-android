package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.CompositionLocalProvider
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixTextField] in `src/test/screenshots/textfield/`: every state of the filled field, adornments, densities. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixTextFieldScreenshotTest {
    @Test
    fun states() =
        captureStates("textfield") {
            ScreenshotStack {
                QeetrixTextField(TextFieldState(), label = "Work email", placeholder = "e.g. asha@acme.in", required = true)
                QeetrixTextField(
                    TextFieldState("Asha Rao"),
                    label = "Name",
                    description = "As it appears on your PAN.",
                    interactionSource = rememberHoveredSource(),
                )
                QeetrixTextField(TextFieldState("27AAPFU0939F1ZV"), label = "GSTIN", interactionSource = rememberFocusedSource())
                QeetrixTextField(
                    TextFieldState("asha@"),
                    label = "Email",
                    description = "Hidden by the error.",
                    error = "Enter a work email, like asha@acme.in.",
                )
                QeetrixTextField(TextFieldState("QX-2041"), label = "Order", readOnly = true)
                QeetrixTextField(
                    TextFieldState("Locked"),
                    label = "Plan",
                    optional = true,
                    enabled = false,
                    description = "Your admin sets this.",
                )
                QeetrixTextField(
                    TextFieldState("2,500"),
                    label = "Amount",
                    icon = QeetrixIconName.Search,
                    startAdornment = { QeetrixText("₹") },
                    endAdornment = { QeetrixText("per month") },
                    help = "Billed on the first of each month.",
                )
                CompositionLocalProvider(LocalQeetrixDensity provides QeetrixDensity.Compact) {
                    QeetrixTextField(TextFieldState("Compact"), label = "Compact density")
                }
                CompositionLocalProvider(LocalQeetrixDensity provides QeetrixDensity.Comfortable) {
                    QeetrixTextField(TextFieldState("Comfortable"), label = "Comfortable density")
                }
            }
        }
}
