# Third-party notices

What Qeetrix for Android redistributes, under which licence, and where the licence text ships. The
library has no third-party runtime code beyond AndroidX — Compose, including Material 3 for the date and time
pickers and pull to refresh, and Activity (Apache 2.0, resolved by the app from Google's Maven repository, not bundled in the
AAR).

| Asset | Shipped as | Source | Licence | Licence text in the AAR |
|:--|:--|:--|:--|:--|
| Qeet Sans — the file is Cal Sans 2.011 (`CalSans-Regular`), variable | `res/font/qx_qeet_sans_vf.ttf` | `@qeetrix/tokens` 0.0.13, `dist/fonts/QeetSans.ttf`, taken unchanged: the TrueType file the package ships for native platforms, decompressed by fontTools 4.68.0 from the web's `QeetSans.woff2` (`fonts/native.json`) | SIL Open Font License 1.1, no Reserved Font Name | `assets/qeetrix/licenses/QeetSans-OFL.txt` (the package's `QeetSans-OFL.txt`) |
| Fira Code — the file is `FiraCode-Light`, Version 6.002, variable | `res/font/qx_fira_code_vf.ttf` | `@qeetrix/tokens` 0.0.13, `dist/fonts/FiraCode-VF.ttf`, taken unchanged, made the same way from `FiraCode-VF.woff2` | SIL Open Font License 1.1, no Reserved Font Name | `assets/qeetrix/licenses/FiraCode-OFL.txt` (the package's `FiraCode-OFL.txt`) |
| Qeetrix icon drawings (41 icons, compiled into path data) | `classes.jar` | `@qeetrix/icons` 2.1.0, its SVG files taken unchanged, based on [Lucide](https://lucide.dev) 1.52.0 | MIT (Qeet Group) with Lucide's ISC licence and Feather's MIT notice | `assets/qeetrix/licenses/qeetrix-icons-LICENSE.txt` (the package's `LICENSE`) |

The fonts are the files `@qeetrix/tokens` ships for Android and iOS: each is accepted only if its sha256, size,
family and axes match the package's `fonts/native.json` and the WOFF2 it was made from is the one the web loads
(`Upstream/manifest.lock.json` records both hashes). WOFF2 decompression keeps every table, axis and glyph; under the
OFL a change of format makes a Modified Version, which the licence allows when, as here, there is no Reserved Font
Name and the font stays under the OFL. The OFL allows bundling and redistribution provided the licence travels with
the font and the font is not sold on its own; the web's "Qeet Sans" family name is used only in the web's CSS and in
documentation, never inside the font file, which is still named Cal Sans. `@qeetrix/icons`' licence also covers Qeet
logos, which this library does not bundle.

The library's own code is MIT-licensed (© 2026 Qeet Group, the repository's `LICENSE`), shipped as
`assets/qeetrix/licenses/qeetrix-android-LICENSE.txt`. An app that shows open-source notices can read all four texts
from its own assets at `qeetrix/licenses/`.

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

