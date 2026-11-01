package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionListLayout
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Facts about one record: rows, an empty value, a settings row with an action, a list in a card, and columns. */
@Composable
fun DescriptionListSample() {
    SamplePage {
        SampleSection("Rows") {
            QeetrixDescriptionList {
                QeetrixDescriptionItem("Email", "priya.sharma@acme.in")
                QeetrixDescriptionItem("Status") { QeetrixBadge("Active", tone = QeetrixBadgeTone.Success) }
                QeetrixDescriptionItem("Billing contact", "Finance team, Qeet Example Private Limited, 4th floor, Indiranagar, Bengaluru")
                QeetrixDescriptionItem("Phone", null)
            }
            SampleNote(
                "Each term beside its value from 24 rem (384 dp) of list width, a rule between rows; on a phone the term " +
                    "moves above the value. Each pair is read as one element; an empty value shows a dash, read as Not set.",
            )
        }
        SampleSection("A settings row") {
            QeetrixDescriptionList {
                QeetrixDescriptionItem(
                    "Display name",
                    "Acme",
                    description = "Shown in the top bar and in invitations.",
                    action = { QeetrixButton("Change", onClick = {}, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm) },
                )
            }
        }
        SampleSection("In a card") {
            QeetrixCard(title = "Payment") {
                QeetrixDescriptionList {
                    QeetrixDescriptionItem("Amount", "₹48,000.00")
                    QeetrixDescriptionItem("Method", "UPI")
                    QeetrixDescriptionItem("Reference", "pay_2K9xQ")
                }
            }
            SampleNote("Placed directly in a card's content, a rows list runs edge to edge, its rows keeping the card's padding.")
        }
        SampleSection("Columns") {
            QeetrixDescriptionList(layout = QeetrixDescriptionListLayout.Columns, columns = 3) {
                QeetrixDescriptionItem("Plan", "Team")
                QeetrixDescriptionItem("Region", "Mumbai")
                QeetrixDescriptionItem("Created", "12 March 2026")
                QeetrixDescriptionItem("Owner", "Ada Lovelace")
            }
            SampleNote("Up to three columns of at least 8 rem, fewer where there isn't room.")
        }
    }
}

@PreviewLightDark
@Composable
private fun DescriptionListSamplePreview() {
    SamplePreview { DescriptionListSample() }
}
