package com.qeetgroup.qeetrix.components.typography

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixTextTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun headingsAreHeadingsAndCopyIsNot() {
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixText("Billing", variant = QeetrixTextVariant.H2)
                QeetrixText("Invoices are monthly.")
            }
        }
        compose.onNodeWithText("Billing").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        compose.onNodeWithText("Invoices are monthly.").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))
        assertTrue(QeetrixTextVariant.entries.filter { it.isHeading }.map { it.value } == listOf("h1", "h2", "h3", "h4"))
    }

    @Test
    fun headingsStepUpAtTheWebsSmBreakpoint() {
        // h1: text-2xl (24) → sm:text-title (32); h2: 20 → 24; h3: 18 → 20; h4 stays 16.
        assertEquals(24.sp, QeetrixTextStyles.style(QeetrixTextVariant.H1, wide = false).fontSize)
        assertEquals(32.sp, QeetrixTextStyles.style(QeetrixTextVariant.H1, wide = true).fontSize)
        assertEquals(20.sp, QeetrixTextStyles.style(QeetrixTextVariant.H2, wide = false).fontSize)
        assertEquals(24.sp, QeetrixTextStyles.style(QeetrixTextVariant.H2, wide = true).fontSize)
        assertEquals(18.sp, QeetrixTextStyles.style(QeetrixTextVariant.H3, wide = false).fontSize)
        assertEquals(20.sp, QeetrixTextStyles.style(QeetrixTextVariant.H3, wide = true).fontSize)
        assertEquals(QeetrixTextStyles.style(QeetrixTextVariant.H4, false), QeetrixTextStyles.style(QeetrixTextVariant.H4, true))
    }

    @Test
    fun copyStylesFollowTheWebClasses() {
        // p: text-body (14 / 1.5); lead: text-base/relaxed; large: text-lg/snug semibold; small: text-label medium.
        val p = QeetrixTextStyles.style(QeetrixTextVariant.P, false)
        assertEquals(14.sp, p.fontSize)
        assertEquals(1.5.em, p.lineHeight)
        val lead = QeetrixTextStyles.style(QeetrixTextVariant.Lead, false)
        assertEquals(16.sp, lead.fontSize)
        assertEquals(1.625.em, lead.lineHeight)
        assertEquals(600, QeetrixTextStyles.style(QeetrixTextVariant.Large, false).fontWeight!!.weight)
        assertEquals(13.sp, QeetrixTextStyles.style(QeetrixTextVariant.Small, false).fontSize)
        // inlineCode: 0.875em of the body size, in Fira Code.
        assertEquals((14 * 0.875).sp, QeetrixTextStyles.style(QeetrixTextVariant.InlineCode, false).fontSize)
        assertFalse(QeetrixTextVariant.InlineCode.isHeading)
    }
}
