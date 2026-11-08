package com.qeetgroup.qeetrix.components.emptystate

import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/emptystate/`: the page size with actions and a footer, the section size, the
 * danger tone; and a data state's loading (the error's height held) and error states.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixEmptyStateScreenshotTest {
    @Test
    fun states() =
        captureStates("emptystate") {
            ScreenshotStack {
                QeetrixEmptyState(
                    "No invoices yet",
                    icon = QeetrixIconName.FileText,
                    description = "Invoices appear here once you bill a customer.",
                    footer = { QeetrixText("Or import them from a CSV file", size = QeetrixTextSize.Sm) },
                ) {
                    QeetrixButton("Create invoice", onClick = {}, icon = QeetrixIconName.Plus)
                    QeetrixButton("Import", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                }
                QeetrixEmptyState(
                    "No results",
                    icon = QeetrixIconName.SearchX,
                    description = "Nothing matches “quarterly”. Check the spelling or clear the filters.",
                    size = QeetrixEmptyStateSize.Section,
                ) { QeetrixButton("Clear filters", onClick = {}, variant = QeetrixButtonVariant.Ghost) }
                QeetrixEmptyState(
                    "You can't see this project",
                    icon = QeetrixIconName.Lock,
                    description = "Ask its owner, Ada Lovelace, for access.",
                    size = QeetrixEmptyStateSize.Section,
                    tone = QeetrixEmptyStateTone.Danger,
                )
                QeetrixEmptyState(
                    "Sign-in approved",
                    icon = QeetrixIconName.ShieldCheck,
                    description = "You're signed in on Chrome on Windows.",
                    size = QeetrixEmptyStateSize.Section,
                    tone = QeetrixEmptyStateTone.Success,
                )
                QeetrixEmptyState(
                    "Approve on your other phone",
                    icon = QeetrixIconName.Smartphone,
                    size = QeetrixEmptyStateSize.Section,
                    tone = QeetrixEmptyStateTone.Warning,
                )
            }
        }

    @Test
    fun dataState() {
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("emptystate", "datastate", config) {
                ScreenshotStack {
                    QeetrixDataState(loading = true, onRetry = {}) {}
                    QeetrixDataState(loading = false, error = true, onRetry = {}) {}
                }
            }
        }
    }
}
