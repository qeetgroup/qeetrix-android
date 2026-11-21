# 📝 Changelog

All notable changes to Qeetrix for Android. The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and versions follow [Semantic Versioning](https://semver.org/) as the [release policy](docs/releases.md)
describes.

## [Unreleased]

## [0.4.0] — 2026-11-21

Additions, a minor release ([docs/releases.md](docs/releases.md)). Built from `@qeetrix/react` 0.5.1,
`@qeetrix/tokens` 0.0.15 and `@qeetrix/icons` 2.1.2. Every 0.3.0 signature stays for compiled apps.

### ✨ Added

- **`QeetrixSlider` without a label** — `@qeetrix/react`'s Slider with only an `aria-label`: the control alone, laid
  out at the web's 24 dp (`size.target.min`) with a tap anywhere in the 48 dp band round its track, for a slider in a
  row between two icons. Name it with a `contentDescription` on its `modifier`, as the switch without a label: TalkBack
  reads the name with the value and its adjust gestures step it.
- **`QeetrixRadioGroup(hideLabel = true)`** — `@qeetrix/react`'s RadioGroup with its label in a VisuallyHidden: the
  options alone, in every orientation and variant, for a group right under a screen heading that already asks the
  question. The label still names the group and is read as its heading before the options. The 0.3.0 signature stays
  for compiled apps.
- **`QeetrixRadioGroup(optionLabel = { option -> … })`** — `@qeetrix/react`'s Radio with a node for its label: each
  option draws its own title in the row's or the tile's place (a language in its own script, a picture beside the
  title, a badge after it), the radio on its first line and the description under it, in the label's colour
  (`LocalQeetrixContentColor`). TalkBack still reads the option's plain `label`, its description and its state.
- **`QeetrixPictureChoiceGroup`** — `@qeetrix/react`'s PictureChoiceGroup (experimental): one choice among pictures —
  an avatar, a background tone, a sticker — each a `QeetrixOption` drawn by the `picture` slot in a round or square
  frame at 36 (`Sm`, for swatches, with a 16 dp tick), 48 (`Md`, the default) or 64 dp (`Lg`), in a wrapping row or a
  grid of `columns`. The chosen one has a 2 dp `color.border.brand` ring
  set off by the surface and a tick at its top end corner; TalkBack reads each as a radio button by its label, in a
  group named by `contentDescription`. picture-choice.css is now pinned (`experimental/picture-choice`).
- **`QeetrixSegmentedControl(fullWidth = true)`** — toggle.css's `[data-full-width]`: the strip fills its container
  and every segment takes an equal share, its label centred and ending in an ellipsis when the share is too narrow.
  The 0.3.0 signature stays for compiled apps.
- **`QeetrixAccordionItem(titleContent = { … })`** — react's AccordionItem with a node for its `title`: the item draws
  its own title (a compact list's `body.md` title with its purpose in `body.sm` under it), with the chevron centred on
  the trigger as on the web. The trigger stays one heading button read with the plain `title` and its state. The
  0.3.0 signature stays for compiled apps.
- **`rememberQeetrixIconPainter(name, shape, variant, stroke, mirrorInRightToLeft)`** and
  **`QeetrixIconName.imageVector(…)`**: an icon, core or extended, as a `Painter` or an `ImageVector`, to draw inside a
  `Canvas` or any `DrawScope` (map markers, chart points). They draw exactly what `QeetrixIcon` draws — the same
  fallback when a variant isn't drawn and the same right-to-left mirroring — tinted with a `ColorFilter` where drawn
  ([docs/icons.md](docs/icons.md)). `QeetrixIcon` now draws through the same painter.
- **`QeetrixButton(…) { icon }` and `QeetrixIconButton(contentDescription, onClick, …) { icon }`** — `@qeetrix/react`'s
  Button and IconButton with a component for the icon: your own drawing (the call-end sign is the phone, filled and
  turned 135°) gets the zone a named icon gets — the icon size for the button's size, the zone's padding and 55 % rule,
  the label's colour through `LocalQeetrixContentColor`, full-width centring — and the spinner takes its place while
  loading.
- **Colour resources for XML themes**: `@color/qx_surface_canvas`, `qx_surface_default`, `qx_surface_raised`,
  `qx_text_default` and `qx_bg_brand_bold`, light in `values` and dark in `values-night`, generated from the same
  `@qeetrix/tokens` as `QeetrixTheme.colors` and equal to them, so a window background or the system splash screen
  (`android:windowBackground`, `windowSplashScreenBackground`) follows Qeetrix with no hex
  ([docs/tokens.md](docs/tokens.md)). The AAR now carries `res/values-night`.
- **A picture-led `QeetrixBottomSheet(picture = { … })`**: the picture centred at the top, the title in `title.lg` and
  the description centred under it, no ruled header or close button (the scrim, Back and a swipe still close it), then the content and
  footer as usual. The 0.3.0 signature stays for compiled apps.
- **`QeetrixBottomSheetSurface`**: the bottom sheet's surface drawn in place — handle, header or picture, body and
  footer, `color.surface.overlay` under `shadow.modal` — for previews, screenshots and a screen's picture of its open
  state, without a window.
- **`QeetrixTextField(size = QeetrixTextFieldSize.Lg)`** — `@qeetrix/react`'s Input `size="lg"`, for the one value a
  sign-up or verification step asks for (a phone number, a name, a date of birth): `size.control.lg` + 16 dp tall (56 dp
  at the default density, 60 dp at the comfortable, as the large OTP input), the value at `title.lg`'s size in regular
  weight with tabular figures and the body's letterforms, the placeholder in `body.md`; the label, help text, error and
  adornments line up as at the default size. Sign-in's email and password stay `Md`. Its 0.2.0 signature stays for
  compiled apps.

### 🔄 Changed

- **`QeetrixSegmentedControl` draws `label.md` with 12 dp padding at every size**, as the web's toggles keep their own
  size inside a group: `Sm` and `Lg` change only the strip's height now (`Sm` was `label.sm` with 8 dp, `Lg` 16 dp).
- **Choice tiles of short choices are at least 128 dp wide**, not 192 dp, as `@qeetrix/react`'s field.css
  (`--qx-choice-tile-min`): when no option of a `QeetrixRadioGroup` or `QeetrixCheckboxGroup` has an icon or a
  description, `columns = 2` keeps two equal tiles a row in a phone's 328 dp column, 8 dp apart. Tiles with an icon or
  a description keep the 192 dp minimum, so they still take fewer columns where there isn't room.
- **Buttons, icon buttons, the bare switch and the bare checkbox are laid out at their drawn size**, as on the web,
  instead of reserving 48 × 48 dp in the layout. A `QeetrixButton(size = Lg)` takes 44 dp at the comfortable density
  (40 dp at the default), so a stack of full-width buttons 8 dp apart is 8 dp apart; a `QeetrixSwitch` without a label
  takes its track's 32 × 18 dp and a `QeetrixCheckbox` without one its 16 dp box, so a one-line settings row keeps the
  web's height. Each still takes a tap anywhere within 48 dp through Compose's touch-target expansion, which TalkBack and
  the Accessibility Test Framework read as its bounds; the focus frame is unchanged. Screens drawn round the old 48 dp
  boxes move: a footer, a stack or a card's actions sit where the web puts them, and a copy field, a file row, an alert
  and a dialog's close button lose the extra height.
  - Controls closer than 48 dp centre to centre, as at the web's spacing, have overlapping touch areas, and Compose
    sends a tap in the overlap to the nearer one. The top app bar and the full-screen form's bar keep each icon's 48 dp in
    the layout, as their icons sit edge to edge. In a toolbar of your own, pass `Modifier.qeetrixMinimumTouchTarget()` to
    each icon button to do the same.

## [0.3.0] — 2026-11-08

A source-breaking change, which before 1.0 takes a minor release ([docs/releases.md](docs/releases.md)). Built from
`@qeetrix/react` 0.5.1, `@qeetrix/tokens` 0.0.15 and `@qeetrix/icons` 2.1.2.

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

[Unreleased]: https://github.com/qeetgroup/qeetrix-android/compare/v0.4.0...HEAD
[0.4.0]: https://github.com/qeetgroup/qeetrix-android/releases/tag/v0.4.0
[0.3.0]: https://github.com/qeetgroup/qeetrix-android/releases/tag/v0.3.0
[0.2.0]: https://github.com/qeetgroup/qeetrix-android/releases/tag/v0.2.0
[0.1.0]: https://github.com/qeetgroup/qeetrix-android/releases/tag/v0.1.0
