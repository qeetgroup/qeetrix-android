# 0016 — Data display and content states: Compose's collections, the app's data

**Status:** Accepted, 2026-10-19 (Phase 5). Builds on [0010](0010-component-api-conventions.md),
[0011](0011-component-tokens-and-web-metrics.md), [0014](0014-form-components.md) and
[0015](0015-navigation-overlays-and-feedback.md).

## Context

Phase 5 adds what collection and detail screens are built from: rows, grouped sections, labelled details,
expandable content, empty and loading states, search, filters and histories.

Compose already provides the hard parts. `LazyColumn` and `LazyVerticalGrid` compose only what is visible,
reuse item slots by `contentType`, keep an item's state under its `key` and keep sticky headers in place;
`TextFieldState` holds a query; `animateItem()` moves rows. What it doesn't have is a disclosure, an empty
state, a description list or a search field outside Material.

The web catalogue (`@qeetrix/ui` 2.1.6) has `DescriptionList`, `Accordion` and `Collapsible`, `EmptyState`,
`DataState`, `Skeleton`, `Chip`, `Timeline`, `Table`, `DataTable` and `AspectRatio`. It has no list row and
no search component: a web screen uses a table or a listbox where a phone uses a list. qeetrix-ios settled
the same questions in its ADR 0015; this record takes its positions where Compose allows.

## Decision

1. **Qeetrix presents; the app owns the data.**
   - Components take values and slots. There is no data store, view model, repository, fetching, caching,
     pagination, search index or filter engine, and no Retrofit, Room, Paging or image loader.
   - Selection, expansion, the query and the load state are parameters the app passes and changes.
     Components keep no state of their own beyond animation.
   - Rows take the app's identity through the lazy list's `key`: a row keeps its state, and the app's
     selection and expansion keyed by id stay right, through insertions, removals and reordering.
2. **Compose's collections are not wrapped.** `LazyColumn`, `LazyVerticalGrid`, `stickyHeader` and
   `animateItem()` are used directly; Qeetrix adds no list container, section container or custom
   virtualization. Qeetrix supplies what goes inside: `QeetrixListItem`, `QeetrixSectionHeader`, and the
   rest of this record's components. A section is the documented pattern of a `stickyHeader` (or `item`)
   holding a `QeetrixSectionHeader`, then the group's `items`.
3. **Web names where the web has the component.** `QeetrixDescriptionList`/`QeetrixDescriptionItem` (the
   proposal's "KeyValueRow"), `QeetrixAccordion`/`QeetrixAccordionItem`, `QeetrixEmptyState`,
   `QeetrixDataState`, `QeetrixSkeleton`, `QeetrixChip`/`QeetrixChipGroup` (the proposal's "FilterChip"),
   `QeetrixTimeline` follow the web's names, variants and metrics, in the web's families' packages
   (`descriptionlist`, `accordion`, `emptystate`, `spinner`, `badge`, `feed`). The `accordion`, `skeleton`
   and `timeline` component tokens are adopted; the generator learns millisecond component tokens
   (`component.accordion.duration`) for it.
4. **One Android and iOS family: `list`.** `QeetrixListItem`, `QeetrixListItemIcon` and
   `QeetrixSectionHeader` have no web counterpart. They are built from web tokens and compositions — the
   notification row's tile and type, the table's selected row (`surface.brand-subtle` and a 2 dp
   `border.brand` edge, so selection is a shape as well as a colour), the density's row height and cell
   padding — with the same names and slots as on iOS. Unlike iOS, an Android row is clickable itself
   (`onClick`, `onLongClick`), because Compose's lists have no row-level link or button.
5. **Search is a field, the filtering the app's.** Android has no native `searchable`, and Material 3's
   `SearchBar` is experimental and Material-drawn, so `QeetrixSearchField` is built on the Phase 3 field
   parts (0014): a search icon, a clear button, the keyboard's search key, "Search" as its name without a
   label. It debounces, cancels and remembers nothing; the documentation shows the `snapshotFlow` +
   `collectLatest` idiom, which needs no experimental coroutine API.
6. **Empty states are drawn by Qeetrix**: the web contract — a tinted tile per variant, three sizes, Qeet
   Sans — with the words and actions always the app's.
7. **The data state follows the web's order**: loading, then failure, then empty, then content.
   - A failure carries a message written for the person and an optional retry ("Try again"). Unlike the
     web's default, it never shows an exception's message.
   - Loading is one "Loading" element for TalkBack; the failure's message is an assertive live region.
   - Branches are composed again when they return, so the app hoists the content's state (its
     `LazyListState`) above the data state.
8. **Accessibility semantics.** A row is one element read title, description, metadata (placed in that
   order, since Compose reads children in placement order) and is `selected` only when it is; accordion
   headers are heading buttons with "Expanded"/"Collapsed" state descriptions and expand/collapse actions
   (Compose 1.12 has no expanded-state property); toggling chips are checkboxes, as Material's filter chips
   are, and single-choice chip groups are radio groups; a removable chip's button is named for its chip
   ("Remove Design"); skeletons are invisible; a timeline event's marker label is its state description.
9. **Shared layout helpers stay internal**: a stack that rules between its children from their positions
   (accordion, description list), the description grid, the timeline's gaps and connectors. Paragraph text
   in these components fills its column rather than measuring to its content, because content-width text in
   a right-to-left paragraph wrapped its last word in the RTL screenshots.
10. **Deferred:**
    - **`QeetrixDataTable`.** The web's is a TanStack data grid: sorting, column filters, resizing and
      selection over wide tables. On a phone a list of rows opening a description list serves the purpose,
      as on iOS; a tablet table is a later phase's decision, with a product that needs one.
    - **Image presentation.** Qeetrix has no business loading or caching images, and adds no image library.
      Apps use their own loader (Coil, Glide) into `QeetrixAvatar`'s `image` or an `Image`, with
      `Modifier.aspectRatio` for the web's `AspectRatio`, and never send credentials with an image request
      implicitly.
    - **Pull to refresh.** Material 3's `PullToRefreshBox` (stable in 1.4.0) works around any Qeetrix list;
      wrapping it would put Material in a second public component, against 0015's boundary. The standard
      documents its use.
    - **`Feed`, `TreeView`, `FilterBar`, `Pagination`, `Stat`**: not in this phase's scope.

## Consequences

- Collections behave as Android users expect — lazy composition, recycling, sticky headers, item
  animations, TalkBack's list navigation — because they are Compose's, and products keep their own data
  layers.
- Apps write their own filtering and loading logic, a few lines each; the sample's collections screen shows
  the idiom with keyed rows, a selection, removal with undo, and every content state.
- Evidence of performance is structural, not timed: every new public composable is restartable and
  skippable in the Compose compiler's report, `QeetrixDataFailure` is stable, a test shows that selecting
  one row of twenty recomposes only that row, and the skeleton's shimmer only redraws. No frame timing or
  Macrobenchmark was run: there is no device in this environment.
- Like iOS, component tokens built on `control.*` resolve at the default density, so the accordion's header
  padding and the timeline's gaps don't follow a compact density as the web's do.
- Chip groups reserve 48 dp rows (0010), so wrapped chips are further apart than on the web.
