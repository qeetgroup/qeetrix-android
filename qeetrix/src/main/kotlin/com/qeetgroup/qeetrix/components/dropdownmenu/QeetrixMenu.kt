package com.qeetgroup.qeetrix.components.dropdownmenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.round
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixAccessibility
import com.qeetgroup.qeetrix.foundation.accessibility.QeetrixFocus
import com.qeetgroup.qeetrix.foundation.color.QeetrixOpacity
import com.qeetgroup.qeetrix.foundation.elevation.qeetrixElevation
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixStroke
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.AnchoredPopupPosition
import com.qeetgroup.qeetrix.internal.FontWeightMedium
import com.qeetgroup.qeetrix.internal.PopupAlign
import com.qeetgroup.qeetrix.internal.QeetrixFont
import com.qeetgroup.qeetrix.internal.QeetrixWebScale
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.popupEnter
import com.qeetgroup.qeetrix.internal.popupExit
import com.qeetgroup.qeetrix.internal.qeetrixFocusRing
import com.qeetgroup.qeetrix.internal.qeetrixTextStyle
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import com.qeetgroup.qeetrix.tokens.generated.QeetrixMenuComponentTokens

/** A menu item's style (`value` is the web's `data-variant`). */
public enum class QeetrixMenuItemVariant(
    public val value: String,
) {
    Default("default"),

    /** An action that deletes or removes: the danger text colour and a danger-tinted highlight. */
    Destructive("destructive"),
}

/** What [QeetrixMenu]'s content can hold: [QeetrixMenuItem], [QeetrixMenuLabel] and [QeetrixMenuSeparator]. */
@Stable
public sealed interface QeetrixMenuScope

/**
 * A menu of actions that opens from a [trigger] — the web's `DropdownMenu`: a short list of
 * [QeetrixMenuItem]s, optionally grouped under [QeetrixMenuLabel]s and divided by [QeetrixMenuSeparator]s,
 * on the overlay surface below the trigger (above it when there is no room below).
 *
 * - **The app owns [expanded].** The [trigger] — usually a [com.qeetgroup.qeetrix.components.button.QeetrixIconButton]
 *   named for what it opens ("More options") — calls `onExpandedChange(true)`. A tap outside, Back, Escape
 *   or choosing an item calls `onExpandedChange(false)`.
 * - **Choosing an item** closes the menu first and then runs its `onClick`, once. The item's action is the
 *   app's; the menu routes nothing anywhere.
 * - **Keyboard.** Opened from a keyboard, focus starts on the first available item; the arrow keys move
 *   between items, Enter chooses, Escape closes, and focus returns to the trigger.
 * - **Accessibility.** Items are read with their labels; a disabled item is read as unavailable; a
 *   destructive item is marked by its words and its danger colour, never by colour alone. Labels are
 *   headings. Every row is at least 48 dp tall — the web's coarse-pointer rule at Android's size.
 * - **Drawing.** `component.menu.*`: the overlay surface at least as wide as the trigger and 128 dp, at most
 *   320 dp; items in the menu item corner, highlighted with the neutral fill (or the danger tint) under the
 *   pointer, the finger or keyboard focus. It fades and scales in from the trigger, and holds still under
 *   reduced motion.
 *
 * For a choice of one value from a list, use [com.qeetgroup.qeetrix.components.select.QeetrixSelect]; for
 * rich content anchored to a control, a [com.qeetgroup.qeetrix.components.drawer.QeetrixBottomSheet] or a
 * [com.qeetgroup.qeetrix.components.dialog.QeetrixDialog].
 *
 * ```
 * var open by remember { mutableStateOf(false) }
 * QeetrixMenu(
 *     expanded = open,
 *     onExpandedChange = { open = it },
 *     trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { open = true }) },
 * ) {
 *     QeetrixMenuItem("Rename", onClick = ::rename, icon = QeetrixIconName.Pencil)
 *     QeetrixMenuItem("Duplicate", onClick = ::duplicate, icon = QeetrixIconName.Copy)
 *     QeetrixMenuSeparator()
 *     QeetrixMenuItem("Delete", onClick = ::confirmDelete, icon = QeetrixIconName.Trash, variant = QeetrixMenuItemVariant.Destructive)
 * }
 * ```
 */
@Composable
public fun QeetrixMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    trigger: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable QeetrixMenuScope.() -> Unit,
) {
    var anchor by remember { mutableStateOf(IntRect.Zero) }
    Box(modifier.onGloballyPositioned { anchor = IntRect(it.positionInWindow().round(), it.size) }) {
        trigger()
        MenuPopup(expanded, anchor, { onExpandedChange(false) }, content)
    }
}

/** One action in a [QeetrixMenu]: its [label], an optional [icon] at the start, its [variant], whether it is [enabled]. */
@Composable
public fun QeetrixMenuScope.QeetrixMenuItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: QeetrixIconName? = null,
    variant: QeetrixMenuItemVariant = QeetrixMenuItemVariant.Default,
    enabled: Boolean = true,
) {
    val scope = this as MenuScopeImpl
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val hovered by source.collectIsHoveredAsState()
    val focused by source.collectIsFocusedAsState()
    val colors = QeetrixTheme.colors
    val destructive = variant == QeetrixMenuItemVariant.Destructive
    val shape = RoundedCornerShape(QeetrixMenuComponentTokens.ItemCorner) // rounded-(--qx-component-menu-item-corner)
    val highlighted = enabled && (pressed || hovered || focused)
    // data-highlighted:bg-accent, and data-[variant=destructive]:data-highlighted:bg-(--qx-component-menu-item-danger-highlight)
    val fill =
        when {
            !highlighted -> Color.Transparent
            destructive -> QeetrixMenuComponentTokens.ItemDangerHighlight
            else -> colors.surfaceInteractive
        }
    val content = if (destructive) colors.textDanger else QeetrixMenuComponentTokens.Foreground // text-destructive-text
    Row(
        modifier
            .fillMaxWidth()
            .qeetrixFocusRing(source, shape, offset = -QeetrixFocus.OutlineWidth) // focus-visible:focus-ring-inset
            .clip(shape)
            .background(fill)
            .clickable(source, indication = null, enabled = enabled) { scope.choose(onClick) }
            .then(if (enabled) Modifier else Modifier.alpha(QeetrixOpacity.Disabled))
            // min-h-(--qx-component-menu-item-height), and at least the 48 dp touch target
            .heightIn(min = maxOf(QeetrixMenuComponentTokens.ItemHeight, QeetrixAccessibility.MinimumTouchTarget))
            .padding(horizontal = QeetrixWebScale.spacing(2f), vertical = QeetrixWebScale.spacing(0.5f)), // px-2 py-0.5
        horizontalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(2f)), // gap-2
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) QeetrixIcon(icon, contentDescription = null, size = QeetrixWebScale.spacing(4f), tint = content) // size-4
        BasicText(label, Modifier.weight(1f), style = ItemStyle.copy(color = content).labelDirection(alignToLayout = true))
    }
}

/** A heading over the items that follow it: `px-2 py-1.5 font-ui text-xs font-medium text-muted-foreground`. */
@Composable
public fun QeetrixMenuScope.QeetrixMenuLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text,
        modifier
            .fillMaxWidth()
            .semantics { heading() }
            .padding(horizontal = QeetrixWebScale.spacing(2f), vertical = QeetrixWebScale.spacing(1.5f)),
        style = LabelStyle.copy(color = QeetrixTheme.colors.textTertiary).labelDirection(alignToLayout = true),
    )
}

/** A rule between groups of items: `-mx-1 my-1 h-px bg-border`, edge to edge. Decorative: accessibility services skip it. */
@Composable
public fun QeetrixMenuScope.QeetrixMenuSeparator(modifier: Modifier = Modifier) {
    val bleed = QeetrixWebScale.spacing(1f)
    Box(
        modifier
            .padding(vertical = QeetrixWebScale.spacing(1f))
            .layout { measurable, constraints ->
                // -mx-1: through the surface's padding to its edges
                val extra = bleed.roundToPx() * 2
                val width = if (constraints.hasBoundedWidth) constraints.maxWidth + extra else 0
                val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
                layout(constraints.maxWidth.takeIf { constraints.hasBoundedWidth } ?: 0, placeable.height) {
                    placeable.place(-extra / 2, 0)
                }
            }.height(QeetrixStroke.Hairline)
            .background(QeetrixTheme.colors.borderDefault),
    )
}

/** The open menu, in a focusable popup below the trigger: it fades and scales in from the trigger's edge, and out again. */
@Composable
private fun MenuPopup(
    expanded: Boolean,
    anchor: IntRect,
    onDismiss: () -> Unit,
    content: @Composable QeetrixMenuScope.() -> Unit,
) {
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = expanded
    if (!visibility.currentState && !visibility.targetState) return

    val dismiss by rememberUpdatedState(onDismiss)
    val scope = remember { MenuScopeImpl() }
    scope.onDismiss = dismiss
    // A new opening may choose again.
    LaunchedEffect(expanded) { if (expanded) scope.chosen = false }

    val density = LocalDensity.current
    val window = LocalWindowInfo.current.containerSize
    val gap = with(density) { QeetrixWebScale.spacing(1f).roundToPx() } // sideOffset 4
    val margin = with(density) { QeetrixWebScale.spacing(2f).roundToPx() }
    val below = window.height - anchor.bottom - gap - margin
    val above = anchor.top - gap - margin
    val maxHeight = with(density) { maxOf(above, below, 0).toDp() } // max-h-(--available-height)
    val opensAbove = above > below
    val minWidth = with(density) { maxOf(anchor.width.toDp(), MinWidth) } // min-w-[max(var(--anchor-width),8rem)]
    val maxWidth = with(density) { minOf(MaxWidth, (window.width - 2 * margin).toDp()) } // max-w-[min(20rem,…)]

    Popup(
        popupPositionProvider = remember(gap, margin) { AnchoredPopupPosition(gap, margin, PopupAlign.Start) },
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(visibleState = visibility, enter = popupEnter(opensAbove), exit = popupExit(opensAbove)) {
            MenuSurface(
                Modifier.widthIn(min = minOf(minWidth, maxWidth), max = maxWidth).heightIn(max = maxHeight),
                focusFirst = LocalInputModeManager.current.inputMode == InputMode.Keyboard,
                onDismiss = onDismiss,
                scope = scope,
                content = content,
            )
        }
    }
}

/**
 * The open menu itself — the overlay surface and its items — drawn by the popup, and on its own by the
 * screenshot tests, which cannot capture a popup window.
 */
@Composable
internal fun MenuSurface(
    modifier: Modifier,
    focusFirst: Boolean,
    onDismiss: () -> Unit,
    scope: QeetrixMenuScope = remember { MenuScopeImpl() },
    content: @Composable QeetrixMenuScope.() -> Unit,
) {
    val shape = RoundedCornerShape(QeetrixMenuComponentTokens.Corner) // rounded-(--qx-component-menu-corner)
    val first = remember { FocusRequester() }
    if (focusFirst) LaunchedEffect(Unit) { first.requestFocus() }
    Column(
        modifier
            .qeetrixElevation(QeetrixMenuComponentTokens.Elevation, shape, QeetrixMenuComponentTokens.Background)
            .border(QeetrixStroke.Hairline, QeetrixMenuComponentTokens.Border, shape)
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
            // Asked for focus, the column gives it to its first focusable item.
            .focusRequester(first),
    ) {
        scope.content()
    }
}

/** Routes an item's choice: closes the menu, then runs the item's action — once per opening. */
private class MenuScopeImpl : QeetrixMenuScope {
    var onDismiss: () -> Unit = {}
    var chosen = false

    fun choose(action: () -> Unit) {
        if (chosen) return
        chosen = true
        onDismiss()
        action()
    }
}

/** min-w 8rem. */
private val MinWidth = QeetrixWebScale.spacing(32f)

/** max-w 20rem. */
private val MaxWidth = QeetrixWebScale.spacing(80f)

/** font-ui text-sm. */
private val ItemStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextSm)

/** font-ui text-xs font-medium. */
private val LabelStyle = qeetrixTextStyle(QeetrixFont.Ui, QeetrixWebScale.TextXs, FontWeightMedium)
