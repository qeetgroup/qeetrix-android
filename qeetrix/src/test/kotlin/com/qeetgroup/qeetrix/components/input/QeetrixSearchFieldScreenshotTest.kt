package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.text.input.TextFieldState
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixSearchField] in `src/test/screenshots/searchfield/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSearchFieldScreenshotTest {
    @Test
    fun states() =
        captureStates("searchfield") {
            ScreenshotStack {
                QeetrixSearchField(TextFieldState(), placeholder = "e.g. Asha")
                QeetrixSearchField(TextFieldState("grace"))
                QeetrixSearchField(TextFieldState("pay"), label = "Find an invoice", interactionSource = rememberFocusedSource())
                QeetrixSearchField(TextFieldState("ada"), readOnly = true)
                QeetrixSearchField(TextFieldState("ada"), enabled = false)
            }
        }
}
