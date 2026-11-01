package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.text.input.ImeAction
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Search fields: a name without a label, searching on a pause and on the search key, clearing by button and Escape. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSearchFieldTest {
    @get:Rule
    val compose = createComposeRule()

    private val field = SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText)

    @Test
    fun withoutALabelTheFieldIsNamedSearch() {
        compose.setQeetrix { QeetrixSearchField(TextFieldState()) }
        val node = compose.onNode(field).fetchSemanticsNode()
        assertEquals(listOf("Search"), node.config.getOrNull(SemanticsProperties.ContentDescription))
        assertEquals(ImeAction.Search, node.config.getOrNull(SemanticsProperties.ImeAction))
    }

    @Test
    fun aLabelNamesTheFieldInstead() {
        compose.setQeetrix { QeetrixSearchField(TextFieldState(), label = "Find a member") }
        val node = compose.onNode(field).fetchSemanticsNode()
        val texts =
            node.config
                .getOrNull(SemanticsProperties.Text)
                ?.map { it.text }
                .orEmpty()
        assertTrue("$texts", "Find a member" in texts)
        assertEquals(null, node.config.getOrNull(SemanticsProperties.ContentDescription))
    }

    @Test
    fun theSearchKeySearchesAtOnceAndOnlyOnce() {
        val searches = mutableListOf<String>()
        compose.setQeetrix { QeetrixSearchField(TextFieldState(), onSearch = { searches += it }) }
        compose.onNode(field).performTextInput("ada")
        compose.onNode(field).performImeAction()
        compose.mainClock.advanceTimeBy(1_000)
        assertEquals(listOf("ada"), searches)
    }

    /** search-field.tsx: onSearch runs when typing pauses for searchDelay (300 ms), once per pause. */
    @Test
    fun typingSearchesOncePerPause() {
        val searches = mutableListOf<String>()
        compose.setQeetrix { QeetrixSearchField(TextFieldState(), onSearch = { searches += it }) }
        compose.mainClock.autoAdvance = false
        compose.onNode(field).performTextInput("a")
        compose.mainClock.advanceTimeBy(100)
        compose.onNode(field).performTextInput("d")
        compose.mainClock.advanceTimeBy(200)
        assertEquals(emptyList<String>(), searches)
        compose.mainClock.advanceTimeBy(400)
        assertEquals(listOf("ad"), searches)
    }

    @Test
    fun textTheAppWritesIsNotSearched() {
        val searches = mutableListOf<String>()
        val state = TextFieldState()
        compose.setQeetrix { QeetrixSearchField(state, onSearch = { searches += it }) }
        compose.runOnIdle { state.setTextAndPlaceCursorAtEnd("restored") }
        compose.mainClock.advanceTimeBy(1_000)
        assertEquals(emptyList<String>(), searches)
    }

    @Test
    fun clearEmptiesTheQuerySearchesNothingAndKeepsTheFocus() {
        val state = TextFieldState()
        val searches = mutableListOf<String>()
        compose.setQeetrix { QeetrixSearchField(state, onSearch = { searches += it }) }
        compose.onNodeWithContentDescription("Clear search").assertDoesNotExist()
        compose.onNode(field).performClick()
        compose.onNode(field).performTextInput("grace")
        compose.onNodeWithContentDescription("Clear search").performClick()
        compose.mainClock.advanceTimeBy(1_000)
        compose.runOnIdle { assertEquals("", state.text.toString()) }
        assertEquals(listOf(""), searches)
        compose.onNodeWithContentDescription("Clear search").assertDoesNotExist()
        compose.onNode(field).assertIsFocused()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun escapeClearsAFieldWithText() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixSearchField(state) }
        compose.onNode(field).performClick()
        compose.onNode(field).performTextInput("grace")
        compose.onNode(field).performKeyInput { pressKey(Key.Escape) }
        compose.runOnIdle { assertEquals("", state.text.toString()) }
    }

    @Test
    fun aDisabledFieldCannotBeEditedOrCleared() {
        compose.setQeetrix { QeetrixSearchField(TextFieldState("ada"), enabled = false) }
        compose.onNode(field).assertIsNotEnabled()
        compose.onNodeWithContentDescription("Clear search").assertDoesNotExist()
    }
}
