package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The side sheet: at the end or start edge from `breakpoint.medium`, full height and `min(size, 100vw -
 * 48px)` wide as `.qx-drawer` is, mirrored in right to left; a bottom sheet below that width.
 */
@RunWith(RobolectricTestRunner::class)
class QeetrixSideSheetTest {
    @get:Rule
    val compose = createComposeRule()

    private var open by mutableStateOf(true)
    private var side by mutableStateOf(QeetrixSideSheetSide.End)
    private val requests = mutableListOf<String>()

    private fun showSheet(
        size: QeetrixSheetSize = QeetrixSheetSize.Md,
        dismissible: Boolean = true,
    ) {
        compose.setQeetrix {
            QeetrixSideSheet(
                open = open,
                onOpenChange = {
                    requests += "open=$it"
                    open = it
                },
                title = "Edit member",
                description = "Changes apply at the next sign-in.",
                side = side,
                size = size,
                dismissible = dismissible,
                footer = { QeetrixButton("Save", onClick = { open = false }) },
            ) { QeetrixButton("Make admin", onClick = {}) }
        }
    }

    /** The sheet's window, and its pane (title, body and footer) within it. */
    private fun window(): Rect = compose.onAllNodes(isRoot())[1].fetchSemanticsNode().boundsInRoot

    private fun title(): Rect = compose.onNode(isHeading() and hasText("Edit member")).fetchSemanticsNode().boundsInRoot

    private fun footer(): Rect = compose.onNodeWithText("Save").fetchSemanticsNode().boundsInRoot

    private fun px(dp: Float) = with(compose.density) { dp.dp.toPx() }

    @Test
    @Config(qualifiers = "w1280dp-h800dp")
    fun atTheEndItHugsTheRightEdgeInLeftToRight() {
        showSheet()
        val window = window()
        // The title starts space.inset.xl inside the sheet, which is 560 dp wide at the window's right edge.
        assertEquals(window.right - px(560f) + px(24f), title().left, 1f)
        // Full height: the footer's button sits near the window's bottom (above the footer's padding and the insets).
        assertTrue(
            "the footer is at the sheet's bottom (${footer().bottom} of ${window.bottom})",
            footer().bottom > window.bottom - px(120f),
        )
    }

    /** The body's first control: its start edge is `space.inset.xl` inside the sheet's start edge. */
    private fun control(): Rect = compose.onNodeWithText("Make admin").fetchSemanticsNode().boundsInRoot

    @Test
    @Config(qualifiers = "w1280dp-h800dp")
    fun atTheStartItHugsTheLeftEdgeInLeftToRight() {
        side = QeetrixSideSheetSide.Start
        showSheet()
        assertEquals(window().left + px(24f), control().left, 1f)
    }

    /**
     * Which way each edge lies in each direction. Right to left is checked here as a rule and on a device: a dialog
     * window takes its direction from the system, not from the app's composition, and Robolectric's first layout of the
     * sheet's window stayed left to right even under an Arabic locale.
     */
    @Test
    fun theEndIsTheLeftEdgeInRightToLeft() {
        assertEquals(1, SheetEdge.End.towardsEdge(rightToLeft = false))
        assertEquals(-1, SheetEdge.End.towardsEdge(rightToLeft = true))
        assertEquals(-1, SheetEdge.Start.towardsEdge(rightToLeft = false))
        assertEquals(1, SheetEdge.Start.towardsEdge(rightToLeft = true))
        assertEquals(1, SheetEdge.Bottom.towardsEdge(rightToLeft = true))
    }

    @Test
    @Config(qualifiers = "w640dp-h800dp")
    fun itAlwaysLeaves48DpOfTheScreenBesideIt() {
        showSheet(size = QeetrixSheetSize.Lg)
        // .qx-drawer[data-size="lg"]: min(800px, 100vw - 48px) = 592 dp in a 640 dp window.
        assertEquals(window().left + px(48f) + px(24f), title().left, 1f)
    }

    @Test
    @Config(qualifiers = "w1280dp-h800dp")
    fun aSwipeTowardsItsEdgeOrATapOnTheScrimAsksToClose() {
        showSheet()
        compose.onNodeWithText("Edit member").performTouchInput { swipeRight(endX = right + 2_000f) }
        compose.onNodeWithText("Make admin").assertDoesNotExist()
        compose.runOnIdle { open = true }
        compose.onAllNodes(isRoot())[1].performTouchInput { click(Offset(40f, centerY)) }
        compose.onNodeWithText("Make admin").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("open=false", "open=false"), requests) }
    }

    @Test
    @Config(qualifiers = "w1280dp-h800dp")
    fun notDismissibleASwipeLeavesItWhereItIs() {
        showSheet(dismissible = false)
        val title = title()
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
        compose.onNodeWithText("Edit member").performTouchInput { swipeRight(endX = right + 2_000f) }
        compose.waitForIdle()
        assertEquals(title.left, title().left, 1f)
        compose.runOnIdle { assertEquals(emptyList<String>(), requests) }
    }

    @Test
    @Config(qualifiers = "w1280dp-h800dp")
    fun aStartSheetSwipesAwayToTheLeft() {
        side = QeetrixSideSheetSide.Start
        showSheet()
        compose.onNodeWithText("Edit member").performTouchInput { swipeLeft(endX = left - 2_000f) }
        compose.onNodeWithText("Make admin").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("open=false"), requests) }
    }

    @Test
    fun onAPhoneItIsABottomSheet() {
        showSheet()
        val window = window()
        assertTrue("the title is in the window's lower part (${title().top} of ${window.bottom})", title().top > window.height * 0.3f)
        assertEquals(window.left + px(24f), title().left, 1f)
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Make admin").assertDoesNotExist()
    }

    @Test
    fun sidesAreTheDrawers() {
        assertEquals(listOf("start", "end"), QeetrixSideSheetSide.entries.map { it.value })
    }
}
