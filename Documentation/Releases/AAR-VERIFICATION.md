# AAR verification

The release AAR of candidate `0.1.0-rc.2`, inspected file by file, and how that inspection is repeated on every
build. Built by `Scripts/release-candidate 0.1.0-rc.2 --devices --allow-dirty` on 2026-10-31, from commit `9291aa1`.

## The artifact

| | |
|:--|:--|
| Built | `qeetrix/build/outputs/aar/qeetrix-release.aar` (variant `release`) |
| Published as | `build/release-candidate/maven/com/qeetgroup/qeetrix/0.1.0-rc.2/qeetrix-0.1.0-rc.2.aar` — byte-identical to the built file (`Scripts/verify-publication`) |
| SHA-256 | `ebb1a27b3545056293c3cf0cc5f8954c083919f9027b654fd0e6b4fa5fedfc48` |
| Size | 2,391,362 bytes (`0.1.0-rc.1`: 1,719,447 bytes, with 21 strings) |
| Toolchain | AGP 9.4.1, Kotlin 2.4.21, Compose BOM 2026.09.00, Gradle 9.6.1, JDK 25 |

## Contents

| Entry | What | Check |
|:--|:--|:--|
| `classes.jar` | The library's compiled Kotlin: components, foundations, theme, generated tokens and icon paths | Only `com/qeetgroup/qeetrix/**` and `META-INF/` — no test, sample, generator or shaded third-party classes |
| `AndroidManifest.xml` | `package="com.qeetgroup.qeetrix"`, `minSdkVersion 29` | No permissions, application attributes, activities, services, receivers or providers |
| `res/font/qx_qeet_sans_vf.ttf`, `res/font/qx_fira_code_vf.ttf` | Qeet Sans (the file is Cal Sans 2.011) and Fira Code 6.002, variable: the TrueType files `@qeetrix/tokens` 0.0.13 ships for native platforms | SHA-256 equal to the upstream TTFs in `Upstream/manifest.lock.json`; one copy each |
| `res/values/values.xml` | 85 strings and 1 plural, all `qx_` | Every resource in `R.txt` is `qx_`-prefixed (2 fonts, 85 strings, 1 plural) |
| `R.txt` | The resource symbol table | — |
| `assets/qeetrix/licenses/` | `qeetrix-android-LICENSE.txt` (the library's MIT licence, from the next candidate), `QeetSans-OFL.txt`, `FiraCode-OFL.txt`, `qeetrix-icons-LICENSE.txt` | All three present; byte-identical to the upstream texts ([LICENSE-COMPLIANCE.md](LICENSE-COMPLIANCE.md)) |
| `META-INF/com/android/build/gradle/aar-metadata.properties` | `minCompileSdk=37`, `minAndroidGradlePluginVersion=1.0.0`, `coreLibraryDesugaringEnabled=false` | `minCompileSdk` 37 adds nothing: Compose UI and Foundation 1.12.1 already require compile SDK 37 and AGP 9.1.0 |

Not in the AAR, correctly: icons as drawables (they are compiled Kotlin path data), sample code or images, screenshot
baselines, research or upstream source files, `proguard.txt` (no consumer rules are needed) and `lint.jar`.

## Resources resolve in an app

- **Debug:** the consumer resolves both fonts and a string through the library's `R` class on API 29 and 36
  (`ConsumerAppTest.theFontsAndStringsResolveFromTheArtifact`).
- **R8 and resource shrinking:** in the consumer's optimised release APK both fonts survive at full size and the
  strings its components reference remain; strings of components it does not use (accordion, removable chip, data
  state) are removed, as they should be. The release capture shows Qeet Sans and Fira Code rendered.
- **Collisions:** every resource is `qx_`-prefixed (`resourcePrefix`), and with non-transitive R classes an app reaches
  them only through `com.qeetgroup.qeetrix.R`.
- **Manifest merge:** in the consumer's merged manifest Qeetrix contributes nothing. The only merged entries come
  from AndroidX libraries every Compose app receives (`androidx.core`'s not-exported-receiver permission, the
  `emoji2` and `lifecycle-process` startup entries, `profileinstaller`'s receiver).

## Independence

An app needs no Qeetrix npm package, Node.js, the token generator, the sample or this repository:
tokens are compiled into `classes.jar` and fonts and strings are resources. The independent consumer builds from the
Maven artifact alone.

## Repeated on every build

`Scripts/verify-aar` (CI and `Scripts/release-candidate`) fails on any unexpected entry, a missing licence, a
manifest permission or component, an unprefixed resource, a class outside `com.qeetgroup.qeetrix`, or a change to the
minimum SDK, `minCompileSdk` or desugaring. It was shown to fail on a deliberately tampered AAR.

## Findings

None open. The library-level defect found while consuming the artifact — the bottom sheet's empty panel — was in
drawing, not packaging ([CONSUMER-INTEGRATION.md](CONSUMER-INTEGRATION.md)).
