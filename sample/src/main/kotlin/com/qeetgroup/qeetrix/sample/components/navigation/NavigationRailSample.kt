package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationRail
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** The rail, as a medium or expanded window shows the destinations, with a primary action in its header. */
@Composable
fun NavigationRailSample() {
    SamplePage {
        SampleSection("Rail with a header") {
            var destination by rememberSaveable { mutableStateOf(SampleDestination.Home) }
            Row(Modifier.height(420.dp)) {
                QeetrixNavigationRail(sampleDestinations(), value = destination, onValueChange = { destination = it }) {
                    QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = {}, variant = QeetrixButtonVariant.Solid)
                }
                Box(Modifier.padding(QeetrixSpace.InsetLg)) { QeetrixHeading(destination.title, level = 3) }
            }
            SampleNote(
                "The current destination has the Ember bar on its start edge and a filled, round Ember icon, as the web " +
                    "Sidebar marks the current page. Each label stays under its icon; at large text sizes the rail widens " +
                    "to fit them, and it scrolls when the destinations do not fit its height.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NavigationRailSamplePreview() {
    SamplePreview { NavigationRailSample() }
}
