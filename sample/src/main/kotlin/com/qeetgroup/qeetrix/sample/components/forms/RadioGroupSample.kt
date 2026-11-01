package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** One choice from a set: rows with descriptions, a required question, a wrapping row, and tiles. */
@Composable
fun RadioGroupSample() {
    SamplePage {
        SampleSection("Billing cycle") {
            var cycle by rememberSaveable { mutableStateOf("monthly") }
            QeetrixRadioGroup(
                cycle,
                { cycle = it },
                listOf(
                    QeetrixOption("monthly", "Monthly"),
                    QeetrixOption("yearly", "Yearly", description = "Two months free, billed once a year."),
                    QeetrixOption("custom", "Custom", description = "Talk to sales.", enabled = false),
                ),
                label = "Billing cycle",
                description = "You can change it later.",
            )
            SampleNote("One tab stop for the group; each option is a radio button read with its description.")
        }
        SampleSection("A required question") {
            var language by rememberSaveable { mutableStateOf<String?>(null) }
            QeetrixRadioGroup(
                language,
                { language = it },
                listOf(QeetrixOption("en", "English"), QeetrixOption("hi", "हिन्दी")),
                label = "Invoice language",
                required = true,
                orientation = QeetrixChoiceOrientation.Horizontal,
                error = if (language == null) "Choose a language for invoices." else null,
            )
        }
        SampleSection("Tiles") {
            var type by rememberSaveable { mutableStateOf("web") }
            QeetrixRadioGroup(
                type,
                { type = it },
                listOf(
                    QeetrixOption("web", "Web app", "Users sign in through a redirect.", icon = QeetrixIconName.House),
                    QeetrixOption("service", "Service", "Machine to machine, no user.", icon = QeetrixIconName.Server),
                    QeetrixOption("native", "Native app", "On phones and desktops.", icon = QeetrixIconName.Settings),
                ),
                label = "Application type",
                variant = QeetrixChoiceVariant.Tile,
                columns = 3,
            )
            SampleNote("The chosen tile takes the selected tint and a 2 dp Ember edge; the whole tile is the target.")
        }
    }
}

@PreviewLightDark
@Composable
private fun RadioGroupSamplePreview() {
    SamplePreview { RadioGroupSample() }
}
