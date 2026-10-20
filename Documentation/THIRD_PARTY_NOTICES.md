# Third-party notices

What Qeetrix for Android redistributes, under which licence, and where the licence text ships. The
library has no third-party runtime code beyond AndroidX — Compose, including Material 3 for the bottom sheet
alone, and Activity (Apache 2.0, resolved by the app from Google's Maven repository, not bundled in the
AAR).

| Asset | Shipped as | Source | Licence | Licence text in the AAR |
|:--|:--|:--|:--|:--|
| Qeet Sans — Cal Sans v2.011, variable | `res/font/qx_cal_sans_vf.ttf` | [calcom/sans](https://github.com/calcom/sans) tag `v2.011`, `fonts/calsans-var-full/CalSansVF.ttf`, unmodified | SIL Open Font License 1.1, no Reserved Font Name | `assets/qeetrix/licenses/CalSans-OFL.txt` |
| Fira Code 6.2, variable | `res/font/qx_fira_code_vf.ttf` | [tonsky/FiraCode](https://github.com/tonsky/FiraCode) release `6.2`, `variable_ttf/FiraCode-VF.ttf`, unmodified | SIL Open Font License 1.1, no Reserved Font Name | `assets/qeetrix/licenses/FiraCode-OFL.txt` |
| Qeetrix icon drawings (39 icons, compiled into path data) | `classes.jar` | [qeetgroup/qeetrix-icons](https://github.com/qeetgroup/qeetrix-icons) `331cd85` (`@qeetrix/icons` 1.0.11), based on [Lucide](https://lucide.dev) 1.52.0 | MIT (Qeet Group) with Lucide's ISC licence and Feather's MIT notice | `assets/qeetrix/licenses/qeetrix-icons-LICENSE.txt` |

The fonts are the files the web ships: each TTF is accepted only if its release's WOFF2 is byte-identical
to the one inside `@qeetrix/ui` 2.1.6 (`Upstream/manifest.lock.json` records both hashes). The OFL allows
bundling and redistribution provided the licence travels with the font and the font is not sold on its
own; the web's "Qeet Sans" family name is used only in the web's CSS and in documentation, never inside
the font file.

An app that shows open-source notices can read the three texts from its own assets at
`qeetrix/licenses/`.

## Build and test tools (not redistributed)

These run only while building or testing this repository; nothing from them is in the AAR or reaches an
app (`Scripts/verify-aar` checks the AAR's contents, and the release classpaths are locked).

| Tool | Used for | Licence |
|:--|:--|:--|
| Robolectric 4.17 | JVM tests of the library and sample | MIT |
| Roborazzi 1.76.0 | Screenshot tests ([0012](Decisions/0012-visual-regression-testing.md)) | Apache 2.0 |
| binary-compatibility-validator 0.18.2, with ASM 9.11.1 and kotlin-metadata-jvm | The public API baseline ([0013](Decisions/0013-public-api-baseline.md)) | Apache 2.0; ASM: BSD 3-Clause |
| Dokka 2.2.0 | API reference | Apache 2.0 |
| ktlint 1.8.0, ktlint-gradle 14.2.0 | Formatting | MIT |
| kotlinx-serialization-json | The generator, `Tools/QeetrixGen` | Apache 2.0 |
| Accessibility Test Framework 4.1.1 (through `ui-test-junit4-accessibility`) | Accessibility checks in the device tests ([0017](Decisions/0017-quality-gates-and-verification.md)) | Apache 2.0 |
| UI Automator 2.4.0 | The independent consumer's release smoke test, `Integration/Consumer` ([0018](Decisions/0018-maven-distribution.md)) | Apache 2.0 |

