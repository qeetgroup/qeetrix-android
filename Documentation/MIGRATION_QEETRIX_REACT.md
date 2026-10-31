# Migration to `@qeetrix/react`, `@qeetrix/tokens` and `@qeetrix/icons` 2

Qeetrix for Android moved from `@qeetrix/ui` 2.1.6 to the new Qeetrix web library: **`@qeetrix/react` 0.4.0**, built from
**`@qeetrix/tokens` 0.0.13** and **`@qeetrix/icons` 2.1.0**, following the style guidelines in qeetrix-files
`qeetrix-react/` (`05-design-rules.md`, `06-component-specs.md`, `07-components.md`). The decision is
[0019](Decisions/0019-migrate-to-qeetrix-react-tokens-and-icons.md). qeetrix-ios writes the same file; the two are compared
side by side.

Nothing had been published, so every change below is breaking and none has a deprecation path.

## 1. Pins

| Input | Before | After |
|:--|:--|:--|
| Tokens | `@qeetrix/ui` 2.1.6 `tokens.json`, `tokens.css`, `index.css` | `@qeetrix/tokens` 0.0.13 `dist/dtcg/` (resolver, sources, resolved light and dark), git `4483f96` |
| Component specification | `@qeetrix/ui` 2.1.6 `component-manifest.json` | `@qeetrix/react` 0.4.0 `dist/manifest.json` (89 families) and the compiled CSS of the 42 families Android maps, git `9a765bb` |
| Typeface | calcom/sans v2.011 and Fira Code 6.2 release TTFs, accepted through their WOFF2 | `@qeetrix/tokens` 0.0.13 `fonts/QeetSans.ttf` and `fonts/FiraCode-VF.ttf`, checked against `fonts/native.json` (QTRX-91) |
| Icons | qeetrix-icons commit `331cd85` (`@qeetrix/icons` 1.0.11) SVGs and `icon-manifest.json`, 39 icons | `@qeetrix/icons` 2.1.0 npm tarball (git `e2a8f9f`), its SVG files and `dist/manifest.json` (QTRX-188), 41 icons |

The migration was done on `@qeetrix/react` 0.2.2 and `@qeetrix/tokens` 0.0.11, then bumped to 0.3.0 and 0.0.12 once QTRX-91
and QTRX-92 shipped. Between those versions no DTCG file and no mapped family's CSS changed; 0.0.12 only adds the native fonts
(byte-identical to what the interim WOFF2 conversion produced: `668e52b0…`, `6017720c…`) and 0.3.0 adds the JSON manifest,
element-typed `icon` props (no Android equivalent: icons are `QeetrixIconName`s) and Slider's `getAriaValueText` (Android had
no Slider).

**The 0.4.0 / 0.0.13 / 2.1.0 bump** (30 October) took the fixes for Android's §7 findings (QTRX-157 to 189). What changed:

- **Tokens 0.0.13:** dark `border.subtle` graphite.800 (`#2E2E2D`) and `border.default` graphite.750 (`#383837`); dark
  `bg.brand.bold-hover` the new `qeet.650` (`#C5430A`); every line height a length on the 4 px grid
  (`$extensions["in.qeet.qeetrix"].lineHeight`) with the full-precision ratio, so `body.md` is 14 / 20 exactly; each role names
  its letter style (`$extensions["in.qeet.qeetrix"].fontGeometry`); `component.timeline.marker-size` 28px. 202 tokens
  generated, now 137 excluded (one more primitive). The generator reads both extensions, fails if a role's ratio and length
  disagree or its letter style is not a `font.geometry.*` token, and emits each ratio as the `Float` whose product with the
  size is the length exactly.
- **Icons 2.1.0:** the sync copies `dist/svg/<shape>-<variant>/<id>.svg` and reads `dist/manifest.json`; the JavaScript reader
  is gone. All 138 drawings are byte-identical to what it rebuilt from 2.0.0's modules. The mask-free Qeet logos
  (`dist/svg/logos/qeet/*.mask-free.svg`) are not taken: Android draws no logo yet.
- **React 0.4.0:** 14 of the 42 mapped families' CSS changed (accordion, avatar, button, card, checkbox, avatar-item,
  number-field, otp-input, input, menu, progress, radio, select, switch); §7 says what each changed on Android. Avatar's
  `status`, Chip and ChipGroup are new in 0.4.0 and not adopted here.

## 2. Tokens

The generator now reads DTCG and resolves it as `qeetrix.resolver.json` says (foundation set, then `colorScheme`, then
`density`), checks the published `light.tokens.json` and `dark.tokens.json` against that resolution, and converts colours from
their OKLCH components per CSS Color 4. **202 tokens generated, 136 excluded** (121 primitives, 10 z-index steps, the Indic font
stack, 4 unadopted component tokens). Every generated token, its Kotlin name, source file, authored value, references and
resolved value per colour scheme and density are in [TOKEN_COVERAGE.md](Foundations/TOKEN_COVERAGE.md) and
[token-values.json](Foundations/token-values.json).

### Naming rule

| Token | Kotlin | Example |
|:--|:--|:--|
| `color.*` | `QeetrixTheme.colors.<camelCase of the path after color.>` | `color.bg.brand.bold` → `bgBrandBold` |
| `shadow.*` | `QeetrixShadow.<Pascal>` (layers per scheme) | `shadow.raised-hover` → `RaisedHover` |
| `typography.<role>.<size>` | `QeetrixTextRole.<Pascal>`, `QeetrixTheme.typography.<camel>` | `typography.label.caps` → `LabelCaps` / `labelCaps` |
| tokens the density modifier changes | `QeetrixTheme.density.<camelCase of the whole path>` | `size.control.md` → `sizeControlMd`, `space.form.gap` → `spaceFormGap` |
| everything else | `Qeetrix<Group>.<Pascal of the rest>` | `space.inset.md` → `QeetrixSpace.InsetMd`, `size.icon.2xs` → `QeetrixSize.Icon2xs`, `motion.spring.snappy.*` → `QeetrixMotion.SpringSnappy` |
| `icon.stroke.*`, `font.geometry.*` | two-segment groups | `QeetrixIconStroke.Thin`, `QeetrixFontGeometry.Display` |
| `font.family.*` | internal: the bundled font each role draws with | — |
| adopted `component.<group>.*` | internal `Qeetrix<Group>ComponentTokens.<Pascal>` | `component.sidebar.width.expanded` → `WidthExpanded` |

### Old → new, by group

| `@qeetrix/ui` 2.1.6 | Kotlin before | `@qeetrix/tokens` 0.0.12 | Kotlin now |
|:--|:--|:--|:--|
| `color.text.primary` / `secondary` / `tertiary` / `placeholder` | `textPrimary` / `textSecondary` / `textTertiary` / `textPlaceholder` | `color.text.default` / `subtle` / `subtlest` / `subtlest` | `textDefault` / `textSubtle` / `textSubtlest` |
| `color.text.on-feedback`, `on-feedback-strong`, `on-warning-strong` | `textOnFeedback`… | `color.text.on-bold`, `on-warning` | `textOnBold`, `textOnWarning` |
| `color.text.on-subtle`, `brand-display` | `textOnSubtle`, `textBrandDisplay` | `color.text.brand` (on a brand tint), `color.icon.brand` | `textBrand`, `iconBrand` |
| `color.surface.elevated` | `surfaceElevated` | `color.surface.raised` | `surfaceRaised` |
| `color.surface.subtle`, `interactive(-hover/-active)` | `surfaceSubtle`, `surfaceInteractive…` | `color.bg.neutral.subtle(-hover/-pressed)` | `bgNeutralSubtle(Hover/Pressed)` |
| `color.surface.brand-subtle(-hover/-active)` | `surfaceBrandSubtle…` | `color.bg.brand.subtle(-hover/-pressed)`, `color.bg.selected(-hover)` | `bgBrandSubtle…`, `bgSelected…` |
| `color.surface.rail` | `surfaceRail` | — (navigation draws on `surface.default`) | — |
| `color.action.primary(-hover/-active)` | `actionPrimary…` | `color.bg.brand.bold(-hover/-pressed)` | `bgBrandBold…` |
| `color.feedback.<s>`, `<s>-subtle`, `<s>-strong` (`error` for danger) | `feedbackError…` | `color.icon.<s>` / `border.<s>`, `bg.<s>.subtle`, `bg.<s>.bold` (`danger`) | `iconDanger`, `bgDangerSubtle`, `bgDangerBold`… |
| `color.border.control`, `control-hover`, `hover`, `focused` | `borderControl…`, `borderFocused` | `color.border.strong`, `strong-hover`, `border.focus` | `borderStrong`, `borderStrongHover`, `borderFocus` |
| `color.focus.ring` | `focusRing` | `color.border.focus` | `borderFocus` |
| `color.selection.background` | `selectionBackground` | `color.selection` | `selection` |
| `color.rating.filled` | `ratingFilled` | `color.icon.highlight` | `iconHighlight` |
| `color.syntax.*` (`key`) | `syntaxKey`… | `color.code.*` (`keyword`) | `codeKeyword`… |
| `color.data.*` | `dataCategorical1`… | `color.dataviz.*` (adds sequential and diverging) | `datavizCategorical1`… |
| — | — | new: `bg.disabled`, `bg.input`, `text.on-brand`, `icon.*` (12), `overlay.hover`/`pressed`, `border.disabled`/`selected` | see TOKEN_COVERAGE.md |
| `space.xxs`…`5xl` | `QeetrixSpacing.Xxs`…`Xl5` | `space.inset.*`, `space.gap.*`, `space.layout.*`, `space.form.gap` | `QeetrixSpace.InsetXs…`, `GapXs…`, `LayoutSm…`; `QeetrixDensity.spaceFormGap` |
| `corner.control`/`field`/`chip`/`surface`/`overlay`/`pill`… (8–16 px) | `QeetrixCorner.*`, `QeetrixShapes.*` | `radius.none`/`sm`/`tooltip`/`control`/`surface`/`overlay`/`modal`/`chip`/`full` (0, 2, 4 px, pill) | `QeetrixRadius.*`, `QeetrixShapes.*` (+ `QeetrixShapes.Field`, top corners only) |
| `stroke.width.hairline`/`thin`/`thick` | `QeetrixStroke.*` | `border.width.default`/`strong`/`accent` (1, 2, 3) | `QeetrixBorder.WidthDefault/Strong/Accent` |
| `focus.ring-width`, `outline-width`, `offset`, `ring-style` | `QeetrixFocus.RingWidth/OutlineWidth/Offset` | `focus.ring.width`, `focus.ring.offset` | `QeetrixFocus.RingWidth/RingOffset` |
| `state.opacity.disabled` | `QeetrixOpacity.Disabled` | `opacity.disabled` | `QeetrixOpacity.Disabled` (only for content with no disabled colour) |
| `icon.size.xs`…`lg` | `QeetrixIconSize.*` | `size.icon.2xs`…`lg` (12–24) | `QeetrixSize.Icon2xs…IconLg` |
| — | — | `size.target.min` | `QeetrixSize.TargetMin` |
| `density.control-height/row-height/cell-padding-y/field-gap.*` | `QeetrixDensity.controlHeight`… | `size.control.sm/md/lg`, `size.row`, `space.form.gap` per density context | `QeetrixDensity.sizeControlSm/Md/Lg`, `sizeRow`, `spaceFormGap`, `iconStroke` |
| `font.weight.*` | `QeetrixFontWeight.*` | — (weights are the roles'; `primitive.font.weight.*` is private) | removed |
| `font.geometry.a11y/ui/base/geo` | `QeetrixFontGeometry.A11y/Ui/Base/Geo` | `font.geometry.body` (0) / `heading` (50) / `display` (100) | `QeetrixFontGeometry.Body/Heading/Display` |
| `typography.display/heading/title/body/label/label-compact/caption/micro/code` (9 roles) | `QeetrixTextRole.*`, `typography.body`… | `typography.{display,heading,title,body,label}.{lg,md,sm}`, `label.caps`, `code.{md,sm}` (18) | `QeetrixTextRole.BodyMd`…, `typography.bodyMd`… |
| `motion.duration.instant/fast/normal/slow/deliberate`, `motion.easing.*`, `motion.reduced.*` | `QeetrixDuration.*`, `QeetrixEasing.*`, `QeetrixMotion.Reduced*` | `motion.duration.instant/fast/normal/slow/slower`, `motion.easing.*`, `motion.spring.snappy/gentle.*` | `QeetrixMotion.Duration*`, `Easing*`, `SpringSnappy/SpringGentle`; reduced motion makes every duration 0 |
| `elevation.flat/inset/raised/hover/overlay/modal/subtle/low/ramp.*` (14) | `QeetrixElevation.*`, `Modifier.qeetrixElevation` | `shadow.raised/raised-hover/overlay/modal/inset` (5) | `QeetrixShadow.*`, `Modifier.qeetrixShadow` |
| — | — | `breakpoint.medium/expanded/large/xlarge` | `QeetrixBreakpoint.*` |
| `component.<14 groups>` (button, input, card…) | internal `Qeetrix<Group>ComponentTokens` | `component.sidebar`, `component.timeline` adopted (command-palette, tour not) | internal `QeetrixSidebarComponentTokens`, `QeetrixTimelineComponentTokens`; every other component value comes from the react CSS |
| `z.*`, gradients | excluded | `z.*` | excluded |

## 3. Typeface

| | Before | After |
|:--|:--|:--|
| Sans | `res/font/qx_cal_sans_vf.ttf` (calcom/sans v2.011), `CalSans-OFL.txt` | `res/font/qx_qeet_sans_vf.ttf` (`@qeetrix/tokens` `fonts/QeetSans.ttf`, sha256 `668e52b0…`, from `QeetSans.woff2` `8362505b…`), `QeetSans-OFL.txt` |
| Mono | `qx_fira_code_vf.ttf` (Fira Code 6.2 release, `04149681…`) | `qx_fira_code_vf.ttf` (`fonts/FiraCode-VF.ttf`, `6017720c…`, from `FiraCode-VF.woff2` `408e876a…`) |
| Axes | opsz, GEOM, wght, YTAS, SHRP, ital / wght | the same, every one kept |
| Letter style per role | `typography.<role>.font-geometry` (ui 25 for body) | display `font.geometry.display` 100, heading/title `heading` 50, body/label/code `body` 0 — the web's `font-variation-settings: "GEOM" var(--qx-font-geometry-*)`; `opsz` = rendered size |

The same faces as before (the web's WOFF2 never changed); only the source and the TTF bytes did.

## 4. Icons

- **Renames in 2.0:** none of the pinned names. 2.0 lists Lucide's former names as aliases (`alert-circle` → `circle-alert`,
  `home` → `house`, `trash-2` → `trash`, `more-horizontal` → `ellipsis`, …); the sync names the new one if a pin uses an alias.
- **Added:** `arrow-big-up` (password Caps Lock), `arrow-right`, `chevron-left`, `chevron-right` (button icons, mirrored),
  `circle-question-mark` (field help), `menu`.
- **Removed (no longer drawn by any component or the sample):** `calendar`, `clock`, `database`, `loader-circle` (the spinner is
  now a ring, as the web's).
- **Defaults:** `QeetrixIcon` draws the **sharp** outline by default (design rules §16); navigation passes the round shape and
  the filled variant for the current item.
- **Mirroring:** the manifest's `mirror` icons (`send`) only; arrows and chevrons are `preserve` and the call site decides
  (`QeetrixIcon(mirrorInRightToLeft)`, Button's `iconMirrorsInRightToLeft`; QTRX-200). 59 icons since the Android tickets.
- **Source:** 2.1.0's plain SVG files and JSON catalogue (QTRX-188). 2.0.0 had only React modules, which the sync parsed.

## 5. Components

Variant, tone, size and parameter names follow `@qeetrix/react` in Kotlin idiom (enums with react's string as `value`;
`enabled` for react's `disabled`; `value`/`onValueChange`, `checked`/`onCheckedChange`, `open`/`onOpenChange`). Metrics come
from react's CSS with the declaration beside each literal. Composable names, files and packages are unchanged. Every row is
**done**; "partial" names what is not ported.

| Component | `@qeetrix/react` | Old API | New API | Status |
|:--|:--|:--|:--|:--|
| QeetrixButton | Button | `variant` Default/Outline/Secondary/Ghost/Destructive/Link, `size` Default/Xs/Sm/Lg, `leadingIcon`, `trailingIcon`, `loadingLabel`, content overload | `variant` Solid/Soft/Ghost, `tone` Brand/Danger/Neutral, `size` Sm/Md/Lg, trailing `icon` in a ruled zone, `loading`, `fullWidth`, `enabled` | Done (no LinkButton, `render`) |
| QeetrixIconButton | IconButton | `QeetrixIconButtonSize` Default/Xs/Sm/Lg, `variant` | `variant` (default Ghost), `tone`, `size: QeetrixButtonSize` | Partial (no tooltip) |
| QeetrixText | Text | `variant` H1–H4/P/Blockquote/Lead/Large/Small/Muted/InlineCode, `color` | `size` Sm/Md/Lg, `weight` Regular/Medium/Semibold, `tone: QeetrixTextTone?`, `mono`, `textAlign`, `maxLines` (ellipsis = `truncate`) | Done |
| QeetrixHeading (new) | Heading | (was `QeetrixText` H1–H4) | `text`, `level` 1–6, `size: QeetrixHeadingSize` DisplayLg…TitleSm, `tone` | Done (no levels in TalkBack) |
| QeetrixSpinner | Spinner | `size` Sm/Default/Lg/Xl (16–32), loader-circle icon | `size` Sm/Md/Lg/Xl (14/16/20/24), `tone` Neutral/Brand, `decorative`, 2 dp ring | Done |
| QeetrixSeparator | Separator | `variant` Default/Muted | `tone` Subtle/Default/Strong | Done |
| QeetrixBadge | Badge | `variant` Default/Secondary/Outline/Brand/Info/Success/Warning/Destructive/Muted | `tone` Neutral/Brand/Info/Success/Warning/Danger, `variant` Subtle/Solid, `size` Sm/Md, `icon` | Done |
| QeetrixField | Field | `label?`, `message: QeetrixFieldMessage`, `required` (asterisk), `optional` | `label` (required), `description`, `error`, `invalid`, `optional`, `required` (spoken only), `hideLabel`, `help`, `enabled` | Done |
| QeetrixFieldSet | Fieldset | `legend`, `legendStyle`, `message`, `required`, `optional` | `legend`, `description`, `enabled` | Done |
| QeetrixFieldMessage, …Tone, QeetrixFieldSetLegend | — | public | removed (errors only, as the web) | Done |
| QeetrixTextField | TextField | `label?`, `message`, `leading`/`trailing` | `state, label`, `placeholder`, `description`, `error`, `invalid`, `optional`, `required`, `hideLabel`, `help`, `enabled`, `readOnly`, `icon`, `startAdornment`, `endAdornment` | Done |
| QeetrixTextarea | Textarea | `minLines`/`maxLines` | `rows` (3), `maxRows`, Field props | Done |
| QeetrixSearchField | SearchField | `onSearch` on IME only, "Clear" | `searchDelay` (300 ms), Escape clears, "Clear search", `readOnly`, `endAdornment` | Done |
| QeetrixPasswordInput | PasswordInput | `purpose` Current/New, `showToggle` | `autoComplete` CurrentPassword/NewPassword/Off, `visible` + `onVisibleChange`, `capsLockWarning`, `icon`, adornments | Done (no hide-on-submit) |
| QeetrixNumberField | NumberField | `range`, steppers at both ends | `min`, `max`, `step`, `largeStep`, `steppers` at the end, adornments, `fractionDigits` | Partial (`format`, `locale`) |
| QeetrixOTPInput | OTPInput | `characters`, `groupSize`, `onComplete`, `label?` | `label`, `length`, `groups`, `size` Md/Lg, `validationType`, `onValueComplete`, `error`, `invalid`, `smsCodeAutofill` | Done |
| QeetrixSelect | Select | `selected`/`onSelect`/`options`, `QeetrixSelectSize` Default/Sm | `value`/`onValueChange`/`items` (or `groups`), `size` Sm/Md/Lg (Lg default), Field props | Partial (`multiple`, `open`, type-ahead) |
| QeetrixOption (+ QeetrixOptionGroup) | Radio / Checkbox / SelectItem props | value, label, description, enabled | + `icon`, `meta` | Done |
| QeetrixCheckbox | Checkbox | two overloads, `QeetrixTriStateCheckbox` | `checked`, `onCheckedChange?`, `label`, `description`, `icon`, `indeterminate`, `enabled`, `readOnly`, `invalid` | Done (no `parent`) |
| QeetrixCheckboxGroup (new) | CheckboxGroup | (a FieldSet of checkboxes) | `value: Set<T>`, `onValueChange`, `options`, `label`, `description`, `error`, `orientation`, `variant` Default/Tile, `columns` | Done |
| QeetrixRadioGroup | RadioGroup, Radio | `selected`/`onSelect`, `message` | `value`/`onValueChange`, `label`, `description`, `error`, `orientation`, `variant` Default/Tile, `columns`, `readOnly`, `required` | Done |
| QeetrixSwitch | Switch | `size` Default/Sm, label at the start | no size, `readOnly`, label after the 32 × 18 track | Done |
| QeetrixForm (new), QeetrixFormErrorSummary, QeetrixFormActions | Form, ErrorSummary | summary `errors`, `title` | `QeetrixForm(errors, summary, summaryTitle)`; summary `items: List<QeetrixFormError>`, `autoFocus`, danger-alert look; actions on `QeetrixActionRow` | Done (`headingLevel`) |
| QeetrixChip, QeetrixChipGroup | none — kept, restyled | pill, `selected`/`onSelect`, `selected: Set` | 4 dp tag (FilterTags' tag), selected `bg.selected` + 2 dp `border.brand` + check, sizes from `size.control.*`; `value`/`onValueChange`, multi `values`/`onValuesChange` | Done |
| QeetrixDialog | Dialog | composed = shown, `onDismissRequest`, `size` Sm/Default/Lg/Xl/Full, `showCloseButton`, `dismissOnClickOutside` | `open`/`onOpenChange`, `intent` Standard/Danger/Warning (3 dp line), `size` Sm/Md/Lg/Xl/Fullscreen, `dismissible`, `footerStart`, `footer` | Done |
| QeetrixAlertDialog | Dialog (`intent` danger/warning) | `onDismissRequest`, `message`, `icon` | `open`/`onOpenChange`, `description`, `intent`, `size`, `dismissible` | Done |
| QeetrixConfirmationDialog | Dialog + Cancel | `destructive`, `message`, `icon` | `open`/`onOpenChange`, `intent`, `description`, `dismissible`, `cancelLabel` | Done |
| QeetrixDialogAction roles | Button roles in DialogFooter | Default/Cancel/Destructive | Primary (solid) / Secondary (soft) / Cancel (ghost) / Destructive (solid danger) | Done |
| QeetrixBottomSheet | Drawer | `visible`, `onDismissRequest`, `title?`, `dismissOnGesture`, `showCloseButton` | `open`/`onOpenChange`, `title`, `dismissible`, `size` Sm/Md/Lg, `footerStart`, `footer`, `partiallyExpandable` | Partial (`side`) |
| QeetrixMenu | Menu | `expanded`/`onExpandedChange`, item `variant` Default/Destructive, `QeetrixMenuLabel` | `open`/`onOpenChange`; item `text`, `icon`, `shortcut`, `description`, `tone` Default/Danger, `closeOnClick`; `QeetrixMenuCheckboxItem`, `QeetrixMenuRadioGroup`/`RadioItem`, `QeetrixMenuGroupLabel` | Partial (submenus, ContextMenu, side/align) |
| QeetrixSnackbar (Host, HostState) | Toast | `show(message, type Info/Success/Warning/Error…)`, 5 or 10 s | `show(title, tone Info/Success/Warning/Danger, description, actionLabel, timeout = 5 s)`, Danger stays | Partial (promise, stack) |
| QeetrixEmptyState | EmptyState | `variant` (5), `size` Sm/Default/Lg | `icon`, `description`, `size` Page/Section, `tone` Neutral/Danger, `level`, `footer`, `actions` | Done |
| QeetrixDataState | DataState | `failure: QeetrixDataFailure`, `emptyContent`, `loadingContent`, `QeetrixDataLoadingRows` | `loading`, `error`, `empty`, `emptyState`, `onRetry`, `errorTitle`, `errorDescription`, `retryLabel` | Done |
| QeetrixSkeleton, QeetrixSkeletonText | Skeleton | `shape` Rectangle/Circle/Capsule, shimmer | `variant` Text/Block/Circle, `size` Sm–Xl, opacity pulse; `QeetrixSkeletonText(lines, role)` | Done |
| QeetrixProgress, QeetrixMeter (new) | Progress, Meter | `progress: Float?`, `size`, `showValue = true` | `value` in `min`…`max`, `tone`, `label`, `showValue = false`, `format`; Meter with 6 tones and `target` | Done |
| QeetrixAvatar, QeetrixAvatarItem (new) | Avatar, AvatarItem | `name?`, `size` Xs/Sm/Default/Lg/Xl, `badgeLabel` | `name`, `image`, `size` Sm/Md/Lg/Xl (24–48), `shape` Circle/Square, `decorative`; AvatarItem `name`, `description` | Done (no status badge) |
| QeetrixCard | Card | `variant` Default/Outline/Elevated, `size`, `selected`, `action` | `variant` Default/Subdued, `tone` Neutral/Danger, `title`, `description`, `actions`, `footer`; clickable overload keeps `selected` | Done |
| QeetrixDescriptionList, …Item | DescriptionList | `layout` Horizontal/Vertical/Grid, `divided` | `layout` Rows/Columns, `columns` 1–4; Item `term`, `value`, `description`, `action`, `emptyLabel` | Done |
| QeetrixListItem, QeetrixListItemIcon | none — kept, restyled | — | same API, Table-row rules; selected is tint only | Done |
| QeetrixSectionHeader | none — kept as the group label | `title`, `description`, `action` | + `ruled`; `label.caps` | Done |
| QeetrixTimeline, …Item, …Group (new) | Timeline | tone `Destructive`, `emphasis` | `variant` Default/Compact; Group(`label`); Item(`title`, `description`, `time`, `icon`, `tone` Neutral/Brand/Info/Success/Warning/Danger) | Done |
| QeetrixTabs, QeetrixTab | Tabs | `variant` Default/Line, `selected`/`onSelect` | `value`/`onValueChange`, `variant` Line/Contained, `orientation`; Tab `icon`, `filledWhenSelected`, `badge`, `badgeTone`, `enabled` | Partial (`activateOnFocus`, panels) |
| QeetrixAccordion, …Item; QeetrixCollapsibleTrigger/Panel (new) | Accordion, Collapsible | item `expanded`/`onExpandedChange` | Accordion `value`/`onValueChange`, `multiple`; Item `value`, `title`; Collapsible `open`/`onOpenChange` | Done |
| QeetrixNavigationBar, …Item | none — app chrome, AppShell + SidebarItem rules | `selected`/`onSelect` | `value`/`onValueChange`; Item `filledWhenCurrent`; Ember bar, round icons | Done |
| QeetrixNavigationRail | none — AppShell nav column + Sidebar | `selected`/`onSelect` | `value`/`onValueChange` | Done |
| QeetrixNavigationScaffold | AppShell, adapted | `selected`/`onSelect`, 600 dp literal | `value`/`onValueChange`, `QeetrixBreakpoint.Medium` | Done |
| QeetrixTopAppBar, QeetrixBackButton | PageHeader, PageHeaderBack | `subtitle`, `scrolled` | `description`, always ruled, `title.lg`; Back is neutral and mirrors | Partial (count, status, breadcrumb, tabs) |

Removed public types, beyond those above: `QeetrixSpacing`, `QeetrixCorner`, `QeetrixStroke`, `QeetrixIconSize`,
`QeetrixElevation`, `QeetrixDuration`, `QeetrixEasing`, `QeetrixFontWeight`, `QeetrixTextVariant`, `QeetrixIconButtonSize`,
`QeetrixSelectSize`, `QeetrixTriStateCheckbox`, `QeetrixDataFailure`, `QeetrixDataLoadingRows`, `QeetrixSnackbarType`,
`QeetrixMenuItemVariant`, `QeetrixEmptyStateVariant`, `QeetrixSkeletonShape`, `QeetrixProgressSize`; `QeetrixProvenance`
now has `tokens`, `react` and `icons` packages. The exact surface is `Documentation/API/qeetrix.api`.

## 6. Decisions made in the migration

The shared decisions (pins, tokens, typeface, icons, components, platform conventions, this record) are in 0019. Beyond them:

1. **Resolver first, published documents as a check.** The generator resolves the DTCG itself and fails if
   `light/dark.tokens.json` disagree; it does not trust either alone.
2. **Colours from OKLCH, hex as the oracle.** Colours are converted from the components (what browsers paint for
   `oklch()`), and must match the recorded hex in 8-bit sRGB; one light colour (`dataviz.categorical.2`) is outside sRGB and
   is drawn in Display P3.
3. **Density.** All three densities are generated; only the five tokens the modifier changes become `QeetrixDensity`
   properties. Icon stroke follows density as design rules §16 says (thin at compact).
4. **Role letter style comes from the tokens**: each typography composite's `$extensions["in.qeet.qeetrix"].fontGeometry`
   (0.0.13, QTRX-186). Until 0.0.13 it was a generator rule restating design rules §5.
5. **`token-values.json` is platform-neutral**: CSS px, ms, em, encoded RGB, and per token its source file, authored value
   and reference chain; no platform names.
6. **Interim font conversion.** Before 0.0.12 the sync converted WOFF2 to TTF with fontTools 4.68.0 in a venv; the
   0.0.12 TTFs are byte-identical to that output, and the conversion is gone.
7. **Upstream CSS snapshot.** The 42 mapped families' compiled CSS is copied and locked, so a pin bump's look changes show
   in review. Component values restate it in the component files (no generated component metrics).
8. **Names kept:** composable names, files and packages stay (`QeetrixSnackbar`, `QeetrixBottomSheet`,
   `components.dropdownmenu`…); each page and KDoc names the react counterpart. `QeetrixText` is split into `QeetrixText`
   and `QeetrixHeading`, as react splits them.
9. **Disabled stays focusable** (react's `focusableWhenDisabled`) on buttons; TalkBack still reads it as disabled.
10. **Loading buttons** keep the label and put the spinner in the icon zone (react); `loadingLabel` is gone.
11. **Button vertical inset.** At 100 % text a button is exactly `size.control.*` tall; as text grows it keeps half of
    what the control height leaves around the label line, so the label never touches its edges (the web zooms instead).
12. **Arrows and chevrons mirror where they're used** (QTRX-200): `mirrorsInRightToLeft` follows the catalogue only; a
    caller passes `mirrorInRightToLeft` / `iconMirrorsInRightToLeft` for an arrow that means back or next, and Qeetrix's
    own components (the back button) mirror theirs, as the web's `mirrorInRtl()`.
13. **Overlays stay composed** while `open` toggles, so exit animations run; Back always asks to close (Android never traps
    Back), so `dismissible` governs only the close button and outside taps; Escape is handled in the dialog window.
14. **48 dp everywhere:** menu rows, select options, choice rows (flush, not 4 dp apart), accordion triggers, vertical tabs,
    error-summary links; tools drawn inside fields keep their drawn size and get touch-target expansion.
15. **Labels wrap instead of ellipsizing** in menus, select options and the avatar item (text grows, not clips), as the CSS
    does since 0.4.0 (QTRX-177, QTRX-178); the icon, mark, shortcut, meta and check stay on the label's first line.
16. **Selection labels come after the control** (§9), including Switch (Android had it at the end).
17. **`required` draws no asterisk** and is spoken; `label` is required on every Field-wrapped control (`hideLabel` hides it).
18. **Chip kept, not mapped to FilterTags**: FilterTags is a whole applied-filters bar; Chip takes its tag's look.
19. **Navigation keeps visible labels** under rail and bar icons (touch has no hover tooltips); the rail stays 80 dp; the
    current item is `bg.neutral.subtle-hover`, weight 600, a filled round Ember icon and the 3 dp Ember bar (start edge on the
    rail, top edge on the bar). Top app bar = PageHeader; TopBar (console chrome) has no Android counterpart.
20. **Filled icons on by default** for the current tab and navigation item (§8, §11, §16), as react defaults them since 0.4.0
    (QTRX-168).
21. **Clickable card kept** in the choice-tile look. The specs now say a Card is not a click target (06 §9, QTRX-182): a
    chosen card is a choice tile (`QeetrixRadioGroup` / `QeetrixCheckboxGroup` `variant = Tile`). Whether Android keeps the
    overload is open (§9).
22. **Snackbar: one at a time, 5 s** (react's single timeout; the old 10 s extended time is gone); danger stays; a snackbar
    with an action never times out under TalkBack.
23. **Tabs:** one focus stop per tab (so no `activateOnFocus`); the focus frame inside the tab, as tabs.css draws it.
24. **Strings** follow react's message catalogue (`qx_*` in `res/values/strings.xml`): "Fix these to continue",
    "Couldn't load this" / "Try again, or check your connection.", "Dismiss", "Clear search", "{value}, target {target}",
    "Not set", "Password shown/hidden", "Caps Lock is on".
25. **Quality catalogue** covers every public composable (a test fails otherwise); recorded findings are listed and must keep
    happening (a scan fails if a recorded finding goes away).

## 7. Qeetrix gaps and bugs

Found during the migration and filed as QTRX issues; numbers are for this document only. "Q-n" are the findings the quality
scans record (`qeetrix/src/sharedTest/kotlin/com/qeetgroup/qeetrix/quality/`), each excluded by name and required to keep
happening. QTRX-91 (native fonts) and QTRX-92 (JSON manifest) were filed during the migration and fixed in 0.0.12 and 0.3.0.
Every issue below is **Done**, released on 30 October 2026 in `@qeetrix/react` 0.4.0, `@qeetrix/tokens` 0.0.13,
`@qeetrix/icons` 2.1.0, or in the qeetrix-react specs (qeetrix-files #2), which Android now builds on. Rejected and dropped
from this list: B8 (arrows and chevrons stay `preserve` by design; Android's own ticket is QTRX-200), G2 (the DTCG states the
reduced-motion rule) and G8 (Android strings, not an upstream gap).

### Gaps (missing)

| # | Issue | What was missing | Settled in | Android now |
|:--|:--|:--|:--|:--|
| G1 | QTRX-188 | `@qeetrix/icons` 2.0.0 had no SVG files and no JSON manifest, only React modules | icons 2.1.0: `dist/svg/<shape>-<variant>/<id>.svg`, `dist/manifest.json` | The sync copies the files and reads the JSON; the module reader (`JsModules.kt`) is removed. Same 138 drawings, byte for byte |
| G3 | QTRX-186 | The typography composite named no letter style; the role → `font.geometry.*` pairing was prose | tokens 0.0.13: `$extensions["in.qeet.qeetrix"].fontGeometry` | Read from the tokens; `Scope.geometryToken` is removed |
| G4 | QTRX-180 | No Chip | react 0.4.0: Chip and ChipGroup (experimental) | `QeetrixChip` unchanged; it already takes FilterTags' tag look, which 0.4.0's Chip shares. Adopting Chip's API is not part of this bump |
| G5 | QTRX-165 | No read-only look for OTP slots | react 0.4.0 `experimental/otp-input.css` `[data-readonly]` | A read-only code has no fill and keeps the `border.strong` line |
| G6 | QTRX-181 | No avatar status badge | react 0.4.0 Avatar `status` | Not adopted in this bump; `badgeLabel` stays dropped |
| G7 | QTRX-182 | SelectionCard was specified (06 §9) but not built | specs: "A Card is not a click target"; a chosen card is a choice tile | The clickable `QeetrixCard` overload remains; keeping or removing it is open (§9) |
| G9 | QTRX-189 | The Qeet logo needed `<mask>`, which native vectors lack | icons 2.1.0: `dist/svg/logos/qeet/{default,dark}.mask-free.svg` | Android draws no logo yet, so nothing is taken |

### Bugs (wrong or inconsistent)

| # | Issue | What happened in 0.3.0 / 0.0.12 | Settled in | Android now |
|:--|:--|:--|:--|:--|
| B1 | QTRX-157 | `button.css`: a solid neutral button turned Ember on hover and press (specificity) | react 0.4.0 `.qx-button[data-tone="neutral"]:not([data-variant="ghost"]):hover` / `:active` | Unchanged look (`bg.neutral.bold-hover` / `-pressed`), now the CSS's |
| B2 | QTRX-158 | Ghost danger had no press rule, so a press painted an Ember tint | react 0.4.0 `[data-tone="danger"][data-variant="ghost"]:is(:hover, :active)` | Unchanged look (`bg.danger.subtle`), now the CSS's |
| B3 | QTRX-159 | Spec said ghost neutral hovers `bg.neutral.subtle-hover`; CSS `bg.neutral.subtle` | specs take the CSS | Unchanged (the CSS) |
| B4 | QTRX-160 | Rules: the button's rule is as tall as the icon (14 px at sm); code: 16 px icon, 14 px rule | specs take the code | Unchanged (16 dp icon, 14 dp rule) |
| B5 | QTRX-183 | Dark `border.subtle` equalled `surface.overlay`: menu, drawer and popover rules vanished | tokens 0.0.13: dark `border.subtle` graphite.800, `border.default` graphite.750 | Regenerated; dark rules show on overlays |
| B6 | QTRX-184 | Line heights were 4-decimal ratios (`body.md` 20.0004 px), which Compose rounds up to 21 px a line | tokens 0.0.13: lengths on the 4 px grid, full-precision ratios | Exact: the 1 px-per-line test tolerance is gone |
| B7 | QTRX-185 | `component.timeline.marker-size` was 24 px; the CSS and 07 draw 28 px | tokens 0.0.13: 28px | The timeline draws its marker from the token |
| B9 | QTRX-161 | Checkbox, Radio, Switch: hover (and checked) outranked `[data-invalid]` | react 0.4.0: `[data-invalid]` after the checked and hover rules | The danger edge stays under the pointer **and when checked** (a chosen invalid radio's ring is danger) |
| B10 | QTRX-162 | Switch's on thumb was `surface.default`, near-black in dark | react 0.4.0: `color.icon.on-brand` | White thumb in both schemes |
| B11 | QTRX-163 | A focused invalid OTP slot showed danger, not focus | react 0.4.0: `:focus` after `[data-invalid]` | The typing slot of an invalid code shows the Ember frame |
| B12 | QTRX-164 | OTP slots' 24 px minimum overflows a column under 312 px (328 px as [5, 5]) | 07-components: the narrowest column each code fits; a longer code is one Input | Slots keep shrinking; recorded finding Q-15 stays |
| B13 | QTRX-166 | Progress's indeterminate segment slid physically, wrong in right to left | react 0.4.0 `qx-progress-slide-rtl` | Unchanged look (mirrored), now the CSS's |
| B14 | QTRX-167 | A disabled Accordion chevron stayed `icon.subtle` | react 0.4.0 `[data-disabled] > .qx-accordion-icon` | Unchanged look (`icon.disabled`), now the CSS's |
| B15 | QTRX-168 | `filledWhenSelected` / `filledWhenCurrent` defaulted to `false` | react 0.4.0: default `true` | Unchanged (default `true`) |
| B16 | QTRX-169 | Rules: tab focus 2 px outside; CSS: inside | specs take the CSS (a tab list scrolls) | Unchanged (inside) |
| B17 | QTRX-170 | Rules: menu items `label`, a focus frame; CSS: `body.md`, the highlight is the focus | specs take the CSS | Unchanged (the CSS) |
| B18 | QTRX-171 | Spec: an 18 px toast icon; code 16 px | specs: 16 px | Unchanged (16 dp) |
| B19 | QTRX-172 | Rules listed three dialog widths; spec and CSS five | specs list all five | Unchanged |
| B20 | QTRX-173 | Rules: page titles `heading.sm`; code and 07 `title.lg` | specs: `title.lg` | Unchanged (`title.lg`) |
| B21 | QTRX-174 | Card always rendered a 32 px body, and its header's rule doubled the footer's | react 0.4.0 `.qx-card-body:empty`, `.qx-card-header:is(:last-child, :has(+ .qx-card-footer, + .qx-card-body:empty))` | No body without content (or content that emits nothing); the header's rule only above a body, so a header and footer share one rule |
| B22 | QTRX-175 | Read-only fields drew their line in `border.subtle` (1.06–1.20:1) | react 0.4.0: `border.strong` in input, select, number-field, otp-input | `border.strong`, no fill; Q-8 no longer happens and is removed |
| B23 | QTRX-176 | Progress and Meter fills under 3:1 against the `border.default` track | react 0.4.0: dark track `surface.sunken`, light warning fill `border.warning` | The same per scheme; Q-9 no longer happens and is removed |
| B24 | QTRX-187 | Dark `bg.brand.bold-hover` was 2.93:1 on `surface.overlay` | tokens 0.0.13: dark `qeet.650` (3.31:1) | Regenerated; Q-10 no longer happens and is removed |
| B25 | QTRX-177, QTRX-178 | Select options, Menu items and AvatarItem ended in an ellipsis | react 0.4.0: they wrap, the side parts on the first line | Options and items already wrapped; their icon, mark, shortcut, meta and check now stay on the first line; AvatarItem wraps; Q-14 no longer happens and is removed |
| B26 | QTRX-179 | DescriptionItem's `description` and `action` weren't in 07 | specs list them | Unchanged |

Also recorded for the owner, Android's own and still open: OTP slots at 200 % in a compact phone (Q-15, above). Done in the
ticket work: paragraphs take their direction from their text in right to left (Q-13, QTRX-199), a long placeholder ends in
an ellipsis at large text (Q-5, QTRX-197), and the bottom sheet is built on Compose foundation, so Material's 12 dp
clickable drag-handle row (Q-6) is gone and the sheet moves on `motion.duration.slow` (QTRX-198).

Found on the device pass (31 October 2026) and filed upstream: the dark Progress and Meter track is darker than every
surface, 1.03:1 on the canvas (QTRX-213); a disabled pressed toggle keeps its bold fill and looks enabled (QTRX-214);
CheckItem centres its icon and status on the whole check, not the title's first line (QTRX-215); `format` has no
duration formatter for native durations to match (QTRX-216). Android follows the web in each until they're settled.

## 8. Compared with qeetrix-ios

Before the 0.0.13 bump (read-only look at qeetrix-ios's uncommitted working tree): the same `@qeetrix/tokens` 0.0.12 tarball
(identical integrity hash), and **every colour both generate is identical** in 8-bit sRGB in both schemes. The scopes differ:
iOS generates 194 tokens including `primitive.font.weight.*` (3), Android 202 including `breakpoint.*` (4) and the adopted
`component.sidebar.*` and `component.timeline.*` (4); and the two `token-values.json` files have different shapes (iOS: a list
with Swift names and `values` keyed by context; Android: a map keyed by path with per-theme `source`, `authored`, `references`
and `value`). Both need reconciling before the files can be diffed directly.

## 9. Open

- **G7 / QTRX-182:** the specs now say a Card is not a click target; the clickable `QeetrixCard` overload remains until
  the owner decides to keep it (a native difference) or remove it in favour of the choice tiles.
- **Device testing.** The Android tickets' components were tried one by one on the phone emulator (`qeetrix-api36`) on
  31 October 2026, in light, dark, 200 % text and right to left, each from its sample page; what that found is fixed
  and listed in the CHANGELOG. The same day they were tried on the tablet emulator (`qeetrix-tablet-api36`, 1280 × 800
  dp), with the library's and the independent consumer's device tests: the bottom action bar's `fullWidth` actions ran
  off the window's start edge from `breakpoint.medium`, and the full-screen form's sheet darkened the screen twice; both
  are fixed (CHANGELOG › Fixed on the tablet pass). New device screenshots of every sample page — light, dark, 200 %
  text and right to left, phone and tablet — are in `docs-redesign-mockups/migration/android/devices/` (31 October; the
  30 October set, before the bump, is under `2026-10-30/`). The sample takes `--ez rtl true` beside `--es route` for
  them. The emulator was unstable under the machine's load (two crashes, one hang); running it headless helped.
- **Android tickets not done:** QTRX-126, 190, 191, 192 and 195 (see CHANGELOG).
- **Material 3** is now used, internally, by the date and time fields (the platform's pickers in Qeetrix's colours, as
  QTRX-120 and 128 ask) and pull to refresh (its nested scroll); `MaterialBoundaryTest` lists the two files and no public
  API names a Material type. The bottom sheet no longer uses it (QTRX-198).
