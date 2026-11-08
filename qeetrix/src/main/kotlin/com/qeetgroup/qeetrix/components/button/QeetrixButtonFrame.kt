package com.qeetgroup.qeetrix.components.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The geometry of one button size (button.css `data-size`), resolved for the current density. */
internal class ButtonMetrics(
    /** `block-size: size.control.*`. */
    val height: Dp,
    /** `padding-inline-start` before the label. */
    val labelPadding: Dp,
    /** `gap`: from the label to the icon zone's rule. */
    val gap: Dp,
    /** The icon zone's `padding-inline`, on each side of the icon. */
    val zonePadding: Dp,
    /** The zone's rule: `block-size` of the `::before`. */
    val ruleHeight: Dp,
    /** control.ts `iconSize`: 16, 20 in `lg`. */
    val iconSize: Dp,
    val textStyle: TextStyle,
) {
    /**
     * The space above and below the label line: half of what `block-size` leaves around the role's line height
     * at the default font size. At 100 % text the button is exactly `block-size` tall, as on the web; as the
     * text grows the button grows around it, keeping that space instead of letting the label touch its edges.
     */
    val labelInset: Dp
        get() = ((height - (textStyle.fontSize.value * textStyle.lineHeight.value).dp) / 2).coerceAtLeast(0.dp)
}

/** button.css per size. */
@Composable
internal fun buttonMetrics(size: QeetrixButtonSize): ButtonMetrics {
    val density = QeetrixTheme.density
    val typography = QeetrixTheme.typography
    return when (size) {
        // block-size size.control.sm; label.sm; gap 8px; padding-inline 10px; zone 8px; rule size.icon.xs
        QeetrixButtonSize.Sm -> {
            ButtonMetrics(density.sizeControlSm, 10.dp, 8.dp, 8.dp, QeetrixSize.IconXs, QeetrixSize.IconSm, typography.labelSm)
        }

        // block-size size.control.md; label.md; gap 12px; padding-inline 14px; zone 10px; rule size.icon.sm
        QeetrixButtonSize.Md -> {
            ButtonMetrics(density.sizeControlMd, 14.dp, 12.dp, 10.dp, QeetrixSize.IconSm, QeetrixSize.IconSm, typography.labelMd)
        }

        // block-size size.control.lg; label.md; gap 14px; padding-inline 16px; zone 12px; rule size.icon.md
        QeetrixButtonSize.Lg -> {
            ButtonMetrics(density.sizeControlLg, 16.dp, 14.dp, 12.dp, QeetrixSize.IconMd, QeetrixSize.IconMd, typography.labelMd)
        }
    }
}

/** A button's fill and label colour in one state. */
internal class ButtonColors(
    val background: Color,
    val content: Color,
)

/**
 * button.css's colours for a variant and tone in a state. A `solid` `neutral` button steps as `soft` does (06 §1, "a
 * solid or soft neutral button looks like soft"), and a `ghost` `danger` button hovers in `bg.danger.subtle` and presses in
 * `bg.danger.subtle-pressed`, never the brand's tints.
 */
@Composable
internal fun buttonColors(
    variant: QeetrixButtonVariant,
    tone: QeetrixButtonTone,
    enabled: Boolean,
    hovered: Boolean,
    pressed: Boolean,
): ButtonColors {
    val c = QeetrixTheme.colors

    fun step(
        rest: Color,
        hover: Color,
        press: Color,
    ) = when {
        pressed -> press
        hovered -> hover
        else -> rest
    }
    if (!enabled) {
        // [data-disabled]:not([data-loading]): bg-disabled / text-disabled; a ghost keeps no fill.
        return ButtonColors(if (variant == QeetrixButtonVariant.Ghost) Color.Transparent else c.bgDisabled, c.textDisabled)
    }
    return when {
        variant == QeetrixButtonVariant.Ghost && tone == QeetrixButtonTone.Brand -> {
            ButtonColors(step(Color.Transparent, c.bgBrandSubtle, c.bgBrandSubtlePressed), c.textBrand)
        }

        variant == QeetrixButtonVariant.Ghost && tone == QeetrixButtonTone.Danger -> {
            // .qx-button[data-tone="danger"][data-variant="ghost"]: bg-danger-subtle on hover, bg-danger-subtle-pressed
            // on a press (as the web's 0.5.1)
            ButtonColors(step(Color.Transparent, c.bgDangerSubtle, c.bgDangerSubtlePressed), c.textDanger)
        }

        variant == QeetrixButtonVariant.Ghost -> {
            // neutral: text-subtle; hover bg-neutral-subtle + text-default; active bg-neutral-subtle-pressed
            ButtonColors(
                step(Color.Transparent, c.bgNeutralSubtle, c.bgNeutralSubtlePressed),
                if (hovered || pressed) c.textDefault else c.textSubtle,
            )
        }

        tone == QeetrixButtonTone.Danger -> {
            ButtonColors(step(c.bgDangerBold, c.bgDangerBoldHover, c.bgDangerBoldPressed), c.textOnBold)
        }

        variant == QeetrixButtonVariant.Soft || tone == QeetrixButtonTone.Neutral -> {
            // .qx-button[data-tone="neutral"]:not([data-variant="ghost"]):hover / :active: bg-neutral-bold-hover / -pressed
            ButtonColors(step(c.bgNeutralBold, c.bgNeutralBoldHover, c.bgNeutralBoldPressed), c.textInverse)
        }

        else -> {
            ButtonColors(step(c.bgBrandBold, c.bgBrandBoldHover, c.bgBrandBoldPressed), c.textOnBrand)
        }
    }
}

/**
 * The surface every Button-family control shares (`.qx-button`): the fill steps to its `-hover` and
 * `-pressed` tokens with no ripple, scaling or movement (design rules §7), in `motion.duration.fast`;
 * keyboard focus draws the 2 dp Ember frame inside the edge with a 1 dp surface gap; disabled takes the
 * disabled colours and stays focusable, so the reason it is disabled can be read; loading keeps its
 * colours, ignores activation, keeps focus and reports a busy state.
 *
 * The drawn control keeps the web's size; [qeetrixMinimumTouchTarget] reserves a 48 × 48 dp touch area
 * around it, as Android requires.
 */
@Composable
internal fun ButtonFrame(
    onClick: () -> Unit,
    modifier: Modifier,
    variant: QeetrixButtonVariant,
    tone: QeetrixButtonTone,
    metrics: ButtonMetrics,
    square: Boolean,
    enabled: Boolean,
    loading: Boolean,
    interactionSource: MutableInteractionSource?,
    horizontalArrangement: Arrangement.Horizontal,
    startPadding: Dp,
    semantics: SemanticsPropertyReceiver.() -> Unit,
    onLongClick: (() -> Unit)? = null,
    content: @Composable RowScope.(contentColor: Color) -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    // [data-loading] keeps the resting look; no hover or press styles while busy or disabled.
    val active = enabled && !loading
    val colors = buttonColors(variant, tone, enabled = enabled || loading, hovered = hovered && active, pressed = pressed && active)
    val motion = qeetrixAnimationSpec<Color>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast)
    val background by animateColorAsState(colors.background, motion, label = "button-background")
    val contentColor by animateColorAsState(colors.content, motion, label = "button-content")
    val busy = stringResource(R.string.qx_loading)
    val shape = QeetrixShapes.Control

    Row(
        modifier =
            modifier
                .combinedClickable(
                    interactionSource = source,
                    indication = null,
                    enabled = active || loading,
                    role = Role.Button,
                    onLongClick = onLongClick,
                    onClick = { if (active) onClick() }, // loading ignores activation but keeps focus
                ).then(if (enabled || loading) Modifier else Modifier.focusable(interactionSource = source)) // focusableWhenDisabled
                .semantics {
                    semantics()
                    if (loading) stateDescription = busy
                }.qeetrixMinimumTouchTarget()
                .qeetrixFocusFrame(source, shape, FocusFrame.InsideWithGap)
                .background(background, shape)
                .then(
                    if (square) {
                        Modifier.size(metrics.height)
                    } else {
                        Modifier
                            .heightIn(min = metrics.height) // grows with the font scale instead of clipping
                            .padding(start = startPadding, top = metrics.labelInset, bottom = metrics.labelInset)
                    },
                ),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalQeetrixContentColor provides contentColor) {
            content(contentColor)
        }
    }
}
