# Release and support policy

How Qeetrix for Android is versioned, what each kind of release may change, and what is supported. It is kept
small enough to be followed by the people who maintain the library today. Nothing has been released yet; the first
candidate is proposed below and needs the owner's approval.

## Versions

[Semantic Versioning 2.0](https://semver.org): `MAJOR.MINOR.PATCH`, with pre-releases `-alpha.N`, `-beta.N` and
`-rc.N`. Gradle orders them as SemVer does: `0.1.0-rc.1` < `0.1.0-rc.2` < `0.1.0`.

| Release | May change |
|:--|:--|
| Patch `x.y.Z` | Fixes only: behaviour that contradicts the documentation, accessibility and security defects. No public API change |
| Minor `x.Y.0` | Additions: components, parameters through new overloads, tokens. Before 1.0, also breaking changes, each listed under **Breaking** in the CHANGELOG with migration steps |
| Major `X.0.0` | From 1.0: breaking changes, after a deprecation (below) |
| Pre-release `-rc.N` | A candidate for the version before the suffix. A release is the approved candidate rebuilt from the same commit with the suffix removed |

What counts as breaking — binary (JVM signatures), source (names, parameter order), behavioural (what a component
does or draws), and dependency (the minimum SDK, Compose or AGP an app needs) — is in
[API-COMPATIBILITY.md](../Quality/API-COMPATIBILITY.md). Raising the minimum Android API level, the Compose baseline,
the compile SDK or the AGP an app needs is breaking.

## Before 1.0

The first release is **`0.1.0`**, proposed as candidate **`0.1.0-rc.1`**, not 1.0: the public API has a reviewed
baseline, but no product has used it yet, and the manual accessibility pass has not been done
([RELEASE-CANDIDATE-REPORT.md](RELEASE-CANDIDATE-REPORT.md)). Until 1.0:

- The binary API is checked on every build (`apiCheck`) and changes only on purpose, in a minor release.
- A breaking change comes with migration steps in the CHANGELOG and, where an alternative exists, one minor release
  in which the old form is `@Deprecated` with a `ReplaceWith`.
- 1.0 is declared when a product has shipped with the library and the release-readiness gaps that block it are
  closed.

## From 1.0

- Public API is removed only in a major release, after at least one minor release in which it was deprecated.
- Behaviour that apps can observe — what a component announces, draws or reports — changes only in a minor or major
  release, never a patch, unless the old behaviour was a defect.

## Fixes

- **Security and privacy** defects in Qeetrix (for example, sensitive text exposed to accessibility services or
  logs): a patch release on the latest minor, reported privately as [SECURITY.md](../../SECURITY.md) describes.
- **Accessibility** defects: by the severity model of [0017](../Decisions/0017-quality-gates-and-verification.md) —
  P0 and P1 in a patch release; P2 in the next release.
- Before 1.0 only the latest minor version is maintained.

## What is supported

| | Supported |
|:--|:--|
| Android | API 29 (Android 10) and newer; phones and tablets. Verified on API 29 and API 36 emulators ([COMPATIBILITY-MATRIX.md](COMPATIBILITY-MATRIX.md)) |
| An app's compile SDK and AGP | Compile SDK 37 and AGP 9.1 or newer: required by Compose 1.12, which Qeetrix uses |
| Kotlin and Compose | Compose 1.12 (BOM 2026.09.00) or newer; the Kotlin versions in [COMPATIBILITY-MATRIX.md](COMPATIBILITY-MATRIX.md) |
| Not supported | Wear OS, Android TV, Android Auto; View-based (XML) UI; Kotlin Multiplatform |

## Approval

Each of these is the owner's decision, never this repository's automation: the registry, a version to publish,
committing and tagging a release, publishing it, and a licence for the code. The steps are in
[RELEASE-CHECKLIST.md](RELEASE-CHECKLIST.md).

## Reporting and contributing

Defects are reported as issues in the repository. A change to a component follows the
[component contract](../Standards/COMPONENT_CONTRACT.md) and [CONTRIBUTING.md](../../CONTRIBUTING.md), and updates
the CHANGELOG and documentation in the same change. No support response time is promised.
