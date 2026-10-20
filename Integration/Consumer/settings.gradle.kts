// An independent app that uses Qeetrix the way a product team would: through Maven coordinates, from a
// repository, with no project dependency, composite build or copied AAR (Documentation/Releases/CONSUMER-INTEGRATION.md).
// It is a separate Gradle build, never included in qeetrix-android's own.
pluginManagement {
    // Plugin versions are properties so the same app can be built with other toolchains (COMPATIBILITY-MATRIX.md).
    val agp = providers.gradleProperty("consumer.agp").get()
    val kotlin = providers.gradleProperty("consumer.kotlin").get()
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version agp
        id("com.android.test") version agp
        id("org.jetbrains.kotlin.plugin.compose") version kotlin
    }
}

// The candidate repository: by default the one Scripts/release-candidate fills
// (qeetrix-android/build/release-candidate/maven); another with -Pqeetrix.repository=<path or URL>.
val qeetrixRepository =
    providers.gradleProperty("qeetrix.repository").map { uri(it) }
        .getOrElse(settingsDir.resolve("../../build/release-candidate/maven").toURI())

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // com.qeetgroup comes only from the candidate repository, and nothing else does.
        exclusiveContent {
            forRepository {
                maven {
                    name = "qeetrixCandidate"
                    url = qeetrixRepository
                }
            }
            filter { includeGroup("com.qeetgroup") }
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "qeetrix-consumer"
include(":app")
include(":smoke")
