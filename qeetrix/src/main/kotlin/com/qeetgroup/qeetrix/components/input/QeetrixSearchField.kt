package com.qeetgroup.qeetrix.components.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

/**
 * A single-line field for a search query, with a search icon, a clear button and the keyboard's search key —
 * built on the same field parts as [QeetrixTextField]. The web has no search component (its data table's
 * search is an `Input` with these strings), and Compose has none outside Material.
 *
 * ```
 * val query = rememberTextFieldState()
 * QeetrixSearchField(query, placeholder = "Search members", onSearch = { search(it) })
 *
 * // Filter as the person types, after a pause.
 * LaunchedEffect(query) {
 *     snapshotFlow { query.text.toString() }.collectLatest { text ->
 *         delay(300)
 *         search(text)
 *     }
 * }
 * ```
 *
 * - **The query is the app's**, in a [TextFieldState] it owns. The field searches, debounces, cancels and
 *   remembers nothing; filter on the device or ask a server from the app's own state holder, and keep recent
 *   searches out of logs.
 * - **Clearing.** While there is text, a clear button ("Clear", `qx_clear`) empties it and keeps the focus.
 * - **The search key** runs [onSearch] with the query and hides the keyboard.
 * - **Accessibility.** The field is named by its [label], or "Search" (`qx_search`) without one; the
 *   placeholder ("Search…", `qx_search_placeholder`) is only a hint. When results change, show them where
 *   they are — a no-results [com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState] — rather than
 *   announcing each keystroke.
 * - It is the control height of the density and grows with the font scale, as every field does. Suggestions,
 *   recent searches and scopes, if an app has them, are its own content below the field.
 */
@Composable
public fun QeetrixSearchField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    onSearch: ((String) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    val textStyle = fieldTextStyle()
    val keyboard = LocalSoftwareKeyboardController.current
    val defaultName = stringResource(R.string.qx_search)
    val hint = placeholder ?: stringResource(R.string.qx_search_placeholder)

    BasicTextField(
        state = state,
        modifier = modifier.fillMaxWidth(),
        enabled = active,
        textStyle = textStyle,
        keyboardOptions =
            KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
            ),
        onKeyboardAction = {
            onSearch?.invoke(state.text.toString())
            keyboard?.hide()
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        interactionSource = source,
        cursorBrush = SolidColor(QeetrixInputComponentTokens.Foreground),
        decorator = { inner ->
            LabelledControl(label, required = false, optional = false, enabled = active) {
                FieldBox(active, readOnly = false, focused = focused, hovered = hovered, tone = null, modifier = Modifier.fillMaxWidth()) {
                    // The search icon, muted; with no visible label it carries the field's name.
                    QeetrixIcon(
                        QeetrixIconName.Search,
                        contentDescription = null,
                        modifier = if (label == null) Modifier.semantics { contentDescription = defaultName } else Modifier,
                        size = QeetrixWebScale.spacing(4f),
                        tint = QeetrixTheme.colors.textTertiary,
                    )
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (state.text.isEmpty()) FieldPlaceholder(hint, textStyle)
                        inner()
                    }
                    if (state.text.isNotEmpty()) {
                        FieldAction(QeetrixIconName.X, stringResource(R.string.qx_clear), onClick = { state.clearText() }, enabled = active)
                    }
                }
            }
        },
    )
}
