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
import androidx.compose.ui.platform.AccessibilityManager as ComposeAccessibilityManager

/** The snackbar lifecycle, apart from drawing: timing, replacement, repeats, cancellation, exactly one result. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSnackbarHostStateTest {
    @Test
    fun aConfirmationStaysFiveSecondsAndANeedToActTen() =
        runTest {
            val state = QeetrixSnackbarHostState()
            val saved = async { state.show("Invoice sent", QeetrixSnackbarType.Success) }
            advanceTimeBy(4_999)
            assertTrue(state.isShowing)
            advanceTimeBy(2)
            assertEquals(QeetrixSnackbarResult.Dismissed, saved.await())
            assertFalse(state.isShowing)

            for (request in listOf<suspend () -> QeetrixSnackbarResult>(
                { state.show("Sync is paused", QeetrixSnackbarType.Warning) },
                { state.show("Couldn't send", QeetrixSnackbarType.Error) },
                { state.show("Conversation archived", actionLabel = "Undo") },
            )) {
                val shown = async { request() }
                advanceTimeBy(9_999)
                assertTrue(state.isShowing)
                advanceTimeBy(2)
                assertEquals(QeetrixSnackbarResult.Dismissed, shown.await())
            }
        }

    @Test
    fun aPersistentSnackbarStaysUntilItIsUsedOrClosed() =
        runTest {
            val state = QeetrixSnackbarHostState()
            val shown = async { state.show("Offline", persistent = true) }
            advanceTimeBy(600_000)
            assertTrue(state.isShowing)
            state.dismiss()
            assertEquals(QeetrixSnackbarResult.Dismissed, shown.await())
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
            val second = async { state.show("Couldn't save", QeetrixSnackbarType.Error) }
            runCurrent()
            assertEquals(QeetrixSnackbarResult.Dismissed, first.await())
            assertEquals("Couldn't save", state.current?.message)
            advanceTimeBy(10_001)
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
            val screen = launch { state.show("Draft saved", persistent = true) }
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
        val info = SnackbarRequest("Saved", QeetrixSnackbarType.Info, null, null, persistent = false)
        val action = SnackbarRequest("Archived", QeetrixSnackbarType.Info, null, "Undo", persistent = false)
        assertEquals(5_000L, SnackbarTiming.Unknown.timeoutMillis(info))
        assertEquals(10_000L, SnackbarTiming(doubled, platform).timeoutMillis(info))
        assertEquals(20_000L, SnackbarTiming(doubled, platform).timeoutMillis(action))
        shadowOf(platform).setTouchExplorationEnabled(true)
        assertNull("stays until used", SnackbarTiming(doubled, platform).timeoutMillis(action))
        assertEquals(10_000L, SnackbarTiming(doubled, platform).timeoutMillis(info))
    }
}
