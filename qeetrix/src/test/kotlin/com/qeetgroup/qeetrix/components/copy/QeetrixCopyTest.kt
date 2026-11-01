package com.qeetgroup.qeetrix.components.copy

import android.content.ClipDescription
import android.content.ClipboardManager
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Copy: the text on the clipboard as plain text, marked sensitive when asked, "Copied" for 2 s, the web's names. */
@RunWith(RobolectricTestRunner::class)
class QeetrixCopyTest {
    @get:Rule
    val compose = createComposeRule()

    private val clipboard: ClipboardManager
        get() = ApplicationProvider.getApplicationContext<android.content.Context>().getSystemService(ClipboardManager::class.java)

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
}
