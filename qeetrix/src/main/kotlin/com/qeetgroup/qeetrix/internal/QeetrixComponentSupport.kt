package com.qeetgroup.qeetrix.internal

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFontWeightTokens

/**
 * A text style the way a web component class list builds one: a font utility (`font-heading`,
 * `font-sans`, `font-ui`, `font-mono` — the role whose family and letter style it takes), a size and
 * line height, and a weight. Built from the same fonts and axes as [QeetrixTypography].
 */
internal fun qeetrixTextStyle(
    font: QeetrixTextRole,
    size: Float,
    lineHeight: Float,
    weight: Int = QeetrixFontWeightTokens.Regular,
): TextStyle = QeetrixTypography.style(font.spec.copy(size = size, lineHeight = lineHeight, weight = weight))

/** [qeetrixTextStyle] for a Tailwind type step (`text-sm`, `text-xs`, …). */
internal fun qeetrixTextStyle(
    font: QeetrixTextRole,
    step: QeetrixWebScale.TextStep,
    weight: Int = QeetrixFontWeightTokens.Regular,
): TextStyle = qeetrixTextStyle(font, step.size, step.lineHeight, weight)

/** The font utility a web class list names, as the role whose family and letter style it sets. */
internal object QeetrixFont {
    /** `font-heading` — Qeet Sans, Base letter style. */
    val Heading = QeetrixTextRole.Heading

    /** `font-sans` — Qeet Sans, UI letter style, for reading copy. */
    val Sans = QeetrixTextRole.Body

    /** `font-ui` — Qeet Sans, UI letter style, for interface text. */
    val Ui = QeetrixTextRole.Label

    /** `font-mono` — Fira Code. */
    val Mono = QeetrixTextRole.Code
}

/** The weight of `font-medium`. */
internal val FontWeightMedium: Int = QeetrixFontWeightTokens.Medium

/** The weight of `font-semibold`. */
internal val FontWeightSemibold: Int = QeetrixFontWeightTokens.Semibold

/** Whether the window is at least [breakpoint] wide — a web `sm:`/`md:` media query, in dp. */
@Composable
@ReadOnlyComposable
internal fun windowAtLeast(breakpoint: Dp): Boolean = LocalWindowInfo.current.containerDpSize.width >= breakpoint

/**
 * The Qeet focus ring (`focus-visible:focus-ring`): an outline of `focus.outline-width` in the ring
 * colour, `focus.offset` outside the shape, drawn while the element has keyboard focus. In touch mode
 * Android does not move focus to clickable elements, so the ring only appears for keyboard, D-pad and
 * switch access, as `:focus-visible` does on the web.
 */
@Composable
internal fun Modifier.qeetrixFocusRing(
    interactionSource: InteractionSource,
    shape: Shape,
    color: Color = QeetrixTheme.colors.focusRing,
    width: Dp = QeetrixFocus.OutlineWidth,
    offset: Dp = QeetrixFocus.Offset,
): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()
    return drawOutlineRing(focused, shape, color, width, offset)
}

/** An outline [width] wide, [offset] outside [shape], when [visible]. */
internal fun Modifier.drawOutlineRing(
    visible: Boolean,
    shape: Shape,
    color: Color,
    width: Dp,
    offset: Dp,
): Modifier =
    drawWithContent {
        drawContent()
        if (!visible) return@drawWithContent
        val inset = offset.toPx() + width.toPx() / 2
        val outline = shape.createOutline(Size(size.width + 2 * inset, size.height + 2 * inset), layoutDirection, this)
        translate(-inset, -inset) {
            drawOutline(outline, color, style = Stroke(width.toPx()))
        }
    }

/** A colour at [alpha], keeping its space. */
internal fun Color.atAlpha(alpha: Float): Color = copy(alpha = this.alpha * alpha)
