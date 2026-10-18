# 0007 — Fonts are the web's fonts, as Android font resources

**Status:** Accepted, 2026-10-18

## Context

`@qeetrix/ui` 2.1.6 ships two variable fonts as WOFF2, a web container Android's font resources do not
accept: "Qeet Sans", which is Cal Sans v2.011, and Fira Code 6.2. Both are SIL OFL 1.1 with no Reserved
Font Name, so redistribution is permitted with the licence. The typography roles differ by Qeet Sans's
`GEOM` axis (letter style) as well as size and weight.

## Decision

- **Files.** Bundle each font's TTF from the same upstream release (calcom/sans `v2.011`, Fira Code
  `6.2`), unmodified, accepted only if that release's WOFF2 is byte-identical to the one in the pinned
  `@qeetrix/ui`. They are the same files qeetrix-ios bundles (sha256-identical).
- **Resources.** `res/font/qx_cal_sans_vf.ttf` and `res/font/qx_fira_code_vf.ttf`; the licences verbatim
  in `assets/qeetrix/licenses/`.
- **Axes.** Set as the web sets them, per role: `wght` from the weight, `GEOM` from the role's letter
  style, `opsz` from the size in sp (resolved with the font scale), `ital` 0. The axes and their ranges
  are read from the font files by the generator.
- **Weights.** Each role's family contains every weight of the scale, so Android's Bold Text setting
  lands on a real heavier instance.

## Consequences

- Android text uses the exact outlines, kerning and letter styles the web uses.
- Glyphs outside the Latin fonts (Indic and other scripts) fall back to the system fonts per character.
- A font upgrade starts upstream: qeetrix-ui ships it, the pin moves, and the sync proves the match
  again.
