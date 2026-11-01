package com.qeetgroup.qeetrix.components.radiogroup

import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixRadioGroup] in `src/test/screenshots/radiogroup/`: rows, a wrapping row, an error, tiles. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixRadioGroupScreenshotTest {
    private val cycles =
        listOf(
            QeetrixOption("monthly", "Monthly"),
            QeetrixOption("yearly", "Yearly", description = "Two months free, billed once a year."),
            QeetrixOption("custom", "Custom", enabled = false),
        )

    @Test
    fun states() =
        captureStates("radiogroup") {
            ScreenshotStack {
                QeetrixRadioGroup("yearly", {}, cycles, label = "Billing cycle", description = "You can change it later.")
                QeetrixRadioGroup<String?>(null, {}, cycles.take(2), label = "Invoice language", error = "Choose a language for invoices.")
                // A chosen option in a group with an error: its ring is the danger colour (radio.css 0.4.0).
                QeetrixRadioGroup("monthly", {}, cycles.take(2), label = "Payout cycle", error = "This cycle isn't available on your plan.")
                QeetrixRadioGroup(
                    "s",
                    {},
                    listOf(
                        QeetrixOption("s", "Small"),
                        QeetrixOption("m", "Medium"),
                        QeetrixOption("l", "Large"),
                        QeetrixOption("xl", "Extra large"),
                    ),
                    label = "Size",
                    orientation = QeetrixChoiceOrientation.Horizontal,
                )
                QeetrixRadioGroup(
                    "web",
                    {},
                    listOf(
                        QeetrixOption("web", "Web app", "Users sign in through a redirect.", icon = QeetrixIconName.House),
                        QeetrixOption("service", "Service", "Machine to machine, no user.", icon = QeetrixIconName.Server),
                        QeetrixOption(
                            "native",
                            "Native app with a longer title that wraps",
                            "On phones and desktops.",
                            icon = QeetrixIconName.Settings,
                        ),
                        QeetrixOption("legacy", "Legacy", "No longer offered.", enabled = false),
                    ),
                    label = "Application type",
                    variant = QeetrixChoiceVariant.Tile,
                    columns = 2,
                )
                QeetrixRadioGroup("monthly", {}, cycles.take(2), label = "Disabled", enabled = false)
            }
        }
}
