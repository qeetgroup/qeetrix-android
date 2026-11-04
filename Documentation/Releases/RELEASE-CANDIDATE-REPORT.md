# Release report: `com.qeetgroup:qeetrix:0.1.0`

The decision to release Qeetrix for Android 0.1.0, and its evidence. Written on 4 November 2026 for the release on
9 November 2026. The release candidates before it are summarised at the end.

## Decision: **GO**, to GitHub Packages

The owner's decisions, 4 November 2026:

- **Release 0.1.0 to GitHub Packages**, through the Release workflow, after the owner approves it in the protected
  `maven-publish` environment ([MAVEN-DISTRIBUTION.md](MAVEN-DISTRIBUTION.md#publishing)). Maven Central comes later,
  if needed.
- **TalkBack, Switch Access and keyboard by a person are a known gap for 0.1.0**, not a blocker
  ([QUALITY.md](../QUALITY.md#-known-gaps) G1, G2).

### The conditions set at `0.1.0-rc.2`, and where they stand

| # | Condition | Status |
|:--|:--|:--|
| 1 | A manual TalkBack, Switch Access and keyboard pass | **Accepted as a known gap** by the owner. The automated semantics checks and the Accessibility Test Framework pass. |
| 2 | Every release gate run again from a clean commit | **At release:** the Release workflow runs `Scripts/release-candidate` on the merge commit, and again before it publishes |
| 3 | CI green, device tests included | ✅ **Pull request #4, on `2f08b04`:** every check passes |
| 4 | A licence | ✅ **MIT**, since 2 November 2026 |

## Evidence

| Area | Result |
|:--|:--|
| ✅ **CI**, pull request #4 on `2f08b04` | Build, Lint, ktlint, every JVM test, the public API check, test coverage, the AAR check, the local publication and the independent consumer: pass |
| ✅ **Device tests** | The library's and the independent consumer's tests on Android 10 (API 29) and Android 16 (API 36) emulators: pass |
| ✅ **Code scanning** | CodeQL over the Kotlin code and the workflows: pass |
| ✅ **Test coverage** | 94.0 % of lines (Kover); CI fails below 92 % |
| ✅ **Screenshots** | 263 of 263 match the reviewed baselines (4 November 2026, on the maintainer's machine) |
| ✅ **By hand** | Every component added for the Android tickets, one by one, on phone and tablet emulators (31 October); the owner's look through the sample on an Android 17 emulator (4 November) |
| ✅ **`main` protected** | Changes only through a pull request with these five checks passing |

### At release, automatically

1. **The gate** checks `0.1.0` is not yet published and that `maven-publish` has a required reviewer, then runs every
   release gate on the merge commit. The device gates ran on the pull request, where `main`'s protection requires them.
2. **The publish job** waits for the owner's approval, verifies the commit again, publishes, and checks the files sent
   against the verified `SHA256SUMS`.
3. **It attests the files' build provenance,** then tags `v0.1.0` and opens the GitHub Release from the CHANGELOG,
   with the AAR, sources, POM, SBOM and checksums attached. The workflow run keeps the full evidence for 90 days.

## Not verified for 0.1.0

- A person using TalkBack, Switch Access, Voice Access or a keyboard (accepted, above)
- Physical devices, foldables and resizable windows
- Frame timing: no performance figure is claimed
- Translations: the built-in strings are English
- Qeet ID Auth as a consumer: it is a React Native app

## Earlier candidates

| Candidate | Built | Result |
|:--|:--|:--|
| `0.1.0-rc.2` | 31 October 2026, from `9291aa1` | 10 of 10 gates. Three defects found, all fixed since: the SBOM step's font and icon entries (rc2-1), the bottom action bar running off a tablet's screen (rc2-2), and the form sheet darkening the screen twice (rc2-3). AAR SHA-256 `ebb1a27b…fedfc48`. |
| `0.1.0-rc.1` | 20 October 2026, on `@qeetrix/ui` 2.1.6 | 10 of 10 gates. One P1 fixed then: the bottom sheet drew an empty panel at the top of the screen. Superseded by the migration. |

Neither was published.
