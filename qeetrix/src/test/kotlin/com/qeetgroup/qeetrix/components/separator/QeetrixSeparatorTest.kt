package com.qeetgroup.qeetrix.components.separator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixSeparatorTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aHorizontalRuleIsOneDpTallAndFullWidth() {
        compose.setQeetrix { Box(Modifier.width(200.dp)) { QeetrixSeparator(Modifier.testTag("rule")) } }
        compose.onNodeWithTag("rule").assertHeightIsEqualTo(1.dp).assertWidthIsEqualTo(200.dp)
    }

    @Test
    fun itIsInvisibleToAccessibilityServices() {
        compose.setQeetrix { QeetrixSeparator() }
        // Nothing but the root: no text, no role, no description for TalkBack to stop on.
        compose
            .onAllNodes(
                !isRoot() and
                    SemanticsMatcher("has any semantics") {
                        it.config.contains(SemanticsProperties.Role) ||
                            it.config.contains(SemanticsProperties.ContentDescription)
                    },
            ).assertCountEquals(0)
    }
}
