package com.qeetgroup.qeetrix.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
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
import com.qeetgroup.qeetrix.internal.QeetrixTextWeight
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlin.math.max
import kotlin.math.min

// The parts @qeetrix/react's Checkbox, Radio and Switch share (field/choice.tsx and field.css's .qx-choice rules):
// the row — the control, 8 dp before its label, the whole row the target — its tile form, and the group around a
// question's options.

/** How a choice group lays out its options (`value` is `@qeetrix/react`'s `orientation`). */
public enum class QeetrixChoiceOrientation(
    public val value: String,
) {
    /** Stacked, one option per row. The default. */
    Vertical("vertical"),

    /** In a wrapping row, 16 dp apart, for short options. */
    Horizontal("horizontal"),
}

/** How a choice group's options look (`value` is `@qeetrix/react`'s `variant`). */
public enum class QeetrixChoiceVariant(
    public val value: String,
) {
    /** Rows: the control, then its label. The default. */
    Default("default"),

    /**
     * Tiles: boxes with an optional icon, a title and a line of description, the control at the end and the whole
     * tile the target — for choices that need explaining (an application's type, a plan).
     */
    Tile("tile"),
}

/** Which control a choice row holds: it sets how far in the description starts (`.qx-choice-description`). */
internal enum class ChoiceKind(
    val descriptionIndent: Dp,
) {
    /** `padding-inline-start: 24px`: the 16 dp box and the 8 dp gap. */
    Checkbox(24.dp),

    /** As [Checkbox]. */
    Radio(24.dp),

    /** `.qx-choice[data-slot="switch"] .qx-choice-description { padding-inline-start: 40px }`: the 32 dp track and the gap. */
    Switch(40.dp),
}

/** The look of the options in the enclosing group. */
internal val LocalChoiceVariant = staticCompositionLocalOf { QeetrixChoiceVariant.Default }

/**
 * One option (react's `ChoiceRow`): the control, then — `space.gap.sm` after it — an optional 16 dp icon and the
 * label in `body.md` (`color.text.disabled` when disabled), with an optional description under the label in
 * `body.sm`, indented to start under the label. The control sits on the label's first line. [modifier] carries the
 * toggle or selection, so the whole row is the target: at least 48 × 48 dp on Android (the web's row is
 * `size.target.min`, 24 px). In a tile group the row is drawn as a [ChoiceTile].
 */
@Composable
internal fun ChoiceRow(
    kind: ChoiceKind,
    label: String,
    description: String?,
    icon: QeetrixIconName?,
    enabled: Boolean,
    chosen: Boolean,
    source: MutableInteractionSource,
    modifier: Modifier,
    control: @Composable () -> Unit,
) {
    if (LocalChoiceVariant.current == QeetrixChoiceVariant.Tile) {
        ChoiceTile(label, description, icon, enabled, chosen, source, modifier, control)
        return
    }
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val labelStyle = typography.bodyMd.copy(color = if (enabled) colors.textDefault else colors.textDisabled)
    val line = firstLineHeight(labelStyle)
    // At least 48 dp wide as well: a horizontal group's short label ("Tue") left the row 47 dp (quality finding Q-3).
    Box(
        modifier.sizeIn(minWidth = QeetrixAccessibility.MinimumTouchTarget, minHeight = QeetrixAccessibility.MinimumTouchTarget),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column {
            Row(
                Modifier.padding(vertical = CHOICE_LABEL_PADDING), // .qx-choice-label { padding-block: 2px }
                horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm),
                verticalAlignment = Alignment.Top, // align-items: flex-start; the control's 2px margins centre it on the line
            ) {
                Box(Modifier.height(line), contentAlignment = Alignment.Center) { control() }
                if (icon != null) {
                    Box(Modifier.height(line), contentAlignment = Alignment.Center) {
                        // .qx-choice-icon: 16px in a row, color.icon.subtle
                        QeetrixIcon(
                            icon,
                            contentDescription = null,
                            size = QeetrixSize.IconSm,
                            shape = QeetrixIconShape.Sharp,
                            tint = colors.iconSubtle,
                        )
                    }
                }
                BasicText(label, Modifier.weight(1f, fill = false), style = labelStyle.labelDirection(alignToLayout = true))
            }
            if (description != null) {
                BasicText(
                    description,
                    Modifier.padding(start = kind.descriptionIndent),
                    style =
                        typography.bodySm
                            .copy(
                                color = if (enabled) colors.textSubtle else colors.textSubtlest,
                            ).labelDirection(alignToLayout = true),
                )
            }
        }
    }
}

/**
 * An option as a tile — radios and checkboxes only (07-components.md, Choice tiles; field.css `[data-variant="tile"]`): `color.surface.default`
 * with a 1 dp `color.border.subtle` edge inside it, 4 dp corners, `space.inset.md` padding; hover gives
 * `color.bg.neutral.subtle`; chosen gives `color.bg.selected` with a 2 dp `color.border.brand` edge (hover never
 * covers it); focus draws the frame round the tile; disabled is `color.bg.disabled` with a `color.border.disabled`
 * edge. Inside, `space.gap.md` apart: the icon in a 36 dp box, the title at `title.sm`'s weight, the control at the
 * end. The icon's box hangs below the title's first line instead of setting the row's height, so the description
 * starts under the title's last line.
 */
@Composable
private fun ChoiceTile(
    label: String,
    description: String?,
    icon: QeetrixIconName?,
    enabled: Boolean,
    chosen: Boolean,
    source: MutableInteractionSource,
    modifier: Modifier,
    control: @Composable () -> Unit,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    // A press shows the hover fill: touch has no hover.
    val lit = enabled && !chosen && (hovered || pressed)
    val fill =
        when {
            !enabled -> colors.bgDisabled
            chosen -> colors.bgSelected
            lit -> colors.bgNeutralSubtle
            else -> colors.surfaceDefault
        }
    val motion = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
    val background by animateColorAsState(fill, motion, label = "choice-tile-fill")
    val edge =
        when {
            !enabled -> colors.borderDisabled
            chosen -> colors.borderBrand
            else -> colors.borderSubtle
        }
    val edgeWidth = if (chosen && enabled) QeetrixBorder.WidthStrong else QeetrixBorder.WidthDefault
    val shape = QeetrixShapes.Control
    val titleStyle =
        typography.bodyMd.copy(
            color = if (enabled) colors.textDefault else colors.textDisabled,
            fontWeight = QeetrixTextWeight.Semibold, // .qx-choice-text { font-weight: typography.title.sm-font-weight }
        )
    val line = firstLineHeight(titleStyle)
    Box(
        modifier
            .qeetrixFocusFrame(source, shape, FocusFrame.Outside)
            .background(background, shape)
            .border(edgeWidth, edge, shape) // box-shadow: inset 0 0 0 width colour
            // min-block-size: calc(36px + 2 * space.inset.md) with an icon; a 48 dp target either way
            .heightIn(min = if (icon != null) TILE_ICON_BOX + QeetrixSpace.InsetMd * 2 else QeetrixAccessibility.MinimumTouchTarget)
            .padding(QeetrixSpace.InsetMd),
    ) {
        Column {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd), verticalAlignment = Alignment.Top) {
                if (icon != null) {
                    Box(
                        Modifier
                            .layout { measurable, constraints ->
                                // margin-block-end: calc(1lh - 36px): the box counts as one line of the row.
                                val placeable = measurable.measure(constraints)
                                layout(placeable.width, min(placeable.height, line.roundToPx())) { placeable.place(0, 0) }
                            }.size(TILE_ICON_BOX)
                            .background(if (chosen && enabled) colors.surfaceDefault else colors.bgNeutralSubtle, shape),
                        contentAlignment = Alignment.Center,
                    ) {
                        // 20px in a tile; chosen, color.icon.brand
                        QeetrixIcon(
                            icon,
                            contentDescription = null,
                            size = QeetrixSize.IconMd,
                            shape = QeetrixIconShape.Sharp,
                            tint = if (chosen && enabled) colors.iconBrand else colors.iconSubtle,
                        )
                    }
                }
                BasicText(label, Modifier.weight(1f), style = titleStyle.labelDirection(alignToLayout = true))
                Box(Modifier.height(line), contentAlignment = Alignment.Center) { control() }
            }
            if (description != null) {
                BasicText(
                    description,
                    // padding-inline: 0 calc(16px + gap.md); with an icon, start calc(36px + gap.md)
                    Modifier.padding(
                        start = if (icon != null) TILE_ICON_BOX + QeetrixSpace.GapMd else 0.dp,
                        end = TILE_CONTROL + QeetrixSpace.GapMd,
                    ),
                    style =
                        typography.bodySm
                            .copy(
                                color = if (enabled) colors.textSubtle else colors.textSubtlest,
                            ).labelDirection(alignToLayout = true),
                )
            }
        }
    }
}

/**
 * A question with options (react's `ChoiceGroup`): the question in the field-label style (`label.sm`,
 * `color.text.subtle`) as a heading, the options, then the description or the error — all `space.gap.xs` apart.
 * Options stack ([QeetrixChoiceOrientation.Vertical]), wrap in a row `space.gap.lg` apart
 * ([QeetrixChoiceOrientation.Horizontal]), or fill a grid of tiles ([QeetrixChoiceVariant.Tile]). Each option
 * reads the group's state from [LocalFieldGroup]: enabled, invalid (its border turns `color.border.danger`) and the
 * error, which it carries for accessibility services.
 */
@Composable
internal fun ChoiceGroup(
    label: String,
    description: String?,
    error: String?,
    enabled: Boolean,
    required: Boolean,
    orientation: QeetrixChoiceOrientation,
    variant: QeetrixChoiceVariant,
    columns: Int,
    selectable: Boolean,
    modifier: Modifier,
    options: @Composable () -> Unit,
) {
    require(columns in 1..MAX_COLUMNS) { "columns ($columns) must be between 1 and $MAX_COLUMNS." }
    val group = LocalFieldGroup.current
    val active = enabled && group.enabled
    Column(modifier.semantics { isTraversalGroup = true }, verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
        FieldLabel(label, optional = false, required = required, enabled = active, modifier = Modifier.semantics { heading() })
        val set = if (selectable) Modifier.selectableGroup() else Modifier
        CompositionLocalProvider(
            LocalFieldGroup provides FieldGroup(active, invalid = error != null, error = error),
            LocalChoiceVariant provides variant,
        ) {
            when {
                variant == QeetrixChoiceVariant.Tile -> {
                    TileGrid(columns, set) { options() }
                }

                orientation == QeetrixChoiceOrientation.Horizontal -> {
                    // column-gap: space.gap.lg; flex-flow: wrap. Each row is already a 48 dp target, so lines sit flush.
                    FlowRow(set, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapLg)) { options() }
                }

                else -> {
                    // gap: space.gap.xs between 24px rows on the web; Android's rows are each a 48 dp target, flush.
                    Column(set.fillMaxWidth()) { options() }
                }
            }
        }
        FieldMessages(description, error, active)
    }
}

/**
 * The tile grid: `repeat(auto-fill, minmax(max(12rem, (100% − (columns − 1) × gap) / columns), 1fr))` with
 * `space.gap.sm` gaps — as many columns as fit tiles of at least 192 dp, up to [columns] — and every tile in a row
 * as tall as the row, as grid items stretch.
 */
@Composable
private fun TileGrid(
    columns: Int,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content, modifier.fillMaxWidth()) { measurables, constraints ->
        val gap = QeetrixSpace.GapSm.roundToPx()
        val width = constraints.maxWidth
        val track = max(TILE_MIN_WIDTH.roundToPx(), (width - (columns - 1) * gap) / columns)
        val count = max(1, min(columns, (width + gap) / (track + gap)))
        val cell = (width - (count - 1) * gap) / count
        val rows = measurables.chunked(count)
        val heights = rows.map { row -> row.maxOf { it.maxIntrinsicHeight(cell) } }
        val placeables = rows.mapIndexed { index, row -> row.map { it.measure(Constraints.fixed(cell, heights[index])) } }
        val height = heights.sum() + gap * (rows.size - 1).coerceAtLeast(0)
        layout(width, height) {
            var y = 0
            placeables.forEachIndexed { index, row ->
                row.forEachIndexed { column, placeable -> placeable.placeRelative(column * (cell + gap), y) }
                y += heights[index] + gap
            }
        }
    }
}

/** `.qx-choice-label { padding-block: 2px }`. */
private val CHOICE_LABEL_PADDING = 2.dp

/** `.qx-choice-icon` on a tile: `inline-size: 36px; block-size: 36px`. */
private val TILE_ICON_BOX = 36.dp

/** The control on a tile, `16px` in the description's end padding. */
private val TILE_CONTROL = 16.dp

/** `minmax(max(12rem, …))`: a tile is at least 192 dp wide. */
private val TILE_MIN_WIDTH = 192.dp

/** checkbox.tsx / radio.tsx `columns?: 1 | 2 | 3 | 4`. */
private const val MAX_COLUMNS = 4

/**
 * The edge of a checkbox, a radio's ring or a switch's track (checkbox.css, radio.css and switch.css, in their cascade
 * order): `border.strong`, `border.strong-hover` under the pointer, the Ember `bg.brand.bold` when on (`-hover` under the
 * pointer), then `border.danger` while invalid — on or off, under the pointer too (`[data-invalid]` after the checked
 * and hover rules) — and `border.disabled` when disabled.
 */
internal fun selectionControlEdge(
    colors: QeetrixColors,
    enabled: Boolean,
    on: Boolean,
    invalid: Boolean,
    hover: Boolean,
): Color =
    when {
        !enabled -> colors.borderDisabled
        invalid -> colors.borderDanger
        on -> if (hover) colors.bgBrandBoldHover else colors.bgBrandBold
        hover -> colors.borderStrongHover
        else -> colors.borderStrong
    }
