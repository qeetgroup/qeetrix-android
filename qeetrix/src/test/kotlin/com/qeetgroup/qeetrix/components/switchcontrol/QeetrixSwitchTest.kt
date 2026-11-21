package com.qeetgroup.qeetrix.components.switchcontrol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.assertTouchWidthIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class QeetrixSwitchTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun itIsANativeSwitchThatToggles() {
        compose.setQeetrix {
            var on by remember { mutableStateOf(false) }
            QeetrixSwitch(on, { on = it }, modifier = Modifier.testTag("s"))
        }
        compose
            .onNodeWithTag("s")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assertIsOff()
            .assertTouchHeightIsEqualTo(48.dp)
            .performClick()
            .assertIsOn()
    }

    @Test
    fun aDisabledSwitchDoesNotToggle() {
        var changes = 0
        compose.setQeetrix { QeetrixSwitch(true, { changes++ }, enabled = false, modifier = Modifier.testTag("s")) }
        compose
            .onNodeWithTag("s")
            .assertIsNotEnabled()
            .assertIsOn()
            .performClick()
        assertEquals(0, changes)
    }

    @Test
    fun aReadOnlySwitchReportsItsStateWithoutAnAction() {
        compose.setQeetrix {
            androidx.compose.foundation.layout.Column {
                QeetrixSwitch(true, onCheckedChange = null, modifier = Modifier.testTag("a"))
                QeetrixSwitch(true, onCheckedChange = {}, readOnly = true, modifier = Modifier.testTag("b"))
            }
        }
        for (tag in listOf("a", "b")) {
            compose
                .onNodeWithTag(tag)
                .assertIsOn()
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
                .assert(!hasClickAction())
        }
    }

    @Test
    fun theLabelledSwitchIsOneControlTheWholeRowToggles() {
        var on by mutableStateOf(false)
        compose.setQeetrix { QeetrixSwitch(on, { on = it }, label = "Email alerts", description = "Sent within a minute") }
        compose
            .onNodeWithText("Email alerts")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Switch))
            .assertIsOff()
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assertEquals(true, on)
        compose.onNodeWithText("Sent within a minute").assertIsOn()
    }

    /**
     * switch.css: the bare switch is laid out at its track's 32 × 18 dp at every density, as on the web, while
     * accessibility services read the 48 × 48 dp area round it that takes its taps.
     */
    @Test
    fun theBareSwitchIsLaidOutAtItsTrack() {
        var density by mutableStateOf(QeetrixDensity.Default)
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides density) {
                Box(Modifier.padding(32.dp)) {
                    QeetrixSwitch(
                        false,
                        {},
                        Modifier.testTag("s").semantics {
                            contentDescription =
                                "Read receipts"
                        },
                    )
                }
            }
        }
        for (each in QeetrixDensity.entries) {
            compose.runOnIdle { density = each }
            val laidOut = compose.onNodeWithTag("s").fetchSemanticsNode().size
            with(compose.density) {
                assertEquals("$each width", 32.dp, laidOut.width.toDp())
                assertEquals("$each height", 18.dp, laidOut.height.toDp())
            }
            compose.onNodeWithTag("s").assertTouchWidthIsEqualTo(48.dp).assertTouchHeightIsEqualTo(48.dp)
        }
    }

    /**
     * A settings row — a body.md label and the bare switch at its end, 16 dp above and below — is the web's 52 dp: the
     * switch adds no height, and the label keeps all but the track's 32 dp and the 16 dp gap.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun aSettingsRowKeepsTheWebsHeightAndWidths() {
        compose.setQeetrix {
            Row(
                Modifier.width(320.dp).testTag("row").padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QeetrixText("Read receipts", Modifier.weight(1f).testTag("label"))
                QeetrixSwitch(true, {}, Modifier.semantics { contentDescription = "Read receipts" })
            }
        }
        compose.onNodeWithTag("row").assertHeightIsEqualTo(52.dp)
        compose.onNodeWithTag("label").assertWidthIsEqualTo(320.dp - 16.dp - 32.dp)
    }

    /** design rules §9: the label sits after the control, 8 dp away; the track is 32 dp wide. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun theLabelFollowsTheTrack() {
        compose.setQeetrix { QeetrixSwitch(false, {}, label = "Auto-renew") }
        val row = compose.onNodeWithText("Auto-renew").getUnclippedBoundsInRoot()
        val label = compose.onNodeWithText("Auto-renew", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertEquals(32f + 8f, (label.left - row.left).value, 0.5f)
        assertTrue(label.right <= row.right)
    }
}
