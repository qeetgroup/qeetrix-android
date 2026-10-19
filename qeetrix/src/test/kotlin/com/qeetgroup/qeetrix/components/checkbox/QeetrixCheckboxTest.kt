package com.qeetgroup.qeetrix.components.checkbox

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixCheckboxTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theRowIsOneCheckboxThatToggles() {
        var checked by mutableStateOf(false)
        compose.setQeetrix { QeetrixCheckbox(checked, { checked = it }, label = "Product updates", description = "About once a month.") }
        compose
            .onNodeWithText("Product updates")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(hasText("About once a month.")) // label and description read together
            .assertIsOff()
            .performClick()
            .assertIsOn()
        assertEquals(true, checked)
    }

    @Test
    fun mixedIsShownAndTheTapIsYoursToDecide() {
        var taps = 0
        compose.setQeetrix { QeetrixTriStateCheckbox(ToggleableState.Indeterminate, onClick = { taps++ }, label = "All members") }
        compose
            .onNodeWithText("All members")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Indeterminate))
            .performClick()
        assertEquals(1, taps)
    }

    @Test
    fun aDisabledCheckboxCannotBeToggled() {
        var checked by mutableStateOf(false)
        compose.setQeetrix { QeetrixCheckbox(checked, { checked = it }, label = "Archive", enabled = false) }
        compose.onNodeWithText("Archive").assertIsNotEnabled().performClick()
        assertEquals(false, checked)
    }

    @Test
    fun withoutACallbackItShowsAStateTheUserCannotChange() {
        compose.setQeetrix { QeetrixCheckbox(true, onCheckedChange = null, label = "Backups") }
        compose.onNodeWithText("Backups").assertIsOn().assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
    }

    @Test
    fun theRowAndTheBareBoxAreFullTouchTargets() {
        compose.setQeetrix { QeetrixCheckbox(false, {}, label = "Email") }
        compose.onNodeWithText("Email").assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun anInvalidCheckboxIsMarkedAsAnErrorOnlyThroughAGroup() {
        compose.setQeetrix { QeetrixCheckbox(false, {}, label = "Accept the terms", invalid = true) }
        // invalid draws the boundary; the message (and the error state) come from a QeetrixFieldSet.
        compose.onNodeWithText("Accept the terms").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
    }
}
