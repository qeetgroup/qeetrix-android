package com.qeetgroup.qeetrix.components.badge

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

/** Baselines for [QeetrixChip] and [QeetrixChipGroup] in `src/test/screenshots/chip/`. */
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
                        QeetrixChip("Unread ${size.value}", selected = false, onSelectedChange = {}, size = size)
                        QeetrixChip("Selected", selected = true, onSelectedChange = {}, size = size)
                        QeetrixChip("Status: Active", onRemove = {}, size = size)
                    }
                }
                ScreenshotFlow {
                    QeetrixChip("Hovered", selected = false, onSelectedChange = {}, interactionSource = rememberHoveredSource())
                    QeetrixChip("Focused", selected = true, onSelectedChange = {}, interactionSource = rememberFocusedSource())
                    QeetrixChip("Starred", selected = true, onSelectedChange = {}, icon = QeetrixIconName.Bell)
                    QeetrixChip("Disabled", selected = false, onSelectedChange = {}, enabled = false)
                    QeetrixChip("Disabled", selected = true, onSelectedChange = {}, enabled = false)
                    QeetrixChip("Removed", onRemove = {}, enabled = false)
                }
                QeetrixChipGroup(
                    label = "Status",
                    value = "open",
                    onValueChange = {},
                    options =
                        listOf(
                            QeetrixOption("all", "All"),
                            QeetrixOption("open", "Open"),
                            QeetrixOption("closed", "Closed"),
                            QeetrixOption("draft", "Draft", enabled = false),
                        ),
                )
                QeetrixChipGroup(
                    label = "Departments",
                    values = setOf("Design", "Legal"),
                    onValuesChange = {},
                    options = listOf("Design", "Engineering", "Finance", "Legal", "People operations").map { QeetrixOption(it, it) },
                    size = QeetrixChipSize.Sm,
                )
            }
        }
}
