# 0019 — Migrate to `@qeetrix/react`, `@qeetrix/tokens` and `@qeetrix/icons` 2.0

**Status:** Accepted, 2026-10-30. Supersedes [0004](0004-tokens-from-the-published-qeetrix-ui-build.md) and
[0011](0011-component-tokens-and-web-metrics.md); replaces the sources in [0007](0007-fonts-are-the-webs-fonts-in-native-format.md)
and [0008](0008-icons-are-generated-vector-paths.md) and the naming source in
[0010](0010-component-api-conventions.md). Taken with qeetrix-ios, which records the same shared decisions.

## Context

Qeetrix's web library moved from `@qeetrix/ui` 2.1.6 to **`@qeetrix/react`** (Base UI, plain CSS per family), whose
tokens are a separate package, **`@qeetrix/tokens`** (DTCG 2025.10 with a resolver: one foundation set, a colour-scheme
modifier and a density modifier), and whose icons are **`@qeetrix/icons` 2.0** (Lucide, round and sharp, outline and
filled). The look changed with it: design direction C, "Structured" (qeetrix-files `qeetrix-react/04-design-language.md`,
`05-design-rules.md`): square 4 dp corners, filled fields with a strong bottom line, label-first buttons with a trailing
icon zone, Ember only where it means something, the 3 dp Ember bar for the current item, ruled surfaces with shadows only on
floating layers. Nothing on Android had been published, so the API could change freely.

## Decision

1. **Pins** (`Upstream/manifest.json`): `@qeetrix/react` 0.4.0, `@qeetrix/tokens` 0.0.13, `@qeetrix/icons` 2.1.0 — the
   npm-published tarballs, each checked against the registry's integrity hash by `Scripts/sync-tokens` and every file taken
   hashed in `Upstream/manifest.lock.json`. `@qeetrix/ui` is no longer an input. (The migration started on react 0.2.2 /
   tokens 0.0.11; the owner bumped the pins once QTRX-91 and QTRX-92 were fixed, neither bump changing a token or a
   mapped family's CSS; then to 0.4.0 / 0.0.13 / 2.1.0, which fix Android's findings, QTRX-157 to 189.)
2. **Tokens** come from the DTCG build in `@qeetrix/tokens`, resolved exactly as `qeetrix.resolver.json` says: the
   foundation set's sources in order, then one context of each modifier (`colorScheme` light/dark, `density`
   default/comfortable/compact), with group `$type`s inherited and references followed inside composites. The generator
   also resolves the published `light.tokens.json` and `dark.tokens.json` and fails unless they agree with the resolver
   token for token. No CSS is parsed. Colours keep [0005](0005-colour-conversion-per-css-color-4.md)'s CSS Color 4
   conversion of the OKLCH components; the `hex` the package records beside each colour is the independent oracle (it is
   culori's, the web's converter) and every colour matches it.
3. **Scope.** The package's tiers decide: `primitive.*` is never generated; every public token is generated or excluded
   with a reason; z-index and the Indic font stack are excluded; component tokens only for adopted groups (`sidebar`,
   `timeline`). 202 generated, 136 excluded (`Documentation/Foundations/TOKEN_COVERAGE.md`).
4. **Names** follow the token path in Kotlin idiom, one rule: colours are `QeetrixColors` properties, camelCase of the path
   after `color.` (`color.bg.brand.bold` → `bgBrandBold`); shadows `QeetrixShadow` entries; typography roles
   `QeetrixTextRole` entries and `QeetrixTypography` properties (`typography.body.md` → `BodyMd` / `bodyMd`); tokens the
   density modifier changes are `QeetrixDensity` properties, camelCase of the whole path (`size.control.md` →
   `sizeControlMd`); every other token is a constant in `Qeetrix<Group>`, PascalCase of the rest of its path
   (`space.inset.md` → `QeetrixSpace.InsetMd`; `icon.stroke` and `font.geometry` keep two segments,
   `QeetrixIconStroke.Thin`, `QeetrixFontGeometry.Body`).
5. **Typeface.** The TrueType files `@qeetrix/tokens` ships for native platforms since 0.0.12 (`fonts/QeetSans.ttf`,
   `fonts/FiraCode-VF.ttf`), accepted only when their sha256, size and family match `fonts/native.json` and the WOFF2
   each was made from matches the one the web loads. Every axis is kept (Qeet Sans: opsz, GEOM, wght, YTAS, SHRP, ital).
   Each role carries the web's `font-variation-settings: "GEOM" …` — display Geo (100), heading and title Base (50),
   body, label and code A11y (0), as each composite's `$extensions["in.qeet.qeetrix"].fontGeometry` names it — and
   `opsz` follows the rendered size, as `font-optical-sizing: auto` does. Line heights are the composite's lengths on
   the 4 px grid (`$extensions["in.qeet.qeetrix"].lineHeight`), exact in Compose's `Float` arithmetic.
6. **Icons** are generated from `@qeetrix/icons` for the subset the components and the sample draw (41): the sync
   copies 2.1.0's plain SVG files (`dist/svg/<shape>-<variant>/<id>.svg`) and reads its JSON catalogue
   (`dist/manifest.json`), and the existing SVG-to-path generator does the rest. (2.0.0 shipped neither, and the sync
   rebuilt each drawing from the React modules; 2.1.0's files are byte-identical to what it rebuilt.) No pinned name
   was renamed in 2.0.
   `QeetrixIcon` draws the sharp shape by default (§16), round in navigation, filled only for a current or selected item.
7. **Components.** Every existing component is re-specified to its `@qeetrix/react` counterpart: variant, tone, size and
   parameter names from react's props in Kotlin idiom (enums with react's string as `value`; `enabled` for react's
   `disabled`); metrics, states, motion, focus and colour use from react's compiled CSS (copied to
   `Upstream/qeetrix-react/css/` for the 42 families Android maps, so a pin bump shows how each look changed) and the
   design rules. Raw px the CSS writes are restated once in the component file with the CSS declaration beside them;
   `QeetrixWebScale` (Tailwind) is gone. Where the CSS contradicts the spec, the CSS is followed unless it is clearly a bug,
   and each case is reported. Components with no counterpart (Chip, ListItem, SectionHeader, the navigation bar, rail and
   scaffold) are kept and restyled to the design rules, not redesigned. Composable names, files and packages stay.
8. **Platform conventions stay**: 48 dp touch targets, TalkBack semantics, system Back and Escape for overlays, no ripple,
   sp text that grows rather than clips, right-to-left mirroring, reduced motion (every duration 0, as the web's
   `prefers-reduced-motion` does).

## Consequences

- Breaking changes everywhere in the public API; the old-to-new mapping is in
  [MIGRATION_QEETRIX_REACT.md](../MIGRATION_QEETRIX_REACT.md) and `Documentation/API/qeetrix.api` is the new baseline.
- The pipeline is simpler: no CSS resolution, no culori vectors, no font conversion. Upstream inputs are three tarballs.
- Every design decision is still made upstream; Android's own choices are the platform conventions above and the bug
  workarounds the migration doc lists, each with the upstream issue it waits on.
- qeetrix-ios reads the same pins and emits the same `token-values.json` file, so token parity is a diff.
