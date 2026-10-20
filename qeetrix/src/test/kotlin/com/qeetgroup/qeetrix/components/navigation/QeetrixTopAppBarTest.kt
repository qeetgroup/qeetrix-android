package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The top app bar: a heading, the app's own back and actions, no state of its own. */
@RunWith(RobolectricTestRunner::class)
class QeetrixTopAppBarTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theTitleAndSubtitleAreOneHeadingAndTheButtonsAreTheApps() {
        val taps = mutableListOf<String>()
        compose.setQeetrix {
            QeetrixTopAppBar(
                title = "Invoice INV-2041",
                subtitle = "Draft",
                navigationIcon = { QeetrixBackButton(onClick = { taps += "back" }) },
                actions = { QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = { taps += "share" }) },
            )
        }
        compose.onNode(isHeading() and hasText("Invoice INV-2041") and hasText("Draft")).assertExists()
        compose
            .onNodeWithContentDescription("Back")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .performClick()
        compose.onNodeWithContentDescription("Share").performClick()
        assertEquals(listOf("back", "share"), taps)
    }

    @Test
    fun itIsAtLeastTheBarHeightAndShowsNoBackButtonUnlessGivenOne() {
        compose.setQeetrix { QeetrixTopAppBar(title = "Settings") }
        compose.onNodeWithText("Settings").onParent().assertHeightIsAtLeast(56.dp)
        compose.onNodeWithContentDescription("Back").assertDoesNotExist()
    }
}
