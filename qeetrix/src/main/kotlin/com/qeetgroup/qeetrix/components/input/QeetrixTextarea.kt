package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.unit.em
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

/**
 * A multi-line text field with its label, description and message — the web's `Textarea` in a field, on the
 * same field parts as [QeetrixTextField], for notes, feedback and addresses.
 *
 * - The text lives in the [TextFieldState] you own; Enter starts a new line.
 * - It grows with its content from two control heights (64 dp at the default density) up to 384 dp, then
 *   scrolls inside itself, so a long note never pushes the rest of a form off screen. [minLines] and
 *   [maxLines] set the height in lines instead, within those bounds.
 * - Labels, required and optional markers, messages, read-only, disabled and [contentType] (for AutoFill,
 *   such as `ContentType.PostalAddress`) behave as in [QeetrixTextField].
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
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    optional: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    contentType: ContentType? = null,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    require(minLines in 1..maxLines) { "minLines ($minLines) must be at least 1 and no more than maxLines ($maxLines)." }
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    // leading-normal: a reading line height for prose, rather than the single-line field's.
    val textStyle = fieldTextStyle().copy(lineHeight = QeetrixWebScale.LEADING_NORMAL.em)

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
            lineLimits = TextFieldLineLimits.MultiLine(minHeightInLines = minLines, maxHeightInLines = maxLines),
            interactionSource = source,
            cursorBrush = SolidColor(QeetrixInputComponentTokens.Foreground),
            decorator = { inner ->
                LabelledControl(label, required, optional, active) {
                    FieldBox(
                        active,
                        readOnly,
                        focused,
                        hovered,
                        message?.tone,
                        Modifier.fillMaxWidth().heightIn(max = TextareaMaxHeight), // max-h-96
                        // min-h-[calc(var(--qx-component-input-height)*2)]
                        minHeight = QeetrixInputComponentTokens.height(QeetrixTheme.density) * 2,
                        contentPadding = TextareaPadding,
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

/** px-2.5 py-2 inside the 1 dp border. */
private val TextareaPadding =
    PaddingValues(
        horizontal = QeetrixWebScale.spacing(2.5f) + QeetrixStroke.Hairline,
        vertical = QeetrixWebScale.spacing(2f) + QeetrixStroke.Hairline,
    )

/** max-h-96: 24rem. */
private val TextareaMaxHeight = QeetrixWebScale.spacing(96f)
