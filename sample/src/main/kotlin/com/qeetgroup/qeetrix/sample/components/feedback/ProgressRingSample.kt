package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.progress.QeetrixMeterTone
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressRing
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressRingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay

/** The progress ring: a value to step, its sizes beside text, its tones, a countdown, and the empty and full ends. */
@Composable
fun ProgressRingSample() {
    SamplePage {
        SampleSection("A value that moves") {
            var value by rememberSaveable { mutableFloatStateOf(0.4f) }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm)) {
                QeetrixProgressRing(value, size = QeetrixProgressRingSize.Lg, contentDescription = "Sync")
                QeetrixText("Syncing contacts · ${(value * 100).toInt()}%")
            }
            val step = { delta: Float -> value = (value + delta).coerceIn(0f, 1f) }
            SampleFlow {
                QeetrixButton("−10%", onClick = { step(-0.1f) }, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm)
                QeetrixButton("+10%", onClick = { step(0.1f) }, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm)
            }
            SampleNote(
                "Clockwise from 12 o'clock, in right to left too. Each new value is reached over motion.duration.normal, " +
                    "at once with animations off. TalkBack reads \"Sync\" and the percentage.",
            )
        }
        SampleSection("Sizes, beside text") {
            for ((size, text) in listOf(
                QeetrixProgressRingSize.Sm to QeetrixTextSize.Sm,
                QeetrixProgressRingSize.Md to QeetrixTextSize.Md,
            )) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                    QeetrixProgressRing(0.65f, size = size)
                    QeetrixText("${size.value}: 65% of the backup", size = text)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm)) {
                QeetrixProgressRing(0.65f, size = QeetrixProgressRingSize.Lg)
                QeetrixText("lg: 65% of the backup", size = QeetrixTextSize.Lg)
            }
            SampleNote("16, 20 and 24 dp, the icon sizes; a ring with text beside it is decorative, so TalkBack reads the text once.")
        }
        SampleSection("Tones") {
            SampleFlow {
                for (tone in QeetrixMeterTone.entries) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                        QeetrixProgressRing(0.7f, tone = tone)
                        QeetrixText(tone.value, size = QeetrixTextSize.Sm)
                    }
                }
            }
        }
        SampleSection("A countdown") {
            val period = 30_000L
            var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
            // Every frame, or once a second with animations off, as the ring asks.
            val reduce = QeetrixTheme.reduceMotion
            LaunchedEffect(reduce) {
                while (true) {
                    if (reduce) {
                        now = System.currentTimeMillis()
                        delay(1_000 - now % 1_000)
                    } else {
                        withFrameMillis { now = System.currentTimeMillis() }
                    }
                }
            }
            val left = period - now % period
            val seconds = ((left + 999) / 1_000).toInt()
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                QeetrixProgressRing(
                    left.toFloat() / period,
                    tone = if (seconds <= 5) QeetrixMeterTone.Warning else QeetrixMeterTone.Brand,
                    contentDescription = "Time left",
                    stateDescription = "$seconds seconds left",
                )
                QeetrixText("$seconds s")
            }
            SampleNote(
                "Set every frame, so it empties smoothly (once a second with animations off); TalkBack reads \"Time left, 12 seconds left\".",
            )
        }
        SampleSection("Empty and full") {
            SampleFlow {
                QeetrixProgressRing(0f, size = QeetrixProgressRingSize.Lg, contentDescription = "Not started")
                QeetrixProgressRing(0.05f, size = QeetrixProgressRingSize.Lg, contentDescription = "Just started")
                QeetrixProgressRing(1f, size = QeetrixProgressRingSize.Lg, tone = QeetrixMeterTone.Success, contentDescription = "Done")
            }
            SampleNote("An empty ring is only its track: the track has to stay visible on the page, in light and in dark.")
        }
    }
}

@PreviewLightDark
@Composable
private fun ProgressRingSamplePreview() {
    SamplePreview { ProgressRingSample() }
}
