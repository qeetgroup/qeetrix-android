package com.qeetgroup.qeetrix.sample.components.datetime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.datepicker.QeetrixDateField
import com.qeetgroup.qeetrix.components.datepicker.QeetrixDateRangeField
import com.qeetgroup.qeetrix.components.datepicker.QeetrixTimeField
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/** Date, date range and time fields: a date with limits, weekdays only, a range, a time in 15-minute steps, and states. */
@Composable
fun DateFieldSample() {
    val today = remember { LocalDate.now() }
    SamplePage {
        SampleSection("A date") {
            var date by rememberSaveable { mutableStateOf<LocalDate?>(null) }
            QeetrixDateField(
                date,
                { date = it },
                label = "Invoice date",
                description = "Within the last 90 days.",
                min = today.minusDays(90),
                max = today,
            )
            SampleNote(
                "Tap the field for the calendar; days outside the limits can't be chosen. The date is written in the device's locale.",
            )
        }
        SampleSection("Weekdays only") {
            var date by rememberSaveable { mutableStateOf<LocalDate?>(today.plusDays(7)) }
            QeetrixDateField(
                date,
                { date = it },
                label = "Payout day",
                min = today,
                isDateAllowed = { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY },
            )
        }
        SampleSection("A range") {
            var range by remember { mutableStateOf<ClosedRange<LocalDate>?>(today.minusDays(6)..today) }
            QeetrixDateRangeField(range, { range = it }, label = "Report period", max = today)
        }
        SampleSection("A time") {
            var time by rememberSaveable { mutableStateOf<LocalTime?>(LocalTime.of(9, 30)) }
            QeetrixTimeField(time, { time = it }, label = "Daily digest at", minuteStep = 15, description = "In 15-minute steps.")
            SampleNote("12- or 24-hour, as the device is set.")
        }
        SampleSection("States") {
            QeetrixDateField(null, {}, label = "Start date", placeholder = "Choose a date", required = true, error = "Choose a start date.")
            QeetrixDateField(today, {}, label = "Created", enabled = false)
        }
    }
}

@PreviewLightDark
@Composable
private fun DateFieldSamplePreview() {
    SamplePreview { DateFieldSample() }
}
