package com.qeetgroup.qeetrix.components.actionbar

import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The bottom action bar's two layouts with the `fullWidth` buttons its KDoc asks for: shared on a phone, at the end
 * at their own width from `breakpoint.medium`. Tablet pass, 2026-10-31: on a 1280 dp window each `fullWidth` button
 * took the bar's whole width, so the row started off the window's start edge and Deny was pushed out of sight.
 */
@RunWith(RobolectricTestRunner::class)
class QeetrixBottomActionBarTest {
    @get:Rule
    val compose = createComposeRule()

    private fun showBar() {
        compose.setQeetrix {
            QeetrixBottomActionBar {
                QeetrixButton("Deny", onClick = {}, variant = QeetrixButtonVariant.Soft, fullWidth = true)
                QeetrixButton("Approve", onClick = {}, fullWidth = true)
            }
        }
    }

    @Test
    @Config(qualifiers = "w1280dp-h800dp")
    fun onAWideWindowTheActionsSitAtTheEndAtTheirOwnWidth() {
        showBar()
        val window = compose.onNode(isRoot()).fetchSemanticsNode().boundsInRoot
        val deny = compose.onNodeWithText("Deny").fetchSemanticsNode().boundsInRoot
        val approve = compose.onNodeWithText("Approve").fetchSemanticsNode().boundsInRoot
        val inset = with(compose.density) { QeetrixSpace.InsetLg.toPx() }
        val gap = with(compose.density) { QeetrixSpace.GapSm.toPx() }
        assertTrue("Deny starts inside the window ($deny in $window)", deny.left >= window.left)
        assertEquals("Approve ends space.inset.lg from the window's end", window.right - inset, approve.right, 1f)
        assertEquals("space.gap.sm between them", gap, approve.left - deny.right, 1f)
        val narrow = with(compose.density) { 200.dp.toPx() }
        assertTrue("each at its own width, not the bar's (${deny.width}, ${approve.width})", deny.width < narrow && approve.width < narrow)
    }

    @Test
    fun onAPhoneTheyShareTheWidth() {
        showBar()
        val window = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val deny = compose.onNodeWithText("Deny").fetchSemanticsNode().boundsInRoot
        val approve = compose.onNodeWithText("Approve").fetchSemanticsNode().boundsInRoot
        val inset = with(compose.density) { QeetrixSpace.InsetLg.toPx() }
        assertEquals(window.left + inset, deny.left, 1f)
        assertEquals(window.right - inset, approve.right, 1f)
        assertEquals(deny.width, approve.width, 1f)
    }
}
