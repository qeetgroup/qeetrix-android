# AAR verification

The release AAR of candidate `0.1.0-rc.1`, inspected file by file, and how that inspection is repeated on every
build. Built by `Scripts/release-candidate 0.1.0-rc.1 --devices --allow-dirty` on 2026-10-20.

## The artifact

| | |
|:--|:--|
| Built | `qeetrix/build/outputs/aar/qeetrix-release.aar` (variant `release`) |
| Published as | `build/release-candidate/maven/com/qeetgroup/qeetrix/0.1.0-rc.1/qeetrix-0.1.0-rc.1.aar` — byte-identical to the built file (`Scripts/verify-publication`) |
| SHA-256 | `fe4531774b849afacc054a7c1f4c90290f0a6f0accd1d17601fa80cfb834beee` |
| Size | 1,719,447 bytes |
| Toolchain | AGP 9.4.1, Kotlin 2.4.21, Compose BOM 2026.09.00, Gradle 9.6.1, JDK 21 |

## Contents

| Entry | What | Check |
|:--|:--|:--|
| `classes.jar` | The library's compiled Kotlin: components, foundations, theme, generated tokens and icon paths | Only `com/qeetgroup/qeetrix/**` and `META-INF/` — no test, sample, generator or shaded third-party classes |
| `AndroidManifest.xml` | `package="com.qeetgroup.qeetrix"`, `minSdkVersion 29` | No permissions, application attributes, activities, services, receivers or providers |
| `res/font/qx_cal_sans_vf.ttf`, `res/font/qx_fira_code_vf.ttf` | Qeet Sans (Cal Sans 2.011) and Fira Code 6.2, variable | SHA-256 equal to the upstream TTFs in `Upstream/manifest.lock.json`; one copy each |
| `res/values/values.xml` | 21 strings, all `qx_` | Every resource in `R.txt` is `qx_`-prefixed (2 fonts, 21 strings) |
| `R.txt` | The resource symbol table | — |
| `assets/qeetrix/licenses/` | `CalSans-OFL.txt`, `FiraCode-OFL.txt`, `qeetrix-icons-LICENSE.txt` | All three present; byte-identical to the upstream texts ([LICENSE-COMPLIANCE.md](LICENSE-COMPLIANCE.md)) |
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

An app needs no qeetrix-ui checkout, Node.js, Style Dictionary, the token generator, the sample or this repository:
tokens are compiled into `classes.jar` and fonts and strings are resources. The independent consumer builds from the
Maven artifact alone.

## Repeated on every build

`Scripts/verify-aar` (CI and `Scripts/release-candidate`) fails on any unexpected entry, a missing licence, a
manifest permission or component, an unprefixed resource, a class outside `com.qeetgroup.qeetrix`, or a change to the
minimum SDK, `minCompileSdk` or desugaring. It was shown to fail on a deliberately tampered AAR.

## Findings

None open. The library-level defect found while consuming the artifact — the bottom sheet's empty panel — was in
drawing, not packaging ([CONSUMER-INTEGRATION.md](CONSUMER-INTEGRATION.md)).
