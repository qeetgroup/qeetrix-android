package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenu
import com.qeetgroup.qeetrix.components.dropdownmenu.QeetrixMenuItem
import com.qeetgroup.qeetrix.components.navigation.QeetrixBackButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixTopAppBar
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Top app bars: a detail screen's back button, a subtitle, actions with an overflow menu, the scrolled rule. */
@Composable
fun TopAppBarSample() {
    SamplePage {
        SampleSection("Detail screen") {
            var scrolled by rememberSaveable { mutableStateOf(true) }
            var menu by remember { mutableStateOf(false) }
            var last by rememberSaveable { mutableStateOf("Nothing yet") }
            QeetrixTopAppBar(
                title = "Invoice INV-2041",
                subtitle = "Draft",
                navigationIcon = { QeetrixBackButton(onClick = { last = "Back" }) },
                actions = {
                    QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = { last = "Share" })
                    QeetrixMenu(
                        expanded = menu,
                        onExpandedChange = { menu = it },
                        trigger = { QeetrixIconButton(QeetrixIconName.EllipsisVertical, "More options", onClick = { menu = true }) },
                    ) {
                        QeetrixMenuItem("Duplicate", onClick = { last = "Duplicate" }, icon = QeetrixIconName.Copy)
                        QeetrixMenuItem("Rename", onClick = { last = "Rename" }, icon = QeetrixIconName.Pencil)
                    }
                },
                scrolled = scrolled,
            )
            QeetrixSwitch(scrolled, { scrolled = it }, label = "Content scrolled under the bar")
            SampleNote("Last action: $last. The bar keeps no navigation state: its back button calls the app's own back.")
        }
        SampleSection("Top-level screen") {
            QeetrixTopAppBar(title = "Settings")
            SampleNote("A top-level screen has no back button: system Back is the app's navigation's, whether or not a bar shows one.")
        }
    }
}

@PreviewLightDark
@Composable
private fun TopAppBarSamplePreview() {
    SamplePreview { TopAppBarSample() }
}
