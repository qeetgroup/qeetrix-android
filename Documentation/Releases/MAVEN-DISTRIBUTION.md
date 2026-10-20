# Maven distribution

How Qeetrix for Android is packaged for apps, how an app installs it, and how a version would be published. The
decision is [0018](../Decisions/0018-maven-distribution.md). **Nothing has been published**: the only repository
this build writes to is a local directory.

## Coordinates

```kotlin
implementation("com.qeetgroup:qeetrix:<version>")
```

`com.qeetgroup` is the group Qeet Group's Gradle projects use; `qeetrix` is the module. The package and Android
namespace are `com.qeetgroup.qeetrix`. Development builds are `0.1.0-SNAPSHOT`; the first candidate is proposed as
`0.1.0-rc.1` ([RELEASE-POLICY.md](RELEASE-POLICY.md)).

## What a version contains

| File | What |
|:--|:--|
| `qeetrix-<version>.aar` | The release build: classes, two fonts, 21 `qx_` strings, three licence texts; no permissions or components ([AAR-VERIFICATION.md](AAR-VERIFICATION.md)) |
| `qeetrix-<version>.pom` | Coordinates, name, description, URL, organisation, SCM, and dependencies with exact versions. No `<licenses>` until the owner chooses one ([LICENSE-COMPLIANCE.md](LICENSE-COMPLIANCE.md)) |
| `qeetrix-<version>.module` | Gradle module metadata: an API variant, a runtime variant and a sources variant. Gradle reads this in preference to the POM |
| `qeetrix-<version>-sources.jar` | The Kotlin sources, so IDEs show the KDoc and navigate into components |
| `*.sha256`, `*.sha512` (and `md5`, `sha1`) | Checksums Gradle writes beside each file |

Dependencies an app receives, as published:

| Scope | Artifacts |
|:--|:--|
| API (on the app's compile classpath — public Qeetrix signatures use their types) | `androidx.compose.runtime:runtime`, `androidx.compose.ui:ui`, `androidx.compose.animation:animation-core`, `androidx.compose.foundation:foundation` 1.12.1; `org.jetbrains.kotlin:kotlin-stdlib` 2.4.21 |
| Runtime only | `androidx.compose.material3:material3` 1.4.0 (the bottom sheet's implementation; no Material type is public); the Compose BOM 2026.09.00 as a platform, aligning the app's Compose artifacts to at least the tested versions |

Everything comes from Google's Maven repository and Maven Central, which every Android app already uses.
`Scripts/verify-publication` checks the coordinates, files, checksums, variants and dependency sources of a
publication.

## Installing it in an app

Requirements, set by Compose 1.12 rather than by Qeetrix: **compile SDK 37** and **AGP 9.1 or newer**; minimum SDK
29. Kotlin 2.3 or newer ([COMPATIBILITY-MATRIX.md](COMPATIBILITY-MATRIX.md)).

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        // The repository the owner chooses (below). Restrict it to com.qeetgroup so nothing else can come from it.
        exclusiveContent {
            forRepository { maven { url = uri("<repository URL>") /* and credentials, if it needs them */ } }
            filter { includeGroup("com.qeetgroup") }
        }
        google()
        mavenCentral()
    }
}

// app/build.gradle.kts
dependencies {
    implementation("com.qeetgroup:qeetrix:<version>")
}
```

Then wrap the app's content in `QeetrixTheme { … }`. An app needs no keep rules for Qeetrix, no other Compose
dependency for Qeetrix's sake, no Node.js, qeetrix-ui checkout or token generator: tokens are compiled into the
library and fonts and strings are in the AAR. This is exactly how the independent consumer in
`Integration/Consumer` is set up, and it is verified on every candidate ([CONSUMER-INTEGRATION.md](CONSUMER-INTEGRATION.md)).

## Building and checking a candidate locally

```sh
Scripts/release-candidate 0.1.0-rc.1 --devices     # every gate; publishes to build/release-candidate/maven only
```

Or the publication alone:

```sh
./gradlew :qeetrix:publishReleasePublicationToReleaseCandidateRepository -Pqeetrix.version=0.1.0-rc.1
Scripts/verify-publication build/release-candidate/maven 0.1.0-rc.1 qeetrix/build/outputs/aar/qeetrix-release.aar
```

## Choosing a registry

The owner's decision; the options are compared in [0018](../Decisions/0018-maven-distribution.md). The
recommendation for the first internal consumers is **GitHub Packages, private**: the code and its access control
are already there. An app then adds the repository `https://maven.pkg.github.com/qeetgroup/qeetrix-android` with a
GitHub user and a token that has `read:packages` (from `~/.gradle/gradle.properties` or the CI's secrets, never the
app's repository). **This has not been tried**: no package exists yet.

## Publishing, once approved

Not implemented, deliberately: there is no registry to point at. When the owner chooses one, the change is:

1. A repository in `qeetrix/build.gradle.kts` whose URL and credentials come from Gradle properties
   (`providers.gradleProperty`), never from the repository's files.
2. A `publish` workflow, started by hand with a version, that runs only in a GitHub **environment** with required
   reviewers (the owner) and holds the registry credential as that environment's secret, with
   `permissions: contents: read, packages: write` and nothing else. It first runs `Scripts/release-candidate` on the
   same commit, then publishes exactly what that verified.
3. A version is published once. A fix is a new version ([ROLLBACK-PROCEDURE.md](ROLLBACK-PROCEDURE.md)).
4. A tag `v<version>` on the commit, pushed by the owner after publishing.

The release-candidate workflow (`.github/workflows/release-candidate.yml`) already does step 2's verification
half and keeps the result as a workflow artifact; it has no credentials and no upload step.
