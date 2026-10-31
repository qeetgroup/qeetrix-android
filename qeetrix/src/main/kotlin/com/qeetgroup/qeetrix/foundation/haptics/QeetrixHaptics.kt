package com.qeetgroup.qeetrix.foundation.haptics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Qeetrix's feedback roles: what a moment means, which each platform turns into its own haptic (iOS its feedback
 * generators, Android its haptic constants). Components ask for a role, never a vibration pattern, so every Qeetrix
 * app feels the same and follows the system: with touch feedback turned off in Settings, nothing is felt.
 *
 * `value` is the name shared with qeetrix-ios.
 */
public enum class QeetrixHapticRole(
    public val value: String,
) {
    /** A choice moved: a switch toggled, a segment or a date chosen, a picker's wheel stepping. */
    Selection("selection"),

    /** Something finished as hoped: a refresh done, a form saved. */
    Success("success"),

    /** Something needs a second look before it goes on: a swipe past the point where it will delete. */
    Warning("warning"),

    /** Something failed: a copy that couldn't reach the clipboard, a wrong code. */
    Error("error"),

    /** A light tap that confirms a touch landed: a code copied, a swipe action revealed, a pull reaching its threshold. */
    LightImpact("light-impact"),
}

/**
 * Performs [QeetrixHapticRole]s on Android through Compose's [HapticFeedback], which goes through the view's
 * `performHapticFeedback`, so the system's touch-feedback setting is always respected.
 *
 * | Role | Android |
 * |:--|:--|
 * | [QeetrixHapticRole.Selection] | `SEGMENT_TICK` |
 * | [QeetrixHapticRole.Success] | `CONFIRM` |
 * | [QeetrixHapticRole.Warning] | `GESTURE_THRESHOLD_ACTIVATE` |
 * | [QeetrixHapticRole.Error] | `REJECT` |
 * | [QeetrixHapticRole.LightImpact] | `KEYBOARD_TAP` |
 *
 * ```
 * val haptics = rememberQeetrixHaptics()
 * QeetrixButton("Save", onClick = { save(); haptics.perform(QeetrixHapticRole.Success) })
 * ```
 */
@Stable
public class QeetrixHaptics internal constructor(
    private val feedback: HapticFeedback,
) {
    /** Plays [role]'s haptic, or nothing where the system has touch feedback turned off. */
    public fun perform(role: QeetrixHapticRole) {
        feedback.performHapticFeedback(role.platformType())
    }
}

/** The [QeetrixHaptics] of the current view. */
@Composable
public fun rememberQeetrixHaptics(): QeetrixHaptics {
    val feedback = LocalHapticFeedback.current
    return remember(feedback) { QeetrixHaptics(feedback) }
}

/** The Android constant each role plays (see [QeetrixHaptics]). */
internal fun QeetrixHapticRole.platformType(): HapticFeedbackType =
    when (this) {
        QeetrixHapticRole.Selection -> HapticFeedbackType.SegmentTick
        QeetrixHapticRole.Success -> HapticFeedbackType.Confirm
        QeetrixHapticRole.Warning -> HapticFeedbackType.GestureThresholdActivate
        QeetrixHapticRole.Error -> HapticFeedbackType.Reject
        QeetrixHapticRole.LightImpact -> HapticFeedbackType.KeyboardTap
    }
