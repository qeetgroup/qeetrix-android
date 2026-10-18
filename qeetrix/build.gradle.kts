plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.dokka)
}

// Coordinates an app resolves through a Gradle composite build (README › Using Qeetrix in an app). Nothing
// is published in Phase 1: there is no maven-publish configuration.
group = "com.qeetgroup"
version = "0.1.0-SNAPSHOT"

android {
    namespace = "com.qeetgroup.qeetrix"
    compileSdk = 37

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Every resource the library declares starts with qx_, so it cannot collide with an app's own.
    resourcePrefix = "qx_"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        // The library defines no BuildConfig fields.
        buildConfig = false
    }

    testOptions {
        unitTests {
            // Robolectric needs the merged resources (fonts) to load them in JVM tests.
            isIncludeAndroidResources = true
            all {
                // Parity tests compare the generated Kotlin with the generator's value table and lock.
                it.systemProperty("qeetrix.root", rootDir.absolutePath)
                // Robolectric's Android 16 runtime sets FileDescriptor internals through
                // jdk.internal.access.SharedSecrets, which JDK 17+ does not export by default.
                it.jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED", "--add-opens=java.base/java.io=ALL-UNNAMED")
            }
        }
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        checkDependencies = false
        // A new AGP/AndroidX release is not a defect in this repository; dependency versions are
        // reviewed deliberately (Documentation/Decisions/0002-gradle-build-and-dependency-policy.md).
        disable += setOf("GradleDependency", "NewerVersionAvailable", "AndroidGradlePluginVersion")
    }
}

kotlin {
    // Every public declaration needs an explicit visibility and type: the public API is deliberate.
    explicitApi()
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

ktlint {
    version.set(libs.versions.ktlint.engine)
    android.set(false)
    filter {
        // Generated files are verified against the generator instead.
        exclude { it.file.path.contains("/generated/") }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    // Public API types (Color, Dp, TextStyle, Shape, Easing, Modifier) come from these, so they are api.
    api(libs.compose.runtime)
    api(libs.compose.ui)
    api(libs.compose.animation.core)
    implementation(libs.compose.foundation)

    testImplementation(composeBom)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.kotlin.reflect)
    debugImplementation(composeBom)
    debugImplementation(libs.compose.ui.test.manifest)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.compose.ui.test.junit4)
}

// Lock what the library hands to apps: its release classpaths. A transitive version change then shows
// up as a reviewed diff of qeetrix/gradle.lockfile (refresh with `./gradlew :qeetrix:dependencies
// --write-locks`). Test and tooling classpaths are not locked; they never reach a consumer.
dependencyLocking {
    lockMode.set(LockMode.STRICT)
}
configurations.matching { it.name == "releaseCompileClasspath" || it.name == "releaseRuntimeClasspath" }.configureEach {
    resolutionStrategy.activateDependencyLocking()
}

// The API reference: KDoc plus the module and package overviews in Module.md.
dokka {
    moduleName.set("Qeetrix for Android")
    dokkaSourceSets.configureEach {
        includes.from("Module.md")
    }
}
