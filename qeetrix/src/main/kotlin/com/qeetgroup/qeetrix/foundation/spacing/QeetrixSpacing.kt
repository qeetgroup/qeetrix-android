package com.qeetgroup.qeetrix.foundation.spacing

import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSpaceTokens

/**
 * The Qeetrix spacing scale (`space.*`), in dp. One CSS px is one dp, so `space.l` (16px on the web)
 * is 16.dp. Names follow the upstream steps; a step that starts with a digit moves the digit to the
 * end (`space.2xl` → [Xl2]), as on iOS.
 */
public object QeetrixSpacing {
    /** `space.0` */
    public val Zero: Dp = QeetrixSpaceTokens.Zero

    /** `space.xxs` */
    public val Xxs: Dp = QeetrixSpaceTokens.Xxs

    /** `space.xs` */
    public val Xs: Dp = QeetrixSpaceTokens.Xs

    /** `space.s` */
    public val S: Dp = QeetrixSpaceTokens.S

    /** `space.m` */
    public val M: Dp = QeetrixSpaceTokens.M

    /** `space.l` */
    public val L: Dp = QeetrixSpaceTokens.L

    /** `space.xl` */
    public val Xl: Dp = QeetrixSpaceTokens.Xl

    /** `space.2xl` */
    public val Xl2: Dp = QeetrixSpaceTokens.Xl2

    /** `space.3xl` */
    public val Xl3: Dp = QeetrixSpaceTokens.Xl3

    /** `space.4xl` */
    public val Xl4: Dp = QeetrixSpaceTokens.Xl4

    /** `space.5xl` */
    public val Xl5: Dp = QeetrixSpaceTokens.Xl5
}
