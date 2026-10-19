package com.qeetgroup.qeetrix.components.switchcontrol

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
class QeetrixSwitchTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun itIsANativeSwitchThatToggles() {
        compose.setQeetrix {
            var on by remember { mutableStateOf(false) }
            QeetrixSwitch(on, { on = it }, modifier = Modifier.testTag("s"))
        }
        compose
            .onNodeWithTag("s")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assertIsOff()
            .assertHeightIsAtLeast(48.dp)
            .performClick()
            .assertIsOn()
    }

    @Test
    fun aDisabledSwitchDoesNotToggle() {
        var changes = 0
        compose.setQeetrix { QeetrixSwitch(true, { changes++ }, enabled = false, modifier = Modifier.testTag("s")) }
        compose
            .onNodeWithTag("s")
            .assertIsNotEnabled()
            .assertIsOn()
            .performClick()
        assertEquals(0, changes)
    }

    @Test
    fun aReadOnlySwitchReportsItsStateWithoutAnAction() {
        compose.setQeetrix { QeetrixSwitch(true, onCheckedChange = null, modifier = Modifier.testTag("s")) }
        compose.onNodeWithTag("s").assertIsOn().assert(!hasClickAction())
    }

    @Test
    fun theLabelledSwitchIsOneControlTheWholeRowToggles() {
        var on by mutableStateOf(false)
        compose.setQeetrix { QeetrixSwitch(on, { on = it }, label = "Email alerts", description = "Sent within a minute") }
        // Label, description, role and state are one node; tapping the label toggles.
        compose
            .onNodeWithText("Email alerts")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assertIsOff()
            .performClick()
        assertEquals(true, on)
        compose.onNodeWithText("Sent within a minute").assertIsOn()
    }
}
