import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint)
    application
}

ktlint {
    version.set(libs.versions.ktlint.engine)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit4)
}

application {
    mainClass.set("com.qeetgroup.qeetrix.gen.MainKt")
}

// The repository this tool generates for: Tools/QeetrixGen → the repository root.
val repositoryRoot: File = rootDir.parentFile.parentFile

// `./gradlew check` at the root depends on this: the upstream snapshot must match its lock and every
// generated file must be exactly what the snapshot generates. Offline.
tasks.register<JavaExec>("verify") {
    group = "verification"
    description = "Fails if the upstream snapshot or any generated file has drifted."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set(application.mainClass)
    args("verify", "--root", repositoryRoot.absolutePath)
}

tasks.test {
    // Tests that read the real snapshot (parity, determinism, the bundled fonts) find it here.
    systemProperty("qeetrix.root", repositoryRoot.absolutePath)
}
