package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionListLayout
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Labelled details in the three layouts, divided, with a badge as a value and a long value that wraps. */
@Composable
fun DescriptionListSample() {
    SamplePage {
        SampleSection("Horizontal") {
            QeetrixDescriptionList {
                PlanDetails()
            }
            SampleNote("Terms beside values from 640 dp of window width; stacked below it. Each pair is read as one element.")
        }
        SampleSection("Divided") {
            QeetrixDescriptionList(divided = true) {
                PlanDetails()
            }
        }
        SampleSection("Vertical") {
            QeetrixDescriptionList(layout = QeetrixDescriptionListLayout.Vertical) {
                QeetrixDescriptionItem("Device", "Pixel 9 · Android 16")
                QeetrixDescriptionItem("Last sign-in", "Today at 09:42 from Bengaluru")
            }
        }
        SampleSection("Grid") {
            QeetrixDescriptionList(layout = QeetrixDescriptionListLayout.Grid) {
                QeetrixDescriptionItem("Plan", "Team")
                QeetrixDescriptionItem("Region", "Mumbai")
                QeetrixDescriptionItem("Created", "12 March 2026")
                QeetrixDescriptionItem("Owner", "Ada Lovelace")
            }
            SampleNote("As many 192 dp columns as fit, sharing the width.")
        }
    }
}

@Composable
private fun PlanDetails() {
    QeetrixDescriptionItem("Plan", "Team")
    QeetrixDescriptionItem("Region", "Mumbai (ap-south-1)")
    QeetrixDescriptionItem("Status") { QeetrixBadge("Active", variant = QeetrixBadgeVariant.Success) }
    QeetrixDescriptionItem("Billing contact", "Finance team, Qeet Example Private Limited, 4th floor, Indiranagar, Bengaluru")
}

@PreviewLightDark
@Composable
private fun DescriptionListSamplePreview() {
    SamplePreview { DescriptionListSample() }
}
