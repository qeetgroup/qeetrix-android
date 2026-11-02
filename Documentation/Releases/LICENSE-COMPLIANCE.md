# Licence and distribution rights

Whether everything in `com.qeetgroup:qeetrix` may be distributed, under what terms, and what is still the owner's
decision. Reviewed 2026-10-20 against the release-candidate artifact `0.1.0-rc.1`, and re-checked 2026-10-31 against
`0.1.0-rc.2`: the same three licence texts in the AAR (four since the library's own MIT licence was added on 2 November 2026) (`Scripts/verify-aar`), the fonts unmodified from
`@qeetrix/tokens` 0.0.13 (`Scripts/verify-tokens`), 92 runtime dependencies all Apache 2.0 and an SBOM of 95
components (`Scripts/dependency-inventory`). This is an engineering review of the licences the materials carry, not
legal advice.

## What the artifact contains

| Material | In the artifact | Rights | Evidence |
|:--|:--|:--|:--|
| Qeetrix for Android's own code (Kotlin, generated tokens and icon paths) | `classes.jar` | **MIT** (© 2026 Qeet Group), chosen by the owner on 2 November 2026, as the web's `@qeetrix/tokens`, `@qeetrix/react` and `@qeetrix/icons` are. The repository is public | The repository's `LICENSE`; the POM's `<licenses>`; `assets/qeetrix/licenses/qeetrix-android-LICENSE.txt` in the AAR (`Scripts/verify-aar`) |
| Cal Sans 2.011, variable ("Qeet Sans" on the web) | `res/font/qx_cal_sans_vf.ttf` (since the move to `@qeetrix/react`: `res/font/qx_qeet_sans_vf.ttf`, `@qeetrix/tokens`' `fonts/QeetSans.ttf`, with its `QeetSans-OFL.txt`) | SIL Open Font License 1.1, **no Reserved Font Name**. Bundling with software and redistributing are permitted if the licence travels with the font and the font is not sold on its own | The file's sha256 in the AAR equals the upstream TTF's in `Upstream/manifest.lock.json` (unmodified); its licence text is byte-identical to `calcom/sans` `v2.011` `OFL.txt` (checked 2026-10-20) |
| Fira Code 6.2, variable | `res/font/qx_fira_code_vf.ttf` (since then, `@qeetrix/tokens`' `fonts/FiraCode-VF.ttf` and `FiraCode-OFL.txt`) | SIL OFL 1.1, no Reserved Font Name | The same checks against `tonsky/FiraCode` `6.2` `LICENSE` |
| Qeetrix icon drawings (39; 41 since the move to `@qeetrix/icons` 2, now 2.1.0, whose `LICENSE` is the same text), compiled to path data | `classes.jar` | MIT (Qeet Group), with Lucide's ISC licence and Feather's MIT notice for the artwork they derive from | `assets/qeetrix/licenses/qeetrix-icons-LICENSE.txt` is byte-identical to `qeetrix-icons/LICENSE` at the pinned commit `331cd85` |
| Licence texts | `assets/qeetrix/licenses/` (4 files: the library's own and the three bundled) | — | `Scripts/verify-aar` requires all four |

## Dependencies an app receives

92 artifacts on the locked release runtime classpath, every one **Apache 2.0** as declared in its own POM
(`Scripts/dependency-inventory`, which fails a release candidate on any other or unknown licence): AndroidX
(Compose, Material 3 for the date and time pickers and pull to refresh, Activity, Lifecycle, Core and their dependencies), Kotlin's standard
library, kotlinx-coroutines, JetBrains annotations and JSpecify. None is bundled in the AAR; the app resolves them.

## Tools (not distributed)

Build and test tools run only in this repository and never reach an app: Robolectric (MIT), Roborazzi, Dokka,
the Binary Compatibility Validator, the Accessibility Test Framework and UI Automator (Apache 2.0), ASM (BSD),
ktlint (MIT). [THIRD_PARTY_NOTICES.md](../THIRD_PARTY_NOTICES.md) lists them.

## Classification

| | Items |
|:--|:--|
| **Confirmed rights** | The library's own code (MIT); both fonts (OFL 1.1, unmodified, no Reserved Font Name); the icon artwork (MIT, ISC); all 92 runtime dependencies (Apache 2.0) |
| **Required notices** | The four licence texts — the library's MIT and the three bundled — shipped in the AAR's assets — an app that shows open-source notices can read them from `qeetrix/licenses/`. Apache 2.0 dependencies carry their own NOTICE handling through the app's build |
| **Owner's decision** | Made on 2 November 2026: the library's own code is MIT, as the web's `@qeetrix/tokens`, `@qeetrix/react` and `@qeetrix/icons` are (the older `@qeetrix/ui` was published marked `UNLICENSED`). Still the owner's: the registry and the first version |
| **Distribution blockers** | None: the code is MIT and every bundled asset and dependency may be redistributed |

## How this stays true

- `Scripts/verify-tokens` (in `./gradlew check`) fails if a font, licence text or icon drawing differs from what
  `Upstream/manifest.lock.json` records.
- `Scripts/verify-aar` fails if a licence text is missing from the AAR or anything unexpected is added to it.
- `Scripts/dependency-inventory` fails if a runtime dependency's licence is not one reviewed here, and writes the
  candidate's CycloneDX SBOM (`build/release-candidate/evidence/qeetrix-<version>.cdx.json`).
