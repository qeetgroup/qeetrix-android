package com.qeetgroup.qeetrix.sample.components.media

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.qrcode.QeetrixQRCode
import com.qeetgroup.qeetrix.components.scanner.QeetrixQRScanner
import com.qeetgroup.qeetrix.components.scanner.QeetrixScannerState
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The QR scanner: the camera permission asked for real, then every state on its own. */
@Composable
fun QRScannerSample() {
    val context = LocalContext.current
    val hasCamera = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    fun allowed() = context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    var refused by rememberSaveable { mutableStateOf(false) }
    var granted by rememberSaveable { mutableStateOf(allowed()) }
    val ask =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
            if (ok) granted = true else refused = true
        }
    // Coming back from Settings, the camera may have been turned on (or off): check again.
    LifecycleResumeEffect(Unit) {
        granted = allowed()
        onPauseOrDispose {}
    }
    val openSettings = {
        context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null)))
    }
    var typed by rememberSaveable { mutableStateOf(false) }
    SamplePage {
        SampleSection("Add an account") {
            val state =
                when {
                    !hasCamera -> QeetrixScannerState.NoCamera
                    granted -> QeetrixScannerState.Scanning
                    refused -> QeetrixScannerState.PermissionDenied
                    else -> QeetrixScannerState.PermissionNeeded
                }
            var torch by rememberSaveable { mutableStateOf(false) }
            ScannerBox {
                QeetrixQRScanner(
                    state,
                    torch = torch,
                    onTorchChange = { torch = it },
                    onEnterKey = { typed = true },
                    onAllowCamera = { ask.launch(Manifest.permission.CAMERA) },
                    onOpenSettings = openSettings,
                ) { StandInPreview() }
            }
            SampleNote(
                if (typed) {
                    "\"Type the key instead\" was tapped: the app would open its key entry here."
                } else {
                    "The camera permission is asked for real: Continue shows the system's dialog; refuse it and the scanner offers " +
                        "Open Settings. The sample has no camera library, so a drawing stands in for the app's CameraX preview."
                },
            )
        }
        SampleSection("Every state") {
            var shown by rememberSaveable { mutableStateOf(QeetrixScannerState.PermissionNeeded) }
            SampleFlow {
                for (option in QeetrixScannerState.entries) {
                    QeetrixButton(
                        option.value,
                        onClick = { shown = option },
                        variant = if (option == shown) QeetrixButtonVariant.Soft else QeetrixButtonVariant.Ghost,
                        size = QeetrixButtonSize.Sm,
                    )
                }
            }
            ScannerBox {
                QeetrixQRScanner(shown, onTorchChange = {}, onEnterKey = {}, onOpenSettings = {}) { StandInPreview() }
            }
        }
    }
}

/** The scanner's box on the page: 3:4, with the surface corners. A real screen gives it the whole window. */
@Composable
private fun ScannerBox(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(QeetrixShapes.Surface)) { content() }
}

/** What the camera sees, drawn: a desk and a QR code on it. */
@Composable
private fun StandInPreview() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF5B5148), Color(0xFF2E2A26), Color(0xFF8A7C6E)))),
        contentAlignment = Alignment.Center,
    ) {
        QeetrixQRCode("otpauth://totp/Acme:priya@acme.in?secret=JBSWY3DPEHPK3PXP&issuer=Acme", "A QR code", size = 180.dp)
    }
}

@PreviewLightDark
@Composable
private fun QRScannerSamplePreview() {
    SamplePreview { QRScannerSample() }
}
