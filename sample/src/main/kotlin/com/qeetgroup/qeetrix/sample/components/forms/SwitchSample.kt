package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
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
