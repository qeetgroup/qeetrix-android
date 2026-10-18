# 0009 — Testing and documentation strategy

**Status:** Accepted, 2026-10-18

## Context

The library's correctness is mostly about values — that a token reaches Kotlin unchanged — and the rest
about rendering and accessibility behaviour. Emulators are slow and not always available; JVM tests can
cover most of it but cannot prove device rendering.

## Decision

- **Generator tests** (`Tools/QeetrixGen/src/test`, JUnit): CSS parsing and alias resolution, colour
  conversion against independent vectors (culori, the web's converter, and the brand values qeetrix-ui
  records), scope rules, SVG conversion, font inspection, determinism and committed-output parity.
- **Library unit tests** (`qeetrix/src/test`, JUnit + Robolectric + Compose UI Test, on the JVM): token
  parity with the generator's value table, theme selection and nesting, typography values and font
  axes, font scaling with real text layout, icon semantics, the WCAG pairs qeetrix-ui gates, motion and
  elevation rendering (Robolectric native graphics).
- **Instrumented tests** (`qeetrix/src/androidTest`): what only a device proves — that the platform
  applies the font axes and that themed pixels reach the screen. Run manually or by the CI's
  `workflow_dispatch` job; never reported as passing unless they ran.
- **Drift.** `./gradlew check` regenerates everything in memory and fails on any difference, and checks
  every pinned file against its locked sha256.
- **Documentation.** KDoc on every public declaration (explicit API mode enforces visibility), Dokka for
  the API reference with package overviews in `qeetrix/Module.md`, and Markdown in `Documentation/` for
  decisions, standards and the cross-platform contract. ui.qeet.in stays the organisation-wide portal.
- **Screenshot tests** wait for Phase 2 components. The official Compose Preview Screenshot Testing is
  the first candidate, evaluated then for stability with this toolchain.

## Consequences

- No test asserts a constant against itself: values are checked against an independent source (the
  generator's table, culori, qeetrix-ui's records or real rendering).
- Accessibility with TalkBack, Switch Access and keyboards, and Bold Text, still need device checks
  (`Documentation/Standards/ACCESSIBILITY.md`).
