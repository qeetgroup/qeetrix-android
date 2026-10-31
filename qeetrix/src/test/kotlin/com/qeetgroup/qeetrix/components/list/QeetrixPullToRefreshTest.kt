package com.qeetgroup.qeetrix.components.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Pull to refresh: a pull past the threshold asks the app to refresh, and the end of a refresh is announced. */
@RunWith(RobolectricTestRunner::class)
class QeetrixPullToRefreshTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aPullAsksForARefreshAndTheEndIsAnnounced() {
        var refreshing by mutableStateOf(false)
        var asked = false
        compose.setQeetrix {
            QeetrixPullToRefresh(
                refreshing,
                onRefresh = {
                    asked = true
                    refreshing = true
                },
                modifier = Modifier.testTag("list").size(360.dp, 480.dp),
            ) {
                LazyColumn(Modifier.fillMaxSize()) { items(20) { BasicText("Request $it") } }
            }
        }
        compose.onNodeWithTag("list").performTouchInput { swipeDown(startY = top + 8.dp.toPx(), endY = bottom, durationMillis = 600) }
        compose.waitForIdle()
        assertTrue(asked)
        compose.onNodeWithText("Updated").assertDoesNotExist()
        refreshing = false
        compose.waitForIdle()
        compose.onNodeWithText("Updated").assertExists()
    }
}
