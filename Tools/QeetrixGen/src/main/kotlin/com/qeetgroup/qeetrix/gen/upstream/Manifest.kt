package com.qeetgroup.qeetrix.gen.upstream

import com.qeetgroup.qeetrix.gen.GeneratorError
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * `Upstream/manifest.json`: what qeetrix-android is built from. Edited by hand to move a pin; the
 * same pins as qeetrix-ios's `Upstream/pins.json`, with Android destinations.
 */
data class Manifest(
    val uiPackage: String,
    val uiVersion: String,
    /** Package-relative source path → repository-relative destination. */
    val uiFiles: Map<String, String>,
    /** The `component.<group>` token groups Android implements — the same groups as qeetrix-ios. */
    val componentTokens: List<String>,
    val fonts: List<FontPin>,
    val icons: IconPins,
) {
    data class FontPin(
        /** The family key the typography roles use: `sans` or `mono`. */
        val family: String,
        /** The family name the web's CSS uses for it, e.g. `Qeet Sans`. */
        val webFamily: String,
        /** The WOFF2 inside the `@qeetrix/ui` tarball that the TTF must match. */
        val webFile: String,
        val repository: String,
        val tag: String,
        /** A release asset holding the files, or null to read them from the tagged tree. */
        val releaseAsset: String?,
        val ttf: String,
        val woff2: String,
        val license: String,
        val destination: String,
        val licenseDestination: String,
    )

    data class IconPins(
        val repository: String,
        val packageName: String,
        val version: String,
        val commit: String,
        val licenseDestination: String,
        val names: List<String>,
    )

    companion object {
        const val PATH = "Upstream/manifest.json"

        fun read(root: File): Manifest {
            val file = File(root, PATH)
            if (!file.exists()) throw GeneratorError("$PATH is missing.")
            val json = Json.parseToJsonElement(file.readText()).jsonObject

            fun JsonObject.string(
                key: String,
                context: String,
            ): String =
                (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content ?: throw GeneratorError("$PATH: $context needs \"$key\".")
            val ui = json["qeetrixUI"]?.jsonObject ?: throw GeneratorError("$PATH: missing qeetrixUI.")
            val fonts = json["fonts"]?.jsonArray ?: throw GeneratorError("$PATH: missing fonts.")
            val icons = json["qeetrixIcons"]?.jsonObject ?: throw GeneratorError("$PATH: missing qeetrixIcons.")
            return Manifest(
                uiPackage = ui.string("package", "qeetrixUI"),
                uiVersion = ui.string("version", "qeetrixUI"),
                uiFiles =
                    (ui["files"] as? JsonObject ?: throw GeneratorError("$PATH: qeetrixUI needs files."))
                        .mapValues { it.value.jsonPrimitive.content },
                componentTokens = ui["componentTokens"]?.jsonArray?.map { it.jsonPrimitive.content }.orEmpty(),
                fonts =
                    fonts.map { element ->
                        val font = element.jsonObject
                        FontPin(
                            family = font.string("family", "a font"),
                            webFamily = font.string("webFamily", "a font"),
                            webFile = font.string("webFile", "a font"),
                            repository = font.string("repository", "a font"),
                            tag = font.string("tag", "a font"),
                            releaseAsset = (font["releaseAsset"] as? JsonPrimitive)?.content,
                            ttf = font.string("ttf", "a font"),
                            woff2 = font.string("woff2", "a font"),
                            license = font.string("license", "a font"),
                            destination = font.string("destination", "a font"),
                            licenseDestination = font.string("licenseDestination", "a font"),
                        )
                    },
                icons =
                    IconPins(
                        repository = icons.string("repository", "qeetrixIcons"),
                        packageName = icons.string("package", "qeetrixIcons"),
                        version = icons.string("version", "qeetrixIcons"),
                        commit = icons.string("commit", "qeetrixIcons"),
                        licenseDestination = icons.string("licenseDestination", "qeetrixIcons"),
                        names =
                            icons["icons"]?.jsonArray?.map { it.jsonPrimitive.content }
                                ?: throw GeneratorError("$PATH: qeetrixIcons needs icons."),
                    ),
            )
        }
    }
}

/**
 * `Upstream/manifest.lock.json`, written only by the sync: exactly which upstream artifacts the
 * snapshot came from, and the sha256 of every file taken from them.
 */
data class Lock(
    val uiPackage: String,
    val uiVersion: String,
    val uiGitHead: String,
    val uiIntegrity: String,
    val iconsRepository: String,
    val iconsPackage: String,
    val iconsVersion: String,
    val iconsCommit: String,
    val fonts: List<LockedFont>,
    /** Repository-relative path → sha256, sorted by path. */
    val files: Map<String, String>,
) {
    data class LockedFont(
        val family: String,
        val webFamily: String,
        val repository: String,
        val tag: String,
        val destination: String,
        val ttfSha256: String,
        val woff2Sha256: String,
        val webFile: String,
    )

    fun toJson(): JsonElement =
        JsonObject(
            linkedMapOf(
                "\$comment" to
                    JsonPrimitive(
                        "Written by Scripts/sync-tokens — do not edit. Records exactly which upstream artifacts this library is built from and the sha256 of every file taken from them; Scripts/verify-tokens fails if any of those files changes without a sync.",
                    ),
                "qeetrixUI" to
                    JsonObject(
                        linkedMapOf(
                            "package" to JsonPrimitive(uiPackage),
                            "version" to JsonPrimitive(uiVersion),
                            "gitHead" to JsonPrimitive(uiGitHead),
                            "integrity" to JsonPrimitive(uiIntegrity),
                        ),
                    ),
                "qeetrixIcons" to
                    JsonObject(
                        linkedMapOf(
                            "repository" to JsonPrimitive(iconsRepository),
                            "package" to JsonPrimitive(iconsPackage),
                            "version" to JsonPrimitive(iconsVersion),
                            "commit" to JsonPrimitive(iconsCommit),
                        ),
                    ),
                "fonts" to
                    JsonArray(
                        fonts.map {
                            JsonObject(
                                linkedMapOf(
                                    "family" to JsonPrimitive(it.family),
                                    "webFamily" to JsonPrimitive(it.webFamily),
                                    "repository" to JsonPrimitive(it.repository),
                                    "tag" to JsonPrimitive(it.tag),
                                    "destination" to JsonPrimitive(it.destination),
                                    "ttfSHA256" to JsonPrimitive(it.ttfSha256),
                                    "woff2SHA256" to JsonPrimitive(it.woff2Sha256),
                                    "webFile" to JsonPrimitive(it.webFile),
                                ),
                            )
                        },
                    ),
                "files" to JsonObject(files.toSortedMap().mapValues { JsonPrimitive(it.value) }),
            ),
        )

    companion object {
        const val PATH = "Upstream/manifest.lock.json"
        val pretty =
            Json {
                prettyPrint = true
                prettyPrintIndent = "  "
            }

        fun read(root: File): Lock {
            val file = File(root, PATH)
            if (!file.exists()) throw GeneratorError("$PATH is missing — run Scripts/sync-tokens.")
            val json = Json.parseToJsonElement(file.readText()).jsonObject

            fun JsonObject.s(key: String) = this[key]?.jsonPrimitive?.content ?: throw GeneratorError("$PATH: missing $key.")
            val ui = json["qeetrixUI"]!!.jsonObject
            val icons = json["qeetrixIcons"]!!.jsonObject
            return Lock(
                uiPackage = ui.s("package"),
                uiVersion = ui.s("version"),
                uiGitHead = ui.s("gitHead"),
                uiIntegrity = ui.s("integrity"),
                iconsRepository = icons.s("repository"),
                iconsPackage = icons.s("package"),
                iconsVersion = icons.s("version"),
                iconsCommit = icons.s("commit"),
                fonts =
                    json["fonts"]!!.jsonArray.map {
                        val font = it.jsonObject
                        LockedFont(
                            font.s("family"),
                            font.s("webFamily"),
                            font.s("repository"),
                            font.s("tag"),
                            font.s("destination"),
                            font.s("ttfSHA256"),
                            font.s("woff2SHA256"),
                            font.s("webFile"),
                        )
                    },
                files = json["files"]!!.jsonObject.mapValues { it.value.jsonPrimitive.content }.toSortedMap(),
            )
        }
    }
}
