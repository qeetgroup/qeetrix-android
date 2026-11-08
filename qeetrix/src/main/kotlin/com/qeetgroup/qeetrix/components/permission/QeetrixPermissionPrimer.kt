package com.qeetgroup.qeetrix.components.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.icons.QeetrixIconName

/** What a [QeetrixPermissionPrimer] asks for (`value` is the name shared with qeetrix-ios), and its icon. */
public enum class QeetrixPermission(
    public val value: String,
    internal val icon: QeetrixIconName,
) {
    /** The camera: scanning a QR code, a photo of a document. */
    Camera("camera", QeetrixIconName.Camera),

    /** The device's location. */
    Location("location", QeetrixIconName.MapPin),

    /** Notifications: sign-in approvals, alerts. */
    Notifications("notifications", QeetrixIconName.Bell),

    /** Photos and videos. */
    Photos("photos", QeetrixIconName.Image),

    /** Contacts. */
    Contacts("contacts", QeetrixIconName.ContactRound),
}

/** Where a permission stands before the primer (`value` is the name shared with qeetrix-ios). */
public enum class QeetrixPermissionStatus(
    public val value: String,
) {
    /** Not asked yet: the primer explains, then Continue lets the system ask. The default. */
    NotAsked("not-asked"),

    /** Refused: the system won't ask again, so the primer sends people to Settings. */
    Denied("denied"),
}

/**
 * Says why the app needs a [permission] before the system asks for it — the camera to scan a code, notifications for
 * sign-in approvals — so the system's prompt comes with a reason. An [QeetrixEmptyState] in the section size: the
 * permission's icon, [title], [description], then the actions. Put it on a screen of its own or in a bottom sheet.
 *
 * - [QeetrixPermissionStatus.NotAsked]: **Continue** (solid) runs [onContinue], where the app launches the system's
 *   request; **Not now** (ghost) runs [onNotNow]. Ask only when the feature is used, never at start-up.
 * - [QeetrixPermissionStatus.Denied]: the system will not ask again, so the actions are **Open Settings**, which opens
 *   the app's page in Settings ([onOpenSettings] to do it yourself), and Not now; [deniedDescription] says what to turn on.
 *
 * [footer] is a short reassurance under the actions, as the empty state's footer — "Only sign-in requests. Nothing
 * else, no marketing." — in `color.text.subtle`: draw its text at `QeetrixTextSize.Sm`. It shows in both
 * states.
 *
 * Qeetrix asks for nothing itself: the app keeps the `ActivityResultLauncher` and decides the [status] — checking
 * again when the screen resumes, as the person may have changed it in Settings.
 *
 * ```
 * QeetrixPermissionPrimer(
 *     QeetrixPermission.Camera,
 *     title = "Scan the QR code",
 *     description = "Qeet Auth uses the camera only to read the code on your screen.",
 *     onContinue = { launcher.launch(Manifest.permission.CAMERA) },
 *     onNotNow = { showKeyEntry = true },
 * )
 * ```
 */
@Composable
public fun QeetrixPermissionPrimer(
    permission: QeetrixPermission,
    title: String,
    description: String,
    onContinue: () -> Unit,
    onNotNow: () -> Unit,
    modifier: Modifier = Modifier,
    status: QeetrixPermissionStatus = QeetrixPermissionStatus.NotAsked,
    deniedDescription: String? = null,
    onOpenSettings: (() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
) {
    PermissionPrimer(permission, title, description, onContinue, onNotNow, modifier, status, deniedDescription, onOpenSettings, footer)
}

/** 0.1.0's signature without `footer`, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun QeetrixPermissionPrimer(
    permission: QeetrixPermission,
    title: String,
    description: String,
    onContinue: () -> Unit,
    onNotNow: () -> Unit,
    modifier: Modifier = Modifier,
    status: QeetrixPermissionStatus = QeetrixPermissionStatus.NotAsked,
    deniedDescription: String? = null,
    onOpenSettings: (() -> Unit)? = null,
): Unit = PermissionPrimer(permission, title, description, onContinue, onNotNow, modifier, status, deniedDescription, onOpenSettings)

/**
 * [QeetrixPermissionPrimer], with Not now left out when [onNotNow] is null — for a screen whose way out is its own
 * (the QR scanner's "Type the key instead" and Back).
 */
@Composable
internal fun PermissionPrimer(
    permission: QeetrixPermission,
    title: String,
    description: String,
    onContinue: () -> Unit,
    onNotNow: (() -> Unit)?,
    modifier: Modifier = Modifier,
    status: QeetrixPermissionStatus = QeetrixPermissionStatus.NotAsked,
    deniedDescription: String? = null,
    onOpenSettings: (() -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
) {
    val context = LocalContext.current
    val denied = status == QeetrixPermissionStatus.Denied
    QeetrixEmptyState(
        title = title,
        modifier = modifier,
        icon = permission.icon,
        description = if (denied) deniedDescription ?: description else description,
        size = QeetrixEmptyStateSize.Section,
        footer = footer,
        actions = {
            if (denied) {
                QeetrixButton(
                    stringResource(R.string.qx_permission_open_settings),
                    onClick = { onOpenSettings?.invoke() ?: openAppSettings(context) },
                )
            } else {
                QeetrixButton(stringResource(R.string.qx_permission_continue), onClick = onContinue)
            }
            if (onNotNow != null) {
                QeetrixButton(stringResource(R.string.qx_permission_not_now), onClick = onNotNow, variant = QeetrixButtonVariant.Ghost)
            }
        },
    )
}

/** The app's own page in Settings, where a refused permission is turned on. */
internal fun openAppSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
