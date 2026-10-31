# Search field

`QeetrixSearchField` · `components.input` · `@qeetrix/react`: SearchField (`experimental`; not yet a page on
ui.qeet.in) · sample: Forms → Search field

A search box for a list: the Input box with a leading search icon, the keyboard's search key, and a Clear search
button while there is text.

```kotlin
val query = rememberTextFieldState()
QeetrixSearchField(query, placeholder = "e.g. Asha", onSearch = { members.search(it) })
```

## API

| `@qeetrix/react` | Android |
|:--|:--|
| `value` / `onValueChange` | `state: TextFieldState` |
| `onSearch`, `searchDelay` (300 ms) | `onSearch: ((String) -> Unit)?`, `searchDelay: Duration = 300.milliseconds` |
| `endAdornment` | `endAdornment` — tools before the clear button |
| `disabled`, `readOnly`, `placeholder` | `enabled`, `readOnly`, `placeholder` |
| a Field around it (often `hideLabel`) | `label: String?` — shown above when given; without one it is named "Search" (`qx_search`) |

## Behaviour

- `onSearch` runs when typing pauses for `searchDelay`, at once on the search key, and with "" when cleared — so
  a server-searched list asks once per pause and the search key never asks twice. Text the app writes into the
  state is not searched.
- **Clear search** (`qx_clear_search`) empties the field, searches "" and returns focus to it. **Escape** clears
  a field with text and lets the key through only once it is empty, so a dialog around it closes on the second
  press.

## Look

The [text field](TextField.md)'s box and states, with `padding-inline-end: space.gap.xs` so the clear button (a
small neutral icon button) sits near the edge.

## Platform differences

- **Search key.** The keyboard shows its search action (`ImeAction.Search`) and hides after a search.
- **Typing pause.** Only typing, pasting and AutoFill start the pause — the edits that pass through an input
  transformation — as react's `onValueChange` does.

Related: [Text field](TextField.md), [Chip](Chip.md) (applied filters).
