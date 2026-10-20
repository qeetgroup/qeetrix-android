# List item

`QeetrixListItem`, `QeetrixListItemIcon` · `components.list` · web: none (Android and iOS only) ·
sample: Data display → List item

A row of a collection — a file, a member, a setting, a device — with a title and, optionally, a
description, metadata, a leading element and a trailing element.

```kotlin
LazyColumn {
    items(files, key = { it.id }) { file ->
        QeetrixListItem(
            title = file.name,
            description = file.owner,
            metadata = file.size,
            onClick = { open(file.id) },
            leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
        ) {
            QeetrixBadge("Shared", variant = QeetrixBadgeVariant.Secondary)
        }
    }
}
```

## Usage

- **In Compose's own lists.** Put rows in a `LazyColumn` with your items' stable `key`, so a row keeps its
  state and its place when items are inserted, removed or reordered. Outside a lazy list — in a card, a
  sheet — stack them in a `Column`, with [separators](Separator.md) if they need rules.
- **Slots.** `leading`: a `QeetrixListItemIcon` (a 32 dp tile), an [avatar](Avatar.md) or a checkbox.
  `trailing` (the trailing lambda): a [badge](Badge.md), a value, an [icon button](IconButton.md).
  `metadata`: a short note at the end of the title line — a time, a size, a count.
- **Interaction.** `onClick` makes the row one clickable element. `onLongClick`, which needs `onClick`,
  adds a long press — to start a selection or open a [menu](Menu.md) — and `onLongClickLabel` names it for
  TalkBack ("Select").
- **Selection is the app's.** `selected` tints the row. For a multiple selection, put a
  `QeetrixCheckbox(checked, onCheckedChange = null)` in `leading` and toggle in `onClick`: the row is the
  control. The collections screen in the sample starts a selection with a long press.
- **A setting that is a switch** is a [switch](Switch.md) with its label, not a row with a switch in it.
- Groups of rows take a [section header](SectionHeader.md).

## States

Resting (transparent, on the surface it sits on), pressed (`surface.interactive-hover`), hovered
(`surface.interactive`), selected (`surface.brand-subtle` and a 2 dp `border.brand` edge at the start;
its own hover and press steps), keyboard-focused (an inset focus ring), disabled (faded).

## Accessibility

The title, description, metadata and any text in the slots are one element, read in that order; the icon
tile is decorative. A control in `trailing` stays its own focusable element. A selected row is read as
selected (an unselectable row is not read as "not selected"). Give a leading avatar
`contentDescription = null` when the title already names the person. Rows are at least the density's row
height and 48 dp; from 150 % text the trailing element moves below the text.

## Theme and tokens

`density.row-height` and `density.cell-padding-y`; 16 dp sides; text-sm medium title in `text.primary`,
text-sm description and text-xs tabular metadata in `text.tertiary`; the tile is `surface.sunken` with a
`border.subtle` hairline and a 16 dp `text.tertiary` icon — the web's notification row; the selected row
is the web table's.

## Platform differences

The web has no list row: a web screen uses a table or a listbox. iOS rows live in SwiftUI's `List` and take
`qeetrixListRow()` inside a link; Android rows are clickable themselves, in Compose's lazy lists. Swipe
actions are not provided: use a long press and a menu, or the row's own actions.

Related: [Section header](SectionHeader.md), [Data state](DataState.md),
[Data display and collections](../Standards/DATA_DISPLAY_AND_COLLECTIONS.md).
