package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCode
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCodeSize
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.QeetrixTextWeight
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant

/** Authenticator codes: one on its own, a list of accounts, a hidden code and an eight-digit one with a 60 s period. */
@Composable
fun OTPCodeSample() {
    SamplePage {
        SampleSection("One code") {
            val (code, expiresAt) = rememberSampleCode("acme", 30)
            QeetrixOTPCode(code, expiresAt, label = "Acme · priya@acme.in")
            SampleNote(
                "Tap to copy: the code goes on the clipboard marked sensitive, with a light haptic. " +
                    "In its last 5 seconds the ring and the seconds turn to the warning tone.",
            )
        }
        SampleSection("A list of accounts") {
            val accounts = listOf("Qeet Pay" to "finance@qeet.in", "Northwind" to "admin@northwind.in", "GitHub" to "priya")
            SampleSurface {
                accounts.forEachIndexed { index, (issuer, account) ->
                    if (index > 0) QeetrixSeparator()
                    // The row is the app's: the code's label is what TalkBack reads, not drawn.
                    Column(Modifier.padding(QeetrixSpace.InsetMd)) {
                        QeetrixText(issuer, weight = QeetrixTextWeight.Medium)
                        QeetrixText(account, size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
                        val (code, expiresAt) = rememberSampleCode(issuer, 30)
                        QeetrixOTPCode(code, expiresAt, label = "$issuer · $account", size = QeetrixOTPCodeSize.Md)
                    }
                }
            }
            SampleNote("Size Md, for a list. The account's name is the row's; the code's label is what TalkBack reads.")
        }
        SampleSection("Hidden until tapped") {
            val (code, expiresAt) = rememberSampleCode("bank", 30)
            QeetrixOTPCode(code, expiresAt, label = "Bank · savings", hidden = true)
            SampleNote("The first tap shows the code; the next copies it. TalkBack's action reads \"Show code\" first.")
        }
        SampleSection("Eight digits, 60 seconds") {
            val (code, expiresAt) = rememberSampleCode("vpn", 60, digits = 8)
            QeetrixOTPCode(code, expiresAt, label = "Corporate VPN", period = Duration.ofSeconds(60))
        }
    }
}

/**
 * A made-up code for [seed] that changes every [period] seconds, as a TOTP does, and the moment it expires. Not a real
 * TOTP: the app owns its codes; this only keeps the sample moving.
 */
@Composable
private fun rememberSampleCode(
    seed: String,
    period: Long,
    digits: Int = 6,
): Pair<String, Instant> {
    var window by remember { mutableLongStateOf(System.currentTimeMillis() / 1_000 / period) }
    LaunchedEffect(window) {
        delay((window + 1) * period * 1_000 - System.currentTimeMillis())
        window = System.currentTimeMillis() / 1_000 / period
    }
    val modulus = (1..digits).fold(1L) { acc, _ -> acc * 10 }
    val value = Math.floorMod((seed.hashCode().toLong() * 1_000_003L) xor (window * 2_654_435_761L), modulus)
    return value.toString().padStart(digits, '0') to Instant.ofEpochSecond((window + 1) * period)
}

@PreviewLightDark
@Composable
private fun OTPCodeSamplePreview() {
    SamplePreview { OTPCodeSample() }
}
