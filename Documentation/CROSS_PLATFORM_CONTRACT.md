# Cross-platform contract

How a Qeetrix design decision made for the web arrives on Android unchanged, what "unchanged" means where
the platforms differ, and what was verified against qeetrix-ios.

**Status.** Written by the Android side. It follows the rules qeetrix-ios recorded in its own
`Documentation/CROSS_PLATFORM_CONTRACT.md` and decision records 0002–0005, and the parity below was
checked against qeetrix-ios's committed Phase 1 (`f335645`). The iOS side has not reviewed this document;
it is not a jointly approved contract.

## Source of truth

| Concern | Authority | Android reads | iOS reads |
|:--|:--|:--|:--|
| Tokens | qeetrix-ui `src/tokens/**` (DTCG), compiled by its Style Dictionary build | `@qeetrix/ui` 2.1.6 `tokens.json`, `tokens.css`, `index.css` | the same files |
| Themes | qeetrix-ui `scripts/config/themes.json` (`light`, `dark`) | the theme keys in `tokens.json` | the same |
| Typeface | qeetrix-ui `fonts/` (WOFF2) | calcom/sans v2.011 and Fira Code 6.2 TTFs, proven byte-identical through their WOFF2 | the same files |
| Icons | qeetrix-icons `icons/**.svg` + `icon-manifest.json` | commit `331cd85` (`@qeetrix/icons` 1.0.11), 12 icons | the same commit and icons |

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
- **Not mapped on either:** z-index tokens and gradients (Phase 1), component tokens (Phase 2), logos.

## Decisions that need both platforms

1. **A shared resolved-values file.** Android publishes `token-values.json`; iOS could emit the same
   shape so parity is checked by a diff instead of by parsing generated code.
2. **Pins.** qeetrix-ios has uncommitted Phase 2 work that adds five icons and the component manifest to
   its pins. Android stays on the Phase 1 pins until both sides agree on Phase 2 scope.
3. **Logos.** The Qeet logo's SVG uses a `<mask>`, which neither native vector format supports. Both
   platforms defer logos until qeetrix-icons provides a mask-free drawing or both agree on a conversion.
4. **Increased contrast.** No upstream values; both platforms resolve standard values today.
5. **Component folders.** Both organise components by qeetrix-ui family (the manifest's `category`), not by
   ui.qeet.in's navigation categories.
6. **API baselines.** iOS checks its public API against a committed baseline; Android does not yet
   ([PUBLIC_API.md](Standards/PUBLIC_API.md)).

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
