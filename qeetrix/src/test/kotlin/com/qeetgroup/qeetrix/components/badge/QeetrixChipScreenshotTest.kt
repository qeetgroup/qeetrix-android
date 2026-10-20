package com.qeetgroup.qeetrix.components.badge

import com.qeetgroup.qeetrix.components.input.QeetrixOption
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

/** Baselines in `src/test/screenshots/chip/`: selected and resting in each size, an icon, disabled, removable, a group. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixChipScreenshotTest {
    @Test
    fun states() =
        captureStates("chip") {
            ScreenshotStack {
                for (size in QeetrixChipSize.entries) {
                    ScreenshotFlow {
                        QeetrixChip("Selected", selected = true, onSelectedChange = {}, size = size)
                        QeetrixChip("Resting", selected = false, onSelectedChange = {}, size = size)
                    }
                }
                ScreenshotFlow {
                    QeetrixChip("Mine", selected = true, onSelectedChange = {}, icon = QeetrixIconName.UserRound)
                    QeetrixChip("Archived", selected = false, onSelectedChange = {}, enabled = false)
                    QeetrixChip("Owner: Ada", onRemove = {})
                }
                QeetrixChipGroup(
                    label = "Labels",
                    selected = setOf("Design", "Legal"),
                    onSelectedChange = {},
                    options = listOf("Design", "Engineering", "Finance", "Legal", "Marketing").map { QeetrixOption(it, it) },
                )
            }
        }
}
