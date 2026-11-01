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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixTypographyTokens

/**
 * The three weights `@qeetrix/react` sets on top of a role (text.css `data-weight`): each is a role's own
 * weight — regular `body.md`, medium `label.md`, semibold `title.md` — so a weight change stays on the
 * roles' scale. Every role's font family is built at these weights, so `style.copy(fontWeight = …)` lands
 * on a real instance with the role's letter style.
 */
internal object QeetrixTextWeight {
    val Regular: FontWeight = FontWeight(QeetrixTypographyTokens.BodyMd.weight)
    val Medium: FontWeight = FontWeight(QeetrixTypographyTokens.LabelMd.weight)
    val Semibold: FontWeight = FontWeight(QeetrixTypographyTokens.TitleMd.weight)
}

/** Whether the window is at least [breakpoint] wide — the web's `@media (--qx-viewport-*)`, in dp. */
@Composable
@ReadOnlyComposable
internal fun windowAtLeast(breakpoint: Dp): Boolean = LocalWindowInfo.current.containerDpSize.width >= breakpoint

/**
 * Where a component draws the Qeetrix focus frame — a 2 dp Ember frame (`focus.ring.width`,
 * `color.border.focus`) — per design rules §10.
 */
internal enum class FocusFrame {
    /** Checkboxes, radios, switches, links, tags, tabs: outside the shape, `focus.ring.offset` away. */
    Outside,

    /** Fields, table rows, menu items: inside the edge. */
    Inside,

    /**
     * Buttons: inside the edge, with a 1 dp `color.surface.default` gap between the frame and the fill —
     * button.css's `outline-offset: -focus.ring.width` over an inset `focus.ring.width + border.width.default`
     * surface shadow.
     */
    InsideWithGap,
}

/**
 * The Qeetrix focus frame, drawn while the element has keyboard focus. In touch mode Android does not
 * move focus to clickable elements, so the frame only appears for keyboard, D-pad and switch access, as
 * `:focus-visible` does on the web; it is never removed and never replaced by colour alone.
 */
@Composable
internal fun Modifier.qeetrixFocusFrame(
    interactionSource: InteractionSource,
    shape: Shape,
    placement: FocusFrame,
): Modifier {
    val focused by interactionSource.collectIsFocusedAsState()
    return focusFrame(focused, shape, placement, QeetrixTheme.colors.borderFocus, QeetrixTheme.colors.surfaceDefault)
}

/** [qeetrixFocusFrame] for a [visible] flag the caller tracks itself. */
internal fun Modifier.focusFrame(
    visible: Boolean,
    shape: Shape,
    placement: FocusFrame,
    color: Color,
    gapColor: Color,
    width: Dp = QeetrixFocus.RingWidth,
    offset: Dp = QeetrixFocus.RingOffset,
): Modifier =
    when (placement) {
        FocusFrame.Outside -> drawOutlineRing(visible, shape, color, width, offset)
        FocusFrame.Inside -> drawInsideRing(visible, shape, color, width, gap = null)
        FocusFrame.InsideWithGap -> drawInsideRing(visible, shape, color, width, gap = gapColor)
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

/**
 * A frame [width] wide just inside [shape]'s edge, over the content, when [visible]; with a [gap] colour, a
 * `border.width.default` band of it runs inside the frame, between the frame and the fill.
 */
internal fun Modifier.drawInsideRing(
    visible: Boolean,
    shape: Shape,
    color: Color,
    width: Dp,
    gap: Color?,
): Modifier =
    drawWithContent {
        drawContent()
        if (!visible) return@drawWithContent
        val frame = width.toPx()

        fun band(
            inset: Float,
            stroke: Float,
            paint: Color,
        ) {
            val middle = inset + stroke / 2
            val bandSize = Size(size.width - 2 * middle, size.height - 2 * middle)
            if (bandSize.width <= 0f || bandSize.height <= 0f) return
            val outline = shape.createOutline(bandSize, layoutDirection, this)
            translate(middle, middle) { drawOutline(outline, paint, style = Stroke(stroke)) }
        }
        if (gap != null) band(frame, QeetrixBorder.WidthDefault.toPx(), gap)
        band(0f, frame, color)
    }

/**
 * A short label's style — a button's, a chip's, a tab's: the paragraph's direction comes from its own text, as a
 * `TextView`'s does. Compose otherwise lays every text out in the layout's direction, so in a right-to-left layout
 * a left-to-right label ("[Draft] Cancel", a product name) is a right-to-left paragraph, and Android's text
 * measurement then comes up a few pixels short for some of them: Phase 6 saw labels cut off, ended in an ellipsis or
 * broken onto two lines, on a device and on the JVM. A right-to-left label (Arabic, Hebrew) is still right to left.
 *
 * For a label sized to its text, alignment can't show. A label wider than its text — a menu item — also takes
 * [alignToLayout], so its start is the layout's start, as a `TextView`'s gravity is, and English and Arabic items
 * line up.
 */
@Composable
@ReadOnlyComposable
internal fun TextStyle.labelDirection(alignToLayout: Boolean = false): TextStyle {
    if (!alignToLayout) return copy(textDirection = TextDirection.Content)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val align =
        when (textAlign) {
            TextAlign.Center, TextAlign.Justify, TextAlign.Left, TextAlign.Right -> textAlign
            TextAlign.End -> if (rtl) TextAlign.Left else TextAlign.Right
            else -> if (rtl) TextAlign.Right else TextAlign.Left // Start and Unspecified: the layout's start
        }
    return copy(textDirection = TextDirection.Content, textAlign = align)
}

/** A colour at [alpha], keeping its space. */
internal fun Color.atAlpha(alpha: Float): Color = copy(alpha = this.alpha * alpha)
