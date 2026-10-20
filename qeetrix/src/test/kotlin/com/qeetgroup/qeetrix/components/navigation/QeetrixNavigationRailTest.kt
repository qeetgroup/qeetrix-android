package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The rail: the same item contract as the bar, a header above the destinations. */
@RunWith(RobolectricTestRunner::class)
class QeetrixNavigationRailTest {
    @get:Rule
    val compose = createComposeRule()

    private val items =
        listOf(
            QeetrixNavigationItem(Destination.Home, "Home", QeetrixIconName.House),
            QeetrixNavigationItem(Destination.Inbox, "Inbox", QeetrixIconName.Inbox, badge = "12"),
            QeetrixNavigationItem(Destination.Settings, "Settings", QeetrixIconName.Settings),
        )

    private enum class Destination { Home, Inbox, Settings }

    @Test
    fun destinationsAreTabsWithTheHeaderAboveThem() {
        val chosen = mutableListOf<Destination>()
        compose.setQeetrix {
            QeetrixNavigationRail(items, selected = Destination.Inbox, onSelect = { chosen += it }, modifier = Modifier) {
                QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = {})
            }
        }
        compose
            .onNodeWithText("Inbox", substring = true)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
            .assertIsSelected()
            .assertHeightIsAtLeast(48.dp)
        val header = compose.onNodeWithContentDescription("New message").getUnclippedBoundsInRoot()
        val first = compose.onNodeWithText("Home").getUnclippedBoundsInRoot()
        assertTrue("header first", header.bottom <= first.top)
        compose.onNodeWithText("Settings").performClick()
        assertEquals(listOf(Destination.Settings), chosen)
    }
}
