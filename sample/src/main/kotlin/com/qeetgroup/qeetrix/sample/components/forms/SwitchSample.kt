package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitchSize
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** On/off settings: with a label and help, the sizes, and the disabled, invalid and read-only states. */
@Composable
fun SwitchSample() {
    SamplePage {
        SampleSection("With a label") {
            var alerts by rememberSaveable { mutableStateOf(true) }
            var digest by rememberSaveable { mutableStateOf(false) }
            QeetrixSwitch(alerts, { alerts = it }, label = "Email alerts", description = "Sent within a minute of the event.")
            QeetrixSwitch(digest, { digest = it }, label = "Weekly digest")
            SampleNote(
                "The whole row toggles. TalkBack reads the label, the description, \"switch\" and on or off; the " +
                    "thumb's side and the track's fill show the state, not colour alone.",
            )
        }
        SampleSection("Sizes") {
            var default by rememberSaveable { mutableStateOf(true) }
            var small by rememberSaveable { mutableStateOf(true) }
            SampleFlow {
                QeetrixSwitch(default, { default = it }, Modifier.semantics { contentDescription = "Default size" })
                QeetrixSwitch(small, { small = it }, Modifier.semantics { contentDescription = "Small size" }, size = QeetrixSwitchSize.Sm)
            }
            SampleNote("A switch without a label needs a name of its own, here a content description.")
        }
        SampleSection("States") {
            var terms by rememberSaveable { mutableStateOf(false) }
            QeetrixSwitch(true, {}, label = "Two-step sign-in", description = "Managed by your organisation.", enabled = false)
            QeetrixSwitch(
                terms,
                { terms = it },
                label = "Accept the terms",
                description = if (terms) "Thank you." else "Required to continue.",
                invalid = !terms,
            )
            QeetrixSwitch(true, onCheckedChange = null, label = "Backups", description = "Always on for this plan.")
            SampleNote("Disabled; invalid until it is switched on; and read-only, which shows a state the user cannot change.")
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
    SamplePreview { QeetrixSwitch(true, {}, label = "Email alerts", description = "Sent within a minute of the event.") }
}
