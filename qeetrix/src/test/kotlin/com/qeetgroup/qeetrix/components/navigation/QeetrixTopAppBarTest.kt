package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

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

    /** An onboarding step's bar — back and "Step 1 of 2" — reads the caption as text, not a heading, 56 dp tall. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text metrics
    fun withoutATitleTheCaptionIsTextNotAHeading() {
        var caption: TextLayoutResult? = null
        var small: TextStyle? = null
        compose.setQeetrix {
            small = QeetrixTheme.typography.bodySm
            QeetrixTopAppBar(
                title = null,
                description = "Step 1 of 2",
                modifier = Modifier.testTag("bar"),
                navigationIcon = { QeetrixBackButton(onClick = {}) },
            )
        }
        compose.onNodeWithText("Step 1 of 2").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))
        compose.onAllNodes(isHeading()).assertCountEquals(0)
        compose.onNodeWithTag("bar").assertHeightIsAtLeast(56.dp)
        val layouts = mutableListOf<TextLayoutResult>()
        compose
            .onNodeWithText("Step 1 of 2", useUnmergedTree = true)
            .fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult]
            .action!!(layouts)
        caption = layouts.first()
        assertEquals(small!!.fontSize, caption!!.layoutInput.style.fontSize)
    }

    @Test
    fun aLargeBarNeedsATitle() {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix { QeetrixTopAppBar(title = null, size = QeetrixTopAppBarSize.Large) }
        }
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
