package com.qeetgroup.qeetrix.internal

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** Where an anchored popup lines up with its anchor along the reading axis: the web's `align`. */
internal enum class PopupAlign {
    /** The popup's inline start on the anchor's — `align="start"`, a menu's default. */
    Start,

    /** Centred on the anchor — `align="center"`, the select list's. */
    Center,
}

/**
 * Below the anchor when the popup fits there, else above when it fits there, else on the roomier side — the
 * web's flip on collision — aligned by [align] and kept [margin] inside the window. [gap] is the web's
 * `sideOffset`.
 */
internal class AnchoredPopupPosition(
    private val gap: Int,
    private val margin: Int,
    private val align: PopupAlign,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val width = popupContentSize.width
        val start =
            when (align) {
                PopupAlign.Center -> anchorBounds.left + (anchorBounds.width - width) / 2
                PopupAlign.Start -> if (layoutDirection == LayoutDirection.Rtl) anchorBounds.right - width else anchorBounds.left
            }
        val x = start.coerceIn(minOf(margin, maxOf(0, windowSize.width - width)), maxOf(0, windowSize.width - width - margin))
        val height = popupContentSize.height
        val roomBelow = windowSize.height - margin - (anchorBounds.bottom + gap)
        val roomAbove = anchorBounds.top - gap - margin
        val above = height > roomBelow && (height <= roomAbove || roomAbove > roomBelow)
        val y = if (above) anchorBounds.top - gap - height else anchorBounds.bottom + gap
        return IntOffset(x, y.coerceIn(0, maxOf(0, windowSize.height - height)))
    }
}

/**
 * How an anchored popup comes and goes: a fade and a scale from 97 %, from the edge that faces its anchor
 * (`data-open:fade-in-0 zoom-in-97`, `duration-fast`), and nothing under reduced motion.
 */
@Composable
internal fun popupEnter(fromBottom: Boolean): EnterTransition {
    if (QeetrixTheme.reduceMotion) return EnterTransition.None
    val spec = qeetrixAnimationSpec<Float>(QeetrixEasing.Enter, QeetrixDuration.Fast)
    return fadeIn(spec) + scaleIn(spec, initialScale = POPUP_SCALE, transformOrigin = TransformOrigin(0.5f, if (fromBottom) 1f else 0f))
}

/** [popupEnter] reversed, on the exit curve. */
@Composable
internal fun popupExit(fromBottom: Boolean): ExitTransition {
    if (QeetrixTheme.reduceMotion) return ExitTransition.None
    val spec = qeetrixAnimationSpec<Float>(QeetrixEasing.Exit, QeetrixDuration.Fast)
    return fadeOut(spec) + scaleOut(spec, targetScale = POPUP_SCALE, transformOrigin = TransformOrigin(0.5f, if (fromBottom) 1f else 0f))
}

/** `zoom-in-97` / `scale-97`. */
private const val POPUP_SCALE = 0.97f
