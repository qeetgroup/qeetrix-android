package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.button.QeetrixIconButtonSize
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDialogComponentTokens

/**
 * A modal sheet that rises from the bottom edge — the web's `Drawer`, "the mobile bottom sheet": a grab
 * handle, an optional [title] and [description], the [content], scrolling when it is long, and an optional
 * [footer] of buttons. It holds supplementary content and actions — details, filters, a short form, a list
 * of choices — and the screen behind stays where it was.
 *
 * - **Presented by [visible]**, from the app's own state. Set it to `false` to close the sheet: it slides
 *   away, and leaves the composition once it is gone. [onDismissRequest] is called when the person closes it
 *   — a swipe down, a tap outside, Back, the close button — and should set [visible] to `false`. Opening it
 *   again while it is still closing turns it round.
 * - **Native.** It is Compose's modal bottom sheet in its own window: it follows the finger and the fling,
 *   scrolls its content before it moves itself, animates with Android's predictive back gesture, keeps
 *   focus inside, and moves up with the keyboard. The handle carries the expand, collapse and dismiss
 *   actions for accessibility services ("Drag handle", `qx_drag_handle`).
 * - **Height.** The sheet takes its content's height, at most 85 % of the window's (`max-h-[85dvh]`), and
 *   at most 640 dp wide on a large window. With [partiallyExpandable] a tall sheet opens half-way and
 *   expands when it is dragged up.
 * - **[dismissOnGesture]** `false` stops a swipe down or a tap outside from closing the sheet, for a step
 *   the person has to finish or cancel deliberately. Back still closes it — Android never traps Back — and
 *   so does the close button; keep unsaved work in [onDismissRequest] (a draft, or a confirmation) rather
 *   than refusing to close.
 * - **Drawing.** The web drawer: the dialog surface (`component.dialog.*`) rounded on top, a rule along its
 *   top edge, a 40 × 4 dp handle in `border.strong`, a 16 dp header, and the scrim behind. No shadow: over the
 *   scrim it would not be seen, and Material's sheet cannot draw one.
 * - **[modifier]** goes to Material's sheet, which applies it before moving the sheet into place: use it for
 *   semantics and test tags. A drawing modifier (a background, a border) would be drawn where the sheet starts,
 *   at the top of the window, not where it is.
 *
 * The app keeps the business state; the sheet only shows it. Use a [com.qeetgroup.qeetrix.components.dialog.QeetrixDialog]
 * for a task that should take the person's whole attention.
 *
 * ```
 * var filtering by rememberSaveable { mutableStateOf(false) }
 * QeetrixBottomSheet(
 *     visible = filtering,
 *     onDismissRequest = { filtering = false },
 *     title = "Filter invoices",
 *     footer = { QeetrixButton("Show results", onClick = { filtering = false }, modifier = Modifier.fillMaxWidth()) },
 * ) {
 *     QeetrixRadioGroup(status, { status = it }, statusOptions, label = "Status")
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun QeetrixBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    partiallyExpandable: Boolean = false,
    dismissOnGesture: Boolean = true,
    showCloseButton: Boolean = true,
    footer: (@Composable ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val gestures by rememberUpdatedState(dismissOnGesture)
    // A swipe that would hide the sheet is refused when gestures may not dismiss it; Back and the code below
    // still hide it.
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = !partiallyExpandable,
            confirmValueChange = { it != SheetValue.Hidden || gestures },
        )
    val dismiss by rememberUpdatedState(onDismissRequest)
    // Whether the sheet is in the composition: from the moment it is asked for until it has finished leaving.
    var showing by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            // Composing the sheet shows it; one still leaving is turned round.
            if (showing) sheetState.show() else showing = true
        } else if (showing) {
            sheetState.hide()
            showing = false
        }
    }
    if (!showing) return

    val shape = RoundedCornerShape(topStart = QeetrixDialogComponentTokens.Corner, topEnd = QeetrixDialogComponentTokens.Corner)
    val maxHeight = LocalWindowInfo.current.containerDpSize.height * MAX_HEIGHT_FRACTION
    val close = if (showCloseButton) onDismissRequest else null
    ModalBottomSheet(
        onDismissRequest = {
            showing = false
            dismiss()
        },
        // Nothing of Qeetrix's drawing goes on this modifier: Material applies it before it moves the sheet into place,
        // so it would draw where the sheet starts, at the top of the window (Phase 7, seen on API 29 and 36). The rule
        // along the top edge is drawn by the handle, inside the sheet.
        modifier = modifier,
        sheetState = sheetState,
        sheetMaxWidth = QeetrixWebScale.BreakpointSm,
        shape = shape,
        containerColor = QeetrixDialogComponentTokens.Background,
        contentColor = QeetrixDialogComponentTokens.Foreground,
        tonalElevation = 0.dp,
        scrimColor = QeetrixDialogComponentTokens.Scrim,
        dragHandle = { DragHandle() },
        contentWindowInsets = { WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom) },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true, shouldDismissOnClickOutside = dismissOnGesture),
    ) {
        SheetLayout(title, description, close, footer, Modifier.heightIn(max = maxHeight), content)
    }
}

/**
 * The sheet's parts below the handle — the header, the scrolling body and the footer, and the close button
 * over the header's end — outside the sheet, for the sheet and for screenshots.
 */
@Composable
internal fun SheetLayout(
    title: String?,
    description: String?,
    onClose: (() -> Unit)?,
    footer: (@Composable ColumnScope.() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    CompositionLocalProvider(LocalQeetrixContentColor provides QeetrixDialogComponentTokens.Foreground) {
        Box(modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth()) {
                if (title != null || description != null) {
                    // SheetHeader: flex flex-col gap-1 p-4; pe-12 beside the close button
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(QeetrixWebScale.spacing(4f))
                            .padding(end = if (onClose != null) QeetrixWebScale.spacing(8f) else QeetrixWebScale.spacing(0f)),
                        verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1f)),
                    ) {
                        if (title != null) {
                            BasicText(
                                title,
                                Modifier.semantics { heading() },
                                style = QeetrixTheme.typography.heading.copy(color = QeetrixTheme.colors.textPrimary),
                            )
                        }
                        if (description != null) {
                            BasicText(description, style = descriptionStyle())
                        }
                    }
                } else if (onClose != null) {
                    // No header: the close button's 48 dp row, clear of the content.
                    Box(Modifier.heightIn(min = QeetrixWebScale.spacing(13f)))
                }
                // SheetBody: -my-1 min-h-0 flex-1 overflow-y-auto px-4 py-1
                Column(
                    Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = QeetrixWebScale.spacing(4f), vertical = QeetrixWebScale.spacing(1f)),
                    content = content,
                )
                if (footer != null) {
                    // SheetFooter: mt-auto flex flex-col gap-2 p-4
                    Column(
                        Modifier.fillMaxWidth().padding(QeetrixWebScale.spacing(4f)),
                        verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)),
                        content = footer,
                    )
                } else {
                    Box(Modifier.heightIn(min = QeetrixWebScale.spacing(4f)))
                }
            }
            if (onClose != null) {
                // ghost icon-sm, absolute inset-e-3.5 top-7.5 — centred on the title's first line, below the handle
                QeetrixIconButton(
                    QeetrixIconName.X,
                    stringResource(R.string.qx_close),
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopEnd).padding(QeetrixWebScale.spacing(1f)),
                    size = QeetrixIconButtonSize.Sm,
                )
            }
        }
    }
}

/**
 * The grab handle: `mx-auto mb-1 h-1 w-10 rounded-full bg-border-strong`, below the sheet's `pt-2`. The
 * sheet gives it the expand, collapse and dismiss actions; it is named so accessibility services can offer
 * them.
 */
@Composable
internal fun DragHandle() {
    val name = stringResource(R.string.qx_drag_handle)
    val rule = QeetrixDialogComponentTokens.Border
    Box(
        Modifier
            .fillMaxWidth()
            .drawWithCache {
                // border-t: the hairline along the sheet's top edge and into its rounded corners, drawn in the handle's
                // row because it is the top of what moves with the sheet.
                val stroke = QeetrixStroke.Hairline.toPx()
                val inset = stroke / 2
                val radius = QeetrixDialogComponentTokens.Corner.toPx()
                val arc = 2 * (radius - inset)
                val edge =
                    Path().apply {
                        moveTo(inset, radius)
                        arcTo(Rect(inset, inset, inset + arc, inset + arc), 180f, 90f, false)
                        lineTo(size.width - radius, inset)
                        arcTo(Rect(size.width - inset - arc, inset, size.width - inset, inset + arc), 270f, 90f, false)
                    }
                onDrawBehind { drawPath(edge, rule, style = Stroke(stroke)) }
            }.padding(top = QeetrixWebScale.spacing(2f), bottom = QeetrixWebScale.spacing(1f))
            .semantics { contentDescription = name },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(QeetrixWebScale.spacing(10f), QeetrixWebScale.spacing(1f))
                .background(QeetrixTheme.colors.borderStrong, QeetrixShapes.Pill),
        )
    }
}

/** text-sm text-muted-foreground. */
@Composable
private fun descriptionStyle(): TextStyle = DescriptionStyle.copy(color = QeetrixTheme.colors.textTertiary)

private val DescriptionStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)

/** max-h-[85dvh]. */
private const val MAX_HEIGHT_FRACTION = 0.85f
