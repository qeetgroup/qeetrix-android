package com.qeetgroup.qeetrix.components.otpinput

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.copy.QeetrixCopyState
import com.qeetgroup.qeetrix.components.copy.rememberCopyAction
import com.qeetgroup.qeetrix.components.progress.QeetrixMeterTone
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressRing
import com.qeetgroup.qeetrix.components.progress.QeetrixProgressRingSize
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant

/** A [QeetrixOTPCode]'s size (`value` is the name shared with qeetrix-ios). */
public enum class QeetrixOTPCodeSize(
    public val value: String,
) {
    /** The code at `title.lg`'s size, 20 sp, with a 20 dp ring: a row in a list of several. */
    Md("md"),

    /** The code at `heading.md`'s size, 28 sp, with a 24 dp ring: one code on its own screen. The default. */
    Lg("lg"),
}

/**
 * A one-time code to read and type elsewhere, with the time it has left — the authenticator's code, beside
 * [QeetrixOTPInput] (which takes a code). `@qeetrix/react` has no counterpart; it is drawn from the OTPInput's slot
 * type and Progress's parts.
 *
 * - **The code** in Fira Code with tabular figures, weight 500, grouped for reading back: 123 456, and 1234 5678 for
 *   eight digits (`space.gap.md` between the groups).
 * - **The time left**: a [QeetrixProgressRing] emptying until [expiresAt] over the [period] (30 or 60 s), and the
 *   seconds beside it (`label.md`, tabular). In its last 5 seconds both take the warning tone. It moves every frame,
 *   and once a second under reduced motion.
 * - **Tap to copy**: the code goes on the clipboard marked sensitive (Android 13 and later hide it in the clipboard
 *   preview), with a light haptic; Android 13 and later confirm it, and a toast says "Copied" below them — [confirm]
 *   `false` drops the toast where the app shows its own confirmation. [clearAfter] (30 seconds, say) clears the
 *   clipboard then if it still holds the code, or once the app is back in front. "Copied" is announced. With [hidden]
 *   the code shows as dots until the first tap, which reveals it.
 * - **[countdownAtEnd]** puts the ring and the seconds at the end of the code's width rather than beside the digits:
 *   give it the width (`Modifier.fillMaxWidth()`), as in an account row's
 *   [com.qeetgroup.qeetrix.components.list.QeetrixListItem] `content`.
 * - **Accessibility**: one button, read as [label], the digits one by one, then the time left ("Acme, 4 8 2 1 0 5, 12
 *   seconds left"); its action is "Copy code" (or "Show code" while hidden).
 *
 * The app owns the code: when [expiresAt] passes, give it the next code and expiry. For a TOTP of 30 s,
 * `expiresAt = Instant.ofEpochSecond((now.epochSecond / 30 + 1) * 30)`.
 *
 * ```
 * QeetrixOTPCode(code, expiresAt, label = "Acme · priya@acme.in")
 * ```
 *
 * @param now A fixed clock, for tests and screenshots: the countdown stops there.
 */
@Composable
public fun QeetrixOTPCode(
    code: String,
    expiresAt: Instant,
    modifier: Modifier = Modifier,
    period: Duration = Duration.ofSeconds(30),
    label: String? = null,
    hidden: Boolean = false,
    size: QeetrixOTPCodeSize = QeetrixOTPCodeSize.Lg,
    now: Instant? = null,
    confirm: Boolean = true,
    clearAfter: Duration? = null,
    countdownAtEnd: Boolean = false,
    onCopied: ((String) -> Unit)? = null,
) {
    require(!period.isNegative && !period.isZero) { "A code's period is a positive duration, such as 30 seconds." }
    val reduce = QeetrixTheme.reduceMotion
    var clock by remember { mutableLongStateOf(System.currentTimeMillis()) }
    if (now == null) {
        LaunchedEffect(expiresAt, reduce) {
            while (true) {
                if (reduce) {
                    // Once a second, on the second.
                    clock = System.currentTimeMillis()
                    delay(1_000 - clock % 1_000)
                } else {
                    withFrameMillis { clock = System.currentTimeMillis() }
                }
            }
        }
    }
    val current = now?.toEpochMilli() ?: clock
    val leftMillis = (expiresAt.toEpochMilli() - current).coerceIn(0, period.toMillis())
    val secondsLeft = ((leftMillis + 999) / 1_000).toInt()
    val share = if (reduce) secondsLeft * 1_000f / period.toMillis() else leftMillis.toFloat() / period.toMillis()
    val urgent = secondsLeft <= URGENT_SECONDS
    var revealed by rememberSaveable(code) { mutableStateOf(false) }
    val showing = !hidden || revealed

    val colors = QeetrixTheme.colors
    val copyName = stringResource(R.string.qx_otp_code_copy)
    val action = rememberCopyAction({ code }, label ?: copyName, sensitive = true, onCopied, confirm, clearAfter)
    val left = pluralStringResource(R.plurals.qx_otp_code_seconds_left, secondsLeft, secondsLeft)
    val spoken = if (showing) code.toCharArray().joinToString(" ") else stringResource(R.string.qx_otp_code_hidden)
    val described = listOfNotNull(label, spoken, left).joinToString(", ")
    val actionLabel = if (showing) copyName else stringResource(R.string.qx_otp_code_reveal)
    val copiedWords = stringResource(R.string.qx_copy_copied)
    val source = remember { MutableInteractionSource() }
    val onTap = {
        if (showing) action.run() else revealed = true
    }
    Row(
        modifier
            .qeetrixFocusFrame(source, QeetrixShapes.Control, FocusFrame.Outside)
            .clickable(interactionSource = source, indication = null, onClick = onTap)
            .heightIn(min = QeetrixAccessibility.MinimumTouchTarget)
            .clearAndSetSemantics {
                role = Role.Button
                contentDescription = described
                onClick(actionLabel) {
                    onTap()
                    true
                }
                if (action.state == QeetrixCopyState.Copied) {
                    stateDescription = copiedWords
                    liveRegion = LiveRegionMode.Polite
                }
            },
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val style = codeStyle(size).copy(color = if (showing) colors.textDefault else colors.textSubtle)
        val groups = groupsOf(code)
        // At the end: the digits take the width the countdown leaves, so it sits at the end, gap.md after them at least.
        Row(if (countdownAtEnd) Modifier.weight(1f) else Modifier, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
            for (group in groups) {
                BasicText(if (showing) group else HIDDEN.repeat(group.length), style = style, maxLines = 1, softWrap = false)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs), verticalAlignment = Alignment.CenterVertically) {
            QeetrixProgressRing(
                share,
                size = if (size == QeetrixOTPCodeSize.Lg) QeetrixProgressRingSize.Lg else QeetrixProgressRingSize.Md,
                tone = if (urgent) QeetrixMeterTone.Warning else QeetrixMeterTone.Brand,
            )
            BasicText(
                secondsLeft.toString(),
                style =
                    QeetrixTheme.typography.labelMd.copy(
                        color = if (urgent) colors.textWarning else colors.textSubtle,
                        fontFeatureSettings = "tnum",
                    ),
            )
        }
    }
}

/** 0.1.0's signature without `confirm` and `clearAfter`, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun QeetrixOTPCode(
    code: String,
    expiresAt: Instant,
    modifier: Modifier = Modifier,
    period: Duration = Duration.ofSeconds(30),
    label: String? = null,
    hidden: Boolean = false,
    size: QeetrixOTPCodeSize = QeetrixOTPCodeSize.Lg,
    now: Instant? = null,
    onCopied: ((String) -> Unit)? = null,
): Unit =
    QeetrixOTPCode(
        code = code,
        expiresAt = expiresAt,
        modifier = modifier,
        period = period,
        label = label,
        hidden = hidden,
        size = size,
        now = now,
        confirm = true,
        clearAfter = null,
        onCopied = onCopied,
    )

/** How a code is split for reading back: 123 456; 1234 5678; any other even length of 6 or more in halves. */
internal fun groupsOf(code: String): List<String> =
    if (code.length >= 6 && code.length % 2 == 0) listOf(code.take(code.length / 2), code.drop(code.length / 2)) else listOf(code)

/** Fira Code, weight 500, tabular, at `title.lg`'s size (`Md`) or `heading.md`'s (`Lg`), on that role's line. */
@Composable
@ReadOnlyComposable
private fun codeStyle(size: QeetrixOTPCodeSize): TextStyle {
    val role = if (size == QeetrixOTPCodeSize.Lg) QeetrixTextRole.HeadingMd else QeetrixTextRole.TitleLg
    val mono = QeetrixTextRole.CodeMd.spec
    return QeetrixTypography
        .style(mono.copy(size = role.spec.size, lineHeight = role.spec.lineHeight, weight = MEDIUM), LocalDensity.current)
        .copy(fontFeatureSettings = "tnum")
}

/** `label.md`'s weight. */
private const val MEDIUM = 500

/** The last seconds, in the warning tone. */
private const val URGENT_SECONDS = 5

/** A hidden character. */
private const val HIDDEN = "•"
