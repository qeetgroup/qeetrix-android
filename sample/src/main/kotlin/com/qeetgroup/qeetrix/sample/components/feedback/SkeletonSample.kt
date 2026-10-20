package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataLoadingRows
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonShape
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface

/** Placeholders in the shape of what will arrive: shapes, text lines, a list row and the data state's rows. */
@Composable
fun SkeletonSample() {
    SamplePage {
        SampleSection("Shapes") {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.M), verticalAlignment = Alignment.CenterVertically) {
                QeetrixSkeleton(Modifier.size(40.dp), QeetrixSkeletonShape.Circle)
                QeetrixSkeleton(Modifier.size(width = 72.dp, height = 24.dp), QeetrixSkeletonShape.Capsule)
                QeetrixSkeleton(Modifier.size(width = 96.dp, height = 32.dp))
            }
            SampleNote("The highlight sweeps every 1.6 s and stands still with animations off. TalkBack doesn't see skeletons.")
        }
        SampleSection("A paragraph") {
            QeetrixSkeletonText(lines = 3, role = QeetrixTextRole.Body)
        }
        SampleSection("A list row") {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.M), verticalAlignment = Alignment.CenterVertically) {
                QeetrixSkeleton(Modifier.size(32.dp), QeetrixSkeletonShape.Circle)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.Xs)) {
                    QeetrixSkeleton(Modifier.width(160.dp).height(14.dp))
                    QeetrixSkeleton(Modifier.width(220.dp).height(14.dp))
                }
            }
        }
        SampleSection("Data state rows") {
            SampleSurface { QeetrixDataLoadingRows(rows = 3) }
        }
    }
}

@PreviewLightDark
@Composable
private fun SkeletonSamplePreview() {
    SamplePreview { SkeletonSample() }
}
