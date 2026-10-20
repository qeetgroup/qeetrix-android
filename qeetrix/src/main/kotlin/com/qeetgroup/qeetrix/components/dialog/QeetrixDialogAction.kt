package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.runtime.Immutable
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant

/** What a dialog action does, which decides how it is drawn and where it goes (`value` is the same string on every platform). */
public enum class QeetrixDialogActionRole(
    public val value: String,
) {
    /** It does what the dialog asks: the primary button. */
    Default("default"),

    /** It closes the dialog without acting: the outline button, first in reading order, focused first from a keyboard. */
    Cancel("cancel"),

    /** It deletes or removes something: the destructive button. A dialog offering one always offers Cancel too. */
    Destructive("destructive"),
}

/**
 * One button of a [QeetrixAlertDialog]: its [label], [role] and [onClick]. Choosing it closes the dialog —
 * `onDismissRequest` first, so the app stops showing it — and then runs [onClick] exactly once, however fast
 * it is tapped again; [onClick] may itself show another dialog.
 *
 * The action records what the person chose. It does not authorise anything: whatever performs the action
 * still decides whether it is allowed.
 */
@Immutable
public class QeetrixDialogAction(
    public val label: String,
    public val role: QeetrixDialogActionRole = QeetrixDialogActionRole.Default,
    public val enabled: Boolean = true,
    public val onClick: () -> Unit,
) {
    override fun toString(): String = "QeetrixDialogAction($label, ${role.value})"
}

/**
 * The actions as a dialog shows them: a Cancel added in front of a destructive choice that has none, and
 * the cancelling actions first — the first in reading order, and so at the bottom of a stacked footer and
 * the start of a row, with the committing action at the end and on top (the web's `AlertDialogFooter`).
 */
internal fun arrangeActions(
    actions: List<QeetrixDialogAction>,
    cancelLabel: String,
): List<QeetrixDialogAction> {
    require(actions.isNotEmpty()) { "A dialog needs at least one action." }
    val withCancel =
        if (actions.any { it.role == QeetrixDialogActionRole.Destructive } && actions.none { it.role == QeetrixDialogActionRole.Cancel }) {
            listOf(QeetrixDialogAction(cancelLabel, QeetrixDialogActionRole.Cancel) {}) + actions
        } else {
            actions
        }
    val (cancelling, committing) = withCancel.partition { it.role == QeetrixDialogActionRole.Cancel }
    return cancelling + committing
}

/** The button variant for an action's role: AlertDialogCancel is `outline`, AlertDialogAction `default`. */
internal val QeetrixDialogActionRole.buttonVariant: QeetrixButtonVariant
    get() =
        when (this) {
            QeetrixDialogActionRole.Default -> QeetrixButtonVariant.Default
            QeetrixDialogActionRole.Cancel -> QeetrixButtonVariant.Outline
            QeetrixDialogActionRole.Destructive -> QeetrixButtonVariant.Destructive
        }
