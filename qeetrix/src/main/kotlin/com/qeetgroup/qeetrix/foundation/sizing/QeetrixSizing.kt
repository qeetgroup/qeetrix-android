package com.qeetgroup.qeetrix.foundation.sizing

import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.tokens.generated.QeetrixBorderTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDensityTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixIconStrokeTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSizeTokens

/** Fixed sizes (`size.*`), in dp. Control heights and the row height follow the density: [QeetrixDensity]. */
public object QeetrixSize {
    /** `size.icon.2xs` — marks inside 16–20 dp chips, such as a badge's icon. */
    public val Icon2xs: Dp = QeetrixSizeTokens.Icon2xs

    /** `size.icon.xs` — the height of a `sm` button's icon rule. */
    public val IconXs: Dp = QeetrixSizeTokens.IconXs

    /** `size.icon.sm` — icons inside controls (design rules §16). */
    public val IconSm: Dp = QeetrixSizeTokens.IconSm

    /** `size.icon.md` — standalone icons, and icons in `lg` buttons. */
    public val IconMd: Dp = QeetrixSizeTokens.IconMd

    /** `size.icon.lg` — icons in empty states. */
    public val IconLg: Dp = QeetrixSizeTokens.IconLg

    /**
     * `size.target.min` — the smallest pointer target the web allows (WCAG 2.2, 2.5.8). Android's touch
     * targets are larger:
     * [com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility.MinimumTouchTarget].
     */
    public val TargetMin: Dp = QeetrixSizeTokens.TargetMin
}

/**
 * Icon stroke widths (`icon.stroke.*`), in units of the 24-unit icon grid, so the stroke scales with
 * the icon. [Regular] at the default and comfortable densities, [Thin] at compact (design rules §16).
 */
public object QeetrixIconStroke {
    /** `icon.stroke.thin` */
    public val Thin: Float = QeetrixIconStrokeTokens.Thin

    /** `icon.stroke.regular` */
    public val Regular: Float = QeetrixIconStrokeTokens.Regular
}

/** Border and rule widths (`border.width.*`), in dp. */
public object QeetrixBorder {
    /** `border.width.default` — borders and 1 dp rules. */
    public val WidthDefault: Dp = QeetrixBorderTokens.WidthDefault

    /** `border.width.strong` — the tab list's rule, the field's focus frame. */
    public val WidthStrong: Dp = QeetrixBorderTokens.WidthStrong

    /** `border.width.accent` — the Ember bar that marks the current item, and a dialog's intent line. */
    public val WidthAccent: Dp = QeetrixBorderTokens.WidthAccent
}

/**
 * The densities (`density` modifier of `@qeetrix/tokens`): one set of components, three sizes. Set once
 * per region with `LocalQeetrixDensity`, never per component (design rules §6):
 *
 * - [Compact] — data-heavy screens: tables and filters;
 * - [Default] — most screens;
 * - [Comfortable] — welcoming screens: sign-in, checkout, onboarding.
 *
 * These are drawn sizes: a control drawn at a compact height still gets a 48 dp touch target
 * ([com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility.MinimumTouchTarget]).
 *
 * @property value The upstream context name, the same string on every platform.
 */
public enum class QeetrixDensity(
    public val value: String,
) {
    Compact("compact"),
    Default("default"),
    Comfortable("comfortable"),
    ;

    /** `size.control.sm` — small controls: 24 / 28 / 32 dp. */
    public val sizeControlSm: Dp get() = QeetrixDensityTokens.sizeControlSm(this)

    /** `size.control.md` — controls: 28 / 32 / 36 dp. */
    public val sizeControlMd: Dp get() = QeetrixDensityTokens.sizeControlMd(this)

    /** `size.control.lg` — fields, and the buttons that line up with them: 32 / 40 / 44 dp. */
    public val sizeControlLg: Dp get() = QeetrixDensityTokens.sizeControlLg(this)

    /** `size.row` — table and list rows: 32 / 40 / 48 dp. */
    public val sizeRow: Dp get() = QeetrixDensityTokens.sizeRow(this)

    /** `space.form.gap` — the space between form fields: 12 / 16 / 20 dp. */
    public val spaceFormGap: Dp get() = QeetrixDensityTokens.spaceFormGap(this)

    /** The icon stroke for this density: [QeetrixIconStroke.Thin] at [Compact], else [QeetrixIconStroke.Regular] (§16). */
    public val iconStroke: Float get() = if (this == Compact) QeetrixIconStroke.Thin else QeetrixIconStroke.Regular
}
