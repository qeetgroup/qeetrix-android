package com.qeetgroup.qeetrix.components.switchcontrol

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.ChoiceKind
import com.qeetgroup.qeetrix.components.input.ChoiceRow
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.selectionControlEdge
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.haptics.QeetrixHapticRole
import com.qeetgroup.qeetrix.foundation.haptics.QeetrixHaptics
import com.qeetgroup.qeetrix.foundation.haptics.rememberQeetrixHaptics
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/**
 * A setting that turns on or off at once, with its [label] — `@qeetrix/react`'s Switch. A setting that waits for a
 * Save button is a checkbox instead.
 *
 * - **Look** (design rules §9): a 32 × 18 dp track with 2 dp corners and a 12 dp square thumb with 1 dp corners.
 *   Off: `color.bg.input` with a `color.border.strong` edge and a graphite thumb (`color.icon.subtle`) on the start
 *   side. On: the Ember track and edge (`color.bg.brand.bold`) and a white thumb (`color.icon.on-brand`, white in dark
 *   too) on the end side — mirrored right to left — so the state never rests on colour alone. The thumb moves in
 *   `motion.duration.normal`; nothing moves under reduced motion.
 * - **The label follows the switch**, 8 dp after it, and the whole row — with its [description] — is the target,
 *   at least 48 dp tall (design rules §9).
 * - **Felt.** A toggle plays [QeetrixHapticRole.Selection] (nothing where the system has touch feedback off).
 * - **State is yours**: [checked] and [onCheckedChange]; `onCheckedChange = null` or [readOnly] shows a state that
 *   cannot be changed. [invalid] turns the track's edge `color.border.danger`, on or off; [enabled] `false` takes
 *   `color.bg.disabled`, a `color.border.disabled` edge and a `color.icon.disabled` thumb.
 * - **Accessibility.** A native switch, read with its label, description and on/off state; double-tap toggles
 *   it. Keyboard focus draws the 2 dp Ember frame 2 dp outside the track.
 *
 * ```
 * var autoRenew by rememberSaveable { mutableStateOf(true) }
 * QeetrixSwitch(autoRenew, { autoRenew = it }, label = "Auto-renew", description = "Renews on the 1st of each month.")
 * ```
 */
@Composable
public fun QeetrixSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val group = LocalFieldGroup.current
    val active = enabled && group.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interactive = onCheckedChange != null && !readOnly
    val haptics = rememberQeetrixHaptics()
    ChoiceRow(
        kind = ChoiceKind.Switch,
        label = label,
        description = description,
        icon = null,
        enabled = active,
        chosen = checked,
        source = source,
        modifier = modifier.switchToggle(checked, onCheckedChange.takeIf { interactive }?.felt(haptics), active, source, group.error),
    ) {
        SwitchTrack(checked, active, interactive, invalid || group.invalid, source)
    }
}

/**
 * A switch with no visible label — react's Switch with an `aria-label`. Give it a name: put it in a row that is
 * itself `toggleable`, or set a `contentDescription` on [modifier]. The drawn track keeps its 32 × 18 dp; a
 * 48 × 48 dp touch area is reserved around it.
 */
@Composable
public fun QeetrixSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    invalid: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val group = LocalFieldGroup.current
    val active = enabled && group.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val interactive = onCheckedChange != null && !readOnly
    val haptics = rememberQeetrixHaptics()
    Box(
        modifier
            .switchToggle(checked, onCheckedChange.takeIf { interactive }?.felt(haptics), active, source, group.error)
            .qeetrixMinimumTouchTarget(),
        contentAlignment = Alignment.Center,
    ) {
        SwitchTrack(checked, active, interactive, invalid || group.invalid, source)
    }
}

/** A toggle that is felt: [QeetrixHapticRole.Selection], then the app's change. */
private fun ((Boolean) -> Unit).felt(haptics: QeetrixHaptics): (Boolean) -> Unit =
    { checked ->
        haptics.perform(QeetrixHapticRole.Selection)
        this(checked)
    }

private fun Modifier.switchToggle(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean,
    source: MutableInteractionSource,
    error: String?,
): Modifier {
    val toggle =
        if (onCheckedChange != null) {
            toggleable(checked, source, indication = null, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
        } else {
            semantics(mergeDescendants = true) {
                toggleableState = ToggleableState(checked)
                role = Role.Switch
            }
        }
    return toggle.then(if (error != null) Modifier.semantics { error(error) } else Modifier)
}

/**
 * The track and thumb (switch.css, in its cascade order): rest `color.border.strong` on `color.bg.input`; hover
 * `color.border.strong-hover`; on, the track and edge `color.bg.brand.bold` (`-hover` under the pointer); invalid
 * `color.border.danger`, on or off and under the pointer too; disabled `color.bg.disabled` with
 * `color.border.disabled`. The thumb is `color.icon.subtle` off and `color.icon.on-brand` on, white in both
 * schemes as the checkbox's tick (`.qx-switch-thumb[data-checked]`). The thumb sits 2 dp in
 * from the start of the track's inside off (`inset-inline-start: 2px`) and 16 dp in on, centred on its height.
 */
@Composable
private fun SwitchTrack(
    checked: Boolean,
    enabled: Boolean,
    interactive: Boolean,
    invalid: Boolean,
    source: MutableInteractionSource,
) {
    val colors = QeetrixTheme.colors
    val hovered by source.collectIsHoveredAsState()
    val hover = hovered && enabled && interactive // :hover:not([data-disabled], [data-readonly])
    val border = selectionControlEdge(colors, enabled, checked, invalid, hover)
    val fill =
        when {
            !enabled -> colors.bgDisabled
            checked -> if (hover) colors.bgBrandBoldHover else colors.bgBrandBold
            else -> colors.bgInput
        }
    val thumbFill = switchThumb(colors, enabled, checked)
    // Colours: transition background-color, border-color in duration.fast; the thumb's inset in duration.normal.
    val colorMotion = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
    val edge by animateColorAsState(border, colorMotion, label = "switch-border")
    val track by animateColorAsState(fill, colorMotion, label = "switch-track")
    val thumb by animateColorAsState(thumbFill, colorMotion, label = "switch-thumb")
    val inset by animateDpAsState(
        if (checked) THUMB_ON else THUMB_OFF,
        qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal),
        label = "switch-thumb-inset",
    )
    val shape = QeetrixShapes.Sm // border-radius: radius.sm
    Box(
        Modifier
            .qeetrixFocusFrame(source, shape, FocusFrame.Outside)
            .size(TRACK_WIDTH, TRACK_HEIGHT)
            .background(track, shape)
            .border(QeetrixBorder.WidthDefault, edge, shape)
            .padding(QeetrixBorder.WidthDefault), // the thumb is placed in the padding box, inside the 1 dp border
        contentAlignment = Alignment.CenterStart, // inset-block-start: 50%; translate: 0 -50%
    ) {
        Box(
            Modifier
                // Read at layout time while it animates; offset {} mirrors right to left, as inset-inline-start does.
                .offset { IntOffset(inset.roundToPx(), 0) }
                .size(THUMB)
                .background(thumb, ThumbShape),
        )
    }
}

/**
 * `.qx-switch-thumb`: `color.icon.subtle` off, `color.icon.on-brand` on — white in both schemes, as the checkbox's
 * tick on the same Ember (`[data-checked]`) — and `color.icon.disabled` when disabled.
 */
internal fun switchThumb(
    colors: QeetrixColors,
    enabled: Boolean,
    checked: Boolean,
): Color =
    when {
        !enabled -> colors.iconDisabled
        checked -> colors.iconOnBrand
        else -> colors.iconSubtle
    }

/** switch.css `.qx-switch { inline-size: 32px; block-size: 18px }`. */
private val TRACK_WIDTH = 32.dp
private val TRACK_HEIGHT = 18.dp

/** switch.css `.qx-switch-thumb { inline-size: 12px; block-size: 12px }`. */
private val THUMB = 12.dp

/** `.qx-switch-thumb { inset-inline-start: 2px }`; `[data-checked] { inset-inline-start: 16px }`. */
private val THUMB_OFF = 2.dp
private val THUMB_ON = 16.dp

/** `.qx-switch-thumb { border-radius: calc(radius.sm / 2) }`: 1 dp. */
private val ThumbShape = RoundedCornerShape(QeetrixRadius.Sm / 2)
