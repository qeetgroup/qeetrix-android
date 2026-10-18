package com.qeetgroup.qeetrix.gen.tokens

import com.qeetgroup.qeetrix.gen.GeneratorError
import com.qeetgroup.qeetrix.gen.css.CssDeclarations
import com.qeetgroup.qeetrix.gen.css.CssVariableResolver
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * The upstream token artifacts as published in `@qeetrix/ui`: `tokens.json` (Style Dictionary's
 * resolved values per theme, compiled from the DTCG sources), `tokens.css` (the runtime variables; its
 * `--qx-*` names are the consumable layer — primitives are deliberately absent) and `index.css` (only
 * for the `--radius-*` scale, which the semantic `corner.*` tokens reference).
 */
class TokenSource(
    tokensJson: String,
    tokensCss: String,
    indexCss: String,
) {
    /** Token paths in `tokens.json` order, e.g. `["color", "text", "primary"]`. */
    val paths: List<List<String>>

    /** Raw values keyed by dotted path, then by theme. */
    val values: Map<String, Map<String, String>>

    /** The themes in registry order (base first). */
    val themes: List<String>

    /** The `--qx-*` names declared on `:root` in tokens.css: semantic + component, never primitives. */
    val consumableNames: Set<String>

    /** Variable tables per theme, for `var()` resolution. */
    val resolvers: Map<String, CssVariableResolver>

    init {
        val root = Json.parseToJsonElement(tokensJson) as? JsonObject ?: throw GeneratorError("tokens.json: top level is not an object.")
        themes = root.keys.filter { !it.startsWith("$") }
        if (themes.isEmpty()) throw GeneratorError("tokens.json: no themes.")
        for (theme in themes) {
            if (theme !in THEME_SELECTORS) {
                throw GeneratorError(
                    "tokens.json: theme \"$theme\" has no Android mapping. Add it to TokenSource.THEME_SELECTORS and to QeetrixTheme before regenerating.",
                )
            }
        }

        var basePaths: List<List<String>> = emptyList()
        val values = LinkedHashMap<String, MutableMap<String, String>>()
        for (theme in themes) {
            val leaves = mutableListOf<Pair<List<String>, String>>()
            flatten(root.getValue(theme), emptyList(), leaves)
            if (theme == themes[0]) {
                basePaths = leaves.map { it.first }
            } else {
                val base = basePaths.map { it.joinToString(".") }.toSet()
                val these = leaves.map { it.first.joinToString(".") }.toSet()
                if (base != these) {
                    throw GeneratorError(
                        "tokens.json: theme \"$theme\" breaks parity with \"${themes[0]}\" — missing ${(base - these).sorted().take(
                            5,
                        )}, extra ${(these - base).sorted().take(5)}.",
                    )
                }
            }
            for ((path, value) in leaves) values.getOrPut(path.joinToString(".")) { LinkedHashMap() }[theme] = value
        }
        paths = basePaths
        this.values = values

        // tokens.css: the consumable names and the per-theme variable tables.
        val perSelector = LinkedHashMap<String, MutableMap<String, String>>()
        for (declaration in CssDeclarations.parse(tokensCss)) {
            if (IGNORED_SELECTOR_PREFIXES.any { declaration.selector.startsWith(it) }) continue
            if (declaration.selector !in THEME_SELECTORS.values) {
                throw GeneratorError(
                    "tokens.css: unexpected selector \"${declaration.selector}\" — a new theme or scope needs an Android mapping.",
                )
            }
            perSelector.getOrPut(declaration.selector) { LinkedHashMap() }[declaration.name] = declaration.value
        }
        consumableNames =
            perSelector[":root"]
                .orEmpty()
                .keys
                .filter { it.startsWith("--qx-") }
                .toSet()

        // index.css: the `--radius-*` scale only. Anything else there is web layout, not a token.
        val radius = LinkedHashMap<String, String>()
        for (declaration in CssDeclarations.parse(indexCss)) {
            if (!declaration.name.startsWith("--radius-")) continue
            radius[declaration.name]?.let {
                if (it != declaration.value) throw GeneratorError("index.css: ${declaration.name} is declared twice with different values.")
            }
            radius[declaration.name] = declaration.value
        }

        val base = perSelector[":root"].orEmpty()
        resolvers =
            themes.associateWith { theme ->
                val table = LinkedHashMap(radius)
                table.putAll(base)
                val selector = THEME_SELECTORS.getValue(theme)
                if (selector != ":root") table.putAll(perSelector[selector].orEmpty())
                CssVariableResolver(table)
            }

        // The two artifacts come from one Style Dictionary run; prove it for every consumable token.
        for (path in paths) {
            val name = cssName(path)
            if (name !in consumableNames || path.first() == "component") continue
            for (theme in themes) {
                val css = perSelector[THEME_SELECTORS.getValue(theme)]?.get(name) ?: perSelector[":root"]?.get(name)
                val json = values[path.joinToString(".")]?.get(theme)
                if (css != json) {
                    throw GeneratorError("${path.joinToString(".")} ($theme): tokens.json says $json but tokens.css says $css.")
                }
            }
        }
    }

    companion object {
        /**
         * The selector each upstream theme's variables are published under (qeetrix-ui
         * `scripts/config/themes.json`). A theme missing here fails generation: it needs an Android
         * mapping first.
         */
        val THEME_SELECTORS: Map<String, String> = linkedMapOf("light" to ":root", "dark" to ".dark")
        private val IGNORED_SELECTOR_PREFIXES = listOf("[data-qx-density=")

        fun cssName(path: List<String>): String = "--qx-" + path.joinToString("-")

        private fun flatten(
            node: JsonElement,
            path: List<String>,
            leaves: MutableList<Pair<List<String>, String>>,
        ) {
            when (node) {
                is JsonObject -> for ((key, child) in node) if (!key.startsWith("$")) flatten(child, path + key, leaves)
                is JsonPrimitive -> leaves += path to node.content
                else -> throw GeneratorError("tokens.json: ${path.joinToString(".")} has an unsupported value.")
            }
        }
    }
}
