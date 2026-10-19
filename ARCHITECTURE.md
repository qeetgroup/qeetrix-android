# Architecture

How Qeetrix for Android turns the web's design tokens into a Compose library, and the rules that keep it
that way. Decisions are recorded in [Documentation/Decisions](Documentation/Decisions/README.md).

## The flow

```text
qeetrix-ui (DTCG source) ──Style Dictionary──▶ @qeetrix/ui 2.1.6 on npm ─┐
calcom/sans v2.011, Fira Code 6.2 (TTF; WOFF2 = the web's) ──────────────┼─▶ Scripts/sync-tokens ─▶ Upstream/ + res/font + licences
qeetrix-icons @ 331cd85 (SVG + manifest) ────────────────────────────────┘        (network, verified, locked)
                                                                                              │
                                                    Scripts/generate-tokens (offline, deterministic)
                                                                                              ▼
                     qeetrix/…/tokens/generated, icons/generated (internal Kotlin) + Documentation/Foundations
                                                                                              │
                                    foundation/* (public types) ◀── theme/ (QeetrixTheme) ── icons/ (QeetrixIcon)
                                                                                              │
                                                                     components/* (QeetrixButton, …)
                                                                                              ▼
                                                                                   apps (and :sample)
```

Three steps, three commands:

- **Sync** (`Scripts/sync-tokens`, network): fetches the pinned `@qeetrix/ui` tarball and checks it
  against the npm registry's integrity hash; fetches each font's TTF and accepts it only if the same
  release's WOFF2 is byte-identical to the one inside the tarball; fetches the pinned icons' SVGs and
  manifest entries. Writes `Upstream/` and `Upstream/manifest.lock.json` (sha256 of every file).
- **Generate** (`Scripts/generate-tokens`, offline): reads only the snapshot and writes the generated
  Kotlin and reports. The same snapshot always gives the same bytes.
- **Verify** (`Scripts/verify-tokens`, part of `./gradlew check`): fails if a locked file changed, if a
  generated file differs from a fresh generation, or if a generated package holds a file the generator
  did not produce.

## Modules and dependency rules

One library module, one sample, one tool ([0001](Documentation/Decisions/0001-single-library-module.md)):

| Unit | Depends on | Never depends on |
|:--|:--|:--|
| `:qeetrix` | Compose runtime, UI, foundation, animation-core | Material, AndroidX Core, the generator |
| `:sample` | `:qeetrix` (public API only), activity-compose; Robolectric and Compose UI Test for its tests | `:qeetrix` internals (Kotlin `internal` makes them unreachable) |
| `Tools/QeetrixGen` | kotlinx-serialization-json, the JDK | Android; it is a separate Gradle build |

Inside `:qeetrix`:

| Package | Holds | May use |
|:--|:--|:--|
| `foundation.*` | Public types: `QeetrixColors`, `QeetrixTypography`, scales, `QeetrixElevation`, motion, accessibility | `tokens.generated` (read values), `internal`, `theme` (to read the current theme) |
| `tokens.generated` | Internal generated data, expressed in foundation types | `foundation.*` types, `internal`, `R` |
| `theme` | `QeetrixTheme`, `QeetrixThemeMode`, the composition locals | `foundation.*`, `tokens.generated` |
| `icons`, `icons.generated` | `QeetrixIcon`, `QeetrixIconName`; the drawings | `foundation`, `theme`, `internal` |
| `components.<family>` | One file per component (`QeetrixButton`, …) and its option enums | `foundation`, `theme`, `icons`, `internal`, `tokens.generated` (component tokens); inputs also `components.input`'s internal field parts |
| `tokens` | `QeetrixProvenance` | `tokens.generated` |
| `internal` | Spec types the generated files are written in; the components' shared helpers (`QeetrixWebScale`, focus ring, busy indicator) | Compose, `foundation`, `theme` |

Generated code is never public. The public API is hand-written, and the generated code is written in
terms of it: `QeetrixLightColors = QeetrixColors(textPrimary = …, …)` names every parameter, and
`QeetrixElevation.lightLayers()` is an exhaustive `when`. A token added or removed upstream is therefore
a compile error until the public API is changed on purpose; scale objects are covered by a reflection
test instead. Material 3 uses the same split (hand-written `ColorScheme`, generated internal tokens).

## Token generation

The generator is a Kotlin port of qeetrix-ios's, so both read the same inputs the same way
([0004](Documentation/Decisions/0004-tokens-from-the-published-qeetrix-ui-build.md)):

- **Source.** `tokens.json` (values per theme), `tokens.css` (which names are consumable: the `--qx-*`
  variables on `:root`; the variable tables per theme) and `index.css` (only the `--radius-*` scale the
  `corner.*` roles reference). The generator proves `tokens.json` and `tokens.css` agree for every
  consumable token.
- **Resolution.** `var()` with fallbacks and cycle detection; `calc()`, `min()`, `max()`;
  `color-mix(in oklab, …)`; `oklch()`/`rgb()` with alpha; `box-shadow` lists; `cubic-bezier()`;
  font stacks. Anything else fails generation by name rather than producing a guess.
- **Scope.** A rule decides every token: the semantic layer, the scales qeetrix-ui gives code, and the
  component-token groups adopted in `Upstream/manifest.json` are generated (279 in 2.1.6); primitives, the
  other component groups, z-index and gradients are excluded with a reason (604). An unmatched
  consumable group fails generation ([0011](Documentation/Decisions/0011-component-tokens-and-web-metrics.md)).
- **Units.** 1 CSS px = 1 dp; 1 rem = 16 dp; font sizes in sp; letter spacing and line height in em;
  durations in ms.
- **Colour** ([0005](Documentation/Decisions/0005-colour-conversion-per-css-color-4.md)). CSS Color 4
  conversion; sRGB as packed 8-bit `Color(0xFF…)` when inside sRGB, else Display P3; six-decimal rounding.
  Checked against qeetrix-ios (identical) and culori, the web's converter (identical in 8-bit).
- **Outputs.** `tokens/generated/{QeetrixColorTokens, QeetrixElevationTokens, QeetrixTypographyTokens,
  QeetrixScaleTokens, QeetrixProvenanceTokens}.kt`, one `Qeetrix<Group>ComponentTokens.kt` per adopted
  group (internal; semantic references stay theme reads), `icons/generated/QeetrixIconDrawings.kt`,
  `Documentation/Foundations/TOKEN_COVERAGE.md` and `token-values.json` (every token's input expression
  and value, for cross-platform comparison).

## Theme

`QeetrixTheme(mode)` resolves light or dark (`System` reads `isSystemInDarkTheme()` live) and provides,
through composition locals: the colours, the typography, the dark flag, the reduced-motion setting
(observed live from the animator duration scale), the content colour and text-selection colours
([0006](Documentation/Decisions/0006-theming-through-composition-locals.md)). Static locals for values that
change rarely; a dynamic one for the content colour. Nested `QeetrixTheme`s change only their subtree's
colours. There is no global mutable state, and Material is not involved.

## Typography

Each role is a `TextStyle` built once: a `FontFamily` of the role's font file at the four weights of the
scale, each `Font` carrying the web's variation settings — `wght`, the role's `GEOM`, `opsz` from the
size in sp (Compose multiplies it by the font scale) and `ital` 0 — plus the size in sp, the line
height in em with `LineHeightStyle(Center, Trim.None)` (CSS half-leading) and the letter spacing in em
([0007](Documentation/Decisions/0007-fonts-are-the-webs-fonts-in-native-format.md)). Android's Bold Text
raises the requested weight and lands on a real heavier instance. Non-Latin glyphs fall back to the
system fonts.

## Elevation

`Modifier.qeetrixElevation(elevation, shape, fill)` is a `Modifier.Node` that reads the theme from the
composition and draws like CSS `box-shadow`: each outer layer is the shape grown by its spread, offset,
blurred with σ = blur ÷ 2 (converted to Android's `BlurMaskFilter` radius) and clipped to outside the
shape; then the fill; then inset layers, clipped inside; then the content. Compose's `dropShadow` was not
used: it blurs with Skia's radius convention and does not clip outer shadows.

## Icons

The generator turns each pinned SVG into Compose path nodes — arcs and smooth curves expanded, ellipses
and rounded rectangles as cubics — and rejects anything outside the shared SVG subset
([0008](Documentation/Decisions/0008-icons-are-generated-vector-paths.md)). `QeetrixIcon` builds an
`ImageVector` for the stroke token, tints it with the content colour, mirrors it in RTL when the
manifest says so, and exposes it to TalkBack only when it has a `contentDescription`.

## Public API conventions

Explicit API mode; Compose types; PascalCase constants and enum entries; camelCase theme accessors;
upstream strings in `value`/`token`; no public `data class`; KDoc on everything public. The rules and the
change policy are in [Documentation/Standards/PUBLIC_API.md](Documentation/Standards/PUBLIC_API.md).

## Relationship to Qeetrix Web and iOS

- **Web** is the authority: tokens, fonts and icons are only ever changed upstream and arrive here by a
  pin bump.
- **iOS** reads the same pinned artifacts with the same rules and produces identical values; the
  verified parity and the deliberate differences are in
  [Documentation/CROSS_PLATFORM_CONTRACT.md](Documentation/CROSS_PLATFORM_CONTRACT.md).

## Components

Each component is one file in its web family's package, `components.<family>`
([0010](Documentation/Decisions/0010-component-api-conventions.md)), and follows
[COMPONENT_CONTRACT.md](Documentation/Standards/COMPONENT_CONTRACT.md):

- **Built from foundation primitives**, not Material: `BasicText`, `BasicTextField` and
  `BasicSecureTextField` with a `TextFieldState`, `clickable` / `toggleable` / `triStateToggleable` /
  `selectable` with `indication = null` and the web's colour steps, `Popup` for the select's list,
  `progressSemantics`, `Canvas` for the busy indicator and progress hatch.
- **Inputs share one field.** Every input is built on the internal field parts in
  `components/input/QeetrixFieldParts.kt` — the label inside the control, the boundary (`FieldBox`) per
  state and tone, description and message — and reads the enclosing field set through
  `LocalFieldGroup` ([0014](Documentation/Decisions/0014-form-components.md),
  [FORMS.md](Documentation/Standards/FORMS.md)).
- **Values** from the theme (`QeetrixTheme.colors`, `.density`), the generated component tokens and
  `QeetrixWebScale` for the web's Tailwind geometry, with the web class beside each use
  ([0011](Documentation/Decisions/0011-component-tokens-and-web-metrics.md)).
- **Tests** beside it: behaviour and semantics with Compose UI Test on Robolectric, and one Roborazzi
  screenshot test across light, dark, 200% text and right to left, verified against local, reviewed
  baselines ([0012](Documentation/Decisions/0012-visual-regression-testing.md)).
- **Documentation** in KDoc (Dokka) and `Documentation/Components/<Component>.md`; a sample page with
  previews under the component's ui.qeet.in group.
- **API** held by `Documentation/API/qeetrix.api` ([0013](Documentation/Decisions/0013-public-api-baseline.md)).

`Scripts/new-component` scaffolds all of these from `Templates/Component/`, validated against the pinned
`component-manifest.json`; the scaffold does not build until it is implemented.
