# Data state

`QeetrixDataState`, `QeetrixDataFailure`, `QeetrixDataLoadingRows` · `components.emptystate` ·
web: [Data State](https://ui.qeet.in/docs/components/data-state) · sample: Feedback → Data state

The loading, failure and empty branches of a surface that shows loaded data, in one place, so none is
forgotten.

```kotlin
val listState = rememberLazyListState()
QeetrixDataState(
    loading = state.loading,
    empty = state.invoices.isEmpty(),
    failure = if (state.failed) QeetrixDataFailure("Invoices couldn't be loaded.", onRetry = ::reload) else null,
    emptyContent = { QeetrixEmptyState("No invoices yet", description = "Invoices appear here once you bill a customer.") },
) {
    LazyColumn(state = listState) {
        items(state.invoices, key = { it.id }) { invoice -> QeetrixListItem(invoice.number, metadata = invoice.amount) }
    }
}
```

## Usage

- **The web's order:** `loading`, then a `failure`, then `empty`, then the content. A change of branch
  cross-fades over the fast duration.
- **The state is the app's** — from a view model, a repository, a paging source. The component fetches,
  retries, caches and paginates nothing, and holds no state of its own.
- **Loading** shows six skeleton rows (`QeetrixDataLoadingRows`) unless `loadingContent` draws skeletons in
  the shape of the content. Set `loading` only when there is nothing to show yet; keep the content while
  refreshing it.
- **A failure** carries a message written for the person — never an exception's message — and an optional
  `onRetry`, which adds "Try again".
- **Branches are composed again when they return**, so hoist what the content must keep — its
  `LazyListState`, expansion, selection — above the data state, as `listState` is above.
- Each branch is placed in a `Box`: give several elements their own `Column` or lazy list.

## Accessibility

Loading is one element, "Loading" (`qx_loading`), instead of a placeholder per row, and nothing is
announced while it waits. The failure's message is an assertive live region, announced as it appears (the
web's `role="alert"`); without a message it says "Something went wrong while loading this data."
(`qx_data_error`). "Try again" is `qx_try_again`.

## Theme and tokens

Loading rows: 40 dp skeletons, 12 dp apart, in a 16 dp inset. The failure is a small error
[empty state](EmptyState.md) with an outline button.

## Platform differences

The web's default error view shows an error object's message; Android never does. The web has no retry
button; Android and iOS offer "Try again" when the app passes a retry.

Related: [Empty state](EmptyState.md), [Skeleton](Skeleton.md).
