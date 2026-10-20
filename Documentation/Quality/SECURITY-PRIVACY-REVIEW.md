# Security and privacy review (Phase 6)

A design-system-level review of qeetrix-android at the Phase 6 working tree (2026-10-20). It covers what the
library itself does with data, resources and dependencies. It is not a security assessment of any app that uses
it. The standing policy is [SECURITY.md](../../SECURITY.md).

## Verdict

No P0 or P1 security or privacy risk was found. One P3 hygiene item was fixed (a test used the company's real
mail domain). The library makes no network requests, declares no permissions or components, persists nothing
the user types (only the password field's reveal flag), and logs nothing.

## What was checked, and how

| Area | Method | Result |
|:--|:--|:--|
| Logging | Search of `qeetrix/src/main` for `Log.`, `println`, `printStackTrace`, `Timber` | None |
| Clipboard | Search for `ClipboardManager`, `Clipboard` | None: no component copies a value |
| Analytics, network | Search for HTTP clients and `java.net`; the locked runtime classpath | None; no networking, database or image-loading dependency |
| Persistence of input | Every `rememberSaveable` in the library | Only `QeetrixPasswordInput`'s reveal flag; text lives in the app's `TextFieldState` |
| Sensitive text in accessibility | `QeetrixPasswordInputTest` scans every semantics node for the password; Phase 6 re-ran it | Never in a label, description or state; the platform exposes a password field to accessibility services as a password, masked |
| `toString()` of public types | Reviewed: `QeetrixOption`, `QeetrixTab`, `QeetrixFormError`, theme types | They print labels and values the app displays, never field text; no public type holds typed text |
| Exceptions | Every `require`/`check` message in `qeetrix/src/main` | Describe the rule broken ("two options share one value"), never a value |
| AutoFill | `QeetrixPasswordInput` (`Password`, `NewPassword`), `QeetrixOTPInput` (`SmsOtpCode` unless turned off), `QeetrixTextField`/`Textarea` (`contentType`) | Types set so password managers work; nothing disables AutoFill; the search field sets none |
| SMS, credentials, passkeys | Search for SMS, `CredentialManager`, Play services | None: Qeetrix reads no messages and holds no credentials |
| Error presentation | `QeetrixDataState` | Shows the app's message, never an exception's ([0016](../Decisions/0016-data-display-and-content-states.md)) |
| Test and sample data | Search of `qeetrix/src/test`, `sharedTest`, `androidTest`, `sample/src` for addresses, passwords, tokens and keys | Synthetic only: `example.com` addresses, "Synthetic-Passw0rd", "not-a-real-pw", short numeric codes. One test typed `ada@qeet.in` (the company's real domain): changed to `ada@example.com` (P3) |
| Screenshots | `.gitignore`, `git status` | Baselines are local and untracked ([0012](../Decisions/0012-visual-regression-testing.md)); they show synthetic data only |
| Manifest | `qeetrix/src/main/AndroidManifest.xml` and the built AAR's merged manifest | `<manifest>` with `uses-sdk` only: no permissions, components, exported entries or application attributes |
| Resources | `resourcePrefix = "qx_"`, `Scripts/verify-aar` | Every resource is `qx_`-prefixed (two fonts, 21 strings); three licence texts under `assets/qeetrix/licenses/` |
| R8 | The sample's release is now minified and resource-shrunk ([RELIABILITY-REPORT.md](RELIABILITY-REPORT.md)) | The library needs and ships no consumer rules |
| Dependencies | `qeetrix/gradle.lockfile` (strict, release classpaths); OSV batch query of all 92 locked runtime artifacts on 2026-10-20 | AndroidX, Kotlin/kotlinx, JetBrains annotations, JSpecify, Guava's empty `listenablefuture` — all Apache-2.0; OSV listed **no** advisories |
| CI supply chain | `.github/workflows/ci.yml` | Third-party actions were pinned to moving tags; now pinned to commit SHAs (P3, fixed) |
| Licences of bundled assets | `Documentation/THIRD_PARTY_NOTICES.md`, the AAR | Cal Sans and Fira Code (SIL OFL 1.1) and qeetrix-icons (MIT, with Lucide's ISC and Feather's MIT notices), each with its licence text in the AAR |

## Dependencies the library hands to apps

From `qeetrix/gradle.lockfile`, `releaseRuntimeClasspath` (92 artifacts): Compose runtime, UI, foundation,
animation and Material 3 (the bottom sheet only), with their AndroidX transitive dependencies (lifecycle,
savedstate, activity, core, collection, annotation, emoji2, window, autofill, profileinstaller, startup, tracing
and others), kotlin-stdlib and kotlinx-coroutines and -serialization-core. Phase 6 added test-only dependencies
(`ui-test-junit4-accessibility`, which brings the Accessibility Test Framework 4.1.1, Apache-2.0); the release
classpaths did not change.

The OSV result is a point-in-time check of known advisories, not an audit of the dependencies' code. Re-run it
before a release.

## Not covered (the consuming app's responsibility)

- Whether a screen should hide sensitive content from screenshots and the recents preview (`FLAG_SECURE`).
- What an app logs, stores, sends or shows in its own text, snackbars and dialogs.
- Image loading and its credentials ([DATA_DISPLAY_AND_COLLECTIONS.md](../Standards/DATA_DISPLAY_AND_COLLECTIONS.md#images)).
- Authorisation: a confirmation dialog records a choice; whatever performs the action checks permission.
