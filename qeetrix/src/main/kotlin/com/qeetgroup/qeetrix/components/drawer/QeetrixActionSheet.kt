package com.qeetgroup.qeetrix.components.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.dropdownmenu.MenuRow
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItemTone
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixBorder
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.icons.QeetrixIconShape
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** What a [QeetrixActionSheet] holds: [QeetrixSheetAction]s, and [QeetrixSheetActionSeparator]s between groups. */
@Stable
public sealed interface QeetrixActionSheetScope

/**
 * A bottom sheet of actions for one thing — an account's Copy code, Rename and Delete — the phone's action sheet
 *. `@qeetrix/react` has none: a web page opens a [com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu]
 * where a phone opens this. It is a [QeetrixBottomSheet] — the handle, the ruled header with the [title] and optional
 * [description], the close button, the same gestures, motion and window — whose body is a list of [QeetrixSheetAction]s
 * running edge to edge.
 *
 * - **The rows are menu items** drawn for a sheet: the same 48 dp minimum, `body.md` label, 16 dp sharp icon,
 *   optional description, danger tone, and the same highlight under the finger or keyboard focus — the neutral tint
 *   and the 2 dp Ember start edge. Their text starts under the title (`space.inset.xl`).
 * - **Choosing an action** closes the sheet first and then runs it, once, as a menu does: the sheet asks
 *   `onOpenChange(false)`. An action with `closeOnClick = false` leaves it open.
 * - **Groups**: a [QeetrixSheetActionSeparator] between them; put a destructive action last, after one.
 * - **Accessibility.** Each action is a button read with its label and description, a disabled one as unavailable;
 *   the sheet's title is announced as it opens. Swiping down, a tap outside, Back and the close button cancel.
 *
 * ```
 * QeetrixActionSheet(open = acting, onOpenChange = { acting = it }, title = "GitHub · priya") {
 *     QeetrixSheetAction("Copy code", onClick = ::copy, icon = QeetrixIconName.Copy)
 *     QeetrixSheetAction("Rename", onClick = ::rename, icon = QeetrixIconName.Pencil)
 *     QeetrixSheetActionSeparator()
 *     QeetrixSheetAction("Delete account", onClick = ::confirmDelete, icon = QeetrixIconName.Trash, tone = QeetrixMenuItemTone.Danger)
 * }
 * ```
 *
 * @param title Names the sheet — usually the thing acted on — as its heading and pane title.
 */
@Composable
public fun QeetrixActionSheet(
    open: Boolean,
    onOpenChange: (Boolean) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    size: QeetrixSheetSize = QeetrixSheetSize.Md,
    actions: @Composable QeetrixActionSheetScope.() -> Unit,
) {
    val scope = remember { ActionSheetScopeImpl() }
    scope.close = { onOpenChange(false) }
    // A new showing takes choices again; while it slides away, a second tap does nothing.
    LaunchedEffect(open) { if (open) scope.chosen = false }
    SheetWindow(
        open = open,
        onOpenChange = onOpenChange,
        title = title,
        edge = SheetEdge.Bottom,
        size = size.width,
        dismissible = true,
        partiallyExpandable = false,
        modifier = modifier,
    ) { onClose ->
        DragHandle()
        ActionSheetLayout(title, description, onClose, scope, Modifier.weight(1f, fill = false), actions)
    }
}

/** The action sheet's parts below the handle, outside the sheet's window: for the sheet and for screenshots. */
@Composable
internal fun ActionSheetLayout(
    title: String,
    description: String?,
    onClose: (() -> Unit)?,
    scope: ActionSheetScopeImpl,
    modifier: Modifier = Modifier,
    actions: @Composable QeetrixActionSheetScope.() -> Unit,
) {
    SheetLayout(
        title,
        description,
        onClose,
        footerStart = null,
        footer = null,
        modifier = modifier,
        // The rows run edge to edge, 4 dp above the first and below the last, as in a menu.
        bodyPadding = PaddingValues(vertical = QeetrixSpace.InsetXs),
    ) { scope.actions() }
}

/**
 * An action in a [QeetrixActionSheet], built as a menu item: its [text], an optional leading [icon] (16 dp, sharp,
 * `color.icon.subtle`), an optional [description] — a subtle `body.sm` second line — and its [tone]:
 * [QeetrixMenuItemTone.Danger] draws the text in `color.text.danger` and the icon in `color.icon.danger`, for an action
 * that deletes or removes; its words say what it does, so the colour is never the only signal. Choosing it runs
 * [onClick]; with [closeOnClick] (the default) the sheet closes first.
 */
@Composable
public fun QeetrixActionSheetScope.QeetrixSheetAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: QeetrixIconName? = null,
    description: String? = null,
    tone: QeetrixMenuItemTone = QeetrixMenuItemTone.Default,
    enabled: Boolean = true,
    closeOnClick: Boolean = true,
) {
    val sheet = this as ActionSheetScopeImpl
    MenuRow(
        text = text,
        modifier = modifier,
        enabled = enabled,
        tone = tone,
        shortcut = null,
        description = description,
        interaction = { source ->
            Modifier.clickable(source, indication = null, enabled = enabled, role = Role.Button) {
                sheet.choose(closeOnClick, onClick)
            }
        },
        leading =
            icon?.let {
                { tint, _ ->
                    QeetrixIcon(it, contentDescription = null, size = QeetrixSize.IconSm, shape = QeetrixIconShape.Sharp, tint = tint)
                }
            },
        inlinePadding = QeetrixSpace.InsetXl,
    )
}

/**
 * A 1 dp `color.border.subtle` rule between groups of [QeetrixSheetAction]s, edge to edge, 4 dp above and below, as a
 * menu's separator. Decorative: accessibility services skip it.
 */
@Composable
public fun QeetrixActionSheetScope.QeetrixSheetActionSeparator(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(QeetrixBorder.WidthDefault)
            .background(QeetrixTheme.colors.borderSubtle),
    )
}

/** Routes the actions' choices: closes the sheet first, once, as a menu's choice does. */
internal class ActionSheetScopeImpl : QeetrixActionSheetScope {
    var close: () -> Unit = {}
    var chosen = false

    fun choose(
        closeOnClick: Boolean,
        action: () -> Unit,
    ) {
        if (chosen) return
        if (closeOnClick) {
            chosen = true
            close()
        }
        action()
    }
}
