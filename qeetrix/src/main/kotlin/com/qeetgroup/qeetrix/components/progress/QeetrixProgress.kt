package com.qeetgroup.qeetrix.components.progress

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import java.text.NumberFormat

/** The indicator's colour while a task advances (`value` is `@qeetrix/react`'s Progress `tone`). */
public enum class QeetrixProgressTone(
    public val value: String,
) {
    /** `color.bg.brand.bold`, while it runs. The default. */
    Brand("brand"),

    /** `color.bg.success.bold`, once it is done. */
    Success("success"),

    /** `color.bg.danger.bold`, if it failed. */
    Danger("danger"),
}

/** The indicator's colour of a measurement (`value` is `@qeetrix/react`'s Meter `tone`), as Badge's tones. */
public enum class QeetrixMeterTone(
    public val value: String,
) {
    /** `color.bg.brand.bold`. The default. */
    Brand("brand"),

    /** `color.bg.neutral.bold`: a plain amount. */
    Neutral("neutral"),

    /** `color.bg.info.bold`: a plain amount. */
    Info("info"),

    /** `color.bg.success.bold`: how it stands — well. */
    Success("success"),

    /**
     * How it stands — nearly full: `color.border.warning` in light and `color.bg.warning.bold` in dark, so it keeps
     * 3:1 against the track (`--qx-progress-warning`).
     */
    Warning("warning"),

    /** `color.bg.danger.bold`: how it stands — over. */
    Danger("danger"),
}

/**
 * A task advancing — `@qeetrix/react`'s Progress: an upload, an import. A 4 dp track with square ends
 * (`color.border.default` in light, `color.surface.sunken` in dark), filled from its start by the [tone]'s indicator to
 * [value] within [min]…[max]; with [value] `null` (unknown), a 30 % segment slides along the track every 1.6 s from the
 * reading start, and under reduced motion the full bar fades instead, so the wait stays visible. The fill moves to a new value over `motion.duration.normal`.
 *
 * - [label] (field-label style: `label.sm`, `color.text.subtle`) sits above the track and names it for accessibility
 *   services; without one, pass a [contentDescription]. With [showValue], the value is written at the header's end in
 *   `label.sm` with tabular figures.
 * - **The value** is its percentage of the range in the device's locale, unless [format] writes the value itself
 *   (`NumberFormat` for "2.4 GB", say) — the web's `format`. Values outside the range are clamped; NaN is rejected.
 * - Accessibility services read it as a progress bar with its value (and the formatted text when there is a
 *   [format]); an unknown value is read as in progress.
 *
 * ```
 * QeetrixProgress(uploaded, max = total, label = "Uploading report.pdf", showValue = true)
 * QeetrixProgress(null, label = "Preparing export")
 * ```
 */
@Composable
public fun QeetrixProgress(
    value: Float?,
    modifier: Modifier = Modifier,
    min: Float = 0f,
    max: Float = 100f,
    label: String? = null,
    showValue: Boolean = false,
    tone: QeetrixProgressTone = QeetrixProgressTone.Brand,
    format: NumberFormat? = null,
    contentDescription: String? = null,
) {
    require(value == null || !value.isNaN()) { "A progress value is a number from min to max, or null when it is unknown." }
    require(max > min) { "max ($max) must be greater than min ($min)." }
    val clamped = value?.coerceIn(min, max)
    val written = clamped?.let { rememberWritten(it, min, max, format) }
    val name = label ?: contentDescription
    val colors = QeetrixTheme.colors
    Bar(
        label = label,
        written = if (showValue) written else null,
        modifier =
            modifier
                .then(if (clamped != null) Modifier.progressSemantics(clamped, min..max) else Modifier.progressSemantics())
                .semantics(mergeDescendants = true) {
                    if (name != null) this.contentDescription = name
                    if (format != null && written != null) stateDescription = written
                },
    ) {
        Track(clamped?.let { (it - min) / (max - min) }, tone.indicator(colors), target = null)
    }
}

/**
 * A measurement within a range — `@qeetrix/react`'s Meter: storage used, a quota spent. It looks like
 * [QeetrixProgress] and is not a task: its [tone] says how it stands, and an optional [target] draws a 2 dp tick on
 * the track ("80 % of people with two-step") and joins what is read out ("40%, target 80%"), written as the value is.
 *
 * Accessibility services read its [label] (or [contentDescription]) and its written value; Android has no meter role,
 * so it is not announced as a progress bar.
 *
 * ```
 * QeetrixMeter(used, max = quota, label = "Storage", showValue = true, tone = QeetrixMeterTone.Warning)
 * ```
 */
@Composable
public fun QeetrixMeter(
    value: Float,
    modifier: Modifier = Modifier,
    min: Float = 0f,
    max: Float = 100f,
    label: String? = null,
    showValue: Boolean = false,
    tone: QeetrixMeterTone = QeetrixMeterTone.Brand,
    target: Float? = null,
    format: NumberFormat? = null,
    contentDescription: String? = null,
) {
    require(!value.isNaN() && target?.isNaN() != true) { "A meter's value and target are numbers from min to max." }
    require(max > min) { "max ($max) must be greater than min ($min)." }
    val clamped = value.coerceIn(min, max)
    val written = rememberWritten(clamped, min, max, format)
    val share = target?.let { ((it - min) / (max - min)).coerceIn(0f, 1f) }
    val writtenTarget = target?.let { rememberWritten(it.coerceIn(min, max), min, max, format) }
    val name = label ?: contentDescription
    val colors = QeetrixTheme.colors
    // meter.withTarget: "{value}, target {target}"
    val state = if (writtenTarget != null) stringResource(R.string.qx_meter_with_target, written, writtenTarget) else written
    Bar(
        label = label,
        written = if (showValue) written else null,
        modifier =
            modifier.semantics(mergeDescendants = true) {
                if (name != null) this.contentDescription = name
                stateDescription = state
            },
    ) {
        Track((clamped - min) / (max - min), tone.indicator(colors, QeetrixTheme.isDark), target = share)
    }
}

/** `.qx-progress`: the header over the track, `space.gap.xs` apart. */
@Composable
private fun Bar(
    label: String?,
    written: String?,
    modifier: Modifier,
    track: @Composable () -> Unit,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
        if (label != null || written != null) {
            // .qx-progress-header: space-between, baseline, gap space.gap.md
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
                BasicText(
                    label.orEmpty(),
                    Modifier.weight(1f).alignByBaseline(),
                    style = typography.labelSm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true), // .qx-progress-label
                )
                if (written != null) {
                    BasicText(
                        written,
                        Modifier.alignByBaseline(),
                        // .qx-progress-value: label.sm, tabular-nums, color.text.default
                        style = typography.labelSm.copy(color = colors.textDefault, fontFeatureSettings = "tnum"),
                    )
                }
            }
        }
        track()
    }
}

/**
 * `.qx-progress-track`: 4 dp of `--qx-progress-track`, square ends, with the indicator from the start to
 * [fraction] — or, with `null`, the indeterminate segment — and a [target] tick. The track is `color.border.default` in
 * light and `color.surface.sunken` in dark, where every tone's fill keeps 3:1 against it.
 */
@Composable
private fun Track(
    fraction: Float?,
    indicator: Color,
    target: Float?,
) {
    val colors = QeetrixTheme.colors
    val track = progressTrack(colors, QeetrixTheme.isDark)
    val tick = colors.textDefault // .qx-meter-target background: color.text.default
    val reduce = QeetrixTheme.reduceMotion
    val shown =
        if (fraction != null) {
            // .qx-progress-indicator: transition inline-size over normal on standard
            animateFloatAsState(
                fraction,
                qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationNormal),
                label = "progress",
            )
        } else {
            null
        }
    val sweep = if (fraction == null) rememberIndeterminate(reduce) else null
    Box(
        Modifier
            .fillMaxWidth()
            .height(TrackHeight)
            .drawBehind {
                val rtl = layoutDirection == LayoutDirection.Rtl
                drawRect(track)
                // overflow: hidden — the indicator stays inside the track; a target tick reaches past it.
                clipRect {
                    if (shown != null) {
                        drawFromStart(indicator, 0f, size.width * shown.value, rtl)
                    } else if (sweep != null) {
                        if (reduce) {
                            // prefers-reduced-motion: inline-size 100%, fading to .35 and back
                            drawRect(indicator, alpha = sweep.value)
                        } else {
                            // inline-size 30%, translate -100% → 334% of itself from the start side: qx-progress-slide,
                            // and qx-progress-slide-rtl (100% → -334%) in right to left
                            val segment = size.width * SEGMENT
                            drawFromStart(indicator, segment * sweep.value, segment, rtl)
                        }
                    }
                }
                if (target != null) {
                    // .qx-meter-target: inline-size 2px, inset-block -3px, inset-inline-start calc(share − 1px)
                    val width = TickWidth.toPx()
                    val reach = TickReach.toPx()
                    drawFromStart(tick, size.width * target - width / 2, width, rtl, top = -reach, height = size.height + 2 * reach)
                }
            },
    )
}

/** A rectangle [width] wide, [start] from the track's inline start (its right edge in right to left). */
private fun DrawScope.drawFromStart(
    color: Color,
    start: Float,
    width: Float,
    rtl: Boolean,
    top: Float = 0f,
    height: Float = size.height,
) {
    val left = if (rtl) size.width - start - width else start
    drawRect(color, Offset(left, top), Size(width, height))
}

/**
 * The indeterminate motion: the segment's offset in its own widths, -1 → 3.34 over `4 × motion.duration.slower` on
 * `motion.easing.standard`, restarting (`qx-progress-slide`); under reduced motion, the full bar's opacity, 1 → .35
 * and back over 1.6 s (`qx-progress-fade`, ease-in-out).
 */
@Composable
private fun rememberIndeterminate(reduce: Boolean): State<Float> {
    val transition = rememberInfiniteTransition(label = "progress-indeterminate")
    return if (reduce) {
        transition.animateFloat(
            initialValue = 1f,
            targetValue = FADE_OPACITY,
            animationSpec = infiniteRepeatable(tween(FADE_MILLIS / 2, easing = FastOutSlowInEasing), RepeatMode.Reverse),
            label = "progress-fade",
        )
    } else {
        transition.animateFloat(
            initialValue = SLIDE_FROM,
            targetValue = SLIDE_TO,
            animationSpec =
                infiniteRepeatable(
                    tween(SLIDE_STEPS * QeetrixMotion.DurationSlower, easing = QeetrixMotion.EasingStandard),
                    RepeatMode.Restart,
                ),
            label = "progress-slide",
        )
    }
}

/** The value as the web writes it: with [format], the value itself; else its share of the range as a percentage. */
@Composable
private fun rememberWritten(
    value: Float,
    min: Float,
    max: Float,
    format: NumberFormat?,
): String {
    val locale = LocalConfiguration.current.locales[0]
    return remember(value, min, max, format, locale) {
        format?.format(value.toDouble()) ?: NumberFormat.getPercentInstance(locale).format(((value - min) / (max - min)).toDouble())
    }
}

/**
 * `--qx-progress-track`: `color.border.default` in light, `color.surface.sunken` in dark, where every tone's fill keeps 3:1
 * against it (progress.css 0.4.0).
 */
internal fun progressTrack(
    colors: QeetrixColors,
    dark: Boolean,
): Color = if (dark) colors.surfaceSunken else colors.borderDefault

/** The indicator colour of a progress tone (`[data-tone] .qx-progress-indicator`). */
internal fun QeetrixProgressTone.indicator(colors: QeetrixColors): Color =
    when (this) {
        QeetrixProgressTone.Brand -> colors.bgBrandBold
        QeetrixProgressTone.Success -> colors.bgSuccessBold
        QeetrixProgressTone.Danger -> colors.bgDangerBold
    }

/** The indicator colour of a meter tone (`[data-tone] .qx-progress-indicator`; warning is `--qx-progress-warning`). */
internal fun QeetrixMeterTone.indicator(
    colors: QeetrixColors,
    dark: Boolean,
): Color =
    when (this) {
        QeetrixMeterTone.Brand -> colors.bgBrandBold
        QeetrixMeterTone.Neutral -> colors.bgNeutralBold
        QeetrixMeterTone.Info -> colors.bgInfoBold
        QeetrixMeterTone.Success -> colors.bgSuccessBold
        QeetrixMeterTone.Warning -> if (dark) colors.bgWarningBold else colors.borderWarning
        QeetrixMeterTone.Danger -> colors.bgDangerBold
    }

/** `.qx-progress-track block-size: 4px`. */
private val TrackHeight = 4.dp

/** `.qx-meter-target inline-size: 2px`. */
private val TickWidth = 2.dp

/** `.qx-meter-target inset-block: -3px`. */
private val TickReach = 3.dp

/** `.qx-progress-indicator[data-indeterminate] inline-size: 30%`. */
private const val SEGMENT = 0.3f

/** `@keyframes qx-progress-slide { from { translate: -100% } to { translate: 334% } }`. */
private const val SLIDE_FROM = -1f
private const val SLIDE_TO = 3.34f

/** `animation-duration: calc(4 * var(--qx-motion-duration-slower))`. */
private const val SLIDE_STEPS = 4

/** `animation: 1.6s ease-in-out infinite qx-progress-fade` under reduced motion; `50% { opacity: .35 }`. */
private const val FADE_MILLIS = 1600
private const val FADE_OPACITY = 0.35f
