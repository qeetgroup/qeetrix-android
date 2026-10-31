package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class QeetrixTextFieldTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun typingUpdatesTheCallersState() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixTextField(state, label = "Work email", placeholder = "e.g. asha@acme.in") }
        // The label names the editable field; the placeholder shows while it is empty.
        val field = compose.onNodeWithText("Work email")
        field.assert(hasSetTextAction())
        compose.onNodeWithText("e.g. asha@acme.in", useUnmergedTree = true).assertExists()
        field.performTextInput("asha@acme.in")
        assertEquals("asha@acme.in", state.text.toString())
        compose.onNodeWithText("e.g. asha@acme.in", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun tappingTheLabelFocusesTheField() {
        compose.setQeetrix { QeetrixTextField(rememberTextFieldState(), label = "Name") }
        compose.onNodeWithText("Name").performClick().assertIsFocused()
    }

    @Test
    fun anErrorMarksTheFieldInvalidIsAnnouncedAtOnceAndReplacesTheHelpText() {
        compose.setQeetrix {
            QeetrixTextField(
                rememberTextFieldState(),
                label = "Email",
                description = "We send receipts here.",
                error = "Enter a work email.",
            )
        }
        compose.onNodeWithText("Email").assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "Enter a work email."))
        compose
            .onNodeWithText("Enter a work email.")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
        compose.onNodeWithText("We send receipts here.").assertDoesNotExist()
    }

    @Test
    fun invalidWithoutAMessageIsNotAnErrorForAccessibilityServices() {
        compose.setQeetrix { QeetrixTextField(rememberTextFieldState(), label = "Code", description = "6 digits.", invalid = true) }
        compose.onNodeWithText("Code").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
        compose.onNodeWithText("6 digits.").assertExists()
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
        val readOnly = TextFieldState("shown")
        compose.setQeetrix {
            Column {
                QeetrixTextField(TextFieldState("locked"), label = "Disabled", enabled = false)
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
            Column {
                QeetrixTextField(rememberTextFieldState(), label = "Username", contentType = ContentType.Username)
                // Without one, Compose's type for the keyboard stays.
                QeetrixTextField(
                    rememberTextFieldState(),
                    label = "Email",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )
            }
        }
        compose.onNodeWithText("Username").assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.Username))
        compose.onNodeWithText("Email").assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.EmailAddress))
    }

    @Test
    fun adornmentsAreReadWithTheFieldAndTheIconIsDecorative() {
        compose.setQeetrix {
            QeetrixTextField(
                rememberTextFieldState(),
                label = "Amount",
                icon = QeetrixIconName.Search,
                startAdornment = { QeetrixText("₹") },
                endAdornment = { QeetrixText(".00") },
            )
        }
        compose.onNodeWithText("Amount").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentDescription))
        compose.onNodeWithText("₹", useUnmergedTree = true).assertExists()
        compose.onNodeWithText(".00", useUnmergedTree = true).assertExists()
    }

    /** input.css: block-size size.control.lg (40 dp at the default density) under a 16 dp label.sm line and its 4 dp gap. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text metrics
    fun theBoxIsTheLargeControlHeight() {
        compose.setQeetrix { QeetrixTextField(rememberTextFieldState(), label = "City", modifier = Modifier.testTag("f")) }
        val bounds = compose.onNodeWithTag("f").getUnclippedBoundsInRoot()
        assertEquals(16f + 4f + 40f, (bounds.bottom - bounds.top).value, 0.5f)
    }

    /** At comfortable density, size.control.lg is 44 dp. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text metrics
    fun theBoxFollowsTheDensity() {
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides QeetrixDensity.Comfortable) {
                QeetrixTextField(rememberTextFieldState(), label = "City", modifier = Modifier.testTag("f"))
            }
        }
        val bounds = compose.onNodeWithTag("f").getUnclippedBoundsInRoot()
        assertEquals(16f + 4f + 44f, (bounds.bottom - bounds.top).value, 0.5f)
    }

    /** A hidden label leaves the field centred in a 48 dp touch height. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text metrics
    fun aFieldWithAHiddenLabelIsA48DpTarget() {
        compose.setQeetrix {
            QeetrixTextField(
                rememberTextFieldState(),
                label = "City",
                hideLabel = true,
                modifier = Modifier.testTag("f"),
            )
        }
        val bounds = compose.onNodeWithTag("f").getUnclippedBoundsInRoot()
        assertEquals(48f, (bounds.bottom - bounds.top).value, 0.5f)
    }
}
