package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.test.espresso.Espresso
import com.qeetgroup.qeetrix.components.button.QeetrixButton
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
    private var open by mutableStateOf(true)

    private fun showDeleteDialog() {
        compose.setQeetrix {
            QeetrixAlertDialog(
                open = open,
                onOpenChange = {
                    events += "open=$it"
                    open = it
                },
                title = "Delete this workspace?",
                description = "Its projects and files are removed for everyone.",
                actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { events += "delete" }),
            )
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
        // The destructive choice comes with Cancel, added by the dialog; a danger dialog has no close button.
        compose.onNodeWithText("Cancel").assertExists()
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
    }

    @Test
    fun anActionClosesTheDialogFirstThenRunsOnce() {
        showDeleteDialog()
        compose.onNodeWithText("Delete workspace").performClick()
        compose.runOnIdle { assertEquals(listOf("open=false", "delete"), events) }
        compose.onNodeWithText("Delete workspace").assertDoesNotExist()
    }

    @Test
    fun aRapidSecondTapDoesNotRunTheActionAgain() {
        var deletes = 0
        compose.setQeetrix {
            // The app keeps it open, so the same dialog receives both taps.
            QeetrixAlertDialog(
                open = true,
                onOpenChange = {},
                title = "Delete?",
                actions = listOf(QeetrixDialogAction("Delete", QeetrixDialogActionRole.Destructive) { deletes++ }),
            )
        }
        compose.onNodeWithText("Delete").performClick()
        compose.onNodeWithText("Delete").performClick()
        compose.runOnIdle { assertEquals(1, deletes) }
    }

    @Test
    fun cancelAndBackOnlyAskToClose() {
        showDeleteDialog()
        compose.onNodeWithText("Cancel").performClick()
        compose.runOnIdle {
            assertEquals(listOf("open=false"), events)
            events.clear()
            open = true
        }
        compose.onNodeWithText("Delete workspace").assertExists()
        // Not dismissible, and still closed by Back: Android never traps Back.
        Espresso.pressBack()
        compose.runOnIdle { assertEquals(listOf("open=false"), events) }
        compose.onNodeWithText("Delete workspace").assertDoesNotExist()
    }

    @Test
    fun aTapOutsideIsNeverAnAnswerEvenWhenDismissible() {
        compose.setQeetrix {
            QeetrixAlertDialog(
                open = true,
                onOpenChange = { events += "open=$it" },
                title = "Send the invoice now?",
                actions = listOf(QeetrixDialogAction("Send") { events += "send" }),
                intent = QeetrixDialogIntent.Warning,
            )
        }
        compose.onNode(isDialog()).performTouchInput { click(Offset(4f, 4f)) }
        compose.runOnIdle { assertEquals(emptyList<String>(), events) }
        // A dismissible warning has the close button.
        compose.onNodeWithContentDescription("Close").performClick()
        compose.runOnIdle { assertEquals(listOf("open=false"), events) }
    }

    @Test
    fun aDismissedDialogOpenedAgainAnswersAgain() {
        showDeleteDialog()
        compose.onNodeWithText("Delete workspace").performClick()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Delete workspace").performClick()
        compose.runOnIdle { assertEquals(listOf("open=false", "delete", "open=false", "delete"), events) }
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun openedFromTheKeyboardFocusStartsOnCancel() {
        val trigger = FocusRequester()
        open = false
        compose.setQeetrix {
            QeetrixButton("Delete…", onClick = { open = true }, modifier = Modifier.focusRequester(trigger))
            QeetrixConfirmationDialog(
                open = open,
                onOpenChange = { open = it },
                title = "Revoke this API key?",
                confirmLabel = "Revoke key",
                onConfirm = {},
                intent = QeetrixDialogIntent.Danger,
            )
        }
        compose.runOnIdle { trigger.requestFocus() }
        compose.onNodeWithText("Delete…").assertIsFocused().performKeyInput { pressKey(Key.Enter) }
        compose.onNodeWithText("Cancel").assertIsFocused()
    }

    @Test
    fun aConfirmationRunsOnConfirmOnceAndCancelOnlyAsksToClose() {
        open = true
        compose.setQeetrix {
            QeetrixConfirmationDialog(
                open = open,
                onOpenChange = {
                    events += "open=$it"
                    open = it
                },
                title = "Remove Ada from the team?",
                confirmLabel = "Remove",
                onConfirm = { events += "remove" },
                intent = QeetrixDialogIntent.Danger,
            )
        }
        compose.onNodeWithText("Cancel").performClick()
        compose.runOnIdle {
            assertEquals(listOf("open=false"), events)
            open = true
        }
        compose.onNodeWithText("Remove").performClick()
        compose.runOnIdle { assertEquals(listOf("open=false", "open=false", "remove"), events) }
        compose.onNode(isDialog()).assertDoesNotExist()
    }
}
