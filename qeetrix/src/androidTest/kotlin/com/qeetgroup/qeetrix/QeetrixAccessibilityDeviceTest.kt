package com.qeetgroup.qeetrix

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.qeetgroup.qeetrix.quality.LocalizedCatalog
import com.qeetgroup.qeetrix.quality.OverlayCatalog
import com.qeetgroup.qeetrix.quality.QualityCatalog
import com.qeetgroup.qeetrix.quality.QualitySpecimen
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * The Accessibility Test Framework's checks — touch target size, text and image contrast measured from a
 * screenshot, labels, duplicate descriptions, clickable spans, traversal order — over every quality-catalog
 * specimen, and the localized ones, in light and dark, on a device. The checks do not run under Robolectric nor,
 * as Phase 6 found, on API 29, where a deliberately bad button passes. So the tests run only from the API level the
 * checks were verified on, and each first proves on that known problem that they run. Below it the runner leaves
 * them out: an assumption failure would count as a failure in Android's device reports, and a pass would mean
 * nothing. Every result is reported before the test fails. Run with `./gradlew :qeetrix:connectedDebugAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = VERIFIED_API)
class QeetrixAccessibilityDeviceTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    /** A known problem — an unlabelled 24 dp button — that the checks must catch for their results to count. */
    private val control = QualitySpecimen("control") { Box(Modifier.size(24.dp).clickable {}) }

    /** Fails unless the checks catch the control here: a silent control is a broken setup, and no result would count. */
    private fun assertChecksAreLive(show: (QualitySpecimen) -> Unit) {
        compose.runOnIdle { show(control) }
        val live = runCatching { compose.onRoot().tryPerformAccessibilityChecks() }.isFailure
        assertTrue("ATF found nothing wrong with an unlabelled 24 dp button on API ${Build.VERSION.SDK_INT}", live)
    }

    @Test
    fun theChecksCatchAKnownProblem() {
        compose.enableAccessibilityChecks()
        var specimen by mutableStateOf(control)
        compose.setContent { specimen.content() }
        assertChecksAreLive { specimen = it }
    }

    @Test
    fun everySpecimenPassesTheAccessibilityTestFramework() {
        compose.enableAccessibilityChecks()
        var specimen by mutableStateOf(QualityCatalog.first())
        var mode by mutableStateOf(QeetrixThemeMode.Light)
        compose.setContent {
            QeetrixTheme(mode) {
                // Bounded as the JVM scans' window is (SpecimenMaxHeight), inside the scroll.
                Box(Modifier.verticalScroll(rememberScrollState()).heightIn(max = SpecimenMaxHeight).padding(16.dp)) { specimen.content() }
            }
        }
        assertChecksAreLive { specimen = it }
        val failures = mutableListOf<String>()
        for (each in QualityCatalog + LocalizedCatalog) {
            for (theme in listOf(QeetrixThemeMode.Light, QeetrixThemeMode.Dark)) {
                compose.runOnIdle {
                    specimen = each
                    mode = theme
                }
                compose.waitForIdle()
                try {
                    compose.onRoot().tryPerformAccessibilityChecks()
                } catch (failure: Throwable) {
                    failures += "${each.name} (${theme.value}): ${failure.message?.lines()?.joinToString(" | ")}"
                }
            }
        }
        assertTrue(failures.joinToString("\n", prefix = "${failures.size} specimens with findings:\n"), failures.isEmpty())
    }

    /**
     * The overlays as apps open them, each in its own window ([OverlayCatalog]): the checks run on every window's
     * root, in light and dark. The recorded finding Q-6 (the sheet's handle row, [isHandleRowFinding]) is excluded by
     * what it is, not by window, and must keep happening, as in the JVM scan (`LayoutQualityTest`'s `SmallFindings`).
     */
    @Test
    fun everyOverlayWindowPassesTheAccessibilityTestFramework() {
        compose.enableAccessibilityChecks()
        var specimen by mutableStateOf(control)
        var mode by mutableStateOf(QeetrixThemeMode.Light)
        compose.setContent { QeetrixTheme(mode) { specimen.content() } }
        assertChecksAreLive { specimen = it }
        val handleName = InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.qx_drag_handle)
        val failures = mutableListOf<String>()
        val handleRowSeen = mutableSetOf<String>()
        for (each in OverlayCatalog) {
            for (theme in listOf(QeetrixThemeMode.Light, QeetrixThemeMode.Dark)) {
                compose.runOnIdle {
                    specimen = each
                    mode = theme
                }
                compose.waitForIdle()
                // The bottom of the sheet's handle row on screen: the handle mark sits at the row's bottom edge.
                val handleRowBottom =
                    compose
                        .onAllNodes(hasContentDescription(handleName), useUnmergedTree = true)
                        .fetchSemanticsNodes()
                        .firstOrNull()
                        ?.let { (it.positionOnScreen.y + it.size.height).roundToInt() }
                val roots = compose.onAllNodes(isRoot())
                for (index in roots.fetchSemanticsNodes().indices) {
                    try {
                        roots[index].tryPerformAccessibilityChecks()
                    } catch (failure: Throwable) {
                        val results =
                            failure.message
                                .orEmpty()
                                .lines()
                                .filter { it.startsWith("View with bounds") }
                        val (recorded, other) = results.partition { isHandleRowFinding(it, handleRowBottom) }
                        if (recorded.isNotEmpty()) handleRowSeen += "${each.name} (${theme.value})"
                        if (other.isNotEmpty() || results.isEmpty()) {
                            failures += "${each.name} (${theme.value}, window $index): ${failure.message?.lines()?.joinToString(" | ")}"
                        }
                    }
                }
            }
        }
        assertTrue(failures.joinToString("\n", prefix = "${failures.size} windows with findings:\n"), failures.isEmpty())
        // Q-6 still happens; once it does not, the component changed and the exclusion goes.
        assertEquals(setOf("bottom-sheet window (light)", "bottom-sheet window (dark)"), handleRowSeen)
    }

    /**
     * Recorded finding Q-6 as the checks see it on a device. Material's sheet makes QeetrixBottomSheet's 12 dp handle row
     * interactive twice over: the row itself (long-clickable, for the handle's tooltip) and Material's clickable handle,
     * 48 dp tall but clipped by the row to 30 dp. ATF reports both under 48 dp; both end at the row's bottom edge, where
     * the handle mark sits, and span the sheet's width. (Found by the first device run of this test, 2026-10-30.)
     */
    private fun isHandleRowFinding(
        result: String,
        handleRowBottom: Int?,
    ): Boolean {
        if (handleRowBottom == null || "TouchTargetSizeCheck" !in result) return false
        val bounds =
            Regex("""\[(-?\d+),(-?\d+)]\[(-?\d+),(-?\d+)]""")
                .find(result)
                ?.groupValues
                ?.drop(1)
                ?.map(String::toInt) ?: return false
        val (_, top, _, bottom) = bounds
        return abs(bottom - handleRowBottom) <= 1 && bottom - top < with(compose.density) { 48.dp.roundToPx() }
    }
}

/** The API level the checks were verified to run on (Phase 6, an API 36 emulator; on API 29 they do not run). */
private const val VERIFIED_API = 36
