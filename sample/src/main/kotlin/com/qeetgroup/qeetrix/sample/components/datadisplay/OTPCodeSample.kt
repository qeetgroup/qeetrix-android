package com.qeetgroup.qeetrix.sample.components.datadisplay

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCode
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCodeSize
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
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
            QeetrixOTPCode(code, expiresAt, label = "Acme · priya@acme.in", clearAfter = Duration.ofSeconds(30))
            SampleNote(
                "Tap to copy: the code goes on the clipboard marked sensitive, with a light haptic, and leaves it " +
                    "30 seconds later if nothing replaced it. In its last 5 seconds the ring and the seconds turn to " +
                    "the warning tone.",
            )
        }
        SampleSection("A list of accounts") {
            val accounts = listOf("Qeet Pay" to "finance@qeet.in", "Northwind" to "admin@northwind.in", "GitHub" to "priya")
            SampleSurface {
                accounts.forEachIndexed { index, (issuer, account) ->
                    if (index > 0) QeetrixSeparator()
                    val (code, expiresAt) = rememberSampleCode(issuer, 30)
                    QeetrixListItem(
                        issuer,
                        description = account,
                        onClick = {},
                        onLongClick = {},
                        onLongClickLabel = "Account actions",
                        content = {
                            QeetrixOTPCode(
                                code,
                                expiresAt,
                                Modifier.fillMaxWidth(),
                                label = "$issuer · $account",
                                size = QeetrixOTPCodeSize.Md,
                                countdownAtEnd = true,
                            )
                        },
                        leading = { QeetrixListItemIcon(issuer.take(1)) },
                    )
                }
            }
            SampleNote(
                "A list row with the code under the account and its countdown at the end. The issuer's initial stands in " +
                    "for a logo. TalkBack reads the row, with its long press, and the code, which copies, as two elements.",
            )
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
