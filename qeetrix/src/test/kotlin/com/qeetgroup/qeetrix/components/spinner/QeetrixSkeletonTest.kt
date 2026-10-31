package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTextRole
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Skeletons: the variants' default sizes, the size they are given, silence for accessibility services, a paragraph's lines. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSkeletonTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aSkeletonTakesItsSizeAndSaysNothing() {
        compose.setQeetrix {
            Box(Modifier.testTag("frame")) {
                QeetrixSkeleton(Modifier.size(40.dp).testTag("skeleton"), QeetrixSkeletonVariant.Block)
            }
        }
        val skeleton = compose.onNodeWithTag("skeleton", useUnmergedTree = true).assertWidthIsEqualTo(40.dp).assertHeightIsEqualTo(40.dp)
        // Nothing for TalkBack: no text, description or role — only the test's own tag.
        assertEquals(
            setOf("TestTag"),
            skeleton
                .fetchSemanticsNode()
                .config
                .map { it.key.name }
                .toSet(),
        )
    }

    @Test
    fun eachVariantHasTheWebsDefaultSize() {
        compose.setQeetrix {
            Column(Modifier.width(300.dp)) {
                QeetrixSkeleton(Modifier.testTag("text"))
                QeetrixSkeleton(Modifier.testTag("block"), QeetrixSkeletonVariant.Block)
                for (size in QeetrixSkeletonSize.entries) {
                    QeetrixSkeleton(Modifier.testTag(size.value), QeetrixSkeletonVariant.Circle, size)
                }
            }
        }
        // A line of body.md (14 / 20) at the default font scale, the full width.
        compose.onNodeWithTag("text", useUnmergedTree = true).assertWidthIsEqualTo(300.dp).assertHeightIsEqualTo(20.dp)
        compose.onNodeWithTag("block", useUnmergedTree = true).assertWidthIsEqualTo(300.dp).assertHeightIsEqualTo(64.dp)
        for ((size, diameter) in listOf("sm" to 24, "md" to 32, "lg" to 40, "xl" to 48)) {
            compose.onNodeWithTag(size, useUnmergedTree = true).assertWidthIsEqualTo(diameter.dp).assertHeightIsEqualTo(diameter.dp)
        }
    }

    @Test
    fun aParagraphTakesItsLinesHeight() {
        compose.setQeetrix { QeetrixSkeletonText(Modifier.width(300.dp).testTag("text"), lines = 3, role = QeetrixTextRole.BodyMd) }
        val spec = QeetrixTextRole.BodyMd.spec
        // Three lines of the role (sp = dp at the default font scale): the text that replaces it moves nothing.
        compose.onNodeWithTag("text", useUnmergedTree = true).assertHeightIsEqualTo((spec.size * spec.lineHeight * 3).dp)
    }

    @Test
    fun namesAreTheWebs() {
        assertEquals(listOf("text", "block", "circle"), QeetrixSkeletonVariant.entries.map { it.value })
        assertEquals(listOf("sm", "md", "lg", "xl"), QeetrixSkeletonSize.entries.map { it.value })
    }
}
