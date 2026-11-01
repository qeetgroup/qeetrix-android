package com.qeetgroup.qeetrix.components.emptystate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** Data states: the web's order, one "Loading" element that holds the error's height, an announced error with a retry. */
@RunWith(RobolectricTestRunner::class)
class QeetrixDataStateTest {
    @get:Rule
    val compose = createComposeRule()

    private var loading by mutableStateOf(false)
    private var error by mutableStateOf(false)
    private var empty by mutableStateOf(false)
    private var retries = 0

    private fun show() {
        compose.setQeetrix {
            QeetrixDataState(
                loading = loading,
                modifier = Modifier.testTag("region"),
                error = error,
                empty = empty,
                onRetry = { retries++ },
                emptyState = { QeetrixText("Nothing yet") },
            ) { QeetrixText("The data") }
        }
    }

    @Test
    fun statesFollowTheWebsOrderLoadingErrorEmptyContent() {
        loading = true
        error = true
        empty = true
        show()
        compose.onNode(hasContentDescription("Loading")).assertExists()
        compose.onNodeWithText("Couldn't load this").assertDoesNotExist()

        compose.runOnIdle { loading = false }
        compose.onNodeWithText("Couldn't load this").assertExists()
        compose.onNodeWithText("Nothing yet").assertDoesNotExist()

        compose.runOnIdle { error = false }
        compose.onNodeWithText("Nothing yet").assertExists()
        compose.onNodeWithText("The data").assertDoesNotExist()

        compose.runOnIdle { empty = false }
        compose.onNodeWithText("The data").assertExists()
        compose.onNode(hasContentDescription("Loading")).assertDoesNotExist()
    }

    @Test
    fun loadingIsOneElementNamedLoadingAndTheHeldErrorIsUnreachable() {
        loading = true
        show()
        val node = compose.onNode(hasContentDescription("Loading")).fetchSemanticsNode()
        assertEquals(0, node.children.size)
        compose.onAllNodesWithText("Try again").assertCountEquals(0)
    }

    /** Real layout: the loading region is as tall as the error state it would become. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun loadingHoldsTheErrorsHeight() {
        loading = true
        show()
        val waiting = compose.onNodeWithTag("region").getUnclippedBoundsInRoot()
        compose.runOnIdle {
            loading = false
            error = true
        }
        val failed = compose.onNodeWithTag("region").getUnclippedBoundsInRoot()
        assertEquals(failed.bottom - failed.top, waiting.bottom - waiting.top)
    }

    @Test
    fun anErrorIsAnnouncedAndOffersTryAgain() {
        error = true
        show()
        compose
            .onNode(
                hasText("Couldn't load this") and SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion),
            ).assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
        compose.onNodeWithText("Try again").performClick()
        assertEquals(1, retries)
    }

    @Test
    fun itsWordsAreTheAppsAndWithoutARetryThereIsNoButton() {
        compose.setQeetrix {
            QeetrixDataState(
                loading = false,
                error = true,
                errorTitle = "Members couldn't be loaded",
                errorDescription = "Check your connection.",
            ) {}
        }
        compose.onNodeWithText("Members couldn't be loaded").assertExists()
        compose.onNodeWithText("Check your connection.").assertExists()
        compose.onAllNodesWithText("Try again").assertCountEquals(0)
    }
}
