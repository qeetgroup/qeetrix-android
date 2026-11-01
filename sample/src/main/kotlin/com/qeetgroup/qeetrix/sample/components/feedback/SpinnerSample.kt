package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerSize
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerTone
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** An indeterminate wait — `@qeetrix/react`'s Spinner: its sizes, its tones, a specific label, and beside text. */
@Composable
fun SpinnerSample() {
    SamplePage {
        SampleSection("Sizes") {
            SampleFlow { for (size in QeetrixSpinnerSize.entries) QeetrixSpinner(size = size, label = "Loading (${size.value})") }
            SampleNote("14, 16, 20 and 24 dp: a 2 dp ring with a quarter clear. Each is announced as \"Loading\" by default.")
        }
        SampleSection("Tones") {
            SampleFlow {
                QeetrixSpinner(size = QeetrixSpinnerSize.Lg)
                QeetrixSpinner(size = QeetrixSpinnerSize.Lg, tone = QeetrixSpinnerTone.Brand)
            }
            SampleNote("Neutral inherits the content colour; brand is Ember, for the one wait that is the screen's focus.")
        }
        SampleSection("With a label") {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), verticalAlignment = Alignment.CenterVertically) {
                QeetrixSpinner(decorative = true)
                QeetrixText("Loading invoices…", tone = QeetrixTextTone.Subtle)
            }
            SampleNote(
                "Say what is loading. When the text beside it already says so, the spinner is decorative and TalkBack " +
                    "skips it. Use it for waits of unknown length; when the amount is known, use Progress. With " +
                    "animations off it holds still and fades instead.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SpinnerSamplePreview() {
    SamplePreview { SpinnerSample() }
}
