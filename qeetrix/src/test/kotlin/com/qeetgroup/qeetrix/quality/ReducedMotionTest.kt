package com.qeetgroup.qeetrix.quality

import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonText
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeletonVariant
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerSize
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

/**
 * With Remove animations on (the animator duration scale at 0), none of Qeetrix's endless animations moves: the
 * skeletons' pulse stands still, and the spinners and indeterminate progress — each one on its own — neither turn nor
 * slide (they may fade, as the web's reduced forms do; Compose itself skips even that on a device, where the setting
 * also scales every animation's duration to 0). With animations on, every one of them changes, and the spinners and
 * the progress move. Compared pixel by pixel 200, 500 and 900 ms in, with the test clock's durations unscaled, so what
 * is proven is Qeetrix's own handling of the setting.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReducedMotionTest {
    @get:Rule
    val compose = createComposeRule()

    private fun animationScale(scale: Float) {
        Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, scale)
    }

    /** An endless animation, and whether its reduced form may fade (a spinner, indeterminate progress) or holds still. */
    private class Endless(
        val name: String,
        val fadesWhenReduced: Boolean,
        val content: @Composable () -> Unit,
    )

    private val endless =
        listOf(
            Endless("spinner", fadesWhenReduced = true) { QeetrixSpinner(size = QeetrixSpinnerSize.Xl) },
            Endless("loading button", fadesWhenReduced = true) { QeetrixButton("Saving", onClick = {}, loading = true) },
            Endless("loading icon button", fadesWhenReduced = true) {
                QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {}, variant = QeetrixButtonVariant.Solid, loading = true)
            },
            Endless("data state", fadesWhenReduced = true) { QeetrixDataState(loading = true) {} },
            Endless("indeterminate progress", fadesWhenReduced = true) { QeetrixProgress(null, label = "Preparing") },
            Endless("skeleton", fadesWhenReduced = false) { QeetrixSkeleton(Modifier.width(160.dp)) },
            Endless("circle skeleton", fadesWhenReduced = false) { QeetrixSkeleton(variant = QeetrixSkeletonVariant.Circle) },
            Endless("skeleton text", fadesWhenReduced = false) { QeetrixSkeletonText(lines = 2) },
        )

    /** Each animation's frames 200, 500 and 900 ms in. */
    private fun frames(): Map<String, List<PixelMap>> {
        compose.mainClock.autoAdvance = false
        compose.setContent {
            QeetrixTheme {
                Column(Modifier.width(200.dp).background(QeetrixTheme.colors.surfaceCanvas)) {
                    for (each in endless) Box(Modifier.testTag(each.name).padding(4.dp)) { each.content() }
                }
            }
        }
        val frames = endless.associate { it.name to mutableListOf<PixelMap>() }
        for (step in listOf(200L, 300L, 400L)) {
            compose.mainClock.advanceTimeBy(step)
            for (each in endless) frames.getValue(each.name) += capture(each.name)
        }
        return frames
    }

    private fun capture(tag: String): PixelMap = compose.onNodeWithTag(tag).captureToImage().toPixelMap()

    @Test
    fun withAnimationsOffNothingMoves() {
        animationScale(0f)
        val frames = frames()
        val moved = mutableListOf<String>()
        for (each in endless) {
            val (first, later) = frames.getValue(each.name).let { it.first() to it.drop(1) }
            // A fade is allowed where the web fades; nothing may turn or slide, and a skeleton holds still.
            val wrong = later.any { if (each.fadesWhenReduced) first.moved(it) else !first.samePixels(it) }
            if (wrong) moved += each.name
        }
        assertEquals("moved with animations off", emptyList<String>(), moved)
    }

    @Test
    fun withAnimationsOnTheyMove() {
        animationScale(1f)
        val frames = frames()
        val still = mutableListOf<String>()
        for (each in endless) {
            val steps = frames.getValue(each.name).zipWithNext()
            // Every one changes; the spinners and the progress segment also move (the skeletons only pulse).
            val changed = steps.any { (before, after) -> !before.samePixels(after) }
            val moved = steps.any { (before, after) -> before.moved(after) }
            if (!changed || (each.fadesWhenReduced && !moved)) still += each.name
        }
        assertEquals("did not move with animations on", emptyList<String>(), still)
    }

    /** The motion check catches a shape that moves 8 dp and lets one that fades to half pass. */
    @Test
    fun theMotionCheckTellsMovementFromAFade() {
        var step by mutableStateOf(0)
        var faded by mutableStateOf(1f)
        compose.setContent {
            QeetrixTheme {
                Column(Modifier.background(QeetrixTheme.colors.surfaceCanvas)) {
                    Box(Modifier.testTag("moving").size(48.dp)) {
                        Box(
                            Modifier
                                .offset {
                                    IntOffset(
                                        (8 + 8 * step).dp.roundToPx(),
                                        8.dp.roundToPx(),
                                    )
                                }.size(24.dp)
                                .background(Color.Black),
                        )
                    }
                    Box(Modifier.testTag("fading").size(48.dp)) {
                        Box(
                            Modifier
                                .offset(8.dp, 8.dp)
                                .size(24.dp)
                                .alpha(faded)
                                .background(Color.Black),
                        )
                    }
                }
            }
        }
        val first = capture("moving")
        compose.runOnIdle { step = 1 }
        assertTrue("an 8 dp move is not seen as motion", first.moved(capture("moving")))

        val opaque = capture("fading")
        compose.runOnIdle { faded = 0.5f }
        val half = capture("fading")
        assertFalse("a fade is seen as motion", opaque.moved(half))
        assertFalse("a fade is not seen at all", opaque.samePixels(half))
    }

    private fun PixelMap.samePixels(other: PixelMap): Boolean {
        if (width != other.width || height != other.height) return false
        for (x in 0 until width) for (y in 0 until height) if (this[x, y] != other[x, y]) return false
        return true
    }

    /**
     * Whether something moved between this frame and [other]: some pixels gained ink (moved away from the frame's
     * corner colour, the background) while others lost it. A fade changes every pixel's ink the same way; a turn or a
     * slide adds ink where the shape arrives and takes it where it leaves.
     */
    private fun PixelMap.moved(other: PixelMap): Boolean {
        if (width != other.width || height != other.height) return true
        val background = this[0, 0]

        fun ink(color: Color): Float =
            abs(color.red - background.red) + abs(color.green - background.green) + abs(color.blue - background.blue)
        var gained = 0
        var lost = 0
        for (x in 0 until width) {
            for (y in 0 until height) {
                val change = ink(other[x, y]) - ink(this[x, y])
                if (change > INK_STEP) gained++
                if (change < -INK_STEP) lost++
            }
        }
        return gained > NOISE_PIXELS && lost > NOISE_PIXELS
    }

    private companion object {
        /** A change of ink worth counting: a tenth of black on white, summed over the channels. */
        const val INK_STEP = 0.3f

        /** Pixels either way that antialiasing can account for. */
        const val NOISE_PIXELS = 4
    }
}
