package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Field composition: a field around a control Qeetrix does not have, and field sets with a group message. */
@Composable
fun FieldSample() {
    SamplePage {
        SampleSection("A field around your own control") {
            var tone by rememberSaveable { mutableStateOf("Calm") }
            QeetrixField(label = "Tone", description = "How notifications sound.", required = true) {
                // A custom choice control: the field names it and marks it, and it states its own value.
                Row(
                    Modifier.qeetrixFieldControl().selectableGroup().semantics { stateDescription = tone },
                    horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.S),
                ) {
                    for (option in listOf("Calm", "Bright")) {
                        QeetrixBadge(
                            option,
                            Modifier.selectable(option == tone, role = Role.RadioButton) { tone = option },
                            variant = if (option == tone) QeetrixBadgeVariant.Brand else QeetrixBadgeVariant.Outline,
                        )
                    }
                }
            }
            SampleNote(
                "qeetrixFieldControl() gives the control the field's label (\"Tone, required\") and error, and hides the " +
                    "visible label from TalkBack so it is read once.",
            )
        }
        SampleSection("Field sets") {
            var email by rememberSaveable { mutableStateOf(false) }
            var push by rememberSaveable { mutableStateOf(false) }
            QeetrixFieldSet(
                "Notifications",
                description = "Choose how we reach you.",
                message = if (email || push) null else QeetrixFieldMessage("Choose at least one."),
            ) {
                QeetrixCheckbox(email, { email = it }, label = "Email")
                QeetrixCheckbox(push, { push = it }, label = "Push")
                QeetrixTextField(rememberTextFieldState(), label = "Quiet hours", optional = true, placeholder = "22:00–07:00")
            }
            SampleNote(
                "The legend is a heading. The group's message is shown after it and heard with each checkbox; the " +
                    "checkbox rows sit flush, the text field keeps the field gap.",
            )
        }
        SampleSection("A disabled field set") {
            QeetrixFieldSet("Billing", description = "Managed by your organisation.", enabled = false) {
                QeetrixCheckbox(true, {}, label = "Send invoices")
                QeetrixTextField(rememberTextFieldState("billing@example.com"), label = "Billing email")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun FieldSamplePreview() {
    SamplePreview { FieldSample() }
}
