package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconSize
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSidebarComponentTokens

/**
 * One top-level destination of a [QeetrixNavigationBar], [QeetrixNavigationRail] or
 * [QeetrixNavigationScaffold]: the app's own [value] for it (a route, an enum entry), its [label] and
 * [icon], an optional short [badge] ("3", "99+", "New"), and whether it is [enabled].
 *
 * Qeetrix keeps no navigation state: the app passes which value is selected and moves to the destination
 * when one is chosen. Values must be distinct.
 */
@Immutable
public class QeetrixNavigationItem<out T>(
    public val value: T,
    public val label: String,
    public val icon: QeetrixIconName,
    public val badge: String? = null,
    public val enabled: Boolean = true,
) {
    override fun equals(other: Any?): Boolean =
        other is QeetrixNavigationItem<*> &&
            value == other.value &&
            label == other.label &&
            icon == other.icon &&
            badge == other.badge &&
            enabled == other.enabled

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + icon.hashCode()
        result = 31 * result + (badge?.hashCode() ?: 0)
        result = 31 * result + enabled.hashCode()
        return result
    }

    override fun toString(): String = "QeetrixNavigationItem($value, $label)"
}

/** Which edge of an item faces the content, and so carries the selection indicator. */
internal enum class IndicatorEdge {
    /** A bottom bar's items: the indicator runs along the top edge. */
    Top,

    /** A rail's items: the indicator runs down the inline-start edge, as in the web sidebar. */
    Start,
}

/**
 * One destination, drawn in the web sidebar's navigation signature: a quiet Qeet tint behind the selected
 * item, its label in the medium weight, its icon in the brand colour, and a 3 dp brand indicator on the
 * edge that faces the content — the cue that does not depend on seeing the tint. Hover and press take the
 * neutral interactive fill, so they never read as selection. The whole item is one tab for accessibility
 * services, at least 48 dp in each direction.
 */
@Composable
internal fun NavigationItem(
    item: QeetrixNavigationItem<*>,
    selected: Boolean,
    onClick: () -> Unit,
    edge: IndicatorEdge,
    modifier: Modifier = Modifier,
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val hovered by source.collectIsHoveredAsState()
    val colors = QeetrixTheme.colors
    val shape = RoundedCornerShape(QeetrixCorner.Chip) // rounded-md
    // data-[active]:bg-sidebar-selected; hover:bg-sidebar-accent
    val fill by animateColorAsState(
        when {
            selected -> colors.surfaceBrandSubtle
            item.enabled && (pressed || hovered) -> colors.surfaceInteractive
            else -> Color.Transparent
        },
        qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast),
        label = "navigation-item-fill",
    )
    // before:opacity-0 … data-[active]:before:opacity-100, so a change of selection fades rather than pops
    val indicator by animateFloatAsState(
        if (selected) 1f else 0f,
        qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast),
        label = "navigation-item-indicator",
    )
    val indicatorColor = colors.borderBrand // bg-sidebar-indicator
    Box(
        modifier
            .heightIn(min = QeetrixAccessibility.MinimumTouchTarget)
            .padding(QeetrixWebScale.spacing(1f)) // the tint sits 4 dp inside the item's cell
            .qeetrixFocusRing(source, shape, offset = -QeetrixFocus.OutlineWidth) // focus-visible:focus-ring-inset
            .clip(shape)
            .background(fill)
            .drawBehind {
                if (indicator == 0f) return@drawBehind
                val thickness = QeetrixSidebarComponentTokens.IndicatorWidth.toPx() // w-(--qx-component-sidebar-indicator-width)
                val color = indicatorColor.copy(alpha = indicatorColor.alpha * indicator)
                if (edge == IndicatorEdge.Top) {
                    // inset-x-1/4 along the top edge
                    drawRoundRect(color, Offset(size.width / 4, 0f), Size(size.width / 2, thickness), CornerRadius(thickness / 2))
                } else {
                    // before:inset-y-1/4 before:inset-s-0
                    val x = if (layoutDirection == LayoutDirection.Rtl) size.width - thickness else 0f
                    drawRoundRect(color, Offset(x, size.height / 4), Size(thickness, size.height / 2), CornerRadius(thickness / 2))
                }
            }.selectable(
                selected = selected,
                enabled = item.enabled,
                role = Role.Tab,
                interactionSource = source,
                indication = null,
                onClick = onClick,
            ).then(if (item.enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(horizontal = QeetrixWebScale.spacing(1f), vertical = QeetrixWebScale.spacing(1.5f)),
            verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1f)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                QeetrixIcon(
                    item.icon,
                    contentDescription = null,
                    size = QeetrixIconSize.Lg,
                    tint = if (selected) colors.textBrand else colors.textPrimary, // data-[active]:[&>svg:first-child]:text-brand
                )
                if (item.badge != null) QeetrixBadge(item.badge, Modifier.overIconEnd())
            }
            // A bar's cells share its width, so a label keeps to one line and ends in an ellipsis there (it is read in
            // full); a rail grows to fit its labels instead.
            val singleLine = edge == IndicatorEdge.Top
            BasicText(
                item.label,
                style =
                    (if (selected) SelectedLabelStyle else LabelStyle)
                        .copy(
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                        ).labelDirection(),
                maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip,
            )
        }
    }
}

/**
 * Places a badge over the icon's inline-end side — starting 14 dp in from the icon's start, 2 dp above it —
 * without taking room, so a long badge ("99+") never moves the icon. Mirrors in right-to-left layouts.
 */
private fun Modifier.overIconEnd(): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0, maxWidth = Constraints.Infinity))
        val inset = QeetrixWebScale.spacing(3.5f).roundToPx()
        val rise = QeetrixWebScale.spacing(0.5f).roundToPx()
        // This zero-size node sits at the icon's start corner: its left in LTR, its right in RTL.
        val x = if (layoutDirection == LayoutDirection.Rtl) -inset - placeable.width else inset
        layout(0, 0) { placeable.place(x, -rise) }
    }

/** text-xs, the interface face. */
private val LabelStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs)

/** `data-[active]:font-medium`. */
private val SelectedLabelStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs, FontWeightMedium)
