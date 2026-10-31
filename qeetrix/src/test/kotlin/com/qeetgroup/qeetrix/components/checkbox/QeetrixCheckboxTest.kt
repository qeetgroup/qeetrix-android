package com.qeetgroup.qeetrix.components.checkbox

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

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

    /** react's Checkbox: a tap on a mixed box calls onCheckedChange(!checked). */
    @Test
    fun anIndeterminateBoxIsPartlyCheckedAndATapChecksIt() {
        var checked by mutableStateOf(false)
        compose.setQeetrix { QeetrixCheckbox(checked, { checked = it }, label = "All members", indeterminate = true) }
        compose
            .onNodeWithText("All members")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Indeterminate))
            .performClick()
        assertEquals(true, checked)
    }

    @Test
    fun aDisabledCheckboxCannotBeToggled() {
        var checked by mutableStateOf(false)
        compose.setQeetrix { QeetrixCheckbox(checked, { checked = it }, label = "Archive", enabled = false) }
        compose.onNodeWithText("Archive").assertIsNotEnabled().performClick()
        assertEquals(false, checked)
    }

    @Test
    fun readOnlyOrWithoutACallbackItShowsAStateTheUserCannotChange() {
        var changes = 0
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixCheckbox(true, onCheckedChange = null, label = "Backups")
                QeetrixCheckbox(true, onCheckedChange = { changes++ }, label = "Exports", readOnly = true)
            }
        }
        for (name in listOf("Backups", "Exports")) {
            compose
                .onNodeWithText(name)
                .assertIsOn()
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
                .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.OnClick))
        }
        assertEquals(0, changes)
    }

    @Test
    fun theRowAndTheBareBoxAreFullTouchTargets() {
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixCheckbox(false, {}, label = "Email")
                QeetrixCheckbox(false, {}, modifier = Modifier.testTag("bare"))
            }
        }
        compose.onNodeWithText("Email").assertHeightIsAtLeast(48.dp)
        compose.onNodeWithTag("bare").assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp)
    }

    @Test
    fun inAFieldWithAnErrorTheCheckboxCarriesIt() {
        compose.setQeetrix {
            QeetrixField(label = "Terms", error = "Accept the terms to continue.") {
                QeetrixCheckbox(false, {}, label = "I accept the terms")
            }
        }
        compose
            .onNodeWithText("I accept the terms")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Accept the terms to continue."))
    }

    @Test
    fun aGroupTogglesValuesInItsSetAndItsErrorIsHeardOnEveryOption() {
        var value by mutableStateOf(setOf("email"))
        val options = listOf(QeetrixOption("email", "Email"), QeetrixOption("sms", "SMS"), QeetrixOption("push", "Push", enabled = false))
        compose.setQeetrix { QeetrixCheckboxGroup(value, { value = it }, options, label = "Notify me by", error = "Choose at least one.") }
        compose.onNodeWithText("Notify me by").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        compose.onNodeWithText("SMS").performClick()
        assertEquals(setOf("email", "sms"), value)
        compose.onNodeWithText("Email").performClick()
        assertEquals(setOf("sms"), value)
        compose.onNodeWithText("Push").assertIsNotEnabled()
        for (name in listOf("Email", "SMS")) {
            compose.onNodeWithText(name).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Choose at least one."))
        }
    }

    /** Vertical rows are each a 48 dp target and sit flush (the web's 24 px rows are 4 px apart). */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun groupRowsAreFullTouchTargetsAndSitFlush() {
        val options = listOf(QeetrixOption("email", "Email"), QeetrixOption("sms", "SMS"))
        compose.setQeetrix { QeetrixCheckboxGroup(emptySet(), {}, options, label = "Notify me by") }
        val email = compose.onNodeWithText("Email").assertHeightIsAtLeast(48.dp).getUnclippedBoundsInRoot()
        val sms = compose.onNodeWithText("SMS").getUnclippedBoundsInRoot()
        assertEquals(email.bottom, sms.top)
    }
}
