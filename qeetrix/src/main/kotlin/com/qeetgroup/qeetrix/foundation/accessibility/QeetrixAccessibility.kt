package com.qeetgroup.qeetrix.foundation.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFocusTokens
import kotlin.math.max
import kotlin.math.pow

/** Accessibility rules Qeetrix applies on Android that are platform requirements rather than tokens. */
public object QeetrixAccessibility {
    /**
     * The minimum size of a touch target, 48 × 48 dp (Android accessibility guidance; it also exceeds
     * WCAG 2.2's 24 × 24 CSS px target size). Not a Qeetrix token: the web's control heights
     * ([com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity], 28–36 dp) are pointer-sized, so a
     * control drawn at those heights must still give its touch area this size.
     */
    public val MinimumTouchTarget: Dp = 48.dp

    /**
     * The WCAG 2.x contrast ratio of [foreground] over [background], from 1 to 21. A translucent
     * foreground is composited over the background first, in gamma-encoded sRGB, as browsers and
     * qeetrix-ui's contrast gate do; the background is treated as opaque. Display P3 colours are
     * converted to sRGB first.
     */
    public fun contrastRatio(
        foreground: Color,
        background: Color,
    ): Double {
        val top = foreground.convert(ColorSpaces.Srgb)
        val bottom = background.convert(ColorSpaces.Srgb)
        val alpha = top.alpha.toDouble()

        fun composite(
            f: Float,
            b: Float,
        ) = f * alpha + b * (1 - alpha)
        val l1 = luminance(composite(top.red, bottom.red), composite(top.green, bottom.green), composite(top.blue, bottom.blue))
        val l2 = luminance(bottom.red.toDouble(), bottom.green.toDouble(), bottom.blue.toDouble())
        return (max(l1, l2) + 0.05) / (minOf(l1, l2) + 0.05)
    }

    private fun luminance(
        red: Double,
        green: Double,
        blue: Double,
    ): Double {
        fun linear(value: Double): Double {
            val c = value.coerceIn(0.0, 1.0)
            return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * linear(red) + 0.7152 * linear(green) + 0.0722 * linear(blue)
    }
}

/**
 * Focus indicator geometry (`focus.*`), in dp. The colour is
 * [com.qeetgroup.qeetrix.foundation.color.QeetrixColors.focusRing]; `focus.ring-style` is always
 * solid.
 */
public object QeetrixFocus {
    /** `focus.ring-width` */
    public val RingWidth: Dp = QeetrixFocusTokens.RingWidth

    /** `focus.outline-width` */
    public val OutlineWidth: Dp = QeetrixFocusTokens.OutlineWidth

    /** `focus.offset` — the gap between a control and its focus ring. */
    public val Offset: Dp = QeetrixFocusTokens.Offset
}

/**
 * Makes this element at least [QeetrixAccessibility.MinimumTouchTarget] in both dimensions without
 * changing how its content is drawn: the content is centred in the larger box. Put it after the
 * modifier that handles input (`Modifier.clickable { }.qeetrixMinimumTouchTarget()`), so the
 * enlarged box is the one that receives touches.
 */
public fun Modifier.qeetrixMinimumTouchTarget(): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val minimum = QeetrixAccessibility.MinimumTouchTarget.roundToPx()
        val width = max(placeable.width, minimum).coerceAtMost(constraints.maxWidth)
        val height = max(placeable.height, minimum).coerceAtMost(constraints.maxHeight)
        layout(width, height) {
            placeable.place((width - placeable.width) / 2, (height - placeable.height) / 2)
        }
    }
