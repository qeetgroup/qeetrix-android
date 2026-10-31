package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
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
 * - **The app owns [open].** The sheet asks to close through `onOpenChange(false)` — a swipe down, a tap on
 *   the scrim, Back, the close button, the handle's dismiss action — and slides away; when the app sets [open]
 *   to `false` it slides away too, and leaves the composition once it is gone. Opening it again while it is
 *   still closing turns it round.
 * - **Native.** It is Material 3's modal bottom sheet in its own window, drawn as Qeetrix: it follows the
 *   finger and the fling, scrolls its content before it moves itself, animates with Android's predictive back
 *   gesture, keeps focus inside and moves up with the keyboard. The handle carries the expand, collapse and
 *   dismiss actions for accessibility services ("Drag handle", `qx_drag_handle`).
 * - **[dismissible]** (default `true`): `false` stops a swipe down and a tap outside from closing it and
 *   removes the close button, for a panel only its own actions should close, such as an unsaved form. Back
 *   still asks to close — Android never traps Back — so keep unsaved work in `onOpenChange` (a draft, or a
 *   confirmation) rather than refusing to close.
 * - **Height and width.** The sheet takes its content's height, at most 85 % of the window's; with
 *   [partiallyExpandable] a tall sheet opens half-way and expands when it is dragged up. [size] caps its width
 *   on a large window.
 * - **Drawing.** The drawer's look (drawer.css): `color.surface.overlay` with square corners over the
 *   `color.overlay.scrim` backdrop; the header's `space.inset.xl` padding and its 1 dp `color.border.subtle`
 *   rule; the body's `space.inset.xl` padding; the footer's rule above and `space.inset.lg` ×
 *   `space.inset.xl` padding. The handle, a 40 × 4 dp `color.border.strong` mark with 2 dp corners, sits in
 *   the header's top padding, so the title is still 24 dp from the sheet's edge.
 * - **[modifier]** goes to Material's sheet, which applies it before moving the sheet into place: use it for
 *   semantics and test tags. A drawing modifier (a background, a border) would be drawn where the sheet
 *   starts, at the top of the window, not where it is.
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
@OptIn(ExperimentalMaterial3Api::class)
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
    val gestures by rememberUpdatedState(dismissible)
    // A swipe that would hide the sheet is refused when it is not dismissible; Back and the code below still hide it.
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = !partiallyExpandable,
            confirmValueChange = { it != SheetValue.Hidden || gestures },
        )
    val change by rememberUpdatedState(onOpenChange)
    // Whether the sheet is in the composition: from the moment it is asked for until it has finished leaving.
    var showing by remember { mutableStateOf(false) }
    LaunchedEffect(open) {
        if (open) {
            // Composing the sheet shows it; one still leaving is turned round.
            if (showing) sheetState.show() else showing = true
        } else if (showing) {
            sheetState.hide()
            showing = false
        }
    }
    if (!showing) return

    val colors = QeetrixTheme.colors
    val maxHeight = LocalWindowInfo.current.containerDpSize.height * MAX_HEIGHT_FRACTION
    val isOpen by rememberUpdatedState(open)
    val close = { if (isOpen) change(false) }
    ModalBottomSheet(
        onDismissRequest = {
            showing = false
            change(false)
        },
        // Nothing of Qeetrix's drawing goes on this modifier: Material applies it before it moves the sheet into place,
        // so it would draw where the sheet starts, at the top of the window (Phase 7, seen on API 29 and 36).
        modifier = modifier,
        sheetState = sheetState,
        sheetMaxWidth = size.width,
        shape = QeetrixShapes.None, // .qx-drawer has no border-radius: square, flush with its edge
        containerColor = colors.surfaceOverlay,
        contentColor = colors.textDefault,
        tonalElevation = 0.dp,
        scrimColor = colors.overlayScrim, // .qx-drawer-backdrop
        dragHandle = { DragHandle() },
        contentWindowInsets = { WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom) },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true, shouldDismissOnClickOutside = dismissible),
    ) {
        SheetLayout(
            title,
            description,
            if (dismissible) close else null,
            footerStart,
            footer,
            Modifier.heightIn(max = maxHeight),
            content,
        )
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
 * 8 dp below the sheet's edge. The web's drawer has none (it swipes from anywhere); Android's sheets show one, and
 * the sheet gives it the expand, collapse and dismiss actions, so it is named for accessibility services.
 */
@Composable
internal fun DragHandle() {
    val name = stringResource(R.string.qx_drag_handle)
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = HandleRow - HandleSize.height)
            .semantics { contentDescription = name },
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

/** The sheet's height cap: 85 % of the window, as the dialog's `max-block-size: 85vh`. */
private const val MAX_HEIGHT_FRACTION = 0.85f
