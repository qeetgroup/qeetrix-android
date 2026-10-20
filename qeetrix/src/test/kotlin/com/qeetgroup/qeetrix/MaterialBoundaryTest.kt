package com.qeetgroup.qeetrix

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Material 3 is an implementation detail of one component (Documentation/Decisions/0015-navigation-overlays-and-feedback.md):
 * only the bottom sheet may import it, and nothing public may mention it. This keeps Material from becoming
 * the design authority one convenient import at a time.
 */
class MaterialBoundaryTest {
    private val sources = File("src/main/kotlin")

    @Test
    fun onlyTheBottomSheetImportsMaterial() {
        assertTrue("run from the qeetrix module: ${sources.absolutePath}", sources.isDirectory)
        val importing =
            sources
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .filter { file -> file.readLines().any { it.startsWith("import androidx.compose.material") } }
                .map { it.relativeTo(sources).path }
                .toList()
        assertEquals(listOf("com/qeetgroup/qeetrix/components/drawer/QeetrixBottomSheet.kt"), importing)
    }

    @Test
    fun thePublicApiNamesNoMaterialType() {
        val api = File("../Documentation/API/qeetrix.api")
        assertTrue("the API baseline: ${api.absolutePath}", api.isFile)
        val material = api.readLines().filter { "androidx/compose/material" in it }
        assertEquals(emptyList<String>(), material)
    }
}
