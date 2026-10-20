package com.qeetgroup.qeetrix.quality

import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.GraphicsMode

/**
 * With Remove animations on (the animator duration scale at 0), Qeetrix's endless animations — the spinner,
 * the skeleton's shimmer, indeterminate progress — draw the same frame however much time passes; with
 * animations on, they move. Compared pixel by pixel across 700 ms of virtual time.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReducedMotionTest {
    @get:Rule
    val compose = createComposeRule()

    private fun animationScale(scale: Float) {
        Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, scale)
    }

    private fun framesDiffer(): Boolean {
        compose.mainClock.autoAdvance = false
        compose.setContent {
            QeetrixTheme {
                Column(Modifier.width(200.dp)) {
                    QeetrixSpinner()
                    QeetrixSkeleton(Modifier.fillMaxWidth().height(24.dp))
                    QeetrixProgress(null, label = "Preparing")
                }
            }
        }
        compose.mainClock.advanceTimeBy(100)
        val before = compose.onRoot().captureToImage()
        compose.mainClock.advanceTimeBy(700)
        val after = compose.onRoot().captureToImage()
        return !before.samePixels(after)
    }

    @Test
    fun withAnimationsOffNothingMoves() {
        animationScale(0f)
        assertFalse("a frame changed with animations off", framesDiffer())
    }

    @Test
    fun withAnimationsOnTheyMove() {
        animationScale(1f)
        assertTrue("nothing moved with animations on", framesDiffer())
    }

    private fun ImageBitmap.samePixels(other: ImageBitmap): Boolean {
        if (width != other.width || height != other.height) return false
        val a = toPixelMap()
        val b = other.toPixelMap()
        for (x in 0 until width) for (y in 0 until height) if (a[x, y] != b[x, y]) return false
        return true
    }
}
