package com.qeetgroup.qeetrix.components.datepicker

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.width
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate
import java.time.LocalTime

/** The date, range and time fields: the value in the locale, the calendar in its dialog, Done, and the time's steps. */
@RunWith(RobolectricTestRunner::class)
class QeetrixDateFieldTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aChosenDayIsReportedOnDone() {
        var date by mutableStateOf<LocalDate?>(LocalDate.of(2026, 10, 6))
        compose.setQeetrix { QeetrixDateField(date, { date = it }, label = "Invoice date") }
        // One button named by its label, the date in the locale's words as its state.
        compose
            .onNodeWithContentDescription("Invoice date")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Oct 6, 2026"))
        compose.onNodeWithContentDescription("Invoice date", substring = true).assertExists().performClick()
        compose.onNodeWithText("October 2026", substring = true).assertExists()
        // Material names each day with its whole date.
        compose.onNode(hasText("October 15", substring = true) or hasContentDescription("October 15", substring = true)).performClick()
        compose.onNodeWithText("Done").performClick()
        assertEquals(LocalDate.of(2026, 10, 15), date)
    }

    /**
     * Found on a device: Material's calendar is 360 dp wide and the dialog's body narrower, so every Saturday was cut.
     * Robolectric's 320 dp screen is narrower still: the calendar is scaled to fit, its last column inside the dialog.
     */
    @Test
    fun theWholeWeekFitsInANarrowDialog() {
        compose.setQeetrix { QeetrixDateField(LocalDate.of(2026, 10, 6), {}, label = "Invoice date") }
        compose.onNodeWithContentDescription("Invoice date", substring = true).assertExists().performClick()

        // Bounds are clipped by the dialog: a cut Saturday is narrower than the week's first day, Sunday.
        fun day(name: String) =
            compose.onNode(hasText(name, substring = true) or hasContentDescription(name, substring = true)).getBoundsInRoot()
        val sunday = day("October 4,")
        val saturday = day("October 10,")
        assertEquals("Sunday $sunday, Saturday $saturday", sunday.width.value, saturday.width.value, 0.5f)
    }

    @Test
    fun aRangeIsDoneOnlyOnceBothEndsAreChosen() {
        var range by mutableStateOf<ClosedRange<LocalDate>?>(null)
        compose.setQeetrix { QeetrixDateRangeField(range, { range = it }, label = "Report period", placeholder = "Choose dates") }
        compose.onNodeWithContentDescription("Report period", substring = true).performClick()
        compose.onNodeWithText("Done").assertIsNotEnabled()
        compose.onNodeWithText("Cancel").performClick()
        assertNull(range)
    }

    /**
     * Found on a device: the time picker's chosen AM was Material's pink `tertiaryContainer`, a role the mapping had
     * left at Material's default. Every colour role of the scheme is now Qeetrix's, in both schemes.
     */
    @Test
    fun noMaterialColourIsLeftAtItsDefault() {
        // ColorScheme's getters return Color as its packed Long (an inline class): getPrimary-0d7_KjU and so on.
        val roles =
            ColorScheme::class.java.methods.filter {
                it.name.startsWith("get") && it.returnType == java.lang.Long.TYPE &&
                    it.parameterCount == 0
            }
        assertTrue(roles.size >= 30)
        // A role the mapping sets is the same whatever scheme it starts from; one it leaves follows the base.
        for ((dark, colors) in listOf(false to QeetrixLightColors, true to QeetrixDarkColors)) {
            val fromLight = materialColors(colors, dark, base = lightColorScheme())
            val fromDark = materialColors(colors, dark, base = darkColorScheme())
            val leaks =
                roles
                    .filter {
                        it.invoke(
                            fromLight,
                        ) != it.invoke(fromDark)
                    }.map { it.name.substringBefore('-').removePrefix("get") }
            assertEquals("Material's own colours left in the ${if (dark) "dark" else "light"} scheme", emptyList<String>(), leaks)
        }
    }

    @Test
    fun aTimeIsRoundedToItsStep() {
        assertEquals(LocalTime.of(9, 30), snapToStep(LocalTime.of(9, 37), 15))
        assertEquals(LocalTime.of(9, 45), snapToStep(LocalTime.of(9, 38), 15))
        assertEquals(LocalTime.of(0, 0), snapToStep(LocalTime.of(23, 53), 15))
        assertEquals(LocalTime.of(9, 37), snapToStep(LocalTime.of(9, 37), 1))
        var time by mutableStateOf<LocalTime?>(LocalTime.of(9, 30))
        compose.setQeetrix { QeetrixTimeField(time, { time = it }, label = "Daily digest at", minuteStep = 15, is24Hour = false) }
        compose.onNodeWithContentDescription("Daily digest at", substring = true).performClick()
        compose.onNodeWithText("Done").assertIsEnabled().performClick()
        assertEquals(LocalTime.of(9, 30), time)
    }
}
