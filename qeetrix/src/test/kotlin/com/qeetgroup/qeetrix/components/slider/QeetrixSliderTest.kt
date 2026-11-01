package com.qeetgroup.qeetrix.components.slider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Slider: set from TalkBack and by touch, the thumb where its value is in either direction, a range's two thumbs. */
@RunWith(RobolectricTestRunner::class)
class QeetrixSliderTest {
    @get:Rule
    val compose = createComposeRule()

    private var volume by mutableFloatStateOf(40f)

    private fun slider(direction: LayoutDirection = LayoutDirection.Ltr) {
        compose.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides direction) {
                QeetrixTheme(QeetrixThemeMode.Light) {
                    Box(Modifier.width(300.dp)) {
                        QeetrixSlider(volume, { volume = it }, label = "Alert volume", modifier = Modifier.testTag("slider"), step = 10f)
                    }
                }
            }
        }
    }

    /** The thumb's centre as a share of the slider's width, from the left edge. */
    private fun thumbFromLeft(): Float {
        val slider = compose.onNodeWithTag("slider").getBoundsInRoot()
        val thumb = compose.onNodeWithContentDescription("Alert volume").getBoundsInRoot()
        return (((thumb.left + thumb.right) / 2) - slider.left) / (slider.right - slider.left)
    }

    @Test
    fun talkBackSetsTheValueToItsStep() {
        slider()
        compose
            .onNodeWithContentDescription("Alert volume")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "40"))
            .performSemanticsAction(SemanticsActions.SetProgress) { it(73f) }
        assertEquals(70f, volume)
    }

    @Test
    fun theThumbIsAtItsValueFromTheStart() {
        slider()
        assertEquals(0.4f, thumbFromLeft(), 0.08f)
    }

    /** Found on a device: in right to left the indicator ended at 40 % from the right while the thumb stood at 40 % from the left. */
    @Test
    fun inRightToLeftTheThumbIsAtItsValueFromTheRight() {
        slider(LayoutDirection.Rtl)
        assertEquals(0.6f, thumbFromLeft(), 0.08f)
    }

    @Test
    fun aTapOnTheTrackSetsTheValueFromTheStart() {
        slider(LayoutDirection.Rtl)
        // A quarter of the way from the left is three quarters from the start in right to left.
        compose
            .onNodeWithTag(
                "slider",
            ).performTouchInput {
                click(
                    androidx.compose.ui.geometry
                        .Offset(width * 0.25f, height - 24.dp.toPx()),
                )
            }
        assertEquals(70f, volume, 10f)
    }

    @Test
    fun aRangeHasTwoNamedThumbs() {
        var hours by mutableStateOf(9f..18f)
        compose.setContent {
            QeetrixTheme(
                QeetrixThemeMode.Light,
            ) { QeetrixRangeSlider(hours, { hours = it }, label = "Quiet hours", valueRange = 0f..24f, step = 1f) }
        }
        compose.onNodeWithContentDescription("Quiet hours, minimum").performSemanticsAction(SemanticsActions.SetProgress) { it(7f) }
        compose.onNodeWithContentDescription("Quiet hours, maximum").performSemanticsAction(SemanticsActions.SetProgress) { it(20.4f) }
        assertEquals(7f..20f, hours)
    }
}
