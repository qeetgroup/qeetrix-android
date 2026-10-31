package com.qeetgroup.qeetrix.components.drawer

import android.view.accessibility.AccessibilityManager
import androidx.activity.BackEventCompat
import androidx.activity.OnBackPressedCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixShadow
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.internal.DialogSystemBars
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/** The window edge a sheet comes in from and leaves by. */
internal enum class SheetEdge {
    /** The bottom sheet: the window's width up to its size, its content's height up to 85 % of the window's. */
    Bottom,

    /** A side sheet at the reading start: the left in left to right, the right in right to left; full height. */
    Start,

    /** A side sheet at the reading end, as `@qeetrix/react`'s Drawer by default; full height. */
    End,
}

/**
 * Which way is towards this edge on the screen: +1 right or down, -1 left. The start edge is the left in left to right
 * and the right in right to left; the end edge the other way.
 */
internal fun SheetEdge.towardsEdge(rightToLeft: Boolean): Int =
    when (this) {
        SheetEdge.Bottom -> 1
        SheetEdge.Start -> if (rightToLeft) 1 else -1
        SheetEdge.End -> if (rightToLeft) -1 else 1
    }

/**
 * Where a sheet stands: [offset] pixels moved towards its edge from fully open — 0 open, [extent] (its height, or its
 * width for a side sheet) gone — and [rest], where it opens to: 0, or half-way for a partially expandable bottom sheet.
 * The offset starts past the edge, so the sheet's first frame is drawn out of sight.
 */
@Stable
internal class SheetMotion {
    /** Whether the window is composed: from the moment the sheet is asked for until it has left. */
    var present by mutableStateOf(false)

    var extent by mutableIntStateOf(0)
    var offset by mutableFloatStateOf(Float.POSITIVE_INFINITY)
    var rest by mutableFloatStateOf(0f)

    /** How far a predictive back gesture has gone, 0 to 1: the sheet shrinks towards its edge as it does. */
    var back by mutableFloatStateOf(0f)

    /** Whether focus was inside the sheet at the last focus event: only its arrival expands a half-open sheet. */
    var focused = false

    val drag = DraggableState { dragBy(it) }

    /** Moves the sheet by [delta] pixels towards its edge, inside its travel; the pixels it moved. */
    fun dragBy(delta: Float): Float {
        val from = offset.coerceAtMost(extent.toFloat())
        offset = (from + delta).coerceIn(0f, extent.toFloat())
        return offset - from
    }

    /** 1 while the sheet is at or past where it opens to, down to 0 as it leaves: the scrim's opacity. */
    val shown: Float
        get() = if (extent == 0) 0f else ((extent - offset) / (extent - rest)).coerceIn(0f, 1f)

    /** Moves the sheet to [target] with [spec]; a drag takes over from it, and so does the next call. */
    suspend fun animateTo(
        target: Float,
        spec: AnimationSpec<Float>,
        velocity: Float = 0f,
    ) {
        drag.drag {
            var last = offset.coerceAtMost(extent.toFloat())
            animate(last, target, velocity, spec) { value, _ ->
                dragBy(value - last)
                last = value
            }
        }
    }

    fun reset() {
        focused = false
        extent = 0
        offset = Float.POSITIVE_INFINITY
        rest = 0f
        back = 0f
    }
}

/**
 * The window every Qeetrix sheet opens in — Compose's own [Dialog], so focus stays in it, the screen behind cannot be
 * reached and moves under the `color.overlay.scrim` backdrop, drawn edge to edge — with the sheet against [edge]:
 * `color.surface.overlay`, square, under `shadow.modal` (drawer.css), its [content] inside the safe-drawing insets of
 * the edges it touches, so it moves up with the keyboard.
 *
 * - **Motion** (drawer.css): it comes in over `motion.duration.slow` on `motion.easing.enter` and leaves over
 *   `motion.duration.normal` on `motion.easing.exit`; under reduced motion it comes and goes at once. The scrim fades
 *   with it.
 * - **Gestures**: it follows the finger towards its edge and back, and settles where the fling or its position says:
 *   open, half-way ([partiallyExpandable], bottom only) or gone — gone only when [dismissible]. A bottom sheet's
 *   content scrolls before the sheet moves, and a half-open sheet expands before its content scrolls.
 * - **Closing**: a swipe or fling away, a tap on the scrim (both only when [dismissible]), Back and Escape (always:
 *   Android never traps Back) ask through `onOpenChange(false)`; the app's [open] decides. A predictive back gesture
 *   shrinks the sheet towards its edge as it goes, and lets it go back if cancelled.
 * - **Accessibility**: a partially expandable sheet opens fully while touch exploration is on, and expands when focus
 *   moves into it, so nothing is left below the window's edge.
 *
 * [content] receives the close request for its close button: `null` when not [dismissible].
 */
@Composable
internal fun SheetWindow(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    title: String,
    edge: SheetEdge,
    size: Dp,
    dismissible: Boolean,
    partiallyExpandable: Boolean,
    modifier: Modifier,
    content: @Composable ColumnScope.(onClose: (() -> Unit)?) -> Unit,
) {
    val sheet = remember { SheetMotion() }
    if (open) sheet.present = true
    if (!sheet.present) return

    val change by rememberUpdatedState(onOpenChange)
    val isOpen by rememberUpdatedState(open)
    val canDismiss by rememberUpdatedState(dismissible)
    val close = remember { { if (isOpen) change(false) } }
    // drawer.css: in over slow on enter; out ([data-ending-style]) over normal on exit.
    val enter = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingEnter, QeetrixMotion.DurationSlow)
    val exit = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingExit, QeetrixMotion.DurationNormal)
    LaunchedEffect(open, sheet.extent > 0) {
        if (sheet.extent == 0) return@LaunchedEffect
        if (open) {
            sheet.animateTo(sheet.rest, enter)
        } else {
            sheet.animateTo(sheet.extent.toFloat(), exit)
            sheet.reset()
            sheet.present = false
        }
    }

    Dialog(
        onDismissRequest = close,
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
        // The status bar is over the scrim, the navigation bar over the sheet or the scrim: dark icons unless in dark mode.
        DialogSystemBars(darkIcons = !QeetrixTheme.isDark)
        PredictiveBack(sheet, close)

        val scope = rememberCoroutineScope()
        val density = LocalDensity.current
        val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        val towardsEdge = edge.towardsEdge(rtl)
        val threshold = with(density) { FLING_THRESHOLD.toPx() }
        val exploring =
            LocalContext.current.let { context ->
                remember(context) { context.getSystemService(AccessibilityManager::class.java)?.isTouchExplorationEnabled == true }
            }

        /** Settles the sheet after a drag that ended at [velocity] pixels a second towards its edge. */
        suspend fun settle(velocity: Float) {
            val extent = sheet.extent.toFloat()
            if (extent == 0f) return
            val anchors = listOfNotNull(0f, sheet.rest.takeIf { it > 0f }, extent.takeIf { canDismiss })
            val target =
                when {
                    velocity > threshold -> anchors.firstOrNull { it > sheet.offset + 1f } ?: anchors.last()
                    velocity < -threshold -> anchors.lastOrNull { it < sheet.offset - 1f } ?: anchors.first()
                    else -> anchors.minBy { abs(it - sheet.offset) }
                }
            if (target == extent) {
                // Swiped away: ask the app, and go. An app that keeps it open brings it back; one that closes it takes
                // over this movement with its own.
                change(false)
                sheet.animateTo(extent, exit, velocity)
                if (isOpen) sheet.animateTo(sheet.rest, enter)
            } else {
                sheet.animateTo(target, enter, velocity)
            }
        }

        val scrollConnection =
            remember(sheet) {
                object : NestedScrollConnection {
                    // A half-open sheet expands before its content scrolls towards its end.
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset =
                        if (available.y < 0f && source == NestedScrollSource.UserInput) {
                            Offset(0f, sheet.dragBy(available.y))
                        } else {
                            Offset.Zero
                        }

                    // Content already at its top: the rest of the drag moves the sheet.
                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource,
                    ): Offset = if (source == NestedScrollSource.UserInput) Offset(0f, sheet.dragBy(available.y)) else Offset.Zero

                    override suspend fun onPreFling(available: Velocity): Velocity {
                        val moved = sheet.offset > 0.5f && sheet.offset < sheet.extent
                        return if (available.y < 0f && moved) available.also { settle(it.y) } else Velocity.Zero
                    }

                    // Settles only a sheet the drag moved; a fling of its content alone never closes it.
                    override suspend fun onPostFling(
                        consumed: Velocity,
                        available: Velocity,
                    ): Velocity {
                        val atAnchor = sheet.offset.let { o -> o < 0.5f || abs(o - sheet.rest) < 0.5f || o >= sheet.extent }
                        return if (atAnchor) Velocity.Zero else available.also { settle(it.y) }
                    }
                }
            }

        val scrim = QeetrixTheme.colors.overlayScrim
        BoxWithConstraints(
            Modifier.fillMaxSize().onPreviewKeyEvent { event ->
                // Escape asks to close on its way down, as Back does; its key-up is consumed with it.
                if (event.key == Key.Escape) {
                    if (event.type == KeyEventType.KeyDown) close()
                    true
                } else {
                    false
                }
            },
        ) {
            val windowHeight = constraints.maxHeight
            Box(
                Modifier
                    .fillMaxSize()
                    .drawBehind { drawRect(scrim, alpha = sheet.shown) } // .qx-drawer-backdrop
                    .pointerInput(Unit) { detectTapGestures { if (canDismiss) close() } },
            )
            Column(
                Modifier
                    .align(
                        when (edge) {
                            SheetEdge.Bottom -> Alignment.BottomCenter
                            SheetEdge.Start -> Alignment.CenterStart
                            SheetEdge.End -> Alignment.CenterEnd
                        },
                    ).then(
                        // A side sheet as .qx-drawer: inline-size min(size, 100vw - 48px), block-size 100%.
                        when (edge) {
                            SheetEdge.Bottom -> Modifier.widthIn(max = size).fillMaxWidth().heightIn(max = maxHeight * MAX_HEIGHT_FRACTION)
                            else -> Modifier.fillMaxHeight().width(min(size, maxWidth - SIDE_ROOM))
                        },
                    ).layout { measurable, constraints ->
                        // Placed from its own measured size, so even its first frame is out of sight until it is moved in.
                        val placeable = measurable.measure(constraints)
                        val extent = if (edge == SheetEdge.Bottom) placeable.height else placeable.width
                        layout(placeable.width, placeable.height) {
                            val moved = sheet.offset.coerceAtMost(extent.toFloat()).roundToInt() * towardsEdge
                            if (edge == SheetEdge.Bottom) placeable.place(0, moved) else placeable.place(moved, 0)
                        }
                    }.onSizeChanged {
                        val extent = if (edge == SheetEdge.Bottom) it.height else it.width
                        val half = windowHeight / 2
                        val halfOpen = edge == SheetEdge.Bottom && partiallyExpandable && !exploring && extent > half
                        sheet.rest = if (halfOpen) (extent - half).toFloat() else 0f
                        sheet.extent = extent
                    }.graphicsLayer {
                        val scale = 1f - BACK_SHRINK * sheet.back
                        scaleX = scale
                        scaleY = scale
                        transformOrigin =
                            when {
                                edge == SheetEdge.Bottom -> TransformOrigin(0.5f, 1f)
                                towardsEdge > 0 -> TransformOrigin(1f, 0.5f)
                                else -> TransformOrigin(0f, 0.5f)
                            }
                    }.then(modifier)
                    .qeetrixShadow(QeetrixShadow.Modal, RectangleShape, QeetrixTheme.colors.surfaceOverlay) // .qx-drawer
                    .then(if (edge == SheetEdge.Bottom) Modifier.nestedScroll(scrollConnection) else Modifier)
                    .draggable(
                        sheet.drag,
                        if (edge == SheetEdge.Bottom) Orientation.Vertical else Orientation.Horizontal,
                        reverseDirection = towardsEdge < 0,
                        onDragStopped = { velocity -> settle(velocity) },
                    ).onFocusChanged {
                        // Focus arriving in a half-open sheet (a keyboard, Switch Access) opens it fully — on its arrival, and
                        // only while the sheet is open, so one that is leaving keeps leaving.
                        val arrived = it.hasFocus && !sheet.focused
                        sheet.focused = it.hasFocus
                        if (arrived && isOpen && sheet.offset > 0.5f && sheet.offset < sheet.extent) {
                            scope.launch { sheet.animateTo(0f, enter) }
                        }
                    }.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            when (edge) {
                                SheetEdge.Bottom -> WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                                SheetEdge.Start -> WindowInsetsSides.Vertical + WindowInsetsSides.Start
                                SheetEdge.End -> WindowInsetsSides.Vertical + WindowInsetsSides.End
                            },
                        ),
                    ),
            ) { content(if (dismissible) close else null) }
        }
    }
}

/**
 * Back for the sheet's window, from the window's own back dispatcher: a predictive back gesture's progress shrinks the
 * sheet ([SheetMotion.back]); a cancelled one lets it grow back; a completed one, or a Back key, asks to close.
 */
@Composable
private fun PredictiveBack(
    sheet: SheetMotion,
    onBack: () -> Unit,
) {
    val view = LocalView.current
    val back by rememberUpdatedState(onBack)
    val scope = rememberCoroutineScope()
    val release = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal)
    val spec by rememberUpdatedState(release)
    DisposableEffect(view, sheet) {
        val dispatcher = view.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher
        val callback =
            object : OnBackPressedCallback(true) {
                fun letGo() {
                    scope.launch { animate(sheet.back, 0f, animationSpec = spec) { value, _ -> sheet.back = value } }
                }

                override fun handleOnBackProgressed(backEvent: BackEventCompat) {
                    sheet.back = backEvent.progress
                }

                override fun handleOnBackCancelled() = letGo()

                override fun handleOnBackPressed() {
                    letGo()
                    back()
                }
            }
        // Added after the dialog's own Back handling, so it comes first; without a dispatcher, the dialog's still asks.
        dispatcher?.addCallback(callback)
        onDispose { callback.remove() }
    }
}

/** A fling faster than this, 125 dp a second, carries the sheet to the next place in its direction. */
private val FLING_THRESHOLD = 125.dp

/** `.qx-drawer inline-size: min(…, 100vw - 48px)`: a side sheet always leaves 48 dp of the screen beside it. */
private val SIDE_ROOM = 48.dp

/** The bottom sheet's height cap: 85 % of the window, as the dialog's `max-block-size: 85vh`. */
private const val MAX_HEIGHT_FRACTION = 0.85f

/** How much a predictive back gesture shrinks the sheet at its end: 5 %. */
private const val BACK_SHRINK = 0.05f
