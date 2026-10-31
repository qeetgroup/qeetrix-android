package com.qeetgroup.qeetrix.foundation.format

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale

/**
 * QeetrixFormat against the web's own results: `format-cases.json` holds what `@qeetrix/react` 0.4.0's format rules
 * write on `Intl` for each case, in five locales (en-IN, hi-IN, en-US, ar-EG, de-DE). Every case must match exactly.
 */
@RunWith(RobolectricTestRunner::class)
class QeetrixFormatTest {
    private val kolkata = ZoneId.of("Asia/Kolkata")

    private val cases: List<JSONObject> by lazy {
        val text = javaClass.classLoader!!.getResource("qeetrix/format-cases.json")!!.readText()
        val array = JSONObject(text).getJSONArray("cases")
        (0 until array.length()).map { array.getJSONObject(it) }
    }

    private fun actual(case: JSONObject): String {
        val locale = Locale.forLanguageTag(case.getString("locale"))
        return when (case.getString("fn")) {
            "number" -> {
                QeetrixFormat.number(case.getDouble("value"), locale)
            }

            "currency" -> {
                QeetrixFormat.currency(case.getDouble("value"), case.getString("currency"), locale)
            }

            "currencyWhole" -> {
                QeetrixFormat.currency(case.getDouble("value"), case.getString("currency"), locale, maximumFractionDigits = 0)
            }

            "percent" -> {
                QeetrixFormat.percent(case.getDouble("value"), locale)
            }

            "relative" -> {
                val now = Instant.parse("2026-10-30T12:00:00Z")
                QeetrixFormat.relativeTime(now.plusMillis(case.getLong("delta")), locale, now)
            }

            "dateInstant" -> {
                QeetrixFormat.date(Instant.ofEpochMilli(case.getLong("epochMillis")), locale, style(case), kolkata)
            }

            "plainDate" -> {
                QeetrixFormat.date(LocalDate.parse(case.getString("date")), locale, style(case))
            }

            "dateTimeInstant" -> {
                val time = if (case.getString("timeStyle") == "medium") QeetrixTimeStyle.Medium else QeetrixTimeStyle.Short
                QeetrixFormat.dateTime(Instant.ofEpochMilli(case.getLong("epochMillis")), locale, style(case), time, kolkata)
            }

            "dateTimeInstantZone" -> {
                QeetrixFormat.dateTime(Instant.ofEpochMilli(case.getLong("epochMillis")), locale, zone = ZoneId.of(case.getString("zone")))
            }

            "duration" -> {
                val style = QeetrixDurationStyle.entries.single { it.value == case.getString("style") }
                QeetrixFormat.duration(Duration.ofSeconds(case.getLong("seconds")), locale, style)
            }

            else -> {
                error("Unknown case ${case.getString("fn")}")
            }
        }
    }

    private fun style(case: JSONObject): QeetrixDateStyle =
        QeetrixDateStyle.entries.single {
            it.value ==
                case.optString("dateStyle", "medium")
        }

    @Test
    fun everyCaseReadsAsTheWebWritesIt() {
        val mismatches =
            cases.mapNotNull { case ->
                val got = actual(case)
                val want = KnownDataDifferences[key(case)]?.first ?: case.getString("out")
                if (got == want) null else "$case\n    Android: \"$got\""
            }
        assertEquals("${mismatches.size} of ${cases.size} cases differ:\n" + mismatches.joinToString("\n"), 0, mismatches.size)
        // Each known difference must still happen, so one that newer ICU data fixes is noticed and removed.
        for ((key, known) in KnownDataDifferences) {
            val case = cases.single { key(it) == key }
            assertEquals("$key: ${known.second}", false, actual(case) == case.getString("out"))
        }
    }

    private fun key(case: JSONObject): String = listOf("fn", "locale", "style", "seconds").joinToString("|") { case.optString(it) }

    private companion object {
        /**
         * Cases where Android's ICU data (Robolectric's, as API 36 ships it) and the web's (ICU 78.2, CLDR 48) write
         * the same rule differently: what Android writes, and why. Not rules Qeetrix can change.
         */
        val KnownDataDifferences: Map<String, Pair<String, String>> =
            mapOf(
                "duration|de-DE|narrow|3900" to
                    ("1 Std., 5 Min." to "German narrow hours are \"h\" in CLDR 48, \"Std.\" in Android's older data"),
                "duration|de-DE|narrow|90061" to
                    ("1 T, 1 Std., 1 Min. und 1 Sek." to "German narrow hours are \"h\" in CLDR 48, \"Std.\" in Android's older data"),
            )
    }

    /** The web's documented examples (format.d.ts), so the rules read plainly here too. */
    @Test
    fun theWebsExamples() {
        val india = Locale.forLanguageTag("en-IN")
        assertEquals("12,34,567", QeetrixFormat.number(1234567, india))
        assertEquals("₹1,24,500.00", QeetrixFormat.currency(124500, "INR", india))
        assertEquals("₹1,24,500", QeetrixFormat.currency(124500, "INR", india, maximumFractionDigits = 0))
        val now = Instant.parse("2026-10-30T12:00:00Z")
        assertEquals("1 hour ago", QeetrixFormat.relativeTime(now.minusSeconds(59 * 60 + 40), india, now))
        assertEquals("last year", QeetrixFormat.relativeTime(now.minus(Duration.ofDays(364)), india, now))
        assertEquals("25 Oct 2026", QeetrixFormat.date(LocalDate.of(2026, 10, 25), india))
        assertEquals("25 Oct 2026", QeetrixFormat.dateTime(LocalDateTime.of(2026, 10, 25, 14, 30), india).substringBefore(","))
    }

    /** A calendar date is never moved by a zone: 25 October is the 25th in Kolkata, New York and Auckland alike. */
    @Test
    fun aCalendarDateStaysOnItsDay() {
        val india = Locale.forLanguageTag("en-IN")
        val written = QeetrixFormat.date(LocalDate.of(2026, 10, 25), india)
        for (zone in listOf("Asia/Kolkata", "America/New_York", "Pacific/Auckland")) {
            val previous = java.util.TimeZone.getDefault()
            try {
                java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone(zone))
                assertEquals(zone, written, QeetrixFormat.date(LocalDate.of(2026, 10, 25), india))
            } finally {
                java.util.TimeZone.setDefault(previous)
            }
        }
    }

    /** Durations, which the web has no formatter for yet: the non-zero units, largest first, as Intl.DurationFormat lists them. */
    @Test
    fun durationsListTheirNonZeroUnits() {
        val india = Locale.forLanguageTag("en-IN")
        assertEquals("1 hour, 5 minutes", QeetrixFormat.duration(Duration.ofMinutes(65), india))
        assertEquals("2 days, 3 seconds", QeetrixFormat.duration(Duration.ofDays(2).plusSeconds(3), india))
        assertEquals("0 seconds", QeetrixFormat.duration(Duration.ofMillis(400), india))
        assertEquals("1 hr, 5 mins", QeetrixFormat.duration(Duration.ofMinutes(65), india, QeetrixDurationStyle.Short))
    }

    /** Android's ICU data puts a narrow no-break space before "pm"; the web's writes a space, and Qeetrix writes the web's. */
    @Test
    fun timesUseTheWebsSpaceBeforeTheDayPeriod() {
        val written = QeetrixFormat.dateTime(LocalDateTime.of(2026, 10, 25, 14, 30), Locale.forLanguageTag("en-US"))
        assertEquals("Oct 25, 2026, 2:30 PM", written)
    }
}
