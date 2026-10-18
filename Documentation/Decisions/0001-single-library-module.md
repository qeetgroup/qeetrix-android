# 0001 — One library module

**Status:** Accepted, 2026-10-18

## Context

Qeetrix will grow to the full component catalogue. Google's modularisation guidance recommends module
boundaries where they pay for their build complexity, and Now in Android keeps its design system in one
module. Splitting by foundation or by component would multiply Gradle configuration, publication units
and API-compatibility surfaces without a consumer asking for any of them.

## Decision

- **`:qeetrix`** — the one reusable Android library (AAR). Foundations, theme, icons and, from Phase 2,
  components are packages inside it, not modules.
- **`:sample`** — an application that depends on `:qeetrix` through its public API only. It is never
  published.
- **`Tools/QeetrixGen`** — the generator, an included Gradle build that apps never resolve.
- No `core`, `common`, `domain` or per-component modules. Convention plugins wait until repeated
  configuration across several real modules justifies them.

## Consequences

- An app adds one dependency and gets one coherent API; Kotlin `internal` hides everything else.
- A future split — a heavy optional component such as charts, or a tokens-only artifact a consumer
  needs — is its own decision record with an API-compatibility review. Moving packages between modules
  changes artifact coordinates, so it is not a simple directory move.
