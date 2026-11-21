package com.qeetgroup.qeetrix.sample.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.QeetrixTextWeight
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme

/** One choice from a set: rows with descriptions, a required question, a wrapping row, tiles, titles of their own, tiles under a heading. */
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
        SampleSection("Titles of their own") {
            var language by rememberSaveable { mutableStateOf("hi") }
            val scripts =
                listOf(
                    Triple("hi", "हिन्दी", "Hindi"),
                    Triple("te", "తెలుగు", "Telugu"),
                    Triple("ta", "தமிழ்", "Tamil"),
                    Triple("en", "English", "English"),
                )
            QeetrixRadioGroup(
                language,
                { language = it },
                scripts.map { (code, own, english) -> QeetrixOption(code, if (own == english) own else "$own, $english") },
                label = "Language",
                variant = QeetrixChoiceVariant.Tile,
                columns = 2,
                optionLabel = { option ->
                    val (code, own, english) = scripts.first { it.first == option.value }
                    Column {
                        // Each language in its own script, with its locale for the right fallback font.
                        BasicText(
                            own,
                            style =
                                QeetrixTheme.typography.titleLg.copy(
                                    color = LocalQeetrixContentColor.current,
                                    localeList = LocaleList(code),
                                ),
                        )
                        if (own != english) QeetrixText(english, size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
                    }
                },
            )
            var sim by rememberSaveable { mutableStateOf("1") }
            QeetrixRadioGroup(
                sim,
                { sim = it },
                listOf(QeetrixOption("1", "SIM 1, Jio", "Number ends in 3210"), QeetrixOption("2", "SIM 2, Airtel", "Number ends in 8841")),
                label = "Which SIM is yours?",
                variant = QeetrixChoiceVariant.Tile,
                optionLabel = { option ->
                    Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), verticalAlignment = Alignment.CenterVertically) {
                        QeetrixIcon(QeetrixIconName.Smartphone, contentDescription = null, size = QeetrixSize.IconMd)
                        QeetrixText(option.label.replace(", ", " · "), weight = QeetrixTextWeight.Semibold)
                    }
                },
            )
            var photo by rememberSaveable { mutableStateOf("contacts") }
            QeetrixRadioGroup(
                photo,
                { photo = it },
                listOf(
                    QeetrixOption("everyone", "Everyone"),
                    QeetrixOption("contacts", "My contacts, recommended"),
                    QeetrixOption("nobody", "Nobody"),
                ),
                label = "Who can see your photo",
                optionLabel = { option ->
                    Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpace.GapSm), verticalAlignment = Alignment.CenterVertically) {
                        QeetrixText(option.label.substringBefore(","))
                        if (option.value == "contacts") QeetrixBadge("Recommended", tone = QeetrixBadgeTone.Brand)
                    }
                },
            )
            SampleNote(
                "A language in its own script, a picture beside a title and a badge after one: each option draws its own " +
                    "title, and TalkBack reads its plain label (\"My contacts, recommended\").",
            )
        }
        SampleSection("Under a screen heading") {
            var sim by rememberSaveable { mutableStateOf("back") }
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapXl)) {
                QeetrixHeading("This phone has a different SIM", level = 3, size = QeetrixHeadingSize.TitleLg)
                QeetrixRadioGroup(
                    sim,
                    { sim = it },
                    listOf(
                        QeetrixOption("back", "I'll put the Jio SIM back", "Your account stays on 98765 43210."),
                        QeetrixOption("move", "Move my account to this SIM", "We'll send a code to the new number."),
                    ),
                    label = "This phone has a different SIM",
                    variant = QeetrixChoiceVariant.Tile,
                    hideLabel = true,
                )
            }
            SampleNote(
                "The heading asks the question, so the group hides its own label: TalkBack still reads it as the group's " +
                    "heading before the tiles.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun RadioGroupSamplePreview() {
    SamplePreview { RadioGroupSample() }
}
