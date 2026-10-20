# Test coverage (Phase 6)

Every test suite, where it runs, what it proves, and its result on 2026-10-20. Machine: Apple M1 Pro, macOS
26.6.2, JDK 21; emulators `qeetrix-api29` (Android 10) and `qeetrix-api36` (Android 16), arm64, Google APIs.

## Suites

| Suite | Command | Runs on | Tests | Result |
|:--|:--|:--|--:|:--|
| Library, JVM | `./gradlew :qeetrix:testDebugUnitTest` | Robolectric 4.17 (Android 16 runtime) | 309 (20 new in Phase 6) | All pass |
| Screenshots | `./gradlew :qeetrix:verifyRoborazziDebug` | Robolectric native graphics | 147 baselines (4 new) | All match the reviewed baselines |
| Sample, JVM | `./gradlew :sample:testDebugUnitTest` | Robolectric | 15 | All pass |
| Generator and scaffolding | `./gradlew :qeetrix-gen:test` | JVM | 44 | All pass |
| Device | `./gradlew :qeetrix:connectedDebugAndroidTest` | API 36 emulator | 12 (7 new) | All pass, ATF included |
| Device | the same | API 29 emulator | 10 (the two ATF tests are left out below API 36) | All pass |
| Static | `./gradlew check`, `Scripts/verify-tokens`, `Scripts/verify-aar`, `:qeetrix:dokkaGenerate` | — | — | Lint: no issues in either module; ktlint clean; API check unchanged; tokens current; AAR content as expected; Dokka builds |
| Device (Phase 7) | `./gradlew :qeetrix:connectedDebugAndroidTest` | API 29 phone, API 36 phone, API 36 tablet | 11, 13, 13 (`QeetrixBottomSheetDeviceTest` added) | All pass, in the release-candidate run |
| Independent consumer (Phase 7) | `./gradlew -p Integration/Consumer :app:connectedDebugAndroidTest :smoke:connectedAndroidTest` | The same three devices, and the API 36 phone in landscape | 12 Compose tests (debug), 4 UI Automator tests (R8 release) | All pass ([CONSUMER-INTEGRATION.md](../Releases/CONSUMER-INTEGRATION.md)) |

## By kind

| Kind | Where | What it covers |
|:--|:--|:--|
| Unit | JVM | Token generation and drift (`Tools/QeetrixGen`), number reading and writing (`NumberTextTest`), colour conversion, contrast arithmetic |
| Compose UI (behaviour and semantics) | JVM, Robolectric | Every component: roles, names, states, actions, focus, keyboard, live regions, state restoration, overlays' windows and Back on dialogs |
| Screenshot | JVM, Robolectric native graphics | Every component in light, dark, 200 % and right to left; localized specimens. Local baselines, reviewed image by image ([0012](../Decisions/0012-visual-regression-testing.md)) |
| Accessibility (automated) | JVM scans; ATF on API 36 | [ACCESSIBILITY-REPORT.md](ACCESSIBILITY-REPORT.md) |
| Instrumented | API 29 and 36 emulators | Font axes and dark canvas on a real surface, ATF, ICU number formats, text clipping, Back on popups and stacked overlays, a long keyed list |
| Integration | JVM, the sample | `SampleTest`, `WorkspaceFlowTest`, `CollectionsFlowTest`: whole screens through the public API; four documentation-example tests compile and compose every documented snippet |
| Consumer build | R8 | The sample's minified release, built and run (below) |

## Line coverage

Measured once, for the library's JVM suite, with AGP's JaCoCo unit-test coverage turned on from a scratch init script
(the build files are unchanged; coverage is not a gate). Robolectric loads classes in a sandbox JaCoCo's agent skips
by default, which first gave a meaningless 3.6 %; with `includeNoLocationClasses = true`:

| Scope | Lines | Branches |
|:--|--:|--:|
| Hand-written code (generated tokens and icon paths excluded) | **94.2 %** (4,908 of 5,212) | 73.1 % (2,279 of 3,117) |
| Everything, generated code included | 97.4 % (11,664 of 11,979) | 75.9 % (2,672 of 3,520) |

The branch figure counts the Compose compiler's generated `$changed` and default-argument branches, so it
understates the branches a person wrote. Lowest packages by lines: `foundation.elevation` 78 %, `components.feed`
85 %, `internal` 86 %, `components.tabs` 86 %, `components.card` 87 %. The device tests are not in these figures.

## Flakiness

| Suite | Runs | Result |
|:--|:--|:--|
| Library JVM suite (309 tests) | 3 complete runs of the final code: the gate, and two coverage runs (one rebuilt from scratch with `--rerun-tasks`) | 0 failures in any run |
| Screenshot comparison (147 baselines) | The gate | All match |
| `QeetrixOverlayBackTest` (device) | 10 class runs on each emulator after the fix, with a build loading the machine | 20 of 20 |

One flaky test was found and fixed: `backClosesTheTopmostOverlayFirst` failed once in a full API 36 run (8 of 8 passed
alone). Back goes to the focused window, and the test sent the second Back before focus had moved from the closed
dialog back to the sheet. It now waits for the window to have focus, as a second press by a person would. No test
was disabled, retried or given a longer timeout to pass.

## Minified consumer

The sample's release build is minified and resource-shrunk by R8 with only Android's default optimising rules: the
library ships no consumer rules and needed none. On the API 36 emulator the release APK installed, launched cold to its
home page, and took 1,500 random touch, gesture and D-pad events from `monkey` (seed 20261020; which pages they reached
was not recorded) with no crash, no ANR and no R8 linkage error (`ClassNotFound`, `NoSuchMethod`, `NoSuchField`, `AbstractMethodError`,
`VerifyError`) in the log; the same process lived throughout. Lint's release checks (`lintVitalRelease`) passed for both
modules. A random-input run finds crashes, not wrong behaviour: the behaviour tests run on the debug build.

## Not covered

- Manual assistive-technology passes and physical devices ([RELEASE-READINESS-GAPS.md](RELEASE-READINESS-GAPS.md)).
- Screenshot comparison in CI: baselines are local by policy, so CI cannot compare images.
- Device tests in CI: configured, not yet run there (G6).
