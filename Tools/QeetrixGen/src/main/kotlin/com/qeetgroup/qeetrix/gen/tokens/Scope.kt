package com.qeetgroup.qeetrix.gen.tokens

import com.qeetgroup.qeetrix.gen.GeneratorError

/** What a generated token's value is, and the DTCG `$type` it must have. */
enum class TokenKind(
    val dtcgType: String,
) {
    COLOR("color"),
    DIMENSION("dimension"),
    NUMBER("number"),
    DURATION("duration"),
    EASING("cubicBezier"),
    SHADOW("shadow"),
    TYPOGRAPHY("typography"),
    FONT_FAMILY("fontFamily"),

    /** A `component.<group>.*` token of a group Android implements (`Upstream/manifest.json`). */
    COMPONENT("dimension"),
}

sealed interface ScopeDecision {
    data class Include(
        val kind: TokenKind,
    ) : ScopeDecision

    data class Exclude(
        val reason: String,
    ) : ScopeDecision
}

/**
 * Which `@qeetrix/tokens` tokens become Kotlin, and why every other token does not.
 *
 * The rule is the package's own tier boundary: the semantic and component tiers are its API (what
 * `tokens.css` publishes as `--qx-*`), and the `primitive.*` palette and scales are private, so
 * product code can never reach a raw palette value. Within the public tiers every group needs a rule;
 * an unmatched token fails generation, so a new upstream group is a decision, not an accident. The
 * rules are qeetrix-ios's, so both platforms generate the same token set.
 */
object Scope {
    private class Rule(
        val pattern: List<String>,
        val decision: ScopeDecision,
    )

    private fun include(
        vararg pattern: String,
        kind: TokenKind,
    ) = Rule(pattern.toList(), ScopeDecision.Include(kind))

    private fun exclude(
        vararg pattern: String,
        reason: String,
    ) = Rule(pattern.toList(), ScopeDecision.Exclude(reason))

    const val PRIMITIVE_REASON =
        "Primitive (`primitive.*`): private to @qeetrix/tokens and not published as CSS. Semantic tokens carry the decision; product code never reads a raw palette or scale value."

    const val UNADOPTED_COMPONENT_REASON =
        "Component tokens for a component Android does not implement. Adopted groups are listed in Upstream/manifest.json."

    private val rules =
        listOf(
            exclude("primitive", "**", reason = PRIMITIVE_REASON),
            include("color", "**", kind = TokenKind.COLOR),
            include("shadow", "*", kind = TokenKind.SHADOW),
            include("typography", "*", "*", kind = TokenKind.TYPOGRAPHY),
            exclude(
                "font",
                "family",
                "indic",
                reason =
                    "A Noto stack for Indian scripts. Qeet Sans has no Indic glyphs and Android already draws them in its system Noto fonts, character by character, so nothing is bundled for it.",
            ),
            include("font", "family", "*", kind = TokenKind.FONT_FAMILY),
            include("font", "geometry", "*", kind = TokenKind.NUMBER),
            include("space", "**", kind = TokenKind.DIMENSION),
            include("size", "**", kind = TokenKind.DIMENSION),
            include("radius", "*", kind = TokenKind.DIMENSION),
            include("border", "width", "*", kind = TokenKind.DIMENSION),
            include("focus", "ring", "*", kind = TokenKind.DIMENSION),
            include("icon", "stroke", "*", kind = TokenKind.NUMBER),
            include("opacity", "*", kind = TokenKind.NUMBER),
            include("motion", "duration", "*", kind = TokenKind.DURATION),
            include("motion", "easing", "*", kind = TokenKind.EASING),
            include("motion", "spring", "*", "*", kind = TokenKind.NUMBER),
            include("breakpoint", "*", kind = TokenKind.DIMENSION),
            exclude(
                "z",
                "**",
                reason =
                    "Web stacking order. Compose zIndex only orders siblings and overlays are separate windows, so a global scale has no meaning (the package says z-index is web-only).",
            ),
            exclude("component", "**", reason = UNADOPTED_COMPONENT_REASON),
        )

    /**
     * @param adoptedComponents the `component.<group>` names Android implements
     *   (`qeetrixTokens.componentTokens` in Upstream/manifest.json) — the same groups as qeetrix-ios.
     */
    fun decide(
        path: List<String>,
        adoptedComponents: Set<String> = emptySet(),
    ): ScopeDecision {
        if (path.first() == "component" && path.size > 2 && path[1] in adoptedComponents) {
            return ScopeDecision.Include(TokenKind.COMPONENT)
        }
        rules.firstOrNull { matches(it.pattern, path) }?.let { return it.decision }
        throw GeneratorError("${path.joinToString(".")}: no scope rule. Decide whether Android consumes it and add a rule to Scope.kt.")
    }

    /** `*` matches one segment, `**` the rest (at least one). */
    fun matches(
        pattern: List<String>,
        path: List<String>,
    ): Boolean {
        for ((index, segment) in pattern.withIndex()) {
            if (segment == "**") return path.size > index
            if (index >= path.size || (segment != "*" && segment != path[index])) return false
        }
        return pattern.size == path.size
    }
}
