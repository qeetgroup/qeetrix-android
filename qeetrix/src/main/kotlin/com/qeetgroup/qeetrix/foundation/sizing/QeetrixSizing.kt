package com.qeetgroup.qeetrix.foundation.sizing

import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDensityTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixIconSizeTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixIconStrokeTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixStrokeWidthTokens

/** Icon box sizes (`icon.size.*`), in dp. The web's `<Icon>` defaults to [Md]. */
public object QeetrixIconSize {
    /** `icon.size.xs` */
    public val Xs: Dp = QeetrixIconSizeTokens.Xs

    /** `icon.size.sm` */
    public val Sm: Dp = QeetrixIconSizeTokens.Sm

    /** `icon.size.md` */
    public val Md: Dp = QeetrixIconSizeTokens.Md

    /** `icon.size.lg` */
    public val Lg: Dp = QeetrixIconSizeTokens.Lg
}

/**
 * Icon stroke widths (`icon.stroke.*`), in units of the 24-unit icon grid, so the stroke scales with
 * the icon. The web's `<Icon>` defaults to [Regular].
 */
public object QeetrixIconStroke {
    /** `icon.stroke.thin` */
    public val Thin: Float = QeetrixIconStrokeTokens.Thin

    /** `icon.stroke.regular` */
    public val Regular: Float = QeetrixIconStrokeTokens.Regular
}

/** Border and divider widths (`stroke.width.*`), in dp. */
public object QeetrixStroke {
    /** `stroke.width.hairline` */
    public val Hairline: Dp = QeetrixStrokeWidthTokens.Hairline

    /** `stroke.width.thin` */
    public val Thin: Dp = QeetrixStrokeWidthTokens.Thin

    /** `stroke.width.thick` */
    public val Thick: Dp = QeetrixStrokeWidthTokens.Thick
}

/**
 * Control metrics per density (`density.*`), in dp — the web's `DensityProvider` modes. These are
 * drawn sizes: a control drawn at a compact height still needs a 48 dp touch target
 * ([com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility.MinimumTouchTarget]).
 *
 * @property value The upstream mode name, the same string on every platform.
 */
public enum class QeetrixDensity(
    public val value: String,
) {
    Comfortable("comfortable"),
    Compact("compact"),
    Default("default"),
    ;

    /** `density.control-height.*` */
    public val controlHeight: Dp
        get() =
            when (this) {
                Comfortable -> QeetrixDensityTokens.ControlHeightComfortable
                Compact -> QeetrixDensityTokens.ControlHeightCompact
                Default -> QeetrixDensityTokens.ControlHeightDefault
            }

    /** `density.row-height.*` */
    public val rowHeight: Dp
        get() =
            when (this) {
                Comfortable -> QeetrixDensityTokens.RowHeightComfortable
                Compact -> QeetrixDensityTokens.RowHeightCompact
                Default -> QeetrixDensityTokens.RowHeightDefault
            }

    /** `density.cell-padding-y.*` */
    public val cellPaddingY: Dp
        get() =
            when (this) {
                Comfortable -> QeetrixDensityTokens.CellPaddingYComfortable
                Compact -> QeetrixDensityTokens.CellPaddingYCompact
                Default -> QeetrixDensityTokens.CellPaddingYDefault
            }

    /** `density.field-gap.*` */
    public val fieldGap: Dp
        get() =
            when (this) {
                Comfortable -> QeetrixDensityTokens.FieldGapComfortable
                Compact -> QeetrixDensityTokens.FieldGapCompact
                Default -> QeetrixDensityTokens.FieldGapDefault
            }
}
