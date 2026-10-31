# Architecture

How Qeetrix for Android turns the published Qeetrix packages into a Compose library, and the rules that keep it
that way. Decisions are recorded in [Documentation/Decisions](Documentation/Decisions/README.md).

## The flow

```text
@qeetrix/tokens 0.0.13 (DTCG build, native TTFs) ─┐
@qeetrix/react 0.4.0 (component manifest, CSS) ───┼─▶ Scripts/sync-tokens ─▶ Upstream/ + res/font + licences
@qeetrix/icons 2.1.0 (SVG files, JSON catalogue) ──┘    (network, registry integrity, locked)
                                                                    │
                                     Scripts/generate-tokens (offline, deterministic)
                                                                    ▼
      qeetrix/…/tokens/generated, icons/generated (internal Kotlin) + Documentation/Foundations
                                                                    │
                  foundation/* (public types) ◀── theme/ (QeetrixTheme) ── icons/ (QeetrixIcon)
                                                                    │
     Upstream/qeetrix-react/css ┄┄ restated by hand ┄┄▶ components/* (QeetrixButton, …)
                                                                    ▼
             :qeetrix release AAR ──maven-publish──▶ com.qeetgroup:qeetrix (AAR + POM + module metadata + sources)
                    │                                        │  build/release-candidate/maven (local); a registry
                    ▼                                        ▼  only with the owner's approval (0018)
                :sample (project dependency)        apps, and Integration/Consumer (by coordinates)
```

Three steps, three commands:

- **Sync** (`Scripts/sync-tokens`, network): fetches the three npm tarballs pinned in `Upstream/manifest.json`
  and checks each against the npm registry's integrity hash, recording its git head.
  - From `@qeetrix/tokens` it copies the DTCG build (`dist/dtcg/`: the resolver, its source sets and the resolved
    light and dark documents) whole, and takes the TrueType fonts the package ships for native platforms
    (`dist/fonts/QeetSans.ttf`, `FiraCode-VF.ttf`, which exist for Android and iOS — QTRX-91). Each font is
    accepted only if its sha256, size and family match `fonts/native.json`, it has the axes the manifest lists, and
    the WOFF2 it was made from is byte for byte the one the web loads. Their licence texts come with them.
  - From `@qeetrix/react` it copies the component manifest (`dist/manifest.json`) and the compiled CSS of the
    families Android implements (`dist/css/`).
  - From `@qeetrix/icons` it takes each pinned icon's entry in the JSON catalogue (`dist/manifest.json`) and copies
    its plain SVG drawings (`dist/svg/<shape>-<variant>/<id>.svg`), checking that the package draws exactly the shapes
    and variants the entry declares. Its licence comes with it.

  It writes `Upstream/` and `Upstream/manifest.lock.json` (each package's version, git head and integrity; each
  font's sha256 and its WOFF2's; the sha256 of every file taken). `--check` re-fetches and compares, changing nothing.
- **Generate** (`Scripts/generate-tokens`, offline): checks the snapshot against the lock, then reads only the
  snapshot and the bundled fonts and writes the generated Kotlin and reports. The same snapshot always gives the
  same bytes.
- **Verify** (`Scripts/verify-tokens`, part of `./gradlew check`): fails if a locked file changed, a file in
  `Upstream/` is not in the lock, a generated file differs from a fresh generation, or a generated package holds a
  file the generator did not produce.

## Modules and dependency rules

One library module, one sample, one tool, and an independent consumer ([0001](Documentation/Decisions/0001-single-library-module.md)):

| Unit | Depends on | Never depends on |
|:--|:--|:--|
| `:qeetrix` | Compose runtime, UI, foundation, animation-core (`api`: their types are public); Material 3, for `QeetrixBottomSheet` alone (`implementation`, [0015](Documentation/Decisions/0015-navigation-overlays-and-feedback.md)) | Material for anything else, AndroidX Core directly, a navigation library, a networking, database or image-loading library, the generator |
| `:sample` | `:qeetrix` (public API only), activity-compose; Robolectric and Compose UI Test for its tests | `:qeetrix` internals (Kotlin `internal` makes them unreachable) |
| `Tools/QeetrixGen` | kotlinx-serialization-json, the JDK | Android, Node.js; it is a separate Gradle build |
| `Integration/Consumer` | `com.qeetgroup:qeetrix:<version>` from a Maven repository, activity-compose; AndroidX Test, Compose UI Test and UI Automator for its tests | `:qeetrix` as a project or composite build, its sources or internals; it is a separate Gradle build, the release acceptance test ([0018](Documentation/Decisions/0018-maven-distribution.md)) |

Inside `:qeetrix`:

| Package | Holds | May use |
|:--|:--|:--|
| `foundation.*` | Public types: `QeetrixColors`, `QeetrixTypography` and `QeetrixTextRole`, `QeetrixSpace`, `QeetrixSize`, `QeetrixRadius` / `QeetrixShapes`, `QeetrixBorder`, `QeetrixDensity`, `QeetrixShadow`, `QeetrixMotion`, `QeetrixBreakpoint`, `QeetrixFocus` and the accessibility helpers | `tokens.generated` (read values), `internal`, `theme` (to read the current theme) |
| `tokens.generated` | Internal generated data, expressed in foundation types | `foundation.*` types, `internal`, `R` |
| `theme` | `QeetrixTheme`, `QeetrixThemeMode`, the composition locals | `foundation.*`, `tokens.generated` |
| `icons`, `icons.generated` | `QeetrixIcon`, `QeetrixIconName`, `QeetrixIconShape`, `QeetrixIconVariant`; the drawings | `foundation`, `theme`, `internal` |
| `components.<family>` | One file per component (`QeetrixButton`, …) and its option enums | `foundation`, `theme`, `icons`, `internal`, `tokens.generated` (component tokens); inputs also `components.input`'s internal field parts |
| `tokens` | `QeetrixProvenance` | `tokens.generated` |
| `internal` | Spec types the generated files are written in; the components' shared helpers (the focus frame, the spinner ring, popup placement and motion, the action row, the ruled stack) | Compose, `foundation`, `theme` |

Generated code is never public. The public API is hand-written, and the generated code is written in
terms of it: `QeetrixLightColors = QeetrixColors(surfaceCanvas = …, …)` names every parameter, and
`QeetrixShadow.lightLayers()` is an exhaustive `when`. A token added or removed upstream is therefore
a compile error until the public API is changed on purpose; scale objects are covered by a reflection
test instead. Material 3 uses the same split (hand-written `ColorScheme`, generated internal tokens).

## Token generation

The generator reads `@qeetrix/tokens`' DTCG 2025.10 build the way its resolver says, as qeetrix-ios's does
([0004](Documentation/Decisions/0004-tokens-from-the-published-qeetrix-ui-build.md)). There is no CSS parsing: the
DTCG documents are the only token input.

- **Resolution.** Exactly as `qeetrix.resolver.json` says: the foundation set's sources in order, then one context
  of each modifier in `resolutionOrder` — `colorScheme` (`light`, `dark`), then `density` (`default`, `comfortable`,
  `compact`); a later definition of a token replaces an earlier one. Group `$type`s are inherited and references
  (`{a.b.c}`) are followed inside composite values too. Each colour scheme is resolved at the default density, and
  each density in the default colour scheme.
- **Proof.** The package also publishes the resolved `light.tokens.json` and `dark.tokens.json`; generation fails
  unless each holds exactly the tokens, types and final values the resolver gives, and unless the two schemes define
  the same tokens. A token that varies by both colour scheme and density fails too: no Kotlin type models it.
- **Scope.** A rule decides every token ([Scope.kt](Tools/QeetrixGen/src/main/kotlin/com/qeetgroup/qeetrix/gen/tokens/Scope.kt)):
  the semantic tier and the adopted `component.*` groups are generated (202 tokens in 0.0.13); `primitive.*`, the
  Indic font stack, `z.*` and the component groups Android does not implement are excluded with a reason (137). An
  unmatched token fails generation, so a new upstream group is a decision
  ([0011](Documentation/Decisions/0011-component-tokens-and-web-metrics.md)).
- **Units.** 1 CSS px = 1 dp; 1 rem = 16 dp; font sizes in sp; line height as a multiple of the size and letter
  spacing in em; durations in ms. A typography role's line height is authored as a length on the 4 px grid and its
  letter style (`GEOM`) as a `font.geometry.*` reference, both in `$extensions["in.qeet.qeetrix"]`; the generator
  checks the composite's ratio against the length and emits the `Float` ratio whose product with the size is the
  length exactly, so no line rounds up a pixel.
- **Colour** ([0005](Documentation/Decisions/0005-colour-conversion-per-css-color-4.md)). Every colour is OKLCH.
  CSS Color 4 conversion: sRGB as packed 8-bit `Color(0xFF…)` when inside sRGB, else Display P3 (gamut-mapped when
  outside P3); six-decimal rounding — the same matrices, gamut decision and rounding as qeetrix-ios's generator. The
  oracle is the `hex` `@qeetrix/tokens` records beside each colour's components, computed by its own build:
  `ColorMathTest` requires every generated colour's 8-bit sRGB rendition (P3 colours clipped) to equal it, and the
  coverage report restates the cross-check.
- **Outputs.** `tokens/generated/{QeetrixColorTokens, QeetrixShadowTokens, QeetrixTypographyTokens,
  QeetrixScaleTokens, QeetrixProvenanceTokens}.kt`, one `Qeetrix<Group>ComponentTokens.kt` per adopted group
  (internal), `icons/generated/QeetrixIconDrawings.kt`, `Documentation/Foundations/TOKEN_COVERAGE.md` (every token
  and its Kotlin name, every exclusion and its reason) and `token-values.json` (every token's DTCG source and value,
  for cross-platform comparison).

## Component values

`@qeetrix/tokens` has few component tokens (0.0.13: `command-palette`, `sidebar`, `timeline`, `tour`); Android adopts
`sidebar` and `timeline`, as qeetrix-ios does. Everything else a component draws — paddings, gaps, sizes the tokens
don't name — is in the family's CSS (`Upstream/qeetrix-react/css/`), and is restated by hand in the component file
with the CSS beside each literal, using the token wherever the CSS names one. The CSS is synced with every
`@qeetrix/react` pin, so its diff shows which components to revisit.

## Theme

`QeetrixTheme(mode)` resolves light or dark (`System` reads `isSystemInDarkTheme()` live) and provides,
through composition locals: the colours, the typography, the dark flag, the reduced-motion setting
(observed live from the animator duration scale), the content colour and text-selection colours
([0006](Documentation/Decisions/0006-theming-through-composition-locals.md)). Static locals for values that
change rarely; dynamic ones for the content colour and the density (`LocalQeetrixDensity`, set per region). Nested
`QeetrixTheme`s change only their subtree's colours and shadows. There is no global mutable state, and Material is
not involved.

## Typography

Each role is a `TextStyle` built once: a `FontFamily` of the role's font file at every weight of the scale, each
`Font` carrying the web's variation settings — `wght`, the role's `GEOM`, `opsz` from the size in sp (Compose
multiplies it by the font scale) and `ital` 0 — plus the size in sp, the line height in em with
`LineHeightStyle(Center, Trim.None)` (CSS half-leading) and the letter spacing in em
([0007](Documentation/Decisions/0007-fonts-are-the-webs-fonts-in-native-format.md)). The font files are
`@qeetrix/tokens`' own TrueType files (`qx_qeet_sans_vf.ttf`, `qx_fira_code_vf.ttf`); the generator describes each
from the file itself (name, version, axes). Android's Bold Text raises the requested weight and lands on a real
heavier instance. Non-Latin glyphs fall back to the system fonts.

## Shadows

Only floating layers have shadows (design rules §12). `Modifier.qeetrixShadow(shadow, shape, fill)` is a
`Modifier.Node` that reads the theme from the composition and draws like CSS `box-shadow`: each outer layer is the
shape grown by its spread, offset, blurred with σ = blur ÷ 2 (converted to Android's `BlurMaskFilter` radius) and
clipped to outside the shape; then the fill; then inset layers, clipped inside; then the content. Compose's
`dropShadow` was not used: it blurs with Skia's radius convention and does not clip outer shadows.

## Icons

The generator turns each pinned SVG into Compose path nodes — arcs and smooth curves expanded, ellipses
and rounded rectangles as cubics — and rejects anything outside the shared SVG subset
([0008](Documentation/Decisions/0008-icons-are-generated-vector-paths.md)). `QeetrixIcon` builds an
`ImageVector` for the stroke (2, or 1.5 at the compact density), in the sharp shape by default and the round one
in navigation, tints it with the content colour, mirrors it in RTL when the manifest says so, and exposes it to
TalkBack only when it has a `contentDescription`. An icon without the requested drawing falls back to its outline.

## Public API conventions

Explicit API mode; Compose types; PascalCase constants and enum entries; camelCase theme accessors;
upstream strings in `value`/`token`; `@qeetrix/react`'s prop names; no public `data class`; KDoc on everything
public. The rules and the change policy are in [Documentation/Standards/PUBLIC_API.md](Documentation/Standards/PUBLIC_API.md).

## Relationship to Qeetrix Web and iOS

- **Web** is the authority: tokens, fonts and icons are only ever changed upstream and arrive here by a
  pin bump; `@qeetrix/react` and its design rules decide how components look and behave.
- **iOS** pins the same packages and versions with the same rules; the verified parity and the deliberate
  differences are in [Documentation/CROSS_PLATFORM_CONTRACT.md](Documentation/CROSS_PLATFORM_CONTRACT.md).

## Components

Each component is one file in its family's package, `components.<family>`
([0010](Documentation/Decisions/0010-component-api-conventions.md)), is the counterpart of an `@qeetrix/react`
component, and follows [COMPONENT_CONTRACT.md](Documentation/Standards/COMPONENT_CONTRACT.md):

- **Built from foundation primitives**, not Material: `BasicText`, `BasicTextField` and
  `BasicSecureTextField` with a `TextFieldState`, `clickable` / `toggleable` / `selectable` with
  `indication = null` and the web's colour steps, `Popup` for the select list and the menu, `Dialog` windows for
  the dialogs and the bottom sheet, `progressSemantics`, `Canvas` for the spinner and progress. The exceptions are the
  date and time fields, which show the platform's own pickers (Material 3's) in Qeetrix's colours, and pull to
  refresh's nested scroll; `MaterialBoundaryTest` keeps Material in those files. The bottom sheet wrapped Material 3's
  `ModalBottomSheet` until QTRX-198.
- **State is the app's.** Selections, `open` flags, expansion, queries, load states and values come in as
  parameters, under `@qeetrix/react`'s names; Qeetrix keeps only presentation state and owns no navigation, back
  stack, Back handler or data ([NAVIGATION_AND_OVERLAYS.md](Documentation/Standards/NAVIGATION_AND_OVERLAYS.md),
  [DATA_DISPLAY_AND_COLLECTIONS.md](Documentation/Standards/DATA_DISPLAY_AND_COLLECTIONS.md)).
- **Collections are Compose's.** Rows, headers and content states go inside `LazyColumn` and its keyed
  `items` and `stickyHeader`s; Qeetrix adds no list container or virtualization
  ([0016](Documentation/Decisions/0016-data-display-and-content-states.md)).
- **Inputs share one field.** Every input is built on the internal field parts in
  `components/input/QeetrixFieldParts.kt` — the label above the control, inside the control's own layout; the
  filled box with its bottom line (`FieldBox`) per state; the help text or the error — and reads the enclosing field
  set through `LocalFieldGroup` ([0014](Documentation/Decisions/0014-form-components.md),
  [FORMS.md](Documentation/Standards/FORMS.md)).
- **Values** from the theme (`QeetrixTheme.colors`, `.typography`, `.density`), the foundation scales, the few
  generated component tokens and the family's CSS, restated with the CSS beside each literal.
- **Tests** beside it: behaviour and semantics with Compose UI Test on Robolectric, and one Roborazzi
  screenshot test across light, dark, 200% text and right to left, verified against local, reviewed
  baselines ([0012](Documentation/Decisions/0012-visual-regression-testing.md)).
- **Documentation** in KDoc (Dokka) and `Documentation/Components/<Component>.md`; a sample page with
  previews under the component's group.
- **API** held by `Documentation/API/qeetrix.api` ([0013](Documentation/Decisions/0013-public-api-baseline.md)).

`Scripts/new-component` scaffolds all of these from `Templates/Component/`, validated against the pinned
`@qeetrix/react` component manifest; the scaffold does not build until it is implemented.
