pluginManagement {
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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "qeetrix-android"

// The token, font and icon generator. A separate Gradle build that apps never resolve; its tasks
// are run through Scripts/ and its verification is part of `./gradlew check`.
includeBuild("Tools/QeetrixGen") {
    name = "qeetrix-gen"
}

include(":qeetrix")
include(":sample")
