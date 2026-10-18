# 0006 — Theming through composition locals

**Status:** Accepted, 2026-10-18

## Context

The web has three theme modes (`light`, `dark`, `system`) and nested `.dark` scopes. Qeet Group uses
one brand across every product: users choose comfort settings — theme, text size, motion — not colours.
Material 3's `MaterialTheme` is not the design authority, and its colour roles do not match Qeetrix's.

## Decision

- **`QeetrixTheme(mode) { }`** provides the theme's values through composition locals; the
  `QeetrixTheme` object reads them (`QeetrixTheme.colors`, `.typography`, `.isDark`, `.reduceMotion`).
- **Static locals** for colours, typography, the dark flag and reduced motion: they change rarely and
  a change should redraw the subtree. A **dynamic** local for the content colour, which components
  change often and locally.
- **Nested themes** override only their subtree's colours. Typography and every non-colour value are
  theme-invariant.
- **No global state** and no public constructor for `QeetrixColors` or `QeetrixTypography`: themes come
  from upstream through the generator, never from app code.
- **System settings** are read live: dark theme (`isSystemInDarkTheme`) and Remove animations (the
  animator duration scale, observed for changes).
- **Increased contrast** has no upstream values yet, so nothing changes under Android's contrast
  settings; it is not invented here.

## Consequences

- Configuration changes, live dark-mode switches, nested scopes and previews need no extra plumbing.
- A new upstream theme fails generation until it is mapped (`TokenSource.THEME_SELECTORS`, then
  `QeetrixThemeMode` and `QeetrixTheme`).
