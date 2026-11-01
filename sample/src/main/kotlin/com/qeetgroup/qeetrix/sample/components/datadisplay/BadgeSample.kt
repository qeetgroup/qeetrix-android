package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeSize
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** A status word for each tone, as the subtle tags most statuses use. */
private val ToneWords =
    listOf(
        QeetrixBadgeTone.Neutral to "Draft",
        QeetrixBadgeTone.Brand to "New",
        QeetrixBadgeTone.Info to "Scheduled",
        QeetrixBadgeTone.Success to "Paid",
        QeetrixBadgeTone.Warning to "Due soon",
        QeetrixBadgeTone.Danger to "Overdue",
    )

/**
 * Status tags — `@qeetrix/react`'s Badge: every tone, subtle and solid, the two sizes, a leading icon, and in context
 * beside the text they qualify.
 */
@Composable
fun BadgeSample() {
    SamplePage {
        SampleSection("Tones") {
            SampleFlow { for ((tone, word) in ToneWords) QeetrixBadge(word, tone = tone) }
            SampleNote("A tinted pill in label.sm. The word carries the status; the tone only reinforces it.")
        }
        SampleSection("Solid") {
            SampleFlow { for ((tone, word) in ToneWords) QeetrixBadge(word, tone = tone, variant = QeetrixBadgeVariant.Solid) }
            SampleNote("The bold fill is for the one status in a view that must stand out, such as Overdue.")
        }
        SampleSection("Sizes") {
            SampleFlow {
                for (size in QeetrixBadgeSize.entries) QeetrixBadge("Size ${size.value}", tone = QeetrixBadgeTone.Info, size = size)
            }
            SampleNote("md is 20 dp, the default; sm is 16 dp, for tight rows and beside small text.")
        }
        SampleSection("With an icon") {
            SampleFlow {
                QeetrixBadge("Verified", tone = QeetrixBadgeTone.Success, icon = QeetrixIconName.CircleCheck)
                QeetrixBadge("Expiring", tone = QeetrixBadgeTone.Warning, icon = QeetrixIconName.TriangleAlert)
                QeetrixBadge("Failed", tone = QeetrixBadgeTone.Danger, icon = QeetrixIconName.CircleAlert)
                QeetrixBadge("Private", icon = QeetrixIconName.Lock)
            }
            SampleNote("A 12 dp leading icon in the sharp shape. It is not announced: the word says what it means.")
        }
        SampleSection("In context") {
            for ((service, status, tone) in ServiceStatuses) {
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixText(service)
                    QeetrixBadge(status, tone = tone)
                }
            }
            Row(
                Modifier.clearAndSetSemantics { contentDescription = "Approvals, 3 pending" },
                horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QeetrixText("Approvals")
                QeetrixBadge("3", size = QeetrixBadgeSize.Sm)
            }
            SampleNote("A badge is static. A count needs context for TalkBack, so its row reads \"Approvals, 3 pending\".")
        }
    }
}

private val ServiceStatuses =
    listOf(
        Triple("Billing service", "Healthy", QeetrixBadgeTone.Success),
        Triple("Search index", "Degraded", QeetrixBadgeTone.Warning),
    )

@PreviewLightDark
@Composable
private fun BadgeSamplePreview() {
    SamplePreview { BadgeSample() }
}
