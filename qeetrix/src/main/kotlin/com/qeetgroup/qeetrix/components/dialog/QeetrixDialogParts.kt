package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.form.ActionRow
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDialogComponentTokens

/**
 * The window every Qeetrix dialog opens in: Compose's own [Dialog] — a separate window, so focus stays in it,
 * the content behind cannot be reached, and Back and Escape call [onDismissRequest] — drawn edge to edge
 * over the whole screen with the Qeetrix scrim (`component.dialog.scrim`) in place of the platform's dim.
 * [content] is laid out inside the safe-drawing insets and a 16 dp margin (`w-[calc(100%-2rem)]`,
 * `max-h-[calc(100dvh-2rem)]`), so a dialog never touches the edges of a phone and moves up with the
 * keyboard. A tap on the scrim dismisses only when [dismissOnClickOutside]. [title] is the window's title,
 * which accessibility services announce as it opens.
 */
@Composable
internal fun DialogWindow(
    onDismissRequest: () -> Unit,
    title: String,
    dismissOnClickOutside: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                // Taps outside are the scrim's, below: the window fills the screen.
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                windowTitle = title,
            ),
    ) {
        val view = LocalView.current
        // The scrim below replaces the window's dim, which would otherwise darken the screen twice.
        SideEffect { (view.parent as? DialogWindowProvider)?.window?.setDimAmount(0f) }
        val dismiss by rememberUpdatedState(onDismissRequest)
        Box(
            Modifier
                .fillMaxSize()
                .background(QeetrixDialogComponentTokens.Scrim) // bg-(--qx-component-dialog-scrim)
                .then(if (dismissOnClickOutside) Modifier.pointerInput(Unit) { detectTapGestures { dismiss() } } else Modifier)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(QeetrixWebScale.spacing(4f)),
            contentAlignment = Alignment.Center,
            content = content,
        )
    }
}

/**
 * The dialog surface — `rounded-(--qx-component-dialog-corner) border bg-(--qx-component-dialog-background)
 * p-6 gap-4 shadow-(--qx-component-dialog-elevation)` — announcing [title] as a pane. A tap on it never counts
 * as a tap outside. Drawn by the dialogs, and on its own by the screenshot tests, which cannot capture a
 * dialog window.
 */
@Composable
internal fun DialogSurface(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(QeetrixDialogComponentTokens.Corner)
    CompositionLocalProvider(LocalQeetrixContentColor provides QeetrixDialogComponentTokens.Foreground) {
        Column(
            modifier
                .qeetrixElevation(QeetrixDialogComponentTokens.Elevation, shape, QeetrixDialogComponentTokens.Background)
                .border(QeetrixStroke.Hairline, QeetrixDialogComponentTokens.Border, shape)
                .clip(shape)
                .pointerInput(Unit) { detectTapGestures {} }
                .semantics { paneTitle = title }
                .padding(QeetrixWebScale.spacing(6f)), // p-6
            verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(4f)), // gap-4
            content = content,
        )
    }
}

/** `DialogHeader`: the title — a heading — and the description, 6 dp apart, at the reading start. */
@Composable
internal fun DialogHeader(
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1.5f))) {
        // font-heading text-heading font-semibold tracking-(--qx-typography-heading-letter-spacing) text-foreground
        val titleStyle = QeetrixTheme.typography.heading.copy(color = QeetrixTheme.colors.textPrimary)
        BasicText(title, Modifier.semantics { heading() }, style = titleStyle)
        if (description != null) {
            BasicText(description, style = DescriptionStyle.copy(color = QeetrixTheme.colors.textTertiary)) // text-sm text-muted-foreground
        }
    }
}

/**
 * A dialog's action buttons in the footer's row ([ActionRow]), arranged by [arrangeActions]. Choosing one
 * calls [onDismissRequest] and then the action, once; the dialog ignores any further taps while it closes.
 * Opened from a keyboard, focus starts on the first action — Cancel where there is one, the least
 * destructive choice.
 */
@Composable
internal fun DialogActions(
    actions: List<QeetrixDialogAction>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cancel = stringResource(R.string.qx_cancel)
    val arranged = arrangeActions(actions, cancel)
    val chosen = remember { BooleanArray(1) }
    val first = remember { FocusRequester() }
    val keyboard = LocalInputModeManager.current.inputMode == InputMode.Keyboard
    if (keyboard) LaunchedEffect(Unit) { first.requestFocus() }
    ActionRow(modifier) {
        arranged.forEachIndexed { index, action ->
            QeetrixButton(
                action.label,
                onClick = {
                    if (!chosen[0]) {
                        chosen[0] = true
                        onDismissRequest()
                        action.onClick()
                    }
                },
                modifier = if (index == 0) Modifier.focusRequester(first) else Modifier,
                variant = action.role.buttonVariant,
                enabled = action.enabled,
            )
        }
    }
}

/** text-sm. */
private val DescriptionStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)
