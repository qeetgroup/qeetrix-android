package com.qeetgroup.qeetrix.components.numberfield

import com.qeetgroup.qeetrix.components.numberfield.NumberText.Reading
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.util.Locale

/** Locale-aware reading of number text: strict, exact, and patient with text that is still being typed. */
class NumberTextTest {
    private fun numbers(
        locale: Locale,
        fractionDigits: Int = 2,
        allowNegative: Boolean = true,
    ) = NumberText(locale, fractionDigits, allowNegative)

    private fun NumberText.value(text: String): BigDecimal? = (read(text) as? Reading.Number)?.value

    @Test
    fun englishUsesAPointAndCommaGrouping() {
        val en = numbers(Locale.US)
        assertEquals(BigDecimal("1234.5"), en.value("1,234.5"))
        assertEquals(BigDecimal("1234.5"), en.value("1234.5"))
        assertEquals(BigDecimal("-0.25"), en.value("-0.25"))
        assertEquals(BigDecimal("0.5"), en.value(".5"))
    }

    @Test
    fun germanSwapsTheSeparatorsAndIsNotReadLeniently() {
        val de = numbers(Locale.GERMANY)
        assertEquals(BigDecimal("1234.5"), de.value("1.234,5"))
        assertEquals(BigDecimal("1234.5"), de.value("1234,5"))
        // A lenient parser reads this as 1234 (or, dropping the separator, 12345); it is neither.
        assertEquals(Reading.Invalid, de.read("1234.5"))
    }

    @Test
    fun frenchGroupsWithAnyOfItsSpaces() {
        val fr = numbers(Locale.FRANCE)
        assertEquals(BigDecimal("1234.5"), fr.value("1 234,5"))
        assertEquals(BigDecimal("1234.5"), fr.value("1 234,5"))
        assertEquals(BigDecimal("1234.5"), fr.value("1 234,5"))
    }

    @Test
    fun groupingCountsOnlyWhereTheLocalePutsIt() {
        val en = numbers(Locale.US)
        assertEquals(Reading.Invalid, en.read("1,23"))
        assertEquals(Reading.Invalid, en.read("12,34,567"))
        assertEquals(BigDecimal("1234567"), en.value("1,234,567"))
    }

    @Test
    fun digitsFromOtherScriptsAreNumbers() {
        val en = numbers(Locale.US)
        assertEquals(BigDecimal("123.45"), en.value("١٢٣.٤٥")) // Arabic-Indic
        assertEquals(BigDecimal("2024"), en.value("२०२४")) // Devanagari
    }

    @Test
    fun textThatIsNotANumberIsNotReadAsOne() {
        val en = numbers(Locale.US)
        assertEquals(Reading.Invalid, en.read("12abc"))
        assertEquals(Reading.Invalid, en.read("1.2.3"))
        assertEquals(Reading.Invalid, en.read("--1"))
    }

    @Test
    fun inBetweenStatesAreIncompleteNotWrong() {
        val en = numbers(Locale.US)
        assertEquals(Reading.Empty, en.read(""))
        assertEquals(Reading.Empty, en.read("  "))
        assertEquals(Reading.Incomplete, en.read("-"))
        assertEquals(Reading.Incomplete, en.read("12."))
        assertEquals(Reading.Incomplete, en.read("-0."))
        assertEquals(Reading.Incomplete, en.read("."))
    }

    @Test
    fun theFieldsRulesLimitWhatCanBeTyped() {
        val wholePositive = numbers(Locale.US, fractionDigits = 0, allowNegative = false)
        assertFalse(wholePositive.accepts('.'))
        assertFalse(wholePositive.accepts('-'))
        assertFalse(wholePositive.accepts('a'))
        assertTrue(wholePositive.accepts('7'))
        assertTrue(wholePositive.accepts('٧'))
        assertEquals(Reading.Invalid, wholePositive.read("1.5"))
        assertEquals(Reading.Invalid, wholePositive.read("-1"))
    }

    @Test
    fun settlingRoundsExactlyAndClamps() {
        val en = numbers(Locale.US, fractionDigits = 1)
        assertEquals(BigDecimal("0.2"), en.settle(BigDecimal("0.25"), null)) // half to even
        assertEquals(BigDecimal("0.4"), en.settle(BigDecimal("0.35"), null))
        assertEquals(BigDecimal("100.0"), en.settle(BigDecimal("250"), BigDecimal.ZERO..BigDecimal(100)))
        assertEquals(BigDecimal("0.0"), en.settle(BigDecimal("-3"), BigDecimal.ZERO..BigDecimal(100)))
        // 0.1 + 0.2 is exactly 0.3, as it is not in binary floating point.
        assertEquals(BigDecimal("0.3"), en.settle(BigDecimal("0.1") + BigDecimal("0.2"), null))
    }

    @Test
    fun whatIsWrittenReadsBackInEveryLocale() {
        val value = BigDecimal("-1234567.5")
        for (locale in listOf(Locale.US, Locale.GERMANY, Locale.FRANCE, Locale.forLanguageTag("ar-EG"), Locale.forLanguageTag("hi-IN"))) {
            val n = numbers(locale, fractionDigits = 1)
            val written = n.write(value)
            assertEquals("$locale wrote \"$written\"", value, n.value(written))
        }
    }

    @Test
    fun writingUsesGroupingAndDropsNeedlessDecimals() {
        val en = numbers(Locale.US, fractionDigits = 2)
        assertEquals("1,234.5", en.write(BigDecimal("1234.50")))
        assertEquals("12", en.write(BigDecimal("12.00")))
    }
}
