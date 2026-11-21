import kotlinx.validation.api.dump
import kotlinx.validation.api.filterOutNonPublic
import kotlinx.validation.api.loadApiFromJvmClasses
import java.util.jar.JarFile
import java.util.zip.ZipFile

buildscript {
    dependencies {
        // The public API dump (bottom of this file) uses the Binary Compatibility Validator's
        // signature engine as a library: its Gradle plugin does not configure AGP 9 built-in Kotlin.
        classpath(libs.binary.compatibility.validator)
        classpath(libs.asm.tree)
        classpath(libs.kotlin.metadata.jvm)
    }
}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.dokka)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.kover)
    `maven-publish`
}

// Maven coordinates com.qeetgroup:qeetrix (docs/decisions.md). The version is
// `qeetrix.version` in gradle.properties, the one the next release publishes; a release-candidate trial passes its own
// (`-Pqeetrix.version=…`, Scripts/release-candidate). Only the Release workflow publishes, after the owner approves.
group = "com.qeetgroup"
version = providers.gradleProperty("qeetrix.version").get()

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

    // The quality catalog — every in-place component in its risky states — is shared by the JVM scans and the
    // device's accessibility checks.
    sourceSets {
        named("test") { kotlin.directories.add("src/sharedTest/kotlin") }
        named("androidTest") { kotlin.directories.add("src/sharedTest/kotlin") }
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

    // One published variant, release, with its sources for IDE navigation and KDoc. No debug variant is published.
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        checkDependencies = false
        // A new AGP/AndroidX release is not a defect in this repository; dependency versions are
        // reviewed deliberately (docs/decisions.md).
        disable += setOf("GradleDependency", "NewerVersionAvailable", "AndroidGradlePluginVersion")
        // A library's own lint checks built for an older lint (AndroidX Lifecycle's, under AGP 9.5) are skipped either
        // way; this notice of that is not a finding in this code.
        disable += "ObsoleteLintCustomCheck"
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
    // Public API types come from these, so they are api: Color, Dp, TextStyle, Shape, Easing and Modifier,
    // and foundation's TextFieldState, KeyboardOptions, MutableInteractionSource, WindowInsets and scopes.
    api(libs.compose.runtime)
    api(libs.compose.ui)
    api(libs.compose.animation.core)
    api(libs.compose.foundation)
    // Only for the date and time fields (the platform's pickers in Qeetrix's colours) and pull to refresh; no Material
    // type is public and no Material theme value is drawn (docs/decisions.md).
    implementation(libs.compose.material3)
    // The sheets' window takes Back from its own dispatcher, predictive back gesture included.
    implementation(libs.androidx.activity)

    testImplementation(composeBom)
    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.compose.ui.test.junit4.accessibility)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    debugImplementation(composeBom)
    debugImplementation(libs.compose.ui.test.manifest)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.compose.ui.test.junit4.accessibility)
}

// Test coverage (Kover) of the JVM tests, debug variant: `./gradlew :qeetrix:koverHtmlReportDebug` for a report,
// `:qeetrix:koverVerifyDebug` (in the local gate) fails below the minimum (docs/quality.md). Generated token and icon
// data, and the classes the Compose compiler writes, are not code anyone writes tests for.
kover {
    reports {
        filters {
            excludes {
                packages("com.qeetgroup.qeetrix.tokens.generated", "com.qeetgroup.qeetrix.icons.generated")
                classes("*ComposableSingletons*", "*.BuildConfig")
            }
        }
        // 94.0 % of lines on 4 November 2026. Raise the minimum as coverage grows; never lower it to pass.
        variant("debug") {
            verify {
                rule("Line coverage") {
                    minBound(92)
                }
            }
        }
    }
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

// The Maven publication (docs/releases.md). Its repository is a local directory a
// release-candidate build fills and checks. The registry below exists only in the
// Release workflow, which runs on merge to main after an approver approves it, so a local build uploads nothing.
publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "qeetrix"
            // AGP creates the component in afterEvaluate (developer.android.com/build/publish-library/upload-library).
            afterEvaluate { from(components["release"]) }
            // Publish the resolved, locked versions instead of the BOM-managed declarations without versions, so the
            // POM and the module metadata name exactly what the library was built and tested with.
            versionMapping {
                usage("java-api") { fromResolutionOf("releaseCompileClasspath") }
                usage("java-runtime") { fromResolutionOf("releaseRuntimeClasspath") }
            }
            pom {
                name.set("Qeetrix for Android")
                description.set(
                    "Qeet Group's design system for Jetpack Compose: the Qeetrix tokens, theme, typography, icons and components.",
                )
                url.set("https://github.com/qeetgroup/qeetrix-android")
                organization {
                    name.set("Qeet Group")
                    url.set("https://qeet.in")
                }
                scm {
                    url.set("https://github.com/qeetgroup/qeetrix-android")
                    connection.set("scm:git:https://github.com/qeetgroup/qeetrix-android.git")
                }
                // The library's own code is MIT (LICENSE). Bundled fonts and icons carry their own licences, all four texts
                // inside the AAR (assets/qeetrix/licenses).
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit")
                        distribution.set("repo")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "releaseCandidate"
            url = uri(rootProject.layout.buildDirectory.dir("release-candidate/maven"))
        }
        // The registry, only when the Release workflow names one (-Pqeetrix.registry.url). Its credentials are the
        // Gradle properties registryUsername and registryPassword, which the workflow sets from its own token; they are
        // never in this repository (docs/releases.md).
        providers.gradleProperty("qeetrix.registry.url").orNull?.let { registryUrl ->
            maven {
                name = "registry"
                url = uri(registryUrl)
                credentials(PasswordCredentials::class)
            }
        }
    }
}

// The API reference: KDoc plus the module and package overviews in Module.md.
dokka {
    moduleName.set("Qeetrix for Android")
    dokkaSourceSets.configureEach {
        includes.from("Module.md")
    }
}

// Screenshot baselines (Roborazzi, docs/decisions.md).
roborazzi {
    outputDir.set(file("src/test/screenshots"))
}

// The public API baseline (docs/decisions.md).
abstract class QeetrixApiTask : DefaultTask() {
    @get:InputFile
    abstract val aar: RegularFileProperty

    @get:Internal
    abstract val baseline: RegularFileProperty

    @get:Input
    abstract val update: Property<Boolean>

    @TaskAction
    fun run() {
        val classes = File(temporaryDir, "classes.jar")
        ZipFile(aar.get().asFile).use { zip ->
            zip.getInputStream(zip.getEntry("classes.jar")).use { input -> classes.outputStream().use { input.copyTo(it) } }
        }
        val dump =
            JarFile(classes).use { jar ->
                // ComposableSingletons$… classes hold the Compose compiler's shared lambdas: public in bytecode,
                // never API, and with members named by a hash that changes with unrelated edits.
                val generated =
                    jar
                        .entries()
                        .asSequence()
                        .map { it.name }
                        .filter { it.endsWith(".class") && "/ComposableSingletons$" in it }
                        .map { it.removeSuffix(".class") }
                        .toList()
                buildString {
                    jar.loadApiFromJvmClasses { true }.filterOutNonPublic(nonPublicClasses = generated).dump(this)
                }
            }
        val file = baseline.get().asFile
        if (update.get()) {
            file.parentFile.mkdirs()
            file.writeText(dump)
            logger.lifecycle("Wrote ${file.path}")
        } else if (!file.exists() || file.readText() != dump) {
            File(temporaryDir, "current.api").writeText(dump)
            throw GradleException(
                "The public API differs from ${file.name}. If the change is intended, run ./gradlew :qeetrix:apiDump, " +
                    "note it in CHANGELOG.md and review the diff. Current API: ${File(temporaryDir, "current.api")}",
            )
        }
    }
}

val releaseAar = layout.buildDirectory.file("outputs/aar/qeetrix-release.aar")
val apiBaseline = rootProject.layout.projectDirectory.file("qeetrix/api/qeetrix.api")
tasks.register<QeetrixApiTask>("apiDump") {
    group = "verification"
    description = "Writes the public API to qeetrix/api/qeetrix.api."
    dependsOn("bundleReleaseAar")
    aar.set(releaseAar)
    baseline.set(apiBaseline)
    update.set(true)
}
val apiCheck =
    tasks.register<QeetrixApiTask>("apiCheck") {
        group = "verification"
        description = "Fails if the public API differs from qeetrix/api/qeetrix.api."
        dependsOn("bundleReleaseAar")
        aar.set(releaseAar)
        baseline.set(apiBaseline)
        update.set(false)
    }
tasks.named("check") { dependsOn(apiCheck) }
