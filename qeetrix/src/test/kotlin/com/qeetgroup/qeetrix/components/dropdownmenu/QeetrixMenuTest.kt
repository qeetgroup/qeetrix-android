package com.qeetgroup.qeetrix.components.dropdownmenu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The menu: the app's expanded flag, items that close the menu and run once, every way of closing it. */
@RunWith(RobolectricTestRunner::class)
class QeetrixMenuTest {
    @get:Rule
    val compose = createComposeRule()

    private var open by mutableStateOf(false)
    private val events = mutableListOf<String>()
    private val triggerFocus = FocusRequester()

    private fun showMenu() {
        compose.setQeetrix {
            QeetrixMenu(
                expanded = open,
                onExpandedChange = {
                    events += if (it) "open" else "close"
                    open = it
                },
                trigger = {
                    QeetrixIconButton(
                        QeetrixIconName.EllipsisVertical,
                        "More options",
                        onClick = { open = true },
                        modifier = Modifier.focusRequester(triggerFocus),
                    )
                },
            ) {
                QeetrixMenuLabel("Workspace")
                QeetrixMenuItem("Rename", onClick = { events += "rename" }, icon = QeetrixIconName.Pencil)
                QeetrixMenuItem("Archive", onClick = { events += "archive" }, enabled = false)
                QeetrixMenuSeparator()
                QeetrixMenuItem(
                    "Delete",
                    onClick = { events += "delete" },
                    icon = QeetrixIconName.Trash,
                    variant = QeetrixMenuItemVariant.Destructive,
                )
            }
        }
    }

    @Test
    fun theTriggerOpensItAndAnItemClosesItBeforeRunningOnce() {
        showMenu()
        compose.onNodeWithText("Rename").assertDoesNotExist()
        compose.onNodeWithContentDescription("More options").performClick()
        compose.onNode(isHeading() and hasText("Workspace")).assertExists()
        compose.onNodeWithText("Rename").assertHeightIsAtLeast(48.dp)
        compose.onNodeWithText("Archive").assertIsNotEnabled()
        compose.onNodeWithText("Delete").performClick()
        compose.runOnIdle { assertEquals(listOf("close", "delete"), events) }
        compose.onNodeWithText("Delete").assertDoesNotExist()
    }

    // Back is the popup window's own (Compose's Popup, dismissOnBackPress); Robolectric does not route Back
    // to a popup window, so it is checked on a device by QeetrixOverlayBackTest (androidTest).
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun escapeCloses() {
        showMenu()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Rename").performKeyInput { pressKey(Key.Escape) }
        compose.onNodeWithText("Rename").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("close"), events) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun openedFromTheKeyboardFocusStartsOnTheFirstAvailableItem() {
        showMenu()
        compose.runOnIdle { triggerFocus.requestFocus() }
        compose.onNodeWithContentDescription("More options").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        compose.onNodeWithText("Rename").assertIsFocused()
    }
}
