package com.qeetgroup.qeetrix.sample.components.forms

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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.form.QeetrixForm
import com.qeetgroup.qeetrix.components.form.QeetrixFormActions
import com.qeetgroup.qeetrix.components.form.QeetrixFormError
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import kotlinx.coroutines.delay

/**
 * The increment-3 form: GSTIN, PAN, an optional trade name, a billing-cycle question, an auto-renew switch, and a
 * "server" error on one field. Submitting shows each field's error and the summary, whose links move focus to the
 * fields. The rules are the sample's; nothing is sent anywhere.
 */
@Composable
fun FormSample() {
    SamplePage {
        SampleSection("Business details") { BusinessForm() }
        SampleNote(
            "Qeetrix shows the errors; the rules here are the sample's. The summary is worded as what to do, takes " +
                "focus when it appears, and each item is worded exactly as the field's own error.",
        )
    }
}

@Composable
private fun BusinessForm() {
    val gstin = rememberTextFieldState()
    val pan = rememberTextFieldState()
    val tradeName = rememberTextFieldState()
    var cycle by rememberSaveable { mutableStateOf<String?>(null) }
    var autoRenew by rememberSaveable { mutableStateOf(true) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }
    val gstinFocus = remember { FocusRequester() }
    val panFocus = remember { FocusRequester() }

    val gstinError = if (submitted && gstin.text.length != 15) "Enter a GSTIN, like 27AAPFU0939F1ZV." else null
    // The "server" knows this PAN already: an error that arrives after a submit.
    val panError =
        when {
            submitted && pan.text.length != 10 -> "Enter a PAN, like AAPFU0939F."
            submitted && pan.text.toString() == "AAPFU0939F" -> "That PAN is already registered. Sign in instead."
            else -> null
        }
    val cycleError = if (submitted && cycle == null) "Choose a billing cycle." else null
    val errors =
        listOfNotNull(
            gstinError?.let { QeetrixFormError(it) { gstinFocus.requestFocus() } },
            panError?.let { QeetrixFormError(it) { panFocus.requestFocus() } },
            cycleError?.let { QeetrixFormError(it) },
        )
    LaunchedEffect(saving) {
        if (saving) {
            delay(1_200)
            saving = false
            saved = true
        }
    }

    QeetrixForm(errors = errors) {
        QeetrixTextField(
            gstin,
            label = "GSTIN",
            modifier = Modifier.focusRequester(gstinFocus),
            description = "15 characters.",
            error = gstinError,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
        )
        QeetrixTextField(
            pan,
            label = "PAN",
            modifier = Modifier.focusRequester(panFocus),
            error = panError,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
        )
        QeetrixTextField(tradeName, label = "Trade name", optional = true, placeholder = "e.g. Acme Pvt Ltd")
        QeetrixRadioGroup(
            cycle,
            { cycle = it },
            listOf(QeetrixOption("monthly", "Monthly"), QeetrixOption("yearly", "Yearly", description = "Two months free.")),
            label = "Billing cycle",
            error = cycleError,
        )
        QeetrixSwitch(autoRenew, { autoRenew = it }, label = "Auto-renew")
        if (saved && errors.isEmpty()) QeetrixBadge("Saved", tone = QeetrixBadgeTone.Success, icon = QeetrixIconName.CircleCheck)
        QeetrixFormActions {
            QeetrixButton("Cancel", onClick = { submitted = false }, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Lg)
            QeetrixButton(
                "Save",
                onClick = {
                    submitted = true
                    saved = false
                    if (isValid(gstin, pan) && cycle != null) saving = true
                },
                size = QeetrixButtonSize.Lg,
                loading = saving,
            )
        }
    }
}

private fun isValid(
    gstin: TextFieldState,
    pan: TextFieldState,
): Boolean = gstin.text.length == 15 && pan.text.length == 10 && pan.text.toString() != "AAPFU0939F"

@PreviewLightDark
@Composable
private fun FormSamplePreview() {
    SamplePreview { FormSample() }
}
