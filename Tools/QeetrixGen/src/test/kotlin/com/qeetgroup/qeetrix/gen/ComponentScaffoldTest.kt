package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.components.ComponentScaffold
import com.qeetgroup.qeetrix.gen.components.ComponentScaffold.Change
import com.qeetgroup.qeetrix.gen.components.ComponentScaffold.Request
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

/**
 * `new-component` against the real repository (planning only) and against a temporary copy (writing), for a component
 * the library does not have yet: `@qeetrix/react`'s Pagination. (It was Slider until QeetrixSlider was built, QTRX-130.)
 */
class ComponentScaffoldTest {
    private val root = File(System.getProperty("qeetrix.root"))
    private val pagination = Request("Pagination", "Pagination", "Navigation")

    @Test
    fun plansEveryFileForANewComponentWithoutWritingAny() {
        val changes = ComponentScaffold(root).plan(pagination)
        assertEquals(
            listOf(
                "create qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/components/pagination/QeetrixPagination.kt",
                "create qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/pagination/QeetrixPaginationTest.kt",
                "create qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/pagination/QeetrixPaginationScreenshotTest.kt",
                "create sample/src/main/kotlin/com/qeetgroup/qeetrix/sample/components/navigation/PaginationSample.kt",
                "create Documentation/Components/Pagination.md",
                "update ${ComponentScaffold.CATALOG_PATH}",
                "update ${ComponentScaffold.INDEX_PATH}",
            ),
            changes.map { "${it.kind.verb} ${it.path}" },
        )
        assertFalse(File(root, changes.first().path).exists())
        for (change in changes) assertFalse("${change.path} keeps a placeholder", "{{" in change.contents)
        val source = changes.first().contents
        assertTrue("package com.qeetgroup.qeetrix.components.pagination" in source)
        assertTrue("public fun QeetrixPagination(modifier: Modifier = Modifier)" in source)
        assertTrue("level = DeprecationLevel.ERROR" in source && "    scaffold()\n" in source)
    }

    @Test
    fun theSameRequestGivesTheSameBytes() {
        assertEquals(ComponentScaffold(root).plan(pagination), ComponentScaffold(root).plan(pagination))
    }

    @Test
    fun anExistingComponentStopsTheRunWithEveryReason() {
        val error = assertThrows(GeneratorError::class.java) { ComponentScaffold(root).plan(Request("Button", "Button", "Actions")) }
        val message = error.message.orEmpty()
        assertTrue(message, message.startsWith("QeetrixButton already exists; nothing was changed:"))
        assertTrue(message, "components/button/QeetrixButton.kt already exists" in message)
        assertTrue(message, "QeetrixButton is already declared in qeetrix/src/main/kotlin/" in message)
        assertTrue(message, "already has a Button page" in message)
        assertTrue(message, "already lists Button" in message)
    }

    @Test
    fun namesFamiliesSlugsAndGroupsAreValidated() {
        fun failure(request: Request) = assertThrows(GeneratorError::class.java) { ComponentScaffold(root).plan(request) }.message.orEmpty()
        assertTrue("UpperCamelCase" in failure(Request("Checkbox", "checkbox", "Navigation")))
        assertTrue("without the Qeetrix prefix" in failure(Request("Checkbox", "QeetrixCheckbox", "Navigation")))
        assertTrue("Unknown category \"Inputs\"" in failure(Request("Checkbox", "Checkbox", "Inputs")))
        // The family and part come from @qeetrix/react's component manifest.
        assertTrue("No family \"tick-box\"" in failure(Request("TickBox", "TickBox", "Navigation")))
        assertTrue("card has no part \"IconButton\"" in failure(Request("Card", "IconButton", "Actions")))
    }

    @Test
    fun aJavaKeywordFamilyGetsAControlPackage() {
        assertEquals("switchcontrol", ComponentScaffold.packageName("Switch"))
        assertEquals("datepicker", ComponentScaffold.packageName("DatePicker"))
    }

    @Test
    fun namesAreDerivedAsOnIos() {
        assertEquals("icon-button", ComponentScaffold.kebab("IconButton"))
        assertEquals("otp-field", ComponentScaffold.kebab("OTPField"))
        assertEquals("Icon button", ComponentScaffold.title("IconButton"))
        assertEquals("OTP field", ComponentScaffold.title("OTPField"))
    }

    @Test
    fun aGroupUpstreamDefinesIsAdoptedAndTheRestOfThePinsIsUntouched() {
        // @qeetrix/tokens defines component.command-palette, which Android has not adopted yet.
        val changes = ComponentScaffold(root).plan(Request("CommandPalette", "CommandPalette", "Navigation"))
        val pins = changes.single { it.path == ComponentScaffold.UPSTREAM_PATH }.contents
        val original = File(root, ComponentScaffold.UPSTREAM_PATH).readText()
        assertTrue("      \"command-palette\",\n      \"sidebar\"," in pins)
        assertEquals(original, pins.replace("      \"command-palette\",\n", ""))
        assertTrue("Component tokens: QeetrixCommandPaletteComponentTokens." in changes.first().contents)
    }

    @Test
    fun writingTwiceChangesNothingTheSecondTime() {
        val copy = repositoryCopy()
        val scaffold = ComponentScaffold(copy)
        scaffold.apply(scaffold.plan(pagination))

        val catalog = File(copy, ComponentScaffold.CATALOG_PATH).readText()
        assertTrue(
            "        SampleEntry(\"pagination\", \"Pagination\", SampleCategory.Navigation) { PaginationSample() },\n" +
                ComponentScaffold.CATALOG_MARKER in catalog,
        )
        val imports = catalog.lines().filter { it.startsWith("import ") }
        assertTrue("import com.qeetgroup.qeetrix.sample.components.navigation.PaginationSample" in imports)
        assertEquals(imports.sorted(), imports)
        val index = File(copy, ComponentScaffold.INDEX_PATH).readText().lines()
        val row = index.indexOfFirst { "[Pagination](Pagination.md)" in it }
        // At the end of its category's rows, whichever they are as the index grows.
        assertTrue(index[row - 1].startsWith("| Navigation |"))
        assertFalse(index[row + 1].startsWith("| Navigation |"))

        val written = copy.walkTopDown().filter { it.isFile }.associateWith { it.readText() }
        assertThrows(GeneratorError::class.java) { scaffold.plan(pagination) }
        assertEquals(written, copy.walkTopDown().filter { it.isFile }.associateWith { it.readText() })
    }

    @Test
    fun aFileThatAppearsAfterPlanningIsNotOverwritten() {
        val copy = repositoryCopy()
        val scaffold = ComponentScaffold(copy)
        val changes = scaffold.plan(pagination)
        val late = File(copy, changes.first { it.kind == Change.Kind.Create }.path)
        late.parentFile.mkdirs()
        late.writeText("mine")
        assertThrows(GeneratorError::class.java) { scaffold.apply(changes) }
        assertEquals("mine", late.readText())
        assertFalse(File(copy, "Documentation/Components/Pagination.md").exists())
    }

    /** The files `new-component` reads, copied into a temporary repository. */
    private fun repositoryCopy(): File {
        val copy = Files.createTempDirectory("qeetrix-scaffold").toFile().apply { deleteOnExit() }
        val paths =
            listOf(
                ComponentScaffold.MANIFEST_PATH,
                ComponentScaffold.TOKENS_PATH,
                ComponentScaffold.UPSTREAM_PATH,
                ComponentScaffold.CATALOG_PATH,
                ComponentScaffold.INDEX_PATH,
                ComponentScaffold.MODULE_DOC_PATH,
            ) + ComponentScaffold.TEMPLATES.map { "${ComponentScaffold.TEMPLATES_PATH}/${it.first}" }
        for (path in paths) File(root, path).copyTo(File(copy, path))
        File(copy, ComponentScaffold.LIBRARY_SOURCES).mkdirs()
        return copy
    }
}
