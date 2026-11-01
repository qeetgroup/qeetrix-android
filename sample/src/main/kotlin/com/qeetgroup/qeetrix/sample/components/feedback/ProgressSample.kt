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
import com.qeetgroup.qeetrix.components.progress.QeetrixMeter
import com.qeetgroup.qeetrix.components.progress.QeetrixMeterTone
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressTone
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import java.text.NumberFormat

/** Progress and Meter: a value the user can move, how a task ended, unknown progress, and measurements with a target. */
@Composable
fun ProgressSample() {
    SamplePage {
        SampleSection("A task advancing") {
            var value by rememberSaveable { mutableFloatStateOf(40f) }
            QeetrixProgress(
                value,
                label = "Uploading report.pdf",
                showValue = true,
                tone = if (value >= 100f) QeetrixProgressTone.Success else QeetrixProgressTone.Brand,
            )
            val step = { delta: Float -> value = (value + delta).coerceIn(0f, 100f) }
            SampleFlow {
                QeetrixButton("−10%", onClick = { step(-10f) }, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm)
                QeetrixButton("+10%", onClick = { step(10f) }, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm)
            }
            SampleNote("TalkBack reads the label and the percentage; the bar moves to each new value, and turns green when it is done.")
        }
        SampleSection("How it ended") {
            QeetrixProgress(100f, label = "Imported 1,204 rows", tone = QeetrixProgressTone.Success)
            QeetrixProgress(64f, label = "Import failed at row 770", tone = QeetrixProgressTone.Danger)
        }
        SampleSection("Unknown progress") {
            QeetrixProgress(null, label = "Preparing export")
            SampleNote("A segment slides along the track; with animations turned off, the whole bar fades instead.")
        }
        SampleSection("Measurements") {
            val gigabytes = NumberFormat.getNumberInstance().apply { maximumFractionDigits = 1 }
            QeetrixMeter(6.2f, max = 8f, label = "Storage used (GB)", showValue = true, tone = QeetrixMeterTone.Warning, format = gigabytes)
            QeetrixMeter(40f, label = "People with two-step", showValue = true, target = 80f)
            SampleNote("A meter is a measurement, not a task: TalkBack reads its label and value, and its target when it has one.")
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
    SamplePreview { QeetrixProgress(42f, label = "Uploading report.pdf", showValue = true) }
}
