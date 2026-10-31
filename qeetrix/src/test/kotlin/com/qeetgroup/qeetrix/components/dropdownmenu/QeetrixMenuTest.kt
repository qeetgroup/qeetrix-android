package com.qeetgroup.qeetrix.components.dropdownmenu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The menu: the app's open flag, items that close the menu and run once, checkbox and radio items, every way of closing it. */
@RunWith(RobolectricTestRunner::class)
class QeetrixMenuTest {
    @get:Rule
    val compose = createComposeRule()

    private var open by mutableStateOf(false)
    private val events = mutableListOf<String>()
    private val triggerFocus = FocusRequester()
    private var compact by mutableStateOf(false)
    private var sort by mutableStateOf("newest")

    private fun showMenu() {
        compose.setQeetrix {
            QeetrixMenu(
                open = open,
                onOpenChange = {
                    events += "open=$it"
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
                QeetrixMenuGroupLabel("Workspace")
                QeetrixMenuItem("Rename", onClick = { events += "rename" }, icon = QeetrixIconName.Pencil, shortcut = "F2")
                QeetrixMenuItem("Archive", onClick = { events += "archive" }, enabled = false)
                QeetrixMenuCheckboxItem("Compact rows", checked = compact, onCheckedChange = {
                    events += "compact=$it"
                    compact = it
                })
                QeetrixMenuSeparator()
                QeetrixMenuRadioGroup(sort, { sort = it }) {
                    QeetrixMenuGroupLabel("Sort by")
                    QeetrixMenuRadioItem("Newest first", "newest")
                    QeetrixMenuRadioItem("Oldest first", "oldest", description = "The first invoice at the top")
                }
                QeetrixMenuSeparator()
                QeetrixMenuItem(
                    "Delete",
                    onClick = { events += "delete" },
                    icon = QeetrixIconName.Trash,
                    tone = QeetrixMenuItemTone.Danger,
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
        compose.runOnIdle { assertEquals(listOf("open=false", "delete"), events) }
        compose.onNodeWithText("Delete").assertDoesNotExist()
    }

    @Test
    fun aShortcutIsHiddenFromAccessibilityServices() {
        showMenu()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Rename").assert(SemanticsMatcher.expectValue(SemanticsProperties.Text, listOf(AnnotatedString("Rename"))))
        compose.onNodeWithText("F2").assertDoesNotExist()
    }

    @Test
    fun aCheckboxItemIsACheckboxThatKeepsTheMenuOpen() {
        showMenu()
        compose.runOnIdle { open = true }
        compose
            .onNodeWithText("Compact rows")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.Off))
            .performClick()
        compose
            .onNodeWithText("Compact rows")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, ToggleableState.On))
        compose.runOnIdle { assertEquals(listOf("compact=true"), events) }
    }

    @Test
    fun aRadioItemChoosesTheGroupsValueAndClosesTheMenu() {
        showMenu()
        compose.runOnIdle { open = true }
        compose
            .onNodeWithText(
                "Newest first",
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assertIsSelected()
        compose.onNodeWithText("Oldest first", substring = true).assertIsNotSelected().performClick()
        compose.runOnIdle {
            assertEquals("oldest", sort)
            assertEquals(listOf("open=false"), events)
        }
        compose.onNodeWithText("Newest first").assertDoesNotExist()
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
        compose.runOnIdle { assertEquals(listOf("open=false"), events) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun openedFromTheKeyboardFocusStartsOnTheFirstAvailableItem() {
        showMenu()
        compose.runOnIdle { triggerFocus.requestFocus() }
        compose.onNodeWithContentDescription("More options").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        compose.onNodeWithText("Rename").assertIsFocused()
    }

    @Test
    fun tonesAreTheWebs() {
        assertEquals(listOf("default", "danger"), QeetrixMenuItemTone.entries.map { it.value })
    }
}
