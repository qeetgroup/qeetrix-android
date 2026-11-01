package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixField] and [QeetrixFieldSet] in `src/test/screenshots/field/`: label, help, messages, the set. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixFieldScreenshotTest {
    @Test
    fun states() =
        captureStates("field") {
            ScreenshotStack {
                QeetrixField(
                    label = "Brand colour",
                    description = "Used on receipts.",
                    help = "Shown on invoices and emails.",
                ) { Swatches() }
                QeetrixField(
                    label = "Accent colour",
                    description = "Hidden while the error shows.",
                    error = "Choose an accent colour.",
                ) { Swatches() }
                QeetrixField(
                    label = "Logo",
                    optional = true,
                    description = "Disabled until the plan allows it.",
                    enabled = false,
                ) { Swatches() }
                QeetrixFieldSet("Billing address", description = "Where we send invoices.") {
                    QeetrixTextField(TextFieldState("12 MG Road"), label = "Address line 1")
                    QeetrixTextField(TextFieldState(), label = "Address line 2", optional = true)
                }
                QeetrixFieldSet("Disabled section", enabled = false) {
                    QeetrixTextField(TextFieldState("Bengaluru"), label = "City")
                }
            }
        }

    @Composable
    private fun Swatches() {
        val colors =
            listOf(QeetrixTheme.colors.bgBrandBold, QeetrixTheme.colors.bgNeutralBold, QeetrixTheme.colors.bgInfoBold, Color.Transparent)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (color in colors) Box(Modifier.size(28.dp).background(color, QeetrixShapes.Control))
        }
    }
}
