## What and why

<!-- What changes, and the reason. Link the issue or the upstream qeetrix-ui / qeetrix-icons change. -->

## Checklist

Each box is a command that exists in this repository; CI runs the same ones.

- [ ] `./gradlew check`: generator tests, snapshot-and-drift verification, Android Lint, ktlint and unit tests pass
- [ ] `./gradlew :qeetrix:assembleRelease :sample:assembleDebug :qeetrix:dokkaGenerate` build
- [ ] `Scripts/verify-aar qeetrix/build/outputs/aar/qeetrix-release.aar`: the AAR ships only what it should
- [ ] Public API changes are intended, documented in KDoc and noted in `CHANGELOG.md`
      (see `Documentation/Standards/PUBLIC_API.md`)
- [ ] Device-only behaviour (fonts, TalkBack, rendering) checked with `./gradlew :qeetrix:connectedDebugAndroidTest`,
      or the PR says it was not run
- [ ] Documentation matches the change (`CHANGELOG.md`, KDoc, `ARCHITECTURE.md` or a decision record)

## Upstream inputs

- [ ] This PR does not change `Upstream/`, **or** it bumps `Upstream/manifest.json`, was produced by
      `Scripts/sync-tokens` and `Scripts/generate-tokens`, and nothing in `Upstream/` or a
      `generated/` package was edited by hand.
