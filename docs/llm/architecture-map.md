# Architecture Map — qeetrix-android

**Level:** L2 · **Last verified:** 2026-11-04
**Verification scope:** every path below was confirmed to exist.

| Need | Path |
|---|---|
| **The library** | `qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/` |
| **Public API baseline** | `qeetrix/api/qeetrix.api` |
| Build, publication, coverage | `qeetrix/build.gradle.kts` |
| Locked release dependencies | `qeetrix/gradle.lockfile` |
| Versions of every tool and library | `gradle/libs.versions.toml` |
| **The version to release** | `gradle.properties` (`qeetrix.version`) |
| The sample app | `sample/` |

## Inside the library

| Package | Holds |
|---|---|
| `foundation/*` | Public types: colour, typography, spacing, sizing, shape, elevation, motion, layout, accessibility, format, haptics |
| `theme/` | `QeetrixTheme` and its composition locals |
| `icons/` | `QeetrixIcon`, `QeetrixIconName`; drawings in `icons/generated/` |
| `components/<family>/` | One file per component (`QeetrixButton`, …) and its option enums |
| `tokens/generated/` | Generated token values, internal |
| `internal/` | Shared helpers: popups, focus frame, motion, system bars |

## Tokens, fonts and icons

| Concern | Path |
|---|---|
| **Pinned packages** | `Upstream/manifest.json` (versions) · `Upstream/manifest.lock.json` (hashes) |
| Synced inputs | `Upstream/qeetrix-tokens/`, `Upstream/qeetrix-react/`, `Upstream/qeetrix-icons/` |
| The generator | `Tools/QeetrixGen/` (a separate Gradle build) |
| Generated reports, read by tests | `docs/tokens.md`, `docs/token-values.json` |

## Tests

| Kind | Path |
|---|---|
| JVM tests (Robolectric, Compose UI Test, screenshots) | `qeetrix/src/test/` |
| The quality catalogue the scans cover | `qeetrix/src/sharedTest/kotlin/com/qeetgroup/qeetrix/quality/QualityCatalog.kt` |
| Device tests | `qeetrix/src/androidTest/` |
| Screenshot baselines (local, never committed) | `qeetrix/src/test/screenshots/` |

## Scripts and workflows

| Need | Path |
|---|---|
| Sync, generate, verify tokens | `Scripts/sync-tokens`, `Scripts/generate-tokens`, `Scripts/verify-tokens` |
| Every release gate | `Scripts/release-candidate` |
| Check the AAR and the publication | `Scripts/verify-aar`, `Scripts/verify-publication` |
| CI | `.github/workflows/ci.yml` (build and test; device tests toward `main`) |
| Release | `.github/workflows/version.yml`, `release.yml`, `rollback.yml` |
| Code scanning, updates | `.github/workflows/codeql.yml`, `.github/dependabot.yml` |
