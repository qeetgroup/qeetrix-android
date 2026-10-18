package com.qeetgroup.qeetrix.foundation.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The Qeetrix semantic colours for one theme — every `color.*` token in qeetrix-ui's consumable
 * layer, under the same names as on the web and iOS (`color.text.on-brand` → [textOnBrand]).
 *
 * Read the current theme's colours with `QeetrixTheme.colors`. There is no public constructor: Qeet
 * Group has one brand across every product, so colours come from the upstream tokens through the
 * generator, never from app code. Palette primitives are deliberately not available — components
 * and screens use these semantic colours only.
 *
 * The groups follow the token paths: `data*` (data visualisation), `text*`, `surface*`, `border*`,
 * `action*`, `feedback*` (status: text-strength, `*Subtle` fills, `*Strong` fills), `focusRing`,
 * `overlayScrim`, `selectionBackground`, `ratingFilled` and `syntax*` (code highlighting).
 *
 * Values are converted from OKLCH per CSS Color 4 (see Documentation/CROSS_PLATFORM_CONTRACT.md).
 * A few data-visualisation and rating colours lie outside sRGB and are Display P3 colours; they are
 * drawn in P3 in a wide-colour-gamut window and converted to sRGB elsewhere.
 */
@Immutable
public class QeetrixColors internal constructor(
    /** `color.data.categorical.1` */
    public val dataCategorical1: Color,
    /** `color.data.categorical.2` */
    public val dataCategorical2: Color,
    /** `color.data.categorical.3` */
    public val dataCategorical3: Color,
    /** `color.data.categorical.4` */
    public val dataCategorical4: Color,
    /** `color.data.categorical.5` */
    public val dataCategorical5: Color,
    /** `color.data.categorical.6` */
    public val dataCategorical6: Color,
    /** `color.data.categorical.7` */
    public val dataCategorical7: Color,
    /** `color.data.categorical.8` */
    public val dataCategorical8: Color,
    /** `color.data.grid` */
    public val dataGrid: Color,
    /** `color.data.axis` */
    public val dataAxis: Color,
    /** `color.data.reference` */
    public val dataReference: Color,
    /** `color.data.positive` */
    public val dataPositive: Color,
    /** `color.data.negative` */
    public val dataNegative: Color,
    /** `color.data.warning` */
    public val dataWarning: Color,
    /** `color.text.primary` */
    public val textPrimary: Color,
    /** `color.text.secondary` */
    public val textSecondary: Color,
    /** `color.text.tertiary` */
    public val textTertiary: Color,
    /** `color.text.placeholder` */
    public val textPlaceholder: Color,
    /** `color.text.disabled` */
    public val textDisabled: Color,
    /** `color.text.inverse` */
    public val textInverse: Color,
    /** `color.text.brand` */
    public val textBrand: Color,
    /** `color.text.brand-display` */
    public val textBrandDisplay: Color,
    /** `color.text.link` */
    public val textLink: Color,
    /** `color.text.link-hover` */
    public val textLinkHover: Color,
    /** `color.text.success` */
    public val textSuccess: Color,
    /** `color.text.warning` */
    public val textWarning: Color,
    /** `color.text.danger` */
    public val textDanger: Color,
    /** `color.text.info` */
    public val textInfo: Color,
    /** `color.text.on-brand` */
    public val textOnBrand: Color,
    /** `color.text.on-subtle` */
    public val textOnSubtle: Color,
    /** `color.text.on-feedback` */
    public val textOnFeedback: Color,
    /** `color.text.on-feedback-strong` */
    public val textOnFeedbackStrong: Color,
    /** `color.text.on-warning-strong` */
    public val textOnWarningStrong: Color,
    /** `color.surface.canvas` */
    public val surfaceCanvas: Color,
    /** `color.surface.default` */
    public val surfaceDefault: Color,
    /** `color.surface.elevated` */
    public val surfaceElevated: Color,
    /** `color.surface.overlay` */
    public val surfaceOverlay: Color,
    /** `color.surface.sunken` */
    public val surfaceSunken: Color,
    /** `color.surface.subtle` */
    public val surfaceSubtle: Color,
    /** `color.surface.interactive` */
    public val surfaceInteractive: Color,
    /** `color.surface.interactive-hover` */
    public val surfaceInteractiveHover: Color,
    /** `color.surface.interactive-active` */
    public val surfaceInteractiveActive: Color,
    /** `color.surface.brand-subtle` */
    public val surfaceBrandSubtle: Color,
    /** `color.surface.brand-subtle-hover` */
    public val surfaceBrandSubtleHover: Color,
    /** `color.surface.brand-subtle-active` */
    public val surfaceBrandSubtleActive: Color,
    /** `color.surface.rail` */
    public val surfaceRail: Color,
    /** `color.surface.inverse` */
    public val surfaceInverse: Color,
    /** `color.border.default` */
    public val borderDefault: Color,
    /** `color.border.subtle` */
    public val borderSubtle: Color,
    /** `color.border.strong` */
    public val borderStrong: Color,
    /** `color.border.control` */
    public val borderControl: Color,
    /** `color.border.control-hover` */
    public val borderControlHover: Color,
    /** `color.border.focused` */
    public val borderFocused: Color,
    /** `color.border.hover` */
    public val borderHover: Color,
    /** `color.border.brand` */
    public val borderBrand: Color,
    /** `color.border.danger` */
    public val borderDanger: Color,
    /** `color.border.success` */
    public val borderSuccess: Color,
    /** `color.border.warning` */
    public val borderWarning: Color,
    /** `color.border.info` */
    public val borderInfo: Color,
    /** `color.action.primary` */
    public val actionPrimary: Color,
    /** `color.action.primary-hover` */
    public val actionPrimaryHover: Color,
    /** `color.action.primary-active` */
    public val actionPrimaryActive: Color,
    /** `color.feedback.success` */
    public val feedbackSuccess: Color,
    /** `color.feedback.warning` */
    public val feedbackWarning: Color,
    /** `color.feedback.error` */
    public val feedbackError: Color,
    /** `color.feedback.info` */
    public val feedbackInfo: Color,
    /** `color.feedback.success-subtle` */
    public val feedbackSuccessSubtle: Color,
    /** `color.feedback.warning-subtle` */
    public val feedbackWarningSubtle: Color,
    /** `color.feedback.error-subtle` */
    public val feedbackErrorSubtle: Color,
    /** `color.feedback.info-subtle` */
    public val feedbackInfoSubtle: Color,
    /** `color.feedback.success-strong` */
    public val feedbackSuccessStrong: Color,
    /** `color.feedback.warning-strong` */
    public val feedbackWarningStrong: Color,
    /** `color.feedback.error-strong` */
    public val feedbackErrorStrong: Color,
    /** `color.feedback.info-strong` */
    public val feedbackInfoStrong: Color,
    /** `color.focus.ring` */
    public val focusRing: Color,
    /** `color.overlay.scrim` */
    public val overlayScrim: Color,
    /** `color.selection.background` */
    public val selectionBackground: Color,
    /** `color.rating.filled` */
    public val ratingFilled: Color,
    /** `color.syntax.key` */
    public val syntaxKey: Color,
    /** `color.syntax.string` */
    public val syntaxString: Color,
    /** `color.syntax.number` */
    public val syntaxNumber: Color,
    /** `color.syntax.literal` */
    public val syntaxLiteral: Color,
    /** `color.syntax.punctuation` */
    public val syntaxPunctuation: Color,
    /** `color.syntax.comment` */
    public val syntaxComment: Color,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QeetrixColors) return false
        return values() == other.values()
    }

    override fun hashCode(): Int = values().hashCode()

    override fun toString(): String = "QeetrixColors(textPrimary=$textPrimary, surfaceCanvas=$surfaceCanvas, …)"

    private fun values(): List<Color> =
        listOf(
            dataCategorical1,
            dataCategorical2,
            dataCategorical3,
            dataCategorical4,
            dataCategorical5,
            dataCategorical6,
            dataCategorical7,
            dataCategorical8,
            dataGrid,
            dataAxis,
            dataReference,
            dataPositive,
            dataNegative,
            dataWarning,
            textPrimary,
            textSecondary,
            textTertiary,
            textPlaceholder,
            textDisabled,
            textInverse,
            textBrand,
            textBrandDisplay,
            textLink,
            textLinkHover,
            textSuccess,
            textWarning,
            textDanger,
            textInfo,
            textOnBrand,
            textOnSubtle,
            textOnFeedback,
            textOnFeedbackStrong,
            textOnWarningStrong,
            surfaceCanvas,
            surfaceDefault,
            surfaceElevated,
            surfaceOverlay,
            surfaceSunken,
            surfaceSubtle,
            surfaceInteractive,
            surfaceInteractiveHover,
            surfaceInteractiveActive,
            surfaceBrandSubtle,
            surfaceBrandSubtleHover,
            surfaceBrandSubtleActive,
            surfaceRail,
            surfaceInverse,
            borderDefault,
            borderSubtle,
            borderStrong,
            borderControl,
            borderControlHover,
            borderFocused,
            borderHover,
            borderBrand,
            borderDanger,
            borderSuccess,
            borderWarning,
            borderInfo,
            actionPrimary,
            actionPrimaryHover,
            actionPrimaryActive,
            feedbackSuccess,
            feedbackWarning,
            feedbackError,
            feedbackInfo,
            feedbackSuccessSubtle,
            feedbackWarningSubtle,
            feedbackErrorSubtle,
            feedbackInfoSubtle,
            feedbackSuccessStrong,
            feedbackWarningStrong,
            feedbackErrorStrong,
            feedbackInfoStrong,
            focusRing,
            overlayScrim,
            selectionBackground,
            ratingFilled,
            syntaxKey,
            syntaxString,
            syntaxNumber,
            syntaxLiteral,
            syntaxPunctuation,
            syntaxComment,
        )
}
