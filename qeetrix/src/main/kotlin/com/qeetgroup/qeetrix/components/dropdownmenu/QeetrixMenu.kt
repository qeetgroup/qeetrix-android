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
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.qeetgroup.qeetrix.components.dialog.keyboardInput
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
import com.qeetgroup.qeetrix.internal.PopupAlign
import com.qeetgroup.qeetrix.internal.firstLineHeight
import com.qeetgroup.qeetrix.internal.labelDirection
import com.qeetgroup.qeetrix.internal.popupEnter
import com.qeetgroup.qeetrix.internal.popupExit
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** A menu item's meaning (`value` is `@qeetrix/react`'s MenuItem `tone`). */
public enum class QeetrixMenuItemTone(
    public val value: String,
) {
    /** The default. */
    Default("default"),

    /**
     * An action that deletes or removes: text in `color.text.danger`, its icon in `color.icon.danger`. Put it last,
     * after a separator; its words say what it does, so the colour is never the only signal.
     */
    Danger("danger"),
}

/**
 * What [QeetrixMenu]'s content can hold: [QeetrixMenuItem], [QeetrixMenuCheckboxItem], [QeetrixMenuRadioGroup],
 * [QeetrixMenuGroupLabel] and [QeetrixMenuSeparator].
 */
@Stable
public sealed interface QeetrixMenuScope

/** A [QeetrixMenuRadioGroup]'s content: [QeetrixMenuRadioItem]s choosing a [T], and anything a menu holds. */
@Stable
public sealed interface QeetrixMenuRadioGroupScope<T> : QeetrixMenuScope

/**
 * A list of actions or options that opens from a [trigger] — `@qeetrix/react`'s Menu: [QeetrixMenuItem]s,
 * checkbox and radio items, group labels and separators, on `color.surface.overlay` with a 1 dp
 * `color.border.subtle` edge, `radius.overlay` corners and `shadow.overlay`, below the trigger and 4 dp from it
 * (above it when there is no room below).
 *
 * - **The app owns [open].** The [trigger] — usually a [com.qeetgroup.qeetrix.components.button.QeetrixIconButton]
 *   named for what it opens ("More options") — calls `onOpenChange(true)`. A tap outside, Back, Escape or choosing
 *   an item calls `onOpenChange(false)`.
 * - **Choosing an item** closes the menu first and then runs it, once. What the item does is the app's.
 * - **Keyboard.** Opened from a keyboard, focus starts on the first available item; the arrow keys move between
 *   items, Enter chooses, Escape closes, and focus returns to the trigger. The menu never traps Tab.
 * - **Highlight.** The item under the finger, the pointer or keyboard focus takes the neutral tint
 *   (`color.bg.neutral.subtle-hover`) and the 2 dp Ember start edge (`border.width.strong`, `color.border.brand`),
 *   on the right in a right-to-left layout — menu.css's `[data-highlighted]`, which is also its focus indicator.
 * - **Rows** run edge to edge with `space.inset.md` inline padding and `body.md` text; they are 48 dp tall — the
 *   web's `size.control.md` grown to Android's touch target — and a long label wraps, the row growing while the icon,
 *   mark and shortcut stay on its first line (menu.css 0.4.0, QTRX-177), so large text is never cut. The popup is 200 to 320 dp wide, with 4 dp above the
 *   first item and below the last. It fades and slides 4 dp from its trigger in `motion.duration.normal`, and
 *   leaves in `motion.duration.fast`; nothing moves under reduced motion.
 * - **Accessibility.** Items are read with their labels and descriptions; a checkbox item is a checkbox and a radio
 *   item a radio button, with their states; a disabled item is read as unavailable; group labels are headings.
 *   Shortcuts are hidden from accessibility services, as on the web.
 *
 * For one value from a long list, use a select; for content anchored to a control, a sheet or a dialog.
 *
 * ```
 * QeetrixMenu(
 *     open = open,
 *     onOpenChange = { open = it },
 *     trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { open = true }) },
 * ) {
 *     QeetrixMenuItem("Rename", onClick = ::rename, icon = QeetrixIconName.Pencil)
 *     QeetrixMenuItem("Duplicate", onClick = ::duplicate, icon = QeetrixIconName.Copy, shortcut = "Ctrl+D")
 *     QeetrixMenuSeparator()
 *     QeetrixMenuItem("Delete", onClick = ::confirmDelete, icon = QeetrixIconName.Trash, tone = QeetrixMenuItemTone.Danger)
 * }
 * ```
 */
@Composable
public fun QeetrixMenu(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    trigger: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable QeetrixMenuScope.() -> Unit,
) {
    var anchor by remember { mutableStateOf(IntRect.Zero) }
    val change by rememberUpdatedState(onOpenChange)
    Box(modifier.onGloballyPositioned { anchor = IntRect(it.positionInWindow().round(), it.size) }) {
        trigger()
        MenuPopup(open, anchor, { change(false) }, content)
    }
}

/**
 * An action in a [QeetrixMenu] (`@qeetrix/react`'s MenuItem): its [text], an optional leading [icon] (16 dp, sharp,
 * `color.icon.subtle`), a [shortcut] at the end in `code.sm` `color.text.subtlest`, an optional [description] — a
 * subtle `body.sm` second line that wraps — and its [tone]. Choosing it runs [onClick]; with [closeOnClick] (the
 * default) the menu closes first.
 */
@Composable
public fun QeetrixMenuScope.QeetrixMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: QeetrixIconName? = null,
    shortcut: String? = null,
    description: String? = null,
    tone: QeetrixMenuItemTone = QeetrixMenuItemTone.Default,
    enabled: Boolean = true,
    closeOnClick: Boolean = true,
) {
    val menu = menuOf(this)
    MenuRow(
        text = text,
        modifier = modifier,
        enabled = enabled,
        tone = tone,
        shortcut = shortcut,
        description = description,
        interaction = { source ->
            Modifier.clickable(source, indication = null, enabled = enabled) { menu.choose(closeOnClick, onClick) }
        },
        leading =
            icon?.let {
                {
                    icon,
                    _,
                    ->
                    QeetrixIcon(it, contentDescription = null, size = QeetrixSize.IconSm, shape = QeetrixIconShape.Sharp, tint = icon)
                }
            },
    )
}

/**
 * An option that turns on or off (`@qeetrix/react`'s MenuCheckboxItem): a sharp 16 dp check in the start column
 * marks it [checked], and accessibility services read it as a checkbox with its state. Choosing it calls
 * [onCheckedChange] with the new state; the menu stays open unless [closeOnClick], so several options can be set
 * in a row.
 */
@Composable
public fun QeetrixMenuScope.QeetrixMenuCheckboxItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shortcut: String? = null,
    description: String? = null,
    enabled: Boolean = true,
    closeOnClick: Boolean = false,
) {
    val menu = menuOf(this)
    MenuRow(
        text = text,
        modifier = modifier,
        enabled = enabled,
        tone = QeetrixMenuItemTone.Default,
        shortcut = shortcut,
        description = description,
        interaction = { source ->
            Modifier.toggleable(checked, source, indication = null, enabled = enabled, role = Role.Checkbox) { next ->
                menu.choose(closeOnClick) { onCheckedChange(next) }
            }
        },
        leading = { _, text ->
            // .qx-menu-mark-indicator takes the item's colour (currentColor), not the icon colour.
            Mark {
                if (checked) QeetrixIcon(QeetrixIconName.Check, contentDescription = null, size = QeetrixSize.IconSm, tint = text)
            }
        },
    )
}

/**
 * Radio items sharing one [value] (`@qeetrix/react`'s MenuRadioGroup): choosing one calls [onValueChange] with its
 * value. Put [QeetrixMenuRadioItem]s in [content], with a [QeetrixMenuGroupLabel] above them to say what they choose.
 */
@Composable
public fun <T> QeetrixMenuScope.QeetrixMenuRadioGroup(
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable QeetrixMenuRadioGroupScope<T>.() -> Unit,
) {
    val menu = menuOf(this)
    val group = remember(menu) { RadioGroupScopeImpl<T>(menu) }
    group.value = value
    group.onValueChange = onValueChange
    Column(modifier.fillMaxWidth().selectableGroup()) { group.content() }
}

/**
 * One choice of a [QeetrixMenuRadioGroup] (`@qeetrix/react`'s MenuRadioItem): a 6 dp dot in the start column marks
 * the chosen one, and accessibility services read it as a radio button with its state. Choosing it sets the group's
 * value to [value]; with [closeOnClick] (the default, as a menu that picks one value should) the menu closes first.
 */
@Composable
public fun <T> QeetrixMenuRadioGroupScope<T>.QeetrixMenuRadioItem(
    text: String,
    value: T,
    modifier: Modifier = Modifier,
    shortcut: String? = null,
    description: String? = null,
    enabled: Boolean = true,
    closeOnClick: Boolean = true,
) {
    val group = this as RadioGroupScopeImpl<T>
    val selected = group.value == value
    MenuRow(
        text = text,
        modifier = modifier,
        enabled = enabled,
        tone = QeetrixMenuItemTone.Default,
        shortcut = shortcut,
        description = description,
        interaction = { source ->
            Modifier.selectable(selected, source, indication = null, enabled = enabled, role = Role.RadioButton) {
                group.menu.choose(closeOnClick) { group.onValueChange(value) }
            }
        },
        leading = { _, text ->
            Mark {
                // .qx-menu-dot: 6px, radius.full, currentColor
                if (selected) Box(Modifier.size(RadioDot).background(text, QeetrixShapes.Full))
            }
        },
    )
}

/**
 * A label over the items that follow it (`@qeetrix/react`'s MenuGroupLabel): `label.sm` in `color.text.subtle`,
 * `8px space.inset.md 4px` padding. Accessibility services read it as a heading.
 */
@Composable
public fun QeetrixMenuScope.QeetrixMenuGroupLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    BasicText(
        text,
        modifier
            .fillMaxWidth()
            .semantics { heading() }
            .padding(start = QeetrixSpace.InsetMd, top = 8.dp, end = QeetrixSpace.InsetMd, bottom = 4.dp), // padding: 8px inset.md 4px
        style =
            QeetrixTheme.typography.labelSm
                .copy(color = QeetrixTheme.colors.textSubtle)
                .labelDirection(alignToLayout = true),
    )
}

/**
 * A 1 dp `color.border.subtle` rule between groups of items, edge to edge, 4 dp above and below
 * (`@qeetrix/react`'s MenuSeparator). Decorative: accessibility services skip it.
 */
@Composable
public fun QeetrixMenuScope.QeetrixMenuSeparator(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp) // margin-block: 4px
            .height(QeetrixBorder.WidthDefault) // block-size: border.width.default
            .background(QeetrixTheme.colors.borderSubtle),
    )
}

/**
 * One row of a menu (`.qx-menu-item`): edge to edge, `space.inset.md` inline padding, `space.gap.sm` between its
 * parts, at least [QeetrixAccessibility.MinimumTouchTarget] tall (the web's `size.control.md`), highlighted while
 * pressed, hovered or focused. [interaction] makes it clickable, toggleable or selectable on the row's interaction
 * source, which merges its text into one announcement; [leading] draws the icon (in the item's icon colour) or the
 * check mark (in its text colour).
 */
@Composable
private fun MenuRow(
    text: String,
    modifier: Modifier,
    enabled: Boolean,
    tone: QeetrixMenuItemTone,
    shortcut: String?,
    description: String?,
    interaction: (MutableInteractionSource) -> Modifier,
    leading: (@Composable (icon: Color, text: Color) -> Unit)?,
) {
    val colors = QeetrixTheme.colors
    val typography = QeetrixTheme.typography
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val hovered by source.collectIsHoveredAsState()
    val focused by source.collectIsFocusedAsState()
    val highlighted = enabled && (pressed || hovered || focused || LocalHighlightedMenuItem.current == text)
    val danger = tone == QeetrixMenuItemTone.Danger
    val textColor =
        when {
            !enabled -> colors.textDisabled

            // [data-disabled]
            danger -> colors.textDanger

            // [data-tone="danger"]
            else -> colors.textDefault
        }
    val iconColor =
        when {
            !enabled -> colors.iconDisabled
            danger -> colors.iconDanger
            else -> colors.iconSubtle // .qx-menu-item-icon
        }
    val tint = colors.bgNeutralSubtleHover
    val edge = colors.borderBrand
    val control = QeetrixTheme.density.sizeControlMd
    // The label's line: --qx-menu-item-line, body.md's line height at the current font scale.
    val line = firstLineHeight(typography.bodyMd)
    FirstLineRow(
        // block-size: size.control.md, grown to 48 dp; padding-block: (size.control.md − the label's line) ÷ 2
        minHeight = maxOf(control, QeetrixAccessibility.MinimumTouchTarget),
        padding = ((control - line) / 2).coerceAtLeast(0.dp),
        line = line,
        gap = QeetrixSpace.GapSm, // gap: space.gap.sm
        modifier =
            modifier
                .fillMaxWidth()
                .then(interaction(source))
                .drawBehind {
                    if (!highlighted) return@drawBehind
                    // [data-highlighted]: background bg.neutral.subtle-hover; ::before inset-inline-start 0, inline-size border.width.strong
                    drawRect(tint)
                    val width = QeetrixBorder.WidthStrong.toPx()
                    val x = if (layoutDirection == LayoutDirection.Rtl) size.width - width else 0f
                    drawRect(edge, Offset(x, 0f), Size(width, size.height))
                }.padding(horizontal = QeetrixSpace.InsetMd), // padding-inline: space.inset.md
        // > :is(.qx-menu-item-icon, .qx-menu-mark): margin-block (line − 16px) ÷ 2, on the label's first line
        leading = leading?.let { { it(iconColor, textColor) } },
        trailing =
            listOfNotNull(
                shortcut?.let {
                    {
                        // .qx-menu-shortcut: margin-inline-start space.gap.lg; line-height: the label's line; nowrap
                        Box(Modifier.padding(start = QeetrixSpace.GapLg).clearAndSetSemantics {}) {
                            // aria-hidden
                            BasicText(it, style = typography.codeSm.copy(color = colors.textSubtlest).labelDirection(), maxLines = 1)
                        }
                    }
                },
            ),
    ) {
        // .qx-menu-item-label: overflow-wrap anywhere — a long label wraps and the row grows (QTRX-177).
        val label = typography.bodyMd.copy(color = textColor).labelDirection(alignToLayout = true)
        if (description == null) {
            BasicText(text, style = label)
        } else {
            Column {
                BasicText(text, style = label)
                BasicText(
                    description,
                    style =
                        typography.bodySm
                            .copy(color = if (enabled) colors.textSubtle else colors.textDisabled)
                            .labelDirection(alignToLayout = true),
                )
            }
        }
    }
}

/** `.qx-menu-mark`: the 16 × 16 dp start column of a checkbox or radio item, its mark centred. */
@Composable
private fun Mark(content: @Composable () -> Unit) {
    Box(Modifier.size(QeetrixSize.IconSm), contentAlignment = Alignment.Center) { content() }
}

/** The open menu, in a focusable popup below the trigger: it fades and slides in from the trigger's side, and out again. */
@Composable
private fun MenuPopup(
    open: Boolean,
    anchor: IntRect,
    onDismiss: () -> Unit,
    content: @Composable QeetrixMenuScope.() -> Unit,
) {
    val visibility = remember { MutableTransitionState(false) }
    visibility.targetState = open
    if (!visibility.currentState && !visibility.targetState) return

    val scope = remember { MenuScopeImpl() }
    scope.onDismiss = onDismiss
    // A new opening may choose again; nothing is chosen while the menu leaves.
    LaunchedEffect(open) { scope.chosen = !open }

    val density = LocalDensity.current
    val window = LocalWindowInfo.current.containerSize
    val gap = with(density) { SideOffset.roundToPx() }
    val margin = with(density) { CollisionPadding.roundToPx() }
    val below = window.height - anchor.bottom - gap - margin
    val above = anchor.top - gap - margin
    val maxHeight = with(density) { maxOf(above, below, 0).toDp() } // max-block-size: var(--available-height)
    val opensAbove = above > below
    val maxWidth = with(density) { minOf(MaxWidth, (window.width - 2 * margin).toDp()) } // max-inline-size: min(320px, --available-width)

    Popup(
        popupPositionProvider = remember(gap, margin) { AnchoredPopupPosition(gap, margin, PopupAlign.Start) },
        onDismissRequest = { if (visibility.targetState) onDismiss() },
        properties = PopupProperties(focusable = true),
    ) {
        AnimatedVisibility(visibleState = visibility, enter = popupEnter(opensAbove), exit = popupExit(opensAbove)) {
            MenuSurface(
                Modifier.widthIn(min = minOf(MinWidth, maxWidth), max = maxWidth).heightIn(max = maxHeight),
                focusFirst = keyboardInput(),
                onDismiss = onDismiss,
                scope = scope,
                content = content,
            )
        }
    }
}

/**
 * The open menu itself (`.qx-menu`) — the overlay surface and its items — drawn by the popup, and on its own by the
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
    val colors = QeetrixTheme.colors
    val shape = QeetrixShapes.Overlay // border-radius: radius.overlay
    val first = remember { FocusRequester() }
    if (focusFirst) LaunchedEffect(Unit) { first.requestFocus() }
    Column(
        modifier
            .width(IntrinsicSize.Max) // as wide as its widest item, within min-inline-size and max-inline-size
            .qeetrixShadow(QeetrixShadow.Overlay, shape, colors.surfaceOverlay) // background surface.overlay; box-shadow shadow.overlay
            .border(QeetrixBorder.WidthDefault, colors.borderSubtle, shape) // border: border.width.default solid border.subtle
            .clip(shape)
            .onPreviewKeyEvent { event ->
                // Escape closes on its way down; its key-up is consumed with it, so nothing else sees half a press.
                if (event.key == Key.Escape) {
                    if (event.type == KeyEventType.KeyDown) onDismiss()
                    true
                } else {
                    false
                }
            }.padding(QeetrixBorder.WidthDefault) // the items sit inside the border, as in the CSS box
            .verticalScroll(rememberScrollState()) // overflow-y: auto
            .padding(vertical = MenuPadding) // padding-block: 4px
            // Asked for focus, the column gives it to its first focusable item.
            .focusRequester(first),
    ) {
        scope.content()
    }
}

/** Routes an item's choice: closes the menu when the item says so, then runs the item's action — once per opening. */
private class MenuScopeImpl : QeetrixMenuScope {
    var onDismiss: () -> Unit = {}
    var chosen = false

    fun choose(
        close: Boolean,
        action: () -> Unit,
    ) {
        if (chosen) return
        if (close) {
            chosen = true
            onDismiss()
        }
        action()
    }
}

/** A radio group's value and change callback, over the menu that routes its choices. */
private class RadioGroupScopeImpl<T>(
    val menu: MenuScopeImpl,
) : QeetrixMenuRadioGroupScope<T> {
    var value: T? = null
    var onValueChange: (T) -> Unit = {}
}

/** The menu that routes the choices of the items in [scope]. */
private fun menuOf(scope: QeetrixMenuScope): MenuScopeImpl =
    when (scope) {
        is MenuScopeImpl -> scope
        is RadioGroupScopeImpl<*> -> scope.menu
    }

/**
 * The text of an item drawn highlighted whatever its interaction state — for screenshots, which have no pointer or
 * keyboard to highlight one.
 */
internal val LocalHighlightedMenuItem = staticCompositionLocalOf<String?> { null }

/** `.qx-menu min-inline-size: 200px`. */
private val MinWidth: Dp = 200.dp

/** `.qx-menu max-inline-size: min(320px, …)`. */
private val MaxWidth: Dp = 320.dp

/** `.qx-menu padding-block: 4px`. */
private val MenuPadding: Dp = 4.dp

/** menu.tsx `sideOffset`: 4px from the trigger. */
private val SideOffset: Dp = 4.dp

/** How close to the window's edges the menu may come. */
private val CollisionPadding: Dp = 8.dp

/** `.qx-menu-dot`: 6px. */
private val RadioDot: Dp = 6.dp
