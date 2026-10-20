plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.qeetgroup.qeetrix.consumer"
    compileSdk = providers.gradleProperty("consumer.compileSdk").get().toInt()

    defaultConfig {
        applicationId = "com.qeetgroup.qeetrix.consumer"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Optimised as an app would ship: R8 in full mode and resource shrinking, with no Qeetrix keep rules. Signed
        // with the debug key only so it installs on an emulator; never distributed.
        release {
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

    buildFeatures { compose = true }
}

dependencies {
    // Qeetrix and nothing else of Compose: its published metadata has to bring what its public API needs.
    implementation("com.qeetgroup:qeetrix:${providers.gradleProperty("qeetrix.version").get()}")
    implementation("androidx.activity:activity-compose:1.13.0")

    androidTestImplementation(platform("androidx.compose:compose-bom:2026.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    debugImplementation(platform("androidx.compose:compose-bom:2026.09.00"))
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
