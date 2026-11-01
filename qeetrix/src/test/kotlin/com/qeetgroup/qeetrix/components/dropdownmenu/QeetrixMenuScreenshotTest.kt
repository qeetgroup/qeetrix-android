package com.qeetgroup.qeetrix.components.dropdownmenu

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureStates
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Baselines in `src/test/screenshots/menu/`: the open menu's surface, drawn in place — a popup cannot be captured. An
 * action menu with a group label, icons, shortcuts, a highlighted item, a disabled item and a destructive
 * one after a separator; and an options menu with checkbox items and a radio group, one with a description.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixMenuScreenshotTest {
    @Test
    fun states() =
        captureStates("menu") {
            ScreenshotStack {
                CompositionLocalProvider(LocalHighlightedMenuItem provides "Duplicate") {
                    MenuSurface(PopupWidth, focusFirst = false, onDismiss = {}) {
                        QeetrixMenuGroupLabel("Workspace")
                        QeetrixMenuItem("Rename", onClick = {}, icon = QeetrixIconName.Pencil, shortcut = "F2")
                        QeetrixMenuItem("Duplicate", onClick = {}, icon = QeetrixIconName.Copy, shortcut = "Ctrl+D")
                        QeetrixMenuItem("Share", onClick = {}, icon = QeetrixIconName.Share2, enabled = false)
                        QeetrixMenuSeparator()
                        QeetrixMenuItem("Delete workspace", onClick = {}, icon = QeetrixIconName.Trash, tone = QeetrixMenuItemTone.Danger)
                    }
                }
                MenuSurface(PopupWidth, focusFirst = false, onDismiss = {}) {
                    QeetrixMenuCheckboxItem("Show archived", checked = true, onCheckedChange = {})
                    QeetrixMenuCheckboxItem("Compact rows", checked = false, onCheckedChange = {}, shortcut = "Ctrl+J")
                    QeetrixMenuSeparator()
                    QeetrixMenuRadioGroup("newest", {}) {
                        QeetrixMenuGroupLabel("Sort by")
                        QeetrixMenuRadioItem("Newest first", "newest")
                        QeetrixMenuRadioItem("Oldest first", "oldest", description = "The first invoice you sent, at the top")
                        QeetrixMenuRadioItem("Amount", "amount", enabled = false, description = "Needs the amounts to load")
                    }
                }
            }
        }
}

/** Baselines in `src/test/screenshots/menusubmenu/`: a submenu row, highlighted, with its chevron mirrored in right to left. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixMenuSubmenuScreenshotTest {
    @Test
    fun states() =
        captureStates("menusubmenu") {
            ScreenshotStack {
                CompositionLocalProvider(LocalHighlightedMenuItem provides "Move to") {
                    MenuSurface(PopupWidth, focusFirst = false, onDismiss = {}) {
                        QeetrixMenuItem("Rename", onClick = {}, icon = QeetrixIconName.Pencil)
                        QeetrixMenuSubmenu("Move to", icon = QeetrixIconName.Folder) { QeetrixMenuItem("Archive", onClick = {}) }
                        QeetrixMenuSubmenu("Share with", description = "People in this workspace") { QeetrixMenuItem("Asha", onClick = {}) }
                    }
                }
            }
        }
}

/** The popup's width range, as the menu gives it: `.qx-menu min-inline-size: 200px; max-inline-size: 320px`. */
private val PopupWidth = Modifier.widthIn(min = 200.dp, max = 320.dp)
