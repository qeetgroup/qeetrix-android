package com.qeetgroup.qeetrix.components.descriptionlist

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Description lists: pairs read as one element, the three layouts, the web's breakpoint, density spacing. */
@RunWith(RobolectricTestRunner::class)
class QeetrixDescriptionListTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun eachPairIsReadTogetherAndAControlInAValueStaysItsOwn() {
        val clicks = mutableListOf<Unit>()
        compose.setQeetrix {
            QeetrixDescriptionList {
                QeetrixDescriptionItem("Plan", "Team")
                QeetrixDescriptionItem("Status") { QeetrixBadge("Active") }
                QeetrixDescriptionItem("Invoice") { QeetrixButton("Download", onClick = { clicks += Unit }) }
            }
        }
        val plan = compose.onNode(hasText("Plan")).fetchSemanticsNode()
        assertEquals(listOf("Plan", "Team"), plan.config.getOrNull(SemanticsProperties.Text)?.map { it.text })
        val status = compose.onNode(hasText("Status")).fetchSemanticsNode()
        assertEquals(listOf("Status", "Active"), status.config.getOrNull(SemanticsProperties.Text)?.map { it.text })
        compose.onNode(hasText("Download") and hasClickAction()).performClick()
        assertEquals(1, clicks.size)
    }

    @Test
    @Config(qualifiers = "w411dp-h900dp")
    fun belowTheSmBreakpointAHorizontalListStacksEachTermAboveItsValue() {
        compose.setQeetrix { QeetrixDescriptionList { QeetrixDescriptionItem("Region", "Mumbai") } }
        val term = compose.onNodeWithText("Region", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val value = compose.onNodeWithText("Mumbai", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("stacked", value.top >= term.bottom)
        assertEquals(term.left, value.left)
    }

    @Test
    @Config(qualifiers = "w700dp-h900dp")
    fun fromTheSmBreakpointTermsSitInA192DpColumnBesideValues() {
        var layout by mutableStateOf(QeetrixDescriptionListLayout.Horizontal)
        compose.setQeetrix { QeetrixDescriptionList(layout = layout) { QeetrixDescriptionItem("Region", "Mumbai") } }
        val term = compose.onNodeWithText("Region", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val value = compose.onNodeWithText("Mumbai", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(term.top, value.top)
        assertEquals(192.dp + 24.dp, value.left - term.left)
        // Vertical stacks at every width.
        compose.runOnIdle { layout = QeetrixDescriptionListLayout.Vertical }
        val stackedValue = compose.onNodeWithText("Mumbai", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(term.left, stackedValue.left)
    }

    @Test
    @Config(qualifiers = "w500dp-h900dp")
    fun aGridFillsTheWidthWithCellsOfAtLeast192Dp() {
        var width by mutableStateOf(420.dp)
        compose.setQeetrix {
            QeetrixDescriptionList(Modifier.width(width), layout = QeetrixDescriptionListLayout.Grid) {
                QeetrixDescriptionItem("Plan", "Team")
                QeetrixDescriptionItem("Region", "Mumbai")
                QeetrixDescriptionItem("Owner", "Ada")
            }
        }
        // 420 dp: two columns of 198 dp, 24 dp apart.
        val plan = compose.onNodeWithText("Plan", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val region = compose.onNodeWithText("Region", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val owner = compose.onNodeWithText("Owner", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(plan.top, region.top)
        assertEquals(198.dp + 24.dp, region.left - plan.left)
        assertTrue("third cell on the next row", owner.top > plan.bottom)
        assertEquals(plan.left, owner.left)
        // 400 dp: one column.
        compose.runOnIdle { width = 400.dp }
        val stacked = compose.onNodeWithText("Region", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(plan.left, stacked.left)
    }

    @Test
    fun pairsAreTheDensitysCellPaddingApart() {
        var density by mutableStateOf(QeetrixDensity.Default)
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides density) {
                QeetrixDescriptionList(layout = QeetrixDescriptionListLayout.Vertical) {
                    QeetrixDescriptionItem("Plan", "Team")
                    QeetrixDescriptionItem("Region", "Mumbai")
                }
            }
        }
        for (each in QeetrixDensity.entries) {
            compose.runOnIdle { density = each }
            val first = compose.onNodeWithText("Team", useUnmergedTree = true).getUnclippedBoundsInRoot()
            val second = compose.onNodeWithText("Region", useUnmergedTree = true).getUnclippedBoundsInRoot()
            assertEquals("$each", each.cellPaddingY, second.top - first.bottom)
        }
    }
}
