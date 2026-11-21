package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.slider.QeetrixRangeSlider
import com.qeetgroup.qeetrix.components.slider.QeetrixSlider
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** Sliders: a value with steps and end labels, a formatted value, a range, one between two icons, and disabled. */
@Composable
fun SliderSample() {
    SamplePage {
        SampleSection("A value") {
            var volume by rememberSaveable { mutableFloatStateOf(40f) }
            QeetrixSlider(volume, { volume = it }, label = "Alert volume", step = 10f, minLabel = "Quiet", maxLabel = "Loud")
            SampleNote("Drag, or tap the track. TalkBack adjusts it a step at a time with its volume gestures.")
        }
        SampleSection("A formatted value") {
            var limit by rememberSaveable { mutableFloatStateOf(25000f) }
            QeetrixSlider(
                limit,
                { limit = it },
                label = "Daily payout limit",
                valueRange = 5000f..100000f,
                step = 5000f,
                format = { "₹" + "%,d".format(it.toInt()) },
            )
        }
        SampleSection("A range") {
            var hours by rememberSaveable(stateSaver = RangeSaver) { mutableStateOf(9f..18f) }
            QeetrixRangeSlider(
                hours,
                { hours = it },
                label = "Quiet hours",
                valueRange = 0f..24f,
                step = 1f,
                format = { "${it.toInt()}:00" },
            )
            SampleNote("Two thumbs, each its own control for TalkBack: \"Quiet hours, minimum\" and \"Quiet hours, maximum\".")
        }
        SampleSection("Between two icons") {
            var zoom by rememberSaveable { mutableFloatStateOf(1f) }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm)) {
                QeetrixIcon(QeetrixIconName.Minus, contentDescription = null, tint = QeetrixTheme.colors.iconSubtle)
                QeetrixSlider(
                    zoom,
                    { zoom = it },
                    Modifier.weight(1f).semantics { contentDescription = "Zoom" },
                    valueRange = 1f..4f,
                    step = 0.5f,
                    format = { "$it×" },
                )
                QeetrixIcon(QeetrixIconName.Plus, contentDescription = null, tint = QeetrixTheme.colors.iconSubtle)
            }
            SampleNote(
                "No visible label: the slider is named \"Zoom\" for TalkBack with a content description, read with its value " +
                    "($zoom×) and stepped by the adjust gestures.",
            )
        }
        SampleSection("Disabled") {
            QeetrixSlider(60f, {}, label = "Managed by your organisation", enabled = false)
        }
    }
}

private val RangeSaver =
    androidx.compose.runtime.saveable.Saver<ClosedFloatingPointRange<Float>, List<Float>>(
        save = { listOf(it.start, it.endInclusive) },
        restore = { it[0]..it[1] },
    )

@PreviewLightDark
@Composable
private fun SliderSamplePreview() {
    SamplePreview { SliderSample() }
}
