# Qeetrix for Android

The Qeet Group design system for native Android apps, in Kotlin and Jetpack Compose.

Qeetrix is the one design language of every Qeet product. On the web it is
[`@qeetrix/ui`](https://github.com/qeetgroup/qeetrix-ui), documented at [ui.qeet.in](https://ui.qeet.in);
[qeetrix-ios](https://github.com/qeetgroup/qeetrix-ios) brings it to SwiftUI. This library brings the
same tokens, fonts and icons to Compose — generated from the published web release, so a colour, a size
or a type role is the same value on every platform.

**Status: Phase 1 — foundations.** Colours, theming, typography, spacing, corners, sizing, elevation,
motion, icons and accessibility helpers. Components arrive in Phase 2 ([PHASES.md](PHASES.md)).

## Requirements

| | |
|:--|:--|
| Android | API 29 (Android 10) and newer; compiled against API 37 |
| Kotlin / Compose | Kotlin 2.4.21, Compose 1.12.1 (BOM 2026.09.00) |
| Build | Android Gradle Plugin 9.4.1, Gradle 9.6.1 (wrapper), JDK 17 or newer (21 recommended) |
| Runtime dependencies | Compose runtime, UI, foundation and animation-core only; no Material |

## Using Qeetrix in an app

Nothing is published yet. An app includes this repository as a Gradle composite build: Gradle builds
`:qeetrix` from source and substitutes it for the dependency.

```kotlin
// settings.gradle.kts of the app
includeBuild("../qeetrix-android")
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.qeetgroup:qeetrix")
}
```

The app needs the Android SDK and the Compose compiler plugin, as any Compose app does. Consumers never
need Node.js, Style Dictionary or a qeetrix-ui checkout: the tokens are compiled Kotlin.

### Theme

```kotlin
setContent {
    QeetrixTheme {                                  // follows the device's dark-theme setting
        Column(Modifier.background(QeetrixTheme.colors.surfaceCanvas).padding(QeetrixSpacing.L)) {
            BasicText(
                "Security",
                style = QeetrixTheme.typography.title.copy(color = QeetrixTheme.colors.textPrimary),
            )
        }
    }
}
```

`QeetrixTheme(QeetrixThemeMode.Dark)` forces a theme; nesting one draws part of a screen in the other
theme. Store a user's choice as `QeetrixThemeMode.value` (`light`, `dark`, `system`) — the same string
the web and iOS store.

### Typography

Nine roles, as `TextStyle`s: `display`, `heading`, `title`, `body`, `label`, `labelCompact`, `caption`,
`micro` and `code`. They use the web's fonts — Qeet Sans (Cal Sans v2.011) and Fira Code — bundled as
variable fonts with each role's weight, letter style and optical size. Sizes are in sp and follow the
user's font size.

```kotlin
BasicText("Signed in on 3 devices", style = QeetrixTheme.typography.caption)
```

### Icons

A representative set of 12 [qeetrix-icons](https://github.com/qeetgroup/qeetrix-icons) icons, in round
and sharp shapes, outline and filled. They draw in the inherited content colour and mirror in
right-to-left layouts when their meaning has a direction.

```kotlin
QeetrixIcon(QeetrixIconName.Search, contentDescription = null)                 // decorative
QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked", size = QeetrixIconSize.Lg)
```

### Everything else

`QeetrixSpacing`, `QeetrixCorner` / `QeetrixShapes`, `QeetrixStroke`, `QeetrixIconSize`, `QeetrixDensity`,
`QeetrixElevation` with `Modifier.qeetrixElevation(...)`, `QeetrixDuration`, `QeetrixEasing` with
`qeetrixAnimationSpec(...)` (honours Remove animations), `QeetrixAccessibility` and
`Modifier.qeetrixMinimumTouchTarget()`. The API reference, with an overview of each package, is built by
`./gradlew :qeetrix:dokkaGenerate` into `qeetrix/build/dokka/html`.

## Repository

```text
qeetrix/          the library (AAR)        — src/main/kotlin/com/qeetgroup/qeetrix/{theme,foundation,icons,tokens,internal}
sample/           a sample app using the library's public API
Tools/QeetrixGen/ the generator (tokens, fonts, icons), an included Gradle build apps never resolve
Upstream/         the pinned upstream inputs and their lock
Scripts/          sync-tokens, generate-tokens, verify-tokens, verify-aar
Documentation/    decisions, standards, the cross-platform contract, notices, generated reports
```

[ARCHITECTURE.md](ARCHITECTURE.md) explains how it fits together; [CONTRIBUTING.md](CONTRIBUTING.md)
how to work on it.

## Building and testing

```sh
./gradlew check                       # generator tests + drift check, Lint, ktlint, unit tests
./gradlew :qeetrix:assembleRelease    # the AAR → qeetrix/build/outputs/aar/
./gradlew :sample:installDebug        # the sample, on a connected device or emulator
./gradlew :qeetrix:connectedDebugAndroidTest   # device tests (needs a device or emulator)
./gradlew :qeetrix:dokkaGenerate      # API reference
```

Point Gradle at an Android SDK with platform 37 (`ANDROID_HOME` or `local.properties`).

## Current limitations

- **No components.** Phase 1 is foundations only.
- **Not published.** Consume it as a composite build until releases exist.
- **12 icons**, no Qeet logos yet.
- **Device-only checks pending.** The instrumented tests, TalkBack, Bold Text, Indic glyph fallback and
  Display P3 rendering have not been run on a device ([PHASES.md](PHASES.md)).
- **No public API baseline check** yet ([Documentation/Standards/PUBLIC_API.md](Documentation/Standards/PUBLIC_API.md)).

## Licences

The library's own code is Qeet Group's (the repository is private and carries no open-source licence).
It bundles the Cal Sans and Fira Code fonts (SIL OFL 1.1) and qeetrix-icons drawings (MIT, based on
Lucide, ISC): see [Documentation/THIRD_PARTY_NOTICES.md](Documentation/THIRD_PARTY_NOTICES.md).
