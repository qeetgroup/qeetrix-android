package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.qeetgroup.qeetrix.R

/**
 * A decision the person makes with buttons before going on — `@qeetrix/react`'s Dialog with its footer of
 * actions, and with [intent] `danger` or `warning` its alert dialog (`role="alertdialog"` on the web): a
 * [title] written as the question, an optional [description], and the [actions] that answer it. For a plain
 * "are you sure?" use [QeetrixConfirmationDialog]; for a form or other content, [QeetrixDialog].
 *
 * - **The app owns [open]**, as for [QeetrixDialog]; `onOpenChange(false)` asks to close.
 * - **Actions** are [QeetrixDialogAction]s with roles, laid out as the design rules say: the cancelling
 *   (tertiary) actions first, then the secondary, then the primary or destructive action at the end. A
 *   destructive action is always offered with Cancel (`qx_cancel`), which is added when missing; a dialog has
 *   at most one primary or destructive action. Choosing an action asks to close the dialog and then runs it,
 *   once per opening.
 * - **[intent]** defaults to [QeetrixDialogIntent.Danger] (the red line) when an action is destructive, and
 *   [QeetrixDialogIntent.Standard] otherwise; pass [QeetrixDialogIntent.Warning] for the amber line.
 * - **Dismissal.** A `danger` dialog is not [dismissible] by default: no close button, and only its actions
 *   answer it. An alert dialog never closes on a tap outside, so a stray tap is never an answer. Back always
 *   asks to close without running an action.
 * - **Accessibility.** The title is announced as the dialog opens and is a heading; opened from a keyboard,
 *   focus starts on the first action — Cancel, the least destructive choice.
 *
 * ```
 * QeetrixAlertDialog(
 *     open = confirmDelete,
 *     onOpenChange = { confirmDelete = it },
 *     title = "Delete this workspace?",
 *     description = "Its projects and files are removed for everyone.",
 *     actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { deleteWorkspace() }),
 * )
 * ```
 *
 * @param size The width; `Sm` (400 dp) by default, as the web's examples size a decision.
 */
@Composable
public fun QeetrixAlertDialog(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    title: String,
    actions: List<QeetrixDialogAction>,
    modifier: Modifier = Modifier,
    description: String? = null,
    intent: QeetrixDialogIntent = alertIntentFor(actions),
    size: QeetrixDialogSize = QeetrixDialogSize.Sm,
    dismissible: Boolean = intent != QeetrixDialogIntent.Danger,
) {
    val arranged = arrangeActions(actions, stringResource(R.string.qx_cancel))
    val change by rememberUpdatedState(onOpenChange)
    val isOpen by rememberUpdatedState(open)
    // One answer per opening, however fast the buttons are tapped, and none while the dialog leaves.
    val answered = remember { BooleanArray(1) }
    LaunchedEffect(open) { if (open) answered[0] = false }
    val close = { if (isOpen) change(false) }
    DialogWindow(
        open = open,
        title = title,
        onDismissRequest = close,
        dismissOnScrimTap = dismissible && !intent.isAlert,
        fullscreen = size == QeetrixDialogSize.Fullscreen,
    ) {
        DialogSurface(
            title = title,
            description = description,
            intent = intent,
            size = size,
            onClose = if (dismissible) close else null,
            modifier = modifier,
            focusFirst = keyboardInput(),
            footer = {
                DialogActions(arranged) { action ->
                    if (!answered[0] && isOpen) {
                        answered[0] = true
                        close()
                        action.onClick()
                    }
                }
            },
        )
    }
}
