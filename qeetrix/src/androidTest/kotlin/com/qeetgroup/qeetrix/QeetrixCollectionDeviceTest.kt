package com.qeetgroup.qeetrix

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToKey
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixSectionHeader
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A long collection on a device's real window and scrolling: rows recycled far out of view keep the app's
 * selection by id, and a sticky section header stays at the top. Run with
 * `./gradlew :qeetrix:connectedDebugAndroidTest`.
 */
@RunWith(AndroidJUnit4::class)
class QeetrixCollectionDeviceTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aLongKeyedListScrollsAndKeepsItsSelectionById() {
        val ids = (1..500).map { "item-$it" }
        var selected by mutableStateOf(setOf("item-3"))
        compose.setContent {
            QeetrixTheme {
                LazyColumn(Modifier.testTag("list")) {
                    stickyHeader(key = "header") {
                        QeetrixSectionHeader("Members", Modifier.background(QeetrixTheme.colors.surfaceCanvas))
                    }
                    items(ids, key = { it }) { id ->
                        QeetrixListItem(id, selected = id in selected, onClick = {
                            selected = if (id in selected) selected - id else selected + id
                        })
                    }
                }
            }
        }
        compose.onNodeWithText("item-3").assertIsSelected()
        compose.onNodeWithTag("list").performScrollToKey("item-480")
        compose.onNodeWithText("item-480").assertIsDisplayed().performClick()
        compose.onNode(isHeading() and hasText("Members")).assertIsDisplayed()

        compose.onNodeWithTag("list").performScrollToKey("item-3")
        compose.onNodeWithText("item-3").assertIsSelected()
        compose.onNodeWithTag("list").performScrollToKey("item-480")
        compose.onNodeWithText("item-480").assertIsSelected()
    }
}
