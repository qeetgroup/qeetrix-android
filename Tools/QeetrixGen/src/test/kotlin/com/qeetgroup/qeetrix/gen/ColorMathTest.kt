package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.color.ColorMath
import com.qeetgroup.qeetrix.gen.color.ColorValues
import com.qeetgroup.qeetrix.gen.css.CssParser
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ColorMathTest {
    private fun encode(css: String) = ColorMath.encode(ColorValues.color(CssParser.parse(css, css), css))

    // The agreed brand values, from qeetrix-ui's own records (its CHANGELOG and contrast gate), not
    // from this generator: Qeet orange #F26D0E (qeet.500), Qeet Ember #D04800 (qeet.600) and the
    // light-theme brand text #B73D10 (qeet.700).
    @Test
    fun brandValuesAreExact() {
        assertEquals("#F26D0E", encode("oklch(0.686 0.1855 47.1629)").hex)
        assertEquals("#D04800", encode("oklch(0.585 0.1835 40)").hex)
        assertEquals("#B73D10", encode("oklch(0.53 0.165 38)").hex)
    }

    @Test
    fun whiteAndBlackStayInSrgb() {
        // oklch(1 0 0) converts to 1.0000000000000004; the tolerance keeps it white, not "out of gamut".
        val white = encode("oklch(1 0 0)")
        assertEquals(ColorMath.Space.SRGB, white.space)
        assertEquals(listOf(1.0, 1.0, 1.0, 1.0), listOf(white.red, white.green, white.blue, white.alpha))
        assertEquals("#000000", encode("oklch(0 0 0)").hex)
    }

    @Test
    fun outsideSrgbInsideP3IsDrawnInP3() {
        // Tailwind amber-400 (color.rating.filled, light): outside sRGB, inside Display P3.
        val amber = encode("oklch(0.828 0.189 84.429)")
        assertEquals(ColorMath.Space.DISPLAY_P3, amber.space)
        assertFalse(amber.gamutMapped)
        assertTrue(ColorMath.inGamut(listOf(amber.red, amber.green, amber.blue)))
    }

    @Test
    fun outsideP3IsGamutMappedPreservingLightnessAndHue() {
        val lab = ColorMath.oklch(0.7, 0.4, 150.0)
        val mapped = ColorMath.encode(lab)
        assertEquals(ColorMath.Space.DISPLAY_P3, mapped.space)
        assertTrue(mapped.gamutMapped)
        for (channel in listOf(mapped.red, mapped.green, mapped.blue)) assertTrue(channel in 0.0..1.0)
        // Back to OKLab through the (unclamped, sign-preserving) sRGB encoding.
        val back = ColorMath.okLabFromSrgb(ColorMath.p3ToSrgb(listOf(mapped.red, mapped.green, mapped.blue)))
        // CSS Color 4 §13.2 reduces chroma: lightness stays within the JND and the hue barely moves.
        assertEquals(0.7, back.l, 0.02)
        assertEquals(150.0, Math.toDegrees(Math.atan2(back.b, back.a)), 3.0)
        assertTrue(Math.hypot(back.a, back.b) < 0.4)
    }

    @Test
    fun alphaIsCarriedAndRounded() {
        val translucent = encode("oklch(0 0 0 / 40%)")
        assertEquals(0.4, translucent.alpha, 0.0)
        assertEquals("#00000066", translucent.hex)
        assertEquals(0x66000000L, translucent.argb)
    }

    @Test
    fun colorMixFollowsCss5() {
        // A colour mixed with transparent keeps its colour and takes the percentage as alpha
        // (premultiplied interpolation) — how color.overlay.scrim is written.
        val scrim = encode("color-mix(in oklab, oklch(0.11 0 0) 40%, transparent)")
        assertEquals(encode("oklch(0.11 0 0)").copy(alpha = 0.4), scrim)
        // No percentages: 50/50.
        val half = ColorMath.mix(ColorMath.OkLab(0.2, 0.0, 0.0), null, ColorMath.OkLab(0.8, 0.0, 0.0), null)
        assertEquals(0.5, half.l, 1e-12)
        // Percentages summing under 100% scale the result's alpha.
        val partial = ColorMath.mix(ColorMath.OkLab(0.2, 0.0, 0.0), 20.0, ColorMath.OkLab(0.8, 0.0, 0.0), 30.0)
        assertEquals(0.5, partial.alpha, 1e-12)
        assertEquals(0.56, partial.l, 1e-12)
    }

    @Test(expected = GeneratorError::class)
    fun onlyOklabMixingIsAccepted() {
        encode("color-mix(in srgb, oklch(0.5 0 0), transparent)")
    }

    /**
     * The independent oracle: culori, the converter qeetrix-ui's contrast gate uses, run over every
     * colour input of the 2.1.6 snapshot (src/test/resources/culori-vectors.json). Every colour must
     * have the same 8-bit sRGB rendition — P3 colours compared after clipping, as culori clips.
     */
    @Test
    fun everySnapshotColourMatchesTheWebsConverter() {
        val vectors =
            Json
                .parseToJsonElement(javaClass.getResource("/culori-vectors.json")!!.readText())
                .jsonObject
                .getValue("vectors")
                .jsonObject
        assertEquals(168, vectors.size)
        val mismatches =
            vectors.mapNotNull { (key, vector) ->
                val input =
                    vector.jsonObject
                        .getValue("input")
                        .jsonPrimitive.content
                val expected =
                    vector.jsonObject
                        .getValue("hex")
                        .jsonPrimitive.content
                val ours = encode(input).hex.let { if (it.length == 7) it + "FF" else it }
                if (ours == expected) null else "$key: $input → $ours, culori $expected"
            }
        assertEquals(emptyList<String>(), mismatches)
        assertTrue(File(System.getProperty("qeetrix.root")).exists())
    }
}
