package com.qeetgroup.qeetrix.components.select

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Several choices (`QeetrixMultiSelect`), an open state the app can own, and type-to-jump. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSelectMoreTest {
    @get:Rule
    val compose = createComposeRule()

    private val regions =
        listOf(
            QeetrixOption("in", "India"),
            QeetrixOption("ae", "United Arab Emirates"),
            QeetrixOption("nl", "Netherlands"),
            QeetrixOption("no", "Norway"),
            QeetrixOption("sg", "Singapore", enabled = false),
        )

    /** An option in the open list — a node that is selectable or toggleable, unlike the trigger showing the same text. */
    private fun option(label: String) =
        compose.onNode(
            hasText(label) and
                (
                    SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected) or
                        SemanticsMatcher.keyIsDefined(SemanticsProperties.ToggleableState)
                ),
        )

    @Test
    fun severalChoicesToggleAndTheListStaysOpen() {
        var chosen by mutableStateOf(setOf("in"))
        compose.setQeetrix { QeetrixMultiSelect(chosen, { chosen = it }, regions, label = "Data regions") }
        compose.onNodeWithText("Data regions").performClick()
        option("India").assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
        option("Norway")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
            .performClick()
        option("India").performClick()
        compose.runOnIdle { assertEquals(setOf("no"), chosen) }
        // Still open: the next option can be toggled straight away.
        option("Netherlands").performClick()
        compose.runOnIdle { assertEquals(setOf("no", "nl"), chosen) }
        option("Singapore").performClick()
        compose.runOnIdle { assertEquals(setOf("no", "nl"), chosen) }
    }

    @Test
    fun theTriggerListsTheChosenLabelsInTheListsOrder() {
        compose.setQeetrix { QeetrixMultiSelect(setOf("no", "in"), {}, regions, label = "Data regions", placeholder = "e.g. India") }
        compose.onNodeWithText("Data regions").assert(hasText("India, Norway"))
    }

    @Test
    fun withNothingChosenItShowsThePlaceholder() {
        compose.setQeetrix { QeetrixMultiSelect(emptySet<String>(), {}, regions, label = "Data regions", placeholder = "e.g. India") }
        compose.onNodeWithText("Data regions").assert(hasText("e.g. India"))
    }

    @Test
    fun anAppThatOwnsTheOpenStateDecides() {
        var open by mutableStateOf(false)
        val requests = mutableListOf<Boolean>()
        var region by mutableStateOf("in")
        compose.setQeetrix {
            QeetrixSelect(region, { region = it }, regions, label = "Region", open = open, onOpenChange = { requests += it })
        }
        // A tap asks; the list shows only when the app says so.
        compose.onNodeWithText("Region").performClick()
        compose.onNodeWithText("Norway").assertDoesNotExist()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Norway").performClick()
        compose.runOnIdle {
            assertEquals("no", region)
            assertEquals(listOf(true, false), requests)
        }
        // The app has not closed it yet, so it is still open.
        compose.onNodeWithText("Netherlands").assertExists()
        compose.runOnIdle { open = false }
        compose.onNodeWithText("Netherlands").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun typingOnTheClosedTriggerChoosesTheMatch() {
        val focus = FocusRequester()
        var region by mutableStateOf("in")
        compose.setQeetrix { QeetrixSelect(region, { region = it }, regions, label = "Region", modifier = Modifier.focusRequester(focus)) }
        compose.runOnIdle { focus.requestFocus() }
        compose.onNodeWithText("Region").performKeyInput { pressKey(Key.N) }
        compose.runOnIdle { assertEquals("nl", region) }
        // The same letter again moves on to the next option starting with it.
        compose.onNodeWithText("Region").performKeyInput { pressKey(Key.N) }
        compose.runOnIdle { assertEquals("no", region) }
        compose.onNodeWithText("Netherlands").assertDoesNotExist() // still closed: only the list shows other options
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun typingInTheOpenListMovesTheFocus() {
        val focus = FocusRequester()
        compose.setQeetrix { QeetrixSelect("in", {}, regions, label = "Region", modifier = Modifier.focusRequester(focus)) }
        compose.runOnIdle { focus.requestFocus() }
        compose.onNodeWithText("Region").performKeyInput { pressKey(Key.Enter) }
        option("India").assertIsFocused()
        option("India").performKeyInput { pressKey(Key.U) }
        option("United Arab Emirates").assertIsFocused()
    }

    @Test
    fun typeaheadPrefixesCyclesAndTimeouts() {
        var now = 0L
        val typeahead = SelectTypeahead { now }
        assertEquals("nl", typeahead.next('n', regions, -1)?.value)
        // "no" typed quickly: Norway, the first label starting with it, from the current option.
        assertEquals("no", typeahead.next('o', regions, 2)?.value)
        now += 2_000
        // After the timeout a new prefix: "n" moves on from Norway, round to Netherlands.
        assertEquals("nl", typeahead.next('n', regions, 3)?.value)
        now += 2_000
        // Disabled options are passed over; nothing else starts with "s".
        assertNull(typeahead.next('s', regions, 0))
        now += 2_000
        assertEquals("ae", typeahead.next('U', regions, 0)?.value)
    }
}
