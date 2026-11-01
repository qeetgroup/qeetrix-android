<div align="center">

# 🎨 Qeetrix for Android

**The Qeet Group design system for native Android apps, in Kotlin and Jetpack Compose.**

[![Android](https://img.shields.io/badge/Android-API%2029%2B-3DDC84?logo=android&logoColor=white)](#-requirements)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.21-7F52FF?logo=kotlin&logoColor=white)](#-requirements)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.12.1-4285F4?logo=jetpackcompose&logoColor=white)](#-requirements)
[![@qeetrix/react](https://img.shields.io/badge/%40qeetrix%2Freact-0.4.0-D04800)](Upstream/manifest.json)
[![@qeetrix/tokens](https://img.shields.io/badge/%40qeetrix%2Ftokens-0.0.13-D04800)](Upstream/manifest.json)
[![@qeetrix/icons](https://img.shields.io/badge/%40qeetrix%2Ficons-2.1.0-D04800)](Upstream/manifest.json)
[![Status](https://img.shields.io/badge/status-pre--release-F59E0B)](#-status)

🔐 Qeet ID · 💳 Qeet Pay · 👥 Qeet People · 🔔 Qeet Notify · 📈 Qeet Logs — one design language, every platform

</div>

Qeetrix is the one design language of every Qeet product. On the web it is
[`@qeetrix/react`](https://github.com/qeetgroup/qeetrix-react), built on
[`@qeetrix/tokens`](https://github.com/qeetgroup/qeetrix-tokens) and [`@qeetrix/icons`](https://github.com/qeetgroup/qeetrix-icons)
and documented at [ui.qeet.in](https://ui.qeet.in); [qeetrix-ios](https://github.com/qeetgroup/qeetrix-ios) brings
it to SwiftUI. This library brings the same tokens, fonts and icons to Compose — generated from the published npm
packages, so a colour, a size or a type role is the same value on every platform — and draws each component as its
`@qeetrix/react` counterpart's CSS and the design rules draw it.

| | |
|:--|:--|
| 📦 **Built from** | `@qeetrix/tokens` 0.0.13 · `@qeetrix/react` 0.4.0 · `@qeetrix/icons` 2.1.0 ([pins](Upstream/manifest.json)) |
| 🧩 **Components** | 62 component pages, over 90 public composables, in seven groups ([catalogue](Documentation/Components/README.md)) |
| 🎨 **Foundations** | Colour roles in light and dark, 18 type roles, space, sizes, radii, borders, shadows, motion, densities, breakpoints, formatting and haptics |
| ✨ **Icons** | 59, sharp and round, outline and (39 of them) filled |
| 🤖 **Android** | API 29 (Android 10) and newer, compiled against API 37 |
| 📍 **Coordinates** | `com.qeetgroup:qeetrix` — not published yet ([status](#-status)) |
| ✅ **Tests** | 492 library, 18 sample and 38 generator tests; screenshots of every component in light, dark, 200 % text and right to left |

## 📑 Contents

- 🚀 [Why Qeetrix for Android](#-why-qeetrix-for-android)
- 📍 [Status](#-status)
- 🧰 [Requirements](#-requirements)
- 📦 [Using Qeetrix in an app](#-using-qeetrix-in-an-app) — installation, theme, components, native components,
  typography, icons, formatting and everything else
- 🧩 [Components at a glance](#-components-at-a-glance)
- ♿ [Accessibility and localisation](#-accessibility-and-localisation)
- 📲 [The sample app](#-the-sample-app)
- 📁 [Repository](#-repository)
- 🔨 [Building and testing](#-building-and-testing)
- 🚧 [Current limitations](#-current-limitations)
- 📚 [Documentation](#-documentation)
- 🤝 [Contributing, security and licences](#-contributing-security-and-licences)

## 🚀 Why Qeetrix for Android

- 🎯 **One source of truth.** Tokens, fonts and icons are generated from the pinned npm packages by
  [`Tools/QeetrixGen`](Tools/QeetrixGen); `./gradlew check` fails if the generated Kotlin drifts from them. A colour
  is the same value in Compose, SwiftUI and CSS.
- 🖌️ **Drawn as the web draws it.** Each component follows its `@qeetrix/react` counterpart's CSS and the design rules —
  its sizes, colours, states and motion — and its page says where Android differs and why.
- 📱 **Native where it matters.** Some components exist for native apps only: the OTP code with its countdown, the QR
  scanner over the app's own camera, swipe actions, pull to refresh, the permission primer, the full-screen form, the
  bottom action bar, the page indicator, haptics by role. The date and time fields open Android's own pickers, in
  Qeetrix's colours.
- ♿ **Accessible by default.** TalkBack names, roles and states on every component, 48 dp touch targets, colour
  contrast checked in both schemes, text that scales to 200 % without being cut, reduced motion honoured.
- 🌍 **Every locale, both directions.** Numbers, money, dates, times, relative times and durations in the device's
  locale through ICU; right-to-left layouts mirror, and text takes its direction from its content.
- 🪶 **A small, stable surface.** No Material type in the public API, no network, no permissions, no image loading;
  the public API is pinned by a baseline check.

## 📍 Status

🚧 **In development, not yet published.** Release candidate `0.1.0-rc.2`, built on 31 October 2026, passed all ten
release gates, its device tests and the independent consumer app included
([report](Documentation/Releases/RELEASE-CANDIDATE-REPORT.md)). Since `0.1.0-rc.1`:

- 🔄 **Migrated** from `@qeetrix/ui` to `@qeetrix/tokens`, `@qeetrix/react` and `@qeetrix/icons`, and on to their 0.0.13,
  0.4.0 and 2.1.0 releases: every component re-specified to its `@qeetrix/react` counterpart, the API changed
  ([MIGRATION_QEETRIX_REACT.md](Documentation/MIGRATION_QEETRIX_REACT.md)).
- 🧱 **All 37 Android tickets built** (QTRX-101 to 107, 120 to 137, 156 and 190 to 200): the native components above,
  and changes to existing ones — icon-button tooltips, money and percentages in the number field, tab panels, the top
  app bar's count, status, tabs and collapsing title, right-to-left text, a bottom sheet without Material, a side sheet,
  Select's multiple choice, open state and type-to-jump, a searchable select, menu submenus and context menus, and
  snackbars for work in progress that stack on a tablet.
- ✅ **Verified** on the JVM (unit and Compose tests, quality scans, screenshots) and, on 31 October 2026, component by
  component on an Android 16 phone emulator in light, dark, 200 % text and right to left, then on a tablet emulator;
  what that found is fixed ([CHANGELOG.md](CHANGELOG.md)).

⏭️ **Next:** the owner's choice of registry and a first version, and a manual TalkBack pass on a phone. The phase history is in [PHASES.md](PHASES.md).

## 🧰 Requirements

| | |
|:--|:--|
| 🤖 Android | API 29 (Android 10) and newer; compiled against API 37 |
| 🟣 Kotlin / Compose | Built with Kotlin 2.4.21 and Compose 1.12.1 (BOM 2026.09.00) |
| 📲 An app using it | Compile SDK 37 and AGP 9.1 or newer (Compose 1.12 requires both); Kotlin 2.3 or newer ([compatibility matrix](Documentation/Releases/COMPATIBILITY-MATRIX.md)) |
| 🛠️ Building this repository | Android Gradle Plugin 9.4.1, Gradle 9.6.1 (wrapper), JDK 17 or newer (21 recommended) |
| 📚 Runtime dependencies | Compose runtime, UI, foundation and animation-core. Material 3, internally only, for the date and time pickers and pull to refresh — no Material type in the public API ([0015](Documentation/Decisions/0015-navigation-overlays-and-feedback.md)). No networking, database or image loading |

## 📦 Using Qeetrix in an app

### 📥 Installation

🏛️ **From a Maven repository** — how apps will use it once a version is published. Nothing is published yet: the
registry and the first version await the owner ([Maven distribution](Documentation/Releases/MAVEN-DISTRIBUTION.md)).
A release candidate can be built and published to a local directory with `Scripts/release-candidate 0.1.0-rc.2`;
`Integration/Consumer` is an independent app that uses it exactly this way.

```kotlin
// settings.gradle.kts of the app
dependencyResolutionManagement {
    repositories {
        exclusiveContent {
            forRepository { maven { url = uri("<the approved repository>") } }
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
    implementation("com.qeetgroup:qeetrix:<version>")
}
```

🔗 **From source**, to work on the library alongside an app: include this repository as a Gradle composite build, and
Gradle builds `:qeetrix` and substitutes it for the dependency.

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

The app needs the Android SDK and the Compose compiler plugin, as any Compose app does, and no keep rules for
Qeetrix. Consumers never need Node.js or the npm packages: the tokens are compiled Kotlin, and the fonts and icons
ship in the library.

### 🎨 Theme

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

`QeetrixTheme(QeetrixThemeMode.Dark)` forces a theme; nesting one draws part of a screen in the other
theme. Store a user's choice as `QeetrixThemeMode.value` (`light`, `dark`, `system`) — the same string
the web and iOS store. The density is set per region, never per component:
`CompositionLocalProvider(LocalQeetrixDensity provides QeetrixDensity.Compact) { … }`.

### 🧩 Components

```kotlin
val email = rememberTextFieldState()
QeetrixCard(title = "Sign-in email", description = "Where receipts are sent") {
    QeetrixTextField(email, label = "Work email", placeholder = "e.g. asha@acme.in")
    QeetrixSwitch(alerts, { alerts = it }, label = "Email alerts")
    QeetrixButton("Save changes", onClick = ::save, loading = saving)
}
```

A form is a `QeetrixForm`: the fields `space.form.gap` apart and, after a failed submit, a summary of what to fix.
The app owns the values and the validation; Qeetrix presents them:

```kotlin
QeetrixForm(errors = problems) {
    QeetrixTextField(email, label = "Work email", error = emailError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
    QeetrixPasswordInput(password, label = "Password")
    QeetrixFormActions { QeetrixButton("Sign in", onClick = ::signIn, size = QeetrixButtonSize.Lg, loading = signingIn) }
}
```

Navigation and overlays present what the app decides — its current destination, its `open` flags:

```kotlin
QeetrixNavigationScaffold(destinations, value = destination, onValueChange = { destination = it }) {
    Box(Modifier.fillMaxSize()) {
        InboxScreen(onArchive = { scope.launch { snackbars.show("Conversation archived", actionLabel = "Undo") } })
        QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
    }
}
```

Collections are Compose's lazy lists with the app's keys; Qeetrix draws the rows and every content state,
and the data, its loading and its identity stay the app's:

```kotlin
QeetrixDataState(loading = loading, empty = people.isEmpty(), emptyState = { QeetrixEmptyState("No members yet") }) {
    LazyColumn {
        items(people, key = { it.id }) { person -> QeetrixListItem(person.name, description = person.email, onClick = { open(person.id) }) }
    }
}
```

### 📱 Native components

Built for the native Qeet apps — Qeet Auth first — with no `@qeetrix/react` counterpart or a native form of one.
They ask for nothing themselves: the app keeps its permissions, its camera and its data.

```kotlin
QeetrixAlert("Push isn't set up", tone = QeetrixAlertTone.Warning,
    description = "Sign-in requests won't reach this phone until you allow notifications.")
QeetrixSegmentedControl(filter, { filter = it }, listOf(QeetrixOption("all", "All"), QeetrixOption("pending", "Pending")))
QeetrixOTPCode(code, expiresAt, label = "Acme · priya@acme.in")   // the code, its countdown ring, tap to copy
QeetrixDateField(date, { date = it }, label = "Invoice date", max = LocalDate.now())
```

Each component's page in [Documentation/Components](Documentation/Components/README.md) names its
`@qeetrix/react` counterpart and covers its API, look, accessibility and where Android differs; the KDoc is the API
reference. [Standards/FORMS.md](Documentation/Standards/FORMS.md) covers the field look, validation, focus, secure
input and AutoFill; [Standards/NAVIGATION_AND_OVERLAYS.md](Documentation/Standards/NAVIGATION_AND_OVERLAYS.md)
state, dismissal, Back and the snackbar lifecycle;
[Standards/DATA_DISPLAY_AND_COLLECTIONS.md](Documentation/Standards/DATA_DISPLAY_AND_COLLECTIONS.md)
identity, selection, search, filters and content states.

### 🔤 Typography

Eighteen roles, as `TextStyle`s: `display.lg` / `md` / `sm`, `heading.*`, `title.*`, `body.*`, `label.lg` / `md` /
`sm` / `caps` and `code.md` / `sm` (`QeetrixTextRole`, `QeetrixTheme.typography`). They use the web's fonts — Qeet
Sans (the file is Cal Sans 2.011) and Fira Code — in the TrueType form `@qeetrix/tokens` ships, with each role's
weight, letter style (`GEOM`: Geo for display, Base for headings and titles, A11y for the rest) and optical size,
which follows the size the text is drawn at. Sizes are in sp and follow the user's font size, Android 14's non-linear
scaling included. Components take them through `QeetrixText` and `QeetrixHeading`:

```kotlin
QeetrixText("Signed in on 3 devices", size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
BasicText("inv_2K9", style = QeetrixTheme.typography.codeSm)
```

### ✨ Icons

The 59 `@qeetrix/icons` icons the components and the sample use, in the sharp shape (the default, for controls) and
the round one (for navigation), outline and — for 39 of them — filled. They draw in the inherited content colour. An
icon whose meaning has a direction (send) mirrors in right to left by itself; an arrow or a chevron mirrors where the
call site says it points the way through the content (`mirrorInRightToLeft`, `iconMirrorsInRightToLeft`).

```kotlin
QeetrixIcon(QeetrixIconName.Search, contentDescription = null)                 // decorative
QeetrixIcon(QeetrixIconName.Lock, contentDescription = "Locked", size = QeetrixSize.IconLg)
```

### 🌐 Formatting

The web's `format` rules on Android's ICU, checked against cases the web's own rules write
([FORMAT.md](Documentation/Foundations/FORMAT.md)):

```kotlin
val india = Locale.forLanguageTag("en-IN")
QeetrixFormat.currency(124500, "INR", india)         // "₹1,24,500.00"
QeetrixFormat.relativeTime(sentAt, india)            // "2 minutes ago"
QeetrixFormat.duration(Duration.ofMinutes(65), india) // "1 hour, 5 minutes"
```

### 🧱 Everything else

`QeetrixSpace`, `QeetrixSize`, `QeetrixRadius` / `QeetrixShapes`, `QeetrixBorder`, `QeetrixFocus`, `QeetrixDensity`
(`sizeControlSm` / `Md` / `Lg`, `sizeRow`, `spaceFormGap`), `QeetrixShadow` with `Modifier.qeetrixShadow(...)`,
`QeetrixMotion` (durations, easings and springs) with `qeetrixAnimationSpec(...)` (honours Remove animations),
`QeetrixBreakpoint`, `QeetrixFontGeometry`, `QeetrixOpacity`, `QeetrixAccessibility` and
`Modifier.qeetrixMinimumTouchTarget()`, haptics by role (`rememberQeetrixHaptics()`, `QeetrixHapticRole`;
[HAPTICS.md](Documentation/Foundations/HAPTICS.md)), and `QeetrixProvenance` (the exact npm packages a build came
from). [TOKEN_COVERAGE.md](Documentation/Foundations/TOKEN_COVERAGE.md) maps every upstream token to its Kotlin name.
The API reference, with an overview of each package, is built by `./gradlew :qeetrix:dokkaGenerate` into
`qeetrix/build/dokka/html`.

## 🧩 Components at a glance

Grouped as the sample app and the [catalogue](Documentation/Components/README.md) group them; each has a page there.

| Group | Components |
|:--|:--|
| 👆 **Actions** | `QeetrixButton`, `QeetrixIconButton` (with its tooltip), `QeetrixCopyButton`, `QeetrixSegmentedControl`, `QeetrixBottomActionBar` |
| ✍️ **Forms and inputs** | `QeetrixTextField`, `QeetrixTextarea`, `QeetrixPasswordInput`, `QeetrixNumberField`, `QeetrixSearchField`, `QeetrixSelect` / `QeetrixMultiSelect`, `QeetrixSearchableSelect` / `QeetrixSearchableMultiSelect`, `QeetrixCheckbox` / `QeetrixCheckboxGroup`, `QeetrixRadioGroup`, `QeetrixSwitch`, `QeetrixSlider` / `QeetrixRangeSlider`, `QeetrixOTPInput`, `QeetrixOTPCode`, `QeetrixDateField` / `QeetrixDateRangeField` / `QeetrixTimeField`, `QeetrixAttachmentPicker` / `QeetrixFileRow`, `QeetrixQRScanner`, `QeetrixPermissionPrimer`, `QeetrixField` / `QeetrixFieldSet`, `QeetrixForm` |
| 📊 **Data display** | `QeetrixText` / `QeetrixHeading`, `QeetrixAvatar` / `QeetrixAvatarItem`, `QeetrixAvatarGroup`, `QeetrixBadge`, `QeetrixChip` / `QeetrixChipGroup`, `QeetrixListItem`, `QeetrixSectionHeader`, `QeetrixSwipeActions`, `QeetrixDescriptionList`, `QeetrixTimeline`, `QeetrixStat` / `QeetrixStatGroup`, `QeetrixCheckList`, `QeetrixRelativeTime`, `QeetrixQRCode`, `QeetrixCopyField` |
| 💬 **Feedback** | `QeetrixAlert`, `QeetrixProgress` / `QeetrixMeter`, `QeetrixProgressRing`, `QeetrixSpinner`, `QeetrixSkeleton`, `QeetrixSnackbar`, `QeetrixEmptyState`, `QeetrixDataState`, `QeetrixPullToRefresh` |
| 🪟 **Overlays** | `QeetrixDialog`, `QeetrixAlertDialog`, `QeetrixConfirmationDialog`, `QeetrixFullScreenForm`, `QeetrixBottomSheet`, `QeetrixSideSheet`, `QeetrixMenu` / `QeetrixContextMenu` |
| 🧭 **Navigation** | `QeetrixTopAppBar`, `QeetrixTabs` / `QeetrixTabPanel`, `QeetrixNavigationBar`, `QeetrixNavigationRail`, `QeetrixNavigationScaffold`, `QeetrixSteps`, `QeetrixPageIndicator` |
| 📐 **Layout** | `QeetrixCard`, `QeetrixAccordion`, `QeetrixSeparator` |

## ♿ Accessibility and localisation

- 🗣️ **TalkBack.** Every component carries its name, role and state; a control that groups parts is read once
  ("Verify, current step, 2 of 4"); actions hidden behind a gesture are accessibility actions too (swipe actions).
- 👉 **Touch.** At least 48 × 48 dp per target, checked by a scan of every component; what is smaller by design is
  listed with its own test of where a touch lands.
- 🌗 **Contrast.** Every colour pair a component draws is checked against WCAG in light and dark
  (`ComponentContrastTest`).
- 🔠 **Text size.** Up to 200 %, Android 14's curve included, without text being cut (`LayoutQualityTest`); layouts
  stack, wrap or go compact rather than truncate.
- 🎞️ **Motion.** Every animation honours Remove animations: spinners stand still, countdowns step once a second.
- 🌐 **Locales.** ICU formatting in the device's locale; digits typed in any script are read; strings are resources.
- ↔️ **Right to left.** Layouts mirror; text takes its direction from its content and its alignment from the layout;
  icons mirror by meaning ([LOCALIZATION-RTL-REPORT.md](Documentation/Quality/LOCALIZATION-RTL-REPORT.md)).

The standards are in [Standards/ACCESSIBILITY.md](Documentation/Standards/ACCESSIBILITY.md) and the findings in
[ACCESSIBILITY-REPORT.md](Documentation/Quality/ACCESSIBILITY-REPORT.md).

## 📲 The sample app

[`sample/`](sample) uses only the library's public API, as an app would. It has a page per component, in the
catalogue's groups, an account-preferences screen that combines the inputs, a workspace screen that combines
navigation, overlays and snackbars, and a collections screen that combines search, filters, sections, selection and
every content state. Its home screen's **Display** card switches the theme, the density, right to left and double
text size for every page.

```sh
./gradlew :sample:installDebug
adb shell am start -n com.qeetgroup.qeetrix.sample/.MainActivity --es route otp-code   # open one page directly
```

The route is a page's id in `SampleCatalog` (`qr-scanner`, `date-field`, `swipe-actions`, …). The QR scanner and
permission primer pages ask for the camera and for notifications for real, to show their states.

## 📁 Repository

```text
qeetrix/          the library (AAR)        — src/main/kotlin/com/qeetgroup/qeetrix/{theme,foundation,icons,components,tokens,internal}
sample/           a sample app using the library's public API, a page per component
Tools/QeetrixGen/ the generator (tokens, fonts, icons, new-component), an included build apps never resolve
Upstream/         the pinned npm inputs — @qeetrix/tokens' DTCG build, @qeetrix/react's manifest and CSS,
                  @qeetrix/icons' drawings — and their lock
Integration/      Consumer, an independent app that builds against a published copy of the library
Templates/        the component template new-component renders
Scripts/          sync-tokens, generate-tokens, verify-tokens, verify-aar, verify-publication, new-component,
                  release-candidate, dependency-inventory
Documentation/    components, foundations, decisions, standards, quality and release reports, the cross-platform
                  contract, the API baseline, notices, website handoffs
```

[ARCHITECTURE.md](ARCHITECTURE.md) explains how it fits together; [CONTRIBUTING.md](CONTRIBUTING.md)
how to work on it.

## 🔨 Building and testing

```sh
./gradlew check                       # generator tests + drift check, Lint, ktlint, unit and Compose UI tests, API check
./gradlew :qeetrix:verifyRoborazziDebug   # screenshots against your local, reviewed baselines
./gradlew :qeetrix:recordRoborazziDebug   # (re)record them — then review every image
./gradlew :qeetrix:apiDump            # update Documentation/API/qeetrix.api after an intended API change
./gradlew :qeetrix:assembleRelease    # the AAR → qeetrix/build/outputs/aar/
./gradlew :sample:installDebug        # the sample, on a connected device or emulator
./gradlew :qeetrix:connectedDebugAndroidTest   # device tests, accessibility checks included (needs a device or emulator; ATF runs on API 36)
./gradlew :qeetrix:dokkaGenerate      # API reference
Scripts/release-candidate 0.1.0-rc.2 --devices   # every release gate; publishes to build/release-candidate/maven only
```

Point Gradle at an Android SDK with platform 37 (`ANDROID_HOME` or `local.properties`). Start a new
component with `Scripts/new-component` ([Templates/Component](Templates/Component/README.md)).

🔍 `check` runs the quality scans with the tests: every component's specimens in the
[quality catalogue](qeetrix/src/sharedTest/kotlin/com/qeetgroup/qeetrix/quality/QualityCatalog.kt) are checked for
contrast, touch targets, and text cut at 200 % and in right to left. 🤖 [CI](.github/workflows/ci.yml) runs `check`,
builds the AAR, the sample and the API reference, verifies the AAR's contents, and builds `Integration/Consumer`
against a local publication.

## 🚧 Current limitations

- 📦 **Not published.** Use it as a composite build until a new release candidate, the registry and a version are
  approved.
- 🧩 **Not every `@qeetrix/react` family** has an Android counterpart: no data table, charts, popover or
  navigation drawer ([0015](Documentation/Decisions/0015-navigation-overlays-and-feedback.md),
  [0016](Documentation/Decisions/0016-data-display-and-content-states.md)).
- 🔗 **Upstream findings open:** the dark progress track (QTRX-213), disabled toggles (QTRX-214), the check item's
  alignment (QTRX-215) and a web duration formatter (QTRX-216); Android follows the web until they are settled.
- 🏷️ **No Qeet logos** yet.
- 🖼️ **Screenshot baselines are local.** They are not committed, so CI does not run visual regression
  ([0012](Documentation/Decisions/0012-visual-regression-testing.md)).
- 🧪 **Emulators, not people.** The new components were tried on an Android 16 phone emulator; the tablet pass, a
  physical device, and TalkBack, Switch Access, Voice Access and keyboard passes by a person have not been done
  ([RELEASE-READINESS-GAPS.md](Documentation/Quality/RELEASE-READINESS-GAPS.md)).

## 📚 Documentation

| Start here | |
|:--|:--|
| 🧩 [Components](Documentation/Components/README.md) | A page per component: use, look, accessibility, tokens, Android differences |
| 🎨 [Foundations](Documentation/Foundations/TOKEN_COVERAGE.md) | Every token's Kotlin name; [formatting](Documentation/Foundations/FORMAT.md); [haptics](Documentation/Foundations/HAPTICS.md) |
| 📏 [Standards](Documentation/Standards) | The component contract, public API rules, accessibility, forms, navigation and overlays, collections |
| 🧭 [Decisions](Documentation/Decisions) | The architecture decision records, from the build policy to the migration |
| 🌉 [Cross-platform contract](Documentation/CROSS_PLATFORM_CONTRACT.md) | What Android, iOS and the web share, and how |
| 🔄 [Migration](Documentation/MIGRATION_QEETRIX_REACT.md) | The move to `@qeetrix/react`, the gaps found and filed, what is open |
| 🔍 [Quality](Documentation/Quality) | Accessibility, localisation, performance, reliability, security and test coverage reports |
| 🚢 [Releases](Documentation/Releases) | Distribution, compatibility, release policy and checklist, rollback |
| 📝 [CHANGELOG.md](CHANGELOG.md) | What changed, release by release |

## 🤝 Contributing, security and licences

🤝 [CONTRIBUTING.md](CONTRIBUTING.md) covers setting up, the commands, what comes from upstream, code, components, tests,
accessibility and review; 🔒 [SECURITY.md](SECURITY.md) the library's scope, supported versions and how to report a
vulnerability.

📜 The library's own code is Qeet Group's (the repository is private and carries no open-source licence).
It bundles Qeet Sans (Cal Sans) and Fira Code (SIL OFL 1.1), from `@qeetrix/tokens`, and `@qeetrix/icons`
drawings (MIT, based on Lucide, ISC): see [Documentation/THIRD_PARTY_NOTICES.md](Documentation/THIRD_PARTY_NOTICES.md).

---

<div align="center">

Made with 🧡 by Qeet Group · [⬆️ Back to top](#-qeetrix-for-android)

</div>
