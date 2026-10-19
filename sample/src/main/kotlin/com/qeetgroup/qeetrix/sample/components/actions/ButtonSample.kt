package com.qeetgroup.qeetrix.sample.components.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.badge.QeetrixBadge
import com.qeetgroup.qeetrix.components.badge.QeetrixBadgeVariant
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.typography.QeetrixText
import com.qeetgroup.qeetrix.components.typography.QeetrixTextVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import kotlinx.coroutines.delay

/** Every Button variant, size and state, and a save action with a real loading state. */
@Composable
fun ButtonSample() {
    SamplePage {
        SampleSection("Variants") {
            SampleFlow { for (variant in QeetrixButtonVariant.entries) QeetrixButton(variant.value, onClick = {}, variant = variant) }
            SampleNote("One default action per view. Link is for an action that reads as part of a sentence.")
        }
        SampleSection("Sizes") {
            SampleFlow { for (size in QeetrixButtonSize.entries) QeetrixButton("Size ${size.value}", onClick = {}, size = size) }
            SampleNote("Every size keeps a 48 × 48 dp touch target. The default height follows the control density.")
        }
        SampleSection("Icons") {
            SampleFlow {
                QeetrixButton("Send", onClick = {}, leadingIcon = QeetrixIconName.Send)
                QeetrixButton("Next", onClick = {}, variant = QeetrixButtonVariant.Outline, trailingIcon = QeetrixIconName.Send)
                QeetrixButton("Remove", onClick = {}, variant = QeetrixButtonVariant.Destructive, leadingIcon = QeetrixIconName.X)
            }
            SampleNote("Icons are decorative: the label names the action. Send mirrors in a right-to-left layout.")
        }
        SampleSection("Loading") {
            var saving by remember { mutableStateOf(false) }
            var saves by remember { mutableIntStateOf(0) }
            LaunchedEffect(saving) {
                if (saving) {
                    delay(2_000)
                    saving = false
                }
            }
            QeetrixButton(
                "Save changes",
                onClick = {
                    saves++
                    saving = true
                },
                loading = saving,
                loadingLabel = "Saving…",
            )
            SampleNote(
                "Saved $saves times. While it saves, taps do nothing, so the action cannot run twice; the button " +
                    "keeps its focus, and TalkBack reads \"Saving…\" with the state \"Loading\". A loading label " +
                    "no longer than the label keeps the width exactly.",
            )
        }
        SampleSection("Disabled") {
            SampleFlow {
                QeetrixButton("Continue", onClick = {}, enabled = false)
                QeetrixButton("Continue", onClick = {}, enabled = false, variant = QeetrixButtonVariant.Outline)
            }
            SampleNote("A disabled button cannot be activated and is announced as disabled.")
        }
        SampleSection("Custom content") {
            QeetrixButton(onClick = {}, variant = QeetrixButtonVariant.Outline) {
                QeetrixText("Inbox", variant = QeetrixTextVariant.Small)
                QeetrixBadge("12", variant = QeetrixBadgeVariant.Secondary)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ButtonSamplePreview() {
    SamplePreview { ButtonSample() }
}

@Preview(name = "Loading, 200% text", fontScale = 2f)
@Composable
private fun ButtonLoadingPreview() {
    SamplePreview { QeetrixButton("Save changes", onClick = {}, loading = true, loadingLabel = "Saving…") }
}
