package com.qeetgroup.qeetrix.components.emptystate

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
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Empty states: a heading, the description, actions as controls, a silent tile, and the web's names. */
@RunWith(RobolectricTestRunner::class)
class QeetrixEmptyStateTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theTitleIsAHeadingFollowedByTheDescriptionAndTheActions() {
        val created = mutableListOf<Unit>()
        compose.setQeetrix {
            QeetrixEmptyState(
                "No invoices yet",
                Modifier.testTag("empty"),
                description = "Invoices appear here once you bill a customer.",
                variant = QeetrixEmptyStateVariant.FirstUse,
            ) { QeetrixButton("Create invoice", onClick = { created += Unit }) }
        }
        compose.onNode(isHeading() and hasText("No invoices yet")).assertExists()
        compose.onNodeWithText("Invoices appear here once you bill a customer.").assertExists()
        compose.onNodeWithText("Create invoice").performClick()
        assertEquals(1, created.size)
    }

    @Test
    fun theTileIsDecorativeForEveryVariant() {
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                for (variant in QeetrixEmptyStateVariant.entries) {
                    QeetrixEmptyState(variant.value, Modifier.testTag("empty"), variant = variant)
                }
            }
        }
        // No image, role or description is exposed: only the titles.
        compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription)).assertCountEquals(0)
        compose.onAllNodesWithTag("empty").assertCountEquals(QeetrixEmptyStateVariant.entries.size)
    }

    @Test
    fun optionsCarryTheWebsNames() {
        assertEquals(
            listOf("default", "first-use", "no-results", "no-permission", "error"),
            QeetrixEmptyStateVariant.entries.map { it.value },
        )
        assertEquals(listOf("sm", "default", "lg"), QeetrixEmptyStateSize.entries.map { it.value })
    }
}
