package com.qeetgroup.qeetrix.components.button

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QeetrixIconButtonTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun anIconButtonIsNamedByItsDescriptionNotItsIcon() {
        var clicks = 0
        compose.setQeetrix {
            QeetrixIconButton(QeetrixIconName.X, contentDescription = "Close", onClick = { clicks++ }, size = QeetrixIconButtonSize.Xs)
        }
        compose
            .onNodeWithContentDescription("Close")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
            .assertHeightIsAtLeast(48.dp)
            .performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun aLoadingIconButtonKeepsItsSquareAndIgnoresActivation() {
        var clicks = 0
        var loading by mutableStateOf(false)
        compose.setQeetrix {
            QeetrixIconButton(
                QeetrixIconName.Send,
                contentDescription = "Send",
                onClick = { clicks++ },
                loading = loading,
            )
        }
        val before = compose.onNodeWithContentDescription("Send").getUnclippedBoundsInRoot()
        loading = true
        compose.onNodeWithContentDescription("Send").performClick()
        assertEquals(0, clicks)
        assertEquals(before, compose.onNodeWithContentDescription("Send").getUnclippedBoundsInRoot())
    }

    @Test
    fun aDisabledIconButtonCannotBeActivated() {
        var clicks = 0
        compose.setQeetrix {
            QeetrixIconButton(QeetrixIconName.X, contentDescription = "Close", onClick = { clicks++ }, enabled = false)
        }
        compose.onNodeWithContentDescription("Close").assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
    }

    @Test
    fun sizesAreTheWebs() {
        assertEquals(listOf("icon", "icon-xs", "icon-sm", "icon-lg"), QeetrixIconButtonSize.entries.map { it.value })
    }
}
