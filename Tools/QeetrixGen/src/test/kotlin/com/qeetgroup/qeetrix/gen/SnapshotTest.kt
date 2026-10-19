package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.fonts.FontInspector
import com.qeetgroup.qeetrix.gen.tokens.TokenModel
import com.qeetgroup.qeetrix.gen.tokens.TokenSource
import com.qeetgroup.qeetrix.gen.tokens.TokenValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Tests against the real pinned snapshot in Upstream/. */
class SnapshotTest {
    private val root = File(System.getProperty("qeetrix.root"))

    private fun upstream(name: String) = File(root, "Upstream/qeetrix-ui/$name").readText()

    private val adopted = setOf("avatar", "badge", "button", "card", "input", "progress", "switch", "typography")
    private val source by lazy { TokenSource(upstream("tokens.json"), upstream("tokens.css"), upstream("index.css")) }
    private val model by lazy { TokenModel(source, adopted) }

    @Test
    fun generationIsDeterministic() {
        val first = Generator(root).outputs()
        val second = Generator(root).outputs()
        assertEquals(first, second)
    }

    @Test
    fun theCommittedFilesAreWhatTheSnapshotGenerates() {
        Generator(root).verify()
    }

    @Test
    fun everyUpstreamTokenIsDecided() {
        // Every leaf of tokens.json is decided. Foundations alone: 217 generated, 666 excluded; with the
        // eight Phase 2 component groups adopted: 279 and 604 — the same splits as qeetrix-ios.
        val foundations = TokenModel(source)
        assertEquals(217, foundations.included.size)
        assertEquals(666, foundations.excluded.size)
        assertEquals(279, model.included.size)
        assertEquals(604, model.excluded.size)
        assertEquals(listOf("light", "dark"), model.themes)
    }

    @Test
    fun aliasesResolveThroughTheBridgeAndRadiusScale() {
        // corner.surface = var(--radius-xl) = calc(var(--radius) * 1.4), --radius = 0.45rem → 10.08.
        val surface = model.included.single { it.dotted == "corner.surface" }
        assertEquals("calc(0.45rem * 1.4)", surface.inputs.getValue("light"))
        assertEquals(
            TokenValue.Dimension(10.08),
            surface.values.getValue("light").let {
                TokenValue.Dimension(round6((it as TokenValue.Dimension).dp))
            },
        )
    }

    @Test
    fun componentTokensKeepTheReferencesUpstreamWrites() {
        fun token(path: String) = model.included.single { it.dotted == path }
        // A semantic colour named in both themes stays that colour.
        assertEquals(TokenValue.ColorRef::class, token("component.button.primary.background").values.getValue("dark")::class)
        assertEquals(
            "color.action.primary",
            (token("component.button.primary.background").values.getValue("light") as TokenValue.ColorRef).path,
        )
        // A reference in one theme and a mix in the other is a value per theme.
        assertEquals(TokenValue.Color::class, token("component.input.background").values.getValue("dark")::class)
        // Density, elevation, percentage and em keep their meaning.
        assertEquals(TokenValue.DensityRef("control-height"), token("component.input.height").values.getValue("light"))
        assertEquals(TokenValue.ElevationRef("elevation.raised"), token("component.card.elevation").values.getValue("light"))
        assertEquals(TokenValue.Percent(0.25), token("component.avatar.corner-square").values.getValue("light"))
        assertEquals(TokenValue.Em(0.875), token("component.typography.code-font-size").values.getValue("light"))
        // The radius scale resolves: button.corner-xs = min(var(--radius-md), 10px) = 5.76.
        assertEquals(5.76, (token("component.button.corner-xs").values.getValue("light") as TokenValue.Dimension).dp, 1e-9)
    }

    @Test
    fun adoptingAGroupUpstreamDoesNotDefineFails() {
        val error = assertThrows(GeneratorError::class.java) { TokenModel(source, setOf("button", "hologram")) }
        assertTrue(error.message!!.contains("hologram"))
    }

    @Test
    fun lightAndDarkHaveTheSameTokens() {
        for (token in model.included) assertEquals(token.dotted, model.themes, token.values.keys.toList())
        val varying =
            model.included
                .filter { !it.isThemeInvariant }
                .map { it.path.first() }
                .toSet()
        assertEquals(setOf("color", "elevation", "component"), varying)
    }

    @Test
    fun aNewThemeNeedsAnAndroidMappingFirst() {
        val json = """{"light": {"a": "1px"}, "contrast": {"a": "1px"}}"""
        val error = assertThrows(GeneratorError::class.java) { TokenSource(json, "", "") }
        assertTrue(error.message!!.contains("no Android mapping"))
    }

    @Test
    fun theBundledFontsDescribeThemselves() {
        val sans = FontInspector.inspect(File(root, "qeetrix/src/main/res/font/qx_cal_sans_vf.ttf").readBytes(), "sans")
        assertEquals("Cal Sans", sans.familyName)
        val geom = sans.axes.single { it.tag == "GEOM" }
        assertEquals(listOf(0.0, 25.0, 100.0), listOf(geom.minimum, geom.defaultValue, geom.maximum))
        assertEquals(listOf("opsz", "GEOM", "wght", "YTAS", "SHRP", "ital"), sans.axes.map { it.tag })
        val mono = FontInspector.inspect(File(root, "qeetrix/src/main/res/font/qx_fira_code_vf.ttf").readBytes(), "mono")
        assertEquals("Fira Code", mono.familyName)
        // WOFF2 is a web container, not a font Android can load.
        assertThrows(GeneratorError::class.java) { FontInspector.inspect("wOF2....".toByteArray() + ByteArray(64), "woff2") }
    }
}
