package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixChipGroup
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataFailure
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.list.QeetrixListItemIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface
import kotlinx.coroutines.delay

/** What the simulated load ends in. */
private enum class LoadOutcome(
    val title: String,
) {
    Data("Data"),
    Empty("Empty"),
    Failure("Failure"),
}

/**
 * One surface's four branches, driven by a simulated load the page owns — as an app's state holder would
 * drive them. Nothing is fetched: the load waits, then shows the chosen outcome.
 */
@Composable
fun DataStateSample() {
    SamplePage {
        SampleSection("A loaded list") {
            var outcome by rememberSaveable { mutableStateOf(LoadOutcome.Data) }
            var attempt by rememberSaveable { mutableIntStateOf(0) }
            var loading by rememberSaveable { mutableStateOf(true) }
            LaunchedEffect(outcome, attempt) {
                loading = true
                delay(SIMULATED_LOAD_MILLIS)
                loading = false
            }
            QeetrixChipGroup(
                label = "Outcome",
                selected = outcome,
                onSelect = { outcome = it },
                options = LoadOutcome.entries.map { QeetrixOption(it, it.title) },
            )
            SampleSurface {
                QeetrixDataState(
                    loading = loading,
                    empty = outcome == LoadOutcome.Empty,
                    failure = if (outcome == LoadOutcome.Failure) QeetrixDataFailure(onRetry = { attempt++ }) else null,
                    emptyContent = {
                        QeetrixEmptyState(
                            "No reports yet",
                            description = "Reports you create appear here.",
                            size = QeetrixEmptyStateSize.Sm,
                            icon = QeetrixIconName.FileText,
                        )
                    },
                ) {
                    Column {
                        for (name in listOf("March summary", "February summary", "January summary")) {
                            QeetrixListItem(
                                name,
                                description = "Generated report",
                                leading = { QeetrixListItemIcon(QeetrixIconName.FileText) },
                            )
                        }
                    }
                }
            }
            SampleNote(
                "Loading is one \"Loading\" element for TalkBack; a failure is announced as it appears and offers Try again. " +
                    "Choose an outcome to load again.",
            )
        }
    }
}

private const val SIMULATED_LOAD_MILLIS = 1500L

@PreviewLightDark
@Composable
private fun DataStateSamplePreview() {
    SamplePreview { DataStateSample() }
}
