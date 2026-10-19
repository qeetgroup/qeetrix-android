package com.qeetgroup.qeetrix.gen.components

import com.qeetgroup.qeetrix.gen.GeneratorError
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * `qeetrix-gen new-component`: the file set every component starts from
 * (Documentation/Standards/COMPONENT_CONTRACT.md), rendered from `Templates/Component/` — the Android
 * counterpart of qeetrix-ios's scaffold, with the same arguments and rules.
 *
 * It plans every change before making any, so a run does everything or nothing. It never overwrites: a
 * component that already exists — any of its files, its declaration, its catalogue entry or its index
 * row — stops the run with every reason, so running it twice changes nothing the second time. Output
 * depends only on the request and the repository's files: the same request gives the same bytes.
 */
class ComponentScaffold(
    private val root: File,
) {
    /** What to scaffold. */
    data class Request(
        /** The qeetrix-ui family (the manifest's `category`), e.g. `Button`. */
        val family: String,
        /** The component name without the `Qeetrix` prefix, e.g. `IconButton`. */
        val component: String,
        /** The ui.qeet.in group, e.g. `Actions`. */
        val category: String,
        /** The web component's manifest slug; defaults to the component name in kebab case. */
        val slug: String? = null,
    )

    /** A planned change: a new file, or the new contents of an existing one. */
    data class Change(
        val kind: Kind,
        val path: String,
        val contents: String,
    ) {
        enum class Kind(
            val verb: String,
            val past: String,
        ) {
            Create("create", "created"),
            Update("update", "updated"),
        }
    }

    /** A ui.qeet.in group: its name, the sample's `SampleCategory` entry and package. */
    data class Category(
        val name: String,
        val sampleCase: String,
        val samplePackage: String,
    )

    /** Validates the request and works out every change, without touching the disk. */
    fun plan(request: Request): List<Change> {
        validateName(request.family, "family")
        validateName(request.component, "component")
        val category =
            CATEGORIES.firstOrNull { it.name == request.category }
                ?: throw GeneratorError("Unknown category \"${request.category}\". Use one of: ${CATEGORIES.joinToString { it.name }}.")
        val slug = request.slug ?: kebab(request.component)
        val entry = manifestEntry(slug)
        if (entry.family != request.family) {
            throw GeneratorError(
                "$slug belongs to the ${entry.family} family in $MANIFEST_PATH, not ${request.family}. " +
                    "Components live in their web family's package (ADR 0010).",
            )
        }
        val packageName = packageName(request.family)
        val tokenGroup = componentTokenGroup(slug, request.family)
        val tokenObject = tokenGroup?.let { "Qeetrix${upperCamel(it)}ComponentTokens" }
        val title = title(request.component)
        val values =
            mapOf(
                "Family" to request.family,
                "Component" to request.component,
                "component" to request.component.lowercase(),
                "package" to packageName,
                "slug" to slug,
                "title" to title,
                "name" to entry.name,
                "description" to entry.description.replace("*/", "* /"),
                "category" to category.name,
                "categoryPackage" to category.samplePackage,
                "tokens" to (tokenObject ?: "none — semantic tokens only"),
                "tokensDoc" to
                    if (tokenGroup != null) {
                        "`component.$tokenGroup.*` (`$tokenObject`)."
                    } else {
                        "Semantic tokens only: qeetrix-ui defines no `component.$slug` group."
                    },
            )

        val problems = mutableListOf<String>()
        val changes = mutableListOf<Change>()
        for ((template, destination) in TEMPLATES) {
            val path = render(destination, values)
            if (file(path).exists()) problems += "$path already exists"
            changes += Change(Change.Kind.Create, path, render(read("$TEMPLATES_PATH/$template"), values))
        }
        existingDeclaration("Qeetrix${request.component}")?.let { problems += "Qeetrix${request.component} is already declared in $it" }

        val catalog = read(CATALOG_PATH)
        val pageId = kebab(request.component)
        if ("${request.component}Sample()" in catalog || "SampleEntry(\"$pageId\"" in catalog) {
            problems += "$CATALOG_PATH already has a ${request.component} page"
        }
        val index = read(INDEX_PATH)
        val row = "| ${category.name} | [$title](${request.component}.md) | `Qeetrix${request.component}` | `$packageName` |"
        if ("(${request.component}.md)" in index) problems += "$INDEX_PATH already lists ${request.component}"
        if (problems.isNotEmpty()) {
            throw GeneratorError("Qeetrix${request.component} already exists; nothing was changed:\n  " + problems.joinToString("\n  "))
        }

        val entryLine =
            "        SampleEntry(\"$pageId\", \"$title\", SampleCategory.${category.sampleCase}) { ${request.component}Sample() },"
        val import = "import com.qeetgroup.qeetrix.sample.components.${category.samplePackage}.${request.component}Sample"
        changes += Change(Change.Kind.Update, CATALOG_PATH, addCatalogEntry(catalog, entryLine, import))
        changes += Change(Change.Kind.Update, INDEX_PATH, addIndexRow(index, row, category.name))
        if (tokenGroup != null) {
            addComponentTokens(tokenGroup, read(UPSTREAM_PATH))?.let { changes += Change(Change.Kind.Update, UPSTREAM_PATH, it) }
        }
        return changes
    }

    /**
     * Makes the planned changes. Each new file is checked again just before anything is written, so
     * nothing is overwritten even if the tree changed after planning.
     */
    fun apply(changes: List<Change>) {
        for (change in changes) {
            if (change.kind == Change.Kind.Create && file(change.path).exists()) {
                throw GeneratorError("${change.path} appeared after planning; nothing was changed.")
            }
        }
        for (change in changes) {
            val target = file(change.path)
            target.parentFile.mkdirs()
            target.writeText(change.contents)
        }
    }

    /** True when the planned changes add a package that `qeetrix/Module.md` does not describe yet. */
    fun introducesUndocumentedPackage(request: Request): Boolean =
        "# Package com.qeetgroup.qeetrix.components.${packageName(request.family)}\n" !in read(MODULE_DOC_PATH)

    private data class ManifestEntry(
        val family: String,
        val name: String,
        val description: String,
    )

    private fun manifestEntry(slug: String): ManifestEntry {
        val components =
            Json
                .parseToJsonElement(read(MANIFEST_PATH))
                .jsonObject["components"]
                ?.jsonArray
                .orEmpty()
        val entry =
            components.map { it.jsonObject }.firstOrNull { it.string("slug") == slug }
                ?: throw GeneratorError(
                    "No component \"$slug\" in $MANIFEST_PATH. Pass the web slug with --slug (for example --slug input for TextField).",
                )
        return ManifestEntry(entry.string("category").orEmpty(), entry.string("name") ?: slug, entry.string("description").orEmpty())
    }

    /** The `component.<group>` token group for the component: the slug's own, or else the family's. */
    private fun componentTokenGroup(
        slug: String,
        family: String,
    ): String? {
        val groups =
            Json
                .parseToJsonElement(read(TOKENS_PATH))
                .jsonObject["light"]
                ?.jsonObject
                ?.get("component")
                ?.jsonObject ?: return null
        return listOf(slug, kebab(family)).firstOrNull { it in groups }
    }

    /** Where `Qeetrix<Component>` is already declared in the library, if anywhere. */
    private fun existingDeclaration(name: String): String? {
        val sources = file(LIBRARY_SOURCES)
        val pattern = Regex("""\b(fun|class|object|interface|typealias)\s+$name\b""")
        return sources
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" && pattern.containsMatchIn(it.readText()) }
            .map { it.relativeTo(root).invariantSeparatorsPath }
            .sorted()
            .firstOrNull()
    }

    private fun file(path: String) = File(root, path)

    private fun read(path: String): String {
        val file = file(path)
        if (!file.isFile) throw GeneratorError("Cannot read $path.")
        return file.readText()
    }

    private fun JsonObject.string(key: String): String? = this[key]?.jsonPrimitive?.content

    companion object {
        const val TEMPLATES_PATH = "Templates/Component"
        const val MANIFEST_PATH = "Upstream/qeetrix-ui/component-manifest.json"
        const val TOKENS_PATH = "Upstream/qeetrix-ui/tokens.json"
        const val UPSTREAM_PATH = "Upstream/manifest.json"
        const val CATALOG_PATH = "sample/src/main/kotlin/com/qeetgroup/qeetrix/sample/SampleCatalog.kt"
        const val INDEX_PATH = "Documentation/Components/README.md"
        const val MODULE_DOC_PATH = "qeetrix/Module.md"
        const val LIBRARY_SOURCES = "qeetrix/src/main/kotlin"
        const val CATALOG_MARKER = "        // new-component: insert above this line"

        /** ui.qeet.in's component groups, in its sidebar order (qeetrix-docs `src/lib/component-groups.json`). */
        val CATEGORIES =
            listOf(
                Category("Actions", "Actions", "actions"),
                Category("Forms & inputs", "Forms", "forms"),
                Category("Date & time", "DateTime", "datetime"),
                Category("Data display", "DataDisplay", "datadisplay"),
                Category("Feedback", "Feedback", "feedback"),
                Category("Overlays", "Overlays", "overlays"),
                Category("Navigation", "Navigation", "navigation"),
                Category("Layout", "Layout", "layout"),
                Category("Utilities", "Utilities", "utilities"),
            )

        val TEMPLATES =
            listOf(
                "Component.kt.template" to "qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/components/{{package}}/Qeetrix{{Component}}.kt",
                "ComponentTest.kt.template" to
                    "qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/{{package}}/Qeetrix{{Component}}Test.kt",
                "ComponentScreenshotTest.kt.template" to
                    "qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/{{package}}/Qeetrix{{Component}}ScreenshotTest.kt",
                "ComponentSample.kt.template" to
                    "sample/src/main/kotlin/com/qeetgroup/qeetrix/sample/components/{{categoryPackage}}/{{Component}}Sample.kt",
                "Component.md.template" to "Documentation/Components/{{Component}}.md",
            )

        /** Words Java reserves, which cannot be a package name segment for Java callers and tools. */
        private val JAVA_KEYWORDS =
            (
                "abstract assert boolean break byte case catch char class const continue default do double else enum " +
                    "extends false final finally float for goto if implements import instanceof int interface long native " +
                    "new null package private protected public return short static strictfp super switch synchronized this " +
                    "throw throws transient true try void volatile while"
            ).split(' ').toSet()

        fun validateName(
            name: String,
            what: String,
        ) {
            if (!Regex("^[A-Z][A-Za-z0-9]*$").matches(name)) {
                throw GeneratorError("The $what \"$name\" must be UpperCamelCase ASCII, e.g. IconButton.")
            }
            if (name.startsWith("Qeetrix")) {
                throw GeneratorError("Give the $what without the Qeetrix prefix: ${name.removePrefix("Qeetrix")}.")
            }
        }

        /** The family's package: lower case, with `control` appended to a Java keyword (`switch` → `switchcontrol`). */
        fun packageName(family: String): String = family.lowercase().let { if (it in JAVA_KEYWORDS) it + "control" else it }

        /** Inserts [line] above the catalogue's marker and [import] in order among its imports. */
        fun addCatalogEntry(
            catalog: String,
            line: String,
            import: String,
        ): String {
            if ("$CATALOG_MARKER\n" !in catalog) {
                throw GeneratorError("$CATALOG_PATH has lost its marker line \"${CATALOG_MARKER.trim()}\".")
            }
            val lines = catalog.replace("$CATALOG_MARKER\n", "$line\n$CATALOG_MARKER\n").split("\n").toMutableList()
            val imports = lines.indices.filter { lines[it].startsWith("import ") }
            if (imports.isEmpty()) throw GeneratorError("$CATALOG_PATH has no imports.")
            if (import !in lines) {
                val at = imports.firstOrNull { lines[it] > import } ?: (imports.last() + 1)
                lines.add(at, import)
            }
            return lines.joinToString("\n")
        }

        /**
         * Adds [row] to the index table: after the last row of [category], or else before the first row of
         * a later group, or else at the end of the table.
         */
        fun addIndexRow(
            index: String,
            row: String,
            category: String,
        ): String {
            val lines = index.split("\n").toMutableList()
            val rows = lines.indices.filter { lines[it].startsWith("| ") && !lines[it].startsWith("| Group") }
            if (rows.isEmpty()) throw GeneratorError("$INDEX_PATH has no component table.")
            val order = CATEGORIES.map { it.name }

            fun groupOf(line: String) = line.removePrefix("| ").substringBefore(" |")
            val rank = order.indexOf(category)
            val at =
                rows.lastOrNull { groupOf(lines[it]) == category }?.plus(1)
                    ?: rows.firstOrNull { order.indexOf(groupOf(lines[it])) > rank }
                    ?: (rows.last() + 1)
            lines.add(at, row)
            return lines.joinToString("\n")
        }

        /**
         * Adds [group] to `Upstream/manifest.json`'s `componentTokens`, keeping the list sorted and the rest of
         * the file byte for byte; `null` when it is already there.
         */
        fun addComponentTokens(
            group: String,
            manifest: String,
        ): String? {
            val start = manifest.indexOf("\"componentTokens\": [")
            if (start < 0) throw GeneratorError("$UPSTREAM_PATH has no componentTokens list.")
            val open = start + "\"componentTokens\": [".length
            val close = manifest.indexOf(']', open)
            val body = manifest.substring(open, close)
            val groups = body.split(',').map { it.trim().trim('"') }.filter { it.isNotEmpty() }
            if (group in groups) return null
            val itemIndent = Regex("""\n([ \t]*)"""").find(body)?.groupValues?.get(1) ?: "      "
            val closeIndent = body.substringAfterLast('\n', "")
            val list = (groups + group).sorted().joinToString(",\n", prefix = "\n", postfix = "\n$closeIndent") { "$itemIndent\"$it\"" }
            return manifest.substring(0, open) + list + manifest.substring(close)
        }

        /** `IconButton` → `icon-button`, `OTPField` → `otp-field`. */
        fun kebab(name: String): String = words(name).joinToString("-") { it.lowercase() }

        /** `IconButton` → `Icon button`; acronyms stay upper case (`OTPField` → `OTP field`). */
        fun title(name: String): String {
            val parts = words(name)
            val first = parts.firstOrNull() ?: return name
            return (listOf(first) + parts.drop(1).map { if (it.length > 1 && it.uppercase() == it) it else it.lowercase() })
                .joinToString(" ")
        }

        /** `icon-button` → `IconButton`. */
        fun upperCamel(kebab: String): String = kebab.split('-').joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }

        private fun words(name: String): List<String> {
            val words = mutableListOf<String>()
            val current = StringBuilder()
            for ((index, character) in name.withIndex()) {
                val next = name.getOrNull(index + 1)
                val last = current.lastOrNull()
                val startsWord =
                    character.isUpperCase() &&
                        current.isNotEmpty() &&
                        (last?.isLowerCase() == true || last?.isDigit() == true || next?.isLowerCase() == true)
                if (startsWord) {
                    words += current.toString()
                    current.clear()
                }
                current.append(character)
            }
            if (current.isNotEmpty()) words += current.toString()
            return words
        }

        /** Replaces every `{{key}}`, in key order, so the result never depends on map iteration order. */
        fun render(
            template: String,
            values: Map<String, String>,
        ): String = values.keys.sorted().fold(template) { text, key -> text.replace("{{$key}}", values.getValue(key)) }
    }
}
