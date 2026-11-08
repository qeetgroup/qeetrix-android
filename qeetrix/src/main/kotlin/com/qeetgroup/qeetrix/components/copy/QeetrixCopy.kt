package com.qeetgroup.qeetrix.components.copy

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.ButtonFrame
import com.qeetgroup.qeetrix.components.button.IconZone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.ZoneContent
import com.qeetgroup.qeetrix.components.button.buttonMetrics
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.haptics.QeetrixHapticRole
import com.qeetgroup.qeetrix.foundation.haptics.rememberQeetrixHaptics
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay
import java.lang.ref.WeakReference
import java.time.Duration

/** Where a copy stands (`value` is `@qeetrix/react`'s CopyButtonState). */
public enum class QeetrixCopyState(
    public val value: String,
) {
    /** Ready to copy. */
    Idle("idle"),

    /** Just copied: the icon is a check for 2 s. */
    Copied("copied"),

    /** The clipboard refused it: the icon is a danger mark for 2 s. */
    Failed("failed"),
}

/**
 * Puts text on the clipboard the way every Qeetrix copy does: as plain text named [label], marked sensitive when
 * [sensitive] (`ClipDescription.EXTRA_IS_SENSITIVE`, so Android 13 and later hide it in the clipboard preview and keep
 * it out of suggestions), with a [QeetrixHapticRole.LightImpact] haptic. Android 13 and later confirm a copy
 * themselves; below them a short toast says "Copied", unless [confirm] is false because the app confirms it. Returns
 * whether the clipboard took it.
 */
internal fun copyToClipboard(
    context: Context,
    text: String,
    label: String,
    sensitive: Boolean,
    copied: String,
    confirm: Boolean = true,
    sdk: Int = Build.VERSION.SDK_INT,
): Boolean {
    val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return false
    val clip = ClipData.newPlainText(label, text)
    if (sensitive) {
        clip.description.extras =
            PersistableBundle().apply {
                // ClipDescription.EXTRA_IS_SENSITIVE from Android 13; the same key is read below it by OEM previews.
                putBoolean(if (Build.VERSION.SDK_INT >= 33) ClipDescription.EXTRA_IS_SENSITIVE else SENSITIVE_KEY, true)
            }
    }
    val taken = runCatching { clipboard.setPrimaryClip(clip) }.isSuccess
    if (taken && confirm && sdk < 33) Toast.makeText(context, copied, Toast.LENGTH_SHORT).show()
    return taken
}

/**
 * A copy's timed clear: when the delay has passed, the clipboard is cleared if it still holds the copied
 * text, so a newer copy — the app's or another app's — is left alone. Android lets only the app with focus read the
 * clipboard, so a deadline that passes while the app is in the background waits until one of its windows next has
 * focus. A newer timed copy replaces the pending one; the clear lasts as long as the app's process.
 */
internal object ClipboardClear {
    private val main by lazy { Handler(Looper.getMainLooper()) }
    private var pending: Pending? = null

    fun schedule(
        context: Context,
        text: String,
        after: Duration,
    ) {
        cancel()
        val clipboard = context.getSystemService(ClipboardManager::class.java) ?: return
        val next = Pending(clipboard, text)
        pending = next
        (context.applicationContext as? Application)?.registerActivityLifecycleCallbacks(next)
        main.postDelayed(next, after.toMillis().coerceAtLeast(0))
    }

    fun cancel() {
        pending?.let {
            main.removeCallbacks(it)
            it.stop()
        }
    }

    /**
     * One timed clear. It keeps no context: it is told of the app's activities from the copy on, acts on them only once
     * its deadline has passed unread, and unregisters itself through the next one once it is done.
     */
    private class Pending(
        private val clipboard: ClipboardManager,
        private val text: String,
    ) : Runnable,
        Application.ActivityLifecycleCallbacks {
        private var done = false
        private var waiting = false
        private var watched: WeakReference<View>? = null
        private val focus = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus -> if (hasFocus) clear(focused = true) }

        /** The deadline. Nothing to read means either an empty clipboard or no focus: wait for focus to tell. */
        override fun run() {
            if (!clear(focused = false)) waiting = true
        }

        /** Clears the clipboard if it holds [text]; false while it can't be read without focus. */
        fun clear(focused: Boolean): Boolean {
            if (done) return true
            val clip = clipboard.primaryClip
            if (clip == null && !focused) return false
            if (clip != null && clip.itemCount > 0 && clip.getItemAt(0).text?.toString() == text) clipboard.clearPrimaryClip()
            stop()
            return true
        }

        fun stop() {
            done = true
            if (pending === this) pending = null
            unwatch()
        }

        // A window's focus listeners move to its attached observer, so the observer is looked up again to remove one.
        private fun unwatch() {
            watched
                ?.get()
                ?.viewTreeObserver
                ?.takeIf { it.isAlive }
                ?.removeOnWindowFocusChangeListener(focus)
            watched = null
        }

        /** Done: stop hearing about activities. */
        private fun retireThrough(activity: Activity): Boolean {
            if (done) activity.application.unregisterActivityLifecycleCallbacks(this)
            return done
        }

        override fun onActivityResumed(activity: Activity) {
            if (retireThrough(activity) || !waiting) return
            val decor = activity.window?.decorView ?: return
            if (decor.hasWindowFocus()) {
                clear(focused = true)
            } else if (watched?.get() !== decor) {
                unwatch()
                decor.viewTreeObserver.addOnWindowFocusChangeListener(focus)
                watched = WeakReference(decor)
            }
        }

        override fun onActivityPaused(activity: Activity) {
            retireThrough(activity)
        }

        override fun onActivityCreated(
            activity: Activity,
            savedInstanceState: Bundle?,
        ) = Unit

        override fun onActivityStarted(activity: Activity) = Unit

        override fun onActivityStopped(activity: Activity) = Unit

        override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle,
        ) = Unit

        override fun onActivityDestroyed(activity: Activity) = Unit
    }
}

private const val SENSITIVE_KEY = "android.content.extra.IS_SENSITIVE"

/** copy-button.tsx `resetAfter`: how long "Copied" or the failure shows, 2 s. */
internal const val COPY_RESET_MILLIS = 2_000L

/**
 * The copy state a [QeetrixCopyButton] shows: [copy] puts [value] on the clipboard, plays the haptic, and sets
 * [QeetrixCopyState.Copied] (or [QeetrixCopyState.Failed]) for 2 s.
 */
internal class CopyAction(
    private val copy: () -> Boolean,
) {
    var state by mutableStateOf(QeetrixCopyState.Idle)
        private set

    fun run() {
        state = if (copy()) QeetrixCopyState.Copied else QeetrixCopyState.Failed
    }

    fun reset() {
        state = QeetrixCopyState.Idle
    }
}

@Composable
internal fun rememberCopyAction(
    value: () -> String,
    label: String,
    sensitive: Boolean,
    onCopied: ((String) -> Unit)?,
    confirm: Boolean = true,
    clearAfter: Duration? = null,
): CopyAction {
    val context = LocalContext.current
    val haptics = rememberQeetrixHaptics()
    val copied = stringResource(R.string.qx_copy_copied)
    val action =
        remember(context, label, sensitive, confirm, clearAfter) {
            CopyAction {
                val text = value()
                val taken = copyToClipboard(context, text, label, sensitive, copied, confirm)
                haptics.perform(if (taken) QeetrixHapticRole.LightImpact else QeetrixHapticRole.Error)
                if (taken) {
                    clearAfter?.let { ClipboardClear.schedule(context, text, it) }
                    onCopied?.invoke(text)
                }
                taken
            }
        }
    LaunchedEffect(action.state) {
        if (action.state != QeetrixCopyState.Idle) {
            delay(COPY_RESET_MILLIS)
            action.reset()
        }
    }
    return action
}

/**
 * Copies text to the clipboard — `@qeetrix/react`'s CopyButton: a ghost, neutral icon button with the `copy` icon,
 * named [label] ("Copy user ID"; "Copy" by default). After a copy the icon is a `check` in `color.icon.success` for
 * 2 s and "Copied" is announced politely; where the clipboard refuses it, a `circle-x` in `color.icon.danger` and
 * "Couldn't copy" (copy-button.css). Focus stays on the button. A light haptic confirms the copy; Android 13 and later
 * show their own clipboard confirmation, and a toast says "Copied" below them.
 *
 * - [value] is read at the moment of the copy, so it can be a fresh token.
 * - [sensitive] marks the clipboard entry sensitive (a secret, a one-time code): Android 13 and later hide it in the
 *   clipboard preview.
 * - [confirm] `false` drops the "Copied" toast below Android 13, for an app that confirms the copy itself — a snackbar
 *   from [onCopied]. Android 13 and later always show the system's own confirmation.
 * - [clearAfter] clears the clipboard after that long if it still holds the copy: 30 seconds for a one-time code.
 *   While the app is in the background Android doesn't let it read the clipboard, so the clear then waits until the
 *   app is back in front.
 * - [text] makes it a [com.qeetgroup.qeetrix.components.button.QeetrixButton] with a visible label and the icon in its
 *   trailing zone, named by the label.
 *
 * ```
 * QeetrixCopyButton({ client.id }, label = "Copy client ID")
 * ```
 */
@Composable
public fun QeetrixCopyButton(
    value: () -> String,
    modifier: Modifier = Modifier,
    label: String? = null,
    text: String? = null,
    sensitive: Boolean = false,
    variant: QeetrixButtonVariant = QeetrixButtonVariant.Ghost,
    tone: QeetrixButtonTone = QeetrixButtonTone.Neutral,
    size: QeetrixButtonSize = QeetrixButtonSize.Md,
    enabled: Boolean = true,
    confirm: Boolean = true,
    clearAfter: Duration? = null,
    onCopied: ((String) -> Unit)? = null,
) {
    val name = label ?: stringResource(R.string.qx_copy)
    val action = rememberCopyAction(value, name, sensitive, onCopied, confirm, clearAfter)
    CopyButtonFrame(action, name, text, modifier, variant, tone, size, enabled)
}

/** 0.1.0's signature without `confirm` and `clearAfter`, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun QeetrixCopyButton(
    value: () -> String,
    modifier: Modifier = Modifier,
    label: String? = null,
    text: String? = null,
    sensitive: Boolean = false,
    variant: QeetrixButtonVariant = QeetrixButtonVariant.Ghost,
    tone: QeetrixButtonTone = QeetrixButtonTone.Neutral,
    size: QeetrixButtonSize = QeetrixButtonSize.Md,
    enabled: Boolean = true,
    onCopied: ((String) -> Unit)? = null,
): Unit =
    QeetrixCopyButton(
        value = value,
        modifier = modifier,
        label = label,
        text = text,
        sensitive = sensitive,
        variant = variant,
        tone = tone,
        size = size,
        enabled = enabled,
        confirm = true,
        clearAfter = null,
        onCopied = onCopied,
    )

@Composable
internal fun CopyButtonFrame(
    action: CopyAction,
    name: String,
    text: String?,
    modifier: Modifier,
    variant: QeetrixButtonVariant,
    tone: QeetrixButtonTone,
    size: QeetrixButtonSize,
    enabled: Boolean,
) {
    val colors = QeetrixTheme.colors
    val metrics = buttonMetrics(size)
    val state = action.state
    val announced =
        when (state) {
            QeetrixCopyState.Idle -> null
            QeetrixCopyState.Copied -> stringResource(R.string.qx_copy_copied)
            QeetrixCopyState.Failed -> stringResource(R.string.qx_copy_failed)
        }
    val icon =
        when (state) {
            QeetrixCopyState.Idle -> QeetrixIconName.Copy
            QeetrixCopyState.Copied -> QeetrixIconName.Check
            QeetrixCopyState.Failed -> QeetrixIconName.CircleX
        }
    ButtonFrame(
        onClick = action::run,
        modifier = modifier,
        variant = variant,
        tone = tone,
        metrics = metrics,
        square = text == null,
        enabled = enabled,
        loading = false,
        interactionSource = null,
        horizontalArrangement = if (text == null) Arrangement.Center else Arrangement.SpaceBetween,
        startPadding = if (text == null) 0.dp else metrics.labelPadding,
        semantics = {
            if (text == null) contentDescription = name
            if (announced != null) {
                stateDescription = announced
                liveRegion = LiveRegionMode.Polite
            }
        },
    ) { color ->
        // .qx-copy-button[data-variant="ghost"][data-state="copied"|"failed"] .qx-button-icon: icon.success / icon.danger
        val iconColor =
            when {
                variant != QeetrixButtonVariant.Ghost -> color
                state == QeetrixCopyState.Copied -> colors.iconSuccess
                state == QeetrixCopyState.Failed -> colors.iconDanger
                else -> color
            }
        if (text == null) {
            ZoneContent(icon, loading = false, metrics = metrics, color = iconColor)
        } else {
            BasicText(
                text,
                Modifier.padding(end = metrics.gap),
                style = metrics.textStyle.copy(color = color).labelDirection(),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
            IconZone(metrics, color) { ZoneContent(icon, loading = false, metrics = metrics, color = iconColor) }
        }
    }
}

/** How a [QeetrixCopyField] is laid out (`value` is `@qeetrix/react`'s CopyFieldLayout). */
public enum class QeetrixCopyFieldLayout(
    public val value: String,
) {
    /** A read-only field: the label above the value and its Copy button. The default. */
    Field("field"),

    /** A fact row on a record page: the label, the value, Copy at the end, lining up with a description list's rows. */
    Row("row"),
}

/**
 * A read-only value with its Copy button — `@qeetrix/react`'s CopyField: an ID, a URL to paste elsewhere, a secret
 * shown once. The value is in `code.sm` and wraps anywhere rather than scrolls ([wrap] `false` keeps it on one line,
 * ending in an ellipsis); a long press selects it. The Copy button ([QeetrixCopyButton]) is named after the [label]
 * ("Copy ACS URL").
 *
 * - [QeetrixCopyFieldLayout.Field] (copy-field.css): the label (`label.sm`, `color.text.subtle`) `space.gap.xs` above a
 *   box at least `size.control.lg` tall, with no fill and a 1 dp `color.border.strong` bottom line — a read-only field's —
 *   `space.inset.md` before the value and `space.inset.xs` after the button; the [description] (`body.sm`,
 *   `color.text.subtle`) under it.
 * - [QeetrixCopyFieldLayout.Row]: the label in the first 30 % (at least 128 dp), the value and Copy after it,
 *   `space.gap.sm` above and below, no line; consecutive rows are ruled in `color.border.subtle` by the caller's list.
 * - [sensitive] marks the clipboard entry sensitive, for a secret. [confirm] and [clearAfter] are
 *   [QeetrixCopyButton]'s: no toast where the app confirms the copy, and a timed clear.
 *
 * ```
 * QeetrixCopyField("ACS URL", acsUrl, description = "Paste it into your identity provider.")
 * ```
 */
@Composable
public fun QeetrixCopyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    layout: QeetrixCopyFieldLayout = QeetrixCopyFieldLayout.Field,
    wrap: Boolean = true,
    sensitive: Boolean = false,
    confirm: Boolean = true,
    clearAfter: Duration? = null,
    onCopied: ((String) -> Unit)? = null,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val name = stringResource(R.string.qx_copy_label, label)
    val action = rememberCopyAction({ value }, name, sensitive, onCopied, confirm, clearAfter)
    val labelText: @Composable (Modifier) -> Unit = { m ->
        // .qx-copy-field-label: label.sm, color.text.subtle
        BasicText(label, m, style = typography.labelSm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
    }
    val box: @Composable (Modifier) -> Unit = { m ->
        val field = layout == QeetrixCopyFieldLayout.Field
        val line = colors.borderStrong
        Row(
            m
                .then(if (field) Modifier.heightIn(min = QeetrixTheme.density.sizeControlLg) else Modifier)
                .drawBehind {
                    if (!field) return@drawBehind
                    // box-shadow: inset 0 -1px border.strong
                    val width = QeetrixBorder.WidthDefault.toPx()
                    drawRect(line, Offset(0f, size.height - width), Size(size.width, width))
                }.padding(start = if (field) QeetrixSpace.InsetMd else 0.dp, end = if (field) QeetrixSpace.InsetXs else 0.dp),
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            androidx.compose.foundation.text.selection.SelectionContainer(Modifier.weight(1f)) {
                // .qx-copy-field-value: code.sm, color.text.default, overflow-wrap anywhere, padding-block inset.xs
                BasicText(
                    value,
                    Modifier.padding(vertical = QeetrixSpace.InsetXs),
                    style = typography.codeSm.copy(color = colors.textDefault).labelDirection(alignToLayout = true),
                    maxLines = if (wrap) Int.MAX_VALUE else 1,
                    softWrap = wrap,
                    overflow = if (wrap) TextOverflow.Clip else TextOverflow.Ellipsis,
                )
            }
            CopyButtonFrame(
                action,
                name,
                text = null,
                modifier = Modifier,
                variant = QeetrixButtonVariant.Ghost,
                tone = QeetrixButtonTone.Neutral,
                size = QeetrixButtonSize.Md,
                enabled = true,
            )
        }
    }
    val describe: @Composable (Modifier) -> Unit = { m ->
        if (description != null) {
            // .qx-copy-field-description: body.sm, color.text.subtle
            BasicText(description, m, style = typography.bodySm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
        }
    }
    if (layout == QeetrixCopyFieldLayout.Field) {
        Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
            labelText(Modifier)
            box(Modifier.fillMaxWidth())
            describe(Modifier)
        }
    } else {
        // [data-layout="row"]: grid-template-columns minmax(8rem, 30%) minmax(0, 1fr); column-gap space.gap.md
        androidx.compose.foundation.layout.BoxWithConstraints(modifier.fillMaxWidth().padding(vertical = QeetrixSpace.GapSm)) {
            val labelWidth = maxOf(ROW_LABEL_MIN, maxWidth * ROW_LABEL_SHARE)
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
                // align-items: baseline — the label is centred on the value's first 48 dp, beside the Copy button's target.
                androidx.compose.foundation.layout.Box(
                    Modifier.width(labelWidth).heightIn(min = QeetrixAccessibility.MinimumTouchTarget),
                    contentAlignment = Alignment.CenterStart,
                ) { labelText(Modifier) }
                Column(Modifier.weight(1f)) {
                    box(Modifier.fillMaxWidth())
                    describe(Modifier)
                }
            }
        }
    }
}

/** copy-field.css `minmax(8rem, 30%)`: the row's label column. */
private val ROW_LABEL_MIN = 128.dp
private const val ROW_LABEL_SHARE = 0.3f

/** 0.1.0's signature without `confirm` and `clearAfter`, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun QeetrixCopyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    layout: QeetrixCopyFieldLayout = QeetrixCopyFieldLayout.Field,
    wrap: Boolean = true,
    sensitive: Boolean = false,
    onCopied: ((String) -> Unit)? = null,
): Unit =
    QeetrixCopyField(
        label = label,
        value = value,
        modifier = modifier,
        description = description,
        layout = layout,
        wrap = wrap,
        sensitive = sensitive,
        confirm = true,
        clearAfter = null,
        onCopied = onCopied,
    )
