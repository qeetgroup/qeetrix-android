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
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.button.QeetrixIconButtonSize
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconVariant
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.ui.platform.AccessibilityManager as ComposeAccessibilityManager

/** What a snackbar reports (`value` is the web Toast's type, the same string on every platform). */
public enum class QeetrixSnackbarType(
    public val value: String,
) {
    /** Neutral news ("Conversation archived"): the info icon, no accent. */
    Info("info"),

    /** Something worked ("Invoice sent"): the filled check, a success accent and title. */
    Success("success"),

    /** Something needs a look ("Sync is paused"): the filled triangle, a warning accent; shown for 10 s. */
    Warning("warning"),

    /** Something failed ("Couldn't send"): the filled cross, a danger accent; announced at once and shown for 10 s. */
    Error("error"),
}

/** How a snackbar ended, as [QeetrixSnackbarHostState.show] returns it. */
public enum class QeetrixSnackbarResult {
    /** The person chose the snackbar's action. */
    ActionPerformed,

    /** It timed out, was closed, was replaced by another, or its screen went away. */
    Dismissed,
}

/**
 * The snackbars of one screen — what a [QeetrixSnackbarHost] shows. Remember one per screen with
 * [rememberQeetrixSnackbarHostState] and call [show] from that screen's coroutine scope; there is no global
 * snackbar service.
 *
 * **One at a time.** A new snackbar replaces the one on screen at once — its [show] returns
 * [QeetrixSnackbarResult.Dismissed] — and nothing queues, so feedback is never late or stale. Showing the
 * snackbar that is already on screen again (the same message, type, description and action) keeps it and
 * restarts its time instead of announcing it twice.
 *
 * **Timing** follows the web Toast: 5 s, or 10 s for warnings, errors and snackbars with an action, then
 * lengthened to the person's "Time to take action" accessibility setting. While TalkBack is running, a
 * snackbar with an action stays until it is used or closed, so the time limit never takes the action away.
 * A `persistent` snackbar stays until it is used or closed.
 *
 * **Lifecycle.** [show] suspends until the snackbar ends and is cancelled with the coroutine that called it:
 * the snackbar leaves with its screen. Each [show] completes exactly once, and an action runs at most once
 * — the app acts on [QeetrixSnackbarResult.ActionPerformed].
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
     * Shows a snackbar with [message] — the title, read first — of [type], with an optional [description]
     * and an optional action button labelled [actionLabel], and suspends until it ends. Never put a
     * password, a code or any other secret in a snackbar: it is shown on screen and announced aloud.
     */
    public suspend fun show(
        message: String,
        type: QeetrixSnackbarType = QeetrixSnackbarType.Info,
        description: String? = null,
        actionLabel: String? = null,
        persistent: Boolean = false,
    ): QeetrixSnackbarResult {
        val onScreen = current
        if (onScreen != null && onScreen.isSameAs(message, type, description, actionLabel, persistent)) {
            onScreen.restart()
            return onScreen.result.await()
        }
        onScreen?.finish(QeetrixSnackbarResult.Dismissed)
        val request = SnackbarRequest(message, type, description, actionLabel, persistent)
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
}

/** A [QeetrixSnackbarHostState] for this screen. */
@Composable
public fun rememberQeetrixSnackbarHostState(): QeetrixSnackbarHostState = remember { QeetrixSnackbarHostState() }

/**
 * Where a screen's snackbars appear — the web's `Toaster`, scoped to the screen: put it at the bottom of the
 * screen's layout, over the content (in a `Box`, aligned to the bottom), and show snackbars through its
 * [state]. On a narrow window a snackbar spans the width less 16 dp margins; from 416 dp it is at most
 * 384 dp wide, at the bottom end, as on the web.
 *
 * [windowInsets] (by default the bottom and horizontal safe-drawing insets) keeps snackbars above the
 * navigation bar and the keyboard; inside a [com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold]
 * those are already consumed by its bar.
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
    val density = LocalDensity.current
    val reduce = QeetrixTheme.reduceMotion
    val enterSpec = qeetrixAnimationSpec<Float>(QeetrixEasing.Enter, QeetrixDuration.Slow)
    val enterOffset = qeetrixAnimationSpec<IntOffset>(QeetrixEasing.Enter, QeetrixDuration.Slow)
    val exitSpec = qeetrixAnimationSpec<Float>(QeetrixEasing.Exit, QeetrixDuration.Normal)
    val exitOffset = qeetrixAnimationSpec<IntOffset>(QeetrixEasing.Exit, QeetrixDuration.Normal)
    val rise = with(density) { QeetrixWebScale.spacing(3f).roundToPx() } // translate-y-3
    val margin = with(density) { QeetrixWebScale.spacing(4f).roundToPx() } // 1rem
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(
        modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets)
            .padding(QeetrixWebScale.spacing(4f)), // mb-4 me-4, w-[calc(100%-2rem)]
        contentAlignment = Alignment.BottomEnd,
    ) {
        AnimatedContent(
            targetState = state.current,
            transitionSpec = {
                if (reduce) {
                    ContentTransform(EnterTransition.None, ExitTransition.None, sizeTransform = null)
                } else {
                    // In: a short rise and fade. Out: toward the inline end, fading (`--toast-dir` mirrors it).
                    (fadeIn(enterSpec) + slideInVertically(enterOffset) { rise })
                        .togetherWith(
                            fadeOut(exitSpec) +
                                slideOutHorizontally(exitOffset) { width ->
                                    if (rtl) -(width + margin) else width + margin
                                },
                        ).using(SizeTransform(clip = false))
                }
            },
            contentAlignment = Alignment.BottomEnd,
            label = "snackbar",
        ) { request ->
            if (request != null) {
                QeetrixSnackbar(
                    message = request.message,
                    modifier = Modifier.widthIn(max = MaxWidth).fillMaxWidth(),
                    type = request.type,
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
 * One snackbar as it is drawn — the web Toast: the overlay surface with its status icon, the [message] as the
 * title, an optional [description] and action button, and a close button when [onDismiss] is given. A
 * [QeetrixSnackbarHost] draws these for its state; draw one directly only for a custom host or a preview.
 *
 * It is announced when it appears — at once for [QeetrixSnackbarType.Error], politely otherwise — and offers
 * accessibility services a dismiss action. Its status is in its icon and words as well as its colour.
 */
@Composable
public fun QeetrixSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    type: QeetrixSnackbarType = QeetrixSnackbarType.Info,
    description: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    val colors = QeetrixTheme.colors
    val shape = RoundedCornerShape(QeetrixCorner.Overlay) // rounded-(--qx-corner-overlay)
    // border-s-success, border-s-warning, border-s-destructive; none for info
    val accent =
        when (type) {
            QeetrixSnackbarType.Info -> Color.Unspecified
            QeetrixSnackbarType.Success -> colors.feedbackSuccess
            QeetrixSnackbarType.Warning -> colors.feedbackWarning
            QeetrixSnackbarType.Error -> colors.feedbackError
        }
    val tone =
        when (type) {
            QeetrixSnackbarType.Info -> colors.textInfo
            QeetrixSnackbarType.Success -> colors.textSuccess
            QeetrixSnackbarType.Warning -> colors.textWarning
            QeetrixSnackbarType.Error -> colors.textDanger
        }
    val title = if (type == QeetrixSnackbarType.Info) colors.textPrimary else tone
    Box(
        modifier
            .qeetrixElevation(QeetrixElevation.Overlay, shape, colors.surfaceOverlay) // shadow-popover bg-popover
            .border(QeetrixStroke.Hairline, colors.borderDefault, shape)
            .clip(shape)
            .drawBehind {
                if (accent == Color.Unspecified) return@drawBehind
                // border-s-[3px]: the status accent on the inline-start edge
                val width = QeetrixWebScale.spacing(0.75f).toPx()
                val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                drawRect(accent, Offset(x, 0f), Size(width, size.height))
            }
            // One announcement of its words; the action and close buttons stay their own controls.
            .semantics(mergeDescendants = true) {
                liveRegion = if (type == QeetrixSnackbarType.Error) LiveRegionMode.Assertive else LiveRegionMode.Polite
                if (onDismiss != null) {
                    dismiss {
                        onDismiss()
                        true
                    }
                }
            },
    ) {
        Row(
            Modifier.padding(
                start = QeetrixWebScale.spacing(3.5f), // ps-3.5
                top = QeetrixWebScale.spacing(3f), // py-3
                bottom = QeetrixWebScale.spacing(3f),
                end = if (onDismiss != null) QeetrixWebScale.spacing(10f) else QeetrixWebScale.spacing(3.5f), // pe-10
            ),
            horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(3f)), // gap-3
        ) {
            QeetrixIcon(
                type.icon,
                contentDescription = null,
                modifier = Modifier.padding(top = QeetrixWebScale.spacing(0.5f)), // mt-0.5
                size = QeetrixWebScale.spacing(4f), // size-4
                variant = if (type == QeetrixSnackbarType.Info) QeetrixIconVariant.Outline else QeetrixIconVariant.Filled,
                tint = tone,
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f))) {
                BasicText(message, style = TitleStyle.copy(color = title))
                if (description != null) BasicText(description, style = DescriptionStyle.copy(color = colors.textTertiary))
                if (actionLabel != null && onAction != null) {
                    Row(Modifier.padding(top = QeetrixWebScale.spacing(2f))) {
                        // h-7 rounded-md border border-border px-2.5 text-xs font-medium
                        QeetrixButton(actionLabel, onClick = onAction, variant = QeetrixButtonVariant.Outline, size = QeetrixButtonSize.Sm)
                    }
                }
            }
        }
        if (onDismiss != null) {
            // absolute inset-e-2 top-2 size-7 rounded-md text-muted-foreground, in its 48 dp touch area
            QeetrixIconButton(
                QeetrixIconName.X,
                stringResource(R.string.qx_close),
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd),
                size = QeetrixIconButtonSize.Sm,
            )
        }
    }
}

/** One snackbar request: what to show, how it ends (once), and a signal that restarts its time. */
internal class SnackbarRequest(
    val message: String,
    val type: QeetrixSnackbarType,
    val description: String?,
    val actionLabel: String?,
    val persistent: Boolean,
) {
    val result = CompletableDeferred<QeetrixSnackbarResult>()
    private val restarts = Channel<Unit>(Channel.CONFLATED)

    fun isSameAs(
        message: String,
        type: QeetrixSnackbarType,
        description: String?,
        actionLabel: String?,
        persistent: Boolean,
    ): Boolean =
        this.message == message &&
            this.type == type &&
            this.description == description &&
            this.actionLabel == actionLabel &&
            this.persistent == persistent

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
        if (request.persistent) return null
        val hasAction = request.actionLabel != null
        // An action must not time out under a screen reader: Android's own snackbars stay, as iOS's toasts do.
        if (hasAction && platform?.isTouchExplorationEnabled == true) return null
        val needsTime = hasAction || request.type == QeetrixSnackbarType.Warning || request.type == QeetrixSnackbarType.Error
        val base = if (needsTime) EXTENDED_MILLIS else DEFAULT_MILLIS
        return compose?.calculateRecommendedTimeoutMillis(base, containsIcons = true, containsText = true, containsControls = hasAction)
            ?: base
    }

    companion object {
        /** No window yet: the web's times. */
        val Unknown = SnackbarTiming(null, null)

        /** The web's default, for a confirmation. */
        const val DEFAULT_MILLIS = 5_000L

        /** TOAST_EXTENDED_TIMEOUT: warnings, errors and anything with an action. */
        const val EXTENDED_MILLIS = 10_000L
    }
}

/** The web's TYPE_ICON. */
private val QeetrixSnackbarType.icon: QeetrixIconName
    get() =
        when (this) {
            QeetrixSnackbarType.Info -> QeetrixIconName.Info
            QeetrixSnackbarType.Success -> QeetrixIconName.CircleCheck
            QeetrixSnackbarType.Warning -> QeetrixIconName.TriangleAlert
            QeetrixSnackbarType.Error -> QeetrixIconName.CircleX
        }

/** The viewport's max-w-sm. */
private val MaxWidth = QeetrixWebScale.spacing(96f)

/** font-heading text-sm font-medium. */
private val TitleStyle = qeetrixTextStyle(QeetrixFont.Heading, QeetrixWebScale.TextSm, FontWeightMedium)

/** text-sm. */
private val DescriptionStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)
