# Workflows — qeetrix-android

**Level:** L2 · **Last verified:** 2026-11-04
**Verification scope:** every command checked against the build files, `Scripts/` and `.github/workflows/`.

## Set up

```bash
./gradlew check                       # lint, every JVM test, the API check, the token drift check
./gradlew :sample:installDebug        # the sample app on a device or emulator
```

JDK 17 or newer, and the Android SDK with platform 37 (`ANDROID_HOME` or `local.properties`).

## Add a component

```text
1  copy a similar component: its source, its test, its screenshot test and its sample page
2  implement it from the @qeetrix/react CSS (Upstream/qeetrix-react/css/), the CSS beside each literal
3  add its sample page to sample/…/SampleCatalog.kt and a specimen to the quality catalogue
4  ./gradlew :qeetrix:apiDump                                                   the API baseline
5  ./gradlew :qeetrix:recordRoborazziDebug                                      then review every image
6  add its row to docs/components.md and an entry to the CHANGELOG
```

## Change a look, a token, a font or an icon

```text
1  change it upstream (qeetrix-tokens, qeetrix-icons or qeetrix-react) and publish to npm
2  bump the pin in Upstream/manifest.json, and tell the qeetrix-ios maintainers
3  Scripts/sync-tokens, then Scripts/generate-tokens
4  if the build fails to compile, a token was added or removed: change the public type on purpose
5  review the diff of Upstream/ and the generated files; a CSS change means that component's values change too
```

## Release

```text
1  raise qeetrix.version on develop if the library changed (docs-only: no new version); pull request develop → main
2  date the CHANGELOG entry: ## [X.Y.Z] — YYYY-MM-DD
3  merge; release.yml checks the version (no gate runs again: they passed in develop's CI)
4  approve: Actions › Release › Review deployments › Approve
5  it builds and verifies the artifacts, publishes to GitHub Packages, then tags vX.Y.Z and opens the GitHub Release
```

A bad release: **Actions › Rollback**, then a new patch. Details: [../releases.md](../releases.md).

## When CI fails

| Symptom | Usually | Do |
|---|---|---|
| Every test fails at once in `MavenArtifactFetcher` | Robolectric couldn't download its Android runtime | Re-run the job |
| A device job can't start the emulator, or one dialog test times out on API 29 | A slow or stuck emulator | Re-run the failed job |
| `verify-tokens` fails | A generated or upstream file was edited by hand | Run `Scripts/generate-tokens` |
| `apiCheck` fails | The public API changed | Run `apiDump` if intended, and note it in the CHANGELOG |
