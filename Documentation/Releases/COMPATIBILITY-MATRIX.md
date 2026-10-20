# Compatibility matrix

Exactly which toolchains and devices Qeetrix `0.1.0-rc.1` was built and run with, on 2026-10-20. Anything not listed
was not tested; no claim is made for it.

## App toolchains

The independent consumer (`Integration/Consumer`) built against the published-format artifact, with its toolchain
set by properties (`-Pconsumer.agp`, `-Pconsumer.kotlin`). Gradle 9.6.1 and JDK 21 throughout.

| | AGP | Kotlin | Compile SDK | Debug build | R8 release build | On a device |
|:--|:--|:--|:--|:--|:--|:--|
| Current | 9.4.1 | 2.4.21 | 37 | ✓ | ✓ | ✓ API 29, API 36 phone, API 36 tablet |
| **Minimum** | **9.1.0** | **2.3.0** | 37 | ✓ | ✓ | ✓ API 36: release smoke 4 / 4; debug tests 11 / 12, the twelfth a test race since fixed (below) and passing in every later run |
| Below the minimum | 9.1.0 | 2.2.21 | 37 | ✗ — `Module was compiled with an incompatible version of Kotlin. The binary version of its metadata is 2.4.0, expected version is 2.2.0` | — | — |

Why the minimums are what they are:

- **AGP 9.1 and compile SDK 37** come from Compose 1.12 itself: `androidx.compose.ui:ui` and `foundation` 1.12.1
  declare `minAndroidGradlePluginVersion=9.1.0` and `minCompileSdk=37` in their AAR metadata. Qeetrix's own AAR
  declares `minCompileSdk=37` and adds nothing. AGP 9.1.0 warns that it was tested only up to compile SDK 36.1; the
  build succeeds.
- **Kotlin 2.3** because Qeetrix and the Kotlin standard library it is built with (2.4.21) carry Kotlin metadata 2.4,
  which a Kotlin compiler can read one version back, not two.
- Older AGP, Gradle or Compose versions were not tried, and cannot work: the metadata above refuses them.

The minimum-toolchain debug failure was `aConfirmationCancelsOnBack`: the test sent Back before the confirmation
dialog's window had input focus. It now waits for the activity's window to lose focus, as the library's own Back test
does.

## Devices

All emulators; **no physical device**. Google APIs images, arm64, on an Apple M1 Pro.

| Device | Android | Screen | Navigation | Library device tests | Consumer, debug (12) | Consumer, R8 release (4) |
|:--|:--|:--|:--|:--|:--|:--|
| Phone, `qeetrix-api29` | 10 (API 29), the minimum | 1080 × 2280, 440 dpi, portrait | 3-button | 11 / 11 (ATF tests left out below API 36) | ✓ | ✓ |
| Phone, `qeetrix-api36` | 16 (API 36) | 1080 × 2400, 420 dpi, portrait | Gesture | 13 / 13 | ✓ | ✓ |
| Tablet, `qeetrix-tablet-api36` (Pixel Tablet profile) | 16 (API 36) | 2560 × 1600, 320 dpi, landscape | Gesture | 13 / 13 | ✓ — a navigation rail, as the scaffold chooses from 600 dp | ✓ |
| Phone, `qeetrix-api36`, landscape | 16 (API 36) | 2400 × 1080 (rotation confirmed during the run) | Gesture | — | ✓ — a rail at 914 dp | ✓ |

## Configurations

| Configuration | How it was tested | Result |
|:--|:--|:--|
| Light and dark | The consumer switches theme; the screen's brightness is measured. JVM screenshots of every component in both | ✓ |
| Larger text | The consumer doubles the font scale in its composition; text grows. JVM scans at 200 %, linear and Android's non-linear curve | ✓ |
| Right to left | The consumer switches layout direction; content moves. JVM screenshots; device capture of localized specimens | ✓ in-place content; dialogs and popups take direction from the system locale, tested on the JVM only |
| System Back | Key events on dialogs, the sheet and menus, API 29 and 36 | ✓ |
| Predictive back gesture | — | Not tested |
| Edge to edge | The consumer calls `enableEdgeToEdge()`; captures show the app bar and navigation clear of the system bars | ✓ by inspection |
| Keyboard and focus | JVM tests of focus and keys (Phase 4); not on a device | Partly |
| Densities | 320, 420 and 440 dpi devices; xhdpi (320) on the JVM | ✓ |
| Resizable windows, foldables | — | Not tested |
| Wear OS, TV, Auto | Out of scope | — |
