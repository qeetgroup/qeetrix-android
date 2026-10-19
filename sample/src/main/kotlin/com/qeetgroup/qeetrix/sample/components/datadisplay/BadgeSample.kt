package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Badges: every variant, with an icon, and in context beside the text they qualify. */
@Composable
fun BadgeSample() {
    SamplePage {
        SampleSection("Variants") {
            SampleFlow { for (variant in QeetrixBadgeVariant.entries) QeetrixBadge(variant.value, variant = variant) }
            SampleNote("One solid fill for emphasis; quiet tints for the brand and the status tones; neutral for the rest.")
        }
        SampleSection("With an icon") {
            SampleFlow {
                QeetrixBadge("Verified", variant = QeetrixBadgeVariant.Success, icon = QeetrixIconName.CircleCheck)
                QeetrixBadge("Expiring", variant = QeetrixBadgeVariant.Warning, icon = QeetrixIconName.TriangleAlert)
                QeetrixBadge("Failed", variant = QeetrixBadgeVariant.Destructive, icon = QeetrixIconName.CircleAlert)
                QeetrixBadge("Private", variant = QeetrixBadgeVariant.Outline, icon = QeetrixIconName.Lock)
            }
            SampleNote("The word carries the status; colour and icon reinforce it. The icon is not announced.")
        }
        SampleSection("In context") {
            for ((service, status, variant) in ServiceStatuses) {
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.S), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixText(service)
                    QeetrixBadge(status, variant = variant)
                }
            }
        }
    }
}

private val ServiceStatuses =
    listOf(
        Triple("Billing service", "Healthy", QeetrixBadgeVariant.Success),
        Triple("Search index", "Degraded", QeetrixBadgeVariant.Warning),
    )

@PreviewLightDark
@Composable
private fun BadgeSamplePreview() {
    SamplePreview { BadgeSample() }
}
