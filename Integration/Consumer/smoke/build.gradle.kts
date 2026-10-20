plugins {
    id("com.android.test")
}

// Black-box checks of the app's optimised release build (R8 and resource shrinking), which instrumented tests
// inside the app cannot reach: this test APK drives the installed app through UI Automator, as Macrobenchmark does.
android {
    namespace = "com.qeetgroup.qeetrix.consumer.smoke"
    compileSdk = providers.gradleProperty("consumer.compileSdk").get().toInt()

    defaultConfig {
        minSdk = 29
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        // Tests the app's release build type.
        create("release") {
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

androidComponents {
    beforeVariants(selector().all()) { it.enable = it.buildType == "release" }
}

dependencies {
    implementation("androidx.test.ext:junit:1.3.0")
    implementation("androidx.test:runner:1.7.0")
    implementation("androidx.test.uiautomator:uiautomator:2.4.0")
}
