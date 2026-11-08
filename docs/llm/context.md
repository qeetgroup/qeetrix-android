# Repository Context — qeetrix-android

**Level:** L2 · **Status:** active · **Last verified:** 2026-11-04
**Verification scope:** versions, paths and release state read from the build files, workflows and GitHub.

## Identity

`com.qeetgroup:qeetrix` — Qeet Group's design system for Jetpack Compose: the Qeetrix tokens, light and dark themes,
fonts, icons and 66 components, as native Android. Kotlin 2.4.21, Compose BOM 2026.09.00, AGP 9.5.0, Gradle 9.8.2,
minimum SDK 29, compile SDK 37. MIT-licensed. Published to **GitHub Packages**.

**Status:** `0.1.0` released on 4 November 2026, to GitHub Packages (tag `v0.1.0`).

## Context inheritance

```text
qeet-context (L0)  →  Qeetrix design system  →  @qeetrix/tokens · @qeetrix/react · @qeetrix/icons (the source)
                                              →  qeetrix-ios (the SwiftUI sibling)
                                              →  qeetrix-android (L2 — this document)
```

## What makes this library different

> **Nothing visual is decided here.** Tokens, fonts and icons are generated from the pinned npm packages, and every
> component restates its `@qeetrix/react` counterpart's CSS. A look changes upstream first, then arrives by a pin bump.

- **Generated, never hand-written:** `Upstream/` and every `generated/` package. `./gradlew check` fails on drift.
- **No Material types in the public API.** Compose foundation only; Material 3 is internal, for the date and time
  pickers and pull to refresh.
- **The app owns state:** values, selections, `open` flags, navigation and data come in as parameters.
- **A stable API:** explicit API mode, no public data classes, a binary baseline in `qeetrix/api/qeetrix.api`.

## Key numbers

66 components · 109 colour roles · 18 text roles · 1,863 icons (100 core, 1,763 in qeetrix-icons-extended) · 94.0 % line coverage (CI minimum 92 %) · 263 screenshots.

## Where to read next

- [architecture-map.md](architecture-map.md): where everything lives
- [boundaries.md](boundaries.md): what this repo owns, and the rules
- [workflows.md](workflows.md): how to do common tasks
- [../quality.md](../quality.md): what is checked, and the known gaps
