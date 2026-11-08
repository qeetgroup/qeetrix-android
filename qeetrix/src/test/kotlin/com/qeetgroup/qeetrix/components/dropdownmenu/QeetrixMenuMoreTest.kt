package com.qeetgroup.qeetrix.components.dropdownmenu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.withKeyDown
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.internal.AnchoredPopupPosition
import com.qeetgroup.qeetrix.internal.PointPopupPosition
import com.qeetgroup.qeetrix.internal.PopupAlign
import com.qeetgroup.qeetrix.internal.PopupSide
import com.qeetgroup.qeetrix.internal.SubmenuPosition
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The menu's side and align, submenus, and a context menu that opens where an area is long-pressed. */
@RunWith(RobolectricTestRunner::class)
class QeetrixMenuMoreTest {
    @get:Rule
    val compose = createComposeRule()

    private val window = IntSize(400, 800)
    private val popup = IntSize(200, 120)

    @Test
    fun aMenuOnTheTopSideOpensAboveItsTriggerWhenItFitsAndFlipsWhenNot() {
        val top = AnchoredPopupPosition(gap = 4, margin = 8, align = PopupAlign.Start, side = PopupSide.Top)
        assertEquals(IntOffset(20, 500 - 4 - 120), top.calculatePosition(IntRect(20, 500, 60, 540), window, LayoutDirection.Ltr, popup))
        // Near the top of the window there is no room above: below instead.
        assertEquals(IntOffset(20, 60 + 4), top.calculatePosition(IntRect(20, 20, 60, 60), window, LayoutDirection.Ltr, popup))
    }

    @Test
    fun alignEndPutsTheMenusEndOnTheTriggersAndMirrorsInRightToLeft() {
        val end = AnchoredPopupPosition(gap = 4, margin = 8, align = PopupAlign.End)
        val anchor = IntRect(300, 100, 340, 140)
        assertEquals(340 - 200, end.calculatePosition(anchor, window, LayoutDirection.Ltr, popup).x)
        // In right to left the end is the left edge: the menu's left edge on the trigger's.
        assertEquals(100, end.calculatePosition(IntRect(100, 100, 140, 140), window, LayoutDirection.Rtl, popup).x)
    }

    @Test
    fun aSubmenuOpensAtItsRowsEndThenItsStartThenOverTheMenu() {
        val position = SubmenuPosition(margin = 8, shift = 5)
        val narrow = IntSize(160, 100)
        // Room at the end: beside the row, its first item level with it.
        assertEquals(IntOffset(200, 95), position.calculatePosition(IntRect(0, 100, 200, 148), window, LayoutDirection.Ltr, narrow))
        // No room at the end: at the start.
        assertEquals(IntOffset(240 - 160, 95), position.calculatePosition(IntRect(240, 100, 392, 148), window, LayoutDirection.Ltr, narrow))
        // Room on neither side (a phone): over the menu, inside the window.
        val wide = IntSize(300, 100)
        assertEquals(400 - 300 - 8, position.calculatePosition(IntRect(60, 100, 340, 148), window, LayoutDirection.Ltr, wide).x)
        // Right to left: the end is the left.
        assertEquals(240 - 160, position.calculatePosition(IntRect(240, 100, 392, 148), window, LayoutDirection.Rtl, narrow).x)
    }

    @Test
    fun aContextMenuOpensAtItsPointAndFlipsAtTheWindowsEdges() {
        val area = IntRect(0, 600, 400, 800)
        assertEquals(
            IntOffset(50, 650),
            PointPopupPosition(IntOffset(50, 50), 8).calculatePosition(area, window, LayoutDirection.Ltr, popup),
        )
        // Near the right and bottom edges it opens to the left and above the point.
        assertEquals(
            IntOffset(350 - 200, 790 - 120),
            PointPopupPosition(IntOffset(350, 190), 8).calculatePosition(area, window, LayoutDirection.Ltr, popup),
        )
    }

    private var open by mutableStateOf(true)
    private val events = mutableListOf<String>()

    private fun showMenuWithSubmenu() {
        compose.setQeetrix {
            QeetrixMenu(
                open = open,
                onOpenChange = {
                    events += "open=$it"
                    open = it
                },
                trigger = { QeetrixButton("Actions", onClick = { open = true }) },
            ) {
                QeetrixMenuItem("Rename", onClick = { events += "rename" })
                QeetrixMenuSubmenu("Move to") {
                    QeetrixMenuItem("Archive", onClick = { events += "archive" })
                    QeetrixMenuItem("Trash", onClick = { events += "trash" })
                }
            }
        }
    }

    @Test
    fun aSubmenuOpensFromItsRowAndAChoiceInItClosesTheWholeMenuOnce() {
        showMenuWithSubmenu()
        compose.onNodeWithText("Archive").assertDoesNotExist()
        compose
            .onNodeWithText("Move to")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Collapsed"))
            .performClick()
        compose.onNodeWithText("Move to").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Expanded"))
        compose.onNodeWithText("Archive").performClick()
        compose.runOnIdle { assertEquals(listOf("open=false", "archive"), events) }
        compose.onNodeWithText("Rename").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun escapeInASubmenuClosesItAloneAndTheArrowOpensIt() {
        showMenuWithSubmenu()
        compose.onNodeWithText("Move to").performClick()
        compose.onNodeWithText("Archive").performKeyInput { pressKey(Key.Escape) }
        compose.onNodeWithText("Archive").assertDoesNotExist()
        compose.onNodeWithText("Rename").assertExists()
        compose.runOnIdle { assertEquals(emptyList<String>(), events) }
        compose.onNodeWithText("Move to").performSemanticsAction(SemanticsActions.RequestFocus)
        compose.onNodeWithText("Move to").performKeyInput { pressKey(Key.DirectionRight) }
        compose.onNodeWithText("Trash").assertExists()
    }

    private var contextOpen by mutableStateOf(false)

    private fun showContextMenu(enabled: Boolean = true) {
        compose.setQeetrix {
            QeetrixContextMenu(
                open = contextOpen,
                onOpenChange = {
                    events += "menu=$it"
                    contextOpen = it
                },
                menu = { QeetrixMenuItem("Rename", onClick = { events += "rename" }) },
                modifier = Modifier.testTag("area"),
                enabled = enabled,
            ) {
                Box(Modifier.size(240.dp, 80.dp)) { QeetrixButton("Open file", onClick = { events += "row" }) }
            }
        }
    }

    @Test
    fun aLongPressOpensTheMenuAndNotTheRowInside() {
        showContextMenu()
        compose.onNodeWithText("Open file").performTouchInput { longClick() }
        compose.onNodeWithText("Rename").assertExists()
        compose.runOnIdle { assertEquals(listOf("menu=true"), events) }
        compose.onNodeWithText("Rename").performClick()
        compose.runOnIdle { assertEquals(listOf("menu=true", "menu=false", "rename"), events) }
    }

    @Test
    fun accessibilityServicesHaveAnOpenMenuAction() {
        showContextMenu()
        compose
            .onNodeWithTag("area")
            .assert(
                SemanticsMatcher("has an Open menu long-click action") {
                    it.config.getOrElseNullable(SemanticsActions.OnLongClick) { null }?.label ==
                        "Open menu"
                },
            ).performSemanticsAction(SemanticsActions.OnLongClick)
        compose.onNodeWithText("Rename").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun shiftF10OpensItFromTheKeyboard() {
        showContextMenu()
        compose.onNodeWithTag("area").performSemanticsAction(SemanticsActions.RequestFocus)
        compose.onNodeWithTag("area").performKeyInput { withKeyDown(Key.ShiftLeft) { pressKey(Key.F10) } }
        compose.onNodeWithText("Rename").assertExists()
    }

    @Test
    fun disabledItDoesNothing() {
        showContextMenu(enabled = false)
        compose.onNodeWithText("Open file").performTouchInput { longClick() }
        compose.onNodeWithText("Rename").assertDoesNotExist()
        // The row inside is the row as usual: a long press on a plain clickable row is a click, then a tap another.
        compose.onNodeWithText("Open file").performClick()
        compose.runOnIdle { assertEquals(listOf("row", "row"), events) }
    }
}
