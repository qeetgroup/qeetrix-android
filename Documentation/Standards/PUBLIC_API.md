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
  - Components are `Qeetrix<Component>`, their options top-level enums `Qeetrix<Component><Axis>` with the
    web's names (`QeetrixButtonVariant.Outline`, `value = "outline"`)
    ([0010](../Decisions/0010-component-api-conventions.md)).
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

## Automated checking

`Documentation/API/qeetrix.api` is the accepted public API, dumped from the release AAR with the
binary-compatibility-validator library ([0013](../Decisions/0013-public-api-baseline.md)).

- `./gradlew :qeetrix:apiCheck` (part of `check`, so CI runs it) fails when the API differs from the
  baseline.
- After an intended change, run `./gradlew :qeetrix:apiDump` and commit the updated file with the change
  and its `CHANGELOG.md` entry; the reviewer reads the surface change as a diff.
- The dump leaves out the Compose compiler's `ComposableSingletons$…` classes: they are public on the JVM
  so composables can share lambdas, but no app can name them, and they change whenever a lambda does.
- Adding a defaulted parameter keeps callers compiling but replaces the composable's JVM signature in the
  dump (one line removed, one added). Before the first release that is allowed and noted in the
  CHANGELOG; after it, add an overload instead and deprecate the old one.
