# List item

`QeetrixListItem`, `QeetrixListItemIcon` · `components.list` · `@qeetrix/react`: none — Android's own row, drawn
by the Table's row rules (a web screen uses a Table where a phone uses a list) · sample: Data display → List item

A row of a collection — a file, a member, a setting, a device — with a title and, optionally, a description,
metadata, a leading element and a trailing element.

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
            QeetrixBadge("Shared")
        }
    }
}
```

## Look

- At least the density's `size.row` and 48 dp tall; `space.gap.xs` above and below; the phone's page gutter
  (`space.inset.lg`) at the sides; `space.gap.md` between leading, text and trailing.
- Title `label.md` in `color.text.default`; description `body.sm` in `color.text.subtle`; metadata `body.sm`,
  tabular figures, at the end of the title line.
- `QeetrixListItemIcon`: a 32 dp tile (a `md` avatar's size) drawn as the Timeline's marker —
  `color.bg.neutral.subtle`, a 1 dp `color.border.subtle` edge, 4 dp corners and a 16 dp sharp icon in
  `color.icon.subtle`.
- Rules between rows are the app's: put a [separator](Separator.md) (`color.border.subtle`) between them.

## States

| State | Look |
|:--|:--|
| Hover / pressed (with `onClick`) | `color.bg.neutral.subtle` / `-hover` (§15: a neutral tint) |
| Selected | `color.bg.selected` (`-hover` while pressed), read as selected. The Ember tint means "chosen for an action"; the Ember bar is for the current item (§11), so a selection has no bar — add a checkbox |
| Focus | The 2 dp Ember frame inside the row |
| Disabled | `color.text.disabled`, `color.icon.disabled` in the icon tile; ignores clicks |

## Accessibility

- The row's text — title, description, metadata, then text in the slots — is one element; a control in
  `trailing` stays its own focusable element. Give a leading avatar `decorative = true` when the title names
  the person.
- `onLongClick` (with `onLongClickLabel`) needs `onClick`. For a multiple selection put a checkbox with
  `onCheckedChange = null` in `leading` and toggle in `onClick`.
- From 150 % text the `trailing` element moves below the text.

## Theme and tokens

`size.row`, `space.gap.xs` / `md`, `space.inset.lg`, `typography.label.md` / `body.sm`, `color.text.*`,
`color.bg.neutral.subtle(-hover/-pressed)`, `color.bg.selected(-hover)`, `color.border.subtle`, `radius.control`.

## Platform differences

The web has no list row. Removed since 2.x: the 2 dp brand edge on selected rows (the Ember bar now means
"current" only) and the faded disabled row (disabled colours instead of opacity).

Related: [Section header](SectionHeader.md), [Avatar](Avatar.md), [Description list](DescriptionList.md).
