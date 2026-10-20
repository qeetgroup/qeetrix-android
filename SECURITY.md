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
  pinned by sha256 and validated in CI. Gradle's dependency *verification* (checksums of every artifact in
  `gradle/verification-metadata.xml`) was evaluated in Phase 7 and is not enabled: locking already fixes every
  version an app receives, both repositories are TLS-served with their own checksums, and verification metadata for
  this build (AGP, Lint, Dokka, Robolectric, Roborazzi and their trees) would run to thousands of entries regenerated
  on every update. It is recommended once publishing runs from CI, where an unreviewed artifact would be published
  on.
- **Bundled resources.** Two font files, three licence texts and twenty-one `qx_` strings, loaded only from
  the app's own resources and assets.
- **Text input.** Every input keeps what is typed only in the state the app owns (`TextFieldState`, or
  the value the app passes); the library never logs, stores or sends it.
- **Passwords and one-time codes.** `QeetrixPasswordInput` is Compose's `BasicSecureTextField`: masked,
  copy and cut disabled, suggestions off. Neither it nor `QeetrixOTPInput` copies the text, saves it
  across configuration changes (only the reveal flag is saved) or puts it in any label, description or
  accessibility text; a test scans every semantics node for it. Like every Android password field, the
  text is exposed to accessibility services marked as a password; that is the platform's behaviour.
  Qeetrix reads no SMS, requests no permissions, uses no Play services, stores no credentials and adds
  no encryption of its own. AutoFill types are set so the user's password manager works, never
  disabled. See [Documentation/Standards/FORMS.md](Documentation/Standards/FORMS.md).
- **Snackbars and dialogs.** Their text is shown on screen and announced aloud; never put a password, a
  code or another secret in them. A confirmation dialog records a person's choice and authorises nothing:
  whatever performs the action checks permission. Snackbars end with their screen, so a message never
  reaches a later one.
- **Displayed data.** Rows, description lists, timelines, chips and empty states display the values the app
  passes and nothing else; they log, copy and announce nothing beyond them, and a data state's failure
  shows the app's message, never an exception's. The search field keeps its query in the app's
  `TextFieldState` and remembers no history. Qeetrix loads no images and makes no requests, so it sends no
  credentials; an app's image loader should not attach cookies or authorisation to image URLs implicitly.
  See [Documentation/Standards/DATA_DISPLAY_AND_COLLECTIONS.md](Documentation/Standards/DATA_DISPLAY_AND_COLLECTIONS.md).

- **Publishing.** The build publishes only to a local directory (`build/release-candidate/maven`); it has no
  remote repository, credential or upload step, and neither do the CI workflows. Publishing to a registry is a
  separate step the owner approves, with credentials held in a protected GitHub environment
  ([Documentation/Releases/MAVEN-DISTRIBUTION.md](Documentation/Releases/MAVEN-DISTRIBUTION.md)). The published POM
  and module metadata name only Google Maven and Maven Central dependencies, at the locked versions, and
  `Scripts/verify-publication` checks it. A consumer should resolve `com.qeetgroup` only from the approved registry
  (`exclusiveContent`), as `Integration/Consumer` does.
- **Release evidence.** `Scripts/release-candidate` records the source revision, the toolchain, the token source,
  SHA-256 checksums of every published file, an inventory of runtime dependencies with their licences, and a
  CycloneDX 1.5 SBOM generated from the same locked resolution. Builds are not claimed to be bit-for-bit
  reproducible.

## Supported versions

The library has no released versions yet. Only `main` is supported. Once released, security fixes go into a patch
release of the latest minor version ([RELEASE-POLICY.md](Documentation/Releases/RELEASE-POLICY.md)).

## Reporting a vulnerability

This repository is private. Report a vulnerability **privately to a repository administrator**. Do not
describe it in an issue or a pull request.

Include:

- what you found and its impact;
- how to reproduce it;
- the commit you tested.

A dedicated security contact for this repository has not been designated yet. Until one is, reports
go to the administrators directly.
