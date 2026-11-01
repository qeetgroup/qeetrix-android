package com.qeetgroup.qeetrix.foundation.spacing

import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSpaceTokens

/**
 * The Qeetrix spacing scale (`space.*`), in dp, on the 4 dp grid. One CSS px is one dp, so
 * `space.inset.lg` (16px on the web) is 16.dp.
 *
 * - `Inset*` — padding inside a container;
 * - `Gap*` — space between siblings;
 * - `Layout*` — space between the sections of a screen.
 *
 * `space.form.gap`, the space between form fields, follows the density:
 * [com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity.spaceFormGap].
 */
public object QeetrixSpace {
    /** `space.inset.xs` */
    public val InsetXs: Dp = QeetrixSpaceTokens.InsetXs

    /** `space.inset.sm` */
    public val InsetSm: Dp = QeetrixSpaceTokens.InsetSm

    /** `space.inset.md` */
    public val InsetMd: Dp = QeetrixSpaceTokens.InsetMd

    /** `space.inset.lg` */
    public val InsetLg: Dp = QeetrixSpaceTokens.InsetLg

    /** `space.inset.xl` */
    public val InsetXl: Dp = QeetrixSpaceTokens.InsetXl

    /** `space.gap.xs` */
    public val GapXs: Dp = QeetrixSpaceTokens.GapXs

    /** `space.gap.sm` */
    public val GapSm: Dp = QeetrixSpaceTokens.GapSm

    /** `space.gap.md` */
    public val GapMd: Dp = QeetrixSpaceTokens.GapMd

    /** `space.gap.lg` */
    public val GapLg: Dp = QeetrixSpaceTokens.GapLg

    /** `space.gap.xl` */
    public val GapXl: Dp = QeetrixSpaceTokens.GapXl

    /** `space.layout.sm` */
    public val LayoutSm: Dp = QeetrixSpaceTokens.LayoutSm

    /** `space.layout.md` */
    public val LayoutMd: Dp = QeetrixSpaceTokens.LayoutMd

    /** `space.layout.lg` */
    public val LayoutLg: Dp = QeetrixSpaceTokens.LayoutLg

    /** `space.layout.xl` */
    public val LayoutXl: Dp = QeetrixSpaceTokens.LayoutXl
}
