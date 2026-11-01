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
import java.text.NumberFormat
import java.util.Locale

/** Progress and Meter: their values and ranges, what is shown and read out, and the web's names. */
@RunWith(RobolectricTestRunner::class)
class QeetrixProgressTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun progressReportsItsValueInItsRangeAndShowsItsPercentage() {
        compose.setQeetrix { QeetrixProgress(42f, label = "Uploading", showValue = true) }
        compose
            .onNodeWithContentDescription("Uploading")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(42f, 0f..100f)))
        compose.onNodeWithText("42%", useUnmergedTree = true).assertExists()
    }

    @Test
    fun theValueIsHiddenUnlessShowValue() {
        compose.setQeetrix { QeetrixProgress(42f, label = "Uploading") }
        compose.onNodeWithText("42%", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun aFormatWritesTheValueItselfAndIsReadOut() {
        val gigabytes = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 1 }
        compose.setQeetrix { QeetrixProgress(2.4f, max = 8f, label = "Downloading", showValue = true, format = gigabytes) }
        compose
            .onNodeWithContentDescription("Downloading")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "2.4"))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(2.4f, 0f..8f)))
    }

    @Test
    fun valuesOutsideTheRangeAreClamped() {
        compose.setQeetrix { QeetrixProgress(170f, contentDescription = "Sync") }
        compose
            .onNodeWithContentDescription("Sync")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(100f, 0f..100f)))
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
        compose.mainClock.autoAdvance = false // the segment slides forever
        compose.setQeetrix { QeetrixProgress(null, label = "Preparing export", showValue = true) }
        compose.mainClock.advanceTimeBy(700)
        compose
            .onNodeWithContentDescription("Preparing export")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate))
        compose.onNodeWithText("%", substring = true, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun aMeterIsReadAsItsLabelAndValueWithItsTarget() {
        compose.setQeetrix { QeetrixMeter(40f, label = "People with two-step", showValue = true, target = 80f) }
        compose
            .onNodeWithContentDescription("People with two-step")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "40%, target 80%"))
            // Not a task: no progress bar for accessibility services.
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ProgressBarRangeInfo))
        compose.onNodeWithText("40%", useUnmergedTree = true).assertExists()
    }

    @Test
    fun tonesAreTheWebs() {
        assertEquals(listOf("brand", "success", "danger"), QeetrixProgressTone.entries.map { it.value })
        assertEquals(listOf("brand", "neutral", "info", "success", "warning", "danger"), QeetrixMeterTone.entries.map { it.value })
    }
}
