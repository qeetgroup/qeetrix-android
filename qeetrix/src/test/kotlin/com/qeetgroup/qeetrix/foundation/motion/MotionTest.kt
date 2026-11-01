package com.qeetgroup.qeetrix.foundation.motion

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.ui.test.junit4.createComposeRule
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.theme.QeetrixThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class MotionTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun aSpecUsesTheTokenDurationAndEasing() {
        val spec = QeetrixMotion.spec<Float>(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal, reduceMotion = false) as TweenSpec
        assertEquals(200, spec.durationMillis)
        assertEquals(QeetrixMotion.EasingStandard, spec.easing)
    }

    @Test
    fun reducedMotionMakesEveryDurationZero() {
        // Under prefers-reduced-motion every --qx-motion-duration-* is 0ms (tokens.css), so the change is immediate.
        assertTrue(QeetrixMotion.spec<Float>(QeetrixMotion.EasingEnter, QeetrixMotion.DurationSlow, reduceMotion = true) is SnapSpec)
        assertTrue(QeetrixMotion.spec<Float>(QeetrixMotion.EasingEnter, QeetrixMotion.DurationInstant, reduceMotion = false) is SnapSpec)
        assertEquals(LinearEasing, QeetrixMotion.EasingLinear)
    }

    @Test
    fun removeAnimationsTurnsOnReducedMotion() {
        val resolver = RuntimeEnvironment.getApplication().contentResolver
        Settings.Global.putFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 0f)
        var reduce: Boolean? = null
        compose.setContent { QeetrixTheme(QeetrixThemeMode.Light) { reduce = QeetrixTheme.reduceMotion } }
        compose.waitForIdle()
        assertEquals(true, reduce)
    }

    @Test
    fun animationsOnMeansFullMotion() {
        Settings.Global.putFloat(RuntimeEnvironment.getApplication().contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        var reduce: Boolean? = null
        compose.setContent { QeetrixTheme(QeetrixThemeMode.Light) { reduce = QeetrixTheme.reduceMotion } }
        compose.waitForIdle()
        assertEquals(false, reduce)
    }
}
