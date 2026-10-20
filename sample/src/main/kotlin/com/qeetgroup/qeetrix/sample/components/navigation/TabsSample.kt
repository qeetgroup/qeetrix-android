package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabsVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import java.time.Month
import java.time.format.TextStyle

private enum class ProjectSection(
    val title: String,
    val summary: String,
) {
    Overview("Overview", "Three open tasks and one review due this week."),
    Activity("Activity", "Ada commented on the design brief two hours ago."),
    Files("Files", "Twelve files, the newest uploaded yesterday."),
    Billing("Billing", "Billing is managed by the workspace owner."),
}

/** In-content tabs: both treatments, a disabled tab, the app's own panel, and a row that scrolls. */
@Composable
fun TabsSample() {
    SamplePage {
        SampleSection("Default: a view switch") {
            var section by rememberSaveable { mutableStateOf(ProjectSection.Overview) }
            val tabs =
                ProjectSection.entries.map { each ->
                    val icon = if (each == ProjectSection.Activity) QeetrixIconName.Bell else null
                    QeetrixTab(each, each.title, icon, enabled = each != ProjectSection.Billing)
                }
            QeetrixTabs(tabs, selected = section, onSelect = { section = it })
            AnimatedContent(section, label = "tab-panel") { QeetrixText(it.summary) }
        }
        SampleSection("Line: section navigation") {
            var section by rememberSaveable { mutableStateOf(ProjectSection.Activity) }
            val tabs = ProjectSection.entries.map { QeetrixTab(it, it.title) }
            QeetrixTabs(tabs, selected = section, onSelect = { section = it }, variant = QeetrixTabsVariant.Line)
            QeetrixText(section.summary)
        }
        SampleSection("More tabs than fit") {
            var month by rememberSaveable { mutableIntStateOf(10) }
            // The month names in the app's language, following a change of locale.
            val locale = LocalLocale.current.platformLocale
            val months = Month.entries.map { QeetrixTab(it.ordinal, it.getDisplayName(TextStyle.FULL, locale)) }
            QeetrixTabs(months, selected = month, onSelect = { month = it }, variant = QeetrixTabsVariant.Line)
            SampleNote(
                "The row scrolls sideways and keeps the selected tab in view. Each tab is a tab for TalkBack, read with whether it is selected.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun TabsSamplePreview() {
    SamplePreview { TabsSample() }
}
