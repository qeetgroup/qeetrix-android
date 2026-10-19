# 0011 — Component tokens, and web metrics that are not tokens

**Status:** Accepted, 2026-10-18 (Phase 2). Refines [0004](0004-tokens-from-the-published-qeetrix-ui-build.md).

## Context

Components need two kinds of values that Phase 1 did not generate:

1. **Component tokens** (`component.<group>.*`): qeetrix-ui 2.1.6 defines 41 groups. Phase 1 excluded
   them all.
2. **Tailwind utility values** the web components use directly — heights (`h-7`), padding (`px-2.5`),
   gaps (`gap-1.5`), type steps (`text-sm`, `leading-snug`), `animate-spin` and the `sm`/`md`
   breakpoints. They come from Tailwind's default theme, not from `tokens.json`.

## Decision

- **Component tokens are adopted per group**, the same groups as qeetrix-ios:
  `Upstream/manifest.json` lists them (`qeetrixUI.componentTokens`: avatar, badge, button, card, input,
  progress, switch, typography). The generator emits one `internal object Qeetrix<Group>ComponentTokens`
  per group and lists the rest as excluded, with the reason, in `TOKEN_COVERAGE.md`. Naming a group
  upstream does not define fails generation. Coverage is 279 tokens generated, 604 excluded — the same
  as qeetrix-ios.
- **References stay references.** A component colour that names one semantic colour in both themes is a
  `@Composable` getter reading `QeetrixTheme.colors`, so it follows the theme. One that differs per
  theme (a `color-mix`, `transparent`) is `if (QeetrixTheme.isDark) … else …`. A size resolved through
  the density is `fun height(density: QeetrixDensity)`; an elevation reference is a `QeetrixElevation`;
  percentages become fractions and `em` values a `TextUnit`. The upstream expression is in each KDoc.
- **Component tokens are internal.** They are implementation details; the public surface stays the
  semantic tokens.
- **Tailwind values are restated once, with their source**, in the internal `QeetrixWebScale`
  (Tailwind 4.3.3, the version in qeetrix-ui's lockfile at the pinned commit). Component code writes the
  web class beside each use (`QeetrixWebScale.spacing(2.5f) // px-2.5`), so a reviewer can compare it
  with the web source line by line.
- **Breakpoints are window widths.** The web's `sm` (640 px) and `md` (768 px) become 640 and 768 dp of
  window width (`LocalWindowInfo`). qeetrix-ios uses size classes instead; on phones both give the web's
  mobile values.

## Upstream discrepancy

Component geometry is part of the design but not of the token contract: a change to Tailwind's defaults
or to a component's classes changes the web without changing `tokens.json`, and the drift check cannot
see it. Until qeetrix-ui publishes component geometry as tokens, component metrics are reviewed against
the web source whenever the `@qeetrix/ui` pin moves. A proposed upstream change: publish
`component.<group>.height`, `padding` and `gap` for every adopted group.

## Consequences

- Adopting a group is a one-line manifest change and a regeneration; `Scripts/new-component` adds it
  when the group exists.
- `QeetrixWebScale` is the one place a Tailwind value enters the library.
