package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import java.math.BigDecimal

/** Numbers: whole and decimal, ranges corrected on commit, steppers, and the device's locale. */
@Composable
fun NumberFieldSample() {
    SamplePage {
        SampleSection("Whole numbers") {
            var seats by rememberSaveable { mutableStateOf<Int?>(5) }
            QeetrixNumberField(
                seats,
                { seats = it },
                label = "Seats",
                range = 1..250,
                imeAction = ImeAction.Done,
                message = if ((seats ?: 0) > 50) SeatsWarning else null,
            )
            SampleNote("Value: ${seats ?: "none"}. Type 300 and press Done: it settles at 250. The steppers stop at the ends.")
        }
        SampleSection("Exact decimals") {
            var discount by rememberSaveable { mutableStateOf<BigDecimal?>(BigDecimal("12.5")) }
            QeetrixNumberField(
                discount,
                { discount = it },
                label = "Discount (%)",
                range = BigDecimal.ZERO..BigDecimal(100),
                step = BigDecimal("0.5"),
                fractionDigits = 1,
                imeAction = ImeAction.Done,
            )
            SampleNote(
                "Value: ${discount?.toPlainString() ?: "none"} — a BigDecimal, never a float. Type \"12.\" and it waits for " +
                    "the decimals. The separator, grouping and digits follow the device's language.",
            )
        }
        SampleSection("States") {
            QeetrixNumberField(null as Int?, {}, label = "Quantity", placeholder = "0")
            QeetrixNumberField(42, {}, label = "Read-only", readOnly = true)
            QeetrixNumberField(42, {}, label = "Disabled", enabled = false)
        }
    }
}

private val SeatsWarning = QeetrixFieldMessage("Over 50 seats needs the Team plan.", QeetrixFieldMessageTone.Warning)

@PreviewLightDark
@Composable
private fun NumberFieldSamplePreview() {
    SamplePreview { NumberFieldSample() }
}

@Preview(name = "German", locale = "de")
@Composable
private fun NumberFieldGermanPreview() {
    SamplePreview { QeetrixNumberField(BigDecimal("1234.5"), {}, label = "Budget", fractionDigits = 2) }
}
