package com.qeetgroup.qeetrix.components.otpinput

import android.content.ClipDescription
import android.content.ClipboardManager
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Duration
import java.time.Instant

/** The one-time code: grouped, read digit by digit with the time left, copied as a sensitive entry, hidden until tapped. */
@RunWith(RobolectricTestRunner::class)
class QeetrixOTPCodeTest {
    @get:Rule
    val compose = createComposeRule()

    private val now = Instant.parse("2026-10-30T12:00:18Z")
    private val expires = Instant.parse("2026-10-30T12:00:30Z")

    @Test
    fun theCodeIsGroupedAndReadDigitByDigitWithTheTimeLeft() {
        compose.setQeetrix { QeetrixOTPCode("482105", expires, label = "Acme", now = now) }
        compose.onNodeWithText("482", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("105", useUnmergedTree = true).assertExists()
        compose.onNodeWithContentDescription("Acme, 4 8 2 1 0 5, 12 seconds left").assertExists()
        assertEquals(listOf("1234", "5678"), groupsOf("12345678"))
        assertEquals(listOf("12345"), groupsOf("12345"))
    }

    @Test
    fun aTapCopiesTheCodeAsASensitiveEntry() {
        compose.setQeetrix { QeetrixOTPCode("482105", expires, label = "Acme", now = now) }
        compose.onNodeWithContentDescription("Acme, 4 8 2 1 0 5, 12 seconds left").performClick()
        compose.waitForIdle()
        val clip =
            ApplicationProvider
                .getApplicationContext<android.content.Context>()
                .getSystemService(
                    ClipboardManager::class.java,
                ).primaryClip!!
        assertEquals("482105", clip.getItemAt(0).text.toString())
        assertTrue(clip.description.extras!!.getBoolean(ClipDescription.EXTRA_IS_SENSITIVE))
    }

    @Test
    fun aHiddenCodeShowsOnTheFirstTap() {
        compose.setQeetrix { QeetrixOTPCode("482105", expires, label = "Acme", hidden = true, now = now) }
        compose.onNodeWithContentDescription("Acme, Code hidden, 12 seconds left").performClick()
        compose.onNodeWithText("482", useUnmergedTree = true).assertExists()
    }

    @Test
    fun theLastSecondsAreCountedOneByOne() {
        compose.setQeetrix { QeetrixOTPCode("482105", expires, label = "Acme", now = expires.minusSeconds(1)) }
        compose.onNodeWithContentDescription("Acme, 4 8 2 1 0 5, 1 second left").assertExists()
    }

    @Test
    fun aPeriodMustBePositive() {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix { QeetrixOTPCode("1", expires, period = Duration.ZERO) }
        }
    }
}
