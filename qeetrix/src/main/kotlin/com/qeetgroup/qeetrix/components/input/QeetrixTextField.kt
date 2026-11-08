package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A single-line text field with its label, help text and error — `@qeetrix/react`'s TextField (Field + Input in
 * one), the default way to build a form row. It is the filled field of design rules §8: `color.bg.neutral.subtle`,
 * a 1 dp `color.border.strong` bottom line, 4 dp top corners, `size.control.lg` tall (32 / 40 / 44 dp with the
 * density, growing with the font scale), the value in `body.md`.
 *
 * - **Text.** The text lives in a [TextFieldState] the caller owns (`rememberTextFieldState()`) — Compose's
 *   controlled text model; read it with `state.text`.
 * - **Label.** [label] is required — a placeholder is never a label — and names the field for accessibility
 *   services; tapping it focuses the field. [optional] appends "(optional)"; fields carry no asterisk, and a
 *   [required] field says "required" to accessibility services only. [hideLabel] is for a toolbar search with a
 *   visible icon. [help] adds an ⓘ button after the label (07-components.md, Field help).
 * - **Placeholder.** An example ("e.g. Acme Pvt Ltd"), never an instruction or the label; `color.text.subtlest`.
 * - **Errors.** [error] replaces the [description] and marks the field invalid: a 2 dp `color.border.danger` frame
 *   inside the box and the message with an alert icon, announced as it appears. Word it as what to do. [invalid]
 *   marks it invalid without a message. Focus draws the 2 dp Ember frame instead, on any focus, since the caret
 *   needs context.
 * - **States.** [readOnly] drops the fill and keeps a `color.border.subtle` line, and the value can still be
 *   selected and copied; [enabled] `false` takes `color.bg.disabled`, the `color.border.disabled` line and
 *   `color.text.disabled`. Inside a disabled [QeetrixFieldSet] it is disabled too.
 * - **Adornments.** [icon] is a 16 dp sharp leading icon in `color.icon.subtle` (search, mail);
 *   [startAdornment] and [endAdornment] hold text affixes (₹, %, .qeet.in), which take `color.text.subtle`, or
 *   tools, 8 dp from the value.
 * - **[mono]** draws the value and the placeholder in `code.md` (Fira Code), as `QeetrixText(mono = true)` does — for a
 *   setup key, a pairing code or an ID, where 0 and O, 1, I and l must differ. The box keeps its height.
 * - [contentType] tells AutoFill and password managers what the field holds (`ContentType.Username`, …); without
 *   one, Compose derives it from the keyboard type.
 *
 * ```
 * val email = rememberTextFieldState()
 * QeetrixTextField(email, label = "Work email", placeholder = "e.g. asha@acme.in", icon = QeetrixIconName.Mail,
 *     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
 * ```
 */
@Composable
public fun QeetrixTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    mono: Boolean = false,
    icon: QeetrixIconName? = null,
    startAdornment: (@Composable () -> Unit)? = null,
    endAdornment: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    contentType: ContentType? = null,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    val base = if (mono) QeetrixTheme.typography.codeMd else fieldTextStyle()
    val textStyle = base.copy(color = if (active) QeetrixTheme.colors.textDefault else QeetrixTheme.colors.textDisabled)

    FieldFrame(description, error, active, modifier) {
        BasicTextField(
            state = state,
            modifier = Modifier.fillMaxWidth().fieldContentType(contentType).fieldError(error),
            enabled = active,
            readOnly = readOnly,
            inputTransformation = inputTransformation,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            lineLimits = TextFieldLineLimits.SingleLine,
            interactionSource = source,
            cursorBrush = SolidColor(textStyle.color),
            decorator = { inner ->
                LabelledControl(label, optional, required, hideLabel, active, help) {
                    FieldBox(active, readOnly, focused, hovered, invalid || error != null, Modifier.fillMaxWidth()) {
                        FieldContent(icon, startAdornment, endAdornment, placeholder.takeIf { state.text.isEmpty() }, textStyle, inner)
                    }
                }
            },
        )
    }
}

/**
 * What a single-line box holds (input.tsx): the leading icon, the start adornment, the value with its placeholder
 * (taking the remaining width), and the end adornment.
 */
@Composable
internal fun RowScope.FieldContent(
    icon: QeetrixIconName?,
    startAdornment: (@Composable () -> Unit)?,
    endAdornment: (@Composable () -> Unit)?,
    placeholder: String?,
    textStyle: TextStyle,
    inner: @Composable () -> Unit,
) {
    if (icon != null) FieldIcon(icon)
    if (startAdornment != null) FieldAdornment(startAdornment)
    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
        if (placeholder != null) FieldPlaceholder(placeholder, textStyle)
        inner()
    }
    if (endAdornment != null) FieldAdornment(endAdornment)
}

/** 0.1.0's signature without `mono`, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun QeetrixTextField(
    state: TextFieldState,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    icon: QeetrixIconName? = null,
    startAdornment: (@Composable () -> Unit)? = null,
    endAdornment: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    contentType: ContentType? = null,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
): Unit =
    QeetrixTextField(
        state = state,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        invalid = invalid,
        optional = optional,
        required = required,
        hideLabel = hideLabel,
        help = help,
        enabled = enabled,
        readOnly = readOnly,
        mono = false,
        icon = icon,
        startAdornment = startAdornment,
        endAdornment = endAdornment,
        keyboardOptions = keyboardOptions,
        contentType = contentType,
        onKeyboardAction = onKeyboardAction,
        inputTransformation = inputTransformation,
        interactionSource = interactionSource,
    )
