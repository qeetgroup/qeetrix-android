package com.qeetgroup.qeetrix.gen.tokens

import com.qeetgroup.qeetrix.gen.GeneratorError

/** What a generated token's value is. */
enum class TokenKind { COLOR, DIMENSION, EM, NUMBER, DURATION, EASING, SHADOW, FONT_FAMILY }

sealed interface ScopeDecision {
    data class Include(
        val kind: TokenKind,
    ) : ScopeDecision

    data class Exclude(
        val reason: String,
    ) : ScopeDecision
}

/**
 * Which upstream tokens become Kotlin, and why every other token does not.
 *
 * The rule mirrors qeetrix-ui's own runtime boundary: what `tokens.css` publishes as `--qx-*` (the
 * semantic and component layers) is consumable, and primitives are not — so product code cannot
 * reach a palette value. The one exception is the short list of non-colour primitive scales that
 * qeetrix-ui itself hands to code (`src/lib/token-values.ts`: spacing, icon size and stroke, stroke
 * width) plus the font weight and geometry scales the typography roles are built from.
 *
 * The rules are qeetrix-ios's (`Tools/QeetrixGen/.../Tokens/Scope.swift`), so both platforms
 * generate the same token set; only the wording of platform-specific reasons differs. Every
 * consumable token must match a rule; an unmatched one fails generation, so a new upstream group is
 * a decision, not an accident.
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

    private val consumableRules =
        listOf(
            include("color", "**", kind = TokenKind.COLOR),
            include("typography", "*", "font-family", kind = TokenKind.FONT_FAMILY),
            include("typography", "*", "font-size", kind = TokenKind.DIMENSION),
            include("typography", "*", "font-weight", kind = TokenKind.NUMBER),
            include("typography", "*", "line-height", kind = TokenKind.NUMBER),
            include("typography", "*", "letter-spacing", kind = TokenKind.EM),
            include("typography", "*", "font-geometry", kind = TokenKind.NUMBER),
            exclude("corner", "circle", reason = "50% is a shape, not a length — use CircleShape."),
            include("corner", "*", kind = TokenKind.DIMENSION),
            exclude("control", "*", reason = "Resolves through the active density — read QeetrixDensity instead."),
            include("density", "*", "*", kind = TokenKind.DIMENSION),
            exclude("focus", "ring-style", reason = "A CSS border-style keyword; Compose strokes are solid unless a path effect is set."),
            include("focus", "*", kind = TokenKind.DIMENSION),
            include("motion", "duration", "*", kind = TokenKind.DURATION),
            include("motion", "easing", "*", kind = TokenKind.EASING),
            include("motion", "reduced", "duration", kind = TokenKind.DURATION),
            include("motion", "reduced", "easing", kind = TokenKind.EASING),
            include("elevation", "**", kind = TokenKind.SHADOW),
            include("state", "opacity", "*", kind = TokenKind.NUMBER),
            exclude(
                "z",
                "**",
                reason =
                    "Web stacking order. Compose zIndex only orders siblings and overlays are separate windows, " +
                        "so a global scale has no meaning.",
            ),
            exclude("gradient", "**", reason = "Not in the Phase 1 foundation set."),
            exclude("component", "**", reason = "Component layer — arrives in Phase 2 with the components that consume it."),
        )

    private val primitiveAllowList =
        listOf(
            include("space", "*", kind = TokenKind.DIMENSION),
            include("icon", "size", "*", kind = TokenKind.DIMENSION),
            include("icon", "stroke", "*", kind = TokenKind.NUMBER),
            include("stroke", "width", "*", kind = TokenKind.DIMENSION),
            include("font", "weight", "*", kind = TokenKind.NUMBER),
            include("font", "geometry", "*", kind = TokenKind.NUMBER),
        )

    const val PRIMITIVE_REASON =
        "Primitive — not exposed. Semantic tokens carry the decision; product code never reads a raw scale or palette value."

    fun decide(
        path: List<String>,
        consumable: Boolean,
    ): ScopeDecision {
        val rules = if (consumable) consumableRules else primitiveAllowList
        rules.firstOrNull { matches(it.pattern, path) }?.let { return it.decision }
        if (consumable) {
            throw GeneratorError("${path.joinToString(".")}: no scope rule. Decide whether Android consumes it and add a rule to Scope.kt.")
        }
        return ScopeDecision.Exclude(PRIMITIVE_REASON)
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
