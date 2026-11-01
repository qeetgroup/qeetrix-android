# Searchable select

`QeetrixSearchableSelect`, `QeetrixSearchableMultiSelect` · `components.select` · `@qeetrix/react`:
[Combobox](https://ui.qeet.in/components/combobox) (experimental), in the form a phone uses · sample: Forms → Select

Pick from a long list — a country, a currency, a time zone — where a [Select](Select.md)'s popup would be too long to
scan. The trigger is the select's; a tap opens a sheet with a search field above the options.

```kotlin
QeetrixSearchableSelect(country, { country = it }, Countries, label = "Country", placeholder = "e.g. India")
QeetrixSearchableMultiSelect(visited, { visited = it }, Countries, label = "Countries visited")
```

## Usage

- **One or several.** `QeetrixSearchableSelect` takes a value and closes on a choice; `QeetrixSearchableMultiSelect`
  takes a `Set`, toggles options — through any number of searches — and closes with its Done button. Both take
  `items` or `groups`, and the select's field parameters (`placeholder`, `description`, `error`, `size`, …).
- **Search.** Typing filters to the options whose label or `meta` contains what was typed, ignoring case; group labels
  stay over the matches left in their group. The search starts empty each time the sheet opens.
- **Nothing matches**: "No matches" and "Nothing matches “…”." take the list's place.
- **The sheet**: a bottom sheet on a phone, a side sheet from `breakpoint.medium` ([Side sheet](SideSheet.md)), named
  by the field's `label`. The close button, a swipe, a tap on the scrim, Back and Escape close it.
- Use a [Select](Select.md) for up to about 15 options.

## Accessibility

The trigger is one drop-down list named by the label, as the select's. The sheet announces its title; the search field
is named "Search"; options read as selected (or, for several, as check boxes, checked or not) with their meta; the
empty state is a polite live region, so TalkBack says "No matches" as it appears.

## Theme and tokens

The trigger and the options are the select's (select.css); the sheet is the drawer's (drawer.css); the search field is
the [search field](SearchField.md); the empty state is `EmptyState` at `size="section"` with the `search-x` icon. The
options area keeps at least 320 dp of height, so the sheet does not jump as the list filters.

## Platform differences

- **A sheet with a search field**, the phone's pattern for long lists, where the web's Combobox is a text input with a
  filtering popup. The web's Combobox is experimental and its look is not in the CSS Android follows yet.
- The empty state's words are Android's own (`qx_no_matches`, `qx_no_matches_description`): the web has no message yet.

Tried on the phone emulator (`qeetrix-api36`, Android 16) on 1 November 2026: the sheet opens with the choice checked;
typing "united" leaves United Arab Emirates, United Kingdom and United States above the keyboard, the sheet making room
for it; choosing one closes the sheet and the field shows it.

Related: [Select](Select.md), [Search field](SearchField.md), [Bottom sheet](BottomSheet.md).
