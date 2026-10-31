package com.qeetgroup.qeetrix.components.passwordinput

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDataType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.input.FieldAction
import com.qeetgroup.qeetrix.components.input.FieldBox
import com.qeetgroup.qeetrix.components.input.FieldContent
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.FieldPaddingWithTool
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.fieldError
import com.qeetgroup.qeetrix.components.input.fieldTextStyle
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * What a password field holds, which tells AutoFill and password managers what to offer (`value` is
 * `@qeetrix/react`'s `autoComplete`).
 */
public enum class QeetrixPasswordAutoComplete(
    public val value: String,
) {
    /** Signing in with an existing password: AutoFill offers saved passwords. The default. */
    CurrentPassword("current-password"),

    /** Choosing one — sign-up, reset, change: AutoFill can suggest a new password and offer to save it. */
    NewPassword("new-password"),

    /** A secret that is not the user's password, such as an API key: the field is left out of AutoFill. */
    Off("off"),
}

/**
 * A password or secret field with a Show password toggle — `@qeetrix/react`'s PasswordInput in a Field, the Input
 * box of [com.qeetgroup.qeetrix.components.input.QeetrixTextField] with its label, help text and error.
 *
 * - **Secure entry.** It is Compose's [BasicSecureTextField]: masked — briefly showing the last character typed
 *   only when the device's "Show passwords" setting allows — with copy and cut disabled, spelling, suggestions and
 *   capitalisation off, and marked as a password for accessibility services, which do not speak it unless the
 *   user has asked them to. Paste and password managers always work: never cap its length or block paste
 *   (WCAG 3.3.8), and never add a "confirm password" field.
 * - **Show password.** A pressed-state toggle at the end ("Show password", `qx_show_password`), eye ↔ eye-off,
 *   shows and hides the password in the same field, so the text, the cursor, focus and the keyboard stay as they
 *   were; it then says "Password shown" or "Password hidden". Hidden by default — people may not be alone.
 *   [visible] and [onVisibleChange] control it; leave [visible] `null` for the field to keep its own.
 * - **Caps Lock.** While a hardware keyboard types with Caps Lock on, "Caps Lock is on" shows before the toggle in
 *   `color.text.warning` ([capsLockWarning], default on).
 * - **AutoFill.** [autoComplete] marks the field as a current or a new password, or leaves it out of AutoFill.
 * - **Your app owns the password.** It lives only in the [TextFieldState] you own; Qeetrix never logs, stores,
 *   sends or describes it. Checking it, its strength and its policy are your app's.
 *
 * ```
 * val password = rememberTextFieldState()
 * QeetrixPasswordInput(password, label = "Password",
 *     keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done), onKeyboardAction = { signIn() })
 * ```
 *
 * @param endAdornment Tools before the toggle, such as a copy button for a secret.
 * @param keyboardOptions The IME action and other options; the keyboard is always a password keyboard with
 *   auto-correction off.
 */
@Composable
public fun QeetrixPasswordInput(
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
    autoComplete: QeetrixPasswordAutoComplete = QeetrixPasswordAutoComplete.CurrentPassword,
    visible: Boolean? = null,
    onVisibleChange: ((Boolean) -> Unit)? = null,
    capsLockWarning: Boolean = true,
    icon: QeetrixIconName? = null,
    startAdornment: (@Composable () -> Unit)? = null,
    endAdornment: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    var own by rememberSaveable { mutableStateOf(false) }
    val shown = visible ?: own
    var capsLock by remember { mutableStateOf(false) }
    LaunchedEffect(focused) { if (!focused) capsLock = false }
    val colors = QeetrixTheme.colors
    val textStyle = fieldTextStyle().copy(color = if (active) colors.textDefault else colors.textDisabled)
    val secureKeyboard =
        keyboardOptions.copy(
            keyboardType = if (shown) KeyboardType.PasswordVisible else KeyboardType.Password,
            capitalization = KeyboardCapitalization.None, // autocapitalize="none"
            autoCorrectEnabled = false, // spellcheck="false", autocorrect="off"
        )

    FieldFrame(description, error, active, modifier) {
        BasicSecureTextField(
            state = state,
            // The outermost semantics win, so these replace the Password type the secure field sets itself.
            modifier =
                Modifier
                    .fillMaxWidth()
                    .autoComplete(autoComplete)
                    .fieldError(error)
                    .onPreviewKeyEvent { event ->
                        if (capsLockWarning) capsLock = event.nativeKeyEvent.isCapsLockOn
                        false
                    },
            enabled = active,
            readOnly = readOnly,
            inputTransformation = inputTransformation,
            textStyle = textStyle,
            keyboardOptions = secureKeyboard,
            onKeyboardAction = onKeyboardAction,
            interactionSource = source,
            cursorBrush = SolidColor(textStyle.color),
            textObfuscationMode = if (shown) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,
            decorator = { inner ->
                LabelledControl(label, optional, required, hideLabel, active, help) {
                    FieldBox(
                        active,
                        readOnly,
                        focused,
                        hovered,
                        invalid || error != null,
                        Modifier.fillMaxWidth(),
                        contentPadding = FieldPaddingWithTool,
                    ) {
                        FieldContent(
                            icon = icon,
                            startAdornment = startAdornment,
                            endAdornment = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (capsLock && capsLockWarning) CapsLockWarning()
                                    endAdornment?.invoke()
                                    FieldAction(
                                        icon = if (shown) QeetrixIconName.EyeOff else QeetrixIconName.Eye,
                                        contentDescription = stringResource(R.string.qx_show_password),
                                        enabled = active,
                                        toggled = shown,
                                        stateDescription =
                                            stringResource(
                                                if (shown) R.string.qx_password_shown else R.string.qx_password_hidden,
                                            ),
                                    ) {
                                        if (visible == null) own = !shown
                                        onVisibleChange?.invoke(!shown)
                                    }
                                }
                            },
                            placeholder = placeholder.takeIf { state.text.isEmpty() },
                            textStyle = textStyle,
                            inner = inner,
                        )
                    }
                }
            },
        )
    }
}

/** What the password is for AutoFill: a current or new password, or nothing AutoFill should touch. */
private fun Modifier.autoComplete(autoComplete: QeetrixPasswordAutoComplete): Modifier =
    semantics {
        when (autoComplete) {
            QeetrixPasswordAutoComplete.CurrentPassword -> contentType = ContentType.Password

            QeetrixPasswordAutoComplete.NewPassword -> contentType = ContentType.NewPassword

            // AUTOFILL_TYPE_NONE: the field offers nothing to fill or save.
            QeetrixPasswordAutoComplete.Off -> contentDataType = ContentDataType.None
        }
    }

/**
 * `.qx-password-input-caps`: a 14 dp sharp icon, `space.gap.xs` before "Caps Lock is on" (`qx_caps_lock`) in
 * `label.sm` / `color.text.warning`, on one line, after a 14 dp `arrow-big-up`.
 */
@Composable
private fun CapsLockWarning() {
    val color = QeetrixTheme.colors.textWarning
    Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs), verticalAlignment = Alignment.CenterVertically) {
        QeetrixIcon(QeetrixIconName.ArrowBigUp, contentDescription = null, size = CAPS_ICON, shape = QeetrixIconShape.Sharp, tint = color)
        BasicText(
            stringResource(R.string.qx_caps_lock),
            style =
                QeetrixTheme.typography.labelSm
                    .copy(color = color)
                    .labelDirection(),
            maxLines = 1,
            softWrap = false,
        )
    }
}

/** password-input.tsx `<ArrowBigUpIcon size={14} />`. */
private val CAPS_ICON = 14.dp
