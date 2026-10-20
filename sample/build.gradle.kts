plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.qeetgroup.qeetrix.sample"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.qeetgroup.qeetrix.sample"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        debug {
            // Android's pseudolocales — en-XA (accented and longer) and ar-XB (right to left) — so the library's
            // strings and layouts can be checked on a device in Settings › Languages.
            isPseudoLocalesEnabled = true
        }
        release {
            // A release built as an app builds one: R8 and resource shrinking over the library, which carries no
            // consumer rules and needs none. Signed with the debug key so it installs for checks; never distributed.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = false
    }

    testOptions {
        unitTests {
            // Robolectric renders the sample's pages on the JVM, with its resources (the Qeet fonts).
            isIncludeAndroidResources = true
            all {
                // As in :qeetrix — Robolectric's Android 16 runtime needs jdk.internal.access on JDK 17+.
                it.jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED", "--add-opens=java.base/java.io=ALL-UNNAMED")
            }
        }
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        disable += setOf("GradleDependency", "NewerVersionAvailable", "AndroidGradlePluginVersion")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

ktlint {
    version.set(libs.versions.ktlint.engine)
    android.set(false)
}

dependencies {
    // The sample consumes Qeetrix exactly as an app would: through its public API.
    implementation(project(":qeetrix"))
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}
