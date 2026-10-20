# Phases

Qeetrix for Android is built in phases, in step with qeetrix-ios. Each phase ends with its own
verification. Later phases are planning boundaries, not commitments of scope.

| Phase | Scope | Status |
|:--|:--|:--|
| 1 — Foundations | Token pipeline from `@qeetrix/ui`; semantic colours; light/dark/system theming; typography with the bundled variable fonts and font scaling; spacing, corners, sizing, stroke, focus, density, elevation, motion; icon infrastructure with a representative set; accessibility helpers; sample app; tests, CI, docs | Done (`0fad2aa`) |
| 2 — Core components | Text, Separator, Card, Badge, Button, Icon button, Spinner, Progress, Avatar, Switch, Text field, with their `component.*` tokens; screenshot testing; public API baseline; component template and `new-component`; sample catalogue | Done (`5b4cb5b`) |
| 3 — Advanced inputs and forms | Field and field set, Textarea, Password input, OTP input, Checkbox, Radio group, Select, Number field; form actions and error summary; the forms standard | Done (`5b4cb5b`) |
| 4 — Navigation, overlays, feedback | Navigation bar, rail and adaptive scaffold; top app bar; tabs; alert, confirmation and custom dialogs; bottom sheet; menu; snackbar; the navigation and overlays standard | Done, verified on the JVM; not yet committed |
| 5 — Data display, collections, content states | List item and section header; description list; accordion; empty state and data state; skeleton; chips; timeline; search field; the data display and collections standard | Done, verified on the JVM; not yet committed |
| 6 — Quality, accessibility and performance hardening | Audit of every component; automated accessibility checks on a device; touch, clipping, contrast and reduced-motion scans; localization and right to left; stability and allocation review; reliability, security and API reviews; device tests in CI; quality reports | Done, verified; not yet committed |
| **7 — Production readiness and release engineering** | Maven publication; independent consumer through the artifact; AAR verification; R8; API freeze; versioning, licensing, supply chain; release-candidate pipeline; release documentation | **Release candidate `0.1.0-rc.1` verified, not published — CONDITIONAL GO** |

## Phase 7 — done, verified (2026-10-20)

- **Distribution.** A Maven publication, `com.qeetgroup:qeetrix`, release variant with sources; versions from
  `-Pqeetrix.version`; a local repository only ([0018](Documentation/Decisions/0018-maven-distribution.md)).
- **Acceptance.** An independent app, `Integration/Consumer`, resolves the candidate by coordinates and builds debug
  and an R8-optimised release; 12 Compose tests and 4 release smoke tests pass on the API 29 phone, the API 36 phone
  and an API 36 tablet, and in landscape. The minimum toolchain (AGP 9.1.0, Kotlin 2.3.0) builds and runs.
- **Fixed.** The bottom sheet drew an empty panel at the top of the screen on devices (P1, found by the consumer).
- **Release engineering.** `Scripts/release-candidate` (10 gates, all passed for `0.1.0-rc.1`), publication and
  dependency checks, a CycloneDX SBOM, a CI publication-and-consumer step and a release-candidate workflow with no
  upload path. Release policy, checklist, rollback, licence review, compatibility matrix, release notes and the
  release-candidate report in [Documentation/Releases](Documentation/Releases/RELEASE-CANDIDATE-REPORT.md).
- **Decision: CONDITIONAL GO** — an internal pre-release on a private registry once the owner commits the work,
  chooses the registry and approves the version; not for production apps until a manual TalkBack, Switch Access
  and keyboard pass, a clean-commit re-run and green CI.

## Phase 7 — not done

- Publishing, tagging, committing: the owner's decisions; nothing was published, tagged, committed or pushed.
- A manual assistive-technology pass (TalkBack could not be driven from automation on the emulator).
- Qeet ID Auth integration: it is a React Native app.

## Phase 6 — done, verified (2026-10-20)

- **Audit.** Every Phase 1–5 component, against an evidence baseline, with a severity model; summary in
  [PHASE-6-AUDIT.md](Documentation/Quality/PHASE-6-AUDIT.md), one report per area in
  [Documentation/Quality](Documentation/Quality/), decision [0017](Documentation/Decisions/0017-quality-gates-and-verification.md).
- **Fixed.** The number field's value on a change of locale (P2); left-to-right labels cut or misordered in
  right-to-left layouts (P2); a 40 dp touch target on fields without a label (P2); a Back test that had never run
  (P2); the skeleton's per-frame gradient (P3); a real mail domain in a test (P3); CI actions on moving tags (P3).
  No P0 or P1 found. No public API, token or default changed.
- **Verification.** 309 library JVM tests (20 new), 15 sample and 44 generator tests, three clean runs; 147
  screenshot baselines (4 new; 22 right-to-left images re-recorded after review); device tests run for the first
  time — 12 of 12 on an API 36 emulator, 10 of 10 on API 29; lint, ktlint, API check, token drift and AAR checks
  clean; the minified sample exercised on a device; 94.2 % of hand-written lines covered by the JVM suite
  ([TEST-COVERAGE.md](Documentation/Quality/TEST-COVERAGE.md)).
- **Not claimed.** No performance figure, and no accessibility compliance: no manual TalkBack, Switch Access,
  Voice Access or keyboard pass was done.

## Phase 6 — not done, by scope

- Increased-contrast colour values: qeetrix-ui has none to follow yet.
- A Macrobenchmark module and Baseline Profile: no representative hardware ([0017](Documentation/Decisions/0017-quality-gates-and-verification.md)).
- Translations of the built-in strings.

## Phase 6 — still to verify

Tracked in [RELEASE-READINESS-GAPS.md](Documentation/Quality/RELEASE-READINESS-GAPS.md): manual TalkBack, Switch
Access, Voice Access and keyboard passes; physical devices; ATF on overlay windows and other API levels; frame
timing; CI's device matrix on its first run; IME and AutoFill; predictive back; large screens.

## Phase 5 — done, verified on the JVM (2026-10-20)

- **Components.** `QeetrixListItem` with `QeetrixListItemIcon`, `QeetrixSectionHeader`,
  `QeetrixDescriptionList` with `QeetrixDescriptionItem`, `QeetrixAccordion` with `QeetrixAccordionItem`,
  `QeetrixEmptyState`, `QeetrixDataState` with `QeetrixDataFailure` and `QeetrixDataLoadingRows`,
  `QeetrixSkeleton` and `QeetrixSkeletonText`, `QeetrixChip` and `QeetrixChipGroup`, `QeetrixTimeline` with
  `QeetrixTimelineItem`, and `QeetrixSearchField`
  ([0016](Documentation/Decisions/0016-data-display-and-content-states.md),
  [DATA_DISPLAY_AND_COLLECTIONS.md](Documentation/Standards/DATA_DISPLAY_AND_COLLECTIONS.md)). Each has KDoc
  with an example, a `Documentation/Components/` page and a sample page with previews; the sample adds a
  collections screen. A list section is the documented `stickyHeader` pattern, not a component.
- **Deferred by decision** (ADR 0016): a data table, image presentation and a pull-to-refresh wrapper.
- **Pins.** 39 icons (eight added); component tokens `accordion`, `skeleton` and `timeline` (305 generated).
  The generator reads millisecond component tokens as durations. No new dependencies.
- **Tests.** 289 library JVM tests (56 new: behaviour and semantics of every component, collection
  identity — keyed rows through insertion, removal and reordering, selection by id, state restoration,
  one-row recomposition — and 9 screenshot tests), 15 sample tests (the collections flow and every Phase 5
  documentation example) and 44 generator tests, all passing. `QeetrixCollectionDeviceTest` (1
  instrumented test: a 500-row keyed list scrolled far and back) compiles; it has not run (no device).
- **Screenshots.** 143 baselines (36 new, and the icon catalogue re-recorded for the new icons), reviewed in
  light, dark, 200 % text and right to left; still not committed.
- **Performance evidence.** Structural only: the Compose compiler's report shows all 20 new public
  composables restartable and skippable and `QeetrixDataFailure` stable; selecting one of twenty keyed rows
  recomposes that row only; the skeleton's shimmer redraws without recomposing. No timing was measured.
- **API.** `Documentation/API/qeetrix.api`: additions only.
- **Fixes found on the way.** Content-width paragraph text wrapped its last word in right-to-left layouts;
  Phase 5 text fills its column instead. The README's dependency row (stale since Phase 4) and its icons
  section's count (stale since Phase 2) are current again.
- **Engineering.** Lint (both modules) and ktlint with no issues; Dokka with overviews for the five new
  packages; AAR content check (twenty-one `qx_` strings).

## Phase 5 — not done, by scope

- A data table, charts, a feed, a tree view, a filter bar, pagination and stats; image presentation; a
  pull-to-refresh component; swipe actions on rows; a chip that is both selectable and removable.
- Phase 6 (accessibility and maturity on devices).

## Phase 5 — still to verify

- **TalkBack:** row reading order and long-press labels, accordion state and actions, chips as checkboxes
  and radio buttons, a removable chip's button, a data state's "Loading" and its announced failure, list
  navigation with sticky headers.
- **On a device:** `QeetrixCollectionDeviceTest` (ran in Phase 6, API 29 and 36); scrolling and item
  animations in long lists, and their frame timing (Macrobenchmark).
- **Window sizes:** the description list's 640 dp switch on tablets and resizable windows (tested on the
  JVM only).

## Phase 4 — done, verified on the JVM (2026-10-19)

- **Components.** `QeetrixNavigationBar`, `QeetrixNavigationRail` and `QeetrixNavigationScaffold` (with
  `QeetrixNavigationItem<T>` and `QeetrixNavigationLayout`), `QeetrixTopAppBar` and `QeetrixBackButton`,
  `QeetrixTabs`, `QeetrixAlertDialog` (with `QeetrixDialogAction`), `QeetrixConfirmationDialog`,
  `QeetrixDialog`, `QeetrixBottomSheet`, `QeetrixMenu` and its items, and `QeetrixSnackbar` with its host and
  host state ([0015](Documentation/Decisions/0015-navigation-overlays-and-feedback.md),
  [NAVIGATION_AND_OVERLAYS.md](Documentation/Standards/NAVIGATION_AND_OVERLAYS.md)). Each has KDoc, a
  `Documentation/Components/` page and a sample page; the sample adds a workspace screen and every page now
  has a top app bar.
- **Deferred by decision** (ADR 0015): a navigation drawer, a popover and a Qeetrix toast.
- **Pins and dependencies.** 31 icons (ten added); component tokens `dialog`, `menu` and `sidebar` (297
  generated). Compose foundation is now `api`; Material 3 is an `implementation` dependency for the bottom
  sheet alone, guarded by `MaterialBoundaryTest`.
- **Tests.** 233 library JVM tests (55 new: behaviour, semantics, dialog windows and Back on dialogs and
  sheets, keyboard focus, the snackbar lifecycle under virtual time, the Material boundary, 6 new screenshot
  tests), 9 sample tests (the workspace flow and every Phase 4 documentation example) and 44 generator
  tests, all passing. `QeetrixOverlayBackTest` (2 instrumented tests) compiles.
- **Screenshots.** 107 baselines (24 new, and the icon catalogue re-recorded for the new icons), reviewed in
  light, dark, 200 % text and right to left; still not committed.
- **API.** `Documentation/API/qeetrix.api`: additions only.
- **Fixes to earlier phases.** The select's highlighted option takes `surface.interactive` (the web's
  `bg-accent`) instead of a step darker; Escape closes the select list once (it acted on key-up too);
  foundation types in the public API now reach apps through an `api` dependency.
- **Engineering.** Lint (both modules) and ktlint with no issues; Dokka with overviews for the six new
  packages and no unresolved links; AAR content check (thirteen `qx_` strings).

## Phase 4 — not done, by scope

- A navigation drawer, a popover, a Qeetrix toast; side sheets; menu checkbox, radio and submenu items;
  snackbar swipe-to-dismiss; collapsing app bars.
- Phase 5 components (data display and the remaining web components).

## Phase 4 — still to verify

- **On a device:** `QeetrixOverlayBackTest` (Back on a popup menu and on stacked overlays; ran in Phase 6, API
  29 and 36); predictive back;
  gesture navigation and display cutouts with the edge-to-edge bars.
- **TalkBack:** window and pane titles announced, focus returning to the opening control after an overlay
  closes, the sheet handle's actions, snackbar announcements (once, errors at once), Switch Access on menus.
- **Window sizes:** tablets, foldables and resizable windows (the 600 dp switch is tested on the JVM only).

## Phase 3 — done, verified (2026-10-19)

- **Components.** `QeetrixField` and `QeetrixFieldSet`, `QeetrixTextarea`, `QeetrixPasswordInput`,
  `QeetrixOTPInput`, `QeetrixCheckbox` and `QeetrixTriStateCheckbox`, `QeetrixRadioGroup`, `QeetrixSelect`,
  `QeetrixNumberField`, `QeetrixFormActions` and `QeetrixFormErrorSummary`, with `QeetrixOption<T>` for typed
  choices. All are built on one set of internal field parts, which `QeetrixTextField` now uses too
  ([0014](Documentation/Decisions/0014-form-components.md),
  [FORMS.md](Documentation/Standards/FORMS.md)). Each has KDoc, a `Documentation/Components/` page and a
  sample page with previews; the sample adds an account-preferences screen.
- **Pins.** 21 icons (chevron-down, eye-off, minus, plus added), identical to qeetrix-ios's; no new
  component-token groups.
- **Tests.** 178 library JVM tests (85 new: behaviour and semantics per component, a password leak scan,
  locale and range rules for numbers, 9 new screenshot tests), 5 sample tests (the error summary's focus,
  a two-step sign-in with a code, and every documented example compiled and composed) and 44 generator
  tests, all passing.
- **Screenshots.** 83 baselines (36 new), reviewed image by image in light, dark, 200 % text and right to
  left, and verified locally; still not committed.
- **API.** `Documentation/API/qeetrix.api`: 108 lines added; one changed (`QeetrixTextField` gained the
  defaulted `contentType`).
- **Engineering.** Android Lint (both modules) with no issues; ktlint clean; Dokka with overviews for the
  seven new packages; AAR content check (nine `qx_` strings); decision record 0014; the generator's
  sync survives an unreachable CDN address.

## Phase 3 — not done, by scope

- Search, multi-select, comboboxes, autocomplete, date and time inputs, currency and phone formatting,
  input masks, sliders, file upload, Native Select, Input Group, Checkbox and Radio cards, and the
  password strength meter.
- A validation engine or form model (deliberately: [0014](Documentation/Decisions/0014-form-components.md)).
- SMS retrieval; credential storage.
- Migrating the tests to Compose 1.12's `junit4.v2.createComposeRule` (the current rule is deprecated
  but works).

## Phase 3 — still to verify

- **TalkBack on a device:** "required" with each label, password and reveal, the OTP read as one code,
  the select's pane and options, the number field's actions, group errors, and the error summary's
  announcement and focus ([ACCESSIBILITY.md](Documentation/Standards/ACCESSIBILITY.md)).
- **AutoFill** offers from a real provider: saved and new passwords, SMS one-time codes, names and
  addresses.
- **Hardware keyboards and D-pads:** focus order, the select's keys, the number field's arrows.
- **IME behaviour** on real keyboards: number pads without a minus key, decimal separators per locale.

## Phase 2 — done, verified (2026-10-18)

- **Components.** The eleven, each with its option enums, KDoc, a `Documentation/Components/` page and a
  sample page with previews; built on foundation primitives (no Material), the theme, eight adopted
  component-token groups and `QeetrixWebScale`.
- **Tokens.** 279 generated, 604 excluded; component tokens 62 of 62 identical to qeetrix-ios's working
  tree. Pins as qeetrix-ios's Phase 2: 17 icons and `component-manifest.json`.
- **Tests.** 44 generator tests (including 9 for `new-component`), 93 library JVM tests (37 from Phase 1;
  44 component behaviour and semantics tests; 12 screenshot tests) and 2 sample tests (every page in
  three display settings; the composition form end to end), all passing.
- **Screenshots.** Roborazzi; 47 baselines (four configurations per component, three for icons),
  reviewed image by image and verified locally. They are not committed, so CI does not verify them
  ([0012](Documentation/Decisions/0012-visual-regression-testing.md)).
- **API.** `Documentation/API/qeetrix.api`, checked by `./gradlew check`.
- **Tooling.** `Scripts/new-component` and `Templates/Component/`, trialled in a temporary copy of the
  repository: a run, a refused second run, byte-identical output from two copies, ktlint-clean output, a
  build that fails with the scaffold's message until implemented.
- **Engineering.** Android Lint (both modules) and ktlint with no findings; Dokka with package overviews
  for the ten component packages; AAR content check; decision records 0010–0013.

## Phase 2 — not done, by scope

- Components beyond the eleven, and the 33 component-token groups they would adopt.
- The Button's `invalid` styling; announcing "required" on a text field (see below).
- Publication of the library.

## Phase 2 — still to verify

- **TalkBack on a device** for every component: the button's "Loading" state, the text field's error and
  live-region announcements, the labelled switch row, a clickable card's name, the avatar's
  description; keyboard and D-pad focus with the focus ring. No emulator or device was available.
- **"Required" text fields.** Not announced in Phase 2; since Phase 3 "required" is read with the label,
  still to be heard with TalkBack ([TextField](Documentation/Components/TextField.md)).
- **Visual regression in CI.** Needs a decision on where baselines may live.
- **The CI workflow** on GitHub-hosted runners (the organisation's Actions billing blocks runs).

## Phase 1 — done, verified

- **Pipeline.** Sync and lock from upstream (npm integrity, font identity, pinned icon commit),
  deterministic generation, and drift verification in `./gradlew check`.
- **Generated tokens.** 217 tokens: 84 semantic colours, 14 elevations, 9 typography roles and the
  dimension, density and motion scales; 666 upstream tokens excluded with written reasons. Identical
  to qeetrix-ios's values for the same release.
- **Fonts.** Qeet Sans and Fira Code, the web's exact fonts as variable TTF resources, with the web's
  per-role axes.
- **Icons.** 12 icons and their 44 drawings (round, sharp, outline and filled where upstream has them).
- **Tests.** 33 generator tests and 37 library tests on the JVM, all passing.
- **Sample.** Light, dark and system themes, colours, typography, spacing and corners, elevation, icons
  (including RTL), font scale, and a two-column layout from 600 dp.
- **Engineering.** Android Lint and ktlint with zero findings, Dokka API reference, AAR content check,
  locked release classpaths, CI workflow, governance files and decision records 0001–0009.

## Phase 1 — not done, by scope

- Components and the 216 component tokens; screenshot tests; the component template.
- The full icon catalogue and the Qeet logos.
- Gradients and z-index tokens.
- Increased-contrast values (upstream defines none).
- Publication of the library.

## Phase 1 — still to verify

- The instrumented tests (`qeetrix/src/androidTest`) compile but have not run: no emulator or device was
  available. Run `./gradlew :qeetrix:connectedDebugAndroidTest`.
- TalkBack, Bold Text, Indic glyph fallback and Display P3 on a real device.
- The CI workflow on GitHub-hosted runners.
- A public API baseline check — done in Phase 2 ([0013](Documentation/Decisions/0013-public-api-baseline.md)).
