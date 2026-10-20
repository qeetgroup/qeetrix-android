package com.qeetgroup.qeetrix.components.emptystate

import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
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
 * Baselines in `src/test/screenshots/emptystate/`: every variant's tile at the small size, the default size with
 * actions, the large size; and a data state's loading and failure branches.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixEmptyStateScreenshotTest {
    @Test
    fun states() =
        captureStates("emptystate") {
            ScreenshotStack {
                for (variant in QeetrixEmptyStateVariant.entries) {
                    QeetrixEmptyState(
                        variant.value,
                        description = "What happened, and what to do.",
                        variant = variant,
                        size = QeetrixEmptyStateSize.Sm,
                        icon =
                            QeetrixIconName.FileText.takeIf {
                                variant ==
                                    QeetrixEmptyStateVariant.FirstUse ||
                                    variant == QeetrixEmptyStateVariant.Default
                            },
                    )
                }
                QeetrixEmptyState(
                    "No invoices yet",
                    description = "Invoices appear here once you bill a customer.",
                    variant = QeetrixEmptyStateVariant.FirstUse,
                    icon = QeetrixIconName.FileText,
                ) {
                    QeetrixButton("Create invoice", onClick = {})
                    QeetrixButton("Import", onClick = {}, variant = QeetrixButtonVariant.Outline)
                }
                QeetrixEmptyState(
                    "Your inbox is clear",
                    description = "New messages appear here.",
                    size = QeetrixEmptyStateSize.Lg,
                    icon = QeetrixIconName.Inbox,
                )
            }
        }

    @Test
    fun dataState() {
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("emptystate", "datastate", config) {
                ScreenshotStack {
                    QeetrixDataState(loading = true, empty = false, emptyContent = {}) {}
                    QeetrixDataState(loading = false, empty = false, failure = QeetrixDataFailure(onRetry = {}), emptyContent = {}) {}
                    QeetrixDataState(loading = false, empty = false, emptyContent = {}) { QeetrixText("The data") }
                }
            }
        }
    }
}
