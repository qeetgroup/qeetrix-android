package com.qeetgroup.qeetrix.components.relativetime

import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

/** Baselines for [QeetrixRelativeTime] in `src/test/screenshots/relativetime/`, at a fixed now. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixRelativeTimeScreenshotTest {
    private val now = Instant.parse("2026-10-30T12:00:00Z")
    private val zone = ZoneId.of("Asia/Kolkata")

    @Test
    fun states() =
        captureStates("relativetime") {
            ScreenshotStack {
                QeetrixRelativeTime(now, now = now, zone = zone)
                QeetrixRelativeTime(now.minusSeconds(45), now = now, zone = zone)
                QeetrixRelativeTime(now.minusSeconds(120), now = now, zone = zone, size = QeetrixTextSize.Lg)
                QeetrixRelativeTime(now.minus(Duration.ofDays(1)), now = now, zone = zone, tone = QeetrixTextTone.Subtle)
                QeetrixRelativeTime(now.plus(Duration.ofDays(3)), now = now, zone = zone, size = QeetrixTextSize.Sm)
                QeetrixRelativeTime(now.minus(Duration.ofDays(45)), now = now, zone = zone)
            }
        }
}
