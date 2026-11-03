<div align="center">

# 🎨 Qeetrix for Android

**Qeet Group's design system for native Android apps, in Kotlin and Jetpack Compose.**

[![Android](https://img.shields.io/badge/Android-API%2029%2B-3DDC84?logo=android&logoColor=white)](#-requirements)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.21-7F52FF?logo=kotlin&logoColor=white)](#-requirements)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.12.1-4285F4?logo=jetpackcompose&logoColor=white)](#-requirements)
[![@qeetrix/react](https://img.shields.io/badge/%40qeetrix%2Freact-0.4.0-D04800)](Upstream/manifest.json)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](LICENSE)
[![Status](https://img.shields.io/badge/status-pre--release-F59E0B)](#-status)

[📖 Docs](https://ui.qeet.in) · [🧩 Components](Documentation/Components/README.md) · [📝 Changelog](CHANGELOG.md) · [🤝 Contributing](CONTRIBUTING.md)

</div>

Qeetrix is the one design language of every Qeet product. This library brings it to Compose, with the same tokens,
fonts and icons as [`@qeetrix/react`](https://github.com/qeetgroup/qeetrix-react) on the web and
[qeetrix-ios](https://github.com/qeetgroup/qeetrix-ios) on iOS, so a colour or a size is the same on every platform.

## ✨ Highlights

- 🎯 **One source of truth.** Tokens, fonts and icons are generated from `@qeetrix/tokens` and `@qeetrix/icons`.
- 🧩 **60+ components**, drawn as their `@qeetrix/react` counterparts draw them, plus native ones: OTP code, QR
  scanner, swipe actions, pull to refresh and more.
- ♿ **Accessible by default.** TalkBack, 48 dp touch targets, checked contrast, text up to 200 % and reduced motion.
- 🌍 **Every locale.** Right-to-left layouts, and numbers, money and dates in the device's locale.
- 🪶 **Small and stable.** No Material types in the public API, no network access, no permissions.

## 📍 Status

🚧 **Pre-release.** Release candidate `0.1.0-rc.2` passed every release check. No version is published yet.

## 🧰 Requirements

| | |
|:--|:--|
| 🤖 Android | API 29 (Android 10) and newer |
| 📲 Your app | Compile SDK 37, Android Gradle Plugin 9.1+, Kotlin 2.3+ |

## 🚀 Getting started

**Install.** Once a version is published:

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.qeetgroup:qeetrix:<version>")
}
```

Until then, use it from source: add `includeBuild("../qeetrix-android")` to your app's `settings.gradle.kts`, and
depend on `com.qeetgroup:qeetrix` without a version.

**Use.** Wrap your UI in `QeetrixTheme`, then use the components:

```kotlin
setContent {
    QeetrixTheme {                                  // follows the device's dark-theme setting
        Column(Modifier.background(QeetrixTheme.colors.surfaceCanvas).padding(QeetrixSpace.InsetLg)) {
            QeetrixHeading("Security", level = 1)
            QeetrixText("Passkeys and recovery codes", tone = QeetrixTextTone.Subtle)
        }
    }
}
```

```kotlin
val email = rememberTextFieldState()
QeetrixCard(title = "Sign-in email", description = "Where receipts are sent") {
    QeetrixTextField(email, label = "Work email", placeholder = "e.g. asha@acme.in")
    QeetrixSwitch(alerts, { alerts = it }, label = "Email alerts")
    QeetrixButton("Save changes", onClick = ::save, loading = saving)
}
```

## 🧩 Components

| Group | Examples |
|:--|:--|
| 👆 **Actions** | Button, icon button, copy button, segmented control, bottom action bar |
| ✍️ **Forms** | Text field, select, checkbox, radio group, switch, slider, date and time fields, OTP input |
| 📊 **Data display** | List item, avatar, badge, chip, stat, timeline, QR code |
| 💬 **Feedback** | Alert, progress, spinner, skeleton, snackbar, empty state |
| 🪟 **Overlays** | Dialog, bottom sheet, side sheet, menu |
| 🧭 **Navigation** | Top app bar, tabs, navigation bar and rail, steps |
| 📐 **Layout** | Card, accordion, separator |

Each one has its own page in [Documentation/Components](Documentation/Components/README.md).

## 📲 Sample app

[`sample/`](sample) has a page for every component, and switches for dark theme, density, right to left and large
text.

```sh
./gradlew :sample:installDebug
```

## 🔨 Building

You need JDK 17 or newer and the Android SDK with platform 37.

```sh
./gradlew check                      # lint, tests and the public API check
./gradlew :qeetrix:assembleRelease   # the AAR, in qeetrix/build/outputs/aar/
```

Every other command is in [CONTRIBUTING.md](CONTRIBUTING.md).

## 📚 Learn more

- 📖 [ui.qeet.in](https://ui.qeet.in): the Qeetrix design system
- 🧩 [Components](Documentation/Components/README.md): a page for each Android component
- 🏗️ [ARCHITECTURE.md](ARCHITECTURE.md): how the library is built
- 📝 [CHANGELOG.md](CHANGELOG.md): what changed

## 📜 License

[MIT](LICENSE) © 2026 Qeet Group. The bundled fonts and icons keep their own licences
([notices](Documentation/THIRD_PARTY_NOTICES.md)). To report a vulnerability, see [SECURITY.md](SECURITY.md).

---

<div align="center">

Made with 🧡 by Qeet Group · [⬆️ Back to top](#-qeetrix-for-android)

</div>
