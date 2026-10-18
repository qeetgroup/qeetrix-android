# 0008 — Icons are generated vector paths

**Status:** Accepted, 2026-10-18

## Context

qeetrix-icons draws on a 24-unit grid, round and sharp, outline and filled, in `currentColor`, with
Lucide's names. The web lets a stroke-width token change outline thickness at run time. VectorDrawable
XML resources bake the stroke width in at build time and would need one resource per stroke.

## Decision

- **Conversion.** At generation time the pinned SVGs become Compose path nodes (`PathNode`). Arcs and
  smooth curves are expanded to cubics; circles and rounded rectangles become cubic Béziers (Compose
  paths have no ellipse primitive; the error is under 0.03% of the radius).
- **Accepted SVG.** The same strict subset qeetrix-ios accepts. Transforms, groups, masks, gradients,
  hard-coded colours and per-element stroke widths fail generation.
- **Rendering.** `QeetrixIcon` builds an `ImageVector` for the requested stroke token (it scales with
  the icon), tints it with the content colour, and mirrors it in right-to-left layouts when the
  manifest says `directionality: "mirror"`.
- **Accessibility.** `contentDescription` is required: `null` makes the icon decorative and invisible to
  TalkBack; a string makes it an image with that label.
- **Scope.** Phase 1 carries qeetrix-ios's 12 icons. Logos are multi-colour and the Qeet logo uses an SVG
  mask, which native vector formats lack; they wait for a decision shared with iOS.

## Consequences

- Vectors stay exact at every size and are testable on the JVM; no bitmaps are shipped.
- Adding an icon is a name in `Upstream/manifest.json`, then sync and generate. Migrating the full
  catalogue (1,863 names) is a later phase and should keep drawings lazily built, as they are now.
