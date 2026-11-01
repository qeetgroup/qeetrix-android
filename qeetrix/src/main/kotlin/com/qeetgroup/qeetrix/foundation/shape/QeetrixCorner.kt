package com.qeetgroup.qeetrix.foundation.shape

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.tokens.generated.QeetrixRadiusTokens

/**
 * Corner radii (`radius.*`), in dp. Qeetrix is square (design rules §3): 4 dp on controls and
 * surfaces, 2 dp on small marks; only people (avatars) and status (tags) are round.
 */
public object QeetrixRadius {
    /** `radius.none` — tables, tab lists, sections, rules. */
    public val None: Dp = QeetrixRadiusTokens.None

    /** `radius.sm` — small marks: checkboxes, switch tracks. */
    public val Sm: Dp = QeetrixRadiusTokens.Sm

    /** `radius.tooltip` */
    public val Tooltip: Dp = QeetrixRadiusTokens.Tooltip

    /** `radius.control` — buttons, fields (top corners only), selects: every control. */
    public val Control: Dp = QeetrixRadiusTokens.Control

    /** `radius.surface` — cards, panels. */
    public val Surface: Dp = QeetrixRadiusTokens.Surface

    /** `radius.overlay` — menus, popovers, the select list. */
    public val Overlay: Dp = QeetrixRadiusTokens.Overlay

    /** `radius.modal` — dialogs, sheets. */
    public val Modal: Dp = QeetrixRadiusTokens.Modal

    /** `radius.chip` — status tags: a pill. Compose clamps it to half the shorter side, as CSS does. */
    public val Chip: Dp = QeetrixRadiusTokens.Chip

    /** `radius.full` — avatars, radios, presence dots: a circle or pill. */
    public val Full: Dp = QeetrixRadiusTokens.Full
}

/** The radius tokens as Compose shapes, for `Modifier.clip`, `background` and `border`. */
public object QeetrixShapes {
    /** [QeetrixRadius.None] */
    public val None: Shape = RectangleShape

    /** [QeetrixRadius.Sm] */
    public val Sm: Shape = RoundedCornerShape(QeetrixRadius.Sm)

    /** [QeetrixRadius.Tooltip] */
    public val Tooltip: Shape = RoundedCornerShape(QeetrixRadius.Tooltip)

    /** [QeetrixRadius.Control] */
    public val Control: Shape = RoundedCornerShape(QeetrixRadius.Control)

    /**
     * A filled field: [QeetrixRadius.Control] on the top corners, square at the bottom line
     * (design rules §3, §8).
     */
    public val Field: Shape = RoundedCornerShape(topStart = QeetrixRadius.Control, topEnd = QeetrixRadius.Control)

    /** [QeetrixRadius.Surface] */
    public val Surface: Shape = RoundedCornerShape(QeetrixRadius.Surface)

    /** [QeetrixRadius.Overlay] */
    public val Overlay: Shape = RoundedCornerShape(QeetrixRadius.Overlay)

    /** [QeetrixRadius.Modal] */
    public val Modal: Shape = RoundedCornerShape(QeetrixRadius.Modal)

    /** [QeetrixRadius.Chip]: a pill. */
    public val Chip: Shape = RoundedCornerShape(QeetrixRadius.Chip)

    /** [QeetrixRadius.Full]: a circle, or a pill in a non-square box. */
    public val Full: Shape = CircleShape
}
