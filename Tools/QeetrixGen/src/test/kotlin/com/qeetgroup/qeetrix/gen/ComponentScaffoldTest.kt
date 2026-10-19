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

/** `new-component` against the real repository (planning only) and against a temporary copy (writing). */
class ComponentScaffoldTest {
    private val root = File(System.getProperty("qeetrix.root"))
    private val slider = Request("Slider", "Slider", "Forms & inputs")

    @Test
    fun plansEveryFileForANewComponentWithoutWritingAny() {
        val changes = ComponentScaffold(root).plan(slider)
        assertEquals(
            listOf(
                "create qeetrix/src/main/kotlin/com/qeetgroup/qeetrix/components/slider/QeetrixSlider.kt",
                "create qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/slider/QeetrixSliderTest.kt",
                "create qeetrix/src/test/kotlin/com/qeetgroup/qeetrix/components/slider/QeetrixSliderScreenshotTest.kt",
                "create sample/src/main/kotlin/com/qeetgroup/qeetrix/sample/components/forms/SliderSample.kt",
                "create Documentation/Components/Slider.md",
                "update ${ComponentScaffold.CATALOG_PATH}",
                "update ${ComponentScaffold.INDEX_PATH}",
            ),
            changes.map { "${it.kind.verb} ${it.path}" },
        )
        assertFalse(File(root, changes.first().path).exists())
        for (change in changes) assertFalse("${change.path} keeps a placeholder", "{{" in change.contents)
        val source = changes.first().contents
        assertTrue("package com.qeetgroup.qeetrix.components.slider" in source)
        assertTrue("public fun QeetrixSlider(modifier: Modifier = Modifier)" in source)
        assertTrue("level = DeprecationLevel.ERROR" in source && "    scaffold()\n" in source)
    }

    @Test
    fun theSameRequestGivesTheSameBytes() {
        assertEquals(ComponentScaffold(root).plan(slider), ComponentScaffold(root).plan(slider))
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
        assertTrue("UpperCamelCase" in failure(Request("Checkbox", "checkbox", "Forms & inputs")))
        assertTrue("without the Qeetrix prefix" in failure(Request("Checkbox", "QeetrixCheckbox", "Forms & inputs")))
        assertTrue("Unknown category \"Inputs\"" in failure(Request("Checkbox", "Checkbox", "Inputs")))
        assertTrue("No component \"tick-box\"" in failure(Request("Checkbox", "TickBox", "Forms & inputs")))
        assertTrue("icon-button belongs to the Button family" in failure(Request("Card", "IconButton", "Actions")))
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
        val changes = ComponentScaffold(root).plan(Request("Alert", "Alert", "Feedback"))
        val pins = changes.single { it.path == ComponentScaffold.UPSTREAM_PATH }.contents
        val original = File(root, ComponentScaffold.UPSTREAM_PATH).readText()
        assertTrue("      \"alert\",\n      \"avatar\"," in pins)
        assertEquals(original, pins.replace("      \"alert\",\n", ""))
        assertTrue("Component tokens: QeetrixAlertComponentTokens." in changes.first().contents)
    }

    @Test
    fun writingTwiceChangesNothingTheSecondTime() {
        val copy = repositoryCopy()
        val scaffold = ComponentScaffold(copy)
        scaffold.apply(scaffold.plan(slider))

        val catalog = File(copy, ComponentScaffold.CATALOG_PATH).readText()
        assertTrue(
            "        SampleEntry(\"slider\", \"Slider\", SampleCategory.Forms) { SliderSample() },\n" +
                ComponentScaffold.CATALOG_MARKER in catalog,
        )
        val imports = catalog.lines().filter { it.startsWith("import ") }
        assertTrue("import com.qeetgroup.qeetrix.sample.components.forms.SliderSample" in imports)
        assertEquals(imports.sorted(), imports)
        val index = File(copy, ComponentScaffold.INDEX_PATH).readText().lines()
        val row = index.indexOfFirst { "[Slider](Slider.md)" in it }
        assertTrue(index[row - 1].startsWith("| Forms & inputs | [Textarea]"))

        val written = copy.walkTopDown().filter { it.isFile }.associateWith { it.readText() }
        assertThrows(GeneratorError::class.java) { scaffold.plan(slider) }
        assertEquals(written, copy.walkTopDown().filter { it.isFile }.associateWith { it.readText() })
    }

    @Test
    fun aFileThatAppearsAfterPlanningIsNotOverwritten() {
        val copy = repositoryCopy()
        val scaffold = ComponentScaffold(copy)
        val changes = scaffold.plan(slider)
        val late = File(copy, changes.first { it.kind == Change.Kind.Create }.path)
        late.parentFile.mkdirs()
        late.writeText("mine")
        assertThrows(GeneratorError::class.java) { scaffold.apply(changes) }
        assertEquals("mine", late.readText())
        assertFalse(File(copy, "Documentation/Components/Slider.md").exists())
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
