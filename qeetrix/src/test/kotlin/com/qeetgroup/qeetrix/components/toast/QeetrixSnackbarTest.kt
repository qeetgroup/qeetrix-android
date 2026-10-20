package com.qeetgroup.qeetrix.components.toast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** A screen's snackbar host: what is shown and announced, and how the action and close button end it. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSnackbarTest {
    @get:Rule
    val compose = createComposeRule()

    private val state = QeetrixSnackbarHostState()
    private lateinit var scope: CoroutineScope
    private val results = mutableListOf<QeetrixSnackbarResult>()

    private fun showHost() {
        compose.setQeetrix {
            scope = rememberCoroutineScope()
            Box(Modifier.fillMaxSize()) {
                QeetrixButton("Archive", onClick = {})
                QeetrixSnackbarHost(state, Modifier.align(Alignment.BottomCenter))
            }
        }
    }

    private fun show(
        message: String,
        type: QeetrixSnackbarType = QeetrixSnackbarType.Info,
        actionLabel: String? = null,
    ) {
        compose.runOnIdle { scope.launch { results += state.show(message, type, actionLabel = actionLabel, persistent = true) } }
    }

    @Test
    fun itIsAnnouncedPolitelyAndAnErrorAtOnce() {
        showHost()
        show("Conversation archived")
        compose
            .onNode(hasText("Conversation archived") and SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Polite))
        show("Couldn't send", QeetrixSnackbarType.Error)
        compose
            .onNode(hasText("Couldn't send") and SemanticsMatcher.keyIsDefined(SemanticsProperties.LiveRegion))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.LiveRegion, LiveRegionMode.Assertive))
        // The first was replaced, not queued.
        compose.onNodeWithText("Conversation archived").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf(QeetrixSnackbarResult.Dismissed), results) }
    }

    @Test
    fun theActionEndsItWithActionPerformed() {
        showHost()
        show("Conversation archived", actionLabel = "Undo")
        compose.onNodeWithText("Undo").performClick()
        compose.onNodeWithText("Conversation archived").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf(QeetrixSnackbarResult.ActionPerformed), results) }
    }

    @Test
    fun theCloseButtonAndTheDismissActionEndItAsDismissed() {
        showHost()
        show("Invoice sent", QeetrixSnackbarType.Success)
        compose.onNodeWithContentDescription("Close").performClick()
        compose.onNodeWithText("Invoice sent").assertDoesNotExist()
        show("Invoice sent again", QeetrixSnackbarType.Success)
        compose
            .onNode(hasText("Invoice sent again") and SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss))
            .performSemanticsAction(SemanticsActions.Dismiss)
        compose.onNodeWithText("Invoice sent again").assertDoesNotExist()
        compose.runOnIdle { assertEquals(listOf(QeetrixSnackbarResult.Dismissed, QeetrixSnackbarResult.Dismissed), results) }
    }
}
