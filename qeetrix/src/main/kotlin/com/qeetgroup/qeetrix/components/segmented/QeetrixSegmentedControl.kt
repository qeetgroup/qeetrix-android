package com.qeetgroup.qeetrix.components.segmented

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.haptics.QeetrixHapticRole
import com.qeetgroup.qeetrix.foundation.haptics.rememberQeetrixHaptics
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * One choice from a few, side by side — `@qeetrix/react`'s ToggleGroup with `variant="segmented"` (toggle.css), as
 * ThemeSwitcher uses it: filters (All, Pending) and short settings (Light, Dark, System).
 *
 * - **Look**: a strip with a 1 dp `color.border.strong` edge and `radius.control` corners, the segments split by 1 dp
 *   rules, each `size.control.*` tall less the edges, `label.md` (`label.sm` at `Sm`), 12 dp inline padding (8 at
 *   `Sm`, 16 at `Lg`). Rest `color.text.subtle`; hover `color.bg.neutral.subtle` with `color.text.default`; chosen
 *   `color.bg.neutral.bold` with `color.text.inverse`; disabled `color.text.disabled`. An option's `icon` (16 dp, sharp)
 *   comes before its label. Colours change in `motion.duration.fast`.
 * - **Touch**: each segment is a 48 dp touch target with the strip drawn at `size.control.*` in its middle; the strip
 *   is as wide as its segments.
 * - **Accessibility**: a group of radio buttons, each read with its label and whether it is chosen; a choice plays
 *   [QeetrixHapticRole.Selection].
 *
 * ```
 * QeetrixSegmentedControl(filter, { filter = it }, listOf(QeetrixOption("all", "All"), QeetrixOption("pending", "Pending")))
 * ```
 */
@Composable
public fun <T> QeetrixSegmentedControl(
    value: T,
    onValueChange: (T) -> Unit,
    options: List<QeetrixOption<T>>,
    modifier: Modifier = Modifier,
    size: QeetrixButtonSize = QeetrixButtonSize.Md,
    enabled: Boolean = true,
) {
    val colors = QeetrixTheme.colors
    val density = QeetrixTheme.density
    val haptics = rememberQeetrixHaptics()
    val shape = QeetrixShapes.Control
    val control =
        when (size) {
            QeetrixButtonSize.Sm -> density.sizeControlSm
            QeetrixButtonSize.Md -> density.sizeControlMd
            QeetrixButtonSize.Lg -> density.sizeControlLg
        }
    val padding =
        when (size) {
            QeetrixButtonSize.Sm -> 8.dp
            QeetrixButtonSize.Md -> 12.dp
            QeetrixButtonSize.Lg -> 16.dp
        }
    val label = if (size == QeetrixButtonSize.Sm) QeetrixTheme.typography.labelSm else QeetrixTheme.typography.labelMd
    // The drawn strip: size.control.* tall (taller only if the label's line is), centred in each segment's 48 dp target.
    val band = maxOf(control, firstLineHeight(label))
    val edge = colors.borderStrong
    Row(
        modifier
            .height(IntrinsicSize.Min)
            .selectableGroup()
            .drawWithContent {
                drawContent()
                // border: border.width.default solid border.strong; border-radius: radius.control
                val bandPx = band.toPx()
                val width = QeetrixBorder.WidthDefault.toPx()
                val top = (this.size.height - bandPx) / 2
                drawRoundRect(
                    edge,
                    topLeft = Offset(width / 2, top + width / 2),
                    size = Size(this.size.width - width, bandPx - width),
                    cornerRadius = CornerRadius(QeetrixRadius.Control.toPx()),
                    style = Stroke(width),
                )
            },
    ) {
        options.forEachIndexed { index, option ->
            val chosen = option.value == value
            val active = enabled && option.enabled
            val source = remember { MutableInteractionSource() }
            val hovered by source.collectIsHoveredAsState()
            val motion = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
            val fill by animateColorAsState(
                when {
                    chosen -> colors.bgNeutralBold
                    hovered && active -> colors.bgNeutralSubtle
                    else -> Color.Transparent
                },
                motion,
                label = "segment-fill",
            )
            val content =
                when {
                    !active -> colors.textDisabled
                    chosen -> colors.textInverse
                    hovered -> colors.textDefault
                    else -> colors.textSubtle
                }
            // The end segments take the strip's rounded corners; the others are square.
            val corner = QeetrixRadius.Control
            val shape =
                when {
                    options.size == 1 -> RoundedCornerShape(corner)
                    index == 0 -> RoundedCornerShape(topStart = corner, bottomStart = corner)
                    index == options.lastIndex -> RoundedCornerShape(topEnd = corner, bottomEnd = corner)
                    else -> RoundedCornerShape(0.dp)
                }
            Row(
                Modifier
                    .fillMaxHeight()
                    .heightIn(min = QeetrixAccessibility.MinimumTouchTarget)
                    .widthIn(min = QeetrixAccessibility.MinimumTouchTarget)
                    .selectable(chosen, interactionSource = source, indication = null, enabled = active, role = Role.RadioButton) {
                        if (!chosen) {
                            haptics.perform(QeetrixHapticRole.Selection)
                            onValueChange(option.value)
                        }
                    }.drawBehind {
                        val bandPx = band.toPx()
                        val top = (this.size.height - bandPx) / 2
                        val outline = shape.createOutline(Size(this.size.width, bandPx), layoutDirection, this)
                        translate(top = top) { drawOutline(outline, fill) }
                        if (index > 0) {
                            // > .qx-toggle + .qx-toggle: border-inline-start border.width.default border.strong
                            val width = QeetrixBorder.WidthDefault.toPx()
                            val x = if (layoutDirection == LayoutDirection.Rtl) this.size.width - width else 0f
                            drawRect(edge, Offset(x, top), Size(width, bandPx))
                        }
                    }.focusBand(source, band)
                    .padding(horizontal = padding),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (option.icon != null) {
                    QeetrixIcon(
                        option.icon,
                        contentDescription = null,
                        size = QeetrixSize.IconSm,
                        shape = QeetrixIconShape.Sharp,
                        tint = content,
                    )
                }
                BasicText(option.label, style = label.copy(color = content).labelDirection(), maxLines = 1, softWrap = false)
            }
        }
    }
}

/** Keyboard focus: the 2 dp Ember frame inside the segment's drawn band (`.qx-toggle:focus-visible`, offset inward). */
@Composable
private fun Modifier.focusBand(
    source: MutableInteractionSource,
    band: androidx.compose.ui.unit.Dp,
): Modifier {
    val focused by source.collectIsFocusedAsState()
    val frame = QeetrixTheme.colors.borderFocus
    return this.drawWithContent {
        drawContent()
        if (!focused) return@drawWithContent
        val width = QeetrixFocus.RingWidth.toPx()
        val bandPx = band.toPx()
        val top = (this.size.height - bandPx) / 2
        drawRect(frame, Offset(width / 2, top + width / 2), Size(this.size.width - width, bandPx - width), style = Stroke(width))
    }
}
