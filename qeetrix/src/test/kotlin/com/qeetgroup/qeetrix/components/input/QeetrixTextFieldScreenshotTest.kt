package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
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
    /**
     * `large_<config>.png`: the large field of a one-value sign-up step at the comfortable density — a phone number with
     * its +91 affix, empty then filled — and a name with its help text, beside a default field for comparison.
     */
    @Test
    fun large() {
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("textfield", "large", config) {
                CompositionLocalProvider(LocalQeetrixDensity provides QeetrixDensity.Comfortable) {
                    Column(Modifier.width(328.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        QeetrixTextField(
                            TextFieldState(),
                            label = "Phone number",
                            placeholder = "10-digit number",
                            startAdornment = { QeetrixText("🇮🇳 +91") },
                            size = QeetrixTextFieldSize.Lg,
                        )
                        QeetrixTextField(
                            TextFieldState("98765 43210"),
                            label = "Phone number",
                            startAdornment = { QeetrixText("🇮🇳 +91") },
                            description = "We'll send a code to it.",
                            size = QeetrixTextFieldSize.Lg,
                        )
                        QeetrixTextField(
                            TextFieldState("Asha Rao"),
                            label = "Your name",
                            error = "Enter your name as people know it.",
                            size = QeetrixTextFieldSize.Lg,
                        )
                        QeetrixTextField(TextFieldState("Asha Rao"), label = "Your name (default size)")
                    }
                }
            }
        }
    }

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
                QeetrixTextField(TextFieldState("JBSW Y3DP EHPK 3PXP"), label = "Setup key (mono)", mono = true)
                QeetrixTextField(TextFieldState(), label = "Pairing code (mono)", placeholder = "e.g. KM7QX-4TRWP", mono = true)
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
