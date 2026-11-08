# Boundaries — qeetrix-android

**Level:** L2 · **Last verified:** 2026-11-04
**Verification scope:** rules taken from the build's checks, the tests and the release workflows.

## Owns

The Android form of Qeetrix: generated Kotlin tokens, the theme, fonts, icons and the Compose components, their tests
and sample pages, and the release of `com.qeetgroup:qeetrix` to GitHub Packages.

## Does not own

| Not owned | Owner |
|---|---|
| **Tokens, fonts, icons and how components look** | `@qeetrix/tokens`, `@qeetrix/icons`, `@qeetrix/react` (upstream) |
| The design rules | qeetrix-files (`qeetrix-react/05-design-rules.md`) |
| The documentation site, ui.qeet.in | `qeetrix-docs` |
| SwiftUI | `qeetrix-ios`, which pins the same packages and versions |
| Apps' navigation, data, permissions and validation | Each app |

## Consumes

`@qeetrix/tokens` 0.0.15, `@qeetrix/react` 0.5.1 and `@qeetrix/icons` 2.1.2, pinned and integrity-checked in
`Upstream/`.

## Rules that the build enforces

- **Never edit `Upstream/`, a `generated/` package, `docs/tokens.md` or `docs/token-values.json` by hand.** Sync and
  generate instead.
- **The public API changes only on purpose:** run `./gradlew :qeetrix:apiDump`, write KDoc, and note it in the
  CHANGELOG. Adding a parameter breaks binary compatibility: add an overload.
- **No Material type in the public API** (`MaterialBoundaryTest`), and no public `data class`.
- **Resource names start with `qx_`.** Lint warnings are errors; ktlint formats Kotlin.
- **Test coverage stays at or above 92 % of lines.**

## Rules every component follows

- **It is the counterpart of an `@qeetrix/react` component:** the same props, variants, tones and sizes, by the same
  names, with its CSS restated beside each literal. No colour or font parameters.
- **The app owns state.** Values, selections and `open` flags come in as parameters. Qeetrix owns no navigation, back
  stack, data or validation.
- **Accessibility:** a 48 dp touch target, a role, name and state for TalkBack, focus order that matches the layout,
  text that reflows at 200 % rather than being cut, motion through `qeetrixAnimationSpec` (honouring Remove
  animations), and colour never the only signal.
- **Right to left:** layouts mirror; text takes its direction from its content; icons mirror by meaning.
- **Secure input:** a password or code never appears in a label, description, semantics node or log, and isn't saved
  across configuration changes.
- **Complete:** tests that fail without the behaviour, a screenshot test, a specimen in the quality catalogue, and a
  sample page.

## Rules of the repository

- **Screenshots stay on the maintainer's machine.** They aren't committed and CI doesn't compare them.
- **Releases go through `main`:** a pull request with CI passing, then the owner's approval of the publish job.
- **Cross-platform:** a change to naming, colour conversion or token scope is made in `qeetrix-ios` too, or recorded as
  a difference.
