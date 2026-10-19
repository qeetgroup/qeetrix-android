package com.qeetgroup.qeetrix.components.avatar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.ColorPainter
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotGap
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Reviewed baselines for [QeetrixAvatar] in `src/test/screenshots/avatar/`, one image per screenshot configuration. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixAvatarScreenshotTest {
    @Test
    fun states() =
        captureStates("avatar") {
            ScreenshotStack {
                Row(horizontalArrangement = Arrangement.spacedBy(ScreenshotGap), verticalAlignment = Alignment.CenterVertically) {
                    for (size in QeetrixAvatarSize.entries) QeetrixAvatar("Ada Lovelace", size = size)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(ScreenshotGap), verticalAlignment = Alignment.CenterVertically) {
                    for (size in QeetrixAvatarSize.entries) {
                        QeetrixAvatar(
                            "Qeet Group",
                            size = size,
                            shape = QeetrixAvatarShape.Square,
                            badgeLabel = "Active",
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(ScreenshotGap), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixAvatar(null, size = QeetrixAvatarSize.Lg)
                    QeetrixAvatar("Priya", size = QeetrixAvatarSize.Lg, badgeLabel = "Online")
                    QeetrixAvatar("Photo", size = QeetrixAvatarSize.Xl, image = ColorPainter(QeetrixTheme.colors.dataCategorical1))
                    QeetrixAvatar("नमन शर्मा", size = QeetrixAvatarSize.Xl)
                }
            }
        }
}
