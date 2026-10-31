package com.qeetgroup.qeetrix.components.emptystate

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinner
import com.qeetgroup.qeetrix.components.spinner.QeetrixSpinnerSize
import com.qeetgroup.qeetrix.icons.QeetrixIconName

/**
 * The four states of loaded content in one place — `@qeetrix/react`'s DataState: [loading], then [error], then
 * [empty], then the [content], in that order, so none is forgotten.
 *
 * ```
 * val listState = rememberLazyListState()
 * QeetrixDataState(
 *     loading = state.loading,
 *     error = state.failed,
 *     empty = state.invoices.isEmpty(),
 *     onRetry = ::reload,
 *     emptyState = { QeetrixEmptyState("No invoices yet", description = "Invoices appear here once you bill a customer.") },
 * ) {
 *     LazyColumn(state = listState) {
 *         items(state.invoices, key = { it.id }) { invoice -> InvoiceRow(invoice) }
 *     }
 * }
 * ```
 *
 * - **Loading** shows a labelled `lg` [QeetrixSpinner] ("Loading", `qx_loading`), centred in a region that keeps the
 *   height the error state would have, so the screen does not jump when a load fails. Accessibility services find one
 *   element, "Loading", while it waits.
 * - **An error** shows a [QeetrixEmptyStateSize.Section] empty state in the danger tone with the sharp circle-alert:
 *   [errorTitle] and [errorDescription], and with [onRetry] a soft "Try again" button ([retryLabel], `qx_try_again`).
 *   It is announced as it appears, as the web's `role="alert"` is. It never shows the raw error, which may hold
 *   internals: log it in the app.
 * - **Empty** shows [emptyState] — usually a [QeetrixEmptyState] with your words.
 * - **The content** is composed as it is, with no wrapper, so a list keeps its place in its parent. [modifier] goes
 *   on each state this draws (loading, error, empty), not on the content.
 *
 * The state is the app's: this component fetches, retries, caches and paginates nothing, and holds no state of its
 * own. Keep showing the content while you refresh it (set [loading] only when there is nothing to show yet). A state
 * that comes back is composed again, so hoist what its content must keep — a `LazyListState`, expansion, selection —
 * above the data state, as `listState` is above.
 */
@Composable
public fun QeetrixDataState(
    loading: Boolean,
    modifier: Modifier = Modifier,
    error: Boolean = false,
    empty: Boolean = false,
    emptyState: @Composable () -> Unit = {},
    onRetry: (() -> Unit)? = null,
    errorTitle: String = stringResource(R.string.qx_data_error),
    errorDescription: String? = stringResource(R.string.qx_data_error_description),
    retryLabel: String = stringResource(R.string.qx_try_again),
    content: @Composable () -> Unit,
) {
    when {
        loading -> {
            LoadingRegion(modifier) {
                DataFailure(errorTitle, errorDescription, onRetry, retryLabel, announce = false, Modifier)
            }
        }

        error -> {
            DataFailure(errorTitle, errorDescription, onRetry, retryLabel, announce = true, modifier)
        }

        empty -> {
            Box(modifier) { emptyState() }
        }

        else -> {
            content()
        }
    }
}

/**
 * `[data-state="loading"]`: the spinner centred over [reserve] — the error state, measured but never placed, so it is
 * unseen, unreachable and silent (`.qx-data-state-reserve`: `visibility: hidden`, `inert`, `aria-hidden`) and holds
 * the region at its height.
 */
@Composable
private fun LoadingRegion(
    modifier: Modifier,
    reserve: @Composable () -> Unit,
) {
    Layout(
        content = {
            QeetrixSpinner(size = QeetrixSpinnerSize.Lg) // <Spinner size="lg" label={spinner.label} />
            Box(Modifier.clearAndSetSemantics {}) { reserve() }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val spinner = measurables[0].measure(loose)
        val held = measurables[1].measure(loose)
        val width = if (constraints.hasBoundedWidth) constraints.maxWidth else maxOf(spinner.width, held.width)
        val height = maxOf(spinner.height, held.height).coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(width, height) {
            // place-self: center; the reserve is not placed.
            spinner.placeRelative((width - spinner.width) / 2, (height - spinner.height) / 2)
        }
    }
}

/** data-state.tsx `failure`: a section-size, danger-toned empty state with circle-alert and an optional soft retry. */
@Composable
private fun DataFailure(
    title: String,
    description: String?,
    onRetry: (() -> Unit)?,
    retryLabel: String,
    announce: Boolean,
    modifier: Modifier,
) {
    EmptyStateContent(
        title = title,
        description = description,
        icon = QeetrixIconName.CircleAlert,
        size = QeetrixEmptyStateSize.Section,
        tone = QeetrixEmptyStateTone.Danger,
        level = null,
        announce = announce,
        modifier = modifier,
        footer = null,
        actions = onRetry?.let { retry -> { QeetrixButton(retryLabel, onClick = retry, variant = QeetrixButtonVariant.Soft) } },
    )
}
