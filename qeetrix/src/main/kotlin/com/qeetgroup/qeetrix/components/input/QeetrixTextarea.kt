package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * Multi-line text with its label, help text and error — `@qeetrix/react`'s Textarea in a Field, with the Input
 * look and states of [QeetrixTextField], for notes, feedback and addresses.
 *
 * - **Height.** It starts [rows] lines tall (default 3) and grows with its content, up to [maxRows] lines when you
 *   set it (then it scrolls inside itself; without it, it keeps growing, as the web's `field-sizing: content`
 *   does). Its block padding is `(size.control.lg − 1 line) / 2`, so one line sits where a text field's does.
 * - The text lives in the [TextFieldState] you own; Enter starts a new line.
 * - Label, "(optional)", help, errors, read-only, disabled and [contentType] (for AutoFill, such as
 *   `ContentType.PostalAddress`) behave as in [QeetrixTextField].
 * - Plain text only: no formatting, Markdown or attachments.
 *
 * ```
 * val feedback = rememberTextFieldState()
 * QeetrixTextarea(feedback, label = "What could be better?", description = "Up to 500 characters.",
 *     inputTransformation = InputTransformation.maxLength(500))
 * ```
 */
@Composable
public fun QeetrixTextarea(
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
    rows: Int = 3,
    maxRows: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    contentType: ContentType? = null,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    require(rows >= 1) { "rows ($rows) must be at least 1." }
    require(maxRows == null || maxRows >= rows) { "maxRows ($maxRows) cannot be fewer than rows ($rows)." }
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    val colors = QeetrixTheme.colors
    val textStyle = fieldTextStyle().copy(color = if (active) colors.textDefault else colors.textDisabled)
    // .qx-textarea { padding-block: calc((size.control.lg - 1lh) / 2) }, inside the box's 12 dp inline padding
    val padding = PaddingValues(horizontal = QeetrixSpace.InsetMd, vertical = (QeetrixTheme.density.sizeControlLg - BodyMdLine) / 2)

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
            // min-block-size: rows lines; max-block-size: maxRows lines (data-max-rows), then it scrolls
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = rows, maxHeightInLines = maxRows ?: Int.MAX_VALUE),
            interactionSource = source,
            cursorBrush = SolidColor(textStyle.color),
            decorator = { inner ->
                LabelledControl(label, optional, required, hideLabel, active, help) {
                    FieldBox(
                        active,
                        readOnly,
                        focused,
                        hovered,
                        invalid || error != null,
                        Modifier.fillMaxWidth(),
                        contentPadding = padding,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Box(Modifier.weight(1f)) {
                            if (placeholder != null && state.text.isEmpty()) FieldPlaceholder(placeholder, textStyle, Int.MAX_VALUE)
                            inner()
                        }
                    }
                }
            },
        )
    }
}
