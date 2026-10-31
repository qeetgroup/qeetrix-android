# Chip

`QeetrixChip`, `QeetrixChipGroup` · `components.badge` · `@qeetrix/react`: none — kept from the earlier library and
restyled (closest: FilterTags' tag) · sample: Data display → Chip

A tag someone acts on: a filter that turns on and off, a choice in a group of chips, or an applied filter with a
remove button. A static status is a [Badge](Badge.md).

```kotlin
QeetrixChip("Unread", selected = unread, onSelectedChange = { unread = it })
QeetrixChip("Status: Active", onRemove = { remove(filter) })
QeetrixChipGroup(label = "Status", value = status, onValueChange = { status = it }, options = statusOptions)
QeetrixChipGroup(label = "Departments", values = departments, onValuesChange = { departments = it }, options = options)
```

## Why it is kept

`@qeetrix/react` had no Chip until 0.4.0, which adds Chip and ChipGroup (experimental, QTRX-180) in the same look
(FilterTags' tag). Badge is static (design rules §14: "an interactive or removable tag is a Chip, which is a separate
component"), and FilterTags is a whole applied-filters bar — a list, Clear all, a summary and focus management — not a
tag. So the Android Chip stays (moving its API onto 0.4.0's Chip is not part of the 0.4.0 bump), drawn in the Qeetrix language from FilterTags' tag and the
choice tile's chosen look; no new design.

## API

| Old | Now |
|:--|:--|
| `QeetrixChip(label, selected, onSelectedChange, icon, size, enabled)` | unchanged |
| `QeetrixChip(label, onRemove, icon, size, enabled)` | unchanged |
| `QeetrixChipGroup(label, selected: T, onSelect, options)` | `value` / `onValueChange` |
| `QeetrixChipGroup(label, selected: Set<T>, onSelectedChange, options)` | `values` / `onValuesChange` |
| `QeetrixChipSize.Sm / Md / Lg` (24 / 28 / 32 dp) | the density's `size.control.sm / md / lg` (28 / 32 / 40 dp by default) |

## Look

- **Resting**: 4 dp corners (`radius.control`; only people and status are round), `color.bg.neutral.subtle` with
  a 1 dp `color.border.subtle` edge inside, the label in `color.text.default` (`label.sm` at `Sm`, `label.md`
  above), `space.gap.sm` padding, `space.gap.xs` between parts; `-hover` and `-pressed` neutral steps.
- **Selected**: `color.bg.selected` with a 2 dp `color.border.brand` edge and a check before the label.
- **Removable**: FilterTags' tag, ending in a small neutral icon button.
- **Disabled**: `color.bg.disabled`, `color.border.disabled`, `color.text.disabled`.

## Accessibility

A selectable chip is a checkbox (a radio button in a single-choice group), read with its label; a removable chip's
label is text and its remove button is named "Remove {label}" (`qx_remove`). Keyboard focus draws the Ember frame
2 dp outside. Each chip reserves a 48 dp touch area.

## Platform differences

- **No web counterpart.** iOS keeps the same component.
- **Applied filters.** FilterTags' Clear all, summary and focus moves are not ported; move focus to the next chip
  after a removal yourself.

Related: [Badge](Badge.md), [Radio group](RadioGroup.md), [Checkbox](Checkbox.md).
