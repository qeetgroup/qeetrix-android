package com.qeetgroup.qeetrix.components.form

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class QeetrixFormTest {
    @get:Rule
    val compose = createComposeRule()

    /** The dialog footer's row: tertiary first, the primary last and at the end, space.gap.sm apart. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun actionsShareARowAtTheEnd() {
        compose.setQeetrix {
            QeetrixFormActions(Modifier.width(360.dp).testTag("row")) {
                QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                QeetrixButton("Save", onClick = {})
            }
        }
        val cancel = compose.onNodeWithText("Cancel").getUnclippedBoundsInRoot()
        val save = compose.onNodeWithText("Save").getUnclippedBoundsInRoot()
        val row = compose.onNodeWithTag("row").getUnclippedBoundsInRoot()
        assertEquals("one row", cancel.top, save.top)
        assertTrue("cancel first", cancel.right <= save.left)
        assertEquals("at the end", row.right.value, save.right.value, 0.5f)
    }

    @Test
    fun theSummaryIsAnnouncedAndEachErrorTakesYouToItsField() {
        val chosen = mutableListOf<String>()
        compose.setQeetrix {
            QeetrixFormErrorSummary(
                listOf(QeetrixFormError("Enter a name.") { chosen += "name" }, QeetrixFormError("Choose a plan.") { chosen += "plan" }),
                title = "Fix these to continue",
            )
        }
        compose
            .onNode(
                hasText("Fix these to continue") and SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion),
                useUnmergedTree = true,
            ).assertDoesNotExist() // the announcement is the alert's, not the title's alone
        compose
            .onNode(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
            .assertExists()
        compose
            .onNodeWithText("Fix these to continue", useUnmergedTree = true)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
            .assert(hasContentDescription("Error: Fix these to continue"))
        compose
            .onNodeWithText("Choose a plan.")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assertEquals(listOf("plan"), chosen)
    }

    @Test
    fun itTakesFocusWhenItAppears() {
        compose.setQeetrix {
            QeetrixFormErrorSummary(listOf(QeetrixFormError("Enter a name.")), title = "Fix these", modifier = Modifier.testTag("s"))
        }
        compose.onNodeWithTag("s").assertIsFocused()
    }

    @Test
    fun anItemWithoutAnActionIsPlainText() {
        compose.setQeetrix { QeetrixFormErrorSummary(listOf(QeetrixFormError("Something went wrong on our side.")), autoFocus = false) }
        compose.onNodeWithText("Something went wrong on our side.").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Role))
    }

    @Test
    fun withNoErrorsThereIsNoSummary() {
        compose.setQeetrix { QeetrixFormErrorSummary(emptyList(), title = "Fix these") }
        compose.onNodeWithText("Fix these").assertDoesNotExist()
    }

    /** form.css: fields stand space.form-gap (16 dp at the default density) apart, the summary at the top. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun aFormStacksItsFieldsTheFormGapApartUnderItsSummary() {
        compose.setQeetrix {
            QeetrixForm(errors = listOf(QeetrixFormError("Enter a GSTIN.")), summaryTitle = "Fix these") {
                QeetrixTextField(TextFieldState(), label = "GSTIN")
                QeetrixTextField(TextFieldState(), label = "Trade name", optional = true)
            }
        }
        val summary = compose.onNodeWithText("Fix these", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val gstin = compose.onNodeWithText("GSTIN").getUnclippedBoundsInRoot()
        val trade = compose.onNodeWithText("Trade name", substring = true).getUnclippedBoundsInRoot()
        assertTrue(summary.bottom < gstin.top)
        assertEquals(16f, (trade.top - gstin.bottom).value, 0.5f)
    }
}
