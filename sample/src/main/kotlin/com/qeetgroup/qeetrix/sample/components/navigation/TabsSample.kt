package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabPanel
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabsOrientation
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabsVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
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

/** Views of one thing: the line and contained looks, icons and counts, a disabled tab, vertical tabs, a row that scrolls. */
@Composable
fun TabsSample() {
    SamplePage {
        SampleSection("Line: the views of a screen") {
            var section by rememberSaveable { mutableStateOf(ProjectSection.Overview) }
            val tabs =
                ProjectSection.entries.map { each ->
                    QeetrixTab(
                        each,
                        each.title,
                        icon = if (each == ProjectSection.Activity) QeetrixIconName.Bell else null,
                        badge = if (each == ProjectSection.Files) 12 else null,
                        enabled = each != ProjectSection.Billing,
                    )
                }
            QeetrixTabs(tabs, value = section, onValueChange = { section = it })
            AnimatedContent(section, label = "tab-panel") { QeetrixText(it.summary) }
            SampleNote(
                "The selected tab is weight 600 with its icon filled, and the 3 dp Ember bar slides to it. Counts are " +
                    "formatted for the locale and read with their tab.",
            )
        }
        SampleSection("Contained: a few views of one card") {
            var range by rememberSaveable { mutableStateOf("week") }
            QeetrixTabs(
                listOf(
                    QeetrixTab("day", "Day"),
                    QeetrixTab("week", "Week"),
                    QeetrixTab("failures", "Failures", badge = 3, badgeTone = QeetrixBadgeTone.Danger),
                ),
                value = range,
                onValueChange = { range = it },
                variant = QeetrixTabsVariant.Contained,
            )
        }
        SampleSection("Vertical") {
            var section by rememberSaveable { mutableStateOf(ProjectSection.Activity) }
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapLg)) {
                QeetrixTabs(
                    ProjectSection.entries.map { QeetrixTab(it, it.title) },
                    value = section,
                    onValueChange = { section = it },
                    orientation = QeetrixTabsOrientation.Vertical,
                )
                QeetrixText(section.summary)
            }
            SampleNote("The rule and the bar move to the start edge, as for the current navigation item.")
        }
        SampleSection("More tabs than fit") {
            var month by rememberSaveable { mutableIntStateOf(10) }
            // The month names in the app's language, following a change of locale.
            val locale = LocalLocale.current.platformLocale
            val months = Month.entries.map { QeetrixTab(it.ordinal, it.getDisplayName(TextStyle.FULL, locale)) }
            QeetrixTabs(months, value = month, onValueChange = { month = it })
            SampleNote(
                "The row scrolls sideways and keeps the selected tab in view. Each tab is a tab for TalkBack, read with " +
                    "whether it is selected.",
            )
        }
        SampleSection("Panels") {
            val sections =
                listOf(QeetrixTab("overview", "Overview"), QeetrixTab("members", "Members", badge = 12), QeetrixTab("billing", "Billing"))
            var section by rememberSaveable { mutableStateOf("overview") }
            QeetrixTabs(sections, section, { section = it }, activateOnFocus = false)
            for (tab in sections) {
                QeetrixTabPanel(tab, section) { QeetrixText("${tab.label}: what this tab shows.") }
            }
            SampleNote("Each panel is named by its tab for TalkBack. With activateOnFocus off, a keyboard moves focus and Enter chooses.")
        }
    }
}

@PreviewLightDark
@Composable
private fun TabsSamplePreview() {
    SamplePreview { TabsSample() }
}
