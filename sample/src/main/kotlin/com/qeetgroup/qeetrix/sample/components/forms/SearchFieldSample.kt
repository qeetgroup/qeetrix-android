package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.input.QeetrixSearchField
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

private val Members = listOf("Asha Rao", "Grace Hopper", "Ada Lovelace", "Ravi Kumar", "Meera Iyer")

/** A search box over a list: it searches when typing pauses, on the search key, and when cleared. */
@Composable
fun SearchFieldSample() {
    SamplePage {
        SampleSection("Filtering a list") {
            val query = rememberTextFieldState()
            var searched by remember { mutableStateOf("") }
            var asks by remember { mutableIntStateOf(0) }
            QeetrixSearchField(
                query,
                placeholder = "e.g. Asha",
                onSearch = {
                    searched = it
                    asks++
                },
            )
            val results = Members.filter { it.contains(searched, ignoreCase = true) }
            if (results.isEmpty()) {
                QeetrixText("No members match \"$searched\".", tone = QeetrixTextTone.Subtle)
            } else {
                for (member in results) QeetrixText(member)
            }
            SampleNote(
                "Searched $asks times: once per pause in typing, at once on the keyboard's search key, and with " +
                    "nothing when cleared. Escape clears it too.",
            )
        }
        SampleSection("With a label, and disabled") {
            QeetrixSearchField(rememberTextFieldState(), label = "Find an invoice", placeholder = "e.g. INV-2041")
            QeetrixSearchField(rememberTextFieldState("archived"), enabled = false)
            SampleNote("Without a label it is named \"Search\", the search icon its visible sign, as in a toolbar.")
        }
    }
}

@PreviewLightDark
@Composable
private fun SearchFieldSamplePreview() {
    SamplePreview { SearchFieldSample() }
}
