package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Field composition: a field around a control Qeetrix does not have, help, and sections of fields. */
@Composable
fun FieldSample() {
    SamplePage {
        SampleSection("A field around your own control") {
            var tone by rememberSaveable { mutableStateOf<String?>(null) }
            QeetrixField(
                label = "Notification tone",
                description = "How alerts sound on this device.",
                error = if (tone == null) "Choose a tone." else null,
                help = "Alerts for failed payments always sound, whatever you choose here.",
            ) {
                // A custom choice control: the field names it and marks it, and it states its own value.
                Row(
                    Modifier.qeetrixFieldControl().selectableGroup().semantics { stateDescription = tone ?: "" },
                    horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
                ) {
                    for (option in listOf("Calm", "Bright")) {
                        QeetrixChip(option, selected = option == tone, onSelectedChange = { tone = option })
                    }
                }
            }
            SampleNote(
                "qeetrixFieldControl() gives the control the field's label and error, and hides the visible label from " +
                    "TalkBack so it is read once. The error replaces the help text until a tone is chosen.",
            )
        }
        SampleSection("Sections of fields") {
            QeetrixFieldSet("Billing address", description = "Where we send invoices.") {
                QeetrixTextField(rememberTextFieldState(), label = "Address line 1", placeholder = "e.g. 12 MG Road")
                QeetrixTextField(rememberTextFieldState(), label = "Address line 2", optional = true)
                QeetrixTextField(rememberTextFieldState(), label = "PIN code", placeholder = "e.g. 560001")
            }
            SampleNote("The legend is a heading in title.sm; the fields stand the density's form gap apart.")
        }
        SampleSection("A disabled section") {
            QeetrixFieldSet("Billing", description = "Managed by your organisation.", enabled = false) {
                QeetrixTextField(rememberTextFieldState("billing@acme.in"), label = "Billing email")
            }
            SampleNote("A disabled set disables every Qeetrix control inside it.")
        }
    }
}

@PreviewLightDark
@Composable
private fun FieldSamplePreview() {
    SamplePreview { FieldSample() }
}
