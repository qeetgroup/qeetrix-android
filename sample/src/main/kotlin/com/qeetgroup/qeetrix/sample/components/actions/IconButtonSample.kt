package com.qeetgroup.qeetrix.sample.components.actions

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.qeetgroup.qeetrix.components.button.QeetrixButtonSize
import com.qeetgroup.qeetrix.components.button.QeetrixButtonTone
import com.qeetgroup.qeetrix.components.button.QeetrixButtonVariant
import com.qeetgroup.qeetrix.components.button.QeetrixIconButton
import com.qeetgroup.qeetrix.foundation.accessibility.qeetrixMinimumTouchTarget
import com.qeetgroup.qeetrix.icons.QeetrixIconName
import com.qeetgroup.qeetrix.sample.SampleFlow
import com.qeetgroup.qeetrix.sample.SampleNote
import com.qeetgroup.qeetrix.sample.SamplePage
import com.qeetgroup.qeetrix.sample.SamplePreview
import com.qeetgroup.qeetrix.sample.SampleSection

/**
 * Icon-only actions — `@qeetrix/react`'s IconButton: the variants, a neutral toolbar, the sizes and the states.
 * Each is named for what it does.
 */
@Composable
fun IconButtonSample() {
    SamplePage {
        SampleSection("Variants") {
            SampleFlow {
                QeetrixIconButton(QeetrixIconName.Pencil, contentDescription = "Edit", onClick = {})
                QeetrixIconButton(QeetrixIconName.Copy, contentDescription = "Duplicate", onClick = {}, variant = QeetrixButtonVariant.Soft)
                QeetrixIconButton(
                    QeetrixIconName.Plus,
                    contentDescription = "New invoice",
                    onClick = {},
                    variant = QeetrixButtonVariant.Solid,
                )
            }
            SampleNote("Ghost is the default; a solid or soft icon button counts towards the region's one solid and one soft.")
        }
        SampleSection("A toolbar") {
            // Edge to edge, each keeps its 48 dp touch area in the layout, as the top app bar's icons do.
            val slot = Modifier.qeetrixMinimumTouchTarget()
            Row {
                QeetrixIconButton(QeetrixIconName.Search, "Search", onClick = {}, modifier = slot, tone = QeetrixButtonTone.Neutral)
                QeetrixIconButton(QeetrixIconName.RefreshCw, "Refresh", onClick = {}, modifier = slot, tone = QeetrixButtonTone.Neutral)
                QeetrixIconButton(QeetrixIconName.Settings, "Settings", onClick = {}, modifier = slot, tone = QeetrixButtonTone.Neutral)
                QeetrixIconButton(QeetrixIconName.Trash, "Delete invoice", onClick = {}, modifier = slot, tone = QeetrixButtonTone.Danger)
            }
            SampleNote(
                "Utility actions take the neutral tone, so their icons stay neutral; Ember is kept for meaning. Side by side, " +
                    "each reserves its 48 dp with Modifier.qeetrixMinimumTouchTarget(), so no two share a tap.",
            )
        }
        SampleSection("Sizes") {
            SampleFlow {
                for (size in QeetrixButtonSize.entries) {
                    QeetrixIconButton(
                        QeetrixIconName.Bell,
                        contentDescription = "Notifications (${size.value})",
                        onClick = {},
                        tone = QeetrixButtonTone.Neutral,
                        size = size,
                    )
                }
            }
            SampleNote(
                "A square of the control height: size.control.sm, md and lg, laid out at that size. Each takes a tap within " +
                    "48 × 48 dp round it, so icon buttons side by side sit 48 dp apart, centre to centre, or share the taps.",
            )
        }
        SampleSection("States") {
            var muted by remember { mutableStateOf(false) }
            SampleFlow {
                QeetrixIconButton(
                    QeetrixIconName.Bell,
                    contentDescription = if (muted) "Unmute notifications" else "Mute notifications",
                    onClick = { muted = !muted },
                    variant = if (muted) QeetrixButtonVariant.Soft else QeetrixButtonVariant.Ghost,
                    tone = QeetrixButtonTone.Neutral,
                )
                QeetrixIconButton(QeetrixIconName.Send, contentDescription = "Send", onClick = {}, enabled = false)
                QeetrixIconButton(QeetrixIconName.RefreshCw, contentDescription = "Refresh", onClick = {}, loading = true)
            }
            SampleNote(
                "The name says what a tap does and changes with the state (\"Mute\" / \"Unmute\"); it never comes from the " +
                    "icon's name. Then disabled, and loading: the spinner takes the icon's place in the same square.",
            )
        }
        SampleSection("Names in tooltips") {
            SampleFlow {
                QeetrixIconButton(QeetrixIconName.Copy, "Copy invoice ID", onClick = {}, tone = QeetrixButtonTone.Neutral)
                QeetrixIconButton(QeetrixIconName.Share2, "Share", onClick = {}, tone = QeetrixButtonTone.Neutral)
                QeetrixIconButton(QeetrixIconName.Trash, "Delete draft", onClick = {}, tone = QeetrixButtonTone.Danger)
            }
            SampleNote("Long-press one (or hover, or focus it from a keyboard): its name shows in a tooltip.")
        }
        SampleSection("Arrows in right to left") {
            SampleFlow {
                // Navigation: Next and More point the way the content goes, so they mirror.
                QeetrixIconButton(QeetrixIconName.ArrowRight, "Next", onClick = {}, iconMirrorsInRightToLeft = true)
                QeetrixIconButton(QeetrixIconName.ChevronRight, "More", onClick = {}, iconMirrorsInRightToLeft = true)
                QeetrixIconButton(QeetrixIconName.Send, "Send", onClick = {})
                QeetrixIconButton(QeetrixIconName.ArrowRight, "Move right", onClick = {})
            }
            SampleNote(
                "Turn on Right-to-left on the home screen. Send mirrors by itself (@qeetrix/icons marks it); an arrow or a " +
                    "chevron mirrors only where the app says it means a way through the content, as Next and More do here. " +
                    "\"Move right\" is a physical direction and stays.",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun IconButtonSamplePreview() {
    SamplePreview { IconButtonSample() }
}
