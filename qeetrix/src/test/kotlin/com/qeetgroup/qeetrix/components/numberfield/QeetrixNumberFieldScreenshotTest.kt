package com.qeetgroup.qeetrix.components.numberfield

import com.qeetgroup.qeetrix.components.input.rememberFocusedSource
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.math.BigDecimal

/** Baselines for [QeetrixNumberField] in `src/test/screenshots/numberfield/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixNumberFieldScreenshotTest {
    @Test
    fun states() =
        captureStates("numberfield") {
            ScreenshotStack {
                QeetrixNumberField(5, {}, label = "Seats", min = 1, max = 250)
                QeetrixNumberField(1, {}, label = "At the minimum", min = 1, max = 250, description = "Decrease is off at the minimum.")
                QeetrixNumberField(
                    BigDecimal("1250000"),
                    {},
                    label = "Amount",
                    startAdornment = { QeetrixText("₹") },
                    steppers = false,
                    fractionDigits = 2,
                )
                QeetrixNumberField(
                    BigDecimal("12.5"),
                    {},
                    label = "Discount",
                    endAdornment = { QeetrixText("%") },
                    fractionDigits = 1,
                    step = BigDecimal("0.5"),
                    interactionSource = rememberFocusedSource(),
                )
                QeetrixNumberField(
                    value = null as Int?,
                    onValueChange = {},
                    label = "Session length",
                    placeholder = "e.g. 30",
                    endAdornment = { QeetrixText("minutes") },
                    error = "Enter a length between 5 and 120 minutes.",
                )
                QeetrixNumberField(30, {}, label = "Read-only", readOnly = true)
                QeetrixNumberField(30, {}, label = "Disabled", enabled = false)
            }
        }
}
