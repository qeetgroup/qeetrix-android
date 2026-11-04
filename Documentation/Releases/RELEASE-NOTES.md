# Qeetrix for Android 0.1.0 — release notes

The first version of Qeet Group's design system for Jetpack Compose, released on **9 November 2026** to **GitHub
Packages**. Built from `@qeetrix/tokens` 0.0.13, `@qeetrix/react` 0.4.0 and `@qeetrix/icons` 2.1.0: its tokens, fonts
and icons are the web's own, and its components look and behave like their ui.qeet.in counterparts. Everything below
is new.

## 📥 Install

GitHub Packages asks for a token even for a public package. Create a GitHub
[personal access token (classic)](https://github.com/settings/tokens) with the **`read:packages`** scope, and keep it in
`~/.gradle/gradle.properties`, never in the app's repository:

```properties
gpr.user=<your GitHub username>
gpr.key=<the token>
```

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        // Qeetrix comes only from its own repository; everything else from Google and Maven Central.
        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://maven.pkg.github.com/qeetgroup/qeetrix-android")
                    credentials {
                        username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                        password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
                    }
                }
            }
            filter { includeGroup("com.qeetgroup") }
        }
        google()
        mavenCentral()
    }
}
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.qeetgroup:qeetrix:0.1.0")
}
```

Then wrap the app's content in `QeetrixTheme { … }`. In an app's own GitHub Actions, the `GITHUB_TOKEN` fallback works
once the package grants that repository read access (the package's **Settings › Manage Actions access**).

| Requires | |
|:--|:--|
| 🤖 Android | 10 (API 29) or newer, phones and tablets |
| 🛠️ An app's build | Compile SDK 37, Android Gradle Plugin 9.1 or newer, Kotlin 2.3 or newer |
| 📦 Brings | Compose runtime, UI, foundation and animation 1.12.1. Material 3, internally only, for the date and time pickers and pull to refresh. |
| 🧹 Keep rules | None |

## ✨ What's in it

- **Foundations:** 109 colour roles in light and dark, 18 text roles in Qeet Sans and Fira Code, spacing, sizes, radii,
  borders, shadows, motion, breakpoints, focus and three densities. 59 icons, sharp and round, outline and filled.
- **Theme:** `QeetrixTheme`, in light, dark or the device's setting, and nestable.
- **Formatting and haptics:** `QeetrixFormat` (numbers, money, percentages, dates, times, relative times, durations)
  and haptics by role.
- **64 components** in seven groups: actions, forms, data display, feedback, overlays, navigation and layout. The full
  list is in the [CHANGELOG](../../CHANGELOG.md), and each has a page in [Components](../Components/README.md).

Not in this version, by decision: a navigation drawer, a popover, a toast, a data table and image presentation.

## ✅ Quality

- **Checked automatically, on every change:** behaviour and TalkBack semantics, contrast in light and dark, 48 dp
  touch targets, no text cut at 200 % or in right to left, reduced motion, Google's Accessibility Test Framework on
  Android 16, and test coverage (94.0 % of lines) ([QUALITY.md](../QUALITY.md)).
- **Checked on devices:** the library's and an independent app's tests on Android 10 and Android 16 emulators, and
  every component by hand on phone and tablet emulators.
- **Verified at release:** every release gate runs again on the merge commit, the files sent are checked against the
  verified checksums, and their build provenance is attested: `gh attestation verify <file> --repo qeetgroup/qeetrix-android`.

## ⚠️ Known limitations

- **TalkBack, Switch Access and keyboard** haven't been tried by a person yet. The automated checks cover names, roles
  and states. The owner accepted this for 0.1.0.
- **Emulators only:** no physical device, foldable or resizable window has been tested.
- **English only:** the 87 built-in strings and one plural are in English; an app can override them to translate.
- The number field's `IntRange` parameter is unstable to the Compose compiler, so an equal range passed afresh re-runs
  that function.

## 🔒 Compatibility and licence

- **Before 1.0,** the API changes only on purpose but may change in a minor release, with migration steps in the
  CHANGELOG ([RELEASE-POLICY.md](RELEASE-POLICY.md)).
- **MIT-licensed** (© 2026 Qeet Group). The bundled fonts (SIL Open Font License 1.1) and icons (MIT, with Lucide's ISC
  notice) keep their own licences; all four texts ship inside the library
  ([LICENSE-COMPLIANCE.md](LICENSE-COMPLIANCE.md)).
