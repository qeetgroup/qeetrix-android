package com.qeetgroup.qeetrix.sample.components.datetime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.relativetime.QeetrixRelativeTime
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import java.time.Duration
import java.time.Instant

/** Relative times from seconds ago to months ago, in the future, and past the threshold as a date. */
@Composable
fun RelativeTimeSample() {
    val now = remember { Instant.now() }
    SamplePage {
        SampleSection("Live") {
            for ((what, ago) in listOf(
                "Signed in" to Duration.ofSeconds(20),
                "Approved" to Duration.ofMinutes(5),
                "Key rotated" to Duration.ofHours(3),
                "Phone linked" to Duration.ofDays(1),
                "Invoice sent" to Duration.ofDays(4),
                "Account created" to Duration.ofDays(75),
            )) {
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixText(what)
                    QeetrixRelativeTime(now.minus(ago), tone = QeetrixTextTone.Subtle)
                }
            }
            SampleNote(
                "Updated as time passes (\"20 seconds ago\" becomes \"1 minute ago\"); past 30 days it is the date. TalkBack " +
                    "also reads the exact date and time.",
            )
        }
        SampleSection("In the future") {
            QeetrixRelativeTime(now.plus(Duration.ofHours(2)))
            QeetrixRelativeTime(now.plus(Duration.ofDays(3)), size = QeetrixTextSize.Sm)
        }
    }
}

@PreviewLightDark
@Composable
private fun RelativeTimeSamplePreview() {
    SamplePreview { RelativeTimeSample() }
}
