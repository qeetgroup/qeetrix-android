package com.qeetgroup.qeetrix.components.radiogroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.dp
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceOrientation
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.typography.QeetrixHeading
import com.qeetgroup.qeetrix.components.typography.QeetrixHeadingSize
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextSize
import com.qeetgroup.qeetrix.components.typography.QeetrixTextTone
import com.qeetgroup.qeetrix.components.typography.QeetrixTextWeight
import com.qeetgroup.qeetrix.foundation.sizing.QeetrixSize
import com.qeetgroup.qeetrix.icons.QeetrixIcon
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.screenshot.SCREENSHOT_QUALIFIERS
import com.qeetgroup.qeetrix.screenshot.ScreenshotConfig
import com.qeetgroup.qeetrix.screenshot.ScreenshotStack
import com.qeetgroup.qeetrix.screenshot.captureQeetrix
import com.qeetgroup.qeetrix.screenshot.captureStates
import com.qeetgroup.qeetrix.theme.LocalQeetrixContentColor
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Baselines for [QeetrixRadioGroup] in `src/test/screenshots/radiogroup/`: rows, a wrapping row, an error, tiles. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = SCREENSHOT_QUALIFIERS)
class QeetrixRadioGroupScreenshotTest {
    private val cycles =
        listOf(
            QeetrixOption("monthly", "Monthly"),
            QeetrixOption("yearly", "Yearly", description = "Two months free, billed once a year."),
            QeetrixOption("custom", "Custom", enabled = false),
        )

    @Test
    fun states() =
        captureStates("radiogroup") {
            ScreenshotStack {
                QeetrixRadioGroup("yearly", {}, cycles, label = "Billing cycle", description = "You can change it later.")
                QeetrixRadioGroup<String?>(null, {}, cycles.take(2), label = "Invoice language", error = "Choose a language for invoices.")
                // A chosen option in a group with an error: its ring is the danger colour (radio.css 0.4.0).
                QeetrixRadioGroup("monthly", {}, cycles.take(2), label = "Payout cycle", error = "This cycle isn't available on your plan.")
                QeetrixRadioGroup(
                    "s",
                    {},
                    listOf(
                        QeetrixOption("s", "Small"),
                        QeetrixOption("m", "Medium"),
                        QeetrixOption("l", "Large"),
                        QeetrixOption("xl", "Extra large"),
                    ),
                    label = "Size",
                    orientation = QeetrixChoiceOrientation.Horizontal,
                )
                QeetrixRadioGroup(
                    "web",
                    {},
                    listOf(
                        QeetrixOption("web", "Web app", "Users sign in through a redirect.", icon = QeetrixIconName.House),
                        QeetrixOption("service", "Service", "Machine to machine, no user.", icon = QeetrixIconName.Server),
                        QeetrixOption(
                            "native",
                            "Native app with a longer title that wraps",
                            "On phones and desktops.",
                            icon = QeetrixIconName.Settings,
                        ),
                        QeetrixOption("legacy", "Legacy", "No longer offered.", enabled = false),
                    ),
                    label = "Application type",
                    variant = QeetrixChoiceVariant.Tile,
                    columns = 2,
                )
                QeetrixRadioGroup("monthly", {}, cycles.take(2), label = "Disabled", enabled = false)
            }
        }

    /**
     * `src/test/screenshots/radiogroup/underaheading_<config>.png`: tiles 24 dp under the screen's heading, which asks
     * the question, so the group's own label is hidden (hideLabel): the tiles start where the label would have been.
     */
    @Test
    fun underAHeading() {
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("radiogroup", "underaheading", config) {
                Column(Modifier.width(328.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    QeetrixHeading("This phone has a different SIM", level = 1, size = QeetrixHeadingSize.TitleLg)
                    QeetrixRadioGroup(
                        "back",
                        {},
                        listOf(
                            QeetrixOption("back", "I'll put the Jio SIM back", "Your account stays on 98765 43210."),
                            QeetrixOption("move", "Move my account to this SIM", "We'll send a code to the new number."),
                        ),
                        label = "This phone has a different SIM",
                        variant = QeetrixChoiceVariant.Tile,
                        hideLabel = true,
                    )
                }
            }
        }
    }

    /**
     * `src/test/screenshots/radiogroup/languages_<config>.png`: ten short language tiles two a row in a 360 dp phone's
     * 328 dp column (`columns = 2`), 160 dp each and 8 dp apart.
     */
    @Test
    fun languages() {
        val languages = listOf("English", "हिन्दी", "বাংলা", "తెలుగు", "मराठी", "தமிழ்", "ગુજરાતી", "ಕನ್ನಡ", "മലയാളം", "ଓଡ଼ିଆ")
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("radiogroup", "languages", config) {
                Box(Modifier.width(328.dp)) {
                    QeetrixRadioGroup(
                        "हिन्दी",
                        {},
                        languages.map { QeetrixOption(it, it) },
                        label = "Language",
                        variant = QeetrixChoiceVariant.Tile,
                        columns = 2,
                    )
                }
            }
        }
    }

    /**
     * `src/test/screenshots/radiogroup/ownlabels_<config>.png`: options that draw their own titles — languages in their
     * own script at title.lg with the English name under it (tiles, two a row), a picture beside a title, and a badge
     * after a title in a row.
     */
    @Test
    fun ownLabels() {
        val scripts = listOf(Triple("hi", "हिन्दी", "Hindi"), Triple("te", "తెలుగు", "Telugu"))
        for (config in ScreenshotConfig.entries) {
            captureQeetrix("radiogroup", "ownlabels", config) {
                Column(Modifier.width(328.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    QeetrixRadioGroup(
                        "hi",
                        {},
                        scripts.map { (code, own, english) -> QeetrixOption(code, "$own, $english") },
                        label = "Language",
                        variant = QeetrixChoiceVariant.Tile,
                        columns = 2,
                        optionLabel = { option ->
                            val (code, own, english) = scripts.first { it.first == option.value }
                            Column {
                                BasicText(
                                    own,
                                    style =
                                        QeetrixTheme.typography.titleLg.copy(
                                            color = LocalQeetrixContentColor.current,
                                            localeList = LocaleList(code),
                                        ),
                                )
                                QeetrixText(english, size = QeetrixTextSize.Sm, tone = QeetrixTextTone.Subtle)
                            }
                        },
                    )
                    QeetrixRadioGroup(
                        "1",
                        {},
                        listOf(QeetrixOption("1", "SIM 1, Jio", "Number ends in 3210")),
                        label = "Which SIM is yours?",
                        variant = QeetrixChoiceVariant.Tile,
                        optionLabel = { option ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                QeetrixIcon(QeetrixIconName.Smartphone, contentDescription = null, size = QeetrixSize.IconMd)
                                QeetrixText(option.label.replace(", ", " · "), weight = QeetrixTextWeight.Semibold)
                            }
                        },
                    )
                    QeetrixRadioGroup(
                        "contacts",
                        {},
                        listOf(QeetrixOption("everyone", "Everyone"), QeetrixOption("contacts", "My contacts, recommended")),
                        label = "Who can see your photo",
                        optionLabel = { option ->
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                QeetrixText(option.label.substringBefore(","))
                                if (option.value == "contacts") QeetrixBadge("Recommended", tone = QeetrixBadgeTone.Brand)
                            }
                        },
                    )
                }
            }
        }
    }
}
