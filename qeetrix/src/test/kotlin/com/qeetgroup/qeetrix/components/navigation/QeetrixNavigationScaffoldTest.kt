package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** The adaptive scaffold: a bar on a compact window, a rail from 600 dp, content state kept across the switch. */
@RunWith(RobolectricTestRunner::class)
class QeetrixNavigationScaffoldTest {
    @get:Rule
    val compose = createComposeRule()

    private val items =
        listOf(
            QeetrixNavigationItem("home", "Home", QeetrixIconName.House),
            QeetrixNavigationItem("inbox", "Inbox", QeetrixIconName.Inbox),
        )

    @Test
    fun theLayoutFollowsTheWindowSizeClassBoundary() {
        assertEquals(QeetrixNavigationLayout.Bar, QeetrixNavigationLayout.forWindowWidth(599.dp))
        assertEquals(QeetrixNavigationLayout.Rail, QeetrixNavigationLayout.forWindowWidth(600.dp))
        assertEquals(QeetrixNavigationLayout.Rail, QeetrixNavigationLayout.forWindowWidth(1280.dp))
    }

    @Test
    @Config(qualifiers = "w411dp-h800dp")
    fun aCompactWindowHasTheBarBelowTheContent() {
        compose.setQeetrix {
            QeetrixNavigationScaffold(items, selected = "home", onSelect = {}, modifier = Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize()) { QeetrixButton("Content", onClick = {}) }
            }
        }
        val content = compose.onNodeWithText("Content").getUnclippedBoundsInRoot()
        val home = compose.onNodeWithText("Home").assertIsSelected().getUnclippedBoundsInRoot()
        assertTrue("bar below: ${home.top} > ${content.bottom}", home.top > content.bottom)
    }

    @Test
    @Config(qualifiers = "w840dp-h600dp")
    fun anExpandedWindowHasTheRailAtTheStart() {
        compose.setQeetrix {
            QeetrixNavigationScaffold(items, selected = "home", onSelect = {}, modifier = Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize()) { QeetrixButton("Content", onClick = {}) }
            }
        }
        val content = compose.onNodeWithText("Content").getUnclippedBoundsInRoot()
        val home = compose.onNodeWithText("Home").getUnclippedBoundsInRoot()
        assertTrue("rail first: ${home.right} <= ${content.left}", home.right <= content.left)
    }

    @Test
    fun theContentKeepsItsStateWhenTheLayoutChanges() {
        var layout by mutableStateOf(QeetrixNavigationLayout.Bar)
        compose.setQeetrix {
            QeetrixNavigationScaffold(items, selected = "home", onSelect = {}, modifier = Modifier.fillMaxSize(), layout = layout) {
                var taps by remember { mutableIntStateOf(0) }
                QeetrixButton("Tapped $taps", onClick = { taps++ })
            }
        }
        compose.onNodeWithText("Tapped 0").performClick()
        compose.onNodeWithText("Tapped 1").performClick()
        compose.runOnIdle { layout = QeetrixNavigationLayout.Rail }
        compose.onNodeWithText("Tapped 2").assertExists()
    }
}
