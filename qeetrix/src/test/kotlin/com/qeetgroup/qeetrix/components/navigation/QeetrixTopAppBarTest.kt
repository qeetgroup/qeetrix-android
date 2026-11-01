package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The top app bar: one heading of the title and description, the app's own back and actions, no state of its own. */
@RunWith(RobolectricTestRunner::class)
class QeetrixTopAppBarTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theTitleAndDescriptionAreOneHeadingAndTheButtonsAreTheApps() {
        val taps = mutableListOf<String>()
        compose.setQeetrix {
            QeetrixTopAppBar(
                title = "Invoice INV-2041",
                description = "Draft",
                navigationIcon = { QeetrixBackButton(onClick = { taps += "back" }) },
                actions = {
                    QeetrixIconButton(
                        QeetrixIconName.Share2,
                        "Share",
                        onClick = { taps += "share" },
                        tone = QeetrixButtonTone.Neutral,
                    )
                },
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

    /** `calc(size.control.lg + 16px)`: 56 dp at the default density. */
    @Test
    fun itIsAtLeastTheChromeHeightAndShowsNoBackButtonUnlessGivenOne() {
        compose.setQeetrix { QeetrixTopAppBar(title = "Settings") }
        compose.onNodeWithText("Settings").onParent().assertHeightIsAtLeast(56.dp)
        compose.onNodeWithContentDescription("Back").assertDoesNotExist()
    }

    @Test
    fun theBackButtonNamesItsParentWhenAsked() {
        compose.setQeetrix {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                QeetrixBackButton(onClick = {}, contentDescription = "Back to Users")
            }
        }
        compose.onNodeWithContentDescription("Back to Users").assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
    }
}
