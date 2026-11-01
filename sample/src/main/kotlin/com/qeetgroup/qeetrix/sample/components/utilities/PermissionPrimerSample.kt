package com.qeetgroup.qeetrix.sample.components.utilities

import android.Manifest
import android.app.NotificationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.qeetgroup.qeetrix.components.alert.QeetrixAlert
import com.qeetgroup.qeetrix.components.alert.QeetrixAlertTone
import com.qeetgroup.qeetrix.components.permission.QeetrixPermission
import com.qeetgroup.qeetrix.components.permission.QeetrixPermissionPrimer
import com.qeetgroup.qeetrix.components.permission.QeetrixPermissionStatus
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The permission primer: notifications asked for real, then the refused state; and the other permissions' icons. */
@Composable
fun PermissionPrimerSample() {
    val context = LocalContext.current

    // On or off in Settings on every version; asked for from Android 13, before which there is no question to ask.
    fun allowed() = context.getSystemService(NotificationManager::class.java).areNotificationsEnabled()
    val canAsk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    var granted by rememberSaveable { mutableStateOf(allowed()) }
    var refused by rememberSaveable { mutableStateOf(false) }
    var later by rememberSaveable { mutableStateOf(false) }
    val ask =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
            if (ok) {
                granted = true
            } else {
                refused =
                    true
            }
        }
    LifecycleResumeEffect(Unit) {
        granted = allowed()
        onPauseOrDispose {}
    }
    SamplePage {
        SampleSection("Sign-in requests") {
            when {
                granted -> {
                    QeetrixAlert(
                        "Notifications are on",
                        tone = QeetrixAlertTone.Success,
                        description = "Sign-in requests will reach this phone.",
                    )
                }

                later -> {
                    QeetrixAlert(
                        "Not now",
                        description = "Sign-in requests won't reach this phone until notifications are on.",
                        onDismiss = { later = false },
                    )
                }

                else -> {
                    QeetrixPermissionPrimer(
                        QeetrixPermission.Notifications,
                        title = "Get sign-in requests",
                        description = "Qeet Auth asks you to approve each sign-in here, so it needs to notify you.",
                        onContinue = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) ask.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        onNotNow = { later = true },
                        status = if (refused || !canAsk) QeetrixPermissionStatus.Denied else QeetrixPermissionStatus.NotAsked,
                        deniedDescription = "Turn on notifications for this app in Settings.",
                    )
                }
            }
            SampleNote("Continue shows the system's question for real. Refuse it and the primer sends you to Settings instead.")
        }
        SampleSection("The other permissions") {
            for (permission in listOf(QeetrixPermission.Location, QeetrixPermission.Photos, QeetrixPermission.Contacts)) {
                QeetrixPermissionPrimer(
                    permission,
                    title = "Allow ${permission.value}",
                    description = "Why the app needs it, in a sentence.",
                    onContinue = {},
                    onNotNow = {},
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PermissionPrimerSamplePreview() {
    SamplePreview { PermissionPrimerSample() }
}
