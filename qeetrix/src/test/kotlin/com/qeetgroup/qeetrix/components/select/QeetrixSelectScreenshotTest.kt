package com.qeetgroup.qeetrix.components.select

import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixSelect] in `src/test/screenshots/select/`: triggers, and the open list on its own. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSelectScreenshotTest {
    private val countries =
        listOf(
            QeetrixOption("in", "India"),
            QeetrixOption("ae", "United Arab Emirates"),
            QeetrixOption("gb", "United Kingdom of Great Britain and Northern Ireland"),
            QeetrixOption("sg", "Singapore", enabled = false),
        )

    @Test
    fun states() =
        captureStates("select") {
            ScreenshotStack {
                QeetrixSelect("in", {}, countries, label = "Country")
                QeetrixSelect<String?>(
                    null,
                    {},
                    countries,
                    label = "Region",
                    placeholder = "Choose a region",
                    description = "Where your data is stored.",
                )
                QeetrixSelect<String?>(null, {}, countries, label = "Billing country", message = QeetrixFieldMessage("Choose a country."))
                QeetrixSelect("ae", {}, countries, label = "Small, read-only", size = QeetrixSelectSize.Sm, readOnly = true)
                QeetrixSelect("in", {}, countries, label = "Disabled", enabled = false)
                // The open list: the popup's surface, drawn in place.
                OptionListSurface(
                    countries,
                    "ae",
                    focusSelected = false,
                    title = null,
                    onDismiss = {},
                    onChoose = {},
                    modifier = Modifier.width(280.dp),
                )
            }
        }
}
