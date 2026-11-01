package com.qeetgroup.qeetrix.components.numberfield

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.input.FieldAdornment
import com.qeetgroup.qeetrix.components.input.FieldBox
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.FieldPadding
import com.qeetgroup.qeetrix.components.input.FieldPlaceholder
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.fieldError
import com.qeetgroup.qeetrix.components.input.fieldTextStyle
import com.qeetgroup.qeetrix.foundation.motion.QeetrixMotion
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import java.math.BigDecimal
import java.util.Locale

/**
 * A number, typed or stepped — `@qeetrix/react`'s NumberField in a Field, for quantities, seats and percentages.
 * The value is a [BigDecimal], so a fractional value is exact, never rounded through binary floating point; `null`
 * is an empty field. For whole numbers there is an `Int` overload.
 *
 * - **Look.** The Input box of every field (same height and edges), the value at the start in tabular figures, and
 *   — with [steppers] — the − and + buttons at the end behind a hairline: `size.control.sm` squares 2 dp apart,
 *   `color.overlay.hover` under the pointer and `color.overlay.pressed` while pressed. Turn them off for amounts
 *   nobody steps through. [startAdornment] and [endAdornment] hold units (₹, "minutes", %).
 * - **Typing** keeps its own text, so in-between states — a lone minus sign, a trailing decimal separator — are
 *   never rejected or rewritten while someone types. [onValueChange] follows the text whenever it reads as a number
 *   within [min] and [max]. Values always have [fractionDigits] decimal places (their scale), so 12 in a
 *   two-decimal field is 12.00; compare values with `compareTo`.
 * - **Committing** — the keyboard's action, or leaving the field — settles the text: a number beyond [min] or [max]
 *   is brought to it, a decimal is rounded to [fractionDigits], text that is not a number goes back to the last
 *   value, and the value is shown formatted, with grouping. Range errors are corrected, not reported; report your
 *   own rules with [error].
 * - **Stepping.** The steppers change the value by [step] and turn off at [min] and [max]. From a keyboard, Up and
 *   Down step by [step], Shift or Page Up and Page Down by [largeStep], Home and End go to [min] and [max]. The
 *   steppers are not keyboard stops; accessibility services also get Decrease and Increase (`qx_decrease`,
 *   `qx_increase`) as actions on the field.
 * - **Locale.** The device's locale — or [locale] — decides the decimal separator, grouping and digits; digits from any
 *   script are read. [grouping] `false` drops the group separators (an ID, a year).
 * - **Format** (`@qeetrix/react`'s `format`, QTRX-193): [currency] (an ISO 4217 code) shows the currency's symbol where
 *   the locale writes it — "₹" before the number in `en-IN`, "€" after it in `de-DE` — and defaults [fractionDigits] to
 *   the currency's (two for INR), always written in full as Intl's currency style does ("1.234,50 €"); [percent] makes the value a share, 0.25 shown and typed as 25 beside "%", with [min],
 *   [max] and a [step] under 1 in the same shares. The web writes the marks inside the text; here they are adornments,
 *   so typing stays a plain number. The keyboard is a number pad, or a decimal pad when [fractionDigits] is above zero.
 *
 * ```
 * var discount by rememberSaveable { mutableStateOf<BigDecimal?>(BigDecimal("12.5")) }
 * QeetrixNumberField(discount, { discount = it }, label = "Discount", endAdornment = { QeetrixText("%") },
 *     min = BigDecimal.ZERO, max = BigDecimal(100), step = BigDecimal("0.5"), fractionDigits = 1)
 * ```
 */
@Composable
public fun QeetrixNumberField(
    value: BigDecimal?,
    onValueChange: (BigDecimal?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    min: BigDecimal? = null,
    max: BigDecimal? = null,
    step: BigDecimal = BigDecimal.ONE,
    largeStep: BigDecimal = BigDecimal.TEN,
    fractionDigits: Int? = null,
    steppers: Boolean = true,
    startAdornment: (@Composable () -> Unit)? = null,
    endAdornment: (@Composable () -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    interactionSource: MutableInteractionSource? = null,
    currency: String? = null,
    percent: Boolean = false,
    grouping: Boolean = true,
    locale: Locale? = null,
) {
    require(currency == null || !percent) { "A number field is an amount of money or a percentage, not both." }
    // Percent: the value is the share (0.25), shown and stepped as its percentage (25), as Intl's percent style does.
    val scale = if (percent) HUNDRED else BigDecimal.ONE
    val fractionDigits =
        fractionDigits ?: currency?.let {
            android.icu.util.Currency
                .getInstance(it.uppercase(Locale.ROOT))
                .defaultFractionDigits
        } ?: 0
    val outerChange = onValueChange
    val value = value?.multiply(scale)
    val onValueChange: (BigDecimal?) -> Unit = { next -> outerChange(next?.divide(scale)) }
    val min = min?.multiply(scale)
    val max = max?.multiply(scale)
    val step = step.multiply(scale).takeIf { percent && step < BigDecimal.ONE } ?: step
    val largeStep = largeStep.multiply(scale).takeIf { percent && largeStep < BigDecimal.ONE } ?: largeStep
    require(fractionDigits >= 0) { "fractionDigits ($fractionDigits) cannot be negative." }
    require(step.signum() > 0) { "step ($step) must be above zero." }
    require(largeStep.signum() > 0) { "largeStep ($largeStep) must be above zero." }
    require(min == null || max == null || min <= max) { "min ($min) cannot be above max ($max)." }
    val active = enabled && LocalFieldGroup.current.enabled
    val locale = locale ?: LocalConfiguration.current.locales[0]
    val allowNegative = min == null || min.signum() < 0
    val money = currency != null
    val numbers =
        remember(locale, fractionDigits, allowNegative, grouping, money) {
            NumberText(locale, fractionDigits, allowNegative, grouping, padFraction = money)
        }
    // The currency's symbol or the percent sign, where the locale writes it ("₹" before in en-IN, "€" after in de-DE),
    // as an adornment unless the field has its own.
    val (prefix, suffix) = remember(locale, currency, percent) { formatMarks(locale, currency, percent) }
    val startAdornment = startAdornment ?: prefix?.let { mark -> @Composable { FieldAdornmentText(mark) } }
    val endAdornment = endAdornment ?: suffix?.let { mark -> @Composable { FieldAdornmentText(mark) } }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    val latestValue by rememberUpdatedState(value)
    val latestOnValueChange by rememberUpdatedState(onValueChange)

    val text = rememberTextFieldState(value?.let(numbers::write).orEmpty())
    // The last value this field reported; a different value arriving from outside rewrites the text.
    val reported = remember { arrayOfNulls<BigDecimal>(1).also { it[0] = value } }
    LaunchedEffect(value, numbers) {
        // Text another locale wrote — after a change of locale, or restored into an activity recreated for one — is
        // written again in this locale rather than read in it, where "1,234" can be 1.234. Text still being typed is
        // left alone. This runs before the typing reader below, so that reader never sees stale text.
        val stale = !numbers.reads(text.text, value)
        if (value?.stripTrailingZeros() != reported[0]?.stripTrailingZeros() || stale) {
            reported[0] = value
            text.setTextAndPlaceCursorAtEnd(value?.let(numbers::write).orEmpty())
        }
    }

    // Compared with what this field last reported, which is current at once — the composed value can lag a frame
    // behind, and reporting against it would send the same number twice.
    fun report(next: BigDecimal?) {
        val previous = reported[0]
        reported[0] = next
        if (next?.stripTrailingZeros() != previous?.stripTrailingZeros()) latestOnValueChange(next)
    }

    fun show(next: BigDecimal) {
        report(next)
        text.setTextAndPlaceCursorAtEnd(numbers.write(next))
    }

    fun commit() {
        when (val reading = numbers.read(text.text)) {
            NumberText.Reading.Empty -> report(null)
            is NumberText.Reading.Number -> show(numbers.settle(reading.value, min, max))
            else -> text.setTextAndPlaceCursorAtEnd(latestValue?.let(numbers::write).orEmpty())
        }
    }

    fun current(): BigDecimal? = (numbers.read(text.text) as? NumberText.Reading.Number)?.value ?: latestValue

    fun stepBy(amount: BigDecimal) {
        val from = current() ?: BigDecimal.ZERO.let { zero -> numbers.settle(zero, min, max) }
        show(numbers.settle(from + amount, min, max))
    }

    // Typing: the value follows the text while it reads as a number within the bounds.
    LaunchedEffect(text, numbers, min, max) {
        snapshotFlow { text.text.toString() }.collect { typed ->
            when (val reading = numbers.read(typed)) {
                NumberText.Reading.Empty -> report(null)
                is NumberText.Reading.Number -> if (reading.value.within(min, max)) report(numbers.settle(reading.value))
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

    val shownValue = current()
    val canStep = active && !readOnly
    val canDecrease = canStep && (min == null || shownValue == null || shownValue > min)
    val canIncrease = canStep && (max == null || shownValue == null || shownValue < max)
    val decrease = stringResource(R.string.qx_decrease)
    val increase = stringResource(R.string.qx_increase)
    val colors = QeetrixTheme.colors
    val textStyle =
        fieldTextStyle().copy(
            color = if (active) colors.textDefault else colors.textDisabled,
            textDirection = TextDirection.Content, // a number stays in number order in a right-to-left form
            fontFeatureSettings = "tnum", // .qx-number-field-input { font-variant-numeric: tabular-nums }
        )

    FieldFrame(description, error, active, modifier) {
        BasicTextField(
            state = text,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .fieldError(error)
                    .semantics {
                        customActions =
                            listOfNotNull(
                                CustomAccessibilityAction(decrease) {
                                    stepBy(-step)
                                    true
                                }.takeIf { canDecrease },
                                CustomAccessibilityAction(increase) {
                                    stepBy(step)
                                    true
                                }.takeIf { canIncrease },
                            )
                    }.onPreviewKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown || !canStep) return@onPreviewKeyEvent false
                        val big = event.isShiftPressed
                        when (event.key) {
                            Key.DirectionUp -> canIncrease.also { if (it) stepBy(if (big) largeStep else step) }
                            Key.DirectionDown -> canDecrease.also { if (it) stepBy(-(if (big) largeStep else step)) }
                            Key.PageUp -> canIncrease.also { if (it) stepBy(largeStep) }
                            Key.PageDown -> canDecrease.also { if (it) stepBy(-largeStep) }
                            Key.MoveHome -> (min != null).also { if (it) show(numbers.settle(min!!, min, max)) }
                            Key.MoveEnd -> (max != null).also { if (it) show(numbers.settle(max!!, min, max)) }
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
                    keyboardType =
                        if (fractionDigits >
                            0
                        ) {
                            KeyboardType.Decimal
                        } else {
                            KeyboardType.Number
                        },
                    imeAction = imeAction,
                ),
            onKeyboardAction = { performDefault ->
                commit()
                if (onKeyboardAction != null) onKeyboardAction.onKeyboardAction(performDefault) else performDefault()
            },
            lineLimits = TextFieldLineLimits.SingleLine,
            interactionSource = source,
            cursorBrush = SolidColor(textStyle.color),
            decorator = { inner ->
                LabelledControl(label, optional, required, hideLabel, active, help) {
                    // .qx-number-field: padding-inline: space.inset.md space.gap.xs (inset.md at both ends without steppers)
                    FieldBox(
                        active,
                        readOnly,
                        focused,
                        hovered,
                        invalid || error != null,
                        Modifier.fillMaxWidth(),
                        contentPadding =
                            if (steppers) {
                                PaddingValues(
                                    start = QeetrixSpace.InsetMd,
                                    end = QeetrixSpace.GapXs,
                                )
                            } else {
                                FieldPadding
                            },
                    ) {
                        if (startAdornment != null) FieldAdornment(startAdornment)
                        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (placeholder != null && text.text.isEmpty()) FieldPlaceholder(placeholder, textStyle)
                            inner()
                        }
                        if (endAdornment != null) FieldAdornment(endAdornment)
                        if (steppers) {
                            Steppers(
                                onDecrease = { stepBy(-step) },
                                onIncrease = { stepBy(step) },
                                canDecrease = canDecrease,
                                canIncrease = canIncrease,
                                decrease = decrease,
                                increase = increase,
                            )
                        }
                    }
                }
            },
        )
    }
}

/**
 * A whole-number [QeetrixNumberField]: the value is an `Int?`, and [min], [max], [step] and [largeStep] are `Int`s.
 *
 * ```
 * var seats by rememberSaveable { mutableStateOf<Int?>(5) }
 * QeetrixNumberField(seats, { seats = it }, label = "Seats", min = 1, max = 250)
 * ```
 */
@Composable
public fun QeetrixNumberField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    description: String? = null,
    error: String? = null,
    invalid: Boolean = false,
    optional: Boolean = false,
    required: Boolean = false,
    hideLabel: Boolean = false,
    help: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    min: Int? = null,
    max: Int? = null,
    step: Int = 1,
    largeStep: Int = 10,
    steppers: Boolean = true,
    startAdornment: (@Composable () -> Unit)? = null,
    endAdornment: (@Composable () -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    QeetrixNumberField(
        value = value?.let { BigDecimal(it) },
        onValueChange = { onValueChange(it?.toInt()) },
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        invalid = invalid,
        optional = optional,
        required = required,
        hideLabel = hideLabel,
        help = help,
        enabled = enabled,
        readOnly = readOnly,
        min = min?.let { BigDecimal(it) },
        max = max?.let { BigDecimal(it) },
        step = BigDecimal(step),
        largeStep = BigDecimal(largeStep),
        fractionDigits = 0,
        steppers = steppers,
        startAdornment = startAdornment,
        endAdornment = endAdornment,
        imeAction = imeAction,
        onKeyboardAction = onKeyboardAction,
        interactionSource = interactionSource,
    )
}

private fun BigDecimal.within(
    min: BigDecimal?,
    max: BigDecimal?,
): Boolean = (min == null || this >= min) && (max == null || this <= max)

/**
 * `.qx-number-field-steppers`: a 1 dp `color.border.default` hairline as tall as the buttons, `space.gap.xs` before
 * them, and the − and + buttons 2 dp apart.
 */
@Composable
private fun Steppers(
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    canDecrease: Boolean,
    canIncrease: Boolean,
    decrease: String,
    increase: String,
) {
    val side = QeetrixTheme.density.sizeControlSm
    Row(verticalAlignment = Alignment.CenterVertically) {
        // border-inline-start: border.width.default solid color.border.default; padding-inline-start: space.gap.xs
        Box(Modifier.size(QeetrixBorder.WidthDefault, side).background(QeetrixTheme.colors.borderDefault))
        Row(Modifier.padding(start = QeetrixSpace.GapXs), horizontalArrangement = Arrangement.spacedBy(STEPPER_GAP)) {
            Stepper(QeetrixIconName.Minus, decrease, canDecrease, onDecrease)
            Stepper(QeetrixIconName.Plus, increase, canIncrease, onIncrease)
        }
    }
}

/**
 * `.qx-number-field-step`: a `size.control.sm` square with 4 dp corners, a 16 dp sharp icon in `color.icon.default`
 * (`color.icon.disabled` when it cannot step); `color.overlay.hover` under the pointer, `color.overlay.pressed`
 * while pressed. Its touch area runs the field's full height, so it is easier to hit than it is drawn; it is not a
 * keyboard stop (the arrow keys step from the field).
 */
@Composable
private fun Stepper(
    icon: QeetrixIconName,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val hovered by source.collectIsHoveredAsState()
    val pressed by source.collectIsPressedAsState()
    val colors = QeetrixTheme.colors
    val fill =
        when {
            enabled && pressed -> colors.overlayPressed
            enabled && hovered -> colors.overlayHover
            else -> Color.Transparent
        }
    val background by animateColorAsState(
        fill,
        qeetrixAnimationSpec(QeetrixMotion.EasingStandard, QeetrixMotion.DurationFast),
        label = "stepper-fill",
    )
    val side = QeetrixTheme.density.sizeControlSm
    Box(
        Modifier
            .width(side)
            .height(QeetrixTheme.density.sizeControlLg)
            .focusProperties { canFocus = false }
            .clickable(source, indication = null, enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(side).background(background, QeetrixShapes.Control), contentAlignment = Alignment.Center) {
            QeetrixIcon(
                icon,
                contentDescription = null,
                size = QeetrixSize.IconSm,
                shape = QeetrixIconShape.Sharp,
                tint = if (enabled) colors.iconDefault else colors.iconDisabled,
            )
        }
    }
}

/** `.qx-number-field-steppers { gap: 2px }`. */
private val STEPPER_GAP = 2.dp

private val HUNDRED = BigDecimal(100)

/**
 * The marks a formatted number carries in [locale], as `Intl.NumberFormat` places them: a [currency]'s symbol, or the
 * percent sign, before (`first`) or after (`second`) the number.
 */
internal fun formatMarks(
    locale: Locale,
    currency: String?,
    percent: Boolean,
): Pair<String?, String?> {
    val uLocale =
        android.icu.util.ULocale
            .forLocale(locale)
    val format =
        when {
            currency != null -> {
                (
                    android.icu.text.NumberFormat
                        .getCurrencyInstance(uLocale) as android.icu.text.DecimalFormat
                ).apply {
                    this.currency =
                        android.icu.util.Currency
                            .getInstance(currency.uppercase(Locale.ROOT))
                }
            }

            percent -> {
                android.icu.text.NumberFormat
                    .getPercentInstance(uLocale) as android.icu.text.DecimalFormat
            }

            else -> {
                return null to null
            }
        }
    return format.positivePrefix.trim().ifEmpty { null } to format.positiveSuffix.trim().ifEmpty { null }
}

/** A format mark as an adornment: `body.md`, `color.text.subtle`, as the field's other adornments. */
@Composable
private fun FieldAdornmentText(mark: String) {
    FieldAdornment {
        androidx.compose.foundation.text
            .BasicText(mark, style = fieldTextStyle().copy(color = QeetrixTheme.colors.textSubtle))
    }
}
