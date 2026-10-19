# Phases

Qeetrix for Android is built in phases, in step with qeetrix-ios. Each phase ends with its own
verification. Later phases are planning boundaries, not commitments of scope.

| Phase | Scope | Status |
|:--|:--|:--|
| 1 — Foundations | Token pipeline from `@qeetrix/ui`; semantic colours; light/dark/system theming; typography with the bundled variable fonts and font scaling; spacing, corners, sizing, stroke, focus, density, elevation, motion; icon infrastructure with a representative set; accessibility helpers; sample app; tests, CI, docs | Done (`0fad2aa`) |
| 2 — Core components | Text, Separator, Card, Badge, Button, Icon button, Spinner, Progress, Avatar, Switch, Text field, with their `component.*` tokens; screenshot testing; public API baseline; component template and `new-component`; sample catalogue | Done |
| **3 — Advanced inputs and forms** | Field and field set, Textarea, Password input, OTP input, Checkbox, Radio group, Select, Number field; form actions and error summary; the forms standard | **Current** |
| 4 — Navigation, overlays, feedback | Navigation patterns, sheets, menus, toasts, dialogs | Planned |
| 5 — Data display and advanced | Lists, tables, charts and the other data components | Planned |
| 6 — Accessibility and maturity | Device-verified TalkBack, Switch Access and keyboard; increased-contrast values (with upstream); performance | Planned |
| 7 — Documentation and release | Published API reference, docs on ui.qeet.in, versioned releases | Planned |

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
