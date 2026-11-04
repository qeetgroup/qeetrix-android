package com.qeetgroup.qeetrix.foundation.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The Qeetrix semantic colours for one colour scheme — every `color.*` token of `@qeetrix/tokens`,
 * named by its path after `color.` in camelCase (`color.bg.brand.bold` → [bgBrandBold]), the same
 * names as on the web (`--qx-color-bg-brand-bold`) and iOS.
 *
 * Read the current scheme's colours with `QeetrixTheme.colors`. There is no public constructor: Qeet
 * Group has one palette across every product, so colours come from the upstream tokens through the
 * generator, never from app code. The `primitive.*` palette is deliberately not available.
 *
 * The groups follow the token paths and the design rules (qeetrix-files `05-design-rules.md` §4):
 * - `bg*` — fills: `neutral`, `brand`, `danger`, `success`, `warning`, `info`, each `subtle` (a tint) or
 *   `bold` (solid), the interactive ones with `-hover` and `-pressed`; `selected`, `disabled`, `input`;
 * - `text*` — text at 4.5:1 or better; `icon*` — icons at 3:1 or better;
 * - `border*` — borders, rules and the focus frame ([borderFocus]);
 * - `surface*` — the layers of a screen: `canvas` (the page), `raised` (content on it), `overlay`
 *   (menus, dialogs), `sunken`, `inverse`;
 * - `overlay*` — translucent hover, pressed and scrim layers; [selection] — selected text;
 * - `dataviz*` — charts; `code*` — syntax highlighting.
 *
 * Ember, the brand orange (`bgBrandBold`, `borderBrand`, `textBrand`), appears only where it means
 * something: the primary action, focus, the current item, checked controls and links.
 *
 * Values are converted from OKLCH per CSS Color 4 (docs/llm/boundaries.md). A colour
 * outside sRGB is a Display P3 colour, drawn in P3 in a wide-colour-gamut window and converted to sRGB
 * elsewhere.
 */
@Immutable
public class QeetrixColors internal constructor(
    /** `color.surface.canvas` */
    public val surfaceCanvas: Color,
    /** `color.surface.default` */
    public val surfaceDefault: Color,
    /** `color.surface.raised` */
    public val surfaceRaised: Color,
    /** `color.surface.overlay` */
    public val surfaceOverlay: Color,
    /** `color.surface.sunken` */
    public val surfaceSunken: Color,
    /** `color.surface.inverse` */
    public val surfaceInverse: Color,
    /** `color.bg.neutral.subtle` */
    public val bgNeutralSubtle: Color,
    /** `color.bg.neutral.subtle-hover` */
    public val bgNeutralSubtleHover: Color,
    /** `color.bg.neutral.subtle-pressed` */
    public val bgNeutralSubtlePressed: Color,
    /** `color.bg.neutral.bold` */
    public val bgNeutralBold: Color,
    /** `color.bg.neutral.bold-hover` */
    public val bgNeutralBoldHover: Color,
    /** `color.bg.neutral.bold-pressed` */
    public val bgNeutralBoldPressed: Color,
    /** `color.bg.brand.subtle` */
    public val bgBrandSubtle: Color,
    /** `color.bg.brand.subtle-hover` */
    public val bgBrandSubtleHover: Color,
    /** `color.bg.brand.subtle-pressed` */
    public val bgBrandSubtlePressed: Color,
    /** `color.bg.brand.bold` */
    public val bgBrandBold: Color,
    /** `color.bg.brand.bold-hover` */
    public val bgBrandBoldHover: Color,
    /** `color.bg.brand.bold-pressed` */
    public val bgBrandBoldPressed: Color,
    /** `color.bg.danger.subtle` */
    public val bgDangerSubtle: Color,
    /** `color.bg.danger.bold` */
    public val bgDangerBold: Color,
    /** `color.bg.danger.bold-hover` */
    public val bgDangerBoldHover: Color,
    /** `color.bg.danger.bold-pressed` */
    public val bgDangerBoldPressed: Color,
    /** `color.bg.warning.subtle` */
    public val bgWarningSubtle: Color,
    /** `color.bg.warning.bold` */
    public val bgWarningBold: Color,
    /** `color.bg.success.subtle` */
    public val bgSuccessSubtle: Color,
    /** `color.bg.success.bold` */
    public val bgSuccessBold: Color,
    /** `color.bg.info.subtle` */
    public val bgInfoSubtle: Color,
    /** `color.bg.info.bold` */
    public val bgInfoBold: Color,
    /** `color.bg.selected` */
    public val bgSelected: Color,
    /** `color.bg.selected-hover` */
    public val bgSelectedHover: Color,
    /** `color.bg.input` */
    public val bgInput: Color,
    /** `color.bg.disabled` */
    public val bgDisabled: Color,
    /** `color.text.default` */
    public val textDefault: Color,
    /** `color.text.subtle` */
    public val textSubtle: Color,
    /** `color.text.subtlest` */
    public val textSubtlest: Color,
    /** `color.text.disabled` */
    public val textDisabled: Color,
    /** `color.text.inverse` */
    public val textInverse: Color,
    /** `color.text.brand` */
    public val textBrand: Color,
    /** `color.text.link` */
    public val textLink: Color,
    /** `color.text.link-hover` */
    public val textLinkHover: Color,
    /** `color.text.on-brand` */
    public val textOnBrand: Color,
    /** `color.text.on-bold` */
    public val textOnBold: Color,
    /** `color.text.on-warning` */
    public val textOnWarning: Color,
    /** `color.text.danger` */
    public val textDanger: Color,
    /** `color.text.warning` */
    public val textWarning: Color,
    /** `color.text.success` */
    public val textSuccess: Color,
    /** `color.text.info` */
    public val textInfo: Color,
    /** `color.icon.default` */
    public val iconDefault: Color,
    /** `color.icon.subtle` */
    public val iconSubtle: Color,
    /** `color.icon.disabled` */
    public val iconDisabled: Color,
    /** `color.icon.inverse` */
    public val iconInverse: Color,
    /** `color.icon.brand` */
    public val iconBrand: Color,
    /** `color.icon.on-brand` */
    public val iconOnBrand: Color,
    /** `color.icon.on-bold` */
    public val iconOnBold: Color,
    /** `color.icon.danger` */
    public val iconDanger: Color,
    /** `color.icon.warning` */
    public val iconWarning: Color,
    /** `color.icon.success` */
    public val iconSuccess: Color,
    /** `color.icon.info` */
    public val iconInfo: Color,
    /** `color.icon.highlight` */
    public val iconHighlight: Color,
    /** `color.border.default` */
    public val borderDefault: Color,
    /** `color.border.subtle` */
    public val borderSubtle: Color,
    /** `color.border.strong` */
    public val borderStrong: Color,
    /** `color.border.strong-hover` */
    public val borderStrongHover: Color,
    /** `color.border.focus` */
    public val borderFocus: Color,
    /** `color.border.brand` */
    public val borderBrand: Color,
    /** `color.border.selected` */
    public val borderSelected: Color,
    /** `color.border.danger` */
    public val borderDanger: Color,
    /** `color.border.warning` */
    public val borderWarning: Color,
    /** `color.border.success` */
    public val borderSuccess: Color,
    /** `color.border.info` */
    public val borderInfo: Color,
    /** `color.border.disabled` */
    public val borderDisabled: Color,
    /** `color.overlay.scrim` */
    public val overlayScrim: Color,
    /** `color.overlay.hover` */
    public val overlayHover: Color,
    /** `color.overlay.pressed` */
    public val overlayPressed: Color,
    /** `color.selection` */
    public val selection: Color,
    /** `color.dataviz.categorical.1` */
    public val datavizCategorical1: Color,
    /** `color.dataviz.categorical.2` */
    public val datavizCategorical2: Color,
    /** `color.dataviz.categorical.3` */
    public val datavizCategorical3: Color,
    /** `color.dataviz.categorical.4` */
    public val datavizCategorical4: Color,
    /** `color.dataviz.categorical.5` */
    public val datavizCategorical5: Color,
    /** `color.dataviz.categorical.6` */
    public val datavizCategorical6: Color,
    /** `color.dataviz.categorical.7` */
    public val datavizCategorical7: Color,
    /** `color.dataviz.categorical.8` */
    public val datavizCategorical8: Color,
    /** `color.dataviz.sequential.1` */
    public val datavizSequential1: Color,
    /** `color.dataviz.sequential.2` */
    public val datavizSequential2: Color,
    /** `color.dataviz.sequential.3` */
    public val datavizSequential3: Color,
    /** `color.dataviz.sequential.4` */
    public val datavizSequential4: Color,
    /** `color.dataviz.sequential.5` */
    public val datavizSequential5: Color,
    /** `color.dataviz.sequential.6` */
    public val datavizSequential6: Color,
    /** `color.dataviz.sequential.7` */
    public val datavizSequential7: Color,
    /** `color.dataviz.diverging.1` */
    public val datavizDiverging1: Color,
    /** `color.dataviz.diverging.2` */
    public val datavizDiverging2: Color,
    /** `color.dataviz.diverging.3` */
    public val datavizDiverging3: Color,
    /** `color.dataviz.diverging.4` */
    public val datavizDiverging4: Color,
    /** `color.dataviz.diverging.5` */
    public val datavizDiverging5: Color,
    /** `color.dataviz.diverging.6` */
    public val datavizDiverging6: Color,
    /** `color.dataviz.diverging.7` */
    public val datavizDiverging7: Color,
    /** `color.dataviz.positive` */
    public val datavizPositive: Color,
    /** `color.dataviz.negative` */
    public val datavizNegative: Color,
    /** `color.dataviz.warning` */
    public val datavizWarning: Color,
    /** `color.dataviz.grid` */
    public val datavizGrid: Color,
    /** `color.dataviz.axis` */
    public val datavizAxis: Color,
    /** `color.dataviz.reference` */
    public val datavizReference: Color,
    /** `color.code.keyword` */
    public val codeKeyword: Color,
    /** `color.code.string` */
    public val codeString: Color,
    /** `color.code.number` */
    public val codeNumber: Color,
    /** `color.code.literal` */
    public val codeLiteral: Color,
    /** `color.code.punctuation` */
    public val codePunctuation: Color,
    /** `color.code.comment` */
    public val codeComment: Color,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QeetrixColors) return false
        return values() == other.values()
    }

    override fun hashCode(): Int = values().hashCode()

    override fun toString(): String = "QeetrixColors(textDefault=$textDefault, surfaceCanvas=$surfaceCanvas, …)"

    private fun values(): List<Color> =
        listOf(
            surfaceCanvas,
            surfaceDefault,
            surfaceRaised,
            surfaceOverlay,
            surfaceSunken,
            surfaceInverse,
            bgNeutralSubtle,
            bgNeutralSubtleHover,
            bgNeutralSubtlePressed,
            bgNeutralBold,
            bgNeutralBoldHover,
            bgNeutralBoldPressed,
            bgBrandSubtle,
            bgBrandSubtleHover,
            bgBrandSubtlePressed,
            bgBrandBold,
            bgBrandBoldHover,
            bgBrandBoldPressed,
            bgDangerSubtle,
            bgDangerBold,
            bgDangerBoldHover,
            bgDangerBoldPressed,
            bgWarningSubtle,
            bgWarningBold,
            bgSuccessSubtle,
            bgSuccessBold,
            bgInfoSubtle,
            bgInfoBold,
            bgSelected,
            bgSelectedHover,
            bgInput,
            bgDisabled,
            textDefault,
            textSubtle,
            textSubtlest,
            textDisabled,
            textInverse,
            textBrand,
            textLink,
            textLinkHover,
            textOnBrand,
            textOnBold,
            textOnWarning,
            textDanger,
            textWarning,
            textSuccess,
            textInfo,
            iconDefault,
            iconSubtle,
            iconDisabled,
            iconInverse,
            iconBrand,
            iconOnBrand,
            iconOnBold,
            iconDanger,
            iconWarning,
            iconSuccess,
            iconInfo,
            iconHighlight,
            borderDefault,
            borderSubtle,
            borderStrong,
            borderStrongHover,
            borderFocus,
            borderBrand,
            borderSelected,
            borderDanger,
            borderWarning,
            borderSuccess,
            borderInfo,
            borderDisabled,
            overlayScrim,
            overlayHover,
            overlayPressed,
            selection,
            datavizCategorical1,
            datavizCategorical2,
            datavizCategorical3,
            datavizCategorical4,
            datavizCategorical5,
            datavizCategorical6,
            datavizCategorical7,
            datavizCategorical8,
            datavizSequential1,
            datavizSequential2,
            datavizSequential3,
            datavizSequential4,
            datavizSequential5,
            datavizSequential6,
            datavizSequential7,
            datavizDiverging1,
            datavizDiverging2,
            datavizDiverging3,
            datavizDiverging4,
            datavizDiverging5,
            datavizDiverging6,
            datavizDiverging7,
            datavizPositive,
            datavizNegative,
            datavizWarning,
            datavizGrid,
            datavizAxis,
            datavizReference,
            codeKeyword,
            codeString,
            codeNumber,
            codeLiteral,
            codePunctuation,
            codeComment,
        )
}
