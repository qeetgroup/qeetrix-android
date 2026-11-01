// The examples keep the documentation's compact layout, so they can be compared with it line by line.
@file:Suppress(
    "ktlint:standard:argument-list-wrapping",
    "ktlint:standard:class-signature",
    "ktlint:standard:function-naming",
    "ktlint:standard:max-line-length",
    "ktlint:standard:no-multi-spaces",
    "ktlint:standard:trailing-comma-on-call-site",
    "ktlint:standard:wrapping",
)

package com.qeetgroup.qeetrix.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarItem
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarShape
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeSize
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.card.QeetrixCardTone
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckboxGroup
import com.qeetgroup.qeetrix.components.form.QeetrixForm
import com.qeetgroup.qeetrix.components.form.QeetrixFormActions
import com.qeetgroup.qeetrix.components.form.QeetrixFormError
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInputSize
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordAutoComplete
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.progress.QeetrixMeter
import com.qeetgroup.qeetrix.components.progress.QeetrixMeterTone
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparatorOrientation
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerSize
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerTone
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal

/**
 * The examples in `README.md` and the component pages in `Documentation/Components` for the core components and
 * the forms, as written there, so they keep compiling against the public API and composing. Only the surrounding
 * state and the app's own functions are added here. Values are synthetic.
 *
 * Where a page writes "…" or "the form's fields", a value or a field stands in.
 */
@RunWith(RobolectricTestRunner::class)
class DocumentationExamplesTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun everyCoreExampleComposes() {
        compose.setContent {
            QeetrixTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    ReadmeTheme()
                    ReadmeCard()
                    ReadmeTypeAndIcons()
                    ButtonExamples()
                    IconButtonExamples()
                    BadgeExamples()
                    TextExamples()
                    SpinnerExamples()
                    SeparatorExamples()
                    CardExamples()
                    AvatarExamples()
                    ProgressExamples()
                }
            }
        }
        assertShown(
            "Security", "Passkeys and recovery codes", "Sign-in email", "Save changes", "Signed in on 3 devices", "Locked",
            "Create invoice", "Export", "Revoke key", "Close", "New invoice", "Refresh", "Paid", "Overdue", "Draft", "Billing",
            "Welcome to Qeet", "Invoices are sent on the first of each month.", "inv_2K9", "Loading invoices", "Docs",
            "Status", "Workspace", "Delete workspace", "Priya Sharma", "Acme Pvt Ltd", "Uploading report.pdf", "Storage",
        )
    }

    @Test
    fun everyFormExampleComposes() {
        compose.setContent {
            QeetrixTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    ReadmeForm()
                    TextFieldExample()
                    FieldExamples()
                    TextareaExample()
                    PasswordInputExample()
                    OTPInputExample()
                    CheckboxExamples()
                    RadioGroupExample()
                    SelectExample()
                    NumberFieldExamples()
                    SwitchExample()
                    FormExample()
                }
            }
        }
        assertShown(
            "Work email", "Notification tone", "Billing address", "Address line 2", "What could be better?", "Password",
            "Verification code", "Product updates", "Notify me by", "Billing cycle", "Seats", "Discount", "Auto-renew",
            "GSTIN", "Trade name",
        )
    }

    private fun assertShown(vararg texts: String) {
        for (text in texts) {
            val named = hasText(text, substring = true) or hasContentDescription(text, substring = true)
            assertTrue(text, compose.onAllNodes(named, useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty())
        }
    }
}

// README.md, "Theme" (inside `setContent`)
@Composable
private fun ReadmeTheme() {
    QeetrixTheme {
        // follows the device's dark-theme setting
        Column(Modifier.background(QeetrixTheme.colors.surfaceCanvas).padding(QeetrixSpace.InsetLg)) {
            QeetrixHeading("Security", level = 1)
            QeetrixText("Passkeys and recovery codes", tone = QeetrixTextTone.Subtle)
        }
    }
}

// README.md, "Components"
@Composable
private fun ReadmeCard() {
    var alerts by remember { mutableStateOf(true) }
    val saving = false
    val email = rememberTextFieldState()
    QeetrixCard(title = "Sign-in email", description = "Where receipts are sent") {
        QeetrixTextField(email, label = "Work email", placeholder = "e.g. asha@acme.in")
        QeetrixSwitch(alerts, { alerts = it }, label = "Email alerts")
        QeetrixButton("Save changes", onClick = ::save, loading = saving)
    }
}

// README.md, "Components"
@Composable
private fun ReadmeForm() {
    val email = rememberTextFieldState()
    val password = rememberTextFieldState()
    val emailError: String? = null
    val problems = emptyList<QeetrixFormError>()
    val signingIn = false
    QeetrixForm(errors = problems) {
        QeetrixTextField(email, label = "Work email", error = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
        QeetrixPasswordInput(password, label = "Password")
        QeetrixFormActions { QeetrixButton("Sign in", onClick = ::signIn, size = QeetrixButtonSize.Lg, loading = signingIn) }
    }
}

// README.md, "Typography" and "Icons"
@Composable
private fun ReadmeTypeAndIcons() {
    QeetrixText("Signed in on 3 devices", size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
    BasicText("inv_2K9", style = QeetrixTheme.typography.codeSm)

    QeetrixIcon(QeetrixIconName.Search, contentDescription = null)                 // decorative
    QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked", size = QeetrixSize.IconLg)
}

// Button.md
@Composable
private fun ButtonExamples() {
    val revoking = false
    QeetrixButton("Create invoice", onClick = ::create, icon = QeetrixIconName.Plus)
    QeetrixButton("Export", onClick = ::export, variant = QeetrixButtonVariant.Soft)
    QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Ghost)
    QeetrixButton("Revoke key", onClick = ::revoke, tone = QeetrixButtonTone.Danger, loading = revoking)
}

// IconButton.md
@Composable
private fun IconButtonExamples() {
    val refreshing = true
    QeetrixIconButton(QeetrixIconName.X, contentDescription = "Close", onClick = ::dismiss, tone = QeetrixButtonTone.Neutral)
    QeetrixIconButton(QeetrixIconName.Plus, contentDescription = "New invoice", onClick = ::create, variant = QeetrixButtonVariant.Solid)
    QeetrixIconButton(QeetrixIconName.RefreshCw, contentDescription = "Refresh", onClick = ::refresh, loading = refreshing)
}

// Badge.md
@Composable
private fun BadgeExamples() {
    QeetrixBadge("Paid", tone = QeetrixBadgeTone.Success, icon = QeetrixIconName.CircleCheck)
    QeetrixBadge("Overdue", tone = QeetrixBadgeTone.Danger, variant = QeetrixBadgeVariant.Solid)
    QeetrixBadge("Draft", size = QeetrixBadgeSize.Sm)
}

// Text.md
@Composable
private fun TextExamples() {
    QeetrixHeading("Billing", level = 2)
    QeetrixHeading("Welcome to Qeet", level = 1, size = QeetrixHeadingSize.DisplaySm)
    QeetrixText("Invoices are sent on the first of each month.", tone = QeetrixTextTone.Subtle)
    QeetrixText("inv_2K9", mono = true, size = QeetrixTextSize.Sm)
    QeetrixText(
        buildAnnotatedString {
            append("Ends ")
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) { append("in 3 days") }
        },
    )
}

// Spinner.md
@Composable
private fun SpinnerExamples() {
    QeetrixSpinner(label = "Loading invoices")
    QeetrixSpinner(size = QeetrixSpinnerSize.Lg, tone = QeetrixSpinnerTone.Brand)
}

// Separator.md
@Composable
private fun SeparatorExamples() {
    QeetrixSeparator()
    Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
        QeetrixText("Docs")
        QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
        QeetrixText("Status")
    }
}

// Card.md
@Composable
private fun CardExamples() {
    QeetrixCard(
        title = "Workspace",
        description = "3 members",
        actions = { QeetrixButton("View all", onClick = ::openMembers, variant = QeetrixButtonVariant.Ghost) },
        footer = { QeetrixButton("Manage", onClick = ::manage) },
    ) {
        QeetrixText("Created 12 March.")
    }
    // The page writes `footer = { … }`.
    QeetrixCard(title = "Delete workspace", tone = QeetrixCardTone.Danger, footer = {
        QeetrixButton("Delete workspace", onClick = ::delete, tone = QeetrixButtonTone.Danger)
    })
}

// Avatar.md
@Composable
private fun AvatarExamples() {
    val logo: Painter = ColorPainter(QeetrixTheme.colors.bgBrandBold)
    QeetrixAvatar("Priya Sharma")
    QeetrixAvatar("Acme Pvt Ltd", size = QeetrixAvatarSize.Lg, shape = QeetrixAvatarShape.Square, image = logo)
    QeetrixAvatarItem("Priya Sharma", description = "priya.sharma@acme.in")
}

// Progress.md
@Composable
private fun ProgressExamples() {
    val uploaded = 30f
    val total = 120f
    val used = 8.2f
    val quota = 10f
    QeetrixProgress(uploaded, max = total, label = "Uploading report.pdf", showValue = true)
    QeetrixProgress(null, label = "Preparing export")
    QeetrixMeter(used, max = quota, label = "Storage", showValue = true, tone = QeetrixMeterTone.Warning)
    QeetrixMeter(40f, label = "People with two-step", showValue = true, target = 80f)
}

// TextField.md
@Composable
private fun TextFieldExample() {
    val emailError: String? = null
    val email = rememberTextFieldState()
    QeetrixTextField(
        email,
        label = "Work email",
        placeholder = "e.g. asha@acme.in",
        icon = QeetrixIconName.Mail,
        error = emailError, // "Enter a work email, like asha@acme.in."
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
    )
}

// Field.md
@Composable
private fun FieldExamples() {
    var selected by remember { mutableStateOf("Chime") }
    val onSelect: (String) -> Unit = { selected = it }
    val toneError: String? = null
    val line1 = rememberTextFieldState()
    val line2 = rememberTextFieldState()

    QeetrixField(label = "Notification tone", description = "How alerts sound.", error = toneError) {
        ToneSwatches(selected, onSelect, Modifier.qeetrixFieldControl())
    }

    QeetrixFieldSet("Billing address", description = "Where we send invoices.") {
        QeetrixTextField(line1, label = "Address line 1")
        QeetrixTextField(line2, label = "Address line 2", optional = true)
    }
}

// Textarea.md
@Composable
private fun TextareaExample() {
    val feedback = rememberTextFieldState()
    QeetrixTextarea(feedback, label = "What could be better?", maxRows = 8,
        description = "Up to 500 characters.", inputTransformation = InputTransformation.maxLength(500))
}

// PasswordInput.md
@Composable
private fun PasswordInputExample() {
    val password = rememberTextFieldState()
    QeetrixPasswordInput(password, label = "Password",
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), onKeyboardAction = { signIn() })
}

// OTPInput.md
@Composable
private fun OTPInputExample() {
    val failed = false
    val code = rememberTextFieldState()
    QeetrixOTPInput(code, label = "Verification code", size = QeetrixOTPInputSize.Lg, groups = listOf(3, 3),
        error = if (failed) "That code didn't work. Enter the new one we sent." else null,
        onValueComplete = { verify(it) })
}

// Checkbox.md
@Composable
private fun CheckboxExamples() {
    var updates by remember { mutableStateOf(false) }
    var channels by remember { mutableStateOf(setOf("email")) }
    QeetrixCheckbox(updates, { updates = it }, label = "Product updates", description = "About once a month.")

    QeetrixCheckboxGroup(channels, { channels = it },
        listOf(QeetrixOption("email", "Email"), QeetrixOption("sms", "SMS")), label = "Notify me by",
        error = if (channels.isEmpty()) "Choose at least one way to reach you." else null)
}

// RadioGroup.md
@Composable
private fun RadioGroupExample() {
    var cycle by rememberSaveable { mutableStateOf("monthly") }
    QeetrixRadioGroup(cycle, { cycle = it },
        listOf(QeetrixOption("monthly", "Monthly"), QeetrixOption("yearly", "Yearly", description = "Two months free.")),
        label = "Billing cycle")
}

// Select.md
@Composable
private fun SelectExample() {
    var cycle by rememberSaveable { mutableStateOf<String?>(null) }
    QeetrixSelect(cycle, { cycle = it }, listOf(QeetrixOption("monthly", "Monthly"), QeetrixOption("yearly", "Yearly")),
        label = "Billing cycle", placeholder = "e.g. Monthly")
}

// NumberField.md
@Composable
private fun NumberFieldExamples() {
    var seats by rememberSaveable { mutableStateOf<Int?>(5) }
    QeetrixNumberField(seats, { seats = it }, label = "Seats", min = 1, max = 250)

    var discount by rememberSaveable { mutableStateOf<BigDecimal?>(BigDecimal("12.5")) }
    QeetrixNumberField(discount, { discount = it }, label = "Discount", endAdornment = { QeetrixText("%") },
        min = BigDecimal.ZERO, max = BigDecimal(100), step = BigDecimal("0.5"), fractionDigits = 1)
}

// Switch.md
@Composable
private fun SwitchExample() {
    var autoRenew by rememberSaveable { mutableStateOf(true) }
    QeetrixSwitch(autoRenew, { autoRenew = it }, label = "Auto-renew", description = "Renews on the 1st of each month.")
}

// Form.md
@Composable
private fun FormExample() {
    val gstin = rememberTextFieldState()
    val tradeName = rememberTextFieldState()
    val gstinFocus = remember { FocusRequester() }
    val gstinError: String? = "Enter a GSTIN, like 27AAPFU0939F1ZV."
    val problems = listOf(QeetrixFormError("Enter a GSTIN, like 27AAPFU0939F1ZV.") { gstinFocus.requestFocus() })
    QeetrixForm(errors = problems) {
        QeetrixTextField(gstin, label = "GSTIN", error = gstinError, modifier = Modifier.focusRequester(gstinFocus))
        QeetrixTextField(tradeName, label = "Trade name", optional = true)
        QeetrixFormActions {
            QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Lg)
            QeetrixButton("Save", onClick = ::submit, size = QeetrixButtonSize.Lg)
        }
    }
}

/** Stands in for a control an app brings: it shows its value as its state. */
@Composable
private fun ToneSwatches(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier
            .size(48.dp)
            .semantics { stateDescription = selected }
            .selectable(selected = true, onClick = { onSelect(if (selected == "Chime") "Bell" else "Chime") }),
    )
}

private fun signIn() = Unit

private fun verify(code: String) = check(code.isNotEmpty())

private fun cancel() = Unit

private fun save() = Unit

private fun submit() = Unit

private fun delete() = Unit

private fun create() = Unit

private fun export() = Unit

private fun revoke() = Unit

private fun refresh() = Unit

private fun dismiss() = Unit

private fun openMembers() = Unit

private fun manage() = Unit
