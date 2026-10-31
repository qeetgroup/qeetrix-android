package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckboxGroup
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private val Channels = listOf(QeetrixOption("email", "Email"), QeetrixOption("sms", "SMS"), QeetrixOption("push", "Push notifications"))

/** Checkboxes: one option, a select-all over a group, a question with an error, a wrapping row and tiles. */
@Composable
fun CheckboxSample() {
    SamplePage {
        SampleSection("One option") {
            var updates by rememberSaveable { mutableStateOf(false) }
            QeetrixCheckbox(updates, { updates = it }, label = "Product updates", description = "About once a month.")
            SampleNote("The label and description are part of the target and are read with the box and its state.")
        }
        SampleSection("Select all") {
            var chosen by rememberSaveable { mutableStateOf(setOf("email")) }
            val all = Channels.map { it.value }.toSet()
            QeetrixCheckbox(
                checked = chosen == all,
                onCheckedChange = { on -> chosen = if (on) all else emptySet() },
                label = "All channels",
                indeterminate = chosen.isNotEmpty() && chosen != all,
            )
            QeetrixCheckboxGroup(
                chosen,
                { chosen = it },
                Channels,
                label = "Notify me by",
                error = if (chosen.isEmpty()) "Choose at least one way to reach you." else null,
            )
            SampleNote("A partly chosen group shows a dash; a tap on it checks every option. TalkBack reads \"partially checked\".")
        }
        SampleSection("A wrapping row") {
            var days by rememberSaveable { mutableStateOf(setOf("mon", "wed", "fri")) }
            QeetrixCheckboxGroup(
                days,
                { days = it },
                listOf("Mon", "Tue", "Wed", "Thu", "Fri").map { QeetrixOption(it.lowercase(), it) },
                label = "Working days",
                orientation = QeetrixChoiceOrientation.Horizontal,
            )
        }
        SampleSection("Tiles") {
            var methods by rememberSaveable { mutableStateOf(setOf("passkey")) }
            QeetrixCheckboxGroup(
                methods,
                { methods = it },
                listOf(
                    QeetrixOption("passkey", "Passkeys", "Face, fingerprint or screen lock", icon = QeetrixIconName.Lock),
                    QeetrixOption("email", "Email link", "A one-time link to the work email", icon = QeetrixIconName.Mail),
                    QeetrixOption("sso", "Single sign-on", "Through the company's provider", icon = QeetrixIconName.Users),
                ),
                label = "Sign-in methods",
                variant = QeetrixChoiceVariant.Tile,
                columns = 2,
            )
            SampleNote("A whole tile is the target. Two columns where 192 dp tiles fit, one on a phone.")
        }
        SampleSection("States") {
            SampleFlow {
                QeetrixCheckbox(true, {}, label = "Disabled", enabled = false)
                QeetrixCheckbox(true, null, label = "Read-only")
                QeetrixCheckbox(false, {}, label = "Invalid", invalid = true)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CheckboxSamplePreview() {
    SamplePreview { CheckboxSample() }
}
