package com.qeetgroup.qeetrix.components.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionItem
import com.qeetgroup.qeetrix.components.descriptionlist.QeetrixDescriptionList
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Cards: a static surface with a heading, the choice card, the web's names, and a description list running edge to edge. */
@RunWith(RobolectricTestRunner::class)
class QeetrixCardTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aStaticCardIsNotAControlAndItsTitleIsAHeading() {
        compose.setQeetrix { QeetrixCard(title = "Workspace", description = "3 members") { QeetrixText("Created in March") } }
        compose.onNodeWithText("Workspace").assert(isHeading())
        compose.onNodeWithText("Created in March").assert(!hasClickAction())
    }

    @Test
    fun withoutATitleThereIsNoHeading() {
        compose.setQeetrix { QeetrixCard { QeetrixText("Body only") } }
        compose.onNodeWithText("Body only").assert(!isHeading())
        assertEquals(0, compose.onAllNodes(isHeading()).fetchSemanticsNodes().size)
    }

    /**
     * A card is not a click target (06-component-specs §9): there is no clickable overload, and nothing on a
     * card is one control but what its content puts there.
     */
    @Test
    fun aCardIsNotAClickTarget() {
        compose.setQeetrix { QeetrixCard(title = "Pro plan", description = "₹999 a month") }
        assertEquals(0, compose.onAllNodes(hasClickAction()).fetchSemanticsNodes().size)
        val overloads = Class.forName("com.qeetgroup.qeetrix.components.card.QeetrixCardKt").methods.filter { it.name == "QeetrixCard" }
        assertEquals("one QeetrixCard, with no onClick", 1, overloads.size)
    }

    @Test
    fun optionsCarryTheWebsNames() {
        assertEquals(listOf("default", "subdued"), QeetrixCardVariant.entries.map { it.value })
        assertEquals(listOf("neutral", "danger"), QeetrixCardTone.entries.map { it.value })
    }

    /** card.css: a rows description list directly in the body runs edge to edge and meets the header's rule. */
    @Test
    fun aRowsListDirectlyInTheBodyRunsEdgeToEdge() {
        compose.setQeetrix {
            Column {
                QeetrixCard(Modifier.width(320.dp).testTag("bled"), title = "Payment") {
                    QeetrixDescriptionList(Modifier.testTag("list")) { QeetrixDescriptionItem("Amount", "₹12,400") }
                }
                QeetrixCard(Modifier.width(320.dp).testTag("nested"), title = "Refund") {
                    Column { QeetrixDescriptionList(Modifier.testTag("inner")) { QeetrixDescriptionItem("Method", "UPI") } }
                }
            }
        }
        val card = compose.onNodeWithTag("bled").getUnclippedBoundsInRoot()
        val list = compose.onNodeWithTag("list", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val term = compose.onNodeWithText("Amount", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val title = compose.onNodeWithText("Payment", useUnmergedTree = true)
        assertEquals(card.right - card.left - 2.dp, list.right - list.left) // inside the 1 dp border only
        assertEquals(17.dp, term.left - card.left) // its rows keep the card's 16 dp padding
        assertEquals(card.bottom - 1.dp, list.bottom) // the last child meets the card's edge
        assertEquals(true, list.top < term.top && title.getUnclippedBoundsInRoot().bottom < list.top)
        // Not the body's direct child: the body's padding stays round it.
        val nested = compose.onNodeWithTag("nested").getUnclippedBoundsInRoot()
        val inner = compose.onNodeWithTag("inner", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(nested.right - nested.left - 34.dp, inner.right - inner.left)
    }

    /**
     * card.css 0.4.0: a card with a header and no body ends at its header, a body whose content emits nothing
     * takes no room, and a header followed by the footer has no rule of its own, so the two share the footer's.
     */
    @Test
    fun withoutBodyContentACardEndsAtItsHeaderAndTheFooterHasTheOnlyRule() {
        compose.setQeetrix {
            Column {
                QeetrixCard(Modifier.width(320.dp).testTag("header"), title = "Payment")
                QeetrixCard(Modifier.width(320.dp).testTag("empty"), title = "Payment") {}
                QeetrixCard(Modifier.width(320.dp).testTag("footer"), footer = { Box(Modifier.size(32.dp)) })
                QeetrixCard(Modifier.width(320.dp).testTag("both"), title = "Payment", footer = { Box(Modifier.size(32.dp)) })
            }
        }

        fun height(tag: String) = compose.onNodeWithTag(tag).getUnclippedBoundsInRoot().let { it.bottom - it.top }
        val title = compose.onAllNodesWithText("Payment", useUnmergedTree = true)[0].getUnclippedBoundsInRoot()
        val header = compose.onNodeWithTag("header").getUnclippedBoundsInRoot()
        // The header's 16 dp padding and the card's 1 dp edge, and nothing under them.
        assertEquals(header.bottom - 17.dp, title.bottom)
        assertEquals(height("header"), height("empty"))
        // Header + footer: both cards' contents inside one pair of edges, with one rule between them (the footer's).
        assertEquals(height("header") + height("footer") - 2.dp, height("both"))
    }
}
