package com.qeetgroup.qeetrix.sample.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparatorOrientation
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparatorVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Separators: horizontal in both variants, and vertical between inline items. */
@Composable
fun SeparatorSample() {
    SamplePage {
        SampleSection("Horizontal") {
            QeetrixText("Account")
            QeetrixSeparator()
            QeetrixText("Notifications")
            QeetrixSeparator(variant = QeetrixSeparatorVariant.Muted)
            QeetrixText("Privacy")
            SampleNote("Default, then muted for quieter divisions. TalkBack skips separators: they are decoration.")
        }
        SampleSection("Vertical") {
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.M)) {
                QeetrixText("Docs", variant = QeetrixTextVariant.Small)
                QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
                QeetrixText("Status", variant = QeetrixTextVariant.Small)
                QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
                QeetrixText("Support", variant = QeetrixTextVariant.Small)
            }
            SampleNote("A vertical separator takes its parent's height; give the row an intrinsic height.")
        }
    }
}

@PreviewLightDark
@Composable
private fun SeparatorSamplePreview() {
    SamplePreview { SeparatorSample() }
}
