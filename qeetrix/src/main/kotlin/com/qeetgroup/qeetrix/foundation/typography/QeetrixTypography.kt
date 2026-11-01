package com.qeetgroup.qeetrix.foundation.typography

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.internal.QeetrixTextRoleSpec
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFontGeometryTokens

/**
 * The text style of every [QeetrixTextRole], built from the `typography.*` tokens and the bundled
 * fonts. Read it with `QeetrixTheme.typography`.
 *
 * Sizes are in sp, so text follows the user's font-size setting, including Android 14's non-linear
 * scaling. Line height is a multiple of the font size (em) with the extra space split evenly above and
 * below, as CSS does. Letter spacing is in em ([labelCaps] is the only tracked role). Glyphs the Qeet
 * fonts lack — Devanagari, Tamil and other Indic scripts — fall back to the system's Noto fonts per
 * character, as the web's `font.family.indic` stack does.
 */
@Immutable
public class QeetrixTypography internal constructor(
    /** `typography.display.lg` */
    public val displayLg: TextStyle,
    /** `typography.display.md` */
    public val displayMd: TextStyle,
    /** `typography.display.sm` */
    public val displaySm: TextStyle,
    /** `typography.heading.lg` */
    public val headingLg: TextStyle,
    /** `typography.heading.md` */
    public val headingMd: TextStyle,
    /** `typography.heading.sm` */
    public val headingSm: TextStyle,
    /** `typography.title.lg` */
    public val titleLg: TextStyle,
    /** `typography.title.md` */
    public val titleMd: TextStyle,
    /** `typography.title.sm` */
    public val titleSm: TextStyle,
    /** `typography.body.lg` */
    public val bodyLg: TextStyle,
    /** `typography.body.md` */
    public val bodyMd: TextStyle,
    /** `typography.body.sm` */
    public val bodySm: TextStyle,
    /** `typography.label.lg` */
    public val labelLg: TextStyle,
    /** `typography.label.md` */
    public val labelMd: TextStyle,
    /** `typography.label.sm` */
    public val labelSm: TextStyle,
    /** `typography.label.caps` */
    public val labelCaps: TextStyle,
    /** `typography.code.md` */
    public val codeMd: TextStyle,
    /** `typography.code.sm` */
    public val codeSm: TextStyle,
) {
    /** The style of [role]. */
    public operator fun get(role: QeetrixTextRole): TextStyle =
        when (role) {
            QeetrixTextRole.DisplayLg -> displayLg
            QeetrixTextRole.DisplayMd -> displayMd
            QeetrixTextRole.DisplaySm -> displaySm
            QeetrixTextRole.HeadingLg -> headingLg
            QeetrixTextRole.HeadingMd -> headingMd
            QeetrixTextRole.HeadingSm -> headingSm
            QeetrixTextRole.TitleLg -> titleLg
            QeetrixTextRole.TitleMd -> titleMd
            QeetrixTextRole.TitleSm -> titleSm
            QeetrixTextRole.BodyLg -> bodyLg
            QeetrixTextRole.BodyMd -> bodyMd
            QeetrixTextRole.BodySm -> bodySm
            QeetrixTextRole.LabelLg -> labelLg
            QeetrixTextRole.LabelMd -> labelMd
            QeetrixTextRole.LabelSm -> labelSm
            QeetrixTextRole.LabelCaps -> labelCaps
            QeetrixTextRole.CodeMd -> codeMd
            QeetrixTextRole.CodeSm -> codeSm
        }

    override fun equals(other: Any?): Boolean = other is QeetrixTypography && QeetrixTextRole.entries.all { this[it] == other[it] }

    override fun hashCode(): Int = QeetrixTextRole.entries.map { this[it] }.hashCode()

    override fun toString(): String = "QeetrixTypography(${QeetrixTextRole.entries.joinToString { "${it.value}=${this[it].fontSize}" }})"

    internal companion object {
        /** The Qeetrix typography at a font scale of 1. Immutable, so one instance serves every theme. */
        val Default: QeetrixTypography by lazy { build { style(it) } }

        /**
         * The typography for [density]'s font scale: the same sizes in sp, with each role's optical size (`opsz`) set
         * to the size its text is drawn at ([QeetrixFonts.opticalSize]). [Default] at a scale of 1.
         */
        fun at(density: Density): QeetrixTypography = if (density.fontScale == 1f) Default else build { style(it, density) }

        private fun build(style: (QeetrixTextRoleSpec) -> TextStyle): QeetrixTypography =
            QeetrixTypography(
                displayLg = style(QeetrixTextRole.DisplayLg.spec),
                displayMd = style(QeetrixTextRole.DisplayMd.spec),
                displaySm = style(QeetrixTextRole.DisplaySm.spec),
                headingLg = style(QeetrixTextRole.HeadingLg.spec),
                headingMd = style(QeetrixTextRole.HeadingMd.spec),
                headingSm = style(QeetrixTextRole.HeadingSm.spec),
                titleLg = style(QeetrixTextRole.TitleLg.spec),
                titleMd = style(QeetrixTextRole.TitleMd.spec),
                titleSm = style(QeetrixTextRole.TitleSm.spec),
                bodyLg = style(QeetrixTextRole.BodyLg.spec),
                bodyMd = style(QeetrixTextRole.BodyMd.spec),
                bodySm = style(QeetrixTextRole.BodySm.spec),
                labelLg = style(QeetrixTextRole.LabelLg.spec),
                labelMd = style(QeetrixTextRole.LabelMd.spec),
                labelSm = style(QeetrixTextRole.LabelSm.spec),
                labelCaps = style(QeetrixTextRole.LabelCaps.spec),
                codeMd = style(QeetrixTextRole.CodeMd.spec),
                codeSm = style(QeetrixTextRole.CodeSm.spec),
            )

        /** [spec]'s style, its optical size for [density]'s font scale (a scale of 1 without one). */
        fun style(
            spec: QeetrixTextRoleSpec,
            density: Density? = null,
        ): TextStyle =
            TextStyle(
                fontFamily = QeetrixFonts.family(spec, density?.let { QeetrixFonts.opticalSize(spec, it) } ?: spec.size),
                fontWeight = FontWeight(spec.weight),
                fontSize = spec.size.sp,
                lineHeight = spec.lineHeight.em,
                letterSpacing = spec.letterSpacing.em,
                // CSS half-leading: the extra line height is split evenly above and below each line,
                // including the first and last.
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
            )
    }
}

/**
 * Qeet Sans's letter styles (`font.geometry.*`): positions on its `GEOM` axis, from accessibility-first
 * forms (A11y, 0) to fully geometric ones (Geo, 100). The web applies them with
 * `font-variation-settings: "GEOM" var(--qx-font-geometry-…)`; each [QeetrixTextRole] carries its own.
 */
public object QeetrixFontGeometry {
    /** `font.geometry.display` — Geo, for display headlines. */
    public val Display: Float = QeetrixFontGeometryTokens.Display

    /** `font.geometry.heading` — Base, for headings and titles. */
    public val Heading: Float = QeetrixFontGeometryTokens.Heading

    /** `font.geometry.body` — A11y, the most legible forms, for body, label and code text. */
    public val Body: Float = QeetrixFontGeometryTokens.Body
}
