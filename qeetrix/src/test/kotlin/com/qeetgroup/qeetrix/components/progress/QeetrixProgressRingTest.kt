package com.qeetgroup.qeetrix.components.progress

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The ring: the icon sizes, read as a progress bar with the app's words, decorative without a name. */
@RunWith(RobolectricTestRunner::class)
class QeetrixProgressRingTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aNamedRingIsReadAsAProgressBarWithItsWords() {
        compose.setQeetrix { QeetrixProgressRing(1.4f, contentDescription = "Time left", stateDescription = "12 seconds left") }
        compose
            .onNodeWithContentDescription("Time left")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo(1f, 0f..1f)))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "12 seconds left"))
    }

    @Test
    fun withoutANameItIsDecorative() {
        compose.setQeetrix { QeetrixProgressRing(0.5f, Modifier.testTag("ring")) }
        compose.onNodeWithTag("ring").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.ProgressBarRangeInfo))
    }

    @Test
    fun sizesAreTheIconSizes() {
        assertEquals(listOf(16.dp, 20.dp, 24.dp), QeetrixProgressRingSize.entries.map { it.diameter })
        assertEquals(listOf("sm", "md", "lg"), QeetrixProgressRingSize.entries.map { it.value })
        compose.setQeetrix { QeetrixProgressRing(0.5f, Modifier.testTag("ring"), size = QeetrixProgressRingSize.Lg) }
        compose.onNodeWithTag("ring").assertWidthIsEqualTo(24.dp).assertHeightIsEqualTo(24.dp)
    }

    @Test
    fun aValueThatIsNotANumberIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { compose.setQeetrix { QeetrixProgressRing(Float.NaN) } }
    }
}
