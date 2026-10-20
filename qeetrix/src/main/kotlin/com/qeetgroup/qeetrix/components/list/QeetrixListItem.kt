package com.qeetgroup.qeetrix.components.list

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A row of a collection — a file, a member, a setting, a device — with a [title] and, optionally, a
 * [description], [metadata], a [leading] element and a [trailing] element.
 *
 * The web has no list row (a web screen uses a table or a listbox where a phone uses a list), so this one
 * is built from web tokens and compositions, as qeetrix-ios's is: the notification row's tile and type,
 * the table's selected-row colours, and the density's row height and cell padding.
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
 *             QeetrixBadge("Shared", variant = QeetrixBadgeVariant.Secondary)
 *         }
 *     }
 * }
 * ```
 *
 * - **The app owns the data and its identity.** Pass each row its values, and give a lazy list your
 *   items' own stable `key`, so a row keeps its state and its place when items are inserted, removed or
 *   reordered. The row fetches, sorts and filters nothing.
 * - **Interaction.** With [onClick] the row is one clickable element: a press tints it edge to edge and
 *   keyboard focus draws an inset ring. [onLongClick] adds a long press — to start a selection or open a
 *   [com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu] — and needs [onClick]. Without either the
 *   row is static. [enabled] `false` fades the row and ignores both.
 * - **Selection is the app's.** [selected] tints the row with the brand and draws a 2 dp brand edge at its
 *   start, so selection is a shape as well as a colour — the web table's selected row — and accessibility
 *   services read the row as selected. For a multiple selection, put a
 *   [com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox] with `onCheckedChange = null` in [leading] and
 *   toggle in [onClick]: the row is the control, and it is read with the box's state.
 * - **Accessibility.** The title, description, metadata and any text in the slots are read as one element;
 *   a control in [trailing] (an icon button, a switch) stays its own focusable element. Give a leading
 *   avatar `contentDescription = null` when the title already names the person, so it isn't read twice. For a setting that
 *   is only a switch, use [com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch] with its label as
 *   the row instead: the whole row is then the switch.
 * - **Size.** Padding is 16 dp at the sides and the density's cell padding above and below; the row is at
 *   least the density's row height and 48 dp. Text follows the font scale and wraps; from 150 % the
 *   [trailing] element moves below the text instead of squeezing it.
 *
 * Outside a lazy list — in a card, a sheet — stack rows in a `Column` with
 * [com.qeetgroup.qeetrix.components.separator.QeetrixSeparator]s between them if they need rules.
 *
 * @param description A second line, muted.
 * @param metadata A short note at the end of the title line — a time, a size, a count — in tabular figures.
 * @param onLongClickLabel What a long press does, for accessibility services ("Select").
 * @param leading Before the text: a [QeetrixListItemIcon], a
 *   [com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar], a checkbox.
 * @param trailing After the text: a badge, a value, an icon button.
 */
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
) {
    require(onLongClick == null || onClick != null) { "A list item with onLongClick needs an onClick too." }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val interactive = onClick != null && enabled
    val colors = QeetrixTheme.colors
    val target =
        when {
            selected && interactive && pressed -> colors.surfaceBrandSubtleActive

            selected && interactive && hovered -> colors.surfaceBrandSubtleHover

            selected -> colors.surfaceBrandSubtle

            // row-background-selected
            interactive && pressed -> colors.surfaceInteractiveHover

            interactive && hovered -> colors.surfaceInteractive

            // hover:bg-surface-interactive
            else -> Color.Transparent
        }
    val background by animateColorAsState(target, qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast), label = "list-item")
    val edge = colors.borderBrand
    val density = QeetrixTheme.density
    val trailingBelow = LocalDensity.current.fontScale >= LARGE_FONT_SCALE

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
            .qeetrixFocusRing(source, RectangleShape, offset = -QeetrixFocus.OutlineWidth) // focus-visible:focus-ring-inset
            .drawBehind {
                drawRect(background)
                if (selected) {
                    // row-indicator: a 2 dp brand edge at the inline start.
                    val width = QeetrixStroke.Thin.toPx()
                    val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                    drawRect(edge, Offset(x, 0f), Size(width, size.height))
                }
            }.then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled))
            .fillMaxWidth()
            .heightIn(min = maxOf(density.rowHeight, QeetrixAccessibility.MinimumTouchTarget))
            .padding(horizontal = HorizontalPadding, vertical = density.cellPaddingY),
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(3f)), // gap-3
        verticalAlignment = if (trailingBelow) Alignment.Top else Alignment.CenterVertically,
    ) {
        leading?.invoke()
        if (trailingBelow && trailing != null) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f))) {
                ListItemText(title, description, metadata)
                trailing()
            }
        } else {
            Box(Modifier.weight(1f)) { ListItemText(title, description, metadata) }
            trailing?.invoke()
        }
    }
}

/**
 * A row's 32 dp icon tile, for the [QeetrixListItem]'s `leading` slot: the icon, muted, on the sunken
 * surface with a hairline edge — as on the web's notification row and empty state. Decorative: the row's
 * text says what it is.
 */
@Composable
public fun QeetrixListItemIcon(
    icon: QeetrixIconName,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(QeetrixCorner.Chip) // rounded-md
    Box(
        modifier
            .size(QeetrixWebScale.spacing(8f)) // size-8
            .background(QeetrixTheme.colors.surfaceSunken, shape)
            .border(QeetrixStroke.Hairline, QeetrixTheme.colors.borderSubtle, shape),
        contentAlignment = Alignment.Center,
    ) {
        // [&>svg]:size-4 text-muted-foreground
        QeetrixIcon(icon, contentDescription = null, size = QeetrixWebScale.spacing(4f), tint = QeetrixTheme.colors.textTertiary)
    }
}

/**
 * The title with the metadata at the end of its line, on the same baseline, and the description below.
 * Laid out by hand so the description comes before the metadata in reading order — what it is, then the
 * note — while the metadata is drawn on the title line. Accessibility services read children in the order
 * they are placed.
 */
@Composable
private fun ListItemText(
    title: String,
    description: String?,
    metadata: String?,
) {
    val colors = QeetrixTheme.colors
    Layout(
        content = {
            BasicText(title, style = TitleStyle.copy(color = colors.textPrimary))
            if (description != null) BasicText(description, style = DescriptionStyle.copy(color = colors.textTertiary))
            if (metadata != null) {
                // shrink-0 whitespace-nowrap tabular-nums
                BasicText(
                    metadata,
                    style = MetadataStyle.copy(color = colors.textTertiary).labelDirection(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val gap = QeetrixWebScale.spacing(2f).roundToPx() // gap-2
        val lineGap = QeetrixWebScale.spacing(0.5f).roundToPx() // gap-0.5
        val meta = if (metadata != null) measurables.last().measure(Constraints(maxWidth = width / 2)) else null
        val titleWidth = (width - (meta?.let { it.width + gap } ?: 0)).coerceAtLeast(0)
        // The texts fill their width rather than their content: content-width text in a right-to-left paragraph
        // can wrap its last word.
        val titlePlaceable = measurables[0].measure(Constraints.fixedWidth(titleWidth))
        val descriptionPlaceable = if (description != null) measurables[1].measure(Constraints.fixedWidth(width)) else null
        // The metadata sits on the title's first baseline.
        val metaY = if (meta != null) titlePlaceable[FirstBaseline] - meta[FirstBaseline] else 0
        val top = minOf(0, metaY)
        val lineHeight = maxOf(titlePlaceable.height, (meta?.height ?: 0) + metaY) - top
        val height = lineHeight + (descriptionPlaceable?.let { lineGap + it.height } ?: 0)
        // Placed in reading order, which accessibility services follow: title, description, metadata.
        layout(width, height) {
            titlePlaceable.placeRelative(0, -top)
            descriptionPlaceable?.placeRelative(0, lineHeight + lineGap)
            meta?.placeRelative(width - meta.width, metaY - top)
        }
    }
}

/** The row's inline padding. */
internal val HorizontalPadding = QeetrixWebScale.spacing(4f)

/** The font scale from which a row's trailing element moves below its text, as at iOS's accessibility sizes. */
internal const val LARGE_FONT_SCALE = 1.5f

// text-sm font-medium text-foreground: the notification title.
private val TitleStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm, FontWeightMedium)

// text-sm text-muted-foreground
internal val DescriptionStyle = qeetrixTextStyle(QeetrixFont.Sans, QeetrixWebScale.TextSm)

// text-xs text-muted-foreground tabular-nums: the notification time.
private val MetadataStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs).copy(fontFeatureSettings = "tnum")
