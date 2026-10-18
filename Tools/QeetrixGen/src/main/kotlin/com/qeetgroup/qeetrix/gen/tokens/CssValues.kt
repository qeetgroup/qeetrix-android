package com.qeetgroup.qeetrix.gen.tokens

import com.qeetgroup.qeetrix.gen.GeneratorError
import com.qeetgroup.qeetrix.gen.color.ColorMath
import com.qeetgroup.qeetrix.gen.color.ColorValues
import com.qeetgroup.qeetrix.gen.css.CssComponent
import com.qeetgroup.qeetrix.gen.css.splitOnCommas

/**
 * The CSS root font size `rem` is measured against. qeetrix-ui never sets `html { font-size }`, so
 * browsers use their default, 16px. qeetrix-ios makes the same assumption.
 */
const val REM_IN_DP: Double = 16.0

/**
 * CSS value → typed value. One CSS px is one Android dp (and one iOS pt): all three are the
 * reference pixel at 1× — 1/96 inch for CSS, 1/160 inch for dp, but each is defined against its own
 * viewing distance and the industry maps them one-to-one.
 */
object CssValues {
    /** A length in dp: `14px`, `0.8rem`, `0`, `calc(0.45rem * 1.4)`, `min(a, b)`. */
    fun dimension(
        components: List<CssComponent>,
        context: String,
    ): Double {
        if (components.size != 1) throw GeneratorError("$context: expected one length, found ${components.size} components.")
        val quantity = evaluate(components[0], context)
        if (!quantity.isLength && quantity.value != 0.0) throw GeneratorError("$context: expected a length, found a unitless number.")
        return quantity.value
    }

    /** An `em` multiple (letter-spacing). `0` is accepted unitless, as CSS does. */
    fun em(
        components: List<CssComponent>,
        context: String,
    ): Double {
        val component = components.singleOrNull() as? CssComponent.Number
        if (component == null || !(component.unit == "em" || (component.unit.isEmpty() && component.value == 0.0))) {
            throw GeneratorError("$context: expected an em value.")
        }
        return component.value
    }

    /** A plain number: a font weight, a line-height multiple, an opacity, an axis position. */
    fun number(
        components: List<CssComponent>,
        context: String,
    ): Double {
        val component = components.singleOrNull() as? CssComponent.Number
        if (component == null || component.unit.isNotEmpty()) throw GeneratorError("$context: expected a unitless number.")
        return component.value
    }

    /** A duration in milliseconds. */
    fun durationMillis(
        components: List<CssComponent>,
        context: String,
    ): Double {
        val component = components.singleOrNull() as? CssComponent.Number ?: throw GeneratorError("$context: expected a duration.")
        return when (component.unit) {
            "ms" -> component.value
            "s" -> component.value * 1000
            else -> throw GeneratorError("$context: unsupported duration unit \"${component.unit}\".")
        }
    }

    /** A cubic Bézier easing. `linear` is `cubic-bezier(0, 0, 1, 1)`, which is what CSS defines it as. */
    fun easing(
        components: List<CssComponent>,
        context: String,
    ): List<Double> {
        val component = components.singleOrNull() ?: throw GeneratorError("$context: expected one easing.")
        return when {
            component == CssComponent.Ident("linear") -> {
                listOf(0.0, 0.0, 1.0, 1.0)
            }

            component is CssComponent.Function && component.name == "cubic-bezier" -> {
                val points = splitOnCommas(component.arguments).map { number(it, context) }
                if (points.size != 4 || points[0] !in 0.0..1.0 || points[2] !in 0.0..1.0) {
                    throw GeneratorError("$context: cubic-bezier() needs four numbers with x in 0…1.")
                }
                points
            }

            else -> {
                throw GeneratorError("$context: unsupported easing $component.")
            }
        }
    }

    /** The first family in a font stack: `"Qeet Sans", ui-sans-serif, …` → `Qeet Sans`. */
    fun primaryFontFamily(
        components: List<CssComponent>,
        context: String,
    ): String {
        val first = splitOnCommas(components).firstOrNull()
        if (first.isNullOrEmpty()) throw GeneratorError("$context: empty font stack.")
        (first.singleOrNull() as? CssComponent.Text)?.let { return it.value }
        return first.joinToString(" ") {
            (it as? CssComponent.Ident)?.name
                ?: throw GeneratorError("$context: unsupported font family syntax.")
        }
    }

    // Shadows

    data class ShadowLayer(
        val inset: Boolean,
        val x: Double,
        val y: Double,
        val blur: Double,
        val spread: Double,
        val color: ColorMath.EncodedColor,
    )

    /** A `box-shadow` list. `none` is the empty list. */
    fun shadows(
        components: List<CssComponent>,
        context: String,
    ): List<ShadowLayer> {
        if (components == listOf(CssComponent.Ident("none"))) return emptyList()
        return splitOnCommas(components).map { layer ->
            var inset = false
            val lengths = mutableListOf<Double>()
            var color: ColorMath.EncodedColor? = null
            for (component in layer) {
                when {
                    component == CssComponent.Ident("inset") -> {
                        inset = true
                    }

                    component is CssComponent.Number -> {
                        lengths += dimension(listOf(component), context)
                    }

                    else -> {
                        if (color != null) throw GeneratorError("$context: a shadow layer has two colours.")
                        color = ColorMath.encode(ColorValues.color(listOf(component), context))
                    }
                }
            }
            val resolvedColor = color
            if (lengths.size !in 2..4 ||
                resolvedColor == null
            ) {
                throw GeneratorError("$context: a shadow layer needs 2–4 lengths and a colour.")
            }
            ShadowLayer(
                inset = inset,
                x = lengths[0],
                y = lengths[1],
                blur = lengths.getOrElse(2) { 0.0 },
                spread = lengths.getOrElse(3) { 0.0 },
                color = resolvedColor,
            )
        }
    }

    // calc()

    private data class Quantity(
        val value: Double,
        val isLength: Boolean,
    )

    private fun evaluate(
        component: CssComponent,
        context: String,
    ): Quantity =
        when {
            component is CssComponent.Number -> {
                when (component.unit) {
                    "" -> Quantity(component.value, isLength = false)
                    "px" -> Quantity(component.value, isLength = true)
                    "rem" -> Quantity(component.value * REM_IN_DP, isLength = true)
                    else -> throw GeneratorError("$context: unsupported length unit \"${component.unit}\".")
                }
            }

            component is CssComponent.Function && component.name == "calc" -> {
                sum(component.arguments, context)
            }

            component is CssComponent.Function && (component.name == "min" || component.name == "max") -> {
                val values = splitOnCommas(component.arguments).map { sum(it, context) }
                val first = values.firstOrNull()
                if (first == null || values.any { it.isLength != first.isLength }) {
                    throw GeneratorError("$context: min()/max() mixes lengths and numbers.")
                }
                val picked = if (component.name == "min") values.minOf { it.value } else values.maxOf { it.value }
                Quantity(picked, first.isLength)
            }

            else -> {
                throw GeneratorError("$context: unsupported value $component.")
            }
        }

    private fun sum(
        components: List<CssComponent>,
        context: String,
    ): Quantity {
        val terms = mutableListOf(mutableListOf<CssComponent>())
        val signs = mutableListOf(1.0)
        for (component in components) {
            if (component == CssComponent.Delimiter('+') || component == CssComponent.Delimiter('-')) {
                terms += mutableListOf<CssComponent>()
                signs += if (component == CssComponent.Delimiter('+')) 1.0 else -1.0
            } else {
                terms.last() += component
            }
        }
        var total: Quantity? = null
        for ((term, sign) in terms.zip(signs)) {
            val quantity = product(term, context)
            val running = total
            total =
                if (running != null) {
                    if (running.isLength != quantity.isLength) throw GeneratorError("$context: calc() adds a length to a number.")
                    Quantity(running.value + sign * quantity.value, running.isLength)
                } else {
                    Quantity(sign * quantity.value, quantity.isLength)
                }
        }
        return total ?: throw GeneratorError("$context: empty calc().")
    }

    private fun product(
        components: List<CssComponent>,
        context: String,
    ): Quantity {
        var result: Quantity? = null
        var pendingDivide = false
        for (component in components) {
            if (component == CssComponent.Delimiter('*')) continue
            if (component == CssComponent.Delimiter('/')) {
                pendingDivide = true
                continue
            }
            val quantity = evaluate(component, context)
            val running = result
            if (running == null) {
                result = quantity
                continue
            }
            result =
                if (pendingDivide) {
                    if (quantity.isLength || quantity.value == 0.0) throw GeneratorError("$context: calc() divides by a length or by zero.")
                    pendingDivide = false
                    Quantity(running.value / quantity.value, running.isLength)
                } else {
                    if (running.isLength && quantity.isLength) throw GeneratorError("$context: calc() multiplies two lengths.")
                    Quantity(running.value * quantity.value, running.isLength || quantity.isLength)
                }
        }
        return result ?: throw GeneratorError("$context: empty calc() term.")
    }
}
