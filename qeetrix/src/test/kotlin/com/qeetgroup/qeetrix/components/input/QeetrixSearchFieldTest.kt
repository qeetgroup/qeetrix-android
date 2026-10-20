package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.ImeAction
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Search fields: a name without a label, the search key, a clear button that keeps focus, disabled. */
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
    fun theSearchKeyReportsTheQuery() {
        val searches = mutableListOf<String>()
        compose.setQeetrix { QeetrixSearchField(TextFieldState(), onSearch = { searches += it }) }
        compose.onNode(field).performTextInput("ada")
        compose.onNode(field).performImeAction()
        assertEquals(listOf("ada"), searches)
    }

    @Test
    fun clearEmptiesTheQueryAndKeepsTheFocus() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixSearchField(state) }
        compose.onNodeWithContentDescription("Clear").assertDoesNotExist()
        compose.onNode(field).performClick()
        compose.onNode(field).performTextInput("grace")
        compose.onNodeWithContentDescription("Clear").performClick()
        compose.runOnIdle { assertEquals("", state.text.toString()) }
        compose.onNodeWithContentDescription("Clear").assertDoesNotExist()
        compose.onNode(field).assertIsFocused()
    }

    @Test
    fun aDisabledFieldCannotBeEdited() {
        compose.setQeetrix { QeetrixSearchField(TextFieldState("ada"), enabled = false) }
        compose.onNode(field).assertIsNotEnabled()
    }
}
