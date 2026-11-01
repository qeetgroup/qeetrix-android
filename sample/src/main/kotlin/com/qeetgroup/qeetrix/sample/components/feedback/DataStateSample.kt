package com.qeetgroup.qeetrix.sample.components.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.emptystate.QeetrixDataState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
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
 * One surface's four states, driven by a simulated load the page owns — as an app's state holder would drive them.
 * Nothing is fetched: the load waits, then shows the chosen outcome.
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
            SampleFlow {
                for (each in LoadOutcome.entries) {
                    QeetrixButton(
                        each.title,
                        onClick = { outcome = each },
                        variant = QeetrixButtonVariant.Ghost,
                        icon = if (each == outcome) QeetrixIconName.Check else null,
                    )
                }
            }
            SampleSurface {
                QeetrixDataState(
                    loading = loading,
                    error = outcome == LoadOutcome.Failure,
                    empty = outcome == LoadOutcome.Empty,
                    onRetry = { attempt++ },
                    emptyState = {
                        QeetrixEmptyState(
                            "No reports yet",
                            icon = QeetrixIconName.FileText,
                            description = "Reports you create appear here.",
                            size = QeetrixEmptyStateSize.Section,
                        )
                    },
                ) {
                    Column {
                        for ((index, name) in listOf("March summary", "February summary", "January summary").withIndex()) {
                            if (index > 0) QeetrixSeparator()
                            Column(Modifier.padding(QeetrixSpace.InsetLg), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXs)) {
                                QeetrixText(name)
                                QeetrixText("Generated report", tone = QeetrixTextTone.Subtle)
                            }
                        }
                    }
                }
            }
            SampleNote(
                "Loading is one \"Loading\" element for TalkBack, and keeps the error's height so nothing jumps; an error is " +
                    "announced as it appears and offers Try again. Choose an outcome to load again.",
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
