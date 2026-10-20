package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotGap
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines in `src/test/screenshots/skeleton/`: the three shapes and text lines, still (animations off). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSkeletonScreenshotTest {
    @Test
    fun states() =
        captureStates("skeleton") {
            ScreenshotStack {
                Row(horizontalArrangement = Arrangement.spacedBy(ScreenshotGap), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixSkeleton(Modifier.size(40.dp), QeetrixSkeletonShape.Circle)
                    QeetrixSkeleton(Modifier.size(width = 72.dp, height = 24.dp), QeetrixSkeletonShape.Capsule)
                    QeetrixSkeleton(Modifier.size(width = 96.dp, height = 32.dp))
                }
                QeetrixSkeletonText(lines = 3, role = QeetrixTextRole.Body)
                QeetrixSkeletonText(lines = 1, role = QeetrixTextRole.Title)
            }
        }
}
