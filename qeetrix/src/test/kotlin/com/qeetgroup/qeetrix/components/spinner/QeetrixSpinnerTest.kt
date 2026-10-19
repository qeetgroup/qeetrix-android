package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixSpinnerTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun itIsAnIndeterminateProgressIndicatorNamedLoading() {
        compose.mainClock.autoAdvance = false // it turns forever
        compose.setQeetrix { QeetrixSpinner() }
        compose.mainClock.advanceTimeBy(250)
        compose
            .onNodeWithContentDescription("Loading")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate))
            .assertHeightIsEqualTo(20.dp)
    }

    @Test
    fun aLabelReplacesTheBuiltInName() {
        compose.mainClock.autoAdvance = false
        compose.setQeetrix { QeetrixSpinner(label = "Fetching invoices", size = QeetrixSpinnerSize.Xl) }
        compose.mainClock.advanceTimeBy(100)
        compose.onNodeWithContentDescription("Fetching invoices").assertHeightIsEqualTo(32.dp)
    }

    @Test
    fun sizesAreTheWebs() {
        assertEquals(listOf(16f, 20f, 24f, 32f), QeetrixSpinnerSize.entries.map { it.side.value })
    }
}
