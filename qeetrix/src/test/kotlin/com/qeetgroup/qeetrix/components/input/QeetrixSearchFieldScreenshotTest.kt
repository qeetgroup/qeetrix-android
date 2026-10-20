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

/** Baselines in `src/test/screenshots/searchfield/`: the placeholder, a query with its clear button, a label, disabled. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSearchFieldScreenshotTest {
    @Test
    fun states() =
        captureStates("searchfield") {
            ScreenshotStack {
                QeetrixSearchField(TextFieldState())
                QeetrixSearchField(TextFieldState("Ada"))
                QeetrixSearchField(TextFieldState(), label = "Find a member", placeholder = "Name or email")
                QeetrixSearchField(TextFieldState("Grace"), enabled = false)
            }
        }
}
