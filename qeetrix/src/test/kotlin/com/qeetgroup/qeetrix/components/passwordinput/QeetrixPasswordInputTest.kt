package com.qeetgroup.qeetrix.components.passwordinput

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.selectAll
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.TextRange
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Secure entry, AutoFill, reveal without losing the editing session, and no leaks. Synthetic values only. */
@RunWith(RobolectricTestRunner::class)
class QeetrixPasswordInputTest {
    @get:Rule
    val compose = createComposeRule()

    private val synthetic = "Synthetic-Pw-0042"

    @Test
    fun itIsAPasswordFieldForAutoFill() {
        compose.setQeetrix { QeetrixPasswordInput(TextFieldState(), label = "Password") }
        compose
            .onNodeWithText("Password")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.Password))
    }

    @Test
    fun aNewPasswordAsksForANewPassword() {
        compose.setQeetrix { QeetrixPasswordInput(TextFieldState(), label = "New password", purpose = QeetrixPasswordPurpose.New) }
        compose
            .onNodeWithText(
                "New password",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.NewPassword))
    }

    @Test
    fun revealingKeepsTheTextTheSelectionAndFocus() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixPasswordInput(state, label = "Password") }
        compose.onNodeWithText("Password").performTextInput(synthetic)
        compose.runOnIdle { state.edit { selection = TextRange(3, 7) } }
        compose.onNodeWithText("Password").assertIsFocused()

        compose.onNodeWithContentDescription("Show password").performClick()
        compose.onNodeWithContentDescription("Hide password").assertExists()
        compose.onNodeWithText("Password").assertIsFocused().assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Password))
        compose.runOnIdle {
            assertEquals(synthetic, state.text.toString())
            assertEquals(TextRange(3, 7), state.selection)
        }

        compose.onNodeWithContentDescription("Hide password").performClick()
        compose.onNodeWithContentDescription("Show password").assertExists()
        compose.onNodeWithText("Password").assertIsFocused()
    }

    @Test
    fun theTextCannotBeCopiedOrCut() {
        val state = TextFieldState(synthetic)
        compose.setQeetrix { QeetrixPasswordInput(state, label = "Password") }
        compose.runOnIdle { state.edit { selectAll() } }
        compose
            .onNodeWithText("Password")
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.CopyText))
            .assert(SemanticsMatcher.keyNotDefined(SemanticsActions.CutText))
    }

    @Test
    fun thePasswordAppearsNowhereButInTheFieldItself() {
        val state = TextFieldState()
        compose.setQeetrix {
            QeetrixPasswordInput(
                state,
                label = "Password",
                description = "At least 12 characters.",
                message = QeetrixFieldMessage("Too short."),
            )
        }
        compose.onNodeWithText("Password").performTextInput(synthetic)
        compose.onNodeWithContentDescription("Show password").performClick()

        val leaks = mutableListOf<String>()

        fun visit(node: SemanticsNode) {
            val config = node.config
            val texts =
                listOfNotNull(
                    config.getOrNull(SemanticsProperties.Text)?.joinToString(),
                    config.getOrNull(SemanticsProperties.ContentDescription)?.joinToString(),
                    config.getOrNull(SemanticsProperties.StateDescription),
                    config.getOrNull(SemanticsProperties.Error),
                    // Only the password field's own text may hold it — marked as a password for accessibility services.
                    config.getOrNull(SemanticsProperties.EditableText)?.text?.takeIf { SemanticsProperties.Password !in config },
                )
            texts.filter { synthetic in it }.forEach { leaks += "node ${node.id}: $it" }
            node.children.forEach(::visit)
        }
        visit(compose.onRoot(useUnmergedTree = true).fetchSemanticsNode())
        assertTrue("the password leaked: $leaks", leaks.isEmpty())
    }

    @Test
    fun aDisabledFieldCannotBeEditedOrRevealed() {
        compose.setQeetrix { QeetrixPasswordInput(TextFieldState(synthetic), label = "Password", enabled = false) }
        compose.onNodeWithText("Password").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Show password").assertIsNotEnabled()
    }
}
