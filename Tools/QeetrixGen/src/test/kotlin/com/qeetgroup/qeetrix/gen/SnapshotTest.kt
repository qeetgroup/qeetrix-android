package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.fonts.FontInspector
import com.qeetgroup.qeetrix.gen.tokens.DtcgSource
import com.qeetgroup.qeetrix.gen.tokens.TokenModel
import com.qeetgroup.qeetrix.gen.tokens.TokenValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Tests against the real pinned snapshot in Upstream/. */
class SnapshotTest {
    private val root = Snapshot.root
    private val model by lazy { Snapshot.model() }

    @Test
    fun generationIsDeterministic() {
        assertEquals(Generator(root).outputs(), Generator(root).outputs())
    }

    @Test
    fun theCommittedFilesAreWhatTheSnapshotGenerates() {
        Generator(root).verify()
    }

    @Test
    fun everyUpstreamTokenIsDecided() {
        // Every token of @qeetrix/tokens 0.0.13 is decided: 122 primitives (0.0.13 adds qeet.650), 10 z-index steps,
        // the Indic stack and the 4 unadopted component tokens are excluded; with sidebar and timeline adopted, 202
        // are generated.
        assertEquals(202, model.included.size)
        assertEquals(137, model.excluded.size)
        assertEquals(listOf("light", "dark"), model.themes)
        assertEquals(listOf("default", "comfortable", "compact"), model.densities)
        assertEquals("default", model.defaultDensity)
        val foundations = Snapshot.model(emptySet())
        assertEquals(198, foundations.included.size)
    }

    @Test
    fun referencesResolveToTheirPrimitives() {
        // color.bg.brand.bold = {primitive.color.qeet.600}, Ember #d04800 in both schemes.
        val ember = model.token("color.bg.brand.bold")
        assertEquals(listOf("primitive.color.qeet.600"), ember.sources.getValue("light").chain)
        assertEquals("#D04800", (ember.themes.getValue("light") as TokenValue.Color).color.hex)
        assertEquals("#D04800", (ember.themes.getValue("dark") as TokenValue.Color).color.hex)
        // The light scheme's text.brand is set by modes/color-scheme/light.tokens.json, the dark one by dark's.
        assertEquals("modes/color-scheme/dark.tokens.json", model.token("color.text.brand").sources.getValue("dark").source.removePrefix("tokens/"))
        // A typography composite's references (font family, size, weight) are followed inside the composite.
        val body = model.token("typography.body.md").themes.getValue("light") as TokenValue.Typography
        assertEquals("Qeet Sans", body.family)
        assertEquals(14.0, body.sizePx, 0.0)
        assertEquals(400, body.weight)
    }

    @Test
    fun everyRoleNamesItsLetterStyleAndLandsOnTheGrid() {
        // @qeetrix/tokens 0.0.13: each composite names its font.geometry token (QTRX-186) and its line height as a
        // length on the 4 px grid (QTRX-184), the web's pairing of design rules §5.
        val roles = model.tokensUnder("typography").map { it to it.themes.getValue("light") as TokenValue.Typography }
        val geometry = roles.groupBy({ it.first.path[1] }, { it.second.geometry.joinToString(".") }).mapValues { it.value.toSet() }
        assertEquals(setOf("font.geometry.display"), geometry["display"])
        assertEquals(setOf("font.geometry.heading"), geometry["heading"])
        assertEquals(setOf("font.geometry.heading"), geometry["title"])
        for (role in listOf("body", "label", "code")) assertEquals(role, setOf("font.geometry.body"), geometry[role])
        for ((token, value) in roles) {
            assertEquals(token.dotted, 0.0, value.lineHeightPx % 4, 0.0)
            assertEquals(token.dotted, value.lineHeightPx, value.sizePx * value.lineHeight, 1e-9)
            // Compose multiplies in single precision: the product is the length exactly, so no line rounds up a pixel.
            assertEquals(token.dotted, value.lineHeightPx.toFloat(), value.sizePx.toFloat() * value.lineHeight.toFloat(), 0f)
        }
        assertEquals(20.0, (model.token("typography.body.md").themes.getValue("light") as TokenValue.Typography).lineHeightPx, 0.0)
    }

    @Test
    fun aLetterStyleThatIsNotAGeometryTokenFails() {
        val tampered = Snapshot.dtcg.mapValues { it.value.replace("\"{font.geometry.body}\"", "\"{space.inset.md}\"") }
        val error = assertThrows(GeneratorError::class.java) { TokenModel(DtcgSource(tampered), setOf("sidebar", "timeline")) }
        assertTrue(error.message!!, error.message!!.contains("fontGeometry {space.inset.md}"))
    }

    @Test
    fun densityTokensFollowTheResolversDensityModifier() {
        val control = model.token("size.control.md")
        assertTrue(control.followsDensity)
        assertEquals(
            mapOf("default" to 32.0, "comfortable" to 36.0, "compact" to 28.0),
            control.densities!!.mapValues { (it.value as TokenValue.Dimension).px },
        )
        assertEquals(
            setOf("size.control.sm", "size.control.md", "size.control.lg", "size.row", "space.form.gap"),
            model.included.filter { it.followsDensity }.map { it.dotted }.toSet(),
        )
        assertNull(model.token("space.inset.md").densities)
    }

    @Test
    fun onlyColoursAndShadowsVaryByColourScheme() {
        for (token in model.included) assertEquals(token.dotted, model.themes, token.themes.keys.toList())
        assertEquals(setOf("color", "shadow"), model.included.filter { !it.isThemeInvariant }.map { it.path.first() }.toSet())
    }

    @Test
    fun theResolvedDocumentsMustAgreeWithTheResolver() {
        // Change one value in the published light document only: verification names the token.
        val tampered =
            Snapshot.dtcg + (
                "light.tokens.json" to
                    Snapshot.dtcg.getValue("light.tokens.json").replaceFirst("\"value\": 3,", "\"value\": 4,")
            )
        val error =
            assertThrows(GeneratorError::class.java) {
                DtcgSource(tampered).verifyPublished("light.tokens.json", mapOf("colorScheme" to "light", "density" to "default"))
            }
        assertTrue(error.message!!, error.message!!.contains("disagrees"))
    }

    @Test
    fun adoptingAGroupUpstreamDoesNotDefineFails() {
        val error = assertThrows(GeneratorError::class.java) { TokenModel(Snapshot.source, setOf("sidebar", "hologram")) }
        assertTrue(error.message!!.contains("hologram"))
    }

    @Test
    fun aMissingReferenceFailsByName() {
        val broken = Snapshot.dtcg + ("tokens/component.tokens.json" to """{"component": {"x": {"y": {"${'$'}type": "dimension", "${'$'}value": "{nowhere.at.all}"}}}}""")
        val error = assertThrows(GeneratorError::class.java) { DtcgSource(broken).resolve(emptyMap()) }
        assertTrue(error.message!!, error.message!!.contains("nowhere.at.all"))
    }

    @Test
    fun theBundledFontsDescribeThemselves() {
        val sans = FontInspector.inspect(File(root, "qeetrix/src/main/res/font/qx_qeet_sans_vf.ttf").readBytes(), "sans")
        assertEquals("Cal Sans", sans.familyName) // Qeet Sans is Cal Sans; the file keeps its own family name
        val geom = sans.axes.single { it.tag == "GEOM" }
        assertEquals(listOf(0.0, 25.0, 100.0), listOf(geom.minimum, geom.defaultValue, geom.maximum))
        assertEquals(listOf("opsz", "GEOM", "wght", "YTAS", "SHRP", "ital"), sans.axes.map { it.tag })
        val mono = FontInspector.inspect(File(root, "qeetrix/src/main/res/font/qx_fira_code_vf.ttf").readBytes(), "mono")
        assertEquals("Fira Code", mono.familyName)
        // WOFF2 is a web container, not a font Android can load.
        assertThrows(GeneratorError::class.java) { FontInspector.inspect("wOF2....".toByteArray() + ByteArray(64), "woff2") }
    }
}
