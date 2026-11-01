package com.qeetgroup.qeetrix.gen.tokens

import com.qeetgroup.qeetrix.gen.GeneratorError
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * One token of a resolved DTCG document.
 *
 * @property raw the `$value` as the winning source file authors it (a `{reference}` stays one).
 * @property value the `$value` with every reference followed, composites included.
 * @property chain the references followed from [raw] to the first non-reference value, in order.
 * @property source the file, relative to the DTCG directory, whose definition won.
 * @property extensions the winning definition's `$extensions`, as authored (a `{reference}` inside stays one).
 */
data class DtcgToken(
    val path: List<String>,
    val type: String,
    val raw: JsonElement,
    val value: JsonElement,
    val chain: List<String>,
    val source: String,
    val description: String?,
    val extensions: JsonObject? = null,
) {
    val dotted: String get() = path.joinToString(".")

    /** Qeetrix's own extension (`$extensions["in.qeet.qeetrix"]`), or null. */
    val qeetrixExtension: JsonObject? get() = extensions?.get(QEETRIX_EXTENSION) as? JsonObject

    companion object {
        const val QEETRIX_EXTENSION = "in.qeet.qeetrix"
    }
}

/**
 * `@qeetrix/tokens`' DTCG 2025.10 build: the resolver, the source sets it names, and the resolved
 * light and dark documents the package also publishes.
 *
 * Resolution follows the resolver exactly: the foundation set's sources in order, then one context
 * of each modifier in `resolutionOrder`; a later definition of a token replaces an earlier one. Group
 * `$type`s are inherited, references (`{a.b.c}`) are followed inside composite values too, and a
 * shadow layer without `inset` is an outer shadow. [verifyPublished] then proves the published
 * per-scheme documents are what the resolver gives, so the two sources can never silently disagree.
 *
 * @param files DTCG-directory-relative path → content.
 */
class DtcgSource(
    private val files: Map<String, String>,
) {
    private val resolverJson: JsonObject =
        parse(RESOLVER).jsonObject

    /** The modifiers in resolution order, each with its contexts in declaration order and its default. */
    val modifiers: List<Modifier>

    data class Modifier(
        val name: String,
        val contexts: Map<String, List<String>>,
        val default: String,
    )

    private val foundationSources: List<String>

    init {
        val sets = resolverJson["sets"]?.jsonObject ?: throw GeneratorError("$RESOLVER: no sets.")
        val modifierJson = resolverJson["modifiers"]?.jsonObject ?: throw GeneratorError("$RESOLVER: no modifiers.")
        val order = resolverJson["resolutionOrder"]?.jsonArray ?: throw GeneratorError("$RESOLVER: no resolutionOrder.")
        val setSources = mutableListOf<String>()
        val mods = mutableListOf<Modifier>()
        for (step in order) {
            val ref = step.jsonObject["\$ref"]?.jsonPrimitive?.content ?: throw GeneratorError("$RESOLVER: a resolutionOrder entry without \$ref.")
            when {
                ref.startsWith("#/sets/") -> {
                    val set = sets[ref.removePrefix("#/sets/")]?.jsonObject ?: throw GeneratorError("$RESOLVER: $ref is not a set.")
                    setSources += refs(set["sources"]!!.jsonArray, ref)
                }

                ref.startsWith("#/modifiers/") -> {
                    val name = ref.removePrefix("#/modifiers/")
                    val modifier = modifierJson[name]?.jsonObject ?: throw GeneratorError("$RESOLVER: $ref is not a modifier.")
                    val contexts = modifier["contexts"]!!.jsonObject.mapValues { (context, sources) -> refs(sources.jsonArray, "$ref/$context") }
                    val default = modifier["default"]?.jsonPrimitive?.content ?: throw GeneratorError("$RESOLVER: $ref has no default.")
                    mods += Modifier(name, LinkedHashMap(contexts), default)
                }

                else -> {
                    throw GeneratorError("$RESOLVER: unsupported resolutionOrder entry $ref.")
                }
            }
        }
        if (setSources.isEmpty()) throw GeneratorError("$RESOLVER: the resolution order names no set.")
        foundationSources = setSources
        modifiers = mods
    }

    fun modifier(name: String): Modifier = modifiers.firstOrNull { it.name == name } ?: throw GeneratorError("$RESOLVER has no modifier \"$name\".")

    /** Every token for one context per modifier (a modifier left out takes its default), by dotted path. */
    fun resolve(contexts: Map<String, String>): Map<String, DtcgToken> {
        val unknown = contexts.keys - modifiers.map { it.name }.toSet()
        if (unknown.isNotEmpty()) throw GeneratorError("$RESOLVER has no modifier ${unknown.sorted()}.")
        val sources = foundationSources.toMutableList()
        for (modifier in modifiers) {
            val context = contexts[modifier.name] ?: modifier.default
            sources += modifier.contexts[context] ?: throw GeneratorError("$RESOLVER: ${modifier.name} has no context \"$context\".")
        }
        val definitions = LinkedHashMap<String, Definition>()
        for (source in sources) {
            flatten(parse(source).jsonObject, emptyList(), null, source) { definition ->
                val key = definition.path.joinToString(".")
                val earlier = definitions.remove(key)
                definitions[key] = if (definition.type == null && earlier != null) definition.copy(type = earlier.type) else definition
            }
        }
        val label = contexts.entries.joinToString(", ") { "${it.key}=${it.value}" }.ifEmpty { "defaults" }
        return resolveAll(definitions, "resolver ($label)")
    }

    /**
     * Fails unless the published resolved document (`light.tokens.json`) holds exactly the tokens, types
     * and final values the resolver gives for its recorded input.
     */
    fun verifyPublished(
        file: String,
        contexts: Map<String, String>,
    ) {
        val published = parse(file).jsonObject
        val definitions = LinkedHashMap<String, Definition>()
        flatten(published, emptyList(), null, file) { definitions[it.path.joinToString(".")] = it }
        val fromPublished = resolveAll(definitions, file)
        val fromResolver = resolve(contexts)
        val problems = mutableListOf<String>()
        for (path in (fromPublished.keys + fromResolver.keys).toSortedSet()) {
            val a = fromPublished[path]
            val b = fromResolver[path]
            when {
                a == null -> problems += "$path: the resolver defines it; $file does not"
                b == null -> problems += "$path: $file defines it; the resolver does not"
                a.type != b.type -> problems += "$path: type ${a.type} in $file, ${b.type} from the resolver"
                a.value != b.value -> problems += "$path: ${a.value} in $file, ${b.value} from the resolver"
                a.extensions != b.extensions -> problems += "$path: \$extensions ${a.extensions} in $file, ${b.extensions} from the resolver"
            }
        }
        if (problems.isNotEmpty()) {
            throw GeneratorError("$file disagrees with $RESOLVER (${contexts.entries.joinToString()}):\n  " + problems.take(20).joinToString("\n  "))
        }
    }

    private data class Definition(
        val path: List<String>,
        val type: String?,
        val raw: JsonElement,
        val source: String,
        val description: String?,
        val extensions: JsonObject?,
    )

    private fun refs(
        array: JsonArray,
        context: String,
    ): List<String> =
        array.map {
            val ref = it.jsonObject["\$ref"]?.jsonPrimitive?.content ?: throw GeneratorError("$RESOLVER: $context has a source without \$ref.")
            ref.removePrefix("./")
        }

    private fun parse(file: String): JsonElement {
        val text = files[file] ?: throw GeneratorError("Upstream/qeetrix-tokens/dtcg/$file is missing — run Scripts/sync-tokens.")
        return Json.parseToJsonElement(text)
    }

    private fun flatten(
        group: JsonObject,
        path: List<String>,
        inheritedType: String?,
        source: String,
        emit: (Definition) -> Unit,
    ) {
        val type = group["\$type"]?.jsonPrimitive?.content ?: inheritedType
        if ("\$value" in group) {
            val extensions = group["\$extensions"]?.let { it as? JsonObject ?: throw GeneratorError("$source: ${path.joinToString(".")} has \$extensions that is not an object.") }
            emit(Definition(path, type, group["\$value"]!!, source, group["\$description"]?.jsonPrimitive?.content, extensions))
            return
        }
        for ((key, child) in group) {
            if (key.startsWith("$")) continue
            val obj = child as? JsonObject ?: throw GeneratorError("$source: ${(path + key).joinToString(".")} is neither a token nor a group.")
            flatten(obj, path + key, type, source, emit)
        }
    }

    private fun resolveAll(
        definitions: Map<String, Definition>,
        context: String,
    ): Map<String, DtcgToken> {
        val resolved = LinkedHashMap<String, DtcgToken>()

        fun reference(element: JsonElement): String? =
            (element as? JsonPrimitive)?.takeIf { it.isString }?.content?.let { REFERENCE.matchEntire(it)?.groupValues?.get(1) }

        fun token(
            key: String,
            stack: List<String>,
        ): DtcgToken {
            resolved[key]?.let { return it }
            if (key in stack) throw GeneratorError("$context: reference cycle ${(stack + key).joinToString(" → ")}.")
            val definition = definitions[key] ?: throw GeneratorError("$context: {$key} is referenced but not defined.")
            val chain = mutableListOf<String>()
            var type = definition.type
            val value: JsonElement
            val direct = reference(definition.raw)
            if (direct != null) {
                val target = token(direct, stack + key)
                chain += direct
                chain += target.chain
                type = type ?: target.type
                value = target.value
            } else {
                value = deep(definition.raw, stack + key, ::token, ::reference)
            }
            val finalType = type ?: throw GeneratorError("$context: $key has no \$type (on the token, a group or its reference).")
            return DtcgToken(
                definition.path,
                finalType,
                definition.raw,
                normalise(finalType, value),
                chain,
                definition.source,
                definition.description,
                definition.extensions,
            )
                .also { resolved[key] = it }
        }
        for (key in definitions.keys) token(key, emptyList())
        return resolved
    }

    private fun deep(
        element: JsonElement,
        stack: List<String>,
        token: (String, List<String>) -> DtcgToken,
        reference: (JsonElement) -> String?,
    ): JsonElement {
        reference(element)?.let { return token(it, stack).value }
        return when (element) {
            is JsonObject -> JsonObject(element.mapValues { deep(it.value, stack, token, reference) })
            is JsonArray -> JsonArray(element.map { deep(it, stack, token, reference) })
            else -> element
        }
    }

    /** DTCG's defaults written out, so equal tokens compare equal however they were authored. */
    private fun normalise(
        type: String,
        value: JsonElement,
    ): JsonElement {
        // A colour without alpha is opaque.
        fun colour(element: JsonElement): JsonElement {
            val obj = element as? JsonObject ?: return element
            return if ("alpha" in obj) obj else JsonObject(obj + ("alpha" to JsonPrimitive(1)))
        }
        return when (type) {
            "color" -> {
                colour(value)
            }

            "shadow" -> {
                // A single layer is a list of one, and a layer without inset is an outer shadow.
                val layers = if (value is JsonArray) value else JsonArray(listOf(value))
                JsonArray(
                    layers.map { layer ->
                        val obj = layer.jsonObject
                        val withInset = if ("inset" in obj) obj else JsonObject(obj + ("inset" to JsonPrimitive(false)))
                        JsonObject(withInset.mapValues { (key, part) -> if (key == "color") colour(part) else part })
                    },
                )
            }

            else -> {
                value
            }
        }
    }

    companion object {
        const val RESOLVER = "qeetrix.resolver.json"
        private val REFERENCE = Regex("""^\{([^{}]+)\}$""")
    }
}
