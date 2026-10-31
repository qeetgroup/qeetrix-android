package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.espresso.Espresso
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The bottom sheet: presented by the app's flag, every way of closing it reported, nothing left behind. */
@RunWith(RobolectricTestRunner::class)
class QeetrixBottomSheetTest {
    @get:Rule
    val compose = createComposeRule()

    private var open by mutableStateOf(false)
    private val requests = mutableListOf<String>()

    private fun showSheet(dismissible: Boolean = true) {
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
                footer = { QeetrixButton("Show results", onClick = { open = false }) },
            ) {
                QeetrixButton("Overdue", onClick = { requests += "overdue" })
            }
        }
    }

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
    fun theCloseButtonAndBackAskToClose() {
        showSheet()
        compose.runOnIdle { open = true }
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { open = true }
        compose.onNodeWithText("Overdue").assertExists()
        Espresso.pressBack()
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("open=false", "open=false"), requests) }
    }

    @Test
    fun theHandleOffersAccessibilityServicesADismissAction() {
        showSheet()
        compose.runOnIdle { open = true }
        compose
            .onNodeWithContentDescription("Drag handle")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Dismiss)
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("open=false"), requests) }
    }

    @Test
    fun notDismissibleItHasNoCloseButtonAndTheHandleCannotCloseItButBackCan() {
        showSheet(dismissible = false)
        compose.runOnIdle { open = true }
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
        compose.onNodeWithContentDescription("Drag handle").performSemanticsAction(SemanticsActions.Dismiss)
        compose.onNodeWithText("Overdue").assertExists()
        compose.runOnIdle { assertEquals(emptyList<String>(), requests) }
        Espresso.pressBack()
        compose.onNodeWithText("Overdue").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf("open=false"), requests) }
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
