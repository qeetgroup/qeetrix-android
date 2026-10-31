package com.qeetgroup.qeetrix.components.badge

import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
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
            ScreenshotStack {
                for (variant in QeetrixBadgeVariant.entries) {
                    ScreenshotFlow { for (tone in QeetrixBadgeTone.entries) QeetrixBadge(tone.value, tone = tone, variant = variant) }
                }
                ScreenshotFlow {
                    QeetrixBadge("Verified", tone = QeetrixBadgeTone.Success, icon = QeetrixIconName.CircleCheck)
                    QeetrixBadge(
                        "Overdue",
                        tone = QeetrixBadgeTone.Danger,
                        variant = QeetrixBadgeVariant.Solid,
                        icon = QeetrixIconName.CircleAlert,
                    )
                    QeetrixBadge("3", size = QeetrixBadgeSize.Sm)
                    QeetrixBadge("Draft", size = QeetrixBadgeSize.Sm, tone = QeetrixBadgeTone.Brand)
                }
            }
        }
}
