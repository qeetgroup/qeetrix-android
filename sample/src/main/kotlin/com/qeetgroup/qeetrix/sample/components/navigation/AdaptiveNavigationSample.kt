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
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationLayout
import com.qeetgroup.qeetrix.components.navigation.QeetrixNavigationScaffold
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
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
            var choice by rememberSaveable { mutableStateOf<String?>(null) }
            val layout = QeetrixNavigationLayout.entries.firstOrNull { it.value == choice } ?: QeetrixNavigationLayout.forWindowWidth(width)
            QeetrixRadioGroup(
                choice,
                { choice = it },
                listOf(
                    QeetrixOption(
                        null,
                        "From the window width",
                        "${width.value.toInt()} dp now: ${QeetrixNavigationLayout.forWindowWidth(width).value}",
                    ),
                    QeetrixOption(QeetrixNavigationLayout.Bar.value, "Bar"),
                    QeetrixOption(QeetrixNavigationLayout.Rail.value, "Rail"),
                ),
                label = "Layout",
            )
            var destination by rememberSaveable { mutableStateOf(SampleDestination.Home) }
            QeetrixNavigationScaffold(
                sampleDestinations(),
                selected = destination,
                onSelect = { destination = it },
                modifier = Modifier.fillMaxWidth().height(360.dp),
                layout = layout,
            ) {
                var taps by rememberSaveable { mutableIntStateOf(0) }
                Column(Modifier.fillMaxSize().padding(QeetrixSpacing.L), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.M)) {
                    QeetrixText(destination.title, variant = QeetrixTextVariant.H3)
                    QeetrixButton("Tapped $taps times", onClick = { taps++ }, variant = QeetrixButtonVariant.Outline)
                }
            }
            SampleNote(
                "Below 600 dp of window width the destinations are a bottom bar, from 600 dp a rail — Android's window size " +
                    "classes. Rotate, fold or resize the window, or switch the layout above: the counter keeps its value.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AdaptiveNavigationSamplePreview() {
    SamplePreview { AdaptiveNavigationSample() }
}
