package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.Column
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/listitem/`: section headers, rows with every slot, selected, disabled, wrapping. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixListItemScreenshotTest {
    @Test
    fun states() =
        captureStates("listitem") {
            ScreenshotStack {
                Column {
                    QeetrixSectionHeader("Members", ruled = false, description = "People with access to this workspace.") {
                        QeetrixButton("Invite", onClick = {}, variant = QeetrixButtonVariant.Ghost, size = QeetrixButtonSize.Sm)
                    }
                    QeetrixListItem(
                        "Priya Sharma",
                        description = "priya.sharma@acme.in",
                        leading = { QeetrixAvatar("Priya Sharma", decorative = true) },
                    ) {
                        QeetrixBadge("Owner", tone = QeetrixBadgeTone.Brand)
                    }
                    QeetrixSeparator()
                    QeetrixListItem(
                        "Quarterly report.pdf",
                        description = "Shared by Grace Hopper",
                        metadata = "2.4 MB",
                        selected = true,
                        onClick = {},
                        leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
                    )
                    QeetrixSeparator()
                    QeetrixListItem(
                        "Archived project",
                        description = "Read-only",
                        enabled = false,
                        onClick = {},
                        leading = { QeetrixListItemIcon(QeetrixIconName.Folder) },
                    )
                    QeetrixSectionHeader("दस्तावेज़")
                    QeetrixListItem(
                        "A file name long enough to wrap onto a second line in the row",
                        metadata = "Yesterday",
                        leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
                    ) {
                        QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More", onClick = {}, tone = QeetrixButtonTone.Neutral)
                    }
                }
            }
        }
}
