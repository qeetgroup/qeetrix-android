# Changelog

All notable changes to Qeetrix for Android. The library has no released version yet.

## Unreleased — Phase 3 advanced inputs and forms

### Added

- **Field** (`components.input`): `QeetrixField` with `QeetrixFieldScope` and
  `Modifier.qeetrixFieldControl()` for any control; `QeetrixFieldSet` with `QeetrixFieldSetLegend`;
  `QeetrixOption<T>` for typed choices. Every input is built on the same internal field parts
  ([0014](Documentation/Decisions/0014-form-components.md)).
- **Inputs:** `QeetrixTextarea`; `QeetrixPasswordInput` with `QeetrixPasswordPurpose`; `QeetrixOTPInput`
  with `QeetrixOTPCharacters`; `QeetrixCheckbox` and `QeetrixTriStateCheckbox`; `QeetrixRadioGroup`;
  `QeetrixSelect` with `QeetrixSelectSize`; `QeetrixNumberField` for `Int?` and `BigDecimal?`.
- **Form patterns** (`components.form`): `QeetrixFormActions`, `QeetrixFormErrorSummary` and
  `QeetrixFormError`.
- **`contentType`** on `QeetrixTextField` and `QeetrixTextarea`, for AutoFill types the keyboard type
  does not imply (`Username`, `PersonFullName`, `PostalAddress`, …).
- **Icons:** chevron-down, eye-off, minus and plus (21 in all).
- **Strings:** `qx_required`, `qx_show_password`, `qx_hide_password`, `qx_one_time_code`, `qx_decrease`,
  `qx_increase` and `qx_error_summary_title`.
- **Sample:** a page per new component under Forms & inputs, and an account-preferences screen; sample
  tests for the error summary's focus and a two-step sign-in with a code.
- **Documentation:** component pages, `Documentation/Standards/FORMS.md`, decision record 0014, Phase 3
  rows in the accessibility standard and the cross-platform contract, and a website handoff
  (`Documentation/Website/PHASE_3_HANDOFF.md`). The documented examples are compiled and composed by
  the sample's `DocumentationExamplesTest`.

### Changed

- **`QeetrixTextField`** is rebuilt on the shared field parts. Its signature gains only the defaulted
  `contentType` parameter (source-compatible; the JVM signature in `qeetrix.api` changes). Behaviour
  changes: "required" is now read with the label; the field is exactly the control height (it was 1–2 dp
  taller); inside a disabled `QeetrixFieldSet` it is disabled. `QeetrixFieldMessage` and
  `QeetrixFieldMessageTone` moved to `QeetrixField.kt` in the same package (no API change).
- **`QeetrixSwitch`** follows a disabled `QeetrixFieldSet`, and its labelled row sits flush in one.
- **API dump** (`apiDump` / `apiCheck`) leaves out the Compose compiler's `ComposableSingletons` classes,
  which are public on the JVM but not API.
- **Generator:** upstream downloads retry a connection timeout up to three times and try a host's
  reachable addresses first, so one unreachable CDN address no longer fails a sync. The
  `new-component` tests scaffold Slider, now that Checkbox exists.

## Unreleased — Phase 2 core components

### Added

- **Components** (`com.qeetgroup.qeetrix.components.*`): `QeetrixText`, `QeetrixSeparator`, `QeetrixCard`,
  `QeetrixBadge`, `QeetrixButton`, `QeetrixIconButton`, `QeetrixSpinner`, `QeetrixProgress`, `QeetrixAvatar`,
  `QeetrixSwitch` and `QeetrixTextField` with `QeetrixFieldMessage`, each with option enums carrying the
  web's names ([0010](Documentation/Decisions/0010-component-api-conventions.md)).
- **Component tokens** for avatar, badge, button, card, input, progress, switch and typography,
  generated as internal objects (279 tokens generated, up from 217)
  ([0011](Documentation/Decisions/0011-component-tokens-and-web-metrics.md)).
- **Theme:** `QeetrixTheme.density` and `LocalQeetrixDensity`.
- **Icons:** circle-alert, circle-check, loader-circle, triangle-alert and user-round (17 in all).
- **Strings:** `qx_loading` ("Loading") and `qx_optional` ("(optional)"), translatable by overriding them.
- **Screenshot tests** with Roborazzi; baselines are local and never committed
  ([0012](Documentation/Decisions/0012-visual-regression-testing.md)).
- **Public API baseline** `Documentation/API/qeetrix.api` with `apiDump` / `apiCheck`
  ([0013](Documentation/Decisions/0013-public-api-baseline.md)).
- **`Scripts/new-component`** and `Templates/Component/`; `component-manifest.json` pinned from
  `@qeetrix/ui` 2.1.6.
- **Sample:** a catalogue grouped as on ui.qeet.in, a page per component with previews, a composition
  screen, app-wide theme, density, right-to-left and text-size settings, and JVM tests.
- **Documentation:** `Documentation/Components/`, decision records 0010–0013, and updated standards and
  cross-platform contract.

### Changed

- The sample's foundations screen moved to `sample/…/foundations/` and no longer carries the theme
  picker (the home screen's Display card does).
- `Scripts/verify-aar` expects the library's `qx_` strings.

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
