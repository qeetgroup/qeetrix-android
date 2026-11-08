package com.qeetgroup.qeetrix.gen.tokens

import com.qeetgroup.qeetrix.gen.GeneratorError
import com.qeetgroup.qeetrix.gen.color.ColorMath
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** One layer of a `shadow` token, in CSS px. */
data class ShadowLayer(
    val inset: Boolean,
    val x: Double,
    val y: Double,
    val blur: Double,
    val spread: Double,
    val color: ColorMath.EncodedColor,
)

sealed interface TokenValue {
    data class Color(
        val color: ColorMath.EncodedColor,
        /** The `hex` @qeetrix/tokens records beside the OKLCH components, for the cross-check. */
        val dtcgHex: String?,
    ) : TokenValue

    /** A length in CSS px (1 rem = 16 px); Android draws 1 CSS px as 1 dp. */
    data class Dimension(
        val px: Double,
    ) : TokenValue

    data class Number(
        val value: Double,
    ) : TokenValue

    data class Duration(
        val millis: Double,
    ) : TokenValue

    data class Easing(
        val points: List<Double>,
    ) : TokenValue

    data class Shadow(
        val layers: List<ShadowLayer>,
    ) : TokenValue

    data class FontFamily(
        val names: List<String>,
    ) : TokenValue

    data class Typography(
        val family: String,
        /** Font size in CSS px at the default root size (Android: sp). */
        val sizePx: Double,
        val weight: Int,
        /** Line height as a multiple of the font size: the composite's full-precision ratio. */
        val lineHeight: Double,
        /** Letter spacing in em (the token's px divided by the font size). */
        val letterSpacingEm: Double,
        /** The line height as authored, in CSS px: `$extensions["in.qeet.qeetrix"].lineHeight`, on the 4 px grid. */
        val lineHeightPx: Double,
        /** The `font.geometry.*` token the role's letter style is: `$extensions["in.qeet.qeetrix"].fontGeometry`. */
        val geometry: List<String>,
    ) : TokenValue
}

/**
 * One generated token.
 *
 * @property themes the value per colour scheme, at the default density.
 * @property densities the value per density, in the default colour scheme — only for a token the
 *   density modifier changes; null otherwise.
 * @property sources the DTCG token each theme's value was resolved from.
 */
data class ResolvedToken(
    val path: List<String>,
    val kind: TokenKind,
    val themes: Map<String, TokenValue>,
    val densities: Map<String, TokenValue>?,
    val sources: Map<String, DtcgToken>,
) {
    val dotted: String get() = path.joinToString(".")
    val isThemeInvariant: Boolean get() = themes.values.toSet().size == 1
    val followsDensity: Boolean get() = densities != null
}

data class ExcludedToken(
    val path: List<String>,
    val reason: String,
)

/**
 * Every `@qeetrix/tokens` token, resolved per colour scheme and density as its resolver says, then
 * either typed into a generated value or excluded with a reason.
 */
class TokenModel(
    source: DtcgSource,
    /** The `component.<group>` names adopted in Upstream/manifest.json. */
    val adoptedComponents: Set<String> = emptySet(),
) {
    val themes: List<String>
    val densities: List<String>
    val defaultDensity: String
    val included: List<ResolvedToken>
    val excluded: List<ExcludedToken>

    init {
        val scheme = source.modifier(COLOR_SCHEME)
        val density = source.modifier(DENSITY)
        themes = scheme.contexts.keys.toList()
        densities = density.contexts.keys.toList()
        defaultDensity = density.default
        for (theme in themes) {
            source.verifyPublished("$theme.tokens.json", mapOf(COLOR_SCHEME to theme, DENSITY to density.default))
        }
        val perTheme = themes.associateWith { source.resolve(mapOf(COLOR_SCHEME to it, DENSITY to density.default)) }
        val perDensity = densities.associateWith { source.resolve(mapOf(COLOR_SCHEME to scheme.default, DENSITY to it)) }
        val paths = perTheme.getValue(themes.first()).keys
        for ((theme, tokens) in perTheme) {
            if (tokens.keys != paths) throw GeneratorError("The $theme colour scheme defines a different set of tokens from ${themes.first()}.")
        }

        val upstreamGroups = paths.map { it.split('.') }.filter { it.first() == "component" && it.size > 2 }.map { it[1] }.toSet()
        val unknown = adoptedComponents - upstreamGroups
        if (unknown.isNotEmpty()) {
            throw GeneratorError("Upstream/manifest.json adopts component groups @qeetrix/tokens does not define: ${unknown.sorted()}.")
        }

        val included = mutableListOf<ResolvedToken>()
        val excluded = mutableListOf<ExcludedToken>()
        for (dotted in paths) {
            val path = dotted.split('.')
            when (val decision = Scope.decide(path, adoptedComponents)) {
                is ScopeDecision.Exclude -> {
                    excluded += ExcludedToken(path, decision.reason)
                }

                is ScopeDecision.Include -> {
                    val sources = perTheme.mapValues { it.value.getValue(dotted) }
                    for ((theme, token) in sources) {
                        if (token.type != decision.kind.dtcgType) {
                            throw GeneratorError("$dotted ($theme): \$type ${token.type}, but its scope rule expects ${decision.kind.dtcgType}.")
                        }
                    }
                    val values = sources.mapValues { (theme, token) -> value(decision.kind, token, "$dotted ($theme)") }
                    val byDensity = perDensity.mapValues { (d, tokens) -> value(decision.kind, tokens.getValue(dotted), "$dotted ($d)") }
                    val follows = byDensity.values.toSet().size > 1
                    if (follows && values.values.toSet().size > 1) {
                        throw GeneratorError("$dotted varies by both colour scheme and density, which no Kotlin type models.")
                    }
                    included += ResolvedToken(path, decision.kind, values, if (follows) byDensity else null, sources)
                }
            }
        }
        this.included = included
        this.excluded = excluded
        // Each role's letter style names a generated `font.geometry.*` token.
        for (token in included.filter { it.kind == TokenKind.TYPOGRAPHY }) {
            for (value in token.themes.values) {
                val geometry = (value as TokenValue.Typography).geometry
                val target = included.firstOrNull { it.path == geometry }
                if (geometry.take(2) != listOf("font", "geometry") || target == null) {
                    throw GeneratorError("${token.dotted}: its fontGeometry {${geometry.joinToString(".")}} is not a generated font.geometry token.")
                }
            }
        }
    }

    /** Included tokens whose path starts with [prefix]. */
    fun tokensUnder(vararg prefix: String): List<ResolvedToken> = included.filter { it.path.take(prefix.size) == prefix.toList() }

    fun token(dotted: String): ResolvedToken = included.firstOrNull { it.dotted == dotted } ?: throw GeneratorError("$dotted is not a generated token.")

    companion object {
        const val COLOR_SCHEME = "colorScheme"
        const val DENSITY = "density"

        fun value(
            kind: TokenKind,
            token: DtcgToken,
            context: String,
        ): TokenValue = value(kind, token.value, context, token.qeetrixExtension)

        fun value(
            kind: TokenKind,
            value: JsonElement,
            context: String,
            extension: JsonObject? = null,
        ): TokenValue =
            when (kind) {
                TokenKind.COLOR -> {
                    val obj = value as? JsonObject ?: throw GeneratorError("$context: a colour that is not an object.")
                    TokenValue.Color(color(obj, context), obj["hex"]?.jsonPrimitive?.content)
                }

                TokenKind.DIMENSION, TokenKind.COMPONENT -> {
                    TokenValue.Dimension(px(value, context))
                }

                TokenKind.NUMBER -> {
                    TokenValue.Number(number(value, context))
                }

                TokenKind.DURATION -> {
                    val obj = value.jsonObject
                    val amount = obj["value"]!!.jsonPrimitive.double
                    when (val unit = obj["unit"]?.jsonPrimitive?.content) {
                        "ms" -> TokenValue.Duration(amount)
                        "s" -> TokenValue.Duration(amount * 1000)
                        else -> throw GeneratorError("$context: duration unit \"$unit\".")
                    }
                }

                TokenKind.EASING -> {
                    val points = value.jsonArray.map { it.jsonPrimitive.double }
                    if (points.size != 4) throw GeneratorError("$context: a cubic Bézier needs 4 numbers.")
                    TokenValue.Easing(points)
                }

                TokenKind.SHADOW -> {
                    TokenValue.Shadow(
                        value.jsonArray.map { layer ->
                            val obj = layer.jsonObject
                            ShadowLayer(
                                inset = obj["inset"]?.jsonPrimitive?.content == "true",
                                x = px(obj.need("offsetX", context), context),
                                y = px(obj.need("offsetY", context), context),
                                blur = px(obj.need("blur", context), context),
                                spread = px(obj.need("spread", context), context),
                                color = color(obj.need("color", context).jsonObject, context),
                            )
                        },
                    )
                }

                TokenKind.FONT_FAMILY -> {
                    TokenValue.FontFamily(families(value, context))
                }

                TokenKind.TYPOGRAPHY -> {
                    val obj = value.jsonObject
                    val size = px(obj.need("fontSize", context), context)
                    val weight = number(obj.need("fontWeight", context), context)
                    if (weight != Math.rint(weight)) throw GeneratorError("$context: font weight $weight is not a whole number.")
                    val spacing = px(obj.need("letterSpacing", context), context)
                    val ratio = number(obj.need("lineHeight", context), context)
                    // The line height as a length and the role's letter style are Qeetrix's extension of the composite
                    // (@qeetrix/tokens 0.0.13); the ratio DTCG requires is the length over the size.
                    val qeetrix = extension ?: throw GeneratorError("$context: no \$extensions[\"${DtcgToken.QEETRIX_EXTENSION}\"].")
                    val lineHeightPx = px(qeetrix.need("lineHeight", "$context \$extensions"), "$context \$extensions")
                    if (Math.abs(size * ratio - lineHeightPx) > LINE_HEIGHT_EPSILON) {
                        throw GeneratorError("$context: lineHeight $ratio × $size px is ${size * ratio} px; its extension says $lineHeightPx px.")
                    }
                    val geometry =
                        (qeetrix.need("fontGeometry", "$context \$extensions") as? JsonPrimitive)
                            ?.takeIf { it.isString }
                            ?.content
                            ?.let { REFERENCE.matchEntire(it)?.groupValues?.get(1) }
                            ?: throw GeneratorError("$context: fontGeometry is not a {reference}.")
                    TokenValue.Typography(
                        family = families(obj.need("fontFamily", context), context).first(),
                        sizePx = size,
                        weight = weight.toInt(),
                        lineHeight = ratio,
                        letterSpacingEm = spacing / size,
                        lineHeightPx = lineHeightPx,
                        geometry = geometry.split('.'),
                    )
                }
            }

        private fun JsonObject.need(
            key: String,
            context: String,
        ): JsonElement = this[key] ?: throw GeneratorError("$context: missing $key.")

        /** A DTCG colour: OKLCH components, converted per CSS Color 4 as a browser paints `oklch()`. */
        private fun color(
            obj: JsonObject,
            context: String,
        ): ColorMath.EncodedColor {
            val space = obj["colorSpace"]?.jsonPrimitive?.content
            if (space != "oklch") throw GeneratorError("$context: colour space \"$space\"; only oklch is converted.")
            val components = obj.need("components", context).jsonArray.map { it.jsonPrimitive.doubleOrNull ?: 0.0 } // "none" is 0, as in CSS
            if (components.size != 3) throw GeneratorError("$context: oklch needs 3 components.")
            val alpha = obj["alpha"]?.jsonPrimitive?.double ?: 1.0
            return ColorMath.encode(ColorMath.oklch(components[0], components[1], components[2], alpha))
        }

        private fun px(
            value: JsonElement,
            context: String,
        ): Double {
            val obj = value as? JsonObject ?: throw GeneratorError("$context: a dimension that is not { value, unit }.")
            val amount = obj.need("value", context).jsonPrimitive.double
            return when (val unit = obj["unit"]?.jsonPrimitive?.content) {
                "px" -> amount
                "rem" -> amount * REM_PX
                else -> throw GeneratorError("$context: dimension unit \"$unit\".")
            }
        }

        private fun number(
            value: JsonElement,
            context: String,
        ): Double = (value as? JsonPrimitive)?.doubleOrNull ?: throw GeneratorError("$context: $value is not a number.")

        private fun families(
            value: JsonElement,
            context: String,
        ): List<String> =
            when (value) {
                is JsonArray -> value.map { it.jsonPrimitive.content }
                is JsonPrimitive -> listOf(value.content)
                else -> throw GeneratorError("$context: a font family that is neither a name nor a list.")
            }

        /** 1 rem = 16 CSS px, the browser default the tokens are designed at. */
        const val REM_PX = 16.0

        /** How far `fontSize × lineHeight` may be from the authored length: double rounding only. */
        private const val LINE_HEIGHT_EPSILON = 1e-9

        private val REFERENCE = Regex("""^\{([^{}]+)\}$""")
    }
}
