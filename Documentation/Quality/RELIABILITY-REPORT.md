# Reliability report (Phase 6)

Compose state, restoration, lifecycle, concurrency, input and overlays: what was reviewed, what was tested, and
what was found.

## State ownership and restoration

Components hoist their state ([0010](../Decisions/0010-component-api-conventions.md)): selections, values, presented
flags, expansion, queries and load states are the app's, so they survive configuration changes and process death
exactly as the app saves them. What the library itself remembers was reviewed line by line:

| Remembered by Qeetrix | How | Survives recreation | Why |
|:--|:--|:--|:--|
| Password reveal flag | `rememberSaveable` | Yes | A revealed password stays revealed; the text is the app's `TextFieldState` |
| Number field's text being typed | `rememberTextFieldState` | Yes | — and since Phase 6 rewritten in the current locale if another locale wrote it (L1) |
| Select list open; menu chosen flag; dialog chosen flag; tab scroll-into-view flag; focus requesters | `remember` | No | Transient: an open list closes on rotation, as Android's own pop-ups do |
| Rule and marker positions (ruled stacks, timeline, description grid) | `remember` + snapshot state written by layout | Recomputed | Derived from layout |
| Snackbar host state | `remember` (`rememberQeetrixSnackbarHostState`) | No | By design: a snackbar never outlives its screen (0015) |

Tests: `StateRestorationTester` for expansion and chip selection (Phase 5), the number field restored into another
locale (Phase 6), and the sample's saved screen state (`CollectionsFlowTest`, `WorkspaceFlowTest`).

## Effects and coroutines

Every `LaunchedEffect`, `DisposableEffect` and `rememberCoroutineScope` in `qeetrix/src/main` was reviewed:

| Where | Effect | Cancellation and staleness |
|:--|:--|:--|
| `QeetrixMotion` | `DisposableEffect` registering a `ContentObserver` for Remove animations | Unregistered on dispose; no `Context` kept beyond composition |
| Bottom sheet | `LaunchedEffect(visible)` driving show and hide | Keyed on `visible`; `onDismissRequest` read through `rememberUpdatedState` |
| Menu, dialog, select | `LaunchedEffect(Unit)` requesting first focus when opened from a keyboard | One-shot per composition of the window |
| Tabs | `LaunchedEffect(selected)` bringing the selection into view | Skips the first composition; cancelled on a newer selection |
| OTP input | `snapshotFlow` of the code calling `onComplete` | `rememberUpdatedState` for the callback; keyed on state and length |
| Number field | Three effects: value sync, typing reader, commit on blur | `rememberUpdatedState` for value and callback; keyed on the formatter |
| Snackbar host state | `show()` suspends in the caller's scope | The caller's cancellation removes its snackbar; each `show()` returns exactly once (Phase 4 tests under virtual time) |

No `GlobalScope`, `runBlocking`, thread creation, `Handler` posting (other than the observer's main-looper
handler) or singleton holding a `Context` or `Activity`.

## Input

Keyboard and IME behaviour of the text inputs is Compose's `BasicTextField` and `BasicSecureTextField` with a
caller-owned `TextFieldState`, so recomposition never resets text or cursor. Covered by JVM tests: typing, paste of
a code into the OTP field, letters refused by the number field, the search key, the clear button keeping focus,
disabled and read-only states, AutoFill content types. Not covered on a device: an IME's composing region (East
Asian input), a hardware keyboard's focus traversal, AutoFill offers.

## Overlays and Back

`QeetrixOverlayBackTest` ran for the first time in Phase 6, on API 29 and 36: system Back closes a popup menu, and
with a dialog over a bottom sheet, Back closes the dialog first, then the sheet — each reported once. Its first run
found an ambiguous matcher in the test, not a defect in the components. The test sends Back as a key event; the
back gesture and predictive back's animation were not exercised, although the API 36 emulator uses gesture
navigation (API 29 uses buttons).

## Rapid and repeated interaction

Exactly-once behaviour is tested on the JVM: dialog actions and menu items run once however fast they are tapped
(Phase 4); a loading button ignores taps (Phase 2); a snackbar repeated restarts its time without a second
announcement (Phase 4); selecting one keyed row recomposes only that row (Phase 5).

## Consumer builds (R8)

See the minified sample result in [TEST-COVERAGE.md](TEST-COVERAGE.md#minified-consumer).

## Findings

| # | Severity | Finding | Fix |
|:--|:--|:--|:--|
| R1 | P2 | Number field reported a different value after a locale change or recreation (localization L1) | Fixed; two regression tests |
| R2 | P2 (test defect) | Back test never run; ambiguous matcher | Fixed; passes on API 29 and 36 |
| R3 | P2 (verification) | Device tests ran only on manual dispatch in CI | CI runs them on every push and pull request, API 29 and 36 |
