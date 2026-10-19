package com.qeetgroup.qeetrix.components.button

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixButtonComponentTokens

/** The geometry of one button size: the web size class, resolved. */
internal class ButtonMetrics(
    /** The drawn height (`h-*`, or `size-*` for a square icon button). */
    val height: Dp,
    /** True for the icon sizes: a square of [height]. */
    val square: Boolean,
    val corner: Dp,
    /** `px-*`. */
    val padding: Dp,
    /** `ps-*`/`pe-*` beside a leading or trailing icon. */
    val iconSidePadding: Dp,
    /** `gap-*`. */
    val gap: Dp,
    /** The icon box (`[&_svg]:size-*`). */
    val iconSize: Dp,
    val textStyle: TextStyle,
)

private class ButtonColors(
    val background: Color,
    val content: Color,
    val border: Color,
    /** `shadow-xs`, which a press settles flat. */
    val shadow: Boolean,
)

/**
 * The surface every Button-family control shares: the Qeet interaction language of qeetrix-ui's
 * `buttonVariants`. Rest is the variant's own surface; hover takes one step along its ladder and a press
 * the next, a resting hairline shadow settling flat (no ripple: the step is the feedback); keyboard focus
 * draws the focus ring; disabled fades (the primary turns graphite instead); loading keeps full colour,
 * ignores activation, keeps focus and reports a busy state.
 *
 * The drawn control keeps the web's size; [qeetrixMinimumTouchTarget] reserves a 48 × 48 dp touch area
 * around it, as Android requires.
 */
@Composable
internal fun ButtonFrame(
    onClick: () -> Unit,
    modifier: Modifier,
    variant: QeetrixButtonVariant,
    metrics: ButtonMetrics,
    enabled: Boolean,
    loading: Boolean,
    interactionSource: MutableInteractionSource?,
    startPadding: Dp,
    endPadding: Dp,
    semantics: SemanticsPropertyReceiver.() -> Unit,
    content: @Composable RowScope.(contentColor: Color, underline: Boolean) -> Unit,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    // data-loading:pointer-events-none — no hover or press styles while busy.
    val colors = buttonColors(variant, enabled = enabled, loading = loading, hovered = hovered && !loading, pressed = pressed && !loading)
    val fade = !enabled && !loading && variant != QeetrixButtonVariant.Default // disabled:not-data-loading:opacity-disabled
    val motion = qeetrixAnimationSpec<Color>(QeetrixEasing.Standard, QeetrixDuration.Fast) // duration-fast ease-standard
    val background by animateColorAsState(colors.background, motion, label = "button-background")
    val contentColor by animateColorAsState(colors.content, motion, label = "button-content")
    val border by animateColorAsState(colors.border, motion, label = "button-border")
    val shape = RoundedCornerShape(metrics.corner)
    val busy = stringResource(R.string.qx_loading)

    Row(
        modifier =
            modifier
                .clickable(
                    interactionSource = source,
                    indication = null,
                    enabled = enabled || loading,
                    role = Role.Button,
                    onClick = { if (!loading) onClick() }, // loading ignores activation but keeps focus
                ).semantics {
                    semantics()
                    if (loading) stateDescription = busy
                }.qeetrixMinimumTouchTarget()
                .qeetrixFocusRing(source, shape)
                .then(if (fade) Modifier.alpha(QeetrixOpacity.Disabled) else Modifier)
                .qeetrixElevation(if (colors.shadow) QeetrixElevation.RampXs else QeetrixElevation.Flat, shape, background)
                .border(QeetrixStroke.Hairline, border, shape) // border (transparent unless outline)
                .then(
                    if (metrics.square) {
                        Modifier.size(metrics.height)
                    } else {
                        Modifier
                            .heightIn(min = metrics.height) // grows with the font scale instead of clipping
                            .padding(start = startPadding + QeetrixStroke.Hairline, end = endPadding + QeetrixStroke.Hairline)
                    },
                ),
        horizontalArrangement = Arrangement.spacedBy(metrics.gap, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalQeetrixContentColor provides contentColor) {
            // hover:underline (link): shown on press too, as touch has no hover.
            content(contentColor, variant == QeetrixButtonVariant.Link && (hovered || pressed) && !loading)
        }
    }
}

@Composable
private fun buttonColors(
    variant: QeetrixButtonVariant,
    enabled: Boolean,
    loading: Boolean,
    hovered: Boolean,
    pressed: Boolean,
): ButtonColors {
    val colors = QeetrixTheme.colors
    val inherited = LocalQeetrixContentColor.current.takeOrElse { colors.textPrimary }
    return when (variant) {
        QeetrixButtonVariant.Default -> {
            if (!enabled && !loading) {
                // disabled:not-data-loading: bg-surface-interactive text-(--qx-color-text-disabled) opacity-100 shadow-none
                ButtonColors(colors.surfaceInteractive, colors.textDisabled, Color.Transparent, shadow = false)
            } else {
                val background =
                    when {
                        pressed -> QeetrixButtonComponentTokens.PrimaryBackgroundActive
                        hovered -> QeetrixButtonComponentTokens.PrimaryBackgroundHover
                        else -> QeetrixButtonComponentTokens.PrimaryBackground
                    }
                ButtonColors(background, QeetrixButtonComponentTokens.PrimaryForeground, Color.Transparent, shadow = !pressed)
            }
        }

        // border-(outline-border) bg-(outline-background) shadow-xs hover:bg-surface-interactive-hover active:bg-surface-interactive-active
        QeetrixButtonVariant.Outline -> {
            val background =
                when {
                    pressed -> colors.surfaceInteractiveActive
                    hovered -> colors.surfaceInteractiveHover
                    else -> QeetrixButtonComponentTokens.OutlineBackground
                }
            ButtonColors(
                background,
                if (hovered ||
                    pressed
                ) {
                    colors.textPrimary
                } else {
                    inherited
                },
                QeetrixButtonComponentTokens.OutlineBorder,
                shadow = !pressed,
            )
        }

        // bg-surface-interactive text-secondary-foreground shadow-xs, hover/active ladder
        QeetrixButtonVariant.Secondary -> {
            val background =
                when {
                    pressed -> colors.surfaceInteractiveActive
                    hovered -> colors.surfaceInteractiveHover
                    else -> colors.surfaceInteractive
                }
            ButtonColors(background, colors.textOnSubtle, Color.Transparent, shadow = !pressed)
        }

        // transparent; hover:bg-surface-interactive-hover hover:text-foreground active:bg-surface-interactive-active
        QeetrixButtonVariant.Ghost -> {
            val background =
                when {
                    pressed -> colors.surfaceInteractiveActive
                    hovered -> colors.surfaceInteractiveHover
                    else -> Color.Transparent
                }
            ButtonColors(background, if (hovered || pressed) colors.textPrimary else inherited, Color.Transparent, shadow = false)
        }

        // bg-destructive-subtle text-destructive-text; hover/active: bg-destructive-strong text-on-feedback-strong
        QeetrixButtonVariant.Destructive -> {
            if (hovered || pressed) {
                ButtonColors(colors.feedbackErrorStrong, colors.textOnFeedbackStrong, Color.Transparent, shadow = false)
            } else {
                ButtonColors(colors.feedbackErrorSubtle, colors.textDanger, Color.Transparent, shadow = false)
            }
        }

        // text-link; hover:text-link-hover hover:underline. Touch has no hover, so a press shows it.
        QeetrixButtonVariant.Link -> {
            ButtonColors(
                Color.Transparent,
                if (hovered ||
                    pressed
                ) {
                    colors.textLinkHover
                } else {
                    colors.textLink
                },
                Color.Transparent,
                shadow = false,
            )
        }
    }
}
