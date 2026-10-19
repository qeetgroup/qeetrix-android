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
    fun consumableTokensAreDecidedByRule() {
        assertEquals(ScopeDecision.Include(TokenKind.COLOR), Scope.decide(listOf("color", "text", "primary"), consumable = true))
        assertEquals(ScopeDecision.Include(TokenKind.EM), Scope.decide(listOf("typography", "body", "letter-spacing"), consumable = true))
        assertTrue(Scope.decide(listOf("corner", "circle"), consumable = true) is ScopeDecision.Exclude)
        assertTrue(Scope.decide(listOf("component", "button", "height"), consumable = true) is ScopeDecision.Exclude)
        assertEquals(
            ScopeDecision.Include(TokenKind.COMPONENT),
            Scope.decide(listOf("component", "button", "height"), consumable = true, adoptedComponents = setOf("button")),
        )
        assertEquals(
            ScopeDecision.Exclude(Scope.UNADOPTED_COMPONENT_REASON),
            Scope.decide(listOf("component", "tour", "offset"), consumable = true, adoptedComponents = setOf("button")),
        )
        assertTrue(Scope.decide(listOf("z", "modal"), consumable = true) is ScopeDecision.Exclude)
    }

    @Test
    fun anUnknownConsumableGroupFailsGeneration() {
        val error = assertThrows(GeneratorError::class.java) { Scope.decide(listOf("blur", "md"), consumable = true) }
        assertTrue(error.message!!.contains("no scope rule"))
    }

    @Test
    fun primitivesAreExcludedExceptTheScalesCodeReads() {
        assertEquals(ScopeDecision.Exclude(Scope.PRIMITIVE_REASON), Scope.decide(listOf("color", "qeet", "500"), consumable = false))
        assertEquals(ScopeDecision.Include(TokenKind.DIMENSION), Scope.decide(listOf("space", "2xl"), consumable = false))
        assertEquals(ScopeDecision.Include(TokenKind.NUMBER), Scope.decide(listOf("font", "geometry", "ui"), consumable = false))
    }

    @Test
    fun namesFollowTheSharedRule() {
        assertEquals("textOnBrand", KotlinNames.camel(listOf("text", "on-brand"), "t"))
        assertEquals("xl2", KotlinNames.camel(listOf("2xl"), "t"))
        assertEquals("ramp2xs", KotlinNames.camel(listOf("ramp", "2xs"), "t"))
        assertEquals("Zero", KotlinNames.pascal(listOf("0"), "t"))
        assertEquals("DataCategorical1", KotlinNames.pascal(listOf("data", "categorical", "1"), "t"))
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
