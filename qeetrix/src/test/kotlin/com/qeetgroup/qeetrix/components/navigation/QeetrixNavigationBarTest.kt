package com.qeetgroup.qeetrix.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The bottom bar: tabs in one group, selection the app's, every choice reported, badges read. */
@RunWith(RobolectricTestRunner::class)
class QeetrixNavigationBarTest {
    @get:Rule
    val compose = createComposeRule()

    private val items =
        listOf(
            QeetrixNavigationItem("home", "Home", QeetrixIconName.House),
            QeetrixNavigationItem("inbox", "Inbox", QeetrixIconName.Inbox, badge = "3"),
            QeetrixNavigationItem("settings", "Settings", QeetrixIconName.Settings),
            QeetrixNavigationItem("admin", "Admin", QeetrixIconName.Lock, enabled = false),
        )

    @Test
    fun itemsAreTabsInOneGroupAndTheAppsSelectionIsMarked() {
        compose.setQeetrix { QeetrixNavigationBar(items, selected = "inbox", onSelect = {}) }
        compose
            .onNodeWithText("Inbox")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
            .assertIsSelected()
            .assertHeightIsAtLeast(48.dp)
            .onParent()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup))
        compose.onNodeWithText("Home").assertIsNotSelected()
        compose.onNodeWithText("Admin").assertIsNotEnabled()
    }

    @Test
    fun choosingReportsTheValueAndTheBarKeepsNoSelectionOfItsOwn() {
        val chosen = mutableListOf<String>()
        var selected by mutableStateOf("home")
        compose.setQeetrix { QeetrixNavigationBar(items, selected = selected, onSelect = { chosen += it }) }
        compose.onNodeWithText("Settings").performClick()
        // Reported, but not selected until the app says so.
        compose.onNodeWithText("Settings").assertIsNotSelected()
        compose.runOnIdle { selected = chosen.last() }
        compose.onNodeWithText("Settings").assertIsSelected()
        // The current destination chosen again is reported too, so the app can return to its top.
        compose.onNodeWithText("Settings").performClick()
        // A disabled destination cannot be chosen.
        compose.onNodeWithText("Admin").performClick()
        assertEquals(listOf("settings", "settings"), chosen)
    }

    @Test
    fun aBadgeIsReadWithItsLabel() {
        compose.setQeetrix { QeetrixNavigationBar(items, selected = "home", onSelect = {}) }
        compose.onNode(hasText("Inbox") and hasText("3")).assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
    }

    @Test
    fun valuesMustBeDistinct() {
        assertThrows(IllegalArgumentException::class.java) {
            compose.setQeetrix {
                Column {
                    QeetrixNavigationBar(
                        listOf(
                            QeetrixNavigationItem(1, "One", QeetrixIconName.House),
                            QeetrixNavigationItem(1, "Again", QeetrixIconName.Inbox),
                        ),
                        selected = 1,
                        onSelect = {},
                    )
                }
            }
        }
    }
}
