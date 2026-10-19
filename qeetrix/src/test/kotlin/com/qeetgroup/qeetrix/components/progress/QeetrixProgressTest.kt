package com.qeetgroup.qeetrix.components.progress

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixProgressTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun determinateProgressReportsItsValueAndShowsItFormatted() {
        compose.setQeetrix { QeetrixProgress(0.42f, label = "Uploading") }
        compose
            .onNodeWithContentDescription("Uploading")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(0.42f, 0f..1f)))
        compose.onNodeWithText("42%", useUnmergedTree = true).assertExists()
    }

    @Test
    fun valuesOutsideTheRangeAreClamped() {
        compose.setQeetrix { QeetrixProgress(1.7f, contentDescription = "Sync") }
        compose
            .onNodeWithContentDescription("Sync")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(1f, 0f..1f)))
    }

    @Test
    fun notANumberIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix { QeetrixProgress(Float.NaN) }
            compose.waitForIdle()
        }
    }

    @Test
    fun unknownProgressIsIndeterminateAndShowsNoValue() {
        compose.mainClock.autoAdvance = false // the segment sweeps forever
        compose.setQeetrix { QeetrixProgress(null, label = "Preparing export") }
        compose.mainClock.advanceTimeBy(700)
        compose
            .onNodeWithContentDescription("Preparing export")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate))
        compose.onNodeWithText("%", substring = true, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun sizesAreTheWebs() {
        assertEquals(listOf("sm", "md", "lg"), QeetrixProgressSize.entries.map { it.value })
        assertEquals(listOf(4f, 8f, 12f), QeetrixProgressSize.entries.map { it.thickness.value })
    }
}
