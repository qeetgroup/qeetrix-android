plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

// The extended icons' size budget (docs/icons.md): one app, built twice by R8 — `none` draws a core icon only,
// `twenty` adds twenty extended ones — and Scripts/icon-budget compares the two. Never published or installed.
android {
    namespace = "com.qeetgroup.qeetrix.iconbudget"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.qeetgroup.qeetrix.iconbudget"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "1"
    }

    flavorDimensions += "icons"
    productFlavors {
        create("none") { dimension = "icons" }
        create("twenty") { dimension = "icons" }
    }

    buildTypes {
        release {
            // As an app ships: R8 over the libraries, which carry no consumer rules and need none.
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

    lint {
        warningsAsErrors = true
        abortOnError = true
        disable += setOf("GradleDependency", "NewerVersionAvailable", "AndroidGradlePluginVersion", "ObsoleteLintCustomCheck")
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
    implementation(project(":qeetrix-icons-extended"))
    implementation(platform(libs.compose.bom))
    implementation(libs.activity.compose)
}
