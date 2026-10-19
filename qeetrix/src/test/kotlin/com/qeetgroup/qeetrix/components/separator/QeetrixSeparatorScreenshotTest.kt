package com.qeetgroup.qeetrix.components.separator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotGap
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixSeparator] in `src/test/screenshots/separator/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSeparatorScreenshotTest {
    @Test
    fun states() =
        captureStates("separator") {
            ScreenshotStack {
                QeetrixText("default")
                QeetrixSeparator()
                QeetrixText("muted")
                QeetrixSeparator(variant = QeetrixSeparatorVariant.Muted)
                Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(ScreenshotGap)) {
                    QeetrixText("start")
                    QeetrixSeparator(orientation = QeetrixSeparatorOrientation.Vertical)
                    QeetrixText("end")
                }
            }
        }
}
