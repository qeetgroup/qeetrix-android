package com.qeetgroup.qeetrix.components.dialog

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixShadow
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.DialogScrimOnly
import com.qeetgroup.qeetrix.internal.DialogSystemBars
import com.qeetgroup.qeetrix.internal.QeetrixActionRow
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * The window every Qeetrix dialog opens in: Compose's own [Dialog] — a separate window, so focus stays in it,
 * the screen behind cannot be reached, and Back calls [onDismissRequest], as Escape does — drawn edge to edge with
 * the `color.overlay.scrim` backdrop in place of the platform's dim. [content] is centred inside the
 * safe-drawing insets and the viewport's `space.inset.xl` padding (none when [fullscreen]), so a dialog never
 * touches a phone's edges and moves up with the keyboard. A tap on the scrim dismisses only with
 * [dismissOnScrimTap].
 *
 * The window is composed while [open] and while it leaves: dialog.css's motion, the scrim and popup fading in
 * over `motion.duration.slow` on `motion.easing.enter` with the popup rising 8 dp, and out one step faster
 * (`motion.duration.normal`, `motion.easing.exit`). Under reduced motion it comes and goes at once. Nothing it
 * holds answers while it leaves.
 */
@Composable
internal fun DialogWindow(
    open: Boolean,
    title: String,
    onDismissRequest: () -> Unit,
    dismissOnScrimTap: Boolean,
    fullscreen: Boolean,
    content: @Composable () -> Unit,
) {
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = open
    if (!visibility.currentState && !visibility.targetState) return

    val dismiss by rememberUpdatedState(onDismissRequest)
    // .qx-dialog / .qx-dialog-backdrop: in over slow on enter, out ([data-ending-style]) over normal on exit.
    val enter = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingEnter, QeetrixMotion.DurationSlow)
    val exit = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingExit, QeetrixMotion.DurationNormal)
    Dialog(
        onDismissRequest = { if (visibility.targetState) dismiss() },
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
        // The scrim below replaces the window's dim, which would otherwise darken the screen twice.
        DialogScrimOnly()
        // The bars are over the overlay surface (full screen) or the scrim on the page: dark icons unless in dark mode.
        DialogSystemBars(darkIcons = !QeetrixTheme.isDark)
        val transition = rememberTransition(visibility, label = "qeetrix-dialog")
        val shown by transition.animateFloat(
            transitionSpec = { if (targetState) enter else exit },
            label = "qeetrix-dialog-shown",
        ) { if (it) 1f else 0f }
        val scrim = QeetrixTheme.colors.overlayScrim
        val rise = with(LocalDensity.current) { DialogRise.toPx() }
        Box(
            Modifier
                .fillMaxSize()
                .onPreviewKeyEvent { event ->
                    // Escape asks to close on its way down, as Back does; its key-up is consumed with it.
                    if (event.key == Key.Escape) {
                        if (event.type == KeyEventType.KeyDown && visibility.targetState) dismiss()
                        true
                    } else {
                        false
                    }
                }.drawBehind { drawRect(scrim, alpha = shown) } // .qx-dialog-backdrop background: color.overlay.scrim
                .then(
                    if (dismissOnScrimTap) {
                        Modifier.pointerInput(Unit) { detectTapGestures { if (visibility.targetState) dismiss() } }
                    } else {
                        Modifier
                    },
                ).then(
                    // .qx-dialog-viewport padding: space.inset.xl; 0 for fullscreen, which keeps the insets itself.
                    if (fullscreen) Modifier else Modifier.windowInsetsPadding(WindowInsets.safeDrawing).padding(QeetrixSpace.InsetXl),
                ),
            contentAlignment = Alignment.Center, // place-items: center
        ) {
            Box(
                Modifier.graphicsLayer {
                    alpha = shown
                    translationY = (1f - shown) * rise // translate: 0 8px in [data-starting-style] and [data-ending-style]
                },
            ) { content() }
        }
    }
}

/**
 * The dialog popup (`.qx-dialog`): `color.surface.overlay` under `shadow.modal`, `radius.modal` corners (square
 * for fullscreen), the 3 dp intent line along its top edge, the header, the scrolling [content], the footer, and
 * the close button at the top end when [onClose] is given. It announces [title] as its pane, and a tap on it never
 * counts as a tap outside. Drawn by the dialogs in their window, and on its own by the screenshot tests, which
 * cannot capture a dialog window.
 *
 * With [focusFirst] (opened from a keyboard) focus starts on its first focusable element — the first field, or
 * the footer's first, least destructive action — as dialog.tsx's DOM order gives it on the web: the close button
 * comes after everything else.
 */
@Composable
internal fun DialogSurface(
    title: String,
    description: String?,
    intent: QeetrixDialogIntent,
    size: QeetrixDialogSize,
    onClose: (() -> Unit)?,
    modifier: Modifier = Modifier,
    focusFirst: Boolean = false,
    footerStart: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    content: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val colors = QeetrixTheme.colors
    val fullscreen = size == QeetrixDialogSize.Fullscreen
    val shape = if (fullscreen) QeetrixShapes.None else QeetrixShapes.Modal // radius.modal; radius.none in fullscreen
    val line = intent.lineColor(colors)
    // max-block-size: 85vh; inline-size: min(100%, <size>).
    val maxHeight = LocalWindowInfo.current.containerDpSize.height * MAX_HEIGHT_FRACTION
    val frame =
        if (fullscreen) {
            // block-size and inline-size 100%, no shadow to show; the surface runs under the system bars and its
            // content, from the intent line down, stays inside them.
            Modifier.fillMaxSize().background(colors.surfaceOverlay).windowInsetsPadding(WindowInsets.safeDrawing)
        } else {
            Modifier
                .widthIn(max = size.width ?: Dp.Infinity)
                .fillMaxWidth()
                .heightIn(max = maxHeight)
                .qeetrixShadow(QeetrixShadow.Modal, shape, colors.surfaceOverlay)
        }
    val first = remember { FocusRequester() }
    if (focusFirst) LaunchedEffect(Unit) { first.requestFocus() }
    CompositionLocalProvider(LocalQeetrixContentColor provides colors.textDefault) {
        Box(
            modifier
                .then(frame)
                .drawWithContent {
                    drawContent()
                    drawIntentLine(line, shape)
                }.pointerInput(Unit) { detectTapGestures {} }
                .semantics { paneTitle = title }
                .padding(top = QeetrixBorder.WidthAccent), // border-block-start: border.width.accent
        ) {
            Column((if (fullscreen) Modifier.fillMaxSize() else Modifier).focusRequester(first)) {
                OverlayHeader(
                    title,
                    description,
                    // .qx-dialog-header padding: space.inset.xl; [data-dismissible] padding-inline-end: 56px
                    Modifier.padding(
                        start = QeetrixSpace.InsetXl,
                        top = QeetrixSpace.InsetXl,
                        end = if (onClose != null) CloseRoom else QeetrixSpace.InsetXl,
                        bottom = QeetrixSpace.InsetXl,
                    ),
                )
                if (content != null) {
                    // .qx-dialog-body: flex 1, min-block-size 0, overflow-y auto, padding 0 space.inset.xl space.inset.xl
                    Column(
                        Modifier
                            .weight(1f, fill = fullscreen)
                            .verticalScroll(rememberScrollState())
                            .padding(start = QeetrixSpace.InsetXl, end = QeetrixSpace.InsetXl, bottom = QeetrixSpace.InsetXl),
                        content = content,
                    )
                }
                if (footer != null || footerStart != null) {
                    // .qx-dialog-footer padding: 0 space.inset.xl space.inset.xl
                    OverlayFooter(
                        footerStart,
                        Modifier.padding(start = QeetrixSpace.InsetXl, end = QeetrixSpace.InsetXl, bottom = QeetrixSpace.InsetXl),
                    ) { footer?.invoke() }
                }
            }
            // .qx-dialog-close: inset-block-start 12px, inset-inline-end 12px; last, so it is focused last.
            if (onClose != null) OverlayClose(onClose, Modifier.align(Alignment.TopEnd), top = 12.dp, end = 12.dp)
        }
    }
}

/** The 3 dp intent line: the popup's top border, inside its corners (`border-block-start` on a rounded box). */
private fun ContentDrawScope.drawIntentLine(
    color: Color,
    shape: Shape,
) {
    val outline = Path().apply { addOutline(shape.createOutline(size, layoutDirection, this@drawIntentLine)) }
    clipPath(outline) { drawRect(color, size = Size(size.width, QeetrixBorder.WidthAccent.toPx())) }
}

/**
 * The header of a dialog or sheet (`.qx-dialog-header`, `.qx-drawer-header`): the title in `title.md` (heading
 * geometry), a heading for accessibility services, over the description in `body.md` `color.text.subtle`,
 * `space.gap.xs` apart.
 */
@Composable
internal fun OverlayHeader(
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
        BasicText(
            title,
            Modifier.semantics {
                heading()
            },
            style = typography.titleMd.copy(color = colors.textDefault).labelDirection(alignToLayout = true),
        )
        if (description !=
            null
        ) {
            BasicText(description, style = typography.bodyMd.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
        }
    }
}

/**
 * The footer of a dialog or sheet: its [actions] end-aligned in a [QeetrixActionRow] (`justify-content:
 * flex-end`, `space.gap.sm`, wrapping), and an optional [start] — `.qx-dialog-footer-start`: `body.sm`
 * `color.text.subtle`, `margin-inline-end: auto` — at the start of the same line, or on a line of its own above
 * the actions when they do not fit beside it.
 */
@Composable
internal fun OverlayFooter(
    start: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit,
) {
    if (start == null) {
        QeetrixActionRow(modifier.fillMaxWidth(), actions)
        return
    }
    val subtle = QeetrixTheme.colors.textSubtle
    Layout(
        contents =
            listOf(
                { CompositionLocalProvider(LocalQeetrixContentColor provides subtle) { start() } },
                { QeetrixActionRow(content = actions) },
            ),
        modifier = modifier.fillMaxWidth(),
    ) { (startMeasurables, actionMeasurables), constraints ->
        val gap = QeetrixSpace.GapSm.roundToPx()
        val width = constraints.maxWidth
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val startPlaceable = startMeasurables.firstOrNull()?.measure(loose)
        val actionRow = actionMeasurables.first()
        val natural = actionRow.maxIntrinsicWidth(Constraints.Infinity)
        val startWidth = startPlaceable?.width ?: 0
        val oneLine = startPlaceable == null || startWidth + gap + natural <= width
        if (oneLine) {
            val actionsPlaceable = actionRow.measure(loose.copy(maxWidth = natural.coerceAtMost(width)))
            val height = maxOf(startPlaceable?.height ?: 0, actionsPlaceable.height)
            layout(width, height) {
                startPlaceable?.placeRelative(0, (height - startPlaceable.height) / 2)
                actionsPlaceable.placeRelative(width - actionsPlaceable.width, (height - actionsPlaceable.height) / 2)
            }
        } else {
            val actionsPlaceable = actionRow.measure(loose)
            val height = (startPlaceable?.height ?: 0) + gap + actionsPlaceable.height
            layout(width, height) {
                startPlaceable?.placeRelative(0, 0)
                actionsPlaceable.placeRelative(width - actionsPlaceable.width, height - actionsPlaceable.height)
            }
        }
    }
}

/**
 * The close button of a dialog or sheet (`.qx-dialog-close`, `.qx-drawer-close`): a neutral ghost `sm` icon
 * button with the x, named [label] ("Close", `qx_close`, by default). Aligned to its parent's top end, the drawn
 * button sits [top] and [end] from those edges, as on the web; its 48 dp touch area spreads around it, and may
 * reach past them.
 */
@Composable
internal fun OverlayClose(
    onClose: () -> Unit,
    modifier: Modifier,
    top: Dp,
    end: Dp,
    label: String = stringResource(R.string.qx_close),
) {
    // The touch area is centred on the drawn button, so it starts half the difference further out. Offsets follow the
    // layout direction: a negative x moves it in from the end edge, on either side.
    val spread = ((QeetrixAccessibility.MinimumTouchTarget - QeetrixTheme.density.sizeControlSm) / 2).coerceAtLeast(0.dp)
    QeetrixIconButton(
        QeetrixIconName.X,
        label,
        onClick = onClose,
        modifier = modifier.offset(x = spread - end, y = top - spread),
        tone = QeetrixButtonTone.Neutral,
        size = QeetrixButtonSize.Sm,
    )
}

/** An alert dialog's action buttons, each drawn for its role; [onChoose] closes the dialog and runs it. */
@Composable
internal fun DialogActions(
    actions: List<QeetrixDialogAction>,
    onChoose: (QeetrixDialogAction) -> Unit,
) {
    for (action in actions) {
        QeetrixButton(
            action.label,
            onClick = { onChoose(action) },
            variant = action.role.buttonVariant,
            tone = action.role.buttonTone,
            enabled = action.enabled,
        )
    }
}

/** Whether the window is used from a keyboard (or a D-pad or switch), when a dialog moves focus into itself. */
@Composable
internal fun keyboardInput(): Boolean = LocalInputModeManager.current.inputMode == InputMode.Keyboard

/** `translate: 0 8px`: how far a dialog rises as it opens. */
private val DialogRise = 8.dp

/** `.qx-dialog[data-dismissible] .qx-dialog-header { padding-inline-end: 56px }`: room for the close button. */
private val CloseRoom = 56.dp

/** `max-block-size: 85vh`. */
private const val MAX_HEIGHT_FRACTION = 0.85f
