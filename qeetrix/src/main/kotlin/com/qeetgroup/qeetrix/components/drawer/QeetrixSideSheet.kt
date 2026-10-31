package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.qeetgroup.qeetrix.foundation.layout.QeetrixBreakpoint
import com.qeetgroup.qeetrix.internal.windowAtLeast

/** The edge a [QeetrixSideSheet] opens from (`value` is `@qeetrix/react`'s Drawer `side`). */
public enum class QeetrixSideSheetSide(
    public val value: String,
) {
    /** The reading start: the left in left to right, the right in right to left (`side="start"`). */
    Start("start"),

    /** The reading end (`side="end"`), the web's default. */
    End("end"),
}

/**
 * A modal sheet at the window's start or end edge, full height — `@qeetrix/react`'s Drawer as the web shows it: the
 * panel for details or an edit form beside the screen it belongs to, with a ruled header holding the [title]
 * (`title.md`) and optional [description], the [content], scrolling when it is long, and an optional ruled [footer] of
 * buttons pinned to its bottom, end-aligned with the tertiary action first.
 *
 * - **From `breakpoint.medium`** (600 dp of window width) it is a side sheet, [size] wide but always leaving 48 dp of
 *   the screen beside it (`min(size, 100vw - 48px)`); below that — a phone — it is a [QeetrixBottomSheet] with the same
 *   content, the phone's pattern. One call does the right thing on both.
 * - **[side]**: [QeetrixSideSheetSide.End] (default) or [QeetrixSideSheetSide.Start], in the reading direction.
 * - **The app owns [open]**, as for the bottom sheet: a swipe or fling towards its edge, a tap on the scrim (both only
 *   when [dismissible]), Back, Escape and the close button ask through `onOpenChange(false)`; Back always asks.
 * - **Native.** Its own window: focus stays inside and the screen behind cannot be reached. It follows the finger
 *   towards its edge and back, shrinks towards its edge with the predictive back gesture, and keeps clear of the
 *   system bars and the display cutout on its side.
 * - **Drawing** (drawer.css): `color.surface.overlay`, square, under `shadow.modal`, over the `color.overlay.scrim`
 *   backdrop; the header's `space.inset.xl` padding and 1 dp `color.border.subtle` rule; the body's `space.inset.xl`
 *   padding; the footer's rule above and `space.inset.lg` × `space.inset.xl` padding; the close button 20 dp from the
 *   top and 16 dp from the end. No handle: the web's drawer has none, and a side sheet is not dragged up.
 * - **Motion**: it slides in from its edge over `motion.duration.slow` on `motion.easing.enter` and out over
 *   `motion.duration.normal` on `motion.easing.exit`, the scrim fading with it; at once under reduced motion.
 *
 * ```
 * QeetrixSideSheet(
 *     open = editing,
 *     onOpenChange = { editing = it },
 *     title = "Edit member",
 *     footer = {
 *         QeetrixButton("Cancel", onClick = { editing = false }, variant = QeetrixButtonVariant.Ghost)
 *         QeetrixButton("Save", onClick = ::save)
 *     },
 * ) {
 *     // the form
 * }
 * ```
 *
 * @param title Names the sheet: its heading and its pane title, announced as it opens.
 * @param footerStart A quiet link or a one-line summary at the footer's start, the actions staying at its end.
 */
@Composable
public fun QeetrixSideSheet(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    side: QeetrixSideSheetSide = QeetrixSideSheetSide.End,
    size: QeetrixSheetSize = QeetrixSheetSize.Md,
    dismissible: Boolean = true,
    footerStart: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!windowAtLeast(QeetrixBreakpoint.Medium)) {
        QeetrixBottomSheet(
            open = open,
            onOpenChange = onOpenChange,
            title = title,
            modifier = modifier,
            description = description,
            size = size,
            dismissible = dismissible,
            footerStart = footerStart,
            footer = footer,
            content = content,
        )
        return
    }
    SheetWindow(
        open = open,
        onOpenChange = onOpenChange,
        title = title,
        edge = if (side == QeetrixSideSheetSide.Start) SheetEdge.Start else SheetEdge.End,
        size = size.width,
        dismissible = dismissible,
        partiallyExpandable = false,
        modifier = modifier,
    ) { onClose ->
        SheetLayout(title, description, onClose, footerStart, footer, Modifier.weight(1f), handle = false, content = content)
    }
}
