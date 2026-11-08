package com.qeetgroup.qeetrix.components.otpinput

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.FieldColors
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.fieldError
import com.qeetgroup.qeetrix.components.input.fieldSurface
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixFontGeometry
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The slot sizes of [QeetrixOTPInput] (`value` is `@qeetrix/react`'s `size`). */
public enum class QeetrixOTPInputSize(
    public val value: String,
) {
    /** A slot is exactly a field's height (`size.control.lg`), the character in `title.md`. For consoles. The default. */
    Md("md"),

    /** 16 dp taller, the character in `title.lg`, for sign-in screens. */
    Lg("lg"),
}

/**
 * How [QeetrixOTPInput]'s slots take the width. Native only: the web's slots always share the container's width, and a
 * web page narrows the input with a layout class, which on a phone would narrow the label and help text with it.
 */
public enum class QeetrixOTPSlotWidth(
    public val value: String,
) {
    /** The slots share the container's width (`flex: 1 1 0`), as on the web. The default. */
    Fill("fill"),

    /**
     * Each slot is as wide as it is tall, the slots at the start: a short code, such as a 2-digit number match. The
     * label, description and error keep the container's width. In a column too narrow for square slots they shrink as
     * [Fill]'s do.
     */
    Square("square"),
}

/** The characters a code is made of (`value` is Base UI's OTP Field `validationType`). */
public enum class QeetrixOTPValidationType(
    public val value: String,
) {
    /** 0–9, with a number pad. The default. Digits typed in another script, such as Devanagari, are entered as 0–9. */
    Numeric("numeric"),

    /** A–Z and a–z, kept as typed. */
    Alpha("alpha"),

    /** A–Z, a–z and 0–9 — recovery codes. Kept as typed. */
    Alphanumeric("alphanumeric"),
}

/**
 * A one-time code in separate slots — `@qeetrix/react`'s OTPInput in a Field.
 *
 * - **Look.** One Input box per character — filled, a bottom line, 4 dp top corners — sharing the container's
 *   width `space.gap.sm` apart, so the code's edges line up with the fields, alerts and buttons around it; in a
 *   narrow column the slots shrink with it (a 10-character recovery code keeps 24 dp slots from 400 dp). [size] [QeetrixOTPInputSize.Md] makes a slot exactly a
 *   field's height; [QeetrixOTPInputSize.Lg] adds 16 dp for sign-in screens. The character is in `title.md`
 *   (`title.lg` at `lg`) with tabular figures. [groups] splits the slots — `listOf(3, 3)` shows 123 – 456 — with a
 *   short `color.border.strong` rule between groups. [slotWidth] [QeetrixOTPSlotWidth.Square] keeps a short code's slots
 *   square at the start, the label and help text still at the container's width. The typing slot shows the Ember frame and caret; an invalid
 *   code ([error] or [invalid]) frames the other slots in `color.border.danger`, the typing slot keeping its focus
 *   frame. A [readOnly] code has no fill and keeps the bottom line, as a read-only field. After a failed attempt,
 *   clear the code.
 * - **One field, drawn as slots.** A single native text field holds the code, so the keyboard, paste, the text
 *   menu, AutoFill and accessibility services all work on it as one value: typing moves on, Backspace moves back,
 *   one paste fills every slot, and TalkBack reads one field named by the [label] rather than a row of boxes.
 * - **The code** is the text of the [TextFieldState] you own, so leading zeros are kept. Characters that are not
 *   [validationType] are dropped and the code never grows past [length]; a pasted or autofilled code of full length
 *   replaces a partial one. [onValueComplete] runs each time the code becomes a complete, different code; nothing is
 *   submitted for you.
 * - [smsCodeAutofill] marks the field for SMS one-time-code AutoFill (the web's `autocomplete="one-time-code"`),
 *   which a device's AutoFill provider may offer. Turn it off for codes that never come by SMS. Qeetrix never reads
 *   messages or asks for permissions.
 * - A code reads left to right in every script, so the slots keep that order in right-to-left layouts.
 *
 * ```
 * val code = rememberTextFieldState()
 * QeetrixOTPInput(code, label = "Verification code", description = "Sent to •••• 4821",
 *     size = QeetrixOTPInputSize.Lg, groups = listOf(3, 3), onValueComplete = { verify(it) })
 * ```
 */
@Composable
public fun QeetrixOTPInput(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    length: Int = 6,
    groups: List<Int>? = null,
    size: QeetrixOTPInputSize = QeetrixOTPInputSize.Md,
    slotWidth: QeetrixOTPSlotWidth = QeetrixOTPSlotWidth.Fill,
    validationType: QeetrixOTPValidationType = QeetrixOTPValidationType.Numeric,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    smsCodeAutofill: Boolean = true,
    onKeyboardAction: KeyboardActionHandler? = null,
    interactionSource: MutableInteractionSource? = null,
    onValueComplete: ((String) -> Unit)? = null,
) {
    require(length in 1..MAX_LENGTH) { "length ($length) must be between 1 and $MAX_LENGTH." }
    require(
        groups == null || (
            groups.all {
                it > 0
            } && groups.sum() == length
        ),
    ) { "groups ($groups) must be positive and add up to length ($length)." }
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val transformation = remember(length, validationType) { CodeTransformation(length, validationType) }

    val latestOnComplete by rememberUpdatedState(onValueComplete)
    LaunchedEffect(state, length) {
        // A code already complete when shown was not just entered, so it does not count.
        var completed: String? = state.text.toString().takeIf { it.length == length }
        snapshotFlow { state.text.toString() }.collect { code ->
            if (code.length != length) {
                completed = null
            } else if (code != completed) {
                completed = code
                latestOnComplete?.invoke(code)
            }
        }
    }

    FieldFrame(description, error, active, modifier) {
        BasicTextField(
            state = state,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(if (smsCodeAutofill) Modifier.semantics { contentType = ContentType.SmsOtpCode } else Modifier)
                    .fieldError(error),
            enabled = active,
            readOnly = readOnly,
            inputTransformation = transformation,
            // The field's own text and cursor are invisible: the slots draw the code and the typing position.
            textStyle = slotStyle(size).copy(color = Color.Transparent),
            keyboardOptions =
                if (validationType == QeetrixOTPValidationType.Numeric) {
                    // A number pad. Not NumberPassword: Compose would mark the field a password for AutoFill, and
                    // password managers would offer passwords in it.
                    KeyboardOptions(keyboardType = KeyboardType.Number, autoCorrectEnabled = false, imeAction = ImeAction.Done)
                } else {
                    KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Done,
                    )
                },
            onKeyboardAction = onKeyboardAction,
            lineLimits = TextFieldLineLimits.SingleLine,
            interactionSource = source,
            cursorBrush = SolidColor(Color.Transparent),
            decorator = { inner ->
                LabelledControl(label, optional, required, hideLabel, active, help) {
                    Box(Modifier.fillMaxWidth()) {
                        Slots(state.text, length, groups, size, slotWidth, focused && active, active, readOnly, invalid || error != null)
                        // The text field spans the slots, so a tap anywhere focuses it and a long press offers Paste.
                        Box(Modifier.matchParentSize()) { inner() }
                    }
                }
            },
        )
    }
}

/** 0.1.0's signature without [QeetrixOTPSlotWidth], kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun QeetrixOTPInput(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    length: Int = 6,
    groups: List<Int>? = null,
    size: QeetrixOTPInputSize = QeetrixOTPInputSize.Md,
    validationType: QeetrixOTPValidationType = QeetrixOTPValidationType.Numeric,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    smsCodeAutofill: Boolean = true,
    onKeyboardAction: KeyboardActionHandler? = null,
    interactionSource: MutableInteractionSource? = null,
    onValueComplete: ((String) -> Unit)? = null,
): Unit =
    QeetrixOTPInput(
        state = state,
        label = label,
        modifier = modifier,
        length = length,
        groups = groups,
        size = size,
        slotWidth = QeetrixOTPSlotWidth.Fill,
        validationType = validationType,
        description = description,
        error = error,
        invalid = invalid,
        optional = optional,
        required = required,
        hideLabel = hideLabel,
        help = help,
        enabled = enabled,
        readOnly = readOnly,
        smsCodeAutofill = smsCodeAutofill,
        onKeyboardAction = onKeyboardAction,
        interactionSource = interactionSource,
        onValueComplete = onValueComplete,
    )

/**
 * The slots, always left to right (otp-input.css): the code's characters, the caret in the typing slot while
 * focused, and a `space.gap.sm` × 2 dp `color.border.strong` rule between groups. Hidden from accessibility
 * services — the field reads the code.
 */
@Composable
private fun Slots(
    code: CharSequence,
    length: Int,
    groups: List<Int>?,
    size: QeetrixOTPInputSize,
    slotWidth: QeetrixOTPSlotWidth,
    focused: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    invalid: Boolean,
) {
    // The typing position: the first empty slot, or the last once the code is complete.
    val typingAt = minOf(code.length, length - 1)
    val breaks =
        groups
            .orEmpty()
            .runningReduce(Int::plus)
            .filter { it < length }
            .toSet()
    val colors = QeetrixTheme.colors
    // block-size: size.control.lg; at lg, + 16px
    val height = QeetrixTheme.density.sizeControlLg + if (size == QeetrixOTPInputSize.Lg) LG_EXTRA else 0.dp
    val style = slotStyle(size).copy(color = if (enabled) colors.textDefault else colors.textDisabled, textAlign = TextAlign.Center)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            // Square slots wrap, so the field places them at its start: the right in right to left, the code still
            // reading left to right inside.
            (if (slotWidth == QeetrixOTPSlotWidth.Fill) Modifier.fillMaxWidth() else Modifier).clearAndSetSemantics {},
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), // .qx-otp-input { gap: space.gap.sm }
            verticalAlignment = Alignment.CenterVertically,
        ) {
            for (index in 0 until length) {
                if (index in breaks) {
                    // .qx-otp-input-separator { inline-size: space.gap.sm; block-size: border.width.strong }
                    Box(Modifier.size(QeetrixSpace.GapSm, QeetrixBorder.WidthStrong).background(colors.borderStrong))
                }
                val typing = focused && index == typingAt
                val slot = otpSlotColors(colors, enabled, readOnly, invalid, typing)
                Box(
                    when (slotWidth) {
                        // flex: 1 1 0 — an equal share of the width. The web stops shrinking at size.target.min
                        // (min-inline-size) and overflows a column too narrow for it; here the slots keep shrinking.
                        QeetrixOTPSlotWidth.Fill -> Modifier.weight(1f)

                        // As wide as tall, or the equal share when that is narrower; spacedBy packs them at the start.
                        QeetrixOTPSlotWidth.Square -> Modifier.weight(1f, fill = false).widthIn(max = height).fillMaxWidth()
                    }.heightIn(min = height)
                        .fieldSurface(slot.fill, slot.line, slot.frame, QeetrixShapes.Field),
                    contentAlignment = Alignment.Center,
                ) {
                    val character = code.getOrNull(index)
                    if (character != null) {
                        BasicText(character.toString(), style = style, maxLines = 1)
                    } else if (typing && !readOnly) {
                        // The caret (caret-color: border.focus), as tall as the character, so it follows the font scale.
                        val caret = with(LocalDensity.current) { style.fontSize.toDp() }
                        Box(Modifier.size(QeetrixBorder.WidthDefault, caret).background(colors.borderFocus))
                    }
                }
            }
        }
    }
}

/**
 * One slot's look (otp-input.css, in its cascade order): `bg.neutral.subtle` with a `border.strong` line; invalid, a
 * 2 dp `border.danger` frame instead of the line; the typing slot, the 2 dp `border.focus` frame on the resting fill,
 * invalid or not, so focus is never hidden (`:focus` after `[data-invalid]`); read-only, no fill and the
 * `border.strong` line, as a read-only field (`[data-readonly]`); disabled, `bg.disabled` with a
 * `border.disabled` line.
 */
internal fun otpSlotColors(
    colors: QeetrixColors,
    enabled: Boolean,
    readOnly: Boolean,
    invalid: Boolean,
    typing: Boolean,
): FieldColors {
    val frame =
        when {
            typing -> colors.borderFocus
            invalid -> colors.borderDanger
            else -> null
        }
    val line =
        when {
            !enabled -> colors.borderDisabled
            readOnly || frame == null -> colors.borderStrong
            else -> null
        }
    val fill =
        when {
            !enabled -> colors.bgDisabled
            readOnly -> Color.Transparent
            else -> colors.bgNeutralSubtle
        }
    return FieldColors(fill, line, frame, if (enabled) colors.textDefault else colors.textDisabled)
}

/**
 * `.qx-otp-input-slot { font: typography.title.md; font-variation-settings: "GEOM" font.geometry.body;
 * font-variant-numeric: tabular-nums }` (`title.lg` at `lg`): the title size with the body's A11y letterforms.
 */
@Composable
@ReadOnlyComposable
private fun slotStyle(size: QeetrixOTPInputSize): TextStyle {
    val role = if (size == QeetrixOTPInputSize.Lg) QeetrixTextRole.TitleLg else QeetrixTextRole.TitleMd
    return QeetrixTypography
        .style(role.spec.copy(geometry = QeetrixFontGeometry.Body), LocalDensity.current)
        .copy(fontFeatureSettings = "tnum")
}

/**
 * Keeps the code to [validationType] and [codeLength]: other characters are dropped (so a pasted "123-456" is
 * 123456), digits from any script become 0–9, and a change that brings a full code — a paste, AutoFill — replaces
 * whatever was there. Anything longer is cut to [codeLength].
 */
private class CodeTransformation(
    private val codeLength: Int,
    private val validationType: QeetrixOTPValidationType,
) : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        val current = asCharSequence().toString()
        var code = filter(current)
        if (code.length > codeLength) {
            // What this change brought: the text between what it kept at the start and at the end.
            val original = originalText.toString()
            val kept = original.commonPrefixWith(current).length
            val keptAtEnd = minOf(original.length, current.length - kept).let { original.takeLast(it).commonSuffixWith(current).length }
            val arrived = filter(current.substring(kept, current.length - keptAtEnd))
            code = (if (arrived.length >= codeLength) arrived else code).take(codeLength)
        }
        if (code != current) {
            // Inside this extension, `length` is the buffer's; the code's limit is codeLength.
            replace(0, length, code)
            placeCursorAtEnd()
        }
    }

    private fun filter(text: String): String =
        buildString {
            for (char in text) {
                val digit = Character.digit(char, 10)
                val letter = char in 'a'..'z' || char in 'A'..'Z'
                when {
                    digit >= 0 && validationType != QeetrixOTPValidationType.Alpha -> append('0' + digit)
                    letter && validationType != QeetrixOTPValidationType.Numeric -> append(char)
                }
            }
        }
}

/** `[data-size="lg"] > .qx-otp-input-slot { block-size: calc(size.control.lg + 16px) }`. */
private val LG_EXTRA = 16.dp

private const val MAX_LENGTH = 12
