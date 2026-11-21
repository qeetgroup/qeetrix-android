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
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.tryPerformAccessibilityChecks
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityViewCheckResult
import com.google.android.apps.common.testing.accessibility.framework.checks.TouchTargetSizeCheck
import com.google.android.apps.common.testing.accessibility.framework.integrations.espresso.AccessibilityValidator
import com.qeetgroup.qeetrix.quality.LocalizedCatalog
import com.qeetgroup.qeetrix.quality.OverlayCatalog
import com.qeetgroup.qeetrix.quality.QualityCatalog
import com.qeetgroup.qeetrix.quality.QualitySpecimen
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The Accessibility Test Framework's checks — touch target size, text and image contrast measured from a
 * screenshot, labels, duplicate descriptions, clickable spans, traversal order — over every quality-catalog
 * specimen, and the localized ones, in light and dark, on a device. The checks do not run under Robolectric nor,
 * as Phase 6 found, on API 29, where a deliberately bad button passes. So the tests run only from the API level the
 * checks were verified on, and each first proves on that known problem that they run. Below it the runner leaves
 * them out: an assumption failure would count as a failure in Android's device reports, and a pass would mean
 * nothing. Every result is reported before the test fails. The touch-target size check is set aside only on the
 * specimens in [SharedTouchAreas]. Run with `./gradlew :qeetrix:connectedDebugAndroidTest`.
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

    /** The specimen the checks are on, for [SharedTouchAreas]. */
    private var checking = ""

    /**
     * Every check, but for the touch-target size reports on the specimens in [SharedTouchAreas]: there Compose trims the
     * bounds it reports to accessibility services where a neighbour's touch area or text overlaps, at the web's
     * spacing, while each control still takes taps over a whole 48 dp area (`LayoutQualityTest` checks that area).
     */
    private val validator =
        AccessibilityValidator().setRunChecksFromRootView(true).setSuppressingResultMatcher(
            object : TypeSafeMatcher<AccessibilityViewCheckResult>() {
                override fun describeTo(description: Description) {
                    description.appendText("a touch area that overlaps a neighbour's at the web's spacing")
                }

                override fun matchesSafely(result: AccessibilityViewCheckResult): Boolean =
                    checking in SharedTouchAreas && result.sourceCheckClass == TouchTargetSizeCheck::class.java
            },
        )

    @Test
    fun everySpecimenPassesTheAccessibilityTestFramework() {
        compose.enableAccessibilityChecks(validator)
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
                checking = each.name
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
     * root, in light and dark. Nothing is excluded: the recorded finding Q-6, Material's 12 dp clickable handle row on
     * the bottom sheet, went with Material's sheet.
     */
    @Test
    fun everyOverlayWindowPassesTheAccessibilityTestFramework() {
        compose.enableAccessibilityChecks()
        var specimen by mutableStateOf(control)
        var mode by mutableStateOf(QeetrixThemeMode.Light)
        compose.setContent { QeetrixTheme(mode) { specimen.content() } }
        assertChecksAreLive { specimen = it }
        val failures = mutableListOf<String>()
        for (each in OverlayCatalog) {
            for (theme in listOf(QeetrixThemeMode.Light, QeetrixThemeMode.Dark)) {
                compose.runOnIdle {
                    specimen = each
                    mode = theme
                }
                compose.waitForIdle()
                val roots = compose.onAllNodes(isRoot())
                for (index in roots.fetchSemanticsNodes().indices) {
                    try {
                        roots[index].tryPerformAccessibilityChecks()
                    } catch (failure: Throwable) {
                        failures += "${each.name} (${theme.value}, window $index): ${failure.message?.lines()?.joinToString(" | ")}"
                    }
                }
            }
        }
        assertTrue(failures.joinToString("\n", prefix = "${failures.size} windows with findings:\n"), failures.isEmpty())
    }
}

/**
 * Specimens whose controls sit at the web's spacing, closer than 48 dp centre to centre, so neighbouring touch areas
 * overlap (the owner's decision, 21 November 2026) and Compose reports trimmed bounds that the touch-target size check
 * flags: buttons stacked 8 dp apart, icon buttons stacked flush, a collapsible trigger with its panel right under it, the
 * QR scanner's torch 12 dp above its fallback, and the attachment picker's hint 4 dp under its buttons. Only that check
 * is set aside for them; every other check runs.
 */
private val SharedTouchAreas = setOf("button", "icon-button", "accordion", "qr-scanner", "file-upload")

/** The API level the checks were verified to run on (Phase 6, an API 36 emulator; on API 29 they do not run). */
private const val VERIFIED_API = 36
