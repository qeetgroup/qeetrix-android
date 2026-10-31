package com.qeetgroup.qeetrix.components.typography

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.sp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
                QeetrixHeading("Billing", level = 2)
                QeetrixText("Invoices are monthly.")
            }
        }
        compose.onNodeWithText("Billing").assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        compose.onNodeWithText("Invoices are monthly.").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))
    }

    @Test
    fun aHeadingsLevelChoosesItsDefaultLook() {
        // heading.tsx: 1 heading-lg, 2 heading-md, 3 heading-sm, 4 title-lg, 5 title-md, 6 title-sm.
        assertEquals(
            listOf("heading-lg", "heading-md", "heading-sm", "title-lg", "title-md", "title-sm"),
            (1..6).map { headingSizeFor(it).value },
        )
        assertEquals(32.sp, typography()[QeetrixHeadingSize.HeadingLg.role].fontSize)
    }

    @Test
    fun textSizesAreTheBodyRolesAndWeightsTheRolesOwn() {
        assertEquals(listOf("sm", "md", "lg"), QeetrixTextSize.entries.map { it.value })
        assertEquals(listOf(12.sp, 14.sp, 16.sp), QeetrixTextSize.entries.map { typography()[it.role].fontSize })
        assertEquals(listOf("regular", "medium", "semibold"), QeetrixTextWeight.entries.map { it.value })
        assertEquals(
            listOf("default", "subtle", "subtlest", "disabled", "inverse", "brand", "danger", "success", "warning", "info"),
            QeetrixTextTone.entries.map { it.value },
        )
        assertFalse(QeetrixHeadingSize.entries.isEmpty())
    }

    private fun typography() = com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography.Default
}
