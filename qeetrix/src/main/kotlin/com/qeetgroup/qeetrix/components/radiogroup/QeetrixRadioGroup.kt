package com.qeetgroup.qeetrix.components.radiogroup

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
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
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * One choice from a set, always answered — `@qeetrix/react`'s RadioGroup with its Radios. [label] is the question,
 * shown in the field-label style; each option is a radio with its label 8 dp after it, the whole row the target.
 *
 * - **Look** (design rules §9): a 16 dp circle with a 1 dp `color.border.strong` edge on `color.bg.input`; selected,
 *   a 5 dp Ember ring (`color.bg.brand.bold`) with a white centre. Hover moves the edge to its `-hover` colour;
 *   an [error] turns unselected edges `color.border.danger`; disabled takes `color.bg.disabled`.
 * - **One choice.** [value] is a value and [onValueChange] receives the chosen option's, so only an option can be
 *   chosen. Choose "nothing yet" with a value no option has — `null` for a nullable type. Options need values of
 *   their own; an option with `enabled = false` cannot be chosen; [readOnly] shows the choice without changing it.
 * - **Layout.** Options stack ([QeetrixChoiceOrientation.Vertical], each row a 48 dp target) or wrap in a row 16 dp
 *   apart ([QeetrixChoiceOrientation.Horizontal]). [QeetrixChoiceVariant.Tile] draws tiles — icon, title, description,
 *   the radio at the end — up to [columns] across where tiles of 192 dp fit (07-components.md, Choice tiles).
 * - **Accessibility.** A selectable group under its question (a heading); each option is a radio button read
 *   with its description and "selected" or "not selected". The [description] or the [error] follows the options;
 *   the error is carried by every option, so it is heard on whichever is focused. A [required] group says so.
 *   Keyboard focus draws the Ember frame 2 dp outside the circle (or round the tile).
 *
 * ```
 * var cycle by rememberSaveable { mutableStateOf("monthly") }
 * QeetrixRadioGroup(cycle, { cycle = it },
 *     listOf(QeetrixOption("monthly", "Monthly"), QeetrixOption("yearly", "Yearly", description = "Two months free")),
 *     label = "Billing cycle")
 * ```
 */
@Composable
public fun <T> QeetrixRadioGroup(
    value: T,
    onValueChange: (T) -> Unit,
    options: List<QeetrixOption<T>>,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    error: String? = null,
    orientation: QeetrixChoiceOrientation = QeetrixChoiceOrientation.Vertical,
    variant: QeetrixChoiceVariant = QeetrixChoiceVariant.Default,
    columns: Int = 1,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
) {
    requireDistinctValues(options)
    ChoiceGroup(
        label = label,
        description = description,
        error = error,
        enabled = enabled,
        required = required,
        orientation = orientation,
        variant = variant,
        columns = columns,
        selectable = true,
        modifier = modifier,
    ) {
        val group = LocalFieldGroup.current
        val stretch = variant == QeetrixChoiceVariant.Default && orientation == QeetrixChoiceOrientation.Vertical
        for (option in options) {
            key(option.value) {
                val selected = option.value == value
                val active = group.enabled && option.enabled
                val source = remember { MutableInteractionSource() }
                ChoiceRow(
                    kind = ChoiceKind.Radio,
                    label = option.label,
                    description = option.description,
                    icon = option.icon,
                    enabled = active,
                    chosen = selected,
                    source = source,
                    modifier =
                        Modifier
                            .then(if (stretch) Modifier.fillMaxWidth() else Modifier)
                            .selectable(
                                selected = selected,
                                interactionSource = source,
                                indication = null,
                                enabled = active && !readOnly,
                                role = Role.RadioButton,
                                onClick = { if (!selected) onValueChange(option.value) },
                            ).then(if (group.error != null) Modifier.semantics { error(group.error) } else Modifier),
                ) {
                    RadioIndicator(selected, active, interactive = !readOnly, invalid = group.invalid, source = source)
                }
            }
        }
    }
}

/**
 * The circle (radio.css `.qx-radio`, in its cascade order): a 1 dp `color.border.strong` edge on `color.bg.input`;
 * hover `color.border.strong-hover`; selected a 5 dp `color.bg.brand.bold` ring (`-hover` under the pointer) — the
 * ring is the border, so the centre stays `color.bg.input`; invalid `color.border.danger`, a chosen invalid radio's
 * ring too, under the pointer as well; disabled
 * `color.bg.disabled` with a `color.border.disabled` edge, or a `color.icon.disabled` ring when selected. The edge's
 * colour and width change in `motion.duration.fast`.
 */
@Composable
private fun RadioIndicator(
    selected: Boolean,
    enabled: Boolean,
    interactive: Boolean,
    invalid: Boolean,
    source: MutableInteractionSource,
) {
    val colors = QeetrixTheme.colors
    val hovered by source.collectIsHoveredAsState()
    val hover = hovered && enabled && interactive // :hover:not([data-disabled], [data-readonly])
    // A disabled chosen radio's ring is color.icon.disabled; otherwise the shared edge, the ring danger while invalid.
    val border = if (!enabled && selected) colors.iconDisabled else selectionControlEdge(colors, enabled, selected, invalid, hover)
    val fill = if (enabled) colors.bgInput else colors.bgDisabled
    val colorMotion = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
    val edge by animateColorAsState(border, colorMotion, label = "radio-border")
    val width by animateDpAsState(
        if (selected) RING else QeetrixBorder.WidthDefault, // .qx-radio[data-checked] { border-width: 5px }
        qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast),
        label = "radio-border-width",
    )
    Box(Modifier.qeetrixFocusFrame(source, QeetrixShapes.Full, FocusFrame.Outside)) {
        RadioCircle(fill, edge, width)
    }
}

/** A 16 dp circle (`block-size: 16px; border-radius: radius.full`) with a [width] border inside its edge. */
@Composable
private fun RadioCircle(
    fill: Color,
    edge: Color,
    width: Dp,
) {
    Canvas(Modifier.size(CIRCLE)) {
        drawCircle(fill)
        val stroke = width.toPx()
        drawCircle(edge, radius = (size.minDimension - stroke) / 2, style = Stroke(stroke))
    }
}

/** radio.css `.qx-radio { block-size: 16px; inline-size: 16px }`. */
private val CIRCLE = 16.dp

/** radio.css `.qx-radio[data-checked] { border-width: 5px }`. */
private val RING = 5.dp
