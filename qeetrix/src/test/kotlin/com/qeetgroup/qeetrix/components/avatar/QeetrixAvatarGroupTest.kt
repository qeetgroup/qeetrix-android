package com.qeetgroup.qeetrix.components.avatar

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import com.qeetgroup.qeetrix.components.setQeetrix
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

/** AvatarGroup: one image named in full, the count of the rest as +N. */
@RunWith(RobolectricTestRunner::class)
class QeetrixAvatarGroupTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun theGroupIsOneImageNamedInFull() {
        compose.setQeetrix {
            QeetrixAvatarGroup(
                listOf("Priya Sharma", "Rahul Verma", "Ananya Iyer").map { QeetrixAvatarPerson(it) },
                total = 412,
            )
        }
        compose.onNodeWithContentDescription("412 people: Priya Sharma, Rahul Verma, Ananya Iyer, and 409 more").assertExists()
        compose.onNodeWithText("+409", useUnmergedTree = true).assertExists()
    }

    /** Found on a device: at 200 % text the count grew into a bubble larger than the avatars beside it. */
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun theCountIsDrawnLikeTheInitialsAtAFontScaleOfOne() {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 2f)) {
                QeetrixTheme(QeetrixThemeMode.Light) {
                    QeetrixAvatarGroup(listOf("Priya Sharma", "Rahul Verma", "Ananya Iyer", "Vikram Singh").map { QeetrixAvatarPerson(it) })
                }
            }
        }
        val count = compose.onNodeWithText("+1", useUnmergedTree = true).getBoundsInRoot()
        assertTrue("the count is ${count.bottom - count.top} high", count.bottom - count.top <= QeetrixAvatarSize.Sm.side)
    }
}
