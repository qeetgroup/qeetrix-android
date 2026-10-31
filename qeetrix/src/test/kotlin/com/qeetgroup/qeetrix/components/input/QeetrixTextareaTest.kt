package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
        compose.setQeetrix { QeetrixTextarea(state, label = "Notes", placeholder = "e.g. Gate code 4471") }
        compose.onNodeWithText("e.g. Gate code 4471", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Notes").assert(hasSetTextAction()).performTextInput("First line\nSecond line")
        assertEquals("First line\nSecond line", state.text.toString())
        compose.onNodeWithText("e.g. Gate code 4471", useUnmergedTree = true).assertDoesNotExist()
    }

    /**
     * input.css `.qx-textarea`: rows lines plus `(size.control.lg − 1lh)` of padding — 3 × 20 + 20 = 80 dp at the
     * default density, under the 16 dp label and its 4 dp gap. Real text layout, so lines follow the bundled font,
     * and exact: body.md's line height is 20 px to the bit (@qeetrix/tokens 0.0.13, QTRX-184), where 0.0.12's 1.4286
     * ratio made 20.0004 px, which Compose rounded up to 21 px a line.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun itStartsAtRowsLinesAndGrowsUpToMaxRows() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixTextarea(state, label = "Notes", maxRows = 5, modifier = Modifier.testTag("t")) }

        fun height() = compose.onNodeWithTag("t").getUnclippedBoundsInRoot().let { (it.bottom - it.top).value }
        assertEquals(16f + 4f + 80f, height(), 0f)
        compose.runOnIdle { state.setTextAndPlaceCursorAtEnd((1..4).joinToString("\n") { "Line $it" }) }
        val four = height()
        assertTrue("grows: $four", four > 101f)
        compose.runOnIdle { state.setTextAndPlaceCursorAtEnd((1..40).joinToString("\n") { "Line $it" }) }
        // maxRows 5: 5 × 20 + 20, then it scrolls.
        assertEquals(16f + 4f + 120f, height(), 0f)
    }

    @Test
    fun anErrorMarksItInvalidAndReadOnlyCannotBeEdited() {
        compose.setQeetrix { QeetrixTextarea(TextFieldState("Kept"), label = "Address", readOnly = true, error = "Add a PIN code.") }
        compose
            .onNodeWithText("Address")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Add a PIN code."))
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
