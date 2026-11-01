package com.qeetgroup.qeetrix.components.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.ChoiceGroup
import com.qeetgroup.qeetrix.components.input.ChoiceKind
import com.qeetgroup.qeetrix.components.input.ChoiceRow
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.components.input.selectionControlEdge
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A checkbox with its [label] — `@qeetrix/react`'s Checkbox. It turns one option on or off; for several options
 * under one question use [QeetrixCheckboxGroup]. A setting that applies at once is a switch instead.
 *
 * - **Look** (design rules §9): a 16 dp box with 2 dp corners, a 1 dp `color.border.strong` edge and
 *   `color.bg.input`; checked, an Ember fill (`color.bg.brand.bold`) with a white 12 dp tick; [indeterminate]
 *   (a "select all" over a partly chosen list), the Ember fill with a dash. The label follows the box, 8 dp after it
 *   in `body.md`, and the whole row — label and [description] included — is the target, at least 48 dp tall.
 * - **State is yours**: [checked] and [onCheckedChange]. A tap calls `onCheckedChange(!checked)`, so an
 *   indeterminate box becomes checked when [checked] is `false` (react's behaviour); you decide what that means
 *   for the items. Pass `onCheckedChange = null`, or [readOnly], to show a state that cannot be changed.
 * - [invalid] turns the box's edge `color.border.danger`, checked or not; inside a group or a [com.qeetgroup.qeetrix.components.input.QeetrixField]
 *   with an error, every checkbox is invalid and carries that error for accessibility services. [enabled] `false`
 *   takes `color.bg.disabled` with a `color.icon.disabled` mark and `color.text.disabled`.
 * - **Accessibility.** One checkbox, read with its label, description and state ("partially checked" when
 *   indeterminate); keyboard focus draws the 2 dp Ember frame 2 dp outside the box.
 *
 * ```
 * var updates by rememberSaveable { mutableStateOf(false) }
 * QeetrixCheckbox(updates, { updates = it }, label = "Product updates", description = "About once a month.")
 * ```
 *
 * @param icon A 16 dp icon before the label; on a tile, 20 dp in a box at its start.
 */
@Composable
public fun QeetrixCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: QeetrixIconName? = null,
    indeterminate: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val group = LocalFieldGroup.current
    val active = enabled && group.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interactive = onCheckedChange != null && !readOnly
    ChoiceRow(
        kind = ChoiceKind.Checkbox,
        label = label,
        description = description,
        icon = icon,
        enabled = active,
        chosen = checked || indeterminate,
        source = source,
        modifier = modifier.checkboxToggle(checked, indeterminate, onCheckedChange.takeIf { interactive }, active, source, group.error),
    ) {
        CheckboxIndicator(checked, indeterminate, active, interactive, invalid || group.invalid, source)
    }
}

/**
 * A checkbox with no visible label, for a table row or a list item that names it — react's Checkbox with an
 * `aria-label`. Give it a name: put it in a row that is itself toggleable, or set a `contentDescription` on
 * [modifier]. The drawn box keeps its 16 dp; a 48 × 48 dp touch area is reserved around it.
 */
@Composable
public fun QeetrixCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    indeterminate: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val group = LocalFieldGroup.current
    val active = enabled && group.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interactive = onCheckedChange != null && !readOnly
    Box(
        modifier
            .checkboxToggle(checked, indeterminate, onCheckedChange.takeIf { interactive }, active, source, group.error)
            .qeetrixMinimumTouchTarget(),
        contentAlignment = Alignment.Center,
    ) {
        CheckboxIndicator(checked, indeterminate, active, interactive, invalid || group.invalid, source)
    }
}

/**
 * Several options under one question — `@qeetrix/react`'s CheckboxGroup: [label] is the question, shown in the
 * field-label style; [value] is the set of chosen options' values and [onValueChange] receives the next set.
 *
 * - Options stack ([QeetrixChoiceOrientation.Vertical], each row a 48 dp target) or wrap in a row 16 dp apart
 *   ([QeetrixChoiceOrientation.Horizontal], for short options). [QeetrixChoiceVariant.Tile] draws each option as a
 *   tile with its icon, title and description, up to [columns] across where tiles of 192 dp fit.
 * - The [description] or the [error] follows the options; an error marks every checkbox invalid and is carried by
 *   each, so it is heard on whichever is focused. [enabled] `false` disables the group; an option with
 *   `enabled = false` cannot be changed. Options need values of their own.
 *
 * ```
 * var channels by rememberSaveable { mutableStateOf(setOf("email")) }
 * QeetrixCheckboxGroup(channels, { channels = it },
 *     listOf(QeetrixOption("email", "Email"), QeetrixOption("sms", "SMS")), label = "Notify me by")
 * ```
 */
@Composable
public fun <T> QeetrixCheckboxGroup(
    value: Set<T>,
    onValueChange: (Set<T>) -> Unit,
    options: List<QeetrixOption<T>>,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    error: String? = null,
    orientation: QeetrixChoiceOrientation = QeetrixChoiceOrientation.Vertical,
    variant: QeetrixChoiceVariant = QeetrixChoiceVariant.Default,
    columns: Int = 1,
    enabled: Boolean = true,
) {
    requireDistinctValues(options)
    ChoiceGroup(
        label = label,
        description = description,
        error = error,
        enabled = enabled,
        required = false,
        orientation = orientation,
        variant = variant,
        columns = columns,
        selectable = false,
        modifier = modifier,
    ) {
        val stretch = variant == QeetrixChoiceVariant.Default && orientation == QeetrixChoiceOrientation.Vertical
        for (option in options) {
            key(option.value) {
                QeetrixCheckbox(
                    checked = option.value in value,
                    onCheckedChange = { on -> onValueChange(if (on) value + option.value else value - option.value) },
                    label = option.label,
                    modifier = if (stretch) Modifier.fillMaxWidth() else Modifier,
                    description = option.description,
                    icon = option.icon,
                    enabled = option.enabled,
                )
            }
        }
    }
}

/** The toggle — or, when it cannot be changed, the same state for accessibility services — and the group's error. */
private fun Modifier.checkboxToggle(
    checked: Boolean,
    indeterminate: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean,
    source: MutableInteractionSource,
    error: String?,
): Modifier {
    val state = if (indeterminate) ToggleableState.Indeterminate else ToggleableState(checked)
    val toggle =
        if (onCheckedChange != null) {
            triStateToggleable(state, source, indication = null, enabled = enabled, role = Role.Checkbox) { onCheckedChange(!checked) }
        } else {
            semantics(mergeDescendants = true) {
                toggleableState = state
                role = Role.Checkbox
            }
        }
    return toggle.then(if (error != null) Modifier.semantics { error(error) } else Modifier)
}

/**
 * The box (checkbox.css `.qx-checkbox`, in its cascade order): rest `color.border.strong` on `color.bg.input`;
 * hover `color.border.strong-hover`; checked or mixed the Ember fill and edge (`-hover` under the pointer); invalid
 * `color.border.danger`, checked or not and under the pointer too (the fill still steps); disabled `color.bg.disabled`, `color.border.disabled` and a `color.icon.disabled`
 * mark. Colours change in `motion.duration.fast`. The mark is a 12 dp sharp `check`, or `minus` when mixed, at
 * stroke 3, in `color.icon.on-brand`.
 */
@Composable
private fun CheckboxIndicator(
    checked: Boolean,
    indeterminate: Boolean,
    enabled: Boolean,
    interactive: Boolean,
    invalid: Boolean,
    source: MutableInteractionSource,
) {
    val colors = QeetrixTheme.colors
    val hovered by source.collectIsHoveredAsState()
    val hover = hovered && enabled && interactive // :hover:not([data-disabled], [data-readonly])
    val on = checked || indeterminate
    val border = selectionControlEdge(colors, enabled, on, invalid, hover)
    val fill =
        when {
            !enabled -> colors.bgDisabled
            on -> if (hover) colors.bgBrandBoldHover else colors.bgBrandBold
            else -> colors.bgInput
        }
    val motion = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
    val edge by animateColorAsState(border, motion, label = "checkbox-border")
    val background by animateColorAsState(fill, motion, label = "checkbox-fill")
    val shape = QeetrixShapes.Sm // border-radius: radius.sm
    Box(
        Modifier
            .qeetrixFocusFrame(source, shape, FocusFrame.Outside)
            .size(BOX) // block-size: 16px; inline-size: 16px
            .background(background, shape)
            .border(QeetrixBorder.WidthDefault, edge, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (on) {
            QeetrixIcon(
                if (indeterminate) QeetrixIconName.Minus else QeetrixIconName.Check,
                contentDescription = null,
                size = MARK,
                stroke = MARK_STROKE,
                shape = QeetrixIconShape.Sharp,
                tint = if (enabled) colors.iconOnBrand else colors.iconDisabled,
            )
        }
    }
}

/** checkbox.css `.qx-checkbox { block-size: 16px; inline-size: 16px }`. */
private val BOX = 16.dp

/** checkbox.tsx `<Mark shape="sharp" size={12} strokeWidth={3} />`. */
private val MARK = 12.dp
private const val MARK_STROKE = 3f
