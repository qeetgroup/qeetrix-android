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

    private val model by lazy { TokenModel(TokenSource(upstream("tokens.json"), upstream("tokens.css"), upstream("index.css"))) }

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
        // 217 generated + 666 excluded = every leaf of tokens.json; the same split as qeetrix-ios.
        assertEquals(217, model.included.size)
        assertEquals(666, model.excluded.size)
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
    fun lightAndDarkHaveTheSameTokens() {
        for (token in model.included) assertEquals(token.dotted, model.themes, token.values.keys.toList())
        val varying =
            model.included
                .filter { !it.isThemeInvariant }
                .map { it.path.first() }
                .toSet()
        assertEquals(setOf("color", "elevation"), varying)
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
