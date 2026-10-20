package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconSize
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A decision the person has to make before going on — the web's `AlertDialog`: a [title], an optional
 * [message] and [icon], and the [actions] that answer it. Keep it for something that needs attention; for a
 * plain "are you sure?" use [QeetrixConfirmationDialog], and for a form or other content [QeetrixDialog].
 *
 * - **Shown while composed.** Call it when the dialog should show and stop when it should not, from the
 *   app's own state, as Compose's dialogs work; [onDismissRequest] asks the app to stop.
 * - **Actions** are [QeetrixDialogAction]s with roles: cancelling actions come first in reading order — at
 *   the bottom when the buttons stack on a narrow window, at the start of the row on a wide one — and a
 *   destructive action is always offered with Cancel (`qx_cancel`), which is added when missing. Choosing an
 *   action closes the dialog and runs it once. The app's own logic decides what the action does, and whether
 *   it is allowed.
 * - **Dismissal.** Back and Escape call [onDismissRequest] without running an action. A tap outside does not,
 *   unless [dismissOnClickOutside]: a stray tap must not count as an answer.
 * - **Native window.** Compose's dialog window: focus stays inside it, and the screen behind is covered by
 *   the Qeetrix scrim and cannot be reached. The window comes and goes as the app's theme animates dialogs.
 * - **Accessibility.** The title is announced as the dialog opens and is a heading; opened from a keyboard,
 *   focus starts on the first action — Cancel, the least destructive choice. The title and message scroll
 *   when the text is very large, and the actions stay in reach below them.
 * - **Drawing.** The web's dialog surface (`component.dialog.*`), at most 448 dp wide (`max-w-md`) with 16 dp
 *   to spare on each side. A destructive dialog's [icon] takes the danger colour.
 *
 * ```
 * if (confirmDelete) {
 *     QeetrixAlertDialog(
 *         title = "Delete this workspace?",
 *         message = "Its projects and files are removed for everyone.",
 *         actions = listOf(QeetrixDialogAction("Delete workspace", QeetrixDialogActionRole.Destructive) { deleteWorkspace() }),
 *         onDismissRequest = { confirmDelete = false },
 *     )
 * }
 * ```
 */
@Composable
public fun QeetrixAlertDialog(
    title: String,
    actions: List<QeetrixDialogAction>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: QeetrixIconName? = null,
    dismissOnClickOutside: Boolean = false,
) {
    DialogWindow(onDismissRequest, title, dismissOnClickOutside) {
        AlertDialogSurface(title, actions, onDismissRequest, modifier, message, icon)
    }
}

/** The alert dialog's surface, outside its window, for the dialog and for screenshots. */
@Composable
internal fun AlertDialogSurface(
    title: String,
    actions: List<QeetrixDialogAction>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    icon: QeetrixIconName? = null,
) {
    DialogSurface(title, modifier.widthIn(max = MaxWidth).fillMaxWidth()) {
        Column(
            Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(4f)),
        ) {
            if (icon != null) {
                val destructive = actions.any { it.role == QeetrixDialogActionRole.Destructive }
                QeetrixIcon(
                    icon,
                    contentDescription = null,
                    size = QeetrixIconSize.Lg,
                    tint = if (destructive) QeetrixTheme.colors.textDanger else QeetrixTheme.colors.textPrimary,
                )
            }
            DialogHeader(title, message)
        }
        DialogActions(actions, onDismissRequest)
    }
}

/** max-w-md. */
private val MaxWidth = QeetrixWebScale.spacing(112f)
