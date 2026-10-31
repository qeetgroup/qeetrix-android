package com.qeetgroup.qeetrix.foundation.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.internal.QeetrixFontFaceSpec
import com.qeetgroup.qeetrix.internal.QeetrixTextRoleSpec
import com.qeetgroup.qeetrix.tokens.generated.QeetrixTypographyWeights

/**
 * Builds the variable-font families the typography roles use. A role's family is the role's font
 * file at every weight of the scale, each instance set the way the web's CSS sets it:
 *
 * - `wght` — the weight (clamped to the file's axis, so Fira Code's 300–700 and Qeet Sans's 400–700
 *   both hold);
 * - `GEOM` — the role's letter style (`font-variation-settings: "GEOM" var(--qx-font-geometry-…)`), for a
 *   font that has the axis;
 * - `opsz` — the size the text is drawn at, in dp ([opticalSize]: the role's sp through the font scale,
 *   including Android 14's non-linear curve), as `font-optical-sizing: auto` uses the used size in CSS px;
 *   clamped to the axis. It is a plain number, not sp: Compose caches a loaded typeface for the whole
 *   process by its [Font], so a font whose `opsz` came from the font scale at load time would keep that
 *   scale's optical size after the scale changes. A number makes each scale its own [Font];
 * - `ital` — 0. `YTAS` and `SHRP` keep the file's defaults, as on the web.
 *
 * Every role weight, and each font's heaviest, is present so the platform's Bold Text setting
 * (`Configuration.fontWeightAdjustment`, API 31+), which raises the requested weight, lands on a
 * heavier instance instead of synthesising one.
 */
internal object QeetrixFonts {
    /** The size [spec]'s text is drawn at under [density]'s font scale, in dp: its `opsz`. */
    fun opticalSize(
        spec: QeetrixTextRoleSpec,
        density: Density,
    ): Float =
        with(density) {
            spec.size.sp
                .toDp()
                .value
        }

    fun family(
        spec: QeetrixTextRoleSpec,
        opticalSize: Float = spec.size,
    ): FontFamily = FontFamily(QeetrixTypographyWeights.map { font(spec, it, opticalSize) })

    fun font(
        spec: QeetrixTextRoleSpec,
        weight: Int,
        opticalSize: Float = spec.size,
    ): Font =
        Font(
            resId = spec.face.resource,
            weight = FontWeight(weight),
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings(*settings(spec.face, spec, weight, opticalSize).toTypedArray()),
        )

    fun settings(
        face: QeetrixFontFaceSpec,
        spec: QeetrixTextRoleSpec,
        weight: Int,
        opticalSize: Float = spec.size,
    ): List<FontVariation.Setting> =
        buildList {
            face.axis("wght")?.let { add(FontVariation.weight(it.clamp(weight.toFloat()).toInt())) }
            val geometry = spec.geometry
            if (geometry != null) {
                val axis = face.axis("GEOM") ?: error("${face.webFamily} has no GEOM axis for geometry $geometry.")
                add(FontVariation.Setting("GEOM", axis.clamp(geometry)))
            }
            face.axis("opsz")?.let { add(FontVariation.Setting("opsz", it.clamp(opticalSize))) }
            if (face.axis("ital") != null) add(FontVariation.italic(0f))
        }
}
