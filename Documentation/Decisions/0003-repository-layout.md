# 0003 — Repository layout

**Status:** Accepted, 2026-10-18

## Context

The layout follows the target structure set for Phase 1: Android's Gradle conventions for the code,
and the same separation qeetrix-ios uses (its record 0007) for tooling, pinned inputs and commands.

## Decision

- **Library.** `qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/` with one job per package:
  - `foundation/{color,typography,spacing,sizing,shape,elevation,motion,accessibility}` — the public
    foundation types;
  - `theme/` — `QeetrixTheme`, the theme mode and the composition locals;
  - `icons/` — the public icon API; `icons/generated/` — the drawings;
  - `tokens/` — provenance; `tokens/generated/` — the generated token data;
  - `internal/` — data types the generated files are written in.
  Tests mirror the packages in `src/test` (JVM, Robolectric) and `src/androidTest` (device).
- **Resources.** `res/font/` holds the two fonts, `qx_`-prefixed (`resourcePrefix = "qx_"`);
  `assets/qeetrix/licenses/` their licences and the icon licence.
- **Tooling and inputs.** `Tools/QeetrixGen/` (generator), `Upstream/` (pinned inputs, `manifest.json`
  and its lock), `Scripts/` (`sync-tokens`, `generate-tokens`, `verify-tokens`, `verify-aar`).
- **Sample.** `sample/`, an app depending on `:qeetrix`.
- **Documentation.** KDoc and `qeetrix/Module.md`, built by Dokka, for the API; `Documentation/` for
  decisions, standards, the cross-platform contract, notices and the generated foundation reports. The
  root keeps only README, ARCHITECTURE, CONTRIBUTING, CHANGELOG, PHASES and SECURITY.
- **Components** (Phase 2) go in `components/<category>/<component>/`, lowercase packages, organised by
  qeetrix-ui's component categories. Folders are created with their first component, never as
  placeholders; so is `Templates/`.

## Consequences

- Generated code lives only in `generated/` packages and `Documentation/Foundations/`, which the
  generator owns; `./gradlew check` fails if anything there was edited by hand or left over.
