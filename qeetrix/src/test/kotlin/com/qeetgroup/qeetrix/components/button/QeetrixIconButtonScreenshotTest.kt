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
                for (tone in QeetrixButtonTone.entries) {
                    ScreenshotFlow {
                        for (variant in QeetrixButtonVariant.entries) {
                            QeetrixIconButton(
                                QeetrixIconName.Search,
                                "${variant.value} ${tone.value}",
                                onClick = {},
                                variant = variant,
                                tone = tone,
                            )
                        }
                    }
                }
                ScreenshotFlow {
                    for (size in QeetrixButtonSize.entries) {
                        QeetrixIconButton(QeetrixIconName.Bell, size.value, onClick = {}, size = size, variant = QeetrixButtonVariant.Soft)
                    }
                }
                ScreenshotFlow {
                    QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, variant = QeetrixButtonVariant.Solid)
                    QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, variant = QeetrixButtonVariant.Solid, enabled = false)
                    QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, variant = QeetrixButtonVariant.Solid, loading = true)
                }
            }
        }
}
