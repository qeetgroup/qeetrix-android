# Menu

`QeetrixMenu`, `QeetrixMenuItem`, `QeetrixMenuCheckboxItem`, `QeetrixMenuRadioGroup`, `QeetrixMenuRadioItem`,
`QeetrixMenuGroupLabel`, `QeetrixMenuSeparator`, `QeetrixMenuItemTone` · `components.dropdownmenu` ·
`@qeetrix/react`: [Menu](https://ui.qeet.in/components/menu) · sample: Overlays → Menu

A list of actions or options that opens from a trigger.

```kotlin
QeetrixMenu(
    open = open,
    onOpenChange = { open = it },
    trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { open = true }) },
) {
    QeetrixMenuItem("Rename", onClick = ::rename, icon = QeetrixIconName.Pencil)
    QeetrixMenuItem("Duplicate", onClick = ::duplicate, icon = QeetrixIconName.Copy, shortcut = "Ctrl+D")
    QeetrixMenuSeparator()
    QeetrixMenuItem("Delete", onClick = ::confirmDelete, icon = QeetrixIconName.Trash, tone = QeetrixMenuItemTone.Danger)
}
```

## Usage

- **The app owns `open`.** The trigger opens it; a tap outside, Back, Escape or choosing an item calls
  `onOpenChange(false)`. It opens below the trigger, 4 dp away, or above it when there is no room.
- **Items**: `QeetrixMenuItem` (an action: `icon`, `shortcut`, `description`, `tone`, `enabled`, `closeOnClick`);
  `QeetrixMenuCheckboxItem` (`checked` / `onCheckedChange`; keeps the menu open by default); radio items in a
  `QeetrixMenuRadioGroup` (`value` / `onValueChange`; close the menu by default); `QeetrixMenuGroupLabel` and
  `QeetrixMenuSeparator`.
- **Choosing** closes the menu (when the item closes it) and then runs it, once per opening.
- Keep about seven items per group; put destructive items last, after a separator. No forms or links to other
  screens in a menu.

## Accessibility

- Items are read with their labels, and their descriptions after them; a checkbox item is a checkbox and a radio
  item a radio button, with their states; disabled items are read as unavailable; group labels are headings.
  Shortcuts are hidden from accessibility services, as on the web.
- Opened from a keyboard, focus starts on the first available item; arrow keys move, Enter chooses, Escape
  closes and focus returns to the trigger.

## Theme and tokens

menu.css: `color.surface.overlay`, a 1 dp `color.border.subtle` edge, `radius.overlay`, `shadow.overlay`,
4 dp vertical padding, 200–320 dp wide. Items: `space.inset.md` inline padding, `space.gap.sm` gaps, `body.md`;
icons 16 dp sharp in `color.icon.subtle`; shortcuts `code.sm` `color.text.subtlest`; descriptions `body.sm`
`color.text.subtle`. Highlighted (pressed, hovered or focused): `color.bg.neutral.subtle-hover` and a 2 dp
(`border.width.strong`) `color.border.brand` start edge. Danger: `color.text.danger` / `color.icon.danger`;
disabled: `color.text.disabled` / `color.icon.disabled`. Group labels `label.sm` `color.text.subtle`; separators
1 dp `color.border.subtle`, 4 dp above and below. Motion: fade and a 4 dp slide over `motion.duration.normal`,
out over `motion.duration.fast`.

## Platform differences

- **Row height.** Items are 48 dp tall — the web's `size.control.md` grown to Android's touch target.
- **Long labels wrap**, as menu.css does since 0.4.0 (QTRX-177): the row grows, and the icon, mark and shortcut stay
  on the label's first line.
- **Highlight, not a frame.** The web's menu marks keyboard focus with the highlight, not the 2 dp focus frame
  the design rules list for menu items (§10); Android does the same.
- **Not implemented:** submenus (`MenuSubmenu`), context menus (`ContextMenu`, long-press), `MenuGroup`, and the
  `side` / `align` / `sideOffset` positioning props.
- Back is the popup window's own; Qeetrix only adds Escape.

Related: [Dialog](Dialog.md), [Bottom sheet](BottomSheet.md).
