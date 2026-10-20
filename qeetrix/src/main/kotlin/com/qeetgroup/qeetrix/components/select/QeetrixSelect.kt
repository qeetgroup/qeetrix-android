package com.qeetgroup.qeetrix.components.select

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.round
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.qeetgroup.qeetrix.components.input.FieldBox
import com.qeetgroup.qeetrix.components.input.FieldFrame
import com.qeetgroup.qeetrix.components.input.LabelledControl
import com.qeetgroup.qeetrix.components.input.LocalFieldGroup
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.fieldError
import com.qeetgroup.qeetrix.components.input.fieldTextStyle
import com.qeetgroup.qeetrix.components.input.requireDistinctValues
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.QeetrixElevation
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.foundation.shape.QeetrixCorner
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.AnchoredPopupPosition
import com.qeetgroup.qeetrix.internal.PopupAlign
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.popupEnter
import com.qeetgroup.qeetrix.internal.popupExit
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixInputComponentTokens

/** The trigger sizes of [QeetrixSelect] (`value` is the web name). */
public enum class QeetrixSelectSize(
    public val value: String,
) {
    /** The control height of the current density, 32 dp by default. */
    Default("default"),

    /** One step under the control height (28 dp by default), with the smaller corner. */
    Sm("sm"),
}

/**
 * Choose one of a short list of [options] — the web's `Select` in a field: a trigger drawn with the field
 * recipe, and a list that opens below it (above, when there is more room there).
 *
 * - **Typed choice.** [selected] is a value and [onSelect] receives the chosen option's value, so only an
 *   option can be chosen. Without a selection — a value no option has, such as `null` — the trigger shows
 *   the [placeholder]. Options need values of their own; disabled options cannot be chosen.
 * - **The list** is a popup in the overlay surface: as wide as the trigger, scrolling when long, with a
 *   brand-tinted row and a check for the selection. It closes on a choice, a tap outside, Back or Escape,
 *   and opened from the keyboard it focuses the selected option.
 * - **Accessibility.** The trigger is one drop-down list control named by the [label], with the selected
 *   option's label as its text; each option reads as selected or not selected. An error [message] marks the
 *   trigger invalid. Option rows are at least 48 dp tall.
 * - **States.** [readOnly] shows the value with a dashed boundary and does not open; [enabled] `false`
 *   fades it. While open the boundary keeps its hover colour and the chevron turns.
 *
 * Searching, loading options remotely and choosing several are other components' work.
 *
 * ```
 * var country by rememberSaveable { mutableStateOf<String?>(null) }
 * QeetrixSelect(country, { country = it }, countries.map { QeetrixOption(it.code, it.name) },
 *     label = "Country", placeholder = "Choose a country")
 * ```
 */
@Composable
public fun <T> QeetrixSelect(
    selected: T,
    onSelect: (T) -> Unit,
    options: List<QeetrixOption<T>>,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    description: String? = null,
    message: QeetrixFieldMessage? = null,
    size: QeetrixSelectSize = QeetrixSelectSize.Default,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    required: Boolean = false,
    optional: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    requireDistinctValues(options)
    val active = enabled && LocalFieldGroup.current.enabled
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val hovered by source.collectIsHoveredAsState()
    var expanded by remember { mutableStateOf(false) }
    // Opened from the keyboard (the trigger had focus), the list moves focus to the selected option.
    var openedByKeyboard by remember { mutableStateOf(false) }
    val current = options.firstOrNull { it.value == selected }
    val small = size == QeetrixSelectSize.Sm
    val textStyle = fieldTextStyle()
    val chevronTurn by animateFloatAsState(
        if (expanded) 180f else 0f,
        qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast),
        label = "select-chevron",
    )

    FieldFrame(description, message, modifier) {
        val trigger =
            if (active && !readOnly) {
                Modifier.clickable(source, indication = null, role = Role.DropdownList) {
                    openedByKeyboard = focused
                    expanded = true
                }
            } else {
                // Read-only or disabled: still read as the drop-down list it is, without an action.
                Modifier
                    .semantics(mergeDescendants = true) {
                        role = Role.DropdownList
                    }.then(if (active) Modifier.focusable(interactionSource = source) else Modifier)
            }
        Box(trigger.fieldError(message)) {
            LabelledControl(label, required, optional, active) {
                var anchor by remember { mutableStateOf(IntRect.Zero) }
                Box(Modifier.onGloballyPositioned { anchor = IntRect(it.positionInWindow().round(), it.size) }) {
                    FieldBox(
                        enabled = active,
                        readOnly = readOnly,
                        focused = focused,
                        hovered = hovered,
                        tone = message?.tone,
                        modifier = Modifier.fillMaxWidth(),
                        holdHover = expanded,
                        shape = RoundedCornerShape(if (small) QeetrixCorner.Chip else QeetrixInputComponentTokens.Corner),
                        minHeight = triggerHeight(size),
                        contentPadding = if (small) PaddingSm else PaddingDefault,
                        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(1.5f)), // gap-1.5
                    ) {
                        val textColor =
                            if (current == null) QeetrixInputComponentTokens.Placeholder else QeetrixInputComponentTokens.Foreground
                        BasicText(
                            current?.label ?: placeholder.orEmpty(),
                            Modifier.weight(1f),
                            style = textStyle.copy(color = textColor),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        // size-4 text-muted-foreground, turning while open
                        QeetrixIcon(
                            QeetrixIconName.ChevronDown,
                            contentDescription = null,
                            modifier = Modifier.rotate(chevronTurn),
                            size = QeetrixWebScale.spacing(4f),
                            tint = QeetrixTheme.colors.textTertiary,
                        )
                    }
                    OptionList(
                        expanded = expanded,
                        anchor = anchor,
                        options = options,
                        selected = selected,
                        title = label ?: placeholder,
                        focusSelected = openedByKeyboard,
                        onDismiss = { expanded = false },
                        onChoose = {
                            expanded = false
                            if (it != selected) onSelect(it)
                        },
                    )
                }
            }
        }
    }
}

/** The list, in a popup below the trigger: it fades and scales in from the trigger's edge, and out again. */
@Composable
private fun <T> OptionList(
    expanded: Boolean,
    anchor: IntRect,
    options: List<QeetrixOption<T>>,
    selected: T,
    title: String?,
    focusSelected: Boolean,
    onDismiss: () -> Unit,
    onChoose: (T) -> Unit,
) {
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = expanded
    if (!visibility.currentState && !visibility.targetState) return

    val density = LocalDensity.current
    val window = LocalWindowInfo.current.containerSize
    val gap = with(density) { QeetrixWebScale.spacing(1f).roundToPx() } // sideOffset 4
    val margin = with(density) { QeetrixWebScale.spacing(2f).roundToPx() }
    val below = window.height - anchor.bottom - gap - margin
    val above = anchor.top - gap - margin
    // As tall as the roomier side allows; the position provider then opens it below when it fits there.
    val maxHeight = with(density) { maxOf(above, below, 0).toDp() }
    val opensAbove = above > below
    val width = with(density) { maxOf(anchor.width.toDp(), MinListWidth) } // w-(--anchor-width) min-w-36

    Popup(
        popupPositionProvider = remember(gap, margin) { AnchoredPopupPosition(gap, margin, PopupAlign.Center) },
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(visibleState = visibility, enter = popupEnter(opensAbove), exit = popupExit(opensAbove)) {
            OptionListSurface(options, selected, focusSelected, title, onDismiss, onChoose, Modifier.width(width).heightIn(max = maxHeight))
        }
    }
}

/**
 * The open list itself — the overlay surface with its options — drawn by the popup, and on its own by the
 * screenshot tests, which cannot capture a popup window.
 */
@Composable
internal fun <T> OptionListSurface(
    options: List<QeetrixOption<T>>,
    selected: T,
    focusSelected: Boolean,
    title: String?,
    onDismiss: () -> Unit,
    onChoose: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(QeetrixCorner.Overlay)
    Column(
        modifier
            .qeetrixElevation(QeetrixElevation.Overlay, shape, QeetrixTheme.colors.surfaceOverlay) // shadow-popover bg-popover
            .border(QeetrixStroke.Hairline, QeetrixTheme.colors.borderDefault, shape)
            .clip(shape)
            .onPreviewKeyEvent { event ->
                // Escape closes on its way down; its key-up is consumed with it, so nothing else sees half a press.
                if (event.key == Key.Escape) {
                    if (event.type == KeyEventType.KeyDown) onDismiss()
                    true
                } else {
                    false
                }
            }.verticalScroll(rememberScrollState())
            .padding(QeetrixWebScale.spacing(1f)) // p-1
            .selectableGroup()
            .semantics { if (title != null) paneTitle = title },
    ) {
        for (option in options) {
            key(option.value) {
                OptionRow(option, option.value == selected, focusSelected && option.value == selected) { onChoose(option.value) }
            }
        }
    }
}

/**
 * One option: the neutral fill when pressed, hovered or focused; the brand tint with a brand check when
 * selected (deepening one step under the pointer); an inset ring for keyboard focus.
 */
@Composable
private fun OptionRow(
    option: QeetrixOption<*>,
    selected: Boolean,
    takeFocus: Boolean,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val hovered by source.collectIsHoveredAsState()
    val focused by source.collectIsFocusedAsState()
    val lit = pressed || hovered || focused
    val shape = RoundedCornerShape(QeetrixCorner.Chip) // rounded-md
    val requester = remember { FocusRequester() }
    if (takeFocus) LaunchedEffect(Unit) { requester.requestFocus() }
    // data-[selected]:bg-brand-subtle, one step deeper when highlighted; data-highlighted:bg-accent
    val fill =
        when {
            selected && lit -> QeetrixTheme.colors.surfaceBrandSubtleHover
            selected -> QeetrixTheme.colors.surfaceBrandSubtle
            lit -> QeetrixTheme.colors.surfaceInteractive
            else -> Color.Transparent
        }
    Row(
        Modifier
            .fillMaxWidth()
            .focusRequester(requester)
            .qeetrixFocusRing(source, shape, offset = -QeetrixFocus.OutlineWidth) // focus-visible:focus-ring-inset
            .clip(shape)
            .background(fill)
            .selectable(selected, interactionSource = source, indication = null, enabled = option.enabled, onClick = onClick)
            .then(if (option.enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled))
            // At least a 48 dp touch target — the web's coarse-pointer rule, at Android's size.
            .heightIn(min = QeetrixAccessibility.MinimumTouchTarget)
            .padding(horizontal = QeetrixWebScale.spacing(2f), vertical = QeetrixWebScale.spacing(1f)), // ps-2 py-1
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)), // gap-2
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            BasicText(option.label, style = OptionStyle.copy(color = QeetrixTheme.colors.textPrimary)) // long labels wrap
            if (option.description != null) {
                BasicText(option.description, style = OptionDescriptionStyle.copy(color = QeetrixTheme.colors.textTertiary))
            }
        }
        // The check at the inline end, text-brand, strokeWidth 2.5; the row keeps its room when unselected.
        Box(Modifier.width(QeetrixWebScale.spacing(4f)), contentAlignment = Alignment.Center) {
            if (selected) {
                QeetrixIcon(
                    QeetrixIconName.Check,
                    contentDescription = null,
                    size = QeetrixWebScale.spacing(4f),
                    stroke = CHECK_STROKE,
                    tint = QeetrixTheme.colors.textBrand,
                )
            }
        }
    }
}

@Composable
private fun triggerHeight(size: QeetrixSelectSize): Dp {
    val height = QeetrixInputComponentTokens.height(QeetrixTheme.density)
    return if (size == QeetrixSelectSize.Sm) height - QeetrixWebScale.spacing(1f) else height // h-[calc(…-0.25rem)]
}

/** ps-2.5 pe-2 inside the 1 dp border; vertically centred in the control height, as a text field. */
private val PaddingDefault =
    PaddingValues(
        start = QeetrixWebScale.spacing(2.5f) + QeetrixStroke.Hairline,
        end = QeetrixWebScale.spacing(2f) + QeetrixStroke.Hairline,
        top = QeetrixStroke.Hairline,
        bottom = QeetrixStroke.Hairline,
    )

/** data-[size=sm]:ps-2 pe-1.5. */
private val PaddingSm =
    PaddingValues(
        start = QeetrixWebScale.spacing(2f) + QeetrixStroke.Hairline,
        end = QeetrixWebScale.spacing(1.5f) + QeetrixStroke.Hairline,
        top = QeetrixStroke.Hairline,
        bottom = QeetrixStroke.Hairline,
    )

/** min-w-36. */
private val MinListWidth = QeetrixWebScale.spacing(36f)

private val OptionStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm)
private val OptionDescriptionStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs)
private const val CHECK_STROKE = 2.5f
