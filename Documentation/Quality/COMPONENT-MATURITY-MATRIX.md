# Component maturity matrix (Phase 6)

Every public component, the automated evidence behind it, and what it still lacks. Counts are test methods in the
JVM suite on 2026-10-20 (`./gradlew :qeetrix:testDebugUnitTest`); device evidence is the API 29 and API 36
emulators ([0017](../Decisions/0017-quality-gates-and-verification.md)).

## Levels

| Level | Means |
|:--|:--|
| **A — automated, complete** | In the reviewed API baseline; behaviour and semantics tests on the JVM; screenshot baselines in light, dark, 200 % and right to left; in the quality catalog, so ATF runs over it on API 36 and the touch, clipping and contrast scans cover it; a documentation page, KDoc and a sample page |
| **B — automated, partial** | As A, except that one layer cannot reach it — named in the gaps column |
| **Release-verified** | A, plus a manual TalkBack, Switch Access and keyboard pass on a physical device. **No component is release-verified**: no manual pass has been done ([RELEASE-READINESS-GAPS.md](RELEASE-READINESS-GAPS.md)) |

Every component has a documentation page ([Components](../Components/README.md)), KDoc published by Dokka, a page in
the sample, and is in the API baseline ([API-COMPATIBILITY.md](API-COMPATIBILITY.md)); those columns are omitted.

## Matrix

| Component | JVM tests | Screenshots | Catalog | Device | Level | Gaps |
|:--|--:|:-:|:-:|:--|:-:|:--|
| Button | 6 | ✓ | ✓ | ATF, clipping | A | |
| Icon button | 4 | ✓ | ✓ | ATF | A | |
| Checkbox, tri-state checkbox | 6 | ✓ | ✓ | ATF | A | |
| Field, field set | 6 | ✓ | ✓ | ATF | A | |
| Form actions, error summary | 4 | ✓ | ✓ | ATF | A | |
| Text field | 8 | ✓ | ✓ | ATF | A | Unlabelled form fixed in Phase 6 (A1) |
| Textarea | 5 | ✓ | ✓ | ATF | A | |
| Search field | 5 | ✓ | ✓ | ATF | A | Unlabelled form fixed in Phase 6 (A1) |
| Password input | 6 | ✓ | ✓ | ATF | A | AutoFill offers not exercised on a device |
| OTP input | 12 | ✓ | ✓ | ATF | A | SMS one-time-code offer not exercised on a device |
| Number field | 14 + 11 (`NumberTextTest`) | ✓ | ✓ | ATF, ICU locales | A | Locale-change defect fixed in Phase 6 (L1) |
| Radio group | 6 | ✓ | ✓ | ATF | A | |
| Select | 8 | ✓ | Trigger | ATF (trigger) | B | The open list is a popup window: no ATF or scan over it |
| Switch | 4 | ✓ | ✓ | ATF | A | |
| Text | 3 + 6 (`TypographyTest`) | ✓ | ✓ | ATF, font axis | A | |
| Avatar | 4 | ✓ | ✓ | ATF | A | |
| Badge | 2 | ✓ | ✓ | ATF | A | |
| Chip, chip group | 7 | ✓ | ✓ | ATF | A | |
| Description list | 5 | ✓ | ✓ | ATF | A | |
| List item, section header | 9 + 4 (`QeetrixCollectionTest`) | ✓ | ✓ | ATF, long keyed list | A | |
| Timeline | 4 | ✓ | ✓ | ATF | A | |
| Empty state | 3 | ✓ | ✓ | ATF | A | |
| Data state | 4 | ✓ | ✓ | ATF | A | |
| Progress | 5 | ✓ | ✓ | ATF | A | |
| Spinner | 3 | ✓ | ✓ | ATF | A | |
| Skeleton | 2 | ✓ | ✓ | ATF | A | Drawing allocation fixed in Phase 6 (performance P1) |
| Snackbar, host, host state | 3 + 7 | ✓ | Snackbar | ATF | A | Announcement timing not checked with TalkBack |
| Alert dialog, confirmation dialog | 5 + 5 (`QeetrixDialogActionTest`) | ✓ (surface) | — | Back | B | Dialog windows: no ATF or scan |
| Dialog | 4 | ✓ (surface) | — | Back | B | As alert dialog |
| Bottom sheet | 5 | ✓ (surface) | — | Back over a dialog | B | Dialog window; drag on a device not exercised |
| Menu | 3 | ✓ (surface) | — | Back | B | Popup window: no ATF or scan |
| Navigation bar | 4 | ✓ | ✓ | ATF | A | |
| Navigation rail | 1 | ✓ | ✓ | ATF | A | |
| Adaptive navigation (scaffold) | 4 | — | — | — | B | A whole-screen layout: covered by behaviour tests and the sample's flows |
| Tabs | 3 | ✓ | ✓ | ATF | A | |
| Top app bar, back button | 2 | ✓ | ✓ | ATF | A | |
| Accordion | 4 | ✓ | ✓ | ATF | A | |
| Card | 4 | ✓ | ✓ | ATF | A | |
| Separator | 2 | ✓ | ✓ | ATF | A | |
| Icon (foundation) | 6 | ✓ | ✓ | ATF | A | |

Cross-cutting suites that cover every catalog component are not repeated per row: `LayoutQualityTest`,
`TouchLandingTest`, `ComponentContrastTest`, `ReducedMotionTest`, `RightToLeftLabelTest`,
`QeetrixLocalizationScreenshotTest`, `MinimumTouchTargetTest`, `MaterialBoundaryTest`, and on devices
`QeetrixAccessibilityDeviceTest` and `QeetrixLayoutDeviceTest`.

## Reading the matrix

- 33 of 39 rows are level A; the six B rows are the overlays and the whole-screen scaffold, where the tools stop at
  a window boundary. Their surfaces have screenshots, their behaviour has JVM tests, and Back has a device test.
- Level A is automated evidence only. It is necessary for a release, not sufficient.
