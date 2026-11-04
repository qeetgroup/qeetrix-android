# 📝 Changelog

All notable changes to Qeetrix for Android. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and versions follow [Semantic Versioning](https://semver.org/) as the [release policy](Documentation/Releases/RELEASE-POLICY.md)
describes. Ticket numbers are QTRX issues.

## [Unreleased] — 0.1.0

The first version. Built from `@qeetrix/react` 0.4.0, `@qeetrix/tokens` 0.0.13 and `@qeetrix/icons` 2.1.0.

### ✨ Added

**Foundations**

- `QeetrixTheme` in light, dark or the device's setting, with 109 colour roles (`QeetrixColors`) and 18 text roles.
- `QeetrixSpace`, `QeetrixSize`, `QeetrixRadius`, `QeetrixBorder`, `QeetrixShadow`, `QeetrixMotion`, `QeetrixBreakpoint`,
  and `QeetrixDensity` (compact, default, comfortable).
- `QeetrixFormat`: numbers, money, percentages, dates, times, relative times and durations in the device's locale, checked
  against 305 cases from the web's own rules (121).
- Haptics by role: `rememberQeetrixHaptics`, `QeetrixHapticRole` (134).
- 59 icons from `@qeetrix/icons`, sharp and round, outline and filled.

**Components**

| Group | Components |
|:--|:--|
| 👆 Actions | `QeetrixButton`, `QeetrixIconButton` with a tooltip (156), `QeetrixCopyButton` (122), `QeetrixSegmentedControl` (105), `QeetrixBottomActionBar` (132) |
| ✍️ Forms | `QeetrixTextField`, `QeetrixTextarea`, `QeetrixPasswordInput`, `QeetrixNumberField` with money and percent (193), `QeetrixSearchField`, `QeetrixSelect` and `QeetrixMultiSelect` (190), `QeetrixSearchableSelect` and `QeetrixSearchableMultiSelect` (126), `QeetrixCheckbox`, `QeetrixCheckboxGroup`, `QeetrixRadioGroup`, `QeetrixSwitch`, `QeetrixSlider` and `QeetrixRangeSlider` (130), `QeetrixOTPInput`, `QeetrixDateField` and `QeetrixDateRangeField` (120), `QeetrixTimeField` (128), `QeetrixAttachmentPicker` and `QeetrixFileRow` (127), `QeetrixField`, `QeetrixFieldSet`, `QeetrixForm` |
| 📊 Data display | `QeetrixText`, `QeetrixHeading`, `QeetrixAvatar`, `QeetrixAvatarItem`, `QeetrixAvatarGroup` (129), `QeetrixBadge`, `QeetrixChip`, `QeetrixChipGroup`, `QeetrixListItem`, `QeetrixSectionHeader`, `QeetrixSwipeActions` (106), `QeetrixDescriptionList`, `QeetrixTimeline`, `QeetrixStat` and `QeetrixStatGroup` (124), `QeetrixCheckList` (131), `QeetrixRelativeTime` (121), `QeetrixQRCode` (125), `QeetrixOTPCode` (101), `QeetrixCopyField` (122) |
| 💬 Feedback | `QeetrixAlert` (104), `QeetrixProgress`, `QeetrixMeter`, `QeetrixProgressRing` (102), `QeetrixSpinner`, `QeetrixSkeleton`, `QeetrixSnackbar` with work-in-progress snackbars and stacking (192), `QeetrixEmptyState`, `QeetrixDataState`, `QeetrixPullToRefresh` (107) |
| 🪟 Overlays | `QeetrixDialog`, `QeetrixAlertDialog`, `QeetrixConfirmationDialog`, `QeetrixFullScreenForm` (133), `QeetrixBottomSheet`, `QeetrixSideSheet` (195), `QeetrixMenu` with submenus, and `QeetrixContextMenu` (191) |
| 🧭 Navigation | `QeetrixTopAppBar` with a count, status, tabs and a collapsing title (196, 137), `QeetrixTabs` and `QeetrixTabPanel` (194), `QeetrixNavigationBar`, `QeetrixNavigationRail`, `QeetrixNavigationScaffold`, `QeetrixSteps` (123), `QeetrixPageIndicator` (136) |
| 📐 Layout | `QeetrixCard`, `QeetrixAccordion`, `QeetrixSeparator` |
| 🧰 Utilities | `QeetrixQRScanner` (103), `QeetrixPermissionPrimer` (135) |

**Project**

- The MIT licence: `LICENSE`, the POM's `<licenses>`, and the licence text inside the AAR.
- The sample app, with a page for every component.
- An independent consumer app that builds against the published library.
- `Scripts/release-candidate`, and the Qeetrix release flow: merging to `main` publishes the version in
  `gradle.properties` to GitHub Packages after an approver approves, then tags it and opens a GitHub Release.
- CodeQL code scanning, OpenSSF Scorecard and monthly Dependabot updates.
- CI with device tests on API 29 and API 36.

### ⚠️ Breaking, since `0.1.0-rc.1`

- **Every public API changed** when the library moved from `@qeetrix/ui` to `@qeetrix/react`, `@qeetrix/tokens` and
  `@qeetrix/icons` ([0019](Documentation/Decisions/0019-migrate-to-qeetrix-react-tokens-and-icons.md)). Nothing had been
  published, so there is no deprecation path. Every old name and its new one are in
  [MIGRATION_QEETRIX_REACT.md](Documentation/MIGRATION_QEETRIX_REACT.md).
- `QeetrixBottomSheetSize` is renamed `QeetrixSheetSize`, the size both sheets take (195).

### 🔄 Changed

- **Tokens** are generated from `@qeetrix/tokens`' DTCG build: 202 generated, and 137 excluded, each with a written
  reason.
- **Fonts** are the native TrueType files `@qeetrix/tokens` publishes (91).
- **The bottom sheet no longer wraps Material** (198). It is built on Compose foundation, with drawer motion, the
  predictive Back gesture, and a handle that is only a mark. The string `qx_drag_handle` is removed.
- **Right to left:** text takes its direction from its content (199), and arrows and chevrons mirror only where the
  call site asks (200).
- **Placeholders** end in an ellipsis at large text sizes (197).
- **Looks from `@qeetrix/react` 0.4.0** take the web team's fixes for Android's findings (157 to 189). For example,
  dark borders are visible on overlays, and line heights sit exactly on the 4 px grid.

### 🐛 Fixed

- Text kept the optical size of an earlier font scale after the person changed their font size.

#### Fixed on the device pass

Found on an Android 16 phone emulator, in light, dark, 200 % text and right to left. Each fix has a test that fails
without it.

- **`QeetrixQRScanner`:** the permission states hid "Type the key instead", and the controls were hard to read on the
  scrim.
- **`QeetrixSwipeActions` and `QeetrixSlider`:** they moved against the finger in right to left.
- **Date and time pickers:** Saturdays were cut off on a phone, and Material's pink leaked into the colours.
- **`QeetrixSteps`:** a label broke mid-word at 200 % text.
- **`QeetrixAvatarGroup`:** the +N count grew with the text size.
- **`QeetrixPageIndicator`:** the dots had too little room to tap.
- **`QeetrixNumberField`:** money lost its currency's decimals.
- **`QeetrixFullScreenForm` and `QeetrixDialog`:** the status bar's icons were white on white.

#### Fixed on the tablet pass

Found on an Android 16 tablet emulator.

- **`QeetrixBottomActionBar`:** on wide screens the first action was pushed off the screen.
- **`QeetrixFullScreenForm`:** as a form sheet, it darkened the screen twice.

### 🗑️ Removed

- **The clickable `QeetrixCard`** (182): a card is not a click target. Use a choice tile (`QeetrixRadioGroup` or
  `QeetrixCheckboxGroup` with `variant = Tile`) or a `QeetrixListItem`.
- **The old foundation names**, such as `QeetrixSpacing`, `QeetrixCorner`, `QeetrixElevation` and `QeetrixTextVariant`.
  The [migration record](Documentation/MIGRATION_QEETRIX_REACT.md) lists each one with its replacement.
- **The icons** `calendar`, `clock`, `database` and `loader-circle`.

## 🧪 Release candidates

Neither candidate was published.

| Candidate | Built | Contents | Result |
|:--|:--|:--|:--|
| `0.1.0-rc.2` | 2026-10-31 | The migration and the first 31 Android tickets | ✅ Passed all ten release checks ([report](Documentation/Releases/RELEASE-CANDIDATE-REPORT.md)) |
| `0.1.0-rc.1` | 2026-10-20 | Built on `@qeetrix/ui`, before the migration | Superseded |

The phase-by-phase notes from before 4 November 2026 are in the git history (`git log -p -- CHANGELOG.md`).
