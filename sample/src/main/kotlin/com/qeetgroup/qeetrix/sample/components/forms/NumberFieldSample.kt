package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import java.math.BigDecimal
import java.util.Locale

/** Numbers typed or stepped: whole numbers in a range, exact decimals with a unit, an amount without steppers. */
@Composable
fun NumberFieldSample() {
    SamplePage {
        SampleSection("Seats") {
            var seats by rememberSaveable { mutableStateOf<Int?>(5) }
            QeetrixNumberField(seats, { seats = it }, label = "Seats", min = 1, max = 250, description = "1 to 250.")
            SampleNote(
                "Value: ${seats ?: "none"}. The steppers turn off at the ends; a number beyond them is brought back " +
                    "when you leave the field. With a keyboard, the arrows step, Page Up and Down step by 10.",
            )
        }
        SampleSection("Discount") {
            var discount by rememberSaveable { mutableStateOf<BigDecimal?>(BigDecimal("12.5")) }
            QeetrixNumberField(
                discount,
                { discount = it },
                label = "Discount",
                endAdornment = { QeetrixText("%") },
                min = BigDecimal.ZERO,
                max = BigDecimal(100),
                step = BigDecimal("0.5"),
                fractionDigits = 1,
            )
            SampleNote("Value: ${discount?.toPlainString() ?: "none"} — exact, never rounded through floating point.")
        }
        SampleSection("Amount, read-only and disabled") {
            var amount by rememberSaveable { mutableStateOf<BigDecimal?>(null) }
            QeetrixNumberField(
                amount,
                { amount = it },
                label = "Amount",
                placeholder = "e.g. 2,500",
                startAdornment = { QeetrixText("₹") },
                steppers = false,
                fractionDigits = 2,
            )
            QeetrixNumberField(30, {}, label = "Session length", endAdornment = { QeetrixText("minutes") }, readOnly = true)
            QeetrixNumberField(30, {}, label = "Retention", endAdornment = { QeetrixText("days") }, enabled = false)
            SampleNote("Nobody steps through an amount, so it has no steppers. The device's locale sets the separators.")
        }
        SampleSection("Money, percentages and locales") {
            var amount by remember { mutableStateOf<BigDecimal?>(BigDecimal("1284500")) }
            QeetrixNumberField(amount, { amount = it }, label = "Invoice total", currency = "INR", steppers = false)
            var rate by remember { mutableStateOf<BigDecimal?>(BigDecimal("0.18")) }
            QeetrixNumberField(rate, { rate = it }, label = "GST rate", percent = true, steppers = false)
            var german by remember { mutableStateOf<BigDecimal?>(BigDecimal("1234.5")) }
            QeetrixNumberField(
                german,
                { german = it },
                label = "Betrag (de-DE)",
                currency = "EUR",
                locale = Locale.GERMANY,
                steppers = false,
            )
            var account by remember { mutableStateOf<BigDecimal?>(BigDecimal("50100")) }
            QeetrixNumberField(account, { account = it }, label = "Branch code", grouping = false, steppers = false)
            SampleNote("The currency and percent signs sit where the locale puts them; a code has no grouping.")
        }
    }
}

@PreviewLightDark
@Composable
private fun NumberFieldSamplePreview() {
    SamplePreview { NumberFieldSample() }
}
