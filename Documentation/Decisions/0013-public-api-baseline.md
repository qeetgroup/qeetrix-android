# 0013 — Public API baseline

**Status:** Accepted, 2026-10-18 (Phase 2)

## Context

Components make the public API large enough that an unintended change — a renamed parameter, a removed
default, a type made public by accident — can slip through review. Explicit API mode
(`kotlin { explicitApi() }`) forces every declaration's visibility to be written, but nothing compared
the surface with the last accepted one. Phase 1 tried Kotlin 2.4's built-in `abiValidation`: with AGP 9's
built-in Kotlin it produced an empty dump. The Kotlin binary-compatibility-validator Gradle plugin
registers no tasks for an AGP 9 built-in-Kotlin library either.

## Decision

Use the **binary-compatibility-validator 0.18.2 library** directly, which the plugin is built on:

- `./gradlew :qeetrix:apiDump` reads the release AAR's `classes.jar`, keeps the public, non-synthetic
  API by its Kotlin metadata (so `internal` declarations are excluded), and writes
  `Documentation/API/qeetrix.api`.
- `./gradlew :qeetrix:apiCheck` compares the same dump with that file and fails with the instruction to
  run `apiDump` if they differ. `check` depends on it, so CI runs it.

## Consequences

- An intended API change updates `qeetrix.api` in the same pull request, where the reviewer sees the
  surface change as a diff, with a CHANGELOG entry ([0010](0010-component-api-conventions.md)).
- The dump shows JVM signatures, including the parameters the Compose compiler adds (`Composer`, change
  and default masks). They change only when a composable's parameters do.
- When the Kotlin Gradle plugin's ABI validation works with AGP 9 built-in Kotlin, this can move to it
  with the same baseline file format.
