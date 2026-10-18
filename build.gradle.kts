plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.dokka) apply false
}

// The generator's own tests and its drift check (upstream snapshot vs lock, generated files vs a fresh
// generation) run with the rest of the build's checks.
tasks.register("check") {
    group = "verification"
    description = "Runs the generator's tests and its drift verification."
    dependsOn(gradle.includedBuild("qeetrix-gen").task(":test"))
    dependsOn(gradle.includedBuild("qeetrix-gen").task(":verify"))
}
