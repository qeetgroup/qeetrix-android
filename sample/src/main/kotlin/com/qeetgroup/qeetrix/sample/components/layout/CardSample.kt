package com.qeetgroup.qeetrix.sample.components.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.card.QeetrixCardTone
import com.qeetgroup.qeetrix.components.card.QeetrixCardVariant
import com.qeetgroup.qeetrix.components.input.QeetrixChoiceVariant
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Cards: the ruled header, body and footer, the subdued variant, a danger zone, and a column of choice cards. */
@Composable
fun CardSample() {
    SamplePage {
        SampleSection("Header, body and footer") {
            QeetrixCard(
                title = "Workspace",
                description = "3 members",
                actions = { QeetrixBadge("Pro", tone = QeetrixBadgeTone.Brand) },
                footer = {
                    QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
                    QeetrixButton("Manage", onClick = {})
                },
            ) {
                QeetrixText("Created 12 March. Billing renews on 12 April.")
            }
            SampleNote("Ruled, flat and square: 1 dp lines and tone, no shadow. The title is a heading for TalkBack.")
        }
        SampleSection("Variant and tone") {
            QeetrixCard(variant = QeetrixCardVariant.Subdued, title = "Subdued", description = "A secondary panel beside the main content")
            QeetrixCard(
                title = "Delete workspace",
                description = "Removes every project and member. This can't be undone.",
                tone = QeetrixCardTone.Danger,
                footer = { QeetrixButton("Delete workspace", onClick = {}, tone = QeetrixButtonTone.Danger) },
            )
            SampleNote("A danger zone takes the danger border and title; its words still say what it does.")
        }
        SampleSection("A card to choose: a choice tile") {
            var plan by rememberSaveable { mutableStateOf("team") }
            QeetrixRadioGroup(
                plan,
                { plan = it },
                Plans.map { (id, title, description) -> QeetrixOption(id, title, description) } +
                    QeetrixOption("enterprise", "Enterprise", "Contact sales", enabled = false),
                label = "Plan",
                variant = QeetrixChoiceVariant.Tile,
            )
            SampleNote(
                "A card is not a click target (06 §9). A card that is chosen is a choice tile — a radio or checkbox group's " +
                    "Tile variant — which keeps the group's name and arrow keys; a card that opens a page keeps a button in " +
                    "its actions or footer.",
            )
        }
    }
}

private val Plans =
    listOf(
        Triple("starter", "Starter", "Up to 5 members"),
        Triple("team", "Team", "Up to 50 members, audit logs"),
    )

@PreviewLightDark
@Composable
private fun CardSamplePreview() {
    SamplePreview { CardSample() }
}
