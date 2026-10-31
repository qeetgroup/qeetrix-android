package com.qeetgroup.qeetrix.components.toast

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import androidx.compose.ui.platform.AccessibilityManager as ComposeAccessibilityManager

/** The snackbar lifecycle, apart from drawing: timing, replacement, repeats, cancellation, exactly one result. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSnackbarHostStateTest {
    @Test
    fun everyToneButDangerStaysFiveSeconds() =
        runTest {
            val state = QeetrixSnackbarHostState()
            for (tone in listOf(QeetrixSnackbarTone.Info, QeetrixSnackbarTone.Success, QeetrixSnackbarTone.Warning)) {
                val shown = async { state.show("Invoice sent", tone) }
                advanceTimeBy(4_999)
                assertTrue(state.isShowing)
                advanceTimeBy(2)
                assertEquals(QeetrixSnackbarResult.Dismissed, shown.await())
                assertFalse(state.isShowing)
            }
            // An action does not lengthen it: toast.tsx's one timeout.
            val undo = async { state.show("Conversation archived", actionLabel = "Undo") }
            advanceTimeBy(5_001)
            assertEquals(QeetrixSnackbarResult.Dismissed, undo.await())
        }

    @Test
    fun aDangerSnackbarStaysUntilItIsUsedOrClosedWhateverItsTimeout() =
        runTest {
            val state = QeetrixSnackbarHostState()
            val shown = async { state.show("Couldn't send", QeetrixSnackbarTone.Danger, timeout = 2.seconds) }
            advanceTimeBy(600_000)
            assertTrue(state.isShowing)
            state.dismiss()
            assertEquals(QeetrixSnackbarResult.Dismissed, shown.await())
        }

    @Test
    fun itsOwnTimeoutAndForever() =
        runTest {
            val state = QeetrixSnackbarHostState()
            val short = async { state.show("Copied", timeout = 2.seconds) }
            advanceTimeBy(2_001)
            assertEquals(QeetrixSnackbarResult.Dismissed, short.await())
            val forever = async { state.show("Offline", timeout = Duration.INFINITE) }
            advanceTimeBy(600_000)
            assertTrue(state.isShowing)
            state.dismiss()
            assertEquals(QeetrixSnackbarResult.Dismissed, forever.await())
        }

    @Test
    fun theActionEndsItOnceWithActionPerformed() =
        runTest {
            val state = QeetrixSnackbarHostState()
            val shown = async { state.show("Conversation archived", actionLabel = "Undo") }
            runCurrent()
            val current = state.current!!
            current.finish(QeetrixSnackbarResult.ActionPerformed)
            current.finish(QeetrixSnackbarResult.Dismissed) // a second tap, or the close button after it
            assertEquals(QeetrixSnackbarResult.ActionPerformed, shown.await())
            assertNull(state.current)
        }

    @Test
    fun aNewSnackbarReplacesTheOneOnScreenAndNothingQueues() =
        runTest {
            val state = QeetrixSnackbarHostState()
            val first = async { state.show("Saved") }
            runCurrent()
            val second = async { state.show("Couldn't save", QeetrixSnackbarTone.Danger) }
            runCurrent()
            assertEquals(QeetrixSnackbarResult.Dismissed, first.await())
            assertEquals("Couldn't save", state.current?.title)
            state.dismiss()
            assertEquals(QeetrixSnackbarResult.Dismissed, second.await())
            assertNull(state.current)
        }

    @Test
    fun aRepeatKeepsTheSnackbarAndRestartsItsTime() =
        runTest {
            val state = QeetrixSnackbarHostState()
            val first = async { state.show("Copied") }
            runCurrent()
            val onScreen = state.current
            advanceTimeBy(4_000)
            val again = async { state.show("Copied") }
            runCurrent()
            assertSame("the same snackbar, not announced again", onScreen, state.current)
            advanceTimeBy(4_000) // 8 s after the first, 4 s after the repeat
            assertTrue(state.isShowing)
            advanceTimeBy(1_001)
            assertEquals(QeetrixSnackbarResult.Dismissed, first.await())
            assertEquals(QeetrixSnackbarResult.Dismissed, again.await())
        }

    @Test
    fun aCancelledCallerTakesItsSnackbarWithIt() =
        runTest {
            val state = QeetrixSnackbarHostState()
            val screen = launch { state.show("Draft saved", timeout = Duration.INFINITE) }
            runCurrent()
            assertTrue(state.isShowing)
            screen.cancel()
            runCurrent()
            assertFalse(state.isShowing)
        }

    @Test
    fun accessibilitySettingsLengthenTheTimeAndAScreenReaderKeepsAnAction() {
        val doubled =
            object : ComposeAccessibilityManager {
                override fun calculateRecommendedTimeoutMillis(
                    originalTimeoutMillis: Long,
                    containsIcons: Boolean,
                    containsText: Boolean,
                    containsControls: Boolean,
                ): Long = originalTimeoutMillis * 2
            }
        val platform = ApplicationProvider.getApplicationContext<Context>().getSystemService(AccessibilityManager::class.java)
        val info = SnackbarRequest("Saved", QeetrixSnackbarTone.Info, null, null, QeetrixSnackbarHostState.DefaultTimeout)
        val action = SnackbarRequest("Archived", QeetrixSnackbarTone.Info, null, "Undo", QeetrixSnackbarHostState.DefaultTimeout)
        assertEquals(5_000L, SnackbarTiming.Unknown.timeoutMillis(info))
        assertEquals(10_000L, SnackbarTiming(doubled, platform).timeoutMillis(info))
        assertEquals(10_000L, SnackbarTiming(doubled, platform).timeoutMillis(action))
        shadowOf(platform).setTouchExplorationEnabled(true)
        assertNull("stays until used", SnackbarTiming(doubled, platform).timeoutMillis(action))
        assertEquals(10_000L, SnackbarTiming(doubled, platform).timeoutMillis(info))
    }

    @Test
    fun tonesAreTheWebs() {
        assertEquals(listOf("info", "success", "warning", "danger"), QeetrixSnackbarTone.entries.map { it.value })
        assertEquals(5.seconds, QeetrixSnackbarHostState.DefaultTimeout)
    }
}
