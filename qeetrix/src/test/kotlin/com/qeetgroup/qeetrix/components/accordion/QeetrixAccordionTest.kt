package com.qeetgroup.qeetrix.components.accordion

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
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Accordions: app-owned expansion, the header's semantics and actions, one at a time, disabled, touch size. */
@RunWith(RobolectricTestRunner::class)
class QeetrixAccordionTest {
    @get:Rule
    val compose = createComposeRule()

    private val header = hasText("Billing") and isHeading()

    @Test
    fun theHeaderIsAHeadingButtonReadWithItsState() {
        var expanded by mutableStateOf(false)
        compose.setQeetrix {
            QeetrixAccordionItem("Billing", expanded, { expanded = it }, description = "Invoices", icon = QeetrixIconName.FileText) {
                QeetrixText("Paid monthly")
            }
        }
        compose
            .onNode(header)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Expand))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Collapse))
        compose.onNodeWithText("Paid monthly").assertDoesNotExist()

        compose.onNode(header).performClick()
        compose
            .onNode(header)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Collapse))
        compose.onNodeWithText("Paid monthly").assertExists()

        // TalkBack's actions do the same.
        compose.onNode(header).performSemanticsAction(SemanticsActions.Collapse)
        compose.onNodeWithText("Paid monthly").assertDoesNotExist()
        compose.onNode(header).performSemanticsAction(SemanticsActions.Expand)
        compose.onNodeWithText("Paid monthly").assertExists()
    }

    @Test
    fun expansionIsTheAppsAndOneOpenValueOpensOneAtATime() {
        val requests = mutableListOf<Boolean>()
        var open by mutableStateOf<String?>("billing")
        compose.setQeetrix {
            QeetrixAccordion {
                QeetrixAccordionItem("Billing", open == "billing", { open = if (it) "billing" else null }) { QeetrixText("Invoices") }
                QeetrixAccordionItem("Seats", open == "seats", { open = if (it) "seats" else null }) { QeetrixText("Five seats") }
                QeetrixAccordionItem("Locked", expanded = false, onExpandedChange = { requests += it }) { QeetrixText("Never shown") }
            }
        }
        compose.onNodeWithText("Invoices").assertExists()
        compose.onNodeWithText("Seats").performClick()
        compose.onNodeWithText("Five seats").assertExists()
        compose.onNodeWithText("Invoices").assertDoesNotExist()
        // An item whose app doesn't change the state stays closed: it only asks.
        compose.onNodeWithText("Locked").performClick()
        assertEquals(listOf(true), requests)
        compose.onNodeWithText("Never shown").assertDoesNotExist()
    }

    @Test
    fun aDisabledItemCannotBeToggled() {
        val requests = mutableListOf<Boolean>()
        compose.setQeetrix {
            QeetrixAccordionItem("Billing", expanded = false, onExpandedChange = { requests += it }, enabled = false) { QeetrixText("x") }
        }
        compose
            .onNode(header)
            .assertIsNotEnabled()
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.Expand))
            .performClick()
        assertEquals(emptyList<Boolean>(), requests)
    }

    @Test
    fun theHeaderIsAtLeastA48DpTarget() {
        compose.setQeetrix { QeetrixAccordionItem("Billing", expanded = false, onExpandedChange = {}) {} }
        compose.onNode(header).assertHeightIsAtLeast(48.dp)
    }
}
