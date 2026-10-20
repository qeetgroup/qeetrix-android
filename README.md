# Qeetrix for Android

The Qeet Group design system for native Android apps, in Kotlin and Jetpack Compose.

Qeetrix is the one design language of every Qeet product. On the web it is
[`@qeetrix/ui`](https://github.com/qeetgroup/qeetrix-ui), documented at [ui.qeet.in](https://ui.qeet.in);
[qeetrix-ios](https://github.com/qeetgroup/qeetrix-ios) brings it to SwiftUI. This library brings the
same tokens, fonts and icons to Compose — generated from the published web release, so a colour, a size
or a type role is the same value on every platform.

**Status: Phase 7 — release candidate `0.1.0-rc.1` built and verified, not published.** The Phase 1 foundations (colours,
theming, typography, spacing, corners, sizing, elevation, motion, icons, accessibility helpers); Phase 2's
Button, Icon button, Text field, Switch, Avatar, Badge, Text, Progress, Spinner, Card and Separator;
Phase 3's Field and field set, Textarea, Password input, OTP input, Checkbox, Radio group, Select, Number
field and the form patterns; Phase 4's navigation bar, navigation rail and adaptive scaffold, top app bar,
tabs, alert, confirmation and custom dialogs, bottom sheet, menu and snackbar; and Phase 5's list item and
section header, description list, accordion, empty state, data state, skeleton, chips, timeline and search
field ([PHASES.md](PHASES.md), [Documentation/Components](Documentation/Components/README.md)). Phase 6
added no components: it verified these on the JVM and on API 29 and API 36 emulators and fixed what it found
([Documentation/Quality](Documentation/Quality/PHASE-6-AUDIT.md)). Phase 7 made it a Maven artifact, verified it
from an independent app, and prepared the release process; the result and what remains are in
[RELEASE-CANDIDATE-REPORT.md](Documentation/Releases/RELEASE-CANDIDATE-REPORT.md).

## Requirements

| | |
|:--|:--|
| Android | API 29 (Android 10) and newer; compiled against API 37 |
| Kotlin / Compose | Built with Kotlin 2.4.21 and Compose 1.12.1 (BOM 2026.09.00) |
| An app using it | Compile SDK 37 and AGP 9.1 or newer (Compose 1.12 requires both); Kotlin 2.3 or newer ([compatibility matrix](Documentation/Releases/COMPATIBILITY-MATRIX.md)) |
| Building this repository | Android Gradle Plugin 9.4.1, Gradle 9.6.1 (wrapper), JDK 17 or newer (21 recommended) |
| Runtime dependencies | Compose runtime, UI, foundation and animation-core; Material 3 for the bottom sheet only ([0015](Documentation/Decisions/0015-navigation-overlays-and-feedback.md)). No networking, database or image loading |

## Using Qeetrix in an app

**From a Maven repository** — how apps will use it once a version is published. Nothing is published yet: the
registry and the first version await the owner ([Maven distribution](Documentation/Releases/MAVEN-DISTRIBUTION.md)).
A release candidate can be built and published to a local directory with `Scripts/release-candidate 0.1.0-rc.1`;
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

**From source**, to work on the library alongside an app: include this repository as a Gradle composite build, and
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
Qeetrix. Consumers never need Node.js, Style Dictionary or a qeetrix-ui checkout: the tokens are compiled Kotlin.

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

### Components

```kotlin
val email = rememberTextFieldState()
QeetrixCard(title = "Sign-in email", description = "Where receipts are sent") {
    QeetrixTextField(email, label = "Work email", placeholder = "you@company.com")
    QeetrixSwitch(alerts, { alerts = it }, label = "Email alerts")
    QeetrixButton("Save changes", onClick = ::save, loading = saving, loadingLabel = "Saving…")
}
```

Forms are the inputs in a column; the app owns the values and the validation, Qeetrix presents them:

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.fieldGap)) {
    QeetrixTextField(email, label = "Work email", required = true, message = emailError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
    QeetrixPasswordInput(password, label = "Password", purpose = QeetrixPasswordPurpose.Current)
    QeetrixFormActions { QeetrixButton("Sign in", onClick = ::signIn, loading = signingIn) }
}
```

Navigation and overlays present what the app decides — its selected destination, its presented flags:

```kotlin
QeetrixNavigationScaffold(destinations, selected = destination, onSelect = { destination = it }) {
    Box(Modifier.fillMaxSize()) {
        InboxScreen(onArchive = { scope.launch { snackbars.show("Conversation archived", actionLabel = "Undo") } })
        QeetrixSnackbarHost(snackbars, Modifier.align(Alignment.BottomCenter))
    }
}
```

Collections are Compose's lazy lists with the app's keys; Qeetrix draws the rows and every content state,
and the data, its loading and its identity stay the app's:

```kotlin
QeetrixDataState(loading = loading, empty = people.isEmpty(), emptyContent = { QeetrixEmptyState("No members yet") }) {
    LazyColumn {
        items(people, key = { it.id }) { person -> QeetrixListItem(person.name, description = person.email, onClick = { open(person.id) }) }
    }
}
```

Each component's page in [Documentation/Components](Documentation/Components/README.md) covers its use,
states, accessibility and where it differs from the web and iOS; the KDoc is the API reference.
[Standards/FORMS.md](Documentation/Standards/FORMS.md) covers validation, focus, secure input and
AutoFill; [Standards/NAVIGATION_AND_OVERLAYS.md](Documentation/Standards/NAVIGATION_AND_OVERLAYS.md) state,
dismissal, Back and the snackbar lifecycle;
[Standards/DATA_DISPLAY_AND_COLLECTIONS.md](Documentation/Standards/DATA_DISPLAY_AND_COLLECTIONS.md)
identity, selection, search, filters and content states. The sample app has a page per component, grouped
as on ui.qeet.in, an account-preferences screen that combines the inputs, a workspace screen that combines
navigation, overlays and snackbars, and a collections screen that combines search, filters, sections,
selection and every content state.

### Typography

Nine roles, as `TextStyle`s: `display`, `heading`, `title`, `body`, `label`, `labelCompact`, `caption`,
`micro` and `code`. They use the web's fonts — Qeet Sans (Cal Sans v2.011) and Fira Code — bundled as
variable fonts with each role's weight, letter style and optical size. Sizes are in sp and follow the
user's font size.

```kotlin
BasicText("Signed in on 3 devices", style = QeetrixTheme.typography.caption)
```

### Icons

The 39 [qeetrix-icons](https://github.com/qeetgroup/qeetrix-icons) icons the components and apps use, in
round and sharp shapes, outline and filled (where upstream draws one). They draw in the inherited content colour and mirror in
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
qeetrix/          the library (AAR)        — src/main/kotlin/com/qeetgroup/qeetrix/{theme,foundation,icons,components,tokens,internal}
sample/           a sample app using the library's public API, a page per component
Tools/QeetrixGen/ the generator (tokens, fonts, icons, new-component), an included build apps never resolve
Upstream/         the pinned upstream inputs and their lock
Templates/        the component template new-component renders
Scripts/          sync-tokens, generate-tokens, verify-tokens, verify-aar, new-component
Documentation/    components, decisions, standards, the cross-platform contract, the API baseline, notices,
                  website handoffs
```

[ARCHITECTURE.md](ARCHITECTURE.md) explains how it fits together; [CONTRIBUTING.md](CONTRIBUTING.md)
how to work on it.

## Building and testing

```sh
./gradlew check                       # generator tests + drift check, Lint, ktlint, unit and Compose UI tests, API check
./gradlew :qeetrix:verifyRoborazziDebug   # screenshots against your local, reviewed baselines
./gradlew :qeetrix:recordRoborazziDebug   # (re)record them — then review every image
./gradlew :qeetrix:apiDump            # update Documentation/API/qeetrix.api after an intended API change
./gradlew :qeetrix:assembleRelease    # the AAR → qeetrix/build/outputs/aar/
./gradlew :sample:installDebug        # the sample, on a connected device or emulator
./gradlew :qeetrix:connectedDebugAndroidTest   # device tests, accessibility checks included (needs a device or emulator; ATF runs on API 36)
./gradlew :qeetrix:dokkaGenerate      # API reference
Scripts/release-candidate 0.1.0-rc.1 --devices   # every release gate; publishes to build/release-candidate/maven only
```

Point Gradle at an Android SDK with platform 37 (`ANDROID_HOME` or `local.properties`). Start a new
component with `Scripts/new-component` ([Templates/Component](Templates/Component/README.md)).

## Current limitations

- **Phase 5's components.** The remaining web components (tables, charts, date and time, …) come in later
  phases; no navigation drawer, popover or Qeetrix toast
  ([0015](Documentation/Decisions/0015-navigation-overlays-and-feedback.md)), and no data table, image
  presentation or pull-to-refresh wrapper ([0016](Documentation/Decisions/0016-data-display-and-content-states.md)),
  by decision.
- **Not published.** A release candidate is built and verified locally; publishing waits for the owner's choice of
  registry and approval of a version. Until then, use it as a composite build.
- **39 icons**, no Qeet logos yet.
- **Screenshot baselines are local.** They are not committed, so CI does not run visual regression
  ([0012](Documentation/Decisions/0012-visual-regression-testing.md)).
- **Emulators only, no manual accessibility pass.** The instrumented tests ran on API 29 and API 36
  emulators. TalkBack, Switch Access, Voice Access and keyboard passes, AutoFill offers, tablets and
  resizable windows, frame timing, Bold Text and Display P3 rendering on a physical device have not been
  done ([RELEASE-READINESS-GAPS.md](Documentation/Quality/RELEASE-READINESS-GAPS.md)).

## Licences

The library's own code is Qeet Group's (the repository is private and carries no open-source licence).
It bundles the Cal Sans and Fira Code fonts (SIL OFL 1.1) and qeetrix-icons drawings (MIT, based on
Lucide, ISC): see [Documentation/THIRD_PARTY_NOTICES.md](Documentation/THIRD_PARTY_NOTICES.md).
