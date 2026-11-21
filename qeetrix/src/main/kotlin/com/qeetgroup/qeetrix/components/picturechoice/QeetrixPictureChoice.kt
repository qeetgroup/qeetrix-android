package com.qeetgroup.qeetrix.components.picturechoice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** A picture's frame (`value` is `@qeetrix/react`'s PictureChoiceGroup `shape`). */
public enum class QeetrixPictureChoiceShape(
    public val value: String,
) {
    /** A circle, for people: avatars. The default. */
    Round("round"),

    /** `radius.control` corners, for anything else: backgrounds, stickers. */
    Square("square"),
}

/** A picture's size (`value` is `@qeetrix/react`'s PictureChoiceGroup `size`). */
public enum class QeetrixPictureChoiceSize(
    public val value: String,
    internal val side: Dp,
) {
    /**
     * 36 dp, for a row of colour swatches; its tick shrinks to a 16 dp disc so it marks the corner without covering the
     * swatch. A tap within 48 dp of it still chooses it, through Compose's touch-target expansion.
     */
    Sm("sm", 36.dp),

    /** 48 dp. The default. */
    Md("md", 48.dp),

    /** 64 dp. */
    Lg("lg", 64.dp),
}

/**
 * One choice among pictures — an avatar, a background tone, a sticker: `@qeetrix/react`'s PictureChoiceGroup with its
 * PictureChoices (picture-choice.css), a radio group whose options are pictures.
 *
 * - **Each option** is a [QeetrixOption]: its `value`, its `label` — the picture's name, which accessibility
 *   services read ("Fox", "Teal"), required as a picture alone has no name — and `enabled`. [picture] draws it: an
 *   image, an avatar, a swatch (`Modifier.fillMaxSize().background(tone)`), filling the frame, which clips it to
 *   [shape] at [size] (36, 48 or 64 dp) with a 1 dp `color.border.subtle` rule inside the edge, so a white swatch or a pale picture
 *   shows on a white surface.
 * - **Chosen**: a 2 dp `color.border.brand` ring set off from the picture by 2 dp of `color.surface.default`, and a
 *   tick on a 20 dp `color.bg.brand.bold` disc at the top end corner, ringed in the surface, so the choice is never
 *   told by colour alone. Keyboard focus draws the 2 dp Ember frame outside both. Disabled options are dimmed to half.
 * - **Layout**: a row `space.gap.md` apart that wraps, or a grid of [columns] (`space.gap.md` both ways).
 * - **Accessibility**: one selectable group named by [contentDescription] (not drawn, as react's `aria-label`); each
 *   option is a radio button read by its label with "selected" or "not selected", and a 48 dp target at every size
 *   (at `Sm` through touch-target expansion).
 *   The pictures themselves are decoration.
 *
 * ```
 * var avatar by rememberSaveable { mutableStateOf("fox") }
 * QeetrixPictureChoiceGroup(avatar, { avatar = it }, avatars.map { QeetrixOption(it.id, it.name) },
 *     contentDescription = "Avatar", columns = 4) { option -> Image(painterFor(option.value), null) }
 * ```
 */
@Composable
public fun <T> QeetrixPictureChoiceGroup(
    value: T,
    onValueChange: (T) -> Unit,
    options: List<QeetrixOption<T>>,
    contentDescription: String,
    modifier: Modifier = Modifier,
    columns: Int? = null,
    shape: QeetrixPictureChoiceShape = QeetrixPictureChoiceShape.Round,
    size: QeetrixPictureChoiceSize = QeetrixPictureChoiceSize.Md,
    enabled: Boolean = true,
    picture: @Composable (option: QeetrixOption<T>) -> Unit,
) {
    requireDistinctValues(options)
    require(columns == null || columns > 0) { "columns ($columns) must be at least 1." }
    val frame = if (shape == QeetrixPictureChoiceShape.Round) CircleShape else RoundedCornerShape(QeetrixRadius.Control)
    val group = modifier.semantics { this.contentDescription = contentDescription }.selectableGroup()
    val items: @Composable () -> Unit = {
        for (option in options) {
            key(option.value) {
                PictureChoice(
                    selected = option.value == value,
                    enabled = enabled && option.enabled,
                    label = option.label,
                    frame = frame,
                    shape = shape,
                    side = size.side,
                    small = size == QeetrixPictureChoiceSize.Sm,
                    onSelect = { if (option.value != value) onValueChange(option.value) },
                ) { picture(option) }
            }
        }
    }
    if (columns == null) {
        // display: flex; flex-wrap: wrap; gap: space.gap.md
        FlowRow(
            group,
            horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
            verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
        ) { items() }
    } else {
        // [data-layout="grid"]: grid-template-columns repeat(columns, max-content); gap: space.gap.md
        Layout(items, group) { measurables, constraints ->
            val gap = QeetrixSpace.GapMd.roundToPx()
            val cell = size.side.roundToPx()
            val placeables = measurables.map { it.measure(Constraints.fixed(cell, cell)) }
            val rows = (placeables.size + columns - 1) / columns
            val across = minOf(columns, placeables.size)
            val width = across * cell + (across - 1).coerceAtLeast(0) * gap
            val height = rows * cell + (rows - 1).coerceAtLeast(0) * gap
            layout(width, height) {
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeRelative((index % columns) * (cell + gap), (index / columns) * (cell + gap))
                }
            }
        }
    }
}

/** `.qx-picture-choice` with its picture, ring, tick and focus frame. */
@Composable
private fun PictureChoice(
    selected: Boolean,
    enabled: Boolean,
    label: String,
    frame: Shape,
    shape: QeetrixPictureChoiceShape,
    side: Dp,
    small: Boolean,
    onSelect: () -> Unit,
    picture: @Composable () -> Unit,
) {
    val colors = QeetrixTheme.colors
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val surface = colors.surfaceDefault
    val ring = colors.borderBrand
    val focus = colors.borderFocus
    val strong = QeetrixBorder.WidthStrong
    Box(
        Modifier
            .size(side)
            .selectable(selected, source, indication = null, enabled = enabled, role = Role.RadioButton, onClick = onSelect)
            .semantics { this.contentDescription = label }
            // [data-disabled] { opacity: 0.5 }
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            // Behind the picture and the tick, which the ring passes under at the corner.
            .drawBehind {
                // [data-checked]: 0 0 0 border.width.strong surface.default, then 0 0 0 2 × border.width.strong
                // border.brand — a ring set off from the picture by the surface.
                val width = strong.toPx()
                if (selected) {
                    outsideRing(frame, shape, width / 2, width, surface)
                    outsideRing(frame, shape, width * 1.5f, width, ring)
                }
                // :focus-visible: focus.ring.width border.focus, offset 2 × border.width.strong + focus.ring.offset
                if (focused) {
                    val offset = 2 * width + QeetrixFocus.RingOffset.toPx()
                    outsideRing(frame, shape, offset + QeetrixFocus.RingWidth.toPx() / 2, QeetrixFocus.RingWidth.toPx(), focus)
                }
            },
    ) {
        // The picture fills the frame, clipped to it, on bg.neutral.subtle while it loads, and is decoration: the option
        // is named by its label. A 1 dp inner rule over it.
        Box(
            Modifier
                .matchParentSize()
                .clip(frame)
                .background(colors.bgNeutralSubtle, frame)
                .clearAndSetSemantics {},
        ) { picture() }
        Box(Modifier.matchParentSize().border(QeetrixBorder.WidthDefault, colors.borderSubtle, frame))
        if (selected) Tick(Modifier.align(Alignment.TopEnd), small)
    }
}

/**
 * `.qx-picture-choice-tick`: a 20 dp `color.bg.brand.bold` disc 4 dp past the top end corner, ringed in
 * `border.width.strong` of `color.surface.default`, with a sharp 12 dp check, stroke 3, in `color.text.on-brand`; at
 * `sm` (`[data-size="sm"] .qx-picture-choice-tick`) a 16 dp disc 3 dp past the corner with a 10 dp check.
 */
@Composable
private fun Tick(
    modifier: Modifier,
    small: Boolean,
) {
    val colors = QeetrixTheme.colors
    val ring = QeetrixBorder.WidthStrong
    val out = if (small) SMALL_TICK_OUT else TICK_OUT
    Box(
        modifier
            // inset-block-start: -4px; inset-inline-end: -4px — offset follows the layout direction.
            .offset(x = out, y = -out)
            .size(if (small) SMALL_TICK else TICK)
            .drawWithContent {
                val grow = ring.toPx()
                drawCircle(colors.surfaceDefault, radius = size.minDimension / 2 + grow)
                drawContent()
            }.background(colors.bgBrandBold, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        QeetrixIcon(
            QeetrixIconName.Check,
            contentDescription = null,
            size = if (small) SMALL_TICK_ICON else TICK_ICON,
            stroke = TICK_STROKE,
            shape = QeetrixIconShape.Sharp,
            tint = colors.textOnBrand,
        )
    }
}

/** A [width] stroke of [frame] grown by [out] beyond the option's edge, centred on that line. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.outsideRing(
    frame: Shape,
    shape: QeetrixPictureChoiceShape,
    out: Float,
    width: Float,
    color: Color,
) {
    val grown = Size(size.width + 2 * out, size.height + 2 * out)
    // A box-shadow's spread grows the corner radius with it.
    val outline =
        if (shape == QeetrixPictureChoiceShape.Round) {
            frame.createOutline(grown, layoutDirection, this)
        } else {
            RoundedCornerShape(QeetrixRadius.Control + out.toDp()).createOutline(grown, layoutDirection, this)
        }
    translate(-out, -out) { drawOutline(outline, color, style = Stroke(width)) }
}

/** `[data-disabled] { opacity: 0.5 }`. */
private const val DISABLED_ALPHA = 0.5f

/** `.qx-picture-choice-tick { inline-size: 20px; block-size: 20px }`. */
private val TICK = 20.dp

/** `inset-block-start: -4px; inset-inline-end: -4px`. */
private val TICK_OUT = 4.dp

/** `[data-size="sm"] .qx-picture-choice-tick`: 16px, -3px, the check 10px. */
private val SMALL_TICK = 16.dp
private val SMALL_TICK_OUT = 3.dp
private val SMALL_TICK_ICON = 10.dp

/** `<CheckIcon size={12} strokeWidth={3} />`. */
private val TICK_ICON = 12.dp
private const val TICK_STROKE = 3f
