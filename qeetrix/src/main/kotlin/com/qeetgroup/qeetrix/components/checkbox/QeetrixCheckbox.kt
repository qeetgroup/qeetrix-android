package com.qeetgroup.qeetrix.components.checkbox

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
import com.qeetgroup.qeetrix.components.input.SelectionMark
import com.qeetgroup.qeetrix.components.input.SelectionRow
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A checkbox with its [label] and an optional [description] — the web's `Checkbox` in a horizontal field.
 * The whole row toggles, and accessibility services read the label, the description, "checkbox" and its
 * state together. The box sits at the start; the row is at least a 48 dp touch target.
 *
 * State is yours: [checked] and [onCheckedChange]. Pass `onCheckedChange = null` to show a state the user
 * cannot change. [invalid] draws the error boundary; inside a [com.qeetgroup.qeetrix.components.input.QeetrixFieldSet]
 * with an error message, every checkbox is invalid and carries that message for accessibility services.
 * Agreement and consent rules are your app's: the checkbox only shows and reports the choice.
 *
 * ```
 * var updates by rememberSaveable { mutableStateOf(false) }
 * QeetrixCheckbox(updates, { updates = it }, label = "Product updates", description = "About once a month.")
 * ```
 */
@Composable
public fun QeetrixCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    QeetrixTriStateCheckbox(
        state = ToggleableState(checked),
        onClick = onCheckedChange?.let { change -> { change(!checked) } },
        label = label,
        modifier = modifier,
        description = description,
        enabled = enabled,
        invalid = invalid,
        interactionSource = interactionSource,
    )
}

/**
 * A checkbox on its own, for a table row or a list item that names it. Give it a name: put it in a row that
 * is itself toggleable, or set a content description. The drawn box keeps the web's 16 dp; a 48 × 48 dp
 * touch area is reserved around it.
 */
@Composable
public fun QeetrixCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    QeetrixTriStateCheckbox(
        state = ToggleableState(checked),
        onClick = onCheckedChange?.let { change -> { change(!checked) } },
        modifier = modifier,
        enabled = enabled,
        invalid = invalid,
        interactionSource = interactionSource,
    )
}

/**
 * A checkbox that can also be mixed ([ToggleableState.Indeterminate]): a "select all" over a list whose
 * items are partly selected. It shows the [state] you pass and calls [onClick]; you decide the next state.
 * The usual rule is that a tap selects everything from unchecked or mixed, and clears everything from
 * checked — so mixed is never reached by tapping, only by changing the items. Accessibility services read
 * mixed as "partially checked".
 *
 * ```
 * QeetrixTriStateCheckbox(allState, onClick = { selectAll(allState != ToggleableState.On) }, label = "All members")
 * ```
 */
@Composable
public fun QeetrixTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val group = LocalFieldGroup.current
    val active = enabled && group.enabled
    val groupError = group.message?.takeIf { it.tone == QeetrixFieldMessageTone.Error }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    SelectionRow(
        label = label,
        description = description,
        enabled = active,
        modifier =
            modifier.toggle(state, onClick, active, source).then(
                if (groupError !=
                    null
                ) {
                    Modifier.semantics { error(groupError.text) }
                } else {
                    Modifier
                },
            ),
    ) {
        CheckboxBox(state, active, invalid || groupError != null, source)
    }
}

/** A tri-state [QeetrixCheckbox] on its own; give it a name from its context. */
@Composable
public fun QeetrixTriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val group = LocalFieldGroup.current
    val active = enabled && group.enabled
    val groupError = group.message?.takeIf { it.tone == QeetrixFieldMessageTone.Error }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    Box(
        modifier
            .toggle(state, onClick, active, source)
            .then(if (groupError != null) Modifier.semantics { error(groupError.text) } else Modifier)
            .qeetrixMinimumTouchTarget(),
        contentAlignment = Alignment.Center,
    ) {
        CheckboxBox(state, active, invalid || groupError != null, source)
    }
}

private fun Modifier.toggle(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    enabled: Boolean,
    source: MutableInteractionSource,
): Modifier =
    if (onClick != null) {
        triStateToggleable(state, source, indication = null, enabled = enabled, role = Role.Checkbox, onClick = onClick)
    } else {
        semantics(mergeDescendants = true) { toggleableState = state }
    }

/** The square box (`rounded-(--qx-corner-sm)`), with a check, or a dash when mixed, at 12 dp and stroke 3. */
@Composable
private fun CheckboxBox(
    state: ToggleableState,
    enabled: Boolean,
    invalid: Boolean,
    source: MutableInteractionSource,
) {
    SelectionMark(RoundedCornerShape(QeetrixCorner.Sm), state != ToggleableState.Off, enabled, invalid, source) {
        QeetrixIcon(
            if (state == ToggleableState.Indeterminate) QeetrixIconName.Minus else QeetrixIconName.Check,
            contentDescription = null,
            size = QeetrixWebScale.spacing(3f),
            stroke = GLYPH_STROKE,
            tint = QeetrixTheme.colors.textOnBrand, // text-primary-foreground
        )
    }
}

/** strokeWidth={3}: the glyph is drawn heavier than an icon so it reads at 12 dp. */
private const val GLYPH_STROKE = 3f
