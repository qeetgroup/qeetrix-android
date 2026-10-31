package com.qeetgroup.qeetrix.quality

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Every public `Qeetrix*` composable of the library has a specimen in the quality catalog (`QualityCatalog.kt`), so
 * the scans cover a component as soon as it exists: read from the sources, a composable is covered when the catalog
 * calls it. [Harness] lists the ones every scan already runs inside.
 */
class QualityCatalogCoverageTest {
    private val sources = File("src/main/kotlin")
    private val catalog = File("src/sharedTest/kotlin/com/qeetgroup/qeetrix/quality/QualityCatalog.kt")

    /** The public `Qeetrix*` composables declared in [sources]: a `public fun` named `Qeetrix…`, marked `@Composable`. */
    private fun publicComposables(sources: File): Set<String> =
        sources
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .flatMap { file ->
                val lines = file.readLines()
                lines.indices.mapNotNull { index ->
                    val name = Declaration.find(lines[index])?.groupValues?.get(1) ?: return@mapNotNull null
                    val annotations = lines.subList(maxOf(0, index - 3), index)
                    name.takeIf { annotations.any { it.trim() == "@Composable" } }
                }
            }.toSet()

    /** The names [catalog] calls, outside its comments. */
    private fun called(catalog: String): Set<String> {
        val code =
            catalog
                .lines()
                .filterNot { it.trim().startsWith("//") || it.trim().startsWith("*") || it.trim().startsWith("/*") }
                .joinToString("\n") { it.substringBefore("// ") }
        return Call.findAll(code).map { it.groupValues[1] }.toSet()
    }

    @Test
    fun everyPublicComposableHasASpecimen() {
        assertTrue("run from the qeetrix module: ${sources.absolutePath}", sources.isDirectory && catalog.isFile)
        val composables = publicComposables(sources)
        assertTrue("public composables found: $composables", composables.size > 50 && "QeetrixButton" in composables)
        val missing = composables - called(catalog.readText()) - Harness
        assertEquals("public composables with no specimen in ${catalog.name}", emptyList<String>(), missing.sorted())
    }

    /** The check finds a composable no catalog calls, and doesn't count a mention in a comment as a call. */
    @Test
    fun theCheckFindsAComposableWithNoSpecimen() {
        val library = createTempDirectory()
        File(library, "QeetrixGauge.kt").writeText(
            """
            /** A gauge. */
            @Composable
            public fun QeetrixGauge(
                value: Float,
            ) {}

            @Composable
            public fun <T> QeetrixPicker(value: T) {}
            """.trimIndent(),
        )
        val composables = publicComposables(library)
        assertEquals(setOf("QeetrixGauge", "QeetrixPicker"), composables)
        assertEquals(setOf("QeetrixGauge"), composables - called("// QeetrixGauge() is not a call\nQeetrixPicker(1)"))
        library.deleteRecursively()
    }

    private fun createTempDirectory(): File =
        kotlin.io.path
            .createTempDirectory("coverage")
            .toFile()

    private companion object {
        /** `public fun QeetrixButton(`, `public fun <T> QeetrixTabs(`, `public fun QeetrixMenuScope.QeetrixMenuItem(`. */
        val Declaration = Regex("""^public fun (?:<[^>]+> )?(?:[\w<>]+\.)?(Qeetrix\w+)\(""")

        /** A call: the name, then an argument list or a trailing lambda. */
        val Call = Regex("""\b(Qeetrix\w+)\s*[({]""")

        /** Every specimen is drawn inside the theme by the scan that shows it. */
        val Harness = setOf("QeetrixTheme")
    }
}
