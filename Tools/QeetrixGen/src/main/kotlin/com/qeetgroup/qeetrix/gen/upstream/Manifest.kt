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
 * same pins as qeetrix-ios, with Android destinations. Every artifact is an npm-published tarball:
 * `@qeetrix/tokens` (the DTCG tokens and the native TrueType fonts), `@qeetrix/react` (the component
 * manifest and the CSS the components are specified by) and `@qeetrix/icons` (the drawings and their
 * manifest).
 */
data class Manifest(
    val tokens: TokensPin,
    val react: ReactPin,
    val fonts: List<FontPin>,
    /** `fonts/native.json` in the `@qeetrix/tokens` tarball: each TTF's axes, size and sha256, and its WOFF2. */
    val nativeFonts: String,
    val icons: IconPins,
) {
    /** A package on the npm registry at an exact version. */
    interface NpmPin {
        val packageName: String
        val version: String
    }

    data class TokensPin(
        override val packageName: String,
        override val version: String,
        /** The tarball directory holding the DTCG build (`package/dist/dtcg`), copied whole. */
        val dtcg: String,
        val destination: String,
        /** The `component.<group>` token groups Android implements — the same groups as qeetrix-ios. */
        val componentTokens: List<String>,
    ) : NpmPin

    data class ReactPin(
        override val packageName: String,
        override val version: String,
        /** The component manifest (`dist/manifest.json`) inside the tarball. */
        val manifest: String,
        val manifestDestination: String,
        /** The tarball directory holding each family's compiled CSS. */
        val css: String,
        val cssDestination: String,
        /** The families whose CSS Android's components are specified by (`button`, `experimental/sidebar`, …). */
        val families: List<String>,
    ) : NpmPin

    data class FontPin(
        /** The family key the typography roles use: `sans` or `mono`. */
        val family: String,
        /** The family name the web's CSS uses for it, e.g. `Qeet Sans`. */
        val webFamily: String,
        /** The full variable TrueType font inside the `@qeetrix/tokens` tarball, listed in its `native.json`. */
        val ttf: String,
        /** The variation axes the TTF must have. */
        val axes: List<String>,
        val destination: String,
        val licenseDestination: String,
    )

    data class IconPins(
        override val packageName: String,
        override val version: String,
        /** The icon catalogue as JSON (`dist/manifest.json`) inside the tarball. */
        val manifest: String,
        /** The tarball directory of plain SVG drawings, `<shape>-<variant>/<id>.svg`. */
        val svg: String,
        val license: String,
        val licenseDestination: String,
        /** The core icons, in `QeetrixIconName`: the ones the components and the sample draw. */
        val names: List<String>,
        /** Whether every other icon in the catalogue is generated too, into the optional extended module. */
        val extended: Boolean,
    ) : NpmPin

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

            fun JsonObject.strings(
                key: String,
                context: String,
            ): List<String> =
                (this[key] as? JsonArray)?.map { it.jsonPrimitive.content } ?: throw GeneratorError("$PATH: $context needs \"$key\".")

            fun JsonObject.section(key: String): JsonObject = this[key] as? JsonObject ?: throw GeneratorError("$PATH: missing $key.")

            val tokens = json.section("qeetrixTokens")
            val react = json.section("qeetrixReact")
            val icons = json.section("qeetrixIcons")
            val fonts = json["fonts"]?.jsonArray ?: throw GeneratorError("$PATH: missing fonts.")
            return Manifest(
                tokens =
                    TokensPin(
                        packageName = tokens.string("package", "qeetrixTokens"),
                        version = tokens.string("version", "qeetrixTokens"),
                        dtcg = tokens.string("dtcg", "qeetrixTokens"),
                        destination = tokens.string("destination", "qeetrixTokens"),
                        componentTokens = tokens.strings("componentTokens", "qeetrixTokens"),
                    ),
                react =
                    ReactPin(
                        packageName = react.string("package", "qeetrixReact"),
                        version = react.string("version", "qeetrixReact"),
                        manifest = react.string("manifest", "qeetrixReact"),
                        manifestDestination = react.string("manifestDestination", "qeetrixReact"),
                        css = react.string("css", "qeetrixReact"),
                        cssDestination = react.string("cssDestination", "qeetrixReact"),
                        families = react.strings("families", "qeetrixReact"),
                    ),
                fonts =
                    fonts.map { element ->
                        val font = element.jsonObject
                        FontPin(
                            family = font.string("family", "a font"),
                            webFamily = font.string("webFamily", "a font"),
                            ttf = font.string("ttf", "a font"),
                            axes = font.strings("axes", "a font"),
                            destination = font.string("destination", "a font"),
                            licenseDestination = font.string("licenseDestination", "a font"),
                        )
                    },
                nativeFonts = json.string("nativeFonts", "the manifest"),
                icons =
                    IconPins(
                        packageName = icons.string("package", "qeetrixIcons"),
                        version = icons.string("version", "qeetrixIcons"),
                        manifest = icons.string("manifest", "qeetrixIcons"),
                        svg = icons.string("svg", "qeetrixIcons"),
                        license = icons.string("license", "qeetrixIcons"),
                        licenseDestination = icons.string("licenseDestination", "qeetrixIcons"),
                        names = icons.strings("icons", "qeetrixIcons"),
                        extended =
                            (icons["extended"] ?: throw GeneratorError("$PATH: qeetrixIcons needs \"extended\"."))
                                .jsonPrimitive.content
                                .toBooleanStrictOrNull()
                                ?: throw GeneratorError("$PATH: qeetrixIcons.extended must be true or false."),
                    ),
            )
        }
    }
}

/** One fetched npm package: the registry's record of exactly which tarball it is. */
data class LockedPackage(
    val packageName: String,
    val version: String,
    val gitHead: String,
    val integrity: String,
) {
    fun toJson(): JsonObject =
        JsonObject(
            linkedMapOf(
                "package" to JsonPrimitive(packageName),
                "version" to JsonPrimitive(version),
                "gitHead" to JsonPrimitive(gitHead),
                "integrity" to JsonPrimitive(integrity),
            ),
        )

    companion object {
        fun from(
            json: JsonObject,
            context: String,
        ): LockedPackage {
            fun s(key: String) = json[key]?.jsonPrimitive?.content ?: throw GeneratorError("${Lock.PATH}: $context is missing $key.")
            return LockedPackage(s("package"), s("version"), s("gitHead"), s("integrity"))
        }
    }
}

/**
 * `Upstream/manifest.lock.json`, written only by the sync: exactly which upstream tarballs the
 * snapshot came from, how the fonts were converted, and the sha256 of every file taken from them.
 */
data class Lock(
    val tokens: LockedPackage,
    val react: LockedPackage,
    val icons: LockedPackage,
    /** The fontTools version `native.json` says made the TTFs from the web's WOFF2. */
    val fontTools: String,
    val fonts: List<LockedFont>,
    /** Repository-relative path → sha256, sorted by path. */
    val files: Map<String, String>,
) {
    data class LockedFont(
        val family: String,
        val webFamily: String,
        /** The TTF inside the `@qeetrix/tokens` tarball. */
        val ttf: String,
        val ttfSha256: String,
        /** The web's WOFF2 the TTF was made from, per `native.json`. */
        val woff2: String,
        val woff2Sha256: String,
        val destination: String,
    )

    fun toJson(): JsonElement =
        JsonObject(
            linkedMapOf(
                "\$comment" to
                    JsonPrimitive(
                        "Written by Scripts/sync-tokens — do not edit. Records exactly which npm tarballs this library is built from (the registry's integrity hash and git head), the native fonts @qeetrix/tokens ships (each TTF's sha256 and the web WOFF2 it was made from, per its fonts/native.json) and the sha256 of every file taken from them; Scripts/verify-tokens fails if any of those files changes without a sync.",
                    ),
                "qeetrixTokens" to tokens.toJson(),
                "qeetrixReact" to react.toJson(),
                "qeetrixIcons" to icons.toJson(),
                "fontTools" to JsonPrimitive(fontTools),
                "fonts" to
                    JsonArray(
                        fonts.map {
                            JsonObject(
                                linkedMapOf(
                                    "family" to JsonPrimitive(it.family),
                                    "webFamily" to JsonPrimitive(it.webFamily),
                                    "ttf" to JsonPrimitive(it.ttf),
                                    "ttfSHA256" to JsonPrimitive(it.ttfSha256),
                                    "woff2" to JsonPrimitive(it.woff2),
                                    "woff2SHA256" to JsonPrimitive(it.woff2Sha256),
                                    "destination" to JsonPrimitive(it.destination),
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

            fun section(key: String) = json[key] as? JsonObject ?: throw GeneratorError("$PATH: missing $key — run Scripts/sync-tokens.")
            return Lock(
                tokens = LockedPackage.from(section("qeetrixTokens"), "qeetrixTokens"),
                react = LockedPackage.from(section("qeetrixReact"), "qeetrixReact"),
                icons = LockedPackage.from(section("qeetrixIcons"), "qeetrixIcons"),
                fontTools = json.s("fontTools"),
                fonts =
                    json["fonts"]!!.jsonArray.map {
                        val font = it.jsonObject
                        LockedFont(
                            family = font.s("family"),
                            webFamily = font.s("webFamily"),
                            ttf = font.s("ttf"),
                            ttfSha256 = font.s("ttfSHA256"),
                            woff2 = font.s("woff2"),
                            woff2Sha256 = font.s("woff2SHA256"),
                            destination = font.s("destination"),
                        )
                    },
                files = json["files"]!!.jsonObject.mapValues { it.value.jsonPrimitive.content }.toSortedMap(),
            )
        }
    }
}
