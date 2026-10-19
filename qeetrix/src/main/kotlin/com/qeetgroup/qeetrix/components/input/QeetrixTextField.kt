package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
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
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

/**
 * A single-line text input with its label, description and message: qeetrix-ui's `Input` inside a
 * vertical `Field`, built on the same field parts as every Qeetrix input ([QeetrixField]).
 *
 * - The text lives in a [TextFieldState] the caller owns (`rememberTextFieldState()`), so the field is
 *   controlled through one state model; read it with `state.text`.
 * - [label] names the field for accessibility services; tapping it focuses the field. [required] adds
 *   the web's red asterisk, which accessibility services read as "required" (`qx_required`); [optional]
 *   the "(optional)" marker (`qx_optional`).
 * - [message] with [QeetrixFieldMessageTone.Error] marks the field invalid: the boundary turns red,
 *   accessibility services report the error, and the message is announced as it appears. Warning and
 *   success colour the boundary and are announced politely. Colour is never the only signal: each tone
 *   has its icon and its text.
 * - [readOnly] shows the value with a dashed boundary and no writing surface; [enabled] `false` fades
 *   the field and it cannot be focused. Inside a disabled [QeetrixFieldSet] it is disabled too.
 * - [contentType] tells AutoFill and password managers what the field holds (`ContentType.Username`,
 *   `ContentType.PersonFullName`, …). Without one, Compose derives it from the keyboard type: an email
 *   keyboard is `ContentType.EmailAddress`. Pass it here rather than on [modifier], which is the whole
 *   field's, not the text's.
 * - The field's height follows the density ([com.qeetgroup.qeetrix.theme.LocalQeetrixDensity]) and
 *   grows with the font scale. Text is 16 sp below 768 dp of window width and 14 sp from there, as the
 *   web's `text-base md:text-sm`.
 *
 * ```
 * val email = rememberTextFieldState()
 * QeetrixTextField(email, label = "Work email", placeholder = "you@company.com",
 *     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
 * ```
 */
@Composable
public fun QeetrixTextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    optional: Boolean = false,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
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
    val textStyle = fieldTextStyle()

    FieldFrame(description, message, modifier) {
        BasicTextField(
            state = state,
            modifier = Modifier.fillMaxWidth().fieldContentType(contentType).fieldError(message),
            enabled = active,
            readOnly = readOnly,
            inputTransformation = inputTransformation,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            lineLimits = TextFieldLineLimits.SingleLine,
            interactionSource = source,
            cursorBrush = SolidColor(QeetrixInputComponentTokens.Foreground),
            decorator = { inner ->
                LabelledControl(label, required, optional, active) {
                    FieldBox(active, readOnly, focused, hovered, message?.tone, Modifier.fillMaxWidth()) {
                        leading?.invoke()
                        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (placeholder != null && state.text.isEmpty()) FieldPlaceholder(placeholder, textStyle)
                            inner()
                        }
                        trailing?.invoke()
                    }
                }
            },
        )
    }
}

/**
 * The label above a control, inside the control's own layout — a text field's decorator, a select's
 * trigger — so accessibility services read the label as part of the control, and tapping it reaches the
 * control.
 */
@Composable
internal fun LabelledControl(
    label: String?,
    required: Boolean,
    optional: Boolean,
    enabled: Boolean,
    control: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(FieldPartGap)) {
        if (label != null) FieldLabel(label, required, optional, enabled)
        control()
    }
}

/** placeholder:text-(--qx-component-input-placeholder), one line. */
@Composable
internal fun FieldPlaceholder(
    text: String,
    textStyle: TextStyle,
    maxLines: Int = 1,
) {
    BasicText(text, style = textStyle.copy(color = QeetrixInputComponentTokens.Placeholder), maxLines = maxLines)
}

/**
 * The control's own AutoFill type. Applied outside the text field's own semantics, so it replaces the type
 * Compose derives from the keyboard; `null` leaves that one.
 */
internal fun Modifier.fieldContentType(contentType: ContentType?): Modifier =
    if (contentType != null) semantics { this.contentType = contentType } else this

/** An error message marks the control invalid for accessibility services (`aria-invalid` with `aria-errormessage`). */
internal fun Modifier.fieldError(message: QeetrixFieldMessage?): Modifier =
    if (message?.tone == QeetrixFieldMessageTone.Error) semantics { error(message.text) } else this
