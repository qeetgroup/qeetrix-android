package com.qeetgroup.qeetrix.foundation.typography

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.internal.QeetrixTextRoleSpec
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFontGeometryTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFontWeightTokens

/**
 * The text style of every [QeetrixTextRole], built from the `typography.*` tokens and the bundled
 * fonts. Read it with `QeetrixTheme.typography`.
 *
 * Sizes are in sp, so text follows the user's font-size setting, including Android 14's non-linear
 * scaling. Line height is a multiple of the font size (em) with the extra space split evenly above and
 * below, as CSS does. Letter spacing is in em. Glyphs the Qeet fonts lack — Devanagari, Tamil and
 * other non-Latin scripts — fall back to the system fonts per character.
 */
@Immutable
public class QeetrixTypography internal constructor(
    /** `typography.display` */
    public val display: TextStyle,
    /** `typography.heading` */
    public val heading: TextStyle,
    /** `typography.title` */
    public val title: TextStyle,
    /** `typography.body` */
    public val body: TextStyle,
    /** `typography.label` */
    public val label: TextStyle,
    /** `typography.label-compact` */
    public val labelCompact: TextStyle,
    /** `typography.caption` */
    public val caption: TextStyle,
    /** `typography.micro` */
    public val micro: TextStyle,
    /** `typography.code` */
    public val code: TextStyle,
) {
    /** The style of [role]. */
    public operator fun get(role: QeetrixTextRole): TextStyle =
        when (role) {
            QeetrixTextRole.Display -> display
            QeetrixTextRole.Heading -> heading
            QeetrixTextRole.Title -> title
            QeetrixTextRole.Body -> body
            QeetrixTextRole.Label -> label
            QeetrixTextRole.LabelCompact -> labelCompact
            QeetrixTextRole.Caption -> caption
            QeetrixTextRole.Micro -> micro
            QeetrixTextRole.Code -> code
        }

    override fun equals(other: Any?): Boolean = other is QeetrixTypography && QeetrixTextRole.entries.all { this[it] == other[it] }

    override fun hashCode(): Int = QeetrixTextRole.entries.map { this[it] }.hashCode()

    override fun toString(): String = "QeetrixTypography(${QeetrixTextRole.entries.joinToString { "${it.value}=${this[it].fontSize}" }})"

    internal companion object {
        /** The Qeetrix typography. Immutable, so one instance serves every theme. */
        val Default: QeetrixTypography by lazy {
            QeetrixTypography(
                display = style(QeetrixTextRole.Display.spec),
                heading = style(QeetrixTextRole.Heading.spec),
                title = style(QeetrixTextRole.Title.spec),
                body = style(QeetrixTextRole.Body.spec),
                label = style(QeetrixTextRole.Label.spec),
                labelCompact = style(QeetrixTextRole.LabelCompact.spec),
                caption = style(QeetrixTextRole.Caption.spec),
                micro = style(QeetrixTextRole.Micro.spec),
                code = style(QeetrixTextRole.Code.spec),
            )
        }

        fun style(spec: QeetrixTextRoleSpec): TextStyle =
            TextStyle(
                fontFamily = QeetrixFonts.family(spec),
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

/** The font weight scale (`font.weight.*`) — the `wght` axis values the roles use. */
public object QeetrixFontWeight {
    /** `font.weight.regular` */
    public val Regular: FontWeight = FontWeight(QeetrixFontWeightTokens.Regular)

    /** `font.weight.medium` */
    public val Medium: FontWeight = FontWeight(QeetrixFontWeightTokens.Medium)

    /** `font.weight.semibold` */
    public val Semibold: FontWeight = FontWeight(QeetrixFontWeightTokens.Semibold)

    /** `font.weight.bold` */
    public val Bold: FontWeight = FontWeight(QeetrixFontWeightTokens.Bold)

    /** The scale, lightest first. */
    public val Scale: List<FontWeight> = listOf(Regular, Medium, Semibold, Bold)
}

/**
 * Qeet Sans's letter styles (`font.geometry.*`): positions on its `GEOM` axis, from
 * accessibility-first neutral forms to fully geometric ones. Each typography role pairs Qeet Sans
 * with one of them; the web applies them with `font-variation-settings: "GEOM" n`.
 */
public object QeetrixFontGeometry {
    /** `font.geometry.a11y` — the most neutral, legible forms. */
    public val A11y: Float = QeetrixFontGeometryTokens.A11y

    /** `font.geometry.ui` — body and interface text. */
    public val Ui: Float = QeetrixFontGeometryTokens.Ui

    /** `font.geometry.base` — headings. */
    public val Base: Float = QeetrixFontGeometryTokens.Base

    /** `font.geometry.geo` — fully geometric. */
    public val Geo: Float = QeetrixFontGeometryTokens.Geo
}
