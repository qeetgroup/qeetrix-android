package com.qeetgroup.qeetrix.components.actionbar

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

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

    private fun showAppLockOffer(
        arrangement: QeetrixBottomActionBarArrangement,
        fontScale: Float = 1f,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                QeetrixTheme {
                    QeetrixBottomActionBar(arrangement = arrangement) {
                        QeetrixButton("Not now", onClick = {}, variant = QeetrixButtonVariant.Ghost, fullWidth = true)
                        QeetrixButton("Continue", onClick = {}, icon = QeetrixIconName.ArrowRight, fullWidth = true)
                    }
                }
            }
        }
    }

    /** The ghost action at its own width at the start, the primary filling the rest, gap.sm between. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun theGhostKeepsItsWidthAndThePrimaryFillsTheRest() {
        showAppLockOffer(QeetrixBottomActionBarArrangement.PrimaryFills)
        val window = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val notNow = compose.onNodeWithText("Not now").fetchSemanticsNode().boundsInRoot
        val next = compose.onNodeWithText("Continue").fetchSemanticsNode().boundsInRoot
        val inset = with(compose.density) { QeetrixSpace.InsetLg.toPx() }
        val gap = with(compose.density) { QeetrixSpace.GapSm.toPx() }
        assertEquals(window.left + inset, notNow.left, 1f)
        assertEquals(window.right - inset, next.right, 1f)
        assertEquals(gap, next.left - notNow.right, 1f)
        assertTrue(
            "the ghost is narrower than an equal share (${notNow.width} of ${window.width})",
            notNow.width < (window.width - 2 * inset - gap) / 2,
        )
        assertEquals(notNow.top, next.top, 1f)
    }

    /** The primary full width on top, the ghost centred under it at its own width. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun stackedPutsThePrimaryOverACentredGhost() {
        showAppLockOffer(QeetrixBottomActionBarArrangement.Stacked)
        val window = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val notNow = compose.onNodeWithText("Not now").fetchSemanticsNode().boundsInRoot
        val next = compose.onNodeWithText("Continue").fetchSemanticsNode().boundsInRoot
        val inset = with(compose.density) { QeetrixSpace.InsetLg.toPx() }
        val gap = with(compose.density) { QeetrixSpace.GapSm.toPx() }
        assertEquals(window.width - 2 * inset, next.width, 1f)
        assertEquals(gap, notNow.top - next.bottom, 1f)
        assertEquals(window.center.x, notNow.center.x, 1f)
        assertTrue(notNow.width < next.width / 2)
    }

    /** At 200 % in a 320 dp window the two no longer fit in a row, so they stack full width, nothing cut. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    @Config(qualifiers = "w320dp-h640dp")
    fun atLargeTextPrimaryFillsStacks() {
        showAppLockOffer(QeetrixBottomActionBarArrangement.PrimaryFills, fontScale = 2f)
        val notNow = compose.onNodeWithText("Not now").fetchSemanticsNode().boundsInRoot
        val next = compose.onNodeWithText("Continue").fetchSemanticsNode().boundsInRoot
        assertEquals(notNow.width, next.width, 1f)
        assertTrue("stacked in the order given", notNow.bottom <= next.top)
    }
}
