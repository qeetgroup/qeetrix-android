package com.qeetgroup.qeetrix.components.badge

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.atAlpha
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The sizes of [QeetrixChip] (`value` is the web name). */
public enum class QeetrixChipSize(
    public val value: String,
) {
    /** 24 dp tall, 12 sp text. */
    Sm("sm"),

    /** 28 dp tall, 14 sp text. The default. */
    Md("md"),

    /** 32 dp tall, 14 sp text. */
    Lg("lg"),
}

/**
 * A pill that filters or chooses — the web's `Chip`, selectable. A tap toggles it.
 *
 * ```
 * var unread by rememberSaveable { mutableStateOf(false) }
 * QeetrixChip("Unread", selected = unread, onSelectedChange = { unread = it })
 * ```
 *
 * - A selected chip is tinted with the brand, edged with `border-brand`, and shows a check mark unless it has
 *   an [icon] of its own — so selection never rests on colour alone.
 * - **Not a badge.** A status is a [QeetrixBadge]; a chip is something the person acts on.
 * - **The filtering is the app's.** The chip reports the new value; the app decides what it filters.
 * - **Accessibility.** A checkbox that is checked or not, read with its label. The drawn chip keeps the web's
 *   height; a 48 dp touch area is reserved around it.
 *
 * For a set of chips that choose one value, or several, use [QeetrixChipGroup].
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
    ChipSurface(
        label = label,
        icon = icon ?: QeetrixIconName.Check.takeIf { selected },
        size = size,
        selected = selected,
        enabled = enabled,
        source = source,
        // Filter chips toggle: a checkbox for accessibility services, as Material's filter chip is.
        interaction =
            Modifier.toggleable(
                value = selected,
                interactionSource = source,
                indication = null,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onSelectedChange,
            ),
        modifier = modifier,
    )
}

/**
 * A chip that shows a choice already made — an applied filter, a tag — with a button that takes it away:
 * the web's `Chip` with `onRemove`. The chip itself is not selectable.
 *
 * ```
 * for (tag in tags) {
 *     key(tag.id) { QeetrixChip(tag.name, onRemove = { tags.remove(tag) }) }
 * }
 * ```
 *
 * The label is read as text; the remove button is its own control, named "Remove" with the label
 * (`qx_remove`: "Remove Design"). It keeps the web's 16 dp size inside the chip; Compose sends a touch within
 * 48 dp of it to it when nothing else is there. Removing the chip, and offering an undo, are the app's.
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
    val metrics = ChipMetrics.of(size)
    val colors = QeetrixTheme.colors
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val lit = enabled && (hovered || pressed)
    val name = stringResource(R.string.qx_remove, label)
    // The same 48 dp row as a selectable chip, so mixed chips line up.
    Box(modifier.heightIn(min = QeetrixAccessibility.MinimumTouchTarget), contentAlignment = Alignment.Center) {
        Row(
            Modifier
                .then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled))
                .chipFrame(Color.Transparent, restingBorder(), metrics)
                // pe-1: the remove button sits close to the end.
                .padding(start = metrics.paddingX + QeetrixStroke.Hairline, end = QeetrixWebScale.spacing(1f) + QeetrixStroke.Hairline),
            horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f)), // ms-0.5
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChipLabel(label, icon, metrics)
            // size-4 rounded-full text-muted-foreground, hover:bg-foreground/10 hover:text-foreground
            Box(
                Modifier
                    .size(QeetrixWebScale.spacing(4f))
                    .qeetrixFocusRing(source, CircleShape)
                    .background(if (lit) colors.textPrimary.atAlpha(REMOVE_HOVER_ALPHA) else Color.Transparent, CircleShape)
                    .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onRemove)
                    .semantics { contentDescription = name },
                contentAlignment = Alignment.Center,
            ) {
                QeetrixIcon(
                    QeetrixIconName.X,
                    contentDescription = null,
                    size = QeetrixWebScale.spacing(3f), // size-3
                    tint = if (lit) colors.textPrimary else colors.textTertiary,
                )
            }
        }
    }
}

/**
 * Chips that choose one value from a set — the web's `ChipGroup`, single-select: they behave as radio buttons.
 *
 * ```
 * var status by rememberSaveable { mutableStateOf<Status?>(null) }
 * QeetrixChipGroup(
 *     label = "Status",
 *     selected = status,
 *     onSelect = { status = it },
 *     options = listOf(QeetrixOption(null, "All"), QeetrixOption(Status.Open, "Open"), QeetrixOption(Status.Closed, "Closed")),
 * )
 * ```
 *
 * - [label] names the group for accessibility services; it is not shown. Options need values of their own;
 *   an option with `enabled = false` cannot be chosen. Chips show labels only: an option's description is
 *   neither shown nor read, so choose a [com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup]
 *   when options need explaining.
 * - The chips wrap onto more lines when they don't fit. For one scrolling line instead, put the group in a
 *   `Row` with `horizontalScroll`.
 */
@Composable
public fun <T> QeetrixChipGroup(
    label: String,
    selected: T,
    onSelect: (T) -> Unit,
    options: List<QeetrixOption<T>>,
    modifier: Modifier = Modifier,
    size: QeetrixChipSize = QeetrixChipSize.Md,
    enabled: Boolean = true,
) {
    requireDistinctValues(options)
    ChipFlow(label, modifier.selectableGroup()) {
        for (option in options) {
            key(option.value) {
                val isSelected = option.value == selected
                val active = enabled && option.enabled
                val source = remember { MutableInteractionSource() }
                ChipSurface(
                    label = option.label,
                    icon = QeetrixIconName.Check.takeIf { isSelected },
                    size = size,
                    selected = isSelected,
                    enabled = active,
                    source = source,
                    // role="radio" aria-checked
                    interaction =
                        Modifier.selectable(
                            selected = isSelected,
                            interactionSource = source,
                            indication = null,
                            enabled = active,
                            role = Role.RadioButton,
                            onClick = { if (!isSelected) onSelect(option.value) },
                        ),
                    modifier = Modifier,
                )
            }
        }
    }
}

/**
 * Chips that choose any number of values from a set — the web's `ChipGroup` with `multiple`: each chip
 * toggles its value in or out of [selected].
 *
 * ```
 * var labels by rememberSaveable { mutableStateOf(setOf<String>()) }
 * QeetrixChipGroup(label = "Labels", selected = labels, onSelectedChange = { labels = it }, options = labelOptions)
 * ```
 *
 * Each chip is a checkbox for accessibility services. Otherwise as the single-choice [QeetrixChipGroup].
 */
@Composable
public fun <T> QeetrixChipGroup(
    label: String,
    selected: Set<T>,
    onSelectedChange: (Set<T>) -> Unit,
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
                    selected = option.value in selected,
                    onSelectedChange = { on -> onSelectedChange(if (on) selected + option.value else selected - option.value) },
                    size = size,
                    enabled = enabled && option.enabled,
                )
            }
        }
    }
}

/** flex flex-wrap gap-2, named by [label]. Each chip reserves a 48 dp row, so rows need no gap of their own. */
@Composable
private fun ChipFlow(
    label: String,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    FlowRow(
        modifier.semantics { contentDescription = label },
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)),
        verticalArrangement = Arrangement.Center,
    ) { content() }
}

/**
 * A selectable chip: the pill in its selected or resting vocabulary, its hover and press steps, the focus ring
 * and the 48 dp touch area, with [interaction] (toggleable or selectable) on the touch area.
 */
@Composable
private fun ChipSurface(
    label: String,
    icon: QeetrixIconName?,
    size: QeetrixChipSize,
    selected: Boolean,
    enabled: Boolean,
    source: MutableInteractionSource,
    modifier: Modifier,
    interaction: Modifier,
) {
    val metrics = ChipMetrics.of(size)
    val colors = QeetrixTheme.colors
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val lit = enabled // disabled:pointer-events-none
    val targetBackground =
        if (selected) {
            // bg-brand-subtle hover:bg-brand-subtle-hover active:bg-brand-subtle-active
            when {
                lit && pressed -> colors.surfaceBrandSubtleActive
                lit && hovered -> colors.surfaceBrandSubtleHover
                else -> colors.surfaceBrandSubtle
            }
        } else {
            // bg-transparent hover:bg-surface-interactive active:bg-surface-interactive-hover
            when {
                lit && pressed -> colors.surfaceInteractiveHover
                lit && hovered -> colors.surfaceInteractive
                else -> colors.surfaceInteractive.copy(alpha = 0f)
            }
        }
    val motion = qeetrixAnimationSpec<Color>(QeetrixEasing.Standard, QeetrixDuration.Fast) // transition-colors duration-fast
    val background by animateColorAsState(targetBackground, motion, label = "chip-background")
    val border by animateColorAsState(if (selected) colors.borderBrand else restingBorder(), motion, label = "chip-border")
    Box(modifier.then(interaction).qeetrixMinimumTouchTarget(), contentAlignment = Alignment.Center) {
        Row(
            Modifier
                .qeetrixFocusRing(source, QeetrixShapes.Pill)
                .then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled))
                .chipFrame(background, border, metrics)
                .padding(horizontal = metrics.paddingX + QeetrixStroke.Hairline),
            verticalAlignment = Alignment.CenterVertically,
        ) { ChipLabel(label, icon, metrics) }
    }
}

/** inline-flex rounded-full border, at least the size's height, growing with the font scale. */
private fun Modifier.chipFrame(
    background: Color,
    border: Color,
    metrics: ChipMetrics,
): Modifier =
    background(background, QeetrixShapes.Pill)
        .border(QeetrixStroke.Hairline, border, QeetrixShapes.Pill)
        .heightIn(min = metrics.height)

/** The leading icon or check mark, then the label on one line: gap-1.5 font-ui font-medium whitespace-nowrap. */
@Composable
private fun ChipLabel(
    label: String,
    icon: QeetrixIconName?,
    metrics: ChipMetrics,
) {
    val color = QeetrixTheme.colors.textPrimary // text-foreground
    Row(horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1.5f)), verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) QeetrixIcon(icon, contentDescription = null, size = metrics.iconSize, tint = color)
        BasicText(
            label,
            style = metrics.textStyle.copy(color = color).labelDirection(),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
        )
    }
}

/** border-border, dark:border-border-strong. */
@Composable
private fun restingBorder(): Color = if (QeetrixTheme.isDark) QeetrixTheme.colors.borderStrong else QeetrixTheme.colors.borderDefault

/** One size's geometry: the web's size classes, resolved. */
private class ChipMetrics(
    val height: Dp,
    val paddingX: Dp,
    val iconSize: Dp,
    val textStyle: TextStyle,
) {
    companion object {
        // h-6 px-2.5 text-xs, icons size-3
        private val Sm =
            ChipMetrics(
                QeetrixWebScale.spacing(6f),
                QeetrixWebScale.spacing(2.5f),
                QeetrixWebScale.spacing(3f),
                qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs, FontWeightMedium),
            )

        // h-7 px-3 text-sm, icons size-3.5
        private val Md =
            ChipMetrics(
                QeetrixWebScale.spacing(7f),
                QeetrixWebScale.spacing(3f),
                QeetrixWebScale.spacing(3.5f),
                qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm, FontWeightMedium),
            )

        // h-8 px-3.5 text-sm, icons size-4
        private val Lg =
            ChipMetrics(
                QeetrixWebScale.spacing(8f),
                QeetrixWebScale.spacing(3.5f),
                QeetrixWebScale.spacing(4f),
                qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm, FontWeightMedium),
            )

        fun of(size: QeetrixChipSize): ChipMetrics =
            when (size) {
                QeetrixChipSize.Sm -> Sm
                QeetrixChipSize.Md -> Md
                QeetrixChipSize.Lg -> Lg
            }
    }
}

/** hover:bg-foreground/10 on the remove button. */
private const val REMOVE_HOVER_ALPHA = 0.1f
