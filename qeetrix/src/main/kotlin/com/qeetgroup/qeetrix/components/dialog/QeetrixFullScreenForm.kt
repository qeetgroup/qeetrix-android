package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.foundation.layout.QeetrixBreakpoint
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.DialogScrimOnly
import com.qeetgroup.qeetrix.internal.DialogSystemBars
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.windowAtLeast
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A modal screen for creating or editing something — a new account, an edited profile — where a dialog or a sheet is
 * too small. `@qeetrix/react` has no counterpart (its Dialog's `fullscreen` size is the nearest).
 *
 * - **The bar**: Close (`x`, "Close") at the start, the [title] (`title.lg`, a heading), and the confirming action
 *   ([confirmLabel], a small solid button) at the end, over a 1 dp `color.border.default` rule.
 * - **The form** ([content]) scrolls with `space.inset.lg` round it, and keeps clear of the keyboard, so the focused field
 *   stays in view above it.
 * - **Unsaved changes**: with [dirty], Close and Back first ask "Discard changes?" (Discard, danger; Keep editing).
 * - **Size**: the whole screen on a phone; from `breakpoint.medium` a form sheet — 560 dp wide, at most 90 % of the
 *   height, `radius.modal` corners, over the scrim.
 *
 * ```
 * QeetrixFullScreenForm(open, { open = it }, title = "New account", confirmLabel = "Save", onConfirm = ::save, dirty = edited) {
 *     QeetrixTextField(name, label = "Account name")
 * }
 * ```
 */
@Composable
public fun QeetrixFullScreenForm(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    title: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    dirty: Boolean = false,
    confirmEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!open) return
    var discarding by rememberSaveable { mutableStateOf(false) }
    val close = { if (dirty) discarding = true else onOpenChange(false) }
    val sheet = windowAtLeast(QeetrixBreakpoint.Medium)
    Dialog(
        onDismissRequest = close,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false),
    ) {
        val colors = QeetrixTheme.colors
        // On a tablet the scrim below replaces the window's dim, which would otherwise darken the screen twice.
        DialogScrimOnly()
        // On a phone the form's surface is under the bars; on a tablet the scrim is.
        DialogSystemBars(darkIcons = !QeetrixTheme.isDark && !sheet)
        Box(
            Modifier
                .fillMaxSize()
                .then(if (sheet) Modifier.background(colors.overlayScrim) else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier
                    .then(
                        if (sheet) {
                            Modifier
                                .widthIn(
                                    max = SHEET_WIDTH,
                                ).fillMaxWidth()
                                .fillMaxHeight(SHEET_HEIGHT)
                                .background(colors.surfaceOverlay, QeetrixShapes.Modal)
                        } else {
                            Modifier.fillMaxSize().background(colors.surfaceDefault)
                        },
                    ).semantics { paneTitle = title }
                    .windowInsetsPadding(if (sheet) WindowInsets.ime else WindowInsets.safeDrawing.union(WindowInsets.ime)),
            ) {
                val rule = colors.borderDefault
                Row(
                    Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val width = QeetrixBorder.WidthDefault.toPx()
                            drawRect(rule, Offset(0f, size.height - width), Size(size.width, width))
                        }.heightIn(min = QeetrixTheme.density.sizeControlLg + BAR_EXTRA)
                        .padding(horizontal = QeetrixSpace.InsetXs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
                ) {
                    QeetrixIconButton(
                        QeetrixIconName.X,
                        stringResource(R.string.qx_close),
                        onClick = close,
                        tone = QeetrixButtonTone.Neutral,
                    )
                    BasicText(
                        title,
                        Modifier.weight(1f).semantics { heading() },
                        style =
                            QeetrixTheme.typography.titleLg
                                .copy(color = colors.textDefault)
                                .labelDirection(alignToLayout = true),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    QeetrixButton(
                        confirmLabel,
                        onClick = onConfirm,
                        size = QeetrixButtonSize.Sm,
                        enabled = confirmEnabled,
                        modifier = Modifier.padding(end = QeetrixSpace.InsetXs),
                    )
                }
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(QeetrixSpace.InsetLg),
                    verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.spaceFormGap),
                    content = content,
                )
            }
        }
    }
    if (discarding) {
        QeetrixConfirmationDialog(
            open = true,
            onOpenChange = { if (!it) discarding = false },
            title = stringResource(R.string.qx_form_discard_title),
            confirmLabel = stringResource(R.string.qx_form_discard),
            onConfirm = {
                discarding = false
                onOpenChange(false)
            },
            description = stringResource(R.string.qx_form_discard_description),
            intent = QeetrixDialogIntent.Danger,
            cancelLabel = stringResource(R.string.qx_form_keep_editing),
        )
    }
}

/** The bar: `size.control.lg` + 16 dp, as the top app bar. */
private val BAR_EXTRA = 16.dp

/** The form sheet on a tablet: Dialog `md`, 560 dp, at most 90 % of the window's height. */
private val SHEET_WIDTH = 560.dp
private const val SHEET_HEIGHT = 0.9f
