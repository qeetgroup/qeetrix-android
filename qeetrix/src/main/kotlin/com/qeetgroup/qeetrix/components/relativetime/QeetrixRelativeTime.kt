package com.qeetgroup.qeetrix.components.relativetime

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.QeetrixTextWeight
import com.qeetgroup.qeetrix.components.typography.textStyle
import com.qeetgroup.qeetrix.components.typography.toneColor
import com.qeetgroup.qeetrix.foundation.format.QeetrixDateStyle
import com.qeetgroup.qeetrix.foundation.format.QeetrixFormat
import com.qeetgroup.qeetrix.foundation.format.QeetrixTimeStyle
import com.qeetgroup.qeetrix.internal.labelDirection
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

/**
 * When something happened, as people say it — `@qeetrix/react`'s RelativeTime (07-components C3): "2 minutes ago",
 * "yesterday", "in 3 days", then, beyond [threshold] (30 days), the date ("25 Oct 2026"). The words are
 * [QeetrixFormat.relativeTime]'s, the web's for the same locale, with tabular figures (relative-time.css).
 *
 * - **Live.** It keeps itself current while its screen is started — every 15 s within a minute of [moment], every
 *   minute within an hour, then every 30 minutes, as the web's does — and stops when the screen is stopped. [live]
 *   `false`, or a fixed [now] (for tests and screenshots), keeps the text as it is.
 * - **Accessibility.** The words are read, then the exact date and time (the web's `title`), so "yesterday" can be
 *   placed.
 * - [size] and [tone] are [QeetrixText]'s, so it sits in a row of text: `QeetrixTextTone.Subtle` beside a name.
 *
 * ```
 * QeetrixRelativeTime(message.sentAt, tone = QeetrixTextTone.Subtle, size = QeetrixTextSize.Sm)
 * ```
 */
@Composable
public fun QeetrixRelativeTime(
    moment: Instant,
    modifier: Modifier = Modifier,
    threshold: Duration = Duration.ofDays(30),
    now: Instant? = null,
    live: Boolean = true,
    size: QeetrixTextSize = QeetrixTextSize.Md,
    tone: QeetrixTextTone? = null,
    zone: ZoneId = ZoneId.systemDefault(),
) {
    val locale = LocalConfiguration.current.locales[0]
    var clock by remember { mutableStateOf(Instant.now()) }
    val current = now ?: clock
    val age = Duration.between(moment, current).abs()
    if (now == null && live) {
        val lifecycle = LocalLifecycleOwner.current.lifecycle
        LaunchedEffect(moment, lifecycle) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    clock = Instant.now()
                    delay(refreshEvery(Duration.between(moment, clock).abs()))
                }
            }
        }
    }
    val text =
        remember(moment, current, threshold, locale, zone) {
            if (age > threshold) {
                QeetrixFormat.date(moment, locale, QeetrixDateStyle.Medium, zone)
            } else {
                QeetrixFormat.relativeTime(moment, locale, current)
            }
        }
    val exact =
        remember(moment, locale, zone) { QeetrixFormat.dateTime(moment, locale, QeetrixDateStyle.Long, QeetrixTimeStyle.Short, zone) }
    // .qx-relative-time: font-variant-numeric tabular-nums, in the surrounding text's role.
    val style = textStyle(size, QeetrixTextWeight.Regular, mono = false)
    BasicText(
        text,
        modifier.semantics { stateDescription = exact },
        style = style.copy(color = toneColor(tone), fontFeatureSettings = "tnum").labelDirection(),
    )
}

/** relative-time.tsx `cadence`: every 15 s within a minute, every minute within an hour, then every 30 minutes. */
internal fun refreshEvery(age: Duration): Long =
    when {
        age < Duration.ofMinutes(1) -> 15_000L
        age < Duration.ofHours(1) -> 60_000L
        else -> 30 * 60_000L
    }
