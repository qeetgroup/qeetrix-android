package com.qeetgroup.qeetrix.foundation.haptics

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Qeetrix's feedback roles, each played as its Android haptic through the view's HapticFeedback. */
@RunWith(RobolectricTestRunner::class)
class QeetrixHapticsTest {
    @get:Rule
    val compose = createComposeRule()

    private class Recorder : HapticFeedback {
        val played = mutableListOf<HapticFeedbackType>()

        override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
            played += hapticFeedbackType
        }
    }

    @Test
    fun eachRolePlaysItsPlatformHaptic() {
        val recorder = Recorder()
        lateinit var haptics: QeetrixHaptics
        compose.setContent { CompositionLocalProvider(LocalHapticFeedback provides recorder) { haptics = rememberQeetrixHaptics() } }
        compose.runOnIdle { QeetrixHapticRole.entries.forEach(haptics::perform) }
        assertEquals(
            listOf(
                HapticFeedbackType.SegmentTick,
                HapticFeedbackType.Confirm,
                HapticFeedbackType.GestureThresholdActivate,
                HapticFeedbackType.Reject,
                HapticFeedbackType.KeyboardTap,
            ),
            recorder.played,
        )
    }

    @Test
    fun rolesCarryTheSharedNames() {
        assertEquals(listOf("selection", "success", "warning", "error", "light-impact"), QeetrixHapticRole.entries.map { it.value })
    }
}
