package com.qeetgroup.qeetrix.sample.components.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButton
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection
import com.qeetgroup.qeetrix.theme.QeetrixTheme
import kotlinx.coroutines.delay

/**
 * Buttons — `@qeetrix/react`'s Button: the three variants, the three tones, the three sizes, a trailing icon, a
 * save with a real loading state, full width and disabled. Each row is its own region, so it has at most one
 * solid and one soft button (design rules §7).
 */
@Composable
fun ButtonSample() {
    SamplePage {
        SampleSection("Variants") {
            SampleFlow {
                QeetrixButton("Create invoice", onClick = {})
                QeetrixButton("Export", onClick = {}, variant = QeetrixButtonVariant.Soft)
                QeetrixButton("Cancel", onClick = {}, variant = QeetrixButtonVariant.Ghost)
            }
            SampleNote(
                "Solid (Ember) is the one main action in a region, soft (warm graphite) the main alternative, and every " +
                    "other action is ghost.",
            )
        }
        SampleSection("Tones") {
            SampleFlow {
                QeetrixButton("Revoke key", onClick = {}, tone = QeetrixButtonTone.Danger)
                QeetrixButton("Remove", onClick = {}, variant = QeetrixButtonVariant.Soft, tone = QeetrixButtonTone.Danger)
                QeetrixButton("Discard draft", onClick = {}, variant = QeetrixButtonVariant.Ghost, tone = QeetrixButtonTone.Danger)
            }
            SampleNote("Danger confirms a destructive flow and replaces the primary; it never sits beside a solid brand button.")
            SampleFlow {
                QeetrixButton("Filter", onClick = {}, tone = QeetrixButtonTone.Neutral)
                QeetrixButton("Sort", onClick = {}, variant = QeetrixButtonVariant.Soft, tone = QeetrixButtonTone.Neutral)
                QeetrixButton("Columns", onClick = {}, variant = QeetrixButtonVariant.Ghost, tone = QeetrixButtonTone.Neutral)
            }
            SampleNote("Neutral is for utility actions in toolbars and headers; a solid or soft neutral button looks like soft.")
        }
        SampleSection("Sizes") {
            Column(verticalArrangement = Arrangement.spacedBy(QeetrixTheme.density.spaceFormGap)) {
                for (size in QeetrixButtonSize.entries) {
                    SampleFlow {
                        QeetrixButton("Save ${size.value}", onClick = {}, size = size, icon = QeetrixIconName.Check)
                        QeetrixButton("Export ${size.value}", onClick = {}, variant = QeetrixButtonVariant.Soft, size = size)
                    }
                }
            }
            SampleNote(
                "Heights from the density: size.control.sm, md and lg. A button that submits a form or sits beside fields " +
                    "is lg, the field height. Every size keeps a 48 × 48 dp touch area.",
            )
        }
        SampleSection("A trailing icon") {
            SampleFlow {
                QeetrixButton("Create invoice", onClick = {}, icon = QeetrixIconName.Plus)
                QeetrixButton("Send", onClick = {}, variant = QeetrixButtonVariant.Soft, icon = QeetrixIconName.Send)
                QeetrixButton("Share", onClick = {}, variant = QeetrixButtonVariant.Ghost, icon = QeetrixIconName.Share2)
            }
            SampleNote(
                "Label first, icon last: the icon sits in its own zone behind a short rule as tall as the icon. Icons are " +
                    "decorative — the label names the action — and Send mirrors right to left.",
            )
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
            )
            SampleNote(
                "Saved $saves times. While it saves, a spinner takes the icon's place and the label stays, taps do " +
                    "nothing so the action can't run twice, and TalkBack reads \"Loading\".",
            )
        }
        SampleSection("Full width") {
            QeetrixButton("Save details", onClick = {}, size = QeetrixButtonSize.Lg, icon = QeetrixIconName.Check, fullWidth = true)
            SampleNote("For forms and phones: the label stays at the start and the icon at the end.")
        }
        SampleSection("Disabled") {
            SampleFlow {
                QeetrixButton("Publish", onClick = {}, enabled = false)
                QeetrixButton("Export", onClick = {}, variant = QeetrixButtonVariant.Soft, enabled = false)
            }
            SampleNote(
                "Add a title and a summary to publish. A disabled button stays focusable so its reason can be read; " +
                    "prefer explaining over disabling.",
            )
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
    SamplePreview { QeetrixButton("Save changes", onClick = {}, loading = true) }
}
