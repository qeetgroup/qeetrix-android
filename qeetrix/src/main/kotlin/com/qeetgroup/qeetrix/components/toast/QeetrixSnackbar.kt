package com.qeetgroup.qeetrix.components.toast

import android.view.accessibility.AccessibilityManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixShadow
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import androidx.compose.ui.platform.AccessibilityManager as ComposeAccessibilityManager

/**
 * What a snackbar reports (`value` is `@qeetrix/react`'s Toast `tone`): the colour of its 3 dp start edge and its
 * status icon, never the only signal — the title says it too.
 */
public enum class QeetrixSnackbarTone(
    public val value: String,
) {
    /** News ("Conversation archived"): `color.icon.info`, the info icon. The default. */
    Info("info"),

    /** Something worked ("Invoice sent"): `color.icon.success`, the circled check. */
    Success("success"),

    /** Something needs a look ("Sync is paused"): `color.icon.warning`, the triangle. */
    Warning("warning"),

    /**
     * Something failed ("Couldn't send"): `color.icon.danger`, the circled alert. It stays until it is used or
     * closed, whatever its timeout, and is announced at once.
     */
    Danger("danger"),
    ;

    /** toast.css `border-inline-start-color` and `.qx-toast-icon` colour per `data-tone`. */
    internal fun color(colors: QeetrixColors): Color =
        when (this) {
            Info -> colors.iconInfo
            Success -> colors.iconSuccess
            Warning -> colors.iconWarning
            Danger -> colors.iconDanger
        }

    /** toast.tsx ICONS. */
    internal val icon: QeetrixIconName
        get() =
            when (this) {
                Info -> QeetrixIconName.Info
                Success -> QeetrixIconName.CircleCheck
                Warning -> QeetrixIconName.TriangleAlert
                Danger -> QeetrixIconName.CircleAlert
            }
}

/** How a snackbar ended, as [QeetrixSnackbarHostState.show] returns it. */
public enum class QeetrixSnackbarResult {
    /** The person chose the snackbar's action. */
    ActionPerformed,

    /** It timed out, was closed, was replaced by another, or its screen went away. */
    Dismissed,
}

/**
 * The snackbars of one screen — what a [QeetrixSnackbarHost] shows, the counterpart of `@qeetrix/react`'s
 * `useToast()`. Remember one per screen with [rememberQeetrixSnackbarHostState] and call [show] from that screen's
 * coroutine scope; there is no global snackbar service.
 *
 * **One at a time.** A new snackbar replaces the one on screen at once — its [show] returns
 * [QeetrixSnackbarResult.Dismissed] — and nothing queues, so feedback is never late or stale. Showing the snackbar
 * that is already on screen again (the same title, tone, description, action and timeout) keeps it and restarts its
 * time instead of announcing it twice.
 *
 * **Timing** follows the web's Toast: [DefaultTimeout] (5 s) unless a snackbar sets its own, lengthened to the
 * person's "Time to take action" accessibility setting; a [QeetrixSnackbarTone.Danger] snackbar stays until it is
 * used or closed (design rules §13). While TalkBack is running, a snackbar with an action also stays, so the time
 * limit never takes the action away.
 *
 * **Lifecycle.** [show] suspends until the snackbar ends and is cancelled with the coroutine that called it: the
 * snackbar leaves with its screen. Each [show] completes exactly once, and an action runs at most once — the app acts
 * on [QeetrixSnackbarResult.ActionPerformed].
 */
@Stable
public class QeetrixSnackbarHostState {
    internal var current: SnackbarRequest? by mutableStateOf(null)
        private set

    /** Set by the host from the accessibility settings of the window it is in. */
    internal var timing: SnackbarTiming = SnackbarTiming.Unknown

    /** Whether a snackbar is showing. */
    public val isShowing: Boolean get() = current != null

    /**
     * Shows a snackbar — [title], what happened in a few words, read first; an optional one-line [description]; its
     * [tone]; at most one action, a button labelled [actionLabel] — and suspends until it ends. It closes after
     * [timeout] (`Duration.INFINITE` keeps it until it is used or closed, as the web's `timeout: 0` does); a `danger`
     * snackbar ignores it. Never put a password, a code or any other secret in a snackbar: it is shown on screen and
     * announced aloud.
     */
    public suspend fun show(
        title: String,
        tone: QeetrixSnackbarTone = QeetrixSnackbarTone.Info,
        description: String? = null,
        actionLabel: String? = null,
        timeout: Duration = DefaultTimeout,
    ): QeetrixSnackbarResult {
        val onScreen = current
        if (onScreen != null && onScreen.isSameAs(title, tone, description, actionLabel, timeout)) {
            onScreen.restart()
            return onScreen.result.await()
        }
        onScreen?.finish(QeetrixSnackbarResult.Dismissed)
        val request = SnackbarRequest(title, tone, description, actionLabel, timeout)
        current = request
        try {
            request.runTimer { timing.timeoutMillis(request) }
            return request.result.await()
        } finally {
            // Ended, replaced or cancelled with its caller: it never stays behind.
            request.finish(QeetrixSnackbarResult.Dismissed)
            if (current === request) current = null
        }
    }

    /** Closes the snackbar on screen, if any; its [show] returns [QeetrixSnackbarResult.Dismissed]. */
    public fun dismiss() {
        current?.finish(QeetrixSnackbarResult.Dismissed)
    }

    /** The defaults of [show]. */
    public companion object {
        /** toast.tsx's `timeout` default: 5000 ms. */
        public val DefaultTimeout: Duration = 5.seconds
    }
}

/** A [QeetrixSnackbarHostState] for this screen. */
@Composable
public fun rememberQeetrixSnackbarHostState(): QeetrixSnackbarHostState = remember { QeetrixSnackbarHostState() }

/**
 * Where a screen's snackbars appear — `@qeetrix/react`'s ToastProvider region, scoped to the screen: put it at the
 * bottom of the screen's layout, over the content (in a `Box`, aligned to the bottom), and show snackbars through its
 * [state]. A snackbar is at most 360 dp wide, `space.inset.lg` from the bottom and end edges (toast.css's viewport); on
 * a phone it spans the width less those margins. It fades in and rises 4 dp in `motion.duration.normal`, and leaves
 * the same way one step faster; nothing moves under reduced motion.
 *
 * [windowInsets] (by default the bottom and horizontal safe-drawing insets) keeps snackbars above the navigation bar
 * and the keyboard; inside a navigation scaffold those are already consumed by its bar.
 *
 * ```
 * val snackbars = rememberQeetrixSnackbarHostState()
 * val scope = rememberCoroutineScope()
 * Box(Modifier.fillMaxSize()) {
 *     InboxList(onArchive = { conversation ->
 *         archive(conversation)
 *         scope.launch {
 *             val result = snackbars.show("Conversation archived", actionLabel = "Undo")
 *             if (result == QeetrixSnackbarResult.ActionPerformed) unarchive(conversation)
 *         }
 *     })
 *     QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
 * }
 * ```
 */
@Composable
public fun QeetrixSnackbarHost(
    state: QeetrixSnackbarHostState,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
) {
    val compose = LocalAccessibilityManager.current
    val context = LocalContext.current
    val platform = remember(context) { context.getSystemService(AccessibilityManager::class.java) }
    SideEffect { state.timing = SnackbarTiming(compose, platform) }
    val reduce = QeetrixTheme.reduceMotion
    // .qx-toast: in over normal on enter; [data-ending-style] out over fast on exit; translate 0 4px either way.
    val enterFade = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingEnter, QeetrixMotion.DurationNormal)
    val enterSlide = qeetrixAnimationSpec<IntOffset>(QeetrixMotion.EasingEnter, QeetrixMotion.DurationNormal)
    val exitFade = qeetrixAnimationSpec<Float>(QeetrixMotion.EasingExit, QeetrixMotion.DurationFast)
    val exitSlide = qeetrixAnimationSpec<IntOffset>(QeetrixMotion.EasingExit, QeetrixMotion.DurationFast)
    val rise = with(LocalDensity.current) { SnackbarRise.roundToPx() }
    Box(
        modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets)
            .padding(QeetrixSpace.InsetLg), // inset-block-end, inset-inline-end: space.inset.lg; 100vw − 32px
        contentAlignment = Alignment.BottomEnd,
    ) {
        AnimatedContent(
            targetState = state.current,
            transitionSpec = {
                if (reduce) {
                    ContentTransform(EnterTransition.None, ExitTransition.None, sizeTransform = null)
                } else {
                    (fadeIn(enterFade) + slideInVertically(enterSlide) { rise })
                        .togetherWith(fadeOut(exitFade) + slideOutVertically(exitSlide) { rise })
                        .using(SizeTransform(clip = false))
                }
            },
            contentAlignment = Alignment.BottomEnd,
            label = "snackbar",
        ) { request ->
            if (request != null) {
                QeetrixSnackbar(
                    title = request.title,
                    modifier = Modifier.widthIn(max = MaxWidth).fillMaxWidth(),
                    tone = request.tone,
                    description = request.description,
                    actionLabel = request.actionLabel,
                    onAction = { request.finish(QeetrixSnackbarResult.ActionPerformed) },
                    onDismiss = { request.finish(QeetrixSnackbarResult.Dismissed) },
                )
            }
        }
    }
}

/**
 * One snackbar as it is drawn — `@qeetrix/react`'s toast: `color.surface.overlay` under `shadow.overlay` with
 * `radius.overlay` corners, the 3 dp status edge on the start side, a sharp 16 dp status icon, the [title] in
 * `title.sm` over an optional [description] in `body.sm` `color.text.subtle`, at most one ghost `sm` action and, when
 * [onDismiss] is given, a neutral close button. A [QeetrixSnackbarHost] draws these for its state; draw one directly
 * only for a custom host or a preview.
 *
 * It is announced when it appears — at once for [QeetrixSnackbarTone.Danger], politely otherwise — as one message, and
 * offers accessibility services a dismiss action; its action and close buttons are their own controls, each with a
 * 48 dp touch area around the drawn button.
 */
@Composable
public fun QeetrixSnackbar(
    title: String,
    modifier: Modifier = Modifier,
    tone: QeetrixSnackbarTone = QeetrixSnackbarTone.Info,
    description: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val shape = QeetrixShapes.Overlay // border-radius: radius.overlay
    val status = tone.color(colors)
    val control = QeetrixTheme.density.sizeControlSm
    // The 48 dp touch area is centred on a drawn sm button, so it spreads this far around it.
    val spread = ((QeetrixAccessibility.MinimumTouchTarget - control) / 2).coerceAtLeast(0.dp)
    val titleStyle = typography.titleSm
    Box(
        modifier
            .qeetrixShadow(QeetrixShadow.Overlay, shape, colors.surfaceOverlay) // background surface.overlay, shadow.overlay
            .drawBehind {
                // border-inline-start: border.width.accent solid <status>, inside the rounded corners
                val width = QeetrixBorder.WidthAccent.toPx()
                val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                val outline = Path().apply { addOutline(shape.createOutline(size, layoutDirection, this@drawBehind)) }
                clipPath(outline) { drawRect(status, Offset(x, 0f), Size(width, size.height)) }
            }
            // One announcement of its words; the action and close buttons stay their own controls.
            .semantics(mergeDescendants = true) {
                liveRegion = if (tone == QeetrixSnackbarTone.Danger) LiveRegionMode.Assertive else LiveRegionMode.Polite
                if (onDismiss != null) {
                    dismiss {
                        onDismiss()
                        true
                    }
                }
            }.padding(start = QeetrixBorder.WidthAccent),
    ) {
        Row(
            // padding: 12px 8px 12px 14px
            Modifier.padding(start = 14.dp, top = 12.dp, end = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd), // gap: space.gap.md
            verticalAlignment = Alignment.Top, // align-items: flex-start
        ) {
            QeetrixIcon(
                tone.icon,
                contentDescription = null,
                // margin-block-start: 4px — the 16 px icon centred on the title's 24 px line, at any font scale
                modifier = Modifier.padding(top = ((firstLineHeight(titleStyle) - QeetrixSize.IconSm) / 2).coerceAtLeast(0.dp)),
                size = QeetrixSize.IconSm,
                shape = QeetrixIconShape.Sharp,
                tint = status,
            )
            // .qx-toast-content: flex 1, gap 2px
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                BasicText(title, style = titleStyle.copy(color = colors.textDefault).labelDirection(alignToLayout = true))
                if (description !=
                    null
                ) {
                    BasicText(
                        description,
                        style = typography.bodySm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true),
                    )
                }
            }
            if (actionLabel != null && onAction != null) {
                // .qx-toast-action: a ghost sm Button, margin-block: -2px
                QeetrixButton(
                    actionLabel,
                    onClick = onAction,
                    modifier = Modifier.drawnSize(horizontal = 0.dp, vertical = spread + ActionMargin),
                    variant = QeetrixButtonVariant.Ghost,
                    size = QeetrixButtonSize.Sm,
                )
            }
            if (onDismiss != null) {
                // BaseToast.Close: a neutral ghost sm IconButton
                QeetrixIconButton(
                    QeetrixIconName.X,
                    stringResource(R.string.qx_dismiss),
                    onClick = onDismiss,
                    modifier = Modifier.drawnSize(horizontal = spread, vertical = spread),
                    tone = QeetrixButtonTone.Neutral,
                    size = QeetrixButtonSize.Sm,
                )
            }
        }
    }
}

/**
 * Lays out a control drawn inside the snackbar at its drawn size: its 48 dp touch area — [horizontal] and [vertical]
 * beyond the drawn button on each side — overhangs its slot instead of widening the row or the snackbar, so the
 * snackbar keeps the web's proportions and the touch area stays whole (nothing here clips).
 */
private fun Modifier.drawnSize(
    horizontal: Dp,
    vertical: Dp,
): Modifier =
    layout { measurable, constraints ->
        val dx = horizontal.roundToPx()
        val dy = vertical.roundToPx()
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        val width = (placeable.width - 2 * dx).coerceAtLeast(0)
        val height = (placeable.height - 2 * dy).coerceAtLeast(0)
        layout(width, height) { placeable.placeRelative(-dx, -dy) }
    }

/** One snackbar request: what to show, how it ends (once), and a signal that restarts its time. */
internal class SnackbarRequest(
    val title: String,
    val tone: QeetrixSnackbarTone,
    val description: String?,
    val actionLabel: String?,
    val timeout: Duration,
) {
    val result = CompletableDeferred<QeetrixSnackbarResult>()
    private val restarts = Channel<Unit>(Channel.CONFLATED)

    fun isSameAs(
        title: String,
        tone: QeetrixSnackbarTone,
        description: String?,
        actionLabel: String?,
        timeout: Duration,
    ): Boolean =
        this.title == title &&
            this.tone == tone &&
            this.description == description &&
            this.actionLabel == actionLabel &&
            this.timeout == timeout

    /** Ends it with [outcome], unless it has already ended. */
    fun finish(outcome: QeetrixSnackbarResult) {
        result.complete(outcome)
    }

    fun restart() {
        restarts.trySend(Unit)
    }

    /** Waits until it ends, ending it as [QeetrixSnackbarResult.Dismissed] when its time runs out; a restart starts the time again. */
    suspend fun runTimer(timeoutMillis: () -> Long?) {
        while (!result.isCompleted) {
            val millis = timeoutMillis()
            val ended: Boolean? =
                if (millis == null) {
                    waitForEndOrRestart()
                } else {
                    withTimeoutOrNull(millis) { waitForEndOrRestart() }
                }
            when (ended) {
                null -> finish(QeetrixSnackbarResult.Dismissed)
                true -> return
                false -> Unit
            }
        }
    }

    /** `true` when it ends, `false` when it is restarted. */
    private suspend fun waitForEndOrRestart(): Boolean =
        select {
            result.onAwait { true }
            restarts.onReceive { false }
        }
}

/** The time a snackbar stays, from the web's rule and the window's accessibility settings. */
internal class SnackbarTiming(
    private val compose: ComposeAccessibilityManager?,
    private val platform: AccessibilityManager?,
) {
    /** `null` keeps the snackbar until it is used or closed. */
    fun timeoutMillis(request: SnackbarRequest): Long? {
        // toast.tsx: danger toasts never time out (timeout 0); neither does a timeout of 0 or forever.
        if (request.tone == QeetrixSnackbarTone.Danger) return null
        if (request.timeout.isInfinite() || request.timeout <= Duration.ZERO) return null
        val hasAction = request.actionLabel != null
        // An action must not time out under a screen reader: Android's own snackbars stay, as iOS's toasts do.
        if (hasAction && platform?.isTouchExplorationEnabled == true) return null
        val base = request.timeout.inWholeMilliseconds
        return compose?.calculateRecommendedTimeoutMillis(base, containsIcons = true, containsText = true, containsControls = hasAction)
            ?: base
    }

    companion object {
        /** No window yet: the web's times. */
        val Unknown = SnackbarTiming(null, null)
    }
}

/** `.qx-toast-viewport inline-size: min(360px, 100vw − 32px)`. */
private val MaxWidth = 360.dp

/** `.qx-toast-action margin-block: -2px`. */
private val ActionMargin = 2.dp

/** `translate: 0 4px` in `[data-starting-style]` and `[data-ending-style]`. */
private val SnackbarRise = 4.dp
