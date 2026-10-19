package com.qeetgroup.qeetrix.components.button

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

/** Reviewed baselines for [QeetrixIconButton] in `src/test/screenshots/iconbutton/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixIconButtonScreenshotTest {
    @Test
    fun states() =
        captureStates("iconbutton") {
            ScreenshotStack {
                ScreenshotFlow {
                    for (variant in QeetrixButtonVariant.entries) {
                        QeetrixIconButton(
                            QeetrixIconName.Search,
                            variant.value,
                            onClick = {},
                            variant = variant,
                        )
                    }
                }
                ScreenshotFlow {
                    for (size in QeetrixIconButtonSize.entries) {
                        QeetrixIconButton(
                            QeetrixIconName.Bell,
                            size.value,
                            onClick = {},
                            size = size,
                            variant = QeetrixButtonVariant.Outline,
                        )
                    }
                }
                ScreenshotFlow {
                    QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, variant = QeetrixButtonVariant.Default)
                    QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, variant = QeetrixButtonVariant.Default, enabled = false)
                    QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, variant = QeetrixButtonVariant.Default, loading = true)
                }
            }
        }
}
