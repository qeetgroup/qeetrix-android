package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Settings that apply at once: labelled switches, a bare switch, and the states. */
@Composable
fun SwitchSample() {
    SamplePage {
        SampleSection("Settings") {
            var autoRenew by rememberSaveable { mutableStateOf(true) }
            var alerts by rememberSaveable { mutableStateOf(false) }
            QeetrixSwitch(autoRenew, { autoRenew = it }, label = "Auto-renew", description = "Renews on the 1st of each month.")
            QeetrixSwitch(alerts, { alerts = it }, label = "Email alerts")
            SampleNote(
                "The switch comes first and its label 8 dp after it; the whole row toggles. A setting that waits for a " +
                    "Save button is a checkbox instead.",
            )
        }
        SampleSection("At a row's end") {
            var receipts by rememberSaveable { mutableStateOf(true) }
            Row(
                Modifier.fillMaxWidth().padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    QeetrixText("Read receipts")
                    QeetrixText("Admins still see who read announcements.", size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
                }
                QeetrixSwitch(receipts, { receipts = it }, Modifier.semantics { contentDescription = "Read receipts" })
            }
            SampleNote(
                "A bare switch, named for TalkBack with a content description. It is laid out at its 32 × 18 dp track, so " +
                    "the row keeps the web's height, and a tap within 48 dp of the track still toggles it.",
            )
        }
        SampleSection("States") {
            SampleFlow {
                QeetrixSwitch(true, {}, label = "Disabled", enabled = false)
                QeetrixSwitch(true, null, label = "Read-only")
                QeetrixSwitch(false, {}, label = "Invalid", invalid = true)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SwitchSamplePreview() {
    SamplePreview { SwitchSample() }
}

@Preview(name = "Right to left", locale = "ar")
@Composable
private fun SwitchRightToLeftPreview() {
    SamplePreview { QeetrixSwitch(true, {}, label = "Auto-renew") }
}
