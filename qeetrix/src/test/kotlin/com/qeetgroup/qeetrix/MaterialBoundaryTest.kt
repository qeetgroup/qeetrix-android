package com.qeetgroup.qeetrix

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier
import java.net.JarURLConnection
import java.util.jar.JarFile

/**
 * Material 3 is an implementation detail of a few components (docs/decisions.md):
 * the platform's own date and time pickers (themed by Qeetrix) and pull to refresh's
 * nested-scroll handling (drawn by Qeetrix). The bottom sheet no longer is. Only those files may use
 * it, and nothing public may mention it. This keeps Material from becoming the design authority one convenient import at a time.
 */
class MaterialBoundaryTest {
    private val sources = File("src/main/kotlin")

    /** Any mention of Material's packages — an import, or a name written out in full — is in one of the allowed files. */
    @Test
    fun onlyTheAllowedComponentsUseMaterial() {
        assertTrue("run from the qeetrix module: ${sources.absolutePath}", sources.isDirectory)
        val using =
            sources
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .filter { file -> file.readLines().any { MATERIAL in it } }
                .map { it.relativeTo(sources).path }
                .toList()
        assertEquals(
            listOf(
                "com/qeetgroup/qeetrix/components/datepicker/QeetrixDateField.kt",
                "com/qeetgroup/qeetrix/components/list/QeetrixPullToRefresh.kt",
            ),
            using.sorted(),
        )
    }

    /**
     * The compiled library, as the test runs it: no public or protected class, method, constructor or field names a
     * Material type — the date fields' parameters and every other declaration included. Checked on the
     * bytecode, so it holds whatever the API baseline says; `internal` declarations are public in bytecode, so it is
     * stricter than the public API.
     */
    @Test
    fun theCompiledLibraryNamesNoMaterialTypeInAnythingPublic() {
        val classes = libraryClasses()
        assertTrue("the library's classes: ${classes.size}", classes.size > 100)
        assertTrue(classes.any { it.name == "com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheetKt" })
        assertEquals(emptyList<String>(), classes.flatMap { it.publicMaterialMentions() })
    }

    /** The bytecode check finds what it looks for: Material's own sheet names its state in its signature. */
    @Test
    fun theBytecodeCheckFindsAMaterialType() {
        val sheet = Class.forName("androidx.compose.material3.ModalBottomSheetKt", false, javaClass.classLoader)
        assertTrue(sheet.publicMaterialMentions().any { "SheetState" in it })
    }

    @Test
    fun thePublicApiBaselineNamesNoMaterialType() {
        val api = File("api/qeetrix.api")
        assertTrue("the API baseline: ${api.absolutePath}", api.isFile)
        val material = api.readLines().filter { "androidx/compose/material" in it }
        assertEquals(emptyList<String>(), material)
    }

    /** Every class under `com.qeetgroup.qeetrix` in the directory or jar the library's classes are loaded from. */
    private fun libraryClasses(): List<Class<*>> {
        val loader = javaClass.classLoader!!
        val anchor = "com/qeetgroup/qeetrix/components/drawer/QeetrixBottomSheetKt.class"
        val url = checkNotNull(loader.getResource(anchor)) { "$anchor is not on the test classpath" }
        val names =
            when (url.protocol) {
                "file" -> {
                    val root = File(url.toURI()).path.removeSuffix(anchor.replace('/', File.separatorChar))
                    File(root, "com/qeetgroup/qeetrix")
                        .walkTopDown()
                        .filter { it.isFile }
                        .map { it.relativeTo(File(root)).path }
                        .toList()
                }

                "jar" -> {
                    JarFile((url.openConnection() as JarURLConnection).jarFileURL.path).use { jar ->
                        jar
                            .entries()
                            .asSequence()
                            .map { it.name }
                            .filter { it.startsWith("com/qeetgroup/qeetrix/") }
                            .toList()
                    }
                }

                else -> {
                    error("unexpected class location $url")
                }
            }
        return names
            .filter { it.endsWith(".class") && "/ComposableSingletons$" !in it }
            .map { Class.forName(it.removeSuffix(".class").replace('/', '.').replace(File.separatorChar, '.'), false, loader) }
            .filter { Modifier.isPublic(it.modifiers) && !it.isSynthetic }
    }

    /** The public and protected members of this class whose signature names a type in Material's packages. */
    private fun Class<*>.publicMaterialMentions(): List<String> {
        fun visible(modifiers: Int) = Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)
        val signatures =
            buildList {
                add(toGenericString())
                genericSuperclass?.let { add(it.typeName) }
                genericInterfaces.forEach { add(it.typeName) }
                declaredMethods.filter { visible(it.modifiers) && !it.isSynthetic }.forEach { add(it.toGenericString()) }
                declaredConstructors.filter { visible(it.modifiers) && !it.isSynthetic }.forEach { add(it.toGenericString()) }
                declaredFields.filter { visible(it.modifiers) && !it.isSynthetic }.forEach { add(it.toGenericString()) }
            }
        return signatures.filter { MATERIAL in it }.map { "$name: $it" }
    }

    private companion object {
        const val MATERIAL = "androidx.compose.material"
    }
}
