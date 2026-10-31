package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Swipe actions: reachable without a swipe, revealed on the side the row leaves in either direction, danger confirmed. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSwipeActionsTest {
    @get:Rule
    val compose = createComposeRule()

    private var pinned = 0
    private var removed = 0

    private fun row(direction: LayoutDirection = LayoutDirection.Ltr) {
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                QeetrixTheme(QeetrixThemeMode.Light) {
                    Box(Modifier.width(320.dp)) {
                        QeetrixSwipeActions(
                            Modifier.testTag("row"),
                            startAction = QeetrixSwipeAction("Pin", QeetrixIconName.MapPin, { pinned++ }, QeetrixSwipeActionTone.Brand),
                            endAction =
                                QeetrixSwipeAction(
                                    "Remove",
                                    QeetrixIconName.Trash,
                                    { removed++ },
                                    QeetrixSwipeActionTone.Danger,
                                    confirmTitle = "Remove Acme from this phone?",
                                ),
                        ) { QeetrixListItem("Acme", Modifier.testTag("item"), description = "priya@acme.in") }
                    }
                }
            }
        }
    }

    @Test
    fun everyActionIsAnAccessibilityActionOfTheRowAndDangerAsksFirst() {
        row()
        val actions = compose.onNodeWithTag("row").fetchSemanticsNode().config[SemanticsActions.CustomActions]
        assertEquals(listOf("Pin", "Remove"), actions.map { it.label })
        compose.runOnIdle { actions.first { it.label == "Pin" }.action() }
        assertEquals(1, pinned)
        compose.runOnIdle { actions.first { it.label == "Remove" }.action() }
        compose.onNodeWithText("Remove Acme from this phone?").assertExists()
        assertEquals(0, removed)
        compose.onNodeWithText("Remove").performClick()
        assertEquals(1, removed)
    }

    @Test
    fun swipingTowardTheStartRevealsTheEndActionAtTheRowsEnd() {
        row()
        val rest = compose.onNodeWithTag("item").getBoundsInRoot()
        compose.onNodeWithTag("row").performTouchInput { swipeLeft(startX = centerX + 60.dp.toPx(), endX = centerX - 60.dp.toPx()) }
        compose.waitForIdle()
        // The row settles open by the action's 88 dp, toward the start (left), and the action is at the end (right).
        assertEquals(rest.left - 88.dp, compose.onNodeWithTag("item").getBoundsInRoot().left)
        val action = compose.onNodeWithText("Remove").getBoundsInRoot()
        val width = compose.onNodeWithTag("row").getBoundsInRoot().right
        assertTrue("action at $action", action.left >= width - 88.dp - 1.dp && action.right <= width + 1.dp)
    }

    /**
     * Found on a device: the row moved with an offset that mirrors in right to left while the drag's deltas don't, so a
     * swipe toward the start moved the row the other way and uncovered a bare strip, the action hidden under the row.
     */
    @Test
    fun inRightToLeftTheRowFollowsTheFingerAndTheActionIsWhereItLeft() {
        row(LayoutDirection.Rtl)
        val rest = compose.onNodeWithTag("item").getBoundsInRoot()
        // Toward the start is to the right in right to left.
        compose.onNodeWithTag("row").performTouchInput { swipeRight(startX = centerX - 60.dp.toPx(), endX = centerX + 60.dp.toPx()) }
        compose.waitForIdle()
        assertEquals(rest.left + 88.dp, compose.onNodeWithTag("item").getBoundsInRoot().left)
        // The end action, at the row's end, which is the left.
        val action = compose.onNodeWithText("Remove").getBoundsInRoot()
        assertTrue("action at $action", action.left >= (-1).dp && action.right <= 88.dp + 1.dp)
    }

    @Test
    fun swipingAllTheWayRunsTheActionAfterItsConfirmation() {
        row()
        compose.onNodeWithTag("row").performTouchInput { swipeLeft(startX = right - 4.dp.toPx(), endX = left + 4.dp.toPx()) }
        compose.waitForIdle()
        compose.onNodeWithText("Remove Acme from this phone?").assertExists()
        compose.onNodeWithText("Remove").performClick()
        assertEquals(1, removed)
    }
}
