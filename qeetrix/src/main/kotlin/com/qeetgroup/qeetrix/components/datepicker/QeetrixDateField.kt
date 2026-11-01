package com.qeetgroup.qeetrix.components.datepicker

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Shapes
import androidx.compose.material3.TimePicker
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialog
import com.qeetgroup.qeetrix.components.dialog.QeetrixDialogSize
import com.qeetgroup.qeetrix.components.input.FieldBox
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.FieldIcon
import com.qeetgroup.qeetrix.components.input.FieldPlaceholder
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.fieldTextStyle
import com.qeetgroup.qeetrix.foundation.color.QeetrixColors
import com.qeetgroup.qeetrix.foundation.format.QeetrixDateStyle
import com.qeetgroup.qeetrix.foundation.format.QeetrixFormat
import com.qeetgroup.qeetrix.foundation.format.QeetrixTimeStyle
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.foundation.typography.QeetrixTypography
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.math.roundToInt

/**
 * A date — `@qeetrix/react`'s DatePicker on a phone: a read-only field showing the chosen date in the locale's words
 * ([QeetrixFormat.date], "25 Oct 2026") with a `calendar` icon, which opens Android's own date picker, in Qeetrix's
 * colours, type and corners, inside a [QeetrixDialog].
 *
 * - **[min] / [max]** bound the dates offered, and [isDateAllowed] turns off others (holidays, weekends); the picker
 *   starts the week on the locale's first day and writes the locale's date format.
 * - **Field** props as every Qeetrix field: [label] (required), [description], [error], [optional], [required],
 *   [enabled]. The field is read by accessibility services as a button named [label], its value as the date or "Not set";
 *   its action is "Choose date" (`datePicker.choose`).
 * - **The value is the app's** ([value], [onValueChange]): Cancel keeps it; Done reports the chosen date.
 *
 * ```
 * QeetrixDateField(dueDate, { dueDate = it }, label = "Due date", min = LocalDate.now())
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun QeetrixDateField(
    value: LocalDate?,
    onValueChange: (LocalDate?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    description: String? = null,
    error: String? = null,
    optional: Boolean = false,
    required: Boolean = false,
    enabled: Boolean = true,
    min: LocalDate? = null,
    max: LocalDate? = null,
    isDateAllowed: (LocalDate) -> Boolean = { true },
) {
    val locale = LocalConfiguration.current.locales[0]
    var open by rememberSaveable { mutableStateOf(false) }
    val shown = value?.let { remember(it, locale) { QeetrixFormat.date(it, locale) } }
    PickerField(
        label = label,
        shown = shown,
        icon = QeetrixIconName.Calendar,
        action = stringResource(R.string.qx_date_choose),
        onOpen = { open = true },
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        optional = optional,
        required = required,
        enabled = enabled,
    )
    if (open) {
        val state =
            rememberDatePickerState(
                initialSelectedDateMillis = value?.toUtcMillis(),
                yearRange = (min?.year ?: 1900)..(max?.year ?: 2100),
                selectableDates = Allowed(min, max, isDateAllowed),
            )
        PickerDialog(
            title = label,
            onClose = { open = false },
            onDone = {
                onValueChange(state.selectedDateMillis?.toLocalDate())
                open = false
            },
        ) {
            DatePicker(state, Modifier.fitDialogWidth(), title = null, headline = null, showModeToggle = false, colors = pickerColors())
        }
    }
}

/**
 * A range of dates — `@qeetrix/react`'s DateRangePicker on a phone: a read-only field showing "1 Oct 2026 – 25 Oct
 * 2026", which opens Android's own date range picker in Qeetrix's colours and type. As [QeetrixDateField], with
 * [min], [max] and [isDateAllowed]; Done reports the range once both ends are chosen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun QeetrixDateRangeField(
    value: ClosedRange<LocalDate>?,
    onValueChange: (ClosedRange<LocalDate>?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    description: String? = null,
    error: String? = null,
    optional: Boolean = false,
    required: Boolean = false,
    enabled: Boolean = true,
    min: LocalDate? = null,
    max: LocalDate? = null,
    isDateAllowed: (LocalDate) -> Boolean = { true },
) {
    val locale = LocalConfiguration.current.locales[0]
    var open by rememberSaveable { mutableStateOf(false) }
    val shown =
        value?.let {
            remember(it, locale) { "${QeetrixFormat.date(it.start, locale)} – ${QeetrixFormat.date(it.endInclusive, locale)}" }
        }
    PickerField(
        label = label,
        shown = shown,
        icon = QeetrixIconName.Calendar,
        action = stringResource(R.string.qx_date_range_choose),
        onOpen = { open = true },
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        optional = optional,
        required = required,
        enabled = enabled,
    )
    if (open) {
        val state =
            rememberDateRangePickerState(
                initialSelectedStartDateMillis = value?.start?.toUtcMillis(),
                initialSelectedEndDateMillis = value?.endInclusive?.toUtcMillis(),
                yearRange = (min?.year ?: 1900)..(max?.year ?: 2100),
                selectableDates = Allowed(min, max, isDateAllowed),
            )
        PickerDialog(
            title = label,
            onClose = { open = false },
            onDone = {
                val start = state.selectedStartDateMillis?.toLocalDate()
                val end = state.selectedEndDateMillis?.toLocalDate()
                onValueChange(if (start != null && end != null) start..end else null)
                open = false
            },
            doneEnabled = state.selectedEndDateMillis != null,
        ) {
            DateRangePicker(
                state,
                Modifier.fitDialogWidth().height(RANGE_HEIGHT),
                title = null,
                headline = null,
                showModeToggle = false,
                colors = pickerColors(),
            )
        }
    }
}

/**
 * A time of day — a read-only field showing the time in the locale's words ("2:30 pm") with a `clock` icon, which
 * opens Android's own time picker in Qeetrix's colours and type: 12 or 24 hours as the device is set, unless [is24Hour]
 * says otherwise. [minuteStep] rounds the chosen minute to the nearest step (15 for quarter hours). No
 * `@qeetrix/react` counterpart; the field is a Qeetrix field as [QeetrixDateField] is.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun QeetrixTimeField(
    value: LocalTime?,
    onValueChange: (LocalTime?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    description: String? = null,
    error: String? = null,
    optional: Boolean = false,
    required: Boolean = false,
    enabled: Boolean = true,
    minuteStep: Int = 1,
    is24Hour: Boolean = DateFormat.is24HourFormat(LocalContext.current),
) {
    require(minuteStep in 1..60 && 60 % minuteStep == 0) { "minuteStep ($minuteStep) divides an hour: 1, 5, 10, 15, 30…" }
    val locale = LocalConfiguration.current.locales[0]
    var open by rememberSaveable { mutableStateOf(false) }
    val shown =
        value?.let {
            remember(it, locale) {
                QeetrixFormat
                    .dateTime(
                        it.atDate(LocalDate.of(2000, 1, 1)),
                        locale,
                        timeStyle = QeetrixTimeStyle.Short,
                    ).substringAfterLast(", ")
            }
        }
    PickerField(
        label = label,
        shown = shown,
        icon = QeetrixIconName.Clock,
        action = stringResource(R.string.qx_time_choose),
        onOpen = { open = true },
        modifier = modifier,
        placeholder = placeholder,
        description = description,
        error = error,
        optional = optional,
        required = required,
        enabled = enabled,
    )
    if (open) {
        val start = value ?: LocalTime.now().withSecond(0).withNano(0)
        val state = rememberTimePickerState(start.hour, start.minute, is24Hour)
        PickerDialog(
            title = label,
            onClose = { open = false },
            onDone = {
                onValueChange(snapToStep(LocalTime.of(state.hour, state.minute), minuteStep))
                open = false
            },
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(
                    state,
                    colors =
                        androidx.compose.material3.TimePickerDefaults.colors(
                            clockDialSelectedContentColor = QeetrixTheme.colors.textOnBrand,
                        ),
                )
            }
        }
    }
}

/** The nearest multiple of [step] minutes, wrapping past midnight. */
internal fun snapToStep(
    time: LocalTime,
    step: Int,
): LocalTime {
    if (step <= 1) return time
    val minutes = time.hour * 60 + time.minute
    val snapped = ((minutes + step / 2) / step * step) % (24 * 60)
    return LocalTime.of(snapped / 60, snapped % 60)
}

/** The read-only field every picker opens from: a Qeetrix field box, the value or placeholder, and the [icon]. */
@Composable
private fun PickerField(
    label: String,
    shown: String?,
    icon: QeetrixIconName,
    action: String,
    onOpen: () -> Unit,
    modifier: Modifier,
    placeholder: String?,
    description: String?,
    error: String?,
    optional: Boolean,
    required: Boolean,
    enabled: Boolean,
) {
    val active = enabled && LocalFieldGroup.current.enabled
    val source = remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    val notSet = stringResource(R.string.qx_not_set)
    val colors = QeetrixTheme.colors
    FieldFrame(description, error, active, modifier) {
        // The whole field — label and box — is one button, read as its label and its value.
        Box(
            Modifier
                .fillMaxWidth()
                .clickable(source, indication = null, enabled = active, role = Role.Button, onClickLabel = action, onClick = onOpen)
                .clearAndSetSemantics {
                    role = Role.Button
                    contentDescription = label
                    stateDescription = shown ?: notSet
                    onClick(action) {
                        if (active) onOpen()
                        active
                    }
                    if (!active) disabled()
                    if (error != null) error(error)
                },
        ) {
            LabelledControl(label, optional, required, hideLabel = false, enabled = active, help = null) {
                FieldBox(
                    active,
                    readOnly = false,
                    focused = focused,
                    hovered = hovered,
                    invalid = error != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if (shown != null) {
                            // The chosen value wraps rather than being cut: the field grows with it.
                            BasicText(
                                shown,
                                style =
                                    fieldTextStyle()
                                        .copy(
                                            color = if (active) colors.textDefault else colors.textDisabled,
                                        ).labelDirection(alignToLayout = true),
                            )
                        } else if (placeholder != null) {
                            FieldPlaceholder(placeholder, fieldTextStyle())
                        }
                    }
                    FieldIcon(icon)
                }
            }
        }
    }
}

/**
 * Material's calendar is 360 dp wide and clips its last column in anything narrower — the Saturday of every week, in a
 * dialog on a phone. It takes the dialog body's whole width, through the body's `space.inset.xl` padding, and below
 * 360 dp it is scaled to fit (taps follow the scaling), so a week is never cut.
 */
private fun Modifier.fitDialogWidth(): Modifier =
    layout { measurable, constraints ->
        val natural = CALENDAR_WIDTH.roundToPx()
        val room = if (constraints.hasBoundedWidth) constraints.maxWidth + 2 * QeetrixSpace.InsetXl.roundToPx() else natural
        val placeable =
            measurable.measure(
                Constraints(minWidth = natural, maxWidth = maxOf(room, natural), maxHeight = constraints.maxHeight),
            )
        val scale = minOf(1f, room.toFloat() / placeable.width)
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else (placeable.width * scale).roundToInt()
        layout(width, (placeable.height * scale).roundToInt()) {
            placeable.placeRelativeWithLayer(((width - placeable.width * scale) / 2).roundToInt(), 0) {
                scaleX = scale
                scaleY = scale
                transformOrigin = TransformOrigin(0f, 0f)
            }
        }
    }

/** Material 3's calendar width (DatePickerModalTokens.ContainerWidth). */
private val CALENDAR_WIDTH = 360.dp

/** A picker in a Qeetrix dialog, under the Material theme mapped from Qeetrix's: Cancel (ghost) and Done. */
@Composable
private fun PickerDialog(
    title: String,
    onClose: () -> Unit,
    onDone: () -> Unit,
    doneEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    QeetrixDialog(
        open = true,
        onOpenChange = { if (!it) onClose() },
        title = title,
        size = QeetrixDialogSize.Sm,
        footer = {
            QeetrixButton(stringResource(R.string.qx_cancel), onClick = onClose, variant = QeetrixButtonVariant.Ghost)
            QeetrixButton(stringResource(R.string.qx_done), onClick = onDone, enabled = doneEnabled)
        },
    ) {
        QeetrixMaterialTheme(content)
    }
}

/**
 * Android's own pickers in Qeetrix's colours, type and corners: Material 3's colour roles mapped to Qeetrix's (the
 * primary is Ember, `color.bg.brand.bold`; surfaces are the overlay's), its type scale to Qeetrix's roles, its corners
 * to `radius.control`. Internal: no Material type is part of Qeetrix's API.
 */
@Composable
internal fun QeetrixMaterialTheme(content: @Composable () -> Unit) {
    val colors = QeetrixTheme.colors
    val type = QeetrixTheme.typography
    MaterialTheme(
        colorScheme = materialColors(colors, QeetrixTheme.isDark),
        typography = materialType(type),
        shapes = Shapes(extraSmall = Corner, small = Corner, medium = Corner),
        content = content,
    )
}

/**
 * Every Material colour role, from Qeetrix's: a role left at Material's default leaked its pink or lavender into the
 * pickers (the time picker's chosen AM / PM was `tertiaryContainer`, pink). Synthetic, so it is internal for its test
 * without becoming public bytecode that names a Material type (MaterialBoundaryTest).
 */
@JvmSynthetic
internal fun materialColors(
    c: QeetrixColors,
    dark: Boolean,
    base: ColorScheme = if (dark) darkColorScheme() else lightColorScheme(),
): ColorScheme =
    base.copy(
        primary = c.bgBrandBold,
        onPrimary = c.textOnBrand,
        primaryContainer = c.bgBrandSubtle,
        onPrimaryContainer = c.textBrand,
        secondaryContainer = c.bgSelected,
        onSecondaryContainer = c.textDefault,
        surface = c.surfaceOverlay,
        onSurface = c.textDefault,
        surfaceVariant = c.bgNeutralSubtle,
        onSurfaceVariant = c.textSubtle,
        surfaceContainerHigh = c.surfaceOverlay,
        surfaceContainerHighest = c.bgNeutralSubtle,
        outline = c.borderStrong,
        outlineVariant = c.borderSubtle,
        error = c.iconDanger,
        onError = c.textOnBold,
        errorContainer = c.bgDangerSubtle,
        onErrorContainer = c.textDanger,
        secondary = c.bgNeutralBold,
        onSecondary = c.textInverse,
        tertiary = c.bgBrandBold,
        onTertiary = c.textOnBrand,
        tertiaryContainer = c.bgBrandSubtle,
        onTertiaryContainer = c.textBrand,
        background = c.surfaceCanvas,
        onBackground = c.textDefault,
        inverseSurface = c.bgNeutralBold,
        inverseOnSurface = c.textInverse,
        inversePrimary = c.bgBrandSubtle,
        // Tonal elevation tints a surface with this; Qeetrix's overlay has its own colour, so none.
        surfaceTint = c.surfaceOverlay,
        scrim = c.overlayScrim,
        surfaceBright = c.surfaceOverlay,
        surfaceDim = c.surfaceSunken,
        surfaceContainer = c.surfaceOverlay,
        surfaceContainerLow = c.surfaceOverlay,
        surfaceContainerLowest = c.surfaceOverlay,
        primaryFixed = c.bgBrandSubtle,
        primaryFixedDim = c.bgBrandSubtlePressed,
        onPrimaryFixed = c.textBrand,
        onPrimaryFixedVariant = c.textBrand,
        secondaryFixed = c.bgNeutralSubtle,
        secondaryFixedDim = c.bgNeutralSubtlePressed,
        onSecondaryFixed = c.textDefault,
        onSecondaryFixedVariant = c.textSubtle,
        tertiaryFixed = c.bgBrandSubtle,
        tertiaryFixedDim = c.bgBrandSubtlePressed,
        onTertiaryFixed = c.textBrand,
        onTertiaryFixedVariant = c.textBrand,
    )

private fun materialType(t: QeetrixTypography): Typography =
    Typography(
        displayLarge = t.displayLg,
        displayMedium = t.displayMd,
        displaySmall = t.displaySm,
        headlineLarge = t.headingLg,
        headlineMedium = t.headingMd,
        headlineSmall = t.headingSm,
        titleLarge = t.titleLg,
        titleMedium = t.titleMd,
        titleSmall = t.titleSm,
        bodyLarge = t.bodyLg,
        bodyMedium = t.bodyMd,
        bodySmall = t.bodySm,
        labelLarge = t.labelLg,
        labelMedium = t.labelMd,
        labelSmall = t.labelSm,
    )

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun pickerColors() =
    DatePickerDefaults.colors(
        containerColor = QeetrixTheme.colors.surfaceOverlay,
        selectedDayContainerColor = QeetrixTheme.colors.bgBrandBold,
        selectedDayContentColor = QeetrixTheme.colors.textOnBrand,
        todayDateBorderColor = QeetrixTheme.colors.borderBrand,
        todayContentColor = QeetrixTheme.colors.textBrand,
    )

/** The dates offered: within [min]…[max] and allowed. */
@OptIn(ExperimentalMaterial3Api::class)
private class Allowed(
    private val min: LocalDate?,
    private val max: LocalDate?,
    private val allowed: (LocalDate) -> Boolean,
) : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val date = utcTimeMillis.toLocalDate()
        return (min == null || date >= min) && (max == null || date <= max) && allowed(date)
    }

    override fun isSelectableYear(year: Int): Boolean = (min == null || year >= min.year) && (max == null || year <= max.year)
}

private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

/** `radius.control` for Material's corner roles. */
private val Corner =
    androidx.compose.foundation.shape
        .RoundedCornerShape(com.qeetgroup.qeetrix.foundation.shape.QeetrixRadius.Control)

/** Room for the range picker's scrolling months inside the dialog. */
private val RANGE_HEIGHT = 480.dp
