# Release checklist

From a commit to a published version. Steps marked **Owner** are decisions no script or workflow takes; everything
else is automated and fails loudly. The policy is [RELEASE-POLICY.md](RELEASE-POLICY.md).

## 1. Prepare (maintainer)

- [ ] A pull request from `develop` into `main`. `qeetrix.version` in `gradle.properties` is the version to release:
      `version.yml` raises the patch, and a minor or major is raised by hand.
- [ ] `CHANGELOG.md` has the version's entry, headed `## [X.Y.Z] — YYYY-MM-DD`: additions, changes, fixes, and
      **Breaking** with migration steps. The GitHub Release's notes are taken from it.
- [ ] `Documentation/Releases/RELEASE-NOTES.md` describes this version.
- [ ] Screenshot baselines are current on the maintainer's machine and every changed image was reviewed.
- [ ] Two emulators or devices are connected: API 29 and API 36.

## 2. Verify the candidate (automated)

```sh
Scripts/release-candidate <X.Y.Z-rc.N> --devices
```

Each gate runs in order; the first failure stops the run with status 1 and the gate's log.

| Gate | Command it runs | Fails when |
|:--|:--|:--|
| tokens | `Scripts/verify-tokens` | an upstream file or a generated file differs from the lock |
| check | `./gradlew check :qeetrix:assembleRelease :qeetrix:dokkaGenerate :sample:assembleRelease` | a JVM test, Lint, ktlint, the API baseline, the generator's tests, the AAR, Dokka or the minified sample fails |
| aar | `Scripts/verify-aar` | the AAR holds anything unexpected, lacks a licence, declares a permission or component, or its SDK metadata changed |
| screenshots | `./gradlew :qeetrix:verifyRoborazziDebug` | an image differs from its reviewed baseline (skipped, and said so, where no baselines exist) |
| publish | `./gradlew :qeetrix:publishReleasePublicationToReleaseCandidateRepository` | publishing to `build/release-candidate/maven` fails |
| publication | `Scripts/verify-publication` | coordinates, files, checksums, variants or dependency sources are wrong, or the published AAR is not the one built |
| dependencies | `Scripts/dependency-inventory` (and the SBOM) | a runtime dependency's licence is unknown or not reviewed |
| consumer | `./gradlew -p Integration/Consumer :app:assembleDebug :app:assembleRelease :smoke:assembleRelease` | the independent app cannot resolve, compile or optimise against the candidate |
| library-devices | `./gradlew :qeetrix:connectedDebugAndroidTest` | a device test fails |
| consumer-devices | `./gradlew -p Integration/Consumer :app:connectedDebugAndroidTest :smoke:connectedAndroidTest` | the consumer's Compose tests (debug) or release smoke test fail |

It then writes `build/release-candidate/evidence/` — `PROVENANCE.md` (revision, working-tree state, toolchain,
token source, checksums), `SHA256SUMS`, `dependencies.md`, a CycloneDX 1.5 SBOM (`qeetrix-<version>.cdx.json`), the
notices, the release notes and every gate's log — and
`qeetrix-<version>-maven.zip`. Without devices, the same verification runs in the release-candidate workflow.

## 3. Review (maintainer, then **Owner**)

- [ ] The evidence shows every gate passed, on a clean revision.
- [ ] Manual checks still open for this release are listed in the report ([QUALITY.md](../QUALITY.md#-known-gaps)).
- [ ] **Owner:** approves the pull request into `main`.

## 4. Release (automated, **Owner** approval required)

Merging the pull request starts `release.yml` ([MAVEN-DISTRIBUTION.md](MAVEN-DISTRIBUTION.md#publishing)).

- [ ] **Owner, once:** the `maven-publish` environment exists with the owner as a required reviewer.
- [ ] The gate passes: the version is new, and every release gate passes on the merge commit.
- [ ] **Owner:** approves the publish job.
- [ ] The workflow publishes once, checks the files sent against the verified checksums, attests their provenance,
      tags the commit `vX.Y.Z` and opens the GitHub Release.
- [ ] The release notes reach app teams, and the docs team is told, with `Documentation/Components/` as the source for
      ui.qeet.in's Android pages.

## 5. After

- [ ] An app resolves the published version from the registry (the consumer, pointed at it with `-Pqeetrix.repository`).
- [ ] A bad release is withdrawn with `rollback.yml` and superseded, never edited ([ROLLBACK-PROCEDURE.md](ROLLBACK-PROCEDURE.md)).
