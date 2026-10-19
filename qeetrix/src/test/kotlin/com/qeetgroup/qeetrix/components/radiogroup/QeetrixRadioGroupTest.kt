package com.qeetgroup.qeetrix.components.radiogroup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixRadioGroupTest {
    @get:Rule
    val compose = createComposeRule()

    private enum class Plan { Starter, Team, Enterprise }

    private val options =
        listOf(
            QeetrixOption(Plan.Starter, "Starter", "Up to 5 members"),
            QeetrixOption(Plan.Team, "Team"),
            QeetrixOption(Plan.Enterprise, "Enterprise", enabled = false),
        )

    @Test
    fun choosingAnOptionSelectsItAndOnlyIt() {
        var plan by mutableStateOf<Plan?>(null)
        compose.setQeetrix { QeetrixRadioGroup(plan, { plan = it }, options, label = "Plan") }
        compose
            .onNodeWithText("Team")
            .assertIsNotSelected()
            .performClick()
            .assertIsSelected()
        assertEquals(Plan.Team, plan)
        compose.onNodeWithText("Starter").assertIsNotSelected().performClick()
        compose.onNodeWithText("Starter").assertIsSelected()
        compose.onNodeWithText("Team").assertIsNotSelected()
    }

    @Test
    fun optionsAreRadioButtonsInASelectableGroupUnderItsLabel() {
        compose.setQeetrix { QeetrixRadioGroup(Plan.Team, {}, options, label = "Plan") }
        compose
            .onNodeWithText("Starter")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assert(hasText("Up to 5 members"))
            .assert(hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)))
        compose.onNodeWithText("Plan").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
    }

    @Test
    fun aDisabledOptionCannotBeChosen() {
        var plan by mutableStateOf(Plan.Team)
        compose.setQeetrix { QeetrixRadioGroup(plan, { plan = it }, options, label = "Plan") }
        compose.onNodeWithText("Enterprise").assertIsNotEnabled().performClick()
        assertEquals(Plan.Team, plan)
    }

    @Test
    fun theGroupsErrorIsHeardOnEveryOption() {
        compose.setQeetrix { QeetrixRadioGroup<Plan?>(null, {}, options, label = "Plan", message = QeetrixFieldMessage("Choose a plan.")) }
        for (name in listOf("Starter", "Team")) {
            compose.onNodeWithText(name).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Choose a plan."))
        }
    }

    @Test
    fun rowsAreFullTouchTargetsAndSitFlush() {
        compose.setQeetrix { QeetrixRadioGroup(Plan.Team, {}, options, label = "Plan") }
        val starter = compose.onNodeWithText("Starter").assertHeightIsAtLeast(48.dp).getUnclippedBoundsInRoot()
        val team = compose.onNodeWithText("Team").assertHeightIsAtLeast(48.dp).getUnclippedBoundsInRoot()
        assertEquals(starter.bottom, team.top)
    }

    @Test
    fun optionsMustHaveValuesOfTheirOwn() {
        assertThrows(IllegalArgumentException::class.java) {
            requireDistinctValues(listOf(QeetrixOption(1, "One"), QeetrixOption(1, "Also one")))
        }
    }
}
