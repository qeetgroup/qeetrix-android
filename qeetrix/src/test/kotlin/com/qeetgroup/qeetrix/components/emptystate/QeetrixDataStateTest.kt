package com.qeetgroup.qeetrix.components.emptystate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Data states: the web's branch order, one "Loading" element, an announced failure with a retry, no state of its own. */
@RunWith(RobolectricTestRunner::class)
class QeetrixDataStateTest {
    @get:Rule
    val compose = createComposeRule()

    private var loading by mutableStateOf(false)
    private var empty by mutableStateOf(false)
    private var failure by mutableStateOf<QeetrixDataFailure?>(null)

    private fun show() {
        compose.setQeetrix {
            QeetrixDataState(
                loading = loading,
                empty = empty,
                failure = failure,
                emptyContent = { QeetrixText("Nothing yet") },
            ) { QeetrixText("The data") }
        }
    }

    @Test
    fun branchesFollowTheWebsOrderLoadingFailureEmptyContent() {
        loading = true
        empty = true
        failure = QeetrixDataFailure("Couldn't load.")
        show()
        compose.onNode(hasContentDescription("Loading")).assertExists()
        compose.onNodeWithText("Couldn't load.").assertDoesNotExist()

        compose.runOnIdle { loading = false }
        compose.onNodeWithText("Couldn't load.").assertExists()
        compose.onNodeWithText("Nothing yet").assertDoesNotExist()

        compose.runOnIdle { failure = null }
        compose.onNodeWithText("Nothing yet").assertExists()
        compose.onNodeWithText("The data").assertDoesNotExist()

        compose.runOnIdle { empty = false }
        compose.onNodeWithText("The data").assertExists()
        compose.onNode(hasContentDescription("Loading")).assertDoesNotExist()
    }

    @Test
    fun loadingIsOneElementNamedLoading() {
        loading = true
        show()
        val node = compose.onNode(hasContentDescription("Loading")).fetchSemanticsNode()
        assertEquals(0, node.children.size)
    }

    @Test
    fun aFailureIsAnnouncedWithTheDefaultMessageAndOffersTryAgain() {
        var retries = 0
        failure = QeetrixDataFailure(onRetry = { retries++ })
        show()
        compose
            .onNodeWithText("Something went wrong while loading this data.")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
        compose.onNodeWithText("Try again").performClick()
        assertEquals(1, retries)
    }

    @Test
    fun withoutARetryThereIsNoButton() {
        failure = QeetrixDataFailure("Members couldn't be loaded.")
        show()
        compose.onNodeWithText("Members couldn't be loaded.").assertExists()
        compose.onAllNodesWithText("Try again").assertCountEquals(0)
        compose.onAllNodes(hasText("Something went wrong", substring = true)).assertCountEquals(0)
    }
}
