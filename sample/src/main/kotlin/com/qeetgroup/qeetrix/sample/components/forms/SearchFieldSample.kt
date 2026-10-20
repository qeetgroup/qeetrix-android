package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyState
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateSize
import com.qeetgroup.qeetrix.components.emptystate.QeetrixEmptyStateVariant
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.sample.SampleSurface

private val Cities = listOf("Bengaluru", "Chennai", "Hyderabad", "Kolkata", "Mumbai", "New Delhi", "Pune")

/** A search field filtering a short list on the device, with its no-results state; and one with a label. */
@Composable
fun SearchFieldSample() {
    SamplePage {
        SampleSection("Filter a list") {
            val query = rememberTextFieldState()
            var submitted by rememberSaveable { mutableStateOf<String?>(null) }
            QeetrixSearchField(query, placeholder = "Search cities", onSearch = { submitted = it })
            val text = query.text.toString().trim()
            val matches = Cities.filter { it.contains(text, ignoreCase = true) }
            SampleSurface {
                if (matches.isEmpty()) {
                    QeetrixEmptyState(
                        "No cities found",
                        description = "Nothing matches “$text”.",
                        variant = QeetrixEmptyStateVariant.NoResults,
                        size = QeetrixEmptyStateSize.Sm,
                    )
                } else {
                    for (city in matches) QeetrixListItem(city)
                }
            }
            SampleNote(submitted?.let { "Searched for “$it”." } ?: "The list filters as you type; the search key reports the query.")
        }
        SampleSection("With a label") {
            QeetrixSearchField(rememberTextFieldState(), label = "Find a member")
            SampleNote("Without a label, the field is named \"Search\" for TalkBack.")
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchFieldSamplePreview() {
    SamplePreview { SearchFieldSample() }
}
