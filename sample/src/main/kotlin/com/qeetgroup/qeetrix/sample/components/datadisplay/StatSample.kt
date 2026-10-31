package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.stat.QeetrixStat
import com.qeetgroup.qeetrix.components.stat.QeetrixStatGroup
import com.qeetgroup.qeetrix.components.stat.QeetrixStatTone
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Stats: a group of figures with notes in their tones, a currency, a text value, and loading. */
@Composable
fun StatSample() {
    SamplePage {
        SampleSection("A dashboard") {
            QeetrixStatGroup {
                QeetrixStat(
                    "Collected this month",
                    value = 1284500,
                    currency = "INR",
                    note = "+12% on September",
                    tone = QeetrixStatTone.Positive,
                )
                QeetrixStat("Failed payments", value = 37, note = "+9 on September", tone = QeetrixStatTone.Negative)
                QeetrixStat("Active members", value = 12480)
                QeetrixStat("Payout status", text = "Delayed", note = "Bank holiday", tone = QeetrixStatTone.Warning)
            }
            SampleNote(
                "Numbers in the device's locale (₹12,84,500 in India). Say a change in words, as these notes do; the tone only colours it.",
            )
        }
        SampleSection("Loading") {
            QeetrixStatGroup {
                QeetrixStat("Collected this month", loading = true)
                QeetrixStat("Failed payments", loading = true)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun StatSamplePreview() {
    SamplePreview { StatSample() }
}
