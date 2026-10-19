package com.qeetgroup.qeetrix.components.badge

import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixBadge] in `src/test/screenshots/badge/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixBadgeScreenshotTest {
    @Test
    fun states() =
        captureStates("badge") {
            ScreenshotFlow {
                for (variant in QeetrixBadgeVariant.entries) QeetrixBadge(variant.value, variant = variant)
                QeetrixBadge("Verified", variant = QeetrixBadgeVariant.Success, icon = QeetrixIconName.CircleCheck)
            }
        }
}
