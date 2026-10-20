package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixTextFieldTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun typingUpdatesTheCallersState() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixTextField(state, label = "Work email", placeholder = "you@company.com") }
        // The label names the editable field; the placeholder shows while it is empty.
        val field = compose.onNodeWithText("Work email")
        field.assert(hasSetTextAction())
        compose.onNodeWithText("you@company.com", useUnmergedTree = true).assertExists()
        field.performTextInput("ada@example.com")
        assertEquals("ada@example.com", state.text.toString())
        compose.onNodeWithText("you@company.com", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun tappingTheLabelFocusesTheField() {
        compose.setQeetrix { QeetrixTextField(rememberTextFieldState(), label = "Name") }
        compose.onNodeWithText("Name").performClick().assertIsFocused()
    }

    @Test
    fun anErrorMarksTheFieldInvalidAndIsAnnouncedAtOnce() {
        compose.setQeetrix {
            QeetrixTextField(rememberTextFieldState(), label = "Email", message = QeetrixFieldMessage("Enter a valid email address."))
        }
        compose.onNodeWithText("Email").assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Enter a valid email address."))
        compose
            .onNodeWithText("Enter a valid email address.")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
    }

    @Test
    fun warningsAndSuccessAreAnnouncedPolitelyAndAreNotErrors() {
        compose.setQeetrix {
            QeetrixTextField(
                rememberTextFieldState(),
                label = "Username",
                message = QeetrixFieldMessage("Available", QeetrixFieldMessageTone.Success),
            )
        }
        compose.onNodeWithText("Username").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
        compose.onNodeWithText("Available").assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
    }

    @Test
    fun theKeyboardActionReachesTheCaller() {
        var submitted = false
        compose.setQeetrix {
            QeetrixTextField(
                rememberTextFieldState(),
                label = "Search",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                onKeyboardAction = { submitted = true },
            )
        }
        compose.onNodeWithText("Search").performClick().performImeAction()
        assertEquals(true, submitted)
    }

    @Test
    fun disabledAndReadOnlyFieldsCannotBeEdited() {
        val disabled = TextFieldState("locked")
        val readOnly = TextFieldState("shown")
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixTextField(disabled, label = "Disabled", enabled = false)
                QeetrixTextField(readOnly, label = "Read only", readOnly = true)
            }
        }
        compose.onNodeWithText("Disabled").assertIsNotEnabled()
        compose.onNodeWithText("Read only").assert(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.IsEditable).not() or
                SemanticsMatcher.expectValue(SemanticsProperties.IsEditable, false),
        )
        assertEquals("shown", readOnly.text.toString())
    }

    @Test
    fun theAutofillTypeIsTheTextFieldsOwn() {
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixTextField(rememberTextFieldState(), label = "Username", contentType = ContentType.Username)
                // Without one, Compose's type for the keyboard stays.
                val email = KeyboardOptions(keyboardType = KeyboardType.Email)
                QeetrixTextField(rememberTextFieldState(), label = "Email", keyboardOptions = email)
            }
        }
        compose
            .onNodeWithText("Username")
            .assert(hasSetTextAction())
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.Username))
        compose.onNodeWithText("Email").assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.EmailAddress))
    }

    @Test
    fun theOptionalMarkerComesFromTheStringResource() {
        compose.setQeetrix { QeetrixTextField(rememberTextFieldState(), label = "Nickname", optional = true) }
        compose.onNodeWithText("(optional)", substring = true).assertExists()
    }
}
