package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.button.QeetrixIconButtonSize
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The text field: labels, help, validation messages, keyboard behaviour, adornments and the states. */
@Composable
fun TextFieldSample() {
    SamplePage {
        SampleSection("Label, placeholder and help") {
            QeetrixTextField(
                rememberTextFieldState(),
                label = "Full name",
                placeholder = "Ada Lovelace",
                description = "As it appears on your ID.",
                required = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                contentType = ContentType.PersonFullName,
            )
            QeetrixTextField(rememberTextFieldState(), label = "Company", optional = true)
            SampleNote(
                "The asterisk is visual only; say \"required\" in the description when it matters. " +
                    "TalkBack reads the label, the text, then the description.",
            )
        }
        SampleSection("Validation") {
            val email = rememberTextFieldState("ada@")
            QeetrixTextField(
                email,
                label = "Work email",
                message = emailMessage(email),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
            )
            val seats = rememberTextFieldState("12")
            QeetrixTextField(
                seats,
                label = "Seats",
                message = seatsMessage(seats),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                inputTransformation = DigitsOnly.then(InputTransformation.maxLength(4)),
            )
            SampleNote(
                "An error marks the field invalid for TalkBack and is announced as it appears; a warning or " +
                    "success is announced politely. Each has an icon as well as a colour. Seats accepts digits only.",
            )
        }
        SampleSection("Keyboard action and adornments") {
            val query = rememberTextFieldState()
            var submitted by rememberSaveable { mutableStateOf("") }
            QeetrixTextField(
                query,
                label = "Search",
                placeholder = "Search people",
                leading = { QeetrixIcon(QeetrixIconName.Search, contentDescription = null, tint = QeetrixTheme.colors.textTertiary) },
                trailing = {
                    if (query.text.isNotEmpty()) {
                        QeetrixIconButton(
                            QeetrixIconName.X,
                            contentDescription = "Clear search",
                            onClick = { query.clearText() },
                            size = QeetrixIconButtonSize.Xs,
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                onKeyboardAction = { submitted = query.text.toString() },
            )
            SampleNote(if (submitted.isEmpty()) "Press the keyboard's search key." else "Searched for \"$submitted\".")
        }
        SampleSection("Read-only and disabled") {
            QeetrixTextField(rememberTextFieldState("QX-2041"), label = "Order number", readOnly = true)
            QeetrixTextField(rememberTextFieldState("Business"), label = "Plan", enabled = false)
            SampleNote("Read-only text can still be focused, selected and copied; disabled text cannot be focused.")
        }
    }
}

private val DigitsOnly = InputTransformation { if (!asCharSequence().all(Char::isDigit)) revertAllChanges() }

private fun emailMessage(state: TextFieldState): QeetrixFieldMessage? {
    val text = state.text
    return when {
        text.isEmpty() -> null
        Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(text) -> QeetrixFieldMessage("Looks good", QeetrixFieldMessageTone.Success)
        else -> QeetrixFieldMessage("Enter an email address like name@company.com.")
    }
}

private fun seatsMessage(state: TextFieldState): QeetrixFieldMessage? {
    val seats = state.text.toString().toIntOrNull() ?: return null
    return if (seats > 10) QeetrixFieldMessage("Your plan includes 10 seats.", QeetrixFieldMessageTone.Warning) else null
}

@PreviewLightDark
@Composable
private fun TextFieldSamplePreview() {
    SamplePreview { TextFieldSample() }
}

@Preview(name = "Right to left", locale = "ar")
@Composable
private fun TextFieldRightToLeftPreview() {
    SamplePreview {
        QeetrixTextField(
            TextFieldState("ada@"),
            label = "Work email",
            message = QeetrixFieldMessage("Enter an email address like name@company.com."),
        )
    }
}
