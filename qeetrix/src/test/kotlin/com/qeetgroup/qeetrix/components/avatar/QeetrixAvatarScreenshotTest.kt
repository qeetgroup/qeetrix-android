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

/** Reviewed baselines for [QeetrixAvatar] and [QeetrixAvatarItem] in `src/test/screenshots/avatar/`. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixAvatarScreenshotTest {
    @Test
    fun states() =
        captureStates("avatar") {
            ScreenshotStack {
                Row(horizontalArrangement = Arrangement.spacedBy(ScreenshotGap), verticalAlignment = Alignment.CenterVertically) {
                    for (size in QeetrixAvatarSize.entries) QeetrixAvatar("Priya Sharma", size = size)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(ScreenshotGap), verticalAlignment = Alignment.CenterVertically) {
                    for (size in QeetrixAvatarSize.entries) QeetrixAvatar("Acme Pvt Ltd", size = size, shape = QeetrixAvatarShape.Square)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(ScreenshotGap), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixAvatar("Photo", size = QeetrixAvatarSize.Xl, image = ColorPainter(QeetrixTheme.colors.datavizCategorical1))
                    QeetrixAvatar(
                        "Logo",
                        size = QeetrixAvatarSize.Xl,
                        shape = QeetrixAvatarShape.Square,
                        image = ColorPainter(QeetrixTheme.colors.datavizCategorical3),
                    )
                    QeetrixAvatar("नमन शर्मा", size = QeetrixAvatarSize.Xl)
                    QeetrixAvatar("Kaveri Foods Private Limited", size = QeetrixAvatarSize.Lg, shape = QeetrixAvatarShape.Square)
                }
                QeetrixAvatarItem("Priya Sharma", description = "priya.sharma@acme.in")
                QeetrixAvatarItem("Acme Pvt Ltd", description = "Organisation", shape = QeetrixAvatarShape.Square)
                QeetrixAvatarItem(
                    "A name long enough to run out of room in this row, which wraps",
                    description = "an.address.long.enough.to.wrap.where.it.must.too@example.com",
                )
            }
        }
}
