package com.qeetgroup.qeetrix.components.emptystate

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Empty states: the title, a heading only with a level, the description, actions and footer, a silent icon, the web's names. */
@RunWith(RobolectricTestRunner::class)
class QeetrixEmptyStateTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theTitleTheDescriptionTheActionsAndTheFooter() {
        val created = mutableListOf<Unit>()
        compose.setQeetrix {
            QeetrixEmptyState(
                "No invoices yet",
                description = "Invoices appear here once you bill a customer.",
                icon = QeetrixIconName.FileText,
                footer = { QeetrixText("Or import them from a CSV file") },
            ) { QeetrixButton("Create invoice", onClick = { created += Unit }) }
        }
        compose.onNodeWithText("No invoices yet").assertExists()
        // Without a level the title is styled text, as on the web.
        compose.onNode(isHeading()).assertDoesNotExist()
        compose.onNodeWithText("Invoices appear here once you bill a customer.").assertExists()
        compose.onNodeWithText("Or import them from a CSV file").assertExists()
        compose.onNodeWithText("Create invoice").performClick()
        assertEquals(1, created.size)
    }

    @Test
    fun withALevelTheTitleIsAHeading() {
        compose.setQeetrix { QeetrixEmptyState("Your inbox is clear", level = 1) }
        compose.onNode(isHeading() and hasText("Your inbox is clear")).assertExists()
    }

    @Test
    fun theIconIsDecorativeInEveryTone() {
        compose.setQeetrix {
            Column {
                for (tone in QeetrixEmptyStateTone.entries) {
                    QeetrixEmptyState(tone.value, Modifier.testTag("empty"), icon = QeetrixIconName.Inbox, tone = tone)
                }
            }
        }
        // No image, role or description is exposed: only the titles.
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription)).assertCountEquals(0)
        compose.onAllNodesWithTag("empty").assertCountEquals(QeetrixEmptyStateTone.entries.size)
    }

    @Test
    fun optionsCarryTheWebsNames() {
        assertEquals(listOf("page", "section"), QeetrixEmptyStateSize.entries.map { it.value })
        assertEquals(listOf("neutral", "danger"), QeetrixEmptyStateTone.entries.map { it.value })
    }
}
