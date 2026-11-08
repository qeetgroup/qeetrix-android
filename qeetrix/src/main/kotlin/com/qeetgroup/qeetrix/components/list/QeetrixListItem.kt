package com.qeetgroup.qeetrix.components.list

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
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
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import java.text.BreakIterator

/**
 * A row of a collection — a file, a member, a setting, a device — with a [title] and, optionally, a [description],
 * [metadata], a [leading] element and a [trailing] element.
 *
 * `@qeetrix/react` has no list row (a web screen uses a Table where a phone uses a list), so this row is Android's
 * own, drawn by the Table's row rules (design rules §15): at least the density's `size.row` (and 48 dp) tall,
 * `space.gap.xs` above and below, a neutral tint (`color.bg.neutral.subtle`) under the pointer and the Ember tint
 * (`color.bg.selected`) when selected. Its inline padding is the phone's page gutter, `space.inset.lg`.
 *
 * ```
 * LazyColumn {
 *     items(files, key = { it.id }) { file ->
 *         QeetrixListItem(
 *             title = file.name,
 *             description = file.owner,
 *             metadata = file.size,
 *             onClick = { open(file.id) },
 *             leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
 *         ) {
 *             QeetrixBadge("Shared")
 *         }
 *     }
 * }
 * ```
 *
 * - **The app owns the data and its identity.** Pass each row its values, and give a lazy list your items' own
 *   stable `key`, so a row keeps its state and its place when items are inserted, removed or reordered.
 * - **Interaction.** With [onClick] the row is one clickable element: a press steps its tint, and keyboard focus
 *   draws the 2 dp Ember frame inside the row. [onLongClick] adds a long press (to start a selection, or open a
 *   [com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu]) and needs [onClick]. [enabled] `false` takes the
 *   disabled text colour and ignores both.
 * - **Selection is the app's.** [selected] is the Ember tint, `color.bg.selected` — "chosen for an action", not the
 *   Ember bar, which marks the current item (§11) — and accessibility services read the row as selected. So the
 *   selection is not colour alone, give a multiple selection a
 *   [com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox] (with `onCheckedChange = null`) in [leading] and
 *   toggle in [onClick]: the row is then the control, read with the box's state.
 * - **Accessibility.** The title, description, metadata and any text in the slots are read as one element; a control
 *   in [trailing] (an icon button, a switch) stays its own focusable element. Give a leading avatar
 *   `decorative = true` when the title already names the person, so it isn't read twice.
 * - **Text** follows the font scale and wraps; from 150 % the [trailing] element moves below the text instead of
 *   squeezing it.
 *
 * Rules between rows are the app's: a lazy list's rows each draw their own, so put a
 * [com.qeetgroup.qeetrix.components.separator.QeetrixSeparator] between them, as the Table does
 * (`color.border.subtle`).
 *
 * @param description A second line, `body.sm` in `color.text.subtle`.
 * @param metadata A short note at the end of the title line — a time, a size, a count — in tabular figures.
 * @param onLongClickLabel What a long press does, for accessibility services ("Select").
 * @param content Under the description, `space.gap.xs` below it: an authenticator's
 *   [com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPCode], a progress bar. A control there stays its own
 *   focusable element, as in [trailing].
 * @param leading Before the text: a [QeetrixListItemIcon] (an icon, or one or two characters), a
 *   [com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar], a checkbox.
 * @param trailing After the text: a badge, a value, an icon button.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun QeetrixListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    metadata: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    interactionSource: MutableInteractionSource? = null,
    content: (@Composable () -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    require(onLongClick == null || onClick != null) { "A list item with onLongClick needs an onClick too." }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val interactive = onClick != null && enabled
    val colors = QeetrixTheme.colors
    // .qx-table-row: [data-selected] bg.selected, kept under the pointer; otherwise bg.neutral.subtle on hover. A
    // touch press has no hover, so it takes the next step of each, as a button's press does.
    val target =
        when {
            selected && interactive && pressed -> colors.bgSelectedHover
            selected -> colors.bgSelected
            interactive && pressed -> colors.bgNeutralSubtleHover
            interactive && hovered -> colors.bgNeutralSubtle
            else -> Color.Transparent
        }
    val background by animateColorAsState(
        target,
        qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast),
        label = "list-item",
    )
    val trailingBelow = LocalDensity.current.fontScale >= LARGE_FONT_SCALE
    val contentColor = if (enabled) colors.textDefault else colors.textDisabled

    Row(
        modifier
            .then(
                if (onClick != null) {
                    Modifier.combinedClickable(
                        interactionSource = source,
                        indication = null,
                        enabled = enabled,
                        onLongClickLabel = onLongClickLabel,
                        onLongClick = onLongClick,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ).semantics(mergeDescendants = true) { if (selected) this.selected = true }
            .qeetrixFocusFrame(source, RectangleShape, FocusFrame.Inside) // table rows: the frame inside the row
            .drawBehind { drawRect(background) }
            .fillMaxWidth()
            // block-size: size.row, and Android's 48 dp touch target
            .heightIn(min = maxOf(QeetrixTheme.density.sizeRow, QeetrixAccessibility.MinimumTouchTarget))
            .padding(horizontal = HorizontalPadding, vertical = QeetrixSpace.GapXs), // padding-block: space.gap.xs
        horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
        verticalAlignment = if (trailingBelow) Alignment.Top else Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalQeetrixContentColor provides contentColor, LocalListItemEnabled provides enabled) {
            leading?.invoke()
            if (trailingBelow && trailing != null) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm)) {
                    TextAndContent(title, description, metadata, enabled, content)
                    trailing()
                }
            } else {
                Box(Modifier.weight(1f)) { TextAndContent(title, description, metadata, enabled, content) }
                trailing?.invoke()
            }
        }
    }
}

/** 0.1.0's signature without `content`, kept so apps compiled against it still link. */
@Deprecated("Kept for binary compatibility.", level = DeprecationLevel.HIDDEN)
@Composable
public fun QeetrixListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    metadata: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
    interactionSource: MutableInteractionSource? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
): Unit =
    QeetrixListItem(
        title = title,
        modifier = modifier,
        description = description,
        metadata = metadata,
        selected = selected,
        enabled = enabled,
        onClick = onClick,
        onLongClick = onLongClick,
        onLongClickLabel = onLongClickLabel,
        interactionSource = interactionSource,
        content = null,
        leading = leading,
        trailing = trailing,
    )

/** The row's text, and the app's [content] `space.gap.xs` under it. */
@Composable
private fun TextAndContent(
    title: String,
    description: String?,
    metadata: String?,
    enabled: Boolean,
    content: (@Composable () -> Unit)?,
) {
    if (content == null) {
        ListItemText(title, description, metadata, enabled)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
            ListItemText(title, description, metadata, enabled)
            content()
        }
    }
}

/**
 * A row's 32 dp icon tile, for the [QeetrixListItem]'s `leading` slot — a `md` avatar's size, so a list of files
 * and a list of people line up. It is drawn as the Timeline's marker is: `color.bg.neutral.subtle` with a 1 dp
 * `color.border.subtle` edge inside, 4 dp corners (`radius.control`), and a 16 dp sharp icon in
 * `color.icon.subtle` (`color.icon.disabled` in a disabled row). Decorative: the row's text says what it is.
 */
@Composable
public fun QeetrixListItemIcon(
    icon: QeetrixIconName,
    modifier: Modifier = Modifier,
) {
    val colors = QeetrixTheme.colors
    val shape = QeetrixShapes.Control
    Box(
        modifier
            .size(ICON_TILE)
            .background(colors.bgNeutralSubtle, shape)
            .border(QeetrixBorder.WidthDefault, colors.borderSubtle, shape), // box-shadow: inset 0 0 0 1px border.subtle
        contentAlignment = Alignment.Center,
    ) {
        val tint = if (LocalListItemEnabled.current) colors.iconSubtle else colors.iconDisabled
        QeetrixIcon(icon, contentDescription = null, size = QeetrixSize.IconSm, shape = QeetrixIconShape.Sharp, tint = tint)
    }
}

/**
 * A row's tile with one or two characters — an issuer's initial ("G") where it has no logo, a step's number ("2") —
 * for the [QeetrixListItem]'s `leading` slot. Drawn as the icon tile: 32 dp, `color.bg.neutral.subtle` with
 * a 1 dp `color.border.subtle` edge inside and 4 dp corners, the [text] centred in `label.md` at weight 600 in
 * `color.text.subtle` (`color.text.disabled` in a disabled row). A person is an
 * [com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar], which is round; this square tile is for things.
 *
 * - **Two characters at most**, as written: longer text is cut to its first two, a letter with its combining marks
 *   counting as one.
 * - **It grows with the font scale**, `space.gap.xs` around the text, so large text is never clipped.
 * - **Decorative**: hidden from accessibility services, as the row's title already names the item.
 */
@Composable
public fun QeetrixListItemIcon(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = QeetrixTheme.colors
    val shape = QeetrixShapes.Control
    val shown = remember(text) { firstCharacters(text, MAX_TILE_CHARACTERS) }
    val style =
        QeetrixTheme.typography.labelMd.copy(
            color = if (LocalListItemEnabled.current) colors.textSubtle else colors.textDisabled,
            fontWeight = QeetrixTextWeight.Semibold,
            textAlign = TextAlign.Center,
        )
    Layout(
        content = { BasicText(shown, style = style, maxLines = 1, softWrap = false) },
        modifier =
            modifier
                .clearAndSetSemantics {}
                .background(colors.bgNeutralSubtle, shape)
                .border(QeetrixBorder.WidthDefault, colors.borderSubtle, shape),
    ) { measurables, _ ->
        val placeable = measurables.single().measure(Constraints())
        val inset = QeetrixSpace.GapXs.roundToPx() * 2
        val side = maxOf(ICON_TILE.roundToPx(), placeable.width + inset, placeable.height + inset)
        layout(side, side) { placeable.placeRelative((side - placeable.width) / 2, (side - placeable.height) / 2) }
    }
}

/** The first [count] user-perceived characters of [text]: a letter with its combining marks is one. */
internal fun firstCharacters(
    text: String,
    count: Int,
): String {
    val characters = BreakIterator.getCharacterInstance().apply { setText(text) }
    var end = 0
    repeat(count) { characters.next().takeIf { it != BreakIterator.DONE }?.let { end = it } }
    return text.substring(0, end)
}

/**
 * The title with the metadata at the end of its line, on the same baseline, and the description below. Laid out by
 * hand so the description comes before the metadata in reading order — what it is, then the note — while the
 * metadata is drawn on the title line. Accessibility services read children in the order they are placed.
 */
@Composable
private fun ListItemText(
    title: String,
    description: String?,
    metadata: String?,
    enabled: Boolean,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val subtle = if (enabled) colors.textSubtle else colors.textDisabled
    Layout(
        content = {
            BasicText(
                title,
                style =
                    typography.labelMd
                        .copy(
                            color = if (enabled) colors.textDefault else colors.textDisabled,
                        ).labelDirection(alignToLayout = true),
            )
            if (description !=
                null
            ) {
                BasicText(description, style = typography.bodySm.copy(color = subtle).labelDirection(alignToLayout = true))
            }
            if (metadata != null) {
                // white-space: nowrap; font-variant-numeric: tabular-nums (§5, §15)
                BasicText(
                    metadata,
                    style = typography.bodySm.copy(color = subtle, fontFeatureSettings = "tnum").labelDirection(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val gap = QeetrixSpace.GapSm.roundToPx()
        val meta = if (metadata != null) measurables.last().measure(Constraints(maxWidth = width / 2)) else null
        val titleWidth = (width - (meta?.let { it.width + gap } ?: 0)).coerceAtLeast(0)
        // The texts fill their width rather than their content: content-width text in a right-to-left paragraph can
        // wrap its last word.
        val titlePlaceable = measurables[0].measure(Constraints.fixedWidth(titleWidth))
        val descriptionPlaceable = if (description != null) measurables[1].measure(Constraints.fixedWidth(width)) else null
        // The metadata sits on the title's first baseline.
        val metaY = if (meta != null) titlePlaceable[FirstBaseline] - meta[FirstBaseline] else 0
        val top = minOf(0, metaY)
        val lineHeight = maxOf(titlePlaceable.height, (meta?.height ?: 0) + metaY) - top
        val height = lineHeight + (descriptionPlaceable?.height ?: 0)
        // Placed in reading order, which accessibility services follow: title, description, metadata.
        layout(width, height) {
            titlePlaceable.placeRelative(0, -top)
            descriptionPlaceable?.placeRelative(0, lineHeight)
            meta?.placeRelative(width - meta.width, metaY - top)
        }
    }
}

/** Whether the row around a [QeetrixListItemIcon] is enabled. */
private val LocalListItemEnabled = staticCompositionLocalOf { true }

/** The row's inline padding: the page gutter on a phone (`space.inset.lg`, PageHeader's below 840 dp). */
internal val HorizontalPadding = QeetrixSpace.InsetLg

/** A `md` avatar's side: two lines of a row (`label.md` and `body.sm`) are this tall. */
private val ICON_TILE = 32.dp

/** The most characters a [QeetrixListItemIcon] tile shows. */
private const val MAX_TILE_CHARACTERS = 2

/** The font scale from which a row's trailing element moves below its text, as at iOS's accessibility sizes. */
internal const val LARGE_FONT_SCALE = 1.5f
