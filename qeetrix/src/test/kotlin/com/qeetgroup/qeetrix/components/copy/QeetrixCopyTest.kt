package com.qeetgroup.qeetrix.components.copy

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Looper
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowToast
import java.time.Duration

/** Copy: the text on the clipboard as plain text, marked sensitive when asked, "Copied" for 2 s, the web's names. */
@RunWith(RobolectricTestRunner::class)
class QeetrixCopyTest {
    @get:Rule
    val compose = createComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private val clipboard: ClipboardManager
        get() = context.getSystemService(ClipboardManager::class.java)

    private fun clipText(): String? =
        clipboard.primaryClip
            ?.getItemAt(0)
            ?.text
            ?.toString()

    @After
    fun noClearOutlivesItsTest() = ClipboardClear.cancel()

    @Test
    fun copyingPutsTheValueOnTheClipboardAndSaysCopiedFor2Seconds() {
        var copied: String? = null
        compose.mainClock.autoAdvance = false
        compose.setQeetrix { QeetrixCopyButton({ "cl_8f2a" }, label = "Copy client ID", onCopied = { copied = it }) }
        compose.onNodeWithContentDescription("Copy client ID").performClick()
        compose.mainClock.advanceTimeByFrame()
        assertEquals(
            "cl_8f2a",
            clipboard.primaryClip!!
                .getItemAt(0)
                .text
                .toString(),
        )
        assertEquals(
            "Copy client ID",
            clipboard.primaryClip!!
                .description.label
                .toString(),
        )
        assertFalse(
            clipboard.primaryClip!!
                .description.extras
                ?.getBoolean(ClipDescription.EXTRA_IS_SENSITIVE) == true,
        )
        assertEquals("cl_8f2a", copied)
        val name = SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Copy client ID"))
        compose.onNode(name).assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Copied"))
        compose.mainClock.advanceTimeBy(COPY_RESET_MILLIS + 100)
        compose.onNode(name).assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.StateDescription))
    }

    @Test
    fun aSensitiveCopyIsMarkedSensitive() {
        compose.setQeetrix { QeetrixCopyButton({ "whsec_2b91" }, label = "Copy secret", sensitive = true) }
        compose.onNodeWithContentDescription("Copy secret").performClick()
        compose.waitForIdle()
        assertTrue(
            clipboard.primaryClip!!
                .description.extras!!
                .getBoolean(ClipDescription.EXTRA_IS_SENSITIVE),
        )
    }

    @Test
    fun aButtonWithTextIsNamedByItAndTheFieldsButtonByItsLabel() {
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixCopyButton({ "x" }, text = "Copy ID")
                QeetrixCopyField("ACS URL", "https://id.qeet.in/saml/acs")
                QeetrixCopyButton({ "y" })
            }
        }
        compose.onNodeWithText("Copy ID").assertExists()
        compose.onNodeWithContentDescription("Copy ACS URL").performClick()
        compose.waitForIdle()
        assertEquals(
            "https://id.qeet.in/saml/acs",
            clipboard.primaryClip!!
                .getItemAt(0)
                .text
                .toString(),
        )
        // Without a label: "Copy", copyButton.copy.
        compose.onNodeWithContentDescription("Copy").assertExists()
        assertEquals(listOf("field", "row"), QeetrixCopyFieldLayout.entries.map { it.value })
        assertEquals(listOf("idle", "copied", "failed"), QeetrixCopyState.entries.map { it.value })
    }

    /** Below Android 13 the toast says "Copied" unless the app confirms the copy itself; from 13 the system does. */
    @Test
    fun theToastBelowAndroid13CanBeLeftToTheApp() {
        assertTrue(copyToClipboard(context, "482105", "Copy code", sensitive = true, copied = "Copied", confirm = false, sdk = 32))
        assertEquals(0, ShadowToast.shownToastCount())
        copyToClipboard(context, "482105", "Copy code", sensitive = true, copied = "Copied", confirm = true, sdk = 32)
        assertEquals(1, ShadowToast.shownToastCount())
        assertEquals("Copied", ShadowToast.getTextOfLatestToast())
        copyToClipboard(context, "482105", "Copy code", sensitive = true, copied = "Copied", confirm = true, sdk = 33)
        assertEquals(1, ShadowToast.shownToastCount())
    }

    /** A timed copy leaves the clipboard when its delay has passed. */
    @Test
    fun aTimedCopyIsClearedAfterItsDelay() {
        compose.setQeetrix {
            QeetrixCopyButton({ "482105" }, label = "Copy code", sensitive = true, confirm = false, clearAfter = Duration.ofSeconds(30))
        }
        compose.onNodeWithContentDescription("Copy code").performClick()
        compose.waitForIdle()
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(29))
        assertEquals("482105", clipText())
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1))
        assertNull(clipText())
    }

    /** What was copied since — here or in another app — is not the code, so it stays. */
    @Test
    fun aNewerCopyIsLeftAlone() {
        compose.setQeetrix { QeetrixCopyField("Recovery code", "WM8K-WX2P", sensitive = true, clearAfter = Duration.ofSeconds(30)) }
        compose.onNodeWithContentDescription("Copy Recovery code").performClick()
        compose.waitForIdle()
        clipboard.setPrimaryClip(ClipData.newPlainText("Note", "Meeting at 4"))
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(30))
        assertEquals("Meeting at 4", clipText())
    }

    /**
     * Without focus Android hides the clipboard, so a deadline that passes in the background waits for one of
     * the app's windows to have focus, then clears the code if it is still there.
     */
    @Test
    fun aDeadlineInTheBackgroundWaitsForTheAppsWindow() {
        clipboard.clearPrimaryClip() // What the app reads without focus: nothing.
        ClipboardClear.schedule(context, "482105", Duration.ofSeconds(30))
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(30))
        clipboard.setPrimaryClip(ClipData.newPlainText("Copy code", "482105")) // Readable again once in front.
        assertEquals("482105", clipText())
        Robolectric.buildActivity(Activity::class.java).setup().windowFocusChanged(true)
        shadowOf(Looper.getMainLooper()).idle()
        assertNull(clipText())
    }
}
