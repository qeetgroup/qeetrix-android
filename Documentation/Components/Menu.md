# Menu

`QeetrixMenu`, `QeetrixMenuItem`, `QeetrixMenuLabel`, `QeetrixMenuSeparator`, `QeetrixMenuItemVariant` ·
`components.dropdownmenu` · web: [Dropdown Menu](https://ui.qeet.in/docs/components/dropdown-menu) · sample:
Overlays → Menu

A short list of actions that opens from a trigger, below it (above when there is no room).

```kotlin
var open by remember { mutableStateOf(false) }
QeetrixMenu(
    expanded = open,
    onExpandedChange = { open = it },
    trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { open = true }) },
) {
    QeetrixMenuItem("Rename", onClick = ::rename, icon = QeetrixIconName.Pencil)
    QeetrixMenuItem("Duplicate", onClick = ::duplicate, icon = QeetrixIconName.Copy)
    QeetrixMenuSeparator()
    QeetrixMenuItem("Delete", onClick = ::confirmDelete, icon = QeetrixIconName.Trash, variant = QeetrixMenuItemVariant.Destructive)
}
```

## Usage

- **The app owns `expanded`.** The trigger opens it; a tap outside, Back, Escape or choosing an item calls
  `onExpandedChange(false)`.
- **Choosing an item** closes the menu, then runs its `onClick`, once. A destructive item that cannot be
  undone should open a [confirmation](ConfirmationDialog.md).
- `QeetrixMenuLabel` heads a group; `QeetrixMenuSeparator` divides groups.
- Not here: checkbox and radio items, submenus, shortcuts. For choosing a value use a
  [select](Select.md); for rich anchored content, a [bottom sheet](BottomSheet.md) or a [dialog](Dialog.md).

## Accessibility

Name the trigger for what it opens ("More options"). Opened from a keyboard, focus starts on the first
available item; arrow keys move between items, Enter chooses, Escape closes. Items are read with their
labels, disabled ones as unavailable; labels are headings; separators are skipped. Rows are at least 48 dp.

## Theme and tokens

`component.menu.*`: `surface.overlay`, `border.default`, `elevation.overlay`, the menu corner; items in the
menu item corner (radius-md), `surface.interactive` when highlighted, `text.danger` and
`component.menu.item-danger-highlight` for destructive items; at least as wide as the trigger and 128 dp, at
most 320 dp; the same fade and 97 % scale as the select list.

## Platform differences

- iOS uses the system menu; Android draws Qeetrix's menu in a Compose popup.
- The web row height follows density (`component.menu.item-height`); Android rows are 48 dp touch targets.
- Long-press context menus are the app's trigger: open the same `QeetrixMenu` from a long-press handler.

Related: [Select](Select.md), [Top app bar](TopAppBar.md).
