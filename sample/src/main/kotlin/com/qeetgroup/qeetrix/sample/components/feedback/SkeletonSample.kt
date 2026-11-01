package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonSize
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Placeholders in the shape of what will arrive: avatars, a list row, a card and paragraphs. */
@Composable
fun SkeletonSample() {
    SamplePage {
        SampleSection("Avatars") {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd), verticalAlignment = Alignment.CenterVertically) {
                for (size in QeetrixSkeletonSize.entries) QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle, size = size)
            }
            SampleNote("They pulse every 1.6 s and stand still with animations off. TalkBack doesn't see skeletons.")
        }
        SampleSection("A list row") {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd), verticalAlignment = Alignment.CenterVertically) {
                QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                    QeetrixSkeleton(Modifier.width(160.dp))
                    QeetrixSkeleton(Modifier.width(220.dp))
                }
            }
        }
        SampleSection("A card") {
            QeetrixSkeleton(variant = QeetrixSkeletonVariant.Block)
        }
        SampleSection("Paragraphs") {
            QeetrixSkeletonText(lines = 1, role = QeetrixTextRole.TitleMd)
            QeetrixSkeletonText(lines = 3)
            SampleNote("Each takes the height of the text that replaces it, so nothing below moves when it arrives.")
        }
    }
}

@PreviewLightDark
@Composable
private fun SkeletonSamplePreview() {
    SamplePreview { SkeletonSample() }
}
