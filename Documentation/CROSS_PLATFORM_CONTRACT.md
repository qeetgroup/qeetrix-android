# Cross-platform contract

How a Qeetrix design decision made for the web arrives on Android unchanged, what "unchanged" means where
the platforms differ, and what was verified against qeetrix-ios.

**Status.** Written by the Android side. It follows the rules qeetrix-ios recorded in its own
`Documentation/CROSS_PLATFORM_CONTRACT.md` and decision records. Foundation parity was checked against
qeetrix-ios's committed Phase 1 (`f335645`); component parity against its **uncommitted** Phase 2 working
tree on 2026-10-18 (on top of `f335645`), which may still change. The iOS side has not reviewed this
document; it is not a jointly approved contract.

## Source of truth

| Concern | Authority | Android reads | iOS reads |
|:--|:--|:--|:--|
| Tokens | qeetrix-ui `src/tokens/**` (DTCG), compiled by its Style Dictionary build | `@qeetrix/ui` 2.1.6 `tokens.json`, `tokens.css`, `index.css` | the same files |
| Component catalogue | qeetrix-ui `component-manifest.json` (137 components, 97 families) | the same file from 2.1.6, for `new-component` | the same file |
| Themes | qeetrix-ui `scripts/config/themes.json` (`light`, `dark`) | the theme keys in `tokens.json` | the same |
| Typeface | qeetrix-ui `fonts/` (WOFF2) | calcom/sans v2.011 and Fira Code 6.2 TTFs, proven byte-identical through their WOFF2 | the same files |
| Icons | qeetrix-icons `icons/**.svg` + `icon-manifest.json` | commit `331cd85` (`@qeetrix/icons` 1.0.11), 31 icons | the same commit; 37 icons (see Pins below) |

Changes flow one way: upstream release → `Upstream/manifest.json` (iOS: `Upstream/pins.json`) → sync →
generate. Generated files and the snapshot are never edited in either repository.

## Verified parity (Android vs qeetrix-ios `f335645`, `@qeetrix/ui` 2.1.6)

| What | Result |
|:--|:--|
| Upstream files taken (token artifacts, fonts, licences, SVGs) | 52 of 52 shared files sha256-identical |
| Token scope | 217 generated, 666 excluded on both |
| Colours | 168 of 168 values (84 tokens × 2 themes) identical: same space, same six-decimal components |
| Elevations | 28 of 28 (14 × 2 themes, 53 shadow layers) identical |
| Scales and roles | Spacing, corners, strokes, focus, icon sizes, density, motion and the 9 typography roles agree (spot-checked: `corner.surface` 10.08, `typography.label-compact` 12.8, `density.control-height.default` 32, `motion.duration.normal` 200 ms) |
| Against the web's own converter | 168 of 168 colour inputs give the same 8-bit sRGB as culori 4.0.2, the converter qeetrix-ui's contrast gate uses |

The comparisons were run on 2026-10-18 by parsing qeetrix-ios's generated Swift. They are not automated
across repositories: Android commits its values as `Documentation/Foundations/token-values.json`
(every generated token, the CSS expression it came from and its resolved value), which iOS can diff
against if it emits the same file.

## Verified component parity (Phase 2, against qeetrix-ios's working tree, 2026-10-18)

| What | Result |
|:--|:--|
| Upstream inputs | `tokens.json`, `tokens.css`, `index.css`, `component-manifest.json` and the 62 shared icon SVGs sha256-identical |
| Component-token groups | The same eight adopted: avatar, badge, button, card, input, progress, switch, typography |
| Token scope | 279 generated, 604 excluded on both |
| Component tokens | 62 of 62 identical in kind and value: dimensions, fractions, `em`, density functions, elevation references, and per-theme colour references or values (six decimals) |
| Components | The same eleven, with the same names (`QeetrixButton` … `QeetrixTextField`), the web's variant and size names, and the same built-in strings ("Loading", "(optional)") |

Compared by parsing qeetrix-ios's generated `QeetrixComponentTokens.swift` against Android's
`token-values.json`, which records each component token's reference and resolved value. Behaviour was
compared through each platform's component documentation, not by running both side by side.

## Verified component parity (Phase 3, against qeetrix-ios's working tree, 2026-10-19)

| What | Result |
|:--|:--|
| Upstream inputs | `tokens.json`, `tokens.css`, `index.css` and `component-manifest.json` sha256-identical; the four icons Phase 3 adds (`chevron-down`, `eye-off`, `minus`, `plus`) identical in every style both pin |
| Components | The same form set and names: `QeetrixField`, `QeetrixFieldSet`, `QeetrixTextarea`, `QeetrixPasswordInput`, `QeetrixOTPInput`, `QeetrixCheckbox`, `QeetrixRadioGroup`, `QeetrixSelect`, `QeetrixNumberField`, `QeetrixFormActions`, `QeetrixFormErrorSummary`, and `QeetrixOption` for typed choices |
| Decisions | Android's [0014](Decisions/0014-form-components.md) takes qeetrix-ios's 0012 positions: one field, native controls, typed options, the app validates, exact numbers, secrets stay in the caller's state, composition only where it adds behaviour, full-height selection rows |
| Component tokens | No new groups: the inputs draw from `component.input.*` and semantic tokens on both. The select list's `bg-popover` resolves to `surface.overlay`, the value `component.popover.*` references |

Behaviour was compared through qeetrix-ios's DocC component pages and ADR 0012, not side by side on
devices.

## Verified component parity (Phase 4, against qeetrix-ios's working tree, 2026-10-19)

| What | Result |
|:--|:--|
| Upstream inputs | The four `@qeetrix/ui` files sha256-identical; the eight icons both added for Phase 4 (`circle-x`, `copy`, `ellipsis`, `house`, `pencil`, `settings`, `share-2`, `trash`) identical in every style both pin |
| Components | The same purposes and names where both have the component: `QeetrixTabs`, `QeetrixAlertDialog`, `QeetrixConfirmationDialog`, `QeetrixMenu` / `QeetrixMenuItem`, and the dialog action with roles. Android's sheet is `QeetrixBottomSheet` (iOS `qeetrixSheet`), its transient feedback `QeetrixSnackbar` (iOS `QeetrixToast`) |
| Decisions | Android's [0015](Decisions/0015-navigation-overlays-and-feedback.md) takes qeetrix-ios's 0014 positions: native presentation, the app owns all state, dialogs as typed actions that run once with Cancel added to a destructive choice, a host per screen and one transient message at a time with the web's timing, no router, modal manager or singleton |
| Component tokens | Both adopt `dialog`. Android also adopts `menu` and `sidebar`, because it draws the menu surface and the rail; iOS adopts `popover` for its popover, which Android defers |

Compared through qeetrix-ios's DocC pages and ADR 0014, not side by side on devices.

## Verified component parity (Phase 5, against qeetrix-ios's working tree, 2026-10-19)

| What | Result |
|:--|:--|
| Upstream inputs | The four `@qeetrix/ui` files byte-identical; the eight icons both added for Phase 5 (`calendar`, `clock`, `file-text`, `folder`, `mail`, `refresh-cw`, `search-x`, `users`) byte-identical in every style both pin (`refresh-cw` has no filled style upstream) |
| Components | The same names and purposes: `QeetrixListItem`, `QeetrixListItemIcon`, `QeetrixSectionHeader`, `QeetrixDescriptionList` / `QeetrixDescriptionItem` and `QeetrixDescriptionListLayout`, `QeetrixAccordion` / `QeetrixAccordionItem`, `QeetrixEmptyState` with the same variants and sizes, `QeetrixDataState` with `QeetrixDataFailure`, `QeetrixDataLoadingRows`, `QeetrixSkeleton` / `QeetrixSkeletonText` with the same shapes, `QeetrixChip` / `QeetrixChipGroup` with the same sizes, `QeetrixTimeline` / `QeetrixTimelineItem` with the same tones and emphases |
| Strings | The same words: "Expanded", "Collapsed", "Try again", "Something went wrong while loading this data."; the remove button is "Remove" on iOS (with the chip's title as its value) and "Remove Design" on Android |
| Decisions | Android's [0016](Decisions/0016-data-display-and-content-states.md) takes qeetrix-ios's 0015 positions: the app owns data, identity, selection, expansion, the query and the load state; native collections are not wrapped; web names and metrics where the web has the component; one native-only list family; the web's data-state order with a message written for the person and an optional retry; the data table and image presentation deferred |
| Component tokens | Both adopt `accordion`, `skeleton` and `timeline` |

Compared through qeetrix-ios's sources and ADR 0015, not side by side on devices.

## Parity re-checked (Phase 6, against qeetrix-ios `10b365f` and its working tree, 2026-10-20)

| What | Result |
|:--|:--|
| Upstream inputs | The four `@qeetrix/ui` files sha256-identical; of 133 icon files both pin, 132 identical — the 133rd is `icons.json`, whose 37 shared entries are identical and which lists Android's two chrome icons (`arrow-left`, `ellipsis-vertical`, below) |
| Tokens | No value changed in Phase 6; `Scripts/verify-tokens` and `TokenParityTest` pass against the pinned files |
| Behaviour Android changed | Short labels take their paragraph direction from their text in right-to-left layouts, and a field without a visible label reserves a 48 dp touch height. Both are Android fixes for defects found on Android ([localization](Quality/LOCALIZATION-RTL-REPORT.md) L2, [accessibility](Quality/ACCESSIBILITY-REPORT.md) A1); iOS's behaviour in the same cases was not assessed |

No token difference without a recorded reason: every Android-only token group and icon is in the deliberate
differences below.

## Release alignment (Phase 7, against qeetrix-ios `10b365f` and its working tree, 2026-10-20)

| What | Android | iOS | Aligned |
|:--|:--|:--|:--|
| First candidate | `0.1.0-rc.1`, not published | `0.1.0-rc.1`, not tagged | Yes |
| Versioning | SemVer; before 1.0 a breaking change only in a `0.MINOR` release, with migration notes; 1.0 once a Qeet Group app has shipped with it ([RELEASE-POLICY.md](Releases/RELEASE-POLICY.md)) | The same, in its `RELEASE_POLICY.md` | Yes |
| Distribution | A Maven artifact (AAR, POM, module metadata, sources), `com.qeetgroup:qeetrix` | A Swift package from source, by Git tag | Native to each platform |
| Minimum OS | Android 10 (API 29) | iOS 17 | Each platform's own decision |
| Bundled fonts | Cal Sans 2.011 and Fira Code 6.2 variable TTFs, sha256 `9e1ae3cb…` and `04149681…` | The same two files, the same hashes | Byte-identical |
| Upstream token inputs | The four `@qeetrix/ui` 2.1.6 files | The same | Byte-identical (re-checked) |
| Code licence | None chosen; the owner's decision | None chosen | Yes |

No contradiction in tokens, typography or design contracts was found. Each library's release still needs its own
manual accessibility pass on a device.

## Names

- A token keeps its upstream path. The Kotlin name is the path without its group, in camelCase for
  properties (`color.text.on-brand` → `QeetrixTheme.colors.textOnBrand`) and PascalCase for constants
  and enum entries (`elevation.ramp.2xs` → `QeetrixElevation.Ramp2xs`). iOS uses the same words in
  lowerCamelCase (`QeetrixColor.textOnBrand`).
- A leading number moves to the end on both platforms (`space.2xl` → `Xl2` / `xl2`), and `0` becomes
  `Zero` / `zero`.
- Theme modes, typography roles and icon names keep their upstream spelling as values (`system`,
  `label-compact`, `send`), so stored preferences and analytics read the same on every platform.

## Units and values

| Web | Android | iOS |
|:--|:--|:--|
| `1px` | 1 dp (`Dp`) | 1 pt |
| `1rem` | 16 dp — qeetrix-ui sets no root font size, so browsers use 16 px | 16 pt |
| font size `px`/`rem` | sp (`TextUnit`), following the font scale | pt, following Dynamic Type |
| `em` letter-spacing | `em` (`TextUnit`) | multiplied by the scaled point size |
| unitless line-height | `em` with `LineHeightStyle(Center, Trim.None)` — CSS half-leading | `lineSpacing` (cannot shrink below the font's natural height) |
| `ms` | `Int` milliseconds | seconds |
| `oklch()` / `color-mix(in oklab, …)` | sRGB 8-bit when inside sRGB, else Display P3 (CSS Color 4) | sRGB or Display P3, six decimals |
| `cubic-bezier()` / `linear` | `CubicBezierEasing` / `LinearEasing` | `timingCurve` / `.linear` |
| `box-shadow` | `Modifier.qeetrixElevation`: offset, spread, blur with σ = blur ÷ 2, outer layers outside the shape, inset inside | `.qeetrixElevation`, the same model |
| `font-weight` / `GEOM` / `font-optical-sizing: auto` | `wght` / `GEOM` / `opsz` = role size in sp × font scale | the same axes; `opsz` = scaled point size |
| `.dark` / nested `.dark` | `QeetrixTheme(QeetrixThemeMode.Dark)`, nestable | `.qeetrixThemeMode(.dark)` / `.qeetrixTheme(.dark)` |
| `prefers-reduced-motion` → `motion.reduced.*` | Remove animations (animator scale 0) → `motion.reduced.*` | Reduce Motion → `motion.reduced.*` |

## Deliberate differences

Each follows a platform convention; none restyles anything.

- **Touch targets.** 48 dp on Android, 44 pt on iOS (each platform's guidance).
- **Bold Text.** Android raises the requested weight by the platform's adjustment (typically +300,
  capped by the font's 700 maximum); iOS raises a role one weight step.
- **Icon scaling.** Android icons are dp and do not grow with the font scale (Android convention); iOS
  icons scale with Dynamic Type like SF Symbols.
- **Line height.** Android reproduces CSS half-leading exactly; iOS cannot go below the font's natural
  line height.
- **Icon geometry.** Android emits circles and rounded corners as cubic Béziers (Compose paths have no
  ellipse); iOS keeps native ellipses. The difference is under 0.03% of the radius.
- **Wide colour.** Display P3 tokens render in P3 on Android only in a window with
  `colorMode="wideColorGamut"`; iOS views are P3-capable by default.
- **Not mapped on either:** z-index tokens, gradients, logos, and the 33 component-token groups no
  adopted component uses yet.

### Components (Phase 2)

Where a component differs, it is because the platforms do; the look, tokens and states are the same.

| Area | Android | iOS |
|:--|:--|:--|
| Touch target of small controls | 48 dp reserved in layout (Compose's `clickable` cannot take input outside its bounds) | Hit area extended to 44 pt without moving layout |
| Press feedback | The web's colour and elevation steps, no ripple; hover appearances on press where the web relies on hover (link underline, card boundary) | The same approach |
| Keyboard focus | The Qeet focus ring, as the web | The system's focus indicator |
| Heading sizes | The web's `sm` breakpoint as a 640 dp window width | The horizontal size class |
| Text field text size | 16 sp below a 768 dp window, 14 sp above, as the web | 16 pt everywhere |
| Text field "required" | "required" read with the label, as the asterisk's description (since Phase 3) | "Required" added to the field's label |
| Field messages | Live regions: assertive for errors, polite otherwise (the web's `alert` / `status`) | Posted VoiceOver announcements |
| Text colour | Inherits the surrounding content colour, as the web | The primary text colour |
| Avatar initials | First grapheme cluster per word (the web takes the first code point) | The same (Swift `Character`s are grapheme clusters) |
| Loading semantics | State description "Loading" | Accessibility value "Loading" |
| Separator | Hidden from TalkBack (no separator role) | Hidden from VoiceOver |
| Labelled switch | Label at the start, switch at the end | The same |

Not implemented on Android in Phase 2: the Button's `invalid` (`aria-invalid`) styling.

### Components (Phase 3)

| Area | Android | iOS |
|:--|:--|:--|
| Field association | The label is drawn inside the control (a text field's decorator); `Modifier.qeetrixFieldControl()` names any other control | The field passes its parts through the environment; the control carries them |
| Field description | Text after the control, read in order | The control's accessibility hint, and text in order |
| Field messages | Error state on the control plus a live region (assertive for errors) | Custom content on the control at high importance, plus an announcement |
| Select | Qeetrix's own list in a Compose popup, opening below (above when it does not fit), focusing the selection from a keyboard | The system menu |
| Checkbox | Checkbox role, "partially checked" for mixed | No checkbox role: VoiceOver reads a toggle |
| Number field steppers | Buttons, plus Up/Down arrow keys and Decrease/Increase accessibility actions on the field | Buttons; no arrow keys |
| AutoFill | `ContentType` (`Password`, `NewPassword`, `SmsOtpCode`, and the `contentType` parameter) | `textContentType` (`.password`, `.newPassword`, `.oneTimeCode`, …) |
| Password reveal | Same field, obscuring switched off; the visible-password keyboard while revealed | `SecureField` swapped for a `TextField` on the same binding |
| Secret handling | No copies, no saved text; the platform exposes the field to accessibility services as a password | No copies; `privacySensitive()` |
| Selection rows and error-summary links | 48 dp | 44 pt |

The same on both, against the web: the OTP cells keep left-to-right order in right-to-left layouts, use one
caret, and offer `Alphanumeric` as a native addition; a revealed password is not re-masked on submit (no
form history); the textarea grows with its text instead of a drag handle; the number field has no pointer
scrubbing; each radio option is its own keyboard focus stop rather than one stop with arrow keys.

### Components (Phase 4)

| Area | Android | iOS |
|:--|:--|:--|
| Top-level navigation | `QeetrixNavigationBar` / `QeetrixNavigationRail` drawn by Qeetrix, switched by `QeetrixNavigationScaffold` at 600 dp of window width | The system `TabView`, styled with `qeetrixTabBar()` / `qeetrixTabItem`; iPad's sidebar adaptation is the system's |
| Screen header | `QeetrixTopAppBar` drawn by Qeetrix, with `QeetrixBackButton` | The system navigation bar, styled with `qeetrixNavigationBar(background:)` |
| Alert and confirmation | Qeetrix's surface in Compose's dialog window; the confirmation is the alert with two fixed roles | The system alert and the system confirmation dialog (action sheet), in the system's style |
| Custom dialog | `QeetrixDialog`, centred on every window size | Deferred: a sheet (a centred form sheet on iPad) |
| Sheet | `QeetrixBottomSheet` (Material's sheet underneath, Qeetrix's surface); predictive back | `qeetrixSheet` with detents, the system sheet |
| Menu | Qeetrix's surface in a popup (`component.menu`) | The system menu |
| Popover | Deferred: menu, dialog or bottom sheet | `qeetrixPopover`, a sheet on iPhone by default |
| Transient feedback | `QeetrixSnackbar`; while TalkBack is on, an action has no time limit | `QeetrixToast`; while VoiceOver is on, the same |
| Back | Platform Back and Escape close the topmost overlay; Qeetrix registers no Back handler | No Back key; swipe and Escape |
| Icons in chrome | `arrow-left` (mirrored for Back) and `ellipsis-vertical` (Android's overflow), which iOS does not pin | `ellipsis` for menu triggers; system chevrons for Back |

The same on both, against the web: one transient message at a time rather than the web's stack; timing 5 s,
or 10 s for warnings, errors and actions; a destructive dialog always offers Cancel; actions run once;
tabs are each their own keyboard focus stop.

### Components (Phase 5)

| Area | Android | iOS |
|:--|:--|:--|
| Collections | Compose's `LazyColumn` with the app's `key`s; sections are `stickyHeader`s holding `QeetrixSectionHeader` | SwiftUI's `List` and `Section`, with `ForEach` identity |
| Rows | `QeetrixListItem` is clickable itself (`onClick`, `onLongClick`); a long press starts a selection | The row sits in a `NavigationLink` or `Button` with `qeetrixListRow()`; swipe actions and context menus are the system's |
| Accordion | Qeetrix's item, state hoisted (`expanded`, `onExpandedChange`); "Expanded"/"Collapsed" as the state description plus expand and collapse actions | Native `DisclosureGroup`s in `QeetrixDisclosureGroupStyle`; items can keep their own state |
| Search | `QeetrixSearchField`, on the shared field parts | The system's `searchable` |
| Chips' semantics | Checkboxes, and radio buttons in a single-choice group, as Material's filter chips are | Buttons with the selected trait |
| Chip options | Labels only: an option's description is neither shown nor read | An option's description is the chip's hint |
| Large text | From 150 % font scale, row trailing content and timeline times move below the text | At the accessibility sizes, the same |
| Reading order | Placement order (title, description, metadata) | Sort priority, the same order |
| Pull to refresh | Material 3's `PullToRefreshBox`, not wrapped | `refreshable` |

The same on both, against the web: the data state never shows an error object's message and offers "Try
again"; rows and section headers exist on both (the web has none); the data table, image presentation,
feeds, trees, filter bars, pagination and stats are deferred; component tokens built on `control.*`
resolve at the default density, so the accordion's padding and the timeline's gaps don't follow a compact
density.

## Decisions that need both platforms

1. **A shared resolved-values file.** Android publishes `token-values.json`; iOS could emit the same
   shape so parity is checked by a diff instead of by parsing generated code.
2. **Pins.** Android's pins match qeetrix-ios's through Phase 5 apart from what each platform draws:
   Android adds `arrow-left` and `ellipsis-vertical` and the `menu` and `sidebar` token groups; iOS adds
   `popover`. Both now carry `calendar`, `clock`, `file-text`, `folder`, `mail`, `refresh-cw`, `search-x`
   and `users`, and the `accordion`, `skeleton` and `timeline` groups. A shared rule for which pins both
   carry would keep the lists from drifting.
3. **Logos.** The Qeet logo's SVG uses a `<mask>`, which neither native vector format supports. Both
   platforms defer logos until qeetrix-icons provides a mask-free drawing or both agree on a conversion.
4. **Increased contrast.** No upstream values; both platforms resolve standard values today.
5. **Component folders.** Both organise components by qeetrix-ui family (the manifest's `category`), not by
   ui.qeet.in's navigation categories.
6. **API baselines.** Both check their public API against a committed baseline
   ([0013](Decisions/0013-public-api-baseline.md)).
7. **Screenshot baselines.** Android keeps its baselines local and out of git, so its CI does not run
   visual regression ([0012](Decisions/0012-visual-regression-testing.md)). If the two platforms want
   comparable visual evidence, they need a shared decision on where baselines live.

## Proposed upstream changes (not made)

These would help every native platform. They are proposals for the qeetrix-ui and qeetrix-icons
maintainers; nothing upstream was changed.

- **qeetrix-ui:** the semantic `corner.*` roles reference Tailwind's `--radius-*` variables, which are
  declared in `index.css` (a web stylesheet), not in the token build. Expressing the corner roles in
  DTCG (`{corner.base}` and a multiplier) would let native generators read tokens only.
- **qeetrix-ui:** `tokens.json` keeps `var()` and `color-mix()` expressions, so every platform resolves
  them again. A fully resolved, typed export (for example the shape of `token-values.json`) would remove
  that duplicated logic.
- **qeetrix-icons:** a mask-free version of the Qeet logo (the bowl and its hole as one even-odd path)
  for native vector formats.
- **qeetrix-files:** the PRD lists native SwiftUI and Compose as a non-goal (in the sense of generating
  them from React). The native libraries are now being built by hand; an accepted decision at the
  organisation level would remove the contradiction.
