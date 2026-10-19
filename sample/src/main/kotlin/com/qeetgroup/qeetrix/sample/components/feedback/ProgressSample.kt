package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressSize
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Progress bars: a value the user can move, the sizes, unlabelled use and the indeterminate form. */
@Composable
fun ProgressSample() {
    SamplePage {
        SampleSection("Determinate") {
            var value by rememberSaveable { mutableFloatStateOf(0.4f) }
            QeetrixProgress(value, label = "Uploading report.pdf")
            val step = { delta: Float -> value = (value + delta).coerceIn(0f, 1f) }
            SampleFlow {
                QeetrixButton("−10%", onClick = { step(-0.1f) }, variant = QeetrixButtonVariant.Outline, size = QeetrixButtonSize.Sm)
                QeetrixButton("+10%", onClick = { step(0.1f) }, variant = QeetrixButtonVariant.Outline, size = QeetrixButtonSize.Sm)
            }
            SampleNote("TalkBack reads the label and the percentage; the bar animates to each new value.")
        }
        SampleSection("Sizes") {
            for (size in QeetrixProgressSize.entries) QeetrixProgress(0.6f, size = size, label = "Size ${size.value}")
        }
        SampleSection("Without a visible label") {
            QeetrixProgress(0.75f, size = QeetrixProgressSize.Sm, showValue = false, contentDescription = "Storage used")
            SampleNote("Give it a content description instead, so it still has a name.")
        }
        SampleSection("Indeterminate") {
            QeetrixProgress(null, label = "Preparing export")
            SampleNote("With animations turned off in the system settings, a still hatched bar replaces the moving sweep.")
        }
    }
}

@PreviewLightDark
@Composable
private fun ProgressSamplePreview() {
    SamplePreview { ProgressSample() }
}

@Preview(name = "Right to left", locale = "ar")
@Composable
private fun ProgressRightToLeftPreview() {
    SamplePreview { QeetrixProgress(0.42f, label = "Uploading report.pdf") }
}
