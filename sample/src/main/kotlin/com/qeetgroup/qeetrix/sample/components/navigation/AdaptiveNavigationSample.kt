package com.qeetgroup.qeetrix.sample.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationLayout
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold
import com.qeetgroup.qeetrix.components.tabs.QeetrixTab
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabs
import com.qeetgroup.qeetrix.components.tabs.QeetrixTabsVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** How the scaffold chooses between bar and rail, and keeps the content's state when it switches. */
@Composable
fun AdaptiveNavigationSample() {
    SamplePage {
        SampleSection("Bar or rail") {
            val width = LocalWindowInfo.current.containerDpSize.width
            var choice by rememberSaveable { mutableStateOf("auto") }
            QeetrixTabs(
                listOf(
                    QeetrixTab("auto", "From the width (${QeetrixNavigationLayout.forWindowWidth(width).value})"),
                    QeetrixTab(QeetrixNavigationLayout.Bar.value, "Bar"),
                    QeetrixTab(QeetrixNavigationLayout.Rail.value, "Rail"),
                ),
                value = choice,
                onValueChange = { choice = it },
                variant = QeetrixTabsVariant.Contained,
            )
            val layout = QeetrixNavigationLayout.entries.firstOrNull { it.value == choice } ?: QeetrixNavigationLayout.forWindowWidth(width)
            var destination by rememberSaveable { mutableStateOf(SampleDestination.Home) }
            QeetrixNavigationScaffold(
                sampleDestinations(),
                value = destination,
                onValueChange = { destination = it },
                modifier = Modifier.fillMaxWidth().height(360.dp),
                layout = layout,
            ) {
                var taps by rememberSaveable { mutableIntStateOf(0) }
                Column(
                    Modifier.fillMaxSize().padding(QeetrixSpace.InsetLg),
                    verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd),
                ) {
                    QeetrixHeading(destination.title, level = 3)
                    QeetrixButton("Tapped $taps times", onClick = { taps++ }, variant = QeetrixButtonVariant.Soft)
                }
            }
            SampleNote(
                "Below breakpoint.medium (600 dp of window width, ${width.value.toInt()} dp now) the destinations are a " +
                    "bottom bar, from it a rail — Android's window size classes. Rotate, fold or resize the window, or " +
                    "switch the layout above: the counter keeps its value.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AdaptiveNavigationSamplePreview() {
    SamplePreview { AdaptiveNavigationSample() }
}
