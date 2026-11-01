package com.qeetgroup.qeetrix.components.form

import androidx.compose.foundation.text.input.TextFieldState
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines for [QeetrixForm], [QeetrixFormErrorSummary] and [QeetrixFormActions] in `src/test/screenshots/form/`:
 * the increment-3 exit form (GSTIN, PAN, an optional trade name, a billing-cycle group, an auto-renew switch, a
 * server error on one field) after a failed submit.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixFormScreenshotTest {
    @Test
    fun states() =
        captureStates("form") {
            ScreenshotStack {
                QeetrixForm(
                    errors =
                        listOf(
                            QeetrixFormError("Enter a GSTIN, like 27AAPFU0939F1ZV.") {},
                            QeetrixFormError("That PAN is already registered."),
                        ),
                    summaryTitle = "Fix these to continue",
                ) {
                    QeetrixTextField(
                        TextFieldState(),
                        label = "GSTIN",
                        description = "15 characters.",
                        error = "Enter a GSTIN, like 27AAPFU0939F1ZV.",
                    )
                    QeetrixTextField(TextFieldState("AAPFU0939F"), label = "PAN", error = "That PAN is already registered.")
                    QeetrixTextField(TextFieldState(), label = "Trade name", optional = true, placeholder = "e.g. Acme Pvt Ltd")
                    QeetrixRadioGroup(
                        "monthly",
                        {},
                        listOf(QeetrixOption("monthly", "Monthly"), QeetrixOption("yearly", "Yearly")),
                        label = "Billing cycle",
                    )
                    QeetrixSwitch(true, {}, label = "Auto-renew")
                    QeetrixFormActions {
                        QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Lg)
                        QeetrixButton("Save", onClick = {}, size = QeetrixButtonSize.Lg)
                    }
                }
                // Not focused: the alert's own 3 dp danger edge, without the focus frame.
                QeetrixFormErrorSummary(listOf(QeetrixFormError("Choose a billing cycle.") {}), autoFocus = false)
            }
        }
}
