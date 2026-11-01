package com.qeetgroup.qeetrix.gen

import com.qeetgroup.qeetrix.gen.tokens.Scope
import com.qeetgroup.qeetrix.gen.tokens.ScopeDecision
import com.qeetgroup.qeetrix.gen.tokens.TokenKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ScopeAndNamesTest {
    @Test
    fun publicTokensAreDecidedByRule() {
        assertEquals(ScopeDecision.Include(TokenKind.COLOR), Scope.decide(listOf("color", "text", "default")))
        assertEquals(ScopeDecision.Include(TokenKind.TYPOGRAPHY), Scope.decide(listOf("typography", "body", "md")))
        assertEquals(ScopeDecision.Include(TokenKind.SHADOW), Scope.decide(listOf("shadow", "overlay")))
        assertEquals(ScopeDecision.Include(TokenKind.NUMBER), Scope.decide(listOf("motion", "spring", "snappy", "stiffness")))
        assertTrue(Scope.decide(listOf("font", "family", "indic")) is ScopeDecision.Exclude)
        assertTrue(Scope.decide(listOf("z", "modal")) is ScopeDecision.Exclude)
        assertEquals(ScopeDecision.Exclude(Scope.UNADOPTED_COMPONENT_REASON), Scope.decide(listOf("component", "tour", "offset")))
        assertEquals(
            ScopeDecision.Include(TokenKind.COMPONENT),
            Scope.decide(listOf("component", "sidebar", "width", "expanded"), adoptedComponents = setOf("sidebar")),
        )
    }

    @Test
    fun anUnknownGroupFailsGeneration() {
        val error = assertThrows(GeneratorError::class.java) { Scope.decide(listOf("blur", "md")) }
        assertTrue(error.message!!.contains("no scope rule"))
    }

    @Test
    fun primitivesAreNeverGenerated() {
        assertEquals(ScopeDecision.Exclude(Scope.PRIMITIVE_REASON), Scope.decide(listOf("primitive", "color", "qeet", "500")))
        assertEquals(ScopeDecision.Exclude(Scope.PRIMITIVE_REASON), Scope.decide(listOf("primitive", "space", "400")))
    }

    @Test
    fun namesFollowTheSharedRule() {
        assertEquals("textOnBrand", KotlinNames.camel(listOf("text", "on-brand"), "t"))
        assertEquals("xl2", KotlinNames.camel(listOf("2xl"), "t"))
        assertEquals("ramp2xs", KotlinNames.camel(listOf("ramp", "2xs"), "t"))
        assertEquals("bgBrandBoldHover", KotlinNames.camel(listOf("bg", "brand", "bold-hover"), "t"))
        assertEquals("sizeControlMd", KotlinNames.camel(listOf("size", "control", "md"), "t"))
        assertEquals("Zero", KotlinNames.pascal(listOf("0"), "t"))
        assertEquals("DatavizCategorical1", KotlinNames.pascal(listOf("dataviz", "categorical", "1"), "t"))
        assertThrows(GeneratorError::class.java) { KotlinNames.camel(listOf("42"), "t") }
        assertThrows(GeneratorError::class.java) { KotlinNames.requireUnique(listOf("a" to "x.a", "a" to "y.a"), "t") }
    }

    @Test
    fun numbersAreWrittenDeterministically() {
        assertEquals("0", numberText(-0.0000001))
        assertEquals("7.2", numberText(7.2000000001))
        assertEquals("10.08", numberText(10.08))
        assertEquals("2", numberText(2.0))
        assertEquals("0.5f", floatLiteral(0.5))
    }
}
