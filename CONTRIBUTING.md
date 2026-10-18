# Contributing

## Setting up

- **JDK** 17 or newer (21 recommended) to run Gradle.
- **Android SDK** with `platforms;android-37.0` and `build-tools;36.0.0`. Set `ANDROID_HOME`, or put
  `sdk.dir=…` in `local.properties` (not committed). Android Studio installs both.
- **Network**, only for `Scripts/sync-tokens` and the first dependency download.

Open the repository root in Android Studio, or use the command line:

```sh
./gradlew check                # what CI runs first; must pass before a pull request
```

## Commands

| Command | What it does |
|:--|:--|
| `./gradlew check` | Generator tests and drift verification; Android Lint, ktlint and unit tests of the library and sample |
| `./gradlew ktlintFormat` | Fix formatting (ktlint, `ktlint_official` style, `.editorconfig`) |
| `./gradlew :qeetrix:assembleRelease` | Build the AAR |
| `Scripts/verify-aar qeetrix/build/outputs/aar/qeetrix-release.aar` | Check the AAR ships only what it should |
| `./gradlew :sample:installDebug` | Install the sample |
| `./gradlew :qeetrix:connectedDebugAndroidTest` | Device tests (needs a device or emulator) |
| `./gradlew :qeetrix:dokkaGenerate` | API reference into `qeetrix/build/dokka/html` |
| `Scripts/sync-tokens [--check]` | Fetch and verify the pinned upstream artifacts (network) |
| `Scripts/generate-tokens` | Regenerate everything derived from the snapshot |
| `Scripts/verify-tokens` | Only the drift verification |

## Tokens, fonts and icons come from upstream

Nothing visual is decided in this repository. To change a colour, size, font or icon:

1. Change it in [qeetrix-ui](https://github.com/qeetgroup/qeetrix-ui) or
   [qeetrix-icons](https://github.com/qeetgroup/qeetrix-icons) and release it.
2. Bump the pin in `Upstream/manifest.json` (and tell the qeetrix-ios maintainers, so both move together).
3. `Scripts/sync-tokens`, then `Scripts/generate-tokens`.
4. If the build now fails to compile, upstream added, removed or renamed a token: update the public type in
   `foundation/` or `theme/` deliberately, and note it in `CHANGELOG.md`.
5. If generation fails on a new token group, decide whether Android consumes it and add a rule to
   `Tools/QeetrixGen/.../tokens/Scope.kt` (mirroring qeetrix-ios).
6. Review the diff of `Upstream/`, the generated files and `Documentation/Foundations/`.

To add an icon, add its name to `qeetrixIcons.icons` in `Upstream/manifest.json`, sync, generate, and add
the entry to `QeetrixIconName` (the build fails until you do).

Never edit `Upstream/`, a `generated/` package or `Documentation/Foundations/` by hand: `./gradlew check`
fails if you do.

When the generator itself changes, regenerate `Tools/QeetrixGen/src/test/resources/culori-vectors.json`
only if the pinned release changed (see the script header in `Tools/QeetrixGen/vectors/`).

## Code

- Kotlin official style, enforced by ktlint; Android Lint with warnings as errors.
- Explicit API mode: every public declaration states its visibility; keep the public surface small
  ([Documentation/Standards/PUBLIC_API.md](Documentation/Standards/PUBLIC_API.md)).
- No literal colours, sizes, durations or fonts outside the generated code; use the foundation types.
- No Material 3 for appearance, no product-specific code (Qeet ID or any other), no new runtime
  dependencies without a decision record.
- Resource names start with `qx_` (enforced by `resourcePrefix`).

## Tests

- New behaviour comes with a test that would fail without it. Check values against an independent source
  (the generator's table, upstream records, real rendering) — never a constant against itself.
- Prefer JVM tests (Robolectric + Compose UI Test) in `qeetrix/src/test`; use
  `@GraphicsMode(GraphicsMode.Mode.NATIVE)` when real text layout or pixels matter.
- What only a device proves goes in `qeetrix/src/androidTest`. Say in the pull request whether you ran
  it; never report it as passing otherwise.

## Accessibility

Every change keeps the guarantees in
[Documentation/Standards/ACCESSIBILITY.md](Documentation/Standards/ACCESSIBILITY.md): contrast pairs, font
scaling, 48 dp touch targets, semantics, RTL and reduced motion. The contrast test runs in `./gradlew
check`; TalkBack and Bold Text need a device.

## Review

- One topic per pull request, using the template's checklist. CI must pass.
- Changes to the public API, the generator's scope rules, or a dependency need a maintainer's review and,
  for anything structural, a decision record in `Documentation/Decisions/`.
- Cross-platform changes (naming, conversion, scope) are mirrored in qeetrix-ios or recorded in
  [Documentation/CROSS_PLATFORM_CONTRACT.md](Documentation/CROSS_PLATFORM_CONTRACT.md) as a difference.
