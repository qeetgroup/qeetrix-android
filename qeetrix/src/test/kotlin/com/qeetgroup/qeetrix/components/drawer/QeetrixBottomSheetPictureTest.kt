package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isHeading
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** The picture-led sheet, and the sheet's surface drawn in place. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class QeetrixBottomSheetPictureTest {
    @get:Rule
    val compose = createComposeRule()

    /**
     * With a picture: the picture centred at the top, the title and description centred under it, no close button;
     * the title still names the pane and is its heading.
     */
    @Test
    fun aPictureLedSheetCentresItsHeadAndHasNoCloseButton() {
        compose.setQeetrix {
            QeetrixBottomSheet(
                open = true,
                onOpenChange = {},
                title = "Find people you know",
                description = "We check your contacts against Qeet Circle, on this phone.",
                picture = { Box(Modifier.size(96.dp).testTag("picture")) },
                footer = { QeetrixButton("Allow contacts", onClick = {}) },
            ) { QeetrixText("You can change this in Settings.") }
        }
        compose.waitForIdle()
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
        val title = compose.onNode(hasText("Find people you know") and isHeading())
        title.assertExists()
        compose
            .onNode(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Find people you know"))
            .assertExists()
        val picture = compose.onNodeWithTag("picture", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val heading = compose.onNodeWithText("Find people you know", useUnmergedTree = true).getUnclippedBoundsInRoot()
        val description =
            compose
                .onNodeWithText(
                    "We check your contacts against Qeet Circle, on this phone.",
                    useUnmergedTree = true,
                ).getUnclippedBoundsInRoot()
        assertTrue("picture above the title", picture.bottom <= heading.top)
        // The picture-led title is title.lg, as Circle's mockup draws it.
        val layouts = mutableListOf<TextLayoutResult>()
        compose
            .onNodeWithText("Find people you know", useUnmergedTree = true)
            .fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult]
            .action
            ?.invoke(layouts)
        assertEquals(
            QeetrixTextRole.TitleLg.spec.size,
            layouts
                .single()
                .layoutInput.style.fontSize.value,
            0.01f,
        )
        val centre = (picture.left + picture.right) / 2
        assertEquals(centre.value, ((heading.left + heading.right) / 2).value, 1f)
        assertEquals(centre.value, ((description.left + description.right) / 2).value, 1f)
    }

    /** In place: the same parts inside the screen's own composition, named as the pane; the close button only without a picture. */
    @Test
    fun theSurfaceDrawsInPlace() {
        var picture by mutableStateOf(false)
        compose.setQeetrix {
            QeetrixBottomSheetSurface(
                title = "Filter invoices",
                modifier = Modifier.testTag("surface"),
                onClose = {},
                footer = { QeetrixButton("Show results", onClick = {}) },
                picture = if (picture) ({ Box(Modifier.size(48.dp)) }) else null,
            ) { QeetrixText("Status") }
        }
        compose.onNodeWithTag("surface").assertExists()
        compose.onNode(SemanticsMatcher.expectValue(SemanticsProperties.PaneTitle, "Filter invoices")).assertExists()
        compose.onNode(hasText("Filter invoices") and isHeading()).assertExists()
        compose.onNodeWithText("Status").assertExists()
        compose.onNodeWithText("Show results").assertExists()
        compose.onNodeWithContentDescription("Close").assertExists()
        compose.runOnIdle { picture = true }
        compose.onNodeWithContentDescription("Close").assertDoesNotExist()
    }
}
