package com.qeetgroup.qeetrix.components.numberfield

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.input.FieldBox
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.FieldPlaceholder
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.fieldError
import com.qeetgroup.qeetrix.components.input.fieldTextStyle
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens
import java.math.BigDecimal

/**
 * A number field with stepper buttons — the web's `NumberField` in a field — for quantities, seats and
 * percentages. The value is a [BigDecimal], so a fractional value is exact, never rounded through binary
 * floating point; `null` is an empty field. For whole numbers there is an `Int` overload.
 *
 * - **Typing** keeps its own text, so in-between states — a lone minus sign, a trailing decimal
 *   separator — are never rejected or rewritten while someone types. [onValueChange] follows the text
 *   whenever it reads as a number inside [range]. Values always have [fractionDigits] decimal places (their
 *   scale), so 12 in a two-decimal field is reported as 12.00; compare values with `compareTo`.
 * - **Committing** — the keyboard's action, or leaving the field — settles the text: a number outside
 *   [range] is brought to its nearest end, a decimal is rounded to [fractionDigits], text that is not a
 *   number goes back to the last value, and the value is shown formatted, with grouping. Range errors are
 *   therefore corrected, not reported; report your own rules with [message].
 * - **Locale.** The device's locale decides the decimal separator, grouping and digits; digits from any
 *   script are read. The keyboard is a number pad, or a decimal pad when [fractionDigits] is above zero.
 *   Some number pads have no minus key; the Decrease button still reaches negative values.
 * - **Steppers** change the value by [step], are disabled at the ends of [range], and are named
 *   "Decrease" and "Increase" (`qx_decrease`, `qx_increase`). They are not keyboard stops — Up and Down
 *   arrows step from the field — and accessibility services also get Decrease and Increase as actions on
 *   the field itself.
 *
 * Currency, units and business limits are your app's: show a unit in the [label] or [description].
 *
 * ```
 * var discount by rememberSaveable { mutableStateOf<BigDecimal?>(BigDecimal("12.5")) }
 * QeetrixNumberField(discount, { discount = it }, label = "Discount (%)",
 *     range = BigDecimal.ZERO..BigDecimal(100), step = BigDecimal("0.5"), fractionDigits = 1)
 * ```
 */
@Composable
public fun QeetrixNumberField(
    value: BigDecimal?,
    onValueChange: (BigDecimal?) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    range: ClosedRange<BigDecimal>? = null,
    step: BigDecimal = BigDecimal.ONE,
    fractionDigits: Int = 0,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    optional: Boolean = false,
    imeAction: ImeAction = ImeAction.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    require(fractionDigits >= 0) { "fractionDigits ($fractionDigits) cannot be negative." }
    require(step.signum() > 0) { "step ($step) must be above zero." }
    val active = enabled && LocalFieldGroup.current.enabled
    val locale = LocalConfiguration.current.locales[0]
    val allowNegative = range == null || range.start.signum() < 0
    val numbers = remember(locale, fractionDigits, allowNegative) { NumberText(locale, fractionDigits, allowNegative) }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    val latestValue by rememberUpdatedState(value)
    val latestOnValueChange by rememberUpdatedState(onValueChange)

    val text = rememberTextFieldState(value?.let(numbers::write).orEmpty())
    // The last value this field reported; a different value arriving from outside rewrites the text.
    val reported = remember { arrayOfNulls<BigDecimal>(1).also { it[0] = value } }
    LaunchedEffect(value, numbers) {
        // Text another locale wrote — after a change of locale, or restored into an activity recreated for one —
        // is written again in this locale rather than read in it, where "1,234" can be 1.234. Text still being
        // typed is left alone. This runs before the typing reader below, so that reader never sees stale text.
        val stale = !numbers.reads(text.text, value)
        if (value?.stripTrailingZeros() != reported[0]?.stripTrailingZeros() || stale) {
            reported[0] = value
            text.setTextAndPlaceCursorAtEnd(value?.let(numbers::write).orEmpty())
        }
    }

    // Compared with what this field last reported, which is current at once — the composed value can lag a
    // frame behind, and reporting against it would send the same number twice.
    fun report(next: BigDecimal?) {
        val previous = reported[0]
        reported[0] = next
        if (next?.stripTrailingZeros() != previous?.stripTrailingZeros()) latestOnValueChange(next)
    }

    fun commit() {
        when (val reading = numbers.read(text.text)) {
            NumberText.Reading.Empty -> {
                report(null)
            }

            is NumberText.Reading.Number -> {
                val settled = numbers.settle(reading.value, range)
                report(settled)
                text.setTextAndPlaceCursorAtEnd(numbers.write(settled))
            }

            else -> {
                text.setTextAndPlaceCursorAtEnd(latestValue?.let(numbers::write).orEmpty())
            }
        }
    }

    fun stepBy(direction: Int) {
        val current =
            (numbers.read(text.text) as? NumberText.Reading.Number)?.value
                ?: latestValue
                ?: range?.let { it.start.max(BigDecimal.ZERO).min(it.endInclusive) }
                ?: BigDecimal.ZERO
        val next = numbers.settle(current + step.multiply(BigDecimal(direction)), range)
        report(next)
        text.setTextAndPlaceCursorAtEnd(numbers.write(next))
    }

    // Typing: the value follows the text while it reads as a number inside the range.
    LaunchedEffect(text, numbers, range) {
        snapshotFlow { text.text.toString() }.collect { typed ->
            when (val reading = numbers.read(typed)) {
                NumberText.Reading.Empty -> report(null)
                is NumberText.Reading.Number -> if (range == null || reading.value in range) report(numbers.settle(reading.value, null))
                else -> Unit
            }
        }
    }
    // Leaving the field commits it.
    LaunchedEffect(source) {
        var had = false
        snapshotFlow { focused }.collect { now ->
            if (had && !now) commit()
            had = now
        }
    }

    val current = (numbers.read(text.text) as? NumberText.Reading.Number)?.value ?: value
    val canDecrease = active && !readOnly && (range == null || current == null || current > range.start)
    val canIncrease = active && !readOnly && (range == null || current == null || current < range.endInclusive)
    val decrease = stringResource(R.string.qx_decrease)
    val increase = stringResource(R.string.qx_increase)
    val textStyle =
        fieldTextStyle().copy(
            textAlign = TextAlign.Center,
            textDirection = TextDirection.Content, // dir="auto": a number stays in number order in a right-to-left form
            fontFeatureSettings = "tnum", // tabular-nums
        )

    FieldFrame(description, message, modifier) {
        BasicTextField(
            state = text,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fieldError(message)
                    .semantics {
                        customActions =
                            listOfNotNull(
                                CustomAccessibilityAction(decrease) {
                                    stepBy(-1)
                                    true
                                }.takeIf { canDecrease },
                                CustomAccessibilityAction(increase) {
                                    stepBy(1)
                                    true
                                }.takeIf { canIncrease },
                            )
                    }.onPreviewKeyEvent { event ->
                        // ArrowUp / ArrowDown step by `step`, as on the web.
                        if (event.type != KeyEventType.KeyDown || readOnly || !active) return@onPreviewKeyEvent false
                        when (event.key) {
                            Key.DirectionUp -> canIncrease.also { if (it) stepBy(1) }
                            Key.DirectionDown -> canDecrease.also { if (it) stepBy(-1) }
                            else -> false
                        }
                    },
            enabled = active,
            readOnly = readOnly,
            inputTransformation =
                remember(
                    numbers,
                ) { InputTransformation { if (!asCharSequence().all(numbers::accepts)) revertAllChanges() } },
            textStyle = textStyle,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = if (fractionDigits > 0) KeyboardType.Decimal else KeyboardType.Number,
                    imeAction = imeAction,
                ),
            onKeyboardAction = { performDefault ->
                commit()
                if (onKeyboardAction != null) onKeyboardAction.onKeyboardAction(performDefault) else performDefault()
            },
            lineLimits = TextFieldLineLimits.SingleLine,
            interactionSource = source,
            cursorBrush = SolidColor(QeetrixInputComponentTokens.Foreground),
            decorator = { inner ->
                LabelledControl(label, required, optional, active) {
                    // The NumberField group: decrement, the input, increment, inside one field boundary.
                    FieldBox(
                        active,
                        readOnly,
                        focused,
                        hovered,
                        message?.tone,
                        Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        contentPadding = PaddingValues(QeetrixStroke.Hairline),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Stepper(QeetrixIconName.Minus, decrease, canDecrease, start = true) { stepBy(-1) }
                        Box(Modifier.weight(1f).padding(horizontal = QeetrixWebScale.spacing(2f)), contentAlignment = Alignment.Center) {
                            if (placeholder != null && text.text.isEmpty()) FieldPlaceholder(placeholder, textStyle)
                            inner()
                        }
                        Stepper(QeetrixIconName.Plus, increase, canIncrease, start = false) { stepBy(1) }
                    }
                }
            },
        )
    }
}

/**
 * A whole-number [QeetrixNumberField]: the value is an `Int?` and [range] an `IntRange`.
 *
 * ```
 * var seats by rememberSaveable { mutableStateOf<Int?>(5) }
 * QeetrixNumberField(seats, { seats = it }, label = "Seats", range = 1..250)
 * ```
 */
@Composable
public fun QeetrixNumberField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    range: IntRange? = null,
    step: Int = 1,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    optional: Boolean = false,
    imeAction: ImeAction = ImeAction.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    QeetrixNumberField(
        value = value?.let { BigDecimal(it) },
        onValueChange = { onValueChange(it?.toInt()) },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        description = description,
        message = message,
        range = range?.let { BigDecimal(it.first)..BigDecimal(it.last) },
        step = BigDecimal(step),
        fractionDigits = 0,
        enabled = enabled,
        readOnly = readOnly,
        required = required,
        optional = optional,
        imeAction = imeAction,
        onKeyboardAction = onKeyboardAction,
        interactionSource = interactionSource,
    )
}

/**
 * A stepper: as wide as the field is tall, muted, with the hover fill (and the active fill while pressed),
 * a 1 dp rule against the input, and the field's inner corner on its outer side. Not a keyboard stop.
 */
@Composable
private fun Stepper(
    icon: QeetrixIconName,
    label: String,
    enabled: Boolean,
    start: Boolean,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val inner = (QeetrixInputComponentTokens.Corner - QeetrixStroke.Hairline).coerceAtLeast(0.dp) // corner − 1px
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val shape =
        if (start) {
            RoundedCornerShape(
                topStart = inner,
                bottomStart = inner,
            )
        } else {
            RoundedCornerShape(topEnd = inner, bottomEnd = inner)
        }
    val rule = QeetrixTheme.colors.borderDefault
    val fill =
        when {
            enabled && pressed -> QeetrixTheme.colors.surfaceInteractiveActive
            enabled && hovered -> QeetrixTheme.colors.surfaceInteractiveHover
            else -> Color.Transparent
        }
    Box(
        Modifier
            .width(QeetrixInputComponentTokens.height(QeetrixTheme.density)) // w-(--qx-component-input-height)
            .fillMaxHeight()
            .background(fill, shape)
            .drawBehind {
                // border-e on the decrement, border-s on the increment: the rule against the input
                val x = if (start != rtl) size.width - QeetrixStroke.Hairline.toPx() / 2 else QeetrixStroke.Hairline.toPx() / 2
                drawLine(rule, Offset(x, 0f), Offset(x, size.height), strokeWidth = QeetrixStroke.Hairline.toPx())
            }.focusProperties { canFocus = false }
            .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        QeetrixIcon(
            icon,
            contentDescription = null,
            size = QeetrixWebScale.spacing(4f),
            tint =
                when {
                    !enabled -> QeetrixTheme.colors.textDisabled
                    hovered || pressed -> QeetrixTheme.colors.textPrimary
                    else -> QeetrixTheme.colors.textTertiary
                },
        )
    }
}
