# Security

## Scope

Qeetrix for Android is a UI library. It makes no network requests, declares no permissions or
components, stores nothing and reads no user data. The security-relevant parts are:

- **Supply chain.** The upstream artifacts it is built from (tokens, fonts, icon drawings) are pinned
  in `Upstream/manifest.json` and locked by sha256 in `Upstream/manifest.lock.json`. The `@qeetrix/ui`
  tarball is checked against the npm registry's integrity hash at sync time, and each bundled font is
  accepted only if its release's WOFF2 is byte-identical to the one the web ships.
  `./gradlew check` (run in CI) fails if any locked file changes, and `Scripts/sync-tokens --check`
  re-fetches the pinned artifacts and compares them (weekly in CI).
- **Dependencies.** The library's release classpaths are locked in `qeetrix/gradle.lockfile`
  (strict mode), so a transitive change is a reviewed diff. The Gradle wrapper's distribution is
  pinned by sha256 and validated in CI.
- **Bundled resources.** Two font files and three licence texts, loaded only from the app's own
  resources and assets.

## Supported versions

The library has no released versions yet. Only `main` is supported.

## Reporting a vulnerability

This repository is private. Report a vulnerability **privately to a repository administrator**. Do not
describe it in an issue or a pull request.

Include:

- what you found and its impact;
- how to reproduce it;
- the commit you tested.

A dedicated security contact for this repository has not been designated yet. Until one is, reports
go to the administrators directly.
