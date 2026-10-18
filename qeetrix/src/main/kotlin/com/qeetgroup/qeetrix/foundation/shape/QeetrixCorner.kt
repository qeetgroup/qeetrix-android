package com.qeetgroup.qeetrix.foundation.shape

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.tokens.generated.QeetrixCornerTokens

/**
 * Corner radii (`corner.*`), in dp. The role tokens ([Control], [Field], [Chip], [Surface],
 * [Overlay]) are what components use; the steps below them are the raw corner scale.
 *
 * `corner.circle` (50% on the web) is a shape rather than a length: use [QeetrixShapes.Circle].
 */
public object QeetrixCorner {
    /** `corner.control` — buttons and other controls. */
    public val Control: Dp = QeetrixCornerTokens.Control

    /** `corner.field` — text fields and selects. */
    public val Field: Dp = QeetrixCornerTokens.Field

    /** `corner.chip` — chips, badges and tags. */
    public val Chip: Dp = QeetrixCornerTokens.Chip

    /** `corner.surface` — cards and panels. */
    public val Surface: Dp = QeetrixCornerTokens.Surface

    /** `corner.overlay` — menus, popovers and dialogs. */
    public val Overlay: Dp = QeetrixCornerTokens.Overlay

    /** `corner.pill` — fully rounded ends; Compose clamps it to half the shorter side, as CSS does. */
    public val Pill: Dp = QeetrixCornerTokens.Pill

    /** `corner.none` */
    public val None: Dp = QeetrixCornerTokens.None

    /** `corner.base` — the radius the scale derives from. */
    public val Base: Dp = QeetrixCornerTokens.Base

    /** `corner.xs` */
    public val Xs: Dp = QeetrixCornerTokens.Xs

    /** `corner.sm` */
    public val Sm: Dp = QeetrixCornerTokens.Sm
}

/** The corner tokens as Compose shapes, for `Modifier.clip`, `background` and `border`. */
public object QeetrixShapes {
    /** [QeetrixCorner.Control] */
    public val Control: Shape = RoundedCornerShape(QeetrixCorner.Control)

    /** [QeetrixCorner.Field] */
    public val Field: Shape = RoundedCornerShape(QeetrixCorner.Field)

    /** [QeetrixCorner.Chip] */
    public val Chip: Shape = RoundedCornerShape(QeetrixCorner.Chip)

    /** [QeetrixCorner.Surface] */
    public val Surface: Shape = RoundedCornerShape(QeetrixCorner.Surface)

    /** [QeetrixCorner.Overlay] */
    public val Overlay: Shape = RoundedCornerShape(QeetrixCorner.Overlay)

    /** [QeetrixCorner.Pill] */
    public val Pill: Shape = RoundedCornerShape(QeetrixCorner.Pill)

    /** `corner.circle`: a circle, or an ellipse in a non-square box, as `border-radius: 50%` draws. */
    public val Circle: Shape = CircleShape

    /** [QeetrixCorner.None] */
    public val None: Shape = RectangleShape
}
