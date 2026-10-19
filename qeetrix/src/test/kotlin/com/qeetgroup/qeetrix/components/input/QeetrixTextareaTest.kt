package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class QeetrixTextareaTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun itTakesSeveralLinesInTheCallersState() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixTextarea(state, label = "Notes", placeholder = "Anything we should know?") }
        compose.onNodeWithText("Anything we should know?", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Notes").assert(hasSetTextAction()).performTextInput("First line\nSecond line")
        assertEquals("First line\nSecond line", state.text.toString())
        compose.onNodeWithText("Anything we should know?", useUnmergedTree = true).assertDoesNotExist()
    }

    /** Real text layout, so heights follow the bundled font's metrics. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun itGrowsWithItsTextUpTo384DpThenScrolls() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixTextarea(state, label = "Notes") }

        fun height() = compose.onNodeWithText("Notes").getUnclippedBoundsInRoot().let { it.bottom - it.top }
        val empty = height()
        compose.runOnIdle { state.setTextAndPlaceCursorAtEnd((1..6).joinToString("\n") { "Line $it" }) }
        val six = height()
        compose.runOnIdle { state.setTextAndPlaceCursorAtEnd((1..80).joinToString("\n") { "Line $it" }) }
        val eighty = height()
        assertTrue("grows: $empty → $six", six > empty)
        // The label (about 19 dp) and its 6 dp gap sit above the 384 dp box.
        assertTrue("caps: $eighty", eighty.value < 384f + 19.25f + 6f + 1f)
    }

    @Test
    fun anErrorMarksItInvalidAndReadOnlyCannotBeEdited() {
        compose.setQeetrix {
            QeetrixTextarea(TextFieldState("Kept"), label = "Address", readOnly = true, message = QeetrixFieldMessage("Add a postcode."))
        }
        compose
            .onNodeWithText("Address")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Add a postcode."))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.IsEditable, false))
    }

    @Test
    fun anAddressCanBeAutofilled() {
        compose.setQeetrix { QeetrixTextarea(TextFieldState(), label = "Address", contentType = ContentType.PostalAddress) }
        compose.onNodeWithText("Address").assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.PostalAddress))
    }

    @Test
    fun aDisabledTextareaCannotBeEdited() {
        compose.setQeetrix { QeetrixTextarea(TextFieldState(), label = "Address", enabled = false) }
        compose.onNodeWithText("Address").assertIsNotEnabled()
    }
}
