package com.qeetgroup.qeetrix.sample

import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import com.qeetgroup.qeetrix.sample.composition.CollectionsScreen
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * The Phase 5 integration flow, as a person would go through it: the members load; a search and a role chip
 * filter them, down to the no-results state and back; a long press starts a selection that is removed after a
 * confirmation and undone from the snackbar; a member's details open, with their accordion, and system Back
 * returns; a failed load offers Try again. All of the collection's state is the sample's own.
 */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h1400dp")
class CollectionsFlowTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private var exits = 0

    @Before
    fun animationsOff() {
        // Skeletons hold still, so the screen goes idle while it loads.
        Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
    }

    private fun showCollections() {
        compose.setContent { SampleDisplay(DisplaySettings(QeetrixThemeMode.Light)) { CollectionsScreen(onExit = { exits++ }) } }
        finishLoading()
    }

    /** The sample's simulated load waits; the test clock moves past it. */
    private fun finishLoading() {
        compose.mainClock.advanceTimeBy(LOAD_MILLIS)
        compose.waitForIdle()
    }

    private fun systemBack() {
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
    }

    @Test
    fun membersLoadThenASearchAndARoleFilterThem() {
        showCollections()
        compose.onNodeWithText("Asha Rao").assertExists()
        compose.onNode(isHeading() and hasText("Engineering")).assertExists()

        compose.onNode(hasSetTextAction()).performTextInput("grace")
        compose.onNodeWithText("Grace Hopper").assertExists()
        compose.onNodeWithText("Asha Rao").assertDoesNotExist()

        compose.onNode(hasSetTextAction()).performTextReplacement("nobody")
        compose.onNode(isHeading() and hasText("No members found")).assertExists()
        compose.onNodeWithText("Clear search and filters").performClick()
        compose.onNodeWithText("Asha Rao").assertExists()

        val ownerChip = hasText("Owner") and SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
        compose.onNode(ownerChip).performClick()
        compose.onNode(ownerChip).assertIsSelected()
        compose.onNodeWithText("Neha Gupta").assertExists()
        compose.onNodeWithText("Ada Lovelace").assertDoesNotExist()
    }

    @Test
    fun aLongPressSelectsAndRemovalCanBeUndone() {
        showCollections()
        compose.onNodeWithText("Kavya Iyer").performTouchInput { longClick() }
        compose.onNode(isHeading() and hasText("1 selected")).assertExists()
        compose.onNodeWithText("Kavya Iyer").assertIsSelected()
        // While selecting, a tap toggles instead of opening.
        compose.onNodeWithText("Meera Pillai").performClick()
        compose.onNode(isHeading() and hasText("2 selected")).assertExists()

        compose.onNodeWithText("Remove").performClick()
        compose.onNode(isHeading() and hasText("Remove 2 people?")).assertExists()
        compose.mainClock.autoAdvance = false
        compose.onNode(hasText("Remove") and hasAnyAncestor(isDialog())).performClick()
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeBy(1_000)
        compose.onNodeWithText("Kavya Iyer").assertDoesNotExist()
        compose.onNodeWithText("2 people removed").assertExists()
        compose.onNodeWithText("Undo").performClick()
        compose.mainClock.autoAdvance = true
        compose.onNodeWithText("Kavya Iyer").assertExists()
        compose.onNodeWithText("Meera Pillai").assertExists()
        compose.onNode(isHeading() and hasText("Members")).assertExists()
    }

    @Test
    fun backClearsASelectionBeforeLeaving() {
        showCollections()
        compose.onNodeWithText("Asha Rao").performTouchInput { longClick() }
        compose.onNode(isHeading() and hasText("1 selected")).assertExists()
        systemBack()
        compose.onNode(isHeading() and hasText("Members")).assertExists()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.runOnIdle { assertEquals(1, exits) }
    }

    @Test
    fun aMembersDetailsOpenAndSystemBackReturns() {
        showCollections()
        compose.onNodeWithText("Ada Lovelace").performClick()
        compose.onNode(isHeading() and hasText("Ada Lovelace")).assertExists()
        compose.onNode(hasText("Email") and hasText("ada@example.com")).assertExists()
        // Permissions starts open; Devices opens on a tap.
        compose.onNodeWithText("Create and edit projects; invite viewers.").assertExists()
        compose.onNode(isHeading() and hasText("Devices")).performClick()
        compose.onNodeWithText("MacBook Air", substring = true).assertExists()
        compose.onNodeWithText("Joined the workspace").assertExists()

        systemBack()
        compose.onNode(isHeading() and hasText("Members")).assertExists()
        compose.onNodeWithText("Ada Lovelace").assertExists()
    }

    @Test
    fun aFailedLoadIsAnnouncedAndOffersTryAgain() {
        showCollections()
        compose.onNodeWithContentDescription("More options").performClick()
        compose.onNodeWithText("Simulate a failed load").performClick()
        compose.onNode(hasContentDescription("Loading")).assertExists()
        finishLoading()
        compose.onNodeWithText("Members couldn't be loaded. Check your connection.").assertExists()
        compose.onNodeWithText("Asha Rao").assertDoesNotExist()
        compose.onNodeWithText("Try again").performClick()
        finishLoading()
        compose.onNodeWithText("Asha Rao").assertExists()
        // The filters the person set survive the reload.
        compose.onNodeWithText("Everyone").assertIsSelected()
    }
}

private const val LOAD_MILLIS = 1_000L
