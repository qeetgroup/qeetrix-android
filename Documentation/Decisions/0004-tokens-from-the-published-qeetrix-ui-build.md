# 0004 — Tokens come from the published `@qeetrix/ui` build

**Status:** Accepted, 2026-10-18

## Context

qeetrix-ui authors its tokens as DTCG JSON (`src/tokens/**`) — the canonical design definitions — and
compiles them with Style Dictionary. Its published package ships the compiled outputs: `tokens.json`
(values per theme), `tokens.css` (the runtime variables) and `index.css`. Reading the DTCG source
directly would mean re-implementing that compiler, which would then disagree with it in small ways.
qeetrix-ios made the same choice (its record 0002), and two native readers of the same artifacts can be
checked against each other.

## Decision

- **Input.** The compiled outputs of a pinned, published `@qeetrix/ui` (2.1.6), checked against the npm
  registry's integrity hash at sync time. The DTCG source remains the authority; Android reads its
  official build.
- **Resolution.** The generator resolves what the web resolves at runtime: `var()` references (including
  the shadcn bridge and the `--radius-*` scale in `index.css`), `calc()`, `min()`/`max()`,
  `color-mix(in oklab, …)`, `rgb()`/`oklch()` with alpha, and `box-shadow` lists. Unknown syntax fails
  generation by name.
- **Scope.** The rules are qeetrix-ios's: the consumable `--qx-*` layer (semantic tokens), plus the scales
  qeetrix-ui itself hands to code. Palette primitives are never generated. Every upstream token is
  either generated or excluded with a written reason; an unknown consumable group fails generation.
  Component tokens arrive in Phase 2 with their components.
- **Provenance.** `Upstream/manifest.lock.json` records the package version, its git head and integrity,
  and the sha256 of every file taken; `QeetrixProvenance` exposes the versions at runtime.

## Consequences

- 2.1.6 yields 217 generated tokens and 666 excluded ones — the same split as qeetrix-ios.
- A token change happens upstream, is released, and arrives through a pin bump, `Scripts/sync-tokens`
  and `Scripts/generate-tokens`. Android can trail the web by a release but never contradicts one.
