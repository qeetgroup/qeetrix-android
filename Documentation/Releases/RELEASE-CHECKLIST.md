# Release checklist

From a commit to a published version. Steps marked **Owner** are decisions no script or workflow takes; everything
else is automated and fails loudly. The policy is [RELEASE-POLICY.md](RELEASE-POLICY.md).

## 1. Prepare (maintainer)

- [ ] Everything to release is committed; `git status` is clean.
- [ ] `CHANGELOG.md` has the version's entry: additions, changes, fixes, and **Breaking** with migration steps.
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
- [ ] Manual checks still open for this release are listed in the report ([RELEASE-READINESS-GAPS.md](../Quality/RELEASE-READINESS-GAPS.md)).
- [ ] **Owner:** approves the version, the registry, and the release.

## 4. Publish (**Owner** approval required)

- [ ] **Owner:** the registry and its credentials exist in a protected environment ([MAVEN-DISTRIBUTION.md](MAVEN-DISTRIBUTION.md)).
- [ ] The approved candidate is rebuilt from the same commit with its final version and verified again.
- [ ] Published once, through the approved workflow.
- [ ] **Owner:** tags the commit `v<version>` and pushes the tag.
- [ ] The release notes are published where app teams read them; the ui.qeet.in handoff is passed to the docs team.

## 5. After

- [ ] An app resolves the published version from the registry (the consumer, pointed at it with `-Pqeetrix.repository`).
- [ ] A bad release is superseded, never edited ([ROLLBACK-PROCEDURE.md](ROLLBACK-PROCEDURE.md)).
