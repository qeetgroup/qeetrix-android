package com.qeetgroup.qeetrix.gen.tokens

import com.qeetgroup.qeetrix.gen.GeneratorError
import com.qeetgroup.qeetrix.gen.color.ColorMath
import com.qeetgroup.qeetrix.gen.color.ColorValues
import com.qeetgroup.qeetrix.gen.css.CssComponent
import com.qeetgroup.qeetrix.gen.css.CssParser

sealed interface TokenValue {
    data class Color(
        val color: ColorMath.EncodedColor,
    ) : TokenValue

    data class Dimension(
        val dp: Double,
    ) : TokenValue

    data class Em(
        val em: Double,
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
        val layers: List<CssValues.ShadowLayer>,
    ) : TokenValue

    data class FontFamily(
        val name: String,
    ) : TokenValue

    /** A component token that names a semantic colour (`var(--qx-color-…)`), with its resolved value. */
    data class ColorRef(
        val path: String,
        val resolved: ColorMath.EncodedColor,
    ) : TokenValue

    /** A component token that names an elevation (`var(--qx-elevation-…)`). */
    data class ElevationRef(
        val path: String,
    ) : TokenValue

    /** A component token that follows the active density (`var(--qx-density-<metric>, …-default)`). */
    data class DensityRef(
        val metric: String,
    ) : TokenValue

    /** A percentage of the element's own size, as a fraction (`25%` → 0.25). */
    data class Percent(
        val fraction: Double,
    ) : TokenValue
}

data class ResolvedToken(
    val path: List<String>,
    val kind: TokenKind,
    /** Whether the token is in tokens.css's consumable layer (false for the allow-listed primitives). */
    val consumable: Boolean,
    /** Value per theme, in registry order. */
    val values: Map<String, TokenValue>,
    /** The CSS expression each theme's value was computed from, after `var()` substitution. */
    val inputs: Map<String, String>,
    /** The upstream expression per theme, as tokens.json publishes it (before substitution). */
    val raws: Map<String, String> = emptyMap(),
) {
    val dotted: String get() = path.joinToString(".")
    val cssName: String get() = TokenSource.cssName(path)
    val isThemeInvariant: Boolean get() = values.values.toSet().size == 1
}

data class ExcludedToken(
    val path: List<String>,
    val reason: String,
)

/** Every upstream token, either resolved into a typed value or excluded with a reason. */
class TokenModel(
    source: TokenSource,
    /** The `component.<group>` names adopted in Upstream/manifest.json. */
    val adoptedComponents: Set<String> = emptySet(),
) {
    val themes: List<String> = source.themes
    val included: List<ResolvedToken>
    val excluded: List<ExcludedToken>

    init {
        val upstreamGroups =
            source.paths
                .filter { it.first() == "component" && it.size > 2 }
                .map { it[1] }
                .toSet()
        val unknown = adoptedComponents - upstreamGroups
        if (unknown.isNotEmpty()) {
            throw GeneratorError("Upstream/manifest.json adopts component groups upstream does not define: ${unknown.sorted()}.")
        }
        // The consumable colour and elevation variables a component token may name, by CSS name.
        val references =
            source.paths
                .filter { (it.first() == "color" || it.first() == "elevation") && TokenSource.cssName(it) in source.consumableNames }
                .associate { TokenSource.cssName(it) to it.joinToString(".") }

        val included = mutableListOf<ResolvedToken>()
        val excluded = mutableListOf<ExcludedToken>()
        for (path in source.paths) {
            val consumable = TokenSource.cssName(path) in source.consumableNames
            when (val decision = Scope.decide(path, consumable, adoptedComponents)) {
                is ScopeDecision.Exclude -> {
                    excluded += ExcludedToken(path, decision.reason)
                }

                is ScopeDecision.Include -> {
                    val values = LinkedHashMap<String, TokenValue>()
                    val inputs = LinkedHashMap<String, String>()
                    val raws = LinkedHashMap<String, String>()
                    for (theme in source.themes) {
                        val context = "${path.joinToString(".")} ($theme)"
                        val raw = source.values[path.joinToString(".")]?.get(theme) ?: throw GeneratorError("$context: missing value.")
                        val resolver = source.resolvers[theme] ?: throw GeneratorError("$context: missing resolver.")
                        val substituted = resolver.substitute(raw, context)
                        inputs[theme] = substituted
                        raws[theme] = raw
                        val components = CssParser.parse(substituted, context)
                        values[theme] =
                            if (decision.kind == TokenKind.COMPONENT) {
                                component(raw, components, references, context)
                            } else {
                                value(decision.kind, components, context)
                            }
                    }
                    included += ResolvedToken(path, decision.kind, consumable, values, inputs, raws)
                }
            }
        }
        this.included = included
        this.excluded = excluded
    }

    /** Included tokens whose path starts with [prefix]. */
    fun tokensUnder(vararg prefix: String): List<ResolvedToken> = included.filter { it.path.take(prefix.size) == prefix.toList() }

    companion object {
        private val SINGLE_VAR = Regex("""^var\(\s*(--qx-[a-z0-9-]+)\s*\)$""")
        private val DENSITY_VAR = Regex("""^var\(\s*--qx-density-([a-z-]+)\s*,\s*var\(\s*--qx-density-\1-default\s*\)\s*\)$""")
        private val COLOR_FUNCTIONS = setOf("oklch", "rgb", "rgba", "color-mix")

        fun value(
            kind: TokenKind,
            components: List<CssComponent>,
            context: String,
        ): TokenValue =
            when (kind) {
                TokenKind.COLOR -> TokenValue.Color(ColorMath.encode(ColorValues.color(components, context)))
                TokenKind.DIMENSION -> TokenValue.Dimension(CssValues.dimension(components, context))
                TokenKind.EM -> TokenValue.Em(CssValues.em(components, context))
                TokenKind.NUMBER -> TokenValue.Number(CssValues.number(components, context))
                TokenKind.DURATION -> TokenValue.Duration(CssValues.durationMillis(components, context))
                TokenKind.EASING -> TokenValue.Easing(CssValues.easing(components, context))
                TokenKind.SHADOW -> TokenValue.Shadow(CssValues.shadows(components, context))
                TokenKind.FONT_FAMILY -> TokenValue.FontFamily(CssValues.primaryFontFamily(components, context))
                TokenKind.COMPONENT -> throw GeneratorError("$context: component values need their raw expression.")
            }

        /**
         * A component token's value. A reference upstream writes — a semantic colour, an elevation, a
         * density metric — stays a reference, so the component follows the theme and density exactly as
         * the web does; anything else is resolved by its syntax.
         */
        fun component(
            raw: String,
            components: List<CssComponent>,
            references: Map<String, String>,
            context: String,
        ): TokenValue {
            DENSITY_VAR.matchEntire(raw.trim())?.let { return TokenValue.DensityRef(it.groupValues[1]) }
            SINGLE_VAR.matchEntire(raw.trim())?.let { match ->
                val path = references[match.groupValues[1]]
                when {
                    path == null -> Unit

                    // another variable (the radius scale): resolve it below
                    path.startsWith("color.") -> return TokenValue.ColorRef(path, ColorMath.encode(ColorValues.color(components, context)))

                    else -> return TokenValue.ElevationRef(path)
                }
            }
            val single = components.singleOrNull()
            return when {
                single is CssComponent.Number && single.unit == "%" -> {
                    TokenValue.Percent(single.value / 100)
                }

                single is CssComponent.Number && single.unit == "em" -> {
                    TokenValue.Em(single.value)
                }

                single is CssComponent.Number && single.unit.isEmpty() && single.value != 0.0 -> {
                    TokenValue.Number(single.value)
                }

                single == CssComponent.Ident("transparent") || (single is CssComponent.Function && single.name in COLOR_FUNCTIONS) -> {
                    TokenValue.Color(ColorMath.encode(ColorValues.color(components, context)))
                }

                else -> {
                    TokenValue.Dimension(CssValues.dimension(components, context))
                }
            }
        }
    }
}
