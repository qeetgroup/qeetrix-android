package com.qeetgroup.qeetrix.components.accordion

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** Accordions: the open values are the app's, the trigger's semantics and actions, one or several open, disabled; Collapsible. */
@RunWith(RobolectricTestRunner::class)
class QeetrixAccordionTest {
    @get:Rule
    val compose = createComposeRule()

    private val trigger = hasText("Billing") and isHeading()

    @Test
    fun theTriggerIsAHeadingButtonReadWithItsState() {
        var open by mutableStateOf(listOf<String>())
        compose.setQeetrix {
            QeetrixAccordion(open, { open = it }) { QeetrixAccordionItem("billing", "Billing") { QeetrixText("Paid monthly") } }
        }
        compose
            .onNode(trigger)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Expand))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Collapse))
        compose.onNodeWithText("Paid monthly").assertDoesNotExist()

        compose.onNode(trigger).performClick()
        assertEquals(listOf("billing"), open)
        compose
            .onNode(trigger)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Collapse))
        compose.onNodeWithText("Paid monthly").assertExists()

        // TalkBack's actions do the same.
        compose.onNode(trigger).performSemanticsAction(SemanticsActions.Collapse)
        compose.onNodeWithText("Paid monthly").assertDoesNotExist()
        compose.onNode(trigger).performSemanticsAction(SemanticsActions.Expand)
        compose.onNodeWithText("Paid monthly").assertExists()
    }

    /** The summary is read with the title and the state, under the title, while the section is closed. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text metrics
    fun aSummaryIsReadWithTheTitleAndSitsUnderIt() {
        compose.setQeetrix {
            QeetrixAccordion(listOf<String>(), {}) {
                QeetrixAccordionItem("advanced", "Advanced", description = "6 digits · every 30 seconds · SHA-1") {
                    QeetrixText("Digits")
                }
            }
        }
        val node = compose.onNode(hasText("Advanced") and isHeading())
        node
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
            .assert(hasText("6 digits · every 30 seconds · SHA-1"))
        val title = compose.onNodeWithText("Advanced", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val summary = compose.onNodeWithText("6 digits · every 30 seconds · SHA-1", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(title.left, summary.left)
        assertEquals(title.bottom, summary.top)
        node.assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun withoutMultipleOpeningOneClosesTheOther() {
        var open by mutableStateOf(listOf("billing"))
        compose.setQeetrix {
            QeetrixAccordion(open, { open = it }) {
                QeetrixAccordionItem("billing", "Billing") { QeetrixText("Invoices") }
                QeetrixAccordionItem("seats", "Seats") { QeetrixText("Five seats") }
            }
        }
        compose.onNodeWithText("Seats").performClick()
        assertEquals(listOf("seats"), open)
        compose.onNodeWithText("Five seats").assertExists()
        compose.onNodeWithText("Invoices").assertDoesNotExist()
    }

    @Test
    fun withMultipleSeveralStayOpen() {
        var open by mutableStateOf(listOf("billing"))
        compose.setQeetrix {
            QeetrixAccordion(open, { open = it }, multiple = true) {
                QeetrixAccordionItem("billing", "Billing") { QeetrixText("Invoices") }
                QeetrixAccordionItem("seats", "Seats") { QeetrixText("Five seats") }
            }
        }
        compose.onNodeWithText("Seats").performClick()
        assertEquals(listOf("billing", "seats"), open)
        compose.onNodeWithText("Billing").performClick()
        assertEquals(listOf("seats"), open)
    }

    @Test
    fun theOpenSectionsAreTheAppsSoAnAccordionThatIgnoresAChangeStaysAsItIs() {
        val requests = mutableListOf<List<String>>()
        compose.setQeetrix {
            QeetrixAccordion(
                listOf<String>(),
                { requests += it },
            ) { QeetrixAccordionItem("locked", "Locked") { QeetrixText("Never shown") } }
        }
        compose.onNodeWithText("Locked").performClick()
        assertEquals(listOf(listOf("locked")), requests)
        compose.onNodeWithText("Never shown").assertDoesNotExist()
    }

    @Test
    fun aDisabledSectionOrAccordionCannotBeToggled() {
        val requests = mutableListOf<List<String>>()
        var accordionEnabled by mutableStateOf(true)
        compose.setQeetrix {
            QeetrixAccordion(listOf<String>(), { requests += it }, enabled = accordionEnabled) {
                QeetrixAccordionItem("billing", "Billing", enabled = false) { QeetrixText("x") }
                QeetrixAccordionItem("seats", "Seats") { QeetrixText("y") }
            }
        }
        compose
            .onNode(trigger)
            .assertIsNotEnabled()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Expand))
            .performClick()
        assertEquals(emptyList<List<String>>(), requests)
        compose.runOnIdle { accordionEnabled = false }
        compose.onNode(hasText("Seats") and isHeading()).assertIsNotEnabled().performClick()
        assertEquals(emptyList<List<String>>(), requests)
    }

    /** accordion.css: min-block-size size.control.lg; Android's touch area reaches 48 dp round it. */
    @Test
    fun theTriggerIsTheFieldHeightAndA48DpTarget() {
        compose.setQeetrix { QeetrixAccordion(listOf<String>(), {}) { QeetrixAccordionItem("billing", "Billing") {} } }
        compose.onNode(trigger).assertHeightIsAtLeast(QeetrixDensity.Default.sizeControlLg)
        val node = compose.onNode(trigger).fetchSemanticsNode()
        val touch = with(compose.density) { node.touchBoundsInRoot.height.toDp() }
        assertTrue("touch height $touch", touch >= 48.dp)
    }

    @Test
    fun anItemGoesInsideAnAccordion() {
        assertThrows(IllegalStateException::class.java) {
            compose.setQeetrix { QeetrixAccordionItem("billing", "Billing") {} }
        }
    }

    @Test
    fun aCollapsibleTriggerSaysWhetherItsPanelIsOpen() {
        var open by mutableStateOf(false)
        compose.setQeetrix {
            Column {
                QeetrixCollapsibleTrigger("Advanced options", open = open, onOpenChange = { open = it })
                QeetrixCollapsiblePanel(open) { QeetrixText("Webhook signing secret") }
            }
        }
        compose
            .onNodeWithText("Advanced options")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
            .performClick()
        assertEquals(true, open)
        compose.onNodeWithText("Webhook signing secret").assertExists()
        compose
            .onNodeWithText("Advanced options")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
            .performSemanticsAction(SemanticsActions.Collapse)
        compose.onNodeWithText("Webhook signing secret").assertDoesNotExist()
    }
}
