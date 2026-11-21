package com.qeetgroup.qeetrix.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
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

    /**
     * `src/test/screenshots/button/stack_<config>.png`: a sheet's three full-width `lg` buttons `space.gap.sm` apart at
     * the comfortable density, laid out at their drawn 44 dp: 148 dp in all, as the web's `space-y-2` stack.
     */
    @Test
    fun stack() {
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("button", "stack", config) {
                CompositionLocalProvider(LocalQeetrixDensity provides QeetrixDensity.Comfortable) {
                    Column(Modifier.width(328.dp), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm)) {
                        QeetrixButton(
                            "Delete for everyone",
                            onClick = {},
                            tone = QeetrixButtonTone.Danger,
                            size = QeetrixButtonSize.Lg,
                            fullWidth = true,
                        )
                        QeetrixButton(
                            "Delete for me",
                            onClick = {},
                            variant = QeetrixButtonVariant.Soft,
                            size = QeetrixButtonSize.Lg,
                            fullWidth = true,
                        )
                        QeetrixButton(
                            "Cancel",
                            onClick = {},
                            variant = QeetrixButtonVariant.Ghost,
                            size = QeetrixButtonSize.Lg,
                            fullWidth = true,
                        )
                    }
                }
            }
        }
    }
}
