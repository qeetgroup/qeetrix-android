package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The plans a workspace can choose — sample data. */
enum class SamplePlan(
    val title: String,
    val detail: String,
) {
    Starter("Starter", "Up to 5 members"),
    Team("Team", "Up to 50 members, audit logs"),
    Enterprise("Enterprise", "Contact sales"),
}

/** The sample's plans as choices; Enterprise cannot be chosen here. */
val SamplePlanOptions: List<QeetrixOption<SamplePlan>> =
    SamplePlan.entries.map { QeetrixOption(it, it.title, it.detail, enabled = it != SamplePlan.Enterprise) }

/** Single choice: typed options, a disabled option, nothing chosen yet, and the group's error. */
@Composable
fun RadioGroupSample() {
    SamplePage {
        SampleSection("Choose a plan") {
            var plan by rememberSaveable { mutableStateOf(SamplePlan.Team) }
            QeetrixRadioGroup(plan, { plan = it }, SamplePlanOptions, label = "Plan", description = "You can change it at any time.")
            SampleNote("The choice is a SamplePlan, not a string: only an option's value can be selected. Enterprise is disabled.")
        }
        SampleSection("Nothing chosen yet") {
            var plan by rememberSaveable { mutableStateOf<SamplePlan?>(null) }
            QeetrixRadioGroup(
                plan,
                { plan = it },
                SamplePlanOptions.take(2),
                label = "Billing plan",
                required = true,
                message = if (plan == null) QeetrixFieldMessage("Choose a plan.") else null,
            )
            SampleNote("null is a value no option has. Until one is chosen, every option carries the error for TalkBack.")
        }
    }
}

@PreviewLightDark
@Composable
private fun RadioGroupSamplePreview() {
    SamplePreview { RadioGroupSample() }
}

@Preview(name = "Right to left", locale = "ar")
@Composable
private fun RadioGroupRightToLeftPreview() {
    SamplePreview { QeetrixRadioGroup(SamplePlan.Team, {}, SamplePlanOptions, label = "Plan") }
}
