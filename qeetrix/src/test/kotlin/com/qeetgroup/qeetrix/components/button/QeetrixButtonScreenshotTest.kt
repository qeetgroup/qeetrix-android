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

/** Reviewed baselines for [QeetrixButton] in `src/test/screenshots/button/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixButtonScreenshotTest {
    @Test
    fun states() =
        captureStates("button") {
            ScreenshotStack {
                for (tone in QeetrixButtonTone.entries) {
                    ScreenshotFlow {
                        for (variant in QeetrixButtonVariant.entries) {
                            QeetrixButton("${variant.value} ${tone.value}", onClick = {}, variant = variant, tone = tone)
                        }
                    }
                }
                ScreenshotFlow {
                    for (size in QeetrixButtonSize.entries) {
                        QeetrixButton(
                            "Size ${size.value}",
                            onClick = {},
                            size = size,
                            icon = QeetrixIconName.Plus,
                        )
                    }
                }
                ScreenshotFlow {
                    QeetrixButton("Create invoice", onClick = {}, icon = QeetrixIconName.Plus)
                    QeetrixButton("Export", onClick = {}, variant = QeetrixButtonVariant.Soft, icon = QeetrixIconName.Share2)
                    QeetrixButton("Learn more", onClick = {
                    }, variant = QeetrixButtonVariant.Ghost, icon = QeetrixIconName.ChevronRight, iconMirrorsInRightToLeft = true)
                }
                ScreenshotFlow {
                    QeetrixButton("Disabled", onClick = {}, enabled = false)
                    QeetrixButton("Disabled", onClick = {}, enabled = false, variant = QeetrixButtonVariant.Ghost)
                    QeetrixButton("Saving", onClick = {}, loading = true)
                    QeetrixButton(
                        "Save draft",
                        onClick = {},
                        loading = true,
                        variant = QeetrixButtonVariant.Soft,
                        icon = QeetrixIconName.Check,
                    )
                }
                QeetrixButton("Continue", onClick = {
                }, size = QeetrixButtonSize.Lg, fullWidth = true, icon = QeetrixIconName.ChevronRight, iconMirrorsInRightToLeft = true)
            }
        }
}
