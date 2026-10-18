package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.emit.FontSpec
import com.qeetgroup.qeetrix.gen.emit.IconEmitter
import com.qeetgroup.qeetrix.gen.emit.IconSource
import com.qeetgroup.qeetrix.gen.emit.Provenance
import com.qeetgroup.qeetrix.gen.emit.Reports
import com.qeetgroup.qeetrix.gen.emit.TokenEmitter
import com.qeetgroup.qeetrix.gen.fonts.FontInspector
import com.qeetgroup.qeetrix.gen.icons.SvgParser
import com.qeetgroup.qeetrix.gen.tokens.TokenModel
import com.qeetgroup.qeetrix.gen.tokens.TokenSource
import com.qeetgroup.qeetrix.gen.upstream.Lock
import com.qeetgroup.qeetrix.gen.upstream.Manifest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * Everything derived from the upstream snapshot: the generated Kotlin and the generated reports.
 * Offline and deterministic — the same snapshot always produces the same bytes.
 */
class Generator(
    private val root: File,
    private val log: (String) -> Unit = {},
) {
    /** Directories the generator owns outright: any file in them it did not produce is stale. */
    private val ownedDirectories = listOf(TokenEmitter.GENERATED_DIR, IconEmitter.GENERATED_DIR)

    /** Every generated file, repository-relative path → content. */
    fun outputs(): Map<String, String> {
        val manifest = Manifest.read(root)
        val lock = Lock.read(root)

        fun upstream(path: String): String {
            val file = File(root, path)
            if (!file.exists()) throw GeneratorError("$path is missing — run Scripts/sync-tokens.")
            return file.readText()
        }

        fun uiFile(name: String) =
            upstream(
                manifest.uiFiles.values.firstOrNull { it.endsWith("/$name") }
                    ?: throw GeneratorError("${Manifest.PATH}: no qeetrix-ui file named $name."),
            )
        val provenance =
            Provenance(
                uiPackage = lock.uiPackage,
                uiVersion = lock.uiVersion,
                uiGitHead = lock.uiGitHead,
                uiIntegrity = lock.uiIntegrity,
                iconsRepository = lock.iconsRepository,
                iconsPackage = lock.iconsPackage,
                iconsVersion = lock.iconsVersion,
                iconsCommit = lock.iconsCommit,
            )

        val model = TokenModel(TokenSource(uiFile("tokens.json"), uiFile("tokens.css"), uiFile("index.css")))
        val fonts =
            manifest.fonts.map { pin ->
                val file = File(root, pin.destination)
                if (!file.exists()) throw GeneratorError("${pin.destination} is missing — run Scripts/sync-tokens.")
                val resourceName = file.nameWithoutExtension
                if (!Regex("qx_[a-z0-9_]+").matches(resourceName)) {
                    throw GeneratorError(
                        "${pin.destination}: font resources must be named qx_<lowercase_name> to avoid clashing with an app's resources.",
                    )
                }
                val bytes = file.readBytes()
                FontSpec(
                    pin.family,
                    pin.webFamily,
                    pin.repository,
                    pin.tag,
                    resourceName,
                    sha256(bytes),
                    FontInspector.inspect(bytes, pin.destination),
                )
            }

        val iconEntries = Json.parseToJsonElement(upstream("Upstream/qeetrix-icons/icons.json")).jsonArray.map { it.jsonObject }
        val icons =
            manifest.icons.names.map { name ->
                val entry =
                    iconEntries.firstOrNull { it["name"]?.jsonPrimitive?.content == name }
                        ?: throw GeneratorError("Upstream/qeetrix-icons/icons.json has no \"$name\" — run Scripts/sync-tokens.")
                val shapes = entry["shapes"]!!.jsonArray.map { it.jsonPrimitive.content }
                val variants = entry["variants"]!!.jsonArray.map { it.jsonPrimitive.content }
                val drawings = linkedMapOf<String, com.qeetgroup.qeetrix.gen.icons.IconDrawing>()
                for (shape in shapes) {
                    for (variant in variants) {
                        val path = "Upstream/qeetrix-icons/svg/$shape-$variant/$name.svg"
                        drawings["$shape-$variant"] = SvgParser.parse(upstream(path), path)
                    }
                }
                IconSource(
                    name = name,
                    componentName = entry["componentName"]!!.jsonPrimitive.content,
                    directionality = entry["directionality"]!!.jsonPrimitive.content,
                    shapes = shapes,
                    variants = variants,
                    drawings = drawings,
                )
            }

        val outputs = sortedMapOf<String, String>()
        outputs += TokenEmitter(model, provenance, fonts).files()
        outputs += IconEmitter(icons, provenance).files()
        outputs += Reports(model, provenance).files()
        log("Generated ${model.included.size} tokens (${model.excluded.size} excluded), ${icons.size} icons, ${fonts.size} fonts.")
        return outputs
    }

    fun generate() {
        verifyLock()
        val outputs = outputs()
        for (stale in strayFiles(outputs.keys)) {
            File(root, stale).delete()
            log("Removed stale $stale")
        }
        var written = 0
        for ((path, content) in outputs) {
            val file = File(root, path)
            if (file.exists() && file.readText() == content) continue
            file.parentFile.mkdirs()
            file.writeText(content)
            written++
        }
        log("✓ ${outputs.size} generated files, $written changed.")
    }

    /**
     * Fails if the upstream snapshot differs from its lock, or if any generated file is not exactly what
     * the snapshot generates (edited by hand, stale, or left over).
     */
    fun verify() {
        verifyLock()
        val outputs = outputs()
        val problems = mutableListOf<String>()
        for ((path, content) in outputs) {
            val file = File(root, path)
            when {
                !file.exists() -> problems += "$path is missing"
                file.readText() != content -> problems += "$path differs from what the snapshot generates"
            }
        }
        strayFiles(outputs.keys).forEach { problems += "$it is not generated (stale or hand-written)" }
        if (problems.isNotEmpty()) {
            throw GeneratorError(
                "Generated files have drifted — run Scripts/generate-tokens and review the diff:\n  " + problems.joinToString("\n  "),
            )
        }
        log("✓ Upstream snapshot matches ${Lock.PATH}; ${outputs.size} generated files are current.")
    }

    /** The snapshot files must be exactly the locked ones, byte for byte. */
    private fun verifyLock() {
        val lock = Lock.read(root)
        val problems = mutableListOf<String>()
        for ((path, expected) in lock.files) {
            val file = File(root, path)
            when {
                !file.exists() -> problems += "$path is missing"
                sha256(file.readBytes()) != expected -> problems += "$path does not match its locked sha256"
            }
        }
        val upstream = File(root, "Upstream")
        val allowed = lock.files.keys + Manifest.PATH + Lock.PATH
        upstream.walkTopDown().filter { it.isFile && it.name != ".DS_Store" }.forEach {
            val relative = it.relativeTo(root).invariantSeparatorsPath
            if (relative !in allowed) problems += "$relative is in Upstream/ but not in the lock"
        }
        if (problems.isNotEmpty()) {
            throw GeneratorError(
                "The upstream snapshot has drifted from ${Lock.PATH} — run Scripts/sync-tokens:\n  " + problems.joinToString("\n  "),
            )
        }
    }

    private fun strayFiles(expected: Set<String>): List<String> =
        ownedDirectories.flatMap { directory ->
            File(root, directory)
                .walkTopDown()
                .filter { it.isFile }
                .map { it.relativeTo(root).invariantSeparatorsPath }
                .filter {
                    it !in
                        expected
                }.toList()
        }
}
