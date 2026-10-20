package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.Column
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparatorVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/listitem/`: a section header, rows with every slot, selected, disabled, wrapping. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixListItemScreenshotTest {
    @Test
    fun states() =
        captureStates("listitem") {
            ScreenshotStack {
                Column {
                    QeetrixSectionHeader("Members", description = "People with access to this workspace.") {
                        QeetrixButton("Invite", onClick = {}, variant = QeetrixButtonVariant.Link, size = QeetrixButtonSize.Sm)
                    }
                    QeetrixListItem(
                        "Ada Lovelace",
                        description = "ada@example.com",
                        leading = { QeetrixAvatar("Ada Lovelace", contentDescription = null) },
                    ) {
                        QeetrixBadge("Owner", variant = QeetrixBadgeVariant.Brand)
                    }
                    QeetrixSeparator(variant = QeetrixSeparatorVariant.Muted)
                    QeetrixListItem(
                        "Quarterly report.pdf",
                        description = "Shared by Grace Hopper",
                        metadata = "2.4 MB",
                        selected = true,
                        onClick = {},
                        leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
                    )
                    QeetrixSeparator(variant = QeetrixSeparatorVariant.Muted)
                    QeetrixListItem("Archived project", description = "Read-only", enabled = false, onClick = {
                    }, leading = { QeetrixListItemIcon(QeetrixIconName.Folder) })
                    QeetrixSeparator(variant = QeetrixSeparatorVariant.Muted)
                    QeetrixListItem(
                        "A file name long enough to wrap onto a second line in the row",
                        metadata = "Yesterday",
                        leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
                    ) { QeetrixBadge("Shared", variant = QeetrixBadgeVariant.Secondary) }
                }
            }
        }
}
