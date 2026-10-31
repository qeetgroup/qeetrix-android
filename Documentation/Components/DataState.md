# Data state

`QeetrixDataState` · `components.emptystate` · `@qeetrix/react`: DataState
([experimental, with EmptyState](https://ui.qeet.in/components/empty-state)) · sample: Feedback → Data state

The four states of loaded content in one place: loading, error, empty and the content.

```kotlin
QeetrixDataState(
    loading = state.loading,
    error = state.failed,
    empty = state.invoices.isEmpty(),
    onRetry = ::reload,
    emptyState = { QeetrixEmptyState("No invoices yet", description = "Invoices appear here once you bill a customer.") },
) {
    InvoiceList(state.invoices)
}
```

## Usage

- **Precedence**: `loading`, then `error`, then `empty`, then the content.
- **Loading** shows a labelled `lg` spinner, centred in a region that holds the error state's height, so the screen
  does not jump when a load fails.
- **Error** shows a section-size empty state in the danger tone with circle-alert: `errorTitle`,
  `errorDescription`, and with `onRetry` a soft "Try again" button (`retryLabel`, `qx_try_again`). The raw error is
  never shown; log it in the app.
- **Empty** shows `emptyState`.
- **The content** is composed as it is, with no wrapper; `modifier` goes on the loading, error and empty regions.
- The state is the app's: nothing is fetched, retried or cached here. Keep showing content while refreshing.

## Accessibility

Loading is one element, "Loading" (`qx_loading`); the held error state is neither seen nor reachable. The error is
announced as it appears (an assertive live region, the web's `role="alert"`); Try again is its own control.

## Theme and tokens

Those of [Empty state](EmptyState.md) (section size, danger tone), [Spinner](Spinner.md) (`lg`) and
[Button](Button.md) (`soft`).

## Platform differences

- **Default words** are the web's: "Couldn't load this" (`qx_data_error`) and "Try again, or check your connection."
  (`qx_data_error_description`).
- `error` is a Boolean: the web accepts any truthy error and never shows it.
- The earlier Android `QeetrixDataFailure`, custom loading content and skeleton rows are gone, as on the web; compose
  skeletons yourself when a screen needs them.

Related: [Empty state](EmptyState.md), [Skeleton](Skeleton.md), [Spinner](Spinner.md).
