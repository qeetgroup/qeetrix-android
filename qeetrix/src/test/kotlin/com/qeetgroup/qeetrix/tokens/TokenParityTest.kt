package com.qeetgroup.qeetrix.tokens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.TestSupport
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconSize
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconStroke
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.foundation.typography.QeetrixFontGeometry
import com.qeetgroup.qeetrix.tokens.generated.QeetrixColorTokenPaths
import com.qeetgroup.qeetrix.tokens.generated.QeetrixCornerTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixElevationTokenPaths
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFocusTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFontGeometryTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixIconSizeTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixIconStrokeTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixOpacityTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSpaceTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixStrokeWidthTokens
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

/**
 * The Kotlin that ships against the generator's value table: every colour the library hands out is the
 * colour the generator computed (which agrees with qeetrix-ios and with the web's own converter, see
 * Tools/QeetrixGen tests), and every generated token is reachable through the public API.
 * (Robolectric only for the real org.json; nothing here draws.)
 */
@RunWith(RobolectricTestRunner::class)
class TokenParityTest {
    @Test
    fun everyColourTokenIsExposedInUpstreamOrder() {
        assertEquals(TestSupport.paths("color"), QeetrixColorTokenPaths.map { it.first })
        assertEquals(84, QeetrixColorTokenPaths.size)
    }

    @Test
    fun colourValuesMatchTheGeneratorInBothThemes() {
        val mismatches = mutableListOf<String>()
        for ((theme, colors) in listOf("light" to QeetrixLightColors, "dark" to QeetrixDarkColors)) {
            for ((path, read) in QeetrixColorTokenPaths) {
                val expected = TestSupport.value(path, theme) as JSONObject
                val actual = read(colors)
                if (!matches(actual, expected)) mismatches += "$path ($theme): $actual vs $expected"
            }
        }
        assertEquals(emptyList<String>(), mismatches)
    }

    @Test
    fun outOfSrgbColoursAreDisplayP3() {
        assertEquals(ColorSpaces.DisplayP3, QeetrixLightColors.dataCategorical2.colorSpace)
        assertEquals(ColorSpaces.DisplayP3, QeetrixLightColors.ratingFilled.colorSpace)
        assertEquals(ColorSpaces.Srgb, QeetrixLightColors.textPrimary.colorSpace)
        // The brand values, exact.
        assertEquals(0xFFB73D10.toInt(), QeetrixLightColors.textBrand.toArgb())
        assertEquals(0xFFD04800.toInt(), QeetrixLightColors.textBrandDisplay.toArgb())
        assertEquals(0xFFF26D0E.toInt(), QeetrixDarkColors.textBrand.toArgb())
    }

    @Test
    fun elevationsMatchTheGenerator() {
        assertEquals(QeetrixElevation.entries.map { it.token }, QeetrixElevationTokenPaths.map { it.first })
        for ((path, elevation) in QeetrixElevationTokenPaths) {
            assertEquals(path, elevation.token)
            for ((theme, dark) in listOf("light" to false, "dark" to true)) {
                val expected = TestSupport.value(path, theme) as JSONArray
                val layers = elevation.layers(dark)
                assertEquals("$path ($theme)", expected.length(), layers.size)
                for ((index, layer) in layers.withIndex()) {
                    val e = expected.getJSONObject(index)
                    assertEquals(e.getBoolean("inset"), layer.inset)
                    assertEquals(e.getDouble("x").toFloat(), layer.offsetX.value, 1e-6f)
                    assertEquals(e.getDouble("y").toFloat(), layer.offsetY.value, 1e-6f)
                    assertEquals(e.getDouble("blur").toFloat(), layer.blur.value, 1e-6f)
                    assertEquals(e.getDouble("spread").toFloat(), layer.spread.value, 1e-6f)
                    assertTrue("$path ($theme) layer $index colour", matches(layer.color, e.getJSONObject("color")))
                }
            }
        }
    }

    /**
     * Every generated scale constant has a public counterpart of the same name and value, and the
     * value is the generator's. A token added upstream without a public property fails here.
     */
    @Test
    fun everyScaleTokenIsPublicWithItsValue() {
        val pairs: List<Triple<KClass<*>, KClass<*>, String>> =
            listOf(
                Triple(QeetrixSpaceTokens::class, QeetrixSpacing::class, "space"),
                Triple(QeetrixCornerTokens::class, QeetrixCorner::class, "corner"),
                Triple(QeetrixStrokeWidthTokens::class, QeetrixStroke::class, "stroke.width"),
                Triple(QeetrixFocusTokens::class, QeetrixFocus::class, "focus"),
                Triple(QeetrixOpacityTokens::class, QeetrixOpacity::class, "state.opacity"),
                Triple(QeetrixIconSizeTokens::class, QeetrixIconSize::class, "icon.size"),
                Triple(QeetrixIconStrokeTokens::class, QeetrixIconStroke::class, "icon.stroke"),
                Triple(QeetrixFontGeometryTokens::class, QeetrixFontGeometry::class, "font.geometry"),
            )
        for ((generated, public, prefix) in pairs) {
            val generatedValues = values(generated)
            val publicValues = values(public)
            assertEquals("$prefix: public names", generatedValues.keys, publicValues.keys)
            assertEquals("$prefix: public values", generatedValues, publicValues)
            val paths =
                TestSupport.tokenValues
                    .keys()
                    .asSequence()
                    .filter {
                        it.startsWith("$prefix.") &&
                            it.count { c -> c == '.' } == prefix.count { c -> c == '.' } + 1
                    }.toList()
            assertEquals("$prefix: token count", paths.size, generatedValues.size)
            for (path in paths) {
                val expected = TestSupport.value(path)
                val number = if (expected is JSONObject) expected.getDouble("dp") else (expected as Number).toDouble()
                val name = pascal(path.removePrefix("$prefix."))
                assertEquals(path, number.toFloat(), asDouble(generatedValues[name]).toFloat(), 1e-6f)
            }
        }
    }

    @Test
    fun provenanceMatchesTheLock() {
        val lock = JSONObject(File(TestSupport.root, "Upstream/manifest.lock.json").readText())
        val ui = lock.getJSONObject("qeetrixUI")
        assertEquals(ui.getString("version"), QeetrixProvenance.tokensVersion)
        assertEquals(ui.getString("gitHead"), QeetrixProvenance.tokensGitHead)
        assertEquals(ui.getString("integrity"), QeetrixProvenance.tokensIntegrity)
        assertEquals(lock.getJSONObject("qeetrixIcons").getString("commit"), QeetrixProvenance.iconsCommit)
        val fonts = lock.getJSONArray("fonts")
        for (index in 0 until fonts.length()) {
            assertEquals(fonts.getJSONObject(index).getString("ttfSHA256"), QeetrixProvenance.fonts[index].sha256)
        }
        assertEquals(TestSupport.tokenValues.length(), QeetrixProvenance.generatedTokens)
    }

    private fun values(type: KClass<*>): Map<String, Any?> {
        val instance = type.objectInstance ?: error("$type is not an object")
        return type.declaredMemberProperties.associate { property -> property.name to property.getter.call(instance) }
    }

    /** The shared naming rule (Tools/QeetrixGen KotlinNames): `2xl` → `Xl2`, `0` → `Zero`, `ring-width` → `RingWidth`. */
    private fun pascal(segment: String): String {
        val words = segment.split('-')
        val first =
            words.first().let { word ->
                when {
                    word == "0" -> "zero"
                    word.first().isDigit() -> word.dropWhile { it.isDigit() } + word.takeWhile { it.isDigit() }
                    else -> word
                }
            }
        return (listOf(first) + words.drop(1)).joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }
    }

    private fun asDouble(value: Any?): Double =
        when (value) {
            is Dp -> value.value.toDouble()
            is Float -> value.toDouble()
            is Int -> value.toDouble()
            else -> Double.NaN
        }

    companion object {
        /** A Compose colour against the generator's `{space, rgba}`: exact 8-bit for sRGB, fp16 for P3. */
        fun matches(
            actual: Color,
            expected: JSONObject,
        ): Boolean {
            val rgba = expected.getJSONArray("rgba")
            val components = (0 until 4).map { rgba.getDouble(it) }
            return when (expected.getString("space")) {
                "srgb" -> {
                    val bytes = components.map { Math.round(it.coerceIn(0.0, 1.0) * 255).toInt() }
                    actual.colorSpace == ColorSpaces.Srgb &&
                        actual.toArgb() == ((bytes[3] shl 24) or (bytes[0] shl 16) or (bytes[1] shl 8) or bytes[2])
                }

                "display-p3" -> {
                    actual.colorSpace == ColorSpaces.DisplayP3 &&
                        listOf(actual.red, actual.green, actual.blue, actual.alpha).zip(components).all { (a, e) -> Math.abs(a - e) < 1e-3 }
                }

                else -> {
                    false
                }
            }
        }
    }
}
