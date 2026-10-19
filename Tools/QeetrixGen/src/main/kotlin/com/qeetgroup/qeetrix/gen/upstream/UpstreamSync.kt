package com.qeetgroup.qeetrix.gen.upstream

import com.qeetgroup.qeetrix.gen.GeneratorError
import com.qeetgroup.qeetrix.gen.sha256
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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
import java.util.zip.ZipInputStream

/**
 * Fetches the artifacts pinned in `Upstream/manifest.json` and refreshes the snapshot and lock — the
 * only step that touches the network.
 *
 * - `@qeetrix/ui` comes from the npm registry: the published tarball, checked against its registry
 *   integrity, is the output of qeetrix-ui's own Style Dictionary build of its DTCG sources.
 * - Fonts come from their projects' releases as TTF. Each is accepted only if the same release's
 *   WOFF2 is byte-identical to the WOFF2 inside the pinned `@qeetrix/ui` tarball — proof that Android
 *   bundles the exact font the web ships, in its native format.
 * - Icons come from the pinned qeetrix-icons commit: the manifest entries and SVGs for the pinned names.
 *
 * With [check] nothing is written; the fetched bytes are compared with the lock instead.
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

        // @qeetrix/ui
        val registry = "https://registry.npmjs.org/${manifest.uiPackage.replace("/", "%2F")}/${manifest.uiVersion}"
        val metadata = Json.parseToJsonElement(fetch(registry).decodeToString()).jsonObject
        val dist = metadata["dist"]?.jsonObject
        val tarball = dist?.get("tarball")?.jsonPrimitive?.content
        val integrity = dist?.get("integrity")?.jsonPrimitive?.content
        val gitHead = metadata["gitHead"]?.jsonPrimitive?.content
        if (tarball == null || integrity == null ||
            gitHead == null
        ) {
            throw GeneratorError("$registry: missing dist.tarball, dist.integrity or gitHead.")
        }
        val archive = fetch(tarball)
        val digest = "sha512-" + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-512").digest(archive))
        if (digest != integrity) throw GeneratorError("$tarball: integrity mismatch — registry says $integrity, download is $digest.")
        log("✓ ${manifest.uiPackage}@${manifest.uiVersion} (${gitHead.take(7)}) — integrity verified")
        val packageFiles = untarGz(archive)

        fun packageFile(path: String) =
            packageFiles[path] ?: throw GeneratorError("${manifest.uiPackage}@${manifest.uiVersion} has no $path.")
        for ((source, destination) in manifest.uiFiles) staged[destination] = packageFile(source)

        // Fonts
        val fonts =
            manifest.fonts.map { font ->
                val ttf: ByteArray
                val woff2: ByteArray
                if (font.releaseAsset != null) {
                    val zip = unzip(fetch("https://github.com/${font.repository}/releases/download/${font.tag}/${font.releaseAsset}"))
                    ttf = zip[font.ttf] ?: throw GeneratorError("${font.releaseAsset} has no ${font.ttf}.")
                    woff2 = zip[font.woff2] ?: throw GeneratorError("${font.releaseAsset} has no ${font.woff2}.")
                } else {
                    ttf = fetch(raw(font.repository, font.tag, font.ttf))
                    woff2 = fetch(raw(font.repository, font.tag, font.woff2))
                }
                if (!woff2.contentEquals(packageFile(font.webFile))) {
                    throw GeneratorError(
                        "${font.repository} ${font.tag}: ${font.woff2} is not byte-identical to ${font.webFile} in ${manifest.uiPackage}@${manifest.uiVersion}. The TTF would not be the font the web ships; pin the release the web uses.",
                    )
                }
                log("✓ ${font.repository} ${font.tag} — WOFF2 matches ${font.webFile}; taking the TTF")
                staged[font.destination] = ttf
                staged[font.licenseDestination] = fetch(raw(font.repository, font.tag, font.license))
                Lock.LockedFont(
                    font.family,
                    font.webFamily,
                    font.repository,
                    font.tag,
                    font.destination,
                    sha256(ttf),
                    sha256(woff2),
                    font.webFile,
                )
            }

        // Icons
        val icons = manifest.icons
        val iconManifestUrl = raw(icons.repository, icons.commit, "icon-manifest.json")
        val entries =
            Json
                .parseToJsonElement(fetch(iconManifestUrl).decodeToString())
                .jsonObject["icons"]
                ?.jsonArray
                .orEmpty()
        val selected =
            icons.names.map { name ->
                val entry =
                    entries.firstOrNull { it.jsonObject["name"]?.jsonPrimitive?.content == name }?.jsonObject
                        ?: throw GeneratorError("${icons.repository}@${icons.commit.take(7)} has no icon named \"$name\".")
                val category = entry["category"]!!.jsonPrimitive.content
                for (shape in entry["shapes"]!!.jsonArray.map { it.jsonPrimitive.content }) {
                    for (variant in entry["variants"]!!.jsonArray.map { it.jsonPrimitive.content }) {
                        staged["Upstream/qeetrix-icons/svg/$shape-$variant/$name.svg"] =
                            fetch(raw(icons.repository, icons.commit, "icons/$shape-$variant/$category/$name.svg"))
                    }
                }
                entry
            }
        staged["Upstream/qeetrix-icons/icons.json"] =
            (Lock.pretty.encodeToString(JsonArray.serializer(), JsonArray(selected)) + "\n").toByteArray()
        staged[icons.licenseDestination] = fetch(raw(icons.repository, icons.commit, "LICENSE"))
        log("✓ ${icons.repository}@${icons.commit.take(7)} — ${icons.names.size} icons")

        val lock =
            Lock(
                uiPackage = manifest.uiPackage,
                uiVersion = manifest.uiVersion,
                uiGitHead = gitHead,
                uiIntegrity = integrity,
                iconsRepository = icons.repository,
                iconsPackage = icons.packageName,
                iconsVersion = icons.version,
                iconsCommit = icons.commit,
                fonts = fonts,
                files = staged.mapValues { sha256(it.value) },
            )

        if (check) {
            val locked = Lock.read(root)
            if (locked != lock) {
                val drift = lock.files.filter { locked.files[it.key] != it.value }.keys
                throw GeneratorError(
                    "The pinned upstream artifacts no longer match the lock. Differing files: ${if (drift.isEmpty()) {
                        "none (metadata changed)"
                    } else {
                        drift
                            .joinToString(
                                ", ",
                            )
                    }}.",
                )
            }
            log("✓ Upstream matches ${Lock.PATH}")
            return
        }

        File(root, "Upstream/qeetrix-icons/svg").deleteRecursively() // drop drawings of icons no longer pinned
        for ((path, bytes) in staged) {
            val file = File(root, path)
            file.parentFile.mkdirs()
            file.writeBytes(bytes)
        }
        File(root, Lock.PATH).writeText(Lock.pretty.encodeToString(JsonObject.serializer(), lock.toJson() as JsonObject) + "\n")
        log("✓ Wrote ${staged.size} files and ${Lock.PATH}")
    }

    private fun raw(
        repository: String,
        ref: String,
        path: String,
    ) = "https://raw.githubusercontent.com/$repository/$ref/$path"

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

    private fun unzip(archive: ByteArray): Map<String, ByteArray> {
        val files = HashMap<String, ByteArray>()
        ZipInputStream(ByteArrayInputStream(archive)).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                if (!entry.isDirectory) files[entry.name] = ByteArrayOutputStream().also { zip.copyTo(it) }.toByteArray()
            }
        }
        return files
    }

    private companion object {
        /** Attempts to open a connection before a fetch fails; each waits up to the 10 s connect timeout. */
        const val CONNECT_ATTEMPTS = 3
    }
}
