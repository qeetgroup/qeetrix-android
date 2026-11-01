package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.qrcode.QeetrixQRCode
import com.qeetgroup.qeetrix.components.qrcode.QeetrixQRCodeLevel
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** QR codes: an authenticator's setup code, the error-correction levels, and sizes. */
@Composable
fun QRCodeSample() {
    SamplePage {
        SampleSection("Add Acme to an authenticator") {
            QeetrixQRCode(
                "otpauth://totp/Acme:priya@acme.in?secret=JBSWY3DPEHPK3PXP&issuer=Acme",
                label = "QR code to add Acme to an authenticator",
                size = 200.dp,
            )
            SampleNote("Dark modules on white with a quiet zone, in dark mode too, so any camera reads it. Scan it with a phone to try.")
        }
        SampleSection("Error correction") {
            SampleFlow {
                for (level in QeetrixQRCodeLevel.entries) {
                    QeetrixQRCode("https://qeet.in/pair/7F3K", label = "Level ${level.value}", level = level, size = 120.dp)
                }
            }
            SampleNote("L, M, Q, H: more modules recover more damage.")
        }
    }
}

@PreviewLightDark
@Composable
private fun QRCodeSamplePreview() {
    SamplePreview { QRCodeSample() }
}
