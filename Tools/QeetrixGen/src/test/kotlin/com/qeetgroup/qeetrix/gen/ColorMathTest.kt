package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.color.ColorMath
import com.qeetgroup.qeetrix.gen.tokens.TokenKind
import com.qeetgroup.qeetrix.gen.tokens.TokenValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorMathTest {
    /** `oklch(L C H)` or `oklch(L C H / A%)`, as @qeetrix/tokens' CSS writes a colour. */
    private fun encode(css: String): ColorMath.EncodedColor {
        val match = Regex("""oklch\(([\d.]+) ([\d.]+) ([\d.]+)(?: / ([\d.]+)%)?\)""").matchEntire(css)!!
        val (l, c, h) = match.destructured
        val alpha = match.groupValues[4].ifEmpty { "100" }.toDouble() / 100
        return ColorMath.encode(ColorMath.oklch(l.toDouble(), c.toDouble(), h.toDouble(), alpha))
    }

    // The agreed brand values, from @qeetrix/tokens' own records (its README's palette), not from this
    // generator: Qeet orange #F26D0E (qeet.500), Ember #D04800 (qeet.600) and #B73D10 (qeet.700).
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

    /**
     * The independent oracle: the `hex` @qeetrix/tokens records beside every colour's OKLCH components,
     * which its build computes with culori, the web's converter. Every generated colour must have the same
     * 8-bit sRGB rendition — P3 colours compared after clipping, as culori clips.
     */
    @Test
    fun everyColourMatchesTheHexTheTokensRecord() {
        val model = Snapshot.model()
        val colours =
            model.included.flatMap { token ->
                token.themes.map { (theme, value) -> Triple(token.dotted, theme, value) }
            }
        val values = colours.filter { (_, _, value) -> value is TokenValue.Color }
        assertTrue(values.size > 150)
        assertTrue(model.included.any { it.kind == TokenKind.COLOR })
        val mismatches =
            values.mapNotNull { (path, theme, value) ->
                val color = value as TokenValue.Color
                val ours = color.color.hex.take(7).lowercase()
                val theirs = color.dtcgHex?.lowercase()
                if (theirs == null || ours == theirs) null else "$path ($theme): $ours, @qeetrix/tokens records $theirs"
            }
        assertEquals(emptyList<String>(), mismatches)
    }
}
