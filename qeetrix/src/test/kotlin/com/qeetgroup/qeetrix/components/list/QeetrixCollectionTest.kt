package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.accordion.QeetrixAccordionItem
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Collections as an app builds them: rows keyed by the app's ids keep their own state and the app's selection
 * through insertions, removals and reordering, survive state restoration, and a change to one row recomposes
 * only that row.
 */
@RunWith(RobolectricTestRunner::class)
class QeetrixCollectionTest {
    @get:Rule
    val compose = createComposeRule()

    private class Item(
        val id: String,
        val title: String,
    )

    @Test
    fun keyedRowsKeepTheirStateThroughInsertionRemovalAndReordering() {
        val items = mutableStateListOf(Item("a", "Alpha"), Item("b", "Beta"), Item("c", "Gamma"))
        compose.setContent {
            QeetrixTheme {
                LazyColumn {
                    items(items, key = { it.id }) { item ->
                        // State held inside the row, which the lazy list keys by the item's id.
                        var open by rememberSaveable { mutableStateOf(false) }
                        QeetrixAccordionItem(item.title, expanded = open, onExpandedChange = { open = it }) {
                            QeetrixText("${item.title} details")
                        }
                    }
                }
            }
        }
        compose.onNodeWithText("Beta").performClick()
        compose.onNodeWithText("Beta details").assertExists()

        compose.runOnIdle { items.add(0, Item("z", "Zeta")) }
        compose.onNodeWithText("Beta details").assertExists()
        compose.onNodeWithText("Zeta details").assertDoesNotExist()

        compose.runOnIdle { items.removeAll { it.id == "a" } }
        compose.onNodeWithText("Beta details").assertExists()

        compose.runOnIdle { items.reverse() }
        compose.onNodeWithText("Beta details").assertExists()
        compose.onNodeWithText("Gamma details").assertDoesNotExist()
        compose.onNodeWithText("Zeta details").assertDoesNotExist()
    }

    @Test
    fun theAppsSelectionFollowsIdsNotPositions() {
        val items = mutableStateListOf(Item("a", "Alpha"), Item("b", "Beta"), Item("c", "Gamma"))
        var selected by mutableStateOf(setOf("b"))
        compose.setContent {
            QeetrixTheme {
                LazyColumn {
                    items(items, key = { it.id }) { item ->
                        QeetrixListItem(item.title, selected = item.id in selected, onClick = {
                            selected = if (item.id in selected) selected - item.id else selected + item.id
                        })
                    }
                }
            }
        }
        compose.onNodeWithText("Beta").assertIsSelected()
        compose.runOnIdle {
            items.reverse()
            items.add(1, Item("d", "Delta"))
        }
        compose.onNodeWithText("Beta").assertIsSelected()
        compose.onNodeWithText("Delta").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Selected))
        compose.onNodeWithText("Gamma").performClick()
        compose.runOnIdle { assertEquals(setOf("b", "c"), selected) }
    }

    @Test
    fun expansionAndSelectionSurviveStateRestoration() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            QeetrixTheme {
                var open by rememberSaveable { mutableStateOf(setOf<String>()) }
                var chosen by rememberSaveable { mutableStateOf(false) }
                Column {
                    QeetrixAccordionItem("Billing", expanded = "billing" in open, onExpandedChange = {
                        open = if (it) open + "billing" else open - "billing"
                    }) { QeetrixText("Invoices") }
                    QeetrixChip("Unread", selected = chosen, onSelectedChange = { chosen = it })
                }
            }
        }
        compose.onNodeWithText("Billing").performClick()
        compose.onNodeWithText("Unread").performClick()
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Invoices").assertExists()
        compose.onNodeWithText("Unread").assertIsOn()
    }

    @Test
    fun selectingOneRowRecomposesOnlyThatRow() {
        val ids = (1..20).map { "item-$it" }
        val compositions = mutableMapOf<String, Int>()
        var selected by mutableStateOf(setOf<String>())
        compose.setContent {
            QeetrixTheme {
                LazyColumn(Modifier.height(2000.dp), state = rememberLazyListState()) {
                    items(ids, key = { it }) { id ->
                        key(id) { CountedRow(id, id in selected, compositions) }
                    }
                }
            }
        }
        compose.waitForIdle()
        val before = compositions.toMap()
        compose.runOnIdle { selected = setOf("item-3") }
        compose.waitForIdle()
        val recomposed = compositions.filter { (id, count) -> count != before[id] }.keys
        assertEquals(setOf("item-3"), recomposed)
    }

    /** An app's row: skippable because its inputs are stable, and counting the times its body runs. */
    @Composable
    private fun CountedRow(
        id: String,
        selected: Boolean,
        compositions: MutableMap<String, Int>,
    ) {
        SideEffect { compositions[id] = (compositions[id] ?: 0) + 1 }
        QeetrixListItem(id, selected = selected, onClick = {})
    }
}
