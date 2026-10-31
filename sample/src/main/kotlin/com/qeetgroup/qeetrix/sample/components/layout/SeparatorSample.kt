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
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparatorTone
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Rules — `@qeetrix/react`'s Separator: horizontal in its three tones, and vertical between inline items. */
@Composable
fun SeparatorSample() {
    SamplePage {
        SampleSection("Horizontal") {
            QeetrixText("Account")
            QeetrixSeparator()
            QeetrixText("Notifications")
            QeetrixSeparator(tone = QeetrixSeparatorTone.Default)
            QeetrixText("Privacy")
            QeetrixSeparator(tone = QeetrixSeparatorTone.Strong)
            QeetrixText("Danger zone")
            SampleNote(
                "Subtle (the default), default and strong: 1 dp rules in the border colours. Ruled surfaces separate " +
                    "content by lines and tone, never shadows. TalkBack skips separators.",
            )
        }
        SampleSection("Vertical") {
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
                QeetrixText("Docs", size = QeetrixTextSize.Sm)
                QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
                QeetrixText("Status", size = QeetrixTextSize.Sm)
                QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
                QeetrixText("Support", size = QeetrixTextSize.Sm)
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
