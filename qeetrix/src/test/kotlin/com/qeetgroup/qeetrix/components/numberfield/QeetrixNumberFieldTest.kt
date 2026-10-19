package com.qeetgroup.qeetrix.components.numberfield

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal

/** Typing, committing, stepping and the locale, with exact decimals. Runs in English (US) unless set. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "en-rUS")
class QeetrixNumberFieldTest {
    @get:Rule
    val compose = createComposeRule()

    private fun field() = compose.onNodeWithText("Discount")

    private fun hasText(text: String) = SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString(text))

    @Test
    fun theValueFollowsTheTextAndInBetweenStatesAreKept() {
        var value by mutableStateOf<BigDecimal?>(null)
        compose.setQeetrix { QeetrixNumberField(value, { value = it }, label = "Discount", fractionDigits = 2) }
        field().performTextInput("12")
        compose.runOnIdle { assertEquals(BigDecimal("12.00"), value) } // at the field's two decimal places
        field().performTextInput(".")
        field().assert(hasText("12.")) // not rewritten while typing
        compose.runOnIdle { assertEquals(BigDecimal("12.00"), value) }
        field().performTextInput("5")
        compose.runOnIdle { assertEquals(BigDecimal("12.50"), value) }
    }

    @Test
    fun committingBringsTheValueIntoRangeRoundsAndFormats() {
        var value by mutableStateOf<BigDecimal?>(BigDecimal("10"))
        compose.setQeetrix {
            QeetrixNumberField(
                value,
                { value = it },
                label = "Discount",
                range = BigDecimal.ZERO..BigDecimal(100),
                fractionDigits = 1,
                imeAction = ImeAction.Done,
            )
        }
        field().performTextClearance()
        field().performTextInput("1250.26")
        compose.runOnIdle { assertNull("out of range is not reported while typing", value) }
        field().performImeAction()
        compose.runOnIdle { assertEquals(BigDecimal("100.0"), value) }
        field().assert(hasText("100"))
    }

    @Test
    fun textThatIsNotANumberGoesBackToTheLastValue() {
        var value by mutableStateOf<BigDecimal?>(BigDecimal("1234"))
        compose.setQeetrix { QeetrixNumberField(value, { value = it }, label = "Discount", imeAction = ImeAction.Done) }
        field().assert(hasText("1,234")).performTextInput(",5") // "1,234,5": a grouping separator where English puts none
        compose.runOnIdle { assertEquals(BigDecimal("1234"), value) } // unchanged while the text is not a number
        field().performImeAction()
        field().assert(hasText("1,234"))
        compose.runOnIdle { assertEquals(BigDecimal("1234"), value) }
    }

    @Test
    fun lettersCannotBeTyped() {
        var value by mutableStateOf<BigDecimal?>(null)
        compose.setQeetrix { QeetrixNumberField(value, { value = it }, label = "Discount") }
        field().performTextInput("12abc")
        field().assert(hasText(""))
    }

    @Test
    fun steppersStepAndStopAtTheEnds() {
        var seats by mutableStateOf<Int?>(1)
        compose.setQeetrix { QeetrixNumberField(seats, { seats = it }, label = "Discount", range = 1..3) }
        compose.onNodeWithContentDescription("Decrease").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Increase").performClick().performClick()
        compose.runOnIdle { assertEquals(3, seats) }
        compose.onNodeWithContentDescription("Increase").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Decrease").assertIsEnabled()
        field().assert(hasText("3"))
    }

    @Test
    fun tappingAStepperDoesNotTakeFocusFromElsewhere() {
        compose.setQeetrix { QeetrixNumberField(5, {}, label = "Discount") }
        compose.onNodeWithContentDescription("Increase").performClick()
        field().assertIsNotFocused()
    }

    @Test
    fun accessibilityServicesCanStepFromTheField() {
        var value by mutableStateOf<Int?>(5)
        compose.setQeetrix { QeetrixNumberField(value, { value = it }, label = "Discount") }
        val actions = field().fetchSemanticsNode().config[SemanticsActions.CustomActions]
        assertEquals(listOf("Decrease", "Increase"), actions.map { it.label })
        compose.runOnIdle { actions.last().action() }
        compose.runOnIdle { assertEquals(6, value) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun arrowKeysStep() {
        var value by mutableStateOf<Int?>(5)
        compose.setQeetrix { QeetrixNumberField(value, { value = it }, label = "Discount") }
        field().performClick().assertIsFocused().performKeyInput { pressKey(Key.DirectionUp) }
        compose.runOnIdle { assertEquals(6, value) }
        field().performKeyInput { pressKey(Key.DirectionDown) }
        field().performKeyInput { pressKey(Key.DirectionDown) }
        compose.runOnIdle { assertEquals(4, value) }
    }

    @Test
    fun aNewValueFromOutsideRewritesTheText() {
        var value by mutableStateOf<BigDecimal?>(BigDecimal("1"))
        compose.setQeetrix { QeetrixNumberField(value, { value = it }, label = "Discount", fractionDigits = 2) }
        value = BigDecimal("4321.5")
        field().assert(hasText("4,321.5"))
    }

    @Test
    @Config(qualifiers = "de-rDE")
    fun germanTypesAndReadsWithACommaDecimal() {
        var value by mutableStateOf<BigDecimal?>(BigDecimal("1234.5"))
        compose.setQeetrix { QeetrixNumberField(value, { value = it }, label = "Discount", fractionDigits = 2) }
        field().assert(hasText("1.234,5")).performTextClearance()
        field().performTextInput("0,25")
        compose.runOnIdle { assertEquals(BigDecimal("0.25"), value) }
    }

    @Test
    fun clearingTheFieldMeansNoValue() {
        var value by mutableStateOf<BigDecimal?>(BigDecimal("7"))
        compose.setQeetrix { QeetrixNumberField(value, { value = it }, label = "Discount") }
        field().performTextClearance()
        compose.runOnIdle { assertNull(value) }
    }

    @Test
    fun aReadOnlyFieldCannotBeStepped() {
        compose.setQeetrix { QeetrixNumberField(5, {}, label = "Discount", readOnly = true) }
        compose.onNodeWithContentDescription("Increase").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Decrease").assertIsNotEnabled()
    }
}
