# Phase 6 audit: quality, accessibility and performance hardening

The summary of Phase 6 (2026-10-20): what was audited, what was found, what was fixed, and the evidence. Each area
has its own report, listed at the end. Phase 6 added no components and changed no public API, token or default.

## Baseline at the start of Phase 6

What the evidence was when Phase 6 began, at the end of Phase 5:

| Area | Evidence | Gap |
|:--|:--|:--|
| Behaviour and semantics | 289 library JVM tests, 15 sample tests, 44 generator tests | — |
| Screenshots | 143 local baselines in light, dark, 200 % and right to left | — |
| Device tests | 5 instrumented tests, compiled | **Never run**: no emulator or device |
| Automated accessibility checks | None | ATF never run |
| Touch areas, clipping, contrast as drawn | Per-component assertions; designed colour pairs (Phase 1) | No scan over every component; no check of colours as components draw them |
| Localization | English; right-to-left screenshots | No other scripts, no expansion, no locale changes, no device number formats |
| Performance | Compiler report spot checks; one recomposition test | — |
| CI | JVM, lint, API check; device tests on manual dispatch only | Actions on moving tags; the sample's release not minified |

## Method

Every check had to prove it could fail, on the platform that runs it
([0017](../Decisions/0017-quality-gates-and-verification.md)). One quality catalog of every in-place component in
its risky states, plus localized specimens, feeds both the JVM scans and the device checks. Two emulators were
installed for this phase: API 29 (the library's minimum) and API 36 (with TalkBack, Switch Access and Voice Access installed), both arm64.

Two checks that looked like passes were not: the Accessibility Test Framework does not run under Robolectric or on
API 29 (a deliberately bad 24 dp button passed there), and `hasVisualOverflow` flags any single-line text
narrower than its parent. Both were replaced by checks shown to fail on a known problem.

## Severity model

**P0** blocks any use (crash, data loss, security exposure). **P1** blocks a release (a component unusable with
an assistive technology, a wrong value shown or reported). **P2** must be fixed or explicitly accepted before a
release. **P3** is an improvement or hygiene.

## Findings

| # | Severity | Area | Finding | Status |
|:--|:--|:--|:--|:--|
| L1 | P2 | Localization, reliability | The number field reported a different value after a change of locale or a recreation | Fixed; 2 regression tests |
| L2 | P2 | Localization, accessibility | Left-to-right labels in right-to-left layouts were cut ("[Çàñçél o]" ellipsized, chips clipped, a tab wrapped) and ended at the wrong side ("…Saving", "days ago 2") | Fixed; `RightToLeftLabelTest` (3); screenshots reviewed |
| A1 | P2 | Accessibility | Fields without a visible label had a 40 dp touch target (ATF, API 36) | Fixed; ATF and `LayoutQualityTest` |
| A2 | P2 | Verification | ATF silent under Robolectric and on API 29 | Device test with a control, run from API 36 and left out below it (a first version's assumption-based skip was counted as a failure by Android's device reports) |
| A3 | P2 | Verification | `QeetrixOverlayBackTest` had never run and its matcher was ambiguous | Fixed; passes on API 29 and 36 |
| R3 | P2 | Verification | Device tests ran in CI only on manual dispatch | CI runs them on every push and pull request (API 29, 36) |
| PF1 | P3 | Performance | The skeleton's shimmer allocated a gradient per frame | Fixed |
| PF2 | P3 | Performance | `IntRange` parameter of the number field is unstable | Accepted, reasoned in the performance report |
| L3 | P3 | Localization | Punctuation of an English paragraph in a right-to-left screen | Accepted: Compose's default for paragraphs |
| L4 | P3 | Privacy | A test typed the company's real mail domain | Fixed |
| S1 | P3 | Supply chain | CI's third-party actions on moving tags | Pinned to commit SHAs |
| D1 | P3 | Documentation | "Every control reserves 48 × 48 dp" overstated five kinds of control; the text field's "exactly 32 dp" no longer held without a label | Corrected |

**No P0 or P1 was found.** Every P2 is fixed; the two accepted P3s are reasoned in their reports.

## Changes

- **Library:** `NumberText.reads` and the number field's resync; `labelDirection()` on short labels; a 48 dp touch
  height for unlabelled fields (`LabelledControl`); the skeleton's cached gradient.
- **Tests:** the quality catalog (`src/sharedTest`, shared by `test` and `androidTest`); six JVM quality test classes;
  three new device test classes; regression tests for each fix.
- **Build:** `ui-test-junit4-accessibility` (tests only, BOM-managed); the sample's minified release and
  pseudolocales. No toolchain, plugin or runtime dependency changed.
- **CI:** device-test matrix on push and pull request; `:sample:assembleRelease`; SHA-pinned actions.
- **Documentation:** decision 0017; these reports; the website handoff; corrections to the components index, the
  text and search field pages, the accessibility and component standards, the contract, README, CONTRIBUTING and
  CHANGELOG.

## Evidence at the end of Phase 6

All on 2026-10-20, on the final working tree ([TEST-COVERAGE.md](TEST-COVERAGE.md)):

- **JVM:** 309 library tests, 15 sample tests and 44 generator tests pass, in three complete runs of the library
  suite with no failure; 147 screenshot baselines match after every changed image was reviewed.
- **Devices:** 12 of 12 on API 36 (the Accessibility Test Framework over 40 specimens — 36 components and 4 localized — in light and dark, with its
  control caught), 10 of 10 on API 29. One flaky test found and fixed (window focus), then 20 of 20 repeated runs.
- **Static:** lint with no issues in either module, ktlint clean, the API check passing against an unchanged
  baseline, tokens current against the pinned upstream, the AAR's content as expected, Dokka building.
- **Consumer:** the sample's R8-minified release built, installed, launched and took 1,500 random events on API 36
  with no crash and no linkage error.
- **Coverage:** 94.2 % of hand-written lines executed by the JVM suite.
- **Parity:** the four `@qeetrix/ui` inputs byte-identical with qeetrix-ios; the icon pins differ only by the two
  documented Android chrome icons.

## Reports

| Report | Covers |
|:--|:--|
| [COMPONENT-MATURITY-MATRIX.md](COMPONENT-MATURITY-MATRIX.md) | Every component's evidence and level |
| [ACCESSIBILITY-REPORT.md](ACCESSIBILITY-REPORT.md) | ATF, touch, clipping, contrast, reduced motion; what needs a person |
| [LOCALIZATION-RTL-REPORT.md](LOCALIZATION-RTL-REPORT.md) | Scripts, expansion, number formats, right to left |
| [PERFORMANCE-REPORT.md](PERFORMANCE-REPORT.md) | Stability, recomposition, animation, allocation |
| [RELIABILITY-REPORT.md](RELIABILITY-REPORT.md) | State, restoration, effects, input, overlays |
| [SECURITY-PRIVACY-REVIEW.md](SECURITY-PRIVACY-REVIEW.md) | Data handling, manifest, dependencies, CI |
| [API-COMPATIBILITY.md](API-COMPATIBILITY.md) | The public surface and how it is checked |
| [TEST-COVERAGE.md](TEST-COVERAGE.md) | Every suite, where it runs, and the results |
| [RELEASE-READINESS-GAPS.md](RELEASE-READINESS-GAPS.md) | What stands between this and a release |
