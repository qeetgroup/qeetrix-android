package com.qeetgroup.qeetrix.components.scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.permission.QeetrixPermission
import com.qeetgroup.qeetrix.components.permission.QeetrixPermissionPrimer
import com.qeetgroup.qeetrix.components.permission.QeetrixPermissionStatus
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** The scanner's states over the app's preview, the torch and the fallback; the permission primer's actions. */
@RunWith(RobolectricTestRunner::class)
class QeetrixQRScannerTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun scanningShowsThePreviewTheGuidanceTheTorchAndTheFallback() {
        var torch = false
        var typed = false
        compose.setQeetrix {
            Box(Modifier.size(360.dp, 640.dp)) {
                QeetrixQRScanner(
                    QeetrixScannerState.Scanning,
                    torch = torch,
                    onTorchChange = { torch = it },
                    onEnterKey = { typed = true },
                ) { Box(Modifier.fillMaxSize().testTag("preview")) }
            }
        }
        compose.onNodeWithTag("preview", useUnmergedTree = true).assertExists()
        compose.onNodeWithText("Point the camera at the QR code").assertExists()
        compose.onNodeWithContentDescription("Turn on the torch").performClick()
        compose.onNodeWithText("Type the key instead").performClick()
        assertEquals(true, torch)
        assertEquals(true, typed)
    }

    @Test
    fun withoutTheCameraItAsksOrSendsToSettings() {
        var asked = false
        compose.setQeetrix { QeetrixQRScanner(QeetrixScannerState.PermissionNeeded, onAllowCamera = { asked = true }) {} }
        compose.onNodeWithText("Allow the camera").assertExists()
        compose.onNodeWithText("Continue").performClick()
        assertEquals(true, asked)
    }

    /**
     * Found on a device: in a 3:4 box the guidance sat on the target's lower corners. The target is centred in the
     * space above the controls, so the controls start below it — here in a short box at 200 % text.
     */
    @Test
    fun theControlsNeverOverlapTheTarget() {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 2f)) {
                QeetrixTheme(QeetrixThemeMode.Light) {
                    Box(Modifier.size(360.dp, 420.dp)) {
                        QeetrixQRScanner(QeetrixScannerState.Scanning, onTorchChange = {}, onEnterKey = {}) {
                            Box(Modifier.fillMaxSize().testTag("preview"))
                        }
                    }
                }
            }
        }
        val guidance = compose.onNodeWithText("Point the camera at the QR code").getBoundsInRoot()
        val fallback = compose.onNodeWithText("Type the key instead").getBoundsInRoot()
        // The target is 70 % of the shorter side of the space above the guidance, centred in it.
        val above = guidance.top - QeetrixSpace.InsetLg
        val side = minOf(360.dp, above) * 0.7f
        val targetBottom = (above + side) / 2
        assertTrue("target ends at $targetBottom, guidance starts at ${guidance.top}", targetBottom <= guidance.top)
        assertTrue(fallback.bottom <= 420.dp)
    }

    /** Found on a device: the permission states hid the fallback and wired the primer's Not now to it instead. */
    @Test
    fun theFallbackIsThereInEveryStateAndNothingIsADeadEnd() {
        var state by mutableStateOf(QeetrixScannerState.Scanning)
        var typed = 0
        compose.setQeetrix {
            Box(Modifier.size(360.dp, 640.dp)) { QeetrixQRScanner(state, onEnterKey = { typed++ }) { Box(Modifier.fillMaxSize()) } }
        }
        for (shown in QeetrixScannerState.entries) {
            state = shown
            compose.waitForIdle()
            compose.onNodeWithText("Type the key instead").performClick()
            compose.onAllNodesWithText("Not now").assertCountEquals(0)
        }
        assertEquals(QeetrixScannerState.entries.size, typed)
    }

    /** One app scans a pairing code and a setup key, so the fallback's words and the camera message are per call. */
    @Test
    fun theFallbackAndTheRefusalSayWhatTheAppAsks() {
        var state by mutableStateOf(QeetrixScannerState.Scanning)
        compose.setQeetrix {
            Box(Modifier.size(360.dp, 640.dp)) {
                QeetrixQRScanner(
                    state,
                    onEnterKey = {},
                    enterKeyLabel = "Type the code instead",
                    deniedDescription = "Turn on the camera for Qeet ID in Settings to scan the code.",
                ) { Box(Modifier.fillMaxSize()) }
            }
        }
        for (shown in QeetrixScannerState.entries) {
            state = shown
            compose.waitForIdle()
            compose.onNodeWithText("Type the code instead").assertExists()
            compose.onAllNodesWithText("Type the key instead").assertCountEquals(0)
        }
        state = QeetrixScannerState.PermissionDenied
        compose.onNodeWithText("Turn on the camera for Qeet ID in Settings to scan the code.").assertExists()
    }

    /** In a row the fallback fills it from the start and the torch keeps its 48 dp touch target at the end. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text widths: the words fit beside the torch
    fun inARowTheFallbackFillsItBesideTheTorch() {
        compose.setQeetrix {
            Box(Modifier.size(320.dp, 640.dp)) {
                QeetrixQRScanner(
                    QeetrixScannerState.Scanning,
                    onTorchChange = {},
                    onEnterKey = {},
                    controlsLayout = QeetrixScannerControlsLayout.Row,
                ) { Box(Modifier.fillMaxSize()) }
            }
        }
        val fallback = compose.onNodeWithText("Type the key instead").getBoundsInRoot()
        val torch = compose.onNodeWithContentDescription("Turn on the torch").getBoundsInRoot()
        // One row, inside the controls' inset.lg (16 dp) padding, gap.sm (8 dp) apart.
        assertEquals(fallback.top, torch.top)
        assertEquals(16.dp, fallback.left)
        assertEquals(320.dp - 16.dp, torch.right)
        assertEquals(48.dp, torch.right - torch.left)
        assertEquals(torch.left - 8.dp, fallback.right)
    }

    /** Where the words don't fit beside the torch, the torch goes above a full-width fallback, never cut off. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE) // real text widths
    fun aRowTooNarrowForTheWordsStacksThem() {
        compose.setQeetrix {
            Box(Modifier.size(200.dp, 640.dp)) {
                QeetrixQRScanner(
                    QeetrixScannerState.Scanning,
                    onTorchChange = {},
                    onEnterKey = {},
                    enterKeyLabel = "Type the code instead",
                    controlsLayout = QeetrixScannerControlsLayout.Row,
                ) { Box(Modifier.fillMaxSize()) }
            }
        }
        val fallback = compose.onNodeWithText("Type the code instead").getBoundsInRoot()
        val torch = compose.onNodeWithContentDescription("Turn on the torch").getBoundsInRoot()
        assertEquals(torch.bottom + 12.dp, fallback.top) // gap.md
        assertEquals(200.dp - 16.dp * 2, fallback.right - fallback.left)
        assertEquals((torch.left + torch.right) / 2, 100.dp)
    }

    @Test
    fun aRefusedPermissionOffersSettings() {
        var opened = false
        compose.setQeetrix {
            QeetrixPermissionPrimer(
                QeetrixPermission.Notifications,
                title = "Get sign-in requests",
                description = "Approve sign-ins from this phone.",
                onContinue = {},
                onNotNow = {},
                status = QeetrixPermissionStatus.Denied,
                deniedDescription = "Turn on notifications for Qeet Auth in Settings.",
                onOpenSettings = { opened = true },
            )
        }
        compose.onNodeWithText("Turn on notifications for Qeet Auth in Settings.").assertExists()
        compose.onNodeWithText("Open Settings").performClick()
        assertEquals(true, opened)
        assertEquals(listOf("camera", "location", "notifications", "photos", "contacts"), QeetrixPermission.entries.map { it.value })
        assertEquals(
            listOf("scanning", "permission-needed", "permission-denied", "no-camera"),
            QeetrixScannerState.entries.map { it.value },
        )
        assertEquals(listOf("stacked", "row"), QeetrixScannerControlsLayout.entries.map { it.value })
    }
}
