package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.css.CssDeclarations
import com.qeetgroup.qeetrix.gen.css.CssParser
import com.qeetgroup.qeetrix.gen.css.CssVariableResolver
import com.qeetgroup.qeetrix.gen.tokens.CssValues
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CssTest {
    private fun dimension(css: String) = CssValues.dimension(CssParser.parse(css, css), css)

    @Test
    fun declarationsKeepTheirSelectorAndIgnoreComments() {
        val css =
            """
            /* header; with { braces } */
            :root { --a: 1px; --b: calc(var(--a) * 2); }
            .dark { --a: 2px; }
            @theme inline { --radius-lg: var(--radius); }
            """.trimIndent()
        val declarations = CssDeclarations.parse(css)
        assertEquals(listOf(":root", ":root", ".dark", "@theme inline"), declarations.map { it.selector })
        assertEquals("calc(var(--a) * 2)", declarations[1].value)
    }

    @Test
    fun aliasesResolveRecursivelyWithFallbacks() {
        val resolver = CssVariableResolver(mapOf("--radius" to "0.45rem", "--radius-xl" to "calc(var(--radius) * 1.4)"))
        assertEquals("calc(0.45rem * 1.4)", resolver.substitute("var(--radius-xl)", "t"))
        assertEquals("28px", resolver.substitute("var(--missing, 28px)", "t"))
        assertEquals(10.08, dimension(resolver.substitute("var(--radius-xl)", "t")), 1e-9)
    }

    @Test
    fun unresolvedAndCyclicAliasesFail() {
        val resolver = CssVariableResolver(mapOf("--a" to "var(--b)", "--b" to "var(--a)"))
        assertTrue(assertThrows(GeneratorError::class.java) { resolver.substitute("var(--a)", "t") }.message!!.contains("cycle"))
        assertTrue(
            assertThrows(GeneratorError::class.java) { CssVariableResolver(emptyMap()).substitute("var(--nope)", "t") }
                .message!!
                .contains("unresolved"),
        )
    }

    @Test
    fun lengthsConvertPxOneToOneAndRemAtSixteen() {
        assertEquals(14.0, dimension("14px"), 0.0)
        assertEquals(12.8, dimension("0.8rem"), 1e-12)
        assertEquals(0.0, dimension("0"), 0.0)
        assertEquals(30.0, dimension("calc(2rem - 2px)"), 0.0)
        assertEquals(4.0, dimension("min(4px, 1rem)"), 0.0)
        assertEquals(-1.0, dimension("-1px"), 0.0)
    }

    @Test
    fun unsupportedUnitsAndMixedArithmeticFail() {
        assertThrows(GeneratorError::class.java) { dimension("1vw") }
        assertThrows(GeneratorError::class.java) { dimension("50%") }
        assertThrows(GeneratorError::class.java) { dimension("calc(1px + 2)") }
        assertThrows(GeneratorError::class.java) { dimension("calc(1px * 2px)") }
    }

    @Test
    fun durationsEasingsAndShadows() {
        assertEquals(150.0, CssValues.durationMillis(CssParser.parse("150ms", "t"), "t"), 0.0)
        assertEquals(300.0, CssValues.durationMillis(CssParser.parse("0.3s", "t"), "t"), 1e-9)
        assertEquals(listOf(0.0, 0.0, 1.0, 1.0), CssValues.easing(CssParser.parse("linear", "t"), "t"))
        assertEquals(listOf(0.4, 0.0, 0.2, 1.0), CssValues.easing(CssParser.parse("cubic-bezier(0.4, 0, 0.2, 1)", "t"), "t"))
        val layers = CssValues.shadows(CssParser.parse("inset 0 1px 2px rgb(12 12 12 / 0.07), 0 0 0 1px rgb(12 12 12 / 0.03)", "t"), "t")
        assertEquals(2, layers.size)
        assertTrue(layers[0].inset)
        assertEquals(listOf(0.0, 1.0, 2.0, 0.0), listOf(layers[0].x, layers[0].y, layers[0].blur, layers[0].spread))
        assertEquals(1.0, layers[1].spread, 0.0)
        assertEquals("#0C0C0C12", layers[0].color.hex)
        assertEquals(emptyList<Any>(), CssValues.shadows(CssParser.parse("none", "t"), "t"))
    }

    @Test
    fun fontStacksYieldTheirFirstFamily() {
        assertEquals("Qeet Sans", CssValues.primaryFontFamily(CssParser.parse("\"Qeet Sans\", ui-sans-serif, system-ui", "t"), "t"))
    }
}
