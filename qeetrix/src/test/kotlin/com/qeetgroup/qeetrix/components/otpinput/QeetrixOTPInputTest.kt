package com.qeetgroup.qeetrix.components.otpinput

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** One text field behind the cells: typing, paste, AutoFill semantics and completion. Synthetic codes only. */
@RunWith(RobolectricTestRunner::class)
class QeetrixOTPInputTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun itKeepsLeadingZerosAndReportsTheCompleteCodeOnce() {
        val state = TextFieldState()
        val completed = mutableListOf<String>()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Verification code") { completed += it } }
        compose.onNodeWithText("Verification code").assert(hasSetTextAction()).performTextInput("012345")
        compose.runOnIdle {
            assertEquals("012345", state.text.toString())
            assertEquals(listOf("012345"), completed)
        }
    }

    @Test
    fun otherCharactersAreDroppedAndTheCodeStopsAtItsLength() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Verification code") }
        val field = compose.onNodeWithText("Verification code")
        field.performTextInput("12-3 4")
        compose.runOnIdle { assertEquals("1234", state.text.toString()) }
        field.performTextInput("5678")
        compose.runOnIdle { assertEquals("123456", state.text.toString()) }
    }

    @Test
    fun aPastedFullCodeReplacesAPartialOne() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Verification code") }
        val field = compose.onNodeWithText("Verification code")
        field.performTextInput("12")
        field.performTextInput("987-654")
        compose.runOnIdle { assertEquals("987654", state.text.toString()) }
    }

    @Test
    fun digitsInAnyScriptAreEnteredAsZeroToNine() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Verification code") }
        compose.onNodeWithText("Verification code").performTextInput("١٢٣٤٥٦")
        compose.runOnIdle { assertEquals("123456", state.text.toString()) }
    }

    @Test
    fun alphanumericCodesKeepLettersAsTyped() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Recovery code", length = 8, characters = QeetrixOTPCharacters.Alphanumeric) }
        compose.onNodeWithText("Recovery code").performTextInput("ab12-CD34")
        compose.runOnIdle { assertEquals("ab12CD34", state.text.toString()) }
    }

    @Test
    fun clearingAndRetypingCompletesAgain() {
        val state = TextFieldState()
        val completed = mutableListOf<String>()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Verification code", length = 4) { completed += it } }
        val field = compose.onNodeWithText("Verification code")
        field.performTextInput("4821")
        compose.runOnIdle { state.clearText() }
        field.performTextInput("4821")
        compose.runOnIdle { assertEquals(listOf("4821", "4821"), completed) }
    }

    @Test
    fun aCodeAlreadyCompleteWhenShownIsNotReported() {
        val completed = mutableListOf<String>()
        compose.setQeetrix { QeetrixOTPInput(TextFieldState("123456"), label = "Verification code") { completed += it } }
        compose.runOnIdle { assertEquals(emptyList<String>(), completed) }
    }

    @Test
    fun itIsMarkedForSmsCodeAutoFillUnlessTurnedOff() {
        compose.setQeetrix { QeetrixOTPInput(TextFieldState(), label = "SMS code") }
        compose.onNodeWithText("SMS code").assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentType, ContentType.SmsOtpCode))
    }

    @Test
    fun anAuthenticatorCodeIsNotOfferedSmsAutoFill() {
        compose.setQeetrix { QeetrixOTPInput(TextFieldState(), label = "Authenticator code", smsCodeAutofill = false) }
        compose.onNodeWithText("Authenticator code").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ContentType))
    }

    @Test
    fun itIsOneFieldNotARowOfBoxes() {
        compose.setQeetrix { QeetrixOTPInput(TextFieldState("135"), label = "Verification code") }
        // The cells draw the code but are not read: in what accessibility services get (the merged tree), no
        // node carries a single digit, and the field's text is the code.
        for (digit in listOf("1", "3", "5")) compose.onAllNodesWithText(digit).assertCountEquals(0)
        compose
            .onNodeWithText(
                "Verification code",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("135")))
    }

    @Test
    fun withoutALabelItIsNamedOneTimeCode() {
        compose.setQeetrix { QeetrixOTPInput(TextFieldState()) }
        compose.onNode(hasSetTextAction() and hasContentDescription("One-time code")).assertExists()
    }

    @Test
    fun anErrorMarksItInvalid() {
        compose.setQeetrix {
            QeetrixOTPInput(TextFieldState("12"), label = "Verification code", message = QeetrixFieldMessage("That code has expired."))
        }
        compose
            .onNodeWithText(
                "Verification code",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "That code has expired."))
    }
}
