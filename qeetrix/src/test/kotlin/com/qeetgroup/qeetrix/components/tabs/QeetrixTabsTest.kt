package com.qeetgroup.qeetrix.components.tabs

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Tabs: tab semantics in a group, the app's selection, both variants, overflow that keeps the selection in view. */
@RunWith(RobolectricTestRunner::class)
class QeetrixTabsTest {
    @get:Rule
    val compose = createComposeRule()

    private val tabs =
        listOf(
            QeetrixTab("overview", "Overview"),
            QeetrixTab("activity", "Activity", QeetrixIconName.Bell),
            QeetrixTab("billing", "Billing", enabled = false),
        )

    @Test
    fun bothVariantsAreTabsInOneGroupWithTheAppsSelection() {
        val chosen = mutableListOf<String>()
        var variant by mutableStateOf(QeetrixTabsVariant.Default)
        compose.setQeetrix { QeetrixTabs(tabs, selected = "overview", onSelect = { chosen += it }, variant = variant) }
        for (each in QeetrixTabsVariant.entries) {
            chosen.clear()
            compose.runOnIdle { variant = each }
            compose
                .onNodeWithText("Overview")
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
                .assertIsSelected()
                .onParent()
                .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
            compose.onNodeWithText("Activity").assertIsNotSelected().performClick()
            compose.onNodeWithText("Billing").assertIsNotEnabled().performClick()
            assertEquals("$each", listOf("activity"), chosen)
            // The selection stays the app's.
            compose.onNodeWithText("Overview").assertIsSelected()
        }
    }

    @Test
    fun eachTabHasA48DpTouchArea() {
        compose.setQeetrix { QeetrixTabs(tabs, selected = "overview", onSelect = {}) }
        val node = compose.onNodeWithText("Activity").fetchSemanticsNode()
        val height = with(compose.density) { node.touchBoundsInRoot.height.toDp() }
        assertTrue("touch height $height", height >= 48.dp)
    }

    @Test
    fun aNewlySelectedTabScrollsIntoView() {
        val many = (1..12).map { QeetrixTab(it, "Section $it") }
        var selected by mutableStateOf(1)
        compose.setQeetrix {
            QeetrixTabs(many, selected = selected, onSelect = {}, modifier = Modifier.width(240.dp), variant = QeetrixTabsVariant.Line)
        }
        compose.onNodeWithText("Section 12").assertIsNotDisplayed()
        compose.runOnIdle { selected = 12 }
        compose.onNodeWithText("Section 12").assertIsDisplayed().assertIsSelected()
    }
}
