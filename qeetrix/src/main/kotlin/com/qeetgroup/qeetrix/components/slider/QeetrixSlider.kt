package com.qeetgroup.qeetrix.components.slider

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.format.QeetrixFormat
import com.qeetgroup.qeetrix.foundation.haptics.QeetrixHapticRole
import com.qeetgroup.qeetrix.foundation.haptics.rememberQeetrixHaptics
import com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A value chosen along a range — `@qeetrix/react`'s Slider with one thumb (slider.css): the [label] and the written
 * value above a 4 dp `color.border.default` track, the `color.bg.brand.bold` indicator from its start to the 16 dp
 * square thumb (`radius.sm`, a 2 dp `color.surface.default` ring), mirrored in right to left.
 *
 * - **[step]** snaps the value (null: any value); [valueRange] bounds it. Dragging and tapping move it; arrow keys
 *   step it (1 % of the range without a step), Page Up/Down by 10 %, Home and End to the ends.
 * - **Touch**: the control is 48 dp tall, so the thumb is a whole touch target, and a tap on the track moves there.
 * - **[minLabel] / [maxLabel]** name the ends under the track ("Quiet", "Loud"; "₹0", "₹50,000").
 * - **The value** is written by [format] — the device locale's number by default — and read by accessibility services
 *   with the label; TalkBack's adjust gestures step it. A change of step plays [QeetrixHapticRole.Selection].
 *
 * ```
 * QeetrixSlider(volume, { volume = it }, label = "Volume", step = 5f)
 * ```
 */
@Composable
public fun QeetrixSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    step: Float? = null,
    showValue: Boolean = true,
    minLabel: String? = null,
    maxLabel: String? = null,
    enabled: Boolean = true,
    format: ((Float) -> String)? = null,
) {
    val locale = LocalConfiguration.current.locales[0]
    val write = format ?: { v: Float -> QeetrixFormat.number(v, locale, maximumFractionDigits = if (step != null && step >= 1f) 0 else 2) }
    SliderFrame(label, if (showValue) write(value) else null, minLabel, maxLabel, modifier) {
        SliderControl(
            values = listOf(value),
            onChange = { _, v -> onValueChange(v) },
            valueRange = valueRange,
            step = step,
            enabled = enabled,
            names = listOf(label),
            write = write,
        )
    }
}

/**
 * A range chosen along a scale — `@qeetrix/react`'s Slider with two thumbs: the indicator runs between them, and each
 * thumb is its own adjustable element, read as "[label], minimum" and "[label], maximum" (`slider.minimumOf`,
 * `slider.maximumOf`). The thumbs never cross.
 */
@Composable
public fun QeetrixRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    step: Float? = null,
    showValue: Boolean = true,
    minLabel: String? = null,
    maxLabel: String? = null,
    enabled: Boolean = true,
    format: ((Float) -> String)? = null,
) {
    val locale = LocalConfiguration.current.locales[0]
    val write = format ?: { v: Float -> QeetrixFormat.number(v, locale, maximumFractionDigits = if (step != null && step >= 1f) 0 else 2) }
    val shown = if (showValue) "${write(value.start)} – ${write(value.endInclusive)}" else null
    SliderFrame(label, shown, minLabel, maxLabel, modifier) {
        SliderControl(
            values = listOf(value.start, value.endInclusive),
            onChange = { index, v ->
                onValueChange(
                    if (index ==
                        0
                    ) {
                        v.coerceAtMost(value.endInclusive)..value.endInclusive
                    } else {
                        value.start..v.coerceAtLeast(value.start)
                    },
                )
            },
            valueRange = valueRange,
            step = step,
            enabled = enabled,
            names = listOf(stringResource(R.string.qx_slider_minimum_of, label), stringResource(R.string.qx_slider_maximum_of, label)),
            write = write,
        )
    }
}

@Composable
private fun SliderFrame(
    label: String,
    value: String?,
    minLabel: String?,
    maxLabel: String?,
    modifier: Modifier,
    control: @Composable () -> Unit,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    // .qx-slider: a column, space.gap.xs apart
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
        // .qx-slider-header: the label and the value, space-between; hidden from accessibility (the thumbs say both).
        Row(Modifier.fillMaxWidth().clearAndSetSemantics {}, horizontalArrangement = Arrangement.SpaceBetween) {
            BasicText(label, style = typography.labelSm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true))
            if (value !=
                null
            ) {
                BasicText(
                    value,
                    style =
                        typography.labelSm
                            .copy(
                                color = colors.textDefault,
                                fontFeatureSettings = "tnum",
                            ).labelDirection(alignToLayout = true),
                )
            }
        }
        control()
        if (minLabel != null || maxLabel != null) {
            Row(Modifier.fillMaxWidth().clearAndSetSemantics {}, horizontalArrangement = Arrangement.SpaceBetween) {
                BasicText(
                    minLabel.orEmpty(),
                    style = typography.labelSm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true),
                )
                BasicText(
                    maxLabel.orEmpty(),
                    style = typography.labelSm.copy(color = colors.textSubtle).labelDirection(alignToLayout = true),
                )
            }
        }
    }
}

@Composable
private fun SliderControl(
    values: List<Float>,
    onChange: (index: Int, value: Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    step: Float?,
    enabled: Boolean,
    names: List<String>,
    write: (Float) -> String,
) {
    require(valueRange.endInclusive > valueRange.start) { "A slider's range must not be empty." }
    require(step == null || step > 0f) { "A slider's step is above zero." }
    val colors = QeetrixTheme.colors
    val haptics = rememberQeetrixHaptics()
    val latest by rememberUpdatedState(values)
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val span = valueRange.endInclusive - valueRange.start

    fun snap(raw: Float): Float {
        val bounded = raw.coerceIn(valueRange.start, valueRange.endInclusive)
        return if (step ==
            null
        ) {
            bounded
        } else {
            (valueRange.start + ((bounded - valueRange.start) / step).roundToInt() * step).coerceIn(valueRange)
        }
    }

    fun move(
        index: Int,
        raw: Float,
    ) {
        val next = snap(raw)
        if (next != latest[index]) {
            if (step != null) haptics.perform(QeetrixHapticRole.Selection)
            onChange(index, next)
        }
    }
    val keyStep = step ?: (span / 100f)
    // .qx-slider-control: block-size size.target.min (here 48 dp), padding-inline 8px
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(QeetrixAccessibility.MinimumTouchTarget)
            .padding(horizontal = THUMB_HALF)
            .then(
                if (!enabled) {
                    Modifier
                } else {
                    Modifier
                        .pointerInput(valueRange, step, rtl) {
                            fun at(x: Float): Float {
                                val share = (x / size.width).coerceIn(0f, 1f)
                                return valueRange.start + (if (rtl) 1f - share else share) * span
                            }

                            fun nearest(v: Float): Int = latest.indices.minBy { abs(latest[it] - v) }
                            detectTapGestures { tap -> at(tap.x).let { v -> move(nearest(v), v) } }
                        }.pointerInput(valueRange, step, rtl) {
                            var dragging = 0

                            fun at(x: Float): Float {
                                val share = (x / size.width).coerceIn(0f, 1f)
                                return valueRange.start + (if (rtl) 1f - share else share) * span
                            }
                            detectHorizontalDragGestures(
                                onDragStart = { start ->
                                    val v = at(start.x)
                                    dragging = latest.indices.minBy { abs(latest[it] - v) }
                                },
                            ) { change, _ -> move(dragging, at(change.position.x)) }
                        }
                },
            ),
    ) {
        val width = maxWidth
        val shares = values.map { ((it - valueRange.start) / span).coerceIn(0f, 1f) }
        val track = colors.borderDefault
        val indicator = if (enabled) colors.bgBrandBold else colors.bgDisabled
        Canvas(Modifier.fillMaxWidth().height(QeetrixAccessibility.MinimumTouchTarget)) {
            val y = size.height / 2
            val thickness = TRACK.toPx()
            // .qx-slider-track: 4 dp of border.default
            drawRect(track, Offset(0f, y - thickness / 2), Size(size.width, thickness))
            // .qx-slider-indicator: bg.brand.bold from the start (or the first thumb) to the thumb
            val from = if (shares.size == 1) 0f else shares[0]
            val to = shares.last()
            val left = if (rtl) size.width * (1f - to) else size.width * from
            drawRect(indicator, Offset(left, y - thickness / 2), Size(size.width * (to - from), thickness))
        }
        values.forEachIndexed { index, v ->
            Thumb(
                share = shares[index],
                width = width,
                rtl = rtl,
                enabled = enabled,
                name = names[index],
                written = write(v),
                value = v,
                valueRange = valueRange,
                onSet = { move(index, it) },
                onStep = { delta -> move(index, v + delta) },
                keyStep = keyStep,
                largeStep = span / 10f,
            )
        }
    }
}

@Composable
private fun Thumb(
    share: Float,
    width: Dp,
    rtl: Boolean,
    enabled: Boolean,
    name: String,
    written: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onSet: (Float) -> Unit,
    onStep: (Float) -> Unit,
    keyStep: Float,
    largeStep: Float,
) {
    val colors = QeetrixTheme.colors
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val ring = colors.surfaceDefault
    val fill = if (enabled) colors.bgBrandBold else colors.iconDisabled
    val frame = colors.borderFocus
    // From the start: the thumb is placed at the start and offset {} mirrors in right to left, so the share is never
    // flipped here (flipping it too put the thumb at its left-to-right place, away from the indicator's end).
    val x = width * share - TOUCH / 2
    androidx.compose.foundation.layout.Box(
        Modifier
            .offset { IntOffset(x.roundToPx(), 0) }
            .size(TOUCH)
            .clearAndSetSemantics {
                contentDescription = name
                stateDescription = written
                progressBarRangeInfo = ProgressBarRangeInfo(value, valueRange)
                if (enabled) {
                    setProgress { target ->
                        onSet(target)
                        true
                    }
                } else {
                    disabled()
                }
            }.focusable(enabled, source)
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown || !enabled) return@onKeyEvent false
                val forward = if (rtl) -1f else 1f
                when (event.key) {
                    Key.DirectionRight -> onStep(keyStep * forward).let { true }
                    Key.DirectionLeft -> onStep(-keyStep * forward).let { true }
                    Key.DirectionUp -> onStep(keyStep).let { true }
                    Key.DirectionDown -> onStep(-keyStep).let { true }
                    Key.PageUp -> onStep(largeStep).let { true }
                    Key.PageDown -> onStep(-largeStep).let { true }
                    Key.MoveHome -> onSet(valueRange.start).let { true }
                    Key.MoveEnd -> onSet(valueRange.endInclusive).let { true }
                    else -> false
                }
            }.drawBehind {
                val side = THUMB.toPx()
                val ringWidth = QeetrixBorder.WidthStrong.toPx()
                val corner = CornerRadius(QeetrixRadius.Sm.toPx())
                val topLeft = Offset((size.width - side) / 2, (size.height - side) / 2)
                // box-shadow: 0 0 0 border.width.strong surface.default, then the thumb
                drawRoundRect(ring, topLeft - Offset(ringWidth, ringWidth), Size(side + 2 * ringWidth, side + 2 * ringWidth), corner)
                drawRoundRect(fill, topLeft, Size(side, side), corner)
                if (focused) {
                    // :focus-visible: focus.ring.width border.focus, focus.ring.offset outside
                    val offset = QeetrixFocus.RingOffset.toPx() + QeetrixFocus.RingWidth.toPx() / 2
                    drawRoundRect(
                        frame,
                        topLeft - Offset(offset, offset),
                        Size(side + 2 * offset, side + 2 * offset),
                        corner,
                        style =
                            androidx.compose.ui.graphics.drawscope
                                .Stroke(QeetrixFocus.RingWidth.toPx()),
                    )
                }
            },
    )
}

/** `.qx-slider-track { block-size: 4px }`. */
private val TRACK = 4.dp

/** `.qx-slider-thumb`: 16 px square. */
private val THUMB = 16.dp

/** `.qx-slider-control { padding-inline: 8px }`: half a thumb, so it reaches each end. */
private val THUMB_HALF = 8.dp

/** Each thumb's touch target. */
private val TOUCH = 48.dp
