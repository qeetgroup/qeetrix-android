# Consumer integration

Phase 7's acceptance test: an Android app that is not part of this build uses Qeetrix the way a product team would —
through Maven coordinates, from a repository — and is built, optimised and run on devices. `:sample` does not count:
it depends on `project(":qeetrix")`.

## The consumer

`Integration/Consumer` is a separate Gradle build (its own `settings.gradle.kts`, never included in
qeetrix-android's) with two modules:

- **`:app`** — a four-page app (foundations, a form, overlays, a searchable list) with switches for the dark theme,
  doubled text and right to left. It depends on `com.qeetgroup:qeetrix:<version>` and `activity-compose`, and on
  nothing else of Compose: what Qeetrix's public API needs must arrive through its published metadata.
- **`:smoke`** — a `com.android.test` module that drives the app's **release** build (R8 full mode and resource
  shrinking, no Qeetrix keep rules) from outside with UI Automator, as Macrobenchmark does.

Its isolation: Qeetrix resolves only from the candidate repository (`exclusiveContent` for `com.qeetgroup`); there
is no project dependency, composite build, copied AAR, source path, generator or sample code; Kotlin's `internal`
keeps it to the public API. It uses the library's own `R` class only to check that fonts and strings resolve.

```sh
Scripts/release-candidate 0.1.0-rc.1 --devices                       # publishes, then builds and runs the consumer
./gradlew -p Integration/Consumer :app:connectedDebugAndroidTest :smoke:connectedAndroidTest   # after publishing
```

## Results (candidate `0.1.0-rc.1`, 2026-10-20)

In the final release-candidate run (`Scripts/release-candidate 0.1.0-rc.1 --devices`):

| Check | API 29 phone | API 36 phone | API 36 tablet | API 36 phone, landscape (separate run) |
|:--|:--|:--|:--|:--|
| Resolves `com.qeetgroup:qeetrix:0.1.0-rc.1` through its module metadata (`releaseVariantReleaseRuntimePublication`) | ✓ | ✓ | ✓ | ✓ |
| Debug build; 12 Compose tests through the public API (`ConsumerAppTest`) | 12 / 12 | 12 / 12 | 12 / 12 | 12 / 12 |
| R8-optimised release build: no missing-rule report; both fonts and the strings the app's components use survive shrinking | ✓ | ✓ | ✓ | ✓ |
| 4 UI Automator tests on the release build (`ReleaseSmokeTest`) | 4 / 4 | 4 / 4 | 4 / 4 | 4 / 4 |

The minimum toolchain (AGP 9.1.0, Kotlin 2.3.0) was also built and run on API 36
([COMPATIBILITY-MATRIX.md](COMPATIBILITY-MATRIX.md)).

## Scenarios

| # | Scenario | How it is exercised |
|:--|:--|:--|
| 1 | Resolve the Maven dependency | `dependencyInsight`: the runtime variant from the candidate repository, Compose's API artifacts transitively |
| 2 | Compile against the public API | The app compiles with only Qeetrix's public declarations and Compose's layout |
| 3 | `QeetrixTheme` | Wraps the whole app |
| 4 | Semantic colours | Swatches of `QeetrixTheme.colors`; the dark-theme test measures the screen darkening |
| 5 | Typography | `QeetrixTheme.typography` body and code; the test resolves both font resources from the artifact; the release capture shows Qeet Sans and Fira Code |
| 6 | Icons | Three `QeetrixIcon`s found by their descriptions, in debug and release |
| 7 | `QeetrixButton` | Pressed; the count changes |
| 8 | `QeetrixTextField` | Typed into |
| 9 | An advanced form | Error summary and field message on an empty submit; email, password, one-time code, a select (its popup list), a radio group, number steppers, a checkbox; a success snackbar |
| 10 | A dialog | Alert dialog runs its action; a confirmation closes on Back |
| 11 | A bottom sheet | Opens, its action closes it; Back closes it in release |
| 12 | Navigation | Adaptive scaffold (a bar on a phone), top app bar, tabs, a menu |
| 13 | Snackbar | Shown, its action runs |
| 14 | A collection | A keyed `LazyColumn` of list items under section headers; selection |
| 15 | Search | Search field and chip-group filter; an empty state when nothing matches |
| 16 | Light and dark | Switched at run time; measured on screen |
| 17 | Larger text | Text doubled through the composition's density; the text grows 1.5× or more |
| 18 | Right to left | Layout direction switched; content moves to the right |
| 19 | Optimised release build | R8 full mode, resource shrinking, the default optimising rules only |
| 20 | Interactions on an emulator | All of the above on API 29 and API 36 |

## Found by the consumer, and fixed

**P1 — the bottom sheet drew a second, empty surface over the top of the screen.** Its elevation and top rule were
passed to Material's sheet as a modifier, which Material applies before moving the sheet into place, so a sheet-sized
white panel covered the top of every screen while the real sheet sat at the bottom (API 29 and API 36, release
captures). In-place screenshots on the JVM could not show it. The rule is now drawn inside the sheet and no shadow
is drawn; `QeetrixBottomSheetDeviceTest` reproduces the defect (the area above the sheet measured pure white, 765 of
765) and passes after the fix.

Test-harness problems were fixed in the consumer's own tests, not in Qeetrix: a check that did not wait for the
accessibility tree after launch; a tap on a node the layout had just moved (UI Automator's stale-object case); Back
sent before a dialog's window had input focus; and checks that assumed a tall portrait screen, which failed in
landscape until they scrolled to what they check.

## Not covered

- **Qeet ID Auth.** The intended first consumer is an **Expo SDK 57 / React Native 0.86** app (`qeet-id/qeet-id-auth`,
  inspected read-only), not a Compose app. It cannot use a Compose library as it stands: it would need a native
  Android module hosting Compose views (Expo's modules API supports this) or a native rewrite. Real integration with
  it is unverified.
- System-level right to left and font scale: the consumer switches them inside its composition, which popups and
  dialogs (separate windows) do not inherit. Those windows' right-to-left rendering is covered by the library's own
  tests and screenshots.
- Physical devices, tablets and foldables.
