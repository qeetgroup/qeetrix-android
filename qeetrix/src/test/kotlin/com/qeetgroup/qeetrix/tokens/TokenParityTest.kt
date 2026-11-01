package com.qeetgroup.qeetrix.tokens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import com.qeetgroup.qeetrix.TestSupport
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.layout.QeetrixBreakpoint
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixIconStroke
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixFontGeometry
import com.qeetgroup.qeetrix.tokens.generated.QeetrixBorderTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixBreakpointTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixColorTokenPaths
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDarkColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixDensityContexts
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFocusTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixFontGeometryTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixIconStrokeTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixLightColors
import com.qeetgroup.qeetrix.tokens.generated.QeetrixOpacityTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixRadiusTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixShadowTokenPaths
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSizeTokens
import com.qeetgroup.qeetrix.tokens.generated.QeetrixSpaceTokens
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
 * colour the generator computed (which agrees with the hex @qeetrix/tokens records, see Tools/QeetrixGen
 * tests), and every generated token is reachable through the public API.
 * (Robolectric only for the real org.json; nothing here draws.)
 */
@RunWith(RobolectricTestRunner::class)
class TokenParityTest {
    @Test
    fun everyColourTokenIsExposedInUpstreamOrder() {
        assertEquals(TestSupport.paths("color"), QeetrixColorTokenPaths.map { it.first })
        assertEquals(109, QeetrixColorTokenPaths.size)
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
        assertEquals(ColorSpaces.DisplayP3, QeetrixLightColors.datavizCategorical2.colorSpace)
        assertEquals(ColorSpaces.Srgb, QeetrixLightColors.textDefault.colorSpace)
        // The brand values, exact, from @qeetrix/tokens' README: Ember #d04800 in both schemes, brand text
        // #9b3314 (light) and #ff9646 (dark).
        assertEquals(0xFFD04800.toInt(), QeetrixLightColors.bgBrandBold.toArgb())
        assertEquals(0xFFD04800.toInt(), QeetrixDarkColors.bgBrandBold.toArgb())
        assertEquals(0xFF9B3314.toInt(), QeetrixLightColors.textBrand.toArgb())
        assertEquals(0xFFFF9646.toInt(), QeetrixDarkColors.textBrand.toArgb())
    }

    @Test
    fun shadowsMatchTheGenerator() {
        assertEquals(QeetrixShadow.entries.map { it.token }.toSet(), QeetrixShadowTokenPaths.map { it.first }.toSet())
        for ((path, elevation) in QeetrixShadowTokenPaths) {
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
                Triple(QeetrixSpaceTokens::class, QeetrixSpace::class, "space"),
                Triple(QeetrixSizeTokens::class, QeetrixSize::class, "size"),
                Triple(QeetrixRadiusTokens::class, QeetrixRadius::class, "radius"),
                Triple(QeetrixBorderTokens::class, QeetrixBorder::class, "border"),
                Triple(QeetrixFocusTokens::class, QeetrixFocus::class, "focus"),
                Triple(QeetrixOpacityTokens::class, QeetrixOpacity::class, "opacity"),
                Triple(QeetrixIconStrokeTokens::class, QeetrixIconStroke::class, "icon.stroke"),
                Triple(QeetrixBreakpointTokens::class, QeetrixBreakpoint::class, "breakpoint"),
                Triple(QeetrixFontGeometryTokens::class, QeetrixFontGeometry::class, "font.geometry"),
            )
        for ((generated, public, prefix) in pairs) {
            val generatedValues = values(generated)
            val publicValues = values(public)
            assertEquals("$prefix: public names", generatedValues.keys, publicValues.keys)
            assertEquals("$prefix: public values", generatedValues, publicValues)
            // Every token under the prefix that does not follow the density (those are QeetrixDensity's).
            val paths =
                TestSupport.tokenValues
                    .keys()
                    .asSequence()
                    .filter { it.startsWith("$prefix.") && !TestSupport.tokenValues.getJSONObject(it).has("densities") }
                    .toList()
            assertEquals("$prefix: token count", paths.size, generatedValues.size)
            for (path in paths) {
                val expected = TestSupport.value(path)
                val number = if (expected is JSONObject) expected.getDouble("px") else (expected as Number).toDouble()
                val name = pascal(path.removePrefix("$prefix.").split('.'))
                assertEquals(path, number.toFloat(), asDouble(generatedValues[name]).toFloat(), 1e-6f)
            }
        }
    }

    /** Every token the density modifier changes is a [QeetrixDensity] property, with its value at every density. */
    @Test
    fun densityTokensMatchTheGenerator() {
        assertEquals(QeetrixDensity.entries.toSet(), QeetrixDensityContexts.map { it.second }.toSet())
        val readers: Map<String, (QeetrixDensity) -> Dp> =
            mapOf(
                "size.control.sm" to { it.sizeControlSm },
                "size.control.md" to { it.sizeControlMd },
                "size.control.lg" to { it.sizeControlLg },
                "size.row" to { it.sizeRow },
                "space.form.gap" to { it.spaceFormGap },
            )
        val following =
            TestSupport.tokenValues
                .keys()
                .asSequence()
                .filter { TestSupport.tokenValues.getJSONObject(it).has("densities") }
        assertEquals(readers.keys, following.toSet())
        for ((path, read) in readers) {
            val densities = TestSupport.tokenValues.getJSONObject(path).getJSONObject("densities")
            for ((name, density) in QeetrixDensityContexts) {
                assertEquals("$path ($name)", densities.getJSONObject(name).getDouble("px").toFloat(), read(density).value, 1e-6f)
                assertEquals(name, density.value)
            }
        }
    }

    @Test
    fun motionMatchesTheGenerator() {
        assertEquals(TestSupport.number("motion.duration.fast"), QeetrixMotion.DurationFast.toDouble(), 0.0)
        assertEquals(TestSupport.number("motion.duration.slower"), QeetrixMotion.DurationSlower.toDouble(), 0.0)
        assertEquals(TestSupport.number("motion.spring.snappy.stiffness"), QeetrixMotion.SpringSnappy.stiffness.toDouble(), 1e-6)
        assertEquals(TestSupport.number("motion.spring.gentle.damping-ratio"), QeetrixMotion.SpringGentle.dampingRatio.toDouble(), 1e-6)
    }

    @Test
    fun provenanceMatchesTheLock() {
        val lock = JSONObject(File(TestSupport.root, "Upstream/manifest.lock.json").readText())
        for ((key, provenance) in listOf(
            "qeetrixTokens" to QeetrixProvenance.tokens,
            "qeetrixReact" to QeetrixProvenance.react,
            "qeetrixIcons" to QeetrixProvenance.icons,
        )) {
            val locked = lock.getJSONObject(key)
            assertEquals(locked.getString("package"), provenance.packageName)
            assertEquals(locked.getString("version"), provenance.version)
            assertEquals(locked.getString("gitHead"), provenance.gitHead)
            assertEquals(locked.getString("integrity"), provenance.integrity)
        }
        val fonts = lock.getJSONArray("fonts")
        for (index in 0 until fonts.length()) {
            assertEquals(fonts.getJSONObject(index).getString("ttfSHA256"), QeetrixProvenance.fonts[index].sha256)
            assertEquals(fonts.getJSONObject(index).getString("woff2SHA256"), QeetrixProvenance.fonts[index].woff2Sha256)
        }
        assertEquals(TestSupport.tokenValues.length(), QeetrixProvenance.generatedTokens)
    }

    private fun values(type: KClass<*>): Map<String, Any?> {
        val instance = type.objectInstance ?: error("$type is not an object")
        return type.declaredMemberProperties.associate { property -> property.name to property.getter.call(instance) }
    }

    /** The shared naming rule (Tools/QeetrixGen KotlinNames): `inset.md` → `InsetMd`, `ring-width` → `RingWidth`. */
    private fun pascal(segments: List<String>): String {
        val words = segments.flatMap { it.split('-') }
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
