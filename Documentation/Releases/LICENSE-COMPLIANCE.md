# Licence and distribution rights

Whether everything in `com.qeetgroup:qeetrix` may be distributed, under what terms, and what is still the owner's
decision. Reviewed 2026-10-20 against the release-candidate artifact `0.1.0-rc.1`. This is an engineering review of
the licences the materials carry, not legal advice.

## What the artifact contains

| Material | In the artifact | Rights | Evidence |
|:--|:--|:--|:--|
| Qeetrix for Android's own code (Kotlin, generated tokens and icon paths) | `classes.jar` | Qeet Group's. The repository is private and carries **no licence** | README › Licences; `gh repo view`: private, no licence |
| Cal Sans 2.011, variable ("Qeet Sans" on the web) | `res/font/qx_cal_sans_vf.ttf` | SIL Open Font License 1.1, **no Reserved Font Name**. Bundling with software and redistributing are permitted if the licence travels with the font and the font is not sold on its own | The file's sha256 in the AAR equals the upstream TTF's in `Upstream/manifest.lock.json` (unmodified); its licence text is byte-identical to `calcom/sans` `v2.011` `OFL.txt` (checked 2026-10-20) |
| Fira Code 6.2, variable | `res/font/qx_fira_code_vf.ttf` | SIL OFL 1.1, no Reserved Font Name | The same checks against `tonsky/FiraCode` `6.2` `LICENSE` |
| Qeetrix icon drawings (39), compiled to path data | `classes.jar` | MIT (Qeet Group), with Lucide's ISC licence and Feather's MIT notice for the artwork they derive from | `assets/qeetrix/licenses/qeetrix-icons-LICENSE.txt` is byte-identical to `qeetrix-icons/LICENSE` at the pinned commit `331cd85` |
| Licence texts | `assets/qeetrix/licenses/` (3 files) | — | `Scripts/verify-aar` requires all three |

## Dependencies an app receives

92 artifacts on the locked release runtime classpath, every one **Apache 2.0** as declared in its own POM
(`Scripts/dependency-inventory`, which fails a release candidate on any other or unknown licence): AndroidX
(Compose, Material 3 for the bottom sheet, Activity, Lifecycle, Core and their dependencies), Kotlin's standard
library, kotlinx-coroutines, JetBrains annotations and JSpecify. None is bundled in the AAR; the app resolves them.

## Tools (not distributed)

Build and test tools run only in this repository and never reach an app: Robolectric (MIT), Roborazzi, Dokka,
the Binary Compatibility Validator, the Accessibility Test Framework and UI Automator (Apache 2.0), ASM (BSD),
ktlint (MIT). [THIRD_PARTY_NOTICES.md](../THIRD_PARTY_NOTICES.md) lists them.

## Classification

| | Items |
|:--|:--|
| **Confirmed rights** | Both fonts (OFL 1.1, unmodified, no Reserved Font Name); the icon artwork (MIT, ISC); all 92 runtime dependencies (Apache 2.0) |
| **Required notices** | The three licence texts, shipped in the AAR's assets — an app that shows open-source notices can read them from `qeetrix/licenses/`. Apache 2.0 dependencies carry their own NOTICE handling through the app's build |
| **Owner's decision** | A licence for Qeetrix's own code. For distribution inside Qeet Group no licence is needed: the code is the organisation's own. Distributing outside it — Maven Central, partners, a public repository — needs one chosen by the owner. The web package, `@qeetrix/ui`, is published publicly to npm marked `UNLICENSED`; that is precedent, not a decision for Android |
| **Distribution blockers** | None for internal distribution. For public distribution: the licence above |

## How this stays true

- `Scripts/verify-tokens` (in `./gradlew check`) fails if a font, licence text or icon drawing differs from what
  `Upstream/manifest.lock.json` records.
- `Scripts/verify-aar` fails if a licence text is missing from the AAR or anything unexpected is added to it.
- `Scripts/dependency-inventory` fails if a runtime dependency's licence is not one reviewed here, and writes the
  candidate's CycloneDX SBOM (`build/release-candidate/evidence/qeetrix-<version>.cdx.json`).
