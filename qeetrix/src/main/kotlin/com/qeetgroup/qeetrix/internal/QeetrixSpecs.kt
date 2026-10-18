package com.qeetgroup.qeetrix.internal

import androidx.annotation.FontRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.PathNode

// The data types the generated files (tokens/generated, icons/generated) are written in. Internal:
// the public API is the foundation, theme and icon types built from them.

/** A variation axis of a bundled font, as its `fvar` table declares it. */
@Immutable
internal data class QeetrixFontAxisSpec(
    val tag: String,
    val minimum: Float,
    val default: Float,
    val maximum: Float,
) {
    fun clamp(value: Float): Float = value.coerceIn(minimum, maximum)
}

/** A bundled variable font file and what the file says about itself. */
@Immutable
internal data class QeetrixFontFaceSpec(
    @param:FontRes val resource: Int,
    /** The family name the web's CSS uses, e.g. `Qeet Sans`. */
    val webFamily: String,
    val familyName: String,
    val postScriptName: String,
    val axes: List<QeetrixFontAxisSpec>,
) {
    fun axis(tag: String): QeetrixFontAxisSpec? = axes.firstOrNull { it.tag == tag }
}

/** One typography role's tokens. */
@Immutable
internal data class QeetrixTextRoleSpec(
    val face: QeetrixFontFaceSpec,
    /** Default-size font size, in sp. */
    val size: Float,
    /** The `wght` value. */
    val weight: Int,
    /** Line height as a multiple of the font size. */
    val lineHeight: Float,
    /** Letter spacing in em. */
    val letterSpacing: Float,
    /** The `GEOM` axis value, or null for a font without one. */
    val geometry: Float?,
)

/** Where a bundled font came from. */
@Immutable
internal data class QeetrixFontProvenanceSpec(
    val family: String,
    val webFamily: String,
    val postScriptName: String,
    val version: String,
    val repository: String,
    val tag: String,
    val sha256: String,
)

/** One icon drawing in the 24-unit grid. */
@Immutable
internal class QeetrixIconDrawing(
    val miterLimit: Float,
    val elements: List<QeetrixIconElement>,
)

/** One SVG element of a drawing, painted in the icon's colour. */
@Immutable
internal class QeetrixIconElement(
    val fill: Boolean,
    val evenOdd: Boolean,
    val stroke: Boolean,
    val cap: StrokeCap,
    val join: StrokeJoin,
    val nodes: List<PathNode>,
)
