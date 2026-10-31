package com.qeetgroup.qeetrix.components.select

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.rememberFocusedSource
import com.qeetgroup.qeetrix.components.input.rememberHoveredSource
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixSelect] in `src/test/screenshots/select/`: triggers in every state, and the open list on its own. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSelectScreenshotTest {
    private val countries =
        listOf(
            QeetrixOption("in", "India", meta = "412"),
            QeetrixOption("ae", "United Arab Emirates", meta = "38"),
            QeetrixOption("gb", "United Kingdom of Great Britain and Northern Ireland"),
            QeetrixOption("sg", "Singapore", enabled = false),
        )

    @Test
    fun states() =
        captureStates("select") {
            ScreenshotStack {
                QeetrixSelect("in", {}, countries, label = "Country", interactionSource = rememberHoveredSource())
                QeetrixSelect<String?>(
                    null,
                    {},
                    countries,
                    label = "Region",
                    placeholder = "e.g. India",
                    description = "Where your data is stored.",
                )
                QeetrixSelect("gb", {}, countries, label = "Focused", interactionSource = rememberFocusedSource())
                QeetrixSelect<String?>(null, {}, countries, label = "Billing country", error = "Choose a billing country.")
                QeetrixSelect("ae", {}, countries, label = "Read-only", readOnly = true)
                QeetrixSelect("in", {}, countries, label = "Disabled", enabled = false)
                ScreenshotFlow {
                    QeetrixSelect(
                        "in",
                        {},
                        countries,
                        label = "Md",
                        size = QeetrixSelectSize.Md,
                        hideLabel = true,
                        modifier = Modifier.width(140.dp),
                    )
                    QeetrixSelect(
                        "ae",
                        {},
                        countries,
                        label = "Sm",
                        size = QeetrixSelectSize.Sm,
                        hideLabel = true,
                        modifier = Modifier.width(140.dp),
                    )
                }
                // The open list: the popup's surface, drawn in place, with group labels.
                OptionListSurface(
                    listOf(SelectSection("Asia", countries.take(2) + countries.last()), SelectSection("Europe", listOf(countries[2]))),
                    "ae",
                    focusSelected = false,
                    title = "Country",
                    onDismiss = {},
                    onChoose = {},
                    modifier = Modifier.width(280.dp),
                )
            }
        }
}
