# Changelog

All notable changes to Qeetrix for Android. The library has no released version yet.

## Unreleased — Phase 1 foundations

### Added

- **Library module `:qeetrix`** (`com.qeetgroup:qeetrix`, namespace `com.qeetgroup.qeetrix`): Kotlin,
  Jetpack Compose, minSdk 29, compileSdk 37; an AAR that ships two fonts, three licence texts and no
  manifest entries.
- **Token pipeline** in `Tools/QeetrixGen`: syncs `@qeetrix/ui` 2.1.6, the native fonts and 12 icons
  from pinned upstream releases, verifies them, and generates Kotlin deterministically. `./gradlew check`
  fails on any drift.
- **Theme.** `QeetrixTheme(mode)` with `Light`, `Dark` and `System`; nested themes;
  `QeetrixTheme.colors`, `.typography`, `.isDark`, `.reduceMotion`, `.shadowLayers()`;
  `LocalQeetrixContentColor`.
- **Colours.** `QeetrixColors`: the 84 semantic colours of `color.*`, light and dark, converted per CSS
  Color 4 (Display P3 for the seven tokens outside sRGB); `QeetrixOpacity`.
- **Typography.** `QeetrixTextRole` and `QeetrixTypography` (nine roles), `QeetrixFontWeight`,
  `QeetrixFontGeometry`; Qeet Sans (Cal Sans v2.011) and Fira Code 6.2 as variable font resources with
  per-role `wght`, `GEOM` and `opsz`.
- **Foundations.** `QeetrixSpacing`, `QeetrixCorner`, `QeetrixShapes`, `QeetrixStroke`, `QeetrixIconSize`,
  `QeetrixIconStroke`, `QeetrixDensity`, `QeetrixFocus`, `QeetrixElevation` with
  `Modifier.qeetrixElevation`, `QeetrixDuration`, `QeetrixEasing`, `QeetrixMotion` and
  `qeetrixAnimationSpec` (honours Remove animations).
- **Accessibility.** `QeetrixAccessibility.MinimumTouchTarget` (48 dp), `Modifier.qeetrixMinimumTouchTarget()`,
  `QeetrixAccessibility.contrastRatio`.
- **Icons.** `QeetrixIcon` and `QeetrixIconName` (12 icons), `QeetrixIconShape`, `QeetrixIconVariant`:
  vector paths in the content colour, stroke token at run time, RTL mirroring, decorative or labelled.
- **Provenance.** `QeetrixProvenance`: the upstream versions, commits and font hashes this build came from.
- **Sample app `:sample`** demonstrating the foundations through the public API.
- **Tests:** 33 generator tests and 37 JVM library tests (Robolectric, Compose UI Test); 2 instrumented
  tests (compiled, not yet run on a device).
- **Engineering:** Android Lint and ktlint, Dokka, dependency locking, CI workflow, governance files,
  decision records, the cross-platform contract and the accessibility, component and public-API standards.
