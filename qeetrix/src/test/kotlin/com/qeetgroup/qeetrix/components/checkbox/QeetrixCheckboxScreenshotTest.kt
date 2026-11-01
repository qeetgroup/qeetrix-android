package com.qeetgroup.qeetrix.components.checkbox

import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.rememberFocusedSource
import com.qeetgroup.qeetrix.components.input.rememberHoveredSource
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

/** Baselines for [QeetrixCheckbox] and [QeetrixCheckboxGroup] in `src/test/screenshots/checkbox/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixCheckboxScreenshotTest {
    @Test
    fun states() =
        captureStates("checkbox") {
            ScreenshotStack {
                ScreenshotFlow {
                    QeetrixCheckbox(false, {})
                    QeetrixCheckbox(true, {})
                    QeetrixCheckbox(false, {}, indeterminate = true)
                    QeetrixCheckbox(false, {}, invalid = true)
                    QeetrixCheckbox(false, {}, interactionSource = rememberHoveredSource())
                    QeetrixCheckbox(true, {}, interactionSource = rememberHoveredSource())
                    QeetrixCheckbox(false, {}, interactionSource = rememberFocusedSource())
                    QeetrixCheckbox(false, {}, enabled = false)
                    QeetrixCheckbox(true, {}, enabled = false)
                }
                QeetrixCheckbox(
                    checked = true,
                    onCheckedChange = {},
                    label = "Product updates",
                    description = "About once a month. A longer line that wraps under the label, not the box.",
                )
                QeetrixCheckbox(
                    false,
                    {},
                    label = "Keep me signed in",
                    icon = QeetrixIconName.Lock,
                    interactionSource = rememberFocusedSource(),
                )
                QeetrixCheckbox(true, {}, label = "Disabled and checked", description = "Your admin sets this.", enabled = false)
                QeetrixCheckboxGroup(
                    setOf("email"),
                    {},
                    listOf(QeetrixOption("email", "Email"), QeetrixOption("sms", "SMS"), QeetrixOption("push", "Push", enabled = false)),
                    label = "Notify me by",
                    error = "Choose at least one way to reach you.",
                )
                QeetrixCheckboxGroup(
                    setOf("mon", "wed"),
                    {},
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri").map { QeetrixOption(it.lowercase(), it) },
                    label = "Working days",
                    orientation = QeetrixChoiceOrientation.Horizontal,
                    description = "Reports go out on these days.",
                )
                QeetrixCheckboxGroup(
                    setOf("passkey"),
                    {},
                    listOf(
                        QeetrixOption("passkey", "Passkeys", "Face, fingerprint or screen lock", icon = QeetrixIconName.Lock),
                        QeetrixOption("email", "Email link", "A one-time link to the work email", icon = QeetrixIconName.Mail),
                        QeetrixOption(
                            "sso",
                            "Single sign-on",
                            "Through the company's provider",
                            icon = QeetrixIconName.Users,
                            enabled = false,
                        ),
                    ),
                    label = "Sign-in methods",
                    variant = QeetrixChoiceVariant.Tile,
                    columns = 2,
                )
            }
        }
}
