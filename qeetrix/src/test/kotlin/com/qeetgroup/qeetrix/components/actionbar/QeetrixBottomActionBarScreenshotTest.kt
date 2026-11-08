package com.qeetgroup.qeetrix.components.actionbar

import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/bottomactionbar/`: the equal split, the primary filling, and stacked. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixBottomActionBarScreenshotTest {
    @Test
    fun states() =
        captureStates("bottomactionbar") {
            ScreenshotStack {
                QeetrixBottomActionBar(ruled = true) {
                    QeetrixButton("Deny", onClick = {}, variant = QeetrixButtonVariant.Soft, fullWidth = true)
                    QeetrixButton("Approve", onClick = {}, fullWidth = true)
                }
                QeetrixBottomActionBar(ruled = true, arrangement = QeetrixBottomActionBarArrangement.PrimaryFills) {
                    QeetrixButton("Not now", onClick = {}, variant = QeetrixButtonVariant.Ghost, fullWidth = true)
                    QeetrixButton("Continue", onClick = {}, icon = QeetrixIconName.ArrowRight, fullWidth = true)
                }
                QeetrixBottomActionBar(ruled = true, arrangement = QeetrixBottomActionBarArrangement.Stacked) {
                    QeetrixButton("Enter the code instead", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                    QeetrixButton("Scan the QR code", onClick = {}, fullWidth = true)
                }
            }
        }
}
