# Phases

Qeetrix for Android is built in phases, in step with qeetrix-ios. Each phase ends with its own
verification. Later phases are planning boundaries, not commitments of scope.

| Phase | Scope | Status |
|:--|:--|:--|
| **1 — Foundations** | Token pipeline from `@qeetrix/ui`; semantic colours; light/dark/system theming; typography with the bundled variable fonts and font scaling; spacing, corners, sizing, stroke, focus, density, elevation, motion; icon infrastructure with a representative set; accessibility helpers; sample app; tests, CI, docs | **Current** |
| 2 — Core components | Actions and basics (button, icon button, link, badge, separator, …) with their `component.*` tokens; screenshot testing; component template | Planned |
| 3 — Forms and inputs | Text fields, selection controls, validation presentation | Planned |
| 4 — Navigation, overlays, feedback | Navigation patterns, sheets, menus, toasts, dialogs | Planned |
| 5 — Data display and advanced | Lists, tables, charts and the other data components | Planned |
| 6 — Accessibility and maturity | Device-verified TalkBack, Switch Access and keyboard; increased-contrast values (with upstream); performance | Planned |
| 7 — Documentation and release | Published API reference, docs on ui.qeet.in, versioned releases | Planned |

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
- A public API baseline check (see `Documentation/Standards/PUBLIC_API.md`).
