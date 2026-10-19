package com.qeetgroup.qeetrix.components.switchcontrol

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.selectionRow
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSwitchComponentTokens

/** The sizes of [QeetrixSwitch] (`value` is the web name). */
public enum class QeetrixSwitchSize(
    public val value: String,
    internal val trackWidth: Dp,
    internal val trackHeight: Dp,
    internal val thumb: Dp,
) {
    /** A 32 × 18.4 dp track with a 16 dp thumb (`w-8 h-(switch-thumb-size)`, thumb `size-4`). */
    Default("default", QeetrixWebScale.spacing(8f), QeetrixSwitchComponentTokens.ThumbSize, QeetrixWebScale.spacing(4f)),

    /** A 24 × 14 dp track with a 12 dp thumb (`w-6 h-3.5`, thumb `size-3`). */
    Sm("sm", QeetrixWebScale.spacing(6f), QeetrixWebScale.spacing(3.5f), QeetrixWebScale.spacing(3f)),
}

/**
 * An on/off control. The state never rests on hue alone: the thumb travels to the inline end when on
 * (mirrored right to left), and is a light disc on both tracks.
 *
 * It is a native toggle: accessibility services read it as a switch with its on/off state, and double-tap
 * toggles it. Pass `onCheckedChange = null` to show a state the user cannot change. Give it a name: use
 * the overload with a `label`, or put it in a row that is itself `toggleable`.
 *
 * The drawn switch keeps the web's size; a 48 × 48 dp touch area is reserved around it.
 *
 * @param invalid Draws the invalid boundary (`aria-invalid`), for a required choice left unset.
 */
@Composable
public fun QeetrixSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: QeetrixSwitchSize = QeetrixSwitchSize.Default,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction =
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = checked,
                interactionSource = source,
                indication = null,
                enabled = active,
                role = Role.Switch,
                onValueChange = onCheckedChange,
            )
        } else {
            Modifier.semantics { toggleableState = ToggleableState(checked) }
        }
    Box(modifier.then(interaction).qeetrixMinimumTouchTarget(), contentAlignment = Alignment.Center) {
        Track(checked, active, size, invalid, source)
    }
}

/**
 * A switch with its [label] — and an optional [description] — as one control: the whole row toggles,
 * and accessibility services read the label, the description, the switch role and its state together.
 * The label sits at the start and the switch at the end, the Android settings convention.
 */
@Composable
public fun QeetrixSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    size: QeetrixSwitchSize = QeetrixSwitchSize.Default,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interaction =
        if (onCheckedChange != null) {
            Modifier.toggleable(checked, source, indication = null, enabled = active, role = Role.Switch, onValueChange = onCheckedChange)
        } else {
            Modifier.semantics(mergeDescendants = true) { toggleableState = ToggleableState(checked) }
        }
    // Field orientation="horizontal": flex-row gap-2, items-start with a FieldContent.
    Row(
        modifier.selectionRow().then(interaction),
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)),
        verticalAlignment = if (description != null) Alignment.Top else Alignment.CenterVertically,
    ) {
        // FieldContent: flex-1 flex-col gap-0.5
        Column(
            Modifier.weight(1f).then(if (active) Modifier else Modifier.alpha(QeetrixOpacity.Disabled)),
            verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(0.5f)),
        ) {
            // Block paragraphs as wide as the column, as on the web.
            BasicText(label, Modifier.fillMaxWidth(), style = LabelStyle.copy(color = QeetrixTheme.colors.textPrimary))
            if (description != null) {
                BasicText(description, Modifier.fillMaxWidth(), style = DescriptionStyle.copy(color = QeetrixTheme.colors.textTertiary))
            }
        }
        Box(Modifier.qeetrixMinimumTouchTarget(), contentAlignment = Alignment.Center) {
            Track(checked, active, size, invalid, source)
        }
    }
}

@Composable
private fun Track(
    checked: Boolean,
    enabled: Boolean,
    size: QeetrixSwitchSize,
    invalid: Boolean,
    source: MutableInteractionSource,
) {
    val hovered by source.collectIsHoveredAsState()
    val motion = qeetrixAnimationSpec<Color>(QeetrixEasing.Standard, QeetrixDuration.Fast) // duration-fast ease-standard
    val trackColor by animateColorAsState(
        when {
            !checked -> QeetrixSwitchComponentTokens.Track

            // data-unchecked:bg-(switch-track)
            hovered && enabled -> QeetrixSwitchComponentTokens.TrackCheckedHover

            else -> QeetrixSwitchComponentTokens.TrackChecked
        },
        motion,
        label = "switch-track",
    )
    val thumbColor by animateColorAsState(
        if (checked) QeetrixSwitchComponentTokens.ThumbChecked else QeetrixSwitchComponentTokens.Thumb,
        motion,
        label = "switch-thumb",
    )
    // The thumb sits in the content box inside the 1 dp border; checked, it moves translate-x-[calc(100%-2px)].
    val travel = size.thumb - QeetrixStroke.Thin
    val x by animateDpAsState(
        if (checked) travel else Dp(0f),
        qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast),
        label = "switch-x",
    )
    val border = if (invalid) QeetrixInputComponentTokens.BorderInvalid else Color.Transparent // aria-invalid:border-(input-border-invalid)

    Box(
        Modifier
            .qeetrixFocusRing(
                source,
                CircleShape,
                color = if (invalid) QeetrixInputComponentTokens.BorderInvalid else QeetrixTheme.colors.focusRing,
            ).then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled)) // data-disabled:opacity-disabled
            .size(size.trackWidth, size.trackHeight)
            .qeetrixElevation(QeetrixElevation.Flat, CircleShape, trackColor) // rounded-full
            .border(QeetrixStroke.Hairline, border, CircleShape),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                // Read at layout time, not recomposition, while it animates; offset mirrors right to left.
                .offset { IntOffset((QeetrixStroke.Hairline + x).roundToPx(), 0) }
                .size(size.thumb)
                .qeetrixElevation(QeetrixElevation.RampXs, CircleShape, thumbColor), // rounded-full shadow-xs
        )
    }
}

// Label: text-sm leading-snug font-medium. FieldDescription: text-caption text-muted-foreground.
private val LabelStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm.size, QeetrixWebScale.LEADING_SNUG, FontWeightMedium)
private val DescriptionStyle = QeetrixTextRole.Caption.spec.let { qeetrixTextStyle(QeetrixFont.Ui, it.size, it.lineHeight) }
