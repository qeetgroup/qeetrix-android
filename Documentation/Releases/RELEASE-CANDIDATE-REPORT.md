# Release-candidate report: `com.qeetgroup:qeetrix:0.1.0-rc.1`

The evidence for Qeetrix for Android's first release candidate, and the decision. Phase 7, 2026-10-20.

## Decision: **CONDITIONAL GO** — an internal pre-release only

The candidate may be published **to a private Qeet Group registry, as a pre-release (`0.1.0-rc.1`), for Qeet Group's
Compose app teams to build against and evaluate**, once the owner has taken the three actions in
[Owner approval](#owner-approval). It is **not** ready for an app to ship to production users, and not for any
distribution outside Qeet Group, until the conditions below are met.

Why not GO: no person has yet used the components with TalkBack, Switch Access or a keyboard on a device (rated P1
for a release since Phase 6); the candidate was built from an uncommitted working tree; CI has never run; and the
code has no licence for use outside the organisation. None of these is a known defect, so the decision is not NO-GO;
together they mean a production release would rest on unverified ground.

### Conditions for GO

1. A manual **TalkBack, Switch Access and keyboard** pass on a physical phone, with every P0/P1 finding fixed
   ([RELEASE-READINESS-GAPS.md](../Quality/RELEASE-READINESS-GAPS.md) G1, G2).
2. The work **committed**, and `Scripts/release-candidate` re-run from that clean commit (no `--allow-dirty`).
3. **CI green** on that commit — Actions billing restored — including the API 29 and 36 device jobs.
4. For distribution outside Qeet Group only: a **licence** for the code.

## What was verified

| Area | Result | Detail |
|:--|:--|:--|
| Release gates | **10 of 10 passed** | tokens · check · aar · screenshots · publish · publication · dependencies · consumer · library-devices · consumer-devices ([RELEASE-CHECKLIST.md](RELEASE-CHECKLIST.md)) |
| JVM tests | 309 library, 15 sample, 44 generator — all pass | Unit, Compose UI, accessibility scans, contrast, layout |
| Screenshots | 147 baselines match | The bottom sheet's 4 re-recorded after review (Phase 7 fix) |
| Static checks | Lint: no issues in either module; ktlint clean; API baseline unchanged; tokens current | |
| AAR | Valid; contents, manifest, metadata and resources as expected | [AAR-VERIFICATION.md](AAR-VERIFICATION.md) |
| Maven metadata | POM and module metadata correct; release variant only; dependencies from Google Maven and Maven Central | [MAVEN-DISTRIBUTION.md](MAVEN-DISTRIBUTION.md) |
| Independent consumer | Resolves by coordinates; debug and R8 release build; on devices 12/12 Compose tests and 4/4 release smoke tests on each of the API 29 phone, API 36 phone and API 36 tablet, and in landscape | [CONSUMER-INTEGRATION.md](CONSUMER-INTEGRATION.md) |
| Library device tests | 11/11 on API 29, 13/13 on the API 36 phone and tablet | ATF over every catalog component on API 36 |
| Toolchain range | AGP 9.1.0 + Kotlin 2.3.0 builds and runs; Kotlin 2.2.21 refused (as Kotlin's metadata rules require) | [COMPATIBILITY-MATRIX.md](COMPATIBILITY-MATRIX.md) |
| Licences | All bundled assets OFL 1.1 / MIT / ISC, unmodified, texts included; 92 runtime dependencies Apache 2.0 | [LICENSE-COMPLIANCE.md](LICENSE-COMPLIANCE.md) |
| Security and privacy | No permissions, components, networking, logging or persistence of input; the Phase 6 review stands; no credential or upload path in the build or CI | [SECURITY.md](../../SECURITY.md) |
| Cross-platform | Token inputs and fonts byte-identical with qeetrix-ios; the same version and versioning policy | [CROSS_PLATFORM_CONTRACT.md](../CROSS_PLATFORM_CONTRACT.md) |

## Defects found in Phase 7

| # | Severity | Defect | Status |
|:--|:--|:--|:--|
| 7-1 | **P1** | The bottom sheet drew an empty, sheet-sized panel at the top of the screen on every device, hiding the dimmed screen there. Found only by the independent consumer | Fixed; `QeetrixBottomSheetDeviceTest` reproduces it (failed before, passes on all three devices after) |
| 7-2 | P3 | `QeetrixBottomSheet`'s `modifier` would draw in the wrong place if an app passed a drawing modifier (Material's ordering) | Documented in KDoc and the component page |

Test-infrastructure faults fixed on the way, none in the library: the consumer's own tests had races (window focus,
stale nodes, an accessibility tree not yet published) and portrait-only assumptions; a new device test sampled the
wrong pixels on a tablet and on API 29's navigation bar. Each was diagnosed before it was changed.

## Evidence

`build/release-candidate/` on the build machine (not committed): the local Maven repository, its archive
`qeetrix-0.1.0-rc.1-maven.zip`, and `evidence/` — `PROVENANCE.md`, `SHA256SUMS`, `consumer-SHA256SUMS`,
`dependencies.md`, the CycloneDX SBOM `qeetrix-0.1.0-rc.1.cdx.json` (95 components), the notices, these release
notes and every gate's log.

| | |
|:--|:--|
| Source | `5b4cb5b` on `phase-4-navigation-overlays-feedback`, **with 219 uncommitted changes** (Phases 4–7); working-tree digest `81ec3c87…5796866` |
| AAR SHA-256 | `fe4531774b849afacc054a7c1f4c90290f0a6f0accd1d17601fa80cfb834beee` |
| POM SHA-256 | `72478167bea4e7d0294e56d5c7a1e4a7d345e5e5c60b6eaaf726ffd807e70a4f` |
| Module metadata SHA-256 | `b246b8b2f3ebd3cb175547b0eb538fd19702ddcfbd51cbd6a324b2f7e75b3f6f` |
| Sources JAR SHA-256 | `23a2037caea284afdd971ec0f510e4e708d710d3115d20fd9886390af46d2b33` |
| Toolchain | Gradle 9.6.1, AGP 9.4.1, Kotlin 2.4.21, Compose BOM 2026.09.00, JDK 21.0.9, macOS arm64 |
| Token source | `@qeetrix/ui` 2.1.6; `@qeetrix/icons` 1.0.11 at `331cd85` |

This run's `PROVENANCE.md` repeats the version-catalog line and lists an empty "files" token source; both are
formatting faults in the script, fixed after the run. Builds are not claimed to be bit-for-bit reproducible.

## Not verified

- Physical devices, foldables, resizable windows, predictive back, system-wide right to left in dialogs.
- Any manual assistive-technology use (TalkBack was started on an emulator but could not be driven from automation).
- Frame timing: no performance figure is claimed.
- Qeet ID Auth: it is a React Native app and cannot use this library as it stands.

## Owner approval

Before the internal pre-release:

1. **Commit** Phases 4–7 (they are uncommitted on the local branch) and re-run `Scripts/release-candidate 0.1.0-rc.1
   --devices` from the clean commit.
2. **Choose the registry** (GitHub Packages, private, is recommended) and create the protected publishing environment
   and credential ([MAVEN-DISTRIBUTION.md](MAVEN-DISTRIBUTION.md)).
3. **Approve the version** `0.1.0-rc.1`, publish it once, and tag the commit `v0.1.0-rc.1`.

Before a production release: the four [conditions for GO](#conditions-for-go).
