package com.qeetgroup.qeetrix.sample.components.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.components.button.QeetrixIconButtonSize
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/** Icon-only actions: every variant and size, and the states. Each one is named for what it does. */
@Composable
fun IconButtonSample() {
    SamplePage {
        SampleSection("Variants") {
            SampleFlow {
                for (variant in QeetrixButtonVariant.entries) {
                    QeetrixIconButton(
                        QeetrixIconName.Search,
                        contentDescription = "Search (${variant.value})",
                        onClick = {},
                        variant = variant,
                    )
                }
            }
            SampleNote("Ghost is the default: an icon button usually sits in a toolbar or beside a field.")
        }
        SampleSection("Sizes") {
            SampleFlow {
                for (size in QeetrixIconButtonSize.entries) {
                    QeetrixIconButton(
                        QeetrixIconName.Bell,
                        contentDescription = "Notifications (${size.value})",
                        onClick = {},
                        variant = QeetrixButtonVariant.Outline,
                        size = size,
                    )
                }
            }
            SampleNote("The smallest is 24 dp across; each still takes a 48 × 48 dp touch target.")
        }
        SampleSection("States") {
            var muted by remember { mutableStateOf(false) }
            SampleFlow {
                QeetrixIconButton(
                    QeetrixIconName.Bell,
                    contentDescription = if (muted) "Unmute notifications" else "Mute notifications",
                    onClick = { muted = !muted },
                    variant = if (muted) QeetrixButtonVariant.Secondary else QeetrixButtonVariant.Outline,
                )
                QeetrixIconButton(
                    QeetrixIconName.Send,
                    contentDescription = "Send",
                    onClick = {},
                    variant = QeetrixButtonVariant.Default,
                    enabled = false,
                )
                QeetrixIconButton(
                    QeetrixIconName.Send,
                    contentDescription = "Send",
                    onClick = {},
                    variant = QeetrixButtonVariant.Default,
                    loading = true,
                )
            }
            SampleNote(
                "The name says what a tap does and changes with the state (\"Mute\" / \"Unmute\"); it never comes " +
                    "from the icon's name. Disabled, then loading.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun IconButtonSamplePreview() {
    SamplePreview { IconButtonSample() }
}
