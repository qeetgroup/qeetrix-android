package com.qeetgroup.qeetrix.components.button

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixDensity
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.LocalQeetrixDensity
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
class QeetrixButtonTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aClickCallsOnClickOnce() {
        var clicks = 0
        compose.setQeetrix { QeetrixButton("Save", onClick = { clicks++ }) }
        compose
            .onNodeWithText("Save")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assertIsEnabled()
            .performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun aDisabledButtonCannotBeActivated() {
        var clicks = 0
        compose.setQeetrix { QeetrixButton("Save", onClick = { clicks++ }, enabled = false) }
        compose.onNodeWithText("Save").assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
    }

    @Test
    fun loadingIgnoresActivationButKeepsTheButtonFocusableAndSaysSo() {
        var clicks = 0
        var loading by mutableStateOf(true)
        compose.setQeetrix { QeetrixButton("Save", onClick = { clicks++ }, loading = loading) }
        compose
            .onNodeWithText("Save")
            .assertIsEnabled() // busy is not disabled
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Loading"))
            .performClick()
        assertEquals(0, clicks)
        loading = false
        compose.onNodeWithText("Save").assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.StateDescription)).performClick()
        assertEquals(1, clicks)
    }

    /** Real layout (native graphics): the zone holds its width, so loading never moves the label. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun loadingPutsTheSpinnerInTheIconZoneAndKeepsTheWidth() {
        var loading by mutableStateOf(false)
        compose.setQeetrix {
            QeetrixButton("Save changes", onClick = {}, icon = QeetrixIconName.Check, loading = loading, modifier = Modifier.testTag("b"))
        }
        val resting = compose.onNodeWithTag("b").getUnclippedBoundsInRoot().let { it.right - it.left }
        loading = true
        compose.onNodeWithTag("b").assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "Loading"))
        assertEquals(resting, compose.onNodeWithTag("b").getUnclippedBoundsInRoot().let { it.right - it.left })
    }

    /**
     * button.css `[data-full-width]`: the label's text is centred on the whole button, with or without an icon,
     * at every size and right to left; the icon keeps its zone at the end.
     */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun aFullWidthLabelIsCentredOnTheWholeButton() {
        val cases =
            listOf(null, QeetrixIconName.ArrowRight).flatMap { icon ->
                QeetrixButtonSize.entries.flatMap { size ->
                    listOf(LayoutDirection.Ltr, LayoutDirection.Rtl).map { direction -> Triple(icon, size, direction) }
                }
            }
        var case by mutableStateOf(cases.first())
        compose.setQeetrix {
            CompositionLocalProvider(LocalLayoutDirection provides case.third) {
                QeetrixButton(
                    "Continue",
                    onClick = {},
                    modifier = Modifier.width(320.dp).testTag("b"),
                    size = case.second,
                    icon = case.first,
                    fullWidth = true,
                )
            }
        }
        val offsets = mutableListOf<String>()
        for (each in cases) {
            case = each
            compose.waitForIdle()
            val button = compose.onNodeWithTag("b").getUnclippedBoundsInRoot()
            val label = compose.onNodeWithText("Continue", useUnmergedTree = true)
            val box = label.getUnclippedBoundsInRoot()
            val layouts = mutableListOf<TextLayoutResult>()
            label.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
            val line = layouts.first()
            val centre = with(compose.density) { box.left + ((line.getLineLeft(0) + line.getLineRight(0)) / 2).toDp() }
            val offset = centre - (button.left + button.right) / 2
            if (abs(offset.value) > 1f) offsets += "$each: $offset"
        }
        assertEquals(emptyList<String>(), offsets)
    }

    /** button.css: block-size is size.control.* of the density. */
    @Test
    fun heightsFollowTheDensity() {
        compose.setQeetrix {
            CompositionLocalProvider(LocalQeetrixDensity provides QeetrixDensity.Comfortable) {
                Row {
                    for (size in QeetrixButtonSize.entries) {
                        QeetrixButton(
                            "Go",
                            onClick = {},
                            size = size,
                            modifier = Modifier.testTag(size.value),
                        )
                    }
                }
            }
        }
        // 32 / 36 / 44 at comfortable; the drawn button sits inside its 48 dp touch area.
        compose.onNodeWithTag("lg").assertHeightIsAtLeast(48.dp)
        assertEquals(44.dp, QeetrixDensity.Comfortable.sizeControlLg)
    }

    @Test
    fun everySizeHasA48DpTouchTarget() {
        compose.setQeetrix {
            Row {
                for (size in QeetrixButtonSize.entries) {
                    QeetrixButton(
                        "Go",
                        onClick = {},
                        size = size,
                        modifier = Modifier.testTag(size.value),
                    )
                }
            }
        }
        for (size in QeetrixButtonSize.entries) {
            compose.onNodeWithTag(size.value).assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp)
        }
    }

    @Test
    fun variantsAndSizesAreTheWebs() {
        assertEquals(
            listOf("solid", "soft", "ghost"),
            QeetrixButtonVariant.entries.map { it.value },
        )
        assertEquals(listOf("brand", "danger", "neutral"), QeetrixButtonTone.entries.map { it.value })
        assertEquals(listOf("sm", "md", "lg"), QeetrixButtonSize.entries.map { it.value })
    }
}
