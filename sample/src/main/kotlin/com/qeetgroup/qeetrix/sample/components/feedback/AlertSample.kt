package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.alert.QeetrixAlert
import com.qeetgroup.qeetrix.components.alert.QeetrixAlertAnnounce
import com.qeetgroup.qeetrix.components.alert.QeetrixAlertTone
import com.qeetgroup.qeetrix.components.alert.QeetrixAlertVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Alerts: the four tones, one with actions and Dismiss, the banner, and one that appears and is announced. */
@Composable
fun AlertSample() {
    SamplePage {
        SampleSection("Tones") {
            QeetrixAlert("New sign-in method", description = "You can now approve sign-ins with a passkey.")
            QeetrixAlert("Phone linked", tone = QeetrixAlertTone.Success, description = "Sign-in requests will reach Priya's Pixel.")
            QeetrixAlert(
                "Push isn't set up",
                tone = QeetrixAlertTone.Warning,
                description = "Sign-in requests won't reach this phone until you allow notifications.",
            )
            QeetrixAlert("This phone isn't linked", tone = QeetrixAlertTone.Danger, description = "Link it again to approve sign-ins.")
            SampleNote("TalkBack reads the tone's word first (\"Warning: Push isn't set up\"), so colour is never the only sign.")
        }
        SampleSection("Actions and Dismiss") {
            var shown by rememberSaveable { mutableStateOf(true) }
            if (shown) {
                QeetrixAlert(
                    "Push isn't set up",
                    tone = QeetrixAlertTone.Warning,
                    description = "Sign-in requests won't reach this phone until you allow notifications.",
                    onDismiss = { shown = false },
                    actions = {
                        QeetrixButton("Allow notifications", onClick = {}, size = QeetrixButtonSize.Sm)
                        QeetrixButton(
                            "Not now",
                            onClick = { shown = false },
                            variant = QeetrixButtonVariant.Ghost,
                            size = QeetrixButtonSize.Sm,
                        )
                    },
                )
            } else {
                QeetrixButton(
                    "Show the alert again",
                    onClick = { shown = true },
                    variant = QeetrixButtonVariant.Ghost,
                    size = QeetrixButtonSize.Sm,
                )
            }
        }
        SampleSection("Banner") {
            QeetrixAlert(
                "You're offline",
                tone = QeetrixAlertTone.Warning,
                variant = QeetrixAlertVariant.Banner,
                description = "Codes still work; new accounts need a connection.",
            )
        }
        SampleSection("After something happens") {
            var offline by rememberSaveable { mutableStateOf(false) }
            SampleFlow {
                QeetrixButton(if (offline) "Go online" else "Go offline", onClick = { offline = !offline }, size = QeetrixButtonSize.Sm)
            }
            if (offline) {
                QeetrixAlert(
                    "You're offline",
                    tone = QeetrixAlertTone.Warning,
                    description = "Codes still work; new accounts need a connection.",
                    announce = QeetrixAlertAnnounce.Polite,
                )
            }
            SampleNote("An alert that appears after something happens is announced; one that is there from the start is not.")
        }
    }
}

@PreviewLightDark
@Composable
private fun AlertSamplePreview() {
    SamplePreview { AlertSample() }
}
