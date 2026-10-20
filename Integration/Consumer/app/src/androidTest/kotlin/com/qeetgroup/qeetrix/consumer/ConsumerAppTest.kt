package com.qeetgroup.qeetrix.consumer

import android.view.KeyEvent
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.core.content.res.ResourcesCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The consumer app on a device, using Qeetrix only through its Maven artifact: every assertion goes through the
 * public API's semantics, as an app's own tests would.
 */
@RunWith(AndroidJUnit4::class)
class ConsumerAppTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    private fun open(page: String) {
        compose.onNodeWithText(page).performClick()
        compose.waitForIdle()
    }

    private fun waitForText(text: String) = compose.waitUntil(5_000) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty() }

    private fun waitForNoText(text: String) = compose.waitUntil(5_000) { compose.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty() }

    @Test
    fun theFontsAndStringsResolveFromTheArtifact() {
        val context = compose.activity
        // The library's own R class, from the AAR: fonts and strings reach the app's resources.
        assertNotNull(ResourcesCompat.getFont(context, com.qeetgroup.qeetrix.R.font.qx_cal_sans_vf))
        assertNotNull(ResourcesCompat.getFont(context, com.qeetgroup.qeetrix.R.font.qx_fira_code_vf))
        assertEquals("Cancel", context.getString(com.qeetgroup.qeetrix.R.string.qx_cancel))
    }

    @Test
    fun foundationsRenderAndRespond() {
        compose.onNodeWithText("Qeetrix consumer").assertIsDisplayed()
        compose.onNodeWithContentDescription("Home icon").assertIsDisplayed()
        compose.onNodeWithContentDescription("Primary swatch").assertIsDisplayed()
        compose.onNodeWithText("Primary action").performClick()
        compose.onNodeWithText("Pressed 1 times").assertIsDisplayed()
        compose.onNode(hasSetTextAction() and hasText("Display name")).performTextInput("Ada")
        compose.onNodeWithText("Ada", useUnmergedTree = true).assertExists()
    }

    @Test
    fun theDarkThemeChangesTheCanvas() {
        // The screen's mean brightness (0–765) over a grid of pixels: the canvas and every surface follow the theme.
        fun canvas(): Int {
            val image = compose.onRoot().captureToImage().toPixelMap()
            val samples =
                (1..9).flatMap { x -> (1..9).map { y -> image[image.width * x / 10, image.height * y / 10] } }
            return samples.sumOf { ((it.red + it.green + it.blue) * 255).toDouble() }.div(samples.size).toInt()
        }
        val light = canvas()
        compose.onNodeWithText("Dark theme").performScrollTo().performClick()
        compose.onNodeWithText("Dark theme").assertIsOn()
        compose.waitForIdle()
        val dark = canvas()
        assertTrue("light screen $light should be brighter than dark $dark", light > dark + 200)
    }

    @Test
    fun largeTextGrowsText() {
        val before = compose.onNodeWithText("Body text in the Qeet typeface").fetchSemanticsNode().size.height
        compose.onNodeWithText("Large text").performScrollTo().performClick()
        compose.waitForIdle()
        val after = compose.onNodeWithText("Body text in the Qeet typeface").fetchSemanticsNode().size.height
        assertTrue("text height $before → $after", after > before * 1.5)
    }

    @Test
    fun rightToLeftMirrorsTheLayout() {
        // Scrolled into view first: on a short landscape screen the switch below pushes it off.
        fun titleLeft(): Float = compose.onNodeWithText("Body text in the Qeet typeface").performScrollTo().fetchSemanticsNode().boundsInRoot.left
        val ltr = titleLeft()
        compose.onNodeWithText("Right to left").performScrollTo().performClick()
        compose.onNodeWithText("Right to left").assertIsOn()
        compose.waitForIdle()
        val rtl = titleLeft()
        assertTrue("the text moves from the left ($ltr) to the right ($rtl)", rtl > ltr + 100)
    }

    @Test
    fun aFormValidatesAndSubmits() {
        open("Form")
        compose.onNodeWithText("Create account").performScrollTo().performClick()
        compose.onAllNodesWithText("Enter your work email").fetchSemanticsNodes().let { assertTrue(it.isNotEmpty()) }
        compose.onNodeWithText("Accept the terms to continue").assertExists()

        compose.onNode(hasSetTextAction() and hasText("Work email")).performScrollTo().performTextInput("ada@example.com")
        compose.onNode(hasSetTextAction() and hasText("Password")).performScrollTo().performTextInput("Synthetic-Passw0rd")
        compose.onNode(hasSetTextAction() and hasText("Verification code")).performScrollTo().performTextInput("123456")
        // The select opens its list in a popup; choosing closes it.
        compose.onNodeWithText("India").performScrollTo().performClick()
        compose.onNodeWithText("Germany").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Germany").assertExists()
        compose.onNodeWithText("Business").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Increase").performScrollTo().performClick()
        compose.onNodeWithText("I accept the terms").performScrollTo().performClick()
        compose.onNodeWithText("I accept the terms").assertIsOn()
        compose.onNodeWithText("Create account").performScrollTo().performClick()
        waitForText("Account created")
        compose.onNodeWithText("Accept the terms to continue").assertDoesNotExist()
    }

    @Test
    fun aDialogOpensAndRunsItsAction() {
        open("Overlays")
        compose.onNodeWithText("Open dialog").performClick()
        compose.onNode(isDialog()).assertExists()
        compose.onNodeWithText("Archive project?").assertIsDisplayed()
        compose.onNodeWithText("Archive").performClick()
        compose.onNode(isDialog()).assertDoesNotExist()
        compose.onNodeWithText("Last action: Archived").assertIsDisplayed()
    }

    @Test
    fun aConfirmationCancelsOnBack() {
        open("Overlays")
        compose.onNodeWithText("Delete workspace").performClick()
        compose.onNodeWithText("Delete workspace?").assertIsDisplayed()
        // Back goes to the window with input focus: wait until the dialog's window has taken it from the activity's.
        compose.waitUntil(5_000) { compose.runOnUiThread { !compose.activity.window.decorView.hasWindowFocus() } }
        InstrumentationRegistry.getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
        waitForNoText("Delete workspace?")
        compose.onNodeWithText("Last action: None").assertIsDisplayed()
    }

    @Test
    fun aBottomSheetPresentsAndCloses() {
        open("Overlays")
        compose.onNodeWithText("Open sheet").performClick()
        waitForText("Share invoice")
        compose.onNodeWithText("Copy link").performClick()
        waitForNoText("Share invoice")
        compose.onNodeWithText("Last action: Link copied").assertIsDisplayed()
    }

    @Test
    fun aMenuTabsAndASnackbarWork() {
        open("Overlays")
        compose.onNodeWithText("Activity").performClick()
        compose.onNodeWithText("Activity content").assertIsDisplayed()
        compose.onNodeWithText("More actions").performClick()
        compose.onNodeWithText("Duplicate").performClick()
        compose.onNodeWithText("Last action: Duplicated").assertIsDisplayed()
        compose.onNodeWithText("Show snackbar").performScrollTo().performClick()
        waitForText("Message archived")
        compose.onNodeWithText("Undo").performClick()
        waitForText("Last action: Undone")
    }

    @Test
    fun aCollectionSearchesFiltersAndSelects() {
        open("People")
        compose.onNodeWithText("Ada Lovelace 1").assertIsDisplayed()
        compose.onNode(hasSetTextAction() and hasText("Search people")).performTextInput("grace")
        compose.waitForIdle()
        compose.onNodeWithText("Ada Lovelace 1").assertDoesNotExist()
        compose.onNodeWithText("Grace Hopper 1").assertIsDisplayed().performClick()
        compose.onNodeWithText("Admins").performClick()
        waitForText("No people found")
        compose.onNodeWithText("Everyone").performClick()
        compose.onNodeWithText("Grace Hopper 1").assertIsDisplayed()
    }

    @Test
    fun theNavigationSwitchesPagesAndAdaptsToTheWindow() {
        open("People")
        compose.onNodeWithText("Search people").assertExists()
        open("Foundations")
        compose.onNodeWithText("Dark theme").performScrollTo().assertIsOff()
        val item = compose.onNodeWithText("People").fetchSemanticsNode().boundsInRoot
        val screen = compose.onRoot().fetchSemanticsNode().boundsInRoot
        // The adaptive scaffold: a bar below 600 dp of window width, a rail from 600 dp.
        if (compose.activity.resources.configuration.screenWidthDp < 600) {
            assertTrue("a bar at the bottom: $item in $screen", item.top > screen.height / 2)
        } else {
            assertTrue("a rail at the start: $item in $screen", item.right < screen.width / 4)
        }
    }
}
