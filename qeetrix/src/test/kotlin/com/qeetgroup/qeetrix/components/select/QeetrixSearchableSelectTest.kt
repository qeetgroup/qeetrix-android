package com.qeetgroup.qeetrix.components.select

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixOptionGroup
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** A select for long lists — a sheet with a search field, groups, the choice marked, one or several, and an empty state. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSearchableSelectTest {
    @get:Rule
    val compose = createComposeRule()

    private val countries =
        listOf(
            QeetrixOption("in", "India", meta = "+91"),
            QeetrixOption("ae", "United Arab Emirates", meta = "+971"),
            QeetrixOption("gb", "United Kingdom", meta = "+44"),
            QeetrixOption("us", "United States", meta = "+1"),
            QeetrixOption("sg", "Singapore", meta = "+65", enabled = false),
        )

    /** An option in the sheet — selectable or toggleable — unlike the trigger showing the same text. */
    private fun option(label: String) =
        compose.onNode(
            hasText(label) and
                (
                    SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected) or
                        SemanticsMatcher.keyIsDefined(SemanticsProperties.ToggleableState)
                ),
        )

    private fun search() = compose.onNode(hasSetTextAction())

    @Test
    fun aTapOpensTheSheetSearchFiltersAndAChoiceCloses() {
        var country by mutableStateOf("in")
        compose.setQeetrix { QeetrixSearchableSelect(country, { country = it }, countries, label = "Country") }
        compose.onNodeWithText("Country").performClick()
        compose.onNode(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Country")).assertExists()
        option("India").assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
        search().performTextInput("united k")
        option("United Kingdom").assertExists()
        option("United States").assertDoesNotExist()
        option("India").assertDoesNotExist()
        option("United Kingdom").performClick()
        compose.runOnIdle { assertEquals("gb", country) }
        compose.onNode(hasSetTextAction()).assertDoesNotExist() // closed
        compose.onNodeWithText("Country").assert(hasText("United Kingdom"))
    }

    @Test
    fun aMetaMatchesTooAndDisabledOptionsCannotBeChosen() {
        var country by mutableStateOf("in")
        compose.setQeetrix { QeetrixSearchableSelect(country, { country = it }, countries, label = "Country") }
        compose.onNodeWithText("Country").performClick()
        search().performTextInput("+65")
        option("Singapore").performClick()
        compose.runOnIdle { assertEquals("in", country) }
    }

    @Test
    fun whenNothingMatchesTheEmptyStateSaysSoPolitely() {
        compose.setQeetrix { QeetrixSearchableSelect("in", {}, countries, label = "Country") }
        compose.onNodeWithText("Country").performClick()
        search().performTextInput("Atlantis")
        compose.onNodeWithText("No matches").assertExists()
        compose.onNodeWithText("Nothing matches “Atlantis”.").assertExists()
        compose
            .onNode(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
            .assertExists()
    }

    @Test
    fun groupLabelsStayOverTheMatchesInTheirGroup() {
        val groups =
            listOf(
                QeetrixOptionGroup("Asia", listOf(QeetrixOption("in", "India"), QeetrixOption("sg", "Singapore"))),
                QeetrixOptionGroup("Europe", listOf(QeetrixOption("gb", "United Kingdom"), QeetrixOption("de", "Germany"))),
            )
        compose.setQeetrix { QeetrixSearchableSelect("in", {}, groups, label = "Country") }
        compose.onNodeWithText("Country").performClick()
        compose.onNodeWithText("Asia").assertExists()
        search().performTextInput("germ")
        compose.onNodeWithText("Europe").assertExists()
        compose.onNodeWithText("Asia").assertDoesNotExist()
        option("Germany").assertExists()
    }

    @Test
    fun severalChoicesStayOpenThroughSearchesUntilDone() {
        var chosen by mutableStateOf(setOf("in"))
        compose.setQeetrix { QeetrixSearchableMultiSelect(chosen, { chosen = it }, countries, label = "Countries") }
        compose.onNodeWithText("Countries").performClick()
        search().performTextInput("united")
        option("United States")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
            .performClick()
        search().performTextReplacement("ind")
        option("India").performClick()
        compose.runOnIdle { assertEquals(setOf("us"), chosen) }
        compose.onNodeWithText("Done").performClick()
        search().assertDoesNotExist()
        compose.onNodeWithText("Countries").assert(hasText("United States"))
    }

    @Test
    fun theSearchStartsEmptyEachTimeTheSheetOpens() {
        compose.setQeetrix { QeetrixSearchableSelect("in", {}, countries, label = "Country") }
        compose.onNodeWithText("Country").performClick()
        search().performTextInput("united")
        option("India").assertDoesNotExist()
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Country").performClick()
        option("India").assertExists()
    }

    @Test
    fun matchingIgnoresCaseAndLooksAtTheMeta() {
        val option = QeetrixOption("ae", "United Arab Emirates", meta = "+971")
        assertTrue(option.matches(""))
        assertTrue(option.matches("arab"))
        assertTrue(option.matches("971"))
        assertFalse(option.matches("india"))
    }
}
