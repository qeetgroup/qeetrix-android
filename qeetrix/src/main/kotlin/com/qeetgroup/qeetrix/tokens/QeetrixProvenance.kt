package com.qeetgroup.qeetrix.tokens

import com.qeetgroup.qeetrix.tokens.generated.QeetrixProvenanceTokens

/**
 * Which upstream packages this build of Qeetrix was generated from — for diagnostics and about
 * screens, and for checking that a web, iOS and Android build ship the same design-system release.
 */
public object QeetrixProvenance {
    /** The tokens package, `@qeetrix/tokens`. */
    public val tokens: QeetrixPackageProvenance =
        QeetrixPackageProvenance(
            QeetrixProvenanceTokens.TOKENS_PACKAGE,
            QeetrixProvenanceTokens.TOKENS_VERSION,
            QeetrixProvenanceTokens.TOKENS_GIT_HEAD,
            QeetrixProvenanceTokens.TOKENS_INTEGRITY,
        )

    /** The component library the components are specified by, `@qeetrix/react`. */
    public val react: QeetrixPackageProvenance =
        QeetrixPackageProvenance(
            QeetrixProvenanceTokens.REACT_PACKAGE,
            QeetrixProvenanceTokens.REACT_VERSION,
            QeetrixProvenanceTokens.REACT_GIT_HEAD,
            QeetrixProvenanceTokens.REACT_INTEGRITY,
        )

    /** The icon package, `@qeetrix/icons`. */
    public val icons: QeetrixPackageProvenance =
        QeetrixPackageProvenance(
            QeetrixProvenanceTokens.ICONS_PACKAGE,
            QeetrixProvenanceTokens.ICONS_VERSION,
            QeetrixProvenanceTokens.ICONS_GIT_HEAD,
            QeetrixProvenanceTokens.ICONS_INTEGRITY,
        )

    /** The colour schemes, the default first. */
    public val themes: List<String> = QeetrixProvenanceTokens.THEMES

    /** The densities, in the resolver's order. */
    public val densities: List<String> = QeetrixProvenanceTokens.DENSITIES

    /** How many upstream tokens are generated as Kotlin. */
    public val generatedTokens: Int = QeetrixProvenanceTokens.GENERATED_TOKENS

    /** How many upstream tokens are deliberately not (docs/tokens.md). */
    public val excludedTokens: Int = QeetrixProvenanceTokens.EXCLUDED_TOKENS

    /** The bundled fonts. */
    public val fonts: List<QeetrixFontProvenance> =
        QeetrixProvenanceTokens.FONTS.map {
            QeetrixFontProvenance(it.family, it.webFamily, it.postScriptName, it.version, it.source, it.woff2Sha256, it.sha256)
        }
}

/**
 * One npm package a build was generated from.
 *
 * @property packageName e.g. `@qeetrix/tokens`.
 * @property version e.g. `0.0.11`.
 * @property gitHead The commit the package was published from.
 * @property integrity The npm tarball's Subresource Integrity hash.
 */
public class QeetrixPackageProvenance internal constructor(
    public val packageName: String,
    public val version: String,
    public val gitHead: String,
    public val integrity: String,
) {
    override fun toString(): String = "$packageName@$version (${gitHead.take(7)})"
}

/**
 * Where a bundled font came from.
 *
 * @property family `sans` or `mono`.
 * @property webFamily The CSS family name on the web, e.g. `Qeet Sans`.
 * @property postScriptName The PostScript name inside the file.
 * @property version The version string inside the file.
 * @property source The TrueType file inside `@qeetrix/tokens` it is, e.g. `dist/fonts/QeetSans.ttf`.
 * @property woff2Sha256 The sha256 of the web's WOFF2 the TTF was made from (`fonts/native.json`).
 * @property sha256 The sha256 of the bundled TTF.
 */
public class QeetrixFontProvenance internal constructor(
    public val family: String,
    public val webFamily: String,
    public val postScriptName: String,
    public val version: String,
    public val source: String,
    public val woff2Sha256: String,
    public val sha256: String,
) {
    override fun toString(): String = "QeetrixFontProvenance($family: $webFamily = $postScriptName $version, from $source)"
}
