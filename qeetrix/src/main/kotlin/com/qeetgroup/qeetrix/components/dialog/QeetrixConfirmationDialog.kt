package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.icons.QeetrixIconName

/**
 * "Are you sure?" — a [QeetrixAlertDialog] with exactly two answers: Cancel ([cancelLabel], `qx_cancel` by
 * default) and the action that confirms ([confirmLabel], drawn as the destructive button when
 * [destructive]). Use it before an action that cannot be undone or that has consequences for others.
 *
 * - [onConfirm] runs once, after [onDismissRequest], when the person confirms; Cancel, Back and Escape only
 *   call [onDismissRequest]. A tap outside does nothing, so a stray tap is never an answer.
 * - Write the title as the question and the confirm label as its answer ("Delete workspace", not "OK").
 * - A confirmation records what the person chose; it does not authorise the action. Check permission
 *   where the action is performed.
 * - Opened from a keyboard, focus starts on Cancel.
 *
 * It is the Android counterpart of the web's confirm-style `AlertDialog`. iOS shows the same choice as the
 * system's confirmation dialog (an action sheet); Android has no separate primitive for it, so this is the
 * alert dialog with its two roles fixed.
 *
 * ```
 * if (confirmRemoval) {
 *     QeetrixConfirmationDialog(
 *         title = "Remove Ada from the team?",
 *         message = "They lose access to the team's projects straight away.",
 *         confirmLabel = "Remove",
 *         destructive = true,
 *         onConfirm = { removeMember(ada) },
 *         onDismissRequest = { confirmRemoval = false },
 *     )
 * }
 * ```
 */
@Composable
public fun QeetrixConfirmationDialog(
    title: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    destructive: Boolean = false,
    cancelLabel: String = stringResource(R.string.qx_cancel),
    icon: QeetrixIconName? = null,
) {
    QeetrixAlertDialog(
        title = title,
        actions = confirmationActions(confirmLabel, onConfirm, destructive, cancelLabel),
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        message = message,
        icon = icon,
    )
}

/** Cancel, which only closes the dialog, and the confirming action in its role. */
internal fun confirmationActions(
    confirmLabel: String,
    onConfirm: () -> Unit,
    destructive: Boolean,
    cancelLabel: String,
): List<QeetrixDialogAction> =
    listOf(
        QeetrixDialogAction(cancelLabel, QeetrixDialogActionRole.Cancel) {},
        QeetrixDialogAction(
            confirmLabel,
            if (destructive) QeetrixDialogActionRole.Destructive else QeetrixDialogActionRole.Default,
            onClick = onConfirm,
        ),
    )
