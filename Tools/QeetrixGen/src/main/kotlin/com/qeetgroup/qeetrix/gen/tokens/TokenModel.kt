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
) {
    val themes: List<String> = source.themes
    val included: List<ResolvedToken>
    val excluded: List<ExcludedToken>

    init {
        val included = mutableListOf<ResolvedToken>()
        val excluded = mutableListOf<ExcludedToken>()
        for (path in source.paths) {
            val consumable = TokenSource.cssName(path) in source.consumableNames
            when (val decision = Scope.decide(path, consumable)) {
                is ScopeDecision.Exclude -> {
                    excluded += ExcludedToken(path, decision.reason)
                }

                is ScopeDecision.Include -> {
                    val values = LinkedHashMap<String, TokenValue>()
                    val inputs = LinkedHashMap<String, String>()
                    for (theme in source.themes) {
                        val context = "${path.joinToString(".")} ($theme)"
                        val raw = source.values[path.joinToString(".")]?.get(theme) ?: throw GeneratorError("$context: missing value.")
                        val resolver = source.resolvers[theme] ?: throw GeneratorError("$context: missing resolver.")
                        val substituted = resolver.substitute(raw, context)
                        inputs[theme] = substituted
                        values[theme] = value(decision.kind, CssParser.parse(substituted, context), context)
                    }
                    included += ResolvedToken(path, decision.kind, consumable, values, inputs)
                }
            }
        }
        this.included = included
        this.excluded = excluded
    }

    /** Included tokens whose path starts with [prefix]. */
    fun tokensUnder(vararg prefix: String): List<ResolvedToken> = included.filter { it.path.take(prefix.size) == prefix.toList() }

    companion object {
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
            }
    }
}
