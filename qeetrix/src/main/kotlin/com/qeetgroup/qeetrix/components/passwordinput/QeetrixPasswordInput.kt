package com.qeetgroup.qeetrix.components.passwordinput

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.input.FieldAction
import com.qeetgroup.qeetrix.components.input.FieldBox
import com.qeetgroup.qeetrix.components.input.FieldContentPadding
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.FieldPlaceholder
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.fieldError
import com.qeetgroup.qeetrix.components.input.fieldTextStyle
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

/** What a password is for, which tells AutoFill and password managers what to offer (`value` is the web's `autoComplete`). */
public enum class QeetrixPasswordPurpose(
    public val value: String,
) {
    /** Signing in with an existing password: AutoFill offers saved passwords. */
    Current("current-password"),

    /** Choosing a password — sign-up, reset, change: AutoFill can suggest a new one and offer to save it. */
    New("new-password"),
}

/**
 * A password field with a reveal button — the web's `PasswordInput` in a field.
 *
 * - **Secure entry.** It is Compose's [BasicSecureTextField]: masked — briefly showing the last character
 *   typed only when the device's "Show passwords" setting allows — with copy and cut disabled,
 *   suggestions and auto-correction off, and marked as a password for accessibility services, which do
 *   not speak it unless the user has asked them to.
 * - **Reveal.** The button at the end shows and hides the password in the same field, so the text, the
 *   cursor, the selection, focus and the keyboard all stay as they were. It is named "Show password" or
 *   "Hide password" (`qx_show_password`, `qx_hide_password`) for its current action, and is drawn inside the
 *   field without changing its height. [showToggle] `false` removes it.
 * - **AutoFill.** [purpose] marks the field as a current or a new password, so AutoFill and password
 *   managers offer to fill or save it. Whether they do depends on the device and the user's provider.
 * - **Your app owns the password.** It lives only in the [TextFieldState] you own. Qeetrix never logs,
 *   stores, sends or describes it, and keeps no copy. Checking it, its strength and its policy is your app's.
 *
 * ```
 * val password = rememberTextFieldState()
 * QeetrixPasswordInput(password, label = "Password", purpose = QeetrixPasswordPurpose.Current,
 *     keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), onKeyboardAction = { signIn() })
 * ```
 *
 * @param keyboardOptions The IME action and other options; the keyboard type is always a password keyboard
 *   and auto-correction is always off.
 */
@Composable
public fun QeetrixPasswordInput(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    purpose: QeetrixPasswordPurpose = QeetrixPasswordPurpose.Current,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    optional: Boolean = false,
    showToggle: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    var revealed by rememberSaveable { mutableStateOf(false) }
    val textStyle = fieldTextStyle()
    val secureKeyboard =
        keyboardOptions.copy(
            keyboardType = if (revealed) KeyboardType.PasswordVisible else KeyboardType.Password,
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
        )
    val autofillType = if (purpose == QeetrixPasswordPurpose.New) ContentType.NewPassword else ContentType.Password

    FieldFrame(description, message, modifier) {
        BasicSecureTextField(
            state = state,
            // The outermost semantics win, so New replaces the Password type the secure field sets itself.
            modifier = Modifier.fillMaxWidth().semantics { contentType = autofillType }.fieldError(message),
            enabled = active,
            readOnly = readOnly,
            inputTransformation = inputTransformation,
            textStyle = textStyle,
            keyboardOptions = secureKeyboard,
            onKeyboardAction = onKeyboardAction,
            interactionSource = source,
            cursorBrush = SolidColor(QeetrixInputComponentTokens.Foreground),
            textObfuscationMode = if (revealed) TextObfuscationMode.Visible else TextObfuscationMode.System,
            decorator = { inner ->
                LabelledControl(label, required, optional, active) {
                    FieldBox(
                        active,
                        readOnly,
                        focused,
                        hovered,
                        message?.tone,
                        Modifier.fillMaxWidth(),
                        contentPadding = if (showToggle) PaddingWithToggle else FieldContentPadding,
                    ) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (placeholder != null && state.text.isEmpty()) FieldPlaceholder(placeholder, textStyle)
                            inner()
                        }
                        if (showToggle) {
                            FieldAction(
                                icon = if (revealed) QeetrixIconName.EyeOff else QeetrixIconName.Eye,
                                label = stringResource(if (revealed) R.string.qx_hide_password else R.string.qx_show_password),
                                onClick = { revealed = !revealed },
                                enabled = active,
                            )
                        }
                    }
                }
            },
        )
    }
}

/** px-2.5 before the text; the reveal button sits inset-e-1 from the end, with the text's gap before it. Vertically, as [FieldContentPadding]. */
private val PaddingWithToggle =
    PaddingValues(
        start = QeetrixWebScale.spacing(2.5f) + QeetrixStroke.Hairline,
        end = QeetrixWebScale.spacing(1f),
        top = QeetrixStroke.Hairline,
        bottom = QeetrixStroke.Hairline,
    )
