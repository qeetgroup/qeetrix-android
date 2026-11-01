# Menu

`QeetrixMenu`, `QeetrixMenuItem`, `QeetrixMenuCheckboxItem`, `QeetrixMenuRadioGroup`, `QeetrixMenuRadioItem`,
`QeetrixMenuSubmenu`, `QeetrixMenuGroupLabel`, `QeetrixMenuSeparator`, `QeetrixMenuItemTone`, `QeetrixMenuSide`,
`QeetrixMenuAlign`, `QeetrixContextMenu` · `components.dropdownmenu` ·
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
  `onOpenChange(false)`. It opens on the trigger's `side` (`Bottom`, the default, or `Top`), 4 dp away, flipping to
  the other side when there is no room, and lines up by `align` (`Start`, `Center` or `End`, in the reading direction).
- **Items**: `QeetrixMenuItem` (an action: `icon`, `shortcut`, `description`, `tone`, `enabled`, `closeOnClick`);
  `QeetrixMenuCheckboxItem` (`checked` / `onCheckedChange`; keeps the menu open by default); radio items in a
  `QeetrixMenuRadioGroup` (`value` / `onValueChange`; close the menu by default); `QeetrixMenuGroupLabel` and
  `QeetrixMenuSeparator`.
- **Submenus**: `QeetrixMenuSubmenu(text, icon, description, enabled) { … }` — a row with a trailing chevron that opens
  the same kind of content beside it (at its end, or its start when there is no room; over the menu on a phone's
  narrow window). A choice in it closes the whole menu; Escape, Back, a tap outside or the arrow back close the
  submenu alone.
- **Context menu**: `QeetrixContextMenu(open, onOpenChange, menu = { … }) { content }` opens the same content at the
  point an area is long-pressed — or right-clicked, or Shift+F10 / the context-menu key on it. A long press on a
  clickable row inside opens the menu, not the row. Keep its actions reachable another way too: it is a shortcut.
- **Choosing** closes the menu (when the item closes it) and then runs it, once per opening.
- Keep about seven items per group; put destructive items last, after a separator. No forms or links to other
  screens in a menu.

## Accessibility

- Items are read with their labels, and their descriptions after them; a checkbox item is a checkbox and a radio
  item a radio button, with their states; disabled items are read as unavailable; group labels are headings.
  Shortcuts are hidden from accessibility services, as on the web.
- Opened from a keyboard, focus starts on the first available item; arrow keys move, Enter chooses, Escape
  closes and focus returns to the trigger. The arrow towards a submenu row's end opens its submenu, the arrow back
  closes it, focus returning to the row; the row is read as expanded or collapsed.
- A context menu's area takes keyboard focus (the Ember frame inside its edge) and offers TalkBack an "Open menu"
  action (`qx_open_menu`); a long press plays Android's long-press haptic.

## Theme and tokens

menu.css: `color.surface.overlay`, a 1 dp `color.border.subtle` edge, `radius.overlay`, `shadow.overlay`,
4 dp vertical padding, 200–320 dp wide. Items: `space.inset.md` inline padding, `space.gap.sm` gaps, `body.md`;
icons 16 dp sharp in `color.icon.subtle`; shortcuts `code.sm` `color.text.subtlest`; descriptions `body.sm`
`color.text.subtle`. Highlighted (pressed, hovered or focused): `color.bg.neutral.subtle-hover` and a 2 dp
(`border.width.strong`) `color.border.brand` start edge. Danger: `color.text.danger` / `color.icon.danger`;
disabled: `color.text.disabled` / `color.icon.disabled`. Group labels `label.sm` `color.text.subtle`; separators
1 dp `color.border.subtle`, 4 dp above and below. Motion: fade and a 4 dp slide over `motion.duration.normal`,
out over `motion.duration.fast`. A submenu row's chevron is 16 dp `chevron-right` in `color.icon.subtle`, mirrored in
right to left; the row stays highlighted while its submenu is open.

## Platform differences

- **Row height.** Items are 48 dp tall — the web's `size.control.md` grown to Android's touch target.
- **Long labels wrap**, as menu.css does since 0.4.0 (QTRX-177): the row grows, and the icon, mark and shortcut stay
  on the label's first line.
- **Highlight, not a frame.** The web's menu marks keyboard focus with the highlight, not the 2 dp focus frame
  the design rules list for menu items (§10); Android does the same.
- **Submenus open on a tap or the arrow key**, not on hover alone; on a phone a submenu that fits on neither side
  opens over its menu.
- **Context menus open on a long press** — the touch form of the web's right-click — and at the press point.
- **Not implemented:** `MenuGroup` (use a group label and separators) and `sideOffset` (4 dp, the web's default);
  `side` takes the top and bottom the CSS styles.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 1 November 2026 after QTRX-191: "Move to" stays
highlighted while its submenu opens beside it, and choosing in the submenu closes everything and runs once; a long press
on a list row opens the context menu at the press point without opening the row; `side = Top`, `align = End` opens above
its button, held inside the window's margin where the end alignment would leave it.
- Back is the popup window's own; Qeetrix only adds Escape.

Related: [Dialog](Dialog.md), [Bottom sheet](BottomSheet.md).
