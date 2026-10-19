package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The busy indicator: its sizes, a specific label, and inline beside text. */
@Composable
fun SpinnerSample() {
    SamplePage {
        SampleSection("Sizes") {
            SampleFlow { for (size in QeetrixSpinnerSize.entries) QeetrixSpinner(size = size) }
            SampleNote("Each is announced as \"Loading\" by default.")
        }
        SampleSection("With a label") {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.S), verticalAlignment = Alignment.CenterVertically) {
                QeetrixSpinner(size = QeetrixSpinnerSize.Sm, label = "Loading invoices")
                QeetrixText("Loading invoices…", variant = QeetrixTextVariant.Muted)
            }
            SampleNote(
                "Say what is loading. Use it for waits of unknown length; when the amount is known, use Progress. " +
                    "With animations off it holds still.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SpinnerSamplePreview() {
    SamplePreview { SpinnerSample() }
}
