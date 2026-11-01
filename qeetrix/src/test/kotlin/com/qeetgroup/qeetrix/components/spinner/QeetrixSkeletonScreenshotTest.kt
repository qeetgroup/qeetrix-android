package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
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

/** Baselines in `src/test/screenshots/skeleton/`: the circle sizes, a list row, a block and paragraphs, still (animations off). */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixSkeletonScreenshotTest {
    @Test
    fun states() =
        captureStates("skeleton") {
            ScreenshotStack {
                Row(horizontalArrangement = Arrangement.spacedBy(ScreenshotGap), verticalAlignment = Alignment.CenterVertically) {
                    for (size in QeetrixSkeletonSize.entries) QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle, size = size)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                        QeetrixSkeleton(Modifier.width(160.dp))
                        QeetrixSkeleton(Modifier.width(220.dp))
                    }
                }
                QeetrixSkeleton(variant = QeetrixSkeletonVariant.Block)
                QeetrixSkeletonText(lines = 3)
                QeetrixSkeletonText(lines = 1, role = QeetrixTextRole.TitleMd)
            }
        }
}
