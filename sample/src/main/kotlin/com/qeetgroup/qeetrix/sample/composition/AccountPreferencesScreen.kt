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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatar
import com.qeetgroup.qeetrix.components.avatar.QeetrixAvatarSize
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.card.QeetrixCard
import com.qeetgroup.qeetrix.components.checkbox.QeetrixCheckbox
import com.qeetgroup.qeetrix.components.form.QeetrixFormActions
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessage
import com.qeetgroup.qeetrix.components.input.QeetrixFieldMessageTone
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSet
import com.qeetgroup.qeetrix.components.input.QeetrixFieldSetLegend
import com.qeetgroup.qeetrix.components.input.QeetrixOption
import com.qeetgroup.qeetrix.components.input.QeetrixTextField
import com.qeetgroup.qeetrix.components.input.QeetrixTextarea
import com.qeetgroup.qeetrix.components.numberfield.QeetrixNumberField
import com.qeetgroup.qeetrix.components.otpinput.QeetrixOTPInput
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordInput
import com.qeetgroup.qeetrix.components.passwordinput.QeetrixPasswordPurpose
import com.qeetgroup.qeetrix.components.radiogroup.QeetrixRadioGroup
import com.qeetgroup.qeetrix.components.select.QeetrixSelect
import com.qeetgroup.qeetrix.components.switchcontrol.QeetrixSwitch
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.foundation.spacing.QeetrixSpacing
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay

private const val SAVED_NAME = "Ada Lovelace"

/** How often a digest is sent — sample data. */
private enum class Digest { Daily, Weekly, Never }

/**
 * Account preferences — Phase 2 and Phase 3 components in one generic form, as a UI demonstration only:
 * profile fields, a password change with a verification code, preferences and feedback. The state is local;
 * nothing is authenticated, stored or sent, and no real password or code is involved.
 */
@Composable
fun AccountPreferencesScreen() {
    val name = rememberTextFieldState(SAVED_NAME)
    val newPassword = rememberTextFieldState()
    val code = rememberTextFieldState()
    var twoStep by rememberSaveable { mutableStateOf(false) }
    var language by rememberSaveable { mutableStateOf("en") }
    var digest by rememberSaveable { mutableStateOf(Digest.Weekly) }
    var pageSize by rememberSaveable { mutableStateOf<Int?>(25) }
    var mentions by rememberSaveable { mutableStateOf(true) }
    var invites by rememberSaveable { mutableStateOf(false) }
    val feedback = rememberTextFieldState()
    var saving by remember { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(saving) {
        if (saving) {
            delay(1_200)
            saving = false
            saved = true
        }
    }
    val nameMessage = if (name.text.isBlank()) QeetrixFieldMessage("Enter your name.") else null
    val codeMessage =
        when {
            !twoStep -> null
            code.text.length < 6 -> null
            else -> QeetrixFieldMessage("Code entered", QeetrixFieldMessageTone.Success)
        }
    val canSave = nameMessage == null && (!twoStep || code.text.length == 6)

    SamplePage {
        QeetrixCard(title = "Profile") {
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.fieldGap)) {
                Row(horizontalArrangement = Arrangement.spacedBy(QeetrixSpacing.M), verticalAlignment = Alignment.CenterVertically) {
                    QeetrixAvatar(name.text.toString().ifBlank { null }, size = QeetrixAvatarSize.Xl)
                    QeetrixText(
                        "Your name and picture are shown to people in your workspaces.",
                        Modifier.weight(1f),
                        variant = QeetrixTextVariant.Muted,
                    )
                }
                QeetrixTextField(
                    name,
                    label = "Display name",
                    required = true,
                    message = nameMessage,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                QeetrixTextField(
                    rememberTextFieldState("ada@example.com"),
                    label = "Email",
                    readOnly = true,
                    description = "Managed by your organisation.",
                )
            }
        }
        QeetrixCard(title = "Security", description = "A demonstration: nothing here signs you in or changes a password.") {
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.fieldGap)) {
                QeetrixPasswordInput(
                    newPassword,
                    label = "New password",
                    purpose = QeetrixPasswordPurpose.New,
                    optional = true,
                    description = "Leave empty to keep your current one.",
                )
                QeetrixSwitch(twoStep, { twoStep = it }, label = "Two-step verification", description = "Ask for a code when signing in.")
                if (twoStep) {
                    QeetrixOTPInput(
                        code,
                        label = "Verification code",
                        groupSize = 3,
                        description = "Enter any six digits.",
                        message = codeMessage,
                    )
                }
            }
        }
        QeetrixCard(title = "Preferences") {
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.fieldGap)) {
                QeetrixSelect(
                    language,
                    { language = it },
                    listOf(
                        QeetrixOption("en", "English"),
                        QeetrixOption("hi", "हिन्दी"),
                        QeetrixOption("ar", "العربية"),
                        QeetrixOption("ta", "தமிழ்"),
                    ),
                    label = "Language",
                )
                QeetrixRadioGroup(
                    digest,
                    { digest = it },
                    listOf(
                        QeetrixOption(Digest.Daily, "Daily"),
                        QeetrixOption(Digest.Weekly, "Weekly", "Mondays at 09:00"),
                        QeetrixOption(Digest.Never, "Never"),
                    ),
                    label = "Email digest",
                )
                QeetrixNumberField(pageSize, { pageSize = it }, label = "Items per page", range = 10..100, step = 5)
                QeetrixFieldSet("Notify me about", legendStyle = QeetrixFieldSetLegend.Label) {
                    QeetrixCheckbox(mentions, { mentions = it }, label = "Mentions")
                    QeetrixCheckbox(invites, { invites = it }, label = "Invitations")
                }
            }
        }
        QeetrixCard(title = "Feedback") {
            QeetrixTextarea(feedback, label = "Anything we should improve?", optional = true)
        }
        if (saved) {
            Row(Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }) {
                QeetrixBadge("Preferences saved", variant = QeetrixBadgeVariant.Success, icon = QeetrixIconName.CircleCheck)
            }
        }
        QeetrixFormActions {
            QeetrixButton(
                "Reset",
                onClick = {
                    name.setTextAndPlaceCursorAtEnd(SAVED_NAME)
                    saved = false
                },
                variant = QeetrixButtonVariant.Outline,
                enabled = !saving,
            )
            QeetrixButton(
                "Save preferences",
                onClick = {
                    saved = false
                    saving = true
                },
                enabled = canSave,
                loading = saving,
                loadingLabel = "Saving…",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AccountPreferencesPreview() {
    SamplePreview { AccountPreferencesScreen() }
}
