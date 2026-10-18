# Public API

Every Qeet Group Android app will compile against this library, so its public API is deliberate.

## Rules

- **Visibility.** Kotlin's explicit API mode is on: every public declaration states its visibility and
  type. `public` only for consumer API; `internal` for implementation (generated token data, spec types,
  helpers); `private` within a file. The sample can only see the public API.
- **No design values from apps.** Token types (`QeetrixColors`, `QeetrixTypography`) have internal
  constructors; scales are objects of constants. Apps choose roles, never values.
- **Names.**
  - Token names keep the upstream path: `color.text.on-brand` → `textOnBrand`, `space.2xl` → `Xl2`
    (a leading digit moves to the end, as on iOS).
  - Constants and enum entries are PascalCase (`QeetrixSpacing.L`, `QeetrixElevation.Raised`); theme
    accessors and instance properties camelCase (`QeetrixTheme.colors.textPrimary`).
  - Values stored or compared across platforms carry the upstream string in `value` (`light`,
    `label-compact`, `send`) or `token` (`elevation.raised`).
  - Composables are PascalCase nouns (`QeetrixTheme`, `QeetrixIcon`); modifiers are `Modifier.qeetrix…`.
- **Types.** Compose's own: `Color`, `Dp`, `TextUnit`, `TextStyle`, `Shape`, `Easing`. Durations are `Int`
  milliseconds, as Compose's animation specs take them.
- **Classes.** No `data class` in the public API (its `copy` and `componentN` are binary-compatibility
  traps); write `equals`, `hashCode` and `toString` by hand.
- **Documentation.** KDoc on every public declaration, naming the upstream token where there is one.

## Changing the API

Until the first release, the API may change, but every change is listed in `CHANGELOG.md`.

After the first release (semantic versioning):

- **Additions** are minor versions.
- **Removals and renames** are major versions, after at least one minor version in which the old symbol
  is `@Deprecated` with a `ReplaceWith` and a note in `CHANGELOG.md`.
- **Behaviour changes** that alter what an app draws for the same tokens are noted in `CHANGELOG.md`
  even when the signature is unchanged.

An upstream token rename reaches Kotlin as a compile error in the generated code until the public API
is updated deliberately, so no rename slips through silently.

## Automated checking — not yet in place

qeetrix-ios checks its API against a committed baseline. Kotlin 2.4's built-in ABI validation was tried
here and produced an empty dump for a library built with AGP 9's built-in Kotlin, so it would have
checked nothing; it was not adopted. Before the first release, re-evaluate it (or the Binary
Compatibility Validator) and commit a baseline to `Documentation/API/`.
