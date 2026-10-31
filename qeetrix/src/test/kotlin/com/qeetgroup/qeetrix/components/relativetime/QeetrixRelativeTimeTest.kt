package com.qeetgroup.qeetrix.components.relativetime

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

/** RelativeTime: the web's words, the date past the threshold, the exact moment for accessibility, the web's cadence. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "en-rIN")
class QeetrixRelativeTimeTest {
    @get:Rule
    val compose = createComposeRule()

    private val now = Instant.parse("2026-10-30T12:00:00Z")
    private val kolkata = ZoneId.of("Asia/Kolkata")

    @Test
    fun itSaysHowLongAgoThenTheDatePastTheThreshold() {
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixRelativeTime(now.minusSeconds(120), now = now, zone = kolkata)
                QeetrixRelativeTime(now.minus(Duration.ofDays(1)), now = now, zone = kolkata)
                QeetrixRelativeTime(now.minus(Duration.ofDays(45)), now = now, zone = kolkata)
                QeetrixRelativeTime(now.minus(Duration.ofDays(45)), now = now, zone = kolkata, threshold = Duration.ofDays(365))
            }
        }
        compose.onNodeWithText("2 minutes ago").assertExists()
        compose.onNodeWithText("yesterday").assertExists()
        compose.onNodeWithText("15 Sept 2026").assertExists()
        compose.onNodeWithText("2 months ago").assertExists()
    }

    @Test
    fun theExactMomentIsReadAfterTheWords() {
        compose.setQeetrix { QeetrixRelativeTime(now.minusSeconds(120), now = now, zone = kolkata) }
        compose
            .onNodeWithText("2 minutes ago")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "30 October 2026 at 5:28 pm"))
    }

    @Test
    fun itRefreshesAsTheWebsDoes() {
        assertEquals(15_000L, refreshEvery(Duration.ofSeconds(30)))
        assertEquals(60_000L, refreshEvery(Duration.ofMinutes(30)))
        assertEquals(1_800_000L, refreshEvery(Duration.ofHours(5)))
    }
}
