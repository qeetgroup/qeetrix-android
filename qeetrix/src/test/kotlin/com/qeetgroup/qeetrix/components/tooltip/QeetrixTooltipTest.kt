package com.qeetgroup.qeetrix.components.tooltip

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Tooltips: an icon button's name on a long press, placed above it and kept inside the window. */
@RunWith(RobolectricTestRunner::class)
class QeetrixTooltipTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aLongPressShowsTheIconButtonsName() {
        compose.setQeetrix { QeetrixIconButton(QeetrixIconName.Copy, "Copy invoice ID", onClick = {}) }
        compose.onNodeWithText("Copy invoice ID", useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithContentDescription("Copy invoice ID").performTouchInput { longClick() }
        compose.onNodeWithText("Copy invoice ID", useUnmergedTree = true).assertExists()
    }

    /** Found on a device: centred over a button at the screen's edge, the tooltip was pressed against the edge. */
    @Test
    fun theTooltipKeepsAMarginInsideTheWindow() {
        val position = TooltipPosition(gap = 8, margin = 8)
        val window = IntSize(400, 800)
        val tooltip = IntSize(200, 40)
        // A button at the start edge: centring would put the tooltip at x −80.
        assertEquals(IntOffset(8, 152), position.calculatePosition(IntRect(0, 200, 40, 240), window, LayoutDirection.Ltr, tooltip))
        // At the end edge.
        assertEquals(392 - 200, position.calculatePosition(IntRect(360, 200, 400, 240), window, LayoutDirection.Ltr, tooltip).x)
        // No room above: below, the web's flip.
        assertEquals(48, position.calculatePosition(IntRect(100, 0, 140, 40), window, LayoutDirection.Ltr, tooltip).y)
        // Centred where there is room.
        assertEquals(20, position.calculatePosition(IntRect(100, 200, 140, 240), window, LayoutDirection.Ltr, tooltip).x)
    }
}
