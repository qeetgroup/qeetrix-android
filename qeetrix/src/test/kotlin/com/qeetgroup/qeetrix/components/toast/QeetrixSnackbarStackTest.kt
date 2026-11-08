package com.qeetgroup.qeetrix.components.toast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.qeetgroup.qeetrix.components.setQeetrix
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

/** A snackbar for work in progress (`promise`), and several at once from `breakpoint.medium`. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSnackbarStackTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aPromiseShowsItsWorkWithoutATimeLimitThenItsSuccess() =
        runTest {
            val state = QeetrixSnackbarHostState().apply { hostScope = backgroundScope }
            val work = CompletableDeferred<Int>()
            val promised =
                async {
                    state.promise(
                        "Sending the invoice…",
                        success = { "Invoice $it sent" },
                        error = { "Couldn't send" },
                    ) { work.await() }
                }
            runCurrent()
            val shown = state.current!!
            assertTrue(shown.pending)
            assertEquals("Sending the invoice…", shown.title)
            advanceTimeBy(600_000)
            assertTrue("pending work has no time limit", state.isShowing)
            work.complete(42)
            runCurrent()
            // The result comes back at once; the same snackbar now says how it went, for the usual time.
            assertEquals(42, promised.await())
            assertFalse(shown.pending)
            assertEquals("Invoice 42 sent", shown.title)
            assertEquals(QeetrixSnackbarTone.Success, shown.tone)
            advanceTimeBy(5_001)
            runCurrent()
            assertFalse(state.isShowing)
        }

    @Test
    fun aFailedPromiseShowsADangerSnackbarThatStaysAndRethrows() =
        runTest {
            val state = QeetrixSnackbarHostState().apply { hostScope = backgroundScope }
            val promised =
                async {
                    runCatching {
                        state.promise<Unit>("Sending…", success = { "Sent" }, error = { "Couldn't send: ${it.message}" }) {
                            throw IOException("offline")
                        }
                    }
                }
            runCurrent()
            assertTrue(promised.await().exceptionOrNull() is IOException)
            val shown = state.current!!
            assertEquals(QeetrixSnackbarTone.Danger, shown.tone)
            assertEquals("Couldn't send: offline", shown.title)
            advanceTimeBy(600_000)
            assertTrue("a danger snackbar stays until it is closed", state.isShowing)
            state.dismiss()
            runCurrent()
            assertFalse(state.isShowing)
        }

    @Test
    fun aCancelledPromiseLeavesWithoutAWord() =
        runTest {
            val state = QeetrixSnackbarHostState().apply { hostScope = backgroundScope }
            val job = launch { state.promise<Unit>("Syncing…", success = { "Synced" }, error = { "Sync failed" }) { awaitCancellation() } }
            runCurrent()
            assertTrue(state.isShowing)
            job.cancel()
            runCurrent()
            assertFalse(state.isShowing)
        }

    @Test
    fun replacedWhilePendingTheOutcomeStillAppears() =
        runTest {
            val state = QeetrixSnackbarHostState().apply { hostScope = backgroundScope }
            val work = CompletableDeferred<Unit>()
            launch { state.promise("Uploading…", success = { "Uploaded" }, error = { "Upload failed" }) { work.await() } }
            runCurrent()
            // On a phone one is shown at a time: a new snackbar replaces the pending one…
            val copied = async { state.show("Link copied") }
            runCurrent()
            assertEquals("Link copied", state.current!!.title)
            // …and when the work is done, its outcome replaces that in turn: feedback is never lost.
            work.complete(Unit)
            runCurrent()
            assertEquals(QeetrixSnackbarResult.Dismissed, copied.await())
            assertEquals("Uploaded", state.current!!.title)
        }

    @Test
    fun overTheLimitTheOldestGoes() =
        runTest {
            val state = QeetrixSnackbarHostState().apply { limit = 3 }
            val first = async { state.show("One") }
            runCurrent()
            launch { state.show("Two") }
            launch { state.show("Three") }
            runCurrent()
            assertEquals(listOf("One", "Two", "Three"), state.stack.map { it.title })
            launch { state.show("Four") }
            runCurrent()
            assertEquals(QeetrixSnackbarResult.Dismissed, first.await())
            assertEquals(listOf("Two", "Three", "Four"), state.stack.map { it.title })
            state.dismiss()
            runCurrent()
            assertFalse(state.isShowing)
        }

    /** Composes a host, then — once it has set its limit, as it has on a device before anything is shown — runs [start]. */
    private fun showHost(
        limit: Int? = null,
        start: (QeetrixSnackbarHostState, CoroutineScope) -> Unit,
    ) {
        val state = QeetrixSnackbarHostState()
        var scope: CoroutineScope? = null
        compose.setQeetrix {
            scope = rememberCoroutineScope()
            Box(Modifier.fillMaxSize()) { QeetrixSnackbarHost(state, Modifier.align(Alignment.BottomCenter), limit = limit) }
        }
        compose.runOnIdle { start(state, scope!!) }
    }

    @Test
    @Config(qualifiers = "w1280dp-h800dp")
    fun onAWideWindowThreeStackNewestLowest() {
        showHost { state, scope ->
            scope.launch { state.show("Invoice sent", timeout = kotlin.time.Duration.INFINITE) }
            scope.launch { state.show("Member invited", timeout = kotlin.time.Duration.INFINITE) }
            scope.launch { state.show("Report ready", timeout = kotlin.time.Duration.INFINITE) }
        }
        val first = compose.onNodeWithText("Invoice sent").fetchSemanticsNode().boundsInRoot
        val second = compose.onNodeWithText("Member invited").fetchSemanticsNode().boundsInRoot
        val third = compose.onNodeWithText("Report ready").fetchSemanticsNode().boundsInRoot
        assertTrue("newest lowest: ${first.top} < ${second.top} < ${third.top}", first.top < second.top && second.top < third.top)
    }

    @Test
    fun onAPhoneOneAtATimeUnlessTheAppSetsALimit() {
        showHost(limit = 2) { state, scope ->
            scope.launch { state.show("Invoice sent", timeout = kotlin.time.Duration.INFINITE) }
            scope.launch { state.show("Member invited", timeout = kotlin.time.Duration.INFINITE) }
            scope.launch { state.show("Report ready", timeout = kotlin.time.Duration.INFINITE) }
        }
        compose.onNodeWithText("Invoice sent").assertDoesNotExist()
        compose.onNodeWithText("Member invited").assertExists()
        compose.onNodeWithText("Report ready").assertExists()
    }

    @Test
    fun aPendingSnackbarIsDrawnAndTurnsIntoItsOutcome() {
        val work = CompletableDeferred<Unit>()
        showHost { state, scope ->
            scope.launch { state.promise("Saving…", success = { "Saved" }, error = { "Couldn't save" }) { work.await() } }
        }
        compose.onNodeWithText("Saving…").assertExists()
        compose.runOnIdle { work.complete(Unit) }
        compose.onNodeWithText("Saved").assertExists()
        compose.onNodeWithText("Saving…").assertDoesNotExist()
    }
}
