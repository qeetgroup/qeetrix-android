package com.qeetgroup.qeetrix

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qeetgroup.qeetrix.components.numberfield.NumberText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.Locale

/**
 * The number field's reading and writing on a device, where Android's ICU formats numbers — not the JDK the JVM
 * tests run on, which knows no Indian grouping, for one. Every value is written in each locale and read back to
 * itself; Indian grouping, Arabic-Indic digits and French spaces are read as people type them. Run with
 * `./gradlew :qeetrix:connectedDebugAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
class QeetrixLocaleDeviceTest {
    private val locales =
        listOf(
            Locale.US,
            Locale.GERMANY,
            Locale.FRANCE,
            Locale.forLanguageTag("hi-IN"),
            Locale.forLanguageTag("te-IN"),
            Locale.forLanguageTag("ar-EG"),
            Locale.forLanguageTag("ar-SA"),
            Locale.forLanguageTag("en-IN"),
        )

    private val values = listOf("0", "7", "12.5", "1234", "123456.78", "-42.25", "10000000").map(::BigDecimal)

    @Test
    fun everyValueReadsBackFromHowEachLocaleWritesIt() {
        val failures = mutableListOf<String>()
        for (locale in locales) {
            val numbers = NumberText(locale, fractionDigits = 2, allowNegative = true)
            for (value in values) {
                val written = numbers.write(value)
                val read = numbers.read(written)
                val back = (read as? NumberText.Reading.Number)?.value
                if (back == null ||
                    back.compareTo(value) != 0
                ) {
                    failures += "${locale.toLanguageTag()}: $value written \"$written\" reads $read"
                }
            }
        }
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    @Test
    fun indianGroupingAndOtherScriptsDigitsAreReadAsTyped() {
        val indian = NumberText(Locale.forLanguageTag("en-IN"), fractionDigits = 2, allowNegative = false)
        assertEquals(BigDecimal("123456.5"), (indian.read("1,23,456.5") as NumberText.Reading.Number).value)
        val hindi = NumberText(Locale.forLanguageTag("hi-IN"), fractionDigits = 0, allowNegative = false)
        assertEquals(BigDecimal("2024"), (hindi.read("२०२४") as NumberText.Reading.Number).value)
        val arabic = NumberText(Locale.forLanguageTag("ar-EG"), fractionDigits = 2, allowNegative = false)
        assertEquals(BigDecimal("12.5"), (arabic.read(arabic.write(BigDecimal("12.5"))) as NumberText.Reading.Number).value)
        val french = NumberText(Locale.FRANCE, fractionDigits = 2, allowNegative = false)
        assertEquals(BigDecimal("1234.5"), (french.read("1 234,5") as NumberText.Reading.Number).value)
    }
}
