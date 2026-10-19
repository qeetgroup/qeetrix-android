package com.qeetgroup.qeetrix.components.button

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
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
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

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

    /** Real text layout (native graphics): widths depend on the bundled font's metrics. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun aLoadingLabelReplacesTheNameAndHoldsTheWidth() {
        var loading by mutableStateOf(false)
        compose.setQeetrix {
            QeetrixButton("Save changes", onClick = {}, loading = loading, loadingLabel = "Saving…", modifier = Modifier.testTag("b"))
        }
        val resting = compose.onNodeWithTag("b").getUnclippedBoundsInRoot().let { it.right - it.left }
        loading = true
        compose.onNodeWithTag("b").assert(SemanticsMatcher.expectValue(SemanticsProperties.ContentDescription, listOf("Saving…")))
        assertEquals(resting, compose.onNodeWithTag("b").getUnclippedBoundsInRoot().let { it.right - it.left })
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
            listOf("default", "outline", "secondary", "ghost", "destructive", "link"),
            QeetrixButtonVariant.entries.map { it.value },
        )
        assertEquals(listOf("default", "xs", "sm", "lg"), QeetrixButtonSize.entries.map { it.value })
    }
}
