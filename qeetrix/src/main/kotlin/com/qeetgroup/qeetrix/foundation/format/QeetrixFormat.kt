package com.qeetgroup.qeetrix.foundation.format

import android.icu.text.DateFormat
import android.icu.text.MeasureFormat
import android.icu.text.NumberFormat
import android.icu.text.RelativeDateTimeFormatter
import android.icu.util.Currency
import android.icu.util.GregorianCalendar
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import android.icu.util.TimeZone
import android.icu.util.ULocale
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sign

/** How much of a date [QeetrixFormat] writes (`value` is `@qeetrix/react`'s `dateStyle`); the examples are `en-IN`. */
public enum class QeetrixDateStyle(
    public val value: String,
) {
    /** "25/10/26". */
    Short("short"),

    /** "25 Oct 2026". The default. */
    Medium("medium"),

    /** "25 October 2026". */
    Long("long"),
}

/** How much of a time [QeetrixFormat] writes (`value` is `@qeetrix/react`'s `timeStyle`); the examples are `en-IN`. */
public enum class QeetrixTimeStyle(
    public val value: String,
) {
    /** "2:30 pm". The default. */
    Short("short"),

    /** "2:30:45 pm". */
    Medium("medium"),
}

/** How [QeetrixFormat.duration] writes each unit; the examples are `en-IN`. */
public enum class QeetrixDurationStyle(
    public val value: String,
) {
    /** "1 hour, 5 minutes". The default. */
    Long("long"),

    /** "1 hr, 5 min". */
    Short("short"),

    /** "1h 5m". */
    Narrow("narrow"),
}

/**
 * Qeetrix's formatting — `@qeetrix/react`'s `format` utilities on Android's own ICU (`android.icu`), so a number, an
 * amount, a date or "2 minutes ago" reads the same in an app as on the web for the same locale: `en-IN` groups in
 * lakhs and crores ("12,34,567") and writes the rupee as ₹. The web's results are the tests' expected values.
 *
 * - [number], [percent] and [currency] are `formatNumber` and `formatCurrency` (`Intl.NumberFormat`): the locale's
 *   digits, grouping and signs, at most three fraction digits for a plain number, the currency's own digits for an
 *   amount (two for INR), and ties rounded away from zero as `Intl` does.
 * - [relativeTime] is `formatRelativeTime`: the largest whole unit, rounded first and then chosen, so 59 minutes 40
 *   seconds is "1 hour ago" and 364 days "last year"; yesterday, tomorrow, last week and now in words.
 * - [date] and [dateTime] are `formatDate` and `formatDateTime`: always the Gregorian calendar. A [LocalDate] is a
 *   calendar date and a [LocalDateTime] a wall-clock time, so no time zone moves them; an [Instant] is shown in a
 *   zone, the device's by default (the web fixes `Asia/Kolkata` so its server and client agree; a phone has one clock).
 * - [duration] writes a length of time ("1 hour, 5 minutes"), as `Intl.DurationFormat` does; the web has no
 *   wrapper for it yet.
 * - Times put a plain space before the day period ("2:30 pm"), as the web's current ICU data does, where Android's
 *   own data has a narrow no-break space.
 *
 * Every function takes the locale to write in, the device's by default; in a composable, pass
 * `LocalConfiguration.current.locales[0]` so the text follows a change of language. [QeetrixRelativeTime][com.qeetgroup.qeetrix.components.relativetime.QeetrixRelativeTime]
 * keeps a relative time current on screen.
 *
 * ```
 * QeetrixFormat.number(1234567, Locale.forLanguageTag("en-IN"))        // "12,34,567"
 * QeetrixFormat.currency(124500, "INR", Locale.forLanguageTag("en-IN")) // "₹1,24,500.00"
 * QeetrixFormat.relativeTime(sentAt)                                     // "2 minutes ago"
 * QeetrixFormat.date(LocalDate.of(2026, 10, 25))                         // "25 Oct 2026"
 * ```
 */
public object QeetrixFormat {
    /**
     * A number in the locale's digits and grouping — `formatNumber`. Without [maximumFractionDigits], at most three
     * fraction digits are written, as `Intl.NumberFormat` does; [grouping] `false` drops the group separators.
     */
    public fun number(
        value: Number,
        locale: Locale = Locale.getDefault(),
        minimumFractionDigits: Int? = null,
        maximumFractionDigits: Int? = null,
        grouping: Boolean = true,
    ): String = format(NumberFormat.getInstance(ULocale.forLocale(locale)), value, minimumFractionDigits, maximumFractionDigits, grouping)

    /** A share as a percentage, `value` 0.25 being "25%" — `formatNumber` with `style: "percent"`: no fraction digits by default. */
    public fun percent(
        value: Number,
        locale: Locale = Locale.getDefault(),
        minimumFractionDigits: Int? = null,
        maximumFractionDigits: Int? = null,
    ): String =
        format(NumberFormat.getPercentInstance(ULocale.forLocale(locale)), value, minimumFractionDigits, maximumFractionDigits, true)

    /**
     * An amount of money in [currency] (an ISO 4217 code: "INR", "USD") — `formatCurrency`: "₹1,24,500.00" in `en-IN`.
     * The currency's own fraction digits by default; whole amounts can drop them with `maximumFractionDigits = 0`.
     */
    public fun currency(
        value: Number,
        currency: String,
        locale: Locale = Locale.getDefault(),
        minimumFractionDigits: Int? = null,
        maximumFractionDigits: Int? = null,
    ): String {
        val formatter = NumberFormat.getCurrencyInstance(ULocale.forLocale(locale))
        formatter.currency = Currency.getInstance(currency.uppercase(Locale.ROOT))
        val digits = formatter.currency.defaultFractionDigits
        formatter.minimumFractionDigits = digits
        formatter.maximumFractionDigits = digits
        return format(formatter, value, minimumFractionDigits, maximumFractionDigits, true)
    }

    /**
     * How long ago or how soon [moment] is from [now], in the largest whole unit — `formatRelativeTime`: "now",
     * "45 seconds ago", "in 3 days", "yesterday", "last week", "2 months ago". It rounds first and then picks the unit
     * (a month is 30 days, a year 365), so 59 minutes 40 seconds is "1 hour ago", not "60 minutes ago".
     */
    public fun relativeTime(
        moment: Instant,
        locale: Locale = Locale.getDefault(),
        now: Instant = Instant.now(),
    ): String {
        val delta = (moment.toEpochMilli() - now.toEpochMilli()).toDouble()
        val formatter = RelativeDateTimeFormatter.getInstance(ULocale.forLocale(locale))
        for ((unit, size, next) in RelativeUnits) {
            val amount = (abs(delta) / size).roundHalfAwayFromZero()
            if (amount < next) return formatter.format(if (amount == 0.0) 0.0 else sign(delta) * amount, unit)
        }
        return formatter.format(0.0, RelativeDateTimeFormatter.RelativeDateTimeUnit.SECOND)
    }

    /** A calendar date in the locale's words — `formatDate("2026-10-25")`: "25 Oct 2026" in `en-IN`. No zone moves it. */
    public fun date(
        date: LocalDate,
        locale: Locale = Locale.getDefault(),
        style: QeetrixDateStyle = QeetrixDateStyle.Medium,
    ): String = moment(date.atStartOfDay(ZoneOffset.UTC).toInstant(), UTC, locale, style, timeStyle = null)

    /** The date [moment] falls on in [zone] — `formatDate` of an instant. */
    public fun date(
        moment: Instant,
        locale: Locale = Locale.getDefault(),
        style: QeetrixDateStyle = QeetrixDateStyle.Medium,
        zone: ZoneId = ZoneId.systemDefault(),
    ): String = moment(moment, zone, locale, style, timeStyle = null)

    /** [moment]'s date and time in [zone] — `formatDateTime`: "25 Oct 2026, 2:30 pm" in `en-IN`. */
    public fun dateTime(
        moment: Instant,
        locale: Locale = Locale.getDefault(),
        dateStyle: QeetrixDateStyle = QeetrixDateStyle.Medium,
        timeStyle: QeetrixTimeStyle = QeetrixTimeStyle.Short,
        zone: ZoneId = ZoneId.systemDefault(),
    ): String = moment(moment, zone, locale, dateStyle, timeStyle)

    /** A wall-clock date and time with no zone, shown as written — `formatDateTime("2026-10-25T14:30")`. */
    public fun dateTime(
        dateTime: LocalDateTime,
        locale: Locale = Locale.getDefault(),
        dateStyle: QeetrixDateStyle = QeetrixDateStyle.Medium,
        timeStyle: QeetrixTimeStyle = QeetrixTimeStyle.Short,
    ): String = moment(dateTime.toInstant(ZoneOffset.UTC), UTC, locale, dateStyle, timeStyle)

    /**
     * A length of time in its non-zero units, largest first — days, hours, minutes, seconds — joined as the locale
     * lists them: "1 hour, 5 minutes" in `en-IN`. Under a second, "0 seconds". Negative durations are written as
     * their length.
     */
    public fun duration(
        duration: Duration,
        locale: Locale = Locale.getDefault(),
        style: QeetrixDurationStyle = QeetrixDurationStyle.Long,
    ): String {
        val length = duration.abs()
        val parts =
            listOf(
                length.toDays() to MeasureUnit.DAY,
                length.toHours() % 24 to MeasureUnit.HOUR,
                length.toMinutes() % 60 to MeasureUnit.MINUTE,
                length.seconds % 60 to MeasureUnit.SECOND,
            ).filter { it.first > 0 }.map { Measure(it.first, it.second) }
        val width =
            when (style) {
                QeetrixDurationStyle.Long -> MeasureFormat.FormatWidth.WIDE
                QeetrixDurationStyle.Short -> MeasureFormat.FormatWidth.SHORT
                QeetrixDurationStyle.Narrow -> MeasureFormat.FormatWidth.NARROW
            }
        val formatter = MeasureFormat.getInstance(ULocale.forLocale(locale), width)
        return if (parts.isEmpty()) formatter.format(Measure(0, MeasureUnit.SECOND)) else formatter.formatMeasures(*parts.toTypedArray())
    }

    private fun format(
        formatter: NumberFormat,
        value: Number,
        minimumFractionDigits: Int?,
        maximumFractionDigits: Int?,
        grouping: Boolean,
    ): String {
        // Intl's roundingMode is "halfExpand": ties away from zero (ICU's default is half-even).
        formatter.roundingMode = android.icu.math.BigDecimal.ROUND_HALF_UP
        formatter.isGroupingUsed = grouping
        // As Intl resolves them: an explicit minimum above the default maximum raises it, and the other way round.
        if (minimumFractionDigits != null) {
            formatter.minimumFractionDigits = minimumFractionDigits
            if (maximumFractionDigits == null && formatter.maximumFractionDigits < minimumFractionDigits) {
                formatter.maximumFractionDigits = minimumFractionDigits
            }
        }
        if (maximumFractionDigits != null) {
            formatter.maximumFractionDigits = maximumFractionDigits
            if (minimumFractionDigits == null && formatter.minimumFractionDigits > maximumFractionDigits) {
                formatter.minimumFractionDigits = maximumFractionDigits
            }
        }
        return formatter.format(value)
    }

    private fun moment(
        instant: Instant,
        zone: ZoneId,
        locale: Locale,
        dateStyle: QeetrixDateStyle,
        timeStyle: QeetrixTimeStyle?,
    ): String {
        val uLocale = ULocale.forLocale(locale)
        val date = dateStyle.icu()
        val formatter =
            if (timeStyle == null) {
                DateFormat.getDateInstance(date, uLocale)
            } else {
                DateFormat.getDateTimeInstance(
                    date,
                    if (timeStyle ==
                        QeetrixTimeStyle.Short
                    ) {
                        DateFormat.SHORT
                    } else {
                        DateFormat.MEDIUM
                    },
                    uLocale,
                )
            }
        val timeZone = TimeZone.getTimeZone(zone.id)
        // calendar: "gregory", whatever the locale's own calendar.
        formatter.calendar = GregorianCalendar(timeZone, uLocale)
        formatter.timeZone = timeZone
        // Android's ICU data (CLDR 42–47) writes a narrow no-break space before the day period ("2:30 pm"); the web's
        // current data writes a space. Qeetrix writes the web's text, so the two read alike byte for byte.
        return formatter.format(java.util.Date(instant.toEpochMilli())).replace(NARROW_NO_BREAK_SPACE, ' ')
    }

    private fun QeetrixDateStyle.icu(): Int =
        when (this) {
            QeetrixDateStyle.Short -> DateFormat.SHORT
            QeetrixDateStyle.Medium -> DateFormat.MEDIUM
            QeetrixDateStyle.Long -> DateFormat.LONG
        }

    /** `Math.round` of the web, for the non-negative amounts it rounds: halves go up. */
    private fun Double.roundHalfAwayFromZero(): Double = (this + 0.5).let { kotlin.math.floor(it) }

    private val UTC: ZoneId = ZoneOffset.UTC

    private const val NARROW_NO_BREAK_SPACE = '\u202F'

    private const val SECOND = 1_000.0
    private const val MINUTE = 60 * SECOND
    private const val HOUR = 60 * MINUTE
    private const val DAY = 24 * HOUR

    /** Each unit, its length, and how many of it make the next: a month is 30 days, so 4 weeks are not yet a month. */
    private val RelativeUnits =
        listOf(
            Triple(RelativeDateTimeFormatter.RelativeDateTimeUnit.SECOND, SECOND, 60.0),
            Triple(RelativeDateTimeFormatter.RelativeDateTimeUnit.MINUTE, MINUTE, 60.0),
            Triple(RelativeDateTimeFormatter.RelativeDateTimeUnit.HOUR, HOUR, 24.0),
            Triple(RelativeDateTimeFormatter.RelativeDateTimeUnit.DAY, DAY, 7.0),
            Triple(RelativeDateTimeFormatter.RelativeDateTimeUnit.WEEK, 7 * DAY, 30.0 / 7),
            Triple(RelativeDateTimeFormatter.RelativeDateTimeUnit.MONTH, 30 * DAY, 12.0),
            Triple(RelativeDateTimeFormatter.RelativeDateTimeUnit.YEAR, 365 * DAY, Double.POSITIVE_INFINITY),
        )
}
