package com.qeetgroup.qeetrix.internal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The menu item's and select option's row (menu.css, select.css 0.4.0, QTRX-177): one line centred in the 48 dp row; a
 * wrapped text grows it by the CSS's padding, and the side parts stay on the first line.
 */
@RunWith(RobolectricTestRunner::class)
class FirstLineRowTest {
    @get:Rule
    val compose = createComposeRule()

    @Composable
    private fun Row(
        textHeight: Dp,
        width: Dp? = 240.dp,
    ) {
        FirstLineRow(
            minHeight = 48.dp,
            padding = 6.dp, // (size.control.md 32 − a 20 dp line) ÷ 2
            line = 20.dp,
            gap = 8.dp,
            modifier = (if (width != null) Modifier.width(width) else Modifier).testTag("row"),
            leading = { Box(Modifier.size(16.dp).testTag("icon")) },
            trailing = listOf { Box(Modifier.size(16.dp).testTag("check")) },
        ) { Box(Modifier.size(100.dp, textHeight).testTag("text")) }
    }

    private fun bounds(tag: String) = compose.onNodeWithTag(tag).getUnclippedBoundsInRoot()

    @Test
    fun oneLineIsCentredInTheRow() {
        compose.setContent { Row(textHeight = 20.dp) }
        assertEquals(48.dp, bounds("row").bottom - bounds("row").top)
        assertEquals(14.dp, bounds("text").top)
        assertEquals(16.dp, bounds("icon").top) // centred on the 20 dp line: 14 + 2
        assertEquals(16.dp, bounds("check").top)
    }

    @Test
    fun aWrappedTextGrowsTheRowAndTheSidePartsStayOnItsFirstLine() {
        compose.setContent { Row(textHeight = 60.dp) } // three lines
        assertEquals(6.dp + 60.dp + 6.dp, bounds("row").bottom - bounds("row").top)
        assertEquals(6.dp, bounds("text").top)
        assertEquals(8.dp, bounds("icon").top) // on the first line, not the middle of the block
        assertEquals(8.dp, bounds("check").top)
        // The text takes the width the side parts leave: 240 − 16 − 16 − 2 gaps.
        assertEquals(240.dp - 32.dp - 16.dp, bounds("text").right - bounds("text").left)
    }

    /** A popup sizes its list to its rows' intrinsic width, measuring them unbounded. */
    @Test
    fun anUnboundedRowIsAsWideAsItsParts() {
        compose.setContent { Box(Modifier.width(IntrinsicSize.Max)) { Row(textHeight = 20.dp, width = null) } }
        assertEquals(16.dp + 8.dp + 100.dp + 8.dp + 16.dp, bounds("row").right - bounds("row").left)
    }
}
