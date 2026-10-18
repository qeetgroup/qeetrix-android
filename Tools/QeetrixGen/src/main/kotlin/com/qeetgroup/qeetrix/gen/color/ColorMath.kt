package com.qeetgroup.qeetrix.gen.color

import com.qeetgroup.qeetrix.gen.GeneratorError
import com.qeetgroup.qeetrix.gen.css.CssComponent
import com.qeetgroup.qeetrix.gen.css.splitOnCommas
import com.qeetgroup.qeetrix.gen.round6
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Colour conversion following the CSS Color 4 sample code
 * (https://www.w3.org/TR/css-color-4/#color-conversion-code), so a token converts to the colour a
 * browser paints. The matrices, gamut decision, gamut mapping and rounding are the same as
 * qeetrix-ios's, so both platforms emit identical components for every token.
 */
object ColorMath {
    /** A colour in OKLab with straight (non-premultiplied) alpha. */
    data class OkLab(
        val l: Double,
        val a: Double,
        val b: Double,
        val alpha: Double = 1.0,
    )

    enum class Space(
        val id: String,
    ) {
        SRGB("srgb"),
        DISPLAY_P3("display-p3"),
    }

    /** A gamma-encoded RGB colour, every component rounded to six decimals in 0…1. */
    data class EncodedColor(
        val space: Space,
        val red: Double,
        val green: Double,
        val blue: Double,
        val alpha: Double,
        /** True when the colour lay outside Display P3 and was gamut-mapped (CSS Color 4 §13.2). */
        val gamutMapped: Boolean,
    ) {
        /** `#RRGGBB` (with `AA` when translucent) of the sRGB rendition, clipped. */
        val hex: String
            get() {
                val srgb = if (space == Space.SRGB) listOf(red, green, blue) else p3ToSrgb(listOf(red, green, blue))
                val channels = srgb + if (alpha < 1) listOf(alpha) else emptyList()
                return "#" + channels.joinToString("") { "%02X".format(byte(it)) }
            }

        /** `0xAARRGGBB` of the sRGB rendition — exactly what Compose stores for an sRGB colour. */
        val argb: Long
            get() {
                require(space == Space.SRGB) { "argb is only defined for sRGB colours." }
                return (byte(alpha).toLong() shl 24) or (byte(red).toLong() shl 16) or (byte(green).toLong() shl 8) or byte(blue).toLong()
            }
    }

    /** A 0…1 channel as a byte, rounded half away from zero, as qeetrix-ios writes its hex values. */
    fun byte(channel: Double): Int = Math.round(min(max(channel, 0.0), 1.0) * 255).toInt()

    // OKLCH / OKLab

    fun oklch(
        l: Double,
        c: Double,
        h: Double,
        alpha: Double = 1.0,
    ): OkLab {
        val radians = h * Math.PI / 180
        return OkLab(l, c * cos(radians), c * sin(radians), alpha)
    }

    private val okLabToLms =
        arrayOf(
            doubleArrayOf(1.0000000000000000, 0.3963377773761749, 0.2158037573099136),
            doubleArrayOf(1.0000000000000000, -0.1055613458156586, -0.0638541728258133),
            doubleArrayOf(1.0000000000000000, -0.0894841775298119, -1.2914855480194092),
        )
    private val lmsToXyz =
        arrayOf(
            doubleArrayOf(1.2268798758459243, -0.5578149944602171, 0.2813910456659647),
            doubleArrayOf(-0.0405757452148008, 1.1122868032803170, -0.0717110580655164),
            doubleArrayOf(-0.0763729366746601, -0.4214933324022432, 1.5869240198367816),
        )
    private val xyzToLms =
        arrayOf(
            doubleArrayOf(0.8190224379967030, 0.3619062600528904, -0.1288737815209879),
            doubleArrayOf(0.0329836539323885, 0.9292868615863434, 0.0361446663506424),
            doubleArrayOf(0.0481771893596242, 0.2642395317527308, 0.6335478284694309),
        )
    private val lmsToOkLab =
        arrayOf(
            doubleArrayOf(0.2104542683093140, 0.7936177747023054, -0.0040720430116193),
            doubleArrayOf(1.9779985324311684, -2.4285922420485799, 0.4505937096174110),
            doubleArrayOf(0.0259040424655478, 0.7827717124575296, -0.8086757660832500),
        )
    private val xyzToLinearSrgb =
        arrayOf(
            doubleArrayOf(12831.0 / 3959, -329.0 / 214, -1974.0 / 3959),
            doubleArrayOf(-851781.0 / 878810, 1648619.0 / 878810, 36519.0 / 878810),
            doubleArrayOf(705.0 / 12673, -2585.0 / 12673, 705.0 / 667),
        )
    private val linearSrgbToXyz =
        arrayOf(
            doubleArrayOf(506752.0 / 1228815, 87881.0 / 245763, 12673.0 / 70218),
            doubleArrayOf(87098.0 / 409605, 175762.0 / 245763, 12673.0 / 175545),
            doubleArrayOf(7918.0 / 409605, 87881.0 / 737289, 1001167.0 / 1053270),
        )
    private val xyzToLinearP3 =
        arrayOf(
            doubleArrayOf(446124.0 / 178915, -333277.0 / 357830, -72051.0 / 178915),
            doubleArrayOf(-14852.0 / 17905, 63121.0 / 35810, 423.0 / 17905),
            doubleArrayOf(11844.0 / 330415, -50337.0 / 660830, 316169.0 / 330415),
        )
    private val linearP3ToXyz =
        arrayOf(
            doubleArrayOf(608311.0 / 1250200, 189793.0 / 714400, 198249.0 / 1000160),
            doubleArrayOf(35783.0 / 156275, 247089.0 / 357200, 198249.0 / 2500400),
            doubleArrayOf(0.0 / 1, 32229.0 / 714400, 5220557.0 / 5000800),
        )

    // Left-to-right accumulation, the same order as the reference implementation, so results agree
    // to the last bit rather than merely to six decimals.
    private fun multiply(
        matrix: Array<DoubleArray>,
        vector: List<Double>,
    ): List<Double> = matrix.map { row -> row.indices.fold(0.0) { sum, i -> sum + row[i] * vector[i] } }

    private fun xyz(lab: OkLab): List<Double> {
        val lms = multiply(okLabToLms, listOf(lab.l, lab.a, lab.b)).map { it * it * it }
        return multiply(lmsToXyz, lms)
    }

    private fun okLabFromXyz(
        xyz: List<Double>,
        alpha: Double,
    ): OkLab {
        val lms = multiply(xyzToLms, xyz).map { cbrt(it) }
        val lab = multiply(lmsToOkLab, lms)
        return OkLab(lab[0], lab[1], lab[2], alpha)
    }

    /** sRGB (gamma-encoded, 0…1) → OKLab. */
    fun okLabFromSrgb(
        rgb: List<Double>,
        alpha: Double = 1.0,
    ): OkLab = okLabFromXyz(multiply(linearSrgbToXyz, rgb.map(::decode)), alpha)

    /** OKLab → gamma-encoded sRGB, unclamped. */
    fun srgb(lab: OkLab): List<Double> = multiply(xyzToLinearSrgb, xyz(lab)).map(::encode)

    /** OKLab → gamma-encoded Display P3, unclamped. */
    fun displayP3(lab: OkLab): List<Double> = multiply(xyzToLinearP3, xyz(lab)).map(::encode)

    fun p3ToSrgb(p3: List<Double>): List<Double> = multiply(xyzToLinearSrgb, multiply(linearP3ToXyz, p3.map(::decode))).map(::encode)

    /** The sRGB transfer function (Display P3 uses the same curve), sign-preserving. */
    fun encode(linear: Double): Double {
        val magnitude = abs(linear)
        val encoded = if (magnitude > 0.0031308) 1.055 * magnitude.pow(1 / 2.4) - 0.055 else 12.92 * magnitude
        return if (linear < 0) -encoded else encoded
    }

    fun decode(encoded: Double): Double {
        val magnitude = abs(encoded)
        val linear = if (magnitude <= 0.04045) magnitude / 12.92 else ((magnitude + 0.055) / 1.055).pow(2.4)
        return if (encoded < 0) -linear else linear
    }

    // Gamut

    /**
     * Floating-point slack when testing gamut membership: oklch(1 0 0) converts to
     * 1.0000000000000004, which is white, not an out-of-gamut colour.
     */
    private const val GAMUT_TOLERANCE = 1e-6

    fun inGamut(rgb: List<Double>): Boolean = rgb.all { it >= -GAMUT_TOLERANCE && it <= 1 + GAMUT_TOLERANCE }

    private fun clamp(rgb: List<Double>): List<Double> = rgb.map { min(max(it, 0.0), 1.0) }

    /**
     * The colour to draw: sRGB when the colour is inside sRGB (the common case, directly comparable
     * with the web's hex values); Display P3 when it is outside sRGB but inside P3, which is what a
     * browser paints on a P3 display; and the CSS Color 4 gamut-mapped P3 colour when it is outside P3
     * too.
     */
    fun encode(lab: OkLab): EncodedColor {
        val s = srgb(lab)
        if (inGamut(s)) return rounded(EncodedColor(Space.SRGB, s[0], s[1], s[2], lab.alpha, gamutMapped = false))
        val p3 = displayP3(lab)
        if (inGamut(p3)) return rounded(EncodedColor(Space.DISPLAY_P3, p3[0], p3[1], p3[2], lab.alpha, gamutMapped = false))
        val mapped = gamutMapToP3(lab)
        return rounded(EncodedColor(Space.DISPLAY_P3, mapped[0], mapped[1], mapped[2], lab.alpha, gamutMapped = true))
    }

    /** CSS Color 4 §13.2.2: binary search on OKLCH chroma with a ΔE_OK JND of 0.02. */
    fun gamutMapToP3(lab: OkLab): List<Double> {
        if (lab.l >= 1) return listOf(1.0, 1.0, 1.0)
        if (lab.l <= 0) return listOf(0.0, 0.0, 0.0)
        val jnd = 0.02
        val epsilon = 0.0001
        val hue = atan2(lab.b, lab.a)

        fun atChroma(chroma: Double) = OkLab(lab.l, chroma * cos(hue), chroma * sin(hue), lab.alpha)

        fun deltaE(
            clipped: List<Double>,
            current: OkLab,
        ): Double {
            val back = okLabFromXyz(multiply(linearP3ToXyz, clipped.map(::decode)), current.alpha)
            return sqrt((back.l - current.l).pow(2) + (back.a - current.a).pow(2) + (back.b - current.b).pow(2))
        }
        var current = lab
        var clipped = clamp(displayP3(current))
        if (deltaE(clipped, current) < jnd) return clipped
        var low = 0.0
        var high = sqrt(lab.a * lab.a + lab.b * lab.b)
        var lowInGamut = true
        while (high - low > epsilon) {
            val chroma = (low + high) / 2
            current = atChroma(chroma)
            val candidate = displayP3(current)
            if (lowInGamut && inGamut(candidate)) {
                low = chroma
                continue
            }
            clipped = clamp(candidate)
            val error = deltaE(clipped, current)
            if (error < jnd) {
                if (jnd - error < epsilon) return clipped
                lowInGamut = false
                low = chroma
            } else {
                high = chroma
            }
        }
        return clipped
    }

    /** Six decimals, clamped into 0…1 once the gamut decision is made, so output is byte-stable. */
    private fun rounded(color: EncodedColor): EncodedColor {
        fun r(value: Double) = round6(min(max(value, 0.0), 1.0))
        return color.copy(red = r(color.red), green = r(color.green), blue = r(color.blue), alpha = r(color.alpha))
    }

    // color-mix()

    /**
     * `color-mix(in oklab, first p1, second p2)` per CSS Color 5 §2: percentages normalised to sum to
     * 100% (a smaller sum scales the result's alpha), then a premultiplied-alpha mix in OKLab.
     */
    fun mix(
        first: OkLab,
        p1: Double?,
        second: OkLab,
        p2: Double?,
    ): OkLab {
        var (weight1, weight2) =
            when {
                p1 == null && p2 == null -> 50.0 to 50.0
                p2 == null -> p1!! to 100 - p1
                p1 == null -> 100 - p2 to p2
                else -> p1 to p2
            }
        val total = weight1 + weight2
        if (weight1 < 0 || weight2 < 0 || total <= 0) {
            throw GeneratorError("color-mix() percentages must be non-negative and not both zero.")
        }
        val alphaMultiplier = if (total < 100) total / 100 else 1.0
        weight1 /= total
        weight2 /= total
        val alpha = first.alpha * weight1 + second.alpha * weight2
        if (alpha <= 0) return OkLab(0.0, 0.0, 0.0, 0.0)

        fun channel(
            x: Double,
            y: Double,
        ) = (x * first.alpha * weight1 + y * second.alpha * weight2) / alpha
        return OkLab(channel(first.l, second.l), channel(first.a, second.a), channel(first.b, second.b), alpha * alphaMultiplier)
    }
}

/** Parsing of the colour syntaxes the Qeetrix token build emits. */
object ColorValues {
    fun color(
        components: List<CssComponent>,
        context: String,
    ): ColorMath.OkLab {
        if (components.size != 1) throw GeneratorError("$context: expected one colour.")
        val component = components[0]
        return when {
            component is CssComponent.Ident && component.name.lowercase() == "transparent" -> {
                ColorMath.OkLab(0.0, 0.0, 0.0, 0.0)
            }

            component is CssComponent.Function && component.name == "oklch" -> {
                val (channels, alpha) = channelsAndAlpha(component.arguments, context)
                if (channels.size != 3) throw GeneratorError("$context: oklch() needs L C H.")
                val lightness = channel(channels[0], percentScale = 1.0, context = context)
                val chroma = channel(channels[1], percentScale = 0.4, context = context)
                val hue = channels[2]
                if (hue !is CssComponent.Number || (hue.unit != "" && hue.unit != "deg")) {
                    throw GeneratorError("$context: unsupported oklch() hue.")
                }
                ColorMath.oklch(lightness, chroma, hue.value, alpha)
            }

            component is CssComponent.Function && (component.name == "rgb" || component.name == "rgba") -> {
                val (channels, alpha) = channelsAndAlpha(component.arguments, context)
                if (channels.size != 3) throw GeneratorError("$context: rgb() needs three channels.")
                ColorMath.okLabFromSrgb(channels.map { channel(it, percentScale = 255.0, context = context) / 255 }, alpha)
            }

            component is CssComponent.Function && component.name == "color-mix" -> {
                colorMix(component.arguments, context)
            }

            else -> {
                throw GeneratorError("$context: unsupported colour syntax $component.")
            }
        }
    }

    /** `L C H [/ A]` or legacy `r, g, b[, a]`. */
    private fun channelsAndAlpha(
        arguments: List<CssComponent>,
        context: String,
    ): Pair<List<CssComponent>, Double> {
        if (CssComponent.Delimiter(',') in arguments) {
            val groups = splitOnCommas(arguments)
            val channels = groups.take(3).map { it.firstOrNull() ?: CssComponent.Ident("") }
            val alpha = if (groups.size == 4) channel(groups[3].firstOrNull() ?: CssComponent.Ident(""), 1.0, context) else 1.0
            return channels to alpha
        }
        val slash = arguments.indexOf(CssComponent.Delimiter('/'))
        if (slash >= 0) {
            val alphaPart = arguments.subList(slash + 1, arguments.size)
            if (alphaPart.size != 1) throw GeneratorError("$context: bad alpha.")
            return arguments.subList(0, slash) to channel(alphaPart[0], 1.0, context)
        }
        return arguments to 1.0
    }

    private fun channel(
        component: CssComponent,
        percentScale: Double,
        context: String,
    ): Double {
        if (component !is CssComponent.Number) throw GeneratorError("$context: unsupported colour channel $component.")
        return when (component.unit) {
            "" -> component.value
            "%" -> component.value / 100 * percentScale
            else -> throw GeneratorError("$context: unsupported colour channel unit \"${component.unit}\".")
        }
    }

    private fun colorMix(
        arguments: List<CssComponent>,
        context: String,
    ): ColorMath.OkLab {
        val groups = splitOnCommas(arguments)
        if (groups.size != 3 || groups[0] != listOf(CssComponent.Ident("in"), CssComponent.Ident("oklab"))) {
            throw GeneratorError("$context: only color-mix(in oklab, …, …) is supported.")
        }

        fun operand(group: List<CssComponent>): Pair<ColorMath.OkLab, Double?> {
            var percentage: Double? = null
            val colour = mutableListOf<CssComponent>()
            for (component in group) {
                if (component is CssComponent.Number && component.unit == "%") percentage = component.value else colour += component
            }
            return color(colour, context) to percentage
        }
        val (first, p1) = operand(groups[1])
        val (second, p2) = operand(groups[2])
        try {
            return ColorMath.mix(first, p1, second, p2)
        } catch (error: GeneratorError) {
            throw GeneratorError("$context: ${error.message}")
        }
    }
}
