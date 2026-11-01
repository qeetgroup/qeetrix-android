package com.qeetgroup.qeetrix.components.copy

import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotFlow
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixCopyButton] and [QeetrixCopyField] in `src/test/screenshots/copy/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixCopyScreenshotTest {
    @Test
    fun states() =
        captureStates("copy") {
            ScreenshotStack {
                ScreenshotFlow {
                    QeetrixCopyButton({ "cl_8f2a" }, label = "Copy client ID")
                    QeetrixCopyButton({ "cl_8f2a" }, text = "Copy ID", variant = QeetrixButtonVariant.Soft)
                    QeetrixCopyButton(
                        { "cl_8f2a" },
                        text = "Copy link",
                        variant = QeetrixButtonVariant.Solid,
                        tone = QeetrixButtonTone.Brand,
                    )
                    QeetrixCopyButton({ "cl_8f2a" }, enabled = false)
                }
                QeetrixCopyField("Client ID", "cl_8f2a91c4e7", description = "Paste it into your app's settings.")
                QeetrixCopyField("ACS URL", "https://id.qeet.in/saml/acs/tenants/acme-industries/apps/payroll-portal-production")
                QeetrixCopyField("Signing secret", "whsec_2b91d0c4e7a8", layout = QeetrixCopyFieldLayout.Row)
                QeetrixCopyField("Webhook", "https://hooks.acme.in/qeet/events/payroll", layout = QeetrixCopyFieldLayout.Row, wrap = false)
            }
        }
}
