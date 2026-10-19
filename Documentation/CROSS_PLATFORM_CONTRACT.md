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
| Icons | qeetrix-icons `icons/**.svg` + `icon-manifest.json` | commit `331cd85` (`@qeetrix/icons` 1.0.11), 21 icons | the same commit; 29 icons (eight more for components beyond Phase 3) |

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
devices. qeetrix-ios's tree also holds components beyond Phase 3 (dialogs, menus, sheets, tabs, toasts)
with eight more icons and the `dialog` and `popover` token groups; Android adds those with its Phase 4.

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

## Decisions that need both platforms

1. **A shared resolved-values file.** Android publishes `token-values.json`; iOS could emit the same
   shape so parity is checked by a diff instead of by parsing generated code.
2. **Pins.** Android's pins match qeetrix-ios's through Phase 3 (21 icons, eight component-token groups,
   the component manifest). qeetrix-ios's working tree has eight more icons and the `dialog` and
   `popover` groups for components beyond Phase 3; Android adds them with those components.
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
