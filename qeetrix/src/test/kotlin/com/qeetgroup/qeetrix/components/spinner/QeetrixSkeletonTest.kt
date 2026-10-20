package com.qeetgroup.qeetrix.components.spinner

import androidx.compose.foundation.layout.Box
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

/** Skeletons: the size they are given, silent for accessibility services, lines in a role's rhythm. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSkeletonTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aSkeletonTakesItsSizeAndSaysNothing() {
        compose.setQeetrix {
            Box(Modifier.testTag("frame")) {
                QeetrixSkeleton(Modifier.size(40.dp).testTag("skeleton"), QeetrixSkeletonShape.Circle)
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
    fun textLinesTakeTheRolesSizeAndRhythm() {
        compose.setQeetrix { QeetrixSkeletonText(Modifier.width(300.dp).testTag("text"), lines = 3, role = QeetrixTextRole.Body) }
        val spec = QeetrixTextRole.Body.spec
        // Bars the font's size (sp = dp at the default font scale), spaced by the rest of the line height.
        val bar = spec.size.dp
        compose.onNodeWithTag("text", useUnmergedTree = true).assertHeightIsEqualTo(bar * 3 + (bar * (spec.lineHeight - 1f)) * 2)
    }
}
