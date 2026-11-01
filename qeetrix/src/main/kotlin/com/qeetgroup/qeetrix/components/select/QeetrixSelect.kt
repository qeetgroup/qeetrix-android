package com.qeetgroup.qeetrix.components.select

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.qeetgroup.qeetrix.components.input.FieldBox
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixOptionGroup
import com.qeetgroup.qeetrix.components.input.accessibleLabel
import com.qeetgroup.qeetrix.components.input.fieldError
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixShadow
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixShadow
import com.qeetgroup.qeetrix.foundation.shape.QeetrixShapes
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.internal.AnchoredPopupPosition
import com.qeetgroup.qeetrix.internal.FirstLineRow
import com.qeetgroup.qeetrix.internal.FocusFrame
import com.qeetgroup.qeetrix.internal.PopupAlign
import com.qeetgroup.qeetrix.internal.QeetrixTextWeight
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.popupEnter
import com.qeetgroup.qeetrix.internal.popupExit
import com.qeetgroup.qeetrix.internal.qeetrixFocusFrame
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** The trigger's height (`value` is `@qeetrix/react`'s `size`). */
public enum class QeetrixSelectSize(
    public val value: String,
) {
    /** `size.control.sm`, `space.inset.sm` padding, `body.sm`: beside small buttons in a toolbar. */
    Sm("sm"),

    /** `size.control.md`: beside medium buttons in a toolbar, such as an editor's paragraph style. */
    Md("md"),

    /** `size.control.lg`, a form field's height. The default. */
    Lg("lg"),
}

/**
 * Pick one option from a short list (up to about 15) — `@qeetrix/react`'s Select in a Field. The trigger looks
 * like every field (design rules §8: filled, a bottom line, 4 dp top corners), with the chosen label in `body.md`
 * — or the [placeholder], an example, in `color.text.subtlest` — and a trailing 16 dp chevron in
 * `color.icon.default`. The list opens below it, like a menu.
 *
 * - **Typed choice.** [value] is a value and [onValueChange] receives the chosen option's, so only an option can
 *   be chosen. Without a choice — a value no option has, such as `null` — the trigger shows the [placeholder].
 *   Options need values of their own; disabled ones cannot be chosen. For options under headings, use the
 *   overload that takes [QeetrixOptionGroup]s.
 * - **The list** is the overlay surface (`color.surface.overlay`, a 1 dp `color.border.subtle` edge, 4 dp corners,
 *   `shadow.overlay`): at least as wide as the trigger, at most 320 dp tall and scrolling, below the trigger — above
 *   it when there is more room there. The chosen option is at `label.md`'s weight with a trailing 16 dp check in
 *   `color.icon.brand`; the highlighted one has a neutral tint and a 2 dp Ember start edge. An option's `meta`
 *   shows at its end. It closes on a choice, a tap outside, Back or Escape; opened from the keyboard it focuses
 *   the chosen option.
 * - **States.** The trigger shows the Ember frame on keyboard focus and while its list is open; an [error] (or
 *   [invalid]) draws the danger frame and replaces the [description]. [readOnly] does not open; [enabled] `false`
 *   takes the disabled colours.
 * - **Accessibility.** One drop-down list control named by the [label], with the chosen label as its text; each
 *   option reads as selected or not. Option rows are at least 48 dp tall.
 * - **Open state.** Pass [open] and [onOpenChange] to own it, as react's `open` / `onOpenChange`: the select asks to
 *   open and close through [onOpenChange] and shows its list while [open] is `true`. Left `null`, it keeps it itself.
 * - **Type to jump** (06 §4). On a hardware keyboard, typing moves to the option whose label starts with what was
 *   typed: in the open list the focus moves there; on the closed trigger it is chosen, as in a native list.
 *
 * For several choices, use [QeetrixMultiSelect].
 *
 * ```
 * var cycle by rememberSaveable { mutableStateOf<String?>(null) }
 * QeetrixSelect(cycle, { cycle = it }, listOf(QeetrixOption("monthly", "Monthly"), QeetrixOption("yearly", "Yearly")),
 *     label = "Billing cycle", placeholder = "e.g. Monthly")
 * ```
 */
@Composable
public fun <T> QeetrixSelect(
    value: T,
    onValueChange: (T) -> Unit,
    items: List<QeetrixOption<T>>,
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
    size: QeetrixSelectSize = QeetrixSelectSize.Lg,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    SelectField(
        chosen = { it == value },
        onChoose = { if (it != value) onValueChange(it) },
        multiple = false,
        sections = listOf(SelectSection(null, items)),
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
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = open,
        onOpenChange = onOpenChange,
        interactionSource = interactionSource,
    )
}

/**
 * A [QeetrixSelect] whose options sit under group labels (react's `SelectItemGroup`): each [QeetrixOptionGroup]'s
 * label heads its options in `label.sm` / `color.text.subtle`.
 */
@Composable
@JvmName("QeetrixSelectGrouped") // the flat overload has the same erased signature
public fun <T> QeetrixSelect(
    value: T,
    onValueChange: (T) -> Unit,
    groups: List<QeetrixOptionGroup<T>>,
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
    size: QeetrixSelectSize = QeetrixSelectSize.Lg,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    SelectField(
        chosen = { it == value },
        onChoose = { if (it != value) onValueChange(it) },
        multiple = false,
        sections = groups.map { SelectSection(it.label, it.options) },
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
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = open,
        onOpenChange = onOpenChange,
        interactionSource = interactionSource,
    )
}

/**
 * Pick any number of options from a short list — `@qeetrix/react`'s Select with `multiple`. Everything else is
 * [QeetrixSelect]'s: the field, the list, the states, [open] / [onOpenChange] and type-to-jump in the open list.
 *
 * - **Typed choices.** [values] is the set chosen and [onValuesChange] receives the next set as an option is toggled;
 *   values no option has are ignored. Disabled options cannot be toggled.
 * - **The trigger** lists the chosen labels in the list's order, comma-separated, cut with an ellipsis when they do not
 *   fit (06 Increment 5) — or the [placeholder] when nothing is chosen.
 * - **The list** stays open while options are toggled, each chosen one with the brand check, and closes on a tap
 *   outside, Back or Escape.
 * - **Accessibility.** One drop-down list control named by the [label], its text the chosen labels; each option is a
 *   check box, read as checked or not.
 *
 * ```
 * var regions by rememberSaveable { mutableStateOf(setOf("in")) }
 * QeetrixMultiSelect(regions, { regions = it }, Regions, label = "Data regions", placeholder = "e.g. India")
 * ```
 */
@Composable
public fun <T> QeetrixMultiSelect(
    values: Set<T>,
    onValuesChange: (Set<T>) -> Unit,
    items: List<QeetrixOption<T>>,
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
    size: QeetrixSelectSize = QeetrixSelectSize.Lg,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    MultiSelectField(
        values = values,
        onValuesChange = onValuesChange,
        sections = listOf(SelectSection(null, items)),
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
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = open,
        onOpenChange = onOpenChange,
        interactionSource = interactionSource,
    )
}

/** A [QeetrixMultiSelect] whose options sit under group labels, as [QeetrixSelect]'s grouped overload. */
@Composable
@JvmName("QeetrixMultiSelectGrouped") // the flat overload has the same erased signature
public fun <T> QeetrixMultiSelect(
    values: Set<T>,
    onValuesChange: (Set<T>) -> Unit,
    groups: List<QeetrixOptionGroup<T>>,
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
    size: QeetrixSelectSize = QeetrixSelectSize.Lg,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    open: Boolean? = null,
    onOpenChange: ((Boolean) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    MultiSelectField(
        values = values,
        onValuesChange = onValuesChange,
        sections = groups.map { SelectSection(it.label, it.options) },
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
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = open,
        onOpenChange = onOpenChange,
        interactionSource = interactionSource,
    )
}

@Composable
private fun <T> MultiSelectField(
    values: Set<T>,
    onValuesChange: (Set<T>) -> Unit,
    sections: List<SelectSection<T>>,
    label: String,
    modifier: Modifier,
    placeholder: String?,
    description: String?,
    error: String?,
    invalid: Boolean,
    optional: Boolean,
    required: Boolean,
    hideLabel: Boolean,
    help: String?,
    size: QeetrixSelectSize,
    enabled: Boolean,
    readOnly: Boolean,
    open: Boolean?,
    onOpenChange: ((Boolean) -> Unit)?,
    interactionSource: MutableInteractionSource?,
) {
    SelectField(
        chosen = { it in values },
        onChoose = { onValuesChange(if (it in values) values - it else values + it) },
        multiple = true,
        sections = sections,
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
        size = size,
        enabled = enabled,
        readOnly = readOnly,
        open = open,
        onOpenChange = onOpenChange,
        interactionSource = interactionSource,
    )
}

/** Options under an optional group label. */
internal class SelectSection<out T>(
    val label: String?,
    val options: List<QeetrixOption<T>>,
)

/**
 * The select's field, for one choice or several: [chosen] tells which options are chosen, [onChoose] takes the option
 * picked (the single select replaces its value, the multiple one toggles it), and [multiple] keeps the list open
 * while options are picked. [searchable] opens a sheet with a search field ([SearchSheet]) instead of the popup list.
 */
@Composable
internal fun <T> SelectField(
    chosen: (T) -> Boolean,
    onChoose: (T) -> Unit,
    multiple: Boolean,
    sections: List<SelectSection<T>>,
    label: String,
    modifier: Modifier,
    placeholder: String?,
    description: String?,
    error: String?,
    invalid: Boolean,
    optional: Boolean,
    required: Boolean,
    hideLabel: Boolean,
    help: String?,
    size: QeetrixSelectSize,
    enabled: Boolean,
    readOnly: Boolean,
    open: Boolean?,
    onOpenChange: ((Boolean) -> Unit)?,
    interactionSource: MutableInteractionSource?,
    searchable: Boolean = false,
    searchPlaceholder: String? = null,
) {
    val all = sections.flatMap { it.options }
    requireDistinctValues(all)
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    // Kept here unless the app owns it ([open]); every change is reported to [onOpenChange] either way.
    var openState by remember { mutableStateOf(false) }
    val expanded = open ?: openState
    val setOpen: (Boolean) -> Unit = {
        if (open == null) openState = it
        onOpenChange?.invoke(it)
    }
    // Opened from the keyboard (the trigger had focus), the list moves focus to the chosen option.
    var openedByKeyboard by remember { mutableStateOf(false) }
    val current = all.filter { chosen(it.value) }
    val typeahead = remember { SelectTypeahead() }
    val metrics = triggerMetrics(size)
    val colors = QeetrixTheme.colors

    FieldFrame(description, error, active, modifier) {
        val trigger =
            if (active && !readOnly) {
                Modifier
                    .onPreviewKeyEvent { event ->
                        // Type to jump on the closed trigger of a single select: the matching option is chosen.
                        if (multiple || expanded) return@onPreviewKeyEvent false
                        val character = event.typeaheadCharacter() ?: return@onPreviewKeyEvent false
                        typeahead.next(character, all, all.indexOfFirst { chosen(it.value) })?.let { onChoose(it.value) }
                        true
                    }.clickable(source, indication = null, role = Role.DropdownList) {
                        openedByKeyboard = focused
                        setOpen(true)
                    }
            } else {
                // Read-only or disabled: still read as the drop-down list it is, without an action.
                Modifier
                    .semantics(mergeDescendants = true) { role = Role.DropdownList }
                    .then(if (active) Modifier.focusable(interactionSource = source) else Modifier)
            }
        Box(trigger.fieldError(error)) {
            LabelledControl(label, optional, required, hideLabel, active, help) {
                var anchor by remember { mutableStateOf(IntRect.Zero) }
                Box(Modifier.onGloballyPositioned { anchor = IntRect(it.positionInWindow().round(), it.size) }) {
                    FieldBox(
                        enabled = active,
                        readOnly = readOnly,
                        // .qx-select-trigger:focus-visible, [data-popup-open]
                        focused = (focused || expanded) && active,
                        hovered = hovered,
                        invalid = invalid || error != null,
                        modifier = Modifier.fillMaxWidth(),
                        minHeight = metrics.height,
                        contentPadding = PaddingValues(horizontal = metrics.padding),
                    ) {
                        BasicText(
                            // The chosen labels in the list's order (06 Increment 5), or the placeholder.
                            if (current.isEmpty()) placeholder.orEmpty() else current.joinToString(", ") { it.label },
                            Modifier.weight(1f),
                            style =
                                metrics.textStyle
                                    .copy(
                                        color =
                                            when {
                                                current.isEmpty() -> colors.textSubtlest

                                                // [data-placeholder]
                                                active -> colors.textDefault

                                                else -> colors.textDisabled
                                            },
                                    ).labelDirection(alignToLayout = true),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        QeetrixIcon(
                            QeetrixIconName.ChevronDown,
                            contentDescription = null,
                            size = QeetrixSize.IconSm,
                            shape = QeetrixIconShape.Sharp,
                            tint = if (active) colors.iconDefault else colors.iconDisabled,
                        )
                    }
                    val choose: (T) -> Unit = {
                        if (!multiple) setOpen(false)
                        onChoose(it)
                    }
                    if (searchable) {
                        SearchSheet(expanded, sections, chosen, multiple, label, searchPlaceholder, { setOpen(false) }, choose)
                    } else {
                        OptionList(
                            expanded = expanded,
                            anchor = anchor,
                            sections = sections,
                            chosen = chosen,
                            multiple = multiple,
                            title = label,
                            focusSelected = openedByKeyboard,
                            onDismiss = { setOpen(false) },
                            onChoose = choose,
                        )
                    }
                }
            }
        }
    }
}

/** select.css per size: `block-size`, `padding-inline` and the value's type role. */
private class TriggerMetrics(
    val height: Dp,
    val padding: Dp,
    val textStyle: TextStyle,
)

@Composable
private fun triggerMetrics(size: QeetrixSelectSize): TriggerMetrics {
    val density = QeetrixTheme.density
    val typography = QeetrixTheme.typography
    return when (size) {
        // [data-size="sm"]: block-size size.control.sm; padding-inline space.inset.sm; font body.sm
        QeetrixSelectSize.Sm -> TriggerMetrics(density.sizeControlSm, QeetrixSpace.InsetSm, typography.bodySm)

        // [data-size="md"]: block-size size.control.md
        QeetrixSelectSize.Md -> TriggerMetrics(density.sizeControlMd, QeetrixSpace.InsetMd, typography.bodyMd)

        // .qx-select-trigger: block-size size.control.lg; padding-inline space.inset.md; font body.md
        QeetrixSelectSize.Lg -> TriggerMetrics(density.sizeControlLg, QeetrixSpace.InsetMd, typography.bodyMd)
    }
}

/**
 * The list, in a popup below the trigger (`sideOffset` 4 dp): it fades and slides 4 dp in from the trigger's edge in
 * `motion.duration.normal` and out in `motion.duration.fast`, as select.css's starting and ending styles do.
 */
@Composable
private fun <T> OptionList(
    expanded: Boolean,
    anchor: IntRect,
    sections: List<SelectSection<T>>,
    chosen: (T) -> Boolean,
    multiple: Boolean,
    title: String,
    focusSelected: Boolean,
    onDismiss: () -> Unit,
    onChoose: (T) -> Unit,
) {
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = expanded
    if (!visibility.currentState && !visibility.targetState) return

    val density = LocalDensity.current
    val window = LocalWindowInfo.current.containerSize
    val gap = with(density) { SIDE_OFFSET.roundToPx() }
    val margin = with(density) { QeetrixSpace.InsetSm.roundToPx() }
    val below = window.height - anchor.bottom - gap - margin
    val above = anchor.top - gap - margin
    // max-block-size: min(320px, available height) on the roomier side; the position provider opens it below when it fits.
    val maxHeight = with(density) { minOf(MAX_LIST_HEIGHT, maxOf(above, below, 0).toDp()) }
    val opensAbove = above > below && with(density) { below.toDp() } < MAX_LIST_HEIGHT
    val width = with(density) { anchor.width.toDp() } // min-inline-size: anchor width; max-inline-size: the window

    Popup(
        popupPositionProvider = remember(gap, margin) { AnchoredPopupPosition(gap, margin, PopupAlign.Center) },
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(visibleState = visibility, enter = popupEnter(opensAbove), exit = popupExit(opensAbove)) {
            OptionListSurface(
                sections,
                chosen,
                focusSelected,
                title,
                onDismiss,
                onChoose,
                // As wide as its widest option, at least the trigger and at most the window: the rows fill the list's
                // width, so without the intrinsic width the list took the whole window (found on API 36 devices).
                Modifier.widthIn(min = width).width(IntrinsicSize.Max).heightIn(max = maxHeight),
                multiple = multiple,
            )
        }
    }
}

/**
 * The open list itself — the overlay surface with its options (`.qx-select-popup`: `padding-block: 4px`) — drawn by
 * the popup, and on its own by the screenshot tests, which cannot capture a popup window. Typing on a hardware
 * keyboard moves the focus to the option whose label starts with what was typed. With [multiple], each option is a
 * check box and the options are not one choice.
 */
@Composable
internal fun <T> OptionListSurface(
    sections: List<SelectSection<T>>,
    chosen: (T) -> Boolean,
    focusSelected: Boolean,
    title: String,
    onDismiss: () -> Unit,
    onChoose: (T) -> Unit,
    modifier: Modifier = Modifier,
    multiple: Boolean = false,
) {
    val colors = QeetrixTheme.colors
    val shape = QeetrixShapes.Overlay
    val options = sections.flatMap { it.options }
    val requesters = remember(options) { options.associate { it.value to FocusRequester() } }
    var focusedIndex by remember { mutableIntStateOf(-1) }
    val typeahead = remember { SelectTypeahead() }
    // Opened from the keyboard, focus starts on the first chosen option.
    val firstChosen = options.firstOrNull { chosen(it.value) }?.value
    Column(
        modifier
            .qeetrixShadow(QeetrixShadow.Overlay, shape, colors.surfaceOverlay)
            .border(QeetrixBorder.WidthDefault, colors.borderSubtle, shape)
            .clip(shape)
            .onPreviewKeyEvent { event ->
                // Escape closes on its way down; its key-up is consumed with it, so nothing else sees half a press.
                if (event.key == Key.Escape) {
                    if (event.type == KeyEventType.KeyDown) onDismiss()
                    true
                } else {
                    // Type to jump: the focus moves to the next option whose label starts with what was typed.
                    val character = event.typeaheadCharacter() ?: return@onPreviewKeyEvent false
                    typeahead.next(character, options, focusedIndex)?.let { requesters[it.value]?.requestFocus() }
                    true
                }
            }.verticalScroll(rememberScrollState())
            .padding(vertical = LIST_PADDING)
            .then(if (multiple) Modifier else Modifier.selectableGroup())
            .semantics { paneTitle = title },
    ) {
        for (section in sections) {
            if (section.label != null) GroupLabel(section.label)
            for (option in section.options) {
                key(option.value) {
                    OptionRow(
                        option = option,
                        selected = chosen(option.value),
                        takeFocus = focusSelected && option.value == firstChosen,
                        multiple = multiple,
                        requester = requesters.getValue(option.value),
                        onFocused = { focusedIndex = options.indexOf(option) },
                    ) { onChoose(option.value) }
                }
            }
        }
    }
}

/** `.qx-select-group-label`: `label.sm` in `color.text.subtle`, padding 8 dp, `space.inset.md`, 4 dp. */
@Composable
internal fun GroupLabel(text: String) {
    BasicText(
        text,
        Modifier
            .fillMaxWidth()
            .padding(start = QeetrixSpace.InsetMd, end = QeetrixSpace.InsetMd, top = GROUP_LABEL_TOP, bottom = GROUP_LABEL_BOTTOM)
            .semantics { heading() },
        style =
            QeetrixTheme.typography.labelSm
                .copy(color = QeetrixTheme.colors.textSubtle)
                .labelDirection(alignToLayout = true),
    )
}

/**
 * One option (`.qx-select-option`): `space.inset.md` inline padding, `body.md`, the label then its `meta` and the
 * check, `space.gap.sm` apart. Highlighted — pressed, hovered or keyboard-focused —
 * `color.bg.neutral.subtle-hover` with a `border.width.strong` `color.border.brand` bar on the start edge; chosen,
 * `label.md`'s weight and a 16 dp `check` in `color.icon.brand`; disabled, `color.text.disabled`. The web's row is at
 * least `size.control.md` tall; here at least 48 dp, Android's touch target. A long label wraps and the row grows, the
 * meta and the check staying on its first line (select.css 0.4.0, QTRX-177), so every option can be read.
 */
@Composable
internal fun OptionRow(
    option: QeetrixOption<*>,
    selected: Boolean,
    takeFocus: Boolean,
    multiple: Boolean,
    requester: FocusRequester,
    onFocused: () -> Unit,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val hovered by source.collectIsHoveredAsState()
    val focused by source.collectIsFocusedAsState()
    val highlighted = option.enabled && (pressed || hovered || focused)
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    if (takeFocus) LaunchedEffect(Unit) { requester.requestFocus() }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val bar = colors.borderBrand
    val control = QeetrixTheme.density.sizeControlMd
    // --qx-select-option-line: body.md's line height at the current font scale.
    val line = firstLineHeight(typography.bodyMd)
    FirstLineRow(
        // min-block-size: size.control.md, grown to 48 dp; padding-block: (size.control.md − the option's line) ÷ 2
        minHeight = maxOf(control, QeetrixAccessibility.MinimumTouchTarget),
        padding = ((control - line) / 2).coerceAtLeast(0.dp),
        line = line,
        gap = QeetrixSpace.GapSm,
        modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(requester)
                .onFocusChanged { if (it.isFocused) onFocused() }
                .then(
                    if (multiple) {
                        // One of several: a check box, checked or not.
                        Modifier.toggleable(
                            selected,
                            interactionSource = source,
                            indication = null,
                            enabled = option.enabled,
                            role = Role.Checkbox,
                        ) { onClick() }
                    } else {
                        Modifier.selectable(
                            selected,
                            interactionSource = source,
                            indication = null,
                            enabled = option.enabled,
                            onClick = onClick,
                        )
                    },
                ).background(if (highlighted) colors.bgNeutralSubtleHover else Color.Transparent)
                .drawBehind {
                    if (!highlighted) return@drawBehind
                    // ::before { inline-size: border.width.strong; inset-inline-start: 0; inset-block: 0 }
                    val width = QeetrixBorder.WidthStrong.toPx()
                    drawRect(bar, Offset(if (rtl) size.width - width else 0f, 0f), Size(width, size.height))
                }.padding(horizontal = QeetrixSpace.InsetMd),
        trailing =
            listOfNotNull(
                option.meta?.let {
                    {
                        // .qx-select-option-meta: body.sm, color.text.subtle, tabular-nums; line-height: the option's line
                        BasicText(
                            it,
                            style = typography.bodySm.copy(color = colors.textSubtle, fontFeatureSettings = "tnum").labelDirection(),
                        )
                    }
                },
                if (selected) {
                    {
                        // .qx-select-option-check: margin-block (line − 16px) ÷ 2, on the label's first line
                        QeetrixIcon(
                            QeetrixIconName.Check,
                            contentDescription = null,
                            size = QeetrixSize.IconSm,
                            shape = QeetrixIconShape.Sharp,
                            tint = colors.iconBrand,
                        )
                    }
                } else {
                    null
                },
            ),
    ) {
        val textColor = if (option.enabled) colors.textDefault else colors.textDisabled
        BasicText(
            option.label,
            style =
                typography.bodyMd
                    .copy(color = textColor, fontWeight = if (selected) QeetrixTextWeight.Medium else typography.bodyMd.fontWeight)
                    .labelDirection(alignToLayout = true),
            // .qx-select-option-text: overflow-wrap anywhere — the label wraps and the row grows (QTRX-177), so 200 %
            // text still shows every option in full.
        )
    }
}

/** select.tsx `<Positioner sideOffset={4}>`. */
private val SIDE_OFFSET = 4.dp

/** `.qx-select-popup { max-block-size: min(320px, …) }`. */
private val MAX_LIST_HEIGHT = 320.dp

/** `.qx-select-popup { padding-block: 4px }`. */
private val LIST_PADDING = 4.dp

/** `.qx-select-group-label { padding: 8px space.inset.md 4px }`. */
private val GROUP_LABEL_TOP = 8.dp
private val GROUP_LABEL_BOTTOM = 4.dp
