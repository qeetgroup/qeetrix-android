# 0012 — Visual regression testing

**Status:** Accepted, 2026-10-18 (Phase 2). Supersedes the screenshot paragraph of
[0009](0009-testing-and-documentation-strategy.md).

## Context

Components need visual regression tests across a representative matrix. 0009 named Google's Compose
Preview Screenshot Testing as the first candidate, to be evaluated against this toolchain (AGP 9.4.1 with
built-in Kotlin, Kotlin 2.4.21, Compose 1.12, compileSdk 37). Only one framework may be adopted.

## Decision

**Roborazzi 1.76.0**, on Robolectric 4.17 with native graphics (Android 16, API 36), as ordinary JVM unit
tests.

- **Google's Compose Preview Screenshot Testing** was still an experimental alpha plugin, documented as
  such, and its APIs may change; rejected for now, to be re-evaluated when it is stable.
- **Paparazzi** had no stable release supporting this toolchain; rejected.
- Roborazzi is stable, supports AGP 9, renders real text and shadows through Robolectric's native
  graphics, and needs no emulator.

The matrix is representative, not a Cartesian product. Each component has one test that lays out its
variants and the states that change its drawing (disabled, loading, error, selected, checked), captured
in four configurations: **light**, **dark**, **200% font scale** and **right to left**, on a fixed
en-US, 411 × 2400 dp, xhdpi device, with animations off so spinners and indeterminate progress draw
their still, reduced-motion frame. Press and hover need a pointer and are covered by behaviour tests.

**Baselines are not committed.** At the maintainer's request (2026-10-18), `**/src/test/screenshots/` is
git-ignored: images stay on the machine that recorded and reviewed them.

- Record with `./gradlew :qeetrix:recordRoborazziDebug`, then review every changed image by eye before
  relying on it. Nothing approves an image automatically.
- Verify with `./gradlew :qeetrix:verifyRoborazziDebug`: any difference, or a missing baseline, fails.
- In an ordinary test run (`check`, CI) the screenshot tests render nothing and compare nothing.

## Consequences

- **CI does not run visual regression.** A fresh checkout has no baselines, so verification is a local
  gate, run before a pull request that changes how a component draws. Committing baselines (directly or
  with Git LFS), or storing them as CI artifacts, would let CI verify; that needs the maintainer's
  decision.
- Baselines are machine-specific in practice: Robolectric's native rendering can differ slightly between
  host operating systems, so each machine records and reviews its own.
- Device rendering is not proven by these images; instrumented tests and manual checks cover it
  ([ACCESSIBILITY.md](../Standards/ACCESSIBILITY.md)).
