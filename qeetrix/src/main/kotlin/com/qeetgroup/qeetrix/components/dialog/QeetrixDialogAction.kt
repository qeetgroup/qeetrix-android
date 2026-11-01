package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.runtime.Immutable
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant

/**
 * What a dialog action does, which decides how it is drawn and where it goes in the footer (`value` is the
 * same string on every platform). The design rules' hierarchy (§7, §13): at most one [Primary] or
 * [Destructive] — a destructive confirmation replaces the primary, never sits beside it — at most one
 * [Secondary], and the cancelling actions first.
 */
public enum class QeetrixDialogActionRole(
    public val value: String,
) {
    /** It does what the dialog asks: the solid Ember button, last in the footer. */
    Primary("primary"),

    /** The main alternative ("Save draft"): the soft graphite button, before the primary. */
    Secondary("secondary"),

    /**
     * It closes the dialog without acting: the ghost (tertiary) button, first in the footer and so focused
     * first from a keyboard.
     */
    Cancel("cancel"),

    /**
     * It deletes or removes something: the solid danger button, in the primary's place. A dialog offering
     * one always offers Cancel too.
     */
    Destructive("destructive"),
}

/**
 * One button of a [QeetrixAlertDialog]: its [label], [role] and [onClick]. Choosing it closes the dialog —
 * `onOpenChange(false)` first, so the app stops showing it — and then runs [onClick] exactly once, however
 * fast it is tapped again; [onClick] may itself open another dialog.
 *
 * The action records what the person chose. It does not authorise anything: whatever performs the action
 * still decides whether it is allowed.
 *
 * @property label The button's text: the answer, as a verb ("Delete workspace", not "OK").
 * @property role What it does, which sets its button and its place in the footer.
 * @property enabled `false` draws the button disabled; explain why nearby.
 * @property onClick Runs once when the action is chosen, after the dialog asks to close.
 */
@Immutable
public class QeetrixDialogAction(
    public val label: String,
    public val role: QeetrixDialogActionRole = QeetrixDialogActionRole.Primary,
    public val enabled: Boolean = true,
    public val onClick: () -> Unit,
) {
    override fun toString(): String = "QeetrixDialogAction($label, ${role.value})"
}

/**
 * The actions as a dialog's footer shows them (`.qx-dialog-footer`, design rules §13): a Cancel added in front
 * of a destructive choice that has none, then the cancelling (tertiary) actions, the secondary, and the primary
 * or destructive action last, at the end of the row. Each group keeps its order.
 */
internal fun arrangeActions(
    actions: List<QeetrixDialogAction>,
    cancelLabel: String,
): List<QeetrixDialogAction> {
    require(actions.isNotEmpty()) { "A dialog needs at least one action." }
    require(actions.count { it.role == QeetrixDialogActionRole.Primary || it.role == QeetrixDialogActionRole.Destructive } <= 1) {
        "A dialog has at most one primary or destructive action: a destructive action replaces the primary (design rules §7)."
    }
    require(actions.count { it.role == QeetrixDialogActionRole.Secondary } <= 1) {
        "A dialog has at most one secondary action (design rules §7)."
    }
    val withCancel =
        if (actions.any { it.role == QeetrixDialogActionRole.Destructive } && actions.none { it.role == QeetrixDialogActionRole.Cancel }) {
            listOf(QeetrixDialogAction(cancelLabel, QeetrixDialogActionRole.Cancel) {}) + actions
        } else {
            actions
        }
    return withCancel.sortedBy { it.role.footerOrder }
}

/** Where a role goes in the footer: tertiary first, then secondary, then the primary or destructive action. */
private val QeetrixDialogActionRole.footerOrder: Int
    get() =
        when (this) {
            QeetrixDialogActionRole.Cancel -> 0
            QeetrixDialogActionRole.Secondary -> 1
            QeetrixDialogActionRole.Primary, QeetrixDialogActionRole.Destructive -> 2
        }

/** The button an action's role draws: design rules §7's hierarchy. */
internal val QeetrixDialogActionRole.buttonVariant: QeetrixButtonVariant
    get() =
        when (this) {
            QeetrixDialogActionRole.Primary, QeetrixDialogActionRole.Destructive -> QeetrixButtonVariant.Solid
            QeetrixDialogActionRole.Secondary -> QeetrixButtonVariant.Soft
            QeetrixDialogActionRole.Cancel -> QeetrixButtonVariant.Ghost
        }

/** `tone="danger"` for the destructive action; the brand tone otherwise. */
internal val QeetrixDialogActionRole.buttonTone: QeetrixButtonTone
    get() = if (this == QeetrixDialogActionRole.Destructive) QeetrixButtonTone.Danger else QeetrixButtonTone.Brand

/**
 * The intent an alert dialog takes when the app gives none: `danger` when it offers a destructive action
 * (dialog.tsx: an alert dialog with the red line), `standard` otherwise.
 */
internal fun alertIntentFor(actions: List<QeetrixDialogAction>): QeetrixDialogIntent =
    if (actions.any { it.role == QeetrixDialogActionRole.Destructive }) QeetrixDialogIntent.Danger else QeetrixDialogIntent.Standard
