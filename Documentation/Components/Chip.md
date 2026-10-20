# Chip

`QeetrixChip`, `QeetrixChipGroup`, `QeetrixChipSize` · `components.badge` ·
web: [Chip](https://ui.qeet.in/docs/components/chip) · sample: Data display → Chip

A pill that filters or chooses: on its own it toggles; in a group it chooses one value or several; removable,
it shows a choice already made.

```kotlin
var unread by rememberSaveable { mutableStateOf(false) }
QeetrixChip("Unread", selected = unread, onSelectedChange = { unread = it })

var status by rememberSaveable { mutableStateOf<Status?>(null) }
QeetrixChipGroup(
    label = "Status",
    selected = status,
    onSelect = { status = it },
    options = listOf(QeetrixOption(null, "All"), QeetrixOption(Status.Open, "Open"), QeetrixOption(Status.Closed, "Closed")),
)

QeetrixChip("Owner: Ada", onRemove = { removeFilter("owner") })
```

## Usage

- **The filtering is the app's.** A chip reports the new value; the app decides what it filters and how.
- **Groups.** With a value, `QeetrixChipGroup` chooses one (radio buttons; "nothing yet" is a value no
  option has); with a `Set`, it chooses several. The label names the group for TalkBack and is not shown —
  put a visible label above it if the screen needs one. Chips wrap onto more lines; for one scrolling line,
  give the group `Modifier.horizontalScroll(…)`.
- **Removable chips** show applied filters or tags; removing them, and offering an undo, are the app's.
- **Not a badge.** A status is a [badge](Badge.md). Options with descriptions belong in a
  [radio group](RadioGroup.md): chips show labels only.

## States

Resting (`border.default`, `border.strong` in dark), selected (`surface.brand-subtle`, `border.brand` and a
check mark — or the chip's own icon — so selection is never colour alone), hovered and pressed (one and two
steps along each ladder), keyboard-focused (the focus ring), disabled (faded).

## Accessibility

A toggling chip, and each chip of a multiple group, is a checkbox, checked or not; a single-choice group is
a selectable group of radio buttons. A removable chip's label is text and its remove button is named for it
(`qx_remove`: "Remove Owner: Ada"). Selectable chips reserve a 48 dp touch area; the remove button keeps
the web's 16 dp, and Compose sends touches within 48 dp of it to it.

## Theme and tokens

Sizes `Sm`, `Md` (the default), `Lg`: 24, 28 and 32 dp tall, text-xs or text-sm medium, 12, 14 and 16 dp
icons. Pill corners and a hairline border.

## Platform differences

Selectable chips are checkboxes for TalkBack (Material's filter chips are too); the web makes them toggle
buttons and iOS buttons with a selected trait. A chip can't be both selectable and removable, as on iOS.

Related: [Search field](SearchField.md), [Badge](Badge.md).
