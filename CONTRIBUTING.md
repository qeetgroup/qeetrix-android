# 🤝 Contributing

Thanks for helping build Qeetrix for Android. This page covers setting up, the commands, and the three things you
are most likely to do: change something, change how something looks, and add a component.

## 🛠️ Setting up

- **JDK** 17 or newer (21 recommended).
- **Android SDK** with `platforms;android-37.0` and `build-tools;36.0.0`. Android Studio installs both. Set
  `ANDROID_HOME`, or put `sdk.dir=…` in `local.properties` (not committed).
- **Network**, only for the first dependency download and for `Scripts/sync-tokens`.

Open the repository root in Android Studio, or check that everything works from the command line:

```sh
./gradlew check
```

## 🔁 Making a change

```mermaid
flowchart LR
    A["🌿 Branch from develop"] --> B["✍️ Change<br/>with a test"]
    B --> C["💻 The local gate"]
    C -- "draws differently?" --> D["📸 Verify or record<br/>the screenshots"]
    C --> E["📬 Pull request into develop<br/>with the template"]
    D --> E
    E --> F["🤖 Verify passes"]
    F --> G["👀 Review"]
    G --> H["✅ Merge"]
```

- **Pull requests go into `develop`.** Only `develop` goes into `main`, through a `develop` → `main` pull request the
  maintainer opens; `main` refuses anything else.
- **One topic per pull request.** Fill in the template's checklist.
- **A maintainer reviews** changes to the public API, the generator's scope rules or a dependency. Anything
  structural also needs a [decision record](docs/decisions.md).
- **Mirror cross-platform changes** (naming, conversion, scope) in qeetrix-ios, or record the difference in the
  [cross-platform contract](docs/llm/boundaries.md).

## 💻 Before you push

CI runs only Verify: the Gradle wrapper, the generated files, the Kotlin compile, ktlint and Android lint. The tests,
coverage, the public API check, the release builds and the icon budget run on your machine before every push. Run them
all, and say so in the pull request:

```sh
./gradlew check :qeetrix:koverVerifyDebug \
  && ./gradlew :qeetrix:assembleRelease :qeetrix-icons-extended:assembleRelease :sample:assembleRelease \
  && Scripts/verify-aar qeetrix/build/outputs/aar/qeetrix-release.aar \
  && ./gradlew :icon-budget:assembleNoneRelease :icon-budget:assembleTwentyRelease && Scripts/icon-budget
```

A change to popups, overlays, accessibility or number formats also runs the device tests on API 29 and 36
(`./gradlew :qeetrix:connectedDebugAndroidTest`, below); before a release, `Scripts/release-candidate` runs every
release gate.

## ⌨️ Commands

**Every day**

| Command | What it does |
|:--|:--|
| `./gradlew check` | Lint, ktlint, every JVM test, the generator's tests and drift check, the public API check |
| `./gradlew ktlintFormat` | Fix formatting |
| `./gradlew :sample:installDebug` | Install the sample app on a device or emulator |
| `./gradlew :qeetrix:assembleRelease` | Build the AAR |

**Screenshots and API**

| Command | What it does |
|:--|:--|
| `./gradlew :qeetrix:verifyRoborazziDebug` | Compare every screenshot with your local baselines |
| `./gradlew :qeetrix:recordRoborazziDebug` | Record the baselines. Review every image before relying on it. |
| `./gradlew :qeetrix:apiDump` / `apiCheck` | Update or check `qeetrix/api/qeetrix.api` |
| `./gradlew :qeetrix:dokkaGenerate` | Build the API reference into `qeetrix/build/dokka/html` |
| `./gradlew :qeetrix:koverHtmlReportDebug` | A test-coverage report in `qeetrix/build/reports/kover/htmlDebug`. The local gate fails below 92 % of lines (`koverVerifyDebug`). |

**Devices and releases**

| Command | What it does |
|:--|:--|
| `./gradlew :qeetrix:connectedDebugAndroidTest` | Device tests on every connected device or emulator, including the Accessibility Test Framework from API 36 |
| `Scripts/verify-aar qeetrix/build/outputs/aar/qeetrix-release.aar` | Check the AAR ships only what it should |
| `Scripts/release-candidate <X.Y.Z-rc.N> [--devices]` | Every release gate and a local publication ([checklist](docs/releases.md)) |

**Upstream and components**

| Command | What it does |
|:--|:--|
| `Scripts/sync-tokens [--check]` | Fetch and verify the pinned npm packages (network) |
| `Scripts/generate-tokens` | Regenerate everything made from `Upstream/` |
| `Scripts/verify-tokens` | Only the drift check |

## 🎨 Changing how something looks

A colour, size, font, icon or component look is never changed here. It changes on the web first, then reaches
Android through a pin bump:

```mermaid
flowchart TD
    A["1️⃣ Change it upstream<br/>qeetrix-tokens · qeetrix-icons · qeetrix-react"] --> B["2️⃣ Publish to npm"]
    B --> C["3️⃣ Bump the pin in Upstream/manifest.json<br/>and tell the qeetrix-ios maintainers"]
    C --> D["4️⃣ Scripts/sync-tokens<br/>then Scripts/generate-tokens"]
    D --> E{"Does it build?"}
    E -- "no: a token was added, removed or renamed" --> F["Update the public type on purpose<br/>and note it in CHANGELOG.md"]
    E -- "yes" --> G["5️⃣ Review the diff"]
    F --> G
    G --> H["CSS changed? Update that component's<br/>values, page and screenshots"]
```

- **Never edit `Upstream/`, a `generated/` package or `docs` by hand.** `./gradlew check` fails
  if you do.
- **A new token group** fails generation until you add a rule for it in
  [Scope.kt](Tools/QeetrixGen/src/main/kotlin/com/qeetgroup/qeetrix/gen/tokens/Scope.kt), mirroring qeetrix-ios.
- **A new component family** gets its CSS by adding the family to `qeetrixReact.families` in `Upstream/manifest.json`.
- **Icons:** every `@qeetrix/icons` icon is already generated — the core ones into `QeetrixIconName`, the rest into
  `qeetrix-icons-extended`. To move one into the core set (a component or the sample draws it), add its name to
  `qeetrixIcons.icons`, then sync and generate ([docs/icons.md](docs/icons.md)).

## 🧩 Adding a component

```mermaid
flowchart LR
    A["Copy a similar component<br/>source, tests, sample page"] --> B["Implement it<br/>from the @qeetrix/react CSS"]
    B --> C["./gradlew :qeetrix:apiDump"]
    C --> D["Record and review<br/>the screenshots"]
    D --> E["📬 Pull request"]
```

Every component has its tests, a screenshot test, a specimen in the quality catalogue, a sample page and a row in
[docs/components.md](docs/components.md). Follow the [component rules](docs/llm/boundaries.md): the web component's
props, variants and states, 48 dp touch targets, and full TalkBack support.

## 🚀 Releasing

Like every Qeetrix package, **merging to `main` releases the version in `gradle.properties`**:

```mermaid
flowchart LR
    A["⬆️ Raise the version<br/>on develop"] --> B["📬 Pull request<br/>develop → main"]
    B --> C["✅ Merge"]
    C --> D["release.yml<br/>new version?"]
    D --> E{"👤 Approve"}
    E --> F["📦 GitHub Packages<br/>🏷️ tag and release"]
```

- **The version is in the pull request's diff.** When the library changes, raise `qeetrix.version` on develop (the next
  patch, or a minor or major) before the develop → main pull request; its Release gate fails until it's raised. Docs,
  tests and CI changes need no new version, and merging them publishes nothing.
- **The CHANGELOG entry** (`## [X.Y.Z] — YYYY-MM-DD`) becomes the GitHub Release's notes.
- **A bad release** is withdrawn with **Actions › Rollback**, and the fix is a new version.

The details are in [MAVEN-DISTRIBUTION.md](docs/releases.md#-how-a-release-works).

## ✍️ Code

- **Kotlin official style**, enforced by ktlint. Android Lint treats warnings as errors.
- **Explicit API mode**, with KDoc on everything public. Keep the public surface small
  ([PUBLIC_API.md](docs/llm/boundaries.md)).
- **No literal colours, durations or fonts.** Use the foundation types. A size the tokens don't name comes from the
  component's CSS, with the CSS beside it.
- **No Material for appearance, no product-specific code, and no new runtime dependency** without a decision record.
- **Resource names start with `qx_`.**

## 🧪 Tests

- **New behaviour comes with a test that would fail without it.** Check values against an independent source, never a
  constant against itself.
- **Prefer JVM tests** (Robolectric and Compose UI Test) in `qeetrix/src/test`. Put what only a device can prove in
  `qeetrix/src/androidTest`, and say in the pull request whether you ran it.
- **Add a new component to the [quality catalogue](qeetrix/src/sharedTest/kotlin/com/qeetgroup/qeetrix/quality/QualityCatalog.kt)**,
  so the touch, clipping, contrast and accessibility scans cover it.
- **Screenshots stay on your machine.** They aren't committed and CI doesn't compare them. If your change alters how a
  component draws, run `verifyRoborazziDebug`. If the difference is intended, record, review each image, and say so in
  the pull request.

## ♿ Accessibility

Every change keeps the guarantees in [ACCESSIBILITY.md](docs/llm/boundaries.md): contrast, font
scaling, 48 dp touch targets, TalkBack, right to left and reduced motion. `./gradlew check` scans most of these
automatically. TalkBack and Bold Text still need a person with a device.

## 📜 Licence

Qeetrix for Android is [MIT](LICENSE)-licensed, and contributions are made under the same licence.
