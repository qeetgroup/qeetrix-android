package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.components.select.QeetrixSelectSize
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private val Regions =
    listOf(
        QeetrixOption("in", "India (Mumbai)"),
        QeetrixOption("ae", "United Arab Emirates (Dubai)"),
        QeetrixOption("eu", "European Union (Frankfurt)", "Data stays inside the EU."),
        QeetrixOption("us", "United States (Virginia)", enabled = false),
    )

/** Choosing from a short list: a placeholder until chosen, descriptions, a disabled option, sizes and states. */
@Composable
fun SelectSample() {
    SamplePage {
        SampleSection("Data region") {
            var region by rememberSaveable { mutableStateOf<String?>(null) }
            QeetrixSelect(
                region,
                { region = it },
                Regions,
                label = "Data region",
                placeholder = "Choose a region",
                description = "Where your workspace's data is stored.",
                required = true,
                message = if (region == null) QeetrixFieldMessage("Choose a region.") else null,
            )
            SampleNote(
                "The list opens below the field, or above when there is more room there, and closes on a choice, a tap " +
                    "outside or Back. From a keyboard, Enter opens it on the selected option.",
            )
        }
        SampleSection("Sizes and states") {
            var plan by rememberSaveable { mutableStateOf(SamplePlan.Team) }
            QeetrixSelect(plan, { plan = it }, SamplePlanOptions, label = "Plan (small)", size = QeetrixSelectSize.Sm)
            QeetrixSelect("in", {}, Regions, label = "Read-only", readOnly = true)
            QeetrixSelect("in", {}, Regions, label = "Disabled", enabled = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun SelectSamplePreview() {
    SamplePreview { SelectSample() }
}
