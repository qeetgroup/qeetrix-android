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
                ScreenshotFlow {
                    for (variant in QeetrixButtonVariant.entries) {
                        QeetrixButton(
                            variant.value,
                            onClick = {},
                            variant = variant,
                        )
                    }
                }
                ScreenshotFlow { for (size in QeetrixButtonSize.entries) QeetrixButton("Size ${size.value}", onClick = {}, size = size) }
                ScreenshotFlow {
                    QeetrixButton("Disabled", onClick = {}, enabled = false)
                    QeetrixButton("Disabled", onClick = {}, enabled = false, variant = QeetrixButtonVariant.Outline)
                    QeetrixButton("Saving", onClick = {}, loading = true)
                    QeetrixButton(
                        "Save changes",
                        onClick = {},
                        loading = true,
                        loadingLabel = "Saving…",
                        variant = QeetrixButtonVariant.Secondary,
                    )
                }
                ScreenshotFlow {
                    QeetrixButton("Send", onClick = {}, leadingIcon = QeetrixIconName.Send)
                    QeetrixButton("Next", onClick = {}, variant = QeetrixButtonVariant.Outline, trailingIcon = QeetrixIconName.Send)
                    QeetrixButton("Delete", onClick = {}, variant = QeetrixButtonVariant.Destructive, leadingIcon = QeetrixIconName.X)
                }
            }
        }
}
