# 0002 — Gradle build and dependency policy

**Status:** Accepted, 2026-10-18

## Context

A design-system library is compiled into every Qeet Group app, so what it pulls in becomes theirs. The
Android toolchain moves fast; versions picked one at a time drift into incompatible combinations.

## Decision

- **Toolchain.** Gradle 9.6.1 (wrapper, distribution pinned by sha256), Android Gradle Plugin 9.4.1
  with its built-in Kotlin, Kotlin 2.4.21 (set through the Compose compiler plugin), JDK 17 bytecode,
  compileSdk 37, minSdk 29. These were chosen together: AGP 9.4 requires Gradle ≥ 9.6.0, JDK 17 and
  KGP ≥ 2.2.10, and supports API levels up to 37.
- **One catalogue.** Every version is in `gradle/libs.versions.toml`; the generator's build reads the
  same file.
- **Minimal runtime dependencies.** Compose runtime, UI, foundation and animation-core, through the
  Compose BOM 2026.09.00 (Compose 1.12.1). No Material, no AndroidX Core, no third-party runtime
  libraries. `api` only for modules whose types appear in the public API (Color, Dp, TextStyle, Shape,
  Easing, Modifier); `implementation` otherwise.
- **Locking.** The library's release compile and runtime classpaths are locked in strict mode
  (`qeetrix/gradle.lockfile`). Test and tooling classpaths are not; they never reach an app. Gradle
  dependency verification metadata is not used: for an Android build it is dominated by
  host-specific AGP artifacts and churns on every tool update.
- **Static analysis.** Android Lint with warnings as errors in both modules; ktlint 1.8.0
  (`ktlint_official`) through the ktlint Gradle plugin, in the library, the sample and the generator.
  Kotlin's explicit API mode in the library.
- **No publication.** Phase 1 configures no `maven-publish`; apps consume the library through a Gradle
  composite build (README). Publishing is its own decision — taken in
  [0018](0018-maven-distribution.md) (Phase 7).

## Consequences

- Updating a dependency is deliberate: change the catalogue, re-run `./gradlew check`, refresh the lock
  with `./gradlew :qeetrix:dependencies --write-locks`, and review the lockfile diff.
- Lint's "newer version available" checks are disabled; versions move by review, not by lint.
