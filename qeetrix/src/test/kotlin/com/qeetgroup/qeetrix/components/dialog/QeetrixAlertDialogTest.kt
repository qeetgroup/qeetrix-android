package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.Espresso
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The alert dialog in its window: what it shows, how each way of closing it reports, and actions that run once. */
@RunWith(RobolectricTestRunner::class)
class QeetrixAlertDialogTest {
    @get:Rule
    val compose = createComposeRule()

    private val events = mutableListOf<String>()
    private var showing by mutableStateOf(true)

    private fun showDeleteDialog() {
        compose.setQeetrix {
            if (showing) {
                QeetrixAlertDialog(
                    title = "Delete this workspace?",
                    message = "Its projects and files are removed for everyone.",
                    actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { events += "delete" }),
                    onDismissRequest = {
                        events += "dismiss"
                        showing = false
                    },
                )
            }
        }
    }

    @Test
    fun itOpensInADialogWindowWithItsTitleAsAHeadingAndPane() {
        showDeleteDialog()
        compose
            .onNode(isHeading() and hasText("Delete this workspace?") and hasAnyAncestor(isDialog()))
            .assertExists()
        compose.onNode(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Delete this workspace?")).assertExists()
        compose.onNodeWithText("Its projects and files are removed for everyone.").assertExists()
        // The destructive choice comes with Cancel, added by the dialog.
        compose.onNodeWithText("Cancel").assertExists()
    }

    @Test
    fun anActionClosesTheDialogFirstThenRunsOnce() {
        showDeleteDialog()
        compose.onNodeWithText("Delete workspace").performClick()
        compose.runOnIdle { assertEquals(listOf("dismiss", "delete"), events) }
        compose.onNodeWithText("Delete workspace").assertDoesNotExist()
    }

    @Test
    fun aRapidSecondTapDoesNotRunTheActionAgain() {
        var deletes = 0
        compose.setQeetrix {
            // The app keeps it shown, so the same dialog receives both taps.
            QeetrixAlertDialog(
                title = "Delete?",
                actions = listOf(QeetrixDialogAction("Delete", QeetrixDialogActionRole.Destructive) { deletes++ }),
                onDismissRequest = {},
            )
        }
        compose.onNodeWithText("Delete").performClick()
        compose.onNodeWithText("Delete").performClick()
        compose.runOnIdle { assertEquals(1, deletes) }
    }

    @Test
    fun cancelAndBackOnlyDismiss() {
        showDeleteDialog()
        compose.onNodeWithText("Cancel").performClick()
        compose.runOnIdle {
            assertEquals(listOf("dismiss"), events)
            events.clear()
            showing = true
        }
        compose.onNodeWithText("Delete workspace").assertExists()
        Espresso.pressBack()
        compose.runOnIdle { assertEquals(listOf("dismiss"), events) }
    }

    @Test
    fun aTapOutsideIsNotAnAnswer() {
        showDeleteDialog()
        compose.onNode(isDialog()).performTouchInput { click(Offset(4f, 4f)) }
        compose.runOnIdle { assertEquals(emptyList<String>(), events) }
        compose.onNodeWithText("Delete workspace").assert(hasAnyAncestor(isDialog()))
    }
}
