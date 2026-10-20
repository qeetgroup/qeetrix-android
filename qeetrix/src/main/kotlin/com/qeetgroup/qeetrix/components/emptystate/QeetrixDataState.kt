package com.qeetgroup.qeetrix.components.emptystate

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.qeetgroup.qeetrix.R
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.spinner.QeetrixSkeleton
import com.qeetgroup.qeetrix.foundation.motion.QeetrixDuration
import com.qeetgroup.qeetrix.foundation.motion.QeetrixEasing
import com.qeetgroup.qeetrix.foundation.motion.qeetrixAnimationSpec
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.internal.QeetrixWebScale

/**
 * A failed load, as a [QeetrixDataState] shows it: a [message] for the person and, optionally, a way to try
 * again.
 *
 * @param message What happened, already localised and written for the person — never an exception's
 *   message, a status code or a stack trace, which can expose internals. `null` shows "Something went wrong
 *   while loading this data." (`qx_data_error`).
 * @param onRetry Runs when the person taps "Try again" (`qx_try_again`). Without it, no button is shown.
 *   Whether and how to load again is the app's decision.
 */
public class QeetrixDataFailure(
    public val message: String? = null,
    public val onRetry: (() -> Unit)? = null,
)

/**
 * The loading, failure and empty branches of a surface that shows loaded data, in one place, so none is
 * forgotten — the web's `DataState`.
 *
 * ```
 * val listState = rememberLazyListState()
 * QeetrixDataState(
 *     loading = state.loading,
 *     empty = state.invoices.isEmpty(),
 *     failure = if (state.failed) QeetrixDataFailure("Invoices couldn't be loaded.", onRetry = ::reload) else null,
 *     emptyContent = { QeetrixEmptyState("No invoices yet", description = "Invoices appear here once you bill a customer.") },
 * ) {
 *     LazyColumn(state = listState) {
 *         items(state.invoices, key = { it.id }) { invoice -> QeetrixListItem(invoice.number, metadata = invoice.amount) }
 *     }
 * }
 * ```
 *
 * One branch shows at a time, in the web's order: [loading], then a [failure], then [empty], then the
 * [content]; a change of branch cross-fades over the fast duration. Each branch is placed in a `Box`, so give
 * several elements their own `Column` or lazy list.
 *
 * - **Loading** shows [loadingContent] — six skeleton rows by default ([QeetrixDataLoadingRows]).
 *   Accessibility services find one element, "Loading", instead of a placeholder per row; nothing is
 *   announced while it waits.
 * - **A failure** shows an error [QeetrixEmptyState] with its message and, given `onRetry`, a "Try again"
 *   button. The message is announced as it appears, as the web's `role="alert"` is.
 * - **Empty** shows [emptyContent] — usually a [QeetrixEmptyState] with your words.
 *
 * The state is the app's: this component fetches, retries, caches and paginates nothing, and holds no state
 * of its own. Keep showing the content while you refresh it (set [loading] only when there is nothing to show
 * yet). A branch that comes back is composed again, so hoist the state its content must keep — a
 * `LazyListState`, expansion, selection — above the data state, as `listState` is above.
 */
@Composable
public fun QeetrixDataState(
    loading: Boolean,
    empty: Boolean,
    emptyContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    failure: QeetrixDataFailure? = null,
    loadingContent: @Composable () -> Unit = { QeetrixDataLoadingRows() },
    content: @Composable () -> Unit,
) {
    val phase =
        when {
            loading -> DataPhase.Loading
            failure != null -> DataPhase.Failed
            empty -> DataPhase.Empty
            else -> DataPhase.Content
        }
    val loadingName = stringResource(R.string.qx_loading)
    Crossfade(phase, modifier, qeetrixAnimationSpec(QeetrixEasing.Standard, QeetrixDuration.Fast), label = "data-state") { shown ->
        when (shown) {
            // aria-busy: one "Loading" element in place of the placeholders.
            DataPhase.Loading -> Box(Modifier.clearAndSetSemantics { contentDescription = loadingName }) { loadingContent() }

            DataPhase.Failed -> DataFailure(failure ?: QeetrixDataFailure())

            DataPhase.Empty -> emptyContent()

            DataPhase.Content -> content()
        }
    }
}

/**
 * The default loading branch of a [QeetrixDataState]: full-width skeleton rows, 40 dp tall and 12 dp apart,
 * in a 16 dp inset — the web's `space-y-3 p-4` of six `h-10` skeletons.
 *
 * @param rows How many rows; at least one is drawn.
 */
@Composable
public fun QeetrixDataLoadingRows(
    modifier: Modifier = Modifier,
    rows: Int = DEFAULT_ROWS,
) {
    Column(
        modifier.fillMaxWidth().padding(QeetrixWebScale.spacing(4f)), // p-4
        verticalArrangement = Arrangement.spacedBy(QeetrixWebScale.spacing(3f)), // space-y-3
    ) {
        repeat(rows.coerceAtLeast(1)) { QeetrixSkeleton(Modifier.fillMaxWidth().height(QeetrixWebScale.spacing(10f))) } // h-10 w-full
    }
}

/** The failure branch: a small error empty state with the message, announced, and "Try again" when the app can. */
@Composable
private fun DataFailure(failure: QeetrixDataFailure) {
    val retry = failure.onRetry
    EmptyStateContent(
        title = null,
        description = failure.message ?: stringResource(R.string.qx_data_error),
        variant = QeetrixEmptyStateVariant.Error,
        size = QeetrixEmptyStateSize.Sm,
        icon = null,
        announce = true,
        modifier = Modifier,
        actions =
            if (retry != null) {
                {
                    QeetrixButton(
                        stringResource(R.string.qx_try_again),
                        onClick = retry,
                        variant = QeetrixButtonVariant.Outline,
                        size = QeetrixButtonSize.Sm,
                        leadingIcon = QeetrixIconName.RefreshCw,
                    )
                }
            } else {
                null
            },
    )
}

/** Which branch a data state shows. */
internal enum class DataPhase { Loading, Failed, Empty, Content }

private const val DEFAULT_ROWS = 6
