# Release-candidate report: `com.qeetgroup:qeetrix:0.1.0-rc.2`

The evidence for Qeetrix for Android's second release candidate, and the decision. 2026-10-31. The candidate is the
first built after the move to `@qeetrix/react` 0.4.0, `@qeetrix/tokens` 0.0.13 and `@qeetrix/icons` 2.1.0, with 31
of the 37 Android tickets in it; it supersedes [`0.1.0-rc.1`](#earlier-candidate-010-rc1).

## Decision: **CONDITIONAL GO** — an internal pre-release only

The candidate may be published **to a private Qeet Group registry, as a pre-release (`0.1.0-rc.2`), for Qeet Group's
Compose app teams to build against and evaluate**, once the owner has taken the actions in
[Owner approval](#owner-approval). It is **not** ready for an app to ship to production users, and not for any
distribution outside Qeet Group, until the conditions below are met.

Why not GO: no person has yet used the components with TalkBack, Switch Access or a keyboard on a device (rated P1
for a release since Phase 6); CI has never run; and the code has no licence for use outside the organisation. None of
these is a known defect, so the decision is not NO-GO; together they mean a production release would rest on
unverified ground.

### Conditions for GO

1. A manual **TalkBack, Switch Access and keyboard** pass on a physical phone, with every P0/P1 finding fixed
   ([RELEASE-READINESS-GAPS.md](../Quality/RELEASE-READINESS-GAPS.md) G1, G2).
2. `Scripts/release-candidate` re-run from a commit with nothing uncommitted (no `--allow-dirty`). This candidate was
   built from commit `9291aa1` on `develop`, pushed; the two uncommitted entries were an editor settings folder
   (`.vscode/`) and the dependency-inventory fix below, which writes the evidence and does not touch the artifact.
3. **CI green** on that commit — Actions billing restored — including the device jobs.
4. For distribution outside Qeet Group only: a **licence** for the code.

## What was verified

| Area | Result | Detail |
|:--|:--|:--|
| Release gates | **10 of 10 passed** | tokens · check · aar · screenshots · publish · publication · dependencies · consumer · library-devices · consumer-devices ([RELEASE-CHECKLIST.md](RELEASE-CHECKLIST.md)) |
| JVM tests | 492 library, 18 sample, 38 generator — all pass | Unit, Compose UI, accessibility scans, contrast, layout, the shared format and QR cases |
| Screenshots | 243 baselines match | Recorded and reviewed on this machine after the device pass |
| Static checks | Lint: no issues in either module; ktlint clean; API baseline unchanged; tokens current | |
| AAR | Valid; contents, manifest, metadata and resources as expected | [AAR-VERIFICATION.md](AAR-VERIFICATION.md) |
| Maven metadata | POM and module metadata correct; release variant only; dependencies from Google Maven and Maven Central | [MAVEN-DISTRIBUTION.md](MAVEN-DISTRIBUTION.md) |
| Independent consumer | Resolves by coordinates; debug and R8 release build; on the API 36 phone and the API 36 tablet 12/12 Compose tests and 4/4 release smoke tests | [CONSUMER-INTEGRATION.md](CONSUMER-INTEGRATION.md) |
| Library device tests | 16/16 on the API 36 phone; on the API 36 tablet 15/16 — the ATF check found rc2-2 — and 16/16 after its fix | ATF over every catalogue component and overlay window, Back on overlays, layout, insets |
| Components on a device | Every component added for the Android tickets tried one by one on the API 36 phone in light, dark, 200 % text and right to left, then on the API 36 tablet; every defect found was fixed, each with a test | [CHANGELOG.md](../../CHANGELOG.md) › Fixed on the device pass, Fixed on the tablet pass |
| Licences | All bundled assets OFL 1.1 / MIT / ISC, unmodified, texts included; 92 runtime dependencies Apache 2.0; SBOM of 95 components | [LICENSE-COMPLIANCE.md](LICENSE-COMPLIANCE.md) |
| Security and privacy | No permissions, components, networking, logging or persistence of input in the AAR; the QR scanner's camera permission is the app's to declare and ask for | [SECURITY.md](../../SECURITY.md) |
| Cross-platform | Token inputs and fonts byte-identical with qeetrix-ios; the same version and versioning policy | [CROSS_PLATFORM_CONTRACT.md](../CROSS_PLATFORM_CONTRACT.md) |

## Defects found in this candidate

| # | Severity | Defect | Status |
|:--|:--|:--|:--|
| rc2-1 | P2 | The dependencies gate failed: `Scripts/dependency-inventory` wrote the SBOM's font and icon entries from a GitHub repository and tag the lock file no longer records — since the migration they come from `@qeetrix/tokens` and `@qeetrix/icons` on npm | Fixed: the entries name the npm package, version and the file taken, with the registry's integrity hash; the gate passes |
| rc2-2 | P2 | On the tablet, `QeetrixBottomActionBar`'s `fullWidth` actions each took the bar's width from `breakpoint.medium`, so the row ran off the window's start edge and the first action was out of sight (ATF: a 24 dp touch target) | Fixed on `develop` after the candidate, with `QeetrixBottomActionBarTest`; not in the `0.1.0-rc.2` artifact |
| rc2-3 | P3 | On the tablet, `QeetrixFullScreenForm`'s form sheet kept the window's dim under the scrim, darkening the screen twice (`#3E3E3E` where the scrim gives `#999897`) | Fixed on `develop` after the candidate, with `QeetrixFullScreenFormTest`; not in the `0.1.0-rc.2` artifact |

## Evidence

`build/release-candidate/` on the build machine (not committed): the local Maven repository, its archive
`qeetrix-0.1.0-rc.2-maven.zip`, and `evidence/` — `PROVENANCE.md`, `SHA256SUMS`, `consumer-SHA256SUMS`,
`dependencies.md`, the CycloneDX SBOM `qeetrix-0.1.0-rc.2.cdx.json` (95 components), the notices, these release
notes and every gate's log.

| | |
|:--|:--|
| Source | `9291aa1` on `develop`, with 2 uncommitted entries (above); working-tree digest `f014774c…71315ae3d` |
| AAR SHA-256 | `ebb1a27b3545056293c3cf0cc5f8954c083919f9027b654fd0e6b4fa5fedfc48` |
| POM SHA-256 | `6d4cc6ef3504b53e12c0f9c0d871858174e8171e01df5f3658ba31242596b87e` |
| Module metadata SHA-256 | `14125cba6c5b922b2145e497331725654066f1e2c910678e8500f387fbedca73` |
| Sources JAR SHA-256 | `ff6f51e0c548cb181656f76eef7863768e5d285fdc5272e04a06ddfbb1f4f1c6` |
| Toolchain | Gradle 9.6.1, AGP 9.4.1, Kotlin 2.4.21, Compose BOM 2026.09.00, JDK 25.0.2, macOS arm64 |
| Upstream | `@qeetrix/react` 0.4.0; `@qeetrix/tokens` 0.0.13; `@qeetrix/icons` 2.1.0 at `e2a8f9f` |

Builds are not claimed to be bit-for-bit reproducible.

## Not verified for this candidate

- The API 29 phone and the minimum toolchain (AGP 9.1.0, Kotlin 2.3.0): run for `0.1.0-rc.1`, not again.
- Physical devices, foldables, resizable windows.
- Any manual assistive-technology use.
- Frame timing: no performance figure is claimed.
- Qeet ID Auth: it is a React Native app and cannot use this library as it stands.

## Owner approval

Before the internal pre-release:

1. **Build the next candidate** (`0.1.0-rc.3`, with rc2-2 and rc2-3 fixed) with `Scripts/release-candidate --devices` from a
   clean commit.
2. **Choose the registry** (GitHub Packages, private, is recommended) and create the protected publishing environment
   and credential ([MAVEN-DISTRIBUTION.md](MAVEN-DISTRIBUTION.md)).
3. **Approve that version**, publish it once, and tag its commit.

Before a production release: the four [conditions for GO](#conditions-for-go).

## Earlier candidate: `0.1.0-rc.1`

Built on 2026-10-20 from Phases 4–7, uncommitted at the time, on `@qeetrix/ui` 2.1.6 and `@qeetrix/icons` 1.0.11:
10 of 10 gates; 309 library, 15 sample and 44 generator tests; 147 screenshots; the consumer and library device tests
on the API 29 phone, the API 36 phone and the API 36 tablet; the minimum toolchain built and run. One P1 found by the
independent consumer — the bottom sheet drew an empty, sheet-sized panel at the top of the screen — was fixed then.
AAR SHA-256 `fe4531774b849afacc054a7c1f4c90290f0a6f0accd1d17601fa80cfb834beee`. It was never published, and every
public API has changed since.
