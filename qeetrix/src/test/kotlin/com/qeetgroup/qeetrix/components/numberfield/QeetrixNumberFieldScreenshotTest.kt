package com.qeetgroup.qeetrix.components.numberfield

import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
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
                QeetrixNumberField(1, {}, label = "Seats", range = 1..250, description = "The decrease button stops at 1.")
                QeetrixNumberField(BigDecimal("12345.5"), {}, label = "Budget", fractionDigits = 2)
                QeetrixNumberField(null as Int?, {}, label = "Quantity", placeholder = "0")
                QeetrixNumberField(
                    260,
                    {},
                    label = "Members",
                    message = QeetrixFieldMessage("Your plan includes 250.", QeetrixFieldMessageTone.Warning),
                )
                QeetrixNumberField(5, {}, label = "Read-only", readOnly = true)
                QeetrixNumberField(5, {}, label = "Disabled", enabled = false)
            }
        }
}
