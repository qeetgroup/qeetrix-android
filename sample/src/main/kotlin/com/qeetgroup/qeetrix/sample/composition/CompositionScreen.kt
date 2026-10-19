package com.qeetgroup.qeetrix.sample.composition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarShape
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.progress.QeetrixProgress
import com.qeetgroup.qeetrix.components.separator.QeetrixSeparator
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import kotlinx.coroutines.delay

private const val SAVED_NAME = "Qeet Group"

/**
 * Several components in one realistic, product-neutral form — a workspace's settings — sharing state:
 * the name is validated as it is typed, Save is disabled while it is invalid and loading while it saves,
 * and the result is announced. Check it in both themes, right to left and with large text.
 */
@Composable
fun CompositionScreen() {
    val name = rememberTextFieldState(SAVED_NAME)
    val email = rememberTextFieldState("billing@example.com")
    var usageAlerts by rememberSaveable { mutableStateOf(true) }
    var digest by rememberSaveable { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(saving) {
        if (saving) {
            delay(1_500)
            saving = false
            saved = true
        }
    }
    val nameMessage = if (name.text.isBlank()) QeetrixFieldMessage("Enter a name for the workspace.") else null

    SamplePage {
        QeetrixCard(
            title = name.text.toString().ifBlank { "Untitled workspace" },
            description = "Workspace · 12 members",
            action = { QeetrixBadge("Pro", variant = QeetrixBadgeVariant.Brand) },
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.S), verticalAlignment = Alignment.CenterVertically) {
                QeetrixAvatar(name.text.toString(), size = QeetrixAvatarSize.Lg, shape = QeetrixAvatarShape.Square)
                for (member in listOf("Ada Lovelace", "Grace Hopper", "Priya Raman")) QeetrixAvatar(member, size = QeetrixAvatarSize.Sm)
                QeetrixText("and 9 more", variant = QeetrixTextVariant.Muted)
            }
        }
        QeetrixCard(
            title = "Settings",
            description = "Changes apply to everyone in the workspace.",
            footer = {
                QeetrixButton(
                    "Save changes",
                    onClick = {
                        saved = false
                        saving = true
                    },
                    enabled = nameMessage == null,
                    loading = saving,
                    loadingLabel = "Saving…",
                )
                QeetrixButton(
                    "Reset",
                    onClick = {
                        name.setTextAndPlaceCursorAtEnd(SAVED_NAME)
                        saved = false
                    },
                    variant = QeetrixButtonVariant.Ghost,
                    enabled = !saving,
                )
            },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixSpacing.L)) {
                QeetrixTextField(
                    name,
                    label = "Workspace name",
                    required = true,
                    message = nameMessage,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                QeetrixTextField(
                    email,
                    label = "Billing email",
                    description = "Invoices are sent here.",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                )
                QeetrixSeparator()
                QeetrixSwitch(
                    usageAlerts,
                    { usageAlerts = it },
                    label = "Usage alerts",
                    description = "When the workspace nears its storage limit.",
                )
                QeetrixSwitch(digest, { digest = it }, label = "Weekly digest")
            }
        }
        if (saved) {
            Row(Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }) {
                QeetrixBadge("Saved", variant = QeetrixBadgeVariant.Success, icon = QeetrixIconName.CircleCheck)
            }
        }
        QeetrixCard(title = "Storage", description = "4.2 GB of 10 GB used") {
            QeetrixProgress(0.42f, label = "Storage used")
        }
    }
}

@PreviewLightDark
@Composable
private fun CompositionScreenPreview() {
    SamplePreview { CompositionScreen() }
}
