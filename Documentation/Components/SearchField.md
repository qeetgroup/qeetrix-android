# Search field

`QeetrixSearchField` · `components.input` · web: none (the data table's search is an `Input`) ·
sample: Forms & inputs → Search field

A single-line field for a search query, with a search icon, a clear button and the keyboard's search key,
built on the same field parts as the [text field](TextField.md).

```kotlin
val query = rememberTextFieldState()
QeetrixSearchField(query, placeholder = "Search members", onSearch = { search(it) })

// Filter as the person types, after a pause.
LaunchedEffect(query) {
    snapshotFlow { query.text.toString() }.collectLatest { text ->
        delay(300)
        search(text)
    }
}
```

## Usage

- **The query is the app's**, in a `TextFieldState` it owns; filtering, debouncing, cancelling and asking a
  server are the app's too. `collectLatest` cancels a pending search when the person types again.
- **The search key** runs `onSearch` with the query and hides the keyboard.
- **Clear** appears while there is text; it empties the field and keeps the focus.
- **Results** show where they are; with nothing to show, a no-results [empty state](EmptyState.md) offers
  to clear the search and any [chips](Chip.md) filtering with it.
- **Privacy.** Keep queries out of logs and analytics unless the person has agreed; never put a secret in
  the placeholder.

## Accessibility

Named by its `label`, or "Search" (`qx_search`) without one; the placeholder ("Search…",
`qx_search_placeholder`) is a hint, not a name. The clear button is "Clear" (`qx_clear`), drawn smaller than 48 dp and reached by taps within 48 dp of
it. Without a label, the field reserves a 48 dp touch height ([Text field](TextField.md)). Announce result
counts sparingly, not on every keystroke.

## Theme and tokens

The field recipe of `component.input`: the control height of the density, the boundary's hover, focus and
disabled states; a 16 dp `text.tertiary` search icon.

## Platform differences

iOS uses the system's `searchable`. Android has no search field outside Material (whose `SearchBar` is
experimental), so this one is Qeetrix's; suggestions and recent searches are the app's content below it.

Related: [Text field](TextField.md), [Chip](Chip.md), [Empty state](EmptyState.md).
