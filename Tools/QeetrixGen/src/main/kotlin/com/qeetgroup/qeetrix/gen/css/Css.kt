package com.qeetgroup.qeetrix.gen.css

import com.qeetgroup.qeetrix.gen.GeneratorError

// A deliberately small CSS reader. It understands exactly the value grammar Qeetrix's generated
// stylesheets use for the tokens Android consumes, and rejects everything else by name, so an
// upstream change that introduces new syntax fails the build instead of producing a quietly wrong
// value. Same grammar and behaviour as qeetrix-ios's reader.

/** One custom-property declaration and the selector block it sits in. */
data class CssDeclaration(
    val selector: String,
    val name: String,
    val value: String,
)

object CssDeclarations {
    /**
     * Every `--name: value` declaration in [css], with the innermost enclosing selector. Comments are
     * ignored; nested at-rule blocks (`@theme inline { … }`) are walked.
     */
    fun parse(css: String): List<CssDeclaration> {
        val text = stripComments(css)
        val stack = ArrayDeque<String>()
        val buffer = StringBuilder()
        val result = mutableListOf<CssDeclaration>()

        fun flush() {
            val statement = buffer.toString().trim()
            buffer.clear()
            if (!statement.startsWith("--")) return
            val colon = statement.indexOf(':')
            if (colon < 0) return
            result +=
                CssDeclaration(
                    selector = stack.lastOrNull() ?: "",
                    name = statement.substring(0, colon).trim(),
                    value = statement.substring(colon + 1).trim(),
                )
        }

        var depth = 0 // parentheses, so `;` or `{` inside a function never splits a value
        for (character in text) {
            when {
                character == '(' -> {
                    depth++
                    buffer.append(character)
                }

                character == ')' -> {
                    depth--
                    buffer.append(character)
                }

                character == '{' && depth == 0 -> {
                    stack.addLast(buffer.toString().trim())
                    buffer.clear()
                }

                character == '}' && depth == 0 -> {
                    flush()
                    stack.removeLastOrNull()
                }

                character == ';' && depth == 0 -> {
                    flush()
                }

                else -> {
                    buffer.append(character)
                }
            }
        }
        return result
    }

    fun stripComments(css: String): String {
        val out = StringBuilder()
        var index = 0
        while (index < css.length) {
            if (css.startsWith("/*", index)) {
                val end = css.indexOf("*/", index + 2)
                if (end < 0) break
                index = end + 2
            } else {
                out.append(css[index])
                index++
            }
        }
        return out.toString()
    }
}

/** Resolves `var(--name, fallback)` references against a variable table. */
class CssVariableResolver(
    val variables: Map<String, String>,
) {
    /** [value] with every `var()` replaced by its declared value, recursively. */
    fun substitute(
        value: String,
        context: String,
    ): String = substitute(value, context, emptyList())

    private fun substitute(
        value: String,
        context: String,
        visiting: List<String>,
    ): String {
        val output = StringBuilder()
        var index = 0
        while (true) {
            val start = value.indexOf("var(", index)
            if (start < 0) break
            output.append(value, index, start)
            val (arguments, end) = balancedArguments(value, start + 4, context)
            val parts = splitTopLevel(arguments, ',', maxSplits = 1)
            val name = parts[0].trim()
            if (!name.startsWith("--")) {
                throw GeneratorError("$context: var() names \"$name\", which is not a custom property.")
            }
            if (name in visiting) {
                throw GeneratorError("$context: reference cycle ${(visiting + name).joinToString(" → ")}.")
            }
            val declared = variables[name]
            when {
                declared != null -> output.append(substitute(declared, context, visiting + name))
                parts.size == 2 -> output.append(substitute(parts[1].trim(), context, visiting))
                else -> throw GeneratorError("$context: unresolved reference $name — no declaration and no fallback.")
            }
            index = end
        }
        output.append(value, index, value.length)
        return output.toString()
    }

    companion object {
        /** The text between `(` (already consumed) and its matching `)`, and the index after the `)`. */
        fun balancedArguments(
            value: String,
            start: Int,
            context: String,
        ): Pair<String, Int> {
            var depth = 1
            var index = start
            while (index < value.length) {
                when (value[index]) {
                    '(' -> {
                        depth++
                    }

                    ')' -> {
                        depth--
                        if (depth == 0) return value.substring(start, index) to index + 1
                    }
                }
                index++
            }
            throw GeneratorError("$context: unbalanced parentheses in \"$value\".")
        }

        fun splitTopLevel(
            text: String,
            separator: Char,
            maxSplits: Int = Int.MAX_VALUE,
        ): List<String> {
            val parts = mutableListOf<String>()
            val current = StringBuilder()
            var depth = 0
            for (character in text) {
                if (character == '(') depth++
                if (character == ')') depth--
                if (character == separator && depth == 0 && parts.size < maxSplits) {
                    parts += current.toString()
                    current.clear()
                } else {
                    current.append(character)
                }
            }
            parts += current.toString()
            return parts
        }
    }
}

/** A parsed CSS component value. */
sealed interface CssComponent {
    data class Number(
        val value: Double,
        val unit: String,
    ) : CssComponent

    data class Ident(
        val name: String,
    ) : CssComponent

    data class Text(
        val value: String,
    ) : CssComponent

    data class Function(
        val name: String,
        val arguments: List<CssComponent>,
    ) : CssComponent

    data class Delimiter(
        val character: Char,
    ) : CssComponent
}

object CssParser {
    /**
     * Parses a resolved (var-free) value into components. Whitespace separates components and is
     * otherwise dropped; commas, slashes and calc() operators are kept as delimiters.
     */
    fun parse(
        value: String,
        context: String,
    ): List<CssComponent> = Scanner(value, context).components(untilClosingParenthesis = false)

    private class Scanner(
        val characters: String,
        val context: String,
    ) {
        var position = 0
        val current: Char? get() = characters.getOrNull(position)

        fun components(untilClosingParenthesis: Boolean): List<CssComponent> {
            val result = mutableListOf<CssComponent>()
            while (true) {
                val character = current ?: break
                when {
                    character.isWhitespace() -> {
                        position++
                    }

                    character == ')' -> {
                        if (!untilClosingParenthesis) throw GeneratorError("$context: unexpected \")\".")
                        position++
                        return result
                    }

                    character == ',' || character == '/' || character == '*' -> {
                        result += CssComponent.Delimiter(character)
                        position++
                    }

                    (character == '+' || character == '-') && startsNumber(position) -> {
                        result += number()
                    }

                    character == '+' || character == '-' -> {
                        result += CssComponent.Delimiter(character)
                        position++
                    }

                    character.isDigit() || character == '.' -> {
                        result += number()
                    }

                    character == '"' || character == '\'' -> {
                        result += CssComponent.Text(string(character))
                    }

                    character.isLetter() || character == '_' -> {
                        val name = identifier()
                        if (current == '(') {
                            position++
                            result += CssComponent.Function(name.lowercase(), components(untilClosingParenthesis = true))
                        } else {
                            result += CssComponent.Ident(name)
                        }
                    }

                    else -> {
                        throw GeneratorError("$context: unsupported character \"$character\".")
                    }
                }
            }
            if (untilClosingParenthesis) throw GeneratorError("$context: missing \")\".")
            return result
        }

        // "-1px" is a number; "a - b" in calc() is an operator (CSS requires the spaces).
        fun startsNumber(index: Int): Boolean {
            val next = characters.getOrNull(index + 1) ?: return false
            val previous = characters.getOrNull(index - 1)
            val previousIsSpaceOrStart = previous == null || previous.isWhitespace() || previous == '(' || previous == ','
            return previousIsSpaceOrStart && (next.isDigit() || next == '.')
        }

        fun number(): CssComponent {
            val text = StringBuilder()
            current?.let {
                if (it == '+' || it == '-') {
                    text.append(it)
                    position++
                }
            }
            while (current?.let { it.isDigit() || it == '.' } == true) {
                text.append(current)
                position++
            }
            val e = current
            val next = characters.getOrNull(position + 1)
            if ((e == 'e' || e == 'E') && next != null && (next.isDigit() || next == '-' || next == '+')) {
                text.append(e)
                position++
                current?.let {
                    if (it == '-' || it == '+') {
                        text.append(it)
                        position++
                    }
                }
                while (current?.isDigit() == true) {
                    text.append(current)
                    position++
                }
            }
            val value = text.toString().toDoubleOrNull() ?: throw GeneratorError("$context: \"$text\" is not a number.")
            val unit = StringBuilder()
            if (current == '%') {
                unit.append('%')
                position++
            } else {
                while (current?.isLetter() == true) {
                    unit.append(current)
                    position++
                }
            }
            return CssComponent.Number(value, unit.toString().lowercase())
        }

        fun identifier(): String {
            val text = StringBuilder()
            while (current?.let { it.isLetterOrDigit() || it == '-' || it == '_' } == true) {
                text.append(current)
                position++
            }
            return text.toString()
        }

        fun string(quote: Char): String {
            position++
            val text = StringBuilder()
            while (true) {
                val character = current ?: throw GeneratorError("$context: unterminated string.")
                position++
                if (character == quote) return text.toString()
                text.append(character)
            }
        }
    }
}

/** Splits a component list on top-level comma delimiters. */
fun splitOnCommas(components: List<CssComponent>): List<List<CssComponent>> {
    val groups = mutableListOf(mutableListOf<CssComponent>())
    for (component in components) {
        if (component == CssComponent.Delimiter(',')) groups += mutableListOf<CssComponent>() else groups.last() += component
    }
    return groups
}
