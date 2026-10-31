package com.qeetgroup.qeetrix.components.tooltip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay

/**
 * When an icon button's name shows in a tooltip — `@qeetrix/react`'s Tooltip as IconButton uses it (tooltip.tsx): after a
 * 500 ms hover with a mouse or trackpad, while it has keyboard focus, and for 1.5 s after a long press (Android's own
 * View tooltips' gesture). The button's name is its content description already, so the tooltip is hidden from
 * accessibility services.
 */
internal class TooltipState {
    var pressed by mutableStateOf(false)
}

@Composable
internal fun rememberTooltipState(): TooltipState = remember { TooltipState() }

/**
 * The tooltip of [text] above (or, without room, below) its anchor (tooltip.css): `color.surface.inverse`,
 * `color.text.inverse`, `body.sm`, 4 × 8 dp padding, `radius.tooltip`, at most 280 dp wide, 8 dp from the anchor.
 * It fades in over `motion.duration.normal` and out over `motion.duration.fast`.
 */
@Composable
internal fun TooltipAnchor(
    text: String,
    state: TooltipState,
    source: InteractionSource,
) {
    val hovered by source.collectIsHoveredAsState()
    val focused by source.collectIsFocusedAsState()
    var hoverShown by remember { mutableStateOf(false) }
    LaunchedEffect(hovered) {
        if (hovered) {
            delay(HOVER_DELAY)
            hoverShown = true
        } else {
            hoverShown = false
        }
    }
    LaunchedEffect(state.pressed) {
        if (state.pressed) {
            delay(LONG_PRESS_SHOWN)
            state.pressed = false
        }
    }
    val shown = hoverShown || focused || state.pressed
    val visible = remember { MutableTransitionState(false) }
    visible.targetState = shown
    if (!visible.currentState && !visible.targetState) return
    val gap = with(LocalDensity.current) { SIDE_OFFSET.roundToPx() }
    val margin = with(LocalDensity.current) { QeetrixSpace.InsetSm.roundToPx() }
    Popup(popupPositionProvider = remember(gap, margin) { TooltipPosition(gap, margin) }, properties = PopupProperties(focusable = false)) {
        AnimatedVisibility(
            visible,
            enter = fadeIn(qeetrixAnimationSpec(QeetrixMotion.EasingEnter, QeetrixMotion.DurationNormal)),
            exit = fadeOut(qeetrixAnimationSpec(QeetrixMotion.EasingExit, QeetrixMotion.DurationFast)),
        ) {
            val colors = QeetrixTheme.colors
            Box(
                Modifier
                    .clearAndSetSemantics {}
                    .widthIn(max = MAX_WIDTH)
                    .background(colors.surfaceInverse, QeetrixShapes.Tooltip)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                BasicText(
                    text,
                    style =
                        QeetrixTheme.typography.bodySm
                            .copy(color = colors.textInverse)
                            .labelDirection(alignToLayout = true),
                )
            }
        }
    }
}

/**
 * Above the anchor, centred, and kept [margin] inside the window as menus are (an icon button at the screen's edge had
 * its tooltip pressed against it); below it where there is no room above (the web's flip).
 */
internal class TooltipPosition(
    private val gap: Int,
    private val margin: Int,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x =
            (anchorBounds.center.x - popupContentSize.width / 2).coerceIn(
                minOf(margin, maxOf(0, windowSize.width - popupContentSize.width)),
                maxOf(0, windowSize.width - popupContentSize.width - margin),
            )
        val above = anchorBounds.top - gap - popupContentSize.height
        val y = if (above >= margin) above else anchorBounds.bottom + gap
        return IntOffset(x, y)
    }
}

/** TooltipProvider `delay = 500`. */
private const val HOVER_DELAY = 500L

/** How long a long press's tooltip stays. */
private const val LONG_PRESS_SHOWN = 1_500L

/** Tooltip `sideOffset = 8`. */
private val SIDE_OFFSET = 8.dp

/** `.qx-tooltip { max-inline-size: 280px }`. */
private val MAX_WIDTH = 280.dp
