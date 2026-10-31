package com.qeetgroup.qeetrix.components.tabs

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** Tabs: tab semantics in a group, the app's selection, both variants and orientations, counts, overflow, metrics. */
@RunWith(RobolectricTestRunner::class)
class QeetrixTabsTest {
    @get:Rule
    val compose = createComposeRule()

    private val tabs =
        listOf(
            QeetrixTab("profile", "Profile"),
            QeetrixTab("alerts", "Alerts", QeetrixIconName.Bell),
            QeetrixTab("billing", "Billing", enabled = false),
        )

    @Test
    fun everyLookIsTabsInOneGroupWithTheAppsSelection() {
        val chosen = mutableListOf<String>()
        var variant by mutableStateOf(QeetrixTabsVariant.Line)
        var orientation by mutableStateOf(QeetrixTabsOrientation.Horizontal)
        compose.setQeetrix {
            QeetrixTabs(tabs, value = "profile", onValueChange = { chosen += it }, variant = variant, orientation = orientation)
        }
        for (eachVariant in QeetrixTabsVariant.entries) {
            for (eachOrientation in QeetrixTabsOrientation.entries) {
                chosen.clear()
                compose.runOnIdle {
                    variant = eachVariant
                    orientation = eachOrientation
                }
                compose
                    .onNodeWithText("Profile")
                    .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
                    .assertIsSelected()
                    .onParent()
                    .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
                compose.onNodeWithText("Alerts").assertIsNotSelected().performClick()
                compose.onNodeWithText("Billing").assertIsNotEnabled().performClick()
                assertEquals("$eachVariant $eachOrientation", listOf("alerts"), chosen)
                // The selection stays the app's.
                compose.onNodeWithText("Profile").assertIsSelected()
            }
        }
    }

    @Test
    fun eachTabHasA48DpTouchArea() {
        compose.setQeetrix { QeetrixTabs(tabs, value = "profile", onValueChange = {}) }
        val node = compose.onNodeWithText("Alerts").fetchSemanticsNode()
        val height = with(compose.density) { node.touchBoundsInRoot.height.toDp() }
        assertTrue("touch height $height", height >= 48.dp)
    }

    /** tabs.css: block-size size.control.lg; the contained segments inside the strip's 1 dp edge. Real text layout. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun tabsAreTheFieldHeightOfTheDensity() {
        var density by mutableStateOf(QeetrixDensity.Default)
        var variant by mutableStateOf(QeetrixTabsVariant.Line)
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides density) {
                QeetrixTabs(tabs, value = "profile", onValueChange = {}, variant = variant)
            }
        }
        for (each in QeetrixDensity.entries) {
            compose.runOnIdle {
                density = each
                variant = QeetrixTabsVariant.Line
            }
            compose.onNodeWithText("Alerts").assertHeightIsEqualTo(each.sizeControlLg)
            compose.runOnIdle { variant = QeetrixTabsVariant.Contained }
            compose.onNodeWithText("Alerts").assertHeightIsEqualTo(each.sizeControlLg - 2.dp)
        }
    }

    @Test
    fun aCountIsFormattedForTheLocaleAndReadWithItsTab() {
        compose.setQeetrix { QeetrixTabs(listOf(QeetrixTab("alerts", "Alerts", badge = 1284)), value = "alerts", onValueChange = {}) }
        compose
            .onNode(hasText("Alerts") and hasText("1,284"))
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
    }

    @Test
    fun verticalTabsStackAndShareTheirWidth() {
        compose.setQeetrix { QeetrixTabs(tabs, value = "profile", onValueChange = {}, orientation = QeetrixTabsOrientation.Vertical) }
        val profile = compose.onNodeWithText("Profile").getUnclippedBoundsInRoot()
        val alerts = compose.onNodeWithText("Alerts").getUnclippedBoundsInRoot()
        assertEquals(profile.bottom, alerts.top)
        assertEquals(profile.right - profile.left, alerts.right - alerts.left)
    }

    @Test
    fun aNewlySelectedTabScrollsIntoView() {
        val many = (1..12).map { QeetrixTab(it, "Section $it") }
        var selected by mutableIntStateOf(1)
        compose.setQeetrix {
            QeetrixTabs(many, value = selected, onValueChange = {}, modifier = Modifier.width(240.dp))
        }
        compose.onNodeWithText("Section 12").assertIsNotDisplayed()
        compose.runOnIdle { selected = 12 }
        compose.onNodeWithText("Section 12").assertIsDisplayed().assertIsSelected()
    }

    @Test
    fun optionsCarryTheWebsNames() {
        assertEquals(listOf("line", "contained"), QeetrixTabsVariant.entries.map { it.value })
        assertEquals(listOf("horizontal", "vertical"), QeetrixTabsOrientation.entries.map { it.value })
    }
}
