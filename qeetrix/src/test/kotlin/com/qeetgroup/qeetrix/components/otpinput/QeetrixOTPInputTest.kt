package com.qeetgroup.qeetrix.components.otpinput

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.math.roundToInt

/** One text field behind the cells: typing, paste, AutoFill semantics and completion. Synthetic codes only. */
@RunWith(RobolectricTestRunner::class)
class QeetrixOTPInputTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun itKeepsLeadingZerosAndReportsTheCompleteCodeOnce() {
        val state = TextFieldState()
        val completed = mutableListOf<String>()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Verification code", onValueComplete = { completed += it }) }
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
        compose.setQeetrix {
            QeetrixOTPInput(
                state,
                label = "Recovery code",
                length = 8,
                validationType = QeetrixOTPValidationType.Alphanumeric,
            )
        }
        compose.onNodeWithText("Recovery code").performTextInput("ab12-CD34")
        compose.runOnIdle { assertEquals("ab12CD34", state.text.toString()) }
    }

    @Test
    fun clearingAndRetypingCompletesAgain() {
        val state = TextFieldState()
        val completed = mutableListOf<String>()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Verification code", length = 4, onValueComplete = { completed += it }) }
        val field = compose.onNodeWithText("Verification code")
        field.performTextInput("4821")
        compose.runOnIdle { state.clearText() }
        field.performTextInput("4821")
        compose.runOnIdle { assertEquals(listOf("4821", "4821"), completed) }
    }

    @Test
    fun aCodeAlreadyCompleteWhenShownIsNotReported() {
        val completed = mutableListOf<String>()
        compose.setQeetrix { QeetrixOTPInput(TextFieldState("123456"), label = "Verification code", onValueComplete = { completed += it }) }
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
    fun alphaCodesTakeLettersOnly() {
        val state = TextFieldState()
        compose.setQeetrix { QeetrixOTPInput(state, label = "Word", length = 4, validationType = QeetrixOTPValidationType.Alpha) }
        compose.onNodeWithText("Word").performTextInput("a1b2cD")
        compose.runOnIdle { assertEquals("abcD", state.text.toString()) }
    }

    @Test
    fun aHiddenLabelStillNamesTheCode() {
        compose.setQeetrix { QeetrixOTPInput(TextFieldState(), label = "One-time code", hideLabel = true) }
        compose.onNode(hasSetTextAction() and hasContentDescription("One-time code")).assertExists()
    }

    @Test
    fun groupsMustAddUpToTheLength() {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix { QeetrixOTPInput(TextFieldState(), label = "Code", groups = listOf(3, 2)) }
        }
    }

    /** otp-input.css: the slots share the container's width, gap.sm apart; md slots are size.control.lg tall, lg 16 dp more. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun theSlotsSpanTheContainer() {
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixOTPInput(TextFieldState(), label = "Md", modifier = Modifier.width(300.dp).testTag("md"))
                QeetrixOTPInput(
                    TextFieldState(),
                    label = "Lg",
                    size = QeetrixOTPInputSize.Lg,
                    modifier = Modifier.width(300.dp).testTag("lg"),
                )
            }
        }
        // The label (16 dp) and its 4 dp gap sit above the slots.
        val md = compose.onNodeWithTag("md").getUnclippedBoundsInRoot()
        val lg = compose.onNodeWithTag("lg").getUnclippedBoundsInRoot()
        assertEquals(300f, (md.right - md.left).value, 0.5f)
        assertEquals(16f + 4f + 40f, (md.bottom - md.top).value, 0.5f)
        assertEquals(16f + 4f + 56f, (lg.bottom - lg.top).value, 0.5f)
    }

    /**
     * A 2-digit number match keeps two 40 dp square slots, 8 dp apart, at the start (the right in right to left),
     * while the label and the field keep the container's 300 dp. Read from the pixels: the slots are hidden from tests.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun squareSlotsSitAtTheStartAndTheFieldKeepsTheWidth() {
        compose.setQeetrix {
            Column {
                QeetrixOTPInput(
                    TextFieldState(),
                    label = "Number match",
                    length = 2,
                    slotWidth = QeetrixOTPSlotWidth.Square,
                    modifier = Modifier.width(300.dp).testTag("ltr"),
                )
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    QeetrixOTPInput(
                        TextFieldState(),
                        label = "Number match",
                        length = 2,
                        slotWidth = QeetrixOTPSlotWidth.Square,
                        modifier = Modifier.width(300.dp).testTag("rtl"),
                    )
                }
                QeetrixOTPInput(TextFieldState(), label = "Number match", length = 2, modifier = Modifier.width(300.dp).testTag("fill"))
            }
        }
        val ltr = compose.onNodeWithTag("ltr")
        ltr.getUnclippedBoundsInRoot().let { assertEquals(300f, (it.right - it.left).value, 0.5f) }
        // The slot row's middle: under the 16 dp label and its 4 dp gap, half a 40 dp slot down.
        val row = 16f + 4f + 20f
        val empty = ltr.colourAt(295f, row)
        for (inSlot in listOf(2f, 38f, 50f, 86f)) assertNotEquals("ltr $inSlot", empty, ltr.colourAt(inSlot, row))
        for (outside in listOf(44f, 92f, 200f)) assertEquals("ltr $outside", empty, ltr.colourAt(outside, row))

        val rtl = compose.onNodeWithTag("rtl")
        for (inSlot in listOf(298f, 262f, 250f, 214f)) assertNotEquals("rtl $inSlot", empty, rtl.colourAt(inSlot, row))
        for (outside in listOf(256f, 208f, 5f)) assertEquals("rtl $outside", empty, rtl.colourAt(outside, row))

        // The default still shares the width: the second slot reaches the end.
        assertNotEquals(empty, compose.onNodeWithTag("fill").colourAt(295f, row))
        // And it is still one field, named by its label.
        compose.onAllNodesWithText("Number match").assertCountEquals(3)
    }

    private fun SemanticsNodeInteraction.colourAt(
        xDp: Float,
        yDp: Float,
    ) = captureToImage().toPixelMap().let { pixels ->
        val density = compose.density.density
        pixels[(xDp * density).roundToInt(), (yDp * density).roundToInt()]
    }

    @Test
    fun anErrorMarksItInvalid() {
        compose.setQeetrix {
            QeetrixOTPInput(TextFieldState("12"), label = "Verification code", error = "That code has expired. Enter the new one we sent.")
        }
        compose
            .onNodeWithText(
                "Verification code",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Error, "That code has expired. Enter the new one we sent."))
    }
}
