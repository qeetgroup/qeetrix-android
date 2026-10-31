package com.qeetgroup.qeetrix.components.drawer

import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.test.espresso.Espresso
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

/**
 * The bottom sheet: presented by the app's flag, every way of closing it reported, nothing left behind; its
 * gestures, its motion (drawer.css, QTRX-198) and its handle, which is only a mark.
 */
@RunWith(RobolectricTestRunner::class)
class QeetrixBottomSheetTest {
    @get:Rule
    val compose = createComposeRule()

    private var open by mutableStateOf(false)
    private val requests = mutableListOf<String>()

    @After
    fun restore() {
        Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        shadowOf(RuntimeEnvironment.getApplication().getSystemService(AccessibilityManager::class.java)).setTouchExplorationEnabled(false)
    }

    private fun showSheet(
        dismissible: Boolean = true,
        partiallyExpandable: Boolean = false,
        rows: Int = 0,
    ) {
        compose.setQeetrix {
            QeetrixBottomSheet(
                open = open,
                onOpenChange = {
                    requests += "open=$it"
                    open = it
                },
                title = "Filter invoices",
                description = "Show only the invoices you need.",
                dismissible = dismissible,
                partiallyExpandable = partiallyExpandable,
                footer = { QeetrixButton("Show results", onClick = { open = false }) },
            ) {
                QeetrixButton("Overdue", onClick = { requests += "overdue" })
                Column { repeat(rows) { QeetrixText("Invoice ${it + 1}") } }
            }
        }
    }

    /** The title's top on screen, in pixels. */
    private fun titleTop(): Float =
        compose
            .onNode(isHeading() and hasText("Filter invoices"))
            .fetchSemanticsNode()
            .positionInRoot.y

    @Test
    fun itShowsWhileTheAppSaysSoAndLeavesWhenItDoesNot() {
        showSheet()
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { open = true }
        compose.onNode(isHeading() and hasText("Filter invoices")).assertExists()
        compose.onNode(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Filter invoices")).assertExists()
        compose.onNodeWithText("Overdue").performClick()
        compose.onNodeWithText("Show results").performClick()
        // Closed by the app: it slides away and leaves the composition, without asking the app again.
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("overdue"), requests) }
    }

    @Test
    fun theCloseButtonBackAndEscapeAskToClose() {
        showSheet()
        compose.runOnIdle { open = true }
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Overdue").assertExists()
        Espresso.pressBack()
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Overdue").performSemanticsAction(SemanticsActions.RequestFocus)
        compose.onNodeWithText("Overdue").performKeyInput { pressKey(Key.Escape) }
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("open=false", "open=false", "open=false"), requests) }
    }

    @Test
    fun aTapOnTheScrimOrASwipeDownAsksToClose() {
        showSheet()
        compose.runOnIdle { open = true }
        // The sheet's window is the last root; its top is scrim.
        compose.onAllNodes(isRoot())[1].performTouchInput { click(Offset(centerX, 8f)) }
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Filter invoices").performTouchInput { swipeDown(endY = centerY + 1_000f) }
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("open=false", "open=false"), requests) }
    }

    @Test
    fun aTapOnTheSheetItselfIsNotATapOutside() {
        showSheet()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Show only the invoices you need.").performClick()
        compose.onNodeWithText("Overdue").assertExists()
        compose.runOnIdle { assertEquals(emptyList<String>(), requests) }
    }

    @Test
    fun anAppThatKeepsItOpenAfterASwipeGetsItBack() {
        compose.setQeetrix {
            QeetrixBottomSheet(open = true, onOpenChange = { requests += "open=$it" }, title = "Filter invoices") {
                QeetrixButton("Overdue", onClick = {})
            }
        }
        val top = titleTop()
        compose.onNodeWithText("Filter invoices").performTouchInput { swipeDown(endY = centerY + 1_000f) }
        compose.waitForIdle()
        assertEquals(top, titleTop(), 1f)
        compose.runOnIdle { assertEquals(listOf("open=false"), requests) }
    }

    @Test
    fun notDismissibleItHasNoCloseButtonAndOnlyBackAsksToClose() {
        showSheet(dismissible = false)
        compose.runOnIdle { open = true }
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
        val top = titleTop()
        compose.onAllNodes(isRoot())[1].performTouchInput { click(Offset(centerX, 8f)) }
        compose.onNodeWithText("Filter invoices").performTouchInput { swipeDown(endY = centerY + 1_000f) }
        compose.waitForIdle()
        // Swiped, it settles back where it was.
        compose.onNodeWithText("Overdue").assertExists()
        assertEquals(top, titleTop(), 1f)
        compose.runOnIdle { assertEquals(emptyList<String>(), requests) }
        Espresso.pressBack()
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("open=false"), requests) }
    }

    @Test
    fun theHandleIsOnlyAMark() {
        showSheet()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Overdue").assertExists()
        // Nothing for an accessibility service to land on, and nothing under 48 dp to tap (once finding Q-6).
        assertEquals(0, compose.onAllNodes(hasContentDescription("Drag handle"), useUnmergedTree = true).fetchSemanticsNodes().size)
    }

    /**
     * The sheet's window is open and half-way; the clock stops; focus moving in expands it, then the app closes it.
     * [animated]: it rises over `motion.duration.slow` and leaves over `motion.duration.normal`, each starting a few
     * frames after the change that sets it off; otherwise both happen at once. The clock moves a frame at a time, as a
     * device's does, and the main thread runs between frames: an effect's coroutine resumes there.
     */
    private fun expandThenClose(animated: Boolean) {
        showSheet(partiallyExpandable = true, rows = 60)
        compose.runOnIdle { open = true }
        val window =
            compose
                .onAllNodes(isRoot())[1]
                .fetchSemanticsNode()
                .size.height
        val halfWay = titleTop()
        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Overdue").performSemanticsAction(SemanticsActions.RequestFocus)
        frames(if (animated) QeetrixMotion.DurationSlow / 2L else FRAMES)
        val partWay = titleTop()
        frames(QeetrixMotion.DurationSlow + FRAMES)
        val expanded = titleTop()
        assertTrue("expanded ($expanded of $window)", expanded < window * 0.25f)
        if (animated) {
            assertTrue(
                "half-way through motion.duration.slow it is still rising ($partWay, from $halfWay to $expanded)",
                partWay > expanded + 1f && partWay < halfWay - 1f,
            )
        } else {
            assertEquals("at once", expanded, partWay, 1f)
        }

        compose.runOnIdle { open = false }
        if (animated) {
            frames(QeetrixMotion.DurationNormal * 3L / 4L + FRAMES)
            assertTrue("three quarters through motion.duration.normal it is leaving (${titleTop()})", titleTop() > expanded + 1f)
            frames(QeetrixMotion.DurationNormal / 4L + FRAMES)
        } else {
            frames(FRAMES)
        }
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.mainClock.autoAdvance = true
    }

    /** At least [milliseconds] of the paused clock, a frame at a time, with the main thread idle after each. */
    private fun frames(milliseconds: Long) =
        repeat(((milliseconds + 15) / 16).toInt()) {
            compose.mainClock.advanceTimeByFrame()
            compose.waitForIdle()
        }

    @Test
    fun itRisesOverTheSlowDurationAndLeavesOverTheNormalOne() = expandThenClose(animated = true)

    @Test
    fun underReducedMotionItMovesAtOnce() {
        Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        expandThenClose(animated = false)
    }

    @Test
    fun aTallPartiallyExpandableSheetOpensHalfWayAndExpandsWhenDraggedUp() {
        showSheet(partiallyExpandable = true, rows = 60)
        compose.runOnIdle { open = true }
        val window =
            compose
                .onAllNodes(isRoot())[1]
                .fetchSemanticsNode()
                .size.height
        val halfWay = titleTop()
        assertTrue("half-open, the title is in the window's lower half ($halfWay of $window)", halfWay > window * 0.45f)
        compose.onNodeWithText("Filter invoices").performTouchInput { swipeUp(endY = centerY - window) }
        compose.waitForIdle()
        assertTrue("expanded, the title is in the window's top (${titleTop()} of $window)", titleTop() < window * 0.25f)
    }

    @Test
    fun focusMovingIntoAHalfOpenSheetExpandsIt() {
        showSheet(partiallyExpandable = true, rows = 60)
        compose.runOnIdle { open = true }
        val window =
            compose
                .onAllNodes(isRoot())[1]
                .fetchSemanticsNode()
                .size.height
        compose.onNodeWithText("Overdue").performSemanticsAction(SemanticsActions.RequestFocus)
        compose.waitForIdle()
        assertTrue("focused, the sheet is expanded (${titleTop()} of $window)", titleTop() < window * 0.25f)
    }

    @Test
    fun withTouchExplorationOnAPartiallyExpandableSheetOpensFully() {
        shadowOf(RuntimeEnvironment.getApplication().getSystemService(AccessibilityManager::class.java)).setTouchExplorationEnabled(true)
        showSheet(partiallyExpandable = true, rows = 60)
        compose.runOnIdle { open = true }
        val window =
            compose
                .onAllNodes(isRoot())[1]
                .fetchSemanticsNode()
                .size.height
        assertTrue("with TalkBack, nothing is left below the window (${titleTop()} of $window)", titleTop() < window * 0.25f)
        compose.onAllNodesWithText("Invoice 60").fetchSemanticsNodes().let { assertEquals(1, it.size) }
    }

    @Test
    fun openingAgainWhileItClosesEndsWithTheSheetOpen() {
        showSheet()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Overdue").assertExists()
        compose.mainClock.autoAdvance = false
        compose.runOnIdle { open = false }
        compose.mainClock.advanceTimeByFrame()
        compose.runOnIdle { open = true }
        compose.mainClock.autoAdvance = true
        compose.onNodeWithText("Overdue").assertExists()
    }

    @Test
    fun sizesAreTheDrawers() {
        assertEquals(listOf("sm", "md", "lg"), QeetrixBottomSheetSize.entries.map { it.value })
        assertEquals(listOf(400f, 560f, 800f), QeetrixBottomSheetSize.entries.map { it.width.value })
    }
}

/** A few frames' slack, in milliseconds, for the frames that compose and measure before anything moves. */
private const val FRAMES = 64L
