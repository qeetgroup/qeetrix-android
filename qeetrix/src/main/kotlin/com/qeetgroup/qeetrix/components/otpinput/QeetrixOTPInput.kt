package com.qeetgroup.qeetrix.components.otpinput

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.input.FieldBox
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.fieldError
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

/** The characters a one-time code is made of (`value` names them). */
public enum class QeetrixOTPCharacters(
    public val value: String,
) {
    /** 0–9 with a number pad — the web's only form. Digits typed in another script, such as Arabic-Indic, are entered as 0–9. */
    Digits("digits"),

    /** A–Z, a–z and 0–9, for services whose codes include letters; kept as typed. A native addition, as on qeetrix-ios. */
    Alphanumeric("alphanumeric"),
}

/**
 * A one-time-code field — the web's `OTPInput` in a field: the code drawn in [length] cells.
 *
 * One native text field holds the code; the cells only draw it. So the keyboard, paste, the text menu,
 * AutoFill and accessibility services all work on the code as one value, and TalkBack reads one field named
 * by the [label] — "One-time code" (`qx_one_time_code`) without one — rather than a row of boxes.
 *
 * - The code is the text of the [TextFieldState] you own, so leading zeros are kept. Characters that are
 *   not [characters] are dropped, and the code never grows past [length]; a pasted or autofilled code of
 *   full length replaces a partial one. Clear it with `state.clearText()`.
 * - [onComplete] runs each time the code becomes a complete, different code. Nothing is submitted for
 *   you: call your own submit from it if you want that.
 * - [smsCodeAutofill] marks the field for SMS one-time-code AutoFill, which a device's AutoFill provider may
 *   offer — not something Qeetrix can guarantee. Turn it off for codes that never come by SMS, such as an
 *   authenticator app's. Qeetrix never reads messages or asks for permissions; an app that retrieves codes
 *   itself writes them into the state.
 * - [groupSize] draws a divider every so many cells — `3` shows a six-digit code as 123 – 456.
 * - A code reads left to right in every script, so the cells keep that order in right-to-left layouts.
 *
 * Generating, sending, checking and storing codes are your app's. The component never logs or describes the code.
 *
 * ```
 * val code = rememberTextFieldState()
 * QeetrixOTPInput(code, label = "Verification code", description = "Sent to •••• 4821") { completed -> verify(completed) }
 * ```
 */
@Composable
public fun QeetrixOTPInput(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    length: Int = 6,
    characters: QeetrixOTPCharacters = QeetrixOTPCharacters.Digits,
    groupSize: Int? = null,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    optional: Boolean = false,
    smsCodeAutofill: Boolean = true,
    onKeyboardAction: KeyboardActionHandler? = null,
    interactionSource: MutableInteractionSource? = null,
    onComplete: ((String) -> Unit)? = null,
) {
    require(length in 1..MAX_LENGTH) { "length ($length) must be between 1 and $MAX_LENGTH." }
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val transformation = remember(length, characters) { CodeTransformation(length, characters) }
    val name = label ?: stringResource(R.string.qx_one_time_code)

    val latestOnComplete by rememberUpdatedState(onComplete)
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

    FieldFrame(description, message, modifier) {
        BasicTextField(
            state = state,
            modifier =
                Modifier
                    .then(if (smsCodeAutofill) Modifier.semantics { contentType = ContentType.SmsOtpCode } else Modifier)
                    .fieldError(message),
            enabled = active,
            readOnly = readOnly,
            inputTransformation = transformation,
            // The field's own text and cursor are invisible: the cells draw the code and the typing position.
            textStyle = CodeStyle.copy(color = Color.Transparent),
            keyboardOptions =
                if (characters == QeetrixOTPCharacters.Digits) {
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
                LabelledControl(label, required, optional, active) {
                    Box {
                        Cells(state.text, length, groupSize, focused && active, active, readOnly, message, name.takeIf { label == null })
                        // The text field spans the cells, so a tap anywhere focuses it and a long press offers Paste.
                        Box(Modifier.matchParentSize()) { inner() }
                    }
                }
            },
        )
    }
}

/**
 * The cells, always left to right: the code's characters, a caret in the typing position while focused,
 * and a divider every [groupSize] cells. Hidden from accessibility services — the field reads the code —
 * except for the default name when the field has no label of its own.
 */
@Composable
private fun Cells(
    code: CharSequence,
    length: Int,
    groupSize: Int?,
    focused: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    message: QeetrixFieldMessage?,
    defaultName: String?,
) {
    // The typing position: the first empty cell, or the last once the code is complete.
    val typingAt = minOf(code.length, length - 1)
    // h-[calc(var(--qx-component-input-height)+1rem)]
    val cellHeight = QeetrixInputComponentTokens.height(QeetrixTheme.density) + QeetrixWebScale.spacing(4f)
    val textStyle = CodeStyle.copy(color = QeetrixInputComponentTokens.Foreground)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        BoxWithConstraints {
            // flex-1 basis-0 min-w-8 max-w-12: an equal share of the width, between 32 and 48 dp.
            val dividers = if (groupSize != null && groupSize > 0) (length - 1) / groupSize else 0
            val gaps = QeetrixWebScale.spacing(2f) * (length + dividers - 1) // gap-2 between every cell and divider
            val share = (maxWidth - gaps - QeetrixWebScale.spacing(2.5f) * dividers) / length
            val cellWidth = share.coerceIn(QeetrixWebScale.spacing(8f), QeetrixWebScale.spacing(12f))
            Row(
                Modifier.clearAndSetSemantics { if (defaultName != null) contentDescription = defaultName },
                horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)), // gap-2
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (index in 0 until length) {
                    if (groupSize != null && groupSize > 0 && index > 0 && index % groupSize == 0) {
                        // h-px w-2.5 rounded-full bg-border-strong
                        Box(
                            Modifier
                                .size(QeetrixWebScale.spacing(2.5f), QeetrixStroke.Hairline)
                                .background(QeetrixTheme.colors.borderStrong, CircleShape),
                        )
                    }
                    val character = code.getOrNull(index)
                    FieldBox(
                        enabled = enabled,
                        readOnly = readOnly,
                        focused = focused && index == typingAt,
                        hovered = false,
                        tone = message?.tone,
                        modifier = Modifier.width(cellWidth),
                        minHeight = cellHeight,
                        contentPadding = PaddingValues(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        if (character != null) {
                            BasicText(character.toString(), style = textStyle.copy(textAlign = TextAlign.Center))
                        } else if (focused && index == typingAt && !readOnly) {
                            // The caret: as tall as the code's text, so it follows the font scale.
                            val caret = with(LocalDensity.current) { CodeStyle.fontSize.toDp() }
                            Box(Modifier.size(QeetrixStroke.Hairline, caret).background(QeetrixInputComponentTokens.Foreground))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Keeps the code to [characters] and [codeLength]: other characters are dropped (so a pasted "123-456" is
 * 123456), digits from any script become 0–9, and a change that brings a full code — a paste, AutoFill —
 * replaces whatever was there. Anything longer is cut to [codeLength].
 */
private class CodeTransformation(
    private val codeLength: Int,
    private val characters: QeetrixOTPCharacters,
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
                when {
                    digit >= 0 -> append('0' + digit)
                    characters == QeetrixOTPCharacters.Alphanumeric && (char in 'a'..'z' || char in 'A'..'Z') -> append(char)
                }
            }
        }
}

/** font-mono text-lg tabular-nums, centred. */
private val CodeStyle = qeetrixTextStyle(QeetrixFont.Mono, QeetrixWebScale.TextLg).copy(fontFeatureSettings = "tnum")

private const val MAX_LENGTH = 12
