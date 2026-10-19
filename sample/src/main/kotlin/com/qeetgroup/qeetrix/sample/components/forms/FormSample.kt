package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.form.QeetrixFormActions
import com.qeetgroup.qeetrix.components.form.QeetrixFormError
import com.qeetgroup.qeetrix.components.form.QeetrixFormErrorSummary
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay

/**
 * Form validation presentation: a profile form whose rules belong to the sample. Submitting shows each
 * field's message and an error summary whose links move focus to the field; the keyboard's Next moves
 * through the fields. Nothing is sent anywhere.
 */
@Composable
fun FormSample() {
    SamplePage {
        SampleSection("Profile information") {
            ProfileForm()
        }
        SampleNote(
            "Qeetrix shows the messages; the rules here are the sample's. A form is a Column spaced by the density's " +
                "field gap; QeetrixFormActions stacks the buttons on a phone, primary on top.",
        )
    }
}

@Composable
private fun ProfileForm() {
    val name = rememberTextFieldState()
    val email = rememberTextFieldState()
    var role by rememberSaveable { mutableStateOf<String?>(null) }
    val bio = rememberTextFieldState()
    var submitted by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }
    val summaryFocus = remember { FocusRequester() }
    val nameFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val roleFocus = remember { FocusRequester() }

    val nameError = if (submitted && name.text.isBlank()) "Enter your name." else null
    val emailError = if (submitted && !looksLikeEmail(email)) "Enter an email address like name@example.com." else null
    val roleError = if (submitted && role == null) "Choose your role." else null
    val errors =
        listOfNotNull(
            nameError?.let { QeetrixFormError(it) { nameFocus.requestFocus() } },
            emailError?.let { QeetrixFormError(it) { emailFocus.requestFocus() } },
            roleError?.let { QeetrixFormError(it) { roleFocus.requestFocus() } },
        )
    LaunchedEffect(saving) {
        if (saving) {
            delay(1_200)
            saving = false
            saved = true
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.fieldGap)) {
        QeetrixFormErrorSummary(errors, Modifier.focusRequester(summaryFocus))
        QeetrixTextField(
            name,
            Modifier.focusRequester(nameFocus),
            label = "Full name",
            required = true,
            message = nameError?.let { QeetrixFieldMessage(it) },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
            contentType = ContentType.PersonFullName,
        )
        QeetrixTextField(
            email,
            Modifier.focusRequester(emailFocus),
            label = "Email",
            required = true,
            message = emailError?.let { QeetrixFieldMessage(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
        )
        QeetrixSelect(
            role,
            { role = it },
            listOf("Design", "Engineering", "Operations", "Sales").map { QeetrixOption(it, it) },
            Modifier.focusRequester(roleFocus),
            label = "Role",
            placeholder = "Choose your role",
            required = true,
            message = roleError?.let { QeetrixFieldMessage(it) },
        )
        QeetrixTextarea(bio, label = "About you", optional = true, placeholder = "A line or two for your colleagues.")
        if (saved &&
            errors.isEmpty()
        ) {
            QeetrixBadge("Profile saved", variant = QeetrixBadgeVariant.Success, icon = QeetrixIconName.CircleCheck)
        }
        QeetrixFormActions {
            QeetrixButton("Cancel", onClick = { submitted = false }, variant = QeetrixButtonVariant.Outline)
            QeetrixButton(
                "Save profile",
                onClick = {
                    submitted = true
                    saved = false
                    val invalid = name.text.isBlank() || !looksLikeEmail(email) || role == null
                    if (invalid) summaryFocus.requestFocus() else saving = true
                },
                loading = saving,
                loadingLabel = "Saving…",
            )
        }
    }
}

private fun looksLikeEmail(state: TextFieldState): Boolean = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(state.text)

@PreviewLightDark
@Composable
private fun FormSamplePreview() {
    SamplePreview { FormSample() }
}
