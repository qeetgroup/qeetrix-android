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
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.card.QeetrixCardSize
import com.qeetgroup.qeetrix.components.card.QeetrixCardVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Cards: the slots, the variants and sizes, a selectable list of cards, and nesting. */
@Composable
fun CardSample() {
    SamplePage {
        SampleSection("Header, content and footer") {
            QeetrixCard(
                title = "Workspace",
                description = "3 members",
                action = { QeetrixBadge("Pro", variant = QeetrixBadgeVariant.Brand) },
                footer = {
                    QeetrixButton("Manage", onClick = {}, variant = QeetrixButtonVariant.Outline, size = QeetrixButtonSize.Sm)
                },
            ) {
                QeetrixText("Created 12 March. Billing renews on 12 April.")
            }
            SampleNote("The title is a heading for TalkBack. Every slot is optional.")
        }
        SampleSection("Variants and sizes") {
            QeetrixCard(title = "Default", description = "A raised surface")
            QeetrixCard(variant = QeetrixCardVariant.Outline, title = "Outline", description = "A border, no shadow")
            QeetrixCard(variant = QeetrixCardVariant.Elevated, title = "Elevated", description = "One step further forward")
            QeetrixCard(size = QeetrixCardSize.Sm, title = "Small", description = "Tighter padding and a smaller title")
        }
        SampleSection("Selectable cards") {
            var plan by rememberSaveable { mutableStateOf("team") }
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.M)) {
                for ((id, title, description) in Plans) {
                    QeetrixCard(
                        onClick = { plan = id },
                        // The card draws the selection; the app, which owns the choice, states it.
                        modifier = Modifier.semantics { selected = plan == id },
                        variant = QeetrixCardVariant.Outline,
                        selected = plan == id,
                        title = title,
                        description = description,
                    )
                }
                QeetrixCard(
                    onClick = {},
                    enabled = false,
                    variant = QeetrixCardVariant.Outline,
                    title = "Enterprise",
                    description = "Contact sales",
                )
            }
            SampleNote(
                "A clickable card is one button for TalkBack, named by its title and description. selected only " +
                    "draws the 2 dp brand boundary, so these cards also state selected or not selected for " +
                    "TalkBack, and their column is a selectable group. The last card is disabled.",
            )
        }
        SampleSection("Nested") {
            QeetrixCard(title = "Billing") {
                QeetrixCard(
                    variant = QeetrixCardVariant.Outline,
                    size = QeetrixCardSize.Sm,
                    title = "Visa ending 4242",
                    description = "Expires 08/28",
                )
            }
            SampleNote("A card inside a card drops its shadow, so surfaces never stack two deep.")
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
