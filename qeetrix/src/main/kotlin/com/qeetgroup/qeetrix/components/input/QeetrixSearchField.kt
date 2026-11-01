package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * A search box for a list — `@qeetrix/react`'s SearchField: the Input box with a leading search icon, the
 * keyboard's search key, and a Clear search button while there is text.
 *
 * ```
 * val query = rememberTextFieldState()
 * QeetrixSearchField(query, placeholder = "e.g. Asha", onSearch = { members.search(it) })
 * ```
 *
 * - **Searching.** [onSearch] runs when typing pauses for [searchDelay] (default 300 ms), at once on the
 *   keyboard's search key, and with "" when the field is cleared — so a server-searched list asks once per
 *   pause, not per key, and the search key never asks twice. Text the app writes into the state itself is not
 *   searched. The query is the app's, in a [TextFieldState] it owns; keep recent searches out of logs.
 * - **Clearing.** While there is text, a "Clear search" button (`qx_clear_search`) empties it, searches "" and
 *   returns focus to the field. **Escape** clears it too, and only lets the key through once the field is empty,
 *   so a dialog around it closes on the second press.
 * - **Name.** The field is named by its [label], shown above it, or — without one — "Search" (`qx_search`), with
 *   the search icon as its visible sign, as a toolbar search is. The [placeholder] is an example.
 * - [endAdornment] holds tools before the clear button. It is the control height of the density and grows with
 *   the font scale, as every field does.
 */
@Composable
public fun QeetrixSearchField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    onSearch: ((String) -> Unit)? = null,
    searchDelay: Duration = 300.milliseconds,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    endAdornment: (@Composable () -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    val colors = QeetrixTheme.colors
    val textStyle = fieldTextStyle().copy(color = if (active) colors.textDefault else colors.textDisabled)
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val latestOnSearch by rememberUpdatedState(onSearch)
    val latestDelay by rememberUpdatedState(searchDelay)
    val pending = remember { PendingSearch() }

    fun searchNow(text: String) {
        pending.cancel()
        latestOnSearch?.invoke(text)
    }

    fun clear() {
        state.clearText()
        searchNow("")
    }
    // Only typing (and pasting, and AutoFill) passes through an input transformation, never the app's own writes:
    // react's `onValueChange`, which starts the pause.
    val typing =
        remember(scope) {
            InputTransformation {
                val text = asCharSequence().toString()
                if (text != originalText.toString()) {
                    pending.schedule(scope, latestDelay) { latestOnSearch?.invoke(text) }
                }
            }
        }
    val clearable = state.text.isNotEmpty() && active && !readOnly
    // Whether this Escape press cleared the field, so its key-up is consumed with it.
    val escape = remember { EscapePress() }

    BasicTextField(
        state = state,
        modifier =
            modifier.fillMaxWidth().focusRequester(focus).onPreviewKeyEvent { event ->
                // Escape clears a field with text and is consumed, key-up included; an empty field lets it through.
                if (event.key != Key.Escape) return@onPreviewKeyEvent false
                when (event.type) {
                    KeyEventType.KeyDown -> {
                        escape.cleared = state.text.isNotEmpty() && !readOnly && active
                        if (escape.cleared) clear()
                        escape.cleared
                    }

                    KeyEventType.KeyUp -> {
                        escape.cleared.also { escape.cleared = false }
                    }

                    else -> {
                        false
                    }
                }
            },
        enabled = active,
        readOnly = readOnly,
        inputTransformation = typing,
        textStyle = textStyle,
        keyboardOptions =
            KeyboardOptions(capitalization = KeyboardCapitalization.None, keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
        onKeyboardAction = {
            searchNow(state.text.toString())
            keyboard?.hide()
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        interactionSource = source,
        cursorBrush = SolidColor(textStyle.color),
        decorator = { inner ->
            LabelledControl(
                label = label ?: stringResource(R.string.qx_search),
                optional = false,
                required = false,
                hideLabel = label == null,
                enabled = active,
                help = null,
            ) {
                FieldBox(
                    active,
                    readOnly,
                    focused,
                    hovered,
                    invalid = false,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = FieldPaddingWithTool,
                ) {
                    val tools: (@Composable () -> Unit)? =
                        if (endAdornment != null || clearable) {
                            {
                                Row {
                                    endAdornment?.invoke()
                                    if (clearable) {
                                        FieldAction(QeetrixIconName.X, stringResource(R.string.qx_clear_search), enabled = true) {
                                            clear()
                                            focus.requestFocus()
                                        }
                                    }
                                }
                            }
                        } else {
                            null
                        }
                    FieldContent(QeetrixIconName.Search, null, tools, placeholder.takeIf { state.text.isEmpty() }, textStyle, inner)
                }
            }
        },
    )
}

private class EscapePress {
    var cleared = false
}

/** The search waiting for typing to pause; a new keystroke or an immediate search replaces it. */
private class PendingSearch {
    private var job: Job? = null

    fun schedule(
        scope: CoroutineScope,
        after: Duration,
        search: () -> Unit,
    ) {
        job?.cancel()
        job =
            scope.launch {
                delay(after)
                job = null
                search()
            }
    }

    fun cancel() {
        job?.cancel()
        job = null
    }
}
