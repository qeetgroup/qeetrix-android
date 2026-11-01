package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors

/**
 * The kind of decision a dialog asks for (`value` is `@qeetrix/react`'s `intent`), drawn as the 3 dp intent
 * line on top of the dialog (`border.width.accent`, design rules §2, §13).
 */
public enum class QeetrixDialogIntent(
    public val value: String,
) {
    /** A task or a question: the Ember line (`color.border.brand`). The default. */
    Standard("standard"),

    /**
     * Something destructive happens if the person confirms: the red line (`color.border.danger`). An alert
     * dialog, which only its actions — and Back — close by default: `dismissible` is `false` for it.
     */
    Danger("danger"),

    /** Something needs a second look before it happens: the amber line (`color.icon.warning`). An alert dialog. */
    Warning("warning"),
    ;

    /** dialog.css `border-block-start-color` per `data-intent`. */
    internal fun lineColor(colors: QeetrixColors) =
        when (this) {
            Standard -> colors.borderBrand
            Danger -> colors.borderDanger
            Warning -> colors.iconWarning
        }

    /** dialog.tsx: `danger` and `warning` render Base UI's Alert Dialog, which never closes on an outside tap. */
    internal val isAlert: Boolean get() = this != Standard
}

/**
 * The width of a dialog (`value` is `@qeetrix/react`'s `size`). Every width is capped by the window less
 * `space.inset.xl` (24 dp) on each side and its safe-drawing insets.
 */
public enum class QeetrixDialogSize(
    public val value: String,
    internal val width: Dp?,
) {
    /** 400 dp (`.qx-dialog[data-size="sm"] inline-size: min(100%, 400px)`): a question, a confirmation. */
    Sm("sm", 400.dp),

    /** 560 dp (`.qx-dialog inline-size: min(100%, 560px)`): a short form. The default. */
    Md("md", 560.dp),

    /** 800 dp (`[data-size="lg"] min(100%, 800px)`): a longer form. */
    Lg("lg", 800.dp),

    /** 1120 dp (`[data-size="xl"] min(100%, 1120px)`): a working surface on a large window. */
    Xl("xl", 1120.dp),

    /**
     * The whole window, for a task that needs the whole screen: square corners, no height cap, still under
     * its intent line (`[data-size="fullscreen"]`).
     */
    Fullscreen("fullscreen", null),
}

/**
 * A focused task or decision over the screen — `@qeetrix/react`'s Dialog: the 3 dp [intent] line on top, a
 * [title] (`title.md`) and optional [description], the [content], scrolling when it is long, and an optional
 * [footer] of buttons, end-aligned with the tertiary action first and the primary or destructive one last.
 * Build the content from Qeetrix components: fields, a radio group, text.
 *
 * - **The app owns [open].** The dialog asks to close through `onOpenChange(false)` — Back, the close
 *   button, a tap on the scrim — and stays until the app sets [open] to `false`; then it fades and sinks
 *   8 dp in `motion.duration.normal` and leaves. It opens with a fade and an 8 dp rise in
 *   `motion.duration.slow` (design rules §18), at once under reduced motion. The footer's buttons are the
 *   app's, so a form validates before it closes: close once the action has succeeded.
 * - **[dismissible]** (default `true`, except for [QeetrixDialogIntent.Danger]): the close button ("Close",
 *   `qx_close`, a neutral ghost icon button at the top end) and, for a [QeetrixDialogIntent.Standard] dialog,
 *   a tap on the scrim. An alert dialog (`danger`, `warning`) never closes on a tap outside. **Back always
 *   asks to close**, dismissible or not — Android never traps Back — so treat it as the least destructive
 *   answer.
 * - **Window and accessibility.** Compose's dialog window, edge to edge, with the `color.overlay.scrim`
 *   backdrop: focus stays inside, the screen behind cannot be reached, and the title — a heading — is the
 *   window's pane title, announced as it opens. Opened from a keyboard, focus goes to the first field, or
 *   else the footer's first action, the least destructive one; the close button comes last.
 * - **Size.** [size] caps the width (400, 560, 800 or 1120 dp, or [QeetrixDialogSize.Fullscreen]); the height
 *   follows the content up to 85 % of the window, and the content scrolls between the header and footer.
 *
 * Choose [QeetrixAlertDialog] or [QeetrixConfirmationDialog] for a decision made with buttons, and a
 * [com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet] for supplementary content on a phone.
 *
 * ```
 * QeetrixDialog(
 *     open = renaming,
 *     onOpenChange = { renaming = it },
 *     title = "Rename workspace",
 *     description = "Everyone in the workspace sees the new name.",
 *     footer = {
 *         QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Ghost)
 *         QeetrixButton("Save", onClick = ::save)
 *     },
 * ) {
 *     // the form's fields
 * }
 * ```
 *
 * @param footerStart A quiet link or a one-line summary at the footer's start ("Adding many? Import a file"),
 *   the actions staying at its end; it takes its own line when there is no room. Draw text in it at
 *   `QeetrixTextSize.Sm`: it inherits `color.text.subtle`.
 * @param footer The actions, as [com.qeetgroup.qeetrix.components.button.QeetrixButton]s: tertiary (ghost)
 *   first, then the primary — at most one primary and one secondary (design rules §7).
 * @param content The body (`DialogBody`), under the header; none for a dialog that is only its title, description
 *   and actions, which then has no body padding either.
 */
@Composable
public fun QeetrixDialog(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    intent: QeetrixDialogIntent = QeetrixDialogIntent.Standard,
    size: QeetrixDialogSize = QeetrixDialogSize.Md,
    dismissible: Boolean = intent != QeetrixDialogIntent.Danger,
    footerStart: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val change by rememberUpdatedState(onOpenChange)
    val isOpen by rememberUpdatedState(open)
    // Nothing asks to close again while the dialog leaves.
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
            footerStart = footerStart,
            footer = footer,
            content = content,
        )
    }
}
