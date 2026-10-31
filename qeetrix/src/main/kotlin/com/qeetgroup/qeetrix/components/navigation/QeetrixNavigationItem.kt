package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeSize
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.icons.QeetrixIconVariant
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.QeetrixTextWeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * One top-level destination of a [QeetrixNavigationBar], [QeetrixNavigationRail] or [QeetrixNavigationScaffold] —
 * `@qeetrix/react`'s SidebarItem: the app's own [value] for it (a route, an enum entry), its [label] and [icon], an
 * optional short [badge] ("3", "99+", "New"), and whether it is [enabled].
 *
 * Qeetrix keeps no navigation state: the app passes the current destination's value and moves to the destination
 * when one is chosen. Values must be distinct.
 *
 * @param icon The destination's icon, drawn in the round shape: navigation is the one place Qeetrix uses round
 *   icons (design rules §16). Icons represent destinations, not actions.
 * @param filledWhenCurrent Draw [icon]'s filled form while the destination is current (§11, §16); an icon without
 *   a filled drawing keeps its outline, and the Ember bar marks the item.
 * @param enabled `false` for a destination shown but not available (outside the plan): the disabled colours, no
 *   hover tint, and it can't be chosen; accessibility services still find it.
 */
@Immutable
public class QeetrixNavigationItem<out T>(
    public val value: T,
    public val label: String,
    public val icon: QeetrixIconName,
    public val badge: String? = null,
    public val filledWhenCurrent: Boolean = true,
    public val enabled: Boolean = true,
) {
    override fun equals(other: Any?): Boolean =
        other is QeetrixNavigationItem<*> &&
            value == other.value &&
            label == other.label &&
            icon == other.icon &&
            badge == other.badge &&
            filledWhenCurrent == other.filledWhenCurrent &&
            enabled == other.enabled

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + (badge?.hashCode() ?: 0)
        result = 31 * result + filledWhenCurrent.hashCode()
        result = 31 * result + enabled.hashCode()
        return result
    }

    override fun toString(): String = "QeetrixNavigationItem($value, $label)"
}

/** Which edge of an item faces the content, and so carries the Ember bar. */
internal enum class IndicatorEdge {
    /** A bottom bar's items: the bar runs along the top edge, the one facing the screen. */
    Top,

    /** A rail's items: the bar runs down the inline-start edge, as in the web Sidebar (design rules §11). */
    Start,
}

/**
 * One destination, drawn by the web Sidebar's row rules (`.qx-sidebar-item`) turned into a cell with its icon over
 * its label, as Android's bars and rails stack them:
 *
 * - at rest: the label in `color.text.subtle`, the round icon in `color.icon.subtle`;
 * - under the pointer `color.overlay.hover`, pressed `color.overlay.pressed`, the label `color.text.default`;
 * - current: `color.bg.neutral.subtle-hover`, the label `color.text.default` at weight 600, the icon in
 *   `color.icon.brand` and filled, and the 3 dp Ember bar (`border.width.accent`, `color.border.brand`) on the edge
 *   that faces the content — so the current item never rests on its tint;
 * - disabled: `color.text.disabled` and `color.icon.disabled`, no tint.
 *
 * Keyboard focus draws the 2 dp Ember frame inside the cell. The whole item is one tab for accessibility services,
 * at least 48 dp in each direction.
 */
@Composable
internal fun NavigationItem(
    item: QeetrixNavigationItem<*>,
    current: Boolean,
    onClick: () -> Unit,
    edge: IndicatorEdge,
    modifier: Modifier = Modifier,
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val hovered by source.collectIsHoveredAsState()
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val active = item.enabled
    val spec = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast) // transition fast standard
    val fill by animateColorAsState(
        when {
            current -> colors.bgNeutralSubtleHover

            // [data-current]
            active && pressed -> colors.overlayPressed

            active && hovered -> colors.overlayHover

            // :hover
            else -> Color.Transparent // [data-disabled] background: none
        },
        spec,
        label = "navigation-item-fill",
    )
    val text by animateColorAsState(
        when {
            !active -> colors.textDisabled
            current || pressed || hovered -> colors.textDefault
            else -> colors.textSubtle
        },
        spec,
        label = "navigation-item-text",
    )
    val iconColor =
        when {
            !active -> colors.iconDisabled

            // [data-disabled] > .qx-sidebar-icon
            current -> colors.iconBrand

            // [data-current] > .qx-sidebar-icon
            else -> colors.iconSubtle
        }
    val bar = colors.borderBrand
    Box(
        modifier
            .selectable(
                selected = current,
                enabled = active,
                role = Role.Tab,
                interactionSource = source,
                indication = null,
                onClick = onClick,
            ).qeetrixFocusFrame(source, RectangleShape, FocusFrame.Inside) // outline-offset: −focus.ring.width
            .drawBehind {
                drawRect(fill)
                if (!current) return@drawBehind
                val thickness = QeetrixBorder.WidthAccent.toPx() // .qx-sidebar-item[data-current]::before
                if (edge == IndicatorEdge.Top) {
                    drawRect(bar, Offset.Zero, Size(size.width, thickness))
                } else {
                    val x = if (layoutDirection == LayoutDirection.Rtl) size.width - thickness else 0f // inset-inline-start: 0
                    drawRect(bar, Offset(x, 0f), Size(thickness, size.height))
                }
            }.heightIn(min = QeetrixAccessibility.MinimumTouchTarget),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(horizontal = QeetrixSpace.InsetXs, vertical = QeetrixSpace.InsetSm),
            verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                QeetrixIcon(
                    item.icon,
                    contentDescription = null,
                    size = QeetrixSize.IconMd,
                    shape = QeetrixIconShape.Round,
                    variant = if (current && item.filledWhenCurrent) QeetrixIconVariant.Filled else QeetrixIconVariant.Outline,
                    tint = iconColor,
                )
                if (item.badge != null) QeetrixBadge(item.badge, Modifier.overIconEnd(), size = QeetrixBadgeSize.Sm)
            }
            // A bar's cells share its width, so a label keeps to one line and ends in an ellipsis there (it is read in
            // full); a rail grows to fit its labels instead.
            val singleLine = edge == IndicatorEdge.Top
            val style = if (current) typography.labelSm.copy(fontWeight = QeetrixTextWeight.Semibold) else typography.labelSm
            BasicText(
                item.label,
                style = style.copy(color = text, textAlign = TextAlign.Center).labelDirection(),
                maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip,
            )
        }
    }
}

/**
 * Places a badge over the icon's inline-end corner — starting 12 dp in from the icon's start, 4 dp above it — without
 * taking room, so a long badge ("99+") never moves the icon. Mirrors in right-to-left layouts.
 */
private fun Modifier.overIconEnd(): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0, maxWidth = Constraints.Infinity))
        val inset = BADGE_INSET.roundToPx()
        val rise = BADGE_RISE.roundToPx()
        // This zero-size node sits at the icon's start corner: its left in LTR, its right in RTL.
        val x = if (layoutDirection == LayoutDirection.Rtl) -inset - placeable.width else inset
        layout(0, 0) { placeable.place(x, -rise) }
    }

/** The badge over a 20 dp icon: from 60% of its width, 4 dp above its top edge. */
private val BADGE_INSET = 12.dp
private val BADGE_RISE = 4.dp

/** Values must be distinct, as each is one destination. */
internal fun requireDistinctValues(items: List<QeetrixNavigationItem<*>>) {
    require(items.map { it.value }.toSet().size == items.size) { "Each navigation item needs a distinct value." }
}
