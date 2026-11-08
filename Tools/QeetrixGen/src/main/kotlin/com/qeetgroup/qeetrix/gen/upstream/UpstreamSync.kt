package com.qeetgroup.qeetrix.gen.upstream

import com.qeetgroup.qeetrix.gen.GeneratorError
import com.qeetgroup.qeetrix.gen.fonts.FontInspector
import com.qeetgroup.qeetrix.gen.sha256
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayInputStream
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import java.time.Duration
import java.util.Base64
import java.util.zip.GZIPInputStream

/**
 * Fetches the npm tarballs pinned in `Upstream/manifest.json` and refreshes the snapshot and lock — the
 * only step that touches the network.
 *
 * - **`@qeetrix/tokens`**: the DTCG build (`dist/dtcg/`, the resolver, its sources and the resolved
 *   light and dark documents) is copied whole; the full variable fonts are the TrueType files the package
 *   ships for native platforms, each accepted only if its sha256, size and family match `fonts/native.json`,
 *   it has the axes the manifest lists, and the WOFF2 it was made from is the one the web loads.
 * - **`@qeetrix/react`**: the component manifest (`dist/manifest.json`) becomes
 *   `component-manifest.json`; the compiled CSS of the families Android implements is copied, so a pin
 *   bump shows exactly how each component's look changed.
 * - **`@qeetrix/icons`**: each icon's entry in the catalogue (`dist/manifest.json`) and its plain SVG
 *   drawings (`dist/svg/<shape>-<variant>/<id>.svg`, one per shape and variant the entry declares), copied
 *   unchanged: the pinned core icons, and with `extended` every other icon the package publishes.
 *
 * Every tarball is checked against the registry's integrity hash. With [check] nothing is written; the
 * fetched bytes are compared with the lock instead.
 */
class UpstreamSync(
    private val root: File,
    private val log: (String) -> Unit,
) {
    private val http =
        HttpClient
            .newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build()

    fun run(check: Boolean) {
        val manifest = Manifest.read(root)
        val staged = sortedMapOf<String, ByteArray>()

        // @qeetrix/tokens: the DTCG build, copied whole.
        val (tokensLock, tokensFiles) = npm(manifest.tokens)
        val dtcgPrefix = manifest.tokens.dtcg.trimEnd('/') + "/"
        val dtcg = tokensFiles.filterKeys { it.startsWith(dtcgPrefix) }
        if (dtcg.isEmpty()) throw GeneratorError("${manifest.tokens.packageName}@${manifest.tokens.version} has no ${manifest.tokens.dtcg}/.")
        for ((path, bytes) in dtcg) staged["${manifest.tokens.destination}/${path.removePrefix(dtcgPrefix)}"] = bytes
        log("  ${dtcg.size} DTCG files")

        // Fonts: the TrueType files @qeetrix/tokens ships for native platforms, each checked against the sha256,
        // size and axes its fonts/native.json records.
        val native = Json.parseToJsonElement(tokensFiles.need(manifest.nativeFonts, manifest.tokens).decodeToString()).jsonObject
        val nativeEntries = native["fonts"]?.jsonArray?.map { it.jsonObject } ?: throw GeneratorError("${manifest.nativeFonts} has no fonts.")
        val fontTools = native["fontTools"]?.jsonPrimitive?.content ?: throw GeneratorError("${manifest.nativeFonts} has no fontTools version.")
        val fontsDirectory = manifest.nativeFonts.substringBeforeLast('/')
        val fonts =
            manifest.fonts.map { font ->
                val file = font.ttf.substringAfterLast('/')
                val entry =
                    nativeEntries.firstOrNull { it["file"]?.jsonPrimitive?.content == file }
                        ?: throw GeneratorError("${manifest.nativeFonts} lists no $file.")
                val ttf = tokensFiles.need(font.ttf, manifest.tokens)
                val recorded = entry["sha256"]!!.jsonPrimitive.content
                if (sha256(ttf) != recorded) throw GeneratorError("${font.ttf}: sha256 ${sha256(ttf)}, ${manifest.nativeFonts} records $recorded.")
                if (ttf.size.toLong() != entry["bytes"]!!.jsonPrimitive.content.toLong()) {
                    throw GeneratorError("${font.ttf}: ${ttf.size} bytes, ${manifest.nativeFonts} records ${entry["bytes"]}.")
                }
                if (entry["family"]?.jsonPrimitive?.content != font.webFamily) {
                    throw GeneratorError("${font.ttf} is ${entry["family"]} in ${manifest.nativeFonts}, not ${font.webFamily}.")
                }
                val info = FontInspector.inspect(ttf, font.destination)
                val missing = font.axes.filter { tag -> info.axes.none { it.tag == tag } }
                if (missing.isNotEmpty()) throw GeneratorError("${font.ttf} has no ${missing.joinToString()} axis.")
                val from = entry["from"]!!.jsonObject
                val woff2 = "$fontsDirectory/${from["file"]!!.jsonPrimitive.content}"
                val woff2Sha256 = from["sha256"]!!.jsonPrimitive.content
                if (sha256(tokensFiles.need(woff2, manifest.tokens)) != woff2Sha256) {
                    throw GeneratorError("$woff2 does not match the sha256 ${manifest.nativeFonts} records for the WOFF2 $file was made from.")
                }
                val licence = "$fontsDirectory/${entry["licence"]!!.jsonPrimitive.content}"
                staged[font.destination] = ttf
                staged[font.licenseDestination] = tokensFiles.need(licence, manifest.tokens)
                log("✓ ${font.webFamily}: $file (axes ${info.axes.joinToString(" ") { it.tag }}), sha256 as native.json records; made from ${woff2.substringAfterLast('/')}")
                Lock.LockedFont(font.family, font.webFamily, font.ttf, sha256(ttf), woff2, woff2Sha256, font.destination)
            }

        // @qeetrix/react: the component manifest and the families' CSS.
        val (reactLock, reactFiles) = npm(manifest.react)
        val reactContext = "${manifest.react.packageName}@${manifest.react.version} ${manifest.react.manifest}"
        val manifestBytes = reactFiles.need(manifest.react.manifest, manifest.react)
        val families = Json.parseToJsonElement(manifestBytes.decodeToString())
        if (families !is JsonArray || families.isEmpty()) throw GeneratorError("$reactContext is not a list of families.")
        staged[manifest.react.manifestDestination] = manifestBytes
        val cssPrefix = manifest.react.css.trimEnd('/')
        for (family in manifest.react.families) {
            staged["${manifest.react.cssDestination}/$family.css"] = reactFiles.need("$cssPrefix/$family.css", manifest.react)
        }
        log("  ${families.jsonArray.size} families in the component manifest; ${manifest.react.families.size} families' CSS")

        // @qeetrix/icons: the pinned catalogue entries and their SVG files, as published.
        val icons = manifest.icons
        val (iconsLock, iconFiles) = npm(icons)
        val iconContext = "${icons.packageName}@${icons.version} ${icons.manifest}"
        val catalogue = Json.parseToJsonElement(iconFiles.need(icons.manifest, icons).decodeToString()) as? JsonObject
        val schema = catalogue?.get("schemaVersion")?.stringOrNull()
        if (schema != ICON_SCHEMA) throw GeneratorError("$iconContext: schemaVersion $schema; the sync reads schema $ICON_SCHEMA.")
        val entries = catalogue["icons"] as? JsonArray ?: throw GeneratorError("$iconContext: no icons array.")
        val svgPrefix = icons.svg.trimEnd('/')
        // The drawing directories the package publishes (logos are not icons).
        val drawingDirectories =
            iconFiles.keys
                .filter { it.startsWith("$svgPrefix/") && it.endsWith(".svg") }
                .map { it.removePrefix("$svgPrefix/").substringBefore('/') }
                .filter { it != "logos" }
                .toSortedSet()
        val names = icons.names.toSet()
        icons.names.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.firstOrNull()?.let {
            throw GeneratorError("Upstream/manifest.json pins \"$it\" twice.")
        }
        // The core pins, then — with the extended catalogue — every other icon the package publishes.
        val wanted =
            if (icons.extended) {
                val all = entries.map { it.jsonObject["name"]?.stringOrNull() ?: throw GeneratorError("$iconContext: an icon has no name.") }
                for (name in icons.names) {
                    if (name !in all) {
                        val renamed = entries.firstOrNull { e -> e.jsonObject["aliases"]?.jsonArray?.any { it.stringOrNull() == name } == true }
                        throw GeneratorError(
                            "$iconContext has no icon named \"$name\"" +
                                (renamed?.let { " — it is now \"${it.jsonObject["name"]!!.jsonPrimitive.content}\"" } ?: "") + ".",
                        )
                    }
                }
                all
            } else {
                icons.names
            }
        val selected =
            wanted.map { name ->
                val entry =
                    entries.firstOrNull { it.jsonObject["name"]?.stringOrNull() == name }?.jsonObject
                        ?: run {
                            val renamed =
                                entries.firstOrNull { e -> e.jsonObject["aliases"]?.jsonArray?.any { it.stringOrNull() == name } == true }
                            throw GeneratorError(
                                "$iconContext has no icon named \"$name\"" +
                                    (renamed?.let { " — it is now \"${it.jsonObject["name"]!!.jsonPrimitive.content}\"" } ?: "") + ".",
                            )
                        }
                val id = entry["id"]?.stringOrNull() ?: throw GeneratorError("$iconContext: \"$name\" has no id.")
                val shapes = entry["shapes"]!!.jsonArray.map { it.jsonPrimitive.content }
                val variants = entry["variants"]!!.jsonArray.map { it.jsonPrimitive.content }
                val declared = shapes.flatMap { shape -> variants.map { "$shape-$it" } }.toSortedSet()
                val published = drawingDirectories.filter { "$svgPrefix/$it/$id.svg" in iconFiles }.toSortedSet()
                if (published != declared) {
                    throw GeneratorError("${icons.packageName}@${icons.version} ${icons.svg} draws $id as $published; its catalogue entry declares $declared.")
                }
                for (drawing in declared) {
                    staged["Upstream/qeetrix-icons/svg/$drawing/$name.svg"] = iconFiles.need("$svgPrefix/$drawing/$id.svg", icons)
                }
                entry
            }
        staged["Upstream/qeetrix-icons/icons.json"] = pretty(JsonArray(selected))
        staged[icons.licenseDestination] = iconFiles.need(icons.license, icons)
        log("  ${names.size} core icons" + if (icons.extended) ", ${selected.size - names.size} more in the extended catalogue" else "")

        val lock =
            Lock(
                tokens = tokensLock,
                react = reactLock,
                icons = iconsLock,
                fontTools = fontTools,
                fonts = fonts,
                files = staged.mapValues { sha256(it.value) },
            )

        if (check) {
            val locked = Lock.read(root)
            if (locked != lock) {
                val drift = lock.files.filter { locked.files[it.key] != it.value }.keys + locked.files.keys.filter { it !in lock.files }
                throw GeneratorError(
                    "The pinned upstream artifacts no longer match the lock. Differing files: " +
                        (if (drift.isEmpty()) "none (metadata changed)" else drift.sorted().joinToString(", ")) + ".",
                )
            }
            log("✓ Upstream matches ${Lock.PATH}")
            return
        }

        // Drop everything a previous pin left behind, then write the new snapshot.
        // The previous lock's file list, read without its schema, so files from an older layout go too.
        val previous =
            runCatching {
                Json
                    .parseToJsonElement(File(root, Lock.PATH).readText())
                    .jsonObject["files"]!!
                    .jsonObject.keys
            }.getOrDefault(emptySet())
        for (path in previous - staged.keys) File(root, path).delete()
        for (directory in listOf("Upstream/qeetrix-ui", "Upstream/qeetrix-icons/svg", manifest.react.cssDestination, manifest.tokens.destination)) {
            File(root, directory).deleteRecursively()
        }
        for ((path, bytes) in staged) {
            val file = File(root, path)
            file.parentFile.mkdirs()
            file.writeBytes(bytes)
        }
        File(root, Lock.PATH).writeText(Lock.pretty.encodeToString(JsonObject.serializer(), lock.toJson() as JsonObject) + "\n")
        log("✓ Wrote ${staged.size} files and ${Lock.PATH}")
    }

    private fun pretty(element: JsonElement): ByteArray = (Lock.pretty.encodeToString(JsonElement.serializer(), element) + "\n").toByteArray()

    private fun JsonElement.stringOrNull(): String? = (this as? JsonPrimitive)?.content

    private fun Map<String, ByteArray>.need(
        path: String,
        pin: Manifest.NpmPin,
    ): ByteArray = this[path] ?: throw GeneratorError("${pin.packageName}@${pin.version} has no $path.")

    /** The pinned tarball, checked against the registry's integrity hash, and its files by path. */
    private fun npm(pin: Manifest.NpmPin): Pair<LockedPackage, Map<String, ByteArray>> {
        val registry = "https://registry.npmjs.org/${pin.packageName.replace("/", "%2F")}/${pin.version}"
        val metadata = Json.parseToJsonElement(fetch(registry).decodeToString()).jsonObject
        val dist = metadata["dist"]?.jsonObject
        val tarball = dist?.get("tarball")?.jsonPrimitive?.content
        val integrity = dist?.get("integrity")?.jsonPrimitive?.content
        val gitHead = metadata["gitHead"]?.jsonPrimitive?.content
        if (tarball == null || integrity == null || gitHead == null) {
            throw GeneratorError("$registry: missing dist.tarball, dist.integrity or gitHead.")
        }
        val archive = fetch(tarball)
        val digest = "sha512-" + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-512").digest(archive))
        if (digest != integrity) throw GeneratorError("$tarball: integrity mismatch — registry says $integrity, download is $digest.")
        log("✓ ${pin.packageName}@${pin.version} (${gitHead.take(7)}) — integrity verified")
        return LockedPackage(pin.packageName, pin.version, gitHead, integrity) to untarGz(archive)
    }

    private fun fetch(address: String): ByteArray {
        val request =
            HttpRequest
                .newBuilder(URI(address))
                .timeout(Duration.ofSeconds(120))
                .GET()
                .build()
        // A connection that never opens is retried (ReachableFirstResolver already steers clear of a CDN
        // address this network cannot reach). What arrives is still checked against the integrity hashes and
        // the lock, so a retry cannot let a different file through.
        var attempt = 1
        while (true) {
            val response =
                try {
                    http.send(request, HttpResponse.BodyHandlers.ofByteArray())
                } catch (timeout: HttpConnectTimeoutException) {
                    if (attempt == CONNECT_ATTEMPTS) throw GeneratorError("GET $address: no connection after $attempt attempts.")
                    attempt++
                    continue
                }
            if (response.statusCode() != 200) throw GeneratorError("GET $address failed (${response.statusCode()}).")
            return response.body()
        }
    }

    /** The regular files of a gzipped ustar/pax archive (npm's format), by path. */
    private fun untarGz(archive: ByteArray): Map<String, ByteArray> {
        val data = GZIPInputStream(ByteArrayInputStream(archive)).readBytes()
        val files = HashMap<String, ByteArray>()
        var offset = 0
        var paxPath: String? = null
        while (offset + 512 <= data.size) {
            val header = data.copyOfRange(offset, offset + 512)
            if (header.all { it.toInt() == 0 }) break

            fun field(
                start: Int,
                length: Int,
            ) = String(header, start, length, Charsets.UTF_8).trimEnd('\u0000', ' ')
            val size =
                field(124, 12)
                    .trim()
                    .ifEmpty { "0" }
                    .toLong(8)
                    .toInt()
            val type = header[156].toInt().toChar()
            val body = data.copyOfRange(offset + 512, offset + 512 + size)
            val prefix = field(345, 155)
            val name = paxPath ?: (if (prefix.isEmpty()) field(0, 100) else "$prefix/${field(0, 100)}")
            when (type) {
                'x' -> {
                    paxPath =
                        String(body, Charsets.UTF_8)
                            .lineSequence()
                            .mapNotNull { line -> line.substringAfter(' ', "").takeIf { it.startsWith("path=") }?.removePrefix("path=") }
                            .firstOrNull()
                }

                '0', '\u0000' -> {
                    files[name] = body
                    paxPath = null
                }

                else -> {
                    paxPath = null
                }
            }
            offset += 512 + (size + 511) / 512 * 512
        }
        return files
    }

    private companion object {
        /** Attempts to open a connection before a fetch fails; each waits up to the 10 s connect timeout. */
        const val CONNECT_ATTEMPTS = 3

        /** The `@qeetrix/icons` catalogue schema this sync understands. */
        const val ICON_SCHEMA = "1"
    }
}
