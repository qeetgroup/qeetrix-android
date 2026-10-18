package com.qeetgroup.qeetrix.foundation.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.internal.QeetrixFontFaceSpec
import com.qeetgroup.qeetrix.internal.QeetrixTextRoleSpec
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFontWeightTokens

/**
 * Builds the variable-font families the typography roles use. A role's family is the role's font
 * file at every weight of the scale, each instance set the way the web's CSS sets it:
 *
 * - `wght` — the weight (clamped to the file's axis, so Fira Code's 300–700 and Qeet Sans's 400–700
 *   both hold);
 * - `GEOM` — the role's letter style, for a font that has the axis;
 * - `opsz` — the role's size in sp, which Compose multiplies by the font scale, as
 *   `font-optical-sizing: auto` follows the rendered size; the platform clamps it to the axis;
 * - `ital` — 0.
 *
 * Every weight of the scale is present so the platform's Bold Text setting
 * (`Configuration.fontWeightAdjustment`, API 31+), which raises the requested weight, lands on a
 * heavier instance instead of synthesising one.
 */
internal object QeetrixFonts {
    private val weights =
        listOf(
            QeetrixFontWeightTokens.Regular,
            QeetrixFontWeightTokens.Medium,
            QeetrixFontWeightTokens.Semibold,
            QeetrixFontWeightTokens.Bold,
        )

    fun family(spec: QeetrixTextRoleSpec): FontFamily = FontFamily(weights.map { font(spec, it) })

    fun font(
        spec: QeetrixTextRoleSpec,
        weight: Int,
    ): Font =
        Font(
            resId = spec.face.resource,
            weight = FontWeight(weight),
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings(*settings(spec.face, spec, weight).toTypedArray()),
        )

    fun settings(
        face: QeetrixFontFaceSpec,
        spec: QeetrixTextRoleSpec,
        weight: Int,
    ): List<FontVariation.Setting> =
        buildList {
            face.axis("wght")?.let { add(FontVariation.weight(it.clamp(weight.toFloat()).toInt())) }
            val geometry = spec.geometry
            if (geometry != null) {
                val axis = face.axis("GEOM") ?: error("${face.webFamily} has no GEOM axis for geometry $geometry.")
                add(FontVariation.Setting("GEOM", axis.clamp(geometry)))
            }
            if (face.axis("opsz") != null) add(FontVariation.opticalSizing(spec.size.sp))
            if (face.axis("ital") != null) add(FontVariation.italic(0f))
        }
}
