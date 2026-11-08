package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.list.QeetrixListItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBarSize
import com.qeetgroup.qeetrix.components.navigation.rememberQeetrixTopAppBarScrollState
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Top app bars: a detail screen's back button, a description and neutral actions; a top-level screen without back. */
@Composable
fun TopAppBarSample() {
    SamplePage {
        SampleSection("Detail screen") {
            var last by rememberSaveable { mutableStateOf("Nothing yet") }
            QeetrixTopAppBar(
                title = "Invoice INV-2041",
                description = "Draft",
                navigationIcon = { QeetrixBackButton(onClick = { last = "Back" }, contentDescription = "Back to Invoices") },
                actions = {
                    QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = { last = "Share" }, tone = QeetrixButtonTone.Neutral)
                    QeetrixIconButton(
                        QeetrixIconName.EllipsisVertical,
                        "More options",
                        onClick = { last = "More options" },
                        tone = QeetrixButtonTone.Neutral,
                    )
                },
            )
            SampleNote(
                "Last action: $last. The page header's band, ruled at its foot, with the title in title.lg. The bar keeps " +
                    "no navigation state: its back button calls the app's own back, and the overflow opens a QeetrixMenu.",
            )
        }
        SampleSection("Top-level screen") {
            QeetrixTopAppBar(title = "Settings")
            SampleNote("A top-level screen has no back button: system Back is the app's navigation's, whether or not a bar shows one.")
        }
        SampleSection("An onboarding step") {
            QeetrixTopAppBar(title = null, description = "Step 1 of 2", navigationIcon = { QeetrixBackButton(onClick = {}) })
            SampleNote("No title: the step is a caption, read as text; the screen's heading is in its content below.")
        }
        SampleSection("Count, status and tabs") {
            var tab by rememberSaveable { mutableStateOf("all") }
            QeetrixTopAppBar(
                title = "Members",
                count = 412,
                status = { QeetrixBadge("Trial", tone = QeetrixBadgeTone.Warning) },
                navigationIcon = { QeetrixBackButton(onClick = {}, contentDescription = "Back to Workspace") },
                tabs = {
                    QeetrixTabs(
                        listOf(
                            QeetrixTab("all", "All"),
                            QeetrixTab("admins", "Admins"),
                            QeetrixTab("invited", "Invited", badge = 3),
                        ),
                        tab,
                        {
                            tab =
                                it
                        },
                    )
                },
            )
        }
        SampleSection("A large title that collapses") {
            val bar = rememberQeetrixTopAppBarScrollState()
            Column(Modifier.fillMaxWidth().height(360.dp)) {
                QeetrixTopAppBar(title = "Approvals", description = "3 waiting", size = QeetrixTopAppBarSize.Large, scrollState = bar)
                Column(Modifier.weight(1f).nestedScroll(bar.nestedScrollConnection).verticalScroll(rememberScrollState())) {
                    repeat(12) { QeetrixListItem("Sign-in request ${12 - it}", description = "Chrome on macOS") }
                }
            }
            SampleNote("Scroll the list: the large title folds into the bar, whose own title fades in.")
        }
    }
}

@PreviewLightDark
@Composable
private fun TopAppBarSamplePreview() {
    SamplePreview { TopAppBarSample() }
}
