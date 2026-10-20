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
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
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
                QeetrixNavigationRail(sampleDestinations(), selected = destination, onSelect = { destination = it }) {
                    QeetrixIconButton(QeetrixIconName.Plus, "New message", onClick = {}, variant = QeetrixButtonVariant.Default)
                }
                Box(Modifier.padding(QeetrixSpacing.L)) { QeetrixText(destination.title, variant = QeetrixTextVariant.H3) }
            }
            SampleNote(
                "The selected destination keeps the sidebar's indicator on its inline start. At large text sizes the rail " +
                    "widens to fit its labels; it scrolls when the destinations do not fit its height.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NavigationRailSamplePreview() {
    SamplePreview { NavigationRailSample() }
}
