package com.qeetgroup.qeetrix.components.numberfield

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

/**
 * Reading and writing the numbers a [QeetrixNumberField] edits, in one locale: its decimal separator, its
 * grouping and its digits. Values are [BigDecimal], so a decimal is exact and never passes through binary
 * floating point.
 *
 * Reading is strict where a lenient parser would guess: text that is not a number is not read as one
 * ("12abc" is not 12), and a grouping separator counts only where the locale puts one — in German "1.234"
 * is 1234 but "1234.5" is not a number, rather than 12345. Digits from any script are read, so Arabic-Indic
 * or Devanagari digits typed on another keyboard are numbers too.
 */
internal class NumberText(
    locale: Locale,
    private val fractionDigits: Int,
    private val allowNegative: Boolean,
) {
    private val symbols = DecimalFormatSymbols.getInstance(locale)
    private val decimal = symbols.decimalSeparator
    private val groupings: Set<Char> =
        symbols.groupingSeparator.let { if (it.isWhitespace() || it in SPACES) SPACES + it else setOf(it) }
    private val minusSigns = setOf('-', symbols.minusSign, '−')
    private val format =
        (NumberFormat.getNumberInstance(locale) as DecimalFormat).apply {
            isGroupingUsed = true
            minimumFractionDigits = 0
            maximumFractionDigits = fractionDigits
            roundingMode = RoundingMode.HALF_EVEN
        }

    /** What some text reads as. */
    sealed interface Reading {
        /** No text: no value. */
        data object Empty : Reading

        /** On the way to a number — a lone minus, a trailing separator: kept while typing, not a value. */
        data object Incomplete : Reading

        /** A number. */
        class Number(
            val value: BigDecimal,
        ) : Reading

        /** Not a number in this locale. */
        data object Invalid : Reading
    }

    /** Whether [char] can be typed into the field at all; anything else is refused as it is entered. */
    fun accepts(char: Char): Boolean =
        Character.isDigit(char) ||
            isFormatMark(char) ||
            (fractionDigits > 0 && char == decimal) ||
            char in groupings ||
            (allowNegative && char in minusSigns)

    fun read(text: CharSequence): Reading {
        // Bidi marks the locale writes around a number (Arabic puts one before the minus sign) are not part of it.
        val trimmed = text.filterNot(::isFormatMark).toString().trim()
        if (trimmed.isEmpty()) return Reading.Empty
        val negative = trimmed.first() in minusSigns
        val body = if (negative) trimmed.substring(1) else trimmed
        if (negative && !allowNegative) return Reading.Invalid
        if (body.isEmpty()) return Reading.Incomplete
        val parts = body.split(decimal)
        if (parts.size > 2 || (parts.size == 2 && fractionDigits == 0)) return Reading.Invalid
        val whole = parts[0]
        val fraction = parts.getOrNull(1)
        if (fraction != null && !fraction.all(Character::isDigit)) return Reading.Invalid
        if (whole.any { !Character.isDigit(it) && it !in groupings }) return Reading.Invalid
        val wholeDigits = whole.filter(Character::isDigit).map { Character.digit(it, 10) }.joinToString("")
        if (wholeDigits.length != whole.length && !isGroupedAsTheLocaleGroups(whole, wholeDigits)) return Reading.Invalid
        if (wholeDigits.isEmpty() && fraction.isNullOrEmpty()) return Reading.Incomplete
        if (fraction != null && fraction.isEmpty()) return Reading.Incomplete // "12," — the decimals are coming
        val fractionDigitsText = fraction?.map { Character.digit(it, 10) }?.joinToString("").orEmpty()
        val plain =
            (if (negative) "-" else "") + wholeDigits.ifEmpty { "0" } + (if (fractionDigitsText.isEmpty()) "" else ".$fractionDigitsText")
        return Reading.Number(BigDecimal(plain))
    }

    /**
     * Whether [text] stands for [value] in this locale: it reads as that number (or is empty for no value), or it
     * is a number still being typed.
     */
    fun reads(
        text: CharSequence,
        value: BigDecimal?,
    ): Boolean =
        when (val reading = read(text)) {
            Reading.Empty -> value == null
            Reading.Incomplete -> true
            Reading.Invalid -> false
            is Reading.Number -> value != null && reading.value.compareTo(value) == 0
        }

    /** [value] as the locale writes it: its digits, its grouping, at most [fractionDigits] decimals. */
    fun write(value: BigDecimal): String = format.format(value.setScale(fractionDigits, RoundingMode.HALF_EVEN).stripTrailingZeros())

    /** Rounds [value] to [fractionDigits] and brings it into [range]. */
    fun settle(
        value: BigDecimal,
        range: ClosedRange<BigDecimal>?,
    ): BigDecimal {
        val inRange = if (range == null) value else value.max(range.start).min(range.endInclusive)
        return inRange.setScale(fractionDigits, RoundingMode.HALF_EVEN)
    }

    /** True when the separators in [whole] sit exactly where the locale's own formatting puts them. */
    private fun isGroupedAsTheLocaleGroups(
        whole: String,
        digits: String,
    ): Boolean {
        if (digits.isEmpty()) return false
        val expected = format.format(BigDecimal(digits)).filter { !Character.isDigit(it) }.map { if (it in groupings) '|' else it }
        val written = whole.filter { !Character.isDigit(it) }.map { if (it in groupings) '|' else it }
        if (expected != written) return false
        // Same separators; now the same positions: compare the lengths of the digit runs.
        val expectedRuns = format.format(BigDecimal(digits)).split(*groupings.toCharArray()).map { it.length }
        val writtenRuns = whole.split(*groupings.toCharArray()).map { it.length }
        return expectedRuns == writtenRuns
    }

    /** An invisible formatting character (Unicode category Cf), such as the Arabic letter mark. */
    private fun isFormatMark(char: Char): Boolean = Character.getType(char) == Character.FORMAT.toInt()

    private companion object {
        /** The spaces locales group with (French uses a narrow no-break space); any of them is read as grouping. */
        val SPACES = setOf(' ', ' ', ' ')
    }
}
