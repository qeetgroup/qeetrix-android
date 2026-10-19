package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.checkbox.QeetrixTriStateCheckbox
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Checkboxes: a "select all" with a mixed state, a required choice, and every state on its own. */
@Composable
fun CheckboxSample() {
    SamplePage {
        SampleSection("Select all") {
            val members = listOf("Ada Lovelace", "Grace Hopper", "Priya Raman")
            val chosen = remember { mutableStateListOf("Grace Hopper") }
            val all =
                when (chosen.size) {
                    0 -> ToggleableState.Off
                    members.size -> ToggleableState.On
                    else -> ToggleableState.Indeterminate
                }
            QeetrixFieldSet("Invite to the workspace") {
                QeetrixTriStateCheckbox(
                    all,
                    onClick = {
                        chosen.clear()
                        if (all != ToggleableState.On) chosen.addAll(members)
                    },
                    label = "All members",
                )
                for (member in members) {
                    QeetrixCheckbox(member in chosen, { if (it) chosen += member else chosen -= member }, label = member)
                }
            }
            SampleNote(
                "The parent shows mixed while some are chosen; tapping it chooses all, or clears all when all are " +
                    "chosen. TalkBack reads mixed as \"partially checked\".",
            )
        }
        SampleSection("A required choice") {
            var accepted by rememberSaveable { mutableStateOf(false) }
            QeetrixFieldSet(legend = null, message = if (accepted) null else QeetrixFieldMessage("Accept the terms to continue.")) {
                QeetrixCheckbox(
                    accepted,
                    { accepted = it },
                    label = "I accept the terms of service",
                    description = "The sample keeps no record of this.",
                )
            }
        }
        SampleSection("States") {
            SampleFlow {
                QeetrixCheckbox(false, {}, Modifier.semantics { contentDescription = "Unchecked" })
                QeetrixCheckbox(true, {}, Modifier.semantics { contentDescription = "Checked" })
                QeetrixTriStateCheckbox(ToggleableState.Indeterminate, {}, Modifier.semantics { contentDescription = "Mixed" })
                QeetrixCheckbox(false, {}, Modifier.semantics { contentDescription = "Invalid" }, invalid = true)
                QeetrixCheckbox(true, {}, Modifier.semantics { contentDescription = "Disabled" }, enabled = false)
            }
            QeetrixCheckbox(true, onCheckedChange = null, label = "Backups", description = "Always on for this plan.")
            SampleNote("A bare checkbox needs a name from its context. The last one shows a state that cannot be changed.")
        }
    }
}

@PreviewLightDark
@Composable
private fun CheckboxSamplePreview() {
    SamplePreview { CheckboxSample() }
}
