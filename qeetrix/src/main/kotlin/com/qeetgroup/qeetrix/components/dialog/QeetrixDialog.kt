package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.button.QeetrixIconButtonSize
import com.qeetgroup.qeetrix.components.form.ActionRow
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixWebScale

/** The widths of a [QeetrixDialog] (`value` is the web name). Every size leaves 16 dp to each side of the window. */
public enum class QeetrixDialogSize(
    public val value: String,
) {
    /** Up to 384 dp (`max-w-sm`): a short question with a field or two. */
    Sm("sm"),

    /** Up to 512 dp (`max-w-lg`). */
    Default("default"),

    /** Up to 672 dp (`max-w-2xl`): a longer form. */
    Lg("lg"),

    /** Up to 896 dp (`max-w-4xl`): a working surface on a large window. */
    Xl("xl"),

    /** The whole window less its margins: a dense surface that should still be a dialog. */
    Full("full"),
}

/**
 * A modal window for a focused task — the web's `Dialog`: a [title] and optional [description] at the top,
 * the [content] below them, scrolling when it is long, and an optional [footer] of buttons that stays in
 * reach. Build the content from Qeetrix components: fields, a radio group, text.
 *
 * - **Shown while composed**, from the app's state; [onDismissRequest] asks the app to stop. The dialog
 *   does nothing with the content's values: the footer's buttons are the app's, and close the dialog only
 *   when the app says so — so a form can validate before it closes.
 * - **Dismissal.** Back, Escape, the close button ([showCloseButton], named "Close", `qx_close`) and — with
 *   [dismissOnClickOutside], the default — a tap outside call [onDismissRequest]. To keep unsaved work, ask
 *   in [onDismissRequest] before closing, rather than refusing to close.
 * - **Footer.** Its buttons are laid out as [com.qeetgroup.qeetrix.components.form.QeetrixFormActions] lays
 *   them out: stacked with the last on top on a narrow window, in a row at the end on a wide one. Put the
 *   secondary action first.
 * - **Size.** [size] caps the width, by the web's steps; the height follows the content up to the window's,
 *   and the content scrolls between the pinned header and footer.
 * - **Native window and accessibility** as for [QeetrixAlertDialog]: focus stays inside, the title is
 *   announced and is a heading, and the screen behind is covered by the scrim.
 *
 * Choose this for content, [QeetrixAlertDialog] for a decision, and a
 * [com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet] for supplementary content or actions on a
 * phone, where a sheet is easier to reach and to swipe away.
 *
 * ```
 * if (renaming) {
 *     QeetrixDialog(
 *         title = "Rename workspace",
 *         description = "Everyone in the workspace sees the new name.",
 *         onDismissRequest = { renaming = false },
 *         footer = {
 *             QeetrixButton("Cancel", onClick = { renaming = false }, variant = QeetrixButtonVariant.Outline)
 *             QeetrixButton("Save", onClick = ::save)
 *         },
 *     ) {
 *         QeetrixTextField(name, label = "Name", required = true, message = nameMessage)
 *     }
 * }
 * ```
 */
@Composable
public fun QeetrixDialog(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    size: QeetrixDialogSize = QeetrixDialogSize.Default,
    showCloseButton: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    DialogWindow(onDismissRequest, title, dismissOnClickOutside) {
        DialogLayout(title, onDismissRequest, modifier, description, size, showCloseButton, footer, content)
    }
}

/** The dialog's surface and parts, outside its window, for the dialog and for screenshots. */
@Composable
internal fun DialogLayout(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    size: QeetrixDialogSize = QeetrixDialogSize.Default,
    showCloseButton: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val full = size == QeetrixDialogSize.Full
    val width = size.maxWidth
    Box(
        modifier
            .then(if (width != null) Modifier.widthIn(max = width) else Modifier)
            .fillMaxWidth()
            .then(if (full) Modifier.fillMaxHeight() else Modifier),
    ) {
        DialogSurface(title, Modifier.fillMaxWidth().then(if (full) Modifier.fillMaxHeight() else Modifier)) {
            // *:data-[slot=dialog-header]:pe-8 keeps a long title clear of the close button
            val headerEnd = if (showCloseButton) QeetrixWebScale.spacing(8f) else QeetrixWebScale.spacing(0f)
            DialogHeader(title, description, Modifier.padding(end = headerEnd))
            // DialogBody: the scrolling region between the pinned header and footer, with room for a focus ring.
            Column(
                Modifier
                    .weight(1f, fill = full)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = QeetrixWebScale.spacing(1f)), // py-1
                content = content,
            )
            if (footer != null) ActionRow(content = footer)
        }
        if (showCloseButton) {
            // variant="ghost" size="icon-sm" absolute inset-e-5.5 top-5.5: the 28 dp button, in its 48 dp touch area
            QeetrixIconButton(
                QeetrixIconName.X,
                stringResource(R.string.qx_close),
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = QeetrixWebScale.spacing(3f), end = QeetrixWebScale.spacing(3f)),
                size = QeetrixIconButtonSize.Sm,
            )
        }
    }
}

/** max-w-sm, max-w-lg, max-w-2xl, max-w-4xl; none for full. */
private val QeetrixDialogSize.maxWidth: Dp?
    get() =
        when (this) {
            QeetrixDialogSize.Sm -> QeetrixWebScale.spacing(96f)
            QeetrixDialogSize.Default -> QeetrixWebScale.spacing(128f)
            QeetrixDialogSize.Lg -> QeetrixWebScale.spacing(168f)
            QeetrixDialogSize.Xl -> QeetrixWebScale.spacing(224f)
            QeetrixDialogSize.Full -> null
        }
