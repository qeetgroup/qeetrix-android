package com.qeetgroup.qeetrix.gen

import java.security.MessageDigest
import java.util.Locale

/** A generation failure with a message that names the file, token or element at fault. */
class GeneratorError(
    message: String,
) : Exception(message)

/**
 * Token path → Kotlin identifier, one rule for every generated name, matching qeetrix-ios's naming
 * so the same token has the same name on both platforms (modulo the platforms' case conventions).
 *
 * Segments are joined in camelCase (`text.on-brand` → `textOnBrand`). Identifiers cannot start with
 * a digit, so a leading numeric prefix moves to the end (`2xl` → `xl2`, `2xs` → `xs2`) and a bare
 * `0` becomes `zero`. Any other leading digit fails generation rather than inventing a spelling.
 */
object KotlinNames {
    fun camel(
        segments: List<String>,
        context: String,
    ): String {
        val words = segments.flatMap { segment -> segment.split('-', '_').filter { it.isNotEmpty() } }
        var first = words.firstOrNull() ?: throw GeneratorError("$context: empty identifier.")
        if (first.first().isDigit()) {
            first =
                when {
                    first == "0" -> {
                        "zero"
                    }

                    first.any { !it.isDigit() } -> {
                        val letters = first.indexOfFirst { !it.isDigit() }
                        first.substring(letters) + first.substring(0, letters)
                    }

                    else -> {
                        throw GeneratorError("$context: \"$first\" cannot start an identifier.")
                    }
                }
        }
        val rest = words.drop(1).joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }
        val name = first.replaceFirstChar(Char::lowercaseChar) + rest
        if (!name.all { it.isLetterOrDigit() }) throw GeneratorError("$context: \"$name\" is not a valid identifier.")
        return name
    }

    /** PascalCase, for enum entries and constants (`2xl` → `Xl2`). */
    fun pascal(
        segments: List<String>,
        context: String,
    ): String = camel(segments, context).replaceFirstChar(Char::uppercaseChar)

    /** Fails when two tokens fold to the same identifier. */
    fun requireUnique(
        names: List<Pair<String, String>>,
        scope: String,
    ) {
        val seen = HashMap<String, String>()
        for ((name, source) in names) {
            seen[name]?.let { throw GeneratorError("$scope: $source and $it both become \"$name\".") }
            seen[name] = source
        }
    }
}

/** Rounds half away from zero to six decimals, normalising -0, as qeetrix-ios does. */
fun round6(value: Double): Double {
    val rounded = Math.round(value * 1_000_000).toDouble() / 1_000_000
    return if (rounded == 0.0) 0.0 else rounded
}

/** Deterministic number text: at most six decimals, no trailing zeros, no `-0`, locale-independent. */
fun numberText(value: Double): String {
    val rounded = round6(value)
    if (rounded == Math.rint(rounded) && Math.abs(rounded) < 1e15) return rounded.toLong().toString()
    return String.format(Locale.ROOT, "%.6f", rounded).trimEnd('0').trimEnd('.')
}

/** A Kotlin `Float` literal: `0.5f`, `2f`, `-1f`. */
fun floatLiteral(value: Double): String = numberText(value) + "f"

/** A Kotlin string literal. */
fun kotlinString(value: String): String = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"").replace("$", "\\$") + "\""

fun sha256(bytes: ByteArray): String = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
