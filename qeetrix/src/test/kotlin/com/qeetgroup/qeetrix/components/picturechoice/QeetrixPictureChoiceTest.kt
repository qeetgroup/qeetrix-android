package com.qeetgroup.qeetrix.components.picturechoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTouchHeightIsEqualTo
import androidx.compose.ui.test.assertTouchWidthIsEqualTo
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Picture choice: a radio group of pictures, each named by its label, in a row or a grid. */
@RunWith(RobolectricTestRunner::class)
class QeetrixPictureChoiceTest {
    @get:Rule
    val compose = createComposeRule()

    private val avatars =
        listOf(
            QeetrixOption("fox", "Fox"),
            QeetrixOption("owl", "Owl"),
            QeetrixOption("cat", "Cat"),
            QeetrixOption("dog", "Dog"),
            QeetrixOption("bee", "Bee", enabled = false),
        )

    @Test
    fun eachPictureIsARadioNamedByItsLabelInANamedGroup() {
        var avatar by mutableStateOf("fox")
        compose.setQeetrix {
            QeetrixPictureChoiceGroup(
                avatar,
                { avatar = it },
                avatars,
                contentDescription = "Avatar",
                modifier = Modifier.testTag("group"),
            ) {
                Box(Modifier.fillMaxSize().background(Color.Gray))
            }
        }
        compose.onNodeWithTag("group").assert(hasContentDescription("Avatar"))
        compose
            .onNodeWithContentDescription("Fox")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assert(hasAnyAncestor(SemanticsMatcher.keyIsDefined(SemanticsProperties.SelectableGroup)))
            .assertIsSelected()
            .assertTouchWidthIsEqualTo(48.dp)
            .assertTouchHeightIsEqualTo(48.dp)
        compose
            .onNodeWithContentDescription("Owl")
            .assertIsNotSelected()
            .performClick()
            .assertIsSelected()
        assertEquals("owl", avatar)
        compose.onNodeWithContentDescription("Fox").assertIsNotSelected()
        compose.onNodeWithContentDescription("Bee").assertIsNotEnabled().performClick()
        assertEquals("owl", avatar)
    }

    /** `[data-layout="grid"]`: four columns of 48 dp pictures, `space.gap.md` (12 dp) apart both ways; lg pictures are 64 dp. */
    @Test
    fun aGridHasItsColumnsAndAPictureItsSize() {
        var size by mutableStateOf(QeetrixPictureChoiceSize.Md)
        compose.setQeetrix {
            QeetrixPictureChoiceGroup("fox", {}, avatars, contentDescription = "Avatar", columns = 4, size = size) {}
        }
        val fox = compose.onNodeWithContentDescription("Fox").getUnclippedBoundsInRoot()
        val owl = compose.onNodeWithContentDescription("Owl").getUnclippedBoundsInRoot()
        val bee = compose.onNodeWithContentDescription("Bee").getUnclippedBoundsInRoot()
        assertEquals(48.dp, fox.right - fox.left)
        assertEquals(fox.right + 12.dp, owl.left)
        assertEquals(fox.left, bee.left)
        assertEquals(fox.bottom + 12.dp, bee.top)
        compose.runOnIdle { size = QeetrixPictureChoiceSize.Lg }
        val large = compose.onNodeWithContentDescription("Fox").getUnclippedBoundsInRoot()
        assertEquals(64.dp, large.bottom - large.top)
        // sm: 36 dp swatches, 12 dp apart, each still a 48 dp touch target.
        compose.runOnIdle { size = QeetrixPictureChoiceSize.Sm }
        val small = compose.onNodeWithContentDescription("Fox").getUnclippedBoundsInRoot()
        val next = compose.onNodeWithContentDescription("Owl").getUnclippedBoundsInRoot()
        assertEquals(36.dp, small.bottom - small.top)
        assertEquals(small.right + 12.dp, next.left)
        compose.onNodeWithContentDescription("Fox").assertTouchWidthIsEqualTo(48.dp).assertTouchHeightIsEqualTo(48.dp)
        assertEquals(listOf("sm", "md", "lg"), QeetrixPictureChoiceSize.entries.map { it.value })
    }

    /** The picture is decoration: what it draws, an avatar's initials say, is not read; the label is. */
    @Test
    fun thePictureIsNotRead() {
        compose.setQeetrix {
            QeetrixPictureChoiceGroup("fox", {}, avatars.take(1), contentDescription = "Avatar") {
                androidx.compose.foundation.text
                    .BasicText("FX")
            }
        }
        compose
            .onNodeWithContentDescription("Fox")
            .assert(SemanticsMatcher("has no text") { SemanticsProperties.Text !in it.config })
    }
}
