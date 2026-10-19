package com.qeetgroup.qeetrix.components.select

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixSelectTest {
    @get:Rule
    val compose = createComposeRule()

    private val options =
        listOf(
            QeetrixOption("starter", "Starter"),
            QeetrixOption("team", "Team"),
            QeetrixOption("enterprise", "Enterprise", enabled = false),
        )

    @Test
    fun theTriggerIsADropDownListNamedByItsLabelShowingTheChoice() {
        compose.setQeetrix { QeetrixSelect("team", {}, options, label = "Plan") }
        compose
            .onNodeWithText("Plan")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList))
            .assert(hasText("Team"))
    }

    @Test
    fun withoutAChoiceItShowsThePlaceholder() {
        compose.setQeetrix { QeetrixSelect<String?>(null, {}, options, label = "Plan", placeholder = "Choose a plan") }
        compose.onNodeWithText("Plan").assert(hasText("Choose a plan"))
    }

    @Test
    fun openingChoosingAndClosing() {
        var plan by mutableStateOf<String?>("team")
        compose.setQeetrix { QeetrixSelect(plan, { plan = it }, options, label = "Plan") }
        compose.onNodeWithText("Plan").performClick()
        // The list: the selection is marked, every option is a full touch target, and disabled ones cannot be chosen.
        compose.onNode(hasText("Team") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)).assertIsSelected()
        compose.onNodeWithText("Starter").assertIsNotSelected().assertHeightIsAtLeast(48.dp)
        compose.onNodeWithText("Enterprise").assertIsNotEnabled()
        compose.onNodeWithText("Starter").performClick()
        assertEquals("starter", plan)
        compose.onNodeWithText("Enterprise").assertDoesNotExist() // closed
        compose.onNodeWithText("Plan").assert(hasText("Starter"))
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun openedFromTheKeyboardItFocusesTheSelectedOption() {
        val focus = FocusRequester()
        compose.setQeetrix { QeetrixSelect("team", {}, options, Modifier.focusRequester(focus), label = "Plan") }
        compose.runOnIdle { focus.requestFocus() }
        compose.onNodeWithText("Plan").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        compose.onNode(hasText("Team") and SemanticsMatcher.keyIsDefined(SemanticsProperties.Selected)).assertIsFocused()
    }

    @Test
    fun readOnlyAndDisabledDoNotOpen() {
        compose.setQeetrix { QeetrixSelect("team", {}, options, label = "Plan", readOnly = true) }
        compose.onNodeWithText("Plan").assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
        compose.onNodeWithText("Plan").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.DropdownList))
    }

    @Test
    fun aDisabledSelectCannotBeOpened() {
        compose.setQeetrix { QeetrixSelect("team", {}, options, label = "Plan", enabled = false) }
        compose.onNodeWithText("Plan").performClick()
        compose.onNodeWithText("Starter").assertDoesNotExist()
    }

    @Test
    fun anErrorMarksTheTriggerInvalid() {
        compose.setQeetrix { QeetrixSelect<String?>(null, {}, options, label = "Plan", message = QeetrixFieldMessage("Choose a plan.")) }
        compose.onNodeWithText("Plan").assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Choose a plan."))
    }
}
