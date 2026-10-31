package com.qeetgroup.qeetrix.quality

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.click
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixChip
import com.qeetgroup.qeetrix.components.input.QeetrixField
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.toast.QeetrixSnackbar
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
 * reach it rather than the control around it. And the negative control: a tap further out reaches the control
 * around it instead.
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

    /** The password's show button is drawn 28 dp: taps 16 dp beside and below its centre still toggle it. */
    @Test
    fun thePasswordRevealButtonTakesTapsWithin48Dp() {
        compose.setContent {
            QeetrixTheme {
                Box(Modifier.width(320.dp).padding(16.dp)) { QeetrixPasswordInput(TextFieldState("Synthetic-1"), label = "Password") }
            }
        }
        val reveal = compose.onNodeWithContentDescription("Show password")
        reveal.assertIsOff()
        tapNear(reveal, dx = (-16).dp, dy = 0.dp)
        reveal.assertIsOn()
        tapNear(reveal, dx = 0.dp, dy = 16.dp)
        reveal.assertIsOff()
    }

    @Test
    fun theSearchClearButtonTakesTapsWithin48Dp() {
        val query = TextFieldState("Ada")
        compose.setContent { QeetrixTheme { Box(Modifier.width(320.dp).padding(16.dp)) { QeetrixSearchField(query) } } }
        tapNear(compose.onNodeWithContentDescription("Clear search"), dx = (-16).dp, dy = 0.dp)
        compose.runOnIdle { assertEquals("", query.text.toString()) }
    }

    /** The steppers are drawn 28 dp wide, 2 dp apart: a tap 16 dp out from either centre still steps. */
    @Test
    fun aNumberFieldStepperTakesTapsWithin48Dp() {
        var seats by mutableStateOf<Int?>(4)
        compose.setContent {
            QeetrixTheme { Box(Modifier.width(320.dp).padding(16.dp)) { QeetrixNumberField(seats, { seats = it }, label = "Seats") } }
        }
        tapNear(compose.onNodeWithContentDescription("Increase"), dx = 16.dp, dy = 0.dp)
        compose.runOnIdle { assertEquals(5, seats) }
        tapNear(compose.onNodeWithContentDescription("Decrease"), dx = (-16).dp, dy = 0.dp)
        compose.runOnIdle { assertEquals(4, seats) }
    }

    @Test
    fun aChipsRemoveButtonTakesTapsWithin48Dp() {
        var removed = 0
        compose.setContent { QeetrixTheme { Box(Modifier.padding(32.dp)) { QeetrixChip("Owner: Ada", onRemove = { removed++ }) } } }
        tapNear(compose.onNodeWithContentDescription("Remove Owner: Ada"), dx = 18.dp, dy = 0.dp)
        tapNear(compose.onNodeWithContentDescription("Remove Owner: Ada"), dx = 0.dp, dy = 16.dp)
        compose.runOnIdle { assertEquals(2, removed) }
    }

    /** A tab is drawn 40 dp tall in a 48 dp band: a tap in the band above it selects it. */
    @Test
    fun aTabTakesTapsInItsBand() {
        var selected by mutableStateOf("overview")
        compose.setContent {
            QeetrixTheme {
                Box(Modifier.padding(32.dp)) {
                    QeetrixTabs(
                        listOf(QeetrixTab("overview", "Overview"), QeetrixTab("activity", "Activity")),
                        value = selected,
                        onValueChange = { selected = it },
                    )
                }
            }
        }
        tapNear(compose.onNodeWithText("Activity"), dx = 0.dp, dy = (-22).dp)
        compose.onNodeWithText("Activity").assertIsSelected()
    }

    /** Without a label the field is centred in a 48 dp touch height: a tap just above the drawn box focuses it. */
    @Test
    fun anUnlabelledSearchFieldTakesTapsWithin48Dp() {
        compose.setContent {
            QeetrixTheme { Box(Modifier.width(320.dp).padding(32.dp)) { QeetrixSearchField(TextFieldState(), placeholder = "e.g. Asha") } }
        }
        tapNear(compose.onNodeWithText("e.g. Asha", useUnmergedTree = true), dx = 0.dp, dy = (-22).dp)
        compose.onNodeWithContentDescription("Search").assertIsFocused()
    }

    /**
     * A field's help button is drawn 28 dp and takes no more height than its label's line: a tap 16 dp beside it opens
     * the help. It is a control of its own beside a field control too (finding Q-7: the label's hidden text hid it).
     */
    @Test
    fun aFieldsHelpButtonTakesTapsWithin48Dp() {
        compose.setContent {
            QeetrixTheme {
                Box(Modifier.width(320.dp).padding(16.dp)) {
                    Column {
                        QeetrixTextField(TextFieldState("2,500"), label = "Amount", help = "Billed on the first of each month.")
                        QeetrixField(label = "Notifications", help = "Sent by email and push.") {
                            QeetrixSwitch(true, {}, Modifier.qeetrixFieldControl())
                        }
                    }
                }
            }
        }
        tapNear(compose.onNodeWithContentDescription("About Amount"), dx = 16.dp, dy = 0.dp)
        compose.onNode(isHeading() and hasText("Amount")).assertExists()
        compose.onNodeWithContentDescription("About Notifications").assertExists()
    }

    /**
     * A snackbar's buttons are laid out at their drawn size and their 48 dp touch areas overhang the snackbar's
     * padding: a tap 22 dp beside the centre of the drawn 28 dp close button, past the snackbar's end, still closes it.
     */
    @Test
    fun aSnackbarsCloseButtonTakesTapsWithin48Dp() {
        var dismissed = 0
        compose.setContent {
            QeetrixTheme {
                Box(Modifier.width(360.dp).padding(32.dp)) { QeetrixSnackbar("Conversation archived", onDismiss = { dismissed++ }) }
            }
        }
        tapNear(compose.onNodeWithContentDescription("Dismiss"), dx = 22.dp, dy = 0.dp)
        tapNear(compose.onNodeWithContentDescription("Dismiss"), dx = 0.dp, dy = (-22).dp)
        compose.runOnIdle { assertEquals(2, dismissed) }
    }

    /** The negative control: a tap 30 dp from the clear button's centre, past its 48 dp target, does not clear. */
    @Test
    fun aTapPastThe48DpTargetMissesIt() {
        val query = TextFieldState("Ada")
        compose.setContent { QeetrixTheme { Box(Modifier.width(320.dp).padding(16.dp)) { QeetrixSearchField(query) } } }
        tapNear(compose.onNodeWithContentDescription("Clear search"), dx = (-30).dp, dy = 0.dp)
        compose.runOnIdle { assertEquals("Ada", query.text.toString()) }
    }
}
