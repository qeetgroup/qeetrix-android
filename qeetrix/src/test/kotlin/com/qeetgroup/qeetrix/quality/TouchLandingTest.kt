package com.qeetgroup.qeetrix.quality

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/**
 * Where a touch lands near the controls that are drawn smaller than 48 dp inside or beside another control
 * (`LayoutQualityTest` lists them): a tap within the 48 dp target around each, outside what is drawn, must
 * reach it rather than the control around it.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TouchLandingTest {
    @get:Rule
    val compose = createComposeRule()

    /** Taps [dx], [dy] from the centre of [node], in root coordinates. */
    private fun tapNear(
        node: SemanticsNodeInteraction,
        dx: Dp,
        dy: Dp,
    ) {
        val bounds = node.fetchSemanticsNode().boundsInRoot
        val offset = with(compose.density) { Offset(bounds.center.x + dx.toPx(), bounds.center.y + dy.toPx()) }
        compose.onRoot().performTouchInput { click(offset) }
        compose.waitForIdle()
    }

    @Test
    fun thePasswordRevealButtonTakesTapsWithin48Dp() {
        compose.setContent {
            QeetrixTheme {
                Box(
                    Modifier.width(320.dp).padding(16.dp),
                ) { QeetrixPasswordInput(TextFieldState("Synthetic-1"), label = "Password") }
            }
        }
        tapNear(compose.onNodeWithContentDescription("Show password"), dx = (-16).dp, dy = 0.dp)
        compose.onNodeWithContentDescription("Hide password").assertExists()
        tapNear(compose.onNodeWithContentDescription("Hide password"), dx = 0.dp, dy = 14.dp)
        compose.onNodeWithContentDescription("Show password").assertExists()
    }

    @Test
    fun theSearchClearButtonTakesTapsWithin48Dp() {
        val query = TextFieldState("Ada")
        compose.setContent { QeetrixTheme { Box(Modifier.width(320.dp).padding(16.dp)) { QeetrixSearchField(query) } } }
        tapNear(compose.onNodeWithContentDescription("Clear"), dx = (-16).dp, dy = 0.dp)
        compose.runOnIdle { assertEquals("", query.text.toString()) }
    }

    @Test
    fun aNumberFieldStepperTakesTapsWithin48Dp() {
        var seats by mutableStateOf<Int?>(4)
        compose.setContent {
            QeetrixTheme { Box(Modifier.width(320.dp).padding(16.dp)) { QeetrixNumberField(seats, { seats = it }, label = "Seats") } }
        }
        tapNear(compose.onNodeWithContentDescription("Increase"), dx = 0.dp, dy = 14.dp)
        compose.runOnIdle { assertEquals(5, seats) }
    }

    @Test
    fun aChipsRemoveButtonTakesTapsWithin48Dp() {
        var removed = 0
        compose.setContent { QeetrixTheme { Box(Modifier.padding(32.dp)) { QeetrixChip("Owner: Ada", onRemove = { removed++ }) } } }
        tapNear(compose.onNodeWithContentDescription("Remove Owner: Ada"), dx = 14.dp, dy = 0.dp)
        tapNear(compose.onNodeWithContentDescription("Remove Owner: Ada"), dx = 0.dp, dy = 16.dp)
        compose.runOnIdle { assertEquals(2, removed) }
    }

    @Test
    fun aTabTakesTapsInItsBand() {
        var selected by mutableStateOf("overview")
        compose.setContent {
            QeetrixTheme {
                Box(Modifier.padding(32.dp)) {
                    QeetrixTabs(
                        listOf(
                            QeetrixTab("overview", "Overview"),
                            QeetrixTab("activity", "Activity"),
                        ),
                        selected = selected,
                        onSelect = {
                            selected =
                                it
                        },
                    )
                }
            }
        }
        tapNear(compose.onNodeWithText("Activity"), dx = 0.dp, dy = (-16).dp)
        compose.onNodeWithText("Activity").assertIsSelected()
    }

    @Test
    fun anUnlabelledSearchFieldTakesTapsWithin48Dp() {
        compose.setContent { QeetrixTheme { Box(Modifier.width(320.dp).padding(32.dp)) { QeetrixSearchField(TextFieldState()) } } }
        tapNear(compose.onNodeWithText("Search…", useUnmergedTree = true), dx = 0.dp, dy = (-20).dp)
        compose.onNodeWithContentDescription("Search").assertIsFocused()
    }
}
