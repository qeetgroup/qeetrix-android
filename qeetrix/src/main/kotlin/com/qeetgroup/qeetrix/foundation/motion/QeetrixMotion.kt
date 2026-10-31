package com.qeetgroup.qeetrix.foundation.motion

import android.content.ContentResolver
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixMotionTokens

/**
 * A spring (`motion.spring.*`) with mass 1, as Jetpack Compose defines one — the motion
 * `@qeetrix/tokens` gives native platforms beside the web's durations and curves.
 */
@Immutable
public class QeetrixSpring internal constructor(
    /** `motion.spring.<name>.stiffness` */
    public val stiffness: Float,
    /** `motion.spring.<name>.damping-ratio` */
    public val dampingRatio: Float,
) {
    /** This spring as a Compose animation spec. */
    public fun <T> spec(visibilityThreshold: T? = null): SpringSpec<T> =
        spring(dampingRatio = dampingRatio, stiffness = stiffness, visibilityThreshold = visibilityThreshold)

    override fun equals(other: Any?): Boolean = other is QeetrixSpring && stiffness == other.stiffness && dampingRatio == other.dampingRatio

    override fun hashCode(): Int = 31 * stiffness.hashCode() + dampingRatio.hashCode()

    override fun toString(): String = "QeetrixSpring(stiffness=$stiffness, dampingRatio=$dampingRatio)"
}

/**
 * Motion (`motion.*`): durations in milliseconds — the unit Compose animation specs take — easing
 * curves (the web's `cubic-bezier()`s) and springs.
 *
 * Motion is productive: quick, never decorative, never bouncing or scaling (design rules §18). Hover,
 * press and colour changes take [DurationFast] with [EasingStandard]; a switch thumb or tab bar
 * [DurationNormal]; menus and popovers enter in [DurationNormal] with [EasingEnter]; dialogs and sheets
 * in [DurationSlow]; anything leaving one step faster, with [EasingExit].
 */
public object QeetrixMotion {
    /** `motion.duration.instant` */
    public val DurationInstant: Int = QeetrixMotionTokens.DurationInstant

    /** `motion.duration.fast` */
    public val DurationFast: Int = QeetrixMotionTokens.DurationFast

    /** `motion.duration.normal` */
    public val DurationNormal: Int = QeetrixMotionTokens.DurationNormal

    /** `motion.duration.slow` */
    public val DurationSlow: Int = QeetrixMotionTokens.DurationSlow

    /** `motion.duration.slower` */
    public val DurationSlower: Int = QeetrixMotionTokens.DurationSlower

    /** `motion.easing.standard` — something on screen changes size, position or colour. */
    public val EasingStandard: Easing = QeetrixMotionTokens.EasingStandard

    /** `motion.easing.enter` — something arrives and decelerates to rest. */
    public val EasingEnter: Easing = QeetrixMotionTokens.EasingEnter

    /** `motion.easing.exit` — something leaves and accelerates away. */
    public val EasingExit: Easing = QeetrixMotionTokens.EasingExit

    /** `motion.easing.emphasized` — large moves: drawers, screen transitions. */
    public val EasingEmphasized: Easing = QeetrixMotionTokens.EasingEmphasized

    /** `motion.easing.linear` — progress and other continuous motion. */
    public val EasingLinear: Easing = QeetrixMotionTokens.EasingLinear

    /** `motion.spring.snappy` */
    public val SpringSnappy: QeetrixSpring =
        QeetrixSpring(QeetrixMotionTokens.SpringSnappyStiffness, QeetrixMotionTokens.SpringSnappyDampingRatio)

    /** `motion.spring.gentle` */
    public val SpringGentle: QeetrixSpring =
        QeetrixSpring(QeetrixMotionTokens.SpringGentleStiffness, QeetrixMotionTokens.SpringGentleDampingRatio)

    /**
     * The animation spec for an easing and duration, honouring reduced motion the way the web does:
     * under `prefers-reduced-motion: reduce` every `--qx-motion-duration-*` becomes `0ms`, so with
     * [reduceMotion] on the change snaps — it happens without animation. So does a zero duration.
     */
    public fun <T> spec(
        easing: Easing,
        durationMillis: Int,
        reduceMotion: Boolean,
    ): FiniteAnimationSpec<T> = if (reduceMotion || durationMillis <= 0) snap() else tween(durationMillis = durationMillis, easing = easing)
}

/**
 * [QeetrixMotion.spec] for the current theme's reduced-motion setting ([QeetrixTheme.reduceMotion]).
 *
 * ```
 * val alpha by animateFloatAsState(target, qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal))
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
