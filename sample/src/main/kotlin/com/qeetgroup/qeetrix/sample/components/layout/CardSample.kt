package com.qeetgroup.qeetrix.sample.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeTone
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.card.QeetrixCardTone
import com.qeetgroup.qeetrix.components.card.QeetrixCardVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpace
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
        SampleSection("Choice cards") {
            var plan by rememberSaveable { mutableStateOf("team") }
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(QeetrixSpace.GapMd)) {
                for ((id, title, description) in Plans) {
                    QeetrixCard(
                        onClick = { plan = id },
                        // The card draws the choice; the app, which owns it, states it.
                        modifier = Modifier.semantics { selected = plan == id },
                        selected = plan == id,
                        title = title,
                        description = description,
                    )
                }
                QeetrixCard(onClick = {}, enabled = false, title = "Enterprise", description = "Contact sales")
            }
            SampleNote(
                "A clickable card is one button for TalkBack, named by its title and description. It takes the choice " +
                    "tile's look: a neutral tint under the pointer, the Ember tint and a 2 dp Ember edge when chosen. The " +
                    "last card is disabled.",
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
