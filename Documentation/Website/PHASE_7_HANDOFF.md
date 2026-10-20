# ui.qeet.in handoff: Android installation, versions and release status

For whoever maintains ui.qeet.in (`qeetrix-docs`). Phase 7 prepared Qeetrix for Android's first release; this is
what an **Android** section of the site may say about getting it, which versions it needs, and its status. **No
version is published.** Until the owner approves one, the site must present Android as "in preview, not yet
available", and must not show an install snippet as working.

The earlier handoffs ([Phase 3](PHASE_3_HANDOFF.md), [4](PHASE_4_HANDOFF.md), [5](PHASE_5_HANDOFF.md),
[6](PHASE_6_HANDOFF.md)) cover the components; this one covers installation and release.

## Release status, as of 2026-10-20

| | |
|:--|:--|
| Candidate | `0.1.0-rc.1`, built and verified locally; the version is a proposal awaiting approval |
| Published | No. No registry has been chosen |
| Stability | Pre-1.0: the API has a reviewed baseline, but may change in a minor release, with migration notes |

## Installation (to publish only once a version is released)

Replace `<repository>` and `<version>` with the approved registry and the released version. The snippet is
exactly what the independent consumer app in qeetrix-android uses, against a local repository.

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        exclusiveContent {
            forRepository { maven { url = uri("<repository>") } }
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

```kotlin
setContent {
    QeetrixTheme {
        QeetrixButton("Continue", onClick = ::next)
    }
}
```

## Requirements to state

| | |
|:--|:--|
| Android | 10 (API 29) and newer; phones and tablets |
| App build | Compile SDK 37 and Android Gradle Plugin 9.1 or newer — required by Jetpack Compose 1.12, which Qeetrix uses |
| Kotlin | 2.3 or newer |
| Compose | 1.12 (BOM 2026.09.00) or newer, brought by Qeetrix |
| Keep rules | None needed; Qeetrix works with R8 full mode and resource shrinking |

## What may be said

- Verified in an independent app through the Maven artifact: debug and an R8-optimised release, on Android 10 and
  Android 16 emulators, a phone and a tablet.
- Fonts (Qeet Sans, Fira Code) and icons ship inside the library; no web tooling is needed.
- Everything in the Phase 6 handoff about accessibility evidence still holds, with the same limits.

## What must not be said

- That Qeetrix for Android is released, available, stable or 1.0.
- That it is tested on physical devices, or accessible / WCAG-compliant (no manual TalkBack, Switch Access or
  keyboard pass has been completed; see the release-candidate report).
- That Qeet ID's authenticator app uses it: that app is built with React Native and does not.

## Sources

[MAVEN-DISTRIBUTION.md](../Releases/MAVEN-DISTRIBUTION.md), [RELEASE-POLICY.md](../Releases/RELEASE-POLICY.md),
[COMPATIBILITY-MATRIX.md](../Releases/COMPATIBILITY-MATRIX.md),
[RELEASE-CANDIDATE-REPORT.md](../Releases/RELEASE-CANDIDATE-REPORT.md).
