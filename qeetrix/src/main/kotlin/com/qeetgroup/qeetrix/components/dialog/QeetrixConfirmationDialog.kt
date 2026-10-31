package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.qeetgroup.qeetrix.R

/**
 * "Are you sure?" — a [QeetrixAlertDialog] with exactly two answers: Cancel ([cancelLabel], `qx_cancel` by
 * default), the tertiary action, and the action that confirms ([confirmLabel]): the destructive (red) button
 * when [intent] is [QeetrixDialogIntent.Danger], the primary otherwise. Use it before an action that cannot be
 * undone or that has consequences for others. `@qeetrix/react` has no separate component for it: on the web it
 * is a Dialog with this footer.
 *
 * - [onConfirm] runs once, after `onOpenChange(false)`, when the person confirms; Cancel and Back only ask to
 *   close. A tap outside does nothing for a `danger` or `warning` dialog, so a stray tap is never an answer.
 * - Write the title as the question and the confirm label as its answer ("Revoke key", not "OK").
 * - A confirmation records what the person chose; it does not authorise the action. Check permission where
 *   the action is performed.
 * - Opened from a keyboard, focus starts on Cancel.
 *
 * ```
 * QeetrixConfirmationDialog(
 *     open = confirmRemoval,
 *     onOpenChange = { confirmRemoval = it },
 *     title = "Remove Ada from the team?",
 *     description = "They lose access to the team's projects straight away.",
 *     confirmLabel = "Remove",
 *     onConfirm = { removeMember(ada) },
 *     intent = QeetrixDialogIntent.Danger,
 * )
 * ```
 */
@Composable
public fun QeetrixConfirmationDialog(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    title: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    intent: QeetrixDialogIntent = QeetrixDialogIntent.Standard,
    dismissible: Boolean = intent != QeetrixDialogIntent.Danger,
    cancelLabel: String = stringResource(R.string.qx_cancel),
) {
    QeetrixAlertDialog(
        open = open,
        onOpenChange = onOpenChange,
        title = title,
        actions = confirmationActions(confirmLabel, onConfirm, intent, cancelLabel),
        modifier = modifier,
        description = description,
        intent = intent,
        dismissible = dismissible,
    )
}

/** Cancel, which only closes the dialog, and the confirming action: destructive for a `danger` dialog. */
internal fun confirmationActions(
    confirmLabel: String,
    onConfirm: () -> Unit,
    intent: QeetrixDialogIntent,
    cancelLabel: String,
): List<QeetrixDialogAction> =
    listOf(
        QeetrixDialogAction(cancelLabel, QeetrixDialogActionRole.Cancel) {},
        QeetrixDialogAction(
            confirmLabel,
            if (intent == QeetrixDialogIntent.Danger) QeetrixDialogActionRole.Destructive else QeetrixDialogActionRole.Primary,
            onClick = onConfirm,
        ),
    )
