// The examples keep the documentation's compact layout, so they can be compared with it line by line.
@file:Suppress(
    "ktlint:standard:argument-list-wrapping",
    "ktlint:standard:class-signature",
    "ktlint:standard:no-multi-spaces",
    "ktlint:standard:trailing-comma-on-call-site",
    "ktlint:standard:wrapping",
)

package com.qeetgroup.qeetrix.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.checkbox.QeetrixTriStateCheckbox
import com.qeetgroup.qeetrix.components.form.QeetrixFormActions
import com.qeetgroup.qeetrix.components.form.QeetrixFormError
import com.qeetgroup.qeetrix.components.form.QeetrixFormErrorSummary
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordPurpose
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal

/**
 * The Phase 3 examples in `README.md` and the component pages in `Documentation/Components`, as written
 * there, so they keep compiling against the public API and composing. Only the surrounding state and the app's own functions
 * are added here. Values are synthetic.
 */
@RunWith(RobolectricTestRunner::class)
class DocumentationExamplesTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun everyFormExampleComposes() {
        compose.setContent {
            QeetrixTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    ReadmeForm()
                    FieldExamples()
                    TextareaExample()
                    PasswordInputExample()
                    OTPInputExample()
                    CheckboxExamples()
                    RadioGroupExample()
                    SelectExample()
                    NumberFieldExamples()
                    FormExample()
                }
            }
        }
        for (label in listOf(
            "Work email",
            "Tone",
            "Notifications",
            "What could be better?",
            "Password",
            "Verification code",
            "Product updates",
            "All members",
            "Plan",
            "Data region",
            "Seats",
            "Discount (%)",
            "Full name",
        )) {
            val nodes = compose.onAllNodesWithText(label, substring = true, useUnmergedTree = true).fetchSemanticsNodes()
            assertTrue(label, nodes.isNotEmpty())
        }
    }
}

// README.md, "Components"
@Composable
private fun ReadmeForm() {
    val email = rememberTextFieldState()
    val password = rememberTextFieldState()
    val emailError: QeetrixFieldMessage? = null
    val signingIn = false
    Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.fieldGap)) {
        QeetrixTextField(email, label = "Work email", required = true, message = emailError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
        QeetrixPasswordInput(password, label = "Password", purpose = QeetrixPasswordPurpose.Current)
        QeetrixFormActions { QeetrixButton("Sign in", onClick = ::signIn, loading = signingIn) }
    }
}

// Field.md
@Composable
private fun FieldExamples() {
    var tone by remember { mutableStateOf("Chime") }
    val onTone: (String) -> Unit = { tone = it }
    var email by remember { mutableStateOf(true) }
    var push by remember { mutableStateOf(false) }
    val groupMessage: QeetrixFieldMessage? = null

    QeetrixField(label = "Tone", description = "How notifications sound.", required = true) {
        ToneChooser(tone, onTone, Modifier.qeetrixFieldControl())   // a control Qeetrix does not provide
    }

    QeetrixFieldSet("Notifications", description = "Choose how we reach you.", message = groupMessage) {
        QeetrixCheckbox(email, { email = it }, label = "Email")
        QeetrixCheckbox(push, { push = it }, label = "Push")
    }
}

// Textarea.md
@Composable
private fun TextareaExample() {
    val feedback = rememberTextFieldState()
    QeetrixTextarea(feedback, label = "What could be better?", description = "Up to 500 characters.",
        inputTransformation = InputTransformation.maxLength(500))
}

// PasswordInput.md
@Composable
private fun PasswordInputExample() {
    val password = rememberTextFieldState()
    QeetrixPasswordInput(password, label = "Password", purpose = QeetrixPasswordPurpose.Current,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), onKeyboardAction = { signIn() })
}

// OTPInput.md
@Composable
private fun OTPInputExample() {
    val code = rememberTextFieldState()
    QeetrixOTPInput(code, label = "Verification code", groupSize = 3, description = "Sent to •••• 4821") { completed ->
        verify(completed)   // your app's
    }
}

// Checkbox.md
@Composable
private fun CheckboxExamples() {
    var updates by remember { mutableStateOf(false) }
    val allState = ToggleableState.Indeterminate
    QeetrixCheckbox(updates, { updates = it }, label = "Product updates", description = "About once a month.")
    QeetrixTriStateCheckbox(allState, onClick = { selectAll(allState != ToggleableState.On) }, label = "All members")
}

private enum class Plan(val title: String, val detail: String) {
    Starter("Starter", "For one team"),
    Team("Team", "Up to 50 people"),
}

// RadioGroup.md
@Composable
private fun RadioGroupExample() {
    var plan by rememberSaveable { mutableStateOf(Plan.Team) }
    QeetrixRadioGroup(plan, { plan = it }, Plan.entries.map { QeetrixOption(it, it.title, it.detail) }, label = "Plan")
}

private class Region(val id: String, val name: String)

private val regions = listOf(Region("in-south", "India (South)"), Region("in-west", "India (West)"))

// Select.md
@Composable
private fun SelectExample() {
    var region by rememberSaveable { mutableStateOf<String?>(null) }
    QeetrixSelect(region, { region = it }, regions.map { QeetrixOption(it.id, it.name) },
        label = "Data region", placeholder = "Choose a region")
}

// NumberField.md
@Composable
private fun NumberFieldExamples() {
    var seats by rememberSaveable { mutableStateOf<Int?>(5) }
    QeetrixNumberField(seats, { seats = it }, label = "Seats", range = 1..250)

    var discount by rememberSaveable { mutableStateOf<BigDecimal?>(BigDecimal("12.5")) }
    QeetrixNumberField(discount, { discount = it }, label = "Discount (%)",
        range = BigDecimal.ZERO..BigDecimal(100), step = BigDecimal("0.5"), fractionDigits = 1)
}

// Form.md
@Composable
private fun FormExample() {
    val name = rememberTextFieldState()
    val nameFocus = remember { FocusRequester() }
    val summaryFocus = remember { FocusRequester() }
    val nameMessage: QeetrixFieldMessage? = null
    val errors = emptyList<QeetrixFormError>()
    val saving = false
    Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.fieldGap)) {
        QeetrixFormErrorSummary(errors, Modifier.focusRequester(summaryFocus))
        QeetrixTextField(name, Modifier.focusRequester(nameFocus), label = "Full name", required = true, message = nameMessage)
        QeetrixFormActions {
            QeetrixButton("Cancel", onClick = ::cancel, variant = QeetrixButtonVariant.Outline)
            QeetrixButton("Save", onClick = ::save, loading = saving, loadingLabel = "Saving…")
        }
    }
}

/** Stands in for a control an app brings: it shows its value as its state. */
@Composable
private fun ToneChooser(
    tone: String,
    onTone: (String) -> Unit,
    modifier: Modifier,
) {
    Box(modifier.size(48.dp).semantics { stateDescription = tone }.clickable { onTone(if (tone == "Chime") "Bell" else "Chime") })
}

private fun signIn() = Unit

private fun verify(code: String) = check(code.isNotEmpty())

private fun selectAll(all: Boolean) = Unit

private fun cancel() = Unit

private fun save() = Unit
