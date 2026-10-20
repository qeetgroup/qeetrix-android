# Qeetrix consumer

An independent Android app that uses Qeetrix only as apps will: `com.qeetgroup:qeetrix:<version>` from a Maven
repository. It is the release acceptance test ([0018](../../Documentation/Decisions/0018-maven-distribution.md),
[CONSUMER-INTEGRATION.md](../../Documentation/Releases/CONSUMER-INTEGRATION.md)) and a separate Gradle build: the
library's build never includes it, and it never includes the library.

- `app/` — four pages (foundations, a form, overlays, a searchable list) with switches for the dark theme, larger
  text and right to left; `src/androidTest` holds Compose tests that run on its debug build.
- `smoke/` — UI Automator tests that drive the app's R8-optimised release build from outside.

## Running it

Publish a candidate first; the consumer resolves `com.qeetgroup` only from that repository.

```sh
# from qeetrix-android
./gradlew :qeetrix:publishReleasePublicationToReleaseCandidateRepository -Pqeetrix.version=0.1.0-rc.1
./gradlew -p Integration/Consumer :app:assembleDebug :app:assembleRelease
./gradlew -p Integration/Consumer :app:connectedDebugAndroidTest :smoke:connectedAndroidTest   # devices
```

`Scripts/release-candidate` does all of this. The SDK comes from `ANDROID_HOME`.

| Property (`gradle.properties` or `-P`) | Default | |
|:--|:--|:--|
| `qeetrix.version` | `0.1.0-rc.1` | The version to resolve |
| `qeetrix.repository` | `../../build/release-candidate/maven` | Another repository, a path or a URL |
| `consumer.agp`, `consumer.kotlin`, `consumer.compileSdk` | `9.4.1`, `2.4.21`, `37` | The toolchain, for the compatibility matrix |
