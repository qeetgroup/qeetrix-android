package com.qeetgroup.qeetrix.components.pageindicator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Page indicator: "Page 2 of 4", adjustable by accessibility services. */
@RunWith(RobolectricTestRunner::class)
class QeetrixPageIndicatorTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun itIsReadAsAPageAndAdjustable() {
        var page = 1
        compose.setQeetrix { QeetrixPageIndicator(page, 4, onPageChange = { page = it }) }
        compose.onNodeWithContentDescription("Page 2 of 4").performSemanticsAction(SemanticsActions.SetProgress) { it(2f) }
        assertEquals(2, page)
    }

    /**
     * Found on a device: the control was only as wide as its dots, so going back from the first page's bar or on from
     * the last left a target a few dp wide. Taps in the room either side of the dots turn the page.
     */
    @Test
    fun aTapEitherSideOfTheDotsTurnsThePage() {
        var page by mutableIntStateOf(0)
        compose.setQeetrix { QeetrixPageIndicator(page, 4, onPageChange = { page = it }) }
        val indicator = compose.onNodeWithContentDescription("Page 1 of 4", substring = true)
        // 16 dp past the last dot: on a page.
        indicator.performTouchInput { click(Offset(right - 8.dp.toPx(), centerY)) }
        assertEquals(1, page)
        // 16 dp before the first dot: back a page.
        compose.onNodeWithContentDescription("Page 2 of 4").performTouchInput { click(Offset(left + 8.dp.toPx(), centerY)) }
        assertEquals(0, page)
        // At least 24 dp of room either side of the dots.
        val bounds = compose.onNodeWithContentDescription("Page 1 of 4").getBoundsInRoot()
        assertTrue("the control is ${bounds.right - bounds.left} wide", bounds.right - bounds.left >= 58.dp + 48.dp - 1.dp)
    }

    /** With two pages the dots are narrower than the 48 dp control and centred in it: a tap is judged from where they are. */
    @Test
    fun withFewPagesATapIsJudgedFromTheCentredDots() {
        var page by mutableIntStateOf(1)
        compose.setQeetrix { QeetrixPageIndicator(page, 2, onPageChange = { page = it }) }
        compose.onNodeWithContentDescription("Page 2 of 2").performTouchInput { click(Offset(left + 4.dp.toPx(), centerY)) }
        assertEquals(0, page)
    }
}
