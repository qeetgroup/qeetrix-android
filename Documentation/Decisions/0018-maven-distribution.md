# 0018 — Maven distribution: one release variant, built and verified here, published only with approval

**Status:** Accepted for the build and its verification, 2026-10-20 (Phase 7). The registry, the final version and
a licence declaration are the owner's decisions and are open. Supersedes the "no publication" point of
[0002](0002-gradle-build-and-dependency-policy.md).

## Context

Until Phase 7 an app could only use Qeetrix as a Gradle composite build of this repository. Product teams need a
versioned dependency they resolve from a repository, with the dependency information an AAR alone does not carry
(Android's guidance: publish libraries to a Maven repository). Nothing about the library had been exercised the way
a consumer would use it: `:sample` depends on `project(":qeetrix")`.

What the organisation already does: its other Gradle projects use the group `com.qeetgroup` (qeet-pay-server,
qeet-people-server), and this repository's README already documents `com.qeetgroup:qeetrix` for composite builds.
The web design system is published publicly to npm as `@qeetrix/ui`, marked `UNLICENSED`; qeetrix-android and
qeetrix-ios are private repositories with no licence.

## Decision

1. **A Maven publication of one variant, `release`**, from AGP's `singleVariant("release")` with a sources JAR: the
   AAR, its POM and its Gradle module metadata. No debug variant, no Javadoc JAR (no destination needs one yet; the
   API reference is Dokka's HTML). Added if a destination requires it.
2. **Coordinates `com.qeetgroup:qeetrix`**, the organisation's group and the module's name, as already documented.
3. **The published dependency versions are the resolved, locked ones** (`versionMapping` from the release
   classpaths), so the POM and module metadata name exactly what was built and tested. The Compose BOM the library
   builds with is published as a platform of its runtime variant (a `dependencyManagement` import in the POM); it
   aligns an app's Compose artifacts to at least the tested versions.
4. **The version is a property.** Builds are `0.1.0-SNAPSHOT` unless `-Pqeetrix.version` passes another; release
   candidates are `X.Y.Z-rc.N` ([RELEASE-POLICY.md](../Releases/RELEASE-POLICY.md)). No version is final until the
   owner approves it.
5. **The build has no remote repository.** Its only repository is `build/release-candidate/maven`, which
   `Scripts/release-candidate` fills and the independent consumer in `Integration/Consumer` resolves from by
   coordinates (an exclusive repository for `com.qeetgroup`, so nothing else can satisfy it). Nothing in this
   repository can upload an artifact.
6. **An independent consumer is the acceptance test.** A separate Gradle build, never included in this one, uses
   only the public API through the Maven artifact, builds debug and an R8-optimised release, and is tested on
   devices: Compose tests on debug, a UI Automator smoke test on release.
7. **Publishing remotely is a separate, approved step.** When the owner chooses a registry, a workflow that only
   runs on a protected environment with required reviewers publishes an already-verified candidate, with
   credentials from that environment's secrets ([MAVEN-DISTRIBUTION.md](../Releases/MAVEN-DISTRIBUTION.md)).

## Options for the registry (the owner's decision)

| Option | Fits | Costs |
|:--|:--|:--|
| **GitHub Packages (Maven), private** — recommended for the first internal consumers | The code and its access control are already on GitHub; packages inherit the repository's visibility | Every consumer's build needs a token with `read:packages`; Actions billing must be restored to publish from CI |
| A private Maven repository the organisation runs | Central control, no per-developer tokens | None exists today that this review found |
| Maven Central | Public, no credentials for consumers | Needs a licence in the POM, signing and a Javadoc JAR; the code has no licence today |

## Consequences

- An app adds `implementation("com.qeetgroup:qeetrix:<version>")` and the repository; the composite build remains
  for working on the library itself.
- A change to a dependency's version appears in the lockfile, the POM and the module metadata together.
- Publishing cannot happen by accident: there is no remote repository to publish to until the owner adds one.
