package com.qeetgroup.qeetrix.components.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconVariant
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** A button's icon as your own drawing: the zone a named icon gets, the label's colour, full-width centring, loading. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class QeetrixButtonCustomIconTest {
    @get:Rule
    val compose = createComposeRule()

    /** The same button with a named icon and with a drawing of the same size takes the same room, its label in the same place. */
    @Test
    fun aCustomIconGetsTheNamedIconsZone() {
        var colour = Color.Unspecified
        var label = Color.Unspecified
        var box = IntSize.Zero
        compose.setQeetrix {
            label = QeetrixTheme.colors.textInverse
            Column(Modifier.width(320.dp)) {
                QeetrixButton(
                    "Hang up and report",
                    onClick = {},
                    modifier = Modifier.testTag("named"),
                    variant = QeetrixButtonVariant.Soft,
                    tone = QeetrixButtonTone.Neutral,
                    size = QeetrixButtonSize.Lg,
                    icon = QeetrixIconName.Smartphone,
                    fullWidth = true,
                )
                QeetrixButton(
                    "Hang up and report",
                    onClick = {},
                    modifier = Modifier.testTag("custom"),
                    variant = QeetrixButtonVariant.Soft,
                    tone = QeetrixButtonTone.Neutral,
                    size = QeetrixButtonSize.Lg,
                    fullWidth = true,
                ) {
                    colour = LocalQeetrixContentColor.current
                    Box(Modifier.fillMaxSize().onSizeChanged { box = it }) {
                        QeetrixIcon(
                            QeetrixIconName.Smartphone,
                            null,
                            Modifier.rotate(135f),
                            size = QeetrixSize.IconMd,
                            variant = QeetrixIconVariant.Filled,
                        )
                    }
                }
            }
        }
        assertEquals(label, colour)
        assertEquals(20.dp, with(compose.density) { box.width.toDp() })
        val labels = compose.onAllNodes(hasText("Hang up and report") and !hasClickAction(), useUnmergedTree = true).fetchSemanticsNodes()
        val (named, custom) = labels.map { it.positionInRoot.x }
        assertEquals(
            compose.onNodeWithTag("named").fetchSemanticsNode().size,
            compose.onNodeWithTag("custom").fetchSemanticsNode().size,
        )
        // Full-width centring: the label starts where the named icon's does.
        assertEquals(2, labels.size)
        assertEquals(named, custom, 1f)
    }

    /** While loading, the spinner takes the drawing's place, and the button keeps its label and reports "Loading". */
    @Test
    fun loadingReplacesTheCustomIcon() {
        var loading by mutableStateOf(false)
        var drawn = false
        compose.setQeetrix {
            QeetrixButton("Save", onClick = {}, loading = loading) { drawn = true }
        }
        assertTrue(drawn)
        drawn = false
        compose.runOnIdle { loading = true }
        compose.waitForIdle()
        assertFalse(drawn)
        compose.onNodeWithText("Save").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Loading"))
    }

    /** An icon button with a drawing: named by its description, the drawing in its colour and icon size, activated by a tap. */
    @Test
    fun anIconButtonTakesADrawingToo() {
        var colour = Color.Unspecified
        var label = Color.Unspecified
        var clicks = 0
        var box = IntSize.Zero
        compose.setQeetrix {
            label = QeetrixTheme.colors.textOnBold
            QeetrixIconButton("End call", onClick = { clicks++ }, variant = QeetrixButtonVariant.Solid, tone = QeetrixButtonTone.Danger) {
                colour = LocalQeetrixContentColor.current
                Box(Modifier.fillMaxSize().onSizeChanged { box = it })
            }
        }
        compose.onNodeWithContentDescription("End call").performClick()
        assertEquals(1, clicks)
        assertEquals(label, colour)
        assertEquals(16.dp, with(compose.density) { box.width.toDp() })
        val square = compose.onNodeWithContentDescription("End call").fetchSemanticsNode().size
        assertEquals(square.width, square.height)
    }
}
