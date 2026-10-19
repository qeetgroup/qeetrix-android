package com.qeetgroup.qeetrix.components.radiogroup

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSetLegend
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.SelectionMark
import com.qeetgroup.qeetrix.components.input.SelectionRow
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A set of mutually exclusive options under a [label] — the web's `RadioGroup` in a field set. A selected
 * radio is a filled Ember disc with a light centre dot, the same selection style as a checkbox.
 *
 * - **One choice.** [selected] is a value and [onSelect] receives the option's value, so the group can only
 *   ever have one option selected, and only one of [options]. Choose "nothing yet" with a value no option
 *   has — `null` for a nullable type. Options need values of their own.
 * - **Accessibility.** The group is a selectable group named by its label; each option is one radio button —
 *   its label and description read together, with "selected" or "not selected". A [message] with an error
 *   is shown after the options and carried by every option, so the error is heard on whichever is focused.
 * - **Touch and layout.** Each option row is at least a 48 dp touch target, and the rows sit flush. An option
 *   with `enabled = false` cannot be chosen; [enabled] `false` disables the group.
 *
 * ```
 * var plan by rememberSaveable { mutableStateOf(Plan.Team) }
 * QeetrixRadioGroup(plan, { plan = it }, listOf(QeetrixOption(Plan.Starter, "Starter"), QeetrixOption(Plan.Team, "Team")), label = "Plan")
 * ```
 */
@Composable
public fun <T> QeetrixRadioGroup(
    selected: T,
    onSelect: (T) -> Unit,
    options: List<QeetrixOption<T>>,
    label: String?,
    modifier: Modifier = Modifier,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    enabled: Boolean = true,
    required: Boolean = false,
    optional: Boolean = false,
) {
    requireDistinctValues(options)
    QeetrixFieldSet(
        legend = label,
        modifier = modifier,
        legendStyle = QeetrixFieldSetLegend.Label,
        description = description,
        message = message,
        required = required,
        optional = optional,
        enabled = enabled,
    ) {
        val group = LocalFieldGroup.current
        val groupError = group.message?.takeIf { it.tone == QeetrixFieldMessageTone.Error }
        Column(Modifier.selectableGroup()) {
            for (option in options) {
                key(option.value) {
                    val isSelected = option.value == selected
                    val active = group.enabled && option.enabled
                    val source = remember { MutableInteractionSource() }
                    SelectionRow(
                        label = option.label,
                        description = option.description,
                        enabled = active,
                        modifier =
                            Modifier
                                .selectable(
                                    selected = isSelected,
                                    interactionSource = source,
                                    indication = null,
                                    enabled = active,
                                    role = Role.RadioButton,
                                    onClick = { if (!isSelected) onSelect(option.value) },
                                ).then(if (groupError != null) Modifier.semantics { error(groupError.text) } else Modifier),
                    ) {
                        SelectionMark(CircleShape, isSelected, active, groupError != null, source) {
                            // size-1.5 rounded-full bg-primary-foreground
                            Box(Modifier.size(QeetrixWebScale.spacing(1.5f)).background(QeetrixTheme.colors.textOnBrand, CircleShape))
                        }
                    }
                }
            }
        }
    }
}
