package com.qeetgroup.qeetrix.components.descriptionlist

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Description lists: pairs read together, the empty value, the rows layout and its container query, columns. */
@RunWith(RobolectricTestRunner::class)
class QeetrixDescriptionListTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun eachPairIsReadTogetherAndAControlInAValueStaysItsOwn() {
        var clicks = 0
        compose.setQeetrix {
            QeetrixDescriptionList {
                QeetrixDescriptionItem("Plan", "Team")
                QeetrixDescriptionItem("Status") { QeetrixBadge("Active") }
                QeetrixDescriptionItem("Display name", "Acme", action = { QeetrixButton("Change", onClick = { clicks++ }) })
            }
        }

        fun texts(term: String) =
            compose
                .onNode(hasText(term))
                .fetchSemanticsNode()
                .config
                .getOrNull(SemanticsProperties.Text)
                ?.map { it.text }
        assertEquals(listOf("Plan", "Team"), texts("Plan"))
        assertEquals(listOf("Status", "Active"), texts("Status"))
        assertEquals(listOf("Display name", "Acme"), texts("Display name"))
        compose.onNode(hasText("Change") and hasClickAction()).performClick()
        assertEquals(1, clicks)
    }

    /** description-list.tsx: an empty value shows "—", hidden, and the visually hidden "Not set". */
    @Test
    fun anEmptyValueShowsADashReadAsNotSet() {
        compose.setQeetrix {
            QeetrixDescriptionList {
                QeetrixDescriptionItem("Phone", null)
                QeetrixDescriptionItem("Fax", "", emptyLabel = "Nicht gesetzt")
            }
        }
        val phone = compose.onNode(hasText("Phone")).fetchSemanticsNode()
        assertEquals(listOf("Phone", "Not set"), phone.config.getOrNull(SemanticsProperties.Text)?.map { it.text })
        val fax = compose.onNode(hasText("Fax")).fetchSemanticsNode()
        assertEquals(listOf("Fax", "Nicht gesetzt"), fax.config.getOrNull(SemanticsProperties.Text)?.map { it.text })
    }

    @Test
    fun belowTwentyFourRemATermSitsAboveItsValue() {
        compose.setQeetrix { QeetrixDescriptionList(Modifier.width(380.dp)) { QeetrixDescriptionItem("Region", "Mumbai") } }
        val term = compose.onNodeWithText("Region", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val value = compose.onNodeWithText("Mumbai", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("stacked", value.top >= term.bottom)
        assertEquals(term.left, value.left)
    }

    /** `grid-template-columns: minmax(8rem, 30%) minmax(0, 1fr)`, `column-gap: space.gap.md`, baseline-aligned. */
    @Test
    @Config(qualifiers = "w720dp-h900dp")
    fun fromTwentyFourRemTheTermColumnIsThirtyPercentAndAtLeastEightRem() {
        var width by mutableStateOf(600.dp)
        compose.setQeetrix { QeetrixDescriptionList(Modifier.width(width)) { QeetrixDescriptionItem("Region", "Mumbai") } }

        fun offset(): androidx.compose.ui.unit.Dp {
            val term = compose.onNodeWithText("Region", useUnmergedTree = true).getUnclippedBoundsInRoot()
            val value = compose.onNodeWithText("Mumbai", useUnmergedTree = true).getUnclippedBoundsInRoot()
            assertTrue("side by side", value.top < term.bottom)
            return value.left - term.left
        }
        assertEquals(180.dp + 12.dp, offset()) // 30% of 600
        compose.runOnIdle { width = 400.dp }
        assertEquals(128.dp + 12.dp, offset()) // 8rem, more than 30% of 400
    }

    @Test
    fun rowsAreAtLeastTheDensitysRowHeight() {
        var density by mutableStateOf(QeetrixDensity.Default)
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides density) {
                QeetrixDescriptionList(Modifier.width(600.dp)) { QeetrixDescriptionItem("Plan", "Team", Modifier.testTag("row")) }
            }
        }
        for (each in QeetrixDensity.entries) {
            compose.runOnIdle { density = each }
            compose.onNodeWithTag("row").assertHeightIsAtLeast(each.sizeRow)
        }
    }

    @Test
    @Config(qualifiers = "w1100dp-h900dp")
    fun columnsFitAsManyAsThereIsRoomForUpToColumns() {
        var width by mutableStateOf(420.dp)
        compose.setQeetrix {
            QeetrixDescriptionList(Modifier.width(width), layout = QeetrixDescriptionListLayout.Columns) {
                QeetrixDescriptionItem("Plan", "Team")
                QeetrixDescriptionItem("Region", "Mumbai")
                QeetrixDescriptionItem("Owner", "Ada")
            }
        }

        fun left(text: String) = compose.onNodeWithText(text, useUnmergedTree = true).getUnclippedBoundsInRoot().left
        // 420 dp, two columns: (420 − 24) / 2 = 198 dp each, space.gap.xl apart; the third on the next row.
        assertEquals(198.dp + 24.dp, left("Region") - left("Plan"))
        assertEquals(left("Plan"), left("Owner"))
        // 240 dp: room for one column of at least 8rem.
        compose.runOnIdle { width = 240.dp }
        assertEquals(left("Plan"), left("Region"))
        // 1000 dp: still two, the most it asks for.
        compose.runOnIdle { width = 1000.dp }
        assertEquals(488.dp + 24.dp, left("Region") - left("Plan"))
        assertEquals(left("Plan"), left("Owner"))
    }

    @Test
    fun optionsCarryTheWebsNamesAndColumnsAreOneToFour() {
        assertEquals(listOf("rows", "columns"), QeetrixDescriptionListLayout.entries.map { it.value })
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix { QeetrixDescriptionList(columns = 5) {} }
        }
    }
}
