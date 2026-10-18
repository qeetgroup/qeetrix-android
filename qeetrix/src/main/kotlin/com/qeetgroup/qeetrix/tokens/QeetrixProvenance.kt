package com.qeetgroup.qeetrix.tokens

import com.qeetgroup.qeetrix.tokens.generated.QeetrixProvenanceTokens

/**
 * Which upstream artifacts this build of Qeetrix was generated from — for diagnostics and about
 * screens, and for checking that a web, iOS and Android build ship the same design-system release.
 */
public object QeetrixProvenance {
    /** The npm package the tokens come from, `@qeetrix/ui`. */
    public val tokensPackage: String = QeetrixProvenanceTokens.TOKENS_PACKAGE

    /** The `@qeetrix/ui` version, e.g. `2.1.6`. */
    public val tokensVersion: String = QeetrixProvenanceTokens.TOKENS_VERSION

    /** The qeetrix-ui commit the package was published from. */
    public val tokensGitHead: String = QeetrixProvenanceTokens.TOKENS_GIT_HEAD

    /** The npm tarball's Subresource Integrity hash. */
    public val tokensIntegrity: String = QeetrixProvenanceTokens.TOKENS_INTEGRITY

    /** The upstream themes, base first. */
    public val themes: List<String> = QeetrixProvenanceTokens.THEMES

    /** How many upstream tokens are generated as Kotlin. */
    public val generatedTokens: Int = QeetrixProvenanceTokens.GENERATED_TOKENS

    /** How many upstream tokens are deliberately not (Documentation/Foundations/TOKEN_COVERAGE.md). */
    public val excludedTokens: Int = QeetrixProvenanceTokens.EXCLUDED_TOKENS

    /** The icon package, `@qeetrix/icons`. */
    public val iconsPackage: String = QeetrixProvenanceTokens.ICONS_PACKAGE

    /** The `@qeetrix/icons` version the drawings were taken at. */
    public val iconsVersion: String = QeetrixProvenanceTokens.ICONS_VERSION

    /** The qeetrix-icons commit the drawings were taken from. */
    public val iconsCommit: String = QeetrixProvenanceTokens.ICONS_COMMIT

    /** The bundled fonts. */
    public val fonts: List<QeetrixFontProvenance> =
        QeetrixProvenanceTokens.FONTS.map {
            QeetrixFontProvenance(it.family, it.webFamily, it.postScriptName, it.version, it.repository, it.tag, it.sha256)
        }
}

/**
 * Where a bundled font came from.
 *
 * @property family `sans` or `mono`.
 * @property webFamily The CSS family name on the web, e.g. `Qeet Sans`.
 * @property postScriptName The PostScript name inside the file.
 * @property version The version string inside the file.
 * @property repository The upstream font project, e.g. `calcom/sans`.
 * @property tag The release tag the file was taken from.
 * @property sha256 The sha256 of the bundled file.
 */
public class QeetrixFontProvenance internal constructor(
    public val family: String,
    public val webFamily: String,
    public val postScriptName: String,
    public val version: String,
    public val repository: String,
    public val tag: String,
    public val sha256: String,
) {
    override fun toString(): String = "QeetrixFontProvenance($family: $webFamily = $postScriptName $version, $repository@$tag)"
}
