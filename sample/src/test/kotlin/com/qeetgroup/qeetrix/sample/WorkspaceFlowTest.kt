package com.qeetgroup.qeetrix.sample

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.qeetgroup.qeetrix.sample.composition.WorkspaceScreen
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The Phase 4 integration flow, as a person would go through it: choose a destination, open a project,
 * rename it in a dialog, delete it after a confirmation and undo from the snackbar, filter in a bottom sheet,
 * and go back with system Back. Navigation state is the sample's own; Qeetrix only presents it.
 */
@RunWith(RobolectricTestRunner::class)
class WorkspaceFlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private var exits = 0

    private fun showWorkspace() {
        compose.setContent { SampleDisplay(DisplaySettings(QeetrixThemeMode.Light)) { WorkspaceScreen(onExit = { exits++ }) } }
    }

    private fun systemBack() {
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
    }

    @Test
    fun destinationsDetailMenuDialogAndBack() {
        showWorkspace()
        compose.onNodeWithText("Inbox").performClick()
        compose.onNodeWithText("Ada mentioned you in Website refresh.").assertExists()
        val projectsTab = hasText("Projects") and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab)
        compose.onNode(projectsTab).performClick()
        compose.onNode(projectsTab).assertIsSelected()

        // A project, then its menu, then a dialog that validates before it closes.
        compose.onNodeWithText("Website refresh").performClick()
        compose.onNode(isHeading() and hasText("Website refresh")).assertExists()
        compose.onNodeWithContentDescription("More options").performClick()
        compose.onNodeWithText("Rename").performClick()
        val name = compose.onNode(hasSetTextAction() and hasAnyAncestor(isDialog()))
        name.performTextClearance()
        compose.onNodeWithText("Save").performClick()
        compose.onNodeWithText("Enter a name.").assertExists()
        name.performTextInput("Website relaunch")
        compose.mainClock.autoAdvance = false
        compose.onNodeWithText("Save").performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.onNode(isDialog()).assertDoesNotExist()
        compose.onNode(isHeading() and hasText("Website relaunch")).assertExists()
        compose.onNodeWithText("Project renamed").assertExists()
        compose.mainClock.autoAdvance = true

        // System Back closes the project first, and is the app's to handle.
        systemBack()
        compose.onNode(isHeading() and hasText("Projects")).assertExists()
        compose.onNodeWithText("Website relaunch").assertExists()
        assertEquals(0, exits)
    }

    @Test
    fun deleteAfterAConfirmationAndUndoFromTheSnackbar() {
        showWorkspace()
        compose.onNodeWithText("Quarterly report").performClick()
        compose.onNodeWithContentDescription("More options").performClick()
        compose.onNodeWithText("Delete project").performClick()
        // The confirmation, with Cancel first; the destructive answer deletes once.
        compose.onNode(isHeading() and hasText("Delete Quarterly report?")).assertExists()
        compose.mainClock.autoAdvance = false
        compose.onNode(hasText("Delete project") and hasAnyAncestor(isDialog())).performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(1_000)
        compose.onNodeWithText("Undo").assertExists().performClick()
        compose.mainClock.autoAdvance = true
        compose.onNodeWithText("Quarterly report").assertExists()
        compose.onNodeWithText("Project deleted").assertDoesNotExist()
    }

    @Test
    fun filterInABottomSheetThenLeave() {
        showWorkspace()
        compose.onNodeWithText("Filter").performClick()
        compose.onNode(isHeading() and hasText("Filter projects")).assertExists()
        compose.onNodeWithText("Active").performClick()
        compose.onNodeWithText("Show projects").performClick()
        compose.onNodeWithText("Show projects").assertDoesNotExist()
        compose.onNode(isHeading() and hasText("Active")).assertExists()
        // The top-level screen's back button is the sample's way out.
        compose.onNodeWithContentDescription("Back").performClick()
        compose.runOnIdle { assertTrue(exits == 1) }
    }
}
