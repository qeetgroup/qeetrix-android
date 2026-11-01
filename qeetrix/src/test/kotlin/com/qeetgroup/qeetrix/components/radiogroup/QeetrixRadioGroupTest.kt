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
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

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
    fun optionsAreRadioButtonsInASelectableGroupUnderItsQuestion() {
        compose.setQeetrix { QeetrixRadioGroup(Plan.Team, {}, options, label = "Plan", required = true) }
        compose
            .onNodeWithText("Starter")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assert(hasText("Up to 5 members"))
            .assert(hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)))
        compose
            .onNodeWithText("Plan")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
            .assert(hasContentDescription("Plan, required"))
    }

    @Test
    fun disabledOptionsAndReadOnlyGroupsCannotBeChanged() {
        var plan by mutableStateOf(Plan.Team)
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixRadioGroup(plan, { plan = it }, options, label = "Plan")
                QeetrixRadioGroup(Plan.Team, {
                    plan = it
                }, listOf(QeetrixOption(Plan.Starter, "Small"), QeetrixOption(Plan.Team, "Large")), label = "Size", readOnly = true)
            }
        }
        compose.onNodeWithText("Enterprise").assertIsNotEnabled().performClick()
        compose.onNodeWithText("Small").performClick()
        assertEquals(Plan.Team, plan)
    }

    @Test
    fun theGroupsErrorIsHeardOnEveryOptionAndShownAfterThem() {
        compose.setQeetrix {
            QeetrixRadioGroup<Plan?>(null, {}, options, label = "Plan", description = "You can change it later.", error = "Choose a plan.")
        }
        for (name in listOf("Starter", "Team")) {
            compose.onNodeWithText(name).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Choose a plan."))
        }
        compose.onNodeWithText("You can change it later.").assertDoesNotExist()
        val team = compose.onNodeWithText("Enterprise").getUnclippedBoundsInRoot()
        val error = compose.onNodeWithText("Choose a plan.").getUnclippedBoundsInRoot()
        assertTrue(error.top >= team.bottom)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun rowsAreFullTouchTargetsAndSitFlush() {
        compose.setQeetrix { QeetrixRadioGroup(Plan.Team, {}, options, label = "Plan") }
        val starter = compose.onNodeWithText("Starter").assertHeightIsAtLeast(48.dp).getUnclippedBoundsInRoot()
        val team = compose.onNodeWithText("Team").assertHeightIsAtLeast(48.dp).getUnclippedBoundsInRoot()
        assertEquals(starter.bottom, team.top)
    }

    /** field.css: horizontal options wrap in a row, space.gap.lg (16 dp) apart. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun horizontalOptionsSitInARowSixteenDpApart() {
        compose.setQeetrix {
            QeetrixRadioGroup(
                Plan.Team,
                {},
                options.take(2),
                label = "Plan",
                orientation = QeetrixChoiceOrientation.Horizontal,
            )
        }
        val starter = compose.onNodeWithText("Starter").getUnclippedBoundsInRoot()
        val team = compose.onNodeWithText("Team").getUnclippedBoundsInRoot()
        assertEquals(starter.top, team.top)
        assertEquals(16f, (team.left - starter.right).value, 0.5f)
    }

    @Test
    fun tilesAreRadioButtonsTooAndTheWholeTileChooses() {
        var plan by mutableStateOf(Plan.Starter)
        val tiles =
            listOf(
                QeetrixOption(Plan.Starter, "Web app", "Sign-in with a redirect", icon = QeetrixIconName.House),
                QeetrixOption(Plan.Team, "Service", "Machine to machine", icon = QeetrixIconName.Server),
            )
        compose.setQeetrix {
            QeetrixRadioGroup(plan, { plan = it }, tiles, label = "Application type", variant = QeetrixChoiceVariant.Tile, columns = 2)
        }
        compose
            .onNodeWithText("Service")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assert(hasText("Machine to machine"))
        compose.onNodeWithText("Machine to machine", useUnmergedTree = true).performClick()
        assertEquals(Plan.Team, plan)
    }

    @Test
    fun optionsMustHaveValuesOfTheirOwn() {
        assertThrows(IllegalArgumentException::class.java) {
            requireDistinctValues(listOf(QeetrixOption(1, "One"), QeetrixOption(1, "Also one")))
        }
    }
}
