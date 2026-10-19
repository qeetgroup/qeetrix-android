package com.qeetgroup.qeetrix.components.card

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixCardTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aStaticCardIsNotAControlAndItsTitleIsAHeading() {
        compose.setQeetrix { QeetrixCard(title = "Workspace", description = "3 members") { QeetrixText("Created in March") } }
        compose.onNodeWithText("Workspace").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        compose.onNodeWithText("Created in March").assert(!hasClickAction())
    }

    @Test
    fun aClickableCardIsOneButton() {
        var clicks = 0
        compose.setQeetrix { QeetrixCard(onClick = { clicks++ }, title = "Pro plan") { QeetrixText("₹999 a month") } }
        // The card merges its content into one button named by it.
        val card = compose.onNodeWithText("Pro plan", substring = true)
        card.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        card.performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun aDisabledCardCannotBeActivated() {
        var clicks = 0
        compose.setQeetrix { QeetrixCard(onClick = { clicks++ }, enabled = false, title = "Archived") }
        compose.onNodeWithText("Archived").assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
    }

    @Test
    fun selectedOnlyDrawsSoTheOwnerOfAChoiceStatesIt() {
        compose.setQeetrix {
            Column {
                QeetrixCard(onClick = {}, selected = true, title = "Drawn only")
                QeetrixCard(onClick = {}, modifier = Modifier.semantics { selected = true }, selected = true, title = "Stated")
            }
        }
        compose.onNodeWithText("Drawn only").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
        // The consumer's state lands on the card's own node, beside its role and its merged name.
        compose
            .onNodeWithText("Stated")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }
}
