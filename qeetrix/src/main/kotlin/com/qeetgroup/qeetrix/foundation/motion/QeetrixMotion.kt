package com.qeetgroup.qeetrix.foundation.motion

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixMotionTokens

/** Motion durations (`motion.duration.*`), in milliseconds — the unit Compose animation specs take. */
public object QeetrixDuration {
    /** `motion.duration.instant` */
    public val Instant: Int = QeetrixMotionTokens.DurationInstant

    /** `motion.duration.fast` */
    public val Fast: Int = QeetrixMotionTokens.DurationFast

    /** `motion.duration.normal` */
    public val Normal: Int = QeetrixMotionTokens.DurationNormal

    /** `motion.duration.slow` */
    public val Slow: Int = QeetrixMotionTokens.DurationSlow

    /** `motion.duration.deliberate` */
    public val Deliberate: Int = QeetrixMotionTokens.DurationDeliberate
}

/** Motion easing curves (`motion.easing.*`): the web's `cubic-bezier()` values. */
public object QeetrixEasing {
    /** `motion.easing.standard` — most transitions. */
    public val Standard: Easing = QeetrixMotionTokens.EasingStandard

    /** `motion.easing.emphasized` */
    public val Emphasized: Easing = QeetrixMotionTokens.EasingEmphasized

    /** `motion.easing.enter` — elements arriving (decelerate). */
    public val Enter: Easing = QeetrixMotionTokens.EasingEnter

    /** `motion.easing.exit` — elements leaving (accelerate). */
    public val Exit: Easing = QeetrixMotionTokens.EasingExit

    /** `motion.easing.linear` */
    public val Linear: Easing = QeetrixMotionTokens.EasingLinear
}

/** Motion behaviour, including the reduced-motion tokens. */
public object QeetrixMotion {
    /** `motion.reduced.duration` — replaces every duration when the user has asked for less motion. */
    public val ReducedDuration: Int = QeetrixMotionTokens.ReducedDuration

    /** `motion.reduced.easing` */
    public val ReducedEasing: Easing = QeetrixMotionTokens.ReducedEasing

    /**
     * The animation spec for an easing and duration, honouring reduced motion the way the web does:
     * with [reduceMotion] on, the `motion.reduced.*` tokens replace both. A zero duration snaps — the
     * change happens without animation.
     */
    public fun <T> spec(
        easing: Easing,
        durationMillis: Int,
        reduceMotion: Boolean,
    ): FiniteAnimationSpec<T> {
        val (curve, length) = if (reduceMotion) ReducedEasing to ReducedDuration else easing to durationMillis
        return if (length > 0) tween(durationMillis = length, easing = curve) else snap()
    }
}

/**
 * [QeetrixMotion.spec] for the current theme's reduced-motion setting ([QeetrixTheme.reduceMotion]).
 *
 * ```
 * val alpha by animateFloatAsState(target, qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Normal))
 * ```
 */
@Composable
@ReadOnlyComposable
public fun <T> qeetrixAnimationSpec(
    easing: Easing,
    durationMillis: Int,
): FiniteAnimationSpec<T> = QeetrixMotion.spec(easing, durationMillis, QeetrixTheme.reduceMotion)

/**
 * Whether the user has turned animations off (Settings › Accessibility › Remove animations, which sets
 * the animator duration scale to 0). Updates live when the setting changes.
 */
@Composable
internal fun rememberSystemReduceMotion(): Boolean {
    val resolver = LocalContext.current.contentResolver
    var reduce by remember(resolver) { mutableStateOf(readReduceMotion(resolver)) }
    DisposableEffect(resolver) {
        val observer =
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    reduce = readReduceMotion(resolver)
                }
            }
        resolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE), false, observer)
        onDispose { resolver.unregisterContentObserver(observer) }
    }
    return reduce
}

private fun readReduceMotion(resolver: ContentResolver): Boolean =
    Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
