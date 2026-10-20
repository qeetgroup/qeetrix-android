package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationBar
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationItem
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The bottom bar with the app's own selection, a badge, a reselected destination and a disabled one. */
@Composable
fun NavigationBarSample() {
    SamplePage {
        SampleSection("Top-level destinations") {
            var destination by rememberSaveable { mutableStateOf(SampleDestination.Inbox) }
            var reselected by rememberSaveable { mutableIntStateOf(0) }
            QeetrixNavigationBar(
                items = sampleDestinations(),
                selected = destination,
                onSelect = {
                    if (it == destination) reselected++
                    destination = it
                },
            )
            SampleNote(
                "Showing ${destination.title}. The app owns the selection: the bar reports each choice, and choosing the " +
                    "current destination again ($reselected times so far) lets the app scroll it back to the top.",
            )
        }
        SampleSection("Unavailable destination") {
            QeetrixNavigationBar(
                items =
                    listOf(
                        QeetrixNavigationItem("home", "Home", QeetrixIconName.House),
                        QeetrixNavigationItem("admin", "Admin", QeetrixIconName.Lock, enabled = false),
                        QeetrixNavigationItem("settings", "Settings", QeetrixIconName.Settings),
                    ),
                selected = "home",
                onSelect = {},
            )
            SampleNote("TalkBack reads each item as a tab with its label, badge and whether it is selected.")
        }
    }
}

@PreviewLightDark
@Composable
private fun NavigationBarSamplePreview() {
    SamplePreview { NavigationBarSample() }
}
