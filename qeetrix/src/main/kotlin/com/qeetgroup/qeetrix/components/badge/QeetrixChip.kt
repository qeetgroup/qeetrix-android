package com.qeetgroup.qeetrix.components.badge

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.input.FieldAction
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The heights of [QeetrixChip] (`value` names them as `@qeetrix/react`'s control sizes). */
public enum class QeetrixChipSize(
    public val value: String,
) {
    /** `size.control.sm` (24 / 28 / 32 dp), `label.sm` — FilterTags' tag height, for filter rows over a list. */
    Sm("sm"),

    /** `size.control.md` (28 / 32 / 36 dp), `label.md`. The default. */
    Md("md"),

    /** `size.control.lg` (32 / 40 / 44 dp), `label.md`, the field height. */
    Lg("lg"),
}

/**
 * A tag someone acts on: a filter that turns on and off. `@qeetrix/react` 0.2.2 has no Chip — Badge is static
 * ("an interactive or removable tag is a Chip", design rules §14) and FilterTags is a whole applied-filters bar —
 * so this is Qeetrix for Android's own, drawn in the Qeetrix language from FilterTags' tag and the choice tile:
 *
 * - **Resting**: a 4 dp-cornered tag (`radius.control`; only people and status are round) in
 *   `color.bg.neutral.subtle` with a 1 dp `color.border.subtle` edge inside, the label in `color.text.default`;
 *   `-hover` and `-pressed` steps under the pointer and a press, with no ripple.
 * - **Selected** — chosen for an action (design rules §11): `color.bg.selected` with a 2 dp `color.border.brand`
 *   edge, and a check before the label unless it has an [icon] of its own, so selection never rests on colour.
 * - **Disabled**: `color.bg.disabled`, a `color.border.disabled` edge and `color.text.disabled`.
 * - **Accessibility.** A checkbox, checked or not, read with its label. Keyboard focus draws the 2 dp Ember frame
 *   2 dp outside (design rules §10, tags). The drawn chip keeps its size; a 48 dp touch area is reserved round it.
 *
 * The filtering is the app's: the chip reports the new value. For a set of chips that choose one value, or
 * several, use [QeetrixChipGroup].
 *
 * ```
 * var unread by rememberSaveable { mutableStateOf(false) }
 * QeetrixChip("Unread", selected = unread, onSelectedChange = { unread = it })
 * ```
 */
@Composable
public fun QeetrixChip(
    label: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: QeetrixIconName? = null,
    size: QeetrixChipSize = QeetrixChipSize.Md,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    SelectableChip(
        label = label,
        icon = icon ?: QeetrixIconName.Check.takeIf { selected },
        size = size,
        selected = selected,
        enabled = enabled,
        source = source,
        // Filter chips toggle: a checkbox for accessibility services.
        interaction =
            Modifier.toggleable(
                selected,
                source,
                indication = null,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onSelectedChange,
            ),
        modifier = modifier,
    )
}

/**
 * A choice already made — an applied filter, a tag — with a button that takes it away: FilterTags' tag
 * (`.qx-filter-tag`: `color.bg.neutral.subtle`, a 1 dp `color.border.subtle` edge inside, 4 dp corners,
 * `space.gap.sm` before the label, `space.gap.xs` between its parts) ending in its remove button, a small neutral
 * icon button named "Remove {label}" (`qx_remove`). The chip itself is not selectable; its label is read as text.
 * The remove button keeps its drawn size inside the chip, and Compose sends a touch within 48 dp of it to it.
 * Removing the chip, and offering an undo, are the app's; move focus to the next chip after a removal, as
 * FilterTags does.
 *
 * ```
 * for (tag in tags) {
 *     key(tag.id) { QeetrixChip(tag.name, onRemove = { tags.remove(tag) }) }
 * }
 * ```
 */
@Composable
public fun QeetrixChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    icon: QeetrixIconName? = null,
    size: QeetrixChipSize = QeetrixChipSize.Md,
    enabled: Boolean = true,
) {
    val metrics = chipMetrics(size)
    val colors = chipColors(selected = false, enabled = enabled, lit = false, pressed = false)
    // The same 48 dp row as a selectable chip, so mixed chips line up.
    Box(modifier.heightIn(min = QeetrixAccessibility.MinimumTouchTarget), contentAlignment = Alignment.Center) {
        Row(
            Modifier
                .chipFrame(colors, metrics.height)
                // .qx-filter-tag { padding-inline-start: space.gap.sm }; the button ends the tag
                .padding(start = QeetrixSpace.GapSm),
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChipLabel(label, icon, metrics, colors.content)
            FieldAction(QeetrixIconName.X, stringResource(R.string.qx_remove, label), enabled = enabled, onClick = onRemove)
        }
    }
}

/**
 * Chips that choose one value from a set: they behave as radio buttons.
 *
 * ```
 * var status by rememberSaveable { mutableStateOf<Status?>(null) }
 * QeetrixChipGroup(label = "Status", value = status, onValueChange = { status = it },
 *     options = listOf(QeetrixOption(null, "All"), QeetrixOption(Status.Open, "Open"), QeetrixOption(Status.Closed, "Closed")))
 * ```
 *
 * - [label] names the group for accessibility services; it is not shown. Options need values of their own; an
 *   option with `enabled = false` cannot be chosen. Chips show labels only — an option's description is neither
 *   shown nor read — so use a [com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup] when options need
 *   explaining.
 * - The chips sit `space.gap.sm` apart and wrap onto more lines when they don't fit. For one scrolling line
 *   instead, put the group in a `Row` with `horizontalScroll`.
 */
@Composable
public fun <T> QeetrixChipGroup(
    label: String,
    value: T,
    onValueChange: (T) -> Unit,
    options: List<QeetrixOption<T>>,
    modifier: Modifier = Modifier,
    size: QeetrixChipSize = QeetrixChipSize.Md,
    enabled: Boolean = true,
) {
    requireDistinctValues(options)
    ChipFlow(label, modifier.selectableGroup()) {
        for (option in options) {
            key(option.value) {
                val chosen = option.value == value
                val active = enabled && option.enabled
                val source = remember { MutableInteractionSource() }
                SelectableChip(
                    label = option.label,
                    icon = option.icon ?: QeetrixIconName.Check.takeIf { chosen },
                    size = size,
                    selected = chosen,
                    enabled = active,
                    source = source,
                    interaction =
                        Modifier.selectable(
                            selected = chosen,
                            interactionSource = source,
                            indication = null,
                            enabled = active,
                            role = Role.RadioButton,
                            onClick = { if (!chosen) onValueChange(option.value) },
                        ),
                    modifier = Modifier,
                )
            }
        }
    }
}

/**
 * Chips that choose any number of values from a set: each toggles its value in or out of [values]. Its parameters
 * are `values` and `onValuesChange`, so a call names which group it means.
 *
 * ```
 * var labels by rememberSaveable { mutableStateOf(setOf<String>()) }
 * QeetrixChipGroup(label = "Labels", values = labels, onValuesChange = { labels = it }, options = labelOptions)
 * ```
 *
 * Each chip is a checkbox for accessibility services. Otherwise as the single-choice [QeetrixChipGroup].
 */
@Composable
public fun <T> QeetrixChipGroup(
    label: String,
    values: Set<T>,
    onValuesChange: (Set<T>) -> Unit,
    options: List<QeetrixOption<T>>,
    modifier: Modifier = Modifier,
    size: QeetrixChipSize = QeetrixChipSize.Md,
    enabled: Boolean = true,
) {
    requireDistinctValues(options)
    ChipFlow(label, modifier) {
        for (option in options) {
            key(option.value) {
                QeetrixChip(
                    label = option.label,
                    selected = option.value in values,
                    onSelectedChange = { on -> onValuesChange(if (on) values + option.value else values - option.value) },
                    icon = option.icon,
                    size = size,
                    enabled = enabled && option.enabled,
                )
            }
        }
    }
}

/** `gap: space.gap.sm`, wrapping, named by [label]. Each chip reserves a 48 dp row, so rows need no gap of their own. */
@Composable
private fun ChipFlow(
    label: String,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier.semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
        verticalArrangement = Arrangement.Center,
    ) { content() }
}

/** A selectable chip: its colours in each state, the focus frame and the 48 dp touch area, with [interaction] on it. */
@Composable
private fun SelectableChip(
    label: String,
    icon: QeetrixIconName?,
    size: QeetrixChipSize,
    selected: Boolean,
    enabled: Boolean,
    source: MutableInteractionSource,
    modifier: Modifier,
    interaction: Modifier,
) {
    val metrics = chipMetrics(size)
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val colors = chipColors(selected, enabled, lit = enabled && (hovered || pressed), pressed = enabled && pressed)
    val motion = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
    val fill by animateColorAsState(colors.fill, motion, label = "chip-fill")
    val edge by animateColorAsState(colors.edge, motion, label = "chip-edge")
    Box(modifier.then(interaction).qeetrixMinimumTouchTarget(), contentAlignment = Alignment.Center) {
        Row(
            Modifier
                .qeetrixFocusFrame(source, QeetrixShapes.Control, FocusFrame.Outside)
                .chipFrame(ChipColors(fill, edge, colors.edgeWidth, colors.content), metrics.height)
                .padding(PaddingValues(horizontal = QeetrixSpace.GapSm)),
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
            verticalAlignment = Alignment.CenterVertically,
        ) { ChipLabel(label, icon, metrics, colors.content) }
    }
}

/** A chip's colours in one state. */
private class ChipColors(
    val fill: Color,
    val edge: Color,
    val edgeWidth: Dp,
    val content: Color,
)

@Composable
private fun chipColors(
    selected: Boolean,
    enabled: Boolean,
    lit: Boolean,
    pressed: Boolean,
): ChipColors {
    val c = QeetrixTheme.colors
    return when {
        !enabled -> {
            ChipColors(c.bgDisabled, c.borderDisabled, QeetrixBorder.WidthDefault, c.textDisabled)
        }

        // Chosen (a choice tile's chosen look): bg.selected with a 2 dp border.brand edge; bg.selected-hover under the pointer.
        selected -> {
            ChipColors(if (lit) c.bgSelectedHover else c.bgSelected, c.borderBrand, QeetrixBorder.WidthStrong, c.textDefault)
        }

        // .qx-filter-tag: bg.neutral.subtle with an inset 1px border.subtle edge; the neutral tints' hover and pressed steps.
        else -> {
            val fill =
                when {
                    pressed -> c.bgNeutralSubtlePressed
                    lit -> c.bgNeutralSubtleHover
                    else -> c.bgNeutralSubtle
                }
            ChipColors(fill, c.borderSubtle, QeetrixBorder.WidthDefault, c.textDefault)
        }
    }
}

/** The tag's surface: the fill and the edge inside it in 4 dp corners, at least the size's height, growing with the font scale. */
private fun Modifier.chipFrame(
    colors: ChipColors,
    height: Dp,
): Modifier =
    background(colors.fill, QeetrixShapes.Control)
        .border(colors.edgeWidth, colors.edge, QeetrixShapes.Control)
        .heightIn(min = height)

/** The leading icon or check mark (16 dp, sharp), then the label on one line. */
@Composable
private fun ChipLabel(
    label: String,
    icon: QeetrixIconName?,
    metrics: ChipMetrics,
    color: Color,
) {
    if (icon != null) QeetrixIcon(icon, contentDescription = null, size = QeetrixSize.IconSm, shape = QeetrixIconShape.Sharp, tint = color)
    BasicText(
        label,
        style = metrics.textStyle.copy(color = color).labelDirection(),
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
    )
}

/** One size's height and text, resolved for the density. */
private class ChipMetrics(
    val height: Dp,
    val textStyle: TextStyle,
)

@Composable
private fun chipMetrics(size: QeetrixChipSize): ChipMetrics {
    val density = QeetrixTheme.density
    val typography = QeetrixTheme.typography
    return when (size) {
        QeetrixChipSize.Sm -> ChipMetrics(density.sizeControlSm, typography.labelSm)
        QeetrixChipSize.Md -> ChipMetrics(density.sizeControlMd, typography.labelMd)
        QeetrixChipSize.Lg -> ChipMetrics(density.sizeControlLg, typography.labelMd)
    }
}
