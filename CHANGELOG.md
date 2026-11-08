# 📝 Changelog

All notable changes to Qeetrix for Android. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and versions follow [Semantic Versioning](https://semver.org/) as the [release policy](docs/releases.md)
describes.

## [Unreleased]

A source-breaking change, which before 1.0 takes a minor release ([docs/releases.md](docs/releases.md)): 0.3.0.

### ⚠️ Breaking

- **`QeetrixIconName` is a class, not an enum**, so every component can take an icon from the extended catalogue too.
  `QeetrixIconName.Send` and every other core name still compile, and names compare by `value`. What changes:
  `QeetrixIconName.entries` is now `QeetrixIconName.catalogue`; `name`, `ordinal` and `valueOf` are gone (use `value`);
  and a `when` over an icon needs an `else` branch.

### ✨ Added

- **Every `@qeetrix/icons` icon** (2.1.2: 1,863 in outline, 775 of them filled too), by its upstream name, in sharp and
  round, mirrored in right to left as upstream says ([docs/icons.md](docs/icons.md), decision 0020).
  - **`com.qeetgroup:qeetrix-icons-extended`**, a new optional module released with the library at the same version:
    the 1,763 icons beyond the core set, as `QeetrixIconName.AlarmClock` (imported from
    `com.qeetgroup.qeetrix.icons.extended`), and `QeetrixIconName.extendedCatalogue`. Each icon is its own class, so
    R8 keeps only the icons an app names; `Scripts/icon-budget` checks that in CI, against a budget of 4 KiB per icon.
  - **12 more core icons** the Qeet ID screens draw, so iOS and Android carry the same 100: `ArrowLeftRight`,
    `BellRing`, `BotOff`, `ClipboardPaste`, `Download`, `Globe`, `Laptop`, `Monitor`, `QrCode`, `RotateCcw`,
    `ShieldAlert` and `Sparkles`, filled for `BellRing`, `Laptop`, `Monitor`, `ShieldAlert` and `Sparkles`. None
    mirrors in right to left: upstream marks every one, the two-way `ArrowLeftRight` included, as reading the same either
    way.
- **The sample's Icons page**: every icon, core and extended, searchable by name.

### 🔧 Changed

- **Icon drawings are compact path strings** decoded the first time an icon is drawn, instead of generated path code:
  the core icons' generated source is 168 KB instead of 900 KB, and the drawings are unchanged.

## [0.2.0] — 2026-11-08

Additions, a minor release ([docs/releases.md](docs/releases.md)). Built from `@qeetrix/react` 0.5.1, `@qeetrix/tokens` 0.0.15 and `@qeetrix/icons` 2.1.2.

### ✨ Added

- **`QeetrixColors.bgDangerSubtlePressed`**: `color.bg.danger.subtle-pressed` (`@qeetrix/tokens` 0.0.14), `#F3DEDF` in
  light and `#412020` in dark, the danger tint's pressed step.
- **29 icons** in `QeetrixIconName`, for the native Qeet ID app and to carry the same 88 names as Qeetrix for iOS:
  `Archive`, `ArrowDown`, `ArrowUp`, `BellOff`, `ChevronUp`, `ChevronsUpDown`, `CirclePlus`, `Database`,
  `ExternalLink`, `FingerprintPattern`, `Hourglass`, `ImagePlus`, `Key`, `KeyRound`, `Link`, `LoaderCircle`, `LockOpen`,
  `PanelRight`, `Pin`, `RectangleEllipsis`, `ScanFace`, `ScanLine`, `ScanQrCode`, `ShieldCheck`, `ShieldX`, `Smartphone`,
  `Timer`, `UserPlus` and `WifiOff`, from `@qeetrix/icons` 2.1.0 in sharp and round, filled where upstream draws one.
  An exhaustive `when` over `QeetrixIconName` needs the new branches.
- **`QeetrixOTPInput(slotWidth = QeetrixOTPSlotWidth.Square)`** keeps a short code's slots square at the field's start —
  a 2-digit number match — while the label, description and error keep the container's width. The default,
  `Fill`, shares the width as before; the 0.1.0 signature stays for compiled apps.
- **`confirm` and `clearAfter`** on `QeetrixCopyButton`, `QeetrixCopyField` and `QeetrixOTPCode`.
  `confirm = false` drops the "Copied" toast below Android 13 for an app that confirms the copy itself.
  `clearAfter = Duration.ofSeconds(30)` clears the clipboard then if it still holds the copy; a deadline that passes
  while the app is in the background waits until the app is back in front, as Android hides the clipboard from it
  until then. The defaults keep 0.1.0's behaviour, and its signatures stay for compiled apps.
- **`QeetrixQRScanner(enterKeyLabel, deniedDescription, controlsLayout)`**: the fallback's words ("Type the
  code instead" for a pairing code) in every state, the refused camera's message naming the app, and
  `QeetrixScannerControlsLayout.Row` to put the fallback and the torch in one row, the fallback filling it. The
  defaults keep 0.1.0's strings and stacked controls; its signature stays for compiled apps.
- **`QeetrixListItemIcon(text)`**: the row's tile with one or two characters — an issuer's initial, a step's number
  — drawn as the icon tile, growing with the font scale, hidden from accessibility services.
- **`QeetrixListItem(content = …)`**, a slot under the description, and **`QeetrixOTPCode(countdownAtEnd = true)`**,
  which puts the ring and seconds at the end of its width: an authenticator's account row from Qeetrix parts. A
  control in the slot stays its own element. `QeetrixListItem`'s 0.1.0 signature stays for compiled apps.
- **`QeetrixActionSheet`** with `QeetrixSheetAction` and `QeetrixSheetActionSeparator`: a bottom sheet of
  actions built as menu items — icon, label, description, the danger tone, the menu's highlight — running edge to
  edge, each a button; choosing one closes the sheet first and runs it once, unless `closeOnClick = false`.
- **`QeetrixBottomActionBar(arrangement = …)`**: `PrimaryFills` keeps a ghost "Not now" at its own width at
  the start and lets the primary fill the rest; `Stacked` puts the primary full width over a centred ghost action. Both
  stack full width where a label wouldn't fit; `Equal`, the default, is 0.1.0's split, and its signature stays.
- **Links**: `qeetrixLink(onClick, tone)` for a link inside a `QeetrixText` sentence, and `QeetrixLink` for
  one on its own — `@qeetrix/react`'s Link: `color.text.link` (or subtle, or the text's colour), underlined, the active
  colour when pressed or focused. Android's own link behaviour: focusable, read as a link, a 48 dp tap. The `link`
  family's CSS is now pinned.
- **`QeetrixEmptyStateTone.Success` and `Warning`**: an outcome screen's icon in `color.icon.success` or
  `color.icon.warning`, 3:1 or more on the canvas and raised surfaces in light and dark. Native only, as on iOS. An
  exhaustive `when` over `QeetrixEmptyStateTone` needs the new branches.
- **`QeetrixTextField(mono = true)`**: the value and placeholder in `code.md` (Fira Code) for keys, codes and
  IDs, read-only too, the box keeping its height. Its 0.1.0 signature stays for compiled apps.
- **`QeetrixAccordionItem(description = …)`**: a summary under the title in the trigger, in `body.sm`
  `color.text.subtle`, read with the title and the state; the chevron stays on the title's first line.
- **`QeetrixFormat.time`**: a time without its date — "10:47 am" — in the short or medium style, the time
  part of `dateTime` on ICU, for an `Instant` in a zone or a `LocalTime`. `hour24` follows the phone's 12- or 24-hour
  setting the way the web's `hourCycle` does. Checked against Node's `Intl` for en-IN, hi-IN, en-US, ar-EG and de-DE.
- **`QeetrixTopAppBar(title = null, description = "Step 1 of 2")`**: a bar with no title shows its
  description as a caption in `body.sm`, read as text rather than a heading, at the bar's height. `title` is now
  nullable, which compiled apps don't notice.
- **`QeetrixPermissionPrimer(footer = …)`**: a short reassurance under the actions, as the empty state's
  footer, in both states. Its 0.1.0 signature stays for compiled apps; the QR scanner's primer is unchanged.

### 🔄 Changed

- **A ghost danger `QeetrixButton` presses in `bgDangerSubtlePressed`**, so a press no longer looks the same as a
  hover, as on the web (`@qeetrix/react` 0.5.1). The hover keeps `bgDangerSubtle`; `textDanger` keeps 4.5:1 on both.
- **The filled Send icon is one paper plane** (`@qeetrix/icons` 2.1.2): the fold no longer cuts through the nose,
  so its two halves meet at one tip, round and sharp.
- **A full-width `QeetrixButton` centres its label** on the whole button, with or without an icon, as the design
  rules now say on every platform (the bottom action bar's stacked actions, the QR scanner's fallback). An icon keeps
  its zone at the end. Where a label is too long to centre, the start gives way first, so the button's natural width
  is unchanged and the QR scanner's row chooses as before.

## [0.1.0] — 2026-11-04

The first version, on GitHub Packages. Built from `@qeetrix/react` 0.4.0, `@qeetrix/tokens` 0.0.13 and `@qeetrix/icons` 2.1.0.

### ✨ Added

**Foundations**

- `QeetrixTheme` in light, dark or the device's setting, with 109 colour roles (`QeetrixColors`) and 18 text roles.
- `QeetrixSpace`, `QeetrixSize`, `QeetrixRadius`, `QeetrixBorder`, `QeetrixShadow`, `QeetrixMotion`, `QeetrixBreakpoint`,
  and `QeetrixDensity` (compact, default, comfortable).
- `QeetrixFormat`: numbers, money, percentages, dates, times, relative times and durations in the device's locale, checked
  against 305 cases from the web's own rules.
- Haptics by role: `rememberQeetrixHaptics`, `QeetrixHapticRole`.
- 59 icons from `@qeetrix/icons`, sharp and round, outline and filled.

**Components**

| Group | Components |
|:--|:--|
| 👆 Actions | `QeetrixButton`, `QeetrixIconButton` with a tooltip, `QeetrixCopyButton`, `QeetrixSegmentedControl`, `QeetrixBottomActionBar` |
| ✍️ Forms | `QeetrixTextField`, `QeetrixTextarea`, `QeetrixPasswordInput`, `QeetrixNumberField` with money and percent, `QeetrixSearchField`, `QeetrixSelect` and `QeetrixMultiSelect`, `QeetrixSearchableSelect` and `QeetrixSearchableMultiSelect`, `QeetrixCheckbox`, `QeetrixCheckboxGroup`, `QeetrixRadioGroup`, `QeetrixSwitch`, `QeetrixSlider` and `QeetrixRangeSlider`, `QeetrixOTPInput`, `QeetrixDateField` and `QeetrixDateRangeField`, `QeetrixTimeField`, `QeetrixAttachmentPicker` and `QeetrixFileRow`, `QeetrixField`, `QeetrixFieldSet`, `QeetrixForm` |
| 📊 Data display | `QeetrixText`, `QeetrixHeading`, `QeetrixAvatar`, `QeetrixAvatarItem`, `QeetrixAvatarGroup`, `QeetrixBadge`, `QeetrixChip`, `QeetrixChipGroup`, `QeetrixListItem`, `QeetrixSectionHeader`, `QeetrixSwipeActions`, `QeetrixDescriptionList`, `QeetrixTimeline`, `QeetrixStat` and `QeetrixStatGroup`, `QeetrixCheckList`, `QeetrixRelativeTime`, `QeetrixQRCode`, `QeetrixOTPCode`, `QeetrixCopyField` |
| 💬 Feedback | `QeetrixAlert`, `QeetrixProgress`, `QeetrixMeter`, `QeetrixProgressRing`, `QeetrixSpinner`, `QeetrixSkeleton`, `QeetrixSnackbar` with work-in-progress snackbars and stacking, `QeetrixEmptyState`, `QeetrixDataState`, `QeetrixPullToRefresh` |
| 🪟 Overlays | `QeetrixDialog`, `QeetrixAlertDialog`, `QeetrixConfirmationDialog`, `QeetrixFullScreenForm`, `QeetrixBottomSheet`, `QeetrixSideSheet`, `QeetrixMenu` with submenus, and `QeetrixContextMenu` |
| 🧭 Navigation | `QeetrixTopAppBar` with a count, status, tabs and a collapsing title, `QeetrixTabs` and `QeetrixTabPanel`, `QeetrixNavigationBar`, `QeetrixNavigationRail`, `QeetrixNavigationScaffold`, `QeetrixSteps`, `QeetrixPageIndicator` |
| 📐 Layout | `QeetrixCard`, `QeetrixAccordion`, `QeetrixSeparator` |
| 🧰 Utilities | `QeetrixQRScanner`, `QeetrixPermissionPrimer` |

**Project**

- The MIT licence: `LICENSE`, the POM's `<licenses>`, and the licence text inside the AAR.
- The sample app, with a page for every component.
- `Scripts/release-candidate`, and the Qeetrix release flow: merging to `main` publishes the version in
  `gradle.properties` to GitHub Packages after an approver approves, then tags it and opens a GitHub Release.
- CodeQL code scanning and monthly Dependabot updates.
- Test coverage with Kover: 94.0 % of lines; CI fails below 92 %.
- CI with device tests on API 29 and API 36.

### ⚠️ Breaking, since `0.1.0-rc.1`

- **Every public API changed** when the library moved from `@qeetrix/ui` to `@qeetrix/react`, `@qeetrix/tokens` and
  `@qeetrix/icons` ([0019](docs/decisions.md)). Nothing had been
  published, so there is no deprecation path. Every old name and its new one are in
  [MIGRATION_QEETRIX_REACT.md](CHANGELOG.md).
- `QeetrixBottomSheetSize` is renamed `QeetrixSheetSize`, the size both sheets take.

### 🔄 Changed

- **Tokens** are generated from `@qeetrix/tokens`' DTCG build: 202 generated, and 137 excluded, each with a written
  reason.
- **Fonts** are the native TrueType files `@qeetrix/tokens` publishes.
- **The bottom sheet no longer wraps Material**. It is built on Compose foundation, with drawer motion, the
  predictive Back gesture, and a handle that is only a mark. The string `qx_drag_handle` is removed.
- **Right to left:** text takes its direction from its content, and arrows and chevrons mirror only where the
  call site asks.
- **Placeholders** end in an ellipsis at large text sizes.
- **Looks from `@qeetrix/react` 0.4.0** take the web team's fixes for Android's findings. For example,
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

- **The clickable `QeetrixCard`**: a card is not a click target. Use a choice tile (`QeetrixRadioGroup` or
  `QeetrixCheckboxGroup` with `variant = Tile`) or a `QeetrixListItem`.
- **The old foundation names**, such as `QeetrixSpacing`, `QeetrixCorner`, `QeetrixElevation` and `QeetrixTextVariant`.
  The [migration record](CHANGELOG.md) lists each one with its replacement.
- **The icons** `calendar`, `clock`, `database` and `loader-circle`.

## 🧪 Release candidates

Neither candidate was published.

| Candidate | Built | Contents | Result |
|:--|:--|:--|:--|
| `0.1.0-rc.2` | 2026-10-31 | The migration and the first 31 Android tickets | ✅ Passed all ten release checks ([report](docs/releases.md)) |
| `0.1.0-rc.1` | 2026-10-20 | Built on `@qeetrix/ui`, before the migration | Superseded |

The phase-by-phase notes from before 4 November 2026 are in the git history (`git log -p -- CHANGELOG.md`).

[Unreleased]: https://github.com/qeetgroup/qeetrix-android/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/qeetgroup/qeetrix-android/releases/tag/v0.2.0
[0.1.0]: https://github.com/qeetgroup/qeetrix-android/releases/tag/v0.1.0
