package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
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
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The text field: label, help and "(optional)", errors that replace the help, adornments, the keyboard and the states. */
@Composable
fun TextFieldSample() {
    SamplePage {
        SampleSection("Label, placeholder and help") {
            QeetrixTextField(
                rememberTextFieldState(),
                label = "Full name",
                placeholder = "e.g. Asha Rao",
                description = "As it appears on your PAN.",
                required = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                contentType = ContentType.PersonFullName,
            )
            QeetrixTextField(rememberTextFieldState(), label = "Trade name", optional = true, placeholder = "e.g. Acme Pvt Ltd")
            QeetrixTextField(
                rememberTextFieldState("3600"),
                label = "Token lifetime",
                help = "How long an access token stays valid before the app asks for a new one.",
                endAdornment = { QeetrixText("seconds") },
            )
            SampleNote(
                "Optional fields say so; required ones carry no asterisk and say \"required\" to TalkBack only. The " +
                    "placeholder is an example, never the label. The ⓘ opens the help in a popover.",
            )
        }
        SampleSection("Errors") {
            val email = rememberTextFieldState("asha@")
            QeetrixTextField(
                email,
                label = "Work email",
                description = "We send receipts here.",
                error = emailError(email),
                icon = QeetrixIconName.Mail,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
            )
            val seats = rememberTextFieldState("12")
            QeetrixTextField(
                seats,
                label = "Seats",
                error = seatsError(seats),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                inputTransformation = DigitsOnly.then(InputTransformation.maxLength(4)),
            )
            SampleNote(
                "An error replaces the help text, frames the field in the danger colour, says what to do, and is " +
                    "announced as it appears. Seats takes digits only.",
            )
        }
        SampleSection("Adornments and the keyboard") {
            val amount = rememberTextFieldState()
            var submitted by rememberSaveable { mutableStateOf("") }
            QeetrixTextField(
                amount,
                label = "Amount",
                placeholder = "e.g. 2,500",
                startAdornment = { QeetrixText("₹") },
                endAdornment = { QeetrixText("per month") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                onKeyboardAction = { submitted = amount.text.toString() },
            )
            SampleNote(if (submitted.isEmpty()) "Press the keyboard's done key." else "Entered ₹$submitted.")
        }
        SampleSection("Read-only and disabled") {
            QeetrixTextField(rememberTextFieldState("QX-2041"), label = "Order number", readOnly = true)
            QeetrixTextField(rememberTextFieldState("Business"), label = "Plan", enabled = false, description = "Your admin sets this.")
            SampleNote("Read-only text can still be focused, selected and copied; disabled text cannot be focused.")
        }
        SampleSection("Keys and codes") {
            QeetrixTextField(rememberTextFieldState(), label = "Setup key", placeholder = "e.g. JBSW Y3DP EHPK 3PXP", mono = true)
            QeetrixTextField(rememberTextFieldState("KM7QX-4TRWP"), label = "Pairing code", mono = true, readOnly = true)
            SampleNote("Mono draws the value in Fira Code, so 0 and O, 1, I and l can't be confused; the box keeps its height.")
        }
        SampleSection("A long placeholder") {
            QeetrixTextField(
                rememberTextFieldState(),
                label = "Registered address",
                placeholder = "e.g. 4th Floor, Prestige Tech Park, Outer Ring Road, Bengaluru",
            )
            SampleNote("At large text a placeholder too long for the field ends in an ellipsis, instead of being cut silently.")
        }
    }
}

private val DigitsOnly = InputTransformation { if (!asCharSequence().all(Char::isDigit)) revertAllChanges() }

private fun emailError(state: TextFieldState): String? {
    val text = state.text
    return if (text.isEmpty() || Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(text)) null else "Enter a work email, like asha@acme.in."
}

private fun seatsError(state: TextFieldState): String? {
    val seats = state.text.toString().toIntOrNull() ?: return null
    return if (seats > 10) "Enter 10 seats or fewer, or upgrade your plan." else null
}

@PreviewLightDark
@Composable
private fun TextFieldSamplePreview() {
    SamplePreview { TextFieldSample() }
}

@Preview(name = "Right to left", locale = "ar")
@Composable
private fun TextFieldRightToLeftPreview() {
    SamplePreview { QeetrixTextField(TextFieldState("asha@"), label = "Work email", error = "Enter a work email, like asha@acme.in.") }
}
