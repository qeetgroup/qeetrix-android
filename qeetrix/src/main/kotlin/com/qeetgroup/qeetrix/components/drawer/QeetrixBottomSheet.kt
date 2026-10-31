package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.dialog.OverlayClose
import com.qeetgroup.qeetrix.components.dialog.OverlayFooter
import com.qeetgroup.qeetrix.components.dialog.OverlayHeader
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The widths of a [QeetrixBottomSheet] (`value` is `@qeetrix/react`'s Drawer `size`). On a phone the sheet spans
 * the window; on a larger one it is at most this wide, centred.
 */
public enum class QeetrixBottomSheetSize(
    public val value: String,
    internal val width: Dp,
) {
    /** 400 dp (`.qx-drawer[data-size="sm"] inline-size: min(400px, …)`). */
    Sm("sm", 400.dp),

    /** 560 dp (`.qx-drawer inline-size: min(560px, …)`). The default. */
    Md("md", 560.dp),

    /** 800 dp (`[data-size="lg"] min(800px, …)`). */
    Lg("lg", 800.dp),
}

/**
 * A modal sheet that rises from the bottom edge — Android's form of `@qeetrix/react`'s Drawer, the panel for
 * details or an edit form beside the screen it belongs to: a grab handle, a ruled header with the [title]
 * (`title.md`) and optional [description], the [content], scrolling when it is long, and an optional ruled
 * [footer] of buttons, end-aligned with the tertiary action first.
 *
 * - **The app owns [open].** The sheet asks to close through `onOpenChange(false)` — a swipe or fling down, a tap
 *   on the scrim, Back, Escape, the close button — and slides away; when the app sets [open] to `false` it slides
 *   away too, and leaves the composition once it is gone. Opening it again while it is still closing turns it
 *   round.
 * - **Native.** Its own window, so focus stays inside and the screen behind cannot be reached: it follows the
 *   finger and the fling, scrolls its content before it moves itself, shrinks with Android's predictive back
 *   gesture and moves up with the keyboard.
 * - **[dismissible]** (default `true`): `false` stops a swipe down and a tap outside from closing it and
 *   removes the close button, for a panel only its own actions should close, such as an unsaved form. Back
 *   still asks to close — Android never traps Back — so keep unsaved work in `onOpenChange` (a draft, or a
 *   confirmation) rather than refusing to close.
 * - **Height and width.** The sheet takes its content's height, at most 85 % of the window's; with
 *   [partiallyExpandable] a tall sheet opens half-way and expands when it is dragged up, when its content is
 *   scrolled, or when focus moves into it — and opens fully while touch exploration (TalkBack) is on. [size] caps
 *   its width on a large window.
 * - **Drawing.** The drawer's look (drawer.css): `color.surface.overlay` with square corners under `shadow.modal`,
 *   over the `color.overlay.scrim` backdrop; the header's `space.inset.xl` padding and its 1 dp
 *   `color.border.subtle` rule; the body's `space.inset.xl` padding; the footer's rule above and `space.inset.lg` ×
 *   `space.inset.xl` padding. The handle, a 40 × 4 dp `color.border.strong` mark with 2 dp corners, sits in the
 *   header's top padding, so the title is still 24 dp from the sheet's edge. It is only a mark: the whole sheet
 *   drags, and the close button and Back close it, so it is neither a control nor announced.
 * - **Motion** (drawer.css): in over `motion.duration.slow` on `motion.easing.enter`, out over
 *   `motion.duration.normal` on `motion.easing.exit`, the scrim fading with it; at once under reduced motion.
 * - **[modifier]** goes to the sheet's surface, as it moves: use it for semantics and test tags.
 *
 * Use a [com.qeetgroup.qeetrix.components.dialog.QeetrixDialog] for a task that should take the person's whole
 * attention.
 *
 * ```
 * QeetrixBottomSheet(
 *     open = filtering,
 *     onOpenChange = { filtering = it },
 *     title = "Filter invoices",
 *     footer = {
 *         QeetrixButton("Clear", onClick = ::clear, variant = QeetrixButtonVariant.Ghost)
 *         QeetrixButton("Show results", onClick = { filtering = false })
 *     },
 * ) {
 *     // the filters
 * }
 * ```
 *
 * @param title Names the sheet: its heading and its pane title, announced as it opens.
 * @param footerStart A quiet link or a one-line summary at the footer's start, the actions staying at its end;
 *   it takes its own line when there is no room. It inherits `color.text.subtle`; draw text in it at
 *   `QeetrixTextSize.Sm`.
 */
@Composable
public fun QeetrixBottomSheet(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    size: QeetrixBottomSheetSize = QeetrixBottomSheetSize.Md,
    dismissible: Boolean = true,
    partiallyExpandable: Boolean = false,
    footerStart: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    SheetWindow(
        open = open,
        onOpenChange = onOpenChange,
        title = title,
        edge = SheetEdge.Bottom,
        size = size.width,
        dismissible = dismissible,
        partiallyExpandable = partiallyExpandable,
        modifier = modifier,
    ) { onClose ->
        DragHandle()
        SheetLayout(title, description, onClose, footerStart, footer, Modifier.weight(1f, fill = false), content)
    }
}

/**
 * The sheet's parts below the handle — the ruled header, the scrolling body, the ruled footer, and the close
 * button over the header's end — outside the sheet, for the sheet and for screenshots. The handle's row takes
 * [HandleRow] of the header's top padding.
 */
@Composable
internal fun SheetLayout(
    title: String,
    description: String?,
    onClose: (() -> Unit)?,
    footerStart: (@Composable () -> Unit)?,
    footer: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = QeetrixTheme.colors
    CompositionLocalProvider(LocalQeetrixContentColor provides colors.textDefault) {
        Box(modifier.fillMaxWidth().semantics { paneTitle = title }) {
            Column(Modifier.fillMaxWidth()) {
                // .qx-drawer-header: padding space.inset.xl, padding-inline-end 56px beside the close button
                OverlayHeader(
                    title,
                    description,
                    Modifier.padding(
                        start = QeetrixSpace.InsetXl,
                        top = QeetrixSpace.InsetXl - HandleRow,
                        end = if (onClose != null) CloseRoom else QeetrixSpace.InsetXl,
                        bottom = QeetrixSpace.InsetXl,
                    ),
                )
                // border-block-end: border.width.default solid color.border.subtle
                QeetrixSeparator()
                // .qx-drawer-body: flex 1, overflow-y auto, padding space.inset.xl
                Column(
                    Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(QeetrixSpace.InsetXl),
                    content = content,
                )
                if (footer != null || footerStart != null) {
                    // .qx-drawer-footer: a rule above, padding space.inset.lg space.inset.xl
                    QeetrixSeparator()
                    OverlayFooter(
                        footerStart,
                        Modifier.padding(horizontal = QeetrixSpace.InsetXl, vertical = QeetrixSpace.InsetLg),
                    ) { footer?.invoke() }
                }
            }
            // .qx-drawer-close: inset-block-start 20px, inset-inline-end 16px, from the sheet's edge above the handle row
            if (onClose != null) OverlayClose(onClose, Modifier.align(Alignment.TopEnd), top = 20.dp - HandleRow, end = 16.dp)
        }
    }
}

/**
 * The grab handle: a 40 × 4 dp mark in `color.border.strong` with `radius.sm` corners — the small-mark corner —
 * 8 dp below the sheet's edge. The web's drawer has none; Android's sheets show one as the sign that the sheet
 * moves. It is drawn only: the whole sheet drags, the close button and Back close it, and an accessibility
 * service has nothing to find here (Material's 12 dp clickable handle row was finding Q-6).
 */
@Composable
internal fun DragHandle() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = HandleRow - HandleSize.height),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(HandleSize.width, HandleSize.height).background(QeetrixTheme.colors.borderStrong, QeetrixShapes.Sm))
    }
}

/** The handle's 40 × 4 dp mark. */
private object HandleSize {
    val width = 40.dp
    val height = 4.dp
}

/** The handle's row: 8 dp above the 4 dp mark, taken from the header's top padding. */
internal val HandleRow = 12.dp

/** `.qx-drawer[data-dismissible] .qx-drawer-header { padding-inline-end: 56px }`. */
private val CloseRoom = 56.dp
