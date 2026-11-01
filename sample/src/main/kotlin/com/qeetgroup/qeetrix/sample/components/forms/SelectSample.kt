package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixOptionGroup
import com.qeetgroup.qeetrix.components.select.QeetrixMultiSelect
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.components.select.QeetrixSelectSize
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Choosing from a short list: a typed choice, groups with counts, several choices, an app-owned open state, sizes, states. */
@Composable
fun SelectSample() {
    SamplePage {
        SampleSection("Billing cycle") {
            var cycle by rememberSaveable { mutableStateOf<String?>(null) }
            QeetrixSelect(
                cycle,
                { cycle = it },
                listOf(
                    QeetrixOption("monthly", "Monthly"),
                    QeetrixOption("yearly", "Yearly"),
                    QeetrixOption("custom", "Custom", enabled = false),
                ),
                label = "Billing cycle",
                placeholder = "e.g. Monthly",
                error = if (cycle == null) "Choose a billing cycle." else null,
            )
            SampleNote("Chosen: ${cycle ?: "nothing yet"}. The list opens below, closes on a choice, Back or a tap outside.")
        }
        SampleSection("Regions, in groups") {
            var region by rememberSaveable { mutableStateOf("blr") }
            QeetrixSelect(
                region,
                { region = it },
                listOf(
                    QeetrixOptionGroup(
                        "India",
                        listOf(QeetrixOption("blr", "Bengaluru", meta = "412"), QeetrixOption("bom", "Mumbai", meta = "38")),
                    ),
                    QeetrixOptionGroup("Singapore", listOf(QeetrixOption("sin", "Singapore", meta = "7"))),
                ),
                label = "Data region",
                description = "Where your data is stored.",
            )
            SampleNote("The count after each option is read with it but not shown in the closed field.")
        }
        SampleSection("Several choices") {
            var regions by rememberSaveable { mutableStateOf(setOf("blr")) }
            QeetrixMultiSelect(
                regions,
                { regions = it },
                listOf(
                    QeetrixOption("blr", "Bengaluru"),
                    QeetrixOption("bom", "Mumbai"),
                    QeetrixOption("sin", "Singapore"),
                    QeetrixOption("fra", "Frankfurt"),
                ),
                label = "Backup regions",
                placeholder = "e.g. Mumbai",
            )
            SampleNote(
                "${regions.size} chosen. Each option is a check box; the list stays open while you toggle, and the field lists " +
                    "the choices in the list's order.",
            )
        }
        SampleSection("Opened by the app, and type to jump") {
            var open by rememberSaveable { mutableStateOf(false) }
            var plan by rememberSaveable { mutableStateOf("team") }
            QeetrixSelect(
                plan,
                { plan = it },
                listOf(QeetrixOption("starter", "Starter"), QeetrixOption("team", "Team"), QeetrixOption("scale", "Scale")),
                label = "Plan",
                open = open,
                onOpenChange = { open = it },
            )
            QeetrixButton(if (open) "Close the list" else "Open the list", onClick = { open = !open }, variant = QeetrixButtonVariant.Soft)
            SampleNote(
                "The app owns open. With a keyboard, type on the closed field to choose (S, then S again for the next), or in " +
                    "the open list to move to an option.",
            )
        }
        SampleSection("Toolbar sizes") {
            var style by rememberSaveable { mutableStateOf("body") }
            val styles = listOf(QeetrixOption("body", "Body"), QeetrixOption("heading", "Heading"), QeetrixOption("quote", "Quote"))
            SampleFlow {
                QeetrixSelect(style, { style = it }, styles, label = "Paragraph style", size = QeetrixSelectSize.Md, hideLabel = true)
                QeetrixSelect(
                    style,
                    { style = it },
                    styles,
                    label = "Paragraph style, small",
                    size = QeetrixSelectSize.Sm,
                    hideLabel = true,
                )
            }
        }
        SampleSection("Read-only and disabled") {
            val plans = listOf(QeetrixOption("team", "Team"), QeetrixOption("business", "Business"))
            QeetrixSelect("team", {}, plans, label = "Plan", readOnly = true)
            QeetrixSelect("business", {}, plans, label = "Plan", enabled = false, description = "Your admin sets this.")
        }
    }
}

@PreviewLightDark
@Composable
private fun SelectSamplePreview() {
    SamplePreview { SelectSample() }
}
