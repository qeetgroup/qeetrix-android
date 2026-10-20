# Data display and collections

How Qeetrix's rows, details, content states, search and filters work with an app's data. The decision record
is [0016](../Decisions/0016-data-display-and-content-states.md); each component's page is in
[Components](../Components/README.md).

## State ownership

Qeetrix presents; the app owns the data and every decision about it. Values below live in the app's state
holder (a view model, `rememberSaveable`).

| Component | The app owns | Qeetrix reports |
|:--|:--|:--|
| List item | The row's values; its selection | `onClick`, `onLongClick` |
| Section header | The grouping | The action's click |
| Description list | Formatted values | — |
| Accordion | Each item's `expanded` | `onExpandedChange(Boolean)` |
| Chip, chip group | `selected` (a Boolean, a value, a set) | `onSelectedChange`, `onSelect` |
| Search field | The query's `TextFieldState` | `onSearch(query)` |
| Data state | `loading`, `empty`, the failure and its retry | The retry's click |
| Empty state, timeline, skeleton | The words, the events, the shape | — |

There is no data store, fetching, caching, pagination, search index or filter engine in Qeetrix, and no
networking, database or image-loading dependency.

## Identity

- **Key every lazy item by the app's stable id** — `items(members, key = { it.id })` — never by index. A
  row's own state and the app's selection and expansion, kept as sets of ids, then follow the item through
  insertions, removals, reordering and paging. Section headers take keys of their own that can't clash
  (`"team-$team"`).
- **`contentType`** for mixed lists (`"header"`, `"member"`) lets Compose reuse slots across item kinds.
- Keep selection and expansion as ids in saved state (`rememberSaveable { mutableStateOf(setOf<String>()) }`),
  so they survive configuration changes and process death; prune ids whose items are gone.
- Hoist the `LazyListState` above anything that can recompose the list away — a data state's branch, a
  detail screen — so the scroll position comes back.

## Lists and sections

- Rows are `QeetrixListItem`s in a `LazyColumn` (or a `Column` for a handful in a card). Separators between
  rows are optional; a lazy list draws them as items' content, not as list chrome.
- A section is a `stickyHeader` (or an `item`) with a `QeetrixSectionHeader`, then its `items`. Sticky headers
  take the surface they sit on through their modifier.
- Rows animate when keyed items move, appear and leave with `Modifier.animateItem(…)`; pass
  `qeetrixAnimationSpec(…)` so animations stop when the person has turned them off.
- Grids are `LazyVerticalGrid` with the same keys; description lists have their own grid layout for details.

## Selection

- Single tap opens; selection mode starts with a long press (`onLongClick`, named with `onLongClickLabel`),
  shows a `QeetrixCheckbox(checked, onCheckedChange = null)` in each row, and turns taps into toggles. The top
  app bar shows the count and the actions; Back clears the selection before it leaves the screen.
- `selected` draws the brand tint and edge and reports the row as selected. Destructive actions on a
  selection ask first ([confirmation dialog](../Components/ConfirmationDialog.md)) and offer Undo
  ([snackbar](../Components/Snackbar.md)).
- Confirmation is not authorisation: whatever performs the action checks permission.

## Search and filters

- The query is a `TextFieldState` the app owns. Filter as the person types after a pause with
  `snapshotFlow { query.text.toString() }.collectLatest { delay(300); search(it) }` — `collectLatest`
  cancels a search the next keystroke makes stale. Filter small lists on the device; ask the server for large
  ones.
- Filters are chips: a `QeetrixChipGroup` for one value or several, `QeetrixChip(onRemove)` for applied
  filters. The chips change state; the app applies it.
- No results is a `NoResults` empty state with a way to clear the search and the filters together.
- Keep queries out of logs and analytics unless the person has agreed; don't put them in crash reports.

## Content states

| Branch | When | What shows | TalkBack |
|:--|:--|:--|:--|
| Loading | Nothing to show yet | Skeletons in the content's shape (`QeetrixDataLoadingRows` by default) | One "Loading" element |
| Failure | The load failed | An error empty state: the app's message for the person, "Try again" if it can | Announced, assertively |
| Empty | Loaded, nothing in it | The app's `QeetrixEmptyState` (default, first use, no results, no permission) | The title is a heading |
| Content | Loaded, with data | The content | — |

- `QeetrixDataState` shows exactly one branch, in that order.
- **Refreshing is not loading.** Keep the content while refreshing — a spinner in the top app bar, or
  Material 3's `PullToRefreshBox` around the list, which Qeetrix doesn't wrap — and set `loading` only when
  there is nothing to show.
- **Pagination** is the app's (Paging 3 or its own): show the next page's loading as skeleton rows or a
  `QeetrixSpinner` in a footer item, and a page's failure as an inline row with a retry, not a data state.
- Never show an exception's message, a status code or a stack trace; write what happened and what to do.

## Images

Qeetrix loads no images. Apps use their own loader into `QeetrixAvatar(image = …)` or an `Image`, give
images `Modifier.aspectRatio` to hold their space while loading, and send no cookies or authorisation with an
image request unless the app does so deliberately for that URL.

## Performance

- Every Phase 5 public composable is restartable and skippable (Compose compiler report); parameters are
  values, enums, strings and lambdas. Pass stable item types — immutable classes, or `@Immutable` ones — so
  rows whose data hasn't changed are skipped.
- A row's semantics are merged once per row; nothing in Qeetrix iterates the collection.
- The skeleton's shimmer runs in the draw phase only and stops with animations off.
- Measure an app's own lists with Macrobenchmark and JankStats on a device; Qeetrix's tests check skipping and
  recomposition counts, not frame times.

## Privacy and security

- Components display only what they are given and log, copy or announce nothing else. Concealed values stay
  concealed: don't put them in a description list, a row, a content description or a snackbar.
- Sample and test data are synthetic: example.com addresses, no passwords, codes, recovery codes or tokens.
- Screenshots in tests use synthetic data and are never committed
  ([0012](../Decisions/0012-visual-regression-testing.md)).

## Accessibility

The full table is in [ACCESSIBILITY.md](ACCESSIBILITY.md). For this family:

- A row is one element, read title, description, metadata; controls in it stay their own.
- Section headers, empty-state titles and accordion headers are headings.
- Selected rows and chips say so; selection never rests on colour alone (an edge, a check mark).
- Loading is one element; failures are announced; result changes are shown, not announced per keystroke.
- From 150 % text, rows and timeline events move their trailing content below the text.

## Testing

- **Unit and Compose UI (JVM):** semantics and reading order, the app's state round trips, branch order,
  sizes and gaps, large text, disabled states, and collection identity — keyed rows keeping state through
  insertion, removal and reordering, selection by id, state restoration, and recomposition of one row only.
- **Screenshots:** every component in light, dark, 200 % text and right to left.
- **Integration:** the sample's `CollectionsFlowTest` — load, search, filter, no results, selection, removal
  with Undo, a detail with Back, a failed load with Try again.
- **Device (not yet run):** `QeetrixCollectionDeviceTest` (a 500-row keyed list scrolled far and back, its
  selection and sticky header); TalkBack reading and actions on rows, chips and accordions; item animations;
  Macrobenchmark of the sample's collection.
